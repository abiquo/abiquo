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

package com.abiquo.model.enumerator;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "PricingPeriod")
@XmlEnum
public enum PricingPeriod
{
    /* 0 */
    MINUTE,

    /* 1 */
    HOUR,

    /* 2 */
    DAY,

    /* 3 */
    WEEK,

    /* 4 */
    MONTH,

    /* 5 */
    QUARTER,

    /* 6 */
    YEAR;

    public int id()
    {
        return ordinal();
    }

    public static PricingPeriod fromId(final int id)
    {
        return values()[id];
    }
}
