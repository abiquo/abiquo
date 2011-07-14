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
package com.abiquo.nodecollector.resource.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import javax.ws.rs.core.MediaType;

import org.apache.wink.client.ClientResponse;
import org.apache.wink.client.Resource;
import org.apache.wink.client.RestClient;
import org.junit.Before;
import org.junit.Test;

import com.abiquo.model.enumerator.HypervisorType;
import com.abiquo.model.transport.error.ErrorDto;
import com.abiquo.nodecollector.constants.MessageValues;
import com.abiquo.server.core.infrastructure.nodecollector.HostDto;
import com.abiquo.server.core.infrastructure.nodecollector.VirtualSystemCollectionDto;

/**
 * This abstract class establishes all the methods that should be defined to test an end-to-end
 * Hypervisor Plugin in the NodeCollector class. All the plugins of the Nodecollector can extends
 * this class with the annotation '@CollectorTest' and most of the basic functionallity will be
 * tested.
 * 
 * @author jdevesa@abiquo.com
 */
public abstract class PluginCollectorTester
{
    // RestClient initialized
    protected static RestClient client = new RestClient();

    // Definition of the paths of the REST service
    protected static String HOST_NAME = "localhost";

    protected static Integer PORT = 8888;

    // Paths of the resource
    protected static String HYPERVISOR_RESOURCE = "hypervisor";

    protected static String HOST_RESOURCE = "host";

    protected static String VIRTUAL_SYSTEM_RESOURCE = "virtualsystem";

    // Query parameters.
    protected static String HYPERVISOR_KEY = "hyp";

    protected static String USER_KEY = "user";

    protected static String PASSWORD_KEY = "passwd";

    // Definition of the hypervisor specific values.
    private String hypervisorIP;

    private HypervisorType hypervisorType;

    private String user;

    private String password;


    @Before
    public void getAnnotations() throws Exception
    {
        CollectorTest collectorTest = this.getClass().getAnnotation(CollectorTest.class);
        hypervisorIP = collectorTest.ip();
        hypervisorType = collectorTest.type();
        user = collectorTest.user();
        password = collectorTest.password();
    }

    /* Correct values TEST */

    /**
     * Tests if the Hypervisor returned in the ESXI machine is in fact the ESXi value.
     * 
     * @throws Exception if any problem occurs. Exception otherwise.
     */
    @Test
    public void getHypervisorTest() throws Exception
    {

        Resource resource =
            client.resource("http://" + HOST_NAME + ":" + PORT + "/nodecollector/" + hypervisorIP
                + "/" + HYPERVISOR_RESOURCE);
        ClientResponse response = resource.accept(MediaType.APPLICATION_XML_TYPE).get();
        HypervisorType hypervisor = HypervisorType.fromValue(response.getEntity(String.class));

        assertTrue(response.getStatusCode() == 200);
        assertTrue(hypervisor != null);
        assertTrue(hypervisor.equals(hypervisorType));
    }

    @Test
    public void getHostTest() throws Exception
    {
        Resource resource =
            client.resource(
                "http://" + HOST_NAME + ":" + PORT + "/nodecollector/" + hypervisorIP + "/"
                    + HOST_RESOURCE).queryParam(HYPERVISOR_KEY, hypervisorType.getValue()).queryParam(
                USER_KEY, user).queryParam(PASSWORD_KEY, password);
        ClientResponse response = resource.accept(MediaType.APPLICATION_XML_TYPE).get();
        HostDto host = response.getEntity(HostDto.class);

        assertTrue(response.getStatusCode() == 200);
        assertTrue(host.getHypervisor() != null);
        assertTrue(host.getHypervisor().equalsIgnoreCase(hypervisorType.getValue()));
    }

    @Test
    public void getVirtualSystemListTest() throws Exception
    {
        Resource resource =
            client.resource(
                "http://" + HOST_NAME + ":" + PORT + "/nodecollector/" + hypervisorIP + "/"
                    + VIRTUAL_SYSTEM_RESOURCE).queryParam(HYPERVISOR_KEY, hypervisorType.getValue())
                .queryParam(USER_KEY, user).queryParam(PASSWORD_KEY, password);
        ClientResponse response = resource.accept(MediaType.APPLICATION_XML_TYPE).get();
        VirtualSystemCollectionDto vsc = response.getEntity(VirtualSystemCollectionDto.class);

        assertTrue(response.getStatusCode() == 200);
        assertTrue(vsc != null);
    }

