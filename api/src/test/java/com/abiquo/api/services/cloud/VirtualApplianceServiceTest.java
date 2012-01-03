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

/**
 * 
 */
package com.abiquo.api.services.cloud;

import static com.abiquo.testng.TestConfig.BASIC_UNIT_TESTS;
import static com.abiquo.testng.TestConfig.VAPP_UNIT_TESTS;

import javax.persistence.EntityManager;

import org.springframework.security.context.SecurityContextHolder;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.abiquo.api.common.AbstractUnitTest;
import com.abiquo.api.common.BasicUserAuthentication;
import com.abiquo.model.enumerator.RemoteServiceType;
import com.abiquo.server.core.cloud.NodeVirtualImage;
import com.abiquo.server.core.cloud.VirtualAppliance;
import com.abiquo.server.core.cloud.VirtualDatacenter;
import com.abiquo.server.core.cloud.VirtualMachine;
import com.abiquo.server.core.cloud.VirtualMachineState;
import com.abiquo.server.core.enterprise.DatacenterLimits;
import com.abiquo.server.core.enterprise.Enterprise;
import com.abiquo.server.core.enterprise.Role;
import com.abiquo.server.core.enterprise.User;
import com.abiquo.server.core.infrastructure.RemoteService;

/**
 * @author jdevesa
 */
@Test(groups = {VAPP_UNIT_TESTS})
public class VirtualApplianceServiceTest extends AbstractUnitTest
{
    protected VirtualDatacenter vdc;

    protected VirtualAppliance vapp;

    protected RemoteService rs;

    @BeforeMethod(groups = {BASIC_UNIT_TESTS, VAPP_UNIT_TESTS})
    public void setupBasicUser()
    {
        Enterprise e = enterpriseGenerator.createUniqueInstance();
        Role r = roleGenerator.createInstance();
        User u = userGenerator.createInstance(e, r, "basicUser", "basicUser");
        setup(e, r, u);

        rs = remoteServiceGenerator.createInstance(RemoteServiceType.DHCP_SERVICE);
        vdc = vdcGenerator.createInstance(rs.getDatacenter(), e);

        vapp = vappGenerator.createInstance(vdc);
        DatacenterLimits dclimit = new DatacenterLimits(vdc.getEnterprise(), vdc.getDatacenter());
        setup(vdc.getDatacenter(), rs, vdc.getNetwork(), vdc, dclimit, vapp);

        SecurityContextHolder.getContext().setAuthentication(new BasicUserAuthentication());
    }

    @Override
    @AfterMethod(groups = {BASIC_UNIT_TESTS, VAPP_UNIT_TESTS})
    public void tearDown()
    {
        super.tearDown();
    }

    @Test(enabled = false, groups = {BASIC_UNIT_TESTS, VAPP_UNIT_TESTS})
    public void deleteVirtualAppliance()
    {
        EntityManager em = getEntityManagerWithAnActiveTransaction();
        VirtualApplianceService service = new VirtualApplianceService(em);
        service.deleteVirtualAppliance(vdc.getId(), vapp.getId(), Boolean.TRUE);
    }

    @Test(enabled = false, groups = {BASIC_UNIT_TESTS, VAPP_UNIT_TESTS}, expectedExceptions = {Exception.class})
    public void deleteVirtualApplianceInvalidState()
    {
        EntityManager em = getEntityManagerWithAnActiveTransaction();
        VirtualApplianceService service = new VirtualApplianceService(em);

        VirtualMachine vm = vmGenerator.createInstance(vdc.getEnterprise());
        vm.setState(VirtualMachineState.OFF);

        NodeVirtualImage nodeVirtualImage = nodeVirtualImageGenerator.createInstance(vapp, vm);

        vapp.addToNodeVirtualImages(nodeVirtualImage);
        setup(vm, nodeVirtualImage, vapp);

        service.deleteVirtualAppliance(vdc.getId(), vapp.getId(), Boolean.TRUE);
    }
}
