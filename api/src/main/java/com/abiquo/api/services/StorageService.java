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

/**
 * 
 */
package com.abiquo.api.services;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.abiquo.api.exceptions.APIError;
import com.abiquo.api.services.cloud.VirtualMachineService;
import com.abiquo.model.enumerator.HypervisorType;
import com.abiquo.model.transport.LinksDto;
import com.abiquo.model.transport.error.CommonError;
import com.abiquo.scheduler.limit.EnterpriseLimitChecker;
import com.abiquo.scheduler.limit.LimitExceededException;
import com.abiquo.scheduler.limit.VirtualMachineRequirements;
import com.abiquo.server.core.cloud.VirtualAppliance;
import com.abiquo.server.core.cloud.VirtualDatacenter;
import com.abiquo.server.core.cloud.VirtualDatacenterRep;
import com.abiquo.server.core.cloud.VirtualMachine;
import com.abiquo.server.core.cloud.VirtualMachineState;
import com.abiquo.server.core.enterprise.DatacenterLimits;
import com.abiquo.server.core.enterprise.Enterprise;
import com.abiquo.server.core.infrastructure.InfrastructureRep;
import com.abiquo.server.core.infrastructure.management.RasdManagement;
import com.abiquo.server.core.infrastructure.storage.DiskManagement;
import com.abiquo.server.core.infrastructure.storage.StorageRep;
import com.abiquo.server.core.infrastructure.storage.VolumeManagement;
import com.abiquo.tracer.ComponentType;
import com.abiquo.tracer.EventType;
import com.abiquo.tracer.SeverityType;

/**
 * Implements all the business logic for storage features.
 * 
 * @author jdevesa
 */
@Service
public class StorageService extends DefaultApiService
{
    /**
     * Static class to specify the operation
     * to execute for a Disk.
     * 
     * @author jaume.devesa@abiquo.com
     */
    public static class DiskOp
    {
        public static enum Op
        {
            ATTACH, DETACH
        };

        public DiskManagement disk;

        public Op type;

        public DiskOp(final DiskManagement disk, final Op type)
        {
            super();
            this.disk = disk;
            this.type = type;
        }
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(StorageService.class);

    private static long MEGABYTE = 1048576;

    @Autowired
    protected InfrastructureRep datacenterRepo;

    @Autowired
    protected EnterpriseLimitChecker enterpriseLimitChecker;

    @Autowired
    protected StorageRep repo;

    /** User service for user-specific privileges */
    @Autowired
    protected UserService userService;

    @Autowired
    protected VirtualDatacenterRep vdcRepo;
    
    @Autowired
    protected VirtualMachineService vmService;

    /** Default constructor. */
    public StorageService()
    {

    }
    
    /**
     * Auxiliar constructor for test purposes. Haters gonna hate 'bzengine'. And his creator as
     * well...
     * 
     * @param em {@link EntityManager} instance with active transaction.
     */
    public StorageService(final EntityManager em)
    {
        vdcRepo = new VirtualDatacenterRep(em);
        userService = new UserService(em);
        repo = new StorageRep(em);
        enterpriseLimitChecker = new EnterpriseLimitChecker(em);
        datacenterRepo = new InfrastructureRep(em);
        vmService = new VirtualMachineService(em);
    }

