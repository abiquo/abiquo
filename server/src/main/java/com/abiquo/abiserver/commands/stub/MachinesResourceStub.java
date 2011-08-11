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
import com.abiquo.abiserver.pojo.infrastructure.PhysicalMachine;
import com.abiquo.abiserver.pojo.infrastructure.PhysicalMachineCreation;
import com.abiquo.abiserver.pojo.infrastructure.UcsRack;
import com.abiquo.abiserver.pojo.infrastructure.VirtualMachine;
import com.abiquo.abiserver.pojo.result.BasicResult;
import com.abiquo.abiserver.pojo.result.DataResult;
import com.abiquo.model.enumerator.HypervisorType;
import com.abiquo.server.core.infrastructure.Machine.State;
import com.abiquo.server.core.infrastructure.MachineDto;

public interface MachinesResourceStub
{
    /**
     * Returns the list of machines from a managed rack (Must be a managed rack, otherwise it throws
     * an exception!)
     * 
     * @param ucsRack which the machines are attached.
     * @return BasicResult
     */
    public DataResult<List<PhysicalMachine>> getMachines(UcsRack ucsRack);

    /**
     * Refresh the list of machines from a managed rack (Must be a managed rack, otherwise it throws
     * an exception!)
     * 
     * @param ucsRack which the machines.
     * @return BasicResult
     */
    public DataResult<List<PhysicalMachine>> refreshMachines(UcsRack ucsRack);

    public DataResult<List<PhysicalMachine>> getPhysicalMachinesByRack(Integer datacenterId,
        Integer rackId, String filters);

    public DataResult<List<VirtualMachine>> getVirtualMachinesFromPM(Integer dcId, Integer rackId,
        Integer pmId);

    public DataResult<State> checkPhysicalMachineState(Integer datacenterId, Integer rackId,
        Integer machineId, String ip, HypervisorType hypervisor, String user, String password,
        Integer port);

    public BasicResult isStonithUp(Integer datacenterId, Integer rackId, Integer machineId,
        String ip, String user, String password, Integer port);

    public DataResult<MachineDto> createPhysicalMachine(
        final PhysicalMachineCreation createPhysicalMachine);

    public DataResult<List<PhysicalMachine>> createMultiplePhysicalMachine(
        final Integer datacenterId, final Integer rackId, final IPAddress ipFrom,
        final IPAddress ipTo, final Integer hypervisorType, final String user,
        final String password, final Integer port, final String vSwitch);

    public BasicResult deletePhysicalMachine(final PhysicalMachine machine);

    public DataResult<MachineDto> editPhysicalMachine(
        final PhysicalMachineCreation createPhysicalMachine);
}
