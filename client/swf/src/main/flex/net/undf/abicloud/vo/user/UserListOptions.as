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

package net.undf.abicloud.vo.user
{

    //This class contains a set of options to retrieve a list of users from the server
    [RemoteClass(alias="com.abiquo.abiserver.pojo.user.UserListOptions")]
    public class UserListOptions
    {
        public var offset:int;

        public var length:int;

        public var filter:String;

        public var orderBy:String;

        public var asc:Boolean;

        public var byEnterprise:Enterprise;

        public var loggedOnly:Boolean;

        public function UserListOptions()
        {
            //Default values
            offset = 0;
            length = 50;
            filter = "";
            orderBy = "name";
            asc = true;
            byEnterprise = null;
            loggedOnly = false;
        }

    }
}