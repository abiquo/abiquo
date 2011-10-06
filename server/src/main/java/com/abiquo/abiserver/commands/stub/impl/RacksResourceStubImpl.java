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
import com.abiquo.abiserver.commands.stub.RacksResourceStub;
import com.abiquo.abiserver.networking.IPAddress;
import com.abiquo.abiserver.pojo.infrastructure.DataCenter;
import com.abiquo.abiserver.pojo.infrastructure.PhysicalMachine;
import com.abiquo.abiserver.pojo.infrastructure.Rack;
import com.abiquo.abiserver.pojo.infrastructure.UcsRack;
import com.abiquo.abiserver.pojo.result.BasicResult;
import com.abiquo.abiserver.pojo.result.DataResult;
import com.abiquo.abiserver.pojo.result.ListRequest;
import com.abiquo.abiserver.pojo.ucs.Fsm;
import com.abiquo.abiserver.pojo.ucs.LogicServer;
import com.abiquo.abiserver.pojo.ucs.Organization;
import com.abiquo.model.enumerator.HypervisorType;
import com.abiquo.server.core.infrastructure.RackDto;
import com.abiquo.server.core.infrastructure.RacksDto;

public class RacksResourceStubImpl extends AbstractAPIStub implements RacksResourceStub
{
    /**
     * @see com.abiquo.abiserver.commands.stub.RacksResourceStub#createUcsRack(com.abiquo.server.core.infrastructure.UcsRack)
     */
    @Override
    public DataResult<UcsRack> createUcsRack(final UcsRack ucsRack)
    {
        // PREMIUM
        return null;
    }

    /**
     * @see com.abiquo.abiserver.commands.stub.RacksResourceStub#getAllNotManagedRacks(com.abiquo.abiserver.pojo.infrastructure.DataCenter)
     */
    @Override
    public DataResult<List<Rack>> getAllNotManagedRacks(final DataCenter datacenter)
    {
        // PREMIUM
        return getAllNotManagedRacks(datacenter, null);
    }

    @Override
    public DataResult<List<Rack>> getAllNotManagedRacks(final DataCenter datacenter,
        final String filter)
    {
        // PREMIUM
        return null;
    }

    @Override
    public BasicResult associateBlades(final Integer datacenterId, final Integer rackId,
        final IPAddress ipFrom, final IPAddress ipTo, final HypervisorType hypervisorType,
        final String user, final String password, final Integer port, final String vSwitchName)
    {
        // PREMIUM
        return null;
    }

    @Override
    public BasicResult powerOnMachine(final Integer datacenterId, final Integer rackId,
        final Integer machineId)
    {
        // PREMIUM
        return null;
    }

    @Override
    public BasicResult powerOffMachine(final Integer datacenterId, final Integer rackId,
        final Integer machineId)
    {
        // PREMIUM
        return null;
    }

    /**
     * @see com.abiquo.abiserver.commands.stub.RacksResourceStub#getUcsRacks(com.abiquo.abiserver.pojo.infrastructure.DataCenter)
     */
    @Override
    public DataResult<List<UcsRack>> getUcsRacks(final DataCenter datacenter)
    {
        // PREMIUM
        return getUcsRacks(datacenter, null);
    }

    @Override
    public DataResult<List<UcsRack>> getUcsRacks(final DataCenter datacenter, final String fitler)
    {
        // PREMIUM
        return null;
    }

    // ___________ COMMUNITY _____________ //

    @Override
    public DataResult<List<Rack>> getRacksByDatacenter(final DataCenter datacenter)
    {
        return getRacksByDatacenter(datacenter, null);
    }

    @Override
    public DataResult<List<Rack>> getRacksByDatacenter(final DataCenter datacenter,
        final String filter)
    {

        DataResult<List<Rack>> result = new DataResult<List<Rack>>();

        String uri = createRacksLink(datacenter.getId());

        if (filter != null && filter.isEmpty())
        {
            uri += filter;
        }

        ClientResponse response = get(uri);

        if (response.getStatusCode() == 200)
        {
            RacksDto responseDto = response.getEntity(RacksDto.class);
            List<Rack> racks = new ArrayList<Rack>();

            for (RackDto rack : responseDto.getCollection())
            {
                racks.add(Rack.create(rack, datacenter));
            }
            result.setSuccess(true);
            result.setData(racks);
        }
        else
        {
            populateErrors(response, result, "getRacksByDatacenter");
        }

        return result;
    }

    @Override
    public DataResult<Rack> createRack(final Rack rack)
    {
        DataResult<Rack> result = new DataResult<Rack>();

        String uri = createRacksLink(rack.getDataCenter().getId());

        RackDto dto = fromRackToDto(rack);
        dto.setId(null);

        ClientResponse response = post(uri, dto);

        if (response.getStatusCode() == 201)
        {
            RackDto rdto = response.getEntity(RackDto.class);

            result.setData(Rack.create(rdto, rack.getDataCenter()));
            result.setSuccess(true);
        }
        else
        {
            populateErrors(response, result, "createRack");
        }

        return result;
    }

    @Override
    public DataResult<Rack> modifyRack(final Rack rack)
    {
        DataResult<Rack> result = new DataResult<Rack>();

        String uri = createRacksLink(rack.getDataCenter().getId(), rack.getId());

        RackDto dto = fromRackToDto(rack);

        ClientResponse response = put(uri, dto);

        if (response.getStatusCode() == 200)
        {
            RackDto rdto = response.getEntity(RackDto.class);

            result.setData(Rack.create(rdto, rack.getDataCenter()));
            result.setSuccess(true);
        }
        else
        {
            populateErrors(response, result, "modifyRack");
        }

        return result;
    }

