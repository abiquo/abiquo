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

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

import com.abiquo.model.enumerator.StorageTechnologyType;
import com.abiquo.model.transport.SingleResourceTransportDto;

@XmlRootElement(name = "storage_device")
public class StorageDeviceDto extends SingleResourceTransportDto implements Serializable
{
    /**
     * Default serial version UID. 
     */
    private static final long serialVersionUID = 1L;
    
    private Integer id;

    public Integer getId()
    {
        return id;
    }

    public void setId(Integer id)
    {
        this.id = id;
    }

    private int managementPort;

    public int getManagementPort()
    {
        return managementPort;
    }

    public void setManagementPort(int managementPort)
    {
        this.managementPort = managementPort;
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

    private String iscsiIp;

    public String getIscsiIp()
    {
        return iscsiIp;
    }

    public void setIscsiIp(String iscsiIp)
    {
        this.iscsiIp = iscsiIp;
    }

    private StorageTechnologyType storageTechnology;

    public StorageTechnologyType getStorageTechnology()
    {
        return storageTechnology;
    }

    public void setStorageTechnology(StorageTechnologyType storageTechnology)
    {
        this.storageTechnology = storageTechnology;
    }

    private String managementIp;

    public String getManagementIp()
    {
        return managementIp;
    }

    public void setManagementIp(String managementIp)
    {
        this.managementIp = managementIp;
    }

    private int iscsiPort;

    public int getIscsiPort()
    {
        return iscsiPort;
    }

    public void setIscsiPort(int iscsiPort)
    {
        this.iscsiPort = iscsiPort;
    }

}
