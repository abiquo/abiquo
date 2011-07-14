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
package com.abiquo.nodecollector.utils;

import java.io.Serializable;
import java.util.Comparator;

import com.abiquo.server.core.infrastructure.nodecollector.ResourceType;


/**
 * Comparator used to sort {@link Resource} in node collector response.
 * 
 * @author ibarrera
 */
public class ResourceComparator implements Comparator<ResourceType>, Serializable
{
    /**
     * Generated serial version.
     */
    private static final long serialVersionUID = 1160486717610965593L;

    @Override
    public int compare(final ResourceType r1, final ResourceType r2)
    {
        Comparator<String> strComparator = String.CASE_INSENSITIVE_ORDER;

        // Sort by type and name

        if (r1.getResourceType() != r2.getResourceType())
        {
            return strComparator.compare(r1.getResourceType().name(), r2.getResourceType().name());
        }
        else
        {
            return strComparator.compare(r1.getElementName(), r2.getElementName());
        }
    }
}
