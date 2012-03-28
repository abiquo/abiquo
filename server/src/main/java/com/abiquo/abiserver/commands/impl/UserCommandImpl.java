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

package com.abiquo.abiserver.commands.impl;

import java.util.ArrayList;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.abiquo.abiserver.business.hibernate.pojohb.user.EnterpriseHB;
import com.abiquo.abiserver.commands.BasicCommand;
import com.abiquo.abiserver.commands.UserCommand;
import com.abiquo.abiserver.commands.stub.APIStubFactory;
import com.abiquo.abiserver.commands.stub.EnterprisesResourceStub;
import com.abiquo.abiserver.commands.stub.UsersResourceStub;
import com.abiquo.abiserver.commands.stub.impl.EnterprisesResourceStubImpl;
import com.abiquo.abiserver.commands.stub.impl.UsersResourceStubImpl;
import com.abiquo.abiserver.persistence.hibernate.HibernateUtil;
import com.abiquo.abiserver.pojo.authentication.UserSession;
import com.abiquo.abiserver.pojo.result.BasicResult;
import com.abiquo.abiserver.pojo.result.DataResult;
import com.abiquo.abiserver.pojo.result.ListRequest;
import com.abiquo.abiserver.pojo.user.Enterprise;
import com.abiquo.abiserver.pojo.user.EnterpriseListResult;
import com.abiquo.abiserver.pojo.user.PrivilegeListResult;
import com.abiquo.abiserver.pojo.user.Role;
import com.abiquo.abiserver.pojo.user.RoleListResult;
import com.abiquo.abiserver.pojo.user.User;
import com.abiquo.abiserver.pojo.user.UserListOptions;
import com.abiquo.abiserver.pojo.user.UserListResult;
import com.abiquo.abiserver.scheduler.limit.exception.HardLimitExceededException;
import com.abiquo.tracer.ComponentType;
import com.abiquo.tracer.EventType;
import com.abiquo.tracer.SeverityType;

/**
 * This command collects all actions related to users management
 * 
 * @author Oliver
 */
public class UserCommandImpl extends BasicCommand implements UserCommand
{

    /*
     * (non-Javadoc)
     * @see
     * com.abiquo.abiserver.commands.UserCommand#getUsers(com.abiquo.abiserver.pojo.authentication
     * .UserSession, com.abiquo.abiserver.pojo.user.UserListOptions)
     */
    @Override
    public DataResult<UserListResult> getUsers(final UserSession userSession,
        final UserListOptions userListOptions)
    {

        UsersResourceStub proxy =
            APIStubFactory.getInstance(userSession, new UsersResourceStubImpl(),
                UsersResourceStub.class);

        return proxy.getUsers(userListOptions);
    }

    /*
     * (non-Javadoc)
     * @see
     * com.abiquo.abiserver.commands.UserCommand#getUser(com.abiquo.abiserver.pojo.authentication
     * .UserSession, java.lang.Integer)
     */
    @Override
    public DataResult<User> getUser(final UserSession userSession, final Integer idUser)
    {

        UsersResourceStub proxy =
            APIStubFactory.getInstance(userSession, new UsersResourceStubImpl(),
                UsersResourceStub.class);

        return proxy.getUser(idUser);
    }

    /*
     * (non-Javadoc)
     * @see
     * com.abiquo.abiserver.commands.UserCommand#createUser(com.abiquo.abiserver.pojo.authentication
     * .UserSession, com.abiquo.abiserver.pojo.user.User)
     */
    @Override
    public DataResult<User> createUser(final UserSession userSession, final User user)
    {
        DataResult<User> dataResult = null;

        UsersResourceStub proxy =
            APIStubFactory.getInstance(userSession, new UsersResourceStubImpl(),
                UsersResourceStub.class);

        dataResult = proxy.createUser(user);

        if (dataResult.getSuccess())
        {
            dataResult.setMessage(resourceManager.getMessage("createUser.success"));
        }

        return dataResult;
    }

