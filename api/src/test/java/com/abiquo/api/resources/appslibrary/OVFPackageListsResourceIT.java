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

import static com.abiquo.api.common.UriTestResolver.resolveOVFPackageListsURI;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.MediaType;

import org.apache.wink.client.ClientResponse;
import org.apache.wink.client.Resource;
import org.testng.annotations.Test;

import com.abiquo.api.resources.AbstractJpaGeneratorIT;
import com.abiquo.server.core.appslibrary.OVFPackageDto;
import com.abiquo.server.core.appslibrary.OVFPackageListDto;
import com.abiquo.server.core.appslibrary.OVFPackageListsDto;

public class OVFPackageListsResourceIT extends AbstractJpaGeneratorIT
{

    private String ovfPackageListsURI = resolveOVFPackageListsURI(1);

    @Test(enabled = false)
    public void getOVFPackagesListsByEnterprise() throws Exception
    {
        Resource resource = client.resource(ovfPackageListsURI).accept(MediaType.APPLICATION_XML);

        ClientResponse response = resource.get();
        assertEquals(response.getStatusCode(), 200);

        OVFPackageListsDto entity = response.getEntity(OVFPackageListsDto.class);
        assertNotNull(entity);
        assertNotNull(entity.getCollection());
        assertEquals(entity.getCollection().size(), 3);
    }

    @Test(enabled = false)
    public void createEmptyOVFPackageList()
    {
        Resource resource = client.resource(ovfPackageListsURI);

        OVFPackageListDto packageList = new OVFPackageListDto();
        packageList.setName("created_name");

        ClientResponse response =
            resource.contentType(MediaType.APPLICATION_XML).accept(MediaType.APPLICATION_XML).post(
                packageList);

        assertEquals(response.getStatusCode(), 201);

        OVFPackageListDto entityPost = response.getEntity(OVFPackageListDto.class);
        assertNotNull(entityPost);
        assertEquals(packageList.getName(), entityPost.getName());
    }

    @Test(enabled = false)
    public void createOVFPackageList()
    {
        Resource resource = client.resource(ovfPackageListsURI);

        OVFPackageListDto packageList = new OVFPackageListDto();
        packageList.setName("created_name");

        List<OVFPackageDto> packages = new ArrayList<OVFPackageDto>();
        OVFPackageDto p = new OVFPackageDto();
        p.setDescription("test_created_desc");
        p.setDiskFormatTypeUri("http://diskFormat"); // TODO: test this is a necessary field
        p.setName("category_1"); // TODO: test this is a necessary field
        packages.add(p);
        packageList.setOvfPackages(packages);

        ClientResponse response =
            resource.contentType(MediaType.APPLICATION_XML).accept(MediaType.APPLICATION_XML).post(
                packageList);

        assertEquals(response.getStatusCode(), 201);

        OVFPackageListDto entityPost = response.getEntity(OVFPackageListDto.class);
        assertNotNull(entityPost);
        assertEquals(packageList.getName(), entityPost.getName());
        assertEquals(entityPost.getOvfPackages().size(), 1);
    }

}
