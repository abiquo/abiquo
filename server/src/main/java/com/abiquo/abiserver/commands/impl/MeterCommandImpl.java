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
import java.util.HashMap;
import java.util.List;

import com.abiquo.abiserver.business.hibernate.pojohb.metering.MeterHB;
import com.abiquo.abiserver.business.hibernate.pojohb.user.UserHB;
import com.abiquo.abiserver.commands.BasicCommand;
import com.abiquo.abiserver.commands.MeterCommand;
import com.abiquo.abiserver.commands.stub.APIStubFactory;
import com.abiquo.abiserver.commands.stub.UsersResourceStub;
import com.abiquo.abiserver.commands.stub.impl.UsersResourceStubImpl;
import com.abiquo.abiserver.exception.MeterCommandException;
import com.abiquo.abiserver.exception.PersistenceException;
import com.abiquo.abiserver.persistence.DAOFactory;
import com.abiquo.abiserver.persistence.dao.metering.MeterDAO;
import com.abiquo.abiserver.persistence.dao.user.UserDAO;
import com.abiquo.abiserver.persistence.hibernate.HibernateDAOFactory;
import com.abiquo.abiserver.pojo.authentication.UserSession;
import com.abiquo.abiserver.pojo.result.DataResult;
import com.abiquo.abiserver.pojo.user.User;
import com.abiquo.abiserver.pojo.user.UserListOptions;
import com.abiquo.abiserver.pojo.user.UserListResult;
import com.abiquo.tracer.EventType;
import com.abiquo.tracer.SeverityType;

/**
 * @author jdevesa
 */
public class MeterCommandImpl extends BasicCommand implements MeterCommand
{

    // private UsersResourceStub userResourceStub;

    /**
     * To create DAOs and manage transactions
     */
    DAOFactory factory;

    public MeterCommandImpl()
    {
        // get the factory from hibernate framework
        factory = HibernateDAOFactory.instance();
        // userResourceStub = new UsersResourceStubImpl();
    }

    /*
     * (non-Javadoc)
     * @see
     * com.abiquo.abiserver.commands.MeteringCommand#getMeter(com.abiquo.abiserver.pojo.authentication
     * .UserSession, java.util.HashMap)
     */
    @Override
    public List<MeterHB> getMeters(final UserSession userSession,
        final HashMap<String, String> filters, final Integer numrows) throws MeterCommandException
    {
        List<MeterHB> listOfMeters = new ArrayList<MeterHB>();
        UserDAO userDAO = factory.getUserDAO();
        MeterDAO meterDAO = factory.getMeterDAO();

        try
        {
            // We split all the users inside the string separated by "/";
            List<String> listOfUsers = new ArrayList<String>();

            UsersResourceStub proxy =
                APIStubFactory.getInstance(userSession, new UsersResourceStubImpl(),
                    UsersResourceStub.class);
            DataResult<UserListResult> users = proxy.getOnlyUsers(new UserListOptions());

            if (users.getData() != null && users.getData().getUsersList() != null
                && !users.getData().getUsersList().isEmpty())
            {
                for (User u : users.getData().getUsersList())
                {
                    listOfUsers.add(u.getUser());

                }
            }

            factory.beginConnection();

            // UserHB user = userDAO.getUserByUserName(userSession.getUser());
            UserHB user =
                userDAO.findUserHBById(userSession.getUserIdDb(), userSession.getAuthType());

            listOfMeters = meterDAO.findAllByFilter(filters, listOfUsers, numrows, user);

            factory.endConnection();
        }

        catch (PersistenceException e)
        {
            throw new MeterCommandException(e.getMessage(), e);
        }

        return listOfMeters;
    }

    /*
     * (non-Javadoc)
     * @see com.abiquo.abiserver.commands.MeterCommand#getListOfEventTypes()
     */
    @Override
    public List<String> getListOfEventTypes()
    {
        List<String> listOfEventTypes = new ArrayList<String>();

        for (EventType event : EventType.values())
        {
            listOfEventTypes.add(event.name());
        }

        return listOfEventTypes;
    }

    /*
     * (non-Javadoc)
     * @see com.abiquo.abiserver.commands.MeterCommand#getListOfSeverityTypes()
     */
    @Override
    public List<String> getListOfSeverityTypes()
    {
        List<String> listOfSeverityTypes = new ArrayList<String>();

        for (SeverityType severity : SeverityType.values())
        {
            listOfSeverityTypes.add(severity.name());
        }

        return listOfSeverityTypes;
    }

    /**
     * @return the factory
     */
    public DAOFactory getFactory()
    {
        return factory;
    }

    /**
     * @param factory the factory to set
     */
    public void setFactory(final DAOFactory factory)
    {
        this.factory = factory;
    }

}