    /*
     * (non-Javadoc)
     * @see
     * com.abiquo.abiserver.commands.UserCommand#editUser(com.abiquo.abiserver.pojo.authentication
     * .UserSession, java.util.ArrayList)
     */
    @Override
    public BasicResult editUser(final UserSession userSession, final ArrayList<User> users)
    {
        BasicResult basicResult = new BasicResult();

        UsersResourceStub proxy =
            APIStubFactory.getInstance(userSession, new UsersResourceStubImpl(),
                UsersResourceStub.class);

        for (User user : users)
        {
            basicResult = proxy.updateUser(user);
            if (basicResult.getSuccess())
            {
                basicResult.setMessage(resourceManager.getMessage("editUser.success"));

            }
            else
            {
                break;
            }
        }

        return basicResult;
    }

    /*
     * (non-Javadoc)
     * @see
     * com.abiquo.abiserver.commands.UserCommand#deleteUser(com.abiquo.abiserver.pojo.authentication
     * .UserSession, com.abiquo.abiserver.pojo.user.User)
     */
    @Override
    public BasicResult deleteUser(final UserSession userSession, final User user)
    {
        BasicResult basicResult = new BasicResult();

        UsersResourceStub proxy =
            APIStubFactory.getInstance(userSession, new UsersResourceStubImpl(),
                UsersResourceStub.class);

        basicResult = proxy.deleteUser(user);
        if (basicResult.getSuccess())
        {
            basicResult.setMessage(resourceManager.getMessage("deleteUser.success"));

        }
        else
        {
            traceLog(SeverityType.CRITICAL, ComponentType.USER, EventType.USER_DELETE, userSession,
                null, null, basicResult.getMessage(), null, null, null, user.getUser(), user
                    .getEnterprise().getName());
        }

        return basicResult;
    }

    /*
     * (non-Javadoc)
     * @seecom.abiquo.abiserver.commands.UserCommand#closeSessionUsers(com.abiquo.abiserver.pojo.
     * authentication.UserSession, java.util.ArrayList)
     */
    @Override
    public BasicResult closeSessionUsers(final UserSession userSession, final ArrayList<User> users)
    {
        BasicResult basicResult = new BasicResult();

        Session session = null;
        Transaction transaction = null;

        try
        {
            session = HibernateUtil.getSession();
            transaction = session.beginTransaction();

            // Generating the list of users for the query

            if (users.size() > 0)
            {
                String userNames = "(";
                User user;
                for (int i = 0; i < users.size(); i++)
                {
                    user = users.get(i);
                    if (i > 0)
                    {
                        userNames = userNames + "," + "'" + user.getUser() + "'";
                    }
                    else
                    {
                        userNames = userNames + "'" + user.getUser() + "'";
                    }
                }
                userNames = userNames + ")";

                // TODO use an HQL query instead of an SQL query
                session.createSQLQuery("DELETE FROM session WHERE user IN " + userNames)
                    .executeUpdate();
            }

            transaction.commit();

            // Generating result
            basicResult.setSuccess(true);
            basicResult.setMessage(resourceManager.getMessage("closeSessionUsers.success"));
        }
        catch (Exception e)
        {
            if (transaction != null && transaction.isActive())
            {
                transaction.rollback();
            }

            // Generating error
            errorManager.reportError(resourceManager, basicResult, "closeSessionUsers", e);
        }

        return basicResult;
    }

    /*
     * (non-Javadoc)
     * @seecom.abiquo.abiserver.commands.UserCommand#closeSessionUsers(com.abiquo.abiserver.pojo.
     * authentication.UserSession)
     */
    @Override
    public BasicResult closeSessionUsers(final UserSession userSession)
    {
        BasicResult basicResult = new BasicResult();

        Session session = null;
        Transaction transaction = null;

        try
        {
            session = HibernateUtil.getSession();
            transaction = session.beginTransaction();

            // Generating a custom query to delete all sessions, except userSession
            String hqlDelete =
                "delete UserSession uS where uS.user != :notUser and uS.key != :notKey";
            session.createQuery(hqlDelete).setString("notUser", userSession.getUser())
                .setString("notKey", userSession.getKey()).executeUpdate();

            transaction.commit();

            // Generating result
            basicResult.setSuccess(true);
            basicResult.setMessage(resourceManager.getMessage("closeSessionUsers.success"));
        }
        catch (Exception e)
        {
            if (transaction != null && transaction.isActive())
            {
                transaction.rollback();
            }

            // Generating error
            errorManager.reportError(resourceManager, basicResult, "closeSessionUsers", e);
        }

        return basicResult;
    }

