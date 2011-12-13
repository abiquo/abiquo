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

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.abiquo.model.rest.RESTLink;
import com.abiquo.server.core.infrastructure.storage.VolumeManagementDto;

/**
 * This Entity is the response of 301. It is a link location.
 * 
 * @author sacedo
 */
@XmlRootElement(name = "moved")
public class MovedPermanentlyDto
{
    protected RESTLink locationLink;

    private VolumeManagementDto volumeDto;

    @XmlElement(name = "location")
    public RESTLink getLocationLink()
    {
        return locationLink;
    }

    public void setLocationLink(final RESTLink locationLink)
    {
        this.locationLink = locationLink;
    }

    @XmlElement(name = "volume")
    public VolumeManagementDto getVolumeDto()
    {
        return volumeDto;
    }

    public void setVolumeDto(final VolumeManagementDto volumeDto)
    {
        this.volumeDto = volumeDto;
    }

}
