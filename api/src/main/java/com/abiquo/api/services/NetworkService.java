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

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
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
import com.abiquo.api.services.cloud.VirtualMachineService;
import com.abiquo.api.tracer.TracerLogger;
import com.abiquo.model.enumerator.NetworkType;
import com.abiquo.model.transport.LinksDto;
import com.abiquo.model.transport.error.CommonError;
import com.abiquo.server.core.cloud.VirtualAppliance;
import com.abiquo.server.core.cloud.VirtualDatacenter;
import com.abiquo.server.core.cloud.VirtualDatacenterRep;
import com.abiquo.server.core.cloud.VirtualMachine;
import com.abiquo.server.core.cloud.VirtualMachineState;
import com.abiquo.server.core.enterprise.DatacenterLimits;
import com.abiquo.server.core.enterprise.EnterpriseRep;
import com.abiquo.server.core.infrastructure.Datacenter;
import com.abiquo.server.core.infrastructure.InfrastructureRep;
import com.abiquo.server.core.infrastructure.Machine;
import com.abiquo.server.core.infrastructure.management.Rasd;
import com.abiquo.server.core.infrastructure.network.DhcpOption;
import com.abiquo.server.core.infrastructure.network.DhcpOptionDto;
import com.abiquo.server.core.infrastructure.network.IpPoolManagement;
import com.abiquo.server.core.infrastructure.network.NetworkConfiguration;
import com.abiquo.server.core.infrastructure.network.VLANNetwork;
import com.abiquo.server.core.infrastructure.network.VMNetworkConfiguration;
import com.abiquo.server.core.util.network.IPAddress;
import com.abiquo.server.core.util.network.IPNetworkRang;
import com.abiquo.server.core.util.network.NetworkResolver;
import com.abiquo.tracer.ComponentType;
import com.abiquo.tracer.EventType;
import com.abiquo.tracer.SeverityType;

@Service
public class NetworkService extends DefaultApiService
{
    /** Static literal for 'FENCE_MODE' values. */
    public static final String FENCE_MODE = "bridge";

    /** Logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(NetworkService.class);

    /**
     * Prepares the {@link Rasd} entity regarding on the virtual machine and the ip we are
     * assigning. It's up to the method that calls this entity either save the Rasd or not.
     * 
     * @param vm {@link VirtualMachine} entity where the IP will belong to.
     * @param ip {@link IpPoolManagement} entity that will store this rasd.
     * @return the created Rasd entity.
     */
    public static Rasd createRasdEntity(final VirtualMachine vm, final IpPoolManagement ip)
    {
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
        rasd.setAddress(ip.getMac());
        rasd.setParent(ip.getNetworkName());
        rasd.setResourceSubType(String.valueOf(defineIpType(ip.getVlanNetwork()).ordinal()));

        return rasd;
    }

    private static IpPoolManagement.Type defineIpType(final VLANNetwork vlan)
    {
        switch (vlan.getType())
        {
            case INTERNAL:
                return IpPoolManagement.Type.PRIVATE;
            case PUBLIC:
                return IpPoolManagement.Type.PUBLIC;
            case EXTERNAL:
                return IpPoolManagement.Type.EXTERNAL;
            default:
                return IpPoolManagement.Type.UNMANAGED;
        }
    }

    /** Autowired infrastructure DAO repository. */
    @Autowired
    protected InfrastructureRep datacenterRepo;

    @Autowired
    protected EnterpriseRep entRep;

    /** Autowired Virtual Infrastructure DAO repository. */
    @Autowired
    protected VirtualDatacenterRep repo;

    /** Autowired tracer logger. */
    @Autowired
    protected TracerLogger tracer;

    /** User service for user-specific privileges */
    @Autowired
    protected UserService userService;

    @Autowired
    protected VirtualMachineService vmService;

    /**
     * Default constructor. Needed by @Autowired injections
     */
    public NetworkService()
    {

    }

