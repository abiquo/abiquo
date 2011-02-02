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

package com.abiquo.virtualfactory.model.ovf.test;

/**
 * The Class TestConfiguration.
 */
public class ConfigurationTest
{

    /** The abicloud_ w s_location. */
    private String abicloud_WS_location;

    /** The request_timeout. */
    private long request_timeout;

    /** The sleep_time. */
    private long sleep_time;

    /**
     * Instantiates a new test configuration.
     * 
     * @param abicloud_WS_location the abicloud_ w s_location
     * @param request_timeout the request_timeout
     * @param sleep_time
     */
    public ConfigurationTest(String abicloud_WS_location, long request_timeout, long sleep_time)
    {
        this.abicloud_WS_location = abicloud_WS_location;
        this.request_timeout = request_timeout;
        this.sleep_time = sleep_time;
    }

    /**
     * Gets the abicloud_ w s_location.
     * 
     * @return the abicloud_ w s_location
     */
    public String getAbicloud_WS_location()
    {
        return abicloud_WS_location;
    }

    /**
     * Sets the abicloud_ w s_location.
     * 
     * @param abicloud_WS_location the new abicloud_ w s_location
     */
    public void setAbicloud_WS_location(String abicloud_WS_location)
    {
        this.abicloud_WS_location = abicloud_WS_location;
    }

    /**
     * Gets the request_timeout.
     * 
     * @return the request_timeout
     */
    public long getRequest_timeout()
    {
        return request_timeout;
    }

    /**
     * Sets the request_timeout.
     * 
     * @param request_timeout the new request_timeout
     */
    public void setRequest_timeout(long request_timeout)
    {
        this.request_timeout = request_timeout;
    }

    /**
     * Gets the sleep_time.
     * 
     * @return the sleep_time
     */
    public long getSleep_time()
    {
        return sleep_time;
    }

    /**
     * Sets the sleep_time.
     * 
     * @param sleep_time the new sleep_time
     */
    public void setSleep_time(long sleep_time)
    {
        this.sleep_time = sleep_time;
    }

}
