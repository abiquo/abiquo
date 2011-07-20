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

package com.abiquo.nodecollector.web.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.abiquo.nodecollector.domain.PluginLoader;

/**
 * Listener to load all the plugins when the web application context is initialized.
 * @author jdevesa@abiquo.com
 */
public class PluginLoaderListener implements ServletContextListener
{

    public static final Logger LOGGER = LoggerFactory.getLogger(PluginLoaderListener.class);
    
    @Override
    public void contextDestroyed(ServletContextEvent sce)
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void contextInitialized(ServletContextEvent sce)
    {
        try
        {
            PluginLoader lp = PluginLoader.getInstance();
            lp.loadPlugins();
        }
        catch (Exception e)
        {
            LOGGER.error("An error occurred while loading the plugins", e);
        }
    }

}
