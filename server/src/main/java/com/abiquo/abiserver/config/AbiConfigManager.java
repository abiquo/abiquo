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

package com.abiquo.abiserver.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Configuration Manager for Community version of abicloud_server
 */
public class AbiConfigManager
{
    protected static final Logger logger = LoggerFactory.getLogger(AbiConfigManager.class);

    /** Singleton object reference */
    private static AbiConfigManager singleton;

    /** The main configuration object for all the abiCloud resources */
    protected AbiConfig abiConfig = new AbiConfig();

    /**
     * Static method that returns the singleton instance of AbiConfiManager
     * 
     * @return a reference to the singleton instance of AbiConfigManager
     */
    public static AbiConfigManager getInstance()
    {
        if (singleton == null)
        {
            singleton = new AbiConfigManager();
        }

        return singleton;
    }

    /**
     * @return a reference to the singleton object of the Configuration class which is an object of
     *         <code>AbiConfig</coded> or any of class inheriting from it
     */
    @SuppressWarnings("unchecked")
    public <T extends AbiConfig> T getAbiConfig()
    {
        return (T) abiConfig;
    }

}
