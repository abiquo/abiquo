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

package com.abiquo.api.resources.cloud;

import static com.abiquo.api.common.Assert.assertLinkExist;
import static com.abiquo.api.common.UriTestResolver.resolveVirtualMachineActionGetIPsURI;
import static com.abiquo.api.common.UriTestResolver.resolveVirtualMachineStateURI;
import static com.abiquo.api.common.UriTestResolver.resolveVirtualMachineTemplateURI;
import static com.abiquo.api.common.UriTestResolver.resolveVirtualMachineURI;
import static com.abiquo.testng.TestConfig.BASIC_INTEGRATION_TESTS;
import static com.abiquo.testng.TestConfig.NETWORK_INTEGRATION_TESTS;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.ws.rs.core.Response.Status;

import org.apache.activemq.broker.BrokerService;
import org.apache.wink.client.ClientConfig;
import org.apache.wink.client.ClientResponse;
import org.apache.wink.client.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import com.abiquo.api.resources.AbstractJpaGeneratorIT;
import com.abiquo.api.resources.TaskResourceUtils;
import com.abiquo.api.resources.appslibrary.VirtualMachineTemplateResource;
import com.abiquo.api.services.TaskService;
import com.abiquo.model.enumerator.RemoteServiceType;
import com.abiquo.model.rest.RESTLink;
import com.abiquo.server.core.appslibrary.VirtualMachineTemplate;
import com.abiquo.server.core.cloud.NodeVirtualImage;
import com.abiquo.server.core.cloud.VirtualAppliance;
import com.abiquo.server.core.cloud.VirtualDatacenter;
import com.abiquo.server.core.cloud.VirtualMachine;
import com.abiquo.server.core.cloud.VirtualMachineDAO;
import com.abiquo.server.core.cloud.VirtualMachineDto;
import com.abiquo.server.core.cloud.VirtualMachineState;
import com.abiquo.server.core.cloud.VirtualMachineStateDto;
import com.abiquo.server.core.cloud.VirtualMachineWithNodeDto;
import com.abiquo.server.core.enterprise.Enterprise;
import com.abiquo.server.core.enterprise.Privilege;
import com.abiquo.server.core.enterprise.Role;
import com.abiquo.server.core.enterprise.User;
import com.abiquo.server.core.infrastructure.Datacenter;
import com.abiquo.server.core.infrastructure.Datastore;
import com.abiquo.server.core.infrastructure.Machine;
import com.abiquo.server.core.infrastructure.RemoteService;
import com.abiquo.server.core.infrastructure.network.NicsDto;
import com.abiquo.server.core.task.Job;
import com.abiquo.server.core.task.Job.JobType;
import com.abiquo.server.core.task.JobGenerator;
import com.abiquo.server.core.task.Task;
import com.abiquo.server.core.task.TaskDto;
import com.abiquo.server.core.task.TaskGenerator;
import com.abiquo.server.core.task.TasksDto;
import com.abiquo.server.core.task.enums.TaskType;
import com.abiquo.tracer.Constants;

public class VirtualMachineResourceIT extends AbstractJpaGeneratorIT
{
    protected Enterprise ent;

    protected Datacenter datacenter;

    protected VirtualDatacenter vdc;

    protected VirtualAppliance vapp;

    protected Role r;

    protected User u;

    protected Enterprise e;

    private static final int CLIENT_TIMEOUT = 1000000000; // DEBUG

    private static final String SYSADMIN = "sysadmin";

    static RestClient client;

    final static Logger LOGGER = LoggerFactory.getLogger(VirtualMachineResourceIT.class);

    @Autowired
    VirtualMachineDAO vmachineDao;

    @Autowired
    protected TaskService taskService;

    @Autowired
    protected JedisPool jedisPool;

    @BeforeClass
    public static void setUpServer() throws Exception
    {
        ClientConfig conf = new ClientConfig();
        conf.readTimeout(CLIENT_TIMEOUT);
        client = new RestClient(conf);
        initTraceProcessor();
    }

    @AfterClass
    public static void tearDownServer() throws Exception
    {
        destroyTraceProcessor();
    }

