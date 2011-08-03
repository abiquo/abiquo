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
    import flash.events.Event;
    import flash.events.EventDispatcher;
    
    import mx.collections.ArrayCollection;
    
    import net.undf.abicloud.events.UserEvent;
    import net.undf.abicloud.model.AbiCloudModel;
    import net.undf.abicloud.vo.result.ListRequest;
    import net.undf.abicloud.vo.user.Enterprise;
    import net.undf.abicloud.vo.user.Privilege;
    import net.undf.abicloud.vo.user.User;

    /**
     * Manager for Users
     * Stores information about users, and methods for manipulate this information
     **/
    public class UserManager extends EventDispatcher
    {

        /* ------------- Constants------------- */
        public static const USERS_UPDATED:String = "usersUpdated_UserManager";

        public static const ROLES_UPDATED:String = "rolesUpdated_UserManager";

        public static const ENTERPRISES_UPDATED:String = "enterprisesUpdated_UserManager";
        
        public static const PRIVILEGES_UPDATED:String = "privilegesUpdated_UserManager";

        /* ------------- Constructor ------------- */
        public function UserManager()
        {
            this._enterprises = new ArrayCollection();
            this._totalEnterprises = 0;

            this._users = new ArrayCollection();
            this._totalUsers = 0;

            this._roles = new ArrayCollection();
            
            this._privileges = new ArrayCollection();
        }


        /* ------------- Public methods ------------- */

        ///////////////////////////////////
        //RELATED TO USERS

        /**
         * ArrayCollection containing only the users (limited by a length) that matched the UserOptionsList object that was given
         * when the list of users was retrieved from the server
         **/
        private var _users:ArrayCollection;

        [Bindable(event="usersUpdated_UserManager")]
        public function get users():ArrayCollection
        {
            return this._users;
        }

        public function set users(array:ArrayCollection):void
        {
            this._users = array;
            dispatchEvent(new Event(USERS_UPDATED, true));
        }

        /**
         * Total number of users that matched the UserOptionsList object that was given when the list of users
         * was retrieved from the server
         */
        private var _totalUsers:int;

        [Bindable]
        public function get totalUsers():int
        {
            return this._totalUsers;
        }

        public function set totalUsers(value:int):void
        {
            this._totalUsers = value;
        }

        /**
         * Adds a new user to the users list
         * This new user must be first created in server
         **/
        public function addUser(user:User):void
        {
            if (!this._users.contains(user))
            {
                this._users.addItem(user);
                dispatchEvent(new Event(USERS_UPDATED, true));
            }
        }


        /**
         * Refreshes the users list when one has been edited
         **/
        public function editUser(oldUsers:ArrayCollection, newUsers:ArrayCollection):void
        {
            var length:int = oldUsers.length;
            var i:int;
            var oldUser:User;
            var newUser:User;
            for (i = 0; i < length; i++)
            {
                oldUser = oldUsers.getItemAt(i) as User;
                newUser = newUsers.getItemAt(i) as User;

                //Updating the old user with the new values, without modifying its memory address
                updateUserInfo(oldUser, newUser);

                //If the edited user is the current logged user, we have to update the information
                //in the current users list. TODO: A way to not have the current logged user object in to different places?
                if (oldUser.id == AbiCloudModel.getInstance().loginManager.user.id)
                {
                    var length2:int = this._users.length;
                    for (var j:int = 0; j < length2; j++)
                    {
                        if (oldUser.id == User(this._users.getItemAt(j)).id)
                        {
                            updateUserInfo(this._users.getItemAt(j) as User, newUser);
                            break;
                        }
                    }

                }
            }


            //Announcing that users has been edited
            var userEvent:UserEvent = new UserEvent(UserEvent.USERS_EDITED, false);
            userEvent.users = oldUsers;
            dispatchEvent(userEvent);
        }

        /**
         * Helper function to update a user information
         */
        private function updateUserInfo(oldUser:User, newUser:User):void
        {
            oldUser.id = newUser.id;
            oldUser.role = newUser.role;
            oldUser.user = newUser.user;
            oldUser.name = newUser.name;
            oldUser.surname = newUser.surname;
            oldUser.description = newUser.description;
            oldUser.email = newUser.email;
            oldUser.pass = newUser.pass;
            oldUser.active = newUser.active;
            oldUser.deleted = newUser.deleted;
            oldUser.locale = newUser.locale;
            oldUser.enterprise = newUser.enterprise;
            oldUser.availableVirtualDatacenters = newUser.availableVirtualDatacenters;
        }


        /**
         * Deletes a user from the users list
         * The user must be first deleted in server
         **/
        public function deleteUser(user:User):void
        {
            var index:int = this._users.getItemIndex(user);
            if (index >= 0)
            {
                this._users.removeItemAt(index);
                dispatchEvent(new Event(USERS_UPDATED, true));
                dispatchEvent(new UserEvent(UserEvent.USER_DELETED));
            }
        }

        /**
         * Called when the session of one or more users has been closed
         * @param users The list of users whose session has been closed
         *
         */
        public function usersSessionClosed(users:ArrayCollection):void
        {
            //Announcing that the session of the given users has been successfully closed
            var userEvent:UserEvent = new UserEvent(UserEvent.USERS_SESSION_CLOSED);
            userEvent.users = users;
            dispatchEvent(userEvent);
        }

        ///////////////////////////////////
        //RELATED TO ROLES


        /**
         * ArrayCollection containing all possible Roles (stored in the server), that can be assigned to a user
         **/
        private var _roles:ArrayCollection;

        [Bindable(event="rolesUpdated_UserManager")]
        public function get roles():ArrayCollection
        {
            return this._roles;
        }

        public function set roles(array:ArrayCollection):void
        {
            this._roles = array;
            dispatchEvent(new Event(ROLES_UPDATED, true));
        }
        
        private var _totalRoles:int;

        [Bindable(event="totalRolesUpdated_UserManager")]
        public function get totalRoles():int
        {
            return this._totalRoles;
        }

        public function set totalRoles(total:int):void
        {
            this._totalRoles = total;
        }
        
        ///////////////////////////////////
        //RELATED TO PRIVILEGES


        /**
         * ArrayCollection containing all user privileges
         **/
        private var _privileges:ArrayCollection;

        [Bindable(event="privilegesUpdated_UserManager")]
        public function get privileges():ArrayCollection
        {
            return this._privileges;
        }

        public function set privileges(array:ArrayCollection):void
        {
            this._privileges = array;
            dispatchEvent(new Event(PRIVILEGES_UPDATED, true));
        }
        
        public function userHasPrivilege(privilege:String):Boolean{
        	for(var i:int = 0 ; i < this._privileges.length ; i++){
        		if(Privilege(this._privileges.getItemAt(i)).name == privilege){
        			return true;
        		}
        	}
        	return false;
        }
        
         /**
         * ArrayCollection containing role privileges
         **/
        private var _rolePrivileges:ArrayCollection;

        [Bindable(event="rolePrivilegesUpdated_UserManager")]
        public function get rolePrivileges():ArrayCollection
        {
            return this._rolePrivileges;
        }

        public function set rolePrivileges(array:ArrayCollection):void
        {
            this._rolePrivileges = array;
            dispatchEvent(new Event(PRIVILEGES_UPDATED, true));
        }
        
        /**
         * Function which check if a user has a privilege
         **/
        public function roleHasPrivilege(privilege:String):Boolean{
        	for(var i:int = 0 ; i < this._rolePrivileges.length ; i++){
        		if(Privilege(this._rolePrivileges.getItemAt(i)).name == privilege){
        			return true;
        		}
        	}
        	return false;
        }        
        
        
        /**
         * Function which return a privilege from role or user privileges 
         **/
        public function getPrivilege(privilege:String,type:String):Privilege{
        	var list:ArrayCollection;
        	if(type == "role"){
        		list = this._rolePrivileges;
        	}else{
        		list = this._privileges;
        	}
        	for(var i:int = 0 ; i < list.length ; i++){
        		if(Privilege(list.getItemAt(i)).name == privilege){
        			return Privilege(list.getItemAt(i));
        		}
        	}
        	return null;
        }

        ///////////////////////////////////
        //RELATED TO ENTERPRISES

        /**
         * ArrayCollection containing only the enterprises (limited by a length) that matched the EnterpriseOptionsList object that was given
         * when the list of users was retrieved from the server
         */
        private var _enterprises:ArrayCollection;

        [Bindable(event="enterprisesUpdated_UserManager")]
        public function get enterprises():ArrayCollection
        {
            return this._enterprises;
        }

        public function set enterprises(value:ArrayCollection):void
        {
            this._enterprises = value;
            dispatchEvent(new Event(ENTERPRISES_UPDATED));
        }

        /**
         * Total number of enterprises that matched the UserOptionsList object that was given when the list of users
         * was retrieved from the server
         */
        private var _totalEnterprises:int;

        [Bindable]
        public function get totalEnterprises():int
        {
            return this._totalEnterprises;
        }

        public function set totalEnterprises(value:int):void
        {
            this._totalEnterprises = value;
        }

        /**
         * Adds a new enterprise (already created in server) to the enterprises list
         * @param enterprise The enterprise to add
         *
         */
        public function addEnterprise(enterprise:Enterprise):void
        {
            this._enterprises.addItem(enterprise);
            
            //Inform the enterprise is created to update the theme if applies
            var event:UserEvent = new UserEvent(UserEvent.ENTERPRISE_CREATED);
            event.enterprise = enterprise;
            dispatchEvent(event);            
        }

        /**
         * Deletes an existing enterprise from the enterprises list, once the enterprise has been deleted from server
         * @param enterprise The enterprise to be deleted
         *
         */
        public function deleteEnterprise(enterprise:Enterprise):void
        {
            var index:int = this._enterprises.getItemIndex(enterprise);
            if (index > -1)
            {
                this._enterprises.removeItemAt(index);
                dispatchEvent(new Event(ENTERPRISES_UPDATED));
                dispatchEvent(new UserEvent(UserEvent.ENTERPRISE_DELETED));
            }
        }

        /**
         * Updates an enterprise that has been changed in server with new values
         * @param oldEnterprise The Enterprise that exists in the enterprises list, to be updated
         * @param newEnterprise An Enterprise object containing the new values for the enterprise that will be edited
         *
         */
        public function editEnterprise(oldEnterprise:Enterprise, newEnterprise:Enterprise):void
        {
            //Updating the old enterprise without modifying its memory address
            oldEnterprise.id = newEnterprise.id;
            oldEnterprise.name = newEnterprise.name;
            if(newEnterprise.limits){
	            oldEnterprise.limits = newEnterprise.limits;
	            //Update the connected user enterprise
	            if(AbiCloudModel.getInstance().loginManager.user.enterprise.id == newEnterprise.id){
	            	AbiCloudModel.getInstance().loginManager.user.enterprise = oldEnterprise;
	            }            
            }

            var userEvent:UserEvent = new UserEvent(UserEvent.ENTERPRISE_EDITED);
            userEvent.enterprise = oldEnterprise;
            dispatchEvent(userEvent);
        }
        
        /**
         * Retrieve a set of enterprise matching with search criteria
         * @param index The index of the page from where we need to perform the search
         * @param numberOfNodes Number of result to display
         * @param filtersLike Additional text criteria if we want to filter by name
         *
         */
         public function requestEnterprises(index:int,numberOfNodes:int, filtersLike:String):void{
         	var listRequest:ListRequest = new ListRequest();
            listRequest.offset = index * numberOfNodes;
            listRequest.numberOfNodes = numberOfNodes;
            listRequest.filterLike = filtersLike;

            var event:UserEvent = new UserEvent(UserEvent.GET_ENTERPRISES);
            event.listRequest = listRequest;
            dispatchEvent(event);
         }

    }
}