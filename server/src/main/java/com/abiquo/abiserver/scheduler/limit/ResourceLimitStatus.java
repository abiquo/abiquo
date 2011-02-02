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

package com.abiquo.abiserver.scheduler.limit;

import com.abiquo.abiserver.business.hibernate.pojohb.infrastructure.DatacenterHB;
import com.abiquo.abiserver.business.hibernate.pojohb.user.EnterpriseHB;
import com.abiquo.abiserver.business.hibernate.pojohb.virtualappliance.VirtualDataCenterHB;

/**
 * Hold the resource allocation limit range and a description reason.
 */
public class ResourceLimitStatus
{
    /**
     * Describe the total resource reservation requirements intervals.
     */
    enum ResourceLimitRange
    {
        /** the current machine will not exceed any resource reservation requirements. */
        OK,
        /** the current machine will exceed the total recommended resource reservation. */
        SOFT,
        /** the current machine will exceed the total allowed resource reservation requirements. */
        HARD
    };

    /** Current limit range. */
    private final ResourceLimitRange limit;

    /**
     * Entity refered to this chekc {@link EnterpriseHB}, {@link DatacenterHB} and
     * {@link VirtualDataCenterHB}
     */
    private final String type;

    public ResourceLimitStatus(final ResourceLimitRange limit, final String type)
    {
        this.limit = limit;
        this.type = type;
    }

    /**
     * @return the resource allocation limit range
     */
    public ResourceLimitRange getLimit()
    {
        return limit;
    }

    /**
     * @return the description reason
     */
    public String getType()
    {
        return type;
    }
}
