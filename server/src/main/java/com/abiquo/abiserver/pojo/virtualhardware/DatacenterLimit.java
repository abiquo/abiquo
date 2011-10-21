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

package com.abiquo.abiserver.pojo.virtualhardware;

import java.io.Serializable;

import com.abiquo.abiserver.business.hibernate.pojohb.virtualhardware.DatacenterLimitHB;
import com.abiquo.abiserver.pojo.IPojo;
import com.abiquo.abiserver.pojo.infrastructure.DataCenter;
import com.abiquo.abiserver.pojo.networking.VlanNetwork;
import com.abiquo.abiserver.pojo.user.Enterprise;

/**
 * @see com.abiquo.abiserver.business.hibernate.pojohb.virtualhardware.DatacenterLimitHB
 */
public class DatacenterLimit implements Serializable, IPojo<DatacenterLimitHB>
{
    private static final long serialVersionUID = 1L;

    private Integer idEnterprise;

    private DataCenter datacenter;

    private Enterprise enterprise;

    private ResourceAllocationLimit limits;

    private VlanNetwork defaultVlan;

    public void setLimits(final ResourceAllocationLimit limits)
    {
        this.limits = limits;
    }

    public ResourceAllocationLimit getLimits()
    {
        return limits;
    }

    public void setDatacenter(final DataCenter datacenter)
    {
        this.datacenter = datacenter;
    }

    public DataCenter getDatacenter()
    {
        return datacenter;
    }

    public void setIdEnterprise(final Integer idEnterprise)
    {
        this.idEnterprise = idEnterprise;
    }

    public Integer getIdEnterprise()
    {
        return idEnterprise;
    }

    public void setEnterprise(final Enterprise enterprise)
    {
        this.enterprise = enterprise;
    }

    public Enterprise getEnterprise()
    {
        return enterprise;
    }

    @Override
    public DatacenterLimitHB toPojoHB()
    {
        DatacenterLimitHB dcLimitHB = new DatacenterLimitHB();

        dcLimitHB.setLimits(limits.toPojoHB());
        dcLimitHB.setDatacenter(datacenter.toPojoHB());

        if (defaultVlan != null)
        {
            dcLimitHB.setDefaultVlan(defaultVlan.toPojoHB());
        }

        return dcLimitHB;
    }

    public VlanNetwork getDefaultVlan()
    {
        return defaultVlan;
    }

    public void setDefaultVlan(final VlanNetwork defaultVlan)
    {
        this.defaultVlan = defaultVlan;
    }

}