    @Override
    public BasicResult deleteRack(final Rack rack)
    {
        BasicResult result = new BasicResult();

        String uri = createRacksLink(rack.getDataCenter().getId(), rack.getId());

        ClientResponse response = delete(uri);

        if (response.getStatusCode() == 204)
        {
            result.setSuccess(true);
        }
        else
        {
            populateErrors(response, result, "deleteRack");
        }

        return result;
    }

    private RackDto fromRackToDto(final Rack rack)
    {
        RackDto dto = new RackDto();

        dto.setId(rack.getId());
        dto.setHaEnabled(rack.getHaEnabled());
        dto.setName(rack.getName());
        dto.setLongDescription(rack.getLargeDescription());
        dto.setShortDescription(rack.getShortDescription());

        if (rack.getVlanNetworkParameters() != null)
        {
            dto.setNrsq(rack.getVlanNetworkParameters().getNRSQ());
            dto.setVlanIdMax(rack.getVlanNetworkParameters().getVlan_id_max());
            dto.setVlanIdMin(rack.getVlanNetworkParameters().getVlan_id_min());
            dto.setVlanPerVdcExpected(rack.getVlanNetworkParameters().getVlan_per_vdc_expected());
            dto.setVlansIdAvoided(rack.getVlanNetworkParameters().getVlans_id_avoided());
        }

        return dto;
    }

    /**
     * @see com.abiquo.abiserver.commands.stub.RacksResourceStub#createUcsRack(com.abiquo.server.core.infrastructure.UcsRack)
     */
    @Override
    public DataResult<UcsRack> editUcsRack(final UcsRack ucsRack)
    {
        // PREMIUM

        return null;
    }

    /**
     * @see com.abiquo.abiserver.commands.stub.RacksResourceStub#getOrganizations(UcsRack,
     *      ListRequest)
     */
    @Override
    public DataResult<List<Organization>> getOrganizations(final UcsRack ucsRack,
        final ListRequest listRequest)
    {
        // PREMIUM
        return null;
    }

    /**
     * @see com.abiquo.abiserver.commands.stub.RacksResourceStub#getLogicServers(UcsRack,
     *      com.abiquo.abiserver.pojo.result.ListRequest)
     */
    @Override
    public DataResult<List<LogicServer>> getLogicServers(final UcsRack ucsRack,
        final ListRequest listRequest)
    {
        // PREMIUM
        return null;
    }

    /**
     * @see com.abiquo.abiserver.commands.stub.RacksResourceStub#getLogicServerTemplates(UcsRack,
     *      com.abiquo.abiserver.pojo.result.ListRequest)
     */
    @Override
    public DataResult<List<LogicServer>> getLogicServerTemplates(final UcsRack ucsRack,
        final ListRequest listRequest)
    {
        // PREMIUM
        return null;
    }

    /**
     * @see com.abiquo.abiserver.commands.stub.RacksResourceStub#cloneLogicServer(com.abiquo.abiserver.pojo.infrastructure.UcsRack,
     *      java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public BasicResult cloneLogicServer(final UcsRack ucsRack, final String lsName,
        final String org, final String newName)
    {
        // PREMIUM
        return null;
    }

    /**
     * @see com.abiquo.abiserver.commands.stub.RacksResourceStub#associateLogicServer(com.abiquo.abiserver.pojo.infrastructure.PhysicalMachine,
     *      String)
     */
    @Override
    public BasicResult associateLogicServer(final PhysicalMachine machine, final String lsName)
    {
        // PREMIUM
        return null;
    }

    /**
     * @see com.abiquo.abiserver.commands.stub.RacksResourceStub#dissociateLogicServer(PhysicalMachine,
     *      String)
     */
    @Override
    public BasicResult dissociateLogicServer(final Rack rack, final String machineDn,
        final String lsName)
    {
        // PREMIUM
        return null;
    }

    /**
     * @see com.abiquo.abiserver.commands.stub.RacksResourceStub#deleteLogicServer(com.abiquo.abiserver.pojo.infrastructure.UcsRack,
     *      java.lang.String)
     */
    @Override
    public BasicResult deleteLogicServer(final UcsRack ucsRack, final String lsName)
    {
        // PREMIUM
        return null;
    }

    /**
     * @see com.abiquo.abiserver.commands.stub.RacksResourceStub#assignLogicServerTemplate(com.abiquo.abiserver.pojo.infrastructure.UcsRack,
     *      java.lang.String)
     */
    @Override
    public BasicResult assignLogicServerTemplate(final PhysicalMachine machine,
        final String lsName, final String org, final String newName)
    {
        // PREMIUM
        return null;
    }

    /**
     * @see com.abiquo.abiserver.commands.stub.RacksResourceStub#assignLogicServerClone(com.abiquo.abiserver.pojo.infrastructure.PhysicalMachine,
     *      java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public BasicResult assignLogicServerClone(final PhysicalMachine machine, final String lsName,
        final String org, final String newName)
    {
        // PREMIUM
        return null;
    }

    /**
     * @see com.abiquo.abiserver.commands.stub.RacksResourceStub#objectUcsCurrentTask(UcsRack,
     *      String)
     */
    @Override
    public DataResult<Fsm> objectUcsCurrentTask(final UcsRack ucsRack, final String dn)
    {
        // PREMIUM
        return null;
    }

}
