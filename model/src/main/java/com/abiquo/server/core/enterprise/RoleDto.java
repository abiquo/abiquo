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

import javax.xml.bind.annotation.XmlRootElement;

import com.abiquo.model.transport.SingleResourceTransportDto;
import com.abiquo.server.core.infrastructure.DatacenterDto;

@XmlRootElement(name = "role")
public class RoleDto extends SingleResourceTransportDto
{
    public static final String BASE_MEDIA_TYPE = "application/vnd.abiquo.role+xml";
    public static final String MEDIA_TYPE = BASE_MEDIA_TYPE + "; version=" + API_VERSION;
    
    public RoleDto()
    {
        super();
    }

    public RoleDto(final Integer id, final String name, final boolean blocked, final String ldap,
        final Integer idEnterprise)
    {
        super();
        this.id = id;
        this.name = name;
        this.blocked = blocked;
        this.ldap = ldap;
        this.idEnterprise = idEnterprise;
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

    private String name;

    public String getName()
    {
        return name;
    }

    public void setName(final String name)
    {
        this.name = name;
    }

    private boolean blocked;

    public boolean isBlocked()
    {
        return blocked;
    }

    public void setBlocked(final boolean blocked)
    {
        this.blocked = blocked;
    }

    protected String ldap;

    public String getLdap()
    {
        return ldap;
    }

    public void setLdap(final String ldap)
    {
        this.ldap = ldap;
    }

    private Integer idEnterprise;

    public Integer getIdEnterprise()
    {
        return idEnterprise;
    }

    public void setIdEnterprise(final Integer idEnterprise)
    {
        this.idEnterprise = idEnterprise;
    }
    
    @Override
    public String getMediaType()
    {
        return RoleDto.MEDIA_TYPE;
    }
    
    @Override
    public String getBaseMediaType()
    {
        return BASE_MEDIA_TYPE;
    }

}