    // ///////////////////////////////////////
    // ENTERPRISES

    /*
     * (non-Javadoc)
     * @see
     * com.abiquo.abiserver.commands.UserCommand#getEnterprises(com.abiquo.abiserver.pojo.authentication
     * .UserSession, com.abiquo.abiserver.pojo.result.ListRequest)
     */
    @Override
    public DataResult<EnterpriseListResult> getEnterprises(final UserSession userSession,
        final ListRequest enterpriseListOptions)
    {
        EnterprisesResourceStub proxy = getEnterpriseStubProxy(userSession);

        DataResult<EnterpriseListResult> dataResult = proxy.getEnterprises(enterpriseListOptions);
        if (dataResult.getSuccess())
        {
            dataResult.setMessage(resourceManager.getMessage("getEnterprises.success"));
        }

        return dataResult;
    }

    /*
     * (non-Javadoc)
     * @seecom.abiquo.abiserver.commands.UserCommand#createEnterprise(com.abiquo.abiserver.pojo.
     * authentication.UserSession, com.abiquo.abiserver.pojo.user.Enterprise)
     */
    @Override
    public DataResult<Enterprise> createEnterprise(final UserSession userSession,
        final Enterprise enterprise)
    {
        EnterprisesResourceStub proxy = getEnterpriseStubProxy(userSession);

        DataResult<Enterprise> dataResult = proxy.createEnterprise(enterprise);

        // if (dataResult.getSuccess())
        // {
        // traceLog(SeverityType.INFO, ComponentType.ENTERPRISE, EventType.ENTERPRISE_CREATE,
        // userSession, null, null, "Enterprise '" + enterprise.getName()
        // + "' has been created", null, null, null, null, enterprise.getName());
        // }
        return dataResult;
    }

    protected EnterprisesResourceStub getEnterpriseStubProxy(final UserSession userSession)
    {
        EnterprisesResourceStub proxy =
            APIStubFactory.getInstance(userSession, new EnterprisesResourceStubImpl(),
                EnterprisesResourceStub.class);
        return proxy;
    }

    /*
     * (non-Javadoc)
     * @see
     * com.abiquo.abiserver.commands.UserCommand#editEnterprise(com.abiquo.abiserver.pojo.authentication
     * .UserSession, com.abiquo.abiserver.pojo.user.Enterprise)
     */
    @Override
    @SuppressWarnings("unchecked")
    public BasicResult editEnterprise(final UserSession userSession, final Enterprise enterprise)
    {

        // Getting the enterprise that will be edited
        Session session = HibernateUtil.getSession();
        Transaction transaction = session.beginTransaction();

        EnterpriseHB enterpriseHB =
            (EnterpriseHB) session.get(EnterpriseHB.class, enterprise.getId());
        // Checked in API
        // try
        // {
        // checkEditLimits(enterpriseHB, enterprise, userSession);
        // }
        // catch (HardLimitExceededException e)
        // {
        // BasicResult basicResult = new BasicResult();
        // basicResult.setSuccess(false);
        // basicResult.setMessage(resourceManager.getMessage("editEnterprise.limitExceeded"));
        //
        // return basicResult;
        //
        // }
        // finally
        // {
        // transaction.commit();
        // }

        EnterprisesResourceStub proxy = getEnterpriseStubProxy(userSession);

        DataResult<Enterprise> result = new DataResult<Enterprise>();

        result = proxy.editEnterprise(enterprise);

        if (result.getSuccess())
        {

            // Building result
            result.setSuccess(true);
            result.setMessage(resourceManager.getMessage("editEnterprise.success"));

            // Log the event
            // traceLog(SeverityType.INFO, ComponentType.ENTERPRISE, EventType.ENTERPRISE_MODIFY,
            // userSession, null, null, "Enterprise '" + enterprise.getName()
            // + "' has been modified [Name: " + enterprise.getName() + "]", null, null, null,
            // null, enterprise.getName());
        }
        else
        {

            // result.setSuccess(false);
            // result.setMessage(resourceManager.getMessage("editEnterprise.limitExceeded"));
            //
            // errorManager.reportError(resourceManager, result, "editEnterprise",
            // result.getMessage());

            traceLog(SeverityType.CRITICAL, ComponentType.ENTERPRISE, EventType.ENTERPRISE_MODIFY,
                userSession, null, null, result.getMessage(), null, null, null, null,
                enterprise.getName());
        }

        return result;
    }

