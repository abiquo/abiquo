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

import org.apache.wink.client.ClientResponse;
import org.apache.wink.common.internal.utils.UriHelper;

import com.abiquo.abiserver.commands.stub.AbstractAPIStub;
import com.abiquo.abiserver.commands.stub.MachineResourceStub;
import com.abiquo.abiserver.pojo.infrastructure.HypervisorRemoteAccessInfo;
import com.abiquo.abiserver.pojo.infrastructure.PhysicalMachine;
import com.abiquo.abiserver.pojo.result.BasicResult;
import com.abiquo.abiserver.pojo.result.DataResult;
import com.abiquo.server.core.infrastructure.MachineDto;

public class MachineResourceStubImpl extends AbstractAPIStub implements MachineResourceStub
{
    @Override
    public DataResult<HypervisorRemoteAccessInfo> getHypervisorRemoteAccess(PhysicalMachine machine)
    {
        String uri = createMachineLink(machine) + "?credentials=true";

        DataResult<HypervisorRemoteAccessInfo> result =
            new DataResult<HypervisorRemoteAccessInfo>();

        ClientResponse response = get(uri);
        if (response.getStatusCode() == 200)
        {
            MachineDto dto = response.getEntity(MachineDto.class);

            HypervisorRemoteAccessInfo info = new HypervisorRemoteAccessInfo();
            info.setParam1(dto.getUser());
            info.setParam2(dto.getPassword());

            result.setSuccess(true);
            result.setData(info);
        }
        else
        {
            populateErrors(response, result, "getMachineWithCredentials");
        }

        return result;
    }

    @Override
    public BasicResult deleteNotManagedVirtualMachines(PhysicalMachine machine)
    {
        String uri = createMachineLink(machine);
        uri = UriHelper.appendPathToBaseUri(uri, "action/virtualmachines");

        BasicResult result = new BasicResult();

        ClientResponse response = delete(uri);
        if (response.getStatusCode() == 204)
        {
            result.setSuccess(true);
        }
        else
        {
            populateErrors(response, result, "deleteNotManagedVirtualMachines");
        }

        return result;
    }

    public static MachineDto fromPhysicalMachineToDto(PhysicalMachine machine)
    {
        MachineDto dto = new MachineDto();
        dto.setId(machine.getId());
        dto.setDescription(machine.getDescription());
        dto.setRealCpuCores(machine.getRealCpu());
        dto.setRealHardDiskInMb(machine.getRealStorage());
        dto.setRealRamInMb(machine.getRealRam());
        dto.setVirtualCpuCores(machine.getCpu());
        dto.setVirtualCpusUsed(machine.getCpuUsed());
        dto.setVirtualCpusPerCore(machine.getCpuRatio());
        dto.setVirtualHardDiskInMb(machine.getHd());
        dto.setVirtualHardDiskUsedInMb(machine.getHdUsed());
        dto.setVirtualRamInMb(machine.getRam());
        dto.setVirtualRamUsedInMb(machine.getRamUsed());

        // FIXME: Complete this method. Right now we don't need any more info.
        return dto;
    }

    /**
     * @see com.abiquo.abiserver.commands.stub.RacksResourceStub#powerOff(com.abiquo.abiserver.pojo.infrastructure.PhysicalMachine)
     */
    @Override
    public BasicResult powerOff(PhysicalMachine machine)
    {
        // PREMIUM
        return null;
    }

    /**
     * @see com.abiquo.abiserver.commands.stub.RacksResourceStub#powerOn(com.abiquo.abiserver.pojo.infrastructure.PhysicalMachine)
     */
    @Override
    public BasicResult powerOn(PhysicalMachine machine)
    {
        // PREMIUM
        return null;
    }

}
