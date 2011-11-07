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

import static com.abiquo.api.common.Assert.assertErrors;
import static com.abiquo.api.common.Assert.assertLinkExist;
import static com.abiquo.api.common.UriTestResolver.resolveDatacenterURI;
import static com.abiquo.api.common.UriTestResolver.resolveDatacenterURIActionDiscover;
import static com.abiquo.api.common.UriTestResolver.resolveDatacenterURIActionDiscoverHypervidor;
import static com.abiquo.api.common.UriTestResolver.resolveDatacenterURIActionDiscoverMultiple;
import static com.abiquo.api.common.UriTestResolver.resolveEnterprisesByDatacenterURI;
import static com.abiquo.api.common.UriTestResolver.resolveHypervisorTypesURI;
import static com.abiquo.api.common.UriTestResolver.resolveRacksURI;
import static com.abiquo.api.common.UriTestResolver.resolveRemoteServicesURI;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.io.IOException;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import org.apache.wink.client.ClientResponse;
import org.apache.wink.client.ClientWebException;
import org.apache.wink.client.Resource;
import org.apache.wink.common.internal.MultivaluedMapImpl;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.abiquo.api.exceptions.APIError;
import com.abiquo.api.services.stub.NodecollectorServiceStubMock;
import com.abiquo.model.enumerator.HypervisorType;
import com.abiquo.model.enumerator.RemoteServiceType;
import com.abiquo.server.core.cloud.HypervisorTypesDto;
import com.abiquo.server.core.cloud.VirtualDatacenter;
import com.abiquo.server.core.enterprise.DatacenterLimits;
import com.abiquo.server.core.enterprise.Enterprise;
import com.abiquo.server.core.enterprise.EnterprisesDto;
import com.abiquo.server.core.infrastructure.Datacenter;
import com.abiquo.server.core.infrastructure.DatacenterDto;
import com.abiquo.server.core.infrastructure.Machine;
import com.abiquo.server.core.infrastructure.MachineDto;
import com.abiquo.server.core.infrastructure.MachinesDto;
import com.abiquo.server.core.infrastructure.RemoteService;
import com.abiquo.server.core.infrastructure.network.VLANNetwork;

public class DatacenterResourceIT extends AbstractJpaGeneratorIT
{

    private String validDatacenterUri;

    private String validDatacenterUriDiscover;

    private String validDatacenterUriDiscoverMultiple;

    private Datacenter datacenter;

    @Override
    @BeforeMethod
    public void setup()
    {
        datacenter = datacenterGenerator.createUniqueInstance();
        RemoteService rs =
            remoteServiceGenerator.createInstance(RemoteServiceType.NODE_COLLECTOR, datacenter);
        setup(datacenter, rs);

        validDatacenterUri = resolveDatacenterURI(datacenter.getId());
    }

    @Test
    public void getDatacenterDoesntExist() throws ClientWebException
    {
        ClientResponse response = get(resolveDatacenterURI(12345));
        assertEquals(404, response.getStatusCode());
        assertErrors(response, 404, APIError.NON_EXISTENT_DATACENTER.getCode());
    }

    @Test
    public void existsLinks()
    {
        DatacenterDto dc = createDatacenter();
        assertNotNull(dc);
        assertLinkExist(dc, resolveRacksURI(dc.getId()), RacksResource.RACKS_PATH);
        assertLinkExist(dc, resolveHypervisorTypesURI(dc.getId()),
            DatacenterResource.HYPERVISORS_PATH);
        assertLinkExist(dc, resolveRemoteServicesURI(dc.getId()),
            RemoteServicesResource.REMOTE_SERVICES_PATH);
        assertLinkExist(dc, resolveDatacenterURI(dc.getId()), "edit");
        assertLinkExist(dc, resolveDatacenterURIActionDiscover(dc.getId()),
            DatacenterResource.ACTION_DISCOVER_SINGLE_REL);
        assertLinkExist(dc, resolveDatacenterURIActionDiscoverMultiple(dc.getId()),
            DatacenterResource.ACTION_DISCOVER_MULTIPLE_REL);
    }

