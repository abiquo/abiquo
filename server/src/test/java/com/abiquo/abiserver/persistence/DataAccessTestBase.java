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

package com.abiquo.abiserver.persistence;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

public class DataAccessTestBase
{

    private static SessionFactory factory;

    private List<Session> allocatedSessions = new ArrayList<Session>();

    private List<Transaction> ongoingTransactions = new ArrayList<Transaction>();

    /**
     * Removes all data from the database. WARNING: Unfortunately, this is an ongoing task: we have
     * no time to implement this fully, just what is needed for a handful of tests.
     * 
     * @throws SQLException
     */
    @SuppressWarnings("deprecation")
    protected void cleanDatabase() throws SQLException
    {
        String[] tablesInDeletionOrder =
            new String[] {"physicalmachine", "rack", "datacenter", "user", "enterprise",
            "workload_enterprise_exclusion_rule", "workload_machine_load_rule"};

        Session session = factory.openSession();
        try
        {
            Connection connection = session.connection();
            for (String table : tablesInDeletionOrder)
            {
                Statement delete = connection.createStatement();
                try
                {
                    delete.execute("DELETE FROM " + table);
                }
                finally
                {
                    delete.close();
                }
            }
        }
        finally
        {
            session.close();
        }
    }

    private static void ensureFactoryInitialized()
    {
        if (factory == null)
        {
            factory =
                new Configuration().configure("/conf/db/hibernate/hibernate.cfg.xml")
                    .buildSessionFactory();
        }
    }

    private static SessionFactory getFactory()
    {
        ensureFactoryInitialized();
        return factory;
    }

    protected Session createSession()
    {
        Session session = getFactory().openSession();
        allocatedSessions.add(session);
        return session;
    }

    protected Session createSessionInTransaction()
    {
        Session session = createSession();
        allocatedSessions.add(session);
        Transaction transaction = session.beginTransaction();
        ongoingTransactions.add(transaction);
        return session;
    }

    @BeforeMethod
    public void methodSetUp() throws Exception
    {
        ensureFactoryInitialized();
        cleanDatabase();
    }

    @AfterMethod
    public void methodTearDown()
    {
        ensureOngoingTransactionsCleanup();
        ensureOpenSessionsCleanup();
    }

    private void ensureOngoingTransactionsCleanup()
    {
        for (Transaction t : ongoingTransactions)
        {
            if (t.isActive())
            {
                t.rollback();
            }
        }
    }

    private void ensureOpenSessionsCleanup()
    {
        for (Session s : allocatedSessions)
        {
            if (s.isOpen())
            {
                s.close();
            }
        }
    }

}
