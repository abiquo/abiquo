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

package com.abiquo.api.util;

import java.io.IOException;

import javax.servlet.ServletException;

import org.apache.wink.common.internal.i18n.Messages;
import org.apache.wink.common.internal.utils.ClassUtils;
import org.apache.wink.server.internal.DeploymentConfiguration;
import org.apache.wink.server.internal.servlet.RestServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.abiquo.api.wink.AbiquoDeploymentConfiguration;

/**
 * Workaround until WINK 1.1.1 will be released with the fix for WINK-290.
 * 
 * @author dcalavera
 */
public class AbiquoRestServlet extends RestServlet
{
    private static final Logger logger = LoggerFactory.getLogger(AbiquoRestServlet.class);

    @Override
    public void init() throws ServletException
    {
        logger.info("Initializing AbiquoRestServlet...");
        super.init();
        DeploymentConfiguration configuration = getRequestProcessor().getConfiguration();
        configuration.setServletConfig(getServletConfig());
        configuration.setServletContext(getServletContext());
    }

//    @Override
//    protected DeploymentConfiguration getDeploymentConfiguration() throws ClassNotFoundException,
//        InstantiationException, IllegalAccessException, IOException
//    {
//        logger.info("Initializing Abiquo's own DeploymentConfiguration entity");
//        AbiquoDeploymentConfiguration deploymentConfiguration = new AbiquoDeploymentConfiguration();
//        deploymentConfiguration.setServletConfig(getServletConfig());
//        deploymentConfiguration.setServletContext(getServletContext());
//        deploymentConfiguration.setProperties(getProperties());
//        deploymentConfiguration.init();
//        return deploymentConfiguration;
//    }
}
