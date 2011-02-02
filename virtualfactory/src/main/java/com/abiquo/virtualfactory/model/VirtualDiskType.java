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

package com.abiquo.virtualfactory.model;

/**
 * The Enum VirtualDiskType.
 * 
 * @author pnavarro
 */
public enum VirtualDiskType
{

    /** The ISCSI. */
    ISCSI("ISCSI"),

    /** The STANDARD. */
    STANDARD("Standard");

    /** The value. */
    private final String value;

    /**
     * Instantiates a new virtual disk type.
     * 
     * @param v the v
     */
    VirtualDiskType(String v)
    {
        value = v;
    }

    /**
     * Value.
     * 
     * @return the string
     */
    public String value()
    {
        return value;
    }

    /**
     * Gets the VirtualDiskType from a string.
     * 
     * @param v the value
     * @return the virtual disk type
     */
    public static VirtualDiskType fromValue(String v)
    {
        for (VirtualDiskType c : VirtualDiskType.values())
        {
            if (c.value.equals(v))
            {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }
}
