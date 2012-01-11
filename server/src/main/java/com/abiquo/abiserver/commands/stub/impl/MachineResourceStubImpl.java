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

import javax.ws.rs.core.Response.Status;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.apache.wink.client.ClientResponse;
import org.apache.wink.common.internal.utils.UriHelper;

import com.abiquo.abiserver.business.hibernate.pojohb.infrastructure.StateEnum;
import com.abiquo.abiserver.commands.stub.AbstractAPIStub;
import com.abiquo.abiserver.commands.stub.MachineResourceStub;
import com.abiquo.abiserver.pojo.infrastructure.HypervisorRemoteAccessInfo;
import com.abiquo.abiserver.pojo.infrastructure.PhysicalMachine;
import com.abiquo.abiserver.pojo.infrastructure.State;
import com.abiquo.abiserver.pojo.infrastructure.VirtualMachine;
import com.abiquo.abiserver.pojo.result.BasicResult;
import com.abiquo.abiserver.pojo.result.DataResult;
import com.abiquo.abiserver.pojo.ucs.BladeLocatorLed;
import com.abiquo.abiserver.pojo.ucs.LogicServer;
import com.abiquo.abiserver.pojo.user.Enterprise;
import com.abiquo.abiserver.pojo.user.User;
import com.abiquo.model.rest.RESTLink;
import com.abiquo.server.core.cloud.VirtualMachineDto;
import com.abiquo.server.core.cloud.VirtualMachinesDto;
import com.abiquo.server.core.enterprise.EnterpriseDto;
import com.abiquo.server.core.enterprise.User.AuthType;
import com.abiquo.server.core.enterprise.UserDto;
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
                VirtualMachine vm = dtoToVirtualMachine(dto);
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

    private VirtualMachine dtoToVirtualMachine(final VirtualMachineDto virtualMachineDto)
    {
        VirtualMachine vm = new VirtualMachine();
        vm.setCpu(virtualMachineDto.getCpu());
        vm.setDescription(virtualMachineDto.getDescription());
        vm.setHd(virtualMachineDto.getHdInBytes());
        vm.setId(virtualMachineDto.getId());
        vm.setIdType(virtualMachineDto.getIdType());
        vm.setName(virtualMachineDto.getName());
        vm.setPassword(virtualMachineDto.getPassword());
        vm.setRam(virtualMachineDto.getRam());
        vm.setState(new State(StateEnum.valueOf(virtualMachineDto.getState().name())));
        vm.setUUID(virtualMachineDto.getUuid());
        vm.setVdrpIP(virtualMachineDto.getVdrpIP());
        vm.setVdrpPort(virtualMachineDto.getVdrpPort());

        RESTLink userLink = virtualMachineDto.searchLink("user");
        if (userLink != null)
        {
            ClientResponse userResponse = get(userLink.getHref());
            if (userResponse.getStatusCode() == Status.OK.getStatusCode())
            {

                UserDto userDto = userResponse.getEntity(UserDto.class);
                User user = dtoToUser(userDto);
                vm.setUser(user);
            }
            else
            {
                populateErrors(userResponse, new BasicResult(), "getUser");
            }

        }
        RESTLink entLink = virtualMachineDto.searchLink("enterprise");
        if (userLink != null)
        {
            ClientResponse entResponse = get(entLink.getHref());
            if (entResponse.getStatusCode() == Status.OK.getStatusCode())
            {

                EnterpriseDto entDto = entResponse.getEntity(EnterpriseDto.class);
                Enterprise ent = dtoToEnterprise(entDto);
                vm.setEnterprise(ent);
            }
            else
            {
                populateErrors(entResponse, new BasicResult(), "getEnterpirse");
            }

        }
        return vm;
    }

    private User dtoToUser(final UserDto userDto)
    {
        User u = new User();

        u.setId(userDto.getId());
        u.setEmail(userDto.getEmail());
        u.setLocale(userDto.getLocale());
        u.setName(userDto.getName());
        u.setSurname(userDto.getSurname());
        u.setUser(userDto.getNick());
        u.setDescription(userDto.getDescription());
        if (!StringUtils.isBlank(userDto.getAvailableVirtualDatacenters()))
        {
            String[] split = userDto.getAvailableVirtualDatacenters().split(",");
            if (split != null)
            {
                Integer[] a = new Integer[split.length];
                int i = 0;
                for (String n : split)
                {
                    a[i++] = Integer.valueOf(n);
                }
                u.setAvailableVirtualDatacenters(a);
            }
        }
        u.setAuthType(AuthType.valueOf(userDto.getAuthType()));
        return u;
    }

    private Enterprise dtoToEnterprise(final EnterpriseDto enterpriseDto)
    {
        Enterprise e = new Enterprise();
        e.setChefClient(enterpriseDto.getChefClient());
        e.setChefClientCertificate(enterpriseDto.getChefClientCertificate());
        e.setChefURL(enterpriseDto.getChefURL());
        e.setChefValidator(enterpriseDto.getChefValidator());
        e.setChefValidatorCertificate(enterpriseDto.getChefValidatorCertificate());
        e.setId(enterpriseDto.getId());
        e.setIsReservationRestricted(enterpriseDto.getIsReservationRestricted());
        e.setName(enterpriseDto.getName());
        return e;
    }
}
