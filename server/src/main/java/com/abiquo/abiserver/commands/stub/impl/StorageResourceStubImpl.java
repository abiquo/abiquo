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

package com.abiquo.abiserver.commands.stub.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.wink.client.ClientResponse;

import com.abiquo.abiserver.commands.stub.AbstractAPIStub;
import com.abiquo.abiserver.commands.stub.StorageResourceStub;
import com.abiquo.abiserver.pojo.result.BasicResult;
import com.abiquo.abiserver.pojo.result.DataResult;
import com.abiquo.abiserver.pojo.virtualhardware.Disk;
import com.abiquo.server.core.infrastructure.storage.DiskManagementDto;
import com.abiquo.server.core.infrastructure.storage.DisksManagementDto;

public class StorageResourceStubImpl extends AbstractAPIStub implements StorageResourceStub
{

    /**
     * Generates the flex object {@link Disk} from the input object {@link DiskManagementDto}
     * 
     * @param dto input object.
     * @return the corresponding flex output object.
     */
    public static Disk createFlexObject(final DiskManagementDto dto)
    {
        Disk disk = new Disk();
        disk.setDiskSizeInMb(dto.getSizeInMb());
        disk.setReadOnly(dto.getReadOnly());
        return disk;
    }

    @Override
    public BasicResult getDisksByVirtualMachine(final Integer vdcId, final Integer vappId,
        final Integer vmId)
    {
        DataResult<List<Disk>> result = new DataResult<List<Disk>>();

        String uri = createVirtualMachineDisksLink(vdcId, vappId, vmId);
        ClientResponse response = get(uri);

        if (response.getStatusCode() == 200)
        {
            DisksManagementDto dtos = response.getEntity(DisksManagementDto.class);
            List<Disk> returnDisks = new ArrayList<Disk>();

            for (DiskManagementDto dto : dtos.getCollection())
            {
                returnDisks.add(createFlexObject(dto));
            }

            result.setData(returnDisks);
            result.setSuccess(Boolean.TRUE);
        }
        else
        {
            populateErrors(response, result, "getDisksByVirtualMachine");
        }

        return result;
    }

    @Override
    public BasicResult getDiskByVirtualMachine(final Integer vdcId, final Integer vappId,
        final Integer vmId, final Integer diskOrder)
    {
        DataResult<Disk> result = new DataResult<Disk>();

        String uri = createVirtualMachineDiskLink(vdcId, vappId, vmId, diskOrder);
        ClientResponse response = get(uri);

        if (response.getStatusCode() == 200)
        {
            DiskManagementDto dto = response.getEntity(DiskManagementDto.class);
            result.setData(createFlexObject(dto));
            result.setSuccess(Boolean.TRUE);
        }
        else
        {
            populateErrors(response, result, "getDiskByVirtualMachine");
        }

        return result;
    }

    @Override
    public BasicResult createDiskIntoVirtualMachine(final Integer vdcId, final Integer vappId,
        final Integer vmId, final Long diskSizeInMb)
    {
        DataResult<Disk> result = new DataResult<Disk>();

        String uri = createVirtualMachineDisksLink(vdcId, vappId, vmId);
        DiskManagementDto inputDto = new DiskManagementDto();
        inputDto.setSizeInMb(diskSizeInMb);
        ClientResponse response = post(uri, inputDto);

        if (response.getStatusCode() == 201)
        {
            DiskManagementDto dto = response.getEntity(DiskManagementDto.class);
            result.setData(createFlexObject(dto));
            result.setSuccess(Boolean.TRUE);
        }
        else
        {
            populateErrors(response, result, "createDiskIntoVirtualMachine");
        }

        return result;
    }

    @Override
    public BasicResult deleteDiskFromVirtualMachine(final Integer vdcId, final Integer vappId,
        final Integer vmId, final Integer diskOrder)
    {
        BasicResult result = new BasicResult();

        String uri = createVirtualMachineDiskLink(vdcId, vappId, vmId, diskOrder);
        ClientResponse response = delete(uri);

        if (response.getStatusCode() == 204)
        {
            result.setSuccess(Boolean.TRUE);
        }
        else
        {
            populateErrors(response, result, "deleteDiskFromVirtualMachine");
        }

        return result;
    }

}
