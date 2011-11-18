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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.wink.client.ClientResponse;

import com.abiquo.abiserver.commands.stub.AbstractAPIStub;
import com.abiquo.abiserver.commands.stub.MachinesResourceStub;
import com.abiquo.abiserver.networking.IPAddress;
import com.abiquo.abiserver.pojo.infrastructure.DataCenter;
import com.abiquo.abiserver.pojo.infrastructure.PhysicalMachine;
import com.abiquo.abiserver.pojo.infrastructure.PhysicalMachineCreation;
import com.abiquo.abiserver.pojo.infrastructure.Rack;
import com.abiquo.abiserver.pojo.infrastructure.UcsRack;
import com.abiquo.abiserver.pojo.infrastructure.VirtualMachine;
import com.abiquo.abiserver.pojo.result.BasicResult;
import com.abiquo.abiserver.pojo.result.DataResult;
import com.abiquo.model.enumerator.HypervisorType;
import com.abiquo.model.enumerator.MachineState;
import com.abiquo.model.rest.RESTLink;
import com.abiquo.model.transport.error.ErrorDto;
import com.abiquo.model.transport.error.ErrorsDto;
import com.abiquo.server.core.infrastructure.DatacenterDto;
import com.abiquo.server.core.infrastructure.MachineDto;
import com.abiquo.server.core.infrastructure.MachinesDto;
import com.abiquo.server.core.infrastructure.MachinesToCreateDto;
import com.abiquo.server.core.infrastructure.RackDto;

public class MachinesResourceStubImpl extends AbstractAPIStub implements MachinesResourceStub
{
    public static final String MULTIPLE_MACHINES_MIME_TYPE = "application/machinesdto+xml";

    /**
     * @see com.abiquo.abiserver.commands.stub.MachinesResourceStub#getMachines(com.abiquo.server.core.infrastructure.UcsRack)
     */
    @Override
    public DataResult<List<PhysicalMachine>> getMachines(final UcsRack ucsRack)
    {
        // PREMIUM
        return null;
    }

    /**
     * @see com.abiquo.abiserver.commands.stub.MachinesResourceStub#refreshMachines(com.abiquo.abiserver.pojo.infrastructure.UcsRack)
     */
    @Override
    public DataResult<List<PhysicalMachine>> refreshMachines(final UcsRack ucsRack)
    {
        // PREMIUM
        return null;
    }

    @Override
    public DataResult<List<PhysicalMachine>> getPhysicalMachinesByRack(final Integer datacenterId,
        final Integer rackId, final String filters)
    {
        DataResult<List<PhysicalMachine>> result = new DataResult<List<PhysicalMachine>>();

        DataResult<Rack> rackResult = new DataResult<Rack>();
        ClientResponse rackResponse = get(createRackLink(datacenterId, rackId));

        if (rackResponse.getStatusCode() == 200)
        {
            RackDto rDto = rackResponse.getEntity(RackDto.class);
            rackResult.setSuccess(true);
            Map<String, DatacenterDto> catchedDatacenters = new HashMap<String, DatacenterDto>();
            Rack rack =
                Rack.create(rDto,
                    getDatacenterFromLink(rDto.searchLink("datacenter"), catchedDatacenters));

            rackResult.setData(rack);

            String uri = createMachinesLink(datacenterId, rackId);

            if (filters != null && !filters.isEmpty())
            {
                uri += '?' + filters;
            }

            ClientResponse response = get(uri);

            if (response.getStatusCode() == 200)
            {
                List<PhysicalMachine> list = new ArrayList<PhysicalMachine>();
                MachinesDto pms = response.getEntity(MachinesDto.class);
                for (MachineDto dto : pms.getCollection())
                {
                    PhysicalMachine pm = PhysicalMachine.create(dto, rack.getDataCenter(), rack);
                    pm.completeInfo(dto);
                    list.add(pm);
                }
                result.setSuccess(true);
                result.setData(list);
            }
            else
            {
                populateErrors(response, result, "getPhysicalMachinesByRack");
            }

        }
        else
        {
            populateErrors(rackResponse, rackResult, "getPhysicalMachinesByRack");
        }
        return result;

    }

    private DataCenter getDatacenterFromLink(final RESTLink link,
        final Map<String, DatacenterDto> cache)
    {
        String dcUri = link.getHref();

        DatacenterDto dto = null;
        if (!cache.containsKey(dcUri))
        {
            dto = get(dcUri).getEntity(DatacenterDto.class);
            cache.put(dcUri, dto);
        }
        else
        {
            dto = cache.get(dcUri);
        }

        return DataCenter.create(dto);
    }

    @Override
    public DataResult<MachineDto> createPhysicalMachine(
        final PhysicalMachineCreation createPhysicalMachine)
    {
        Rack rack = (Rack) createPhysicalMachine.getPhysicalMachine().getAssignedTo();
        String uri = createMachinesLink(rack.getDataCenter().getId(), rack.getId());

        DataResult<MachineDto> result = new DataResult<MachineDto>();

        MachineDto dto = createPhysicalMachine.toMachineDto();

        ClientResponse response = post(uri, dto);
        if (response.getStatusCode() == 201)
        {
            result.setSuccess(true);
        }
        else
        {
            populateErrors(response, result, "createPhysicalMachine");
        }

        return result;

    }