    @Test
    public void modifyDatacenter() throws ClientWebException
    {
        DatacenterDto dc = createDatacenter();
        String uri = dc.getEditLink().getHref();

        DatacenterDto datacenter = get(uri).getEntity(DatacenterDto.class);

        datacenter.setLocation("datacenter_situation_changed");
        ClientResponse response = put(uri, datacenter);
        assertEquals(200, response.getStatusCode());

        datacenter = get(uri).getEntity(DatacenterDto.class);
        assertEquals("datacenter_situation_changed", datacenter.getLocation());
    }

    @Test
    public void getHypervisorTypes() throws ClientWebException, IOException
    {
        Datacenter datacenter = datacenterGenerator.createUniqueInstance();
        Machine m0 = machineGenerator.createMachine(datacenter);
        Machine m1 = machineGenerator.createMachine(datacenter);

        m0.setHypervisor(hypervisorGenerator.createInstance(m0));
        m1.setHypervisor(hypervisorGenerator.createInstance(m1));

        setup(datacenter, m0, m1);

        String uri = resolveHypervisorTypesURI(datacenter.getId());

        ClientResponse response = get(uri);
        assertEquals(response.getStatusCode(), 200);

        HypervisorTypesDto types = response.getEntity(HypervisorTypesDto.class);
        assertNotNull(types);
        assertEquals(types.getCollection().isEmpty(), false);

        response = get(resolveHypervisorTypesURI(12345));
        assertEquals(404, response.getStatusCode());
        assertErrors(response, 404, APIError.NON_EXISTENT_DATACENTER.getCode());
    }

    @Test
    public void getEnterprisesByDatacenters() throws ClientWebException, IOException
    {
        Datacenter datacenter = datacenterGenerator.createUniqueInstance();
        Enterprise enterprise = enterpriseGenerator.createUniqueInstance();
        DatacenterLimits dcl = datacenterLimitsGenerator.createInstance(enterprise, datacenter);

        setup(enterprise, datacenter, dcl);
        Integer datacenterId = datacenter.getId();

        String uri = resolveEnterprisesByDatacenterURI(datacenterId);

        ClientResponse response = get(uri);
        assertEquals(response.getStatusCode(), 200);

        EnterprisesDto enterprises = response.getEntity(EnterprisesDto.class);
        assertNotNull(enterprises);
        assertEquals(enterprises.getCollection().isEmpty(), false);

        assertEquals(enterprises.getCollection().get(0).getName(), enterprise.getName());
        assertEquals(enterprises.getCollection().get(0).getId(), enterprise.getId());
    }

    @Test
    public void getEnterprisesWithNetworkByDatacenters() throws ClientWebException, IOException
    {

        RemoteService rs;
        VLANNetwork vlan;
        VirtualDatacenter vdc;
        rs = remoteServiceGenerator.createInstance(RemoteServiceType.DHCP_SERVICE);
        vdc = vdcGenerator.createInstance(rs.getDatacenter());
        setup(vdc.getDatacenter(), rs, vdc.getEnterprise(), vdc.getNetwork(), vdc);
        vlan = vlanGenerator.createInstance(vdc.getNetwork(), rs, "255.255.255.0");
        setup(vlan);

        Enterprise enterprise = enterpriseGenerator.createUniqueInstance();
        DatacenterLimits dcl =
            datacenterLimitsGenerator.createInstance(enterprise, vdc.getDatacenter());
        DatacenterLimits dcl2 =
            datacenterLimitsGenerator.createInstance(vdc.getEnterprise(), vdc.getDatacenter());
        setup(enterprise, dcl, dcl2);
        Integer datacenterId = vdc.getDatacenter().getId();

        String uri0 = resolveEnterprisesByDatacenterURI(datacenterId);
        String uri1 = uri0 + "?network=true";
        String uri2 = uri0 + "?network=true&startwith=2&limit=1";
        ClientResponse response0 = get(uri0);
        ClientResponse response1 = get(uri1);
        ClientResponse response2 = get(uri2);
        assertEquals(response0.getStatusCode(), 200);
        assertEquals(response1.getStatusCode(), 200);
        assertEquals(response2.getStatusCode(), 200);

        EnterprisesDto enterprises0 = response0.getEntity(EnterprisesDto.class);
        EnterprisesDto enterprises1 = response1.getEntity(EnterprisesDto.class);
        EnterprisesDto enterprises2 = response2.getEntity(EnterprisesDto.class);
        assertNotNull(enterprises0);
        assertEquals(enterprises0.getCollection().size(), 2);
        assertEquals(enterprises1.getCollection().size(), 1);
        assertEquals(enterprises2.getCollection().size(), 1);
        assertEquals(enterprises1.getCollection().get(0).getName(), vdc.getEnterprise().getName());
    }