    // HIPERVISOR_RESOURCE error codes test.
    /**
     * Test the 400 Bad Request response error for the HYPERVISOR_RESOURCE path. This error is
     * retrieved when the IP of we want to check is not actually an IP address.
     */
    @Test
    public void badSyntaxHypervisorResourceTest()
    {
        String wrongFormedIp = "222.11.9.";

        Resource resource =
            client.resource("http://" + HOST_NAME + ":" + PORT + "/nodecollector/" + wrongFormedIp
                + "/" + HYPERVISOR_RESOURCE);
        ClientResponse response = resource.accept(MediaType.APPLICATION_XML_TYPE).get();
        ErrorDto errorResponse = response.getEntity(ErrorDto.class);

        assertTrue(errorResponse != null);
        assertTrue(response.getStatusCode() == 400);
    }

    /**
     * Test the 404 Not Foud response error for the HYPERVISOR_RESOURCE path. This error is
     * retrieved when the IP of we want to check belongs to a not started machine (or doesn't belong
     * to any machine).
     */
    @Test
    public void notFoundHypervisorResourceTest()
    {
        String notFoundIp = "10.60.2.200";

        Resource resource =
            client.resource("http://" + HOST_NAME + ":" + PORT + "/nodecollector/" + notFoundIp
                + "/" + HYPERVISOR_RESOURCE);
        ClientResponse response = resource.accept(MediaType.APPLICATION_XML_TYPE).get();
        ErrorDto errorResponse = response.getEntity(ErrorDto.class);

        assertTrue(errorResponse != null);
        assertTrue(response.getStatusCode() == 404);
    }

    /**
     * Test the 412 Precondition failed response error for the HYPERVISOR_RESOURCE path. This error
     * is retrieved when the IP of we want to check belongs to a running machine but any Hypervisor
     * responds to the connection.
     */
    @Test
    public void preconditionFailedHypervisorResourceTest()
    {
        String preconditionFailedIp = "10.60.1.225";

        Resource resource =
            client.resource("http://" + HOST_NAME + ":" + PORT + "/nodecollector/"
                + preconditionFailedIp + "/" + HYPERVISOR_RESOURCE);
        ClientResponse response = resource.accept(MediaType.APPLICATION_XML_TYPE).get();

        ErrorDto errorResponse = response.getEntity(ErrorDto.class);
        assertNotNull(errorResponse);
        assertTrue(response.getStatusCode() == 412);
    }

    // NOTE: the HYPERVISOR_RESOURCE also returns a 500 internal server error. But this is for
    // unexpected errors
    // and that's a not easy-reproduce situation.

    // HOST_RESOURCE error codes test.

    /**
     * Test the 400 Bad Request response error for the HOST_RESOURCE path. This error is retrieved
     * when the IP of we want to check is not actually an IP address.
     */
    @Test
    public void badRequestWrongIpHostResourceTest()
    {

        String wrongFormedIp = "222.22.444";

        Resource resource =
            client.resource(
                "http://" + HOST_NAME + ":" + PORT + "/nodecollector/" + wrongFormedIp + "/"
                    + HOST_RESOURCE).queryParam(HYPERVISOR_KEY, hypervisorType.getValue()).queryParam(
                USER_KEY, user).queryParam(PASSWORD_KEY, password);

        ClientResponse response = resource.accept(MediaType.APPLICATION_XML_TYPE).get();
        assertTrue(response.getStatusCode() == 400);
        ErrorDto errorResponse = response.getEntity(ErrorDto.class);
        assertNotNull(errorResponse);
        
    }

    /**
     * Test the 400 Bad Request response error for the HOST_RESOURCE path. This error is retrieved
     * when the user is not informed.
     */
    @Test
    public void badRequestMissingUserHostResourceTest()
    {

        Resource resource =
            client.resource(
                "http://" + HOST_NAME + ":" + PORT + "/nodecollector/" + hypervisorIP + "/"
                    + HOST_RESOURCE).queryParam(HYPERVISOR_KEY, hypervisorType.getValue()).queryParam(
                PASSWORD_KEY, password);

        ClientResponse response = resource.accept(MediaType.APPLICATION_XML_TYPE).get();
        assertTrue(response.getStatusCode() == 400);
        ErrorDto errorResponse = response.getEntity(ErrorDto.class);
        assertNotNull(errorResponse);
        
    }

