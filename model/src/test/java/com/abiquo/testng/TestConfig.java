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
package com.abiquo.testng;

/**
 * Common configuration to customize test execution.
 * 
 * @author ibarrera
 */
public class TestConfig
{
    // Test group configuration

    /** TestNG group for default unit tests. */
    public static final String BASIC_UNIT_TESTS = "test-basic";

    /** TestNG group for default integration tests. */
    public static final String BASIC_INTEGRATION_TESTS = "it-basic";

    /** TestNG group for networking unit tests. */
    public static final String NETWORK_UNIT_TESTS = "test-network";

    /** TestNG group for networking integration tests. */
    public static final String NETWORK_INTEGRATION_TESTS = "it-network";

    /** TestNG group for appliance library tests. */
    public static final String AM_INTEGRATION_TESTS = "it-am";

    /** TestNG group for all unit tests. */
    public static final String ALL_UNIT_TESTS = "test-all";

    /** TestNG group for all integration tests. */
    public static final String ALL_INTEGRATION_TESTS = "it-all";

    /** TestNG group for default premium unit tests. */
    public static final String PREMIUM_BASIC_UNIT_TESTS = "test-premium-basic";

    /** TestNG group for default premium integration tests. */
    public static final String PREMIUM_BASIC_INTEGRATION_TESTS = "it-premium-basic";

    /** TestNG group for networking premium unit tests. */
    public static final String PREMIUM_NETWORK_UNIT_TESTS = "test-premium-network";

    /** TestNG group for networking premium integration tests. */
    public static final String PREMIUM_NETWORK_INTEGRATION_TESTS = "it-premium-network";

    /** TestNG group for all premium unit tests. */
    public static final String PREMIUM_ALL_UNIT_TESTS = "test-premium-all";

    /** TestNG group for all premium integration tests. */
    public static final String PREMIUM_ALL_INTEGRATION_TESTS = "it-premium-all";

    // Test database configuration

    /** The default test database user. */
    public static final String DEFAULT_DB_USER = "root";

    /** The default test database pass. */
    public static final String DEFAULT_DB_PASS = "root";

    /** Default test server port. */
    public static final String DEFAULT_SERVER_PORT = "9009";

    // Utility methods

    public static String getParameter(final String name, final String defaultValue)
    {
        return System.getProperty(name, defaultValue);
    }

    public static String getParameter(final String name)
    {
        String parameter = System.getProperty(name);
        if (parameter == null)
        {
            throw new RuntimeException("Missing parameter: " + name);
        }
        return parameter;
    }
}
