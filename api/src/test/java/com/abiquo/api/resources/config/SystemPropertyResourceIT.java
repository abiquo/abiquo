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

import static com.abiquo.api.common.Assert.assertErrors;
import static com.abiquo.api.common.Assert.assertLinkExist;
import static com.abiquo.api.common.UriTestResolver.resolveSystemPropertyURI;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import javax.ws.rs.core.MediaType;

import org.apache.wink.client.ClientResponse;
import org.apache.wink.client.ClientWebException;
import org.apache.wink.client.Resource;
import org.testng.annotations.Test;

import com.abiquo.api.exceptions.APIError;
import com.abiquo.api.resources.AbstractJpaGeneratorIT;
import com.abiquo.server.core.config.SystemProperty;
import com.abiquo.server.core.config.SystemPropertyDto;
import com.abiquo.server.core.enterprise.Enterprise;
import com.abiquo.server.core.enterprise.Role;
import com.abiquo.server.core.enterprise.User;

public class SystemPropertyResourceIT extends AbstractJpaGeneratorIT
{

    @Test
    public void getUnexistingSystemProperty() throws ClientWebException
    {
        ClientResponse response = get(resolveSystemPropertyURI(12345));
        assertErrors(response, 404, APIError.NON_EXISTENT_SYSTEM_PROPERTY.getCode());
    }

    @Test
    public void getSystemProperty() throws Exception
    {
        assertNotNull(createSystemproperty());
    }

    @Test
    public void existEditLink()
    {
        SystemPropertyDto p = createSystemproperty();
        assertLinkExist(p, resolveSystemPropertyURI(p.getId()), "edit");
    }

    @Test
    public void modifySystemProperty() throws ClientWebException
    {
        SystemPropertyDto p = createSystemproperty();
        String uri = p.getEditLink().getHref();

        SystemPropertyDto property = get(uri).getEntity(SystemPropertyDto.class);
        property.setValue("new property value");

        ClientResponse response = put(uri, property);
        assertEquals(200, response.getStatusCode());

        property = get(uri).getEntity(SystemPropertyDto.class);
        assertEquals(property.getValue(), "new property value");
    }

    @Test(enabled = false)
    // API does not allow to delete System Properties
    public void removeSystemProperty() throws ClientWebException
    {
        SystemPropertyDto p = createSystemproperty();

        Resource resource = client.resource(p.getEditLink().getHref());

        ClientResponse response = resource.accept(MediaType.APPLICATION_XML).delete();
        assertEquals(204, response.getStatusCode());
    }

    private SystemPropertyDto createSystemproperty()
    {
        SystemProperty property = systemPropertyGenerator.createUniqueInstance();
        Enterprise ent = enterpriseGenerator.createUniqueInstance();
        Role sysadminRole = roleGenerator.createInstance();
        User sysadmin = userGenerator.createInstance(ent, sysadminRole, "foo");
        setup(property, ent, sysadminRole, sysadmin);

        String href = resolveSystemPropertyURI(property.getId());

        return get(href, sysadmin.getNick(), "foo").getEntity(SystemPropertyDto.class);
    }

}
