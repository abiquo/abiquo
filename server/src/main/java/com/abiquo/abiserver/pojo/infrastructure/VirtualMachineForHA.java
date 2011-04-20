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

package com.abiquo.abiserver.pojo.infrastructure;

import com.abiquo.abiserver.business.hibernate.pojohb.infrastructure.VirtualMachineForHAHB;
import com.abiquo.abiserver.pojo.IPojo;

public class VirtualMachineForHA implements IPojo<VirtualMachineForHAHB>
{
    private Integer id;

    private VirtualMachine virtualMachine;

    private PhysicalMachine machine;

    public Integer getId()
    {
        return id;
    }

    public void setId(Integer id)
    {
        this.id = id;
    }

    public VirtualMachine getVirtualMachine()
    {
        return virtualMachine;
    }

    public void setVirtualMachine(VirtualMachine virtualMachine)
    {
        this.virtualMachine = virtualMachine;
    }

    public PhysicalMachine getMachine()
    {
        return machine;
    }

    public void setMachine(PhysicalMachine machine)
    {
        this.machine = machine;
    }

    public VirtualMachineForHAHB toPojoHB()
    {
        VirtualMachineForHAHB vmForHAHB = new VirtualMachineForHAHB();

        vmForHAHB.setId(id);
        vmForHAHB.setMachine(machine.toPojoHB());
        vmForHAHB.setVirtualMachine(virtualMachine.toPojoHB());

        return vmForHAHB;
    }
}
