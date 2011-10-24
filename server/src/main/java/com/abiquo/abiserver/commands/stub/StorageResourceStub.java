/**
 * 
 */
package com.abiquo.abiserver.commands.stub;

import com.abiquo.abiserver.pojo.result.BasicResult;

/**
 * @author jdevesa
 */
public interface StorageResourceStub
{
    /**
     * Requests to API to retrieve all the virtual machine disks.
     * 
     * @param vdcId virtual datacenter id.
     * @param vappId virtual appliance id.
     * @param vmId virtual machine id.
     * @return a BasicResult encapsulating the disk info.
     */
    public BasicResult getDisksByVirtualMachine(final Integer vdcId, final Integer vappId,
        final Integer vmId);

    /**
     * Requests to API to retrieve the virtual machine disk based on its order.
     * 
     * @param vdcId virtual datacenter id.
     * @param vappId virtual appliance id.
     * @param vmId virtual machine id.
     * @param diskOrder order of the disk inside the virtual machine.
     * @return a BasicResult encapsulating the disk info.
     */
    public BasicResult getDiskByVirtualMachine(final Integer vdcId, final Integer vappId,
        final Integer vmId, Integer diskOrder);

    /**
     * Requests to API to create a new disk into the virtual machine.
     * 
     * @param vdcId virtual datacenter id.
     * @param vappId virtual appliance id.
     * @param vmId virtual machine id.
     * @return a BasicResult encapsulating the disk info.
     */
    public BasicResult createDiskIntoVirtualMachine(final Integer vdcId, final Integer vappId,
        final Integer vmId, final Long diskSizeInMb);

    /**
     * Requests to API to delete a disk into the virtual machine.
     * 
     * @param vdcId virtual datacenter id.
     * @param vappId virtual appliance id.
     * @param vmId virtual machine id.
     * @param diskOrder order of the disk inside the virtual machine.
     * @return a BasicResult encapsulating the disk info.
     */
    public BasicResult deleteDiskFromVirtualMachine(final Integer vdcId, final Integer vappId,
        final Integer vmId, Integer diskOrder);

}
