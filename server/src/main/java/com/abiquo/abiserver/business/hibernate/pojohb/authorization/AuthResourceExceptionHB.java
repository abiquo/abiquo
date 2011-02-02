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

package com.abiquo.abiserver.business.hibernate.pojohb.authorization;

import com.abiquo.abiserver.business.hibernate.pojohb.user.UserHB;

public class AuthResourceExceptionHB implements java.io.Serializable
{

    private static final long serialVersionUID = -8418818268838658001L;

    private Integer id;

    private AuthResourceHB authResourceHB;

    private UserHB userHB;

    public AuthResourceExceptionHB()
    {

    }

    public Integer getId()
    {
        return id;
    }

    public void setId(Integer id)
    {
        this.id = id;
    }

    public AuthResourceHB getAuthResourceHB()
    {
        return authResourceHB;
    }

    public void setAuthResourceHB(AuthResourceHB authResourceHB)
    {
        this.authResourceHB = authResourceHB;
    }

    public UserHB getUserHB()
    {
        return userHB;
    }

    public void setUserHB(UserHB userHB)
    {
        this.userHB = userHB;
    }

}
