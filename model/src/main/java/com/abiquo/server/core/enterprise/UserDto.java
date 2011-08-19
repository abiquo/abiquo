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
    /**
     * UID.
     */
    private static final long serialVersionUID = -5897165717112120594L;

    private Integer id;

    public UserDto()
    {

    }

    /**
     * Constructor.
     * 
     * @param name Name.
     * @param surname Surname.
     * @param email Email Address.
     * @param nick Login.
     * @param password Password.
     * @param locale Language.
     * @param description Desc.
     * @deprecated use
     *             {@link #UserDto(String, String, String, String, String, String, String, String)}
     *             instead.
     */
    @Deprecated
    public UserDto(final String name, final String surname, final String email, final String nick,
        final String password, final String locale, final String description)
    {
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.nick = nick;
        this.password = password;
        this.locale = locale;
        this.description = description;
    }

    /**
     * Constructor.
     * 
     * @param name Name.
     * @param surname Surname.
     * @param email Email Address.
     * @param nick Login.
     * @param password Password.
     * @param locale Language.
     * @param description Desc.
     * @param authType AuthType value.
     */
    public UserDto(final String name, final String surname, final String email, final String nick,
        final String password, final String locale, final String description, final String authType)
    {
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.nick = nick;
        this.password = password;
        this.locale = locale;
        this.description = description;
        this.authType = authType;
    }

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

    private String nick;

    public String getNick()
    {
        return nick;
    }

    public void setNick(final String nick)
    {
        this.nick = nick;
    }

    private String locale;

    public String getLocale()
    {
        return locale;
    }

    public void setLocale(final String locale)
    {
        this.locale = locale;
    }

    private String password;

    public String getPassword()
    {
        return password;
    }

    public void setPassword(final String password)
    {
        this.password = password;
    }

    private String surname;

    public String getSurname()
    {
        return surname;
    }

    public void setSurname(final String surname)
    {
        this.surname = surname;
    }

    private boolean active;

    public boolean isActive()
    {
        return active;
    }

    public void setActive(final int active)
    {
        this.active = active == 1;
    }

    public void setActive(final boolean active)
    {
        this.active = active;
    }

    private String email;

    public String getEmail()
    {
        return email;
    }

    public void setEmail(final String email)
    {
        this.email = email;
    }

    private String description;

    public String getDescription()
    {
        return description;
    }

    public void setDescription(final String description)
    {
        this.description = description;
    }

    // comma separated values
    private String availableVirtualDatacenters;

    public String getAvailableVirtualDatacenters()
    {
        return availableVirtualDatacenters;
    }

    public void setAvailableVirtualDatacenters(final String availableVirtualDatacenters)
    {
        this.availableVirtualDatacenters = availableVirtualDatacenters;
    }

    /**
     * String representation for {@link com.abiquo.server.core.enterprise.User.AuthType} value.
     */
    private String authType;

    /**
     * {@link com.abiquo.server.core.enterprise.User.AuthType} value.
     * 
     * @return {@link com.abiquo.server.core.enterprise.User.AuthType} value. String.
     */
    public String getAuthType()
    {
        return authType;
    }

    /**
     * {@link com.abiquo.server.core.enterprise.User.AuthType} value.
     * 
     * @param authType {@link com.abiquo.server.core.enterprise.User.AuthType} value.
     */
    public void setAuthType(final String authType)
    {
        this.authType = authType;
    }
}
