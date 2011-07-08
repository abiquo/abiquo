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

public class Enterprise implements Serializable
{
    private static final long serialVersionUID = -2787051086998140963L;

    public static Enterprise SYSTEM_ENTERPRISE =
        Enterprise.enterprise("Abiquo System").user(User.SYSTEM_USER);

    private String name;

    private User user;

    private VirtualDatacenter virtualDatacenter;
    
    private String chefURL;
    
    private String chefCertificate;

    private Enterprise(String storagePool)
    {
        this.setName(storagePool);
    }

    public static Enterprise enterprise(String enterprise)
    {
        return new Enterprise(enterprise);
    }

    public Enterprise user(User user)
    {
        this.setUser(user);
        return this;
    }

    public Enterprise virtualDatacenter(VirtualDatacenter virtualDatacenter)
    {
        this.setVirtualDatacenter(virtualDatacenter);
        return this;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }

    public void setUser(User user)
    {
        this.user = user;
    }

    public User getUser()
    {
        return user;
    }

    public void setVirtualDatacenter(VirtualDatacenter virtualDatacenter)
    {
        this.virtualDatacenter = virtualDatacenter;
    }

    public VirtualDatacenter getVirtualDatacenter()
    {
        return virtualDatacenter;
    }

    public void setChefURL(String URL)
    {
        this.chefURL= URL;
    }

    public String getURL()
    {
        return chefURL;
    }

    public void setChefCertificate(String certificate)
    {
        this.chefCertificate = certificate;
    }

    public String getChefCertificate()
    {
        return chefCertificate;
    }
}
