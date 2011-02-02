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

package com.abiquo.vsm.events;

/**
 * Type of the events propagated by the monitor.
 */
public enum VMEventType
{
    /** A new virtual machine has been created. */
    CREATED,

    /** The virtual machine has been powered on. */
    POWER_ON,

    /** The virtual machine has been powered off. */
    POWER_OFF,

    /** The virtual machine has been paused. */
    PAUSED,

    /** The virtual machine has been resumed. */
    RESUMED,

    /** The virtual machine has been destroyed. */
    DESTROYED,

    /** The virtual machine has been moved to another hypervisor. */
    MOVED,

    /** The virtual machine has been saved. For AIM compatibility. */
    SAVED,

    /** An unknown event. */
    UNKNOWN;

}
