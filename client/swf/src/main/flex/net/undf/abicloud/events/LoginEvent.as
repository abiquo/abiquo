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

    import net.undf.abicloud.vo.authentication.Login;
    import net.undf.abicloud.vo.authentication.Session;

    public class LoginEvent extends Event
    {

        /* ------------- Constants------------- */
        public static const LOGIN:String = "loginLoginEvent";

        public static const LOGOUT:String = "logoutLoginEvent";


        /* ------------- Public atributes ------------- */
        //Necesary information to perform a login action
        public var login:Login;

        //Necessary information to perform a logout action
        public var logout:Session;


        /* ------------- Constructor ------------- */
        public function LoginEvent(type:String, bubbles:Boolean = true, cancelable:Boolean = false)
        {
            super(type, bubbles, cancelable);
        }

    }
}