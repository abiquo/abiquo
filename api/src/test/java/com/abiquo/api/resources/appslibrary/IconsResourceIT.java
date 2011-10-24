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

import static com.abiquo.api.common.Assert.assertError;
import static com.abiquo.api.common.Assert.assertLinkExist;
import static com.abiquo.api.common.UriTestResolver.resolveIconURI;
import static com.abiquo.api.common.UriTestResolver.resolveIconsURI;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import org.apache.wink.client.ClientResponse;
import org.testng.annotations.Test;

import com.abiquo.api.exceptions.APIError;
import com.abiquo.api.resources.AbstractJpaGeneratorIT;
import com.abiquo.server.core.appslibrary.Icon;
import com.abiquo.server.core.appslibrary.IconDto;
import com.abiquo.server.core.appslibrary.IconsDto;

public class IconsResourceIT extends AbstractJpaGeneratorIT
{
    @Test
    public void addIcon()
    {
        IconDto iconDto = new IconDto();
        iconDto.setName("newName");
        iconDto.setPath("http://newPath.com/newlogo.jpg");

        ClientResponse response = post(resolveIconsURI(), iconDto);
        assertEquals(response.getStatusCode(), 201);

        IconDto result = response.getEntity(IconDto.class);

        assertNotNull(iconDto);
        assertEquals(result.getName(), "newName");
        assertEquals(result.getPath(), "http://newPath.com/newlogo.jpg");
        assertLinkExist(result, resolveIconURI(result.getId()), "edit");
    }

    @Test
    public void addIconRaises409WhenDuplicatedPath()
    {
        IconDto iconDto = new IconDto();
        iconDto.setName("newName");
        iconDto.setPath("http://newPath.com/newlogo.jpg");

        ClientResponse response = post(resolveIconsURI(), iconDto);
        assertEquals(response.getStatusCode(), 201);

        iconDto.setName("Another Icon");
        response = post(resolveIconsURI(), iconDto);
        assertError(response, 409, APIError.ICON_DUPLICATED_PATH);
    }

    @Test
    public void findIconByPath()
    {
        Icon icon = iconGenerator.createUniqueInstance();
        setup(icon);

        String validURI = resolveIconsURI();// + "?path=" + icon.getPath();

        // StringBuilder URI = new StringBuilder(validURI + "?path=" + icon.getPath());

        ClientResponse response = get(validURI);

        IconsDto iconsDto = response.getEntity(IconsDto.class);
        assertNotNull(iconsDto);
        for (IconDto iconDto : iconsDto.getCollection())
        {
            assertEquals(iconDto.getId(), icon.getId());
        }
    }
}
