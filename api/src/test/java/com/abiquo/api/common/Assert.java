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

/**
 * abiCloud  community version
 * cloud management application for hybrid clouds
 * Copyright (C) 2008-2010 - Soluciones Grid SL
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

package com.abiquo.api.common;

import java.util.Collection;

import org.apache.wink.client.ClientResponse;

import com.abiquo.api.exceptions.APIError;
import com.abiquo.model.rest.RESTLink;
import com.abiquo.model.transport.SingleResourceTransportDto;
import com.abiquo.model.transport.error.ErrorDto;
import com.abiquo.model.transport.error.ErrorsDto;

public class Assert extends org.testng.Assert
{
    public static void assertErrors(ClientResponse response, APIError... errors)
    {
        assertErrors(response, 400, errors);
    }

    public static void assertErrors(ClientResponse response, int statusCode, APIError... errors)
    {
        assertEquals(response.getStatusCode(), statusCode);
        ErrorsDto responseErrors = response.getEntity(ErrorsDto.class);

        for (APIError error : errors)
        {
            assertError(responseErrors, error.getCode());
        }
    }

    public static void assertErrors(ClientResponse response, String... errorCodes)
    {
        assertErrors(response, 400, errorCodes);
    }

    public static void assertErrors(ClientResponse response, int statusCode, String... errorCodes)
    {
        assertEquals(response.getStatusCode(), statusCode);
        ErrorsDto errors = response.getEntity(ErrorsDto.class);

        for (String error : errorCodes)
        {
            assertError(errors, error);
        }
    }

    public static void assertError(ClientResponse response, APIError error)
    {
        assertError(response.getEntity(ErrorsDto.class), error.getCode());
    }

    public static void assertError(ClientResponse response, int statusCode, APIError error)
    {
        assertError(response, statusCode, error.getCode());
    }

    public static void assertError(ClientResponse response, String errorCode)
    {
        assertError(response.getEntity(ErrorsDto.class), errorCode);
    }

    public static void assertError(ClientResponse response, int statusCode, String errorCode)
    {
        assertEquals(response.getStatusCode(), statusCode);
        assertError(response.getEntity(ErrorsDto.class), errorCode);
    }

    public static void assertError(ErrorsDto errors, String errorCode)
    {
        for (ErrorDto error : errors.getCollection())
        {
            if (error.getCode().equals(errorCode))
            {
                return;
            }
        }
        fail(errors.getCollection().toString());
    }

    public static RESTLink assertLinkExist(SingleResourceTransportDto resource, String href,
        String expectedRel)
    {
        assertNotNull(resource.getLinks());
        RESTLink link = resource.searchLinkByHref(href);
        assertNotNull(link);
        assertEquals(link.getRel(), expectedRel);
        return link;
    }

    public static RESTLink assertLinkExist(SingleResourceTransportDto resource, String href,
        String expectedRel, String expectedTitle)
    {
        RESTLink link = assertLinkExist(resource, href, expectedRel);
        assertEquals(link.getTitle(), expectedTitle);
        return link;
    }

    public static void assertNonEmptyErrors(ErrorsDto errors)
    {
        assertNotNull(errors);
        assertNotNull(errors.getCollection());
        assertFalse(errors.getCollection().isEmpty());

        ErrorDto error = errors.getCollection().get(0);

        assertTrue(error.getCode().length() > 0);
        assertTrue(error.getMessage().length() > 0);
    }

    public static void assertNotEmpty(Collection< ? > collection)
    {
        assertNotNull(collection);
        assertFalse(collection.isEmpty());
    }

    public static void assertEmpty(Collection< ? > collection)
    {
        assertNotNull(collection);
        assertTrue(collection.isEmpty());
    }

    public static void assertSize(Collection< ? > collection, int expectedSize)
    {
        assertNotNull(collection);

        if (expectedSize == 0)
        {
            assertTrue(collection.isEmpty());
        }
        else
        {
            assertEquals(collection.size(), expectedSize);
        }
    }
}
