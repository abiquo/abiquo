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
package com.abiquo.nodecollector.domain.collectors.hyperv;

/**
 * States for the virtual machines.
 * 
 * @author ibarrera
 */
public enum HyperVState
{
    /**
     * The VM is running.
     */
    POWER_ON(2),

    /**
     * The VM is turned off.
     */
    POWER_OFF(3),

    /**
     * The VM is paused.
     */
    PAUSED(32768),

    /**
     * The VM is in a saved state.
     */
    SUSPENDED(32769);
    
    /**
     * State value.
     */
    private final int value;

    /**
     * Enum constructor.
     * 
     * @param value The state value.
     */
    HyperVState(final int value)
    {
        this.value = value;
    }

    /**
     * Get the enum value.
     * 
     * @return The enum value.
     */
    public int value()
    {
        return value;
    }

    /**
     * Converts a value into a {@link HyperVState} enum.
     * 
     * @param value Value to convert.
     * @return The <code>HyperVState</code>.
     */
    public static HyperVState fromValue(final int value)
    {
        for (HyperVState c : HyperVState.values())
        {
            if (c.value == value)
            {
                return c;
            }
        }
     // Probably we got a transition state Starting (32770), Saving (32773), Saving (32773), Saving (32773), Saving (32773)
        throw new IllegalArgumentException(String.valueOf(value));
    }
}
