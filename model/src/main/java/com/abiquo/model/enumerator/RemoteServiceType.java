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

package com.abiquo.model.enumerator;

import java.net.URI;

import org.apache.commons.lang.StringUtils;
import org.apache.wink.common.internal.utils.UriHelper;

public enum RemoteServiceType
{
    VIRTUAL_FACTORY("Virtualization Manager", "virtualfactory", "http://", 80), STORAGE_SYSTEM_MONITOR(
        "Storage Manager", "ssm", "http://", 80), VIRTUAL_SYSTEM_MONITOR("Monitor Manager", "vsm",
        "http://", 80), NODE_COLLECTOR("Discovery Manager", "nodecollector", "http://", 80), DHCP_SERVICE(
        "DHCP Service", "dhcp", "omapi://", 7911), BPM_SERVICE("Business Process Manager", "bpm",
        "tcp://", 61616), APPLIANCE_MANAGER("Appliance Manager", "am", "http://", 80);

    String name;

    String serviceMapping;

    String defaultProtocol;

    Integer defaultPort;

    public Integer getDefaultPort()
    {
        return defaultPort;
    }

    public String getDefaultProtocol()
    {
        return defaultProtocol;
    }

    public String getName()
    {
        return name;
    }

    public String getServiceMapping()
    {
        return serviceMapping;
    }

    RemoteServiceType(final String name, final String serviceMapping, final String defaultProtocol,
        final Integer defaultPort)
    {
        this.name = name;
        this.serviceMapping = serviceMapping;
        this.defaultProtocol = defaultProtocol;
        this.defaultPort = defaultPort;
    }

    RemoteServiceType(String name, String serviceMapping)
    {
        this.name = name;
        this.serviceMapping = serviceMapping;
    }

    public boolean canBeChecked()
    {
        return this != BPM_SERVICE && this != DHCP_SERVICE;
    }

    public boolean checkUniqueness()
    {
        return this == APPLIANCE_MANAGER || this == VIRTUAL_FACTORY;
    }

    public String fixUri(URI uri)
    {
        String protocol = uri.getScheme();
        String domainName = uri.getHost();
        Integer port = uri.getPort();
        String path = this == BPM_SERVICE ? null : uri.getPath();

        String domainHost = domainName + (port != null ? ":" + port : "");

        String fullURL = StringUtils.join(new String[] {fixProtocol(protocol), domainHost});

        if (!StringUtils.isEmpty(path))
        {
            fullURL = UriHelper.appendPathToBaseUri(fullURL, path);
        }

        return fullURL;
    }

    private String fixProtocol(String protocol)
    {
        if (!protocol.endsWith("://"))
        {
            protocol += "://";
        }
        return protocol;
    }
}
