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

import javax.xml.namespace.QName;

/**
 * The Enum MachineState.
 */
public enum State
{

    /** The POWER OFF state. */
    POWER_OFF("PowerOff"),
    /** The POWER UP state. */
    POWER_UP("PowerUp"),
    /** The PAUSE state. */
    PAUSE("Pause"),
    /** The RESUME state. */
    RESUME("Resume"),
    /** The UNDEPLOY state */
    NOT_DEPLOYED("NotDeployed"),
    /** The DEPLOY state */
    DEPLOYED("Deployed"),
    /** The CANCEL state */
    CANCELLED("Cancelled"),
    /** THE UNKNOWN state */
    UNKNOWN("Unknown");

    /** The value. */
    private final String value;

    /** The Constant machineStateQname. */
    public final static QName machineStateQname = new QName("machineStateAction");

    /**
     * Instantiates a new machine state.
     * 
     * @param value the value
     */
    State(String value)
    {
        this.value = value;
    }

    /**
     * Gets the value.
     * 
     * @return the string
     */
    public String value()
    {
        return value;
    }

    /**
     * Gets the MachineState representation from a string
     * 
     * @param value the value
     * @return the machine state
     */
    public static State fromValue(String value)
    {
        for (State c : State.values())
        {
            if (c.value.equals(value))
            {
                return c;
            }
        }

        if (value == null)
        {
            return UNKNOWN;
        }
        throw new IllegalArgumentException(value);
    }
}
