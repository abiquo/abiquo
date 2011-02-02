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
import org.hibernate.criterion.Restrictions;

import com.abiquo.abiserver.business.AuthService;
import com.abiquo.abiserver.business.hibernate.pojohb.authorization.AuthClientResourceHB;
import com.abiquo.abiserver.business.hibernate.pojohb.authorization.AuthClientresourceExceptionHB;
import com.abiquo.abiserver.business.hibernate.pojohb.user.UserHB;
import com.abiquo.abiserver.commands.BasicCommand;
import com.abiquo.abiserver.commands.LoginCommand;
import com.abiquo.abiserver.persistence.hibernate.HibernateUtil;
import com.abiquo.abiserver.pojo.authentication.Login;
import com.abiquo.abiserver.pojo.authentication.LoginResult;
import com.abiquo.abiserver.pojo.authentication.UserSession;
import com.abiquo.abiserver.pojo.authorization.Resource;
import com.abiquo.abiserver.pojo.result.BasicResult;
import com.abiquo.abiserver.pojo.result.DataResult;
import com.abiquo.tracer.ComponentType;
import com.abiquo.tracer.EventType;
import com.abiquo.tracer.SeverityType;

/**
 * This command collects all actions related to Login actions
 * 
 * @author Oliver
 */

public class LoginCommandImpl extends BasicCommand implements LoginCommand
{

    /*
     * (non-Javadoc)
     * @see
     * com.abiquo.abiserver.commands.impl.LoginCommand#login(com.abiquo.abiserver.pojo.authentication
     * .Login)
     */
    @SuppressWarnings("unchecked")
    public DataResult<LoginResult> login(Login loginData)
    {
        DataResult<LoginResult> resultResponse = AuthService.getInstance().doLogin(loginData);

        if (resultResponse.getSuccess())
        {
            // Generating the list of client resources for the user that has
            // logged in
            Session session = null;
            Transaction transaction = null;

            ArrayList<Resource> userResources = new ArrayList<Resource>();

            try
            {
                session = HibernateUtil.getSession();
                transaction = session.beginTransaction();

                // Getting the user that is being loggin in
                UserHB userHBLogged =
                    (UserHB) session.get(UserHB.class, resultResponse.getData().getUser().getId());

                // Getting the list of all client resources
                ArrayList<AuthClientResourceHB> allClientResourcesHB =
                    (ArrayList<AuthClientResourceHB>) session.createCriteria(
                        AuthClientResourceHB.class).list();

                AuthClientresourceExceptionHB authClientresourceExceptionHB;
                for (AuthClientResourceHB authClientResourceHB : allClientResourcesHB)
                {
                    // Checking if there is any exception for this client
                    // resource and this user
                    authClientresourceExceptionHB = null;
                    authClientresourceExceptionHB =
                        (AuthClientresourceExceptionHB) session.createCriteria(
                            AuthClientresourceExceptionHB.class).add(
                            Restrictions.eq("userHB", userHBLogged)).add(
                            Restrictions.eq("authResourceHB", authClientResourceHB)).uniqueResult();

                    int priorAuth =
                        authClientResourceHB.getRoleHB().getSecurityLevel().compareTo(
                            userHBLogged.getRoleHB().getSecurityLevel());
                    if (priorAuth >= 0)
                    {
                        // User has authorization for this client resource.
                        // Checking if there is any
                        // exception for that
                        if (authClientresourceExceptionHB == null)
                        {
                            // No exceptions. Adding the client resource for
                            // this user
                            userResources.add(authClientResourceHB.toPojo());
                        }
                        else
                        {
                            // There is an exception, so this user is not
                            // authorized to use this
                            // client resource
                            // We do not add this client resource
                        }
                    }
                    else
                    {
                        // User is not authorized for this client resource.
                        // Checking if there is any
                        // exception for that
                        if (authClientresourceExceptionHB != null)
                        {
                            // An exception exists, so this user is authorized.
                            // Adding the client
                            // resource
                            userResources.add(authClientResourceHB.toPojo());
                        }
                        else
                        {
                            // No exception exists, so we do not add this client
                            // resource
                        }
                    }
                }

                transaction.commit();

                // log the event
                traceLog(SeverityType.INFO, ComponentType.USER, EventType.USER_LOGIN,
                    resultResponse.getData().getSession(), null, null, null, null, null, null,
                    resultResponse.getData().getSession().getUser(), resultResponse.getData()
                        .getSession().getEnterpriseName());

            }
            catch (Exception e)
            {
                if (transaction != null && transaction.isActive())
                {
                    transaction.rollback();
                }

                errorManager.reportError(resourceManager, resultResponse, "login.resourceCreation",
                    e);

                traceLog(SeverityType.CRITICAL, ComponentType.USER, EventType.USER_LOGIN,
                    resultResponse.getData().getSession(), null, null, e.getMessage(), null, null,
                    null, resultResponse.getData().getUser().getUser(), resultResponse.getData()
                        .getSession().getEnterpriseName());

                return resultResponse;
            }

            // Returning result
            resultResponse.getData().setClientResources(userResources);
        }

        return resultResponse;
    }

    /*
     * (non-Javadoc)
     * @see
     * com.abiquo.abiserver.commands.impl.LoginCommand#logout(com.abiquo.abiserver.pojo.authentication
     * .UserSession)
     */
    public BasicResult logout(UserSession session)
    {
        traceLog(SeverityType.INFO, ComponentType.USER, EventType.USER_LOGOUT, session, null, null,
            null, null, null, null, null, null);

        return AuthService.getInstance().doLogout(session);
    }

}
