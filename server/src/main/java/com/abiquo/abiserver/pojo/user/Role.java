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

import java.util.HashSet;
import java.util.Set;

import com.abiquo.abiserver.business.hibernate.pojohb.user.EnterpriseHB;
import com.abiquo.abiserver.business.hibernate.pojohb.user.PrivilegeHB;
import com.abiquo.abiserver.business.hibernate.pojohb.user.RoleHB;
import com.abiquo.abiserver.persistence.DAOFactory;
import com.abiquo.abiserver.persistence.hibernate.HibernateDAOFactory;
import com.abiquo.abiserver.pojo.IPojo;
import com.abiquo.server.core.enterprise.RoleDto;

public class Role implements IPojo<RoleHB>
{

    /* ------------- Public atributes ------------- */
    private int id;

    private String name;

    private boolean blocked;

    private Enterprise enterprise;

    private String ldap;

    private int idEnterprise;

    Set<Privilege> privileges;

    /* ------------- Constructor ------------- */
    public Role()
    {
        id = 0;
        name = "";

    }

    public Role(final Integer id, final String name, final boolean blocked, final String ldap,
        final Integer idEnterprise)
    {

        this.id = id;
        this.name = name;
        this.blocked = blocked;
        this.ldap = ldap;
        this.idEnterprise = idEnterprise;
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

    public boolean isBlocked()
    {
        return blocked;
    }

    public void setBlocked(final boolean blocked)
    {
        this.blocked = blocked;
    }

    public Enterprise getEnterprise()
    {
        return enterprise;
    }

    public void setEnterprise(final Enterprise enterprise)
    {
        this.enterprise = enterprise;
    }

    public Set<Privilege> getPrivileges()
    {
        return privileges;
    }

    public void setPrivileges(final Set<Privilege> privileges)
    {
        this.privileges = privileges;
    }

    public String getLdap()
    {
        return ldap;
    }

    public void setLdap(final String ldap)
    {
        this.ldap = ldap;
    }

    public int getIdEnterprise()
    {
        return idEnterprise;
    }

    public void setIdEnterprise(final int idEnterprise)
    {
        this.idEnterprise = idEnterprise;
    }

    @Override
    public RoleHB toPojoHB()
    {
        RoleHB roleHB = new RoleHB();

        roleHB.setIdRole(id);
        roleHB.setName(name);
        roleHB.isBlocked();

        roleHB.setLdap(ldap);

        if (enterprise != null)
        {
            roleHB.setEnterpriseHB(enterprise.toPojoHB());
        }
        else
        {
            roleHB.setEnterpriseHB(null);

        }

        if (idEnterprise != 0)
        {
            DAOFactory factory = HibernateDAOFactory.instance();
            EnterpriseHB enterpriseHB = factory.getEnterpriseDAO().findById(idEnterprise);
            roleHB.setEnterpriseHB(enterpriseHB);
        }
        else
        {
            roleHB.setEnterpriseHB(null);

        }

        Set<PrivilegeHB> privilegeHB = new HashSet<PrivilegeHB>();
        if (privileges != null)
        {
            for (Privilege privilege : privileges)
            {
                privilegeHB.add(privilege.toPojoHB());
            }
        }

        roleHB.setPrivilegesHB(privilegeHB);

        return roleHB;
    }

    public static Role create(final RoleDto dto, final Enterprise enterprise,
        final Set<Privilege> privileges)
    {
        Role role = new Role();
        role.setId(dto.getId());
        role.setName(dto.getName());
        role.setBlocked(dto.isBlocked());
        if (enterprise != null)
        {
            role.setEnterprise(enterprise);
        }
        role.setLdap(dto.getLdap());
        role.setPrivileges(privileges);

        return role;
    }
}
