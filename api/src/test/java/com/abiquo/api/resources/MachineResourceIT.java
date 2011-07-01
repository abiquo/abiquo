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

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.MediaType;

import org.apache.wink.client.ClientResponse;
import org.apache.wink.client.ClientWebException;
import org.apache.wink.client.Resource;
import org.apache.wink.client.RestClient;
import org.springframework.security.context.SecurityContextHolder;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.abiquo.api.common.SysadminAuthentication;
import com.abiquo.model.enumerator.RemoteServiceType;
import com.abiquo.model.transport.error.ErrorsDto;
import com.abiquo.server.core.cloud.Hypervisor;
import com.abiquo.server.core.cloud.NodeVirtualImage;
import com.abiquo.server.core.cloud.VirtualAppliance;
import com.abiquo.server.core.cloud.VirtualDatacenter;
import com.abiquo.server.core.cloud.VirtualMachine;
import com.abiquo.server.core.cloud.VirtualMachineDto;
import com.abiquo.server.core.cloud.VirtualMachinesDto;
import com.abiquo.server.core.enterprise.Privilege;
import com.abiquo.server.core.infrastructure.Machine;
import com.abiquo.server.core.infrastructure.MachineDto;
import com.abiquo.server.core.infrastructure.RemoteService;

public class MachineResourceIT extends AbstractJpaGeneratorIT
{
    private String validMachineUri;

    private Machine validMachine;

    @Override
    @BeforeMethod
    public void setup()
    {
        Hypervisor hypervisor = hypervisorGenerator.createUniqueInstance();
        Machine machine = hypervisor.getMachine();

        RemoteService rs =
            machine.getDatacenter().createRemoteService(RemoteServiceType.VIRTUAL_SYSTEM_MONITOR,
                "http://localhost:8080/fooo", 1);
        setup(machine.getDatacenter(), machine.getRack(), machine, hypervisor, rs);

        validMachine = machine;
        validMachineUri =
            resolveMachineURI(machine.getDatacenter().getId(), machine.getRack().getId(),
                machine.getId());
    }

    @Test
    public void getMachine() throws ClientWebException
    {
        MachineDto machine = getValidMachine();
        assertNotNull(machine);
    }

    @Test
    public void getMachineDoesntExist() throws Exception
    {
        Resource resource = client.resource(resolveMachineURI(1, 1, 123));

        ClientResponse response = resource.accept(MediaType.APPLICATION_XML).get();
        assertEquals(404, response.getStatusCode());

        assertNonEmptyErrors(response.getEntity(ErrorsDto.class));
    }

    @Test
    public void getMachineWithWrongDatacenter() throws ClientWebException
    {
        Resource resource = client.resource(resolveMachineURI(1234, 1, 1));

        ClientResponse response = resource.accept(MediaType.APPLICATION_XML).get();
        assertEquals(404, response.getStatusCode());

        assertNonEmptyErrors(response.getEntity(ErrorsDto.class));
    }

    @Test
    public void getMachineWithWrongRack() throws ClientWebException
    {
        Resource resource = client.resource(resolveMachineURI(1, 1234, 1));

        ClientResponse response = resource.accept(MediaType.APPLICATION_XML).get();
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
        Resource resource = client.resource(validMachineUri);

        MachineDto machine = resource.accept(MediaType.APPLICATION_XML).get(MachineDto.class);
        machine.setName("dummy_name");

        ClientResponse response =
            resource.accept(MediaType.APPLICATION_XML).contentType(MediaType.APPLICATION_XML)
                .put(machine);
        assertEquals(200, response.getStatusCode());

        MachineDto modified = response.getEntity(MachineDto.class);
        assertEquals("dummy_name", modified.getName());
    }

    @Test
    public void modifyMachineDoesntExist() throws ClientWebException
    {
        MachineDto machine = getValidMachine();
        machine.setName("dummy_name");

        Resource resource = client.resource(resolveMachineURI(1, 1, 123));

        ClientResponse response =
            resource.accept(MediaType.APPLICATION_XML).contentType(MediaType.APPLICATION_XML)
                .put(machine);

        assertEquals(404, response.getStatusCode());
    }

