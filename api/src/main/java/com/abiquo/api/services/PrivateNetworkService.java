/**
 * Abiquo community edition
 * cloud management application for hybrid clouds
 * Copyright (C) 2008-2010 - Abiquo Holdings S.L.
 *
 * This application is free software; you can redistribute it and/or
 * modify it under the terms of the GNU LESSER GENERAL PUBLIC
 * LICENSE as published by the Free Software Foundation under
 * version 3 of the License
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * LESSER GENERAL PUBLIC LICENSE v.3 for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 */

package com.abiquo.api.services;

import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.abiquo.api.exceptions.APIError;
import com.abiquo.api.exceptions.NotFoundException;
import com.abiquo.model.enumerator.RemoteServiceType;
import com.abiquo.server.core.cloud.VirtualDatacenter;
import com.abiquo.server.core.cloud.VirtualDatacenterRep;
import com.abiquo.server.core.infrastructure.Datacenter;
import com.abiquo.server.core.infrastructure.DatacenterRep;
import com.abiquo.server.core.infrastructure.RemoteService;
import com.abiquo.server.core.infrastructure.network.Dhcp;
import com.abiquo.server.core.infrastructure.network.IpPoolManagement;
import com.abiquo.server.core.infrastructure.network.NetworkConfiguration;
import com.abiquo.server.core.infrastructure.network.VLANNetwork;
import com.abiquo.server.core.infrastructure.network.VLANNetworkDto;
import com.abiquo.server.core.util.network.IPAddress;
import com.abiquo.server.core.util.network.IPNetworkRang;

@Service
@Transactional(readOnly = true)
public class PrivateNetworkService extends DefaultApiService
{
    @Autowired
    VirtualDatacenterRep repo;

    @Autowired
    DatacenterRep datacenterRepo;

    public static final String FENCE_MODE = "bridge";

    public PrivateNetworkService()
    {

    }

    public PrivateNetworkService(final EntityManager em)
    {
        repo = new VirtualDatacenterRep(em);
        datacenterRepo = new DatacenterRep(em);
    }

    public Collection<VLANNetwork> getNetworks()
    {
        return repo.findAllVlans();
    }

    public Collection<VLANNetwork> getNetworksByVirtualDatacenter(final Integer virtualDatacenterId)
    {
        VirtualDatacenter virtualDatacenter = repo.findById(virtualDatacenterId);
        Collection<VLANNetwork> networks = null;

        if (virtualDatacenter != null)
        {
            networks = repo.findVlansByVirtualDatacener(virtualDatacenter);
        }

        return networks;
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public VLANNetwork createPrivateNetwork(final Integer virtualDatacenterId,
        final VLANNetworkDto networkdto)
    {
        VirtualDatacenter virtualDatacenter = repo.findById(virtualDatacenterId);
        if (virtualDatacenter == null)
        {
            throw new NotFoundException(APIError.NON_EXISTENT_VIRTUAL_DATACENTER);
        }

        // check if we have reached the maximum number of VLANs for this virtualdatacenter
        checkNumberOfCurrentVLANs(virtualDatacenterId);

        // Create the NetworkConfiguration object
        NetworkConfiguration config =
            new NetworkConfiguration(networkdto.getAddress(), networkdto.getMask(), IPNetworkRang
                .transformIntegerMaskToIPMask(networkdto.getMask()).toString(), networkdto
                .getGateway(), FENCE_MODE);
        config.setPrimaryDNS(networkdto.getPrimaryDNS());
        config.setSecondaryDNS(networkdto.getSecondaryDNS());
        config.setSufixDNS(networkdto.getSufixDNS());
        if (!config.isValid())
        {
            validationErrors.addAll(config.getValidationErrors());
            flushErrors();
        }
        repo.insertNetworkConfig(config);

        // Create the VLANObject inside the VirtualDatacenter network
        VLANNetwork vlan =
            new VLANNetwork(networkdto.getName(), virtualDatacenter.getNetwork(), networkdto
                .getDefaultNetwork(), config);
        if (!vlan.isValid())
        {
            validationErrors.addAll(vlan.getValidationErrors());
            flushErrors();
        }

        repo.insertVlan(vlan);

        // Calculate all the IPs of the VLAN and generate the DHCP entity that stores these IPs
        Collection<IPAddress> addressRange = calculateIPRange(networkdto);
        createDhcp(virtualDatacenter.getDatacenter(), virtualDatacenter, vlan, config, addressRange);

        return vlan;
    }

    public VLANNetwork getNetwork(final Integer id)
    {
        return repo.findVlanById(id);
    }

    public boolean isAssignedTo(final Integer virtualDatacenterId, final Integer networkId)
    {
        VLANNetwork nw = repo.findVlanById(networkId);
        VirtualDatacenter vdc = repo.findById(virtualDatacenterId);

        return nw != null && vdc != null
            && nw.getNetwork().getId().equals(vdc.getNetwork().getId());
    }

    private Collection<IPAddress> calculateIPRange(final VLANNetworkDto vlan)
    {
        Collection<IPAddress> range =
            IPNetworkRang.calculateWholeRange(IPAddress.newIPAddress(vlan.getAddress()), vlan
                .getMask());

        if (!IPAddress.isIntoRange(range, vlan.getGateway()))
        {
            errors.add(APIError.NETWORK_GATEWAY_OUT_OF_RANGE);
            flushErrors();
        }

        return range;
    }

    private Dhcp createDhcp(final Datacenter datacenter, final VirtualDatacenter vdc,
        final VLANNetwork vlan, final NetworkConfiguration networkConfiguration,
        final Collection<IPAddress> range)
    {
        List<RemoteService> dhcpServiceList =
            datacenterRepo.findRemoteServiceWithTypeInDatacenter(datacenter,
                RemoteServiceType.DHCP_SERVICE);
        Dhcp dhcp = new Dhcp();

        if (!dhcpServiceList.isEmpty())
        {
            RemoteService dhcpService = dhcpServiceList.get(0);
            dhcp = new Dhcp(dhcpService);
        }

        repo.insertDhcp(dhcp);

        Collection<String> allMacAddresses = repo.getAllMacs();

        for (IPAddress address : range)
        {
            String macAddress = null;
            do
            {
                macAddress = IPNetworkRang.requestRandomMacAddress(vdc.getHypervisorType());
            }
            while (allMacAddresses.contains(macAddress));
            allMacAddresses.add(macAddress);

            // Replacing the ':' char into an empty char (it seems the dhcp.leases fails when reload
            // leases with the ':' char in the lease name)
            String name = macAddress.replace(":", "") + "_host";

            IpPoolManagement ipManagement =
                new IpPoolManagement(dhcp, vlan, macAddress, name, address.toString(), vlan
                    .getName(), IpPoolManagement.Type.PRIVATE);

            ipManagement.setVirtualDatacenter(vdc);

            repo.insertIpManagement(ipManagement);
        }

        networkConfiguration.setDhcp(dhcp);

        return dhcp;
    }

    protected void checkNumberOfCurrentVLANs(final Integer virtualDatacenterId)
    {
        // TODO Auto-generated method stub

    }
}
