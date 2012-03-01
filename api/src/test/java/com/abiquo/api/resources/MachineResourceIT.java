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
import static com.abiquo.api.common.UriTestResolver.resolveDatastoresURI;
import static com.abiquo.api.common.UriTestResolver.resolveEnterpriseURI;
import static com.abiquo.api.common.UriTestResolver.resolveMachineActionGetVirtualMachinesURI;
import static com.abiquo.api.common.UriTestResolver.resolveMachineURI;
import static com.abiquo.api.common.UriTestResolver.resolveRackURI;
import static com.abiquo.api.common.UriTestResolver.resolveUserURI;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import junit.framework.Assert;

import org.apache.wink.client.ClientResponse;
import org.apache.wink.client.ClientWebException;
import org.apache.wink.client.Resource;
import org.apache.wink.client.RestClient;
import org.springframework.security.context.SecurityContextHolder;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.abiquo.api.common.SysadminAuthentication;
import com.abiquo.api.services.stub.NodecollectorServiceStubMock;
import com.abiquo.model.enumerator.RemoteServiceType;
import com.abiquo.model.transport.error.ErrorsDto;
import com.abiquo.server.core.cloud.Hypervisor;
import com.abiquo.server.core.cloud.NodeVirtualImage;
import com.abiquo.server.core.cloud.VirtualAppliance;
import com.abiquo.server.core.cloud.VirtualDatacenter;
import com.abiquo.server.core.cloud.VirtualMachine;
import com.abiquo.server.core.cloud.VirtualMachineDto;
import com.abiquo.server.core.cloud.VirtualMachinesDto;
import com.abiquo.server.core.enterprise.Enterprise;
import com.abiquo.server.core.enterprise.Privilege;
import com.abiquo.server.core.enterprise.Role;
import com.abiquo.server.core.enterprise.User;
import com.abiquo.server.core.infrastructure.Machine;
import com.abiquo.server.core.infrastructure.MachineDto;
import com.abiquo.server.core.infrastructure.RemoteService;

public class MachineResourceIT extends AbstractJpaGeneratorIT
{
    private String validMachineUri;

    private Machine validMachine;

    private Hypervisor validHypervisor;

    private Enterprise e;

    private User u;