    @Test
    public void modifyMachineWrongDatacenter() throws ClientWebException
    {
        MachineDto machine = getValidMachine();
        String old = machine.getName();

        machine.setName("dummy_name");

        Resource resource =
            client.resource(resolveMachineURI(123, validMachine.getRack().getId(),
                validMachine.getId()));

        ClientResponse response =
            resource.accept(MediaType.APPLICATION_XML).contentType(MediaType.APPLICATION_XML)
                .put(machine);

        assertEquals(404, response.getStatusCode());

        assertNonEmptyErrors(response.getEntity(ErrorsDto.class));

        resource = client.resource(validMachineUri);
        machine = resource.accept(MediaType.APPLICATION_XML).get(MachineDto.class);

        assertEquals(old, machine.getName());
    }

    @Test
    public void modifyMachineWrongRack() throws ClientWebException
    {
        MachineDto machine = getValidMachine();
        String old = machine.getName();

        machine.setName("dummy_name");

        Resource resource =
            client.resource(resolveMachineURI(validMachine.getDatacenter().getId(), 1234,
                validMachine.getId()));

        ClientResponse response =
            resource.accept(MediaType.APPLICATION_XML).contentType(MediaType.APPLICATION_XML)
                .put(machine);

        assertEquals(404, response.getStatusCode());

        assertNonEmptyErrors(response.getEntity(ErrorsDto.class));

        resource = client.resource(validMachineUri);
        machine = resource.accept(MediaType.APPLICATION_XML).get(MachineDto.class);

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
        Resource resource =
            client.resource(resolveMachineURI(1234, validMachine.getRack().getId(),
                validMachine.getId()));

        ClientResponse response = resource.accept(MediaType.APPLICATION_XML).delete();
        assertEquals(404, response.getStatusCode());

        resource = client.resource(validMachineUri);

        response = resource.accept(MediaType.APPLICATION_XML).get();
        MachineDto machine = response.getEntity(MachineDto.class);

        assertEquals(200, response.getStatusCode());
        assertNotNull(machine);
    }

    @Test
    public void removeMachineWrongRack() throws ClientWebException
    {
        Resource resource =
            client.resource(resolveMachineURI(validMachine.getDatacenter().getId(), 1234,
                validMachine.getId()));

        ClientResponse response = resource.accept(MediaType.APPLICATION_XML).delete();
        assertEquals(404, response.getStatusCode());

        resource = client.resource(validMachineUri);

        response = resource.accept(MediaType.APPLICATION_XML).get();
        MachineDto machine = response.getEntity(MachineDto.class);

        assertEquals(200, response.getStatusCode());
        assertNotNull(machine);
    }

    @Test
    public void getMachineActionVirtualMachines()
    {

        VirtualMachine vm = vmGenerator.createUniqueInstance();
        VirtualDatacenter vdc =
            vdcGenerator.createInstance(vm.getHypervisor().getMachine().getDatacenter(),
                vm.getEnterprise());
        VirtualAppliance vapp = vappGenerator.createInstance(vdc);
        NodeVirtualImage nvi = nodeVirtualImageGenerator.createInstance(vapp, vm);

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

        ClientResponse response = get(uri, "sysadmin", "sysadmin", MediaType.APPLICATION_XML);

        Assert.assertEquals(response.getStatusCode(), 200);
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
        VirtualMachine vm = vmGenerator.createUniqueInstance();
        vm.setIdType(VirtualMachine.NOT_MANAGED);
        VirtualMachine vm2 = vmGenerator.createInstance(vm.getHypervisor());
        vm.setIdType(VirtualMachine.MANAGED);

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
        entitiesToSetup.add(vm2.getEnterprise());
        for (Privilege p : vm2.getUser().getRole().getPrivileges())
        {
            entitiesToSetup.add(p);
        }
        entitiesToSetup.add(vm2.getUser().getRole());
        entitiesToSetup.add(vm2.getUser());
        entitiesToSetup.add(vm2.getVirtualImage());
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

        ClientResponse response = get(uri, "sysadmin", "sysadmin");
        VirtualMachinesDto vms = response.getEntity(VirtualMachinesDto.class);
        Assert.assertEquals(vms.getCollection().size(), 2);

        response = delete(uri);
        Assert.assertEquals(response.getStatusCode(), 204);

        response = get(uri, "sysadmin", "sysadmin");
        vms = response.getEntity(VirtualMachinesDto.class);
        Assert.assertEquals(vms.getCollection().size(), 1);
    }

    private MachineDto getValidMachine()
    {
        RestClient client = new RestClient();
        Resource resource = client.resource(validMachineUri);

        ClientResponse response = resource.accept(MediaType.APPLICATION_XML).get();
        assertEquals(200, response.getStatusCode());
        return response.getEntity(MachineDto.class);
    }
}
