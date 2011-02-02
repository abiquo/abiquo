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

import java.util.Collection;

import org.testng.annotations.Test;

import com.abiquo.api.common.AbstractGeneratorTest;
import com.abiquo.api.common.Assert;
import com.abiquo.server.core.cloud.VirtualDatacenter;
import com.abiquo.server.core.enterprise.Enterprise;
import com.abiquo.server.core.enterprise.Role;
import com.abiquo.server.core.enterprise.User;
import com.abiquo.server.core.infrastructure.Datacenter;

public class VirtualDatacenterServiceTest extends AbstractGeneratorTest
{
    @Test
    public void findVirtualDatacenterAssignedToUser()
    {
        Enterprise enterprise = enterpriseGenerator.createUniqueInstance();
        Datacenter datacenter = datacenterGenerator.createUniqueInstance();

        Role role = roleGenerator.createInstance(Role.Type.USER);
        User user = userGenerator.createInstance(enterprise, role);

        VirtualDatacenter vdc1 = vdcGenerator.createInstance(datacenter, enterprise);
        VirtualDatacenter vdc2 = vdcGenerator.createInstance(datacenter, enterprise);
        VirtualDatacenter vdc3 = vdcGenerator.createInstance(datacenter, enterprise);

        setup(enterprise, datacenter, vdc1, vdc2, vdc3);
        String ids = vdc1.getId() + "," + vdc2.getId();

        user.setAvailableVirtualDatacenters(ids);
        setup(user.getRole(), user);

        VirtualDatacenterService service =
            new VirtualDatacenterService(getEntityManagerWithAnActiveTransaction());

        Collection<VirtualDatacenter> vdcs = service.getVirtualDatacenters(enterprise, null, null);
        Assert.assertSize(vdcs, 3);

        vdcs = service.getVirtualDatacenters(enterprise, null, user);
        Assert.assertSize(vdcs, 2);
    }

    @Test
    public void findVirtualDatacenterAssignedToEnterpriseAdmin()
    {
        Enterprise enterprise = enterpriseGenerator.createUniqueInstance();
        Enterprise enterprise2 = enterpriseGenerator.createUniqueInstance();
        Datacenter datacenter = datacenterGenerator.createUniqueInstance();

        Role role = roleGenerator.createInstance(Role.Type.ENTERPRISE_ADMIN);
        User user = userGenerator.createInstance(enterprise, role);

        VirtualDatacenter vdc1 = vdcGenerator.createInstance(datacenter, enterprise);
        VirtualDatacenter vdc2 = vdcGenerator.createInstance(datacenter, enterprise);
        VirtualDatacenter vdc3 = vdcGenerator.createInstance(datacenter, enterprise2);

        setup(enterprise, enterprise2, datacenter, vdc1, vdc2, vdc3, user.getRole(), user);

        VirtualDatacenterService service =
            new VirtualDatacenterService(getEntityManagerWithAnActiveTransaction());

        Collection<VirtualDatacenter> vdcs = service.getVirtualDatacenters(null, null, null);
        Assert.assertSize(vdcs, 3);

        vdcs = service.getVirtualDatacenters(null, null, user);
        Assert.assertSize(vdcs, 2);
    }
}
