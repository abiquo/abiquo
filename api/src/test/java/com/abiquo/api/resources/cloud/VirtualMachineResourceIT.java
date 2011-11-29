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
import static com.abiquo.api.common.UriTestResolver.resolveVirtualImageURI;
import static com.abiquo.api.common.UriTestResolver.resolveVirtualMachineActionGetIPsURI;
import static com.abiquo.api.common.UriTestResolver.resolveVirtualMachineStateURI;
import static com.abiquo.api.common.UriTestResolver.resolveVirtualMachineURI;
import static com.abiquo.testng.TestConfig.BASIC_INTEGRATION_TESTS;
import static com.abiquo.testng.TestConfig.NETWORK_INTEGRATION_TESTS;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import org.apache.activemq.broker.BrokerService;
import org.apache.wink.client.ClientConfig;
import org.apache.wink.client.ClientResponse;
import org.apache.wink.client.Resource;
import org.apache.wink.client.RestClient;
import org.apache.wink.common.internal.utils.UriHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.abiquo.api.common.UriTestResolver;
import com.abiquo.api.exceptions.APIError;
import com.abiquo.api.resources.appslibrary.VirtualImageResource;
import com.abiquo.model.enumerator.RemoteServiceType;
import com.abiquo.model.transport.error.ErrorDto;
import com.abiquo.model.transport.error.ErrorsDto;
import com.abiquo.scheduler.AllocatorAction;
import com.abiquo.scheduler.PopulateTestCase;
import com.abiquo.scheduler.TestPopulate;
import com.abiquo.server.core.appslibrary.VirtualImage;
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
import com.abiquo.tracer.Constants;

public class VirtualMachineResourceIT extends TestPopulate
{
    protected Enterprise ent;

    protected Datacenter datacenter;

    protected VirtualDatacenter vdc;

    protected VirtualAppliance vapp;

    protected Role r;

    protected User u;

    protected Enterprise e;

    private static final int CLIENT_TIMEOUT = 1000000000; // DEBUG

    private static final String FORCE_ENTERPRISE_LIMITS = "true";

    static RestClient client;

    final static Logger LOGGER = LoggerFactory.getLogger(VirtualMachineResourceIT.class);

    @Autowired
    VirtualMachineDAO vmachineDao;

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

    @BeforeMethod
    public void setUp()
    {
        ent = enterpriseGenerator.createUniqueInstance();
        datacenter = datacenterGenerator.createUniqueInstance();
        vdc = vdcGenerator.createInstance(datacenter, ent);
        vapp = vappGenerator.createInstance(vdc);
    }

    @Override
    @AfterMethod(groups = {BASIC_INTEGRATION_TESTS, NETWORK_INTEGRATION_TESTS})
    public void tearDown()
    {
        super.tearDown();
    }

    @Test(enabled = false, dataProvider = TestPopulate.DATA_PROVIDER)
    public void allocator(final List<String> model)
    {
        PopulateTestCase tcase = setUpModel(model);

        LOGGER.info("Running allocator test [{}]", tcase.testName);
        LOGGER.debug("[{}]", tcase.testDescription);

        for (AllocatorAction action : tcase.actions)
        {
            allocatorAction(action);
        }
    }

