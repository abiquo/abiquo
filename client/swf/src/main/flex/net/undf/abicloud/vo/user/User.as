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
	import mx.collections.ArrayCollection;
	

    [Bindable]
    [RemoteClass(alias="com.abiquo.abiserver.pojo.user.User")]
    public class User
    {
        /* ------------- Public atributes ------------- */
        public var id:int;

        public var role:Role;

        public var user:String;

        public var name:String;

        public var surname:String;

        public var description:String;

        public var email:String;

        public var pass:String;

        public var active:Boolean;

        public var deleted:Boolean;

        public var locale:String;

        public var enterprise:Enterprise;
        
        public var availableVirtualDatacenters:Array;

        public function User()
        {
            id = 0;
            role = new Role();
            user = '';
            name = '';
            surname = '';
            description = '';
            email = '';
            pass = '';
            active = false;
            deleted = false;
            locale = "";
            enterprise = new Enterprise();
            availableVirtualDatacenters = new Array();

        }

    }
}