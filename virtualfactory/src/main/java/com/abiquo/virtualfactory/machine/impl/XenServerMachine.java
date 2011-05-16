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

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.abiquo.virtualfactory.exception.HypervisorException;
import com.abiquo.virtualfactory.exception.VirtualMachineException;
import com.abiquo.virtualfactory.hypervisor.impl.XenServerHypervisor;
import com.abiquo.virtualfactory.model.AbsVirtualMachine;
import com.abiquo.virtualfactory.model.State;
import com.abiquo.virtualfactory.model.VirtualDisk;
import com.abiquo.virtualfactory.model.VirtualDiskType;
import com.abiquo.virtualfactory.model.config.VirtualMachineConfiguration;
import com.abiquo.virtualfactory.network.VirtualNIC;
import com.abiquo.virtualfactory.repositorymanager.RepositoryManagerException;
import com.abiquo.virtualfactory.repositorymanager.RepositoryManagerStub;
import com.xensource.xenapi.Host;
import com.xensource.xenapi.Network;
import com.xensource.xenapi.PIF;
import com.xensource.xenapi.SR;
import com.xensource.xenapi.VBD;
import com.xensource.xenapi.VDI;
import com.xensource.xenapi.VIF;
import com.xensource.xenapi.VLAN;
import com.xensource.xenapi.VM;
import com.xensource.xenapi.Types.OnCrashBehaviour;
import com.xensource.xenapi.Types.VbdMode;
import com.xensource.xenapi.Types.VbdType;
import com.xensource.xenapi.Types.VmPowerState;

/**
 * XenServer virtual machine implementation.
 * 
 * @author ibarrera
 */
public class XenServerMachine extends AbsVirtualMachine
{
    /** The logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(XenServerMachine.class);

    /** The default description for Abiquo managed virtual machines. */
    private static final String DEFAULT_MACHINE_DESCRIPTION = "Abiquo managed virtual machine";

    /** The default template used to generate virtual machines. */
    private static final String DEFAULT_TEMPLATE = "Other install media";

    /** The extension of the Virtual Disk Image files. */
    private static final String DISK_FILE_EXTENSION = ".vhd";

    /** The XEN hypervisor */
    protected XenServerHypervisor hypervisor;

    /** The RepositoryManager stub. */
    protected RepositoryManagerStub repositoryManager;

    /**
     * The standard constructor
     * 
     * @param configuration the virtual machine configuration
     * @throws VirtualMachineException
     */
    public XenServerMachine(final VirtualMachineConfiguration configuration)
        throws VirtualMachineException
    {
        super(configuration);

        if (config.getHyper() == null)
        {
            throw new VirtualMachineException("Hypervisor must not be null");
        }

        hypervisor = (XenServerHypervisor) configuration.getHyper();
        if (configuration.getRepositoryManagerAddress() != null)
        {
            repositoryManager =
                new RepositoryManagerStub(configuration.getRepositoryManagerAddress());
        }

        // Check if repository is configured in hypervisor,
        // and configure it if still does not exist
        try
        {
            hypervisor.initAbiquoRepository();
        }
        catch (HypervisorException ex)
        {
            throw new VirtualMachineException(ex);
        }
    }

    @Override
    public void deployMachine() throws VirtualMachineException
    {
        try
        {
            hypervisor.reconnect();

            LOGGER.info("Deploying machine: " + config.getMachineName());

            if (!isVMAlreadyCreated())
            {
                LOGGER.info("Machine: " + config.getMachineName()
                    + " does not exist. Creating it...");

                // Create the virtual machine
                createVirtualMachine();
            }

            checkIsCancelled();
        }
        catch (Exception ex)
        {
            // Rollback changes
            rollBackVirtualMachine();

            state = State.CANCELLED;

            // Propagate exception
            throw new VirtualMachineException(ex);
        }
        finally
        {
            if (hypervisor.getConn() != null)
            {
                hypervisor.logout();
            }
        }

        state = State.DEPLOYED;
    }

    @Override
    public boolean isVMAlreadyCreated() throws VirtualMachineException
    {
        try
        {
            Set<VM> vms = VM.getByNameLabel(hypervisor.getConn(), config.getMachineName());
            return vms != null && !vms.isEmpty();
        }
        catch (Exception ex)
        {
            throw new VirtualMachineException(ex);
        }
    }

