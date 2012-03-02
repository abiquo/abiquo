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

@XmlRootElement(name = "userWithRole")
public class UserWithRoleDto extends UserDto
{
    public static final String BASE_MEDIA_TYPE = "application/vnd.abiquo.userwithroles+xml";
    public static final String MEDIA_TYPE = BASE_MEDIA_TYPE + "; version=" + API_VERSION;
    
    private RoleWithPrivilegesDto role;

    private EnterpriseDto enterprise;

    public RoleWithPrivilegesDto getRole()
    {
        return role;
    }

    public void setRole(final RoleWithPrivilegesDto role)
    {
        this.role = role;
    }

    public EnterpriseDto getEnterprise()
    {
        return enterprise;
    }

    public void setEnterprise(final EnterpriseDto enterprise)
    {
        this.enterprise = enterprise;
    }
    
    @Override
    public String getMediaType()
    {
        return UserWithRoleDto.MEDIA_TYPE;
    }
    
    @Override
    public String getBaseMediaType()
    {
        return BASE_MEDIA_TYPE;
    }

}
