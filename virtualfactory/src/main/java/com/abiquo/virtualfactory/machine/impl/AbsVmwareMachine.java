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
package com.abiquo.virtualfactory.machine.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.abiquo.util.ExtendedAppUtil;
import com.abiquo.virtualfactory.exception.VirtualMachineException;
import com.abiquo.virtualfactory.hypervisor.impl.VmwareHypervisor;
import com.abiquo.virtualfactory.machine.impl.vcenter.VCenterBridge;
import com.abiquo.virtualfactory.model.AbiCloudModel;
import com.abiquo.virtualfactory.model.AbsVirtualMachine;
import com.abiquo.virtualfactory.model.State;
import com.abiquo.virtualfactory.model.VirtualDiskType;
import com.abiquo.virtualfactory.model.config.VirtualMachineConfiguration;
import com.abiquo.virtualfactory.model.config.VmwareHypervisorConfiguration;
import com.abiquo.virtualfactory.network.VirtualNIC;
import com.vmware.vim25.GenericVmConfigFault;
import com.vmware.vim25.HostConfigInfo;
import com.vmware.vim25.HostConfigManager;
import com.vmware.vim25.HostNetworkInfo;
import com.vmware.vim25.HostNetworkPolicy;
import com.vmware.vim25.HostPortGroupSpec;
import com.vmware.vim25.HostVirtualSwitch;
import com.vmware.vim25.ManagedObjectReference;
import com.vmware.vim25.ResourceAllocationInfo;
import com.vmware.vim25.VirtualDeviceConfigSpec;
import com.vmware.vim25.VirtualMachineConfigSpec;
import com.vmware.vim25.VirtualMachinePowerState;
import com.vmware.vim25.mo.Folder;
import com.vmware.vim25.mo.InventoryNavigator;
import com.vmware.vim25.mo.ServiceInstance;
import com.vmware.vim25.mo.Task;
import com.vmware.vim25.mo.VirtualMachine;

/**
 * @author pnavarro
 */
public abstract class AbsVmwareMachine extends AbsVirtualMachine
{
    /** The logger */
    private static final Logger logger = LoggerFactory.getLogger(AbsVmwareMachine.class);

    /** The generic virtual machine configuration. */
    protected VirtualMachineConfiguration vmConfig;

    protected String networkUUID = UUID.randomUUID().toString();

    /** The VMWaare hypervisor specific configuration. */
    protected final VmwareHypervisorConfiguration vmwareConfig;

    /** The machine name. */
    protected final String machineName;

    /** Identifier of the machine */
    private final String uuid;

    /** The virtual machine managed object reference. */
    private ManagedObjectReference _virtualMachine;

    /** Help using the ExtendedAppUtil main VMWare API interface. */
    protected VmwareMachineUtils utils;

    /** Hold all the disk (HD and iSCSI targets) related logic. */
    protected VmwareMachineDisk disks;
    
    /** Check if the DVS feature is enabled; */
    protected Boolean dvsEnabled;
    
    /** Tasks to be required for a VM to change its state. */
    enum VMTasks
    {
        PAUSE, POWER_OFF, POWER_ON, RESET, RESUME, DELETE
    };

    /**
     * The standard constructor
     * 
     * @param configuration the virtual machine configuration
     * @throws VirtualMachineException
     */
    public AbsVmwareMachine(final VirtualMachineConfiguration configuration)
        throws VirtualMachineException
    {
        super(configuration);
        VmwareHypervisor vmwareHyper;

        if (config.getHyper() == null)
        {
            final String msgErr = "Null hypervisor";
            throw new VirtualMachineException(msgErr);
        }

        if (config.isSetHypervisor() && config.getHyper() instanceof VmwareHypervisor)
        {
            vmwareHyper = (VmwareHypervisor) config.getHyper();
        }
        else
        {
            throw new VirtualMachineException("Vmware machine requires a Vmware Hypervisor "
                + "on VirtualMachineConfiguration, not a "
                + config.getHyper().getClass().getCanonicalName());
        }

        vmConfig = configuration;
        vmwareConfig =
            AbiCloudModel.getInstance().getConfigManager().getConfiguration()
                .getVmwareHyperConfig();

        machineName = config.getMachineName();
        uuid = config.getMachineId().toString();

        // Gets the VMWare API main interface
        utils = new VmwareMachineUtils(vmwareHyper);
        disks = new VmwareMachineDisk(utils, vmConfig, vmwareConfig);
        
        // Get if the DVS experimental feature is enabled.
        dvsEnabled = Boolean.valueOf(System.getProperty("abiquo.experimentaldvs.enabled"));
    }

