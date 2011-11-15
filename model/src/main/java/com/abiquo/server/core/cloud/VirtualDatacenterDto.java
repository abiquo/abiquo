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

package com.abiquo.server.core.cloud;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.abiquo.model.enumerator.HypervisorType;
import com.abiquo.model.transport.SingleResourceWithLimitsDto;
import com.abiquo.server.core.infrastructure.network.VLANNetworkDto;

@XmlRootElement(name = "virtualDatacenter")
public class VirtualDatacenterDto extends SingleResourceWithLimitsDto
{
    /**
     * 
     */
    private static final long serialVersionUID = -2165018992377526633L;

    private Integer id;

    private String name;

    private HypervisorType hypervisorType;

    private VLANNetworkDto vlan;

    public Integer getId()
    {
        return id;
    }

    public void setId(final Integer id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(final String name)
    {
        this.name = name;
    }

    public HypervisorType getHypervisorType()
    {
        return hypervisorType;
    }

    public void setHypervisorType(final HypervisorType hypervisorType)
    {
        this.hypervisorType = hypervisorType;
    }

    public void setVlan(final VLANNetworkDto vlan)
    {
        this.vlan = vlan;
    }

    @XmlElement(name = "network")
    public VLANNetworkDto getVlan()
    {
        return vlan;
    }
}
