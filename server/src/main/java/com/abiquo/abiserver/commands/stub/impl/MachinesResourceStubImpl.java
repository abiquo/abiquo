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
import com.abiquo.abiserver.commands.stub.MachinesResourceStub;
import com.abiquo.abiserver.pojo.infrastructure.PhysicalMachine;
import com.abiquo.abiserver.pojo.infrastructure.UcsRack;
import com.abiquo.abiserver.pojo.result.DataResult;

public class MachinesResourceStubImpl extends AbstractAPIStub implements MachinesResourceStub
{
    /**
     * @see com.abiquo.abiserver.commands.stub.MachinesResourceStub#getMachines(com.abiquo.server.core.infrastructure.UcsRack)
     */
    @Override
    public DataResult<List<PhysicalMachine>> getMachines(UcsRack ucsRack)
    {
        // PREMIUM
        return null;
    }

    /**
     * @see com.abiquo.abiserver.commands.stub.MachinesResourceStub#refreshMachines(com.abiquo.abiserver.pojo.infrastructure.UcsRack)
     */
    @Override
    public DataResult<List<PhysicalMachine>> refreshMachines(UcsRack ucsRack)
    {
        // PREMIUM
        return null;
    }

}