    @Override
    public void deployMachine() throws VirtualMachineException
    {
        try
        {
            // if (!apputil.getServiceConnection3().isConnected())
            utils.reconnect();

            /**
             * XXX
             */
            // try
            // {
            // ManagedObjectReference dsmor =
            // disks.createVMFSDatastore(vmwareConfig.getXxIscsiTarget(), vmwareConfig.getXxIqn());
            //
            // logger.info("---------------------- win datastore VMSF ----------------------------");
            // }
            // catch (Exception e)
            // {
            // // TODO Auto-generated catch block
            // e.printStackTrace();
            // }
            //

            if (!isVMAlreadyCreated())
            {
                configureNetwork();

                // Create the template vdestPathirtual machine
                // <DVS>
                List<VirtualNIC> dvNIClist = new ArrayList<VirtualNIC>();
                if (dvsEnabled)
                {
                    // if any of the vnics have a "dvs" as switch, then all of them will have.
                    // because it refeers a target machine property, not a NIC-specific property
                    if (config.getVnicList().get(0).getVSwitchName().toLowerCase().startsWith("dvs"))
                    {
                        dvNIClist.addAll(config.getVnicList());
                        config.getVnicList().clear();
                    }
                }
                // </DVS>
                createVirtualMachine();
                // <DVS>
                // Once the machine is defined and created, attach its vnics which refeers a dvs
                // to a vcenter.
                if (dvsEnabled)
                {
                    VCenterBridge vcenterBridge =
                        VCenterBridge.createVCenterBridge(utils.getAppUtil().getServiceInstance());
                    vcenterBridge.attachVMToPortGroup(config.getMachineName(), dvNIClist);

                }
                // </DVS>
                
                
                

                // Stateless image located on the Enterprise Repository require to be copy on the
                // local fs.
                if (vmConfig.getVirtualDiskBase().getDiskType() == VirtualDiskType.STANDARD)
                {
                    // Copy from the NAS to the template virtual machine
                    cloneVirtualDisk();
                }

                // Attach the initial extended disks
                initDisks();

                // reconfigureNetwork();

            }

            // TODO The method areDisksAlreadyDeployed is not used to check if the disks are already
            // deployed

            checkIsCancelled();
        }
        catch (Exception e)
        {
            logger.error("Failed to deploy machine :{}", e);
            // The roll back in the virtual machine is done in top level when rolling back the
            // virtual appliance
            rollBackVirtualMachine();
            state = State.CANCELLED;
            throw new VirtualMachineException(e);
        }
        finally
        {
            utils.logout();
        }

        logger.info("Created vmware machine name:" + config.getMachineName() + "\t ID:"
            + config.getMachineId().toString() + "\t " + "using hypervisor connection at "
            + config.getHyper().getAddress().toString());

        state = State.DEPLOYED;
    }