    @Override
    public DataResult<MachineDto> editPhysicalMachine(
        final PhysicalMachineCreation createPhysicalMachine)
    {
        Rack rack = (Rack) createPhysicalMachine.getPhysicalMachine().getAssignedTo();
        String uri =
            createMachineLink(rack.getDataCenter().getId(), rack.getId(), createPhysicalMachine
                .getPhysicalMachine().getId());

        DataResult<MachineDto> result = new DataResult<MachineDto>();

        MachineDto dto = createPhysicalMachine.toMachineDto();

        ClientResponse response = put(uri, dto);
        if (response.getStatusCode() == 200)
        {
            result.setSuccess(true);
        }
        else
        {
            populateErrors(response, result, "editPhysicalMachine");
        }

        return result;

    }

    @Override
    public DataResult<List<PhysicalMachine>> createMultiplePhysicalMachine(
        final Integer datacenterId, final Integer rackId, final IPAddress ipFrom,
        final IPAddress ipTo, final Integer hypervisorType, final String user,
        final String password, final Integer port, final String vSwitch)
    {
        DataResult<List<PhysicalMachine>> result = new DataResult<List<PhysicalMachine>>();

        // getting rack
        DataResult<Rack> rackResult = new DataResult<Rack>();
        ClientResponse rackResponse = get(createRackLink(datacenterId, rackId));

        if (rackResponse.getStatusCode() == 200)
        {
            RackDto rDto = rackResponse.getEntity(RackDto.class);
            rackResult.setSuccess(true);
            Map<String, DatacenterDto> catchedDatacenters = new HashMap<String, DatacenterDto>();
            Rack rack =
                Rack.create(rDto,
                    getDatacenterFromLink(rDto.searchLink("datacenter"), catchedDatacenters));

            rackResult.setData(rack);

            // creating machines
            String uri = createMachinesLinkMultiplePost(datacenterId, rackId);

            MachinesToCreateDto dto =
                new MachinesToCreateDto(ipFrom.toString(), ipTo.toString(), HypervisorType.fromId(
                    hypervisorType).getValue(), user, password, port, vSwitch);

            ClientResponse response =
                post(uri, dto, MachinesResourceStubImpl.MULTIPLE_MACHINES_MIME_TYPE);

            if (response.getStatusCode() == 201)
            {
                List<PhysicalMachine> list = new ArrayList<PhysicalMachine>();
                MachinesDto pms = response.getEntity(MachinesDto.class);
                for (MachineDto d : pms.getCollection())
                {
                    PhysicalMachine pm = PhysicalMachine.create(d, rack.getDataCenter(), rack);
                    pm.completeInfo(d);
                    list.add(pm);
                }
                result.setSuccess(true);
                result.setData(list);

                if (pms.getErrors() == null || pms.getErrors().isEmpty())
                {
                    result.setSuccess(true);
                }
                else
                {
                    result.setMessage(fromErrorsToMessage(pms.getErrors()));
                    result.setSuccess(false);
                }

            }
            else
            {
                populateErrors(response, result, "createMultiplePhysicalMachine");
            }
        }
        else
        {
            populateErrors(rackResponse, rackResult, "createMultiplePhysicalMachine");
        }

        return result;

    }

    @Override
    public BasicResult isStonithUp(final Integer datacenterId, final Integer rackId,
        final Integer machineId, final String ip, final String user, final String password,
        final Integer port)
    {
        // PREMIUM
        return null;
    }

    @Override
    public DataResult<List<VirtualMachine>> getVirtualMachinesFromPM(final Integer dcId,
        final Integer rackId, final Integer pmId)
    {
        // PREMIUM
        return null;
    }

    @Override
    public DataResult<MachineState> checkPhysicalMachineState(final Integer datacenterId,
        final Integer rackId, final Integer machineId, final String ip,
        final HypervisorType hypervisor, final String user, final String password,
        final Integer port)
    {
        // PREMIUM
        return null;
    }

    @Override
    public DataResult<PhysicalMachineCreation> getMachineInfo(final Integer datacenterId,
        final String ip, final String user, final String password, final HypervisorType hypervisor,
        final Integer port)
    {
        // PREMIUM
        return null;
    }

    @Override
    public DataResult<Integer> getHypervisorType(final Integer datacenterId, final String ip)
    {
        // PREMIUM
        return null;
    }

    private String fromErrorsToMessage(final ErrorsDto errors)
    {
        String message = "";

        if (errors != null && !errors.isEmpty())
        {
            for (ErrorDto dto : errors.getCollection())
            {
                message += dto.getCode() + ": " + dto.getMessage() + "\n";
            }
        }

        return message;
    }

    @Override
    public BasicResult deletePhysicalMachine(final PhysicalMachine machine)
    {
        String uri = createMachineLink(machine);

        BasicResult result = new BasicResult();

        ClientResponse response = delete(uri);

        if (response.getStatusCode() == 204)
        {
            result.setSuccess(true);
        }
        else
        {
            populateErrors(response, result, "deletePhysicalMachine");
        }

        return result;
    }

    @Override
    public BasicResult importVirtualMachineToVirtualAppliance(final Integer dcId,
        final Integer rackId, final Integer machineId, final Integer vmId, final Integer vdcId,
        final Integer vappId)
    {
        // PREMIUM
        return null;
    }

}
