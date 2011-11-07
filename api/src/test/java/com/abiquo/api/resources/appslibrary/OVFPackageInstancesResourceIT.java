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

package com.abiquo.api.resources.appslibrary;

import static com.abiquo.api.common.UriTestResolver.resolveOVFPackageInstancesURI;

import java.util.Collections;
import java.util.List;

import javax.ws.rs.core.MediaType;

import org.apache.wink.client.ClientResponse;
import org.apache.wink.client.Resource;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.abiquo.api.resources.AbstractResourceIT;
import com.abiquo.appliancemanager.transport.OVFPackageInstanceStatusDto;
import com.abiquo.appliancemanager.transport.OVFPackageInstanceStatusType;
import com.abiquo.appliancemanager.transport.OVFPackageInstancesDto;

public class OVFPackageInstancesResourceIT extends AbstractResourceIT
{
    @Override
    protected List<String> data()
    {
        return Collections.singletonList("/data/ovfpackageinstances.xml");
        // return Collections.emptyList();
    }

    private String ovfPackageInstancesURI = resolveOVFPackageInstancesURI(1,
        AMResource.AM_SERVICE_TYPE, 1);

    @Test(enabled = false)
    public void getOVFPackageInstances() throws Exception
    {
        Resource resource =
            client.resource(ovfPackageInstancesURI).accept(MediaType.APPLICATION_XML);
        ClientResponse response = resource.get();

        Assert.assertEquals(response.getStatusCode(), 200);

        OVFPackageInstancesDto entity = response.getEntity(OVFPackageInstancesDto.class);
        Assert.assertNotNull(entity);
        Assert.assertNotNull(entity.getCollection());
        Assert.assertEquals(entity.getCollection().size(), 3);

        // Resource resource;
        // ClientResponse response;
        // ovfPackageInstancesURI = "http://localhost:9009/api/admin/datacenters/1/remoteServices";
        // resource = client.resource(ovfPackageInstancesURI).accept(MediaType.APPLICATION_XML);
        // System.out.println("ovfPackageInstancesURI: " + ovfPackageInstancesURI + " StatusCode: "
        // + resource.get().getStatusCode());
        //
        // ovfPackageInstancesURI =
        // "http://localhost:9009/api/admin/datacenters/1/remoteServices/appliance_manager";
        // resource = client.resource(ovfPackageInstancesURI).accept(MediaType.APPLICATION_XML);
        // System.out.println("ovfPackageInstancesURI: " + ovfPackageInstancesURI + " StatusCode: "
        // + resource.get().getStatusCode());
        //
        // ovfPackageInstancesURI =
        // "http://localhost:9009/api/admin/datacenters/1/remoteServices/appliance_manager/er";
        // resource = client.resource(ovfPackageInstancesURI).accept(MediaType.APPLICATION_XML);
        // System.out.println("ovfPackageInstancesURI: " + ovfPackageInstancesURI + " StatusCode: "
        // + resource.get().getStatusCode());
        //
        // ovfPackageInstancesURI =
        // "http://localhost:9009/api/admin/datacenters/1/remoteServices/appliance_manager/er/1";
        // resource = client.resource(ovfPackageInstancesURI).accept(MediaType.APPLICATION_XML);
        // System.out.println("ovfPackageInstancesURI: " + ovfPackageInstancesURI + " StatusCode: "
        // + resource.get().getStatusCode());
        //
        // ovfPackageInstancesURI =
        // "http://localhost:9009/api/admin/datacenters/1/remoteServices/appliance_manager/er/1/ovfPackageInstances";
        // resource = client.resource(ovfPackageInstancesURI).accept(MediaType.APPLICATION_XML);
        // System.out.println("ovfPackageInstancesURI: " + ovfPackageInstancesURI + " StatusCode: "
        // + resource.get().getStatusCode());

    }

    @Test
    public void createOVFPackageInstance()
    {
        Resource resource = client.resource(ovfPackageInstancesURI);

        String ovfUrl = "test_ovfurl";

        ClientResponse response =
            resource.contentType(MediaType.APPLICATION_XML).accept(MediaType.APPLICATION_XML)
                .post(ovfUrl);

        Assert.assertEquals(response.getStatusCode(), 201);

        OVFPackageInstanceStatusDto entityPost =
            response.getEntity(OVFPackageInstanceStatusDto.class);
        Assert.assertNotNull(entityPost);
        Assert
            .assertFalse(entityPost.getOvfPackageStatus() == OVFPackageInstanceStatusType.DOWNLOADING
                || entityPost.getOvfPackageStatus() == OVFPackageInstanceStatusType.DOWNLOAD);
        Assert.assertTrue(entityPost.getProgress() > 0);
    }
}
