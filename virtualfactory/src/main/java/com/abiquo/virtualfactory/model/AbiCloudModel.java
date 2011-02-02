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

package com.abiquo.virtualfactory.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.abiquo.virtualfactory.model.config.ConfigurationManager;

/**
 * Class representing AbiCloud's Model It is a Singleton call
 * 
 * @author pnavarro
 */
public class AbiCloudModel
{
    /** The logger */
    private static final Logger logger = LoggerFactory.getLogger(AbiCloudModel.class);

    /** The singleton instance for this class. */
    private static AbiCloudModel singleton;

    private ConfigurationManager configManager;

    /**
     * Default Constructor
     */
    public AbiCloudModel()
    {
        configManager = new ConfigurationManager();
    }

    /**
     * Singleton accessor.
     * 
     * @return the model
     */
    public static AbiCloudModel getInstance()
    {
        if (singleton == null)
        {
            singleton = new AbiCloudModel();
        }

        return singleton;
    }

    /**
     * Gets the configuration manager
     * 
     * @return the configuration manager
     */
    public ConfigurationManager getConfigManager()
    {
        return configManager;
    }

    /**
     * Sets the configuration manager
     * 
     * @param configManager the configuration manager
     */
    public void setConfigManager(ConfigurationManager configManager)
    {
        this.configManager = configManager;
    }
}
