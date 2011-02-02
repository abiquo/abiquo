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

import java.util.ArrayList;
import java.util.Collection;

public class EnterpriseListResult
{
    // The total number of enterprises that matched the EnterprisesListOptions given to retrieve the
    // list of Enterprises
    private int totalEnterprises;

    // The List of Enterprises (limited by a length) that match the EnterpriseListOptions given to
    // retrieve the list of Enterprises
    private Collection<Enterprise> enterprisesList;

    public EnterpriseListResult()
    {
        totalEnterprises = 0;
        enterprisesList = new ArrayList<Enterprise>();
    }

    public int getTotalEnterprises()
    {
        return totalEnterprises;
    }

    public void setTotalEnterprises(int totalEnterprises)
    {
        this.totalEnterprises = totalEnterprises;
    }

    public Collection<Enterprise> getEnterprisesList()
    {
        return enterprisesList;
    }

    public void setEnterprisesList(Collection<Enterprise> enterprisesList)
    {
        this.enterprisesList = enterprisesList;
    }

}
