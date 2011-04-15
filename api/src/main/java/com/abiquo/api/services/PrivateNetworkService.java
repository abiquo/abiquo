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
import com.abiquo.api.exceptions.BadRequestException;
import com.abiquo.api.exceptions.ConflictException;
import com.abiquo.api.exceptions.NotFoundException;
import com.abiquo.api.tracer.TracerLogger;
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
import com.abiquo.server.core.util.network.NetworkResolver;
import com.abiquo.tracer.ComponentType;
import com.abiquo.tracer.EventType;
import com.abiquo.tracer.SeverityType;

@Service
@Transactional(readOnly = true)
public class PrivateNetworkService extends DefaultApiService
{
    @Autowired
    VirtualDatacenterRep repo;

    @Autowired
    DatacenterRep datacenterRepo;

    public static final String FENCE_MODE = "bridge";

    @Autowired
    TracerLogger tracer;

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
        checkNumberOfCurrentVLANs(virtualDatacenter);

        // check if we have a vlan with the same name in the VirtualDatacenter
        if (repo.existAnyVlanWithName(virtualDatacenter.getNetwork(), networkdto.getName()))
        {
            throw new ConflictException(APIError.VLANS_DUPLICATED_VLAN_NAME);
        }

        // Create the NetworkConfiguration object
        NetworkConfiguration config =
            new NetworkConfiguration(networkdto.getAddress(),
                networkdto.getMask(),
                IPNetworkRang.transformIntegerMaskToIPMask(networkdto.getMask()).toString(),
                networkdto.getGateway(),
                FENCE_MODE);

        config.setPrimaryDNS(networkdto.getPrimaryDNS());
        config.setSecondaryDNS(networkdto.getSecondaryDNS());
        config.setSufixDNS(networkdto.getSufixDNS());
        if (!config.isValid())
        {
            validationErrors.addAll(config.getValidationErrors());
            flushErrors();
        }
        // once we have validated we have IPs in all IP parameters (isValid() method), we should
        // ensure they are
        // actually PRIVATE IPs. Also check if the gateway is in the range, and
        checkAddressAndMaskCoherency(IPAddress.newIPAddress(networkdto.getAddress()),
            networkdto.getMask());
        repo.insertNetworkConfig(config);

        // Create the VLANObject inside the VirtualDatacenter network
        VLANNetwork vlan =
            new VLANNetwork(networkdto.getName(),
                virtualDatacenter.getNetwork(),
                networkdto.getDefaultNetwork(),
                config);
        if (!vlan.isValid())
        {
            validationErrors.addAll(vlan.getValidationErrors());
            flushErrors();
        }
        // Before to insert the new VLAN, check if we want the vlan as the default one. If it is,
        // put the previous default one as non-default.
        if (networkdto.getDefaultNetwork())
        {
            VLANNetwork vlanDefault = repo.findVlanByDefault(virtualDatacenter);
            if (vlanDefault != null)
            {
                vlanDefault.setDefaultNetwork(Boolean.FALSE);
                repo.updateVlan(vlanDefault);
            }
        }
        repo.insertVlan(vlan);

        // Calculate all the IPs of the VLAN and generate the DHCP entity that stores these IPs
        Collection<IPAddress> addressRange = calculateIPRange(networkdto);
        createDhcp(virtualDatacenter.getDatacenter(), virtualDatacenter, vlan, config, addressRange);

        // Trace the creation.
        String messageTrace =
            "A new VLAN with in a private range with name '" + vlan.getName()
                + "' has been created in " + virtualDatacenter.getName();
        if (tracer != null)
        {
            tracer.log(SeverityType.INFO, ComponentType.NETWORK, EventType.VLAN_CREATED, messageTrace);
        }
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

    protected void checkNumberOfCurrentVLANs(VirtualDatacenter vdc)
    {
        // TODO Auto-generated method stub
        Integer maxVLANs =
            Integer.valueOf(System.getProperty("abiquo.server.networking.vlanPerVdc"));
        Integer currentVLANs = repo.findVlansByVirtualDatacener(vdc).size();
        if (currentVLANs == maxVLANs)
        {
            throw new ConflictException(APIError.VLANS_PRIVATE_MAXIMUM_REACHED);
        }
    }

    /**
     * Check if the informed ip address and mask are correct for a private IPs. Check also if the
     * netmask and the address are coherent.
     * 
     * @param networkAddress network address
     * @param netmask mask of the network
     * @throws NetworkCommandException if the values are not coherent into a public or private
     *             network environment.
     */
    protected void checkAddressAndMaskCoherency(IPAddress netAddress, Integer netmask)
    {

        // Parse the correct IP. (avoid 127.00.00.01), for instance
        IPAddress networkAddress = IPAddress.newIPAddress(netAddress.toString());

        // First of all, check if the networkAddress is correct.
        Integer firstOctet = Integer.parseInt(networkAddress.getFirstOctet());
        Integer secondOctet = Integer.parseInt(networkAddress.getSecondOctet());

        // if the value is a private network.
        if (firstOctet == 10 || (firstOctet == 192 && secondOctet == 168)
            || (firstOctet == 172 && (secondOctet >= 16 && secondOctet < 32)))
        {
            // check the mask is coherent with the server.
            if (firstOctet == 10 && netmask < 22)
            {
                throw new ConflictException(APIError.VLANS_TOO_BIG_NETWORK);
            }
            if ((firstOctet == 172 || firstOctet == 192) && netmask < 24)
            {
                throw new ConflictException(APIError.VLANS_TOO_BIG_NETWORK_II);
            }
            if (netmask > 30)
            {
                throw new ConflictException(APIError.VLANS_TOO_SMALL_NETWORK);
            }

            // Check the network address depending on the mask. For instance, the network address
            // 192.168.1.128
            // is valid for the mask 25, but the same (192.168.1.128) is an invalid network address
            // for the mask 24.
            if (!NetworkResolver.isValidNetworkMask(netAddress, netmask))
            {
                throw new BadRequestException(APIError.VLANS_INVALID_NETWORK_AND_MASK);
            }
        }
        else
        {
            throw new BadRequestException(APIError.VLANS_PRIVATE_ADDRESS_WRONG);
        }

    }

    /**
     * Calculate the whole range of IPs and ensure the gateway is inside this range.
     * 
     * @param vlan transfer object to create/delete/modify vlans.
     * @return the collection of ip ranges.
     */
    private Collection<IPAddress> calculateIPRange(VLANNetworkDto vlan)
    {
        Collection<IPAddress> range =
            IPNetworkRang.calculateWholeRange(IPAddress.newIPAddress(vlan.getAddress()),
                vlan.getMask());

        //
        if (!IPAddress.isIntoRange(range, vlan.getGateway()))
        {
            throw new BadRequestException(APIError.VLANS_GATEWAY_OUT_OF_RANGE);
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

        // check the gateway belongs to the networks.
        networkConfiguration.setDhcp(dhcp);

        return dhcp;
    }

}
