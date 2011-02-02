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

package com.abiquo.abiserver.business.hibernate.pojohb.virtualhardware;

import java.io.Serializable;

import com.abiquo.abiserver.business.hibernate.pojohb.IPojoHB;
import com.abiquo.abiserver.business.hibernate.pojohb.infrastructure.DatacenterHB;
import com.abiquo.abiserver.business.hibernate.pojohb.user.EnterpriseHB;
import com.abiquo.abiserver.config.AbiConfigManager;
import com.abiquo.abiserver.pojo.virtualhardware.DatacenterLimit;

/**
 * @see com.abiquo.abiserver.business.hibernate.pojohb.virtualhardware.DatacenterLimitHB
 */
public class DatacenterLimitHB implements Serializable, IPojoHB<DatacenterLimit>
{
    private static final long serialVersionUID = 1L;

    /** The Enterprise identification. */
    private Integer idDatacenterLimit;

    // private Integer idEnterprise;

    private DatacenterHB datacenter;

    private EnterpriseHB enterprise;

    private ResourceAllocationLimitHB limits;

    public DatacenterHB getDatacenter()
    {
        return datacenter;
    }

    public void setDatacenter(DatacenterHB datacenter)
    {
        this.datacenter = datacenter;
    }

    public DatacenterLimitHB()
    {
        // Load default limits
        this.limits = AbiConfigManager.getInstance().getAbiConfig().getResourceReservationLimits();
    }

    @Override
    public String toString()
    {
        return limits.toString();
    }

    public void setLimits(ResourceAllocationLimitHB limits)
    {
        this.limits = limits;
    }

    public ResourceAllocationLimitHB getLimits()
    {
        return limits;
    }

    public void setIdDatacenterLimit(Integer idDatacenterLimit)
    {
        this.idDatacenterLimit = idDatacenterLimit;
    }

    public Integer getIdDatacenterLimit()
    {
        return idDatacenterLimit;
    }

    public void setEnterprise(EnterpriseHB enterprise)
    {
        this.enterprise = enterprise;
    }

    public EnterpriseHB getEnterprise()
    {
        return enterprise;
    }

    @Override
    public DatacenterLimit toPojo()
    {
        DatacenterLimit dcLimit = new DatacenterLimit();

        if (getDatacenter() != null)
        {
            dcLimit.setDatacenter(getDatacenter().toPojo());
        }

        if (getLimits() != null)
        {
            dcLimit.setLimits(getLimits().toPojo());
        }

        return dcLimit;
    }
}
