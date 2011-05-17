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

package com.abiquo.server.core.cloud.stateful;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

import com.abiquo.model.transport.SingleResourceTransportDto;
import com.abiquo.server.core.cloud.State;
import com.abiquo.server.core.infrastructure.storage.VolumeManagement;

@XmlRootElement(name = "diskStatefulConversion")
public class DiskStatefulConversionDto extends SingleResourceTransportDto
{
    private static final long serialVersionUID = -1803802363006402113L;

    private Date timestamp;

    public Date getTimestamp()
    {
        return timestamp;
    }

    public void setTimestamp(final Date timestamp)
    {
        this.timestamp = timestamp;
    }

    private String imagePath;

    public String getImagePath()
    {
        return imagePath;
    }

    public void setImagePath(final String imagePath)
    {
        this.imagePath = imagePath;
    }

    private Integer id;

    public Integer getId()
    {
        return id;
    }

    public void setId(final Integer id)
    {
        this.id = id;
    }

    private State state;

    public State getState()
    {
        return state;
    }

    public void setState(final State state)
    {
        this.state = state;
    }

    private VolumeManagement volume;

    public VolumeManagement getVolume()
    {
        return volume;
    }

    public void setVolume(final VolumeManagement volume)
    {
        this.volume = volume;
    }

}
