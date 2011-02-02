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

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

import com.abiquo.model.transport.SingleResourceTransportDto;

@XmlRootElement(name = "configuration")
public class NetworkConfigurationDto extends SingleResourceTransportDto implements Serializable
{
    String networkName;

    String gateway;

    String address;

    Integer mask;

    String netMask;

    String primaryDNS;

    String secondaryDNS;

    String sufixDNS;

    String fenceMode;

    boolean defaultNetwork;

    public boolean isDefaultNetwork()
    {
        return defaultNetwork;
    }

    public void setDefaultNetwork(boolean defaultNetwork)
    {
        this.defaultNetwork = defaultNetwork;
    }

    public String getNetworkName()
    {
        return networkName;
    }

    public void setNetworkName(String networkName)
    {
        this.networkName = networkName;
    }

    public String getGateway()
    {
        return gateway;
    }

    public void setGateway(String gateway)
    {
        this.gateway = gateway;
    }

    public String getAddress()
    {
        return address;
    }

    public void setAddress(String networkAddress)
    {
        this.address = networkAddress;
    }

    public Integer getMask()
    {
        return mask;
    }

    public void setMask(Integer mask)
    {
        this.mask = mask;
    }

    public String getNetMask()
    {
        return netMask;
    }

    public void setNetMask(String netMask)
    {
        this.netMask = netMask;
    }

    public String getPrimaryDNS()
    {
        return primaryDNS;
    }

    public void setPrimaryDNS(String primaryDNS)
    {
        this.primaryDNS = primaryDNS;
    }

    public String getSecondaryDNS()
    {
        return secondaryDNS;
    }

    public void setSecondaryDNS(String secondaryDNS)
    {
        this.secondaryDNS = secondaryDNS;
    }

    public String getSufixDNS()
    {
        return sufixDNS;
    }

    public void setSufixDNS(String sufixDNS)
    {
        this.sufixDNS = sufixDNS;
    }

    public String getFenceMode()
    {
        return fenceMode;
    }

    public void setFenceMode(String fenceMode)
    {
        this.fenceMode = fenceMode;
    }
}