    /**
     * Configures the port groups and tags the corresponding VLAN
     * 
     * @throws Exception
     */
    private void configureNetwork() throws Exception
    {

        // Creating or updating the needed port group and tagging
        for (VirtualNIC vnic : config.getVnicList())
        {
            String portGroupName = vnic.getNetworkName() + "_" + vnic.getVlanTag();

            // Create the port group in the vCenter and attach it to a dvSwitch
            // <DVS>
            Boolean dvsEnabled =
                Boolean.valueOf(System.getProperty("abiquo.experimentaldvs.enabled"));
            if (dvsEnabled && vnic.getVSwitchName().toLowerCase().startsWith("dvs"))
            {
                VCenterBridge vcenterBridge =
                    VCenterBridge.createVCenterBridge(utils.getAppUtil().getServiceInstance());
                vcenterBridge.createPortGroupInVCenter(vnic.getVSwitchName(),
                    vnic.getNetworkName(), vnic.getVlanTag());
                continue;
            }
            // </DVS>

            // Try to find if a group corresponding the network name is found. If not create it.
            ManagedObjectReference networkMor = utils.getNetwork(portGroupName);
            ManagedObjectReference hostmor = utils.getHostSystemMOR();
            if (networkMor == null)
            {
                ExtendedAppUtil apputil = utils.getAppUtil();
                Object cmobj =
                    apputil.getServiceUtil3().getDynamicProperty(hostmor, "configManager");
                HostConfigManager configMgr = (HostConfigManager) cmobj;
                ManagedObjectReference nwSystem = configMgr.getNetworkSystem();

                HostPortGroupSpec portgrp = new HostPortGroupSpec();
                portgrp.setName(portGroupName);
                if (existsVswitch(hostmor, vnic.getVSwitchName()))
                {
                    portgrp.setVswitchName(vnic.getVSwitchName());
                }
                else
                {
                    String msg =
                        "The Virtual Switch "
                            + vnic.getVSwitchName()
                            + " couln't be found in the hypervisor. The virtual machine networking resources can't be configured";
                    throw new VirtualMachineException(msg);
                }
                portgrp.setPolicy(new HostNetworkPolicy());
                portgrp.setVlanId(vnic.getVlanTag());

                logger.debug("Adding port group: " + portGroupName + " tagged with VLAN: "
                    + vnic.getVlanTag() + " to Virtual Switch " + vnic.getVSwitchName());
                apputil.getServiceUtil().getVimService().addPortGroup(nwSystem, portgrp);
            }
            else
            {
                ExtendedAppUtil apputil = utils.getAppUtil();
                HostConfigInfo hostConfigInfo =
                    (HostConfigInfo) apputil.getServiceUtil3()
                        .getDynamicProperty(hostmor, "config");
                HostNetworkInfo network = hostConfigInfo.getNetwork();
                for (HostVirtualSwitch vswitch : network.getVswitch())
                {
                    if (!vswitch.getName().equals(vnic.getVSwitchName()))
                    {
                        String[] portGroups = vswitch.getPortgroup();
                        for (String portGroup : portGroups)
                        {
                            // If the port group of the vSwitch ends with the name of the port group
                            // name
                            // It means somebody has attached the same port group to another switch.
                            // FAIL!
                            if (portGroup.endsWith("-" + portGroupName) == true)
                            {
                                throw new VirtualMachineException("The port group: "
                                    + portGroupName + " is attached to the virtual switch: "
                                    + vswitch.getName()
                                    + " It doesn't match the expected virtual switch: "
                                    + vnic.getVSwitchName());
                            }
                        }
                    }
                }
            }
        }

    }