    /**
     * Auxiliar constructor for test purposes. Haters gonna hate 'bzengine'. And his creator as
     * well...
     * 
     * @param em {@link EntityManager} instance with active transaction.
     */
    public NetworkService(final EntityManager em)
    {
        repo = new VirtualDatacenterRep(em);
        datacenterRepo = new InfrastructureRep(em);
        userService = new UserService(em);
        // TODO initialize the tracer
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public DhcpOption addDhcpOption(final DhcpOptionDto dto)
    {
        DhcpOption opt =
            new DhcpOption(dto.getOption(),
                dto.getGateway(),
                dto.getNetworkAddress(),
                dto.getMask(),
                dto.getNetmask());

        if (!opt.isValid())
        {
            addValidationErrors(opt.getValidationErrors());
            flushErrors();
        }

        datacenterRepo.insertDhcpOption(opt);
        return opt;
    }

    /**
     * Assign the default NIC to a Virtual Machine. Depending on which vlan type is, we should do an
     * action, or another one. This method will be only called from another services, so we
     * understand we don't have to check the NotFound case.
     * 
     * @param vmId identifier of the Virtual Machine.
     */
    public void assignDefaultNICToVirtualMachine(final Integer vmId)
    {
        // Get the needed objects.
        VirtualMachine vm = repo.findVirtualMachineById(vmId);
        VirtualAppliance vapp = repo.findVirtualApplianceByVirtualMachine(vm);
        VirtualDatacenter vdc = vapp.getVirtualDatacenter();

        VLANNetwork vlan = vdc.getDefaultVlan();

        IpPoolManagement ip = null;
        switch (vlan.getType())
        {
            case INTERNAL:
                // find next available IP to use.
                ip = repo.findNextIpAvailable(vlan.getId(), vlan.getConfiguration().getGateway());

                break;

            case UNMANAGED:

                ip = new IpPoolManagement(vlan, "?", "?", "?", vlan.getName());
                ip.setVirtualDatacenter(vdc);
                ip.setMac(IPNetworkRang.requestRandomMacAddress(vdc.getHypervisorType()));
                ip.setName(ip.getMac() + "_host");
                repo.insertIpManagement(ip);

                break;

            default:
                ip =
                    repo.findNextExternalIpAvailable(vlan.getId(), vlan.getConfiguration()
                        .getGateway());
                ip.setVirtualDatacenter(vdc);
                ip.setMac(IPNetworkRang.requestRandomMacAddress(vdc.getHypervisorType()));
                ip.setName(ip.getMac() + "_host");
        }

        Rasd rasd = createRasdEntity(vm, ip);

        repo.insertRasd(rasd);

        ip.setRasd(rasd);
        ip.attach(0, vm, vapp);

        repo.updateIpManagement(ip);

        vm.setNetworkConfiguration(vlan.getConfiguration());
        repo.updateVirtualMachine(vm);

        return;
    }

    /**
     * Attach a list of NICs to a virtual machine.
     * <p>
     * If the virtual machine is not deployed, the method simply returns <code>null</code>. If the
     * virtual machine is deployed, the attachment will run a reconfigure operation and this method
     * will return the identifier of the task object associated to the reconfigure operation.
     * 
     * @param vdcId identifier of the virtual datacenter.
     * @param vappId identifier of the virtual appliance
     * @param vmId identifier of the virtual machine
     * @param nicRefs list of links to disks to attach.
     * @return The id of the Tarantino task if the virtual machine is deployed, <code>null</code>
     *         otherwise.
     */
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public Object attachNICs(final Integer vdcId, final Integer vappId, final Integer vmId,
        final LinksDto nicRefs, final VirtualMachineState originalState)
    {
        VirtualDatacenter vdc = getVirtualDatacenter(vdcId);
        VirtualAppliance vapp = getVirtualAppliance(vdc, vappId);
        VirtualMachine oldvm = getVirtualMachine(vapp, vmId);

        VirtualMachine newvm = vmService.duplicateVirtualMachineObject(oldvm);
        List<IpPoolManagement> ips = vmService.getNICsFromDto(vdc, nicRefs);
        if (0 == ips.size())
        {
            addValidationErrors(APIError.VIRTUAL_MACHINE_AT_LEAST_ONE_NIC_SHOULD_BE_LINKED);
            flushErrors();
        }

        checkIps(ips);

        newvm.getIps().addAll(ips);

        return vmService.reconfigureVirtualMachine(vdc, vapp, oldvm, newvm, originalState);
    }

    private void checkIps(final List<IpPoolManagement> ips)
    {
        for (IpPoolManagement ip : ips)
        {
            if (ip.getQuarantine())
            {
                LOGGER.debug("Cannot attach ip " + ip.toString()
                    + " to a virtual machine because the ip is in quarantine");
                addConflictErrors(new CommonError(APIError.VLANS_IP_IS_IN_QUARANTINE.getCode(),
                    String.format(APIError.VLANS_IP_IS_IN_QUARANTINE.getMessage(), ip.getIp())));
                flushErrors();
            }
        }

    }

    /**
     * Change the default network configuration of a virtual machine.
     * <p>
     * If the virtual machine is not deployed, the method simply returns <code>null</code>. If the
     * virtual machine is deployed, the attachment will run a reconfigure operation and this method
     * will return the identifier of the task object associated to the reconfigure operation. If the @param
     * configurationRef is an empty list, we will set no network configuration to this machine.
     * Stupid behavior, but we allow it.
     * 
     * @param vdcId identifier of the virtual datacenter.
     * @param vappId identifier of the virtual appliance
     * @param vmId identifier of the virtual machine
     * @param configurationRef the link to the available configuration.
     * @return The id of the Tarantino task if the virtual machine is deployed, <code>null</code>
     *         otherwise.
     */
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public Object changeNetworkConfiguration(final Integer vdcId, final Integer vappId,
        final Integer vmId, final LinksDto configurationRef, final VirtualMachineState originalState)
    {
        VirtualDatacenter vdc = getVirtualDatacenter(vdcId);
        VirtualAppliance vapp = getVirtualAppliance(vdc, vappId);
        VirtualMachine oldvm = getVirtualMachine(vapp, vmId);

        VirtualMachine newvm = vmService.duplicateVirtualMachineObject(oldvm);
        NetworkConfiguration netconf =
            vmService.getNetworkConfigurationFromDto(vapp, newvm, configurationRef);

        newvm.setNetworkConfiguration(netconf);

        return vmService.reconfigureVirtualMachine(vdc, vapp, oldvm, newvm, originalState);
    }

    /**
     * Change the list of NICs to a virtual machine.
     * <p>
     * If the virtual machine is not deployed, the method simply returns <code>null</code>. If the
     * virtual machine is deployed, the attachment will run a reconfigure operation and this method
     * will return the identifier of the task object associated to the reconfigure operation.
     * 
     * @param vdcId identifier of the virtual datacenter.
     * @param vappId identifier of the virtual appliance
     * @param vmId identifier of the virtual machine
     * @param nicRefs list of links to disks to attach.
     * @return The id of the Tarantino task if the virtual machine is deployed, <code>null</code>
     *         otherwise.
     */
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public Object changeNICs(final Integer vdcId, final Integer vappId, final Integer vmId,
        final LinksDto nicRefs, final VirtualMachineState originalState)
    {
        VirtualDatacenter vdc = getVirtualDatacenter(vdcId);
        VirtualAppliance vapp = getVirtualAppliance(vdc, vappId);
        VirtualMachine oldvm = getVirtualMachine(vapp, vmId);

        VirtualMachine newvm = vmService.duplicateVirtualMachineObject(oldvm);
        List<IpPoolManagement> ips = vmService.getNICsFromDto(vdc, nicRefs);
        if (0 == ips.size())
        {
            addValidationErrors(APIError.VIRTUAL_MACHINE_AT_LEAST_ONE_NIC_SHOULD_BE_LINKED);
            flushErrors();
        }

        newvm.setIps(ips);

        return vmService.reconfigureVirtualMachine(vdc, vapp, oldvm, newvm, originalState);
    }

    /**
     * Creates a new Private Network.
     * 
     * @param virtualDatacenterId Identifier of the Virtual Datacenter
     * @param newVlan {@link VLANNetwork} object with the new parameters to create.
     * @return the created {@link VLANNetwork} object.
     */
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public VLANNetwork createPrivateNetwork(final Integer vdcId, final VLANNetwork newVlan,
        final Boolean defaultVlan)
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
        checkAddressAndMaskCoherency(
            IPAddress.newIPAddress(newVlan.getConfiguration().getAddress()), newVlan
                .getConfiguration().getMask());

        List<DhcpOption> opts = new ArrayList<DhcpOption>(newVlan.getDhcpOption());
        for (DhcpOption dhcpOption : newVlan.getDhcpOption())
        {
            dhcpOption.setOption(121);
            dhcpOption.setMask(getMaskbyNetMask(dhcpOption.getNetmask()));
            if (dhcpOption.getMask() != 0)
            {
                datacenterRepo.insertDhcpOption(dhcpOption);
                DhcpOption dhcpOption2 =
                    new DhcpOption(249,
                        dhcpOption.getGateway(),
                        dhcpOption.getNetworkAddress(),
                        dhcpOption.getMask(),
                        dhcpOption.getNetmask());
                datacenterRepo.insertDhcpOption(dhcpOption2);
                opts.add(dhcpOption2);
            }
            else
            {
                opts.remove(dhcpOption);
            }
        }
        newVlan.setDhcpOption(opts);
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

        // store the dhcp and all the ips.
        storeIPs(virtualDatacenter.getDatacenter(), virtualDatacenter, newVlan, range);
        // Trace
        if (tracer != null)
        {
            tracer.log(SeverityType.INFO, ComponentType.NETWORK, EventType.VLAN_CREATED,
                "privateVlan.created", newVlan.getName(), virtualDatacenter.getName());
        }
        if (defaultVlan != null && defaultVlan == true)
        {
            setInternalNetworkAsDefaultInVirtualDatacenter(vdcId, newVlan.getId());
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
        datacenterRepo.deleteAllDhcpOption(vlanToDelete.getDhcpOption());

        if (tracer != null)
        {
            tracer.log(SeverityType.INFO, ComponentType.NETWORK, EventType.VLAN_DELETED,
                "privateVlan.deleted", vlanToDelete.getName());
        }
    }

    /**
     * Detach all the list of NICs from a Virtual Machine.
     * <p>
     * If the virtual machine is not deployed, the method simply returns <code>null</code>. If the
     * virtual machine is deployed, the detachment will run a reconfigure operation and this method
     * will return the identifier of the task object associated to the reconfigure operation.
     * 
     * @param vdcId identifier of the virtual datacenter.
     * @param vappId identifier of the virtual appliance
     * @param vmId identifier of the virtual machine
     * @return The id of the Tarantino task if the virtual machine is deployed, <code>null</code>
     *         otherwise.
     */
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public Object detachNIC(final Integer vdcId, final Integer vappId, final Integer vmId,
        final Integer nicId, final VirtualMachineState originalState)
    {
        VirtualDatacenter vdc = getVirtualDatacenter(vdcId);
        VirtualAppliance vapp = getVirtualAppliance(vdc, vappId);
        VirtualMachine vm = getVirtualMachine(vapp, vmId);

        List<IpPoolManagement> ips = repo.findIpsByVirtualMachine(vm);
        if (ips.size() == 1)
        {
            addConflictErrors(APIError.VLANS_CAN_NOT_DETACH_LAST_NIC);
            flushErrors();
        }

        IpPoolManagement ipToDetach = repo.findIpByVirtualMachine(vm, nicId);
        if (ipToDetach == null)
        {
            addNotFoundErrors(APIError.NON_EXISTENT_IP);
            flushErrors();
        }

        VirtualMachine newVm = vmService.duplicateVirtualMachineObject(vm);
        Iterator<IpPoolManagement> ipIterator = newVm.getIps().iterator();
        while (ipIterator.hasNext())
        {
            IpPoolManagement currentIp = ipIterator.next();
            if (currentIp.getRasd().equals(ipToDetach.getRasd()))
            {
                ipIterator.remove();
                return vmService.reconfigureVirtualMachine(vdc, vapp, vm, newVm, originalState);
            }
        }

        addUnexpectedErrors(APIError.NON_EXISTENT_IP);
        flushErrors();

        return null;
    }

    public Collection<DhcpOption> findAllDhcpOptions()
    {
        return datacenterRepo.findAllDhcp();
    }

    /**
     * Get the default network for virtual datacenter.
     * 
     * @param id identifier of the virtual datacenter
     * @return the requested {@link VLANNetwork}
     */
    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public VLANNetwork getDefaultNetworkForVirtualDatacenter(final Integer vdcId)
    {
        VirtualDatacenter vdc = getVirtualDatacenter(vdcId);

        if (vdc.getDefaultVlan() == null)
        {
            addUnexpectedErrors(APIError.VLANS_VIRTUAL_DATACENTER_SHOULD_HAVE_A_DEFAULT_VLAN);
            flushErrors();
        }

        LOGGER.debug("Returning the default network used by Virtual Datacenter '" + vdc.getName()
            + "'.");
        return vdc.getDefaultVlan();
    }

    public DhcpOption getDhcpOption(final Integer id)
    {
        DhcpOption option = datacenterRepo.findDhcpOptionById(id);
        if (option == null)
        {
            addNotFoundErrors(APIError.NON_EXISTENT_DHCP_OPTION);
            flushErrors();
        }

        return option;
    }

    /**
     * Asks for an IP managed by a Virtual Virtual Machine.
     * 
     * @param vdcId identifier of the Virtual Datacenter.
     * @param vappId identifier of the Virtual Appliance.
     * @param vmId identifier of the Virtual Machine.
     * @param nicId identifier of the IP to return
     * @return the list of matching elements.
     */
    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public IpPoolManagement getIpPoolManagementByVirtualMachine(final Integer vdcId,
        final Integer vappId, final Integer vmId, final Integer nicId)
    {
        VirtualDatacenter vdc = getVirtualDatacenter(vdcId);
        VirtualAppliance vapp = getVirtualAppliance(vdc, vappId);
        VirtualMachine vm = getVirtualMachine(vapp, vmId);

        IpPoolManagement ip = repo.findIpByVirtualMachine(vm, nicId);
        if (ip == null)
        {
            addNotFoundErrors(APIError.NON_EXISTENT_IP);
            flushErrors();
        }
        if (ip.getVlanNetwork().getEnterprise() != null)
        {
            // needed for REST links.
            DatacenterLimits dl =
                datacenterRepo.findDatacenterLimits(ip.getVlanNetwork().getEnterprise(),
                    vdc.getDatacenter());
            ip.getVlanNetwork().setLimitId(dl.getId());
        }

        LOGGER.debug("Returning the list of IPs used by Virtual Machine '" + vm.getName() + "'.");
        return ip;
    }

    /**
     * Retrieve a private IP object.
     * 
     * @param vdcId identifier of the {@link VirtualDatacenter}
     * @param vlanId identifier of the {@link VLANNetwork}
     * @param ipId identifier of the {@link IpPoolManagement} object to retrieve.
     * @return the found object.
     */
    public IpPoolManagement getIpPoolManagementByVlan(final Integer vdcId, final Integer vlanId,
        final Integer ipId)
    {
        VirtualDatacenter vdc = getVirtualDatacenter(vdcId);
        VLANNetwork vlan = getPrivateVlan(vdc, vlanId);
        IpPoolManagement ip = repo.findIp(vlan, ipId);

        if (ip == null)
        {
            addNotFoundErrors(APIError.NON_EXISTENT_IP);
            flushErrors();
        }

        LOGGER.debug("Returning the private Ip Address with id '" + ip.getId() + "'.");

        return ip;
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
    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
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
    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public List<IpPoolManagement> getListIpPoolManagementByVdc(final Integer vdcId,
        final Integer firstElem, final Integer numElem, final String has, final String orderBy,
        final Boolean asc, final String type, final Boolean all)
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

        // Acquire the Virtual Datacenter
        VirtualDatacenter vdc = repo.findById(vdcId);
        if (vdc == null)
        {
            addNotFoundErrors(APIError.NON_EXISTENT_VIRTUAL_DATACENTER);
            flushErrors();
        }

        NetworkType netType = NetworkType.INTERNAL;
        if (type != null && !type.equals("false") && !type.equals("INTERNAL"))
        {
            netType = NetworkType.fromValue(type);
            if (netType == null || netType.equals(NetworkType.INTERNAL))
            {
                LOGGER
                    .info("Bad parameter 'type' in request to get the public networks by a datacenter.");
                addValidationErrors(APIError.QUERY_NETWORK_TYPE_INVALID_PARAMETER);
                flushErrors();
            }
        }

        // Query the list to database.
        if (netType.equals(NetworkType.EXTERNAL_UNMANAGED))
        {
            // get the enterprise and datacenter and get the external and unmanaged ips
            List<IpPoolManagement> ips =
                repo.findPublicIpsByEnterprise(vdc.getDatacenter().getId(), vdc.getEnterprise()
                    .getId(), firstElem, numElem, has, orderByEnum, asc, netType, all);
            LOGGER
                .debug("Returning the list of external and unmanaged IPs used by VirtualDatacenter '"
                    + vdc.getName() + "'.");
            return ips;
        }
        else
        {
            List<IpPoolManagement> ips =
                repo.findIpsByVdc(vdcId, firstElem, numElem, has, orderByEnum, asc, netType);
            LOGGER.debug("Returning the list of private IPs used by VirtualDatacenter '"
                + vdc.getName() + "'.");
            return ips;
        }

    }

    /**
     * Asks for all the Private IPs managed by a Virtual Appliance.
     * 
     * @param vapp Virtual Appliance object.
     * @return the list of matching elements.
     */
    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
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
    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public List<IpPoolManagement> getListIpPoolManagementByVirtualMachine(final Integer vdcId,
        final Integer vappId, final Integer vmId)
    {
        VirtualDatacenter vdc = getVirtualDatacenter(vdcId);
        VirtualAppliance vapp = getVirtualAppliance(vdc, vappId);
        VirtualMachine vm = getVirtualMachine(vapp, vmId);

        List<IpPoolManagement> ips = repo.findIpsByVirtualMachine(vm);

        for (IpPoolManagement ip : ips)
        {
            Hibernate.initialize(ip.getVlanNetwork().getEnterprise());
            if (ip.getVlanNetwork().getEnterprise() != null)
            {
                // needed for REST links.
                DatacenterLimits dl =
                    datacenterRepo.findDatacenterLimits(ip.getVlanNetwork().getEnterprise(),
                        vdc.getDatacenter());
                ip.getVlanNetwork().setLimitId(dl.getId());
            }
        }

        LOGGER.debug("Returning the list of IPs used by Virtual Machine '" + vm.getName() + "'.");
        return ips;
    }

    public List<IpPoolManagement> getListIpPoolManagementByInfrastructureVirtualMachine(
        final Integer datacenterId, final Integer rackId, final Integer machineId,
        final Integer vmId)
    {
        Machine pm = datacenterRepo.findMachineByIds(datacenterId, rackId, machineId);
        if (pm == null)
        {
            addNotFoundErrors(APIError.NON_EXISTENT_MACHINE);
            flushErrors();
        }

        VirtualMachine vm = vmService.getVirtualMachineByHypervisor(pm.getHypervisor(), vmId);
        VirtualAppliance vapp = repo.findVirtualApplianceByVirtualMachine(vm);
        if (vapp == null)
        {
            // If vapp is 'null' it means the virtual machine does not belong
            // to any virtual appliance and hence, is an imported virtual machine.
            // since we don't manage NICs in imported Virtual Machines, return an empty lise
            return new ArrayList<IpPoolManagement>();
        }

        VirtualDatacenter vdc = vapp.getVirtualDatacenter();

        List<IpPoolManagement> ips = repo.findIpsByVirtualMachine(vm);

        for (IpPoolManagement ip : ips)
        {
            Hibernate.initialize(ip.getVlanNetwork().getEnterprise());
            if (ip.getVlanNetwork().getEnterprise() != null)
            {
                // needed for REST links.
                DatacenterLimits dl =
                    datacenterRepo.findDatacenterLimits(ip.getVlanNetwork().getEnterprise(),
                        vdc.getDatacenter());
                ip.getVlanNetwork().setLimitId(dl.getId());
            }
        }

        LOGGER.debug("Returning the list of IPs used by Virtual Machine '" + vm.getName() + "'.");
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
    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
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

        List<IpPoolManagement> ips =
            repo.findIpsByPrivateVLANFiltered(vdcId, vlanId, startwith, limit, filter, orderByEnum,
                descOrAsc, freeIps);

        LOGGER.debug("Returning the list of IPs used by private VLAN with id '" + vlanId + "'.");

        return ips;
    }

    /**
     * Retrieve a Private Network.
     * 
     * @param virtualdatacenterId identifier of the virtual datacenter.
     * @param vlanId identifier of the vlan.
     * @return an instance of the requested {@link VLANNetwork}
     */
    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public VLANNetwork getPrivateNetwork(final Integer vdcId, final Integer vlanId)
    {
        VirtualDatacenter vdc = getVirtualDatacenter(vdcId);
        VLANNetwork vlan = getPrivateVlan(vdc, vlanId);

        LOGGER.debug("Returning the private VLAN entity with name '" + vlan.getName() + "'.");
        return vlan;
    }

    /**
     * Return the private networks defined in a Virtual Datacenter.
     * 
     * @param virtualDatacenterId identifier of the virtual datacenter.
     * @return a Collection of {@link VLANNetwork} defined inside the Virtual Datacenter.
     */
    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public Collection<VLANNetwork> getPrivateNetworks(final Integer vdcId)
    {
        VirtualDatacenter vdc = getVirtualDatacenter(vdcId);

        Collection<VLANNetwork> networks = null;
        networks = repo.findVlansByVirtualDatacener(vdc);
        LOGGER.debug("Returning the list of private VLANs for VirtualDatacenter '" + vdc.getName()
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
    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public VMNetworkConfiguration getVirtualMachineConfiguration(final Integer vdcId,
        final Integer vappId, final Integer vmId, final Integer vmConfigId)
    {
        VirtualDatacenter vdc = getVirtualDatacenter(vdcId);
        VirtualAppliance vapp = getVirtualAppliance(vdc, vappId);
        VirtualMachine vm = getVirtualMachine(vapp, vmId);

        // Generally there is only one IP, but we avoid problemes and
        // we return IPs
        List<IpPoolManagement> ips = repo.findIpsWithConfigurationIdInVirtualMachine(vm);
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
            if (ip.getVlanNetwork().getConfiguration().getId()
                .equals(vm.getNetworkConfiguration().getId()))
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
        vmconfig.setUsed(vlan.getConfiguration().getId()
            .equals(vm.getNetworkConfiguration().getId()));
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
    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
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
            vmconfig.setUsed(ip.itHasTheDefaultConfiguration(vm));
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
        if (!vm.getState().equals(VirtualMachineState.NOT_ALLOCATED))
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

        if (tracer != null)
        {
            tracer.log(SeverityType.INFO, ComponentType.VIRTUAL_MACHINE,
                EventType.NIC_REORDER_VIRTUAL_MACHINE, "nic.reordered", vm.getName());
        }
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

        if (tracer != null)
        {
            tracer.log(SeverityType.INFO, ComponentType.VIRTUAL_DATACENTER, EventType.VLAN_DEFAULT,
                "vlan.default", vlan.getName(), vdc.getName());
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
        // set the dhcp option
        datacenterRepo.deleteAllDhcpOption(oldNetwork.getDhcpOption());
        List<DhcpOption> opts = new ArrayList<DhcpOption>(newNetwork.getDhcpOption());
        for (DhcpOption dhcpOption : newNetwork.getDhcpOption())
        {
            dhcpOption.setOption(121);
            dhcpOption.setMask(getMaskbyNetMask(dhcpOption.getNetmask()));
            if (dhcpOption.getMask() != 0)
            {
                datacenterRepo.insertDhcpOption(dhcpOption);
                DhcpOption dhcpOption2 =
                    new DhcpOption(249,
                        dhcpOption.getGateway(),
                        dhcpOption.getNetworkAddress(),
                        dhcpOption.getMask(),
                        dhcpOption.getNetmask());
                datacenterRepo.insertDhcpOption(dhcpOption2);
                opts.add(dhcpOption2);
            }
            else
            {
                opts.remove(dhcpOption);
            }
        }
        oldNetwork.setDhcpOption(opts);

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
            tracer.log(SeverityType.INFO, ComponentType.NETWORK, EventType.VLAN_EDITED,
                "privateVlan.updated", oldNetwork.getName(), vdc.getName());
        }

        return oldNetwork;
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
    protected void checkAddressAndMaskCoherency(final IPAddress netAddress, final Integer netmask)
    {

        // Parse the correct IP. (avoid 127.00.00.01), for instance
        IPAddress networkAddress = IPAddress.newIPAddress(netAddress.toString());

        // First of all, check if the networkAddress is correct.

        // if the value is a private network.
        if (netmask < 22)
        {
            addConflictErrors(APIError.VLANS_TOO_BIG_NETWORK);
            flushErrors();
        }

        if (!NetworkResolver.isValidNetworkMask(networkAddress, netmask))
        {
            addValidationErrors(APIError.VLANS_INVALID_NETWORK_AND_MASK);
            flushErrors();
        }

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

    /**
     * Store all the IPs of the network in database.
     * 
     * @param datacenter Datacenter where the network is created.
     * @param vdc Virtual Dataceneter where the network are assigned. Can be null for public
     *            networks.
     * @param vlan VLAN to assign its ips.
     * @param range List of ips to create inside the DHCP
     * @return
     */
    protected void storeIPs(final Datacenter datacenter, final VirtualDatacenter vdc,
        final VLANNetwork vlan, final Collection<IPAddress> range)
    {

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
                new IpPoolManagement(vlan, macAddress, name, address.toString(), vlan.getName());

            if (vdc != null)
            {
                // public network does not have VDC by default.
                ipManagement.setVirtualDatacenter(vdc);
            }

            repo.insertIpManagement(ipManagement);
        }

    }

    private Integer getMaskbyNetMask(final String netmask)
    {

        Inet4Address address;
        try
        {
            address = (Inet4Address) InetAddress.getByName(netmask);
            byte[] values = address.getAddress();
            int hex = values[0] << 24 | values[1] << 16 | values[2] << 8 | values[3] << 0;

            Integer mask = Integer.bitCount(hex);
            if (mask < 32)
            {
                return mask;
            }
        }
        catch (UnknownHostException e)
        {
            // invalid netmask
        }

        return 0;
    }

}
