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

package com.abiquo.abiserver.business.hibernate.pojohb.service;

import java.util.Arrays;
import java.util.Comparator;

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

    public static RemoteServiceType[] getCommunityServices()
    {
        RemoteServiceType[] communityServices =
            {VIRTUAL_FACTORY, VIRTUAL_SYSTEM_MONITOR, APPLIANCE_MANAGER};
        Arrays.sort(communityServices, new RemoteServiceTypeComparator());
        return communityServices;
    }

    public static RemoteServiceType[] getEnterpriseServices()
    {
        RemoteServiceType[] enterpriseServices = RemoteServiceType.values();
        Arrays.sort(enterpriseServices, new RemoteServiceTypeComparator());
        return enterpriseServices;
    }

    public boolean canBeChecked()
    {
        return this != BPM_SERVICE && this != DHCP_SERVICE;
    }

    private static class RemoteServiceTypeComparator implements Comparator<RemoteServiceType>
    {

        @Override
        public int compare(final RemoteServiceType rst1, final RemoteServiceType rst2)
        {
            Comparator<String> nameComparator = String.CASE_INSENSITIVE_ORDER;
            return nameComparator.compare(rst1.getName(), rst2.getName());
        }

    }
}
