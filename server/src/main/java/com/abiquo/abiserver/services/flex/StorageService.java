/**
 * 
 */
package com.abiquo.abiserver.services.flex;

import com.abiquo.abiserver.commands.stub.APIStubFactory;
import com.abiquo.abiserver.commands.stub.StorageResourceStub;
import com.abiquo.abiserver.commands.stub.impl.StorageResourceStubImpl;
import com.abiquo.abiserver.pojo.authentication.UserSession;
import com.abiquo.abiserver.pojo.result.BasicResult;

/**
 * Exposes the Disk storage functionality to be used by the Flex framework.
 * 
 * @author jdevesa@abiquo.com
 */
public class StorageService
{
    /** The stub used to connect to the API. */
    private StorageResourceStub storageStub;

    /**
     * Default constructor. Initializes the stub object with its implementation.
     */
    public StorageService()
    {
        storageStub = new StorageResourceStubImpl();
    }

    /**
     * Proxies the stub with the user session who performs the call.
     * 
     * @param userSession user who performs the action.
     * @return a proxied instance of the {@link StorageResourceStub}
     */
    protected StorageResourceStub proxyStub(final UserSession userSession)
    {
        return APIStubFactory.getInstance(userSession, storageStub, StorageResourceStub.class);
    }

    /**
     * Requests to API to retrieve all the virtual machine disks.
     * 
     * @param userSession user who performs the action.
     * @param vdcId virtual datacenter id.
     * @param vappId virtual appliance id.
     * @param vmId virtual machine id.
     * @return a BasicResult encapsulating the disk info.
     */
    public BasicResult getDisksByVirtualMachine(final UserSession userSession, final Integer vdcId,
        final Integer vappId, final Integer vmId)
    {
        return proxyStub(userSession).getDisksByVirtualMachine(vdcId, vappId, vmId);
    }

    /**
     * Requests to API to retrieve the virtual machine disk based on its order.
     * 
     * @param userSession user who performs the action.
     * @param vdcId virtual datacenter id.
     * @param vappId virtual appliance id.
     * @param vmId virtual machine id.
     * @param diskOrder order of the disk inside the virtual machine.
     * @return a BasicResult encapsulating the disk info.
     */
    public BasicResult getDiskByVirtualMachine(final UserSession userSession, final Integer vdcId,
        final Integer vappId, final Integer vmId, final Integer diskOrder)
    {
        return proxyStub(userSession).getDiskByVirtualMachine(vdcId, vappId, vmId, diskOrder);
    }

    /**
     * Requests to API to create a new disk into the virtual machine.
     * 
     * @param userSession user who performs the action.
     * @param vdcId virtual datacenter id.
     * @param vappId virtual appliance id.
     * @param vmId virtual machine id.
     * @return a BasicResult encapsulating the disk info.
     */
    public BasicResult createDiskIntoVirtualMachine(final UserSession userSession,
        final Integer vdcId, final Integer vappId, final Integer vmId, final Long diskSizeInMb)
    {
        return proxyStub(userSession).createDiskIntoVirtualMachine(vdcId, vappId, vmId,
            diskSizeInMb);
    }

    /**
     * Requests to API to delete a disk into the virtual machine.
     * 
     * @param userSession user who performs the action.
     * @param vdcId virtual datacenter id.
     * @param vappId virtual appliance id.
     * @param vmId virtual machine id.
     * @param diskOrder order of the disk inside the virtual machine.
     * @return a BasicResult encapsulating the disk info.
     */
    public BasicResult deleteDiskFromVirtualMachine(final UserSession userSession,
        final Integer vdcId, final Integer vappId, final Integer vmId, final Integer diskOrder)
    {
        return proxyStub(userSession).deleteDiskFromVirtualMachine(vdcId, vappId, vmId, diskOrder);
    }
}
