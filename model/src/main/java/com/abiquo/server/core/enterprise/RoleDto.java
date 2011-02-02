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

package com.abiquo.server.core.enterprise;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.abiquo.model.transport.SingleResourceTransportDto;

@XmlRootElement(name = "role")
public class RoleDto extends SingleResourceTransportDto
{
    private Integer id;

    public Integer getId()
    {
        return id;
    }

    public void setId(Integer id)
    {
        this.id = id;
    }

    private String shortDescription;

    @XmlElement(name = "name")
    public String getShortDescription()
    {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription)
    {
        this.shortDescription = shortDescription;
    }

    private String largeDescription;

    @XmlElement(name = "description")
    public String getLargeDescription()
    {
        return largeDescription;
    }

    public void setLargeDescription(String largeDescription)
    {
        this.largeDescription = largeDescription;
    }

    private float securityLevel;

    public float getSecurityLevel()
    {
        return securityLevel;
    }

    public void setSecurityLevel(float securityLevel)
    {
        this.securityLevel = securityLevel;
    }

    private Role.Type type;

    public Role.Type getType()
    {
        return type;
    }

    public void setType(Role.Type type)
    {
        this.type = type;
    }

}
