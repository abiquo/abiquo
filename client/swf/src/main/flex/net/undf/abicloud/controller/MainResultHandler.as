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

package net.undf.abicloud.controller
{
    import mx.collections.ArrayCollection;
    import mx.resources.ResourceBundle;
    
    import net.undf.abicloud.model.AbiCloudModel;
    import net.undf.abicloud.vo.main.MainResult;
    import net.undf.abicloud.vo.result.BasicResult;
    import net.undf.abicloud.vo.result.DataResult;
    import net.undf.abicloud.vo.user.NewRole;

    /**
     * Class to handle server responses when calling infrastructure remote services defined in InfrastructureEventMap
     **/
    public class MainResultHandler extends ResultHandler
    {


        /* ------------- Constructor --------------- */
        public function MainResultHandler()
        {
            super();
        }

        [ResourceBundle("Common")]
        private var rb:ResourceBundle;

        public function handleGetCommonInformation(result:BasicResult):void
        {
            if (result.success)
            {
                var mainResult:MainResult = DataResult(result).data as MainResult;

                //Setting the common information
                //AbiCloudModel.getInstance().userManager.roles = mainResult.roles;
                AbiCloudModel.getInstance().infrastructureManager.hypervisorTypes = mainResult.hypervisorTypes;
                
                //Roles
                var roles:ArrayCollection = new ArrayCollection();
                
                var role:NewRole = new NewRole();
                role.id = 1;
                role.idEnterprise = 0;
                role.name = "Cloud Admin";                
                roles.addItem(role);
                
                role = new NewRole();
                role.id = 2;
                role.idEnterprise = 1;
                role.name = "Enterprise Admin";
                roles.addItem(role);
                
                role = new NewRole();
                role.id = 3;
                role.name = "User";
                roles.addItem(role);
                
                AbiCloudModel.getInstance().userManager.roles = roles;
            }
            else
            {
                //There was a problem retrieving the common information
                super.handleResult(result);
            }
        }
    }
}
