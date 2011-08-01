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
import com.abiquo.abiserver.pojo.infrastructure.DataCenter;
import com.abiquo.abiserver.pojo.infrastructure.PhysicalMachine;
import com.abiquo.abiserver.pojo.infrastructure.Rack;
import com.abiquo.abiserver.pojo.infrastructure.UcsRack;
import com.abiquo.abiserver.pojo.infrastructure.VirtualMachine;
import com.abiquo.abiserver.pojo.result.DataResult;
import com.abiquo.model.rest.RESTLink;
import com.abiquo.server.core.infrastructure.DatacenterDto;
import com.abiquo.server.core.infrastructure.MachineDto;
import com.abiquo.server.core.infrastructure.MachinesDto;
import com.abiquo.server.core.infrastructure.RackDto;

public class MachinesResourceStubImpl extends AbstractAPIStub implements MachinesResourceStub
{
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
    public DataResult<List<VirtualMachine>> getVirtualMachinesFromPM(final Integer dcId,
        final Integer rackId, final Integer pmId)
    {
        // PREMIUM
        return null;
    }
}
