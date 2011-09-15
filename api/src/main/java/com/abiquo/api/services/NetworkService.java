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

import org.hibernate.Hibernate;
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
import com.abiquo.model.enumerator.NetworkType;
import com.abiquo.model.enumerator.RemoteServiceType;
import com.abiquo.server.core.cloud.State;
import com.abiquo.server.core.cloud.VirtualAppliance;
import com.abiquo.server.core.cloud.VirtualDatacenter;
import com.abiquo.server.core.cloud.VirtualDatacenterRep;
import com.abiquo.server.core.cloud.VirtualMachine;
import com.abiquo.server.core.enterprise.DatacenterLimits;
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
    /** Static literal for 'FENCE_MODE' values. */
    public static final String FENCE_MODE = "bridge";

    /** Logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(NetworkService.class);

    /** Autowired infrastructure DAO repository. */
    @Autowired
    InfrastructureRep datacenterRepo;

    /** Autowired Virtual Infrastructure DAO repository. */
    @Autowired
    VirtualDatacenterRep repo;

    /** Autowired tracer logger. */
    @Autowired
    TracerLogger tracer;

    /** User service for user-specific privileges */
    @Autowired
    UserService userService;

    /**
     * Default constructor. Needed by @Autowired injections
     */
    public NetworkService()
    {

    }

    /**
     * Auxiliar constructor for test purposes. Haters gonna hate bzengine.
     * 
     * @param em {@link EntityManager} instance with active transaction.
     */
    public NetworkService(final EntityManager em)
    {
        repo = new VirtualDatacenterRep(em);
        datacenterRepo = new InfrastructureRep(em);
        userService = new UserService(em);
    }

    /**
     * Associates a NIC to a Private IP address.
     * 
     * @param vdcId Identifier of the Virtual Datacenter.
     * @param vappId Identifier of the Virtual Appliance.
     * @param vmId Identifier of the Virtual Machine that will store the NIC.
     * @param vlanId Identifier of the VLAN of the IP
     * @param ipId Identifier of the IP address inside the VLAN.
     * @return the resulting {@link IpPoolManagement} object.
     */
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public IpPoolManagement associateVirtualMachinePrivateNic(final Integer vdcId,
        final Integer vappId, final Integer vmId, final Integer vlanId, final Integer ipId)
    {
        // Get the needed objects.
        VirtualDatacenter vdc = getVirtualDatacenter(vdcId);
        VLANNetwork vlan = getPrivateVlan(vdc, vlanId);
        VirtualAppliance vapp = getVirtualAppliance(vdc, vappId);
        VirtualMachine vm = getVirtualMachine(vapp, vmId);

        IpPoolManagement ip = repo.findIp(vlan, ipId);

        if (ip == null)
        {
            addConflictErrors(APIError.VLANS_IP_DOES_NOT_EXISTS);
            flushErrors();
        }

        // The user has the role for manage This. But... is the user from the same enterprise
        // than Virtual Datacenter?
        userService.checkCurrentEnterpriseForPostMethods(vdc.getEnterprise());

        // Check if the machine is in the correct state to perform the action.
        if (!vm.getState().equals(State.NOT_DEPLOYED))
        {
            addConflictErrors(APIError.VIRTUAL_MACHINE_INCOHERENT_STATE);
            flushErrors();
        }

        // check if the ip address is already defined to a virtual machine
        if (ip.getVirtualMachine() != null)
        {
            addConflictErrors(APIError.VLANS_IP_ALREADY_ASSIGNED_TO_A_VIRTUAL_MACHINE);
            flushErrors();
        }

        // create the Rasd object.
        Rasd rasd =
            new Rasd(UUID.randomUUID().toString(), IpPoolManagement.DEFAULT_RESOURCE_NAME, Integer
                .valueOf(IpPoolManagement.DISCRIMINATOR));

        rasd.setDescription(IpPoolManagement.DEFAULT_RESOURCE_DESCRIPTION);
        rasd.setConnection("");
        rasd.setAllocationUnits("0");
        rasd.setAutomaticAllocation(0);
        rasd.setAutomaticDeallocation(0);
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

        if (tracer != null)
        {
            String messageTrace =
                "Virtual Machine '" + vm.getName()
                    + "' has created a NIC associated to IP Address '" + ip.getIp()
                    + "' from VLAN '" + ip.getNetworkName() + "'";
            tracer.log(SeverityType.INFO, ComponentType.VIRTUAL_MACHINE,
                EventType.NIC_ASSIGNED_VIRTUAL_MACHINE, messageTrace);
        }

        return ip;
    }

    /**
     * Creates a new Private Network.
     * 
     * @param virtualDatacenterId Identifier of the Virtual Datacenter
     * @param newVlan {@link VLANNetwork} object with the new parameters to create.
     * @return the created {@link VLANNetwork} object.
     */
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public VLANNetwork createPrivateNetwork(final Integer vdcId, final VLANNetwork newVlan)
    {

        VirtualDatacenter virtualDatacenter = getVirtualDatacenter(vdcId);
        newVlan.setNetwork(virtualDatacenter.getNetwork());
        newVlan.setType(NetworkType.INTERNAL);
        validate(newVlan);
        validate(newVlan.getConfiguration());

        // The user has the role for manage This. But... is the user from the same enterprise
        // than Virtual Datacenter?
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

        // Trace
        if (tracer != null)
        {
            String messageTrace =
                "A new internal VLAN with in a private range with name '" + newVlan.getName()
                    + "' has been created in " + virtualDatacenter.getName();
            tracer.log(SeverityType.INFO, ComponentType.NETWORK, EventType.VLAN_CREATED,
                messageTrace);
        }
        return newVlan;
    }

    /**
     * Delete a VLAN identified by its Virtual Datacenter id and its VLAN id
     * 
     * @param vdcId identifier of the virtual datacenter.
     * @param vlanId identifier of the VLAN.
     */
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public void deletePrivateNetwork(final Integer vdcId, final Integer vlanId)
    {
        VLANNetwork vlanToDelete = getPrivateNetwork(vdcId, vlanId);
        VirtualDatacenter vdc = getVirtualDatacenter(vdcId);

        // Check input parameters existence
        if (repo.findVlansByVirtualDatacener(vdc).size() == 1)
        {
            addConflictErrors(APIError.VIRTUAL_DATACENTER_MUST_HAVE_NETWORK);
            flushErrors();
        }
        if (vdc.getDefaultVlan().getId().equals(vlanToDelete.getId()))
        {
            addConflictErrors(APIError.VLANS_DEFAULT_NETWORK_CAN_NOT_BE_DELETED);
            flushErrors();
        }
        if (repo.findUsedIpsByPrivateVLAN(vdcId, vlanId).size() != 0)
        {
            addConflictErrors(APIError.VLANS_WITH_USED_IPS_CAN_NOT_BE_DELETED);
            flushErrors();
        }

        if (repo.isDefaultNetworkofanyVDC(vlanId))
        {
            addConflictErrors(APIError.VLANS_CANNOT_DELETE_DEFAULT);
            flushErrors();
        }

        // The user has the role for manage This. But... is the user from the same enterprise
        // than Virtual Datacenter?
        userService.checkCurrentEnterpriseForPostMethods(vdc.getEnterprise());

        repo.deleteVLAN(vlanToDelete);

        if (tracer != null)
        {
            String messageTrace =
                "The internal VLAN with name '" + vlanToDelete.getName() + "' has been deleted.";
            tracer.log(SeverityType.INFO, ComponentType.NETWORK, EventType.VLAN_DELETED,
                messageTrace);
        }
    }

    /**
     * Get the default network for virtual datacenter.
     * 
     * @param id identifier of the virtual datacenter
     * @return
     */
    public VLANNetwork getDefaultNetworkForVirtualDatacenter(final Integer vdcId)
    {
        VirtualDatacenter vdc = getVirtualDatacenter(vdcId);

        if (vdc.getDefaultVlan() == null)
        {
            addUnexpectedErrors(APIError.VLANS_VIRTUAL_DATACENTER_SHOULD_HAVE_A_DEFAULT_VLAN);
            flushErrors();
        }

        return vdc.getDefaultVlan();
    }

    /**
     * Asks for all the Private IPs managed by an Enterprise.
     * 
     * @param entId identifier of the Enterprise.
     * @param firstElem first element to retrieve.
     * @param numElem number of elements to retrieve.
     * @param has filter by name.
     * @param orderBy oderBy filter.
     * @param asc if the 'orderBy' should be ascendant or descendant.
     * @return the list of matching elements.
     */
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
        List<IpPoolManagement> ips =
            repo.findIpsByEnterprise(entId, firstElem, numElem, has, orderByEnum, asc);
        LOGGER.debug("Returning the list of IPs used by Enterprise '" + entId + "'.");
        return ips;
    }

    /**
     * Asks for all the Private IPs managed by a Virtual Datacenter.
     * 
     * @param vdcId identifier of the Virtual Datacenter.
     * @param firstElem first element to retrieve.
     * @param numElem number of elements to retrieve.
     * @param has filter by name.
     * @param orderBy oderBy filter.
     * @param asc if the 'orderBy' should be ascendant or descendant.
     * @return the list of matching elements.
     */
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

        VirtualDatacenter vdc = repo.findById(vdcId);

        if (vdc == null)
        {
            addNotFoundErrors(APIError.NON_EXISTENT_VIRTUAL_DATACENTER);
            flushErrors();
        }
        List<IpPoolManagement> ips =
            repo.findIpsByVdc(vdcId, firstElem, numElem, has, orderByEnum, asc);
        LOGGER
            .debug("Returning the list of IPs used by VirtualDatacenter '" + vdc.getName() + "'.");
        return ips;
    }

    /**
     * Asks for all the Private IPs managed by a Virtual Appliance.
     * 
     * @param vapp Virtual Appliance object.
     * @return the list of matching elements.
     */
    public List<IpPoolManagement> getListIpPoolManagementByVirtualApp(final VirtualAppliance vapp)
    {
        List<IpPoolManagement> ips = repo.findIpsByVirtualAppliance(vapp);
        LOGGER
            .debug("Returning the list of IPs used by VirtualAppliance '" + vapp.getName() + "'.");
        return ips;
    }

    /**
     * Asks for all the Private IPs managed by a Virtual Virtual Machine.
     * 
     * @param vdcId identifier of the Virtual Datacenter.
     * @param vappId identifier of the Virtual Appliance.
     * @param vmId identifier of the Virtual Machine.
     * @return the list of matching elements.
     */
    public List<IpPoolManagement> getListIpPoolManagementByVirtualMachine(final Integer vdcId,
        final Integer vappId, final Integer vmId)
    {
        VirtualDatacenter vdc = getVirtualDatacenter(vdcId);
        VirtualAppliance vapp = getVirtualAppliance(vdc, vappId);
        VirtualMachine vm = getVirtualMachine(vapp, vmId);

        List<IpPoolManagement> ips = repo.findIpsByVirtualMachine(vm);
        LOGGER.debug("Returning the list of IPs used by Virtual Machine '" + vm.getName() + "'.");

        for (IpPoolManagement ip : ips)
        {
            Hibernate.initialize(ip.getVlanNetwork().getEnterprise());
            if (ip.getVlanNetwork().getEnterprise() != null)
            {
                // needed for REST links.
                DatacenterLimits dl =
                    datacenterRepo.findDatacenterLimits(ip.getVlanNetwork().getEnterprise(), vdc
                        .getDatacenter());
                ip.getVlanNetwork().setLimitId(dl.getId());
            }
        }
        return ips;
    }

    /**
     * Asks for all the Private IPs managed by a VLAN.
     * 
     * @param vdcId Identifier of the Virtual Datacenter.
     * @param vlanId Identifier of the VLAN.
     * @param startwith First element to retrieve.
     * @param limit Number of elements to retrieve.
     * @param filter Filter by name.
     * @param orderBy OrderBy filter.
     * @param descOrAsc If the 'orderBy' should be ascendant or descendant.
     * @return The list of matching elements.
     */
    public List<IpPoolManagement> getListIpPoolManagementByVlan(final Integer vdcId,
        final Integer vlanId, final Integer startwith, final String orderBy, final String filter,
        final Integer limit, final Boolean descOrAsc, final Boolean freeIps)
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

        return repo.findIpsByPrivateVLANFiltered(vdcId, vlanId, startwith, limit, filter,
            orderByEnum, descOrAsc, freeIps);
    }

    /**
     * Retrieve a Private Network.
     * 
     * @param virtualdatacenterId identifier of the virtual datacenter.
     * @param vlanId identifier of the vlan.
     * @return an instance of the requested {@link VLANNetwork}
     */
    public VLANNetwork getPrivateNetwork(final Integer vdcId, final Integer vlanId)
    {
        VirtualDatacenter vdc = getVirtualDatacenter(vdcId);
        VLANNetwork vlan = getPrivateVlan(vdc, vlanId);

        LOGGER.debug("Returning the VLAN entity with name '" + vlan.getName() + "'.");
        return vlan;
    }

    /**
     * Return the private networks defined in a Virtual Datacenter.
     * 
     * @param virtualDatacenterId identifier of the virtual datacenter.
     * @return a Collection of {@link VLANNetwork} defined inside the Virtual Datacenter.
     */
    public Collection<VLANNetwork> getPrivateNetworks(final Integer vdcId)
    {
        VirtualDatacenter vdc = getVirtualDatacenter(vdcId);

        Collection<VLANNetwork> networks = null;
        networks = repo.findVlansByVirtualDatacener(vdc);
        LOGGER.debug("Returning the list of internal VLANs for VirtualDatacenter '" + vdc.getName()
            + "'.");

        return networks;
    }

    /**
     * Get a single {@link VMNetworkConfiguration} for a given Virtual Machine configuration id.
     * 
     * @param vdcId Identifier of the VirtualDatacenter.
     * @param vappId Identifier of the Virtual Appliance.
     * @param vmId Identifier of the Virtual Machine.
     * @param vmConfigId Identifier of the configuration value.
     * @return
     */
    public VMNetworkConfiguration getVirtualMachineConfiguration(final Integer vdcId,
        final Integer vappId, final Integer vmId, final Integer vmConfigId)
    {
        VirtualDatacenter vdc = getVirtualDatacenter(vdcId);
        VirtualAppliance vapp = getVirtualAppliance(vdc, vappId);
        VirtualMachine vm = getVirtualMachine(vapp, vmId);

        // Generally there is only one IP, but we avoid problemes and
        // we return IPs
        List<IpPoolManagement> ips =
            repo.findIpsWithConfigurationIdInVirtualMachine(vm, vmConfigId);
        if (ips == null || ips.isEmpty())
        {
            addNotFoundErrors(APIError.VLANS_NON_EXISTENT_CONFIGURATION);
            flushErrors();
        }

        // The configuration is the same for all the Ips found. Check if there is any with
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

        LOGGER
            .debug("Returning one of the Virtual Machine Configurations available by Virtual Machine '"
                + vm.getName() + "'.");
        return vmconfig;
    }

    /**
     * Return the list of NetworkConfiguration objects for a given virtual machine.
     * 
     * @param vdcId Identifier of the Virtual Datacenter.
     * @param vappId Identifier of the Virtual Appliance.
     * @param vmId Identifier of the Virtual Machine.
     * @return the found list of {@link VMNetworkConfiguration}.
     */
    public List<VMNetworkConfiguration> getVirtualMachineConfigurations(final Integer vdcId,
        final Integer vappId, final Integer vmId)
    {
        // Check the parameter's correctness
        VirtualDatacenter vdc = getVirtualDatacenter(vdcId);
        VirtualAppliance vapp = getVirtualAppliance(vdc, vappId);
        VirtualMachine vm = getVirtualMachine(vapp, vmId);

        // Find all the IPs for the physical machine
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

            // if its the same configuration fails in the case when you have set used true in one
            // and in the other its false
            if (!configs.contains(vmconfig))
            {
                configs.add(vmconfig);
            }
            else
            {
                if (vmconfig.getUsed())
                {
                    configs.remove(vmconfig);
                    configs.add(vmconfig);
                }
            }
        }

        LOGGER.debug("Returning the list of Virtual Machine Configurations for machine '"
            + vm.getName() + "'.");
        return configs;
    }

    /**
     * Remove a NIC from a Virtual Machine and reorder the rest of NICs with 'order' value greater
     * than it.
     * 
     * @param vdcId Identifier of the Virtual Datacenter.
     * @param vappId Identifier of the Virtual Appliance.
     * @param vmId Identifier of the Virtual Machine.
     * @param nicOrder NIC of the Virtual Machine identified by its 'order' value.
     */
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public void releaseNicFromVirtualMachine(final Integer vdcId, final Integer vappId,
        final Integer vmId, final Integer nicOrder)
    {
        VirtualDatacenter vdc = getVirtualDatacenter(vdcId);
        VirtualAppliance vapp = getVirtualAppliance(vdc, vappId);
        VirtualMachine vm = getVirtualMachine(vapp, vmId);

        // The user has the role for manage This. But... is the user from the same enterprise
        // than Virtual Datacenter?
        userService.checkCurrentEnterpriseForPostMethods(vdc.getEnterprise());

        // Check if the machine is in the correct state to perform the action.
        if (!vm.getState().equals(State.NOT_DEPLOYED))
        {
            addConflictErrors(APIError.VIRTUAL_MACHINE_INCOHERENT_STATE);
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
                    if (ip.isExternalIp())
                    {
                        // set virtual datacenter as null when release an external IP.
                        ip.setVirtualDatacenter(null);
                        ip.setName(null);
                        ip.setMac(null);
                    }
                    Boolean privateIp = ip.isPrivateIp(); // set the private value before to set the
                    // RASD to null;
                    ip.setRasd(null);
                    repo.updateIpManagement(ip);

                    found = Boolean.TRUE;

                    String messageTrace =
                        "Virtual Machine '" + vm.getName()
                            + "' has released the NIC associated to IP Address '" + ip.getIp()
                            + "' from VLAN '" + ip.getNetworkName() + "'";
                    if (tracer != null)
                    {
                        if (privateIp)
                        {
                            tracer.log(SeverityType.INFO, ComponentType.VIRTUAL_MACHINE,
                                EventType.NIC_RELEASED_VIRTUAL_MACHINE, messageTrace);
                        }
                        else
                        {
                            tracer.log(SeverityType.INFO, ComponentType.VIRTUAL_MACHINE,
                                EventType.PUBLIC_IP_UNASSIGN, messageTrace);
                        }
                    }
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

    /**
     * Reorder a NIC from 'linkOldOrder' to 'nicOrder'. Reorder the rest of affected NICs.
     * 
     * @param vdcId Identifier of the Virtual Datacenter.
     * @param vappId Identifier of the Virtual Appliance.
     * @param vmId Identifier of the Virtual Machine.
     * @param linkOldOrder old 'order' value of the NIC.
     * @param nicOrder new 'order' value of the NIC.
     */
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public void reorderVirtualMachineNic(final Integer vdcId, final Integer vappId,
        final Integer vmId, final Integer linkOldOrder, final Integer nicOrder)
    {
        // Find if the parameters exist.
        VirtualDatacenter vdc = getVirtualDatacenter(vdcId);
        VirtualAppliance vapp = getVirtualAppliance(vdc, vappId);
        VirtualMachine vm = getVirtualMachine(vapp, vmId);

        // The user has the role for manage this. But... is the user from the same enterprise
        // than Virtual Datacenter?
        userService.checkCurrentEnterpriseForPostMethods(vdc.getEnterprise());

        // Check if the machine is in the correct state to perform the action.
        if (!vm.getState().equals(State.NOT_DEPLOYED))
        {
            addConflictErrors(APIError.VIRTUAL_MACHINE_INCOHERENT_STATE);
            flushErrors();
        }

        List<IpPoolManagement> ips = repo.findIpsByVirtualMachine(vm);
        // If the order is bigger or equal than the size, then
        // the resource does not exist. Ex: size = 2 -> nicOrder = 2 -> (ERROR! the order begins
        // with 0 and if the size is 2, the available values are 0,1.
        if (nicOrder >= ips.size() || linkOldOrder >= ips.size() || linkOldOrder < 0)
        {
            addNotFoundErrors(APIError.VLANS_NIC_NOT_FOUND);
            flushErrors();
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

        LOGGER.debug("Reordering NICs into a Virtual Machine '" + vm.getName()
            + "' finished successfully");
    }

    /**
     * Set one of the internal networks of the Virtual Datacenter as the default one.
     * 
     * @param vdcId identifier of the virtual datacenter.
     * @param vlanId identifier of the vlan to be the default one.
     */
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public void setInternalNetworkAsDefaultInVirtualDatacenter(final Integer vdcId,
        final Integer vlanId)
    {
        VirtualDatacenter vdc = getVirtualDatacenter(vdcId);
        VLANNetwork vlan = getPrivateVlan(vdc, vlanId);

        if (vlan == null)
        {
            addNotFoundErrors(APIError.VLANS_NON_EXISTENT_VIRTUAL_NETWORK);
        }

        vdc.setDefaultVlan(vlan);
        repo.update(vdc);

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
        VirtualDatacenter vdc = getVirtualDatacenter(vdcId);
        VLANNetwork oldNetwork = getPrivateVlan(vdc, vlanId);

        newNetwork.setNetwork(vdc.getNetwork());
        newNetwork.setType(oldNetwork.getType());
        validate(newNetwork);
        if (!vlanId.equals(newNetwork.getId()))
        {
            addValidationErrors(APIError.INCOHERENT_IDS);
            flushErrors();
        }
        validate(newNetwork.getConfiguration());

        // The user has the role for manage This. But... is the user from the same enterprise
        // than Virtual Datacenter?
        userService.checkCurrentEnterpriseForPostMethods(vdc.getEnterprise());

        // Values 'address', 'mask', and 'tag' can not be changed by the edit process
        if (!oldNetwork.getConfiguration().getAddress().equalsIgnoreCase(
            newNetwork.getConfiguration().getAddress())
            || !oldNetwork.getConfiguration().getMask().equals(
                newNetwork.getConfiguration().getMask())
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

        // Check the new gateway is inside the range of IPs.
        if (!newNetwork.getConfiguration().getGateway().equalsIgnoreCase(
            oldNetwork.getConfiguration().getGateway()))
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

        repo.updateVlan(oldNetwork);

        if (tracer != null)
        {
            // Trace and log message.
            String messageTrace =
                "The Private VLAN with name '" + oldNetwork.getName()
                    + "' has been modified in Virtual Datacenter " + vdc.getName();
            tracer.log(SeverityType.INFO, ComponentType.NETWORK, EventType.VLAN_EDITED,
                messageTrace);
        }

        return oldNetwork;
    }

    /**
     * Updates a Virtual Machine configuration. In fact, the only attribute to be updated is 'used'.
     * That means this method is used to mark a single Virtual Machine Configuration as the default
     * configuration to be used by the machine in network terms.
     * 
     * @param vdcId Identifier of the Virtual Datacenter.
     * @param vappId Identifier of the Virtual Appliance.
     * @param vmId Identifier of the Virtual Machine.
     * @param vmConfigId Identifier of the Configuration.
     * @param vmConfig New configuration to apply.
     * @return the updated {@VMNetworkConfiguration} object.
     */
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

        // Recover the virtual machine for trace purposes
        VirtualMachine vm = repo.findVirtualMachineById(vmId);

        // The user has the role for manage This. But... is the user from the same enterprise
        // than Virtual Datacenter?
        userService.checkCurrentEnterpriseForPostMethods(repo.findById(vdcId).getEnterprise());

        // Check if the machine is in the correct state to perform the action.
        if (!vm.getState().equals(State.NOT_DEPLOYED))
        {
            addConflictErrors(APIError.VIRTUAL_MACHINE_INCOHERENT_STATE);
            flushErrors();
        }

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
            List<IpPoolManagement> ips =
                repo.findIpsByVirtualMachine(repo.findVirtualMachineById(vmId));

            if (!vmConfig.getUsed())
            {
                // // That means : before it was the default configuration and now it doesn't.
                // // Raise an exception: it should be at least one network configuration.
                // addConflictErrors(APIError.VIRTUAL_MACHINE_AT_LEAST_ONE_USED_CONFIGURATION);
                // flushErrors();

                for (IpPoolManagement ip : ips)
                {
                    ip.setConfigureGateway(Boolean.FALSE);
                    repo.updateIpManagement(ip);
                }
            }
            // }

            // If we have arrived here, that means the 'used' configuration has changed and
            // user wants to use a new configuration. Update the corresponding 'configureGateway' in
            // the IPs.
            else
            {
                Boolean foundIpConfigureGateway = Boolean.FALSE;

                for (IpPoolManagement ip : ips)
                {
                    if (!foundIpConfigureGateway)
                    {
                        if (ip.getVlanNetwork().getConfiguration().getId().equals(vmConfigId)
                            && vmConfig.getGateway() != null)
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
        }

        if (tracer != null)
        {
            String messageTrace =
                "Virtual Machine '" + vm.getName() + "' has updated its default configuration";

            tracer.log(SeverityType.INFO, ComponentType.NETWORK,
                EventType.NETWORK_CONFIGURATION_UPDATED, messageTrace);
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
     * Check if the informed IP address and mask are correct for to be private IPs. Check also if
     * the netmask and the address are coherent.
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
     * Create the {@link Dhcp} object and store all the IPs of the network in database.
     * 
     * @param datacenter Datacenter where the network is created. Needed to get the DHCP.
     * @param vdc Virtual Dataceneter where the network are assigned. Can be null for public
     *            networks.
     * @param vlan VLAN to assign its ips.
     * @param range List of ips to create inside the DHCP
     * @return The created {@link Dhpc} object
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
                new IpPoolManagement(dhcp, vlan, macAddress, name, address.toString(), vlan
                    .getName(), type);

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

    /**
     * Gets a private Vlan. Raises a NOT_FOUND exception if it does not exist.
     * 
     * @param vdc {@link VirtualDatacenter} instance where the Vlan should be.
     * @param vlanId identifier of the {@link VLANNetwork} instance.
     * @return the found {@link VLANNetwork} instance.
     */
    protected VLANNetwork getPrivateVlan(final VirtualDatacenter vdc, final Integer vlanId)
    {
        VLANNetwork vlan = repo.findVlanByVirtualDatacenterId(vdc, vlanId);
        if (vlan == null)
        {
            addNotFoundErrors(APIError.VLANS_NON_EXISTENT_VIRTUAL_NETWORK);
            flushErrors();
        }
        return vlan;
    }

    /**
     * Gets a Virtual Appliance. Raises a NOT_FOUND exception if it does not exist.
     * 
     * @param vdc {@link VirtualDatacenter} instance where the vapp should be.
     * @param vappId identifier of the {@link VirtualAppliance} instance.
     * @return the found {@link VirtualAppliance} instance.
     */
    protected VirtualAppliance getVirtualAppliance(final VirtualDatacenter vdc, final Integer vappId)
    {
        VirtualAppliance vapp = repo.findVirtualApplianceById(vdc, vappId);
        if (vapp == null)
        {
            addNotFoundErrors(APIError.NON_EXISTENT_VIRTUALAPPLIANCE);
            flushErrors();
        }
        return vapp;
    }

    /**
     * Gets a VirtualDatacenter. Raises an exception if it does not exist.
     * 
     * @param vdcId identifier of the virtual datacenter.
     * @return the found {@link VirtualDatacenter} instance.
     */
    protected VirtualDatacenter getVirtualDatacenter(final Integer vdcId)
    {
        VirtualDatacenter vdc = repo.findById(vdcId);
        if (vdc == null)
        {
            addNotFoundErrors(APIError.NON_EXISTENT_VIRTUAL_DATACENTER);
            flushErrors();
        }
        return vdc;
    }

    /**
     * Gets a Virtual Machine. Raises a NOT_FOUND exception if it does not exist.
     * 
     * @param vapp {@link VirtualAppliance} instance where the VirtualMachine should be.
     * @param vmId identifier of the {@link VirtualMachine} instance.
     * @return the found {@link VirtualMachine} instance.
     */
    protected VirtualMachine getVirtualMachine(final VirtualAppliance vapp, final Integer vmId)
    {
        VirtualMachine vm = repo.findVirtualMachineById(vapp, vmId);
        if (vm == null)
        {
            addNotFoundErrors(APIError.NON_EXISTENT_VIRTUALMACHINE);
            flushErrors();
        }
        return vm;

    }

}