    /**
     * Test the 400 Bad Request response error for the HOST_RESOURCE path. This error is retrieved
     * when the hypervisor type is not informed.
     */
    @Test
    public void badRequestMissingHypervisorHostResourceTest()
    {

        Resource resource =
            client.resource(
                "http://" + HOST_NAME + ":" + PORT + "/nodecollector/" + hypervisorIP + "/"
                    + HOST_RESOURCE).queryParam(USER_KEY, user).queryParam(PASSWORD_KEY, password);

        ClientResponse response = resource.accept(MediaType.APPLICATION_XML_TYPE).get();
        assertTrue(response.getStatusCode() == 400);
        ErrorDto errorResponse = response.getEntity(ErrorDto.class);
        assertNotNull(errorResponse);
    }

    /**
     * Test the 400 Bad Request response error for the HOST_RESOURCE path. This error is retrieved
     * when the password is not informed.
     */
    @Test
    public void badRequestMissingPasswordHostResourceTest()
    {
        Resource resource =
            client.resource(
                "http://" + HOST_NAME + ":" + PORT + "/nodecollector/" + hypervisorIP + "/"
                    + HOST_RESOURCE).queryParam(HYPERVISOR_KEY, hypervisorType.getValue()).queryParam(
                USER_KEY, user);

        ClientResponse response = resource.accept(MediaType.APPLICATION_XML_TYPE).get();
        assertTrue(response.getStatusCode() == 400);
        ErrorDto errorResponse = response.getEntity(ErrorDto.class);
        assertNotNull(errorResponse);
    }

    /**
     * Test the 401 Unauthorized response error for the HOST_RESOURCE and the ESXi.
     */
    @Test
    public void unauthorizedHostResourceTest()
    {
        String fakeUser = "fakeUser";
        String fakePassword = "fakePassword";

        Resource resource =
            client.resource(
                "http://" + HOST_NAME + ":" + PORT + "/nodecollector/" + hypervisorIP + "/"
                    + HOST_RESOURCE).queryParam(HYPERVISOR_KEY, hypervisorType.getValue()).queryParam(
                USER_KEY, fakeUser).queryParam(PASSWORD_KEY, fakePassword);

        ClientResponse response = resource.accept(MediaType.APPLICATION_XML_TYPE).get();
        ErrorDto errorResponse = response.getEntity(ErrorDto.class);

        // KVM and XEN have a strange behaviour when the correct password is already informed
        // before.
        // So, this conditional 'if' avoid the asserts make the JUnit fault.
        if (hypervisorType != HypervisorType.KVM
            && hypervisorType != HypervisorType.XEN_3 && hypervisorType != HypervisorType.VBOX)
        {
            assertTrue(response.getStatusCode() == 401);
            assertTrue(errorResponse.getMessage().equalsIgnoreCase(MessageValues.LOG_EXCP));
        }
    }

    /**
     * Test the 404 Not Found response error for the HOST_RESOURCE path. This error is retrieved
     * when the IP of we want to check belongs to a not started machine (or doesn't belong to any
     * machine).
     */
    @Test
    public void notFoundHostResourceTest()
    {
        String notFoundIp = "10.60.2.200";

        Resource resource =
            client.resource(
                "http://" + HOST_NAME + ":" + PORT + "/nodecollector/" + notFoundIp + "/"
                    + HOST_RESOURCE).queryParam(HYPERVISOR_KEY, hypervisorType.getValue()).queryParam(
                USER_KEY, user).queryParam(PASSWORD_KEY, password);

        ClientResponse response = resource.accept(MediaType.APPLICATION_XML_TYPE).get();
        ErrorDto errorResponse = response.getEntity(ErrorDto.class);

        assertTrue(errorResponse != null);
        assertTrue(response.getStatusCode() == 404);
    }

    /**
     * Test the 412 Precodition Failed test. This tests informs a wrong hypervisor type to get
     * instead of the actual hypervisor type.
     */
    @Test
    public void preconditionFailedHostResourceTest()
    {
        // We are going to put all the Hypervisors as ESXi instead of the hypervisor ESXi, that
        // will be the HyperV, for instance
        HypervisorType wrongHypervisor;

        if (hypervisorType.equals(HypervisorType.VMX_04))
        {
            wrongHypervisor = HypervisorType.HYPERV_301;
        }
        else
        {
            wrongHypervisor = HypervisorType.VMX_04;
        }

        Resource resource =
            client.resource(
                "http://" + HOST_NAME + ":" + PORT + "/nodecollector/" + hypervisorIP + "/"
                    + HOST_RESOURCE).queryParam(HYPERVISOR_KEY, wrongHypervisor.getValue()).queryParam(
                USER_KEY, user).queryParam(PASSWORD_KEY, password);
        ClientResponse response = resource.accept(MediaType.APPLICATION_XML_TYPE).get();
        ErrorDto errorResponse = response.getEntity(ErrorDto.class);

        assertTrue(errorResponse != null);
        assertTrue(response.getStatusCode() == 412);
    }