    /* delete is disabled at this moment */
    @Test(enabled = false)
    public void removeDatacenter() throws ClientWebException
    {
        DatacenterDto dc = createDatacenter();

        Resource resource = client.resource(dc.getEditLink().getHref());

        ClientResponse response = resource.accept(MediaType.APPLICATION_XML).delete();
        assertEquals(204, response.getStatusCode());
    }

    protected DatacenterDto createDatacenter()
    {
        Datacenter datacenter = datacenterGenerator.createUniqueInstance();
        setup(datacenter);

        String href = resolveDatacenterURI(datacenter.getId());

        return get(href).getEntity(DatacenterDto.class);
    }

    /**
     * Test the discover machine functionality for a correct behaviour. As you see, here we use the
     * {@link NodecollectorServiceStubMock} mock class. Because we don't test the nodecollector
     * behaviour, but the service behaviour in front of a correct response.
     * 
     * @throws Exception
     */
    @Test
    public void discoverMachineFunctionality() throws Exception
    {
        validDatacenterUriDiscover = resolveDatacenterURIActionDiscover(datacenter.getId());

        MultivaluedMapImpl<String, String> params = new MultivaluedMapImpl<String, String>();
        params.putSingle(DatacenterResource.IP, NodecollectorServiceStubMock.IP_CORRECT_1);
        params.putSingle(DatacenterResource.HYPERVISOR, HypervisorType.VMX_04.getValue());
        params.putSingle(DatacenterResource.USER, "user");
        params.putSingle(DatacenterResource.PASSWORD, "password");

        Resource resource = client.resource(validDatacenterUriDiscover).queryParams(params);
        ClientResponse response = resource.get();

        MachineDto machine = response.getEntity(MachineDto.class);
        assertNotNull(machine);
    }

    /**
     * Test the constraints check the id of the datacenter can not be less than 1.
     */
    @Test
    public void discoverMachineRaises400WhenDatacenterIdIsMinorThan1() throws Exception
    {
        validDatacenterUriDiscover = resolveDatacenterURIActionDiscover(0);
        MultivaluedMapImpl<String, String> params = new MultivaluedMapImpl<String, String>();
        params.putSingle(DatacenterResource.IP, NodecollectorServiceStubMock.IP_CORRECT_1);
        params.putSingle(DatacenterResource.HYPERVISOR, HypervisorType.VMX_04.getValue());
        params.putSingle(DatacenterResource.USER, "user");
        params.putSingle(DatacenterResource.PASSWORD, "password");

        Resource resource = client.resource(validDatacenterUriDiscover).queryParams(params);
        ClientResponse response = resource.get();

        assertEquals(response.getStatusCode(), Status.BAD_REQUEST.getStatusCode());
    }

    /**
     * Test the constraint @IP raises when it is not really an IP address
     * 
     * @throws Exception
     */
    @Test
    public void discoverMachineRaises400WhenParameterIPIsInvalid() throws Exception
    {
        validDatacenterUriDiscover = resolveDatacenterURIActionDiscover(0);
        MultivaluedMapImpl<String, String> params = new MultivaluedMapImpl<String, String>();
        params.putSingle(DatacenterResource.IP, "234.12..1");
        params.putSingle(DatacenterResource.HYPERVISOR, HypervisorType.VMX_04.getValue());
        params.putSingle(DatacenterResource.USER, "user");
        params.putSingle(DatacenterResource.PASSWORD, "password");

        Resource resource = client.resource(validDatacenterUriDiscover).queryParams(params);
        ClientResponse response = resource.get();

        assertEquals(response.getStatusCode(), Status.BAD_REQUEST.getStatusCode());
    }

