/**
 * 
 */
package com.abiquo.api.services;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.abiquo.api.exceptions.APIError;
import com.abiquo.server.core.cloud.State;
import com.abiquo.server.core.cloud.VirtualAppliance;
import com.abiquo.server.core.cloud.VirtualDatacenter;
import com.abiquo.server.core.cloud.VirtualDatacenterRep;
import com.abiquo.server.core.cloud.VirtualImage;
import com.abiquo.server.core.cloud.VirtualMachine;
import com.abiquo.server.core.infrastructure.storage.DiskManagement;

/**
 * Implements all the business logic for storage features.
 * 
 * @author jdevesa
 */
@Service
public class StorageService extends DefaultApiService
{
    @Autowired
    protected VirtualDatacenterRep vdcRepo;

    /** User service for user-specific privileges */
    @Autowired
    protected UserService userService;

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
    }

    /**
     * Create a new Hard Disk inside a Virtual Machine.
     * 
     * @param vdcId identifier of the virtual datacenter.
     * @param vappId identifier of the virtual appliance.
     * @param vmId identifier of the virtual machine.
     * @param diskSizeInMb disk size in mega bytes.
     * @return the created object {@link DiskManagement}
     */
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public DiskManagement createHardDiskIntoVM(final Integer vdcId, final Integer vappId,
        final Integer vmId, final Long diskSizeInMb)
    {
        VirtualDatacenter vdc = getVirtualDatacenter(vdcId);
        VirtualAppliance vapp = getVirtualAppliance(vdc, vappId);
        VirtualMachine vm = getVirtualMachine(vapp, vmId);

        // create the new disk from the diskSize
        Integer diskOrder = vdcRepo.findHardDisksByVirtualMachine(vm).size() + 1;
        DiskManagement createdDisk = new DiskManagement(vdc, vapp, vm, diskSizeInMb, diskOrder);
        validate(createdDisk);

        vdcRepo.insertHardDisk(createdDisk);

        return createdDisk;
    }

    /**
     * Deletes a hard disk. Machine must be stopped and user should have the enough permissions.
     * 
     * @param vdcId identifier of the virtual datacenter.
     * @param vappId identifier of the virtual appliance.
     * @param vmId identifier of the virtual machine.
     * @param diskSizeInMb disk size in mega bytes.
     */
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public void deleteHardDisk(final Integer vdcId, final Integer vappId, final Integer vmId,
        final Integer diskOrder)
    {
        if (diskOrder == 0)
        {
            addConflictErrors(APIError.HD_DISK_0_CAN_NOT_BE_DELETED);
            flushErrors();
        }

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

        List<DiskManagement> disks = vdcRepo.findHardDisksByVirtualMachine(vm);
        for (DiskManagement disk : disks)
        {
            // delete the disk.
            if (disk.getAttachmentOrder() == diskOrder)
            {
                vdcRepo.removeHardDisk(disk);
            }
            // disk already deleted, update the attachment order in the rest
            // if their order is bigger than the deleted one.
            if (disk.getAttachmentOrder() > diskOrder)
            {
                disk.setAttachmentOrder(disk.getAttachmentOrder() - 1);
                vdcRepo.updateDisk(disk);
            }
        }
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
        final Integer vmId, final Integer diskOrder)
    {
        VirtualDatacenter vdc = getVirtualDatacenter(vdcId);
        VirtualAppliance vapp = getVirtualAppliance(vdc, vappId);
        VirtualMachine vm = getVirtualMachine(vapp, vmId);

        // disk order 0 always will be the readOnly image disk
        if (diskOrder.equals(0))
        {
            VirtualImage vi = vm.getVirtualImage();
            DiskManagement diskRO = new DiskManagement(vdc, vapp, vm, vi.getDiskFileSize(), 0);
            return diskRO;
        }
        else
        {
            DiskManagement targetDisk = vdcRepo.findHardDiskByVirtualMachine(vm, diskOrder);
            if (targetDisk == null)
            {
                addNotFoundErrors(APIError.HD_NON_EXISTENT_HARD_DISK);
                flushErrors();
            }

            return targetDisk;
        }
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

        // Insert the first hard disk, based on its virtual image
        VirtualImage vi = vm.getVirtualImage();
        DiskManagement diskRO = new DiskManagement(vdc, vapp, vm, vi.getDiskFileSize(), 0);
        disks.add(diskRO);

        disks.addAll(vdcRepo.findHardDisksByVirtualMachine(vm));

        return disks;
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
