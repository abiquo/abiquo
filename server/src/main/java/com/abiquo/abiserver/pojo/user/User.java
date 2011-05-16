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

import java.util.Collection;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.apache.commons.lang.StringUtils;

import com.abiquo.abiserver.business.hibernate.pojohb.user.UserHB;
import com.abiquo.abiserver.pojo.IPojo;
import com.abiquo.server.core.enterprise.UserDto;

import edu.emory.mathcs.backport.java.util.Arrays;

public class User implements IPojo<UserHB>
{

    /* ------------- Public atributes ------------- */
    private Integer id;

    private Role role;

    private String user;

    private String name;

    private String surname;

    private String description;

    private String email;

    private String pass;

    private Boolean active;

    private String locale;

    private Enterprise enterprise;

    private Integer[] availableVirtualDatacenters;

    public Integer[] getAvailableVirtualDatacenters()
    {
        return availableVirtualDatacenters;
    }

    public void setAvailableVirtualDatacenters(final Integer[] availableVirtualDatacenters)
    {
        this.availableVirtualDatacenters = availableVirtualDatacenters;
    }

    public Integer getId()
    {
        return id;
    }

    public void setId(final Integer id)
    {
        this.id = id;
    }

    public Role getRole()
    {
        return role;
    }

    public void setRole(final Role role)
    {
        this.role = role;
    }

    public String getUser()
    {
        return user;
    }

    public void setUser(final String user)
    {
        this.user = user;
    }

    public String getName()
    {
        return name;
    }

    public void setName(final String name)
    {
        this.name = name;
    }

    public String getSurname()
    {
        return surname;
    }

    public void setSurname(final String surname)
    {
        this.surname = surname;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(final String description)
    {
        this.description = description;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(final String email)
    {
        this.email = email;
    }

    public String getPass()
    {
        return pass;
    }

    public void setPass(final String pass)
    {
        this.pass = pass;
    }

    public Boolean getActive()
    {
        return active;
    }

    public void setActive(final Boolean active)
    {
        this.active = active;
    }

    public String getLocale()
    {
        return locale;
    }

    public void setLocale(final String locale)
    {
        this.locale = locale;
    }

    public Enterprise getEnterprise()
    {
        return enterprise;
    }

    public void setEnterprise(final Enterprise enterprise)
    {
        this.enterprise = enterprise;
    }

    /**
     * Method to create the hibernate pojo object
     * 
     * @deprecated all the persistence should be throught the api and we don't need this method any
     *             more
     */
    @Deprecated
    public UserHB toPojoHB()
    {
        UserHB userHB = new UserHB();

        userHB.setIdUser(id);
        userHB.setRoleHB(role.toPojoHB());
        userHB.setUser(user);
        userHB.setName(name);
        userHB.setSurname(surname);
        userHB.setDescription(description);
        userHB.setEmail(email);
        userHB.setLocale(locale);
        userHB.setPassword(pass);
        userHB.setActive(active ? 1 : 0);
        if (enterprise != null)
        {
            userHB.setEnterpriseHB(enterprise.toPojoHB());
        }
        else
        {
            userHB.setEnterpriseHB(null);
        }

        return userHB;
    }

    @SuppressWarnings("unchecked")
    public static User create(final UserDto dto, final Enterprise enterprise, final Role role)
    {
        User user = new User();

        user.setId(dto.getId());
        user.setEnterprise(enterprise);
        user.setRole(role);
        user.setUser(dto.getNick());
        user.setName(dto.getName());
        user.setSurname(dto.getSurname());
        user.setDescription(dto.getDescription());
        user.setEmail(dto.getEmail());
        user.setLocale(dto.getLocale());
        user.setPass(dto.getPassword());
        user.setActive(dto.isActive());

        if (!StringUtils.isEmpty(dto.getAvailableVirtualDatacenters()))
        {
            String[] ids = dto.getAvailableVirtualDatacenters().split(",");
            Collection<Integer> idsI =
                CollectionUtils.collect(Arrays.asList(ids), new Transformer()
                {
                    @Override
                    public Object transform(final Object input)
                    {
                        return Integer.valueOf(input.toString());
                    }
                });

            user.setAvailableVirtualDatacenters(idsI.toArray(new Integer[idsI.size()]));
        }
        else
        {
            user.setAvailableVirtualDatacenters(new Integer[] {});
        }

        return user;
    }
}