    @Override
    public void deleteMachine() throws VirtualMachineException
    {
        try
        {
            LOGGER.info("Deleting machine: " + config.getMachineName());

            // Get the Virtual Machine and the Connection
            VM vm = getVirtualMachine();

            try
            {
                // Destroy all virtual block devices
                Set<VBD> vbds = vm.getVBDs(hypervisor.getConn());
                for (VBD vbd : vbds)
                {
                    if (vbd.getType(hypervisor.getConn()).equals(VbdType.DISK))
                    {
                        LOGGER.debug("Destroying Virtual Disk...");
                        destroyVirtualDisk(vbd);
                    }
                    else
                    {
                        vbd.destroy(hypervisor.getConn());
                    }
                }

                try
                {
                    // If this VM is the only VM in the VLAN, delete also the VLAN
                    deleteVLAN(vm);
                }
                catch (Exception e)
                {
                    LOGGER.error("An error occurred deconfiguring the networking resources: {}", e);
                }

                // Destroy the Virtual Machine
                vm.destroy(hypervisor.getConn());
            }
            catch (Exception ex)
            {
                throw new VirtualMachineException(ex);
            }
        }
        finally
        {
            hypervisor.logout();
        }
    }

    @Override
    public void pauseMachine() throws VirtualMachineException
    {
        try
        {
            LOGGER.info("Pausing machine: " + config.getMachineName());

            VM vm = getVirtualMachine();

            if (!checkState(vm, State.PAUSE))
            {
                try
                {
                    vm.pause(hypervisor.getConn());
                }
                catch (Exception ex)
                {
                    throw new VirtualMachineException(ex);
                }
            }
        }
        finally
        {
            hypervisor.logout();
        }
    }

    @Override
    public void powerOffMachine() throws VirtualMachineException
    {
        try
        {
            LOGGER.info("Shutting down machine: " + config.getMachineName());

            VM vm = getVirtualMachine();

            if (!checkState(vm, State.POWER_OFF))
            {
                try
                {
                    vm.hardShutdown(hypervisor.getConn());
                }
                catch (Exception ex)
                {
                    throw new VirtualMachineException(ex);
                }
            }
        }
        finally
        {
            hypervisor.logout();
        }
    }

    @Override
    public void powerOnMachine() throws VirtualMachineException
    {
        try
        {
            LOGGER.info("Starting machine: " + config.getMachineName());

            VM vm = getVirtualMachine();

            if (!checkState(vm, State.POWER_UP))
            {
                try
                {
                    Host host = hypervisor.getHost();
                    vm.startOn(hypervisor.getConn(), host, false, false);
                }
                catch (Exception ex)
                {
                    throw new VirtualMachineException(ex);
                }
            }
        }
        finally
        {
            hypervisor.logout();
        }
    }

    @Override
    public void reconfigVM(final VirtualMachineConfiguration newConfiguration)
        throws VirtualMachineException
    {
        try
        {
            VM vm = getVirtualMachine();

            try
            {
                // Reconfigure Memory
                if (newConfiguration.isRam_set())
                {
                    LOGGER.info("Reconfiguring memory for virtual machine "
                        + config.getMachineName());

                    // Virtual Machine will be stopped, so use this method to set memory
                    setMemory(vm, newConfiguration.getMemoryRAM());
                }

                // Reconfigure CPU
                if (newConfiguration.isCpu_number_set())
                {
                    LOGGER.info("Reconfiguring CPU for virtual machine " + config.getMachineName());

                    // Virtual Machine will be stopped, so use this methods to set cpu number
                    vm.setVCPUsAtStartup(hypervisor.getConn(), Long.valueOf(newConfiguration
                        .getCpuNumber()));
                }

                // Reconfigure disks
                reconfigDisks(vm, newConfiguration, config);
            }
            catch (Exception ex)
            {
                throw new VirtualMachineException(ex);
            }
        }
        finally
        {
            hypervisor.logout();
        }

    }

    @Override
    public void resetMachine() throws VirtualMachineException
    {
        try
        {
            LOGGER.info("Rebooting machine: " + config.getMachineName());

            VM vm = getVirtualMachine();

            if (!checkState(vm, State.POWER_UP))
            {
                try
                {
                    vm.hardReboot(hypervisor.getConn());
                }
                catch (Exception ex)
                {
                    throw new VirtualMachineException(ex);
                }
            }
        }
        finally
        {
            hypervisor.logout();
        }
    }

