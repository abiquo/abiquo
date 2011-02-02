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

package net.undf.abicloud.security
{

    /**
     * Resource represents a resource in server or client.
     * A resource is an entity that can be secured server side. If we associated a resource to a client component (like a Flex component),
     * we can give security to that component from the server
     *
     * A Resource is identified by its name and the name of the group which it belongs to
     **/
    public class SecurableResource
    {

        /* ------------- Public atributes ------------- */
        private var _name:String;

        private var _group:String;

        /* ------------- Constructor ------------- */
        public function SecurableResource(name:String, group:String)
        {
            _name = name;
            _group = group;
        }

        public function get name():String
        {
            return this._name;
        }

        public function get group():String
        {
            return this._group;
        }

        public function applyAuthorization(authorizationManager:IAuthorizationManager):Boolean
        {
            return authorizationManager.isAuthorized(this);
        }

    }
}