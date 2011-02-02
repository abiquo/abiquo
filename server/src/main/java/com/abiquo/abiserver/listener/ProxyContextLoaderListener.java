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

package com.abiquo.abiserver.listener;

import javax.servlet.ServletContextEvent;

import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;

import com.abiquo.abiserver.config.AbiConfig;
import com.abiquo.abiserver.config.AbiConfigManager;

public class ProxyContextLoaderListener extends ContextLoaderListener
{
    private static WebApplicationContext ctx;

    private static final String SERVER_NAME = "server.name";

    private static final String SERVER_VERSION = "server.version";

    private static final String ABICLOUD_VERSION = "abicloud.version";

    private static final String ABICLOUD_DISTRIBUTION = "abicloud.distribution";

    @Override
    public void contextInitialized(final ServletContextEvent event)
    {
        super.contextInitialized(event);

        ctx = ContextLoader.getCurrentWebApplicationContext();

        setAbicloudSystemProperties(event);
    }

    @Override
    public void contextDestroyed(final ServletContextEvent event)
    {
        super.contextDestroyed(event);
        ctx = null;
    }

    public static WebApplicationContext getCtx()
    {
        return ctx;
    }

    private void setAbicloudSystemProperties(final ServletContextEvent event)
    {
        String serverInfo = event.getServletContext().getServerInfo();
        String serverName = null;
        String serverVersion = null;

        if (serverInfo != null && serverInfo.indexOf("/") != -1)
        {
            String[] info = serverInfo.split("/");

            serverName = info[0];
            serverVersion = info[1];
        }
        else if (serverInfo != null)
        {
            serverName = serverInfo;
        }

        System.setProperty(SERVER_NAME, serverName);
        System.setProperty(SERVER_VERSION, serverVersion);

        AbiConfig config = AbiConfigManager.getInstance().getAbiConfig();

        if (config != null)
        {
            System.setProperty(ABICLOUD_VERSION, config.getAbiquoVersion());
            System.setProperty(ABICLOUD_DISTRIBUTION, config.getAbiquoDistribution());
        }

    }
}
