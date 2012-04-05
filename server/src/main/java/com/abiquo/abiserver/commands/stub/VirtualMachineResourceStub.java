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

import com.abiquo.abiserver.pojo.infrastructure.VirtualMachine;
import com.abiquo.abiserver.pojo.result.BasicResult;
import com.abiquo.abiserver.pojo.result.DataResult;
import com.abiquo.server.core.cloud.VirtualMachineState;

public interface VirtualMachineResourceStub

{
    public BasicResult updateVirtualMachine(Integer virtualDatacenterId,
        Integer virtualApplianceId, VirtualMachine virtualMachine, final boolean force);

    public BasicResult deleteVirtualMachine(Integer virtualDatacenterId,
        Integer virtualApplianceId, VirtualMachine virtualMachine);

    DataResult editVirtualMachineState(final Integer virtualDatacenterId,
        final Integer virtualApplianceId, VirtualMachine virtualMachine,
        VirtualMachineState virtualMachineState);

    public DataResult rebootVirtualMachine(final Integer virtualDatacenterId,
        final Integer virtualApplianceId, final VirtualMachine virtualMachine);

    public BasicResult rebootInfrastructureVirtualMachine(final Integer datacenterId,
        final Integer rackId, final Integer machineId, VirtualMachine virtualMachine);

    public BasicResult editInfrastructureVirtualMachineState(final Integer datacenterId,
        final Integer rackId, final Integer machineId, VirtualMachine virtualMachine,
        VirtualMachineState off);
}
