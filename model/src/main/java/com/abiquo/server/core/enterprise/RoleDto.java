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

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import com.abiquo.model.transport.SingleResourceTransportDto;

@XmlRootElement(name = "role")
public class RoleDto extends SingleResourceTransportDto
{
    public RoleDto()
    {
        super();
    }

    public RoleDto(final Integer id, final String name, final boolean blocked)
    {
        super();
        this.id = id;
        this.name = name;
        this.blocked = blocked;
    }

    public RoleDto(final Integer id, final String name, final boolean blocked,
        final EnterpriseDto entDto)
    {
        this(id, name, blocked);
        this.enterprise = entDto;
    }

    public RoleDto(final Integer id, final String name, final boolean blocked,
        final PrivilegeDto... privileges)
    {
        this(id, name, blocked);
        if (privileges != null && privileges.length > 0)
        {
            this.privileges = new ArrayList<PrivilegeDto>();
            for (PrivilegeDto p : privileges)
            {
                this.privileges.add(p);
            }
        }
    }

    public RoleDto(final Integer id, final String name, final boolean blocked,
        final EnterpriseDto entDto, final PrivilegeDto... privileges)
    {
        this(id, name, blocked, privileges);
        this.enterprise = entDto;
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

    private EnterpriseDto enterprise;

    public EnterpriseDto getEnterprise()
    {
        return enterprise;
    }

    public void setEnterprise(final EnterpriseDto enterprise)
    {
        this.enterprise = enterprise;
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

    private List<PrivilegeDto> privileges;

    public List<PrivilegeDto> getPrivileges()
    {
        return privileges;
    }

    public void setPrivileges(final List<PrivilegeDto> privileges)
    {
        this.privileges = privileges;
    }

}
