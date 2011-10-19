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

package com.abiquo.api.resources.config;

import org.apache.wink.client.ClientResponse;
import org.testng.annotations.Test;

import com.abiquo.api.resources.AbstractJpaGeneratorIT;
import com.abiquo.server.core.config.Icon;
import com.abiquo.server.core.config.IconDto;

import static com.abiquo.testng.TestConfig.ALL_INTEGRATION_TESTS;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static com.abiquo.api.common.UriTestResolver.resolveIconURI;

public class IconResourceIT extends AbstractJpaGeneratorIT
{
    private String validURI;

    @Test(groups = {ALL_INTEGRATION_TESTS})
    public void getIcon()
    {
        Icon icon = iconGenerator.createUniqueInstance();
        setup(icon);

        validURI = resolveIconURI(icon.getId());

        ClientResponse response = get(validURI);

        IconDto iconDto = response.getEntity(IconDto.class);
        assertNotNull(iconDto);
        assertEquals(icon.getPath(), iconDto.getPath());
    }

    @Test(groups = {ALL_INTEGRATION_TESTS})
    public void updateIcon()
    {
        Icon icon = iconGenerator.createUniqueInstance();
        setup(icon);

        validURI = resolveIconURI(icon.getId());

        IconDto iconDto = new IconDto();
        iconDto.setId(icon.getId());
        iconDto.setName("newName");
        iconDto.setPath("http://newPath.com/image.jpg");

        ClientResponse response = put(validURI, iconDto);

        response = get(validURI);

        IconDto newiconDto = response.getEntity(IconDto.class);
        assertEquals(newiconDto.getPath(), "http://newPath.com/image.jpg");

    }

}
