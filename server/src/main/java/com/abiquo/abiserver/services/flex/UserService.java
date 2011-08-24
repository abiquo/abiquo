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
import com.abiquo.abiserver.commands.stub.APIStubFactory;
import com.abiquo.abiserver.commands.stub.NetworkResourceStub;
import com.abiquo.abiserver.commands.stub.impl.NetworkResourceStubImpl;
import com.abiquo.abiserver.pojo.authentication.UserSession;
import com.abiquo.abiserver.pojo.networking.VlanNetwork;
import com.abiquo.abiserver.pojo.result.BasicResult;
import com.abiquo.abiserver.pojo.result.DataResult;
import com.abiquo.abiserver.pojo.result.ListRequest;
import com.abiquo.abiserver.pojo.user.Enterprise;
import com.abiquo.abiserver.pojo.user.User;
import com.abiquo.abiserver.pojo.user.UserListOptions;
import com.abiquo.abiserver.pojo.virtualhardware.DatacenterLimit;

/**
 * This class defines all services related to Users management
 * 
 * @author Oliver
 */

public class UserService
{

    private UserCommand userCommand;

    /** The stub used to connect to the API. */
    private NetworkResourceStub networkStub;

    public UserService()
    {
        try
        {
            userCommand =
                (UserCommand) Thread.currentThread().getContextClassLoader()
                    .loadClass("com.abiquo.abiserver.commands.impl.UserCommandPremiumImpl")
                    .newInstance();
        }
        catch (Exception e)
        {
            userCommand = new UserCommandImpl();
        }
        networkStub = new NetworkResourceStubImpl();
    }

    protected UserCommand proxyCommand(final UserSession userSession)
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
    public BasicResult getUsers(final UserSession userSession, final UserListOptions userListOptions)
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
    public BasicResult createUser(final UserSession userSession, final User user)
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
    public BasicResult deleteUser(final UserSession userSession, final User user)
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
    public BasicResult editUser(final UserSession userSession, final ArrayList<User> users)
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
    public BasicResult closeSessionUsers(final UserSession userSession, final ArrayList<User> users)
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
    public BasicResult getEnterprises(final UserSession userSession,
        final ListRequest enterpriseListOptions)
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
    public BasicResult createEnterprise(final UserSession userSession, final Enterprise enterprise)
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
    public BasicResult editEnterprise(final UserSession userSession, final Enterprise enterprise)
    {
        UserCommand command = proxyCommand(userSession);
        try
        {
            BasicResult res = command.editEnterprise(userSession, enterprise);

            // now edit the vlan
            VlanNetwork defaultvlan = enterprise.getDcLimits().iterator().next().getDefaultVlan();

            for (DatacenterLimit dl : enterprise.getDcLimits())
            {
                Integer datacenterId = dl.getDatacenter().getId();
                if (defaultvlan == null)
                {
                    proxyStub(userSession).setInternalVlansAsDefaultInEnterpriseByDatacenterLimit(
                        enterprise.getId(), datacenterId);
                }
                else
                {
                    proxyStub(userSession).setExternalVlanAsDefaultInEnterpriseByDatacenterLimit(
                        enterprise.getId(), datacenterId, defaultvlan.getNetworkId());
                }
            }
            return res;
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
    public BasicResult deleteEnterprise(final UserSession userSession, final Enterprise enterprise)
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

    public BasicResult getEnterprise(final UserSession userSession, final Integer enterpriseId)
    {
        UserCommand command = proxyCommand(userSession);
        try
        {
            DataResult<Enterprise> dataResult = command.getEnterprise(userSession, enterpriseId);

            Enterprise ent = dataResult.getData();

            for (DatacenterLimit dl : ent.getDcLimits())
            {
                Integer datacenterId = dl.getDatacenter().getId();
                @SuppressWarnings("unchecked")
                DataResult<VlanNetwork> defaultVlan =
                    (DataResult<VlanNetwork>) proxyStub(userSession)
                        .getExternalVlanAsDefaultInEnterpriseByDatacenterLimit(ent.getId(),
                            datacenterId);
                dl.setDefaultVlan(defaultVlan.getData());
            }

            return dataResult;
        }
        catch (UserSessionException e)
        {
            return e.getResult();
        }
    }

    /**
     * Returns a list of roles stored in the Data Base.
     * 
     * @param userSession
     * @param roleListOptions an RoleListOptions object containing the options to retrieve the list
     *            of users
     * @return A DataResult object containing an RoleListResult object
     */
    public BasicResult getRoles(final UserSession userSession, final ListRequest roleListOptions,
        final Enterprise enterprise)
    {
        UserCommand command = proxyCommand(userSession);

        try
        {
            return command.getRoles(userSession, roleListOptions, enterprise);

        }
        catch (UserSessionException e)
        {
            return e.getResult();
        }
    }

    public BasicResult getRole(final UserSession userSession, final Integer roleId)
    {
        UserCommand command = proxyCommand(userSession);
        try
        {
            return command.getRole(userSession, roleId);

        }
        catch (UserSessionException e)
        {
            return e.getResult();
        }
    }

    public BasicResult getPrivilegesByRole(final UserSession userSession, final Integer roleId)
    {
        UserCommand command = proxyCommand(userSession);
        try
        {
            return command.getPrivilegesByRole(userSession, roleId.intValue());

        }
        catch (UserSessionException e)
        {
            return e.getResult();
        }
    }

    /**
     * Checks if a Role has a Privilege
     * 
     * @param userSession
     * @param idRole the Role id
     * @param String namePrivilege the name of a Privilege to check
     * @return A DataResult object containing a Boolean if Role has a Privilege
     */
    public BasicResult checkRolePrivilege(final UserSession userSession, final Integer idRole,
        final String namePrivilege)
    {
        UserCommand command = proxyCommand(userSession);

        try
        {
            return command.checkRolePrivilege(userSession, idRole, namePrivilege);
        }
        catch (UserSessionException e)
        {
            return e.getResult();
        }
    }

    protected NetworkResourceStub proxyStub(final UserSession userSession)
    {
        return APIStubFactory.getInstance(userSession, networkStub, NetworkResourceStub.class);
    }
}
