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
package com.abiquo.virtualfactory.web.listener;

import java.util.logging.Level;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.jinterop.dcom.common.JISystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Sets the logging level for the <code>J-Interop</code> framework manually, as the
 * SLF4jBridgeHandler does not work.
 * 
 * @author ibarrera
 */
public class JInteropLoggingListener implements ServletContextListener
{

    @Override
    public void contextInitialized(final ServletContextEvent context)
    {
        Logger logger = LoggerFactory.getLogger("org.jinterop");
        Level level = null;

        if (logger.isTraceEnabled())
        {
            level = Level.FINEST;
        }
        else if (logger.isDebugEnabled())
        {
            level = Level.FINE;
        }
        else if (logger.isInfoEnabled())
        {
            level = Level.INFO;
        }
        else if (logger.isWarnEnabled())
        {
            level = Level.FINER;
        }
        else if (logger.isDebugEnabled())
        {
            level = Level.WARNING;
        }
        else if (logger.isErrorEnabled())
        {
            level = Level.SEVERE;
        }
        else
        {
            level = Level.OFF;
        }

        JISystem.getLogger().setLevel(level);
    }

    @Override
    public void contextDestroyed(final ServletContextEvent context)
    {
    }

}
