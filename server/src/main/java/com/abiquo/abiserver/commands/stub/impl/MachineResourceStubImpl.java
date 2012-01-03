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

import org.apache.commons.codec.binary.Base64;
import org.apache.wink.client.ClientResponse;
import org.apache.wink.common.internal.utils.UriHelper;

import com.abiquo.abiserver.commands.stub.AbstractAPIStub;
import com.abiquo.abiserver.commands.stub.MachineResourceStub;
import com.abiquo.abiserver.pojo.infrastructure.HypervisorRemoteAccessInfo;
import com.abiquo.abiserver.pojo.infrastructure.PhysicalMachine;
import com.abiquo.abiserver.pojo.infrastructure.VirtualMachine;
import com.abiquo.abiserver.pojo.result.BasicResult;
import com.abiquo.abiserver.pojo.result.DataResult;
import com.abiquo.abiserver.pojo.ucs.BladeLocatorLed;
import com.abiquo.abiserver.pojo.ucs.LogicServer;
import com.abiquo.server.core.cloud.VirtualMachineDto;
import com.abiquo.server.core.cloud.VirtualMachinesDto;
import com.abiquo.server.core.infrastructure.MachineDto;

public class MachineResourceStubImpl extends AbstractAPIStub implements MachineResourceStub
{

    @Override
    public DataResult<HypervisorRemoteAccessInfo> getHypervisorRemoteAccess(
        final PhysicalMachine machine)
    {
        String uri = createMachineLink(machine) + "?credentials=true";

        DataResult<HypervisorRemoteAccessInfo> result =
            new DataResult<HypervisorRemoteAccessInfo>();

        ClientResponse response = get(uri);
        if (response.getStatusCode() == 200)
        {
            MachineDto dto = response.getEntity(MachineDto.class);

            HypervisorRemoteAccessInfo info = new HypervisorRemoteAccessInfo();

            String encodedUser = new String(Base64.encodeBase64(dto.getUser().getBytes()));
            String encodedPass = new String(Base64.encodeBase64(dto.getPassword().getBytes()));

            info.setParam1(encodedUser);
            info.setParam2(encodedPass);

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
    public BasicResult deleteNotManagedVirtualMachines(final PhysicalMachine machine)
    {
        String uri = createMachineLink(machine);
        uri = UriHelper.appendPathToBaseUri(uri, "/virtualmachines");

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

    public static MachineDto fromPhysicalMachineToDto(final PhysicalMachine machine)
    {
        MachineDto dto = new MachineDto();
        dto.setId(machine.getId());
        dto.setDescription(machine.getDescription());
        dto.setVirtualCpuCores(machine.getCpu());
        dto.setVirtualCpusUsed(machine.getCpuUsed());
        dto.setVirtualCpusPerCore(machine.getCpuRatio());
        dto.setVirtualRamInMb(machine.getRam());
        dto.setVirtualRamUsedInMb(machine.getRamUsed());

        // FIXME: Complete this method. Right now we don't need any more info.
        return dto;
    }

    /**
     * @see com.abiquo.abiserver.commands.stub.MachineResourceStub#powerOff(com.abiquo.abiserver.pojo.infrastructure.PhysicalMachine)
     */
    @Override
    public BasicResult powerOff(final PhysicalMachine machine)
    {
        // PREMIUM
        return null;
    }

    /**
     * @see com.abiquo.abiserver.commands.stub.MachineResourceStub#powerOn(com.abiquo.abiserver.pojo.infrastructure.PhysicalMachine)
     */
    @Override
    public BasicResult powerOn(final PhysicalMachine machine)
    {
        // PREMIUM
        return null;
    }

    @Override
    public BasicResult deletePhysicalMachine(final PhysicalMachine machine)
    {
        // PREMIUM
        return null;
    }

    /**
     * @see com.abiquo.abiserver.commands.stub.MachineResourceStub#bladeLocatorLED(PhysicalMachine)
     */
    @Override
    public BasicResult bladeLocatorLED(final PhysicalMachine machine)
    {
        // PREMIUM
        return null;
    }

    /**
     * Returns teh {@link LogicServer} in blade.
     * 
     * @param ucsRack ucsRack.
     * @return wrapper which contains the {@link LogicServer} which is the blade. Or in case of
     *         error the appropiate object.
     */
    @Override
    public DataResult<LogicServer> getBladeLogicServer(final PhysicalMachine machine)
    {
        // PREMIUM
        return null;
    }

    /**
     * @see com.abiquo.abiserver.commands.stub.MachineResourceStub#bladeLocatorLEDoff(com.abiquo.abiserver.pojo.infrastructure.PhysicalMachine)
     */
    @Override
    public BasicResult bladeLocatorLEDoff(final PhysicalMachine machine)
    {
        // PREMIUM
        return null;
    }

    /**
     * @see com.abiquo.abiserver.commands.stub.MachineResourceStub#getBladeLocatorLed(com.abiquo.abiserver.pojo.infrastructure.PhysicalMachine)
     */
    @Override
    public DataResult<BladeLocatorLed> getBladeLocatorLed(final PhysicalMachine machine)
    {
        // PREMIUM
        return null;
    }

    @Override
    public BasicResult getVirtualMachinesFromMachine(final Integer datacenterId,
        final Integer rackId, final Integer machineId)
    {
        String uri = createMachineLinkVms(datacenterId, rackId, machineId);

        DataResult<List<VirtualMachine>> result = new DataResult<List<VirtualMachine>>();

        ClientResponse response = get(uri);
        if (response.getStatusCode() == 200)
        {
            VirtualMachinesDto dtos = response.getEntity(VirtualMachinesDto.class);
            List<VirtualMachine> vms = new ArrayList<VirtualMachine>();
            for (VirtualMachineDto dto : dtos.getCollection())
            {
                VirtualMachine vm = VirtualMachine.createFlexObject(dto);
                vms.add(vm);
            }
            result.setData(vms);
            result.setSuccess(Boolean.TRUE);
        }
        else
        {
            populateErrors(response, result, "getVirtualMachinesFromMachine");
        }

        return result;
    }

}