    /**
     * Attach a list of disks to a virtual machine.
     * <p>
     * If the virtual machine is not deployed, the method simply returns <code>null</code>. If the
     * virtual machine is deployed, the attachment will run a reconfigure operation and this method
     * will return the identifier of the task object associated to the reconfigure operation.
     * 
     * @param vdcId identifier of the virtual datacenter.
     * @param vappId identifier of the virtual appliance
     * @param vmId identifier of the virtual machine
     * @param disks list of disks to attach.
     * @return The id of the Tarantino task if the virtual machine is deployed, <code>null</code>
     *         otherwise.
     */
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public Object attachHardDisks(final Integer vdcId, final Integer vappId, final Integer vmId,
        final LinksDto hdRefs)
    {
        VirtualDatacenter vdc = getVirtualDatacenter(vdcId);
        VirtualAppliance vapp = getVirtualAppliance(vdc, vappId);
        VirtualMachine vm = getVirtualMachine(vapp, vmId);
        
        List<DiskManagement> disks = vmService.getHardDisksFromDto(vdc, hdRefs);
        
        for (DiskManagement disk : disks)
        {
            // if the hard disk is already attached to another virtual machine
            // , raise a conflict error.
            if (disk.getVirtualMachine() != null)
            {
                addConflictErrors(APIError.HD_CURRENTLY_ALLOCATED);
                flushErrors();
            }
            disk.setVirtualAppliance(vapp);
            disk.setVirtualMachine(vm);
            disk.setAttachmentOrder(getFreeAttachmentSlot(vm));
            
            vm.getDisks().add(disk);
        }
        
        return vmService.reconfigureVirtualMachine(vdcId, vappId, vmId, vm);
    }
    
    /**
     * Detach all the list of disks from a Virtual Machine.
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
    public Object detachHardDisks(final Integer vdcId, final Integer vappId, final Integer vmId)
    {
        VirtualDatacenter vdc = getVirtualDatacenter(vdcId);
        VirtualAppliance vapp = getVirtualAppliance(vdc, vappId);
        VirtualMachine vm = getVirtualMachine(vapp, vmId);
        
        vm.getDisks().clear();
        
        return vmService.reconfigureVirtualMachine(vdcId, vappId, vmId, vm);
    }

    /**
     * Attach a list of disks to a virtual machine.
     * <p>
     * If the virtual machine is not deployed, the method simply returns <code>null</code>. If the
     * virtual machine is deployed, the attachment will run a reconfigure operation and this method
     * will return the identifier of the task object associated to the reconfigure operation.
     * 
     * @param volume The volume to attach.
     * @param vm The virtual machine.
     * @return The id of the Tarantino task if the virtual machine is deployed, <code>null</code>
     *         otherwise.
     */
    
    
    /**
     * Creates a new resource {@link DiskManagement} associated to a virtual machine.
     * 
     * @param vdcId identifier of the virtual datacenter.
     * @param sizeInMb size of the disk.
     * @return {@link DiskManagement} instance created.
     */
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public DiskManagement createHardDisk(final Integer vdcId, final Long sizeInMb)
    {
        VirtualDatacenter vdc = getVirtualDatacenter(vdcId);

        // The user has the role for manage This. But... is the user from the same enterprise
        // than Virtual Datacenter?
        userService.checkCurrentEnterpriseForPostMethods(vdc.getEnterprise());

        // check input parameters
        if (sizeInMb == null || sizeInMb < 1L)
        {
            addValidationErrors(APIError.HD_INVALID_DISK_SIZE);
            flushErrors();
        }

        // check the limits.
        checkStorageLimits(vdc, sizeInMb);

        DiskManagement disk = new DiskManagement(vdc, sizeInMb);
        validate(disk);
        // vdcRepo.insertHardDisk(disk);

        // Trace
        if (tracer != null)
        {
            String messageTrace =
                "A new hard disk VLAN of " + sizeInMb
                    + " MB has been created as a resource in VirtualDatacenter '" + vdc.getName()
                    + "'.";
            tracer.log(SeverityType.INFO, ComponentType.VIRTUAL_DATACENTER,
                EventType.HARD_DISK_CREATE, messageTrace);
        }

        return disk;
    }

