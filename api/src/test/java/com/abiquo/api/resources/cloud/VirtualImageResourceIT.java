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
import static com.abiquo.api.common.UriTestResolver.resolveCategoryURI;
import static com.abiquo.api.common.UriTestResolver.resolveIconURI;
import static com.abiquo.api.common.UriTestResolver.resolveVirtualImageURI;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.util.HashMap;
import java.util.Map;

import org.apache.wink.client.ClientResponse;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.abiquo.api.resources.AbstractJpaGeneratorIT;
import com.abiquo.api.resources.CategoryResource;
import com.abiquo.api.resources.config.IconResource;
import com.abiquo.appliancemanager.util.URIResolver;
import com.abiquo.model.enumerator.RemoteServiceType;
import com.abiquo.server.core.cloud.VirtualImage;
import com.abiquo.server.core.cloud.VirtualImageDto;
import com.abiquo.server.core.enterprise.Enterprise;
import com.abiquo.server.core.infrastructure.Datacenter;
import com.abiquo.server.core.infrastructure.RemoteService;

@Test
public class VirtualImageResourceIT extends AbstractJpaGeneratorIT
{
    private final static String AM_BASE_URI = "http://localhost:"
        + String.valueOf(getEmbededServerPort()) + "/am";

    private Enterprise ent;

    private Datacenter datacenter;

    @BeforeMethod
    public void setUpDatacenterRepository()
    {
        ent = enterpriseGenerator.createUniqueInstance();
        datacenter = datacenterGenerator.createUniqueInstance();
        RemoteService am =
            remoteServiceGenerator.createInstance(RemoteServiceType.APPLIANCE_MANAGER, datacenter);
        am.setUri(AM_BASE_URI);

        setup(ent, datacenter, am);
    }

    @Test
    public void getVirtualImage()
    {
        VirtualImage virtualImage = virtualImageGenerator.createInstance(ent, datacenter);
        setup(virtualImage.getCategory(), virtualImage.getRepository(), virtualImage);
        assertNotNull(virtualImage.getId());

        String uri = resolveVirtualImageURI(ent.getId(), datacenter.getId(), virtualImage.getId());
        ClientResponse response = get(uri);
        assertEquals(response.getStatusCode(), 200);

        VirtualImageDto dto = response.getEntity(VirtualImageDto.class);
        assertVirtualImageWithLinks(virtualImage, dto);
    }

    private void assertVirtualImageWithLinks(final VirtualImage vi, final VirtualImageDto dto)
    {
        // Required fields
        assertNotNull(dto.getId());
        assertNotNull(dto.getName());
        assertNotNull(dto.getPathName());
        assertNotNull(dto.getDiskFormatType());
        assertNotNull(dto.getDiskFileSize());

        // Required links
        String edit = resolveVirtualImageURI(ent.getId(), datacenter.getId(), vi.getId());
        String category = resolveCategoryURI(vi.getCategory().getId());
        assertLinkExist(dto, edit, "edit");
        assertLinkExist(dto, category, CategoryResource.CATEGORY);

        // Optional links
        if (vi.getIcon() != null)
        {
            String icon = resolveIconURI(vi.getIcon().getId());
            assertLinkExist(dto, icon, IconResource.ICON);
        }

        if (vi.getMaster() != null)
        {
            String master =
                resolveVirtualImageURI(vi.getMaster().getEnterprise().getId(), datacenter.getId(),
                    vi.getMaster().getId());
            assertLinkExist(dto, master, "master");
        }

        if (vi.getOvfid() != null)
        {
            String amHref = amOVFPackageInstanceUrl(ent.getId(), vi.getOvfid());

            assertLinkExist(dto, vi.getOvfid(), "ovfpackage");
            assertLinkExist(dto, amHref, "ovfpackageinstance");
            assertLinkExist(dto, amHref + "?format=status", "ovfpackagestatus");
            assertLinkExist(dto, amHref + "?format=envelope", "ovfdocument");
            assertLinkExist(dto, amHref + "?format=diskFile", "imagefile");
        }
    }

    private static String amOVFPackageInstanceUrl(final Integer enterpriseId, final String ovf)
    {
        Map<String, String> params = new HashMap<String, String>();
        params.put("erepo", enterpriseId.toString());
        params.put("ovf", ovf);

        // Must use the URI resolver in the AM in order to encode the ovf parameter
        return URIResolver.resolveURI(AM_BASE_URI, "erepos/{erepo}/ovfs/{ovf}", params);
    }

}
