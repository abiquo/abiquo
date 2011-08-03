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

import javax.persistence.EntityManager;

import org.springframework.security.context.SecurityContextHolder;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.abiquo.api.common.AbstractGeneratorTest;
import com.abiquo.api.common.Assert;
import com.abiquo.api.common.BasicUserAuthentication;
import com.abiquo.api.resources.cloud.VirtualDatacenterResource;
import com.abiquo.api.services.DatacenterService;
import com.abiquo.api.services.UserService;
import com.abiquo.model.enumerator.HypervisorType;
import com.abiquo.server.core.cloud.Hypervisor;
import com.abiquo.server.core.cloud.VirtualDatacenter;
import com.abiquo.server.core.cloud.VirtualDatacenterDto;
import com.abiquo.server.core.enterprise.Enterprise;
import com.abiquo.server.core.enterprise.Role;
import com.abiquo.server.core.enterprise.User;
import com.abiquo.server.core.infrastructure.Datacenter;
import com.abiquo.server.core.infrastructure.Machine;
import com.abiquo.server.core.infrastructure.network.VLANNetworkDto;

public class VirtualDatacenterServiceTest extends AbstractGeneratorTest
{
    @BeforeMethod
    public void setupBasicUser()
    {
        Enterprise e = enterpriseGenerator.createUniqueInstance();
        Role r = roleGenerator.createInstance();
        User u = userGenerator.createInstance(e, r, "basicUser", "basicUser");
        setup(e, r, u);

        SecurityContextHolder.getContext().setAuthentication(new BasicUserAuthentication());
    }

    @Test
    public void findVirtualDatacenterAssignedToUser()
    {
        Enterprise enterprise = enterpriseGenerator.createUniqueInstance();
        Datacenter datacenter = datacenterGenerator.createUniqueInstance();

        Role role = roleGenerator.createInstance();
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

        Role role = roleGenerator.createInstance();
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

    @Test
    public void createVirtualDatacenterByUserWithVdcsAssigned()
    {
        Enterprise enterprise = enterpriseGenerator.createUniqueInstance();
        Datacenter d = datacenterGenerator.createUniqueInstance();
        Machine machine = machineGenerator.createMachine(d);
        Hypervisor hypervisor = hypervisorGenerator.createInstance(machine, HypervisorType.KVM);
        VirtualDatacenter vdc = vdcGenerator.createInstance(d, enterprise, HypervisorType.KVM);

        VirtualDatacenter vdc1 = vdcGenerator.createInstance(d, enterprise, HypervisorType.KVM);

        setup(enterprise, d, machine, hypervisor, vdc);

        Role role = roleGenerator.createInstance();
        User user = userGenerator.createInstance(enterprise, role);
        user.setAvailableVirtualDatacenters(vdc.getId().toString());

        setup(role, user);

        SecurityContextHolder.getContext().setAuthentication(
            new BasicUserAuthentication(user.getNick()));

        EntityManager em = getEntityManagerWithAnActiveTransaction();

        DatacenterService datacenterService = new DatacenterService(em);
        VirtualDatacenterService service = new VirtualDatacenterService(em);

        Datacenter datacenter = datacenterService.getDatacenter(d.getId());

        VirtualDatacenterDto dto = VirtualDatacenterResource.createTransferObject(vdc1);
        VLANNetworkDto networkDto = new VLANNetworkDto();
        networkDto.setName("DefaultNetwork");
        networkDto.setDefaultNetwork(Boolean.TRUE);
        networkDto.setAddress("192.168.0.0");
        networkDto.setGateway("192.168.0.1");
        networkDto.setMask(24);
        networkDto.setPrimaryDNS("10.0.0.1");
        networkDto.setSecondaryDNS("10.0.0.1");

        dto.setVlan(networkDto);

        VirtualDatacenter virtualDatacenter =
            service.createVirtualDatacenter(dto, datacenter, enterprise);

        UserService userService = new UserService(em);

        User currentUser = userService.getCurrentUser();

        commitActiveTransaction(em);

        Assert.assertTrue(currentUser.getAvailableVirtualDatacenters().endsWith(
            "," + virtualDatacenter.getId()));

    }
}