    @Test(dataProvider = TestPopulate.DATA_PROVIDER, enabled = false)
    public void allocatorConcurrent(final List<String> model)
    {
        PopulateTestCase tcase = setUpModel(model);

        LOGGER.info("Running allocator concurrent test [{}]", tcase.testName);
        LOGGER.debug("[{}]", tcase.testDescription);

        ExecutorService executor = Executors.newFixedThreadPool(4);

        for (AllocatorAction action : tcase.actions)
        {
            executor.submit(new AllocatorActionRunner(action));
        }

        try
        {
            executor.awaitTermination(10, TimeUnit.SECONDS);
            executor.shutdownNow();
        }
        catch (InterruptedException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    // @Test(groups = "redisaccess")
    // public void test_enric()
    // {
    // // Create a virtual machine
    // VirtualMachine vm = vmGenerator.createInstance(ent);
    //
    // vm.getVirtualImage().getRepository().setDatacenter(datacenter);
    // Machine machine = vm.getHypervisor().getMachine();
    // machine.setDatacenter(vdc.getDatacenter());
    // machine.setRack(null);
    //
    // // Associate it to the created virtual appliance
    // NodeVirtualImage nvi = nodeVirtualImageGenerator.createInstance(vapp, vm);
    //
    // List<Object> entitiesToSetup = new ArrayList<Object>();
    //
    // entitiesToSetup.add(ent);
    // entitiesToSetup.add(datacenter);
    // entitiesToSetup.add(vdc);
    // entitiesToSetup.add(vapp);
    //
    // for (Privilege p : vm.getUser().getRole().getPrivileges())
    // {
    // entitiesToSetup.add(p);
    // }
    //
    // entitiesToSetup.add(vm.getUser().getRole());
    // entitiesToSetup.add(vm.getUser());
    // entitiesToSetup.add(vm.getVirtualImage().getRepository());
    // entitiesToSetup.add(vm.getVirtualImage().getCategory());
    // entitiesToSetup.add(vm.getVirtualImage());
    // entitiesToSetup.add(machine);
    // entitiesToSetup.add(vm.getHypervisor());
    // entitiesToSetup.add(vm);
    // entitiesToSetup.add(nvi);
    //
    // setup(entitiesToSetup.toArray());
    //
    // // Persist some redis data
    // TaskGenerator taskGenerator = new TaskGenerator();
    // JobGenerator jobGenerator = new JobGenerator();
    //
    // Job configure = jobGenerator.createUniqueInstance();
    // configure.setType(JobType.CONFIGURE);
    //
    // Job poweron = jobGenerator.createUniqueInstance();
    // poweron.setType(JobType.POWER_ON);
    //
    // Task deploy = taskGenerator.createUniqueInstance();
    // deploy.setType(TaskType.DEPLOY);
    // deploy.setOwnerId(vm.getId().toString());
    // deploy.setUserId(vm.getUser().getId().toString());
    // deploy.getJobs().add(configure);
    // deploy.getJobs().add(poweron);
    //
    // // Check for vm
    // ClientResponse response =
    // get(resolveVirtualMachineURI(vdc.getId(), vapp.getId(), vm.getId()) + "/tasks");
    //
    // TasksDto tasks = response.getEntity(TasksDto.class);
    //
    // assertEquals(response.getStatusCode(), Status.OK.getStatusCode());
    // }

    /**
     * Create two virtual machines into a virtual appliance. Check the resources are addressable.
     */
    // @Test
    public void getVirtualMachineTest()
    {
        // Create a virtual machine
        VirtualMachine vm = vmGenerator.createInstance(ent);
        VirtualMachine vm2 = vmGenerator.createInstance(ent);

        vm.getVirtualImage().getRepository().setDatacenter(datacenter);
        vm2.getVirtualImage().getRepository().setDatacenter(datacenter);
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
        entitiesToSetup.add(vm.getVirtualImage().getRepository());
        entitiesToSetup.add(vm.getVirtualImage().getCategory());
        entitiesToSetup.add(vm.getVirtualImage());
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
        entitiesToSetup.add(vm2.getVirtualImage().getRepository());
        entitiesToSetup.add(vm2.getVirtualImage().getCategory());
        entitiesToSetup.add(vm2.getVirtualImage());
        entitiesToSetup.add(machine2);
        entitiesToSetup.add(vm2.getHypervisor());
        entitiesToSetup.add(vm2);
        entitiesToSetup.add(nvi2);

        setup(entitiesToSetup.toArray());

        // Check for vm
        ClientResponse response =
            get(resolveVirtualMachineURI(vdc.getId(), vapp.getId(), vm.getId()));
        assertEquals(response.getStatusCode(), Status.OK.getStatusCode());
        VirtualMachineDto vmDto = response.getEntity(VirtualMachineDto.class);
        assertLinkExist(vmDto,
            resolveVirtualMachineActionGetIPsURI(vdc.getId(), vapp.getId(), vm.getId()),
            VirtualMachineNetworkConfigurationResource.NIC,
            VirtualMachineNetworkConfigurationResource.NIC);
        assertLinkExist(vmDto, resolveVirtualMachineURI(vdc.getId(), vapp.getId(), vm.getId()),
            "edit");
        assertLinkExist(
            vmDto,
            resolveVirtualImageURI(vm.getVirtualImage().getEnterprise().getId(), vm
                .getVirtualImage().getRepository().getDatacenter().getId(), vm.getVirtualImage()
                .getId()), VirtualImageResource.VIRTUAL_IMAGE);

        assertNotNull(vmDto);

        // Check for vm2
        response = get(resolveVirtualMachineURI(vdc.getId(), vapp.getId(), vm2.getId()));
        assertEquals(response.getStatusCode(), Status.OK.getStatusCode());
        vmDto = response.getEntity(VirtualMachineDto.class);
        resolveVirtualMachineActionGetIPsURI(vdc.getId(), vapp.getId(), vm2.getId());
        assertLinkExist(vmDto, resolveVirtualMachineURI(vdc.getId(), vapp.getId(), vm2.getId()),
            "edit");
        assertLinkExist(
            vmDto,
            resolveVirtualImageURI(vm2.getVirtualImage().getEnterprise().getId(), vm2
                .getVirtualImage().getRepository().getDatacenter().getId(), vm2.getVirtualImage()
                .getId()), VirtualImageResource.VIRTUAL_IMAGE);

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
            get(resolveVirtualMachineURI(vdc.getId(), vapp.getId(), new Random().nextInt(1000)));
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

        vm.getVirtualImage().getRepository().setDatacenter(datacenter);
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
        entitiesToSetup.add(vm.getVirtualImage().getRepository());
        entitiesToSetup.add(vm.getVirtualImage().getCategory());
        entitiesToSetup.add(vm.getVirtualImage());
        entitiesToSetup.add(machine);
        entitiesToSetup.add(vm.getHypervisor());
        entitiesToSetup.add(vm);
        entitiesToSetup.add(nvi);

        setup(entitiesToSetup.toArray());

        // Check the vm has been succesfully created
        ClientResponse response =
            get(resolveVirtualMachineURI(vdc.getId(), vapp.getId(), vm.getId()));
        assertEquals(response.getStatusCode(), Status.OK.getStatusCode());
        VirtualMachineDto vmDto = response.getEntity(VirtualMachineDto.class);
        assertNotNull(vmDto);

        // Check again the valid value of vm Id but with an invalid vapp Id
        response =
            get(resolveVirtualMachineURI(vdc.getId(), new Random().nextInt(1000), vm.getId()));
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

        vm.getVirtualImage().getRepository().setDatacenter(datacenter);
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
        entitiesToSetup.add(vm.getVirtualImage().getRepository());
        entitiesToSetup.add(vm.getVirtualImage().getCategory());
        entitiesToSetup.add(vm.getVirtualImage());
        entitiesToSetup.add(machine);
        entitiesToSetup.add(vm.getHypervisor());
        entitiesToSetup.add(vm);
        entitiesToSetup.add(nvi);

        setup(entitiesToSetup.toArray());

        // Check the vm has been succesfully created
        ClientResponse response =
            get(resolveVirtualMachineURI(vdc.getId(), vapp.getId(), vm.getId()));
        assertEquals(response.getStatusCode(), Status.OK.getStatusCode());
        VirtualMachineDto vmDto = response.getEntity(VirtualMachineDto.class);
        assertNotNull(vmDto);

        // Check again the valid value of vm Id but with an invalid vdc Id
        response =
            get(resolveVirtualMachineURI(new Random().nextInt(1000), vapp.getId(), vm.getId()));
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

        vm.getVirtualImage().getRepository()
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
        entitiesToSetup.add(vm.getVirtualImage().getRepository());
        entitiesToSetup.add(vm.getVirtualImage().getCategory());
        entitiesToSetup.add(vm.getVirtualImage());
        entitiesToSetup.add(machine);
        entitiesToSetup.add(vm.getHypervisor().getMachine());
        entitiesToSetup.add(vm.getHypervisor());
        entitiesToSetup.add(vm);
        entitiesToSetup.add(nvi);

        setup(entitiesToSetup.toArray());

        ClientResponse response =
            get(resolveVirtualMachineActionGetIPsURI(vdc.getId(), vapp.getId(), vm.getId()));
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
                new Random().nextInt(1000)));
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

        vm.getVirtualImage().getRepository().setDatacenter(datacenter);
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
        entitiesToSetup.add(vm.getVirtualImage().getRepository());
        entitiesToSetup.add(vm.getVirtualImage().getCategory());
        entitiesToSetup.add(vm.getVirtualImage());
        entitiesToSetup.add(machine);
        entitiesToSetup.add(vm.getHypervisor());
        entitiesToSetup.add(vm);
        entitiesToSetup.add(nvi);

        setup(entitiesToSetup.toArray());

        ClientResponse response =
            get(resolveVirtualMachineActionGetIPsURI(new Random().nextInt(1000), vapp.getId(),
                vm.getId()));
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

        vm.getVirtualImage().getRepository().setDatacenter(datacenter);
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
        entitiesToSetup.add(vm.getVirtualImage().getRepository());
        entitiesToSetup.add(vm.getVirtualImage().getCategory());
        entitiesToSetup.add(vm.getVirtualImage());
        entitiesToSetup.add(machine);
        entitiesToSetup.add(vm.getHypervisor());
        entitiesToSetup.add(vm);
        entitiesToSetup.add(nvi);

        setup(entitiesToSetup.toArray());

        ClientResponse response =
            get(resolveVirtualMachineActionGetIPsURI(vdc.getId(), new Random().nextInt(1000),
                vm.getId()));
        assertEquals(response.getStatusCode(), Status.NOT_FOUND.getStatusCode());
    }

