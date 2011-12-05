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

import static com.abiquo.api.common.UriTestResolver.resolveVirtualMachineActionGetIPsURI;
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
import com.abiquo.model.transport.error.ErrorDto;
import com.abiquo.model.transport.error.ErrorsDto;
import com.abiquo.scheduler.AllocatorAction;
import com.abiquo.scheduler.PopulateTestCase;
import com.abiquo.scheduler.TestPopulate;
import com.abiquo.server.core.cloud.NodeVirtualImage;
import com.abiquo.server.core.cloud.VirtualAppliance;
import com.abiquo.server.core.cloud.VirtualDatacenter;
import com.abiquo.server.core.cloud.VirtualMachine;
import com.abiquo.server.core.cloud.VirtualMachineDAO;
import com.abiquo.server.core.cloud.VirtualMachineDto;
import com.abiquo.server.core.enterprise.Enterprise;
import com.abiquo.server.core.enterprise.Privilege;
import com.abiquo.server.core.infrastructure.Datacenter;
import com.abiquo.server.core.infrastructure.Machine;
import com.abiquo.server.core.infrastructure.network.NicsDto;
import com.abiquo.tracer.Constants;

public class VirtualMachineResourceIT extends TestPopulate
{
    protected Enterprise ent;

    protected Datacenter datacenter;

    protected VirtualDatacenter vdc;

    protected VirtualAppliance vapp;

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

    @BeforeMethod(groups = {BASIC_INTEGRATION_TESTS, NETWORK_INTEGRATION_TESTS})
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

    /**
     * Create two virtual machines into a virtual appliance. Check the resources are addressable.
     */
    @Test
    public void getVirtualMachineTest()
    {
        // Create a virtual machine
        VirtualMachine vm = vmGenerator.createInstance(ent);
        VirtualMachine vm2 = vmGenerator.createInstance(ent);

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
        // assertLinkExist(vmDto,
        // resolveVirtualMachineActionGetIPsURI(vdc.getId(), vapp.getId(), vm.getId()), "action",
        // VirtualMachineNetworkConfigurationResource.NICS_PATH);
        // assertLinkExist(vmDto, resolveVirtualMachineURI(vdc.getId(), vapp.getId(), vm.getId()),
        // "edit");
        assertNotNull(vmDto);

        // Check for vm2
        response = get(resolveVirtualMachineURI(vdc.getId(), vapp.getId(), vm2.getId()));
        assertEquals(response.getStatusCode(), Status.OK.getStatusCode());
        vmDto = response.getEntity(VirtualMachineDto.class);
        // assertLinkExist(vmDto,
        // resolveVirtualMachineActionGetIPsURI(vdc.getId(), vapp.getId(), vm2.getId()), "action",
        // VirtualMachineNetworkConfigurationResource.NICS_PATH);
        // assertLinkExist(vmDto, resolveVirtualMachineURI(vdc.getId(), vapp.getId(), vm2.getId()),
        // "edit");
        assertNotNull(vmDto);
    }

    /**
     * Check an invalid virtual machine id Server response should return a 404 NOT FOUND status code
     */
    @Test
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
    @Test
    public void getVirtualMachineRaises404WhenInvalidVirtualApplianceId()
    {
        VirtualMachine vm = vmGenerator.createInstance(ent);
        NodeVirtualImage nvi = nodeVirtualImageGenerator.createInstance(vapp, vm);

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
    @Test
    public void getVirtualMachineRaises404WhenInvalidVirtualDatacenterId()
    {
        VirtualMachine vm = vmGenerator.createInstance(ent);
        NodeVirtualImage nvi = nodeVirtualImageGenerator.createInstance(vapp, vm);

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
    @Test(groups = {NETWORK_INTEGRATION_TESTS})
    public void getVirtualMachineActionIPsEmptyList()
    {
        VirtualMachine vm = vmGenerator.createInstance(ent);
        NodeVirtualImage nvi = nodeVirtualImageGenerator.createInstance(vapp, vm);

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
    @Test(groups = {NETWORK_INTEGRATION_TESTS})
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
    @Test(groups = {NETWORK_INTEGRATION_TESTS})
    public void getVirtualMachineActionIPsRaises404WhenVappNotBelongsToVDC()
    {
        VirtualMachine vm = vmGenerator.createInstance(ent);
        NodeVirtualImage nvi = nodeVirtualImageGenerator.createInstance(vapp, vm);

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
    @Test
    public void getVirtualMachineActionIPsRaises404WhenVMNotBelongsToVapp()
    {
        VirtualMachine vm = vmGenerator.createInstance(ent);
        NodeVirtualImage nvi = nodeVirtualImageGenerator.createInstance(vapp, vm);

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
}
