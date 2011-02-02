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

package net.undf.abicloud.controller.user
{
    import mx.collections.ArrayCollection;

    import net.undf.abicloud.controller.ResultHandler;
    import net.undf.abicloud.model.AbiCloudModel;
    import net.undf.abicloud.vo.result.BasicResult;
    import net.undf.abicloud.vo.result.DataResult;
    import net.undf.abicloud.vo.user.Enterprise;
    import net.undf.abicloud.vo.user.EnterpriseListResult;
    import net.undf.abicloud.vo.user.User;
    import net.undf.abicloud.vo.user.UserListResult;


    /**
     * Class to handle server responses when calling user remote services defined in UserEventMap
     **/
    public class UserResultHandler extends ResultHandler
    {

        /* ------------- Constructor --------------- */
        public function UserResultHandler()
        {
            super();
        }


        public function handleGetUsers(result:BasicResult):void
        {
            if (result.success)
            {
                var userListResult:UserListResult = DataResult(result).data as UserListResult;

                //Adding to the model the list of users and number of users
                AbiCloudModel.getInstance().userManager.totalUsers = userListResult.totalUsers;
                AbiCloudModel.getInstance().userManager.users = userListResult.usersList;
            }
            else
            {
                //There was a problem retrieving the list of data centers
                super.handleResult(result);
            }
        }


        public function handleCreateUser(result:BasicResult):void
        {
            if (result.success)
            {
                //Adding the new user to the model
                var createdUser:User = DataResult(result).data as User;
                AbiCloudModel.getInstance().userManager.addUser(createdUser);
            }
            else
            {
                //There was a problem creating a new user
                super.handleResult(result);
            }
        }


        public function handleDeleteUser(result:BasicResult, deletedUser:User):void
        {
            if (result.success)
            {
                //Deleting the user in the model
                AbiCloudModel.getInstance().userManager.deleteUser(deletedUser);
            }
            else
            {
                //There was a problem deleting the user
                super.handleResult(result);
            }
        }


        public function handleEditUser(result:BasicResult, oldUsers:ArrayCollection,
                                       newUsers:ArrayCollection):void
        {
            if (result.success)
            {
                //Announcing the model that the user has been edited
                AbiCloudModel.getInstance().userManager.editUser(oldUsers, newUsers);
            }
            else
                //There was a problem editing the user
                super.handleResult(result);
        }

        public function handleCloseSessionUsers(result:BasicResult, users:ArrayCollection):void
        {
            if (result.success)
            {
                //Announcing the model that given user's session has been closed
                AbiCloudModel.getInstance().userManager.usersSessionClosed(users);
            }
            else
            {
                //There was a problem closing the session of the given users
                super.handleResult(result);
            }
        }

        //////////////////////////////////////////
        //ENTERPRISES

        public function handleGetEnterprises(result:BasicResult):void
        {
            if (result.success)
            {
                var enterpriseListResult:EnterpriseListResult = DataResult(result).data as EnterpriseListResult;

                //Adding the list of enterprises to the model, and the total number of enterprises in server
                AbiCloudModel.getInstance().userManager.totalEnterprises = enterpriseListResult.totalEnterprises;
                AbiCloudModel.getInstance().userManager.enterprises = enterpriseListResult.enterprisesList;
            }
            else
                //There was a problem retrieving the enterprises list
                super.handleResult(result);
        }

        public function handleCreateEnterprise(result:BasicResult):void
        {
            if (result.success)
            {
                //Adding the new enterprise to the model
                AbiCloudModel.getInstance().userManager.addEnterprise(DataResult(result).data as Enterprise);
            }
            else
                //There as a problem creating the enterprise
                super.handleResult(result);
        }

        public function handleEditEnterprise(result:BasicResult, oldEnterprise:Enterprise,
                                             newEnterprise:Enterprise):void
        {
            if (result.success)
            {
                //Editing the old enterprise in model
                AbiCloudModel.getInstance().userManager.editEnterprise(oldEnterprise,
                                                                       newEnterprise);
            }
            else
                //There as a problem editing the enterprise
                super.handleResult(result);
        }

        public function handleDeleteEnterprise(result:BasicResult, enterprise:Enterprise):void
        {
            if (result.success)
            {
                //Deleting the enterprise from the model
                AbiCloudModel.getInstance().userManager.deleteEnterprise(enterprise);
            }
            else
                //There as a problem deleting the enterprise
                super.handleResult(result);
        }
    }
}