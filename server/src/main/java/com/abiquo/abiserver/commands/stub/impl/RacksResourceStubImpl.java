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

import java.util.List;

import com.abiquo.abiserver.commands.stub.AbstractAPIStub;
import com.abiquo.abiserver.commands.stub.RacksResourceStub;
import com.abiquo.abiserver.networking.IPAddress;
import com.abiquo.abiserver.pojo.infrastructure.DataCenter;
import com.abiquo.abiserver.pojo.infrastructure.PhysicalMachine;
import com.abiquo.abiserver.pojo.infrastructure.Rack;
import com.abiquo.abiserver.pojo.infrastructure.UcsRack;
import com.abiquo.abiserver.pojo.result.BasicResult;
import com.abiquo.abiserver.pojo.result.DataResult;
import com.abiquo.abiserver.pojo.ucs.LogicServer;
import com.abiquo.abiserver.pojo.ucs.Organization;
import com.abiquo.model.enumerator.HypervisorType;

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
        return null;
    }

    @Override
    public BasicResult associateBlades(final Integer datacenterId, final Integer rackId,
        final IPAddress ipFrom, final IPAddress ipTo, final HypervisorType hypervisorType,
        final String user, final String password, final Integer port, final String vSwitchName)
    {
        return null;
    }

    @Override
    public BasicResult powerOnMachine(final Integer datacenterId, final Integer rackId,
        final Integer machineId)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public BasicResult powerOffMachine(final Integer datacenterId, final Integer rackId,
        final Integer machineId)
    {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see com.abiquo.abiserver.commands.stub.RacksResourceStub#getUcsRacks(com.abiquo.abiserver.pojo.infrastructure.DataCenter)
     */
    @Override
    public DataResult<List<UcsRack>> getUcsRacks(final DataCenter datacenter)
    {
        // PREMIUM
        return null;
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
     * @see com.abiquo.abiserver.commands.stub.RacksResourceStub#getOrganizations(com.abiquo.abiserver.pojo.infrastructure.UcsRack)
     */
    @Override
    public DataResult<List<Organization>> getOrganizations(final UcsRack ucsRack)
    {
        // PREMIUM
        return null;
    }

    /**
     * @see com.abiquo.abiserver.commands.stub.RacksResourceStub#getLogicServers(com.abiquo.abiserver.pojo.infrastructure.UcsRack)
     */
    @Override
    public DataResult<List<LogicServer>> getLogicServers(final UcsRack ucsRack)
    {
        // PREMIUM
        return null;
    }

    /**
     * @see com.abiquo.abiserver.commands.stub.RacksResourceStub#getLogicServerTemplates(com.abiquo.abiserver.pojo.infrastructure.UcsRack)
     */
    @Override
    public DataResult<List<LogicServer>> getLogicServerTemplates(final UcsRack ucsRack)
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
    public BasicResult dissociateLogicServer(final PhysicalMachine machine, final String lsName)
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

}
