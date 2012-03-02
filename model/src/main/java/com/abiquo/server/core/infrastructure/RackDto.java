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

package com.abiquo.server.core.infrastructure;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

import com.abiquo.model.transport.SingleResourceTransportDto;

@XmlRootElement(name = "rack")
public class RackDto extends SingleResourceTransportDto implements Serializable
{
    public static final String BASE_MEDIA_TYPE = "application/vnd.abiquo.rack+xml";
    public static final String MEDIA_TYPE = BASE_MEDIA_TYPE + "; version=" + API_VERSION;
    
    private static final long serialVersionUID = 1L;

    private Integer id;

    private String name, shortDescription, longDescription;

    private Integer vlanIdMin, vlanIdMax, vlanPerVdcReserved, nrsq;

    private String vlansIdAvoided;

    private boolean haEnabled = false;

    public Integer getId()
    {
        return id;
    }

    public void setId(final Integer id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(final String name)
    {
        this.name = name;
    }

    public String getShortDescription()
    {
        return shortDescription;
    }

    public void setShortDescription(final String description)
    {
        this.shortDescription = description;
    }

    public String getLongDescription()
    {
        return longDescription;
    }

    public void setLongDescription(final String description)
    {
        this.longDescription = description;
    }

    public Integer getVlanIdMin()
    {
        return vlanIdMin;
    }

    public void setVlanIdMin(final Integer vlanIdMin)
    {
        this.vlanIdMin = vlanIdMin;
    }

    public Integer getVlanIdMax()
    {
        return vlanIdMax;
    }

    public void setVlanIdMax(final Integer vlanIdMax)
    {
        this.vlanIdMax = vlanIdMax;
    }

    public Integer getNrsq()
    {
        return nrsq;
    }

    public void setNrsq(final Integer nrsq)
    {
        this.nrsq = nrsq;
    }

    public String getVlansIdAvoided()
    {
        return vlansIdAvoided;
    }

    public void setVlansIdAvoided(final String vlansIdAvoided)
    {
        this.vlansIdAvoided = vlansIdAvoided;
    }

    public Integer getVlanPerVdcReserved()
    {
        return vlanPerVdcReserved;
    }

    public void setVlanPerVdcReserved(final Integer vlanPerVdcReserved)
    {
        this.vlanPerVdcReserved = vlanPerVdcReserved;
    }

    public boolean isHaEnabled()
    {
        return haEnabled;
    }

    public void setHaEnabled(final boolean haEnabled)
    {
        this.haEnabled = haEnabled;
    }
    
    @Override
    public String getMediaType()
    {
        return RackDto.MEDIA_TYPE;
    }
    
    @Override
    public String getBaseMediaType()
    {
        return BASE_MEDIA_TYPE;
    }
}
