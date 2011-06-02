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

}
