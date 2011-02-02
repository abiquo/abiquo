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

package net.undf.abicloud.events
{
    import flash.events.Event;

    public class MainEvent extends Event
    {
        /* ------------- Constants------------- */
        public static const GET_COMMON_INFORMATION:String = "getCommonInformationMainEvent";
        public static const GET_SYSTEM_PROPERTIES:String = "getSystemProperties";

        public static const IBODYCOMPONENT_READY_TO_BE_CHANGED:String = "iBodyComponentReadyToBeChangedMainEvent"

        /* ------------- Constructor ------------- */
        public function MainEvent(type:String, bubbles:Boolean = true, cancelable:Boolean = false)
        {
            super(type, bubbles, cancelable);
        }

    }
}