    protected void checkEditLimits(final EnterpriseHB currentEnterprise,
        final Enterprise newEnterprise, final UserSession userSession)
        throws HardLimitExceededException
    {
        // community impl (no limits at all)
    }

    /*
     * (non-Javadoc)
     * @seecom.abiquo.abiserver.commands.UserCommand#deleteEnterprise(com.abiquo.abiserver.pojo.
     * authentication.UserSession, com.abiquo.abiserver.pojo.user.Enterprise)
     */
    @Override
    public BasicResult deleteEnterprise(final UserSession userSession, final Enterprise enterprise)
    {
        EnterprisesResourceStub proxy = getEnterpriseStubProxy(userSession);

        BasicResult result = proxy.deleteEnterprise(enterprise.getId());

        if (result.getSuccess())
        {
            result.setMessage(resourceManager.getMessage("deleteEnterprise.success"));

            // traceLog(SeverityType.INFO, ComponentType.ENTERPRISE, EventType.ENTERPRISE_DELETE,
            // userSession, null, null, null, null, null, null, null, enterprise.getName());
        }
        else
        {
            traceLog(SeverityType.CRITICAL, ComponentType.ENTERPRISE, EventType.ENTERPRISE_DELETE,
                userSession, null, null, result.getMessage(), null, null, null, null,
                enterprise.getName());
        }

        return result;
    }

    @Override
    public DataResult<Enterprise> getEnterprise(final UserSession userSession,
        final Integer enterpriseId)
    {
        EnterprisesResourceStub proxy = getEnterpriseStubProxy(userSession);

        DataResult<Enterprise> dataResult = proxy.getEnterprise(enterpriseId);

        return dataResult;
    }

    @Override
    public DataResult<Role> getRole(final UserSession userSession, final Integer roleId)
    {
        UsersResourceStub proxy =
            APIStubFactory.getInstance(userSession, new UsersResourceStubImpl(),
                UsersResourceStub.class);

        DataResult<Role> dataResult = proxy.getRole(roleId);

        return dataResult;
    }

    @Override
    public DataResult<RoleListResult> getRoles(final UserSession userSession,
        final ListRequest roleListOptions, final Enterprise enterprise)
    {

        UsersResourceStub proxy =
            APIStubFactory.getInstance(userSession, new UsersResourceStubImpl(),
                UsersResourceStub.class);

        return proxy.getRoles(roleListOptions, enterprise);
    }

    @Override
    public DataResult<PrivilegeListResult> getPrivilegesByRole(final UserSession userSession,
        final int roleId)
    {

        UsersResourceStub proxy =
            APIStubFactory.getInstance(userSession, new UsersResourceStubImpl(),
                UsersResourceStub.class);

        return proxy.getPrivilegesByRole(roleId);
    }

    /*
     * (non-Javadoc)
     * @see
     * com.abiquo.abiserver.commands.com.abiquo.abiserver.commands.UserCommandPremium#checkRolePrivilege
     * (com.abiquo.abiserver.pojo.authentication.UserSession, java.lang.Integer, java.lang.String)
     */
    @Override
    public BasicResult checkRolePrivilege(final UserSession userSession, final Integer idRole,
        final String namePrivilege)
    {
        BasicResult basicResult = new BasicResult();

        UsersResourceStub proxy =
            APIStubFactory.getInstance(userSession, new UsersResourceStubImpl(),
                UsersResourceStub.class);

        basicResult = proxy.checkRolePrivilege(idRole, namePrivilege);
        if (basicResult.getSuccess())
        {
            basicResult.setMessage(resourceManager.getMessage("checkRolePrivilege.success"));
        }

        return basicResult;
    }

    @Override
    public BasicResult checkRoleAccess(final UserSession userSession, final Integer idRole)
    {
        BasicResult basicResult = new BasicResult();

        UsersResourceStub proxy =
            APIStubFactory.getInstance(userSession, new UsersResourceStubImpl(),
                UsersResourceStub.class);

        basicResult = proxy.checkRoleAccess(idRole);
        if (basicResult.getSuccess())
        {
            basicResult.setMessage(resourceManager.getMessage("checkRoleAccess.success"));
        }

        return basicResult;
    }
}
