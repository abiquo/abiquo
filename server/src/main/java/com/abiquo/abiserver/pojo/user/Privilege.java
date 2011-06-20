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

package com.abiquo.abiserver.pojo.user;

import com.abiquo.abiserver.business.hibernate.pojohb.user.PrivilegeHB;
import com.abiquo.abiserver.pojo.IPojo;
import com.abiquo.server.core.enterprise.PrivilegeDto;

public class Privilege implements IPojo<PrivilegeHB>
{

    /* ------------- Public atributes ------------- */
    private int id;

    private String name;

    /* ------------- Constructor ------------- */
    public Privilege()
    {
        id = 0;
        name = "";

    }

    public int getId()
    {
        return id;
    }

    public void setId(final int id)
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

    @Override
    public PrivilegeHB toPojoHB()
    {
        PrivilegeHB privilegeHB = new PrivilegeHB();

        privilegeHB.setIdPrivilege(id);
        privilegeHB.setName(name);

        return privilegeHB;
    }

    public static Privilege create(final PrivilegeDto dto)
    {
        Privilege privilege = new Privilege();
        privilege.setId(dto.getId());
        privilege.setName(dto.getName());

        return privilege;
    }

}