    @Override
    public void resumeMachine() throws VirtualMachineException
    {
        try
        {
            LOGGER.info("Resuming machine: " + config.getMachineName());

            VM vm = getVirtualMachine();

            if (!checkState(vm, State.POWER_UP))
            {
                try
                {
                    vm.unpause(hypervisor.getConn());
                }
                catch (Exception ex)
                {
                    throw new VirtualMachineException(ex);
                }
            }
        }
        finally
        {
            hypervisor.logout();
        }
    }

    @Override
    public void bundleVirtualMachine(final String sourcePath, final String destinationPath,
        final String snapshotName, final boolean isManaged) throws VirtualMachineException
    {
        LOGGER.info("Bundling machine: " + config.getMachineName());

        VDI bundledVDI = null;

        try
        {
            LOGGER.debug("Copying disk to Abiquo Repository...");

            // Get the virtual machine disk to copy (should be only one)
            VM vm = getVirtualMachine();
            VBD vbd = vm.getVBDs(hypervisor.getConn()).iterator().next();
            VDI vdi = vbd.getVDI(hypervisor.getConn());

            // Copy disk to target repository
            SR abiquoSR = SR.getByUuid(hypervisor.getConn(), hypervisor.getAbiquoRepositoryID());
            bundledVDI = vdi.copy(hypervisor.getConn(), abiquoSR);
        }
        catch (Exception ex)
        {
            // Logout only if copy fails; otherwise will logout later
            hypervisor.logout();

            throw new VirtualMachineException(ex);
        }

        try
        {
            LOGGER.debug("Moving bundled disk to Appliance Library...");

            String srcBundle =
                hypervisor.getAbiquoRepositoryID() + "/" + bundledVDI.getUuid(hypervisor.getConn())
                    + DISK_FILE_EXTENSION;

            // Move from Abiquo Repository to Appliance Library
            repositoryManager.copy(srcBundle, destinationPath + "/" + snapshotName);
        }
        catch (Exception ex)
        {
            throw new VirtualMachineException(ex);
        }
        finally
        {
            // Bundled copy in Abiquo repository must always be removed (whether an Exception is
            // thrown or not) since it is only a temporal file created to perform the copy between
            // repository directories
            try
            {
                bundledVDI.destroy(hypervisor.getConn());
            }
            catch (Exception ex)
            {
                // Just log error and continue to logout
                LOGGER.error("Could not destroy Virtual Disk Image", ex);
            }

            hypervisor.logout();
        }
    }

    /**
     * Perform the virtual image cloning.
     * <p>
     * Creates a copy of the original image and put it on where the current hypervisor expects to
     * load it.
     * 
     * @return The copied Virtual Disk Image.
     */
    protected VDI cloneVirtualDisk() throws Exception
    {
        LOGGER.debug("Copying disk from Appliance Library to Abiquo Repository...");

        // Copy conversion to local repository directory
        String imagePath = config.getVirtualDiskBase().getImagePath();
        String destination = config.getMachineId().toString() + DISK_FILE_EXTENSION;

        try
        {
            repositoryManager.copy(imagePath, hypervisor.getAbiquoRepositoryID() + "/"
                + destination);

            // Rescan the repository to add the copied image
            SR sr = SR.getByUuid(hypervisor.getConn(), hypervisor.getAbiquoRepositoryID());
            sr.scan(hypervisor.getConn());

            // The copied image has the same UUID than the machine UUID
            VDI vdi = VDI.getByUuid(hypervisor.getConn(), config.getMachineId().toString());

            // Copy the image to its target repository
            SR deploySR = getDeployRepository(config.getVirtualDiskBase());

            LOGGER.debug("Moving disk from Abiquo Repository to XenServer {} Repository...",
                deploySR.getNameLabel(hypervisor.getConn()));

            VDI newVDI = vdi.copy(hypervisor.getConn(), deploySR);
            newVDI.setNameLabel(hypervisor.getConn(), config.getMachineName());

            LOGGER.debug("Virtual Disk Image was moved and has UUID: "
                + newVDI.getUuid(hypervisor.getConn()));

            // Destroy the source VDI (it is not needed anymore)
            vdi.destroy(hypervisor.getConn());

            return newVDI;
        }
        catch (RepositoryManagerException ex)
        {
            // RepositoryManagerException are controlled exception
            throw ex;
        }
        catch (Exception ex)
        {
            // Scan failure exceptions and exceptions due to corrupt SRs
            String msg =
                String.format("Could not copy the image [%s] to the selected datastore."
                    + " Please, verify all Storage Repositories are available.", imagePath);

            throw new Exception(msg);
        }
    }