    /**
     * Test the constraint @Hypervisor raises when it is not really an IP address
     * 
     * @throws Exception
     */
    @Test
    public void discoverMachineRaises400WhenParameterHypervisorIsInvalid() throws Exception
    {
        validDatacenterUriDiscover = resolveDatacenterURIActionDiscover(0);
        MultivaluedMapImpl<String, String> params = new MultivaluedMapImpl<String, String>();
        params.putSingle(DatacenterResource.IP, NodecollectorServiceStubMock.IP_CORRECT_1);
        params.putSingle(DatacenterResource.HYPERVISOR, "tetas");
        params.putSingle(DatacenterResource.USER, "user");
        params.putSingle(DatacenterResource.PASSWORD, "password");

        Resource resource = client.resource(validDatacenterUriDiscover).queryParams(params);
        ClientResponse response = resource.get();

        assertEquals(response.getStatusCode(), Status.BAD_REQUEST.getStatusCode());
    }

    /**
     * Test the constraint @Port when the value is bigger than 65535.
     * 
     * @throws Exception
     */
    @Test
    public void discoverMachineRaises400WhenParameterPortIsInvalid() throws Exception
    {
        validDatacenterUriDiscover = resolveDatacenterURIActionDiscover(0);
        MultivaluedMapImpl<String, String> params = new MultivaluedMapImpl<String, String>();
        params.putSingle(DatacenterResource.IP, NodecollectorServiceStubMock.IP_CORRECT_1);
        params.putSingle(DatacenterResource.HYPERVISOR, HypervisorType.VMX_04.getValue());
        params.putSingle(DatacenterResource.USER, "user");
        params.putSingle(DatacenterResource.PASSWORD, "password");
        params.putSingle(DatacenterResource.PORT, "70000");

        Resource resource = client.resource(validDatacenterUriDiscover).queryParams(params);
        ClientResponse response = resource.get();

        assertEquals(response.getStatusCode(), Status.BAD_REQUEST.getStatusCode());
    }

    /**
     * Test the discover multiple machines functionality and check some errors are ignored and only
     * returns the machines that match the query.
     * 
     * @throws Exception
     */
    @Test
    public void discoverMultipleMachinesFunctionality() throws Exception
    {
        validDatacenterUriDiscoverMultiple =
            resolveDatacenterURIActionDiscoverMultiple(datacenter.getId());

        MultivaluedMapImpl<String, String> params = new MultivaluedMapImpl<String, String>();
        params.putSingle(DatacenterResource.IP_FROM, NodecollectorServiceStubMock.IP_CORRECT_1);
        params.putSingle(DatacenterResource.IP_TO, NodecollectorServiceStubMock.IP_CORRECT_3);
        params.putSingle(DatacenterResource.HYPERVISOR, HypervisorType.VMX_04.getValue());
        params.putSingle(DatacenterResource.USER, "user");
        params.putSingle(DatacenterResource.PASSWORD, "password");

        Resource resource = client.resource(validDatacenterUriDiscoverMultiple).queryParams(params);
        ClientResponse response = resource.get();

        MachinesDto machines = response.getEntity(MachinesDto.class);
        assertNotNull(machines);
        assertEquals(machines.getCollection().size(), 3);
    }

    /**
     * Test the constraint @IP raises when the parameter IpFrom it is not really an IP address
     * 
     * @throws Exception
     */
    @Test
    public void discoverMachineRaises400WhenParameterIPFromIsInvalid() throws Exception
    {
        validDatacenterUriDiscover = resolveDatacenterURIActionDiscover(0);
        MultivaluedMapImpl<String, String> params = new MultivaluedMapImpl<String, String>();
        params.putSingle(DatacenterResource.IP_FROM, "234.12..1");
        params.putSingle(DatacenterResource.IP_TO, NodecollectorServiceStubMock.IP_CORRECT_3);
        params.putSingle(DatacenterResource.HYPERVISOR, HypervisorType.VMX_04.getValue());
        params.putSingle(DatacenterResource.USER, "user");
        params.putSingle(DatacenterResource.PASSWORD, "password");

        Resource resource = client.resource(validDatacenterUriDiscover).queryParams(params);
        ClientResponse response = resource.get();

        assertEquals(response.getStatusCode(), Status.BAD_REQUEST.getStatusCode());
    }

