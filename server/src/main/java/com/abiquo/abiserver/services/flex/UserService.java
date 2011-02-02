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

package com.abiquo.abiserver.services.flex;

import java.util.ArrayList;

import com.abiquo.abiserver.business.BusinessDelegateProxy;
import com.abiquo.abiserver.business.UserSessionException;
import com.abiquo.abiserver.commands.UserCommand;
import com.abiquo.abiserver.commands.impl.UserCommandImpl;
import com.abiquo.abiserver.pojo.authentication.UserSession;
import com.abiquo.abiserver.pojo.result.BasicResult;
import com.abiquo.abiserver.pojo.result.ListRequest;
import com.abiquo.abiserver.pojo.user.Enterprise;
import com.abiquo.abiserver.pojo.user.User;
import com.abiquo.abiserver.pojo.user.UserListOptions;

/**
 * This class defines all services related to Users management
 * 
 * @author Oliver
 */

public class UserService
{

    private UserCommand userCommand;

    public UserService()
    {
        try
        {
            userCommand =
                (UserCommand) Thread.currentThread().getContextClassLoader().loadClass(
                    "com.abiquo.abiserver.commands.impl.UserCommandPremiumImpl").newInstance();
        }
        catch (Exception e)
        {
            userCommand = new UserCommandImpl();
        }
    }

    protected UserCommand proxyCommand(UserSession userSession)
    {
        return BusinessDelegateProxy.getInstance(userSession, userCommand, UserCommand.class);
    }

    /**
     * Returns a list of users stored in the Data Base. Users marked as deleted will not be returned
     * 
     * @param userSession
     * @param userListOptions an UserListOptions object containing the options to retrieve the list
     *            of users
     * @return A DataResult object containing an UserListResult object
     */
    public BasicResult getUsers(UserSession userSession, UserListOptions userListOptions)
    {
        UserCommand command = proxyCommand(userSession);

        try
        {
            return command.getUsers(userSession, userListOptions);
        }
        catch (UserSessionException e)
        {
            return e.getResult();
        }
    }

    /**
     * Creates a new User in the Data Base
     * 
     * @param userSession
     * @param user the User to be created
     * @return A DataResult object containing the a User object with the user created in the Data
     *         Base
     */
    public BasicResult createUser(UserSession userSession, User user)
    {
        UserCommand command = proxyCommand(userSession);

        try
        {
            return command.createUser(userSession, user);
        }
        catch (UserSessionException e)
        {
            return e.getResult();
        }
    }

    /**
     * Marks an user in Data Base as deleted. This services DOES NOT delete the user from the Data
     * Base
     * 
     * @param userSession
     * @param user The user to be deleted
     * @return A BasicResult object, informing if the user deletion was successful
     */
    public BasicResult deleteUser(UserSession userSession, User user)
    {
        UserCommand command = proxyCommand(userSession);

        try
        {
            return command.deleteUser(userSession, user);
        }
        catch (UserSessionException e)
        {
            return e.getResult();
        }
    }

    /**
     * Modifies a User that already exists in the Data Base
     * 
     * @param userSession
     * @param users A list of users to be modified
     * @return A BasicResult object, informing if the user edition was successful
     */
    public BasicResult editUser(UserSession userSession, ArrayList<User> users)
    {
        UserCommand command = proxyCommand(userSession);

        try
        {
            return command.editUser(userSession, new ArrayList<User>(users));
        }
        catch (UserSessionException e)
        {
            return e.getResult();
        }
    }

    /**
     * Closes any existing session for the given users
     * 
     * @param userSession
     * @param users The list of users whose session will be closed. If null, all current active
     *            sessions will be closed, except the userSession
     * @return A BasicResult object, informing if the operation had success
     */
    public BasicResult closeSessionUsers(UserSession userSession, ArrayList<User> users)
    {
        UserCommand command = proxyCommand(userSession);

        if (users != null)
        {
            return command.closeSessionUsers(userSession, new ArrayList<User>(users));
        }
        else
        {
            return command.closeSessionUsers(userSession);
        }

    }

    // ///////////////////////////////////////
    // ENTERPRISES

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
    public BasicResult getEnterprises(UserSession userSession, ListRequest enterpriseListOptions)
    {
        UserCommand command = proxyCommand(userSession);

        try
        {
            return command.getEnterprises(userSession, enterpriseListOptions);
        }
        catch (UserSessionException e)
        {
            return e.getResult();
        }
    }

    /**
     * Creates a new Enterprise in data base
     * 
     * @param userSession UserSession object with the info of the user that called this method
     * @param enterprise An Enterprise object with the enterprise that will be created
     * @return A DataResult object with success = true and the Enterprise that has been created (if
     *         the creation had success) or success = false otherwise
     */
    public BasicResult createEnterprise(UserSession userSession, Enterprise enterprise)
    {

        UserCommand command = proxyCommand(userSession);

        try
        {
            return command.createEnterprise(userSession, enterprise);
        }
        catch (UserSessionException e)
        {
            return e.getResult();
        }
    }

    /**
     * Updates an Enterprise in Data Base with new information
     * 
     * @param userSession UserSession object with the info of the user that called this method
     * @param enterprise The enterprise that will be updated
     * @return A BasicResult object with success = true if the edition had success
     */
    public BasicResult editEnterprise(UserSession userSession, Enterprise enterprise)
    {
        UserCommand command = proxyCommand(userSession);
        try
        {
            return command.editEnterprise(userSession, enterprise);
        }
        catch (UserSessionException e)
        {
            return e.getResult();
        }
    }

    /**
     * Marks an Enterprise as deleted. This service DOES NOT deletes the enterprise from the Data
     * Base
     * 
     * @param userSession UserSession object with the info of the user that called this method
     * @param enterprise The enterprise that will be marked as deleted
     * @return A BasicResult object with success = true if the operation had success. success =
     *         false otherwise
     */
    public BasicResult deleteEnterprise(UserSession userSession, Enterprise enterprise)
    {
        UserCommand command = proxyCommand(userSession);
        try
        {
            return command.deleteEnterprise(userSession, enterprise);
        }
        catch (UserSessionException e)
        {
            return e.getResult();
        }
    }
}
