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
package com.abiquo.server.core.infrastructure.storage;

import javax.xml.bind.annotation.XmlRootElement;

import com.abiquo.model.enumerator.StorageTechnologyType;
import com.abiquo.model.transport.SingleResourceTransportDto;

@XmlRootElement(name = "storagePool")
public class StoragePoolDto extends SingleResourceTransportDto
{
    private static final long serialVersionUID = 1L;

    private String id;

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    private int hostPort;

    public int getHostPort()
    {
        return hostPort;
    }

    public void setHostPort(int hostPort)
    {
        this.hostPort = hostPort;
    }

    private String urlManagement;

    public String getUrlManagement()
    {
        return urlManagement;
    }

    public void setUrlManagement(String urlManagement)
    {
        this.urlManagement = urlManagement;
    }

    private String name;

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    private StorageTechnologyType type;

    public StorageTechnologyType getType()
    {
        return type;
    }

    public void setType(StorageTechnologyType type)
    {
        this.type = type;
    }

    private String hostIp;

    public String getHostIp()
    {
        return hostIp;
    }

    public void setHostIp(String hostIp)
    {
        this.hostIp = hostIp;
    }

}
