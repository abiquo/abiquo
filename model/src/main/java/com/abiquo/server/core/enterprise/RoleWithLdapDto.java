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

@XmlRootElement(name = "roleWithLdap")
public class RoleWithLdapDto extends RoleDto
{
    public RoleWithLdapDto()
    {
    }

    public RoleWithLdapDto(final RoleDto dto)
    {
        super();
        this.setBlocked(dto.isBlocked());
        this.setId(dto.getId());
        this.setIdEnterprise(dto.getIdEnterprise());
        this.setName(dto.getName());
        this.setLinks(dto.getLinks());
    }

    private Integer idLdap;

    @Override
    public String getLdap()
    {
        return ldap;
    }

    @Override
    public void setLdap(final String ldap)
    {
        this.ldap = ldap;
    }

    public Integer getIdLdap()
    {
        return idLdap;
    }

    public void setIdLdap(final Integer idLdap)
    {
        this.idLdap = idLdap;
    }

    public RoleDto toRoleDto()
    {
        RoleDto dto = new RoleDto();
        dto.setId(this.getId());
        dto.setName(this.getName());
        dto.setBlocked(this.isBlocked());
        dto.setIdEnterprise(this.getIdEnterprise());
        dto.setLinks(this.getLinks());
        return dto;
    }

}
