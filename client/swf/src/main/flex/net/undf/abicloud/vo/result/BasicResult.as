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

package net.undf.abicloud.vo.result
{

    [RemoteClass(alias="com.abiquo.abiserver.pojo.result.BasicResult")]
    [Bindable]
    public class BasicResult
    {
        public static const STANDARD_RESULT:int = 0;

        public static const SESSION_INVALID:int = 1;

        public static const SESSION_TIMEOUT:int = 2;

        public static const SESSION_MAX_NUM_REACHED:int = 3;

        public static const USER_INVALID:int = 4;

        public static const AUTHORIZATION_NEEDED:int = 5;

        public static const NOT_AUTHORIZED:int = 6;

        public static const VIRTUAL_IMAGE_IN_USE:int = 7;


        public static const SOFT_LIMT_EXCEEDED:int = 8;


        public static const HARD_LIMT_EXCEEDED:int = 9;


        public static const CLOUD_LIMT_EXCEEDED:int = 10;


        public static const NOT_MANAGED_VIRTUAL_IMAGE:int = 11;

        public static const IP_USED:int = 12;

        public var success:Boolean;

        public var message:String;

        public var resultCode:int;

        public function BasicResult()
        {
            success = false;
            message = "";
            resultCode = BasicResult.STANDARD_RESULT;
        }

    }
}