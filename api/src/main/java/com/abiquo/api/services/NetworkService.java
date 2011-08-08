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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import javax.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.abiquo.api.config.ConfigService;
import com.abiquo.api.exceptions.APIError;
import com.abiquo.api.exceptions.BadRequestException;
import com.abiquo.api.tracer.TracerLogger;
import com.abiquo.model.enumerator.RemoteServiceType;
import com.abiquo.server.core.cloud.VirtualAppliance;
import com.abiquo.server.core.cloud.VirtualDatacenter;
import com.abiquo.server.core.cloud.VirtualDatacenterRep;
import com.abiquo.server.core.cloud.VirtualMachine;
import com.abiquo.server.core.infrastructure.Datacenter;
import com.abiquo.server.core.infrastructure.InfrastructureRep;
import com.abiquo.server.core.infrastructure.RemoteService;
import com.abiquo.server.core.infrastructure.management.Rasd;
import com.abiquo.server.core.infrastructure.network.Dhcp;
import com.abiquo.server.core.infrastructure.network.IpPoolManagement;
import com.abiquo.server.core.infrastructure.network.VLANNetwork;
import com.abiquo.server.core.infrastructure.network.VMNetworkConfiguration;
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
    public static final String FENCE_MODE = "bridge";

    private static final Logger LOGGER = LoggerFactory.getLogger(NetworkService.class);

    @Autowired
    InfrastructureRep datacenterRepo;

    @Autowired
    VirtualDatacenterRep repo;

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

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public IpPoolManagement associateVirtualMachinePrivateNic(final Integer vdcId,
        final Integer vappId, final Integer vmId, final Integer vlanId, final Integer ipId)
    {
        VirtualDatacenter vdc = repo.findById(vdcId);
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

        VirtualAppliance vapp = repo.findVirtualApplianceById(vdc, vappId);
        if (vapp == null)
        {
            addNotFoundErrors(APIError.NON_EXISTENT_VIRTUALAPPLIANCE);
            flushErrors();
        }

        VirtualMachine vm = repo.findVirtualMachineById(vapp, vmId);
        if (vm == null)
        {
            addNotFoundErrors(APIError.NON_EXISTENT_VIRTUALMACHINE);
            flushErrors();
        }

        IpPoolManagement ip = repo.findIp(vlan, ipId);

        // check if the ip address is already defined to a virtual machine
        if (ip.getVirtualMachine() != null)
        {
            addConflictErrors(APIError.VLANS_IP_ALREADY_ASSIGNED_TO_A_VIRTUAL_MACHINE);
            flushErrors();
        }

        // create the Rasd object.
        Rasd rasd =
            new Rasd(UUID.randomUUID().toString(),
                IpPoolManagement.DEFAULT_RESOURCE_NAME,
                Integer.valueOf(IpPoolManagement.DISCRIMINATOR));

        rasd.setDescription(IpPoolManagement.DEFAULT_RESOURCE_DESCRIPTION);
        rasd.setConnection("");
        rasd.setAllocationUnits("0");
        rasd.setAutomaticAllocation(0);
        rasd.setAutomaticDeallocation(0);
        rasd.setConfigurationName("0");
        rasd.setAddress(ip.getMac());
        rasd.setParent(ip.getNetworkName());
        rasd.setResourceSubType(String.valueOf(IpPoolManagement.Type.PRIVATE.ordinal()));
        // Configuration Name sets the order in the virtual machine, put it in the last place.
        rasd.setConfigurationName(String.valueOf(repo.findIpsByVirtualMachine(vm).size()));
        repo.insertRasd(rasd);

        ip.setRasd(rasd);
        ip.setVirtualAppliance(vapp);
        ip.setVirtualMachine(vm);
        repo.updateIpManagement(ip);

        return ip;
    }

    /**
     * Creates a new private address.
     * 
     * @param virtualDatacenterId identifier of the virtual datacenter
     * @param newVlan {@link VLANNetwork} object with the new parameters to create.
     * @return the created {@link VLANNetwork} object.
     */
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public VLANNetwork createPrivateNetwork(final Integer virtualDatacenterId,
        final VLANNetwork newVlan)
    {

        VirtualDatacenter virtualDatacenter = repo.findById(virtualDatacenterId);
        if (virtualDatacenter == null)
        {
            addNotFoundErrors(APIError.NON_EXISTENT_VIRTUAL_DATACENTER);
            flushErrors();
        }
        newVlan.setNetwork(virtualDatacenter.getNetwork());
        validate(newVlan);
        validate(newVlan.getConfiguration());

        userService.checkCurrentEnterpriseForPostMethods(virtualDatacenter.getEnterprise());

        // check if we have reached the maximum number of VLANs for this virtualdatacenter
        checkNumberOfCurrentVLANs(virtualDatacenter);

        // check if we have a vlan with the same name in the VirtualDatacenter
        if (repo.existAnyVlanWithName(virtualDatacenter.getNetwork(), newVlan.getName()))
        {
            addConflictErrors(APIError.VLANS_DUPLICATED_VLAN_NAME_VDC);
            flushErrors();
        }

        // once we have validated we have IPs in all IP parameters (isValid() method), we should
        // ensure they are
        // actually PRIVATE IPs. Also check if the gateway is in the range, and
        checkPrivateAddressAndMaskCoherency(IPAddress.newIPAddress(newVlan.getConfiguration()
            .getAddress()), newVlan.getConfiguration().getMask());

        // Before to insert the new VLAN, check if we want the vlan as the default one. If it is,
        // put the previous default one as non-default.
        if (newVlan.getDefaultNetwork())
        {
            VLANNetwork vlanDefault = repo.findVlanByDefaultInVirtualDatacenter(virtualDatacenter);
            if (vlanDefault != null)
            {
                vlanDefault.setDefaultNetwork(Boolean.FALSE);
                repo.updateVlan(vlanDefault);
            }
        }
        repo.insertNetworkConfig(newVlan.getConfiguration());
        repo.insertVlan(newVlan);

        // Calculate all the IPs of the VLAN and generate the DHCP entity that stores these IPs
        Collection<IPAddress> range =
            IPNetworkRang.calculateWholeRange(IPAddress.newIPAddress(newVlan.getConfiguration()
                .getAddress()), newVlan.getConfiguration().getMask());

        if (!IPAddress.isIntoRange(range, newVlan.getConfiguration().getGateway()))
        {
            addValidationErrors(APIError.VLANS_GATEWAY_OUT_OF_RANGE);
            flushErrors();
        }
        createDhcp(virtualDatacenter.getDatacenter(), virtualDatacenter, newVlan, range,
            IpPoolManagement.Type.PRIVATE);

        // Trace the creation.
        String messageTrace =
            "A new VLAN with in a private range with name '" + newVlan.getName()
                + "' has been created in " + virtualDatacenter.getName();
        if (tracer != null)
        {
            tracer.log(SeverityType.INFO, ComponentType.NETWORK, EventType.VLAN_CREATED,
                messageTrace);
        }
        return newVlan;
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

    public List<IpPoolManagement> getListIpPoolManagementByEnterprise(final Integer entId,
        final Integer firstElem, final Integer numElem, final String has, final String orderBy,
        final Boolean asc)
    {

        // Check if the orderBy element is actually one of the available ones
        IpPoolManagement.OrderByEnum orderByEnum = IpPoolManagement.OrderByEnum.fromValue(orderBy);
        if (orderByEnum == null)
        {
            LOGGER.info("Bad parameter 'by' in request to get the private ips by enterprise.");
            addValidationErrors(APIError.QUERY_INVALID_PARAMETER);
            flushErrors();
        }
        return repo.findIpsByEnterprise(entId, firstElem, numElem, has, orderByEnum, asc);
    }

    public List<IpPoolManagement> getListIpPoolManagementByVdc(final Integer vdcId,
        final Integer firstElem, final Integer numElem, final String has, final String orderBy,
        final Boolean asc)
    {
        // Check if the orderBy element is actually one of the available ones
        IpPoolManagement.OrderByEnum orderByEnum = IpPoolManagement.OrderByEnum.fromValue(orderBy);
        if (orderByEnum == null)
        {
            LOGGER
                .info("Bad parameter 'by' in request to get the private ips by virtualdatacenter.");
            addValidationErrors(APIError.QUERY_INVALID_PARAMETER);
            flushErrors();
        }
        return repo.findIpsByVdc(vdcId, firstElem, numElem, has, orderByEnum, asc);
    }

    public List<IpPoolManagement> getListIpPoolManagementByVirtualApp(final VirtualAppliance vapp)
    {
        return repo.findIpsByVirtualAppliance(vapp);
    }

    public List<IpPoolManagement> getListIpPoolManagementByVirtualMachine(final Integer vdcId,
        final Integer vappId, final Integer vmId)
    {
        VirtualDatacenter vdc = repo.findById(vdcId);
        if (vdc == null)
        {
            addNotFoundErrors(APIError.NON_EXISTENT_VIRTUAL_DATACENTER);
            flushErrors();
        }

        VirtualAppliance vapp = repo.findVirtualApplianceById(vdc, vappId);
        if (vapp == null)
        {
            addNotFoundErrors(APIError.NON_EXISTENT_VIRTUALAPPLIANCE);
            flushErrors();
        }

        VirtualMachine vm = repo.findVirtualMachineById(vapp, vmId);
        if (vm == null)
        {
            addNotFoundErrors(APIError.NON_EXISTENT_VIRTUALMACHINE);
            flushErrors();
        }

        return repo.findIpsByVirtualMachine(vm);
    }

    /**
     * Return the list of IPs by a private VLAN.
     * 
     * @param vdcId
     * @param vlanId
     * @param startwith
     * @param orderBy
     * @param filter
     * @param limit
     * @param descOrAsc
     * @param available
     * @return
     */
    public List<IpPoolManagement> getListIpPoolManagementByVlan(final Integer vdcId,
        final Integer vlanId, final Integer startwith, final String orderBy, final String filter,
        final Integer limit, final Boolean descOrAsc, final Boolean available)
    {
        // Check if the orderBy element is actually one of the available ones
        IpPoolManagement.OrderByEnum orderByEnum = IpPoolManagement.OrderByEnum.fromValue(orderBy);
        if (orderByEnum == null)
        {
            LOGGER
                .info("Bad parameter 'by' in request to get the private ips by virtualdatacenter.");
            addValidationErrors(APIError.QUERY_INVALID_PARAMETER);
            flushErrors();
        }

        if (available)
        {
            return repo.findIpsByPrivateVLANFiltered(vdcId, vlanId, startwith, limit, filter,
                orderByEnum, descOrAsc);
        }
        else
        {
            return repo.findIpsByPrivateVLANFiltered(vdcId, vlanId, startwith, limit, filter,
                orderByEnum, descOrAsc);
        }
    }

    public Collection<VLANNetwork> getNetworks()
    {
        return repo.findAllVlans();
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
     * Return the private networks defined in a Virtual Datacenter.
     * 
     * @param virtualDatacenterId identifier of the virtual datacenter.
     * @return a Collection of {@link VLANNetwork} defined inside the Virtual Datacenter.
     */
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

    public VMNetworkConfiguration getVirtualMachineConfiguration(final Integer vdcId,
        final Integer vappId, final Integer vmId, final Integer vmConfigId)
    {
        VirtualDatacenter vdc = repo.findById(vdcId);
        if (vdc == null)
        {
            addNotFoundErrors(APIError.NON_EXISTENT_VIRTUAL_DATACENTER);
            flushErrors();
        }

        VirtualAppliance vapp = repo.findVirtualApplianceById(vdc, vappId);
        if (vapp == null)
        {
            addNotFoundErrors(APIError.NON_EXISTENT_VIRTUALAPPLIANCE);
            flushErrors();
        }

        VirtualMachine vm = repo.findVirtualMachineById(vapp, vmId);
        if (vm == null)
        {
            addNotFoundErrors(APIError.NON_EXISTENT_VIRTUALMACHINE);
            flushErrors();
        }

        // Generally there is only one IP, but we avoid problemes and
        // we return IPs
        List<IpPoolManagement> ips =
            repo.findIpsWithConfigurationIdInVirtualMachine(vm, vmConfigId);

        // The configuration is the same for all the Ips. Check if there is any with
        // configureGateway == true -> that means the configuration is the used.
        IpPoolManagement resultIp = ips.get(0);
        for (IpPoolManagement ip : ips)
        {
            if (ip.getConfigureGateway() == true)
            {
                resultIp = ip;
                break;
            }
        }

        VMNetworkConfiguration vmconfig = new VMNetworkConfiguration();
        VLANNetwork vlan = resultIp.getVlanNetwork();
        vmconfig.setGateway(vlan.getConfiguration().getGateway());
        vmconfig.setPrimaryDNS(vlan.getConfiguration().getPrimaryDNS());
        vmconfig.setSecondaryDNS(vlan.getConfiguration().getSecondaryDNS());
        vmconfig.setSuffixDNS(vlan.getConfiguration().getSufixDNS());
        vmconfig.setUsed(resultIp.getConfigureGateway());
        vmconfig.setId(vlan.getConfiguration().getId());

        return vmconfig;
    }

    /**
     * Return the list of NetworkConfiguration objects for a given virtual machine.
     * 
     * @param vdcId identifier of the virtual datacenter.
     * @param vappId identifier of the virtual appliance.
     * @param vmId identifier of the virtual machine.
     * @return the found list of {@link VMNetworkConfiguration}.
     */
    public List<VMNetworkConfiguration> getVirtualMachineConfigurations(final Integer vdcId,
        final Integer vappId, final Integer vmId)
    {
        VirtualDatacenter vdc = repo.findById(vdcId);
        if (vdc == null)
        {
            addNotFoundErrors(APIError.NON_EXISTENT_VIRTUAL_DATACENTER);
            flushErrors();
        }

        VirtualAppliance vapp = repo.findVirtualApplianceById(vdc, vappId);
        if (vapp == null)
        {
            addNotFoundErrors(APIError.NON_EXISTENT_VIRTUALAPPLIANCE);
            flushErrors();
        }

        VirtualMachine vm = repo.findVirtualMachineById(vapp, vmId);
        if (vm == null)
        {
            addNotFoundErrors(APIError.NON_EXISTENT_VIRTUALMACHINE);
            flushErrors();
        }

        // find all the IPs for the physical machine
        List<IpPoolManagement> ips = repo.findIpsByVirtualMachine(vm);
        List<VMNetworkConfiguration> configs = new ArrayList<VMNetworkConfiguration>();
        for (IpPoolManagement ip : ips)
        {
            VMNetworkConfiguration vmconfig = new VMNetworkConfiguration();
            VLANNetwork vlan = ip.getVlanNetwork();
            vmconfig.setGateway(vlan.getConfiguration().getGateway());
            vmconfig.setPrimaryDNS(vlan.getConfiguration().getPrimaryDNS());
            vmconfig.setSecondaryDNS(vlan.getConfiguration().getSecondaryDNS());
            vmconfig.setSuffixDNS(vlan.getConfiguration().getSufixDNS());
            vmconfig.setUsed(ip.getConfigureGateway());
            vmconfig.setId(vlan.getConfiguration().getId());

            if (!configs.contains(vmconfig))
            {
                configs.add(vmconfig);
            }
        }
        return configs;
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public void releaseNicFromVirtualMachine(final Integer vdcId, final Integer vappId,
        final Integer vmId, final Integer nicOrder)
    {
        VirtualDatacenter vdc = repo.findById(vdcId);
        if (vdc == null)
        {
            addNotFoundErrors(APIError.NON_EXISTENT_VIRTUAL_DATACENTER);
            flushErrors();
        }

        VirtualAppliance vapp = repo.findVirtualApplianceById(vdc, vappId);
        if (vapp == null)
        {
            addNotFoundErrors(APIError.NON_EXISTENT_VIRTUALAPPLIANCE);
            flushErrors();
        }

        VirtualMachine vm = repo.findVirtualMachineById(vapp, vmId);
        if (vm == null)
        {
            addNotFoundErrors(APIError.NON_EXISTENT_VIRTUALMACHINE);
            flushErrors();
        }

        // We need to find the NICs that we want to release from the virtual machine
        // and reorder the rest of NICs decrementing by 1 its 'configurationName'.
        // We can do it in a simple loop because the method
        // 'findIpsByVirtualMachine' return the ips from a VirtualMachine ordered by
        // its order (rasd.configurationName).
        List<IpPoolManagement> ips = repo.findIpsByVirtualMachine(vm);
        if (ips.size() == 1)
        {
            addConflictErrors(APIError.VLANS_CAN_NOT_DELETE_LAST_NIC);
            flushErrors();
        }
        Boolean found = Boolean.FALSE;
        for (IpPoolManagement ip : ips)
        {
            if (!found)
            {
                if (Integer.valueOf(ip.getRasd().getConfigurationName()).equals(nicOrder))
                {
                    // if this ip is the used by configurate the network, raise an exception.
                    if (ip.getConfigureGateway() == Boolean.TRUE)
                    {
                        addConflictErrors(APIError.VLANS_IP_CAN_NOT_BE_DEASSIGNED_DUE_CONFIGURATION);
                        flushErrors();
                    }
                    repo.deleteRasd(ip.getRasd());
                    // this is the object to release.
                    ip.setVirtualAppliance(null);
                    ip.setVirtualMachine(null);
                    ip.setRasd(null);
                    repo.updateIpManagement(ip);

                    found = Boolean.TRUE;
                }
            }
            else
            {
                // Decrement by 1 the order of the rest of NICs
                Integer currentOrder = Integer.valueOf(ip.getRasd().getConfigurationName());
                ip.getRasd().setConfigurationName(String.valueOf(currentOrder - 1));
                repo.updateRasd(ip.getRasd());
            }
        }

        // if the found is FALSE it means any NIC matches with the URI! Throw a NotFound
        if (!found)
        {
            addNotFoundErrors(APIError.VLANS_NIC_NOT_FOUND);
            flushErrors();
        }
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public void reorderVirtualMachineNic(final Integer vdcId, final Integer vappId,
        final Integer vmId, final Integer linkOldOrder, final Integer nicOrder)
    {
        VirtualDatacenter vdc = repo.findById(vdcId);
        if (vdc == null)
        {
            addNotFoundErrors(APIError.NON_EXISTENT_VIRTUAL_DATACENTER);
            flushErrors();
        }

        VirtualAppliance vapp = repo.findVirtualApplianceById(vdc, vappId);
        if (vapp == null)
        {
            addNotFoundErrors(APIError.NON_EXISTENT_VIRTUALAPPLIANCE);
            flushErrors();
        }

        VirtualMachine vm = repo.findVirtualMachineById(vapp, vmId);
        if (vm == null)
        {
            addNotFoundErrors(APIError.NON_EXISTENT_VIRTUALMACHINE);
            flushErrors();
        }

        // All the
        List<IpPoolManagement> ips = repo.findIpsByVirtualMachine(vm);
        if (nicOrder >= ips.size())
        {
            // If the order is bigger or equal than the size, then
            // the resource does not exist. Ex: size = 2 -> nicOrder = 2 -> (ERROR! the order begins
            // with 0 and if the size is true, the available values are 0,1.
            addNotFoundErrors(APIError.VLANS_NIC_NOT_FOUND);
        }

        if (nicOrder == linkOldOrder)
        {
            // do nothing
            return;
        }

        // Update the rest of ethernet values. The goal of this bucle is shift all
        // the ethernet values between the new order and the previous one. Depending
        // on the new order is bigger or smaller than the previous one, the shift
        // will be right to left or left to right.

        // The shift of the new values are leftToRight or rightToLeft?
        Boolean leftToRight = Boolean.TRUE;
        if (nicOrder > linkOldOrder)
        {
            leftToRight = Boolean.FALSE;
        }

        // move backwards or forwards the ip order in the loop.
        for (IpPoolManagement ip : ips)
        {
            Integer eth = Integer.valueOf(ip.getRasd().getConfigurationName());
            if (eth.equals(linkOldOrder))
            {
                // if its the value to modify, do it :)
                // Set the new order to the NIC
                ip.getRasd().setConfigurationName(String.valueOf(nicOrder));
            }
            if (leftToRight && eth >= nicOrder && eth < linkOldOrder)
            {
                ip.getRasd().setConfigurationName(String.valueOf(eth + 1));

            }
            else if (!leftToRight && eth <= nicOrder && eth > linkOldOrder)
            {
                ip.getRasd().setConfigurationName(String.valueOf(eth - 1));
            }

            repo.updateRasd(ip.getRasd());
        }
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
            VLANNetwork duplicatedVLAN =
                repo.findVlanByNameInNetwork(vdc.getNetwork(), newNetwork.getName());
            if (duplicatedVLAN != null)
            {
                addConflictErrors(APIError.VLANS_DUPLICATED_VLAN_NAME_VDC);
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

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public VMNetworkConfiguration updateVirtualMachineConfiguration(final Integer vdcId,
        final Integer vappId, final Integer vmId, final Integer vmConfigId,
        final VMNetworkConfiguration vmConfig)
    {
        VMNetworkConfiguration oldConfig =
            getVirtualMachineConfiguration(vdcId, vappId, vmId, vmConfigId);

        // Check the Id of the path param is the same than the IP of the entity.
        if (vmConfig.getId() == null || !vmConfig.getId().equals(vmConfigId))
        {
            addValidationErrors(APIError.INCOHERENT_IDS);
            flushErrors();
        }

        // Only the 'used' attribute can be changed!

        // First check the primary DNS.
        if (oldConfig.getPrimaryDNS() == null && vmConfig.getPrimaryDNS() != null
            && !vmConfig.getPrimaryDNS().isEmpty())
        {
            addConflictErrors(APIError.VIRTUAL_MACHINE_NETWORK_CONFIGURATION_CAN_NOT_BE_CHANGED);
            flushErrors();
        }

        if (oldConfig.getPrimaryDNS() != null
            && (vmConfig.getPrimaryDNS() == null || !vmConfig.getPrimaryDNS().equals(
                oldConfig.getPrimaryDNS())))
        {
            addConflictErrors(APIError.VIRTUAL_MACHINE_NETWORK_CONFIGURATION_CAN_NOT_BE_CHANGED);
            flushErrors();
        }

        // Then check the secondaryDNS
        if (oldConfig.getSecondaryDNS() == null && vmConfig.getSecondaryDNS() != null
            && !vmConfig.getSecondaryDNS().isEmpty())
        {
            addConflictErrors(APIError.VIRTUAL_MACHINE_NETWORK_CONFIGURATION_CAN_NOT_BE_CHANGED);
            flushErrors();
        }

        if (oldConfig.getSecondaryDNS() != null
            && (vmConfig.getSecondaryDNS() == null || !vmConfig.getSecondaryDNS().equals(
                oldConfig.getSecondaryDNS())))
        {
            addConflictErrors(APIError.VIRTUAL_MACHINE_NETWORK_CONFIGURATION_CAN_NOT_BE_CHANGED);
            flushErrors();
        }

        // Then check the suffixDNS
        if (oldConfig.getSuffixDNS() == null && vmConfig.getSuffixDNS() != null
            && !vmConfig.getSuffixDNS().isEmpty())
        {
            addConflictErrors(APIError.VIRTUAL_MACHINE_NETWORK_CONFIGURATION_CAN_NOT_BE_CHANGED);
            flushErrors();
        }

        if (oldConfig.getSuffixDNS() != null
            && (vmConfig.getSuffixDNS() == null || !vmConfig.getSuffixDNS().equals(
                oldConfig.getSuffixDNS())))
        {
            addConflictErrors(APIError.VIRTUAL_MACHINE_NETWORK_CONFIGURATION_CAN_NOT_BE_CHANGED);
            flushErrors();
        }

        // Then the rest of NOT_NULLABLE attributes.
        if (!oldConfig.getId().equals(vmConfig.getId())
            || !oldConfig.getGateway().equals(vmConfig.getGateway()))
        {
            addConflictErrors(APIError.VIRTUAL_MACHINE_NETWORK_CONFIGURATION_CAN_NOT_BE_CHANGED);
            flushErrors();
        }

        // Check if something has changed.
        if (!oldConfig.getUsed().equals(vmConfig.getUsed()))
        {
            if (!vmConfig.getUsed())
            {
                // That means : before it was the default configuration and now it doesn't.
                // Raise an exception: it should be at least one network configuration.
                addConflictErrors(APIError.VIRTUAL_MACHINE_AT_LEAST_ONE_USED_CONFIGURATION);
                flushErrors();
            }

            // If we have arribed here, that means the 'used' configuration has changed and
            // user wants to use a new configuration. Put the corresponding 'configureGateway' in
            // the IPs.
            Boolean foundIpConfigureGateway = Boolean.FALSE;
            List<IpPoolManagement> ips =
                repo.findIpsByVirtualMachine(repo.findVirtualMachineById(vmId));
            for (IpPoolManagement ip : ips)
            {
                if (!foundIpConfigureGateway)
                {
                    if (ip.getVlanNetwork().getConfiguration().getId().equals(vmConfigId))
                    {
                        ip.setConfigureGateway(Boolean.TRUE);
                        foundIpConfigureGateway = Boolean.TRUE;
                    }
                    else
                    {
                        ip.setConfigureGateway(Boolean.FALSE);
                    }
                }
                else
                {
                    ip.setConfigureGateway(Boolean.FALSE);
                }

                repo.updateIpManagement(ip);
            }

        }
        return vmConfig;

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
        final VLANNetwork vlan, final Collection<IPAddress> range, final IpPoolManagement.Type type)
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
                    type);

            if (vdc != null)
            {
                // public network does not have VDC by default.
                ipManagement.setVirtualDatacenter(vdc);
            }

            repo.insertIpManagement(ipManagement);
        }

        // check the gateway belongs to the networks.
        vlan.getConfiguration().setDhcp(dhcp);

        return dhcp;
    }

}
