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

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.abiquo.model.transport.SingleResourceTransportDto;

@XmlRootElement(name = "network")
public class VLANNetworkDto extends SingleResourceTransportDto implements Serializable
{
    private Integer id;

    private String name;

    private Integer tag;

    private Boolean defaultNetwork;

    private NetworkConfigurationDto networkConfiguration;

    public Integer getId()
    {
        return id;
    }

    public void setId(Integer id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public Integer getTag()
    {
        return tag;
    }

    public void setTag(Integer tag)
    {
        this.tag = tag;
    }

    public Boolean getDefaultNetwork()
    {
        return defaultNetwork;
    }

    public void setDefaultNetwork(Boolean defaultNetwork)
    {
        this.defaultNetwork = defaultNetwork;
    }

    @XmlElement(name = "configuration")
    public NetworkConfigurationDto getNetworkConfiguration()
    {
        return networkConfiguration;
    }

    public void setNetworkConfiguration(NetworkConfigurationDto networkConfiguration)
    {
        this.networkConfiguration = networkConfiguration;
    }
}
