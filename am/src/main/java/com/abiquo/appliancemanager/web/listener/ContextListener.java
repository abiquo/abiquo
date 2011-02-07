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

package com.abiquo.appliancemanager.web.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.abiquo.am.services.util.TimeoutFSUtils;
import com.abiquo.am.services.notify.AMNotifierFactory;

/**
 * Application Lifecycle Listener implementation class ContextListener
 */
public class ContextListener implements ServletContextListener
{

    public static final Logger logger = LoggerFactory.getLogger(ContextListener.class);

    @Override
    public void contextInitialized(ServletContextEvent sce)
    {

        logger.info("Initializing the context [" + sce.getServletContext().getServletContextName()
            + "] ...");

        // TODO check the AMProducer can be created

        logger.info("The context [" + sce.getServletContext().getServletContextName()
            + "] initialized!");

    }

    @Override
    public void contextDestroyed(ServletContextEvent sce)
    {

        logger.info("Destroying the context  [" + sce.getServletContext().getServletContextName()
            + "] ... ");

        try
        {
            AMNotifierFactory.destroy();
        }
        catch (Exception e)
        {
            logger.error("Can not close AMQP channel ", e);

        }

        // TimeoutFSUtils.getInstance().destroyExecutor();

        logger.info("The context [" + sce.getServletContext().getServletContextName()
            + "] has been destroyed");
    }
}
