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

@XmlRootElement(name = "user")
public class UserDto extends SingleResourceTransportDto
{
    private Integer id;

    public UserDto()
    {

    }

    public UserDto(String name, String surname, String email, String nick, String password,
        String locale, String description)
    {
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.nick = nick;
        this.password = password;
        this.locale = locale;
        this.description = description;
    }

    public Integer getId()
    {
        return id;
    }

    public void setId(Integer id)
    {
        this.id = id;
    }

    private String name;

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    private String nick;

    public String getNick()
    {
        return nick;
    }

    public void setNick(String nick)
    {
        this.nick = nick;
    }

    private String locale;

    public String getLocale()
    {
        return locale;
    }

    public void setLocale(String locale)
    {
        this.locale = locale;
    }

    private String password;

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    private String surname;

    public String getSurname()
    {
        return surname;
    }

    public void setSurname(String surname)
    {
        this.surname = surname;
    }

    private boolean active;

    public boolean isActive()
    {
        return active;
    }

    public void setActive(int active)
    {
        this.active = active == 1;
    }

    public void setActive(boolean active)
    {
        this.active = active;
    }

    private String email;

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    private String description;

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    // comma separated values
    private String availableVirtualDatacenters;

    public String getAvailableVirtualDatacenters()
    {
        return availableVirtualDatacenters;
    }

    public void setAvailableVirtualDatacenters(String availableVirtualDatacenters)
    {
        this.availableVirtualDatacenters = availableVirtualDatacenters;
    }

    private int idRole;

    public int getIdRole()
    {
        return idRole;
    }

    public void setIdRole(int idRole)
    {
        this.idRole = idRole;
    }

    private int idEnterprise;

    public int getIdEnterprise()
    {
        return idEnterprise;
    }

    public void setIdEnterprise(int idEnterprise)
    {
        this.idEnterprise = idEnterprise;
    }
}
