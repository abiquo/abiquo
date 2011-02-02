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

package com.abiquo.abiserver.pojo.main;

import java.util.ArrayList;

import com.abiquo.abiserver.pojo.infrastructure.HyperVisorType;
import com.abiquo.abiserver.pojo.user.Role;

/**
 * This class defines the objects that represents the common information for the whole application
 * 
 * @author Oliver
 */
public class MainResult
{
    private ArrayList<Role> roles;

    private ArrayList<HyperVisorType> hypervisorTypes;

    private boolean enableVRDP;

    public MainResult()
    {
        roles = new ArrayList<Role>();
    }

    public ArrayList<Role> getRoles()
    {
        return roles;
    }

    public void setRoles(ArrayList<Role> roles)
    {
        this.roles = roles;
    }

    public ArrayList<HyperVisorType> getHypervisorTypes()
    {
        return hypervisorTypes;
    }

    public void setHypervisorTypes(ArrayList<HyperVisorType> hypervisorTypes)
    {
        this.hypervisorTypes = hypervisorTypes;
    }

    public boolean isEnableVRDP()
    {
        return enableVRDP;
    }

    public void setEnableVRDP(boolean enableVRDP)
    {
        this.enableVRDP = enableVRDP;
    }

}
