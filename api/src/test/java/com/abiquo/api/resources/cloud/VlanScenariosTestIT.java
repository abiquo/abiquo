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

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.ws.rs.core.MediaType;

import org.apache.wink.client.ClientConfig;
import org.apache.wink.client.ClientResponse;
import org.apache.wink.client.Resource;
import org.apache.wink.client.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.abiquo.api.common.UriTestResolver;
import com.abiquo.scheduler.AllocatorAction;
import com.abiquo.scheduler.PopulateInfrastructure;
import com.abiquo.scheduler.PopulateVirtualInfrastructure;
import com.abiquo.scheduler.TestPopulate;
import com.abiquo.scheduler.VlanPopulateReader;
import com.abiquo.server.core.cloud.VirtualMachineDto;

public class VlanScenariosTestIT extends TestPopulate
{
    public static final String RACK_FULL_PROVIDER = "rackFullModel";

    public static final String RACK_WITH_1_VLAN_SLOT = "rackWithNRSQwarning";

    private static final String FORCE_ENTERPRISE_LIMITS = "true";

    private static final int CLIENT_TIMEOUT = 1000000000; // DEBUG

    final static Logger LOGGER = LoggerFactory.getLogger(VlanScenariosTestIT.class);

    static RestClient client;

    @Autowired
    PopulateInfrastructure populateInfrastructure;

    @Autowired
    PopulateVirtualInfrastructure populateVirtualInfrastructure;

    @Autowired
    VlanPopulateReader vlanPopulateReader;

    @BeforeClass
    public static void setUpServer() throws Exception
    {
        ClientConfig conf = new ClientConfig();
        conf.readTimeout(CLIENT_TIMEOUT);
        client = new RestClient(conf);
    }

    @BeforeMethod
    public void setupModel()
    {
        setup();
    }

    @Test(dataProvider = RACK_FULL_PROVIDER, enabled = false)
    public void testRackFullAllocation(AllocatorAction allocatorAction)
    {
        allocatorAction(allocatorAction);
    }

    @Test(dataProvider = RACK_WITH_1_VLAN_SLOT, enabled = false)
    public void testrackWithNRSQwarning(AllocatorAction allocatorAction)
    {
        allocatorAction(allocatorAction);
    }

    @DataProvider(name = RACK_FULL_PROVIDER)
    public Iterator<Object[]> rackFullModel()
    {
        List<Object[]> models = new LinkedList<Object[]>();
        models.add(new Object[] {vlanPopulateReader.rackFullModel()});
        return models.iterator();
    }

    @DataProvider(name = RACK_WITH_1_VLAN_SLOT)
    public Iterator<Object[]> rackWithNRSQwarning()
    {
        List<Object[]> models = new LinkedList<Object[]>();
        models.add(new Object[] {vlanPopulateReader.rackWithNRSQwarning()});
        return models.iterator();
    }

    private void allocatorAction(AllocatorAction action)
    {
        Integer virtualDatacenterId = action.virtualDatacenterId;
        Integer virtualApplianceId = action.virtualApplianceId;
        Integer virtualMachineId = action.virtualMachineId;

        String vmUrl =
            UriTestResolver.resolveVirtualMachineURI(virtualDatacenterId, virtualApplianceId,
                virtualMachineId);

        // http://localhost:9009/api/cloud/virtualdatacenters/1/vapps/1/virtualMachines/1
        Resource resource =
            client.resource(vmUrl).contentType(MediaType.TEXT_PLAIN)
                .accept(MediaType.APPLICATION_XML);

        if (action.allocate)
        {
            ClientResponse response = resource.put(FORCE_ENTERPRISE_LIMITS);

            Assert.assertTrue(response.getStatusCode() / 200 == 1);

            VirtualMachineDto vmachineDto = response.getEntity(VirtualMachineDto.class);

            Assert.assertTrue(vmachineDto != null);

            final String machineName = vmachineDto.getDescription();

        }
        // TODO else deallocate

        // assertEquals(404, response.getStatusCode());
        //
        // ErrorsDto errors = response.getEntity(ErrorsDto.class);
        // assertNonEmptyErrors(errors);
    }

}
