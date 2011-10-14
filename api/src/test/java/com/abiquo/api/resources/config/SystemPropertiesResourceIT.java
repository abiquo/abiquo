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

import static com.abiquo.api.common.Assert.assertNotEmpty;
import static com.abiquo.api.common.Assert.assertSize;
import static com.abiquo.api.common.UriTestResolver.resolveSystemPropertiesURI;
import static com.abiquo.api.common.UriTestResolver.resolveSystemPropertiesURIByComponent;
import static com.abiquo.api.common.UriTestResolver.resolveSystemPropertiesURIByName;
import static com.abiquo.testng.TestConfig.BASIC_INTEGRATION_TESTS;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import org.apache.wink.client.ClientResponse;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import com.abiquo.api.resources.AbstractJpaGeneratorIT;
import com.abiquo.server.core.config.SystemPropertiesDto;
import com.abiquo.server.core.config.SystemProperty;
import com.abiquo.server.core.config.SystemPropertyDto;

public class SystemPropertiesResourceIT extends AbstractJpaGeneratorIT
{
    @Override
    @AfterMethod
    public void tearDown()
    {
        tearDown("user", "role", "enterprise", "system_properties");
    }

    @Test
    public void test_getSystemProperties() throws Exception
    {
        SystemProperty property0 = systemPropertyGenerator.createUniqueInstance();
        SystemProperty property1 = systemPropertyGenerator.createUniqueInstance();

        setup(property0, property1);

        ClientResponse response = get(resolveSystemPropertiesURI());

        assertEquals(response.getStatusCode(), 200);
        assertNotEmpty(response.getEntity(SystemPropertiesDto.class).getCollection());
        assertEquals(response.getEntity(SystemPropertiesDto.class).getCollection().size(), 2);
    }

    @Test
    public void test_getSystemPropertyByName() throws Exception
    {
        SystemProperty property = systemPropertyGenerator.createUniqueInstance();
        property.setName("test.property");

        setup(property);

        ClientResponse response = get(resolveSystemPropertiesURIByName("test.property"));

        assertEquals(response.getStatusCode(), 200);

        assertNotEmpty(response.getEntity(SystemPropertiesDto.class).getCollection());
        assertSize(response.getEntity(SystemPropertiesDto.class).getCollection(), 1);
        assertEquals(
            response.getEntity(SystemPropertiesDto.class).getCollection().get(0).getName(),
            "test.property");
    }

    @Test
    public void test_getSystemPropertiesByComponent() throws Exception
    {
        SystemProperty property = systemPropertyGenerator.createUniqueInstance();
        property.setName("test.property");

        setup(property);

        ClientResponse response = get(resolveSystemPropertiesURIByComponent("test"));

        assertEquals(response.getStatusCode(), 200);

        assertNotEmpty(response.getEntity(SystemPropertiesDto.class).getCollection());
        assertSize(response.getEntity(SystemPropertiesDto.class).getCollection(), 1);
        assertEquals(
            response.getEntity(SystemPropertiesDto.class).getCollection().get(0).getName(),
            "test.property");
    }

    @Test(enabled = false)
    // API does not allow to create System Properties
    public void test_addSystemProperty() throws Exception
    {
        SystemPropertyDto property = new SystemPropertyDto();
        property.setName("test.property");
        property.setValue("test property value");

        ClientResponse response = post(resolveSystemPropertiesURI(), property);

        assertEquals(response.getStatusCode(), 201);

        SystemPropertyDto entityPost = response.getEntity(SystemPropertyDto.class);
        assertNotNull(entityPost);
        assertEquals(property.getName(), entityPost.getName());
    }

    @Test
    public void test_modifySystemProperties() throws Exception
    {
        SystemProperty p1 = systemPropertyGenerator.createUniqueInstance();
        SystemProperty p2 = systemPropertyGenerator.createUniqueInstance();
        SystemProperty p3 = systemPropertyGenerator.createUniqueInstance();

        setup(p1, p2, p3);

        SystemPropertiesDto entityPut = new SystemPropertiesDto();
        SystemPropertyDto dto1 = new SystemPropertyDto();
        SystemPropertyDto dto2 = new SystemPropertyDto();
        dto1.setName("test1.property");
        dto1.setValue("test1 property value");
        dto2.setName("test2.property");
        dto2.setValue("test2 property value");
        entityPut.add(dto1);
        entityPut.add(dto2);

        // Perform the put and verify return code
        ClientResponse response = put(resolveSystemPropertiesURI(), entityPut);
        assertEquals(response.getStatusCode(), 200);

        // Perform a get operation and verify the returned list size
        response = get(resolveSystemPropertiesURI());

        assertEquals(response.getStatusCode(), 200);
        assertNotEmpty(response.getEntity(SystemPropertiesDto.class).getCollection());
        assertSize(response.getEntity(SystemPropertiesDto.class).getCollection(), 2);
    }

    @Test
    public void test_modifySystemPropertiesByComponent() throws Exception
    {
        SystemProperty p1 = systemPropertyGenerator.createUniqueInstance();
        SystemProperty p2 = systemPropertyGenerator.createUniqueInstance();
        SystemProperty p3 = systemPropertyGenerator.createUniqueInstance();
        p1.setName("server.property1");
        p2.setName("server.property2");
        p3.setName("vsm.property1");

        setup(p1, p2, p3);

        SystemPropertiesDto entityPut = new SystemPropertiesDto();
        SystemPropertyDto dto = new SystemPropertyDto();
        dto.setName("server.property");
        dto.setValue("server property value");
        entityPut.add(dto);

        // Perform the put and verify return code
        ClientResponse response = put(resolveSystemPropertiesURIByComponent("server"), entityPut);
        assertEquals(response.getStatusCode(), 200);

        // Perform a get operation and verify the returned list size
        response = get(resolveSystemPropertiesURIByComponent("server"));

        assertEquals(response.getStatusCode(), 200);
        assertNotEmpty(response.getEntity(SystemPropertiesDto.class).getCollection());
        assertSize(response.getEntity(SystemPropertiesDto.class).getCollection(), 1);
    }
}
