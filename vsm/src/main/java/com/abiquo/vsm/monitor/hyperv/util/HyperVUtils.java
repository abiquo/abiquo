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
package com.abiquo.vsm.monitor.hyperv.util;

import com.abiquo.vsm.events.VMEventType;

/**
 * Utility methods for the HyperV hypervisor.
 * 
 * @author ibarrera
 */
public class HyperVUtils
{
    /**
     * Translates an HyperV event code into an {@link VMEventType}.
     * 
     * @param eventCode Event code to translate.
     * @return Translated event.
     */
    public static VMEventType translateEvent(int eventCode)
    {
        switch (eventCode)
        {
            case HyperVConstants.POWER_ON:
                return VMEventType.POWER_ON;
            case HyperVConstants.POWER_OFF:
                return VMEventType.POWER_OFF;
            case HyperVConstants.SUSPENDED:
                return VMEventType.PAUSED;
            case HyperVConstants.PAUSED:
                return VMEventType.PAUSED;
            default:
                return VMEventType.UNKNOWN;
        }
    }
}