    /**
     * Test the constraint @IP raises when the parameter IpTo it is not really an IP address
     * 
     * @throws Exception
     */
    @Test
    public void discoverMachineRaises400WhenParameterIPToIsInvalid() throws Exception
    {
        validDatacenterUriDiscover = resolveDatacenterURIActionDiscover(0);
        MultivaluedMapImpl<String, String> params = new MultivaluedMapImpl<String, String>();
        params.putSingle(DatacenterResource.IP_FROM, NodecollectorServiceStubMock.IP_CORRECT_1);
        params.putSingle(DatacenterResource.IP_TO, "234.12..1");
        params.putSingle(DatacenterResource.HYPERVISOR, HypervisorType.VMX_04.getValue());
        params.putSingle(DatacenterResource.USER, "user");
        params.putSingle(DatacenterResource.PASSWORD, "password");

        Resource resource = client.resource(validDatacenterUriDiscover).queryParams(params);
        ClientResponse response = resource.get();

        assertEquals(response.getStatusCode(), Status.BAD_REQUEST.getStatusCode());
    }

    /**
     * Test the constraint @Hypervisor raises when it is not really an IP address
     * 
     * @throws Exception
     */
    @Test
    public void discoverMultipleMachineRaises400WhenParameterHypervisorIsInvalid() throws Exception
    {
        validDatacenterUriDiscover = resolveDatacenterURIActionDiscover(0);
        MultivaluedMapImpl<String, String> params = new MultivaluedMapImpl<String, String>();
        params.putSingle(DatacenterResource.IP_FROM, NodecollectorServiceStubMock.IP_CORRECT_1);
        params.putSingle(DatacenterResource.IP_TO, NodecollectorServiceStubMock.IP_CORRECT_3);
        params.putSingle(DatacenterResource.HYPERVISOR, "tetas");
        params.putSingle(DatacenterResource.USER, "user");
        params.putSingle(DatacenterResource.PASSWORD, "password");

        Resource resource = client.resource(validDatacenterUriDiscover).queryParams(params);
        ClientResponse response = resource.get();

        assertEquals(response.getStatusCode(), Status.BAD_REQUEST.getStatusCode());
    }

    /**
     * Test the constraint @Port when the value is bigger than 65535.
     * 
     * @throws Exception
     */
    @Test
    public void discoverMultipleMachineRaises400WhenParameterPortIsInvalid() throws Exception
    {
        validDatacenterUriDiscover = resolveDatacenterURIActionDiscover(0);
        MultivaluedMapImpl<String, String> params = new MultivaluedMapImpl<String, String>();
        params.putSingle(DatacenterResource.IP_FROM, NodecollectorServiceStubMock.IP_CORRECT_1);
        params.putSingle(DatacenterResource.IP_TO, NodecollectorServiceStubMock.IP_CORRECT_3);
        params.putSingle(DatacenterResource.HYPERVISOR, HypervisorType.VMX_04.getValue());
        params.putSingle(DatacenterResource.USER, "user");
        params.putSingle(DatacenterResource.PASSWORD, "password");
        params.putSingle(DatacenterResource.PORT, "70000");

        Resource resource = client.resource(validDatacenterUriDiscover).queryParams(params);
        ClientResponse response = resource.get();

        assertEquals(response.getStatusCode(), Status.BAD_REQUEST.getStatusCode());
    }

    @Test
    public void discoverHypervisorType() throws Exception
    {
        String ip = NodecollectorServiceStubMock.IP_CORRECT_1;

        ClientResponse response =
            get(resolveDatacenterURIActionDiscoverHypervidor(datacenter.getId(), ip));

        String hType = response.getEntity(String.class);
        assertNotNull(hType);
        assertEquals(hType, HypervisorType.VMX_04.getValue());
    }
}
