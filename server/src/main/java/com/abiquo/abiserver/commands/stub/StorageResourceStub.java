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
