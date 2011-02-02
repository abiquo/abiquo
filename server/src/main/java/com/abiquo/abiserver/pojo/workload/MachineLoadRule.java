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

package com.abiquo.abiserver.pojo.workload;

import com.abiquo.abiserver.business.hibernate.pojohb.workload.MachineLoadRuleHB;
import com.abiquo.abiserver.pojo.IPojo;
import com.abiquo.abiserver.pojo.infrastructure.DataCenter;
import com.abiquo.abiserver.pojo.infrastructure.PhysicalMachine;
import com.abiquo.abiserver.pojo.infrastructure.Rack;

public class MachineLoadRule implements IPojo<MachineLoadRuleHB>, PersistentRule
{
    private int cpuLoadPercentage;

    private int ramLoadPercentage;

    private DataCenter datacenter;

    private Rack rack;

    private PhysicalMachine machine;

    public int getCpuLoadPercentage()
    {
        return cpuLoadPercentage;
    }

    public void setCpuLoadPercentage(final int cpuLoadPercentage)
    {
        this.cpuLoadPercentage = cpuLoadPercentage;
    }

    public int getRamLoadPercentage()
    {
        return ramLoadPercentage;
    }

    public void setRamLoadPercentage(final int ramLoadPercentage)
    {
        this.ramLoadPercentage = ramLoadPercentage;
    }

    public DataCenter getDatacenter()
    {
        return datacenter;
    }

    public void setDatacenter(final DataCenter datacenter)
    {
        this.datacenter = datacenter;
    }

    public Rack getRack()
    {
        return rack;
    }

    public void setRack(final Rack rack)
    {
        this.rack = rack;
    }

    public PhysicalMachine getMachine()
    {
        return machine;
    }

    public void setMachine(final PhysicalMachine machine)
    {
        this.machine = machine;
    }

    @Override
    public MachineLoadRuleHB toPojoHB()
    {
        MachineLoadRuleHB ruleHB = new MachineLoadRuleHB();
        ruleHB.setRamLoadPercentage(ramLoadPercentage);
        ruleHB.setCpuLoadPercentage(cpuLoadPercentage);
        ruleHB.setDatacenter(datacenter.toPojoHB());

        if (rack != null)
        {
            ruleHB.setRack(rack.toPojoHB());
        }

        if (machine != null)
        {
            ruleHB.setMachine(machine.toPojoHB());
        }

        return ruleHB;
    }

}
