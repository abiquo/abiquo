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

import static com.abiquo.api.common.UriTestResolver.resolveRemoteServicesURI;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.util.Arrays;
import java.util.List;

import javax.ws.rs.core.MediaType;

import org.apache.wink.client.ClientResponse;
import org.apache.wink.client.Resource;
import org.mortbay.jetty.Server;
import org.testng.annotations.Test;

import com.abiquo.model.enumerator.RemoteServiceType;
import com.abiquo.server.core.infrastructure.RemoteServiceDto;

public class RemoteServicesResourceCheckStatusIT extends AbstractResourcePlusAMIT
{
    private String remoteServicesURI = resolveRemoteServicesURI(1);

    private static Server server;

//    @BeforeClass
//    public static void setupServer() throws Exception
//    {
//        server = new Server(9010);
//
//        Context amCheckContextHandler = new Context(server, "/am/check", Context.SESSIONS);
//
//        ServletHolder amCheckServletHolder = new ServletHolder(new CheckServlet());
//        amCheckContextHandler.addServlet(amCheckServletHolder, "/");
//
//        Context amContextHandler = new Context(server, "/am", Context.SESSIONS);
//
//        Map<String, String> amInitParameters = new HashMap<String, String>();
//        amInitParameters.put("contextConfigLocation", amContextConfigLocation);
//
//        amContextHandler.setInitParams(amInitParameters);
//        amContextHandler.addEventListener(new ContextLoaderListener());
//
//        ServletHolder amServletHolder = new ServletHolder(new CXFServlet());
//        amContextHandler.addServlet(amServletHolder, "/*");
//
//        // VM Test Repository for AM
//        File vmrepo = new File(AbstractResourcePlusAMIT.VM_REPOSITORY_TEST);
//        if (vmrepo != null && vmrepo.exists())
//        {
//            deleteDirectory(vmrepo);
//        }
//        vmrepo.mkdirs();
//
//        server.start();
//    }

//    @AfterClass
//    public static void tearDownServet() throws Exception
//    {
//        if (server != null)
//        {
//            server.stop();
//        }
//
//        File vmrepo = new File(AbstractResourcePlusAMIT.VM_REPOSITORY_TEST);
//        if (vmrepo != null || !vmrepo.exists())
//        {
//            vmrepo.delete();
//        }
//    }
//
    @Override
    protected List<String> data()
    {
        return Arrays.asList("/data/machine-am-infrastructure.xml");
    }

    @Test(enabled = false)
    public void createRemoteService()
    {
        Resource resource = client.resource(remoteServicesURI);

        RemoteServiceDto dto = new RemoteServiceDto();
        dto.setType(RemoteServiceType.APPLIANCE_MANAGER);
        dto.setUri("http://localhost:9010/am");

        ClientResponse response =
            resource.contentType(MediaType.APPLICATION_XML).accept(MediaType.APPLICATION_XML).post(
                dto);

        assertEquals(201, response.getStatusCode());

        RemoteServiceDto entityPost = response.getEntity(RemoteServiceDto.class);
        assertNotNull(entityPost);
        assertEquals(entityPost.getUri(), dto.getUri());
        assertEquals(entityPost.getStatus(), 1);
    }

}