    /**
     * Gets a Virtual machine.
     * 
     * @return The Virtual machine object.
     * @throws VirtualMachineException If the virtual machine is not found.
     */
    protected VM getVirtualMachine() throws VirtualMachineException
    {
        Set<VM> vms = null;

        try
        {
            hypervisor.reconnect();
            vms = VM.getByNameLabel(hypervisor.getConn(), config.getMachineName());
        }
        catch (Exception ex)
        {
            throw new VirtualMachineException(ex);
        }

        if (vms == null || vms.isEmpty())
        {
            throw new VirtualMachineException("Machine " + config.getMachineName()
                + " not found in hypervisor");
        }

        return vms.iterator().next();
    }

    /**
     * Private helper to configure the virtual machine resources. .
     * 
     * @return The configuration for the Virtual Machine.
     * @throws Exception If the virtual machine cannot be created.
     */
    protected VM createVirtualMachine() throws Exception
    {
        // Create the virtual machine based on a template
        VM template = getVMTemplate();

        VM vm = template.createClone(hypervisor.getConn(), config.getMachineName());
        vm.setIsATemplate(hypervisor.getConn(), false);
        vm.powerStateReset(hypervisor.getConn()); // Necessary to reset the allowed operations
        vm.setNameDescription(hypervisor.getConn(), DEFAULT_MACHINE_DESCRIPTION);

        // TODO: Verify this to force the preferred host
        Host host = hypervisor.getHost();
        vm.setAffinity(hypervisor.getConn(), host);

        // Configure the Virtual Machine
        configureBasicResources(vm);
        configureNetwork(vm);
        configureStorage(vm);

        return vm;
    }

    /**
     * Configure virtual machine basic resources.
     * 
     * @param vm The virtual machine to configure.
     * @throws Exception If the virtual machine cannot be configured.
     */
    protected void configureBasicResources(final VM vm) throws Exception
    {
        // CPU number and Memory size
        setMemory(vm, config.getMemoryRAM());
        vm.setVCPUsAtStartup(hypervisor.getConn(), Long.valueOf(config.getCpuNumber()));

        // Default behaviors
        vm.setActionsAfterCrash(hypervisor.getConn(), OnCrashBehaviour.DESTROY);

        // Boot parameters: boot from disk (c = disk, d = CD/DVD)
        vm.removeFromHVMBootParams(hypervisor.getConn(), "order");
        vm.addToHVMBootParams(hypervisor.getConn(), "order", "c");
    }

