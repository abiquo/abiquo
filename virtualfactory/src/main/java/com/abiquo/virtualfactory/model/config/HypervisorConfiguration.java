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
package com.abiquo.virtualfactory.model.config;

import java.net.URL;

public class HypervisorConfiguration
{
    private String adminUser;

    private String adminPassword;

    private String addressManagement;

    private String hypervisorType;

    public HypervisorConfiguration(String adminUser, String adminPassword, String addresManagement,
        String hypervisorType)
    {
        this.adminUser = adminUser;
        this.adminPassword = adminPassword;
        this.addressManagement = addresManagement;
        this.hypervisorType = hypervisorType;
    }

    public String getAdminUser()
    {
        return adminUser;
    }

    public void setAdminUser(String adminUser)
    {
        this.adminUser = adminUser;
    }

    public String getAdminPassword()
    {
        return adminPassword;
    }

    public void setAdminPassword(String adminPassword)
    {
        this.adminPassword = adminPassword;
    }

    public String getAddressManagement()
    {
        return addressManagement;
    }

    public void setAddressManagement(String addressManagement)
    {
        this.addressManagement = addressManagement;
    }

    public String getHypervisorType()
    {
        return hypervisorType;
    }

    public void setHypervisorType(String hypervisorType)
    {
        this.hypervisorType = hypervisorType;
    }

}
