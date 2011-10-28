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

/**
 * 
 */
package com.abiquo.model.enumerator;

import javax.xml.bind.annotation.XmlEnum;

/**
 * Enumerator to specify all the states of a VlanTag
 * 
 * @author jdevesa@abiquo.com
 */
@XmlEnum
public enum VlanTagAvailabilityType
{
    AVAILABLE("This tag is available."), USED("This tag is used by another VLAN in the Datacenter"), INVALID(
        "VLAN tag out of limits");

    private String message;

    /**
     * Private Constructor with a default message;
     * 
     * @param message
     */
    private VlanTagAvailabilityType(final String message)
    {
        this.message = message;
    }

    public String getMessage()
    {
        return message;
    }
}
