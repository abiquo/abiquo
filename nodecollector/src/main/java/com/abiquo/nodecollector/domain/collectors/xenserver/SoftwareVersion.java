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

package com.abiquo.nodecollector.domain.collectors.xenserver;

import java.util.Map;

import com.xensource.xenapi.Host;

/**
 * Details of the XenServer version.
 * 
 * @author Ignasi Barrera
 */
public class SoftwareVersion
{
    /** Software version properties. */
    private Map<String, String> props;

    public static SoftwareVersion of(final Host.Record host)
    {
        return new SoftwareVersion(host.softwareVersion);
    }

    private SoftwareVersion(final Map<String, String> props)
    {
        super();
        this.props = props;
    }

    public String productVersion()
    {
        String version = props.get("product_version_text");

        // XenServer 5 and previous version do not have the 'product_version_text' field
        if (version == null)
        {
            version = props.get("product_version");
        }

        return version;
    }

    public String rawProductVersion()
    {
        // In XenServer the "printable" version is stored in another field
        return props.get("product_version");
    }

    public boolean isVersion6OrGreater()
    {
        return Character.getNumericValue(productVersion().charAt(0)) >= 6;
    }

    public String buildNumber()
    {
        return props.get("build_number");
    }

    public boolean hasLinuxPack()
    {
        if (isVersion6OrGreater())
        {
            // XenServer 6 includes by default the Linux pack
            return true;
        }

        String linuxInstallStatus = props.get("package-linux");
        String linuxDetails = props.get("xs:linux");

        return linuxInstallStatus != null && linuxDetails != null
            && linuxInstallStatus.equalsIgnoreCase("installed");
    }

    public String xapiVersion()
    {
        return props.get("xapi");
    }

    public String xenVersion()
    {
        return props.get("xen");
    }
}
