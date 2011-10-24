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
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import org.apache.wink.client.ClientResponse;
import org.testng.annotations.Test;

import com.abiquo.api.exceptions.APIError;
import com.abiquo.api.resources.AbstractJpaGeneratorIT;
import com.abiquo.server.core.appslibrary.Icon;
import com.abiquo.server.core.appslibrary.IconDto;

public class IconResourceIT extends AbstractJpaGeneratorIT
{
    @Test
    public void getIcon()
    {
        Icon icon = iconGenerator.createUniqueInstance();
        setup(icon);

        String validURI = resolveIconURI(icon.getId());

        ClientResponse response = get(validURI);
        assertEquals(response.getStatusCode(), 200);

        IconDto iconDto = response.getEntity(IconDto.class);

        assertNotNull(iconDto);
        assertEquals(icon.getName(), iconDto.getName());
        assertEquals(icon.getPath(), iconDto.getPath());
        assertLinkExist(iconDto, validURI, "edit");
    }

    @Test
    public void getUnexistingIcon()
    {
        ClientResponse response = get(resolveIconURI(12345));
        assertError(response, 404, APIError.NON_EXISENT_ICON);
    }

    @Test
    public void updateIcon()
    {
        Icon icon = iconGenerator.createUniqueInstance();
        setup(icon);

        String validURI = resolveIconURI(icon.getId());

        IconDto iconDto = new IconDto();
        iconDto.setId(icon.getId());
        iconDto.setName("newName");
        iconDto.setPath("http://newPath.com/image.jpg");

        ClientResponse response = put(validURI, iconDto);
        assertEquals(response.getStatusCode(), 200);

        IconDto newiconDto = response.getEntity(IconDto.class);
        assertEquals(newiconDto.getName(), "newName");
        assertEquals(newiconDto.getPath(), "http://newPath.com/image.jpg");
    }

    @Test
    public void deleteIcon()
    {
        Icon icon = iconGenerator.createUniqueInstance();
        setup(icon);

        String validURI = resolveIconURI(icon.getId());
        ClientResponse response = delete(validURI);
        assertEquals(response.getStatusCode(), 204);

        response = get(validURI);
        assertError(response, 404, APIError.NON_EXISENT_ICON);
    }

}
