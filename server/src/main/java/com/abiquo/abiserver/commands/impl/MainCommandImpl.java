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

import com.abiquo.abiserver.commands.BasicCommand;
import com.abiquo.abiserver.commands.MainCommand;
import com.abiquo.abiserver.persistence.hibernate.HibernateUtil;
import com.abiquo.abiserver.pojo.authentication.UserSession;
import com.abiquo.abiserver.pojo.infrastructure.HyperVisorType;
import com.abiquo.abiserver.pojo.main.MainResult;
import com.abiquo.abiserver.pojo.result.DataResult;
import com.abiquo.model.enumerator.HypervisorType;
import com.abiquo.server.core.enterprise.User.AuthType;

/**
 * This command collects all actions related to Main Application actions
 * 
 * @author Oliver
 */

public class MainCommandImpl extends BasicCommand implements MainCommand
{
    @Override
    @SuppressWarnings("unchecked")
    public DataResult<MainResult> getCommonInformation(final UserSession userSession)
    {
        DataResult<MainResult> dataResult = new DataResult<MainResult>();
        ArrayList<HyperVisorType> hypervisorTypesList = new ArrayList<HyperVisorType>();

        Session session = null;
        Transaction transaction = null;

        try
        {
            session = HibernateUtil.getSession();
            transaction = session.beginTransaction();

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
        mainResult.setHypervisorTypes(hypervisorTypesList);

        dataResult.setData(mainResult);
        dataResult.setSuccess(true);
        dataResult.setMessage(resourceManager.getMessage("getCommonInformation.success"));

        return dataResult;
    }
}
