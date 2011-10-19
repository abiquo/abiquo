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

import static com.abiquo.api.common.UriTestResolver.resolveIconsURI;
import static com.abiquo.testng.TestConfig.ALL_INTEGRATION_TESTS;
import static com.abiquo.testng.TestConfig.BASIC_INTEGRATION_TESTS;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import org.apache.wink.client.ClientResponse;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import com.abiquo.api.resources.AbstractJpaGeneratorIT;
import com.abiquo.server.core.config.Icon;
import com.abiquo.server.core.config.IconDto;
import com.abiquo.server.core.config.IconsDto;

public class IconsResourceIT extends AbstractJpaGeneratorIT
{

    private String validURI;

    @Test(groups = {ALL_INTEGRATION_TESTS})
    public void addIcon()
    {
        IconDto iconDto = new IconDto();
        iconDto.setName("newName");
        iconDto.setPath("http://newPath.com/newlogo.jpg");

        ClientResponse response = post(resolveIconsURI(), iconDto);

        IconDto result = response.getEntity(IconDto.class);
        assertEquals(result.getPath(), "http://newPath.com/newlogo.jpg");
    }

    @Test(groups = {BASIC_INTEGRATION_TESTS})
    public void findIconByPath()
    {
        Icon icon = iconGenerator.createUniqueInstance();
        setup(icon);

        validURI = resolveIconsURI();// + "?path=" + icon.getPath();

        // StringBuilder URI = new StringBuilder(validURI + "?path=" + icon.getPath());

        ClientResponse response = get(validURI);

        IconsDto iconsDto = response.getEntity(IconsDto.class);
        assertNotNull(iconsDto);
        for (IconDto iconDto : iconsDto.getCollection())
        {
            assertEquals(iconDto.getId(), icon.getId());
        }
    }

    @AfterMethod
    @Test(groups = {BASIC_INTEGRATION_TESTS})
    public void tearDown()
    {
        super.tearDown();
    }
}
