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

import com.abiquo.api.config.ConfigService;
import com.abiquo.api.exceptions.APIError;
import com.abiquo.api.exceptions.BadRequestException;
import com.abiquo.api.tracer.TracerLogger;
import com.abiquo.model.enumerator.RemoteServiceType;
import com.abiquo.server.core.cloud.VirtualDatacenter;
import com.abiquo.server.core.cloud.VirtualDatacenterRep;
import com.abiquo.server.core.infrastructure.Datacenter;
import com.abiquo.server.core.infrastructure.InfrastructureRep;
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
public class NetworkService extends DefaultApiService
{
    @Autowired
    VirtualDatacenterRep repo;

    @Autowired
    InfrastructureRep datacenterRepo;

    public static final String FENCE_MODE = "bridge";

    @Autowired
    TracerLogger tracer;

    @Autowired
    UserService userService;

    public NetworkService()
    {

    }

    public NetworkService(final EntityManager em)
    {
        repo = new VirtualDatacenterRep(em);
        datacenterRepo = new InfrastructureRep(em);
        userService = new UserService(em);
    }

    public Collection<VLANNetwork> getNetworks()
    {
        return repo.findAllVlans();
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public VLANNetwork createPrivateNetwork(final Integer virtualDatacenterId,
        final VLANNetworkDto networkdto)
    {
        VirtualDatacenter virtualDatacenter = repo.findById(virtualDatacenterId);
        if (virtualDatacenter == null)
        {
            addNotFoundErrors(APIError.NON_EXISTENT_VIRTUAL_DATACENTER);
            flushErrors();
        }

        userService.checkCurrentEnterpriseForPostMethods(virtualDatacenter.getEnterprise());

        // check if we have reached the maximum number of VLANs for this virtualdatacenter
        checkNumberOfCurrentVLANs(virtualDatacenter);

        // check if we have a vlan with the same name in the VirtualDatacenter
        if (repo.existAnyVlanWithName(virtualDatacenter.getNetwork(), networkdto.getName()))
        {
            addConflictErrors(APIError.VLANS_DUPLICATED_VLAN_NAME);
            flushErrors();
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
            addValidationErrors(config.getValidationErrors());
            flushErrors();
        }
        // once we have validated we have IPs in all IP parameters (isValid() method), we should
        // ensure they are
        // actually PRIVATE IPs. Also check if the gateway is in the range, and
        checkPrivateAddressAndMaskCoherency(IPAddress.newIPAddress(networkdto.getAddress()),
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
            addValidationErrors(vlan.getValidationErrors());
            flushErrors();
        }
        // Before to insert the new VLAN, check if we want the vlan as the default one. If it is,
        // put the previous default one as non-default.
        if (networkdto.getDefaultNetwork())
        {
            VLANNetwork vlanDefault = repo.findVlanByDefaultInVirtualDatacenter(virtualDatacenter);
            if (vlanDefault != null)
            {
                vlanDefault.setDefaultNetwork(Boolean.FALSE);
                repo.updateVlan(vlanDefault);
            }
        }
        repo.insertVlan(vlan);

        // Calculate all the IPs of the VLAN and generate the DHCP entity that stores these IPs
        Collection<IPAddress> addressRange = calculateIPRange(networkdto);
        createDhcp(virtualDatacenter.getDatacenter(), virtualDatacenter, vlan, addressRange);

        // Trace the creation.
        String messageTrace =
            "A new VLAN with in a private range with name '" + vlan.getName()
                + "' has been created in " + virtualDatacenter.getName();
        if (tracer != null)
        {
            tracer.log(SeverityType.INFO, ComponentType.NETWORK, EventType.VLAN_CREATED,
                messageTrace);
        }
        return vlan;
    }

    public Collection<VLANNetwork> getPrivateNetworks(final Integer virtualDatacenterId)
    {
        VirtualDatacenter virtualDatacenter = repo.findById(virtualDatacenterId);
        Collection<VLANNetwork> networks = null;

        if (virtualDatacenter != null)
        {
            networks = repo.findVlansByVirtualDatacener(virtualDatacenter);
        }

        return networks;
    }

    /**
     * Retrieve a Private Network.
     * 
     * @param virtualdatacenterId identifier of the virtual datacenter.
     * @param vlanId identifier of the vlan.
     * @return an instance of the requested {@link VLANNetwork}
     */
    public VLANNetwork getPrivateNetwork(final Integer virtualdatacenterId, final Integer vlanId)
    {
        VirtualDatacenter vdc = repo.findById(virtualdatacenterId);
        if (vdc == null)
        {
            addNotFoundErrors(APIError.NON_EXISTENT_VIRTUAL_DATACENTER);
            flushErrors();
        }
        VLANNetwork vlan = repo.findVlanByVirtualDatacenterId(vdc, vlanId);
        if (vlan == null)
        {
            addNotFoundErrors(APIError.VLANS_NON_EXISTENT_VIRTUAL_NETWORK);
            flushErrors();
        }
        return vlan;
    }

    /**
     * Edit an existing VLAN.
     * 
     * @param vdcId identifier of the virtual datacenter the VLAN belongs to.
     * @param vlanId identifier of the VLAN
     * @param newNetwork new object to edit.
     * @return an instance of the modified {@link VLANNetwork}
     */
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public VLANNetwork updatePrivateNetwork(final Integer vdcId, final Integer vlanId,
        final VLANNetwork newNetwork)
    {

        // Check if the fields are ok.
        VLANNetwork oldNetwork = getPrivateNetwork(vdcId, vlanId);
        VirtualDatacenter vdc = repo.findById(vdcId);
        newNetwork.setNetwork(vdc.getNetwork());
        validate(newNetwork);
        if (!vlanId.equals(newNetwork.getId()))
        {
            addValidationErrors(APIError.INCOHERENT_IDS);
            flushErrors();
        }

        // Values 'address', 'mask', and 'tag' can not be changed by the edit process
        if (!oldNetwork.getConfiguration().getAddress()
            .equalsIgnoreCase(newNetwork.getConfiguration().getAddress())
            || !oldNetwork.getConfiguration().getMask()
                .equals(newNetwork.getConfiguration().getMask())
            || oldNetwork.getTag() == null
            && newNetwork.getTag() != null
            || oldNetwork.getTag() != null
            && newNetwork.getTag() == null
            || oldNetwork.getTag() != null
            && newNetwork.getTag() != null && !oldNetwork.getTag().equals(newNetwork.getTag()))
        {
            addConflictErrors(APIError.VLANS_EDIT_INVALID_VALUES);
            flushErrors();
        }

        // If we want to set the default network as non-default, and the network is
        // actually the default one, raise an error: it should be at least one default vlan
        if (!newNetwork.getDefaultNetwork() && oldNetwork.getDefaultNetwork())
        {
            addConflictErrors(APIError.VLANS_AT_LEAST_ONE_DEFAULT_NETWORK);
            flushErrors();
        }

        // In the same order: if we put the newNetwork as default, set the previous one
        // as non-default
        if (newNetwork.getDefaultNetwork() && !oldNetwork.getDefaultNetwork())
        {
            VLANNetwork defaultVLAN = repo.findVlanByDefaultInVirtualDatacenter(vdc);
            defaultVLAN.setDefaultNetwork(Boolean.FALSE);
            repo.updateVlan(defaultVLAN);
        }

        // Check the new gateway is inside the range of IPs.
        if (!newNetwork.getConfiguration().getGateway()
            .equalsIgnoreCase(oldNetwork.getConfiguration().getGateway()))
        {
            IPAddress networkIP =
                IPAddress.newIPAddress(newNetwork.getConfiguration().getAddress());
            String newGateway = newNetwork.getConfiguration().getGateway();
            Integer mask = newNetwork.getConfiguration().getMask();

            if (!IPAddress.isIntoRange(IPNetworkRang.calculateWholeRange(networkIP, mask),
                newGateway))
            {
                addConflictErrors(APIError.VLANS_GATEWAY_OUT_OF_RANGE);
                flushErrors();
            }
        }

        // Check if the network name has changed. If it has:
        // 1 - Check there is not another VLAN in the same VDC with the same name.
        // 2 - Change all the IpPoolManagement entities with the new network name
        if (!newNetwork.getName().equalsIgnoreCase(oldNetwork.getName()))
        {
            VLANNetwork duplicatedVLAN = repo.findVlanByNameInVDC(vdc, newNetwork.getName());
            if (duplicatedVLAN != null)
            {
                addConflictErrors(APIError.VLANS_DUPLICATED_VLAN_NAME);
                flushErrors();
            }

            // update the ips with the new values.
            List<IpPoolManagement> ips = repo.findIpsByPrivateVLAN(vdcId, vlanId);
            for (IpPoolManagement ip : ips)
            {
                ip.setNetworkName(newNetwork.getName());
            }
            // the updates flushes the session. Due we have so many IPs to update,
            // better wait until it finishes.
            repo.updateIpManagement(null);

        }

        // Set the new values and update the VLAN
        oldNetwork.getConfiguration().setGateway(newNetwork.getConfiguration().getGateway());
        oldNetwork.getConfiguration().setPrimaryDNS(newNetwork.getConfiguration().getPrimaryDNS());
        oldNetwork.getConfiguration().setSecondaryDNS(
            newNetwork.getConfiguration().getSecondaryDNS());
        oldNetwork.getConfiguration().setSufixDNS(newNetwork.getConfiguration().getSufixDNS());
        oldNetwork.setName(newNetwork.getName());
        oldNetwork.setDefaultNetwork(newNetwork.getDefaultNetwork());

        repo.updateVlan(oldNetwork);

        // Trace log.
        String messageTrace =
            "The Private VLAN with id '" + oldNetwork.getId()
                + "' has been modified in Virtual Datacenter " + vdc.getName();
        if (tracer != null)
        {
            tracer.log(SeverityType.INFO, ComponentType.NETWORK, EventType.VLAN_EDITED,
                messageTrace);
        }

        return oldNetwork;
    }

    /**
     * Delete a VLAN identified by its virtual datacenter id and its vlan id
     * 
     * @param vdcId identifier of the virtual datacenter.
     * @param vlanId identifier of the VLAN.
     */
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public void deletePrivateNetwork(final Integer vdcId, final Integer vlanId)
    {
        VLANNetwork vlanToDelete = getPrivateNetwork(vdcId, vlanId);
        VirtualDatacenter vdc = repo.findById(vdcId);

        // If it is the last VLAN, can not be deleted
        if (repo.findVlansByVirtualDatacener(vdc).size() == 1)
        {
            addConflictErrors(APIError.VIRTUAL_DATACENTER_MUST_HAVE_NETWORK);
            flushErrors();
        }

        // Default VLAN can not be deleted.
        if (vlanToDelete.getDefaultNetwork())
        {
            addConflictErrors(APIError.VLANS_DEFAULT_NETWORK_CAN_NOT_BE_DELETED);
            flushErrors();
        }

        // If any virtual machine is using by any IP of the VLAN, raise an exception
        if (repo.findUsedIpsByPrivateVLAN(vdcId, vlanId).size() != 0)
        {
            addConflictErrors(APIError.VLANS_WITH_USED_IPS_CAN_NOT_BE_DELETED);
            flushErrors();
        }

        repo.deleteVLAN(vlanToDelete);
    }

    /**
     * Check if the VLANs used have reached the maximum.
     * 
     * @param vdc each virtualdatacenter has a maximum of VLANs.
     */
    protected void checkNumberOfCurrentVLANs(final VirtualDatacenter vdc)
    {
        Integer maxVLANs = Integer.valueOf(ConfigService.getVlanPerVdc());
        Integer currentVLANs = repo.findVlansByVirtualDatacener(vdc).size();
        if (currentVLANs >= maxVLANs)
        {
            addConflictErrors(APIError.VLANS_PRIVATE_MAXIMUM_REACHED);
            flushErrors();
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
    protected void checkPrivateAddressAndMaskCoherency(final IPAddress netAddress,
        final Integer netmask)
    {

        // Parse the correct IP. (avoid 127.00.00.01), for instance
        IPAddress networkAddress = IPAddress.newIPAddress(netAddress.toString());

        // First of all, check if the networkAddress is correct.
        Integer firstOctet = Integer.parseInt(networkAddress.getFirstOctet());
        Integer secondOctet = Integer.parseInt(networkAddress.getSecondOctet());

        // if the value is a private network.
        if (firstOctet == 10 || firstOctet == 192 && secondOctet == 168 || firstOctet == 172
            && secondOctet >= 16 && secondOctet < 32)
        {
            // check the mask is coherent with the server.
            if (firstOctet == 10 && netmask < 22)
            {
                addConflictErrors(APIError.VLANS_TOO_BIG_NETWORK);
            }
            if ((firstOctet == 172 || firstOctet == 192) && netmask < 24)
            {
                addConflictErrors(APIError.VLANS_TOO_BIG_NETWORK_II);
            }
            if (netmask > 30)
            {
                addConflictErrors(APIError.VLANS_TOO_SMALL_NETWORK);
            }
            flushErrors();

            // Check the network address depending on the mask. For instance, the network address
            // 192.168.1.128
            // is valid for the mask 25, but the same (192.168.1.128) is an invalid network address
            // for the mask 24.
            if (!NetworkResolver.isValidNetworkMask(netAddress, netmask))
            {
                addValidationErrors(APIError.VLANS_INVALID_NETWORK_AND_MASK);
                flushErrors();
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
    private Collection<IPAddress> calculateIPRange(final VLANNetworkDto vlan)
    {
        Collection<IPAddress> range =
            IPNetworkRang.calculateWholeRange(IPAddress.newIPAddress(vlan.getAddress()),
                vlan.getMask());

        //
        if (!IPAddress.isIntoRange(range, vlan.getGateway()))
        {
            addValidationErrors(APIError.VLANS_GATEWAY_OUT_OF_RANGE);
            flushErrors();
        }

        return range;
    }

    /**
     * Create the {@link DhcpService} object and stores all the IPs of the network in database.
     * 
     * @param datacenter datacenter where the network is created. Needed to get the DHCP.
     * @param vdc virtual dataceneter where the network are assigned. Can be null for public
     *            networks.
     * @param vlan vlan to assign its ips.
     * @param range list of ips to create inside the DHCP
     * @return the created {@link DHCP} object
     */
    protected Dhcp createDhcp(final Datacenter datacenter, final VirtualDatacenter vdc,
        final VLANNetwork vlan, final Collection<IPAddress> range)
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
            String name = null;
            if (vdc != null)
            {
                do
                {
                    macAddress = IPNetworkRang.requestRandomMacAddress(vdc.getHypervisorType());
                }
                while (allMacAddresses.contains(macAddress));
                allMacAddresses.add(macAddress);

                // Replacing the ':' char into an empty char (it seems the dhcp.leases fails when
                // reload
                // leases with the ':' char in the lease name)
                name = macAddress.replace(":", "") + "_host";
            }

            IpPoolManagement ipManagement =
                new IpPoolManagement(dhcp,
                    vlan,
                    macAddress,
                    name,
                    address.toString(),
                    vlan.getName(),
                    IpPoolManagement.Type.PRIVATE);

            if (vdc != null)
            {
                // public network does not have VDC by default.
                ipManagement.setVirtualDatacenter(vdc);
            }

            repo.insertIpManagement(ipManagement);
        }

        // check the gateway belongs to the networks.
        // networkConfiguration.setDhcp(dhcp);

        return dhcp;
    }

}
