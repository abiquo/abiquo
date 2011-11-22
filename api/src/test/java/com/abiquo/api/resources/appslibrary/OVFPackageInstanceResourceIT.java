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

import static com.abiquo.api.common.UriTestResolver.resolveOVFPackageInstanceURI;
import static org.testng.Assert.assertEquals;

import java.util.Collections;
import java.util.List;

import javax.ws.rs.core.MediaType;

import org.apache.wink.client.ClientResponse;
import org.apache.wink.client.Resource;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.abiquo.api.resources.AbstractResourceIT;
import com.abiquo.appliancemanager.transport.OVFPackageInstanceDto;
import com.abiquo.appliancemanager.transport.OVFPackageInstanceStatusDto;
import com.abiquo.appliancemanager.transport.OVFPackageInstanceStatusType;

public class OVFPackageInstanceResourceIT extends AbstractResourceIT
{
    @Override
    protected List<String> data()
    {
        return Collections.singletonList("/data/ovfpackageinstances.xml");
        // return Collections.emptyList();
    }

    private String ovfPackageInstanceURI =
        resolveOVFPackageInstanceURI(1, AMResource.AM_SERVICE_PATH, 1, "test_ovfUrl");

    @Test
    public void getOVFPackagesInstanceStatus() throws Exception
    {
        Resource resource =
            client.resource(ovfPackageInstanceURI).accept(
                "application/application/ovfpackagestatus-xml");
        ClientResponse response = resource.get();

        Assert.assertEquals(response.getStatusCode(), 200);

        OVFPackageInstanceStatusDto entity = response.getEntity(OVFPackageInstanceStatusDto.class);
        Assert.assertNotNull(entity);
        Assert.assertFalse(entity.getOvfPackageStatus() == OVFPackageInstanceStatusType.ERROR);
        Assert.assertTrue(entity.getProgress() > 0);
    }

    @Test
    public void downloadOVFPackagesInstance() throws Exception
    {
        System.out.println("ovfPackageInstanceURI: " + ovfPackageInstanceURI);

        Resource resource =
            client.resource(ovfPackageInstanceURI).accept(
                "application/application/ovfpackageenvelope-xml");
        ClientResponse response = resource.get();

        Assert.assertEquals(response.getStatusCode(), 200);

        String downloadURL = response.getEntity(String.class);
        Assert.assertNotNull(downloadURL);

        // TODO: Download this -> FILE Exists??
        Assert.assertTrue(0 == 1);

    }

    @Test
    public void getOVFPackagesInstanceInfo() throws Exception
    {
        System.out.println("ovfPackageInstanceURI: " + ovfPackageInstanceURI);

        Resource resource =
            client.resource(ovfPackageInstanceURI).accept("application/ovfpackage-octet-stream");
        ClientResponse response = resource.get();

        Assert.assertEquals(response.getStatusCode(), 200);

        OVFPackageInstanceDto entity = response.getEntity(OVFPackageInstanceDto.class);
        Assert.assertNotNull(entity);
        Assert.assertNotNull(entity.getOvfUrl());
        Assert.assertNotNull(entity.getDescription());
    }

    @Test
    public void deleteOVFPackagesInstance() throws Exception
    {
        Resource resource = client.resource(ovfPackageInstanceURI);

        ClientResponse response = resource.accept(MediaType.APPLICATION_XML).delete();
        assertEquals(204, response.getStatusCode());

    }
}
