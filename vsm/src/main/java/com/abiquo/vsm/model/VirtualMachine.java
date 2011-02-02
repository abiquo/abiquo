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

package com.abiquo.vsm.model;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import redis.clients.johm.Attribute;
import redis.clients.johm.Id;
import redis.clients.johm.Indexed;
import redis.clients.johm.Model;
import redis.clients.johm.Reference;

@Model
public class VirtualMachine
{
    @Id
    private Integer id;

    @Attribute
    @Indexed
    private String name;

    @Attribute
    private String lastKnownState;

    @Reference
    private PhysicalMachine physicalMachine;

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

    public void setName(String uuid)
    {
        this.name = uuid;
    }

    public String getLastKnownState()
    {
        return lastKnownState;
    }

    public void setLastKnownState(String lastKnownState)
    {
        this.lastKnownState = lastKnownState;
    }

    public PhysicalMachine getPhysicalMachine()
    {
        return physicalMachine;
    }

    public void setPhysicalMachine(PhysicalMachine physicalMachine)
    {
        this.physicalMachine = physicalMachine;
    }

    @Override
    public int hashCode()
    {
        return new HashCodeBuilder().append(getName()).append(getLastKnownState()).append(
            getPhysicalMachine()).toHashCode();
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }

        if (obj instanceof VirtualMachine)
        {
            VirtualMachine other = (VirtualMachine) obj;

            return new EqualsBuilder().append(getName(), other.getName()).append(
                getLastKnownState(), other.getLastKnownState()).append(getPhysicalMachine(),
                other.getPhysicalMachine()).isEquals();
        }

        return false;
    }
}
