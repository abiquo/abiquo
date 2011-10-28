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

package com.abiquo.api.resources;

import static com.abiquo.api.common.UriTestResolver.resolveDatacenterURI;
import static com.abiquo.api.common.UriTestResolver.resolveDatacentersURI;

import org.apache.wink.client.ClientResponse;
import org.apache.wink.client.Resource;
import org.testng.Assert;
import org.testng.annotations.Test;

public class ResourceOptionsIT extends AbstractJpaGeneratorIT
{
    @Test
    public void datacenterReturnsRestOptions()
    {
        assertAllow(resolveDatacenterURI(12345), "GET", "PUT", "DELETE", "OPTIONS");
        assertNotAllow(resolveDatacenterURI(12345), "POST");
    }

    @Test
    public void datacentersReturnsRestOptions()
    {
        assertAllow(resolveDatacentersURI(), "GET", "POST", "OPTIONS");
        assertNotAllow(resolveDatacentersURI(), "PUT", "DELETE");
    }

    private void assertAllow(final String url, final String... expectedMethods)
    {
        Resource resource = client.resource(url);
        ClientResponse response = resource.options();
        String allowed = response.getHeaders().getFirst("Allow");

        for (String method : expectedMethods)
        {
            Assert.assertTrue(allowed.contains(method));
        }
    }

    private void assertNotAllow(final String url, final String... unxpectedMethods)
    {
        Resource resource = client.resource(url);
        ClientResponse response = resource.options();
        String allowed = response.getHeaders().getFirst("Allow");

        for (String method : unxpectedMethods)
        {
            Assert.assertFalse(allowed.contains(method));
        }
    }
}