    class AllocatorActionRunner implements Callable<Boolean>
    {
        AllocatorAction action;

        public AllocatorActionRunner(final AllocatorAction action)
        {
            this.action = action;
        }

        @Override
        public Boolean call() throws AssertionError
        {
            allocatorAction(action);
            return true;
        }
    }

    private void allocatorAction(final AllocatorAction action)
    {
        Integer virtualDatacenterId = action.virtualDatacenterId;
        Integer virtualApplianceId = action.virtualApplianceId;
        Integer virtualMachineId = action.virtualMachineId;

        String vmUrl =
            UriTestResolver.resolveVirtualMachineURI(virtualDatacenterId, virtualApplianceId,
                virtualMachineId);

        vmUrl = UriHelper.appendPathToBaseUri(vmUrl, "action/allocate");

        Resource resource =
            client.resource(vmUrl).contentType(MediaType.TEXT_PLAIN)
                .accept(MediaType.APPLICATION_XML);

        if (action.allocate)
        {
            ClientResponse response = resource.put(FORCE_ENTERPRISE_LIMITS);

            boolean success = response.getStatusCode() / 200 == 1;

            if (!success)
            {
                ErrorsDto errors = response.getEntity(ErrorsDto.class);

                boolean noResources = false;
                boolean limit = false;
                for (ErrorDto error : errors.getCollection())
                {
                    noResources =
                        noResources
                            || error.getCode().equalsIgnoreCase(
                                APIError.NOT_ENOUGH_RESOURCES.getCode());

                    limit =
                        limit
                            || error.getCode().equalsIgnoreCase(APIError.LIMIT_EXCEEDED.getCode());

                }

                if (action.targetMachineName.contains("no_resource"))
                {
                    Assert.assertTrue(noResources,
                        String.format("expected no_resource for vmId [%d]", virtualMachineId));
                }
                else if (action.targetMachineName.contains("limit"))
                {
                    Assert.assertTrue(limit,
                        String.format("expected limit for vmId [%d]", virtualMachineId));
                }
                else
                {
                    Assert.fail("For vmId[" + String.valueOf(virtualMachineId) + "]Expected "
                        + action.targetMachineName + ", but was : " + errors.toString());
                }

            }// no success
            else
            {
                VirtualMachineDto vmachineDto = response.getEntity(VirtualMachineDto.class);

                Assert.assertTrue(vmachineDto != null, "virtual machine not found");

                final String machineName = vmachineDto.getDescription();

                Assert.assertTrue(action.targetMachineName.contains(machineName), String.format(
                    "Expected machine was [%s] but selected [%s],\n for vmId [%d]",
                    action.targetMachineName, machineName, virtualMachineId));
            } // success
        }
        else
        // deallocate
        {
            ClientResponse response = resource.delete();

            boolean success = response.getStatusCode() / 200 == 1;

            Assert.assertTrue(success, "Deallocation fail");

            removeVirtualMachine(virtualMachineId);
        }
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
    // @Test
    public void getVirtualMachineStateTest()
    {
        VirtualImage vi = virtualImageGenerator.createInstance(ent, datacenter);
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
        entitiesToSetup.add(vm.getVirtualImage());
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
        assertEquals(VirtualMachineState.OFF.name(), vmDto.getPower());

    }

    /**
     * Create a virtual machines and retrieve its state.
     */
    // @Test
    public void getVirtualMachineStateLinkTest()
    {
        // Create a virtual machine
        VirtualImage vi = virtualImageGenerator.createInstance(ent, datacenter);
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
        entitiesToSetup.add(vm.getVirtualImage());
        entitiesToSetup.add(machine);
        entitiesToSetup.add(vm.getHypervisor());
        entitiesToSetup.add(vm);
        entitiesToSetup.add(nvi);
        setup(entitiesToSetup.toArray());

        // Check for vm state
        ClientResponse response =
            get(resolveVirtualMachineURI(vdc.getId(), vapp.getId(), vm.getId()), "sysadmin",
                "sysadmin");
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
        entitiesToSetup.add(vm.getVirtualImage());
        entitiesToSetup.add(machine);
        entitiesToSetup.add(vm.getHypervisor());
        entitiesToSetup.add(vm);
        entitiesToSetup.add(nvi);

        setup(entitiesToSetup.toArray());

        VirtualMachineStateDto dto = new VirtualMachineStateDto();
        dto.setPower(VirtualMachineState.OFF.name());
        // Check for vm state
        ClientResponse response =
            put(resolveVirtualMachineStateURI(vdc.getId(), vapp.getId(), vm.getId()), dto,
                "sysadmin", "sysadmin");
        VirtualMachineStateDto vmDto = response.getEntity(VirtualMachineStateDto.class);
        assertEquals(VirtualMachineState.OFF.name(), vmDto.getPower());

    }

    /**
     * Delete a virtual machines state.
     */
    public void deleteVirtualMachineTest()
    {
        // Create a virtual machine
        VirtualImage vi = virtualImageGenerator.createInstance(ent, datacenter);
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
        assertEquals(Status.NO_CONTENT.getStatusCode(), response.getStatusCode());

    }

    /**
     * Delete a virtual machines fail its state.
     */
    public void deleteVirtualMachineInvalidTest()
    {
        // Create a virtual machine
        VirtualImage vi = virtualImageGenerator.createInstance(ent, datacenter);
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
        entitiesToSetup.add(vm.getVirtualImage());
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

        vm.getVirtualImage().getRepository().setDatacenter(datacenter);
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
        entitiesToSetup.add(vm.getVirtualImage().getRepository());
        entitiesToSetup.add(vm.getVirtualImage().getCategory());
        entitiesToSetup.add(vm.getVirtualImage());
        entitiesToSetup.add(machine);
        entitiesToSetup.add(vm.getHypervisor());
        entitiesToSetup.add(vm);
        entitiesToSetup.add(nvi);

        setup(entitiesToSetup.toArray());

        // Check for vm
        ClientResponse response =
            get(resolveVirtualMachineURI(vdc.getId(), vapp.getId(), vm.getId()), "sysadmin",
                "sysadmin", VirtualMachineResource.VM_NODE_MEDIA_TYPE);
        VirtualMachineWithNodeDto vmDto = response.getEntity(VirtualMachineWithNodeDto.class);

        assertNotNull(vmDto);

    }
}
