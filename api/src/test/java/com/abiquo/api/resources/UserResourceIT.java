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

package com.abiquo.api.resources;

import static com.abiquo.api.common.Assert.assertLinkExist;
import static com.abiquo.api.common.Assert.assertNonEmptyErrors;
import static com.abiquo.api.common.UriTestResolver.resolveEnterpriseURI;
import static com.abiquo.api.common.UriTestResolver.resolveMachineURI;
import static com.abiquo.api.common.UriTestResolver.resolveRoleURI;
import static com.abiquo.api.common.UriTestResolver.resolveUserActionGetVirtualMachinesURI;
import static com.abiquo.api.common.UriTestResolver.resolveUserURI;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import org.apache.wink.client.ClientResponse;
import org.apache.wink.client.ClientWebException;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.abiquo.api.common.UriTestResolver;
import com.abiquo.api.resources.cloud.VirtualMachinesResource;
import com.abiquo.model.rest.RESTLink;
import com.abiquo.model.transport.error.ErrorsDto;
import com.abiquo.server.core.cloud.VirtualMachine;
import com.abiquo.server.core.cloud.VirtualMachineDto;
import com.abiquo.server.core.cloud.VirtualMachinesDto;
import com.abiquo.server.core.enterprise.Enterprise;
import com.abiquo.server.core.enterprise.Role;
import com.abiquo.server.core.enterprise.User;
import com.abiquo.server.core.enterprise.UserDto;
import com.abiquo.server.core.infrastructure.Machine;

public class UserResourceIT extends AbstractJpaGeneratorIT
{
    @BeforeMethod
    public void setupSysadmin()
    {
        Enterprise e = enterpriseGenerator.createUniqueInstance();
        Role r = roleGenerator.createInstance(Role.Type.SYS_ADMIN);

        User u = userGenerator.createInstance(e, r, "sysadmin", "sysadmin");
        setup(e, r, u);
    }

    @AfterMethod
    public void tearDown()
    {
        tearDown("virtualmachine", "hypervisor", "physicalmachine", "rack", "datacenter",
            "virtualimage", "user", "enterprise", "role");
    }

    @Test
    public void getUser() throws ClientWebException
    {
        User user = userGenerator.createUniqueInstance();
        setup(user.getRole(), user.getEnterprise(), user);

        ClientResponse response =
            get(resolveUserURI(user.getEnterprise().getId(), user.getId()), "sysadmin", "sysadmin");
        UserDto dto = response.getEntity(UserDto.class);

        assertEquals(200, response.getStatusCode());
        assertNotNull(dto);
    }

    @Test
    public void getUserDoesntExist() throws Exception
    {
        User user = userGenerator.createUniqueInstance();
        setup(user.getRole(), user.getEnterprise(), user);

        ClientResponse response =
            get(resolveUserURI(user.getEnterprise().getId(), 123), "sysadmin", "sysadmin");
        assertEquals(404, response.getStatusCode());

        assertNonEmptyErrors(response.getEntity(ErrorsDto.class));
    }

    @Test
    public void getUserWithWrongEnterprise() throws ClientWebException
    {
        User user = userGenerator.createUniqueInstance();
        setup(user.getRole(), user.getEnterprise(), user);

        ClientResponse response = get(resolveUserURI(1234, user.getId()), "sysadmin", "sysadmin");
        assertEquals(404, response.getStatusCode());

        assertNonEmptyErrors(response.getEntity(ErrorsDto.class));
    }

    @Test
    public void userContainCorrectLinks() throws ClientWebException
    {
        User user = userGenerator.createUniqueInstance();
        setup(user.getRole(), user.getEnterprise(), user);

        String href = resolveUserURI(user.getEnterprise().getId(), user.getId());
        String enterpriseUri = resolveEnterpriseURI(user.getEnterprise().getId());
        ClientResponse response = get(href, "sysadmin", "sysadmin");

        UserDto dto = response.getEntity(UserDto.class);

        assertNotNull(dto.getLinks());

        assertLinkExist(dto, href, "edit");
        assertLinkExist(dto, enterpriseUri, "enterprise");
        assertLinkExist(dto, resolveUserActionGetVirtualMachinesURI(user.getEnterprise().getId(),
            user.getId()), "action", VirtualMachinesResource.VIRTUAL_MACHINES_PATH);

    }