    /**
     * Delete the disk from the virtual datacenter.\
     * 
     * @param vdcId identifier of the {@link VirtualDatacenter}
     * @param diskId identifier of the {@link DiskManagement}
     */
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public void deleteHardDisk(final Integer vdcId, final Integer diskId)
    {
        VirtualDatacenter vdc = getVirtualDatacenter(vdcId);
        // The user has the role for manage This. But... is the user from the same enterprise
        // than Virtual Datacenter?
        userService.checkCurrentEnterpriseForPostMethods(vdc.getEnterprise());

        DiskManagement disk = vdcRepo.findHardDiskByVirtualDatacenter(vdc, diskId);
        if (disk == null)
        {
            addNotFoundErrors(APIError.HD_NON_EXISTENT_HARD_DISK);
            flushErrors();
        }
        if (disk.getVirtualMachine() != null)
        {
            addConflictErrors(APIError.HD_CURRENTLY_ALLOCATED);
            flushErrors();
        }

        repo.removeHardDisk(disk);

        // Trace
        if (tracer != null)
        {
            String messageTrace =
                "The hard disk resource '" + disk.getId() + "' and size of "
                    + disk.getSizeInMb() + "MB has been deleted from VirtualDatacenter '"
                    + vdc.getName() + "'.";
            tracer.log(SeverityType.INFO, ComponentType.VIRTUAL_DATACENTER,
                EventType.HARD_DISK_DELETE, messageTrace);
        }
    }

    /**
     * Return a single of {@link DiskManagement} defined into a Virtual datacenter.
     * 
     * @param vdcId identifier of the {@link VirtualDatacenter}
     * @param diskId identifier of the {@link DiskManagement}
     * @return the found {@link DiskManagement}
     */
    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public DiskManagement getHardDiskByVirtualDatacenter(final Integer vdcId, final Integer diskId)
    {
        VirtualDatacenter vdc = getVirtualDatacenter(vdcId);

        DiskManagement disk = vdcRepo.findHardDiskByVirtualDatacenter(vdc, diskId);

        if (disk == null)
        {
            addNotFoundErrors(APIError.HD_NON_EXISTENT_HARD_DISK);
            flushErrors();
        }

        LOGGER.debug("Returning a single disk created into VirtualDatacenter '" + vdc.getName()
            + "' identifier by id: " + diskId + ".");

        return disk;
    }

    /**
     * Returns a single DiskManagement from a Virtual Machine.
     * 
     * @param vdcId identifier of the virtual datacenter.
     * @param vappId identifier of the virtual appliance.
     * @param vmId identifier of the virtual machine.
     * @param diskOrder disk order inside the virtual machine.
     * @return a single Disk according to its order.
     */
    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public DiskManagement getHardDiskByVM(final Integer vdcId, final Integer vappId,
        final Integer vmId, final Integer diskId)
    {
        VirtualDatacenter vdc = getVirtualDatacenter(vdcId);
        VirtualAppliance vapp = getVirtualAppliance(vdc, vappId);
        VirtualMachine vm = getVirtualMachine(vapp, vmId);

        DiskManagement targetDisk = vdcRepo.findHardDiskByVirtualMachine(vm, diskId);
        if (targetDisk == null)
        {
            addNotFoundErrors(APIError.HD_NON_EXISTENT_HARD_DISK);
            flushErrors();
        }

        LOGGER.debug("Returning a single disk allocated into VirtualMachine '" + vm.getName()
            + "' identifier by id: " + diskId + ".");

        return targetDisk;
    }

    /**
     * Return the list of {@link DiskManagement} defined into a Virtual datacenter.
     * 
     * @param vdcId identifier of the {@link VirtualDatacenter}
     * @return the list of found {@link DiskManagement}
     */
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public List<DiskManagement> getListOfHardDisksByVirtualDatacenter(final Integer vdcId)
    {
        VirtualDatacenter vdc = getVirtualDatacenter(vdcId);

        List<DiskManagement> disks = vdcRepo.findHardDisksByVirtualDatacenter(vdc);

        LOGGER.debug("Returning list of disks created into VirtualDatacenter '" + vdc.getName()
            + ".");

        return disks;
    }