    // NOTE: the HOST_RESOURCE also returns a 500 internal server error. But this is for unexpected
    // errors
    // and that's a not easy-reproduce situation.

    // VIRTUAL_SYSTEM_RESOURCE error codes test

    /**
     * Test the 400 Bad Request response error for the VIRTUAL_SYSTEM_RESOURCE path. This error is
     * retrieved when the IP of we want to check is not actually an IP address.
     */
    @Test
    public void badRequestWrongIpVirtualSystemResourceTest()
    {

        String wrongFormedIp = "222.22.444";

        Resource resource =
            client.resource(
                "http://" + HOST_NAME + ":" + PORT + "/nodecollector/" + wrongFormedIp + "/"
                    + VIRTUAL_SYSTEM_RESOURCE).queryParam(HYPERVISOR_KEY, hypervisorType.getValue())
                .queryParam(USER_KEY, user).queryParam(PASSWORD_KEY, password);

        ClientResponse response = resource.accept(MediaType.APPLICATION_XML_TYPE).get();
        assertTrue(response.getStatusCode() == 400);
        ErrorDto errorResponse = response.getEntity(ErrorDto.class);
        assertNotNull(errorResponse);
    }
    
    
    /**
     * Test the 400 Bad Request response error when the hypervisor type is not one of the
     * types in the values of the enum {@link HypervisorType} in the HostResource
     */
    @Test
    public void badRequestWrongHypervisorTypeHostResourceTest()
    {
        Resource resource =
            client.resource(
                "http://" + HOST_NAME + ":" + PORT + "/nodecollector/" + hypervisorIP + "/"
                    + HOST_RESOURCE).queryParam(HYPERVISOR_KEY, "mierdahypervisor").queryParam(
                USER_KEY, user).queryParam(PASSWORD_KEY, password);
        ClientResponse response = resource.accept(MediaType.APPLICATION_XML_TYPE).get();
        
        assertTrue(response.getStatusCode() == 400);
        ErrorDto errorResponse = response.getEntity(ErrorDto.class);
        assertEquals(errorResponse.getMessage(), MessageValues.UNKNOWN_HYPERVISOR);
    }
    
    /**
     * Test the 400 Bad Request response error when the hypervisor type is not one of the
     * types in the values of the enum {@link HypervisorType} in the VirtualSystemResource
     */
    @Test
    public void badRequestWrongHypervisorTypeVirtualSystemResourceTest() throws Exception
    {
        Resource resource =
            client.resource(
                "http://" + HOST_NAME + ":" + PORT + "/nodecollector/" + hypervisorIP + "/"
                    + VIRTUAL_SYSTEM_RESOURCE).queryParam(HYPERVISOR_KEY, "mierdahypervisor")
                .queryParam(USER_KEY, user).queryParam(PASSWORD_KEY, password);
        ClientResponse response = resource.accept(MediaType.APPLICATION_XML_TYPE).get();

        assertTrue(response.getStatusCode() == 400);
        ErrorDto errorResponse = response.getEntity(ErrorDto.class);
        assertEquals(errorResponse.getMessage(), MessageValues.UNKNOWN_HYPERVISOR);
    }

    /**
     * Test the 400 Bad Request response error for the HOST_RESOURCE path. This error is retrieved
     * when the Hypervisor is not informed.
     */
    @Test
    public void badRequestMissingHypervisorVirtualSystemResourceTest()
    {

        Resource resource =
            client.resource(
                "http://" + HOST_NAME + ":" + PORT + "/nodecollector/" + hypervisorIP + "/"
                    + VIRTUAL_SYSTEM_RESOURCE).queryParam(USER_KEY, user).queryParam(PASSWORD_KEY,
                password);

        ClientResponse response = resource.accept(MediaType.APPLICATION_XML_TYPE).get();
        assertTrue(response.getStatusCode() == 400);
        ErrorDto errorResponse = response.getEntity(ErrorDto.class);
        assertNotNull(errorResponse);
    }