    @BeforeMethod
    public void setupSysadmin()
    {
        Enterprise e = enterpriseGenerator.createUniqueInstance();
        Role r = roleGenerator.createInstanceSysAdmin();
        User u = userGenerator.createInstance(e, r, SYSADMIN, SYSADMIN);

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

    @BeforeMethod
    public void setUp()
    {
        ent = enterpriseGenerator.createUniqueInstance();
        datacenter = datacenterGenerator.createUniqueInstance();
        vdc = vdcGenerator.createInstance(datacenter, ent);
        vapp = vappGenerator.createInstance(vdc);
    }

    @AfterTest
    public void clearRedis()
    {
        Jedis jedis = jedisPool.getResource();
        jedis.flushDB();
        jedisPool.returnResource(jedis);
    }

    @Override
    @AfterMethod(groups = {BASIC_INTEGRATION_TESTS, NETWORK_INTEGRATION_TESTS})
    public void tearDown()
    {
        super.tearDown();
    }

    @Test(groups = "redisaccess")
    public void test_redisBackedTasks()
    {
        // Create a virtual machine
        VirtualMachine vm = vmGenerator.createInstance(ent);

        vm.getVirtualMachineTemplate().getRepository().setDatacenter(datacenter);
        Machine machine = vm.getHypervisor().getMachine();
        machine.setDatacenter(vdc.getDatacenter());
        machine.setRack(null);

        // Associate it to the created virtual appliance
        NodeVirtualImage nvi = nodeVirtualImageGenerator.createInstance(vapp, vm);

        List<Object> entitiesToSetup = new ArrayList<Object>();

        entitiesToSetup.add(ent);
        entitiesToSetup.add(datacenter);
        entitiesToSetup.add(vdc);
        entitiesToSetup.add(vapp);

        for (Privilege p : vm.getUser().getRole().getPrivileges())
        {
            entitiesToSetup.add(p);
        }

        entitiesToSetup.add(vm.getUser().getRole());
        entitiesToSetup.add(vm.getUser());
        entitiesToSetup.add(vm.getVirtualMachineTemplate().getRepository());
        entitiesToSetup.add(vm.getVirtualMachineTemplate().getCategory());
        entitiesToSetup.add(vm.getVirtualMachineTemplate());
        entitiesToSetup.add(machine);
        entitiesToSetup.add(vm.getHypervisor());
        entitiesToSetup.add(vm);
        entitiesToSetup.add(nvi);

        setup(entitiesToSetup.toArray());

        // Persist redis data
        TaskGenerator taskGenerator = new TaskGenerator();
        JobGenerator jobGenerator = new JobGenerator();

        Job configure = jobGenerator.createUniqueInstance();
        configure.setType(JobType.CONFIGURE);

        Job poweron = jobGenerator.createUniqueInstance();
        poweron.setType(JobType.POWER_ON);

        Task deploy = taskGenerator.createUniqueInstance();
        deploy.setType(TaskType.DEPLOY);
        deploy.setOwnerId(vm.getId().toString());
        deploy.setUserId(vm.getUser().getId().toString());
        deploy.getJobs().add(configure);
        deploy.getJobs().add(poweron);

        taskService.addTask(deploy);

        // Test happy path TASKS
        String vmURI = resolveVirtualMachineURI(vdc.getId(), vapp.getId(), vm.getId());
        String tasksURI = vmURI.concat(TaskResourceUtils.TASKS_PATH);

        ClientResponse response = get(tasksURI, SYSADMIN, SYSADMIN, TasksDto.MEDIA_TYPE);

        TasksDto tasks = response.getEntity(TasksDto.class);
        assertEquals(response.getStatusCode(), Status.OK.getStatusCode());
        assertNotNull(tasks);
        assertEquals(tasks.getCollection().size(), 1);

        RESTLink parent = tasks.searchLink("parent");
        RESTLink self = tasks.searchLink("self");

        assertNotNull(parent);
        assertNotNull(self);

        assertEquals(parent.getHref(), vmURI);
        assertEquals(self.getHref(), tasksURI);

        // Test happy path TASK
        vmURI = resolveVirtualMachineURI(vdc.getId(), vapp.getId(), vm.getId());
        tasksURI = vmURI.concat(TaskResourceUtils.TASKS_PATH);
        String taskURI = tasksURI.concat("/").concat(deploy.getTaskId());

        response = get(taskURI, SYSADMIN, SYSADMIN, TaskDto.MEDIA_TYPE);

        TaskDto task = response.getEntity(TaskDto.class);
        assertEquals(response.getStatusCode(), Status.OK.getStatusCode());
        assertNotNull(task);

        parent = task.searchLink("parent");
        self = task.searchLink("self");

        assertNotNull(parent);
        assertNotNull(self);

        assertEquals(parent.getHref(), tasksURI);
        assertEquals(self.getHref(), taskURI);

        // NOT FOUND when invalid VDC
        vmURI = resolveVirtualMachineURI(vdc.getId() + 1, vapp.getId(), vm.getId());
        tasksURI = vmURI.concat(TaskResourceUtils.TASKS_PATH);
        taskURI = tasksURI.concat("/").concat(deploy.getTaskId());

        response = get(taskURI, SYSADMIN, SYSADMIN, TaskDto.MEDIA_TYPE);
        assertEquals(response.getStatusCode(), Status.NOT_FOUND.getStatusCode());

        // NOT FOUND when invalid VAPP
        vmURI = resolveVirtualMachineURI(vdc.getId(), vapp.getId() + 1, vm.getId());
        tasksURI = vmURI.concat(TaskResourceUtils.TASKS_PATH);
        taskURI = tasksURI.concat("/").concat(deploy.getTaskId());

        response = get(taskURI, SYSADMIN, SYSADMIN, TaskDto.MEDIA_TYPE);
        assertEquals(response.getStatusCode(), Status.NOT_FOUND.getStatusCode());

        // NOT FOUND when invalid VM
        vmURI = resolveVirtualMachineURI(vdc.getId(), vapp.getId(), vm.getId() + 1);
        tasksURI = vmURI.concat(TaskResourceUtils.TASKS_PATH);
        taskURI = tasksURI.concat("/").concat(deploy.getTaskId());

        response = get(taskURI, SYSADMIN, SYSADMIN, TaskDto.MEDIA_TYPE);
        assertEquals(response.getStatusCode(), Status.NOT_FOUND.getStatusCode());
    }

    /**
     * Create two virtual machines into a virtual appliance. Check the resources are addressable.
     */
    // @Test
    public void getVirtualMachineTest()
    {
        // Create a virtual machine
        VirtualMachine vm = vmGenerator.createInstance(ent);
        VirtualMachine vm2 = vmGenerator.createInstance(ent);

        vm.getVirtualMachineTemplate().getRepository().setDatacenter(datacenter);
        vm2.getVirtualMachineTemplate().getRepository().setDatacenter(datacenter);
        Machine machine = vm.getHypervisor().getMachine();
        machine.setDatacenter(vdc.getDatacenter());
        machine.setRack(null);

        Machine machine2 = vm2.getHypervisor().getMachine();
        machine2.setDatacenter(vdc.getDatacenter());
        machine2.setRack(null);

        // Asociate it to the created virtual appliance
        NodeVirtualImage nvi = nodeVirtualImageGenerator.createInstance(vapp, vm);
        NodeVirtualImage nvi2 = nodeVirtualImageGenerator.createInstance(vapp, vm2);

        List<Object> entitiesToSetup = new ArrayList<Object>();

        entitiesToSetup.add(ent);
        entitiesToSetup.add(datacenter);
        entitiesToSetup.add(vdc);
        entitiesToSetup.add(vapp);

        for (Privilege p : vm.getUser().getRole().getPrivileges())
        {
            entitiesToSetup.add(p);
        }

        entitiesToSetup.add(vm.getUser().getRole());
        entitiesToSetup.add(vm.getUser());
        entitiesToSetup.add(vm.getVirtualMachineTemplate().getRepository());
        entitiesToSetup.add(vm.getVirtualMachineTemplate().getCategory());
        entitiesToSetup.add(vm.getVirtualMachineTemplate());
        entitiesToSetup.add(machine);
        entitiesToSetup.add(vm.getHypervisor());
        entitiesToSetup.add(vm);
        entitiesToSetup.add(nvi);

        for (Privilege p : vm2.getUser().getRole().getPrivileges())
        {
            entitiesToSetup.add(p);
        }

        entitiesToSetup.add(vm2.getUser().getRole());
        entitiesToSetup.add(vm2.getUser());
        entitiesToSetup.add(vm2.getVirtualMachineTemplate().getRepository());
        entitiesToSetup.add(vm2.getVirtualMachineTemplate().getCategory());
        entitiesToSetup.add(vm2.getVirtualMachineTemplate());
        entitiesToSetup.add(machine2);
        entitiesToSetup.add(vm2.getHypervisor());
        entitiesToSetup.add(vm2);
        entitiesToSetup.add(nvi2);

        setup(entitiesToSetup.toArray());

        // Check for vm
        ClientResponse response =
            get(resolveVirtualMachineURI(vdc.getId(), vapp.getId(), vm.getId()), SYSADMIN,
                SYSADMIN, VirtualMachineDto.MEDIA_TYPE);
        assertEquals(response.getStatusCode(), Status.OK.getStatusCode());

        response.toString();

        VirtualMachineDto vmDto = response.getEntity(VirtualMachineDto.class);
        assertLinkExist(vmDto,
            resolveVirtualMachineActionGetIPsURI(vdc.getId(), vapp.getId(), vm.getId()),
            VirtualMachineNetworkConfigurationResource.NICS_PATH);
        assertLinkExist(vmDto, resolveVirtualMachineURI(vdc.getId(), vapp.getId(), vm.getId()),
            "edit");
        assertLinkExist(
            vmDto,
            resolveVirtualMachineTemplateURI(
                vm.getVirtualMachineTemplate().getEnterprise().getId(), vm
                    .getVirtualMachineTemplate().getRepository().getDatacenter().getId(), vm
                    .getVirtualMachineTemplate().getId()),
            VirtualMachineTemplateResource.VIRTUAL_MACHINE_TEMPLATE);

        assertNotNull(vmDto);

        // Check for vm2
        response =
            get(resolveVirtualMachineURI(vdc.getId(), vapp.getId(), vm2.getId()), SYSADMIN,
                SYSADMIN, VirtualMachineDto.MEDIA_TYPE);
        assertEquals(response.getStatusCode(), Status.OK.getStatusCode());
        vmDto = response.getEntity(VirtualMachineDto.class);
        resolveVirtualMachineActionGetIPsURI(vdc.getId(), vapp.getId(), vm2.getId());
        assertLinkExist(vmDto, resolveVirtualMachineURI(vdc.getId(), vapp.getId(), vm2.getId()),
            "edit");
        assertLinkExist(
            vmDto,
            resolveVirtualMachineTemplateURI(vm2.getVirtualMachineTemplate().getEnterprise()
                .getId(), vm2.getVirtualMachineTemplate().getRepository().getDatacenter().getId(),
                vm2.getVirtualMachineTemplate().getId()),
            VirtualMachineTemplateResource.VIRTUAL_MACHINE_TEMPLATE);

        assertNotNull(vmDto);
    }

    /**
     * Check an invalid virtual machine id Server response should return a 404 NOT FOUND status code
     */
    // @Test
    public void getVirtualMachineRaises404WhenInvalidVirtualMachineId()
    {
        setup(ent, datacenter, vdc, vapp);

        // Check a randomly value
        ClientResponse response =
            get(resolveVirtualMachineURI(vdc.getId(), vapp.getId(), new Random().nextInt(1000)),
                SYSADMIN, SYSADMIN, VirtualMachineDto.MEDIA_TYPE);
        assertEquals(response.getStatusCode(), Status.NOT_FOUND.getStatusCode());
    }

    /**
     * Check an invalid virtual appliance value for a valid virtual machine id Server response
     * should return a 404 NOT FOUND status code
     */
    // @Test
    public void getVirtualMachineRaises404WhenInvalidVirtualApplianceId()
    {
        VirtualMachine vm = vmGenerator.createInstance(ent);
        NodeVirtualImage nvi = nodeVirtualImageGenerator.createInstance(vapp, vm);

        vm.getVirtualMachineTemplate().getRepository().setDatacenter(datacenter);
        Machine machine = vm.getHypervisor().getMachine();
        machine.setDatacenter(vdc.getDatacenter());
        machine.setRack(null);

        List<Object> entitiesToSetup = new ArrayList<Object>();

        entitiesToSetup.add(ent);
        entitiesToSetup.add(datacenter);
        entitiesToSetup.add(vdc);
        entitiesToSetup.add(vapp);

        for (Privilege p : vm.getUser().getRole().getPrivileges())
        {
            entitiesToSetup.add(p);
        }

        entitiesToSetup.add(vm.getUser().getRole());
        entitiesToSetup.add(vm.getUser());
        entitiesToSetup.add(vm.getVirtualMachineTemplate().getRepository());
        entitiesToSetup.add(vm.getVirtualMachineTemplate().getCategory());
        entitiesToSetup.add(vm.getVirtualMachineTemplate());
        entitiesToSetup.add(machine);
        entitiesToSetup.add(vm.getHypervisor());
        entitiesToSetup.add(vm);
        entitiesToSetup.add(nvi);

        setup(entitiesToSetup.toArray());

        // Check the vm has been succesfully created
        ClientResponse response =
            get(resolveVirtualMachineURI(vdc.getId(), vapp.getId(), vm.getId()), SYSADMIN,
                SYSADMIN, VirtualMachineDto.MEDIA_TYPE);
        assertEquals(response.getStatusCode(), Status.OK.getStatusCode());
        VirtualMachineDto vmDto = response.getEntity(VirtualMachineDto.class);
        assertNotNull(vmDto);

        // Check again the valid value of vm Id but with an invalid vapp Id
        response =
            get(resolveVirtualMachineURI(vdc.getId(), vapp.getId() + 1, vm.getId()), SYSADMIN,
                SYSADMIN, VirtualMachineDto.MEDIA_TYPE);
        assertEquals(response.getStatusCode(), Status.NOT_FOUND.getStatusCode());
    }

    /**
     * Check the virtual machine object of an invalid virtualdatacenter id Server response should
     * return a 404 NOT FOUND status code
     */
    // @Test
    public void getVirtualMachineRaises404WhenInvalidVirtualDatacenterId()
    {
        VirtualMachine vm = vmGenerator.createInstance(ent);
        NodeVirtualImage nvi = nodeVirtualImageGenerator.createInstance(vapp, vm);

        vm.getVirtualMachineTemplate().getRepository().setDatacenter(datacenter);
        Machine machine = vm.getHypervisor().getMachine();
        machine.setDatacenter(vdc.getDatacenter());
        machine.setRack(null);

        List<Object> entitiesToSetup = new ArrayList<Object>();

        entitiesToSetup.add(ent);
        entitiesToSetup.add(datacenter);
        entitiesToSetup.add(vdc);
        entitiesToSetup.add(vapp);

        for (Privilege p : vm.getUser().getRole().getPrivileges())
        {
            entitiesToSetup.add(p);
        }

        entitiesToSetup.add(vm.getUser().getRole());
        entitiesToSetup.add(vm.getUser());
        entitiesToSetup.add(vm.getVirtualMachineTemplate().getRepository());
        entitiesToSetup.add(vm.getVirtualMachineTemplate().getCategory());
        entitiesToSetup.add(vm.getVirtualMachineTemplate());
        entitiesToSetup.add(machine);
        entitiesToSetup.add(vm.getHypervisor());
        entitiesToSetup.add(vm);
        entitiesToSetup.add(nvi);

        setup(entitiesToSetup.toArray());

        // Check the vm has been succesfully created
        ClientResponse response =
            get(resolveVirtualMachineURI(vdc.getId(), vapp.getId(), vm.getId()), SYSADMIN,
                SYSADMIN, VirtualMachineDto.MEDIA_TYPE);
        assertEquals(response.getStatusCode(), Status.OK.getStatusCode());
        VirtualMachineDto vmDto = response.getEntity(VirtualMachineDto.class);
        assertNotNull(vmDto);

        // Check again the valid value of vm Id but with an invalid vdc Id
        response =
            get(resolveVirtualMachineURI(new Random().nextInt(1000), vapp.getId(), vm.getId()),
                SYSADMIN, SYSADMIN, VirtualMachineDto.MEDIA_TYPE);
        assertEquals(response.getStatusCode(), Status.NOT_FOUND.getStatusCode());
    }

    // TODO: Create a test to return a non-empty list of IPs.

    /**
     * Create a virtual machine. Check the action resource returns an empty list
     */
    // @Test(groups = {NETWORK_INTEGRATION_TESTS})
    public void getVirtualMachineActionIPsEmptyList()
    {
        VirtualMachine vm = vmGenerator.createInstance(ent);
        NodeVirtualImage nvi = nodeVirtualImageGenerator.createInstance(vapp, vm);

        Machine machine = vm.getHypervisor().getMachine();
        machine.setDatacenter(vdc.getDatacenter());
        machine.setRack(null);

        vm.getVirtualMachineTemplate().getRepository()
            .setDatacenter(vm.getHypervisor().getMachine().getDatacenter());

        List<Object> entitiesToSetup = new ArrayList<Object>();

        entitiesToSetup.add(ent);
        entitiesToSetup.add(datacenter);
        entitiesToSetup.add(vdc);
        entitiesToSetup.add(vapp);

        for (Privilege p : vm.getUser().getRole().getPrivileges())
        {
            entitiesToSetup.add(p);
        }

        entitiesToSetup.add(vm.getUser().getRole());
        entitiesToSetup.add(vm.getUser());
        entitiesToSetup.add(vm.getVirtualMachineTemplate().getRepository());
        entitiesToSetup.add(vm.getVirtualMachineTemplate().getCategory());
        entitiesToSetup.add(vm.getVirtualMachineTemplate());
        entitiesToSetup.add(machine);
        entitiesToSetup.add(vm.getHypervisor().getMachine());
        entitiesToSetup.add(vm.getHypervisor());
        entitiesToSetup.add(vm);
        entitiesToSetup.add(nvi);

        setup(entitiesToSetup.toArray());

        ClientResponse response =
            get(resolveVirtualMachineActionGetIPsURI(vdc.getId(), vapp.getId(), vm.getId()),
                SYSADMIN, SYSADMIN, NicsDto.MEDIA_TYPE);
        assertEquals(response.getStatusCode(), Status.OK.getStatusCode());

        NicsDto entity = response.getEntity(NicsDto.class);
        assertNotNull(entity);
        assertNotNull(entity.getCollection());
        assertEquals(entity.getCollection().size(), 0);
    }

    /**
     * Create a virtual machine. Ask the IPs for an invalid virtual machine identifier value.
     */
    // @Test(groups = {NETWORK_INTEGRATION_TESTS})
    public void getVirtualMachineActionIPsRaises404WhenVmIsARandomValue()
    {
        setup(ent, datacenter, vdc, vapp);
        ClientResponse response =
            get(resolveVirtualMachineActionGetIPsURI(vdc.getId(), vapp.getId(),
                new Random().nextInt(1000)), SYSADMIN, SYSADMIN, NicsDto.MEDIA_TYPE);
        assertEquals(response.getStatusCode(), Status.NOT_FOUND.getStatusCode());
    }

    /**
     * Create a virtual machine. Ask the IPs for a valid virtual appliance but invalid virtual
     * datacenter.
     */
    // @Test(groups = {NETWORK_INTEGRATION_TESTS})
    public void getVirtualMachineActionIPsRaises404WhenVappNotBelongsToVDC()
    {
        VirtualMachine vm = vmGenerator.createInstance(ent);
        NodeVirtualImage nvi = nodeVirtualImageGenerator.createInstance(vapp, vm);

        vm.getVirtualMachineTemplate().getRepository().setDatacenter(datacenter);
        Machine machine = vm.getHypervisor().getMachine();
        machine.setDatacenter(vdc.getDatacenter());
        machine.setRack(null);

        List<Object> entitiesToSetup = new ArrayList<Object>();

        entitiesToSetup.add(ent);
        entitiesToSetup.add(datacenter);
        entitiesToSetup.add(vdc);
        entitiesToSetup.add(vapp);

        for (Privilege p : vm.getUser().getRole().getPrivileges())
        {
            entitiesToSetup.add(p);
        }

        entitiesToSetup.add(vm.getUser().getRole());
        entitiesToSetup.add(vm.getUser());
        entitiesToSetup.add(vm.getVirtualMachineTemplate().getRepository());
        entitiesToSetup.add(vm.getVirtualMachineTemplate().getCategory());
        entitiesToSetup.add(vm.getVirtualMachineTemplate());
        entitiesToSetup.add(machine);
        entitiesToSetup.add(vm.getHypervisor());
        entitiesToSetup.add(vm);
        entitiesToSetup.add(nvi);

        setup(entitiesToSetup.toArray());

        ClientResponse response =
            get(resolveVirtualMachineActionGetIPsURI(vdc.getId() + 1, vapp.getId(), vm.getId()),
                SYSADMIN, SYSADMIN, NicsDto.MEDIA_TYPE);
        assertEquals(response.getStatusCode(), Status.NOT_FOUND.getStatusCode());
    }

    /**
     * Create a virtual machine. Ask the IPs for a valid virtual machine but invalid virtual
     * appliance.
     */
    // @Test
    public void getVirtualMachineActionIPsRaises404WhenVMNotBelongsToVapp()
    {
        VirtualMachine vm = vmGenerator.createInstance(ent);
        NodeVirtualImage nvi = nodeVirtualImageGenerator.createInstance(vapp, vm);

        vm.getVirtualMachineTemplate().getRepository().setDatacenter(datacenter);
        Machine machine = vm.getHypervisor().getMachine();
        machine.setDatacenter(vdc.getDatacenter());
        machine.setRack(null);

        List<Object> entitiesToSetup = new ArrayList<Object>();

        entitiesToSetup.add(ent);
        entitiesToSetup.add(datacenter);
        entitiesToSetup.add(vdc);
        entitiesToSetup.add(vapp);

        for (Privilege p : vm.getUser().getRole().getPrivileges())
        {
            entitiesToSetup.add(p);
        }

        entitiesToSetup.add(vm.getUser().getRole());
        entitiesToSetup.add(vm.getUser());
        entitiesToSetup.add(vm.getVirtualMachineTemplate().getRepository());
        entitiesToSetup.add(vm.getVirtualMachineTemplate().getCategory());
        entitiesToSetup.add(vm.getVirtualMachineTemplate());
        entitiesToSetup.add(machine);
        entitiesToSetup.add(vm.getHypervisor());
        entitiesToSetup.add(vm);
        entitiesToSetup.add(nvi);

        setup(entitiesToSetup.toArray());

        ClientResponse response =
            get(resolveVirtualMachineActionGetIPsURI(vdc.getId(), vapp.getId() + 1, vm.getId()),
                SYSADMIN, SYSADMIN, NicsDto.MEDIA_TYPE);
        assertEquals(response.getStatusCode(), Status.NOT_FOUND.getStatusCode());
    }

    /**
     * Trace related
     */

    static private BrokerService broker;

    static private String brokerUrl;

    /**
     * API use the tracer in order to write the Soft/Hard limits exceeded
     */
    static private void initTraceProcessor()
    {
        System.setProperty(Constants.ABICLOUD_TRACER_BROKER_URL, "tcp://localhost:6996");
        broker = new BrokerService();

        // configure the broker
        try
        {
            brokerUrl =
                System.getProperty(Constants.ABICLOUD_TRACER_BROKER_URL, Constants.BROKER_URL);
            broker.addConnector(brokerUrl);
            broker.setPersistent(false);
            broker.start();
        }
        catch (Exception e)
        {
            Assert.fail("Can't configure the trace borker", e);
        }
    }

    static private void destroyTraceProcessor() throws Exception
    {
        broker.stop();
    }

    /**
     * Create a virtual machines and retrieve its state.
     */
    @Test(enabled = false)
    public void getVirtualMachineStateTest()
    {
        VirtualMachineTemplate vi = virtualMachineTemplateGenerator.createInstance(ent, datacenter);
        VirtualMachine vm = vmGenerator.createInstance(vi);
        vm.setState(VirtualMachineState.OFF);

        Machine machine = vm.getHypervisor().getMachine();
        machine.setDatacenter(vdc.getDatacenter());
        machine.setRack(null);

        // Asociate it to the created virtual appliance
        NodeVirtualImage nvi = nodeVirtualImageGenerator.createInstance(vapp, vm);

        List<Object> entitiesToSetup = new ArrayList<Object>();

        entitiesToSetup.add(ent);
        entitiesToSetup.add(datacenter);
        entitiesToSetup.add(vi.getCategory());
        entitiesToSetup.add(vi.getRepository());
        entitiesToSetup.add(vi);
        entitiesToSetup.add(vdc);
        entitiesToSetup.add(vapp);

        for (Privilege p : vm.getUser().getRole().getPrivileges())
        {
            entitiesToSetup.add(p);
        }

        entitiesToSetup.add(vm.getUser().getEnterprise());
        entitiesToSetup.add(vm.getUser().getRole());
        entitiesToSetup.add(vm.getUser());
        entitiesToSetup.add(vm.getVirtualMachineTemplate());
        entitiesToSetup.add(machine);
        entitiesToSetup.add(vm.getHypervisor());
        entitiesToSetup.add(vm);
        entitiesToSetup.add(nvi);

        setup(entitiesToSetup.toArray());

        // Check for vm state
        ClientResponse response =
            get(resolveVirtualMachineStateURI(vdc.getId(), vapp.getId(), vm.getId()), "sysadmin",
                "sysadmin");
        VirtualMachineStateDto vmDto = response.getEntity(VirtualMachineStateDto.class);
        assertEquals(vmDto.getState().name(), VirtualMachineState.OFF.name());

    }

    /**
     * Create a virtual machines and retrieve its state.
     */
    // @Test
    public void getVirtualMachineStateLinkTest()
    {
        // Create a virtual machine
        VirtualMachineTemplate vi = virtualMachineTemplateGenerator.createInstance(ent, datacenter);
        VirtualMachine vm = vmGenerator.createInstance(vi);
        vm.setState(VirtualMachineState.OFF);

        Machine machine = vm.getHypervisor().getMachine();
        machine.setDatacenter(vdc.getDatacenter());
        machine.setRack(null);

        // Asociate it to the created virtual appliance
        NodeVirtualImage nvi = nodeVirtualImageGenerator.createInstance(vapp, vm);

        List<Object> entitiesToSetup = new ArrayList<Object>();

        entitiesToSetup.add(ent);
        entitiesToSetup.add(datacenter);
        entitiesToSetup.add(vi.getCategory());
        entitiesToSetup.add(vi.getRepository());
        entitiesToSetup.add(vi);
        entitiesToSetup.add(vdc);
        entitiesToSetup.add(vapp);

        for (Privilege p : vm.getUser().getRole().getPrivileges())
        {
            entitiesToSetup.add(p);
        }

        entitiesToSetup.add(vm.getUser().getEnterprise());
        entitiesToSetup.add(vm.getUser().getRole());
        entitiesToSetup.add(vm.getUser());
        entitiesToSetup.add(vm.getVirtualMachineTemplate());
        entitiesToSetup.add(machine);
        entitiesToSetup.add(vm.getHypervisor());
        entitiesToSetup.add(vm);
        entitiesToSetup.add(nvi);
        setup(entitiesToSetup.toArray());

        // Check for vm state
        ClientResponse response =
            get(resolveVirtualMachineURI(vdc.getId(), vapp.getId(), vm.getId()), "sysadmin",
                "sysadmin", VirtualMachineDto.MEDIA_TYPE);
        VirtualMachineDto vmDto = response.getEntity(VirtualMachineDto.class);
        assertLinkExist(vmDto,
            resolveVirtualMachineStateURI(vdc.getId(), vapp.getId(), vm.getId()), "state");

    }

    /**
     * Create a virtual machines and retrieve its state.
     */
    @Test(enabled = false)
    public void getVirtualMachineSetStateTest()
    {
        // Create a virtual machine
        VirtualMachine vm = vmGenerator.createInstance(ent);
        vm.setState(VirtualMachineState.ON);

        Machine machine = vm.getHypervisor().getMachine();
        machine.setDatacenter(vdc.getDatacenter());
        machine.setRack(null);

        Datastore datastore = datastoreGenerator.createInstance(machine);

        vm.setDatastore(datastore);

        // Asociate it to the created virtual appliance
        NodeVirtualImage nvi = nodeVirtualImageGenerator.createInstance(vapp, vm);

        RemoteService rs =
            remoteServiceGenerator.createInstance(RemoteServiceType.VIRTUAL_FACTORY, datacenter);

        List<Object> entitiesToSetup = new ArrayList<Object>();
        entitiesToSetup.add(datacenter);
        entitiesToSetup.add(ent);
        entitiesToSetup.add(rs);
        entitiesToSetup.add(vdc);
        entitiesToSetup.add(vapp);
        entitiesToSetup.add(datastore);

        for (Privilege p : vm.getUser().getRole().getPrivileges())
        {
            entitiesToSetup.add(p);
        }

        entitiesToSetup.add(vm.getUser().getRole());
        entitiesToSetup.add(vm.getUser());
        entitiesToSetup.add(vm.getVirtualMachineTemplate());
        entitiesToSetup.add(machine);
        entitiesToSetup.add(vm.getHypervisor());
        entitiesToSetup.add(vm);
        entitiesToSetup.add(nvi);

        setup(entitiesToSetup.toArray());

        VirtualMachineStateDto dto = new VirtualMachineStateDto();
        dto.setState(VirtualMachineState.OFF);
        // Check for vm state
        ClientResponse response =
            put(resolveVirtualMachineStateURI(vdc.getId(), vapp.getId(), vm.getId()), dto,
                "sysadmin", "sysadmin");
        VirtualMachineStateDto vmDto = response.getEntity(VirtualMachineStateDto.class);
        assertEquals(VirtualMachineState.OFF, vmDto.getState());

    }

    /**
     * Delete a virtual machines state.
     */
    public void deleteVirtualMachineTest()
    {
        // Create a virtual machine
        VirtualMachineTemplate vi = virtualMachineTemplateGenerator.createInstance(ent, datacenter);
        VirtualMachine vm = vmGenerator.createInstance(vi);
        vm.setState(VirtualMachineState.NOT_ALLOCATED);

        Machine machine = vm.getHypervisor().getMachine();
        machine.setDatacenter(vdc.getDatacenter());
        machine.setRack(null);

        // Asociate it to the created virtual appliance
        NodeVirtualImage nvi = nodeVirtualImageGenerator.createInstance(vapp, vm);

        List<Object> entitiesToSetup = new ArrayList<Object>();

        entitiesToSetup.add(ent);
        entitiesToSetup.add(datacenter);

        entitiesToSetup.add(vi.getCategory());
        entitiesToSetup.add(vi.getRepository());
        entitiesToSetup.add(vi);

        entitiesToSetup.add(vdc);
        entitiesToSetup.add(vapp);

        for (Privilege p : vm.getUser().getRole().getPrivileges())
        {
            entitiesToSetup.add(p);
        }

        entitiesToSetup.add(vm.getUser().getRole());
        entitiesToSetup.add(vm.getUser().getEnterprise());
        entitiesToSetup.add(vm.getUser());

        entitiesToSetup.add(machine);
        entitiesToSetup.add(vm.getHypervisor());
        entitiesToSetup.add(vm);
        entitiesToSetup.add(nvi);

        setup(entitiesToSetup.toArray());

        // Check for vm state
        ClientResponse response =
            delete(resolveVirtualMachineURI(vdc.getId(), vapp.getId(), vm.getId()), "sysadmin",
                "sysadmin");

        assertEquals(response.getStatusCode(), Status.NO_CONTENT.getStatusCode());
    }

    /**
     * Delete a virtual machines fail its state.
     */
    @Test(enabled = false)
    public void deleteVirtualMachineInvalidTest()
    {
        // Create a virtual machine
        VirtualMachineTemplate vi = virtualMachineTemplateGenerator.createInstance(ent, datacenter);
        VirtualMachine vm = vmGenerator.createInstance(vi);
        vm.setState(VirtualMachineState.ON);

        Machine machine = vm.getHypervisor().getMachine();
        machine.setDatacenter(vdc.getDatacenter());
        machine.setRack(null);
        // Asociate it to the created virtual appliance
        NodeVirtualImage nvi = nodeVirtualImageGenerator.createInstance(vapp, vm);

        List<Object> entitiesToSetup = new ArrayList<Object>();

        entitiesToSetup.add(ent);
        entitiesToSetup.add(datacenter);

        entitiesToSetup.add(vi.getCategory());
        entitiesToSetup.add(vi.getRepository());
        entitiesToSetup.add(vi);

        entitiesToSetup.add(vdc);
        entitiesToSetup.add(vapp);

        for (Privilege p : vm.getUser().getRole().getPrivileges())
        {
            entitiesToSetup.add(p);
        }

        entitiesToSetup.add(vm.getUser().getEnterprise());
        entitiesToSetup.add(vm.getUser().getRole());
        entitiesToSetup.add(vm.getUser());
        entitiesToSetup.add(vm.getVirtualMachineTemplate());
        entitiesToSetup.add(machine);
        entitiesToSetup.add(vm.getHypervisor());
        entitiesToSetup.add(vm);
        entitiesToSetup.add(nvi);

        setup(entitiesToSetup.toArray());

        // Check for vm state
        ClientResponse response =
            delete(resolveVirtualMachineURI(vdc.getId(), vapp.getId(), vm.getId()), "sysadmin",
                "sysadmin");
        assertEquals(Status.CONFLICT.getStatusCode(), response.getStatusCode());

    }

    /**
     * Create two virtual machines into a virtual appliance. Check the resources are addressable.
     */
    // @Test
    public void getVirtualMachineWithNodeTest()
    {
        // Create a virtual machine
        VirtualMachine vm = vmGenerator.createInstance(ent);

        vm.getVirtualMachineTemplate().getRepository().setDatacenter(datacenter);
        Machine machine = vm.getHypervisor().getMachine();
        machine.setDatacenter(vdc.getDatacenter());
        machine.setRack(null);

        // Asociate it to the created virtual appliance
        NodeVirtualImage nvi = nodeVirtualImageGenerator.createInstance(vapp, vm);

        List<Object> entitiesToSetup = new ArrayList<Object>();

        entitiesToSetup.add(ent);
        entitiesToSetup.add(datacenter);
        entitiesToSetup.add(vdc);
        entitiesToSetup.add(vapp);

        for (Privilege p : vm.getUser().getRole().getPrivileges())
        {
            entitiesToSetup.add(p);
        }

        entitiesToSetup.add(vm.getUser().getRole());
        entitiesToSetup.add(vm.getUser());
        entitiesToSetup.add(vm.getVirtualMachineTemplate().getRepository());
        entitiesToSetup.add(vm.getVirtualMachineTemplate().getCategory());
        entitiesToSetup.add(vm.getVirtualMachineTemplate());
        entitiesToSetup.add(machine);
        entitiesToSetup.add(vm.getHypervisor());
        entitiesToSetup.add(vm);
        entitiesToSetup.add(nvi);

        setup(entitiesToSetup.toArray());

        // Check for vm
        ClientResponse response =
            get(resolveVirtualMachineURI(vdc.getId(), vapp.getId(), vm.getId()), "sysadmin",
                "sysadmin", VirtualMachineWithNodeDto.MEDIA_TYPE);
        VirtualMachineWithNodeDto vmDto = response.getEntity(VirtualMachineWithNodeDto.class);

        assertNotNull(vmDto);

    }

}
