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
import com.abiquo.abiserver.pojo.infrastructure.Rack;
import com.abiquo.abiserver.pojo.infrastructure.UcsRack;
import com.abiquo.abiserver.pojo.result.BasicResult;
import com.abiquo.abiserver.pojo.result.DataResult;
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
    
    public BasicResult associateBlades(final Integer datacenterId, final Integer rackId, IPAddress ipFrom, IPAddress ipTo,
        final HypervisorType hypervisorType, final String user, final String password,
        final Integer port, final String vSwitchName);
    
    public BasicResult powerOnMachine(final Integer datacenterId, final Integer rackId, final Integer machineId);
    
    public BasicResult powerOffMachine(final Integer datacenterId, final Integer rackId, final Integer machineId);

    /**
     * Returns all {@link UcsRack} in {@link DataCenter}.
     * 
     * @param datacenter datacenter.
     * @return wrapper which contains the list of {@link UcsRack}. Or in case of error the
     *         appropiate object.
     */
    public DataResult<List<UcsRack>> getUcsRacks(DataCenter datacenter);

    public DataResult<UcsRack> editUcsRack(UcsRack ucsRack);
}