    /**
     * Return the list of disks a Virtual Machine is using.
     * 
     * @param vdcId identifier of the virtual datacenter.
     * @param vappId identifier of the virtual appliance.
     * @param vmId identifier of the virtual machine.
     * @return the list of Disks.
     */
    @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
    public List<DiskManagement> getListOfHardDisksByVM(final Integer vdcId, final Integer vappId,
        final Integer vmId)
    {
        List<DiskManagement> disks = new ArrayList<DiskManagement>();

        // Check the parameter's correctness
        VirtualDatacenter vdc = getVirtualDatacenter(vdcId);
        VirtualAppliance vapp = getVirtualAppliance(vdc, vappId);
        VirtualMachine vm = getVirtualMachine(vapp, vmId);

        disks.addAll(vdcRepo.findHardDisksByVirtualMachine(vm));

        LOGGER.debug("Returning list of disks allocated into VirtualMachine '" + vdc.getName()
            + ".");

        return disks;
    }

    /**
     * Attaches a new Hard Disk inside a Virtual Machine into Database.
     * The disk should already be attached to the virtual machine.
     * 
     * @param vdcId identifier of the virtual datacenter.
     * @param vappId identifier of the virtual appliance.
     * @param vmId identifier of the virtual machine.
     * @param diskSizeInMb disk size in mega bytes.
     * @return the created object {@link DiskManagement}
     */
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public DiskManagement registerHardDiskIntoVMInDatabase(final Integer vdcId, final Integer vappId,
        final Integer vmId, final Integer diskId)
    {
        VirtualDatacenter vdc = getVirtualDatacenter(vdcId);
        VirtualAppliance vapp = getVirtualAppliance(vdc, vappId);
        VirtualMachine vm = getVirtualMachine(vapp, vmId);

        // Check if the machine is in the correct state to perform the action.
        if (!vm.getState().equals(VirtualMachineState.NOT_ALLOCATED))
        {
            addConflictErrors(APIError.VIRTUAL_MACHINE_INCOHERENT_STATE);
            flushErrors();
        }

        // get the disk from the virtualdatacenter's list
        DiskManagement createdDisk = vdcRepo.findHardDiskByVirtualDatacenter(vdc, diskId);
        if (createdDisk == null)
        {
            addNotFoundErrors(APIError.HD_NON_EXISTENT_HARD_DISK);
            flushErrors();
        }
        // if the hard disk is already attached to another virtual machine
        // , raise a conflict error.
        if (createdDisk.getVirtualMachine() != null)
        {
            addConflictErrors(APIError.HD_CURRENTLY_ALLOCATED);
            flushErrors();
        }
        createdDisk.setVirtualAppliance(vapp);
        createdDisk.setVirtualMachine(vm);
        createdDisk.setAttachmentOrder(getFreeAttachmentSlot(vm));

        vdcRepo.updateDisk(createdDisk);

        // Trace
        if (tracer != null)
        {
            String messageTrace =
                "The hard disk resource '" + createdDisk.getId() + "' and size of "
                    + createdDisk.getSizeInMb() + "MB has been assigned to virtual machine '"
                    + vm.getName() + "'.";
            tracer.log(SeverityType.INFO, ComponentType.VIRTUAL_MACHINE,
                EventType.HARD_DISK_ASSIGN, messageTrace);
        }

        return createdDisk;
    }

    /**
     * Detach a hard disk. Machine must be stopped and user should have the enough permissions.
     * 
     * @param vdcId identifier of the virtual datacenter.
     * @param vappId identifier of the virtual appliance.
     * @param vmId identifier of the virtual machine.
     * @param diskId identifier of the disk
     */
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public void unregisterHardDiskFromVMInDatabase(final Integer vdcId, final Integer vappId, final Integer vmId,
        final Integer diskId)
    {

        VirtualDatacenter vdc = getVirtualDatacenter(vdcId);
        VirtualAppliance vapp = getVirtualAppliance(vdc, vappId);
        VirtualMachine vm = getVirtualMachine(vapp, vmId);

        // The user has the role for manage This. But... is the user from the same enterprise
        // than Virtual Datacenter?
        userService.checkCurrentEnterpriseForPostMethods(vdc.getEnterprise());

        // Check if the machine is in the correct state to perform the action.
        if (!vm.getState().equals(VirtualMachineState.NOT_ALLOCATED))
        {
            addConflictErrors(APIError.VIRTUAL_MACHINE_INCOHERENT_STATE);
            flushErrors();
        }

        // Be sure the disk exists.
        DiskManagement disk = vdcRepo.findHardDiskByVirtualMachine(vm, diskId);
        if (disk == null)
        {
            addNotFoundErrors(APIError.HD_NON_EXISTENT_HARD_DISK);
            flushErrors();
        }

        // unregister the disk from the virtual machine
        disk.setVirtualAppliance(null);
        disk.setVirtualMachine(null);
        vdcRepo.updateDisk(disk);
        
        // Trace
        if (tracer != null)
        {
            String messageTrace =
                "The hard disk resource '" + disk.getId() + "' and size of "
                    + disk.getSizeInMb() + "MB has been released from virtual machine '"
                    + vm.getName() + "'.";
            tracer.log(SeverityType.INFO, ComponentType.VIRTUAL_MACHINE,
                EventType.HARD_DISK_UNASSIGN, messageTrace);
        }
    }

