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

package com.abiquo.abiserver.pojo.authentication;

import java.util.Date;

/**
 * This class represents a Session in Abiserver. A user needs a session to be able to access to this
 * server, and AuthManager determines if this session is valid or not
 * 
 * @author Oliver
 */

public class UserSession
{
    // internal id of the session
    private int id;

    // user who this session belongs to
    private String user;

    // Ticket to identify user's session
    private String key;

    // Session's expiration Date
    private Date expireDate;

    // User's locale
    private String locale;

    private Integer userIdDb;

    private String enterpriseName;

    private String authType;

    public UserSession()
    {
        user = "";
        key = "";
        locale = "";
        authType = "ABIQUO";
    }

    public String getUser()
    {
        return user;
    }

    public void setUser(String user)
    {
        this.user = user;
    }

    public String getKey()
    {
        return key;
    }

    public void setKey(String key)
    {
        this.key = key;
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public Date getExpireDate()
    {
        return expireDate;
    }

    public void setExpireDate(Date expireDate)
    {
        this.expireDate = expireDate;
    }

    public String getLocale()
    {
        return locale;
    }

    public void setLocale(String locale)
    {
        this.locale = locale;
    }

    public Integer getUserIdDb()
    {
        return userIdDb;
    }

    public void setUserIdDb(Integer userIdDb)
    {
        this.userIdDb = userIdDb;
    }

    public String getEnterpriseName()
    {
        return enterpriseName;
    }

    public void setEnterpriseName(String enterpriseName)
    {
        this.enterpriseName = enterpriseName;
    }

    /**
     * Against which system should authenticate. @returna {@link AuthType} value. String
     */
    public String getAuthType()
    {
        return authType;
    }

    /**
     * Against which system should authenticate.
     * 
     * @param authType a {@link AuthType} value.
     */
    public void setAuthType(String authType)
    {
        this.authType = authType;
    }
}
