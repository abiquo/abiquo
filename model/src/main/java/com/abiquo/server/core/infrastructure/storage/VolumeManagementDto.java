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

@XmlRootElement(name = "volume")
@XmlType(propOrder = {"id", "uuid", "name", "description", "state", "sizeInMB",
/* "availableSizeInMB", "usedSizeInMB", */"idScsi", "idImage"})
public class VolumeManagementDto extends SingleResourceTransportDto
{
    private static final long serialVersionUID = 1L;

    private Integer id;

    private String uuid;

    private String name;

    private String description;

    private String state;

    private long sizeInMB;

    // These fields are deprecated. We are only considering the whole volume size
    // private long availableSizeInMB;
    //
    // private long usedSizeInMB;

    private String idScsi;

    // TODO: vmahe. Replace this field by a link when the VirtualImage Resource is created
    private Integer idImage;

    public Integer getId()
    {
        return id;
    }

    public void setId(final Integer id)
    {
        this.id = id;
    }

    public String getIdScsi()
    {
        return idScsi;
    }

    public void setIdScsi(final String idScsi)
    {
        this.idScsi = idScsi;
    }

    public String getState()
    {
        return state;
    }

    public void setState(final String state)
    {
        this.state = state;
    }

    // public long getUsedSizeInMB()
    // {
    // return usedSizeInMB;
    // }
    //
    // public void setUsedSizeInMB(final long usedSizeInMB)
    // {
    // this.usedSizeInMB = usedSizeInMB;
    // }

    public String getUuid()
    {
        return uuid;
    }

    public void setUuid(final String uuid)
    {
        this.uuid = uuid;
    }

    public String getName()
    {
        return name;
    }

    public void setName(final String name)
    {
        this.name = name;
    }

    public long getSizeInMB()
    {
        return sizeInMB;
    }

    public void setSizeInMB(final long sizeInMB)
    {
        this.sizeInMB = sizeInMB;
    }

    // public long getAvailableSizeInMB()
    // {
    // return availableSizeInMB;
    // }
    //
    // public void setAvailableSizeInMB(final long availableSizeInMB)
    // {
    // this.availableSizeInMB = availableSizeInMB;
    // }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(final String description)
    {
        this.description = description;
    }

    public Integer getIdImage()
    {
        return idImage;
    }

    public void setIdImage(final Integer idImage)
    {
        this.idImage = idImage;
    }
}
