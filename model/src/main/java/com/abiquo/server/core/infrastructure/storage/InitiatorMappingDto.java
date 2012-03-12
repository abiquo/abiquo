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

@XmlRootElement(name = "initiatorMapping")
@XmlType(propOrder = {"id", "initiatorIqn", "targetIqn", "targetLun"})
public class InitiatorMappingDto extends SingleResourceTransportDto
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    public static final String BASE_MEDIA_TYPE = "application/vnd.abiquo.initiatormapping+xml";
    public static final String MEDIA_TYPE = BASE_MEDIA_TYPE + "; version=" + API_VERSION;

    private Integer id;

    public Integer getId()
    {
        return id;
    }

    public void setId(final Integer id)
    {
        this.id = id;
    }

    private Integer targetLun;

    public Integer getTargetLun()
    {
        return targetLun;
    }

    public void setTargetLun(final Integer targetLun)
    {
        this.targetLun = targetLun;
    }

    private String targetIqn;

    public String getTargetIqn()
    {
        return targetIqn;
    }

    public void setTargetIqn(final String targetIqn)
    {
        this.targetIqn = targetIqn;
    }

    private String initiatorIqn;

    public String getInitiatorIqn()
    {
        return initiatorIqn;
    }

    public void setInitiatorIqn(final String initiatorIqn)
    {
        this.initiatorIqn = initiatorIqn;
    }

    @Override
    public String getMediaType()
    {
        return InitiatorMappingDto.MEDIA_TYPE;
    }
    
    @Override
    public String getBaseMediaType()
    {
        return BASE_MEDIA_TYPE;
    }
}
