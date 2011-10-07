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

package com.abiquo.abiserver.commands.stub;

import java.util.List;

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

public interface RacksResourceStub
{
    public DataResult<UcsRack> createUcsRack(UcsRack ucsRack);

    /**
     * Returns all (runtime type){@link Rack} in {@link DataCenter}.
     * 
     * @param datacenter datacenter.
     * @return wrapper which contains the list of {@link Rack}. Or in case of error the appropiate
     *         object.
     */
    public DataResult<List<Rack>> getAllNotManagedRacks(DataCenter datacenter);

    public DataResult<List<Rack>> getAllNotManagedRacks(DataCenter datacenter, String filter);

    public BasicResult associateBlades(final Integer datacenterId, final Integer rackId,
        IPAddress ipFrom, IPAddress ipTo, final HypervisorType hypervisorType, final String user,
        final String password, final Integer port, final String vSwitchName);

    public BasicResult powerOnMachine(final Integer datacenterId, final Integer rackId,
        final Integer machineId);

    public BasicResult powerOffMachine(final Integer datacenterId, final Integer rackId,
        final Integer machineId);

    /**
     * Returns all {@link UcsRack} in {@link DataCenter}.
     * 
     * @param datacenter datacenter.
     * @return wrapper which contains the list of {@link UcsRack}. Or in case of error the
     *         appropiate object.
     */
    public DataResult<List<UcsRack>> getUcsRacks(DataCenter datacenter);

    public DataResult<List<UcsRack>> getUcsRacks(DataCenter datacenter, String filter);

    public DataResult<List<Rack>> getRacksByDatacenter(DataCenter datacenter);

    public DataResult<List<Rack>> getRacksByDatacenter(DataCenter datacenter, String filter);

    public DataResult<Rack> createRack(Rack rack);

    public DataResult<Rack> modifyRack(final Rack rack);

    public BasicResult deleteRack(Rack rack);

    public DataResult<UcsRack> editUcsRack(UcsRack ucsRack);

    /**
     * Returns all {@link Organization} in {@link UcsRack}.
     * 
     * @param ucsRack ucsRack.
     * @return wrapper which contains the list of {@link Organization} in the {@link UcsRack}. Or in
     *         case of error the appropiate object.
     */
    public DataResult<List<Organization>> getOrganizations(UcsRack ucsRack,
        final ListRequest listRequest);

    /**
     * Returns all {@link LogicServer} in {@link UcsRack}.
     * 
     * @param ucsRack ucsRack.
     * @return wrapper which contains the list of {@link LogicServer} in the {@link UcsRack}. Or in
     *         case of error the appropiate object.
     */
    public DataResult<List<LogicServer>> getLogicServers(UcsRack ucsRack,
        final ListRequest listRequest);

    /**
     * Returns all {@link LogicServer} templates in {@link UcsRack}.
     * 
     * @param ucsRack ucsRack.
     * @return wrapper which contains the list of {@link LogicServer} templates in the
     *         {@link UcsRack}. Or in case of error the appropiate object.
     */
    public DataResult<List<LogicServer>> getLogicServerTemplates(UcsRack ucsRack,
        final ListRequest listRequest);

    /**
     * Clone the LogicServer from the UCS rack.
     * 
     * @param ucsRack rack.
     * @param lsName logic server to clone (dn).
     * @param org organization dn to associate.
     * @param newName name of the new ls.
     * @return BasicResult.
     */
    public BasicResult cloneLogicServer(final UcsRack ucsRack, String lsName, String org,
        String newName);

    /**
     * Associate the LogicServer from the UCS rack.
     * 
     * @param machine rack's blade.
     * @param lsName dn.
     * @return BasicResult.
     */
    public BasicResult associateLogicServer(final PhysicalMachine machine, final String lsName);

    /**
     * LogicServer from the UCS rack.
     * 
     * @param ucsRack rack.
     * @param bladeDn dn blade.
     * @return BasicResult.
     */
    public BasicResult dissociateLogicServer(final Rack rack, final String machineDn,
        final String lsName);

    /**
     * Delete LogicServer from the UCS rack.
     * 
     * @param ucsRack rack.
     * @param lsName dn ls.
     * @return BasicResult.
     */
    public BasicResult deleteLogicServer(final UcsRack ucsRack, String lsName);

    /**
     * Assign LogicServer template from the UCS rack.
     * 
     * @param ucsRack rack.
     * @param lsName dn ls.
     * @param newName name of the new ls.
     * @param bladeDn dn blade. * @return BasicResult.
     * @param org organization dn to associate.
     */
    public BasicResult assignLogicServerTemplate(final PhysicalMachine machine, String lsName,
        final String org, final String newName);

    /**
     * Assign LogicServer from a clone from the UCS rack.
     * 
     * @param ucsRack rack.
     * @param lsName dn ls.
     * @param newName name of the new ls.
     * @param bladeDn dn blade. * @return BasicResult.
     * @param org organization dn to associate.
     */
    public BasicResult assignLogicServerClone(final PhysicalMachine machine, String lsName,
        final String org, final String newName);

    /**
     * Retrieves the current task and progress of an object from the UCS rack.
     * 
     * @param dn of the object.
     */
    public DataResult<Fsm> objectUcsCurrentTask(UcsRack ucsRack, String dn);
}