    @Test
    public void modifyUser() throws ClientWebException
    {
        User user = userGenerator.createUniqueInstance();
        setup(user.getRole(), user.getEnterprise(), user);

        String uri = resolveUserURI(user.getEnterprise().getId(), user.getId());
        ClientResponse response = get(uri, "sysadmin", "sysadmin");

        UserDto dto = response.getEntity(UserDto.class);
        dto.setName("name");

        response = put(uri, dto, "sysadmin", "sysadmin");
        assertEquals(response.getStatusCode(), 200);

        UserDto modified = response.getEntity(UserDto.class);
        assertEquals("name", modified.getName());
    }

    @Test
    public void modifyUserDoesntExist() throws ClientWebException
    {
        User user = userGenerator.createUniqueInstance();
        setup(user.getRole(), user.getEnterprise(), user);

        String uri = resolveUserURI(user.getEnterprise().getId(), user.getId());
        ClientResponse response = get(uri, "sysadmin", "sysadmin");

        UserDto dto = response.getEntity(UserDto.class);
        dto.setName("name");

        uri = resolveUserURI(user.getEnterprise().getId(), 1234);

        response = put(uri, dto, "sysadmin", "sysadmin");

        assertEquals(404, response.getStatusCode());
    }
    
    @Test
    public void modifyUserEmailIsNotValid() throws ClientWebException
    {
    	User user = userGenerator.createUniqueInstance();
        setup(user.getRole(), user.getEnterprise(), user);

        String uri = resolveUserURI(user.getEnterprise().getId(), user.getId());
        ClientResponse response = get(uri, "sysadmin", "sysadmin");

        UserDto dto = response.getEntity(UserDto.class);
        dto.setName("name");
        dto.setEmail("bademailsyntaxis");

        response = put(uri, dto, "sysadmin", "sysadmin");
        assertEquals(response.getStatusCode(), 400);
    }

    @Test
    public void modifyUserWrongEnterprise() throws ClientWebException
    {
        User user = userGenerator.createUniqueInstance();
        setup(user.getRole(), user.getEnterprise(), user);

        String uri = resolveUserURI(user.getEnterprise().getId(), user.getId());
        ClientResponse response = get(uri, "sysadmin", "sysadmin");

        UserDto dto = response.getEntity(UserDto.class);
        String old = dto.getName();

        dto.setName("name");

        uri = resolveUserURI(123, dto.getId());

        response = put(uri, dto, "sysadmin", "sysadmin");

        assertEquals(404, response.getStatusCode());

        assertNonEmptyErrors(response.getEntity(ErrorsDto.class));

        uri = resolveUserURI(user.getEnterprise().getId(), user.getId());

        dto = get(uri, "sysadmin", "sysadmin").getEntity(UserDto.class);

        assertEquals(old, dto.getName());
    }

    @Test
    public void removeUser() throws ClientWebException
    {
        User user = userGenerator.createUniqueInstance();
        setup(user.getRole(), user.getEnterprise(), user);

        ClientResponse response =
            delete(resolveUserURI(user.getEnterprise().getId(), user.getId()), "sysadmin",
                "sysadmin");
        assertEquals(204, response.getStatusCode());
    }

    @Test
    public void removeUserDoesntExist() throws ClientWebException
    {
        User user = userGenerator.createUniqueInstance();
        setup(user.getRole(), user.getEnterprise(), user);

        ClientResponse response =
            delete(resolveUserURI(user.getEnterprise().getId(), 1234), "sysadmin", "sysadmin");
        assertEquals(404, response.getStatusCode());
    }

