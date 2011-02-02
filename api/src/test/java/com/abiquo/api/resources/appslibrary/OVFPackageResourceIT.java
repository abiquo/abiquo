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

import static com.abiquo.api.common.UriTestResolver.resolveOVFPackageListURI;
import static com.abiquo.api.common.UriTestResolver.resolveOVFPackageURI;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.util.Collections;
import java.util.List;

import javax.ws.rs.core.MediaType;

import org.apache.wink.client.ClientResponse;
import org.apache.wink.client.ClientWebException;
import org.apache.wink.client.Resource;
import org.apache.wink.client.RestClient;
import org.apache.wink.common.internal.utils.UriHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

import com.abiquo.api.resources.AbstractResourceIT;
import com.abiquo.api.services.appslibrary.OVFPackageService;
import com.abiquo.server.core.appslibrary.OVFPackageDto;
import com.abiquo.server.core.appslibrary.OVFPackageListDto;

public class OVFPackageResourceIT extends AbstractResourceIT
{

    private String validOVFPackage = resolveOVFPackageURI(1, 1);

    @Autowired
    OVFPackageService service;

    @Override
    protected List<String> data()
    {
        // return Collections.singletonList("/data/ovfpackage.xml");
        return Collections.singletonList("/data/ovfpackagelist.xml");
    }

    @Test
    public void getOVFPackage() throws ClientWebException
    {
        OVFPackageDto ovfPackageDto = getValidOVFPackage();
        assertNotNull(ovfPackageDto);
        assertEquals(ovfPackageDto.getDescription(), "ovfPackage_1");
        assertEquals(ovfPackageDto.getDiskFormatTypeUri(), "http://diskFormat");
        assertEquals(ovfPackageDto.getCategoryName(), "category_1");
    }

    private OVFPackageDto getValidOVFPackage()
    {
        RestClient client = new RestClient();
        Resource resource = client.resource(validOVFPackage);

        ClientResponse response = resource.accept(MediaType.APPLICATION_XML).get();
        assertEquals(response.getStatusCode(), 200);
        return response.getEntity(OVFPackageDto.class);
    }

    @Test
    public void modifyOVFPackage() throws ClientWebException
    {
        OVFPackageDto ovfPackageDto = getValidOVFPackage();
        assertNotNull(ovfPackageDto);

        // modifications
        ovfPackageDto.setDescription("new_description");
        // TODO: Add more fields

        Resource resource = client.resource(validOVFPackage);
        ClientResponse response =
            resource.contentType(MediaType.APPLICATION_XML).accept(MediaType.APPLICATION_XML).put(
                ovfPackageDto);

        assertEquals(response.getStatusCode(), 200);

        OVFPackageDto retrievedPackageDto = getValidOVFPackage();
        assertNotNull(retrievedPackageDto);
        assertEquals(retrievedPackageDto.getDescription(), "new_description");
        assertEquals(retrievedPackageDto.getDiskFormatTypeUri(), "http://diskFormat");
    }

    @Test
    public void deleteOVFPackage() throws ClientWebException
    {

        Resource resource = client.resource(validOVFPackage);
        ClientResponse response =
            resource.contentType(MediaType.APPLICATION_XML).accept(MediaType.APPLICATION_XML)
                .delete();
        assertEquals(response.getStatusCode(), 204);

        response = resource.accept(MediaType.APPLICATION_XML).get();
        assertEquals(response.getStatusCode(), 404);

        // Checks if item is removed from list
        resource =
            client.resource(resolveOVFPackageListURI(1, 1)).accept(MediaType.APPLICATION_XML);
        response = resource.get();
        assertEquals(response.getStatusCode(), 200);

        OVFPackageListDto entity = response.getEntity(OVFPackageListDto.class);
        assertNotNull(entity);
        assertNotNull(entity.getOvfPackages());
        assertEquals(entity.getOvfPackages().size(), 2);
    }

    @Test
    public void installOVFPackage()
    {
        String installPackageAction =
            UriHelper.appendPathToBaseUri(resolveOVFPackageURI(1, 1),
                OVFPackageResource.INSTALL_ACTION);

        Resource resource =
            client.resource(installPackageAction).accept(MediaType.APPLICATION_XML).queryParam(
                OVFPackageResource.INSTALL_TARGET_QUERY_PARAM, 1);

        ClientResponse response = resource.post(null);
        assertEquals(201, response.getStatusCode());

    }

}
