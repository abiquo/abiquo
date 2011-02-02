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

package com.abiquo.server.core.infrastructure;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.abiquo.model.transport.SingleResourceTransportDto;

@XmlRootElement(name = "datacenter")
public class DatacenterDto extends SingleResourceTransportDto implements Serializable
{
    /**
     * Identifier of the datacenter.
     */
    private Integer id;

    /**
     * Name of the datacenter. 
     */
    private String name;
    
    /**
     * Where the Datacenter is located. 
     */
    private String location;
    
    /**
     * List of remote services of the datacenter. 
     */
    private RemoteServicesDto remoteServices;

    /**
     * @return the id
     */
    public Integer getId()
    {
        return id;
    }

    /**
     * @param id id to set
     */
    public void setId(Integer id)
    {
        this.id = id;
    }

    /**
     * @return the name
     */
    public String getName()
    {
        return name;
    }

    /**
     * @param name name to set
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * @return the location
     */
    public String getLocation()
    {
        return location;
    }

    /**
     * @param location set the location
     */
    public void setLocation(String location)
    {
        this.location = location;
    }

    /**
     * @param remoteServices the remoteServices to set
     */
    public void setRemoteServices(RemoteServicesDto remoteServices)
    {
        this.remoteServices = remoteServices;
    }

    /**
     * @return the remoteServices
     */
    public RemoteServicesDto getRemoteServices()
    {
        return remoteServices;
    }
}
