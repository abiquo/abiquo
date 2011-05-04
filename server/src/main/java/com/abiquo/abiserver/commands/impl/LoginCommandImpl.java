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

import com.abiquo.abiserver.business.AuthService;
import com.abiquo.abiserver.business.hibernate.pojohb.user.PrivilegeHB;
import com.abiquo.abiserver.business.hibernate.pojohb.user.UserHB;
import com.abiquo.abiserver.commands.BasicCommand;
import com.abiquo.abiserver.commands.LoginCommand;
import com.abiquo.abiserver.persistence.hibernate.HibernateUtil;
import com.abiquo.abiserver.pojo.authentication.Login;
import com.abiquo.abiserver.pojo.authentication.LoginResult;
import com.abiquo.abiserver.pojo.authentication.UserSession;
import com.abiquo.abiserver.pojo.result.BasicResult;
import com.abiquo.abiserver.pojo.result.DataResult;
import com.abiquo.abiserver.pojo.user.Privilege;
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

    @Override
    @SuppressWarnings("unchecked")
    public DataResult<LoginResult> login(final Login loginData)
    {
        DataResult<LoginResult> resultResponse = AuthService.getInstance().doLogin(loginData);

        if (resultResponse.getSuccess())
        {
            // Generating the list of client resources for the user that has
            // logged in
            Session session = null;
            Transaction transaction = null;

            ArrayList<Privilege> clientPrivileges = new ArrayList<Privilege>();

            try
            {
                session = HibernateUtil.getSession();
                transaction = session.beginTransaction();

                // Getting the user that is being loggin in
                UserHB userHBLogged =
                    (UserHB) session.get(UserHB.class, resultResponse.getData().getUser().getId());

                // Getting the list of user privileges
                ArrayList<PrivilegeHB> allUserPrivilegesHB =
                    (ArrayList<PrivilegeHB>) userHBLogged.getRoleHB().getPrivilegesHB();

                for (PrivilegeHB userPrivilegeHB : allUserPrivilegesHB)
                {
                    clientPrivileges.add(userPrivilegeHB.toPojo());

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
            resultResponse.getData().setClientPrivileges(clientPrivileges);
        }

        return resultResponse;
    }

    /*
     * (non-Javadoc)
     * @see
     * com.abiquo.abiserver.commands.impl.LoginCommand#logout(com.abiquo.abiserver.pojo.authentication
     * .UserSession)
     */
    @Override
    public BasicResult logout(final UserSession session)
    {
        traceLog(SeverityType.INFO, ComponentType.USER, EventType.USER_LOGOUT, session, null, null,
            null, null, null, null, null, null);

        return AuthService.getInstance().doLogout(session);
    }

}
