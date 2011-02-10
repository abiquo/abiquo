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
    
    import mx.collections.ArrayCollection;
    
    import net.undf.abicloud.vo.result.ListRequest;
    import net.undf.abicloud.vo.user.Enterprise;
    import net.undf.abicloud.vo.user.User;
    import net.undf.abicloud.vo.user.UserListOptions;

    public class UserEvent extends Event
    {

        /* ------------- Constants------------- */
        public static const GET_USERS:String = "getUsersUserEvent";

        public static const CREATE_USER:String = "createUserUserEvent";

        public static const DELETE_USER:String = "deleteUserUserEvent";

        public static const EDIT_USERS:String = "editUsersUserEvent";

        public static const CLOSE_SESSION_USERS:String = "deleteSessionUsersUserEvent";

        public static const GET_ENTERPRISES:String = "getEnterprisesUserEvent";
        
        public static const GET_ENTERPRISE:String = "getEnterpriseUserEvent";

        public static const CREATE_ENTERPRISE:String = "createEnterpriseUserEvent";

        public static const EDIT_ENTERPRISE:String = "editEnterpriseUserEvent";

        public static const DELETE_ENTERPRISE:String = "deleteEnterpriseUserEvent";

        public static const USERS_EDITED:String = "usersEditedUserEvent";

        public static const USER_DELETED:String = "userDeletedUserEvent";
        
        public static const ENTERPRISE_CREATED:String = "enterpriseCreatedUserEvent";

        public static const ENTERPRISE_EDITED:String = "enterpriseEditedUserEvent";

        public static const ENTERPRISE_DELETED:String = "enterpriseDeletedUserEvent";

        public static const USERS_SESSION_CLOSED:String = "usersSessionClosedUserEvent";

        /* ------------- Public atributes ------------- */
        public var user:User;

        public var users:ArrayCollection;

        public var oldUsers:ArrayCollection;

        public var enterprise:Enterprise;

        public var oldEnterprise:Enterprise;

        public var userListOptions:UserListOptions = new UserListOptions();

        public var listRequest:ListRequest;
        
        public var callback:Function;
        
        /* ------------- Constructor ------------- */
        public function UserEvent(type:String, bubbles:Boolean = true, cancelable:Boolean = false)
        {
            super(type, bubbles, cancelable);
        }

    }
}