    /**
     * Sets the memory limits for the given {@link VM}.
     * 
     * @param vm The Virtual Machine.
     * @param memory The amount of memory.
     * @throws Exception If an error occurs.
     */
    protected void setMemory(final VM vm, final long memory) throws Exception
    {
        VM.Record vmRecord = vm.getRecord(hypervisor.getConn());

        LOGGER.debug("Setting memory for VM {} to {}. Current memory values are:",
            vmRecord.nameLabel, memory);

        LOGGER.debug(" - Static min: {}", vmRecord.memoryStaticMin);
        LOGGER.debug(" - Dynamic min: {}", vmRecord.memoryDynamicMin);
        LOGGER.debug(" - Dynamic max: {}", vmRecord.memoryDynamicMax);
        LOGGER.debug(" - Static max: {}", vmRecord.memoryStaticMax);

        try
        {
            // This method is only available from XenServer 5.6
            vm.setMemoryLimits(hypervisor.getConn(), memory, memory, memory, memory);
        }
        catch (Exception ex)
        {
            LOGGER.warn("Could not set memory using XenServer 5.6 API."
                + "Trying XenServer 5.5 APi calls");

            // Memory limits must always satisfy: static_min <= dynamic_min <= dynamic_max <=
            // static_max

            // ORDER MATTERS. After each API call the previous condition must be true.
            // The vm.setMemoryLimits shouldn't be used because it is only available since XenServer
            // 5.6

            if (memory <= vmRecord.memoryDynamicMin)
            {
                // If the new value is lesser than or equal to the dynamic_min =>
                // Update from minimum to maximum

                LOGGER.debug("Updating static_min from {} to {}", vmRecord.memoryStaticMin, memory);
                vm.setMemoryStaticMin(hypervisor.getConn(), memory);

                LOGGER.debug("Updating dynamic_min from {} to {}", vmRecord.memoryDynamicMin,
                    memory);
                vm.setMemoryDynamicMin(hypervisor.getConn(), memory);

                LOGGER.debug("Updating dynamic_max from {} to {}", vmRecord.memoryDynamicMax,
                    memory);
                vm.setMemoryDynamicMax(hypervisor.getConn(), memory);

                LOGGER.debug("Updating static_max from {} to {}", vmRecord.memoryStaticMax, memory);
                vm.setMemoryStaticMax(hypervisor.getConn(), memory);
            }
            else if (memory >= vm.getMemoryDynamicMax(hypervisor.getConn()))
            {
                // If the new value is greater than or equal to the dynamic_max =>
                // Update from maximum to minimum

                LOGGER.debug("Updating static_max from {} to {}", vmRecord.memoryStaticMax, memory);
                vm.setMemoryStaticMax(hypervisor.getConn(), memory);

                LOGGER.debug("Updating dynamic_max from {} to {}", vmRecord.memoryDynamicMax,
                    memory);
                vm.setMemoryDynamicMax(hypervisor.getConn(), memory);

                LOGGER.debug("Updating dynamic_min from {} to {}", vmRecord.memoryDynamicMin,
                    memory);
                vm.setMemoryDynamicMin(hypervisor.getConn(), memory);

                LOGGER.debug("Updating static_min from {} to {}", vmRecord.memoryStaticMin, memory);
                vm.setMemoryStaticMin(hypervisor.getConn(), memory);
            }
            else
            {
                // If the new value is between the dynamic_min and the dynamic_max =>
                // Update the dynamic values and then the static ones

                LOGGER.debug("Updating dynamic_min from {} to {}", vmRecord.memoryDynamicMin,
                    memory);
                vm.setMemoryDynamicMin(hypervisor.getConn(), memory);

                LOGGER.debug("Updating dynamic_max from {} to {}", vmRecord.memoryDynamicMax,
                    memory);
                vm.setMemoryDynamicMax(hypervisor.getConn(), memory);

                LOGGER.debug("Updating static_min from {} to {}", vmRecord.memoryStaticMin, memory);
                vm.setMemoryStaticMin(hypervisor.getConn(), memory);

                LOGGER.debug("Updating static_max from {} to {}", vmRecord.memoryStaticMax, memory);
                vm.setMemoryStaticMax(hypervisor.getConn(), memory);
            }
        }
    }

    /**
     * Configure virtual machine storage resources.
     * 
     * @param vm The virtual machine to configure.
     * @param vdi The Virtual Disk Image to use
     * @throws Exception If the virtual machine cannot be configured.
     */
    protected void configureStorage(final VM vm) throws Exception
    {
        if (config.getVirtualDiskBase().getDiskType() == VirtualDiskType.STANDARD)
        {
            // Copy virtual image to local repository
            VDI vdi = cloneVirtualDisk();

            // Attach disk to virtual machine
            attachDisk(vm, vdi, "hda");
        }
    }

    /**
     * Attach the disk to the specified VM.
     * 
     * @param vm The virtual machine.
     * @param vdi The disk to attach.
     * @param device The device to map to disk.
     * @throws Exception If the disk cannot be attached.
     */
    protected void attachDisk(final VM vm, final VDI vdi, final String device) throws Exception
    {
        // Create the Virtual Block Device to link the VDI to the VM
        VBD.Record vbdConfig = new VBD.Record();

        vbdConfig.VM = vm;
        vbdConfig.VDI = vdi;
        vbdConfig.device = device;
        vbdConfig.userdevice = "0";
        vbdConfig.mode = VbdMode.RW;
        vbdConfig.type = VbdType.DISK;

        VBD.create(hypervisor.getConn(), vbdConfig);
    }

    /**
     * Destroy a Virtual Block Device and its Virtual Disk Image.
     * 
     * @param vbd The Virtual Block device to destroy
     * @throws Exception If volume cannot be destroyed.
     */
    protected void destroyVirtualDisk(final VBD vbd) throws Exception
    {
        VDI vdi = vbd.getVDI(hypervisor.getConn());
        vbd.destroy(hypervisor.getConn());

        try
        {
            vdi.destroy(hypervisor.getConn());
        }
        catch (Exception e)
        {
            // Occurs when several vdi are attached to the same backing vbd file
            LOGGER.warn("Virtual Disk Image [" + vdi.getNameLabel(hypervisor.getConn())
                + "] could not be deleted");
        }
    }

