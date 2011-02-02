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

package com.abiquo.appliancemanager.util;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import javax.ws.rs.core.MultivaluedMap;

import org.apache.wink.common.internal.uritemplate.JaxRsUriTemplateProcessor;
import org.apache.wink.common.internal.uritemplate.UriTemplateProcessor;
import org.apache.wink.common.internal.utils.UriHelper;

public class URIResolver
{
    public static MultivaluedMap<String, String> resolve(String template, String uriString)
    {
        UriTemplateProcessor processor = new JaxRsUriTemplateProcessor(template);
        return processor.matcher().match(uriString);
    }

    public static String resolveURI(String baseUri, String pathTemplate, Map<String, String> values)
    {
        UriTemplateProcessor template = new JaxRsUriTemplateProcessor(pathTemplate);
        return UriHelper.appendPathToBaseUri(baseUri, template.expand(values));
    }

    public static String buildPath(String... values)
    {
        String path = "";
        for (String value : values)
        {
            path = UriHelper.appendPathToBaseUri(path, value);
        }
        return path;
    }

    public static MultivaluedMap<String, String> resolveFromURI(String buildPath, String uriString)
    {
        String targetPath = null;

        try
        {
            URI uri = new URI(uriString);
            targetPath = uri.getPath().replace("/am", ""); // FIXME: Hack!! we need to figure it
            // out
        }
        catch (URISyntaxException e)
        {
            throw new RuntimeException("Malformed URL " + uriString);
        }

        return resolve(buildPath, targetPath);
    }
}
