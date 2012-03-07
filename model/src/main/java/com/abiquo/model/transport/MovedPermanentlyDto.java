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

package com.abiquo.model.transport;

import com.abiquo.model.rest.RESTLink;

/**
 * This Entity is the response of 301
 * 
 * @author sacedo
 */
// IMPORTANT: To avoid unmarshalling issues this class should be kept abstract and add a concrete
// implementation for each concrete entity that supports move operations
// Also, to avoid unmarshalling issues, ALL JAXB ANNOTATIONS should be set only in the concrete
// subclasses
public abstract class MovedPermanentlyDto
{
    /** The link to the new location of the moved resource. */
    private RESTLink location;

    public RESTLink getLocation()
    {
        return location;
    }

    public void setLocation(final RESTLink locationLink)
    {
        this.location = locationLink;
    }

}