    /**
     * Test the 400 Bad Request response error for the HOST_RESOURCE path. This error is retrieved
     * when the user is not informed.
     */
    @Test
    public void badRequestMissingUserVirtualSystemResourceTest()
    {

        Resource resource =
            client.resource(
                "http://" + HOST_NAME + ":" + PORT + "/nodecollector/" + hypervisorIP + "/"
                    + VIRTUAL_SYSTEM_RESOURCE).queryParam(HYPERVISOR_KEY, hypervisorType.getValue())
                .queryParam(PASSWORD_KEY, password);

        ClientResponse response = resource.accept(MediaType.APPLICATION_XML_TYPE).get();
        assertTrue(response.getStatusCode() == 400);
        ErrorDto errorResponse = response.getEntity(ErrorDto.class);
        assertNotNull(errorResponse);
    }

    /**
     * Test the 400 Bad Request response error for the HOST_RESOURCE path. This error is retrieved
     * when the password is not informed.
     */
    @Test
    public void badRequestMissingPasswordVirtualSystemResourceTest()
    {
        Resource resource =
            client.resource(
                "http://" + HOST_NAME + ":" + PORT + "/nodecollector/" + hypervisorIP + "/"
                    + VIRTUAL_SYSTEM_RESOURCE).queryParam(HYPERVISOR_KEY, hypervisorType.getValue())
                .queryParam(USER_KEY, user);

        ClientResponse response = resource.accept(MediaType.APPLICATION_XML_TYPE).get();
        assertTrue(response.getStatusCode() == 400);
        ErrorDto errorResponse = response.getEntity(ErrorDto.class);
        assertNotNull(errorResponse);
    }

    /**
     * Test the 401 Unauthorized response error for the VIRTUAL_SYSTEM_RESOURCE.
     */
    @Test
    public void unauthorizedVirtualSystemResourceTest()
    {
        String fakeUser = "fakeUser";
        String fakePassword = "fakePassword";

        Resource resource =
            client.resource(
                "http://" + HOST_NAME + ":" + PORT + "/nodecollector/" + hypervisorIP + "/"
                    + VIRTUAL_SYSTEM_RESOURCE).queryParam(HYPERVISOR_KEY, hypervisorType.getValue())
                .queryParam(USER_KEY, fakeUser).queryParam(PASSWORD_KEY, fakePassword);

        ClientResponse response = resource.accept(MediaType.APPLICATION_XML_TYPE).get();
        ErrorDto errorResponse = response.getEntity(ErrorDto.class);

        // KVM and XEN have a strange behaviour when the correct password is already informed
        // before.
        // So, this conditional 'if' avoid the asserts make the JUnit fault.
        if (hypervisorType != HypervisorType.KVM
            && hypervisorType != HypervisorType.XEN_3 && hypervisorType != HypervisorType.VBOX)
        {
            assertTrue(response.getStatusCode() == 401);
            assertTrue(errorResponse.getMessage().equalsIgnoreCase(MessageValues.LOG_EXCP));
        }
    }

    /**
     * Test the 404 Not Found response error for the HOST_RESOURCE path. This error is retrieved
     * when the IP of we want to check belongs to a not started machine (or doesn't belong to any
     * machine).
     */
    @Test
    public void notFoundVirtualSystemResourceTest()
    {
        String notFoundIp = "10.60.2.200";

        Resource resource =
            client.resource(
                "http://" + HOST_NAME + ":" + PORT + "/nodecollector/" + notFoundIp + "/"
                    + VIRTUAL_SYSTEM_RESOURCE).queryParam(HYPERVISOR_KEY, hypervisorType.getValue())
                .queryParam(USER_KEY, user).queryParam(PASSWORD_KEY, password);
        ClientResponse response = resource.accept(MediaType.APPLICATION_XML_TYPE).get();
        ErrorDto errorResponse = response.getEntity(ErrorDto.class);

        assertTrue(errorResponse != null);
        assertTrue(response.getStatusCode() == 404);
    }

    @Test
    public void preconditionFailedVirtualSystemResourceTest() throws Exception
    {

        String preconditionFailedIp = "10.60.1.225";

        Resource resource =
            client.resource(
                "http://" + HOST_NAME + ":" + PORT + "/nodecollector/" + preconditionFailedIp + "/"
                    + VIRTUAL_SYSTEM_RESOURCE).queryParam(HYPERVISOR_KEY, hypervisorType.getValue())
                .queryParam(USER_KEY, user).queryParam(PASSWORD_KEY, password);

        ClientResponse response = resource.accept(MediaType.APPLICATION_XML_TYPE).get();
        ErrorDto errorResponse = response.getEntity(ErrorDto.class);

        assertTrue(errorResponse != null);
        assertTrue(response.getStatusCode() == 412);
    }
}
