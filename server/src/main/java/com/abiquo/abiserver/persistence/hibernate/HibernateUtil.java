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

package com.abiquo.abiserver.persistence.hibernate;

import java.io.File;
import java.net.URL;

import org.hibernate.FlushMode;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.classic.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO documentation
 */
public class HibernateUtil
{

    private static final SessionFactory sessionFactory;

    private static final Logger logger = LoggerFactory.getLogger(HibernateUtil.class);

    static
    {
        try
        {
            logger.info("Creating SessionFactory .... ");

            String hibernateConfRootDir = "conf/db/hibernate/";

            // Create the SessionFactory from hibernate.cfg.xml
            Configuration conf = new Configuration();
            conf.configure(hibernateConfRootDir + "hibernate.cfg.xml");

            hibernateConfRootDir += "ext/";
            URL url = HibernateUtil.class.getClassLoader().getResource(hibernateConfRootDir);

            if (url != null)
            {

                logger.info("Loading extra hibernate configurations ...");

                for (String extFileName : new File(url.toURI()).list())
                {
                    conf.configure(hibernateConfRootDir + extFileName);
                }

                logger.info("Loaded extra hibernate configurations");

            }

            sessionFactory = conf.buildSessionFactory();
            logger.info("SessionFactory created!");

        }
        catch (Throwable ex)
        {
            // Make sure you log the exception, as it might be swallowed
            logger.error("Initial SessionFactory creation failed.", ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static SessionFactory getSessionFactory()
    {
        return sessionFactory;
    }

    public static void beginTransaction()
    {
        sessionFactory.getCurrentSession().beginTransaction();
    }

    // public static SessionFactory getSessionFactory()
    // {
    // return sessionFactory;
    //
    // }

    public static void commitTransaction()
    {

        Transaction transaction = sessionFactory.getCurrentSession().getTransaction();

        if (transaction != null && transaction.isActive())
        {
            transaction.commit();
        }
    }

    public static void rollbackTransaction()
    {

        Transaction transaction = sessionFactory.getCurrentSession().getTransaction();

        if (transaction != null && transaction.isActive())
        {
            transaction.rollback();
        }

    }

    public static Session getSession()
    {
        return getSession(false);
    }

    public static Session getSession(final boolean ro)
    {
        if (ro)
        {
            sessionFactory.getCurrentSession().setFlushMode(FlushMode.MANUAL);
        }
        else
        {
            sessionFactory.getCurrentSession().setFlushMode(FlushMode.AUTO);
        }
        return sessionFactory.getCurrentSession();
    }
    //
    // public static Criteria createCriteria(Class clase)
    // {
    // return sessionFactory.getCurrentSession().createCriteria(clase);
    // }

}