    /**
     * Configure virtual machine network resources.
     * 
     * @param vm The virtual machine to configure.
     * @throws Exception If the virtual machine cannot be configured.
     */
    protected void configureNetwork(final VM vm) throws Exception
    {
        // Create all VLAN if necessary
        int device = 0;
        for (VirtualNIC vnic : config.getVnicList())
        {
            // Get the corresponding VLAN network
            VLAN vlan = getOrCreateVLAN(vnic);

            // Strange, but method getUntaggedPIF returns the PIF attached to the VLAN network
            PIF vlanPIF = vlan.getUntaggedPIF(hypervisor.getConn());
            Network vlanNetwork = vlanPIF.getNetwork(hypervisor.getConn());

            // Create the Virtual Interface
            VIF.Record vif = new VIF.Record();

            vif.VM = vm; // Link to virtual machine
            vif.network = vlanNetwork;// Assign to VLAN network
            vif.device = String.valueOf(device); // First device
            vif.MTU = 1500L; // Default MTU
            vif.MAC = vnic.getMacAddress(); // Set MAC address

            VIF.create(hypervisor.getConn(), vif);

            device++;
        }
    }

    /**
     * Gets the VLAN and creates it if still does not exist.
     * 
     * @param vnic The VLAN configuration.
     * @return The VLAN.
     * @throws Exception If VLAN cannot be retrieved or created.
     */
    protected VLAN getOrCreateVLAN(final VirtualNIC vnic) throws Exception
    {
        // Check if VLAN already exists
        VLAN vlan = getVLANByTag(Long.valueOf(vnic.getVlanTag()), vnic.getVSwitchName());
        if (vlan != null)
        {
            return vlan;
        }

        // At this point, the VLAN does not exist. Must create the network and the VLAN

        // Get the PIF where the VLAN will be attached
        PIF targetPIF = null;
        Set<PIF> pifs = PIF.getAll(hypervisor.getConn());
        for (PIF pif : pifs)
        {
            String device = pif.getDevice(hypervisor.getConn());
            Long tag = pif.getVLAN(hypervisor.getConn());

            if (device.equals(vnic.getVSwitchName()) && tag == -1)
            {
                targetPIF = pif;
                break;
            }
        }

        if (targetPIF == null)
        {
            throw new VirtualMachineException("Physical Interface " + vnic.getVSwitchName()
                + " not found in hypervisor.");
        }

        // Create the network for the VLAN
        Network.Record networkConfig = new Network.Record();
        networkConfig.nameLabel = vnic.getNetworkName() + "_" + vnic.getVlanTag();
        Network targetNetwork = Network.create(hypervisor.getConn(), networkConfig);

        // Create the VLAN and return it
        return VLAN.create(hypervisor.getConn(), targetPIF, Long.valueOf(vnic.getVlanTag()),
            targetNetwork);
    }

    /**
     * Delete the VLAN where the specified Virtual Machine is connected to.
     * 
     * @param vm The virtual machine used to find the VLAN.
     * @throws Exception If VLAN deletion fails.
     */
    protected void deleteVLAN(final VM vm) throws Exception
    {
        String vmUUID = vm.getUuid(hypervisor.getConn());

        // For each VIF check if this machine is the only machine in the VLAN
        for (VIF vmVIF : vm.getVIFs(hypervisor.getConn()))
        {
            Network net = vmVIF.getNetwork(hypervisor.getConn());

            // Check if there are other VMs attached to the network
            boolean emptyNetwork = true;
            for (VIF netVIF : net.getVIFs(hypervisor.getConn()))
            {
                VM netVM = netVIF.getVM(hypervisor.getConn());
                if (!netVM.getUuid(hypervisor.getConn()).equals(vmUUID))
                {
                    LOGGER.debug("There are still VMs attached to the VLAN."
                        + " It will not be deleted.");
                    emptyNetwork = false;
                    break;
                }
            }

            // If there is only this VM attached to the network, the VLAN can be deleted
            if (emptyNetwork)
            {
                LOGGER.debug("There are no VMs attached to the VLAN. It will be deleted.");

                for (PIF pif : net.getPIFs(hypervisor.getConn()))
                {
                    Long tag = pif.getVLAN(hypervisor.getConn());
                    String device = pif.getDevice(hypervisor.getConn());

                    VLAN vlan = getVLANByTag(tag, device);
                    if (vlan != null)
                    {
                        vlan.destroy(hypervisor.getConn());
                    }
                }

                net.destroy(hypervisor.getConn());
            }
        }
    }

