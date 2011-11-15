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

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.binary.Hex;
import org.apache.wink.client.ClientResponse;
import org.apache.wink.client.ClientWebException;
import org.testng.Assert;
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
import com.abiquo.server.core.enterprise.Privilege;
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
        Role r = roleGenerator.createInstanceSysAdmin();
        User u = userGenerator.createInstance(e, r, "sysadmin", "sysadmin");

        List<Object> entitiesToSetup = new ArrayList<Object>();
        entitiesToSetup.add(e);
        for (Privilege p : r.getPrivileges())
        {
            entitiesToSetup.add(p);
        }
        entitiesToSetup.add(r);
        entitiesToSetup.add(u);

        setup(entitiesToSetup.toArray());
    }

    @Test
    public void getUser() throws ClientWebException
    {
        User user = userGenerator.createUniqueInstance();

        List<Object> entitiesToSetup = new ArrayList<Object>();

        for (Privilege p : user.getRole().getPrivileges())
        {
            entitiesToSetup.add(p);
        }
        entitiesToSetup.add(user.getRole());
        entitiesToSetup.add(user.getEnterprise());
        entitiesToSetup.add(user);

        setup(entitiesToSetup.toArray());

        ClientResponse response =
            get(resolveUserURI(user.getEnterprise().getId(), user.getId()), "sysadmin", "sysadmin");
        UserDto dto = response.getEntity(UserDto.class);

        assertEquals(response.getStatusCode(), 200);
        assertNotNull(dto);
    }

    @Test
    public void getUserDoesntExist() throws Exception
    {
        User user = userGenerator.createUniqueInstance();

        List<Object> entitiesToSetup = new ArrayList<Object>();

        for (Privilege p : user.getRole().getPrivileges())
        {
            entitiesToSetup.add(p);
        }
        entitiesToSetup.add(user.getRole());
        entitiesToSetup.add(user.getEnterprise());
        entitiesToSetup.add(user);

        setup(entitiesToSetup.toArray());

        ClientResponse response =
            get(resolveUserURI(user.getEnterprise().getId(), 123), "sysadmin", "sysadmin");
        assertEquals(response.getStatusCode(), 404);

        assertNonEmptyErrors(response.getEntity(ErrorsDto.class));
    }

    @Test
    public void getUserWithWrongEnterprise() throws ClientWebException
    {
        User user = userGenerator.createUniqueInstance();

        List<Object> entitiesToSetup = new ArrayList<Object>();

        for (Privilege p : user.getRole().getPrivileges())
        {
            entitiesToSetup.add(p);
        }
        entitiesToSetup.add(user.getRole());
        entitiesToSetup.add(user.getEnterprise());
        entitiesToSetup.add(user);

        setup(entitiesToSetup.toArray());

        ClientResponse response = get(resolveUserURI(1234, user.getId()), "sysadmin", "sysadmin");
        assertEquals(response.getStatusCode(), 404);

        assertNonEmptyErrors(response.getEntity(ErrorsDto.class));
    }

    @Test
    public void userContainCorrectLinks() throws ClientWebException
    {
        User user = userGenerator.createUniqueInstance();

        List<Object> entitiesToSetup = new ArrayList<Object>();

        for (Privilege p : user.getRole().getPrivileges())
        {
            entitiesToSetup.add(p);
        }
        entitiesToSetup.add(user.getRole());
        entitiesToSetup.add(user.getEnterprise());
        entitiesToSetup.add(user);

        setup(entitiesToSetup.toArray());

        String href = resolveUserURI(user.getEnterprise().getId(), user.getId());
        String enterpriseUri = resolveEnterpriseURI(user.getEnterprise().getId());
        ClientResponse response = get(href, "sysadmin", "sysadmin");

        UserDto dto = response.getEntity(UserDto.class);

        assertNotNull(dto.getLinks());

        assertLinkExist(dto, href, "edit");
        assertLinkExist(dto, enterpriseUri, "enterprise");
        assertLinkExist(dto, resolveUserActionGetVirtualMachinesURI(user.getEnterprise().getId(),
            user.getId()), VirtualMachinesResource.VIRTUAL_MACHINES_PATH);

    }

    @Test
    public void modifyUser() throws ClientWebException
    {
        User user = userGenerator.createUniqueInstance();

        List<Object> entitiesToSetup = new ArrayList<Object>();

        for (Privilege p : user.getRole().getPrivileges())
        {
            entitiesToSetup.add(p);
        }
        entitiesToSetup.add(user.getRole());
        entitiesToSetup.add(user.getEnterprise());
        entitiesToSetup.add(user);

        setup(entitiesToSetup.toArray());

        String uri = resolveUserURI(user.getEnterprise().getId(), user.getId());
        ClientResponse response = get(uri, "sysadmin", "sysadmin");

        UserDto dto = response.getEntity(UserDto.class);
        dto.setName("name");

        response = put(uri, dto, "sysadmin", "sysadmin");
        assertEquals(response.getStatusCode(), 200);

        UserDto modified = response.getEntity(UserDto.class);
        assertEquals(modified.getName(), "name");
    }

    @Test
    public void modifyUserCheckPasswordIsEncrypted() throws ClientWebException
    {
        User user = userGenerator.createUniqueInstance();

        List<Object> entitiesToSetup = new ArrayList<Object>();

        for (Privilege p : user.getRole().getPrivileges())
        {
            entitiesToSetup.add(p);
        }
        entitiesToSetup.add(user.getRole());
        entitiesToSetup.add(user.getEnterprise());
        entitiesToSetup.add(user);

        setup(entitiesToSetup.toArray());

        String uri = resolveUserURI(user.getEnterprise().getId(), user.getId());
        ClientResponse response = get(uri, "sysadmin", "sysadmin");

        UserDto dto = response.getEntity(UserDto.class);
        dto.setPassword("unencryptedPass");

        MessageDigest messageDigest = null;
        try
        {
            messageDigest = MessageDigest.getInstance("MD5");
        }
        catch (NoSuchAlgorithmException e)
        {

        }
        messageDigest.reset();
        messageDigest.update(new String("unencryptedPass").getBytes(Charset.forName("UTF8")));
        final byte[] resultByte = messageDigest.digest();
        String result = new String(Hex.encodeHex(resultByte));

        response = put(uri, dto, "sysadmin", "sysadmin");
        assertEquals(response.getStatusCode(), 200);

        UserDto modified = response.getEntity(UserDto.class);
        assertEquals(modified.getPassword(), result);

    }

    @Test
    public void modifyUserDoesntExist() throws ClientWebException
    {
        User user = userGenerator.createUniqueInstance();

        List<Object> entitiesToSetup = new ArrayList<Object>();

        for (Privilege p : user.getRole().getPrivileges())
        {
            entitiesToSetup.add(p);
        }
        entitiesToSetup.add(user.getRole());
        entitiesToSetup.add(user.getEnterprise());
        entitiesToSetup.add(user);

        setup(entitiesToSetup.toArray());

        String uri = resolveUserURI(user.getEnterprise().getId(), user.getId());
        ClientResponse response = get(uri, "sysadmin", "sysadmin");

        UserDto dto = response.getEntity(UserDto.class);
        dto.setName("name");

        uri = resolveUserURI(user.getEnterprise().getId(), 1234);

        response = put(uri, dto, "sysadmin", "sysadmin");

        assertEquals(response.getStatusCode(), 404);
    }

    @Test
    public void modifyUserEmailIsNotValid() throws ClientWebException
    {
        User user = userGenerator.createUniqueInstance();

        List<Object> entitiesToSetup = new ArrayList<Object>();

        for (Privilege p : user.getRole().getPrivileges())
        {
            entitiesToSetup.add(p);
        }
        entitiesToSetup.add(user.getRole());
        entitiesToSetup.add(user.getEnterprise());
        entitiesToSetup.add(user);

        setup(entitiesToSetup.toArray());

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

        List<Object> entitiesToSetup = new ArrayList<Object>();

        for (Privilege p : user.getRole().getPrivileges())
        {
            entitiesToSetup.add(p);
        }
        entitiesToSetup.add(user.getRole());
        entitiesToSetup.add(user.getEnterprise());
        entitiesToSetup.add(user);

        setup(entitiesToSetup.toArray());

        String uri = resolveUserURI(user.getEnterprise().getId(), user.getId());
        ClientResponse response = get(uri, "sysadmin", "sysadmin");

        UserDto dto = response.getEntity(UserDto.class);
        String old = dto.getName();

        dto.setName("name");

        uri = resolveUserURI(123, dto.getId());

        response = put(uri, dto, "sysadmin", "sysadmin");

        assertEquals(response.getStatusCode(), 404);

        assertNonEmptyErrors(response.getEntity(ErrorsDto.class));

        uri = resolveUserURI(user.getEnterprise().getId(), user.getId());

        dto = get(uri, "sysadmin", "sysadmin").getEntity(UserDto.class);

        assertEquals(dto.getName(), old);
    }

    @Test
    public void removeUser() throws ClientWebException
    {
        User user = userGenerator.createUniqueInstance();

        List<Object> entitiesToSetup = new ArrayList<Object>();

        for (Privilege p : user.getRole().getPrivileges())
        {
            entitiesToSetup.add(p);
        }
        entitiesToSetup.add(user.getRole());
        entitiesToSetup.add(user.getEnterprise());
        entitiesToSetup.add(user);

        setup(entitiesToSetup.toArray());

        ClientResponse response =
            delete(resolveUserURI(user.getEnterprise().getId(), user.getId()), "sysadmin",
                "sysadmin");
        assertEquals(response.getStatusCode(), 204);
    }

    @Test
    public void removeUserDoesntExist() throws ClientWebException
    {
        User user = userGenerator.createUniqueInstance();

        List<Object> entitiesToSetup = new ArrayList<Object>();

        for (Privilege p : user.getRole().getPrivileges())
        {
            entitiesToSetup.add(p);
        }
        entitiesToSetup.add(user.getRole());
        entitiesToSetup.add(user.getEnterprise());
        entitiesToSetup.add(user);

        setup(entitiesToSetup.toArray());

        ClientResponse response =
            delete(resolveUserURI(user.getEnterprise().getId(), 1234), "sysadmin", "sysadmin");
        assertEquals(response.getStatusCode(), 404);
    }

    @Test
    public void removeUserWrongEnterprise() throws ClientWebException
    {
        User user = userGenerator.createUniqueInstance();

        List<Object> entitiesToSetup = new ArrayList<Object>();

        for (Privilege p : user.getRole().getPrivileges())
        {
            entitiesToSetup.add(p);
        }
        entitiesToSetup.add(user.getRole());
        entitiesToSetup.add(user.getEnterprise());
        entitiesToSetup.add(user);

        setup(entitiesToSetup.toArray());

        ClientResponse response =
            delete(resolveUserURI(1234, user.getId()), "sysadmin", "sysadmin");
        assertEquals(response.getStatusCode(), 404);

        response =
            get(resolveUserURI(user.getEnterprise().getId(), user.getId()), "sysadmin", "sysadmin");
        UserDto dto = response.getEntity(UserDto.class);

        assertEquals(response.getStatusCode(), 200);
        assertNotNull(dto);
    }

    @Test
    public void getVirtualMachinesByUser()
    {
        VirtualMachine vm = vmGenerator.createUniqueInstance();

        List<Object> entitiesToSetup = new ArrayList<Object>();

        entitiesToSetup.add(vm.getEnterprise());
        for (Privilege p : vm.getUser().getRole().getPrivileges())
        {
            entitiesToSetup.add(p);
        }
        entitiesToSetup.add(vm.getUser().getRole());
        entitiesToSetup.add(vm.getUser());
        entitiesToSetup.add(vm.getHypervisor().getMachine().getDatacenter());
        entitiesToSetup.add(vm.getHypervisor().getMachine().getRack());
        entitiesToSetup.add(vm.getHypervisor().getMachine());
        entitiesToSetup.add(vm.getHypervisor());
        entitiesToSetup.add(vm.getVirtualImage().getEnterprise());
        entitiesToSetup.add(vm.getVirtualImage());
        entitiesToSetup.add(vm);

        setup(entitiesToSetup.toArray());

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
        Role r1 = roleGenerator.createInstanceSysAdmin();
        Role r2 = roleGenerator.createInstanceSysAdmin();

        User user = userGenerator.createInstance(r1);

        List<Object> entitiesToSetup = new ArrayList<Object>();

        for (Privilege p : r1.getPrivileges())
        {
            entitiesToSetup.add(p);
        }
        entitiesToSetup.add(r1);
        for (Privilege p : r2.getPrivileges())
        {
            entitiesToSetup.add(p);
        }
        entitiesToSetup.add(r2);
        entitiesToSetup.add(user.getEnterprise());
        entitiesToSetup.add(user);

        setup(entitiesToSetup.toArray());

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

        List<Object> entitiesToSetup = new ArrayList<Object>();

        entitiesToSetup.add(e1);
        entitiesToSetup.add(e2);
        for (Privilege p : r.getPrivileges())
        {
            entitiesToSetup.add(p);
        }
        entitiesToSetup.add(r);
        entitiesToSetup.add(u);

        setup(entitiesToSetup.toArray());

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
