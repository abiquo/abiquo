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
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.abiquo.abiserver.business.hibernate.pojohb.user.RoleHB;
import com.abiquo.abiserver.business.hibernate.pojohb.user.UserHB;
import com.abiquo.abiserver.commands.BasicCommand;
import com.abiquo.abiserver.commands.MainCommand;
import com.abiquo.abiserver.persistence.hibernate.HibernateUtil;
import com.abiquo.abiserver.pojo.authentication.UserSession;
import com.abiquo.abiserver.pojo.infrastructure.HyperVisorType;
import com.abiquo.abiserver.pojo.main.MainResult;
import com.abiquo.abiserver.pojo.result.DataResult;
import com.abiquo.abiserver.pojo.user.Role;
import com.abiquo.server.core.enumerator.HypervisorType;

/**
 * This command collects all actions related to Main Application actions
 * 
 * @author Oliver
 */

public class MainCommandImpl extends BasicCommand implements MainCommand
{
    @Override
    @SuppressWarnings("unchecked")
    public DataResult<MainResult> getCommonInformation(UserSession userSession)
    {
        DataResult<MainResult> dataResult = new DataResult<MainResult>();
        ArrayList<Role> rolesList = new ArrayList<Role>();
        ArrayList<HyperVisorType> hypervisorTypesList = new ArrayList<HyperVisorType>();

        Session session = null;
        Transaction transaction = null;

        try
        {
            session = HibernateUtil.getSession();
            transaction = session.beginTransaction();

            // Getting the user that called this method
            UserHB userHB =
                (UserHB) session.createCriteria(UserHB.class).add(
                    Restrictions.eq("user", userSession.getUser())).uniqueResult();

            // 2 - Retrieving the list of Roles
            // Only the Roles with a security level equal or less than the user who called this
            // method will be returned
            ArrayList<RoleHB> rolesHB =
                (ArrayList<RoleHB>) session.createCriteria(RoleHB.class).addOrder(
                    Order.asc("shortDescription")).list();

            for (RoleHB roleHB : rolesHB)
            {
                // Tip: in securityLevel scale, 1 is the greater level of security, and 99 the
                // lowest
                if (roleHB.getSecurityLevel().compareTo(userHB.getRoleHB().getSecurityLevel()) > -1)
                {
                    // This user can view this role
                    rolesList.add(roleHB.toPojo());
                }
            }

            for (HypervisorType type : HypervisorType.values())
            {
                hypervisorTypesList.add(new HyperVisorType(type));
            }

            transaction.commit();
        }
        catch (Exception e)
        {
            if (transaction != null && transaction.isActive())
            {
                transaction.rollback();
            }

            dataResult.setSuccess(false);
            dataResult.setMessage(e.getMessage());

            errorManager.reportError(resourceManager, dataResult, "getCommonInformation.Exception",
                e);

            return dataResult;
        }

        MainResult mainResult = new MainResult();
        mainResult.setRoles(rolesList);
        mainResult.setHypervisorTypes(hypervisorTypesList);

        dataResult.setData(mainResult);
        dataResult.setSuccess(true);
        dataResult.setMessage(resourceManager.getMessage("getCommonInformation.success"));

        return dataResult;
    }
}