    /**
     * Checks if exists the virtual switch in the hypervisors
     * 
     * @param hostmor the Host managed object reference
     * @param vSwitchName the virtual switch to check
     * @return true if the virtual switch exists, false if contrary
     * @throws Exception
     */
    private boolean existsVswitch(ManagedObjectReference hostmor, String vSwitchName)
        throws Exception
    {
        ExtendedAppUtil apputil = utils.getAppUtil();
        Object hiobj = apputil.getServiceUtil3().getDynamicProperty(hostmor, "config");
        HostConfigInfo hostConfigInfo = (HostConfigInfo) hiobj;
        HostNetworkInfo network = hostConfigInfo.getNetwork();
        for (HostVirtualSwitch vswitch : network.getVswitch())
        {
            if (vswitch.getName().equals(vSwitchName))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Attach the initial extended disk on configuration
     */
    private void initDisks() throws VirtualMachineException
    {
        VirtualMachineConfigSpec vmConfigSpec = new VirtualMachineConfigSpec();
        VirtualDeviceConfigSpec[] vdiskSpec;

        try
        {
            _virtualMachine = utils.getVmMor(machineName);

            vdiskSpec = disks.initialDiskDeviceConfigSpec();

            if (vdiskSpec != null)
            {
                logger.debug("Adding [{}] initial extended disks", vdiskSpec.length);
                vmConfigSpec.setDeviceChange(vdiskSpec);
            }
            else
            {
                logger.debug("Any disk configruation to add");
            }

            ManagedObjectReference tmor =
                utils.getService().reconfigVM_Task(_virtualMachine, vmConfigSpec);

            utils.monitorTask(tmor);
        }
        catch (Exception e)
        {
            throw new VirtualMachineException("Can not initialize the extended disks", e);
        }
    }

    /**
     * Perform the virtual image cloning. Creates a copy of the original image and put it on where
     * the current hypervisor expects to load it.
     */
    protected void cloneVirtualDisk() throws VirtualMachineException
    {
        disks.moveVirtualDiskToDataStore();
    }

    /**
     * Private helper to create a virtual machine template from the open virtualization format
     * parameters
     * 
     * @throws Exception
     */
    private void createVirtualMachine() throws VirtualMachineException
    {
        String dcName; // datacenter name
        ManagedObjectReference dcmor; // datacenter
        ManagedObjectReference hfmor; // host folder
        ManagedObjectReference hostmor;// host
        ArrayList<ManagedObjectReference> crmors;// all computer resources on host folder
        ManagedObjectReference crmor; // computer resource
        VirtualMachineConfigSpec vmConfigSpec; // virtual machine configuration
        VirtualDeviceConfigSpec[] vdiskSpec; // disk configuration

        try
        {

            dcName = utils.getOption("datacentername");
            dcmor =
                utils.getAppUtil().getServiceUtil().getDecendentMoRef(null, "Datacenter", dcName);

            if (dcmor == null)
            {
                String message = "Datacenter " + dcName + " not found.";
                logger.error(message);
                throw new VirtualMachineException(message);
            }

            hfmor = utils.getAppUtil().getServiceUtil().getMoRefProp(dcmor, "hostFolder");

            hostmor = utils.getHostSystemMor(dcmor, hfmor);

            crmors = getAllComputerResourcesOnHostFolder(hfmor);

            crmor = utils.getComputerResourceFromHost(crmors, hostmor);

            // TODO #createVMConfigSpec defines not convenient default data, change this
            vmConfigSpec = configureVM(crmor, hostmor);

            logger.info("Machine name :{} Machine ID: {} ready to be created", machineName,
                config.getMachineId());

            ManagedObjectReference resourcePool =
                utils.getAppUtil().getServiceUtil().getMoRefProp(crmor, "resourcePool");
            ManagedObjectReference vmFolderMor =
                utils.getAppUtil().getServiceUtil().getMoRefProp(dcmor, "vmFolder");

            ManagedObjectReference taskmor =
                utils.getService().createVM_Task(vmFolderMor, vmConfigSpec, resourcePool, hostmor);

            /*
             * TODO ing //customizationIPSettings for the deploy CustomizationSpec customMachine =
             * setCustomizationSpec(); //setting the variable _virtualMachine
             * getVmMor(this.machineName);
             * apputil.getServiceConnection3().getService().customizeVM_Task(_virtualMachine,
             * customMachine);
             */

            utils.checkTaskState(taskmor);

        }
        catch (Exception e)
        {
            throw new VirtualMachineException("Can not create the VM:" + e.getCause().getMessage(),
                e);
        }

    }

    @SuppressWarnings("unchecked")
    private ArrayList<ManagedObjectReference> getAllComputerResourcesOnHostFolder(
        final ManagedObjectReference hfmor) throws Exception
    {
        return utils.getAppUtil().getServiceUtil().getDecendentMoRefs(hfmor, "ComputeResource");
    }

    @Override
    public void deleteMachine() throws VirtualMachineException
    {
        try
        {
            utils.reconnect();
            // Force to power off the machine before deleting
            powerOffMachine();

            // affect all the VM for the given machine name
            ServiceInstance si = utils.getAppUtil().getServiceInstance();

            Folder rootFolder = si.getRootFolder();

            executeTaskOnVM(VMTasks.DELETE);

            // Deconfigure networking resources
            try
            {
                utils.reconnect();
                // <DVS>
                // if any of the vnics have a "dvs" as switch, then all of them will have.
                // because it refers a target machine property, not a NIC-specific property
                if (dvsEnabled && config.getVnicList().get(0).getVSwitchName().toLowerCase().startsWith("dvs"))
                {
                    VCenterBridge vcenterBridge =
                        VCenterBridge.createVCenterBridge(utils.getAppUtil().getServiceInstance());
                    
                    // Since the machine has been deleted, the vcenter needs to unregister it before delete the port group.
                    vcenterBridge.unregisterVM(config.getMachineName());
                    vcenterBridge.deconfigureNetwork(config.getVnicList());
                    
                }
                else
                {
                    deconfigureNetwork();
                }
            }
            catch (Exception e)
            {
                logger.error(
                    "An error was occurred then deconfiguring the networking resources: {}", e);
            }
        }
        finally
        {
            utils.logout();
        }

        logger.debug("Deleted machine [{}]", machineName);
    }

    /**
     * Deconfigures the switch groups used by the Virtual machine
     * 
     * @throws VirtualMachineException
     */
    private void deconfigureNetwork() throws VirtualMachineException
    {
        // Just deletes the switch groups if they are just used by the VM to delete

        try
        {
            for (VirtualNIC vnic : config.getVnicList())
            {
                String portGroup = vnic.getNetworkName() + "_" + vnic.getVlanTag();
                ManagedObjectReference[] vmsUsedByNetwork = utils.getVmsFromNetworkName(portGroup);
                if (vmsUsedByNetwork.length == 0)
                {
                    logger.debug("There is no virtual machine using network: " + portGroup
                        + " proceeding to delete");
                    ExtendedAppUtil apputil = utils.getAppUtil();
                    ManagedObjectReference hostmor = utils.getHostSystemMOR();
                    Object cmobj =
                        apputil.getServiceUtil3().getDynamicProperty(hostmor, "configManager");
                    HostConfigManager configMgr = (HostConfigManager) cmobj;
                    ManagedObjectReference nwSystem = configMgr.getNetworkSystem();

                    utils.getAppUtil().getServiceUtil().getVimService()
                        .removePortGroup(nwSystem, portGroup);
                    logger.debug("Removing port group: " + portGroup);
                }

            }
        }
        catch (Exception e)
        {
            throw new VirtualMachineException("Can not deconfigure the network resources of virtual machine "
                + machineName,
                e);
        }

    }

    /**
     * Destroy a VM.
     * 
     * @param vmMOR, a virtual machine related to ''machineName''
     */
    private void destroyVM(final ManagedObjectReference vmMOR) throws VirtualMachineException
    {
        ManagedObjectReference taskDestroy;

        String vmName = "unknow dynamic property ''name''";
        try
        {
            vmName = (String) utils.getAppUtil().getServiceUtil().getDynamicProperty(vmMOR, "name");

            logger.info("Powering off virtualmachine '{}'", vmName);
        }
        catch (Exception e) // getDynamicProperty
        {
            logger.warn("Can not get the dynamic property 'name' for the VM [{}]",
                vmMOR.get_value());
        }

        try
        {
            taskDestroy = utils.getService().destroy_Task(vmMOR);

            utils.checkTaskState(taskDestroy);

            logger.info("VM {} powered off successfuly", vmName);
        }
        catch (Exception e)
        {
            throw new VirtualMachineException("Can not destroy the VM " + vmName, e);
        }
    }

    /**
     * Call the API service to achieve the desired Task on the current VM.
     */
    private void executeTaskOnVM(final VMTasks task) throws VirtualMachineException
    {
        utils.reconnect();

        ServiceInstance si = utils.getAppUtil().getServiceInstance();

        Folder rootFolder = si.getRootFolder();

        try
        {
            VirtualMachine vm =
                (VirtualMachine) new InventoryNavigator(rootFolder).searchManagedEntity(
                    "VirtualMachine", machineName);
            
            
            if (vm == null)
            {
                throw new VirtualMachineException("Error while " + task.name()
                    + " the machine: any virtual machine was not found");
            }

            Task taskMOR;
            switch (task)
            {
                case POWER_OFF:
                    taskMOR = vm.powerOffVM_Task();
                    break;
                case POWER_ON:
                    taskMOR = vm.powerOnVM_Task(null);
                    break;
                case PAUSE:
                    taskMOR = vm.suspendVM_Task();
                    break;
                case RESUME:
                    taskMOR = vm.powerOnVM_Task(null);
                    break;
                case RESET:
                    taskMOR = vm.resetVM_Task();
                    break;
                case DELETE:
                    taskMOR = vm.destroy_Task();
                    break;
                default:
                    throw new Exception("Invalid task action " + task.name());
            }

            if (taskMOR.waitForMe() == Task.SUCCESS)
            {
                logger.info("[" + task.name() + "] successfuly for VM [{}]", machineName);
            }
            else
            {
                final String msg = "[" + task.name() + "] on " + machineName + " fail";
                throw new VirtualMachineException(msg);
            }
        }
        catch (Exception e)
        {
            String msg = "[" + task.name() + "] on " + machineName + " failed ";
            if (e instanceof GenericVmConfigFault)
            {
                GenericVmConfigFault configFault = (GenericVmConfigFault) e;
                msg = msg + "Raison : " + configFault.getReason();
            }
            throw new VirtualMachineException(msg);
        }
        finally
        {
            utils.logout();
        }

    }

    @Override
    public State getStateInHypervisor()
    {
        State state = null;

        try
        {
            utils.reconnect();
            if (!isVMAlreadyCreated())
            {
                return State.NOT_DEPLOYED;
            }

            ServiceInstance si = utils.getAppUtil().getServiceInstance();

            Folder rootFolder = si.getRootFolder();

            VirtualMachine vm =
                (VirtualMachine) new InventoryNavigator(rootFolder).searchManagedEntity(
                    "VirtualMachine", machineName);

            if (vm == null)
            {
                throw new VirtualMachineException("Any virtual machine was not found");
            }

            VirtualMachinePowerState vmstate = vm.getRuntime().getPowerState();
            switch (vmstate)
            {
                case poweredOff:
                    state = State.POWER_OFF;
                    break;
                case poweredOn:
                    state = State.POWER_UP;
                    break;
                case suspended:
                    state = State.PAUSE;
                    break;
            }
        }
        catch (Exception e)
        {
            logger.error("An error was occurred when getting the virtual machine state", e);
            return State.UNKNOWN;
        }
        finally
        {
            utils.logout();
        }
        return state;

    }

    @Override
    public void pauseMachine() throws VirtualMachineException
    {
        if (!checkState(State.PAUSE))
            executeTaskOnVM(VMTasks.PAUSE);
    }

    @Override
    public void powerOffMachine() throws VirtualMachineException
    {
        // If the VM is suspended can not be powered off. So first powering on
        if (checkState(State.PAUSE))
        {
            logger
                .info("As the power off action in susponded state is forbidden, proceeding to power on");
            executeTaskOnVM(VMTasks.POWER_ON);
        }

        if (!checkState(State.POWER_OFF))
            executeTaskOnVM(VMTasks.POWER_OFF);
    }

    @Override
    public void powerOnMachine() throws VirtualMachineException
    {
        if (!checkState(State.POWER_UP))
            executeTaskOnVM(VMTasks.POWER_ON);
    }

    @Override
    public void resetMachine() throws VirtualMachineException
    {
        if (!checkState(State.POWER_UP))
            executeTaskOnVM(VMTasks.RESET);
    }

    @Override
    public void resumeMachine() throws VirtualMachineException
    {
        if (!checkState(State.POWER_UP))
            executeTaskOnVM(VMTasks.RESUME);
    }

    @Override
    public void reconfigVM(final VirtualMachineConfiguration newConfiguration)
        throws VirtualMachineException
    {
        ResourceAllocationInfo raRAM;
        ResourceAllocationInfo raCPU;

        VirtualMachineConfigSpec vmConfigSpec;
        VirtualDeviceConfigSpec[] vdiskSpec = null;

        utils.reconnect();

        try
        {
            _virtualMachine = utils.getVmMor(machineName);
            vmConfigSpec = new VirtualMachineConfigSpec();

            // Setting the new Ram value
            if (newConfiguration.isRam_set())
            {
                logger.info("Reconfiguring The Virtual Machine For Memory Update " + machineName);

                vmConfigSpec.setMemoryMB(newConfiguration.getMemoryRAM() / 1048576);

            }

            // Setting the number cpu value
            if (newConfiguration.isCpu_number_set())
            {
                logger.info("Reconfiguring The Virtual Machine For CPU Update " + machineName);

                vmConfigSpec.setNumCPUs(newConfiguration.getCpuNumber());
            }

            // Setting the disk disk value
            // logger.info("Reconfiguring The Virtual Machine For disk Update " + machineName);

            vdiskSpec = disks.getDiskDeviceConfigSpec(newConfiguration);

            if (vdiskSpec != null)
            {
                vmConfigSpec.setDeviceChange(vdiskSpec);
            }
            else
            {
                logger.debug("Any disk configruation changed");
            }

            ManagedObjectReference tmor =
                utils.getService().reconfigVM_Task(_virtualMachine, vmConfigSpec);
            utils.monitorTask(tmor);
            // Updating configuration

            vmConfig = newConfiguration;
            disks.setVMConfig(vmConfig);
        }
        catch (Exception e)
        {
            throw new VirtualMachineException(e);
        }
        finally
        {
            utils.logout();
        }
    }

    /**
     * Used during creation sets the additional configuration into the VM.
     * 
     * @param computerResMOR, the computer resource related to the current VM.
     * @param hostMOR, the host related to the current VM.
     * @return a configuration containing the specified resources
     */
    public abstract VirtualMachineConfigSpec configureVM(ManagedObjectReference computerResMOR,
        ManagedObjectReference hostMOR) throws VirtualMachineException;

    @Override
    public boolean isVMAlreadyCreated() throws VirtualMachineException
    {
        try
        {
            ServiceInstance si = utils.getAppUtil().getServiceInstance();
            Folder rootFolder = si.getRootFolder();
            VirtualMachine machinemor =
                (VirtualMachine) new InventoryNavigator(rootFolder).searchManagedEntity(
                    "VirtualMachine", machineName);
            VirtualMachine machinemoruuid =
                (VirtualMachine) new InventoryNavigator(rootFolder).searchManagedEntity(
                    "VirtualMachine", uuid);
            return machinemor != null || machinemoruuid != null;
        }
        catch (Exception e)
        {
            throw new VirtualMachineException(e);
        }
    }

    /**
     * Private helper to check the real state of the virtual machine
     * 
     * @param stateToCheck the state to check
     * @return true if the state in the hypervisors equals to the state as parameter, false if
     *         contrary
     */
    private boolean checkState(final State stateToCheck) throws VirtualMachineException
    {
        return getStateInHypervisor().compareTo(stateToCheck) == 0;
    }

    @Override
    public void bundleVirtualMachine(final String sourcePath, final String destinationPath,
        final String snapshotName, final boolean isManaged) throws VirtualMachineException
    {
        try
        {
            utils.reconnect();

            disks.bundleVirtualDisk(sourcePath, destinationPath, snapshotName, isManaged);
        }
        finally
        {
            utils.logout();
        }

    }

}
