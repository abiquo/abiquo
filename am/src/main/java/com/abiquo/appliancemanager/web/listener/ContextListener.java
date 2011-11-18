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

import java.io.IOException;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.abiquo.am.services.notify.AMNotifier;
import com.abiquo.appliancemanager.config.AMConfigurationManager;
import com.abiquo.commons.amqp.impl.am.AMProducer;
import com.abiquo.ovfmanager.ovf.xml.OVFSerializer;

/**
 * Application Lifecycle Listener implementation class ContextListener.
 * <p>
 * It opens the {@link AMProducer} AMQP channel
 */
public class ContextListener implements ServletContextListener
{
    public static final Logger LOG = LoggerFactory.getLogger(ContextListener.class);

    @Override
    public void contextInitialized(final ServletContextEvent sce)
    {

        OVFSerializer.getInstance().setValidateXML(false); // TODO delme

        try
        {
            WebApplicationContextUtils.getRequiredWebApplicationContext(sce.getServletContext())
                .getBean("AMNotifier", AMNotifier.class).openChannel();
            LOG.debug("AM amqp producer channel open");
        }
        catch (IOException e)
        {
            LOG.error("Can not open the AMQP channel ", e);
            AMConfigurationManager.getInstance().addConfigurationError(e.getLocalizedMessage());
        }
    }

    @Override
    public void contextDestroyed(final ServletContextEvent sce)
    {
        try
        {
            WebApplicationContextUtils.getRequiredWebApplicationContext(sce.getServletContext())
                .getBean("AMNotifier", AMNotifier.class).closeChannel();
            LOG.debug("AM amqp producer channel closed");
        }
        catch (Exception e)
        {
            LOG.error("Can not close the AMQP channel ", e);
        }
    }
}
