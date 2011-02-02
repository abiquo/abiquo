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

package net.undf.abicloud.business.managers
{
    import mx.collections.ArrayCollection;

    import net.undf.abicloud.security.IAuthorizationManager;
    import net.undf.abicloud.security.SecurableResource;
    import net.undf.abicloud.vo.authorization.Resource;

    public class AuthorizationManager implements IAuthorizationManager
    {

        /* ------------- Private attributes ------------- */

        //Where the Resources are saved
        private var _authorizedResources:ArrayCollection;


        /* ------------- Setters & Getters ------------- */
        public function set authorizedResources(array:ArrayCollection):void
        {
            this._authorizedResources = array;
        }


        /* ------------- Constructor ------------- */
        public function AuthorizationManager()
        {
            _authorizedResources = new ArrayCollection();
        }


        /* ------------- Public methods ------------- */

        /**
         * Implementation of method isAuthorized, described in IAthorizationManager interface
         * Since this AuthorizationManager saves Resources (received from a server), we need to
         * transform a Resource into a SecurableResource to perform an authorization check
         * @param securableResourceToCheck
         * @return
         *
         */
        public function isAuthorized(securableResourceToCheck:SecurableResource):Boolean
        {
            var resourcesLength:int = _authorizedResources.length;
            var i:int;
            var resource:Resource;

            for (i = 0; i < resourcesLength; i++)
            {
                resource = _authorizedResources.getItemAt(i) as Resource;
                if (securableResourceToCheck.name == resource.name && securableResourceToCheck.group == resource.group.name)
                    return true;

            }
            return false;
        }
    }
}