    /**
     * Check the limits for a new disk.
     * 
     * @param vdc object {@link VirtualDatacenter}
     * @param sizeInMB new size we want to add as a resource.
     */
    protected void checkStorageLimits(final VirtualDatacenter vdc, final long sizeInMB)
    {
        // Must use the enterprise from VDC. When creating iSCSI volumes will be the cloud admin
        // creating volumes in other enterprises VDC
        Enterprise enterprise = vdc.getEnterprise();

        LOGGER.debug("Checking limits for enterprise {} to a locate a volume of {}MB",
            enterprise.getName(), sizeInMB);

        DatacenterLimits dcLimits =
            datacenterRepo.findDatacenterLimits(enterprise, vdc.getDatacenter());

        if (dcLimits == null)
        {
            addForbiddenErrors(APIError.DATACENTER_NOT_ALLOWED);
            flushErrors();
        }

        VirtualMachineRequirements requirements =
            new VirtualMachineRequirements(0L, 0L, sizeInMB * 1024 * 1024, 0L, 0L, 0L, 0L);

        try
        {
            enterpriseLimitChecker.checkLimits(enterprise, requirements, true);
        }
        catch (LimitExceededException ex)
        {
            addConflictErrors(new CommonError(APIError.LIMIT_EXCEEDED.getCode(), ex.toString()));
            flushErrors();
        }
    }

    /**
     * Get the next free attachment slot to be used to attach a disk or volume to the virtual
     * machine.
     * 
     * @param vm The virtual machine where the disk will be attached.
     * @return The free slot to use.
     */
    protected int getFreeAttachmentSlot(final VirtualMachine vm)
    {
        // The list is already ordered by attachment ascendent order
        List< ? extends RasdManagement> attachments = repo.findDisksAndVolumesByVirtualMachine(vm);

        // In Hyper-v only 2 attached volumes are allowed
        if (vm.getHypervisor() != null && vm.getHypervisor().getType() == HypervisorType.HYPERV_301
            && attachments.size() >= 2)
        {
            addConflictErrors(APIError.VOLUME_TOO_MUCH_ATTACHMENTS);
            flushErrors();
        }

        // Find the first free slot
        for (int i = 0; i < attachments.size(); i++)
        {
            long sequence = attachments.get(i).getAttachmentOrder();
            if (sequence != i + RasdManagement.FIRST_ATTACHMENT_SEQUENCE)
            {
                return i + RasdManagement.FIRST_ATTACHMENT_SEQUENCE; // Found gap
            }
        }

        // If no gap was found, use the next sequence
        return attachments.size() + RasdManagement.FIRST_ATTACHMENT_SEQUENCE;
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
        VirtualAppliance vapp = vdcRepo.findVirtualApplianceById(vdc, vappId);
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
        VirtualDatacenter vdc = vdcRepo.findById(vdcId);
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
        VirtualMachine vm = vdcRepo.findVirtualMachineById(vapp, vmId);
        if (vm == null)
        {
            addNotFoundErrors(APIError.NON_EXISTENT_VIRTUALMACHINE);
            flushErrors();
        }
        return vm;
    }
}
