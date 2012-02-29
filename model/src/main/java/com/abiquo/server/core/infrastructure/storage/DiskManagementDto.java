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

package com.abiquo.server.core.infrastructure.storage;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.abiquo.model.transport.SingleResourceTransportDto;
import com.abiquo.server.core.infrastructure.DatacenterDto;

@XmlRootElement(name = "disk")
@XmlType(propOrder = {"sequence", "sizeInMb"})
public class DiskManagementDto extends SingleResourceTransportDto
{
    public static final String BASE_MEDIA_TYPE = "application/vnd.abiquo.harddisk+xml";
    public static final String MEDIA_TYPE = BASE_MEDIA_TYPE + "; version=" + API_VERSION;

    private static final long serialVersionUID = 1L;

    private Long sizeInMb;
    
    private Integer sequence;

    public void setSizeInMb(final Long sizeInMb)
    {
        this.sizeInMb = sizeInMb;
    }
    
    public Long getSizeInMb()
    {
        return this.sizeInMb;
    }

    public void setSequence(Integer sequence)
    {
        this.sequence = sequence;
    }

    public Integer getSequence()
    {
        return sequence;
    }
    
    @Override
    public String getMediaType()
    {
        return DiskManagementDto.MEDIA_TYPE;
    }
    
    @Override
    public String getBaseMediaType()
    {
        return BASE_MEDIA_TYPE;
    }
}
