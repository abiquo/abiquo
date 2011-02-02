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

package com.abiquo.server.core.infrastructure.management;

import javax.xml.bind.annotation.XmlRootElement;

import com.abiquo.model.transport.SingleResourceTransportDto;

@XmlRootElement(name = "")
public class RasdDto extends SingleResourceTransportDto
{
    private String id;

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    private String addressOnParent;

    public String getAddressOnParent()
    {
        return addressOnParent;
    }

    public void setAddressOnParent(String addressOnParent)
    {
        this.addressOnParent = addressOnParent;
    }

    private String address;

    public String getAddress()
    {
        return address;
    }

    public void setAddress(String address)
    {
        this.address = address;
    }

    private String parent;

    public String getParent()
    {
        return parent;
    }

    public void setParent(String parent)
    {
        this.parent = parent;
    }

    private long virtualQuantity;

    public long getVirtualQuantity()
    {
        return virtualQuantity;
    }

    public void setVirtualQuantity(long virtualQuantity)
    {
        this.virtualQuantity = virtualQuantity;
    }

    private String hostResource;

    public String getHostResource()
    {
        return hostResource;
    }

    public void setHostResource(String hostResource)
    {
        this.hostResource = hostResource;
    }

    private long generation;

    public long getGeneration()
    {
        return generation;
    }

    public void setGeneration(long generation)
    {
        this.generation = generation;
    }

    private int changeableType;

    public int getChangeableType()
    {
        return changeableType;
    }

    public void setChangeableType(int changeableType)
    {
        this.changeableType = changeableType;
    }

    private int automaticAllocation;

    public int getAutomaticAllocation()
    {
        return automaticAllocation;
    }

    public void setAutomaticAllocation(int automaticAllocation)
    {
        this.automaticAllocation = automaticAllocation;
    }

    private String resourceSubType;

    public String getResourceSubType()
    {
        return resourceSubType;
    }

    public void setResourceSubType(String resourceSubType)
    {
        this.resourceSubType = resourceSubType;
    }

    private long reservation;

    public long getReservation()
    {
        return reservation;
    }

    public void setReservation(long reservation)
    {
        this.reservation = reservation;
    }

    private String poolId;

    public String getPoolID()
    {
        return poolId;
    }

    public void setPoolID(String poolId)
    {
        this.poolId = poolId;
    }

    private String connection;

    public String getConnection()
    {
        return connection;
    }

    public void setConnection(String connection)
    {
        this.connection = connection;
    }

    private String configurationName;

    public String getConfigurationName()
    {
        return configurationName;
    }

    public void setConfigurationName(String configurationName)
    {
        this.configurationName = configurationName;
    }

    private String weight;

    public String getWeight()
    {
        return weight;
    }

    public void setWeight(String weight)
    {
        this.weight = weight;
    }

    private String otherResourceType;

    public String getOtherResourceType()
    {
        return otherResourceType;
    }

    public void setOtherResourceType(String otherResourceType)
    {
        this.otherResourceType = otherResourceType;
    }

    private int mappingBehaviour;

    public int getMappingBehaviour()
    {
        return mappingBehaviour;
    }

    public void setMappingBehaviour(int mappingBehaviour)
    {
        this.mappingBehaviour = mappingBehaviour;
    }

    private int automaticDeallocation;

    public int getAutomaticDeallocation()
    {
        return automaticDeallocation;
    }

    public void setAutomaticDeallocation(int automaticDeallocation)
    {
        this.automaticDeallocation = automaticDeallocation;
    }

    private String caption;

    public String getCaption()
    {
        return caption;
    }

    public void setCaption(String caption)
    {
        this.caption = caption;
    }

    private String allocationUnits;

    public String getAllocationUnits()
    {
        return allocationUnits;
    }

    public void setAllocationUnits(String allocationUnits)
    {
        this.allocationUnits = allocationUnits;
    }

    private String elementName;

    public String getElementName()
    {
        return elementName;
    }

    public void setElementName(String elementName)
    {
        this.elementName = elementName;
    }

    private String instanceId;

    public String getInstanceID()
    {
        return instanceId;
    }

    public void setInstanceID(String instanceId)
    {
        this.instanceId = instanceId;
    }

    private String description;

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    private int consumerVisibility;

    public int getConsumerVisibility()
    {
        return consumerVisibility;
    }

    public void setConsumerVisibility(int consumerVisibility)
    {
        this.consumerVisibility = consumerVisibility;
    }

    private long limit;

    public long getLimit()
    {
        return limit;
    }

    public void setLimit(long limit)
    {
        this.limit = limit;
    }

    private int resourceType;

    public int getResourceType()
    {
        return resourceType;
    }

    public void setResourceType(int resourceType)
    {
        this.resourceType = resourceType;
    }

}
