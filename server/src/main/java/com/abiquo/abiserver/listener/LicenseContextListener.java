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
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Application Lifecycle Listener implementation class ContextListener
 */
public class LicenseContextListener implements ServletContextListener
{

    public static final Logger logger = LoggerFactory.getLogger("");
    
    @Override
    public void contextInitialized(ServletContextEvent sce)
    {
    	logger.info("********************************************************************************");    
    	logger.info("*                              ABICLOUD: SERVER                                *");
    	logger.info("********************************************************************************");
        logger.info("* This application is free software; you can redistribute it and/or modify it  *");
        logger.info("* under the terms of the GNU LESSER GENERAL PUBLIC LICENSE as published by     *");	
        logger.info("* the Free Software Foundation under version 3 of the License.                 *");
        logger.info("*                                                                              *");
        logger.info("* This software is distributed in the hope that it will be useful, but WITHOUT *"); 
        logger.info("* ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or        *");
        logger.info("* FITNESS FOR A PARTICULAR PURPOSE.  See the GNU LESSER GENERAL PUBLIC LICENSE *");
        logger.info("* v.3 for more details.                                                        *");
    	logger.info("********************************************************************************");

    }

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		
	}

}