    @Override
    @BeforeMethod
    public void setup()
    {
        Hypervisor hypervisor = hypervisorGenerator.createUniqueInstance();
        hypervisor.setIpService(NodecollectorServiceStubMock.IP_DISCOVER_FIRST);

        Machine machine = hypervisor.getMachine();

        RemoteService rs =
            machine.getDatacenter().createRemoteService(RemoteServiceType.VIRTUAL_SYSTEM_MONITOR,
                "http://localhost:8080/fooo", 1);
        setup(machine.getDatacenter(), machine.getRack(), machine, hypervisor, rs);

        validMachine = machine;
        validHypervisor = hypervisor;
        validMachineUri =
            resolveMachineURI(machine.getDatacenter().getId(), machine.getRack().getId(),
                machine.getId());

        e = enterpriseGenerator.createUniqueInstance();
        Role r = roleGenerator.createInstanceSysAdmin();
        u = userGenerator.createInstance(e, r, "sysadmin", "sysadmin");

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
    public void getMachine() throws ClientWebException
    {
        MachineDto machine = getValidMachine();
        assertNotNull(machine);

        // Verify that the credentials are not returned
        assertNull(machine.getUser());
        assertNull(machine.getPassword());
    }

    @Test
    public void testGetMachineWithCredentials()
    {
        ClientResponse response = get(validMachineUri + "?credentials=true", MachineDto.MEDIA_TYPE);
        assertEquals(response.getStatusCode(), Status.OK.getStatusCode());
        MachineDto machine = response.getEntity(MachineDto.class);

        // Verify that the credentials are not returned
        assertNotNull(machine);
        assertNotNull(machine.getUser());
        assertNotNull(machine.getPassword());
    }

    @Test
    public void getMachineDoesntExist() throws Exception
    {
        String machineUri = resolveMachineURI(1, 1, 123);

        ClientResponse response = get(machineUri, MachineDto.MEDIA_TYPE);
        assertEquals(response.getStatusCode(), Status.NOT_FOUND.getStatusCode());

        assertNonEmptyErrors(response.getEntity(ErrorsDto.class));
    }

    @Test
    public void getMachineWithWrongDatacenter() throws ClientWebException
    {
        String machineUri = resolveMachineURI(1234, 1, 1);

        ClientResponse response = get(machineUri, MachineDto.MEDIA_TYPE);
        assertEquals(404, response.getStatusCode());

        assertNonEmptyErrors(response.getEntity(ErrorsDto.class));
    }

    @Test
    public void getMachineWithWrongRack() throws ClientWebException
    {
        String machineUri = resolveMachineURI(1, 1234, 1);

        ClientResponse response = get(machineUri, MachineDto.MEDIA_TYPE);
        assertEquals(404, response.getStatusCode());

        assertNonEmptyErrors(response.getEntity(ErrorsDto.class));
    }

    @Test
    public void machineContainsLink() throws ClientWebException
    {
        assertLinkExist(getValidMachine(), validMachineUri, "edit");
        assertLinkExist(getValidMachine(),
            resolveRackURI(validMachine.getDatacenter().getId(), validMachine.getRack().getId()),
            "rack");
        assertLinkExist(
            getValidMachine(),
            resolveDatastoresURI(validMachine.getDatacenter().getId(), validMachine.getRack()
                .getId(), validMachine.getId()), DatastoresResource.DATASTORES_PATH);
    }

    @Test
    public void modifyMachine() throws ClientWebException
    {

        MachineDto machine = get(validMachineUri, MachineDto.MEDIA_TYPE).getEntity(MachineDto.class);
        machine.setName("dummy_name");

        ClientResponse response = put(validMachineUri, machine);
        assertEquals(200, response.getStatusCode());

        MachineDto modified = response.getEntity(MachineDto.class);
        assertEquals("dummy_name", modified.getName());
    }

    @Test
    public void modifyMachineDoesntExist() throws ClientWebException
    {
        MachineDto machine = getValidMachine();
        machine.setName("dummy_name");

        ClientResponse response = put(resolveMachineURI(1, 1, 123), machine);

        assertEquals(404, response.getStatusCode());
    }

    @Test
    public void modifyMachineWrongDatacenter() throws ClientWebException
    {
        MachineDto machine = getValidMachine();
        String old = machine.getName();

        machine.setName("dummy_name");

        String machineUri = resolveMachineURI(123, validMachine.getRack().getId(),
                validMachine.getId());

        ClientResponse response = put(machineUri, machine);

        assertEquals(404, response.getStatusCode());

        assertNonEmptyErrors(response.getEntity(ErrorsDto.class));

        machine = get(validMachineUri, MachineDto.MEDIA_TYPE).getEntity(MachineDto.class);

        assertEquals(old, machine.getName());
    }

    @Test
    public void modifyMachineWrongRack() throws ClientWebException
    {
        MachineDto machine = getValidMachine();
        String old = machine.getName();

        machine.setName("dummy_name");

        String machineUri = resolveMachineURI(validMachine.getDatacenter().getId(), 1234,
                validMachine.getId());

        ClientResponse response = put(machineUri, machine);

        assertEquals(404, response.getStatusCode());

        assertNonEmptyErrors(response.getEntity(ErrorsDto.class));

        machine = get(validMachineUri, MachineDto.MEDIA_TYPE).getEntity(MachineDto.class);

        assertEquals(old, machine.getName());
    }

    @Test
    public void removeMachine() throws ClientWebException
    {
        Resource resource = client.resource(validMachineUri);

        ClientResponse response = resource.accept(MediaType.APPLICATION_XML).delete();
        assertEquals(response.getStatusCode(), 204);
    }

    @Test
    public void removeMachineDoesntExist() throws ClientWebException
    {
        Resource resource =
            client.resource(resolveMachineURI(validMachine.getDatacenter().getId(), validMachine
                .getRack().getId(), 1234));

        ClientResponse response = resource.accept(MediaType.APPLICATION_XML).delete();
        assertEquals(404, response.getStatusCode());
    }

    @Test
    public void removeMachineWrongDatacenter() throws ClientWebException
    {
        ClientResponse response = delete(resolveMachineURI(1234, validMachine.getRack().getId(),
                validMachine.getId()));
        assertEquals(response.getStatusCode(), Status.NOT_FOUND.getStatusCode());

        response = get(validMachineUri, MachineDto.MEDIA_TYPE);
        MachineDto machine = response.getEntity(MachineDto.class);

        assertEquals(200, response.getStatusCode());
        assertNotNull(machine);
    }

    @Test
    public void removeMachineWrongRack() throws ClientWebException
    {

        ClientResponse response = delete(resolveMachineURI(validMachine.getDatacenter().getId(), 1234,
            validMachine.getId()));
        assertEquals(404, response.getStatusCode());

        response = get(validMachineUri, MachineDto.MEDIA_TYPE);
        MachineDto machine = response.getEntity(MachineDto.class);

        assertEquals(200, response.getStatusCode());
        assertNotNull(machine);
    }

    @Test
    public void getMachineActionVirtualMachines()
    {

        VirtualMachine vm = vmGenerator.createInstance(validHypervisor, e, u);
        VirtualDatacenter vdc =
            vdcGenerator.createInstance(vm.getHypervisor().getMachine().getDatacenter(),
                vm.getEnterprise());
        VirtualAppliance vapp = vappGenerator.createInstance(vdc);
        NodeVirtualImage nvi = nodeVirtualImageGenerator.createInstance(vapp, vm);
        vm.getVirtualMachineTemplate().getRepository()
            .setDatacenter(vm.getHypervisor().getMachine().getDatacenter());

        List<Object> entitiesToSetup = new ArrayList<Object>();

        entitiesToSetup.add(vm.getVirtualMachineTemplate().getRepository());
        entitiesToSetup.add(vm.getVirtualMachineTemplate().getCategory());
        entitiesToSetup.add(vm.getVirtualMachineTemplate());
        entitiesToSetup.add(vm);
        entitiesToSetup.add(vdc);
        entitiesToSetup.add(vapp);
        entitiesToSetup.add(nvi);

        setup(entitiesToSetup.toArray());
        SecurityContextHolder.getContext().setAuthentication(new SysadminAuthentication());

        Machine m = vm.getHypervisor().getMachine();

        String uri =
            resolveMachineActionGetVirtualMachinesURI(m.getDatacenter().getId(), m.getRack()
                .getId(), m.getId());

        // Resource resource = client.resource(uri);
        // ClientResponse response = resource.accept(MediaType.APPLICATION_XML).get();

        ClientResponse response = get(uri, "sysadmin", "sysadmin", VirtualMachinesDto.MEDIA_TYPE);

        Assert.assertEquals(Status.OK.getStatusCode(), response.getStatusCode());
        VirtualMachinesDto vms = response.getEntity(VirtualMachinesDto.class);
        Assert.assertEquals(vms.getCollection().size(), 1);

        VirtualMachineDto vmDto = vms.getCollection().get(0);
        assertLinkExist(vmDto, resolveEnterpriseURI(vm.getEnterprise().getId()), "enterprise");
        assertLinkExist(vmDto, resolveUserURI(vm.getEnterprise().getId(), vm.getUser().getId()),
            "user");
        assertLinkExist(vmDto,
            resolveMachineURI(m.getDatacenter().getId(), m.getRack().getId(), m.getId()), "machine");
    }

    @Test
    public void notManagedActionVirtualMachines()
    {
        VirtualMachine vm = vmGenerator.createInstance(validHypervisor, e, u);
        vm.setIdType(VirtualMachine.NOT_MANAGED);
        VirtualMachine vm2 = vmGenerator.createInstance(validHypervisor, e, u);
        vm.setIdType(VirtualMachine.MANAGED);
        vm.getVirtualMachineTemplate().getRepository()
            .setDatacenter(vm.getHypervisor().getMachine().getDatacenter());
        vm2.getVirtualMachineTemplate().getRepository()
            .setDatacenter(vm2.getHypervisor().getMachine().getDatacenter());

        VirtualDatacenter vdc =
            vdcGenerator.createInstance(vm.getHypervisor().getMachine().getDatacenter(),
                vm.getEnterprise());
        VirtualAppliance vapp = vappGenerator.createInstance(vdc);
        NodeVirtualImage nvi = nodeVirtualImageGenerator.createInstance(vapp, vm);

        VirtualDatacenter vdc2 =
            vdcGenerator.createInstance(vm2.getHypervisor().getMachine().getDatacenter(),
                vm2.getEnterprise());
        VirtualAppliance vapp2 = vappGenerator.createInstance(vdc2);
        NodeVirtualImage nvi2 = nodeVirtualImageGenerator.createInstance(vapp2, vm2);

        List<Object> entitiesToSetup = new ArrayList<Object>();

        entitiesToSetup.add(vm.getVirtualMachineTemplate().getRepository());
        entitiesToSetup.add(vm.getVirtualMachineTemplate().getCategory());
        entitiesToSetup.add(vm.getVirtualMachineTemplate());
        entitiesToSetup.add(vm);
        entitiesToSetup.add(vm2.getVirtualMachineTemplate().getRepository());
        entitiesToSetup.add(vm2.getVirtualMachineTemplate().getCategory());
        entitiesToSetup.add(vm2.getVirtualMachineTemplate());
        entitiesToSetup.add(vm2);
        entitiesToSetup.add(vdc);
        entitiesToSetup.add(vapp);
        entitiesToSetup.add(nvi);
        entitiesToSetup.add(vdc2);
        entitiesToSetup.add(vapp2);
        entitiesToSetup.add(nvi2);

        setup(entitiesToSetup.toArray());

        Machine m = vm.getHypervisor().getMachine();

        String uri =
            resolveMachineActionGetVirtualMachinesURI(m.getDatacenter().getId(), m.getRack()
                .getId(), m.getId());

        ClientResponse response = get(uri, "sysadmin", "sysadmin", VirtualMachinesDto.MEDIA_TYPE);
        VirtualMachinesDto vms = response.getEntity(VirtualMachinesDto.class);
        Assert.assertEquals(vms.getCollection().size(), 2);

        response = delete(uri);
        Assert.assertEquals(response.getStatusCode(), Status.NO_CONTENT.getStatusCode());

        response = get(uri, "sysadmin", "sysadmin", VirtualMachinesDto.MEDIA_TYPE);
        vms = response.getEntity(VirtualMachinesDto.class);
        Assert.assertEquals(vms.getCollection().size(), 1);
    }

    private MachineDto getValidMachine()
    {
        ClientResponse response = get(validMachineUri, MachineDto.MEDIA_TYPE);
        assertEquals(200, response.getStatusCode());
        return response.getEntity(MachineDto.class);
    }
}
