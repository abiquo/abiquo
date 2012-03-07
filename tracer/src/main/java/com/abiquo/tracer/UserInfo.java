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

package com.abiquo.tracer;

import java.io.Serializable;

public class UserInfo implements Serializable
{

    /**
	 * 
	 */
    private static final long serialVersionUID = -27154623274629450L;

    private long id;

    private String username;

    private String enterprise;

    public UserInfo()
    {
        this.username = "";
        this.id = 0;
        this.enterprise = "";
    }

    public UserInfo(String username)
    {
        this.username = username;
        this.id = 0;
        this.enterprise = "";
    }

    public UserInfo(String username, Long id)
    {
        this.username = username;
        this.id = id;
        this.enterprise = "";
    }

    public UserInfo(String username, Long id, String enterprise)
    {
        this.username = username;
        this.id = id;
        this.enterprise = enterprise;
    }

    public UserInfo(String username, String enterprise)
    {
        this.username = username;
        this.id = 0;
        this.enterprise = enterprise;
    }

    public long getId()
    {
        return id;
    }

    public void setId(long id)
    {
        this.id = id;
    }

    public String getUsername()
    {
        return username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public String getEnterprise()
    {
        return enterprise;
    }

    public void setEnterprise(String enterprise)
    {
        this.enterprise = enterprise;
    }

}
