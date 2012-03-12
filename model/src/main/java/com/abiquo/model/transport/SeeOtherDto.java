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
 * 
 */
package com.abiquo.model.transport;

import javax.xml.bind.annotation.XmlRootElement;

import com.abiquo.server.core.infrastructure.DatacenterDto;
import com.abiquo.server.core.task.TaskDto;

/**
 * This Entity is the response of 303.
 * 
 * @author enric.ruiz@abiquo.com
 */
@XmlRootElement(name = "seeother")
public class SeeOtherDto extends TaskDto
{
    private static final long serialVersionUID = 4645761892464380938L;
    
    public static final String BASE_MEDIA_TYPE = "application/vnd.abiquo.seeother+xml";
    public static final String MEDIA_TYPE = BASE_MEDIA_TYPE + "; version=" + API_VERSION;

    protected String location;

    public SeeOtherDto(final String location)
    {
        setLocation(location);
    }

    public String getLocation()
    {
        return location;
    }

    public void setLocation(String location)
    {
        this.location = location;
    }
    
    @Override
    public String getMediaType()
    {
        return SeeOtherDto.MEDIA_TYPE;
    }
}
