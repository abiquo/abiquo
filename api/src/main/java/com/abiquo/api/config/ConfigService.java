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

package com.abiquo.api.config;

import org.springframework.stereotype.Service;

import com.abiquo.server.core.enterprise.User;

@Service
public class ConfigService
{

    public static final String SECURITY_MODE = "abiquo.auth.module";// "abiquo.security.mode";

    public static final String SERVER_TIMEOUT = "abiquo.server.timeout";

    public static final String VLAN_PER_VDC = "abiquo.server.networking.vlanPerVdc";

    public static String getSystemProperty(final String property)
    {
        return System.getProperty(property);
    }

    public static String getSystemProperty(final String property, final String defaultValue)
    {
        return System.getProperty(property, defaultValue);
    }

    public static String getSecurityMode()
    {
        return getSystemProperty(SECURITY_MODE, User.AuthType.ABIQUO.toString());

    }

    public static String getServerTimeout()
    {
        return getSystemProperty(SERVER_TIMEOUT, "0");

    }

    public static String getVlanPerVdc()
    {
        return getSystemProperty(VLAN_PER_VDC, "0");

    }
}
