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

package com.abiquo.api.services.cloud;

import javax.persistence.EntityManager;

import org.springframework.security.context.SecurityContextHolder;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.abiquo.api.common.AbstractUnitTest;
import com.abiquo.api.common.SysadminAuthentication;
import com.abiquo.api.spring.security.SecurityService;
import com.abiquo.server.core.cloud.NodeVirtualImage;
import com.abiquo.server.core.cloud.VirtualMachineState;
import com.abiquo.server.core.cloud.VirtualAppliance;
import com.abiquo.server.core.cloud.VirtualDatacenter;
import com.abiquo.server.core.cloud.VirtualImage;
import com.abiquo.server.core.cloud.VirtualMachine;
import com.abiquo.server.core.enterprise.Enterprise;
import com.abiquo.server.core.enterprise.Privilege;
import com.abiquo.server.core.enterprise.Role;
import com.abiquo.server.core.enterprise.User;
import com.abiquo.server.core.infrastructure.Datacenter;

public class VirtualMachineServiceTest extends AbstractUnitTest
{
    @BeforeMethod
    public void setupSysadmin()
    {
        Enterprise e = enterpriseGenerator.createUniqueInstance();
        Privilege p1 = new Privilege(SecurityService.USERS_MANAGE_OTHER_ENTERPRISES);
        Privilege p2 = new Privilege(SecurityService.USERS_MANAGE_USERS);
        Role role = roleGenerator.createInstance(p1, p2);
        User user = userGenerator.createInstance(e, role, "sysadmin", "sysadmin");

        setup(e, p1, p2, role, user);

        SecurityContextHolder.getContext().setAuthentication(new SysadminAuthentication());

    }

    @Test(enabled = false)
    public void changeState()
    {
        Datacenter datacenter = datacenterGenerator.createUniqueInstance();
        Enterprise enterprise = enterpriseGenerator.createUniqueInstance();
        VirtualMachine virtualMachine = vmGenerator.createInstance(enterprise);
        virtualMachine.setState(VirtualMachineState.ON);

        Role role = roleGenerator.createInstance();
        User user = userGenerator.createInstance(enterprise, role);

        VirtualDatacenter vdc1 = vdcGenerator.createInstance(datacenter, enterprise);
        VirtualAppliance vapp = virtualApplianceGenerator.createInstance(vdc1);

        VirtualImage image = virtualImageGenerator.createInstance(vdc1.getEnterprise());

        virtualMachine.getHypervisor().getMachine().setDatacenter(datacenter);
        virtualMachine.getHypervisor().getMachine().getRack().setDatacenter(datacenter);
        virtualMachine.setUser(user);
        NodeVirtualImage node = new NodeVirtualImage("node_test", vapp, image, virtualMachine);

        setup(enterprise, user.getRole(), user, datacenter, vdc1, virtualMachine.getHypervisor()
            .getMachine().getRack(), virtualMachine.getHypervisor().getMachine(),
            virtualMachine.getHypervisor(), virtualMachine.getVirtualImage(), virtualMachine, vapp,
            image, node);

        EntityManager em = getEntityManagerWithAnActiveTransaction();
        VirtualMachineService service = new VirtualMachineService(em);

        service.changeVirtualMachineState(virtualMachine.getId(), vapp.getId(), vdc1.getId(),
            VirtualMachineState.OFF);

        VirtualMachine vm = service.getVirtualMachine(virtualMachine.getId());
        Assert.assertEquals(vm.getState(), VirtualMachineState.OFF);
    }

}