    /**
     * Find a VLAN given its tag.
     * 
     * @param tag The VLAN tag.
     * @param device The PIF device.
     * @return The VLAN or <code>null</code> if there is no VLAN defined with the provided tag.
     * @throws Exception If VLAN cannot be found.
     */
    protected VLAN getVLANByTag(final Long tag, final String device) throws Exception
    {
        Set<VLAN> vlans = VLAN.getAll(hypervisor.getConn());

        for (VLAN vlan : vlans)
        {
            Long vlanTag = vlan.getTag(hypervisor.getConn());

            // Get the device (tagged and untagged PIFs have the same device)
            PIF pif = vlan.getTaggedPIF(hypervisor.getConn());
            String vlanDevice = pif.getDevice(hypervisor.getConn());

            if (tag.equals(vlanTag) && vlanDevice.equals(device))
            {
                return vlan;
            }
        }

        return null;
    }

    /**
     * Get the template for the Virtual Machine creation.
     * 
     * @return The Virtual Machine template.
     * @throws Exception If template cannot be retrieved.
     */
    protected VM getVMTemplate() throws Exception
    {
        Set<VM> vms = VM.getAll(hypervisor.getConn());

        for (VM vm : vms)
        {
            if (vm.getIsATemplate(hypervisor.getConn())
                && vm.getNameLabel(hypervisor.getConn()).equals(DEFAULT_TEMPLATE))
            {
                return vm;
            }
        }

        throw new VirtualMachineException("Could not find Virtual Machine template");
    }

    /**
     * Gets the local Storage Repository where the VDI will be deployed.
     * 
     * @param disk The Virtual Disk to be deployed.
     * @return The local Storage Reporitory.
     * @throws Exception If the local Storage Repository is not found.
     */
    protected SR getDeployRepository(final VirtualDisk disk) throws Exception
    {
        SR target = SR.getByUuid(hypervisor.getConn(), disk.getTargetDatastore());
        LOGGER.info("Deploying to Repository {}...", target.getNameLabel(hypervisor.getConn()));
        return target;
    }

    /**
     * Checks if the Virtual Machine is in the specified state.
     * 
     * @param vm The Virtual Machine.
     * @param state The state to check.
     * @return Boolean indicating if the Virtual Machine is in the specified state.
     * @throws VirtualMachineException If Virtual Machine state cannot be retrieved.
     */
    protected boolean checkState(final VM vm, final State state) throws VirtualMachineException
    {
        return getMachineState(vm).compareTo(state) == 0;
    }

    /**
     * Gets the state of the Virtual Machine.
     * 
     * @param vm The Virtual Machine.
     * @return The state of the Virtual Machine.
     * @throws VirtualMachineException If Virtual Machine state cannot be retrieved.
     */
    protected State getMachineState(final VM vm) throws VirtualMachineException
    {
        try
        {
            VmPowerState powerState = vm.getPowerState(hypervisor.getConn());
            State state = null;

            switch (powerState)
            {
                case HALTED:
                    state = State.POWER_OFF;
                    break;
                case RUNNING:
                    state = State.POWER_UP;
                    break;
                case SUSPENDED:
                    state = State.POWER_OFF;
                    break;
                case PAUSED:
                    state = State.PAUSE;
                    break;
                case UNRECOGNIZED:
                default:
                    state = State.UNKNOWN;
                    break;
            }

            return state;

        }
        catch (Exception ex)
        {
            throw new VirtualMachineException("Could not get the state of Virtual Machine: "
                + config.getMachineName(), ex);
        }
    }

    /**
     * Reconfigure the disks of the given virtual machine.
     * 
     * @param vm The virtual machine to reconfigure.
     * @param newConfig The new disk configuration.
     * @param config The current disk configuration.
     * @throws Exception If the disks cannot be reconfigured.
     */
    protected void reconfigDisks(final VM vm, final VirtualMachineConfiguration newConfig,
        final VirtualMachineConfiguration config) throws Exception
    {
        // Do nothing
    }
}
