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
package com.abiquo.abiserver.commands;

import java.util.ArrayList;

import com.abiquo.abiserver.pojo.authentication.UserSession;
import com.abiquo.abiserver.pojo.result.BasicResult;
import com.abiquo.abiserver.pojo.result.DataResult;
import com.abiquo.abiserver.pojo.result.ListRequest;
import com.abiquo.abiserver.pojo.user.Enterprise;
import com.abiquo.abiserver.pojo.user.EnterpriseListResult;
import com.abiquo.abiserver.pojo.user.Role;
import com.abiquo.abiserver.pojo.user.User;
import com.abiquo.abiserver.pojo.user.UserListOptions;
import com.abiquo.abiserver.pojo.user.UserListResult;

public interface UserCommand
{

    /**
     * Returns a list of users stored in the Data Base
     * 
     * @param userSession
     * @param userListOptions an UserListOptions object containing the options to retrieve the list
     *            of users
     * @return A DataResult object containing an UserListResult object with an ArrayList of User and
     *         the number of total users
     */
    @SuppressWarnings("unchecked")
    public abstract DataResult<UserListResult> getUsers(final UserSession userSession,
        final UserListOptions userListOptions);

    /**
     * Creates a new User in the Data Base
     * 
     * @param userSession
     * @param user the User to be created
     * @return A DataResult object containing the a User object with the user created in the Data
     *         Base
     */
    public abstract DataResult<User> createUser(final UserSession userSession, final User user);

    /**
     * Modifies a User that already exists in the Data Base An user can only modify another user
     * with a role with the same security level or below
     * 
     * @param userSession
     * @param users A list of users to be modified
     * @return A BasicResult object, informing if the user edition was successful
     */
    public abstract BasicResult editUser(final UserSession userSession, final ArrayList<User> users);

    /**
     * Marks an user in Data Base as deleted. This services DOES NOT delete the user from the Data
     * Base An user can only delete another user with a role with the same security level or below
     * 
     * @param userSession
     * @param user The user to be deleted
     * @return A BasicResult object, informing if the user deletion was successful
     */
    public abstract BasicResult deleteUser(final UserSession userSession, final User user);

    /**
     * Closes any existing session for the given users
     * 
     * @param userSession
     * @param users The list of users whose session will be closed
     * @return A BasicResult object, informing if the operation had success
     */
    public abstract BasicResult closeSessionUsers(final UserSession userSession,
        final ArrayList<User> users);

    /**
     * Closes all current active sessions, except the userSession
     * 
     * @param userSession
     * @param users The list of users whose session will be closed
     * @return A BasicResult object, informing if the operation had success
     */
    public abstract BasicResult closeSessionUsers(final UserSession userSession);

    /**
     * Gets the List of enterprises from the Data Base. Enterprises marked as deleted will not be
     * returned
     * 
     * @param userSession A UserSession object containing the information of the user that called
     *            this method
     * @param enterpriseListOptions an UserListOptions object containing the options to retrieve the
     *            list of users
     * @return A DataResult object containing an EnterpriseListResult object
     */
    @SuppressWarnings("unchecked")
    public abstract DataResult<EnterpriseListResult> getEnterprises(final UserSession userSession,
        final ListRequest enterpriseListOptions);

    /**
     * Creates a new Enterprise in data base
     * 
     * @param userSession UserSession object with the info of the user that called this method
     * @param enterprise An Enterprise object with the enterprise that will be created
     * @return A DataResult object with success = true and the Enterprise that has been created (if
     *         the creation had success) or success = false otherwise
     */
    public abstract DataResult<Enterprise> createEnterprise(final UserSession userSession,
        final Enterprise enterprise);

    /**
     * Updates an Enterprise in Data Base with new information
     * 
     * @param userSession UserSession object with the info of the user that called this method
     * @param enterprise The enterprise that will be updated
     * @return A BasicResult object with success = true if the edition had success
     */
    @SuppressWarnings("unchecked")
    public abstract BasicResult editEnterprise(final UserSession userSession,
        final Enterprise enterprise);

    /**
     * Marks an Enterprise as deleted. This service DOES NOT deletes the enterprise from the Data
     * Base
     * 
     * @param userSession UserSession object with the info of the user that called this method
     * @param enterprise The enterprise that will be marked as deleted
     * @return A BasicResult object with success = true if the operation had success. success =
     *         false otherwise
     */
    public abstract BasicResult deleteEnterprise(final UserSession userSession,
        final Enterprise enterprise);

    public DataResult<Enterprise> getEnterprise(final UserSession userSession,
        final Integer enterpriseId);

    public DataResult<Role> getRole(final UserSession userSession, final Integer roleId);

}
