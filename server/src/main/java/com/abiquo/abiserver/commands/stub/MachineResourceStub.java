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

import com.abiquo.abiserver.pojo.infrastructure.HypervisorRemoteAccessInfo;
import com.abiquo.abiserver.pojo.infrastructure.PhysicalMachine;
import com.abiquo.abiserver.pojo.result.BasicResult;
import com.abiquo.abiserver.pojo.result.DataResult;
import com.abiquo.abiserver.pojo.ucs.BladeLocatorLed;
import com.abiquo.abiserver.pojo.ucs.LogicServer;

public interface MachineResourceStub
{
    public DataResult<HypervisorRemoteAccessInfo> getHypervisorRemoteAccess(PhysicalMachine machine);

    public BasicResult deleteNotManagedVirtualMachines(PhysicalMachine machine);

    /**
     * Changes the operPower state to down. Actually turns the blade associated to the provided
     * logic server off.
     * 
     * @param machine machine to shutdown.
     * @return BasicResult
     */
    public BasicResult powerOff(PhysicalMachine machine);

    /**
     * Changes the operPower state to up. Actually turns the blade associated to the provided logic
     * server on.
     * 
     * @param machine to power on.
     * @return BasicResult
     */
    public BasicResult powerOn(PhysicalMachine machine);

    public BasicResult deletePhysicalMachine(PhysicalMachine machine);

    /**
     * Returns the list of virtual machines by the machine identifier.
     * 
     * @param datacenterId identifier of the datacenter.
     * @param rackId identifier of the rack.
     * @param machineId identifier of the machine.
     * @return a {@link BasicResult} containing a list of Virtual Machines.
     */
    public BasicResult getVirtualMachinesFromMachine(final Integer datacenterId,
        final Integer rackId, final Integer machineId);

    /**
     * Light the LED.
     * 
     * @param PhysicalMachine machine.
     */
    public BasicResult bladeLocatorLED(final PhysicalMachine machine);

    /**
     * Returns teh {@link LogicServer} in blade.
     * 
     * @param ucsRack ucsRack.
     * @return wrapper which contains the {@link LogicServer} which is the blade. Or in case of
     *         error the appropiate object.
     */
    public DataResult<LogicServer> getBladeLogicServer(final PhysicalMachine machine);

    /**
     * Light the LED. off.
     * 
     * @param PhysicalMachine machine.
     */
    public BasicResult bladeLocatorLEDoff(final PhysicalMachine machine);

    /**
     * Retrieve the LED. off.
     * 
     * @param PhysicalMachine machine.
     */
    public DataResult<BladeLocatorLed> getBladeLocatorLed(PhysicalMachine machine);

    /**
     * Refreshes the datastores of the physical machine.
     * 
     * @param datacenterId identifier of the datacenter
     * @param rackId identifier of the rack
     * @param machineId identifier of the machine
     * @return BasicResult without data.
     */
    public BasicResult refreshDatastores(Integer datacenterId, Integer rackId, Integer machineId);
}
