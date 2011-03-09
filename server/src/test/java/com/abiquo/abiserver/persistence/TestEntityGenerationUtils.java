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

package com.abiquo.abiserver.persistence;

import rpc.core.UUID;

import com.abiquo.abiserver.business.hibernate.pojohb.infrastructure.DatacenterHB;
import com.abiquo.abiserver.business.hibernate.pojohb.infrastructure.HypervisorHB;
import com.abiquo.abiserver.business.hibernate.pojohb.infrastructure.PhysicalmachineHB;
import com.abiquo.abiserver.business.hibernate.pojohb.infrastructure.RackHB;
import com.abiquo.abiserver.business.hibernate.pojohb.infrastructure.StateEnum;
import com.abiquo.abiserver.business.hibernate.pojohb.user.EnterpriseHB;
import com.abiquo.abiserver.business.hibernate.pojohb.virtualappliance.VirtualmachineHB;
import com.abiquo.abiserver.business.hibernate.pojohb.virtualhardware.LimitHB;
import com.abiquo.server.core.enumerator.HypervisorType;

public class TestEntityGenerationUtils
{
    public static EnterpriseHB createEnterprise(String name)
    {
        EnterpriseHB enterprise = new EnterpriseHB();
        enterprise.setName(name);
        enterprise.getLimits().setCpu(new LimitHB(10, 5));
        enterprise.getLimits().setHd(new LimitHB(1000, 500));
        enterprise.getLimits().setRam(new LimitHB(2000, 1500));

        return enterprise;
    }

    public static DatacenterHB createDatacenter(String name)
    {
        DatacenterHB datacenter = new DatacenterHB();
        datacenter.setName(name);
        return datacenter;
    }

    public static RackHB createRack(DatacenterHB datacenter, String name)
    {
        RackHB result = new RackHB();
        result.setDatacenter(datacenter);
        result.setName(name);
        return result;
    }

    public static PhysicalmachineHB createMachine(RackHB rack, String name)
    {
        PhysicalmachineHB result = new PhysicalmachineHB();
        result.setRack(rack);
        result.setName(name);
        result.setDataCenter(rack.getDatacenter());
        result.setVswitchName(name);
        return result;
    }

    public static HypervisorHB createHypervisor(String description, PhysicalmachineHB machine,
        HypervisorType type)
    {
        HypervisorHB hypervisor = new HypervisorHB();
        hypervisor.setPhysicalMachine(machine);
        hypervisor.setPort(8080);
        hypervisor.setIp("127.0.0.0");
        hypervisor.setType(type);
        hypervisor.setUser("user");
        hypervisor.setPassword("pwd");
        hypervisor.setIpService("ipService");

        return hypervisor;
    }

    public static VirtualmachineHB createVirtualmachine(String name)
    {
        VirtualmachineHB result = new VirtualmachineHB();
        result.setUuid(new UUID().toString());
        result.setName(name);
        result.setState(StateEnum.RUNNING);
        result.setIdType(1);
        return result;
    }
}