    @Test
    public void removeUserWrongEnterprise() throws ClientWebException
    {
        User user = userGenerator.createUniqueInstance();
        setup(user.getRole(), user.getEnterprise(), user);

        ClientResponse response =
            delete(resolveUserURI(1234, user.getId()), "sysadmin", "sysadmin");
        assertEquals(404, response.getStatusCode());

        response =
            get(resolveUserURI(user.getEnterprise().getId(), user.getId()), "sysadmin", "sysadmin");
        UserDto dto = response.getEntity(UserDto.class);

        assertEquals(200, response.getStatusCode());
        assertNotNull(dto);
    }

    @Test
    public void getVirtualMachinesByUser()
    {
        VirtualMachine vm = vmGenerator.createUniqueInstance();
        setup(vm.getEnterprise(), vm.getUser().getRole(), vm.getUser(), vm.getHypervisor()
            .getMachine().getDatacenter(), vm.getHypervisor().getMachine().getRack(), vm
            .getHypervisor().getMachine(), vm.getHypervisor(),
            vm.getVirtualImage().getEnterprise(), vm.getVirtualImage(), vm);

        String uri =
            resolveUserActionGetVirtualMachinesURI(vm.getEnterprise().getId(), vm.getUser().getId());

        Machine m = vm.getHypervisor().getMachine();
        Enterprise e = vm.getEnterprise();
        User u = vm.getUser();

        ClientResponse response = get(uri, "sysadmin", "sysadmin");
        Assert.assertEquals(response.getStatusCode(), 200);

        VirtualMachinesDto vms = response.getEntity(VirtualMachinesDto.class);
        Assert.assertEquals(vms.getCollection().size(), 1);

        VirtualMachineDto vmDto = vms.getCollection().get(0);
        assertLinkExist(vmDto, resolveEnterpriseURI(e.getId()), "enterprise");
        assertLinkExist(vmDto, resolveUserURI(e.getId(), u.getId()), "user");
        assertLinkExist(vmDto, resolveMachineURI(m.getDatacenter().getId(), m.getRack().getId(), m
            .getId()), "machine");
    }

    @Test
    public void shouldModifyRoleWhenUpdateUser()
    {
        Role r1 = roleGenerator.createInstance(Role.Type.ENTERPRISE_ADMIN);
        Role r2 = roleGenerator.createInstance(Role.Type.USER);

        User user = userGenerator.createInstance(r1);
        setup(r1, r2, user.getEnterprise(), user);

        String userURI = resolveUserURI(user.getEnterprise().getId(), user.getId());
        String roleURI = resolveRoleURI(r2.getId());

        ClientResponse response = get(userURI, "sysadmin", "sysadmin");
        UserDto dto = response.getEntity(UserDto.class);

        dto.modifyLink("role", roleURI);

        response = put(userURI, dto, "sysadmin", "sysadmin");
        assertEquals(response.getStatusCode(), 200);

        dto = response.getEntity(UserDto.class);
        assertLinkExist(dto, roleURI, RoleResource.ROLE);
    }

    @Test
    public void shouldAllowToModifyUserWithEnterpriseWildcard()
    {
        Enterprise e1 = enterpriseGenerator.createUniqueInstance();
        Enterprise e2 = enterpriseGenerator.createUniqueInstance();
        Role r = roleGenerator.createUniqueInstance();
        User u = userGenerator.createInstance(e1, r);

        setup(e1, e2, r, u);

        UserDto dto = UserResource.createTransferObject(u);

        String entURI = UriTestResolver.resolveEnterpriseURI(e2.getId());
        dto.addLink(new RESTLink(EnterpriseResource.ENTERPRISE, entURI));

        String userURI = resolveUserURI("_", u.getId());

        ClientResponse response = put(userURI, dto, "sysadmin", "sysadmin");
        assertEquals(response.getStatusCode(), 200);

        dto = response.getEntity(UserDto.class);
        assertLinkExist(dto, entURI, EnterpriseResource.ENTERPRISE);
    }
}
