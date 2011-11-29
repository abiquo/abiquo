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
package com.abiquo.api.tracer.hierarchy;

import java.util.Map;

/**
 * Base class for all {@link HierarchyProcessor}.
 * 
 * @author ibarrera
 */
public abstract class AbstractHierarchyProcessor<T> implements HierarchyProcessor
{
    @Override
    public void process(final String uri, final Map<String, String> resourceData)
    {
        String resourcePrefix = getIdentifierPrefix();
        String resourceId = getIdentifier(uri);

        // If the resource prefix is not found, ignore this processor
        if (resourceId != null && !resourceId.isEmpty())
        {
            String resourceName = getResourceName(resourceId);
            resourceData.put(resourcePrefix, resourceId + "|" + resourceName);
        }
    }

    /**
     * Processes the URI to find the resource identifier.
     * 
     * @param uri The URI being processed.
     * @return The identifier of the current resource or <code>null</code> if the identifier is not
     *         found.
     */
    private String getIdentifier(final String uri)
    {
        String prefix = getIdentifierPrefix();
        int prefixPos = uri.indexOf('/' + prefix);

        if (prefixPos == -1)
        {
            // If the resource prefix is not found, ignore this processor, return null to ignore
            // this processor
            return null;
        }

        // Check if it is the global resource (without the identifier)
        int pos = prefixPos + prefix.length() + 1;
        if (pos > uri.length())
        {
            return null;
        }

        String partial = uri.substring(pos);
        int idEnd = partial.indexOf('/');

        return idEnd == -1 ? partial : partial.substring(0, idEnd);
    }

    /**
     * Get the prefix of the identifier of the resource to process.
     * 
     * @return The prefix of the identifier of the resource to process.
     */
    protected abstract String getIdentifierPrefix();

    /**
     * Get the name of the resource that will appear in the traces.
     * 
     * @param resourceId The identifier of the resource being processed.
     * @return The name of the resource.
     */
    protected abstract String getResourceName(final String resourceId);

}
