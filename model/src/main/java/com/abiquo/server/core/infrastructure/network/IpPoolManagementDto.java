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

package com.abiquo.server.core.infrastructure.network;

import javax.xml.bind.annotation.XmlRootElement;

import com.abiquo.model.transport.SingleResourceTransportDto;

@XmlRootElement(name = "ipPoolManagement")
public class IpPoolManagementDto extends SingleResourceTransportDto
{
    private Integer id;

    public Integer getId()
    {
        return id;
    }

    public void setId(final Integer id)
    {
        this.id = id;
    }

    private String name;

    public String getName()
    {
        return name;
    }

    public void setName(final String name)
    {
        this.name = name;
    }

    private String mac;

    public String getMac()
    {
        return mac;
    }

    public void setMac(final String mac)
    {
        this.mac = mac;
    }

    private boolean configurationGateway;

    public boolean getConfigurationGateway()
    {
        return configurationGateway;
    }

    public void setConfigurationGateway(final boolean configurationGateway)
    {
        this.configurationGateway = configurationGateway;
    }

    private boolean quarantine;

    public boolean getQuarantine()
    {
        return quarantine;
    }

    public void setQuarantine(final boolean quarantine)
    {
        this.quarantine = quarantine;
    }

    private String ip;

    public String getIp()
    {
        return ip;
    }

    public void setIp(final String ip)
    {
        this.ip = ip;
    }

    private String networkName;

    public String getNetworkName()
    {
        return this.networkName;
    }

    public void setNetworkName(final String networkName)
    {
        this.networkName = networkName;
    }

    private boolean available;

    public boolean getAvailable()
    {
        return available;
    }

    public void setAvailable(final boolean available)
    {
        this.available = available;
    }

}
