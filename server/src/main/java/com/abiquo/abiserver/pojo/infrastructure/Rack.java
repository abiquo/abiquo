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

package com.abiquo.abiserver.pojo.infrastructure;

import org.springframework.util.StringUtils;

import com.abiquo.abiserver.business.hibernate.pojohb.infrastructure.RackHB;
import com.abiquo.abiserver.pojo.IPojo;
import com.abiquo.abiserver.pojo.networking.VlanNetworkParameters;
import com.abiquo.server.core.infrastructure.RackDto;

public class Rack extends InfrastructureElement implements IPojo<RackHB>, Comparable<Rack>
{

    /* ------------- Public atributes ------------- */
    private String shortDescription;

    private String largeDescription;

    private DataCenter dataCenter;

    private VlanNetworkParameters vlanNetworkParameters;

    private Boolean haEnabled;

    private final String type = "Standard Rack";

    /* ------------- Constructor ------------- */
    public Rack()
    {
        super();
        shortDescription = "";
        largeDescription = "";
        dataCenter = null;
        vlanNetworkParameters = null;
    }

    public String getShortDescription()
    {
        return shortDescription;
    }

    public void setShortDescription(final String shortDescription)
    {
        this.shortDescription = shortDescription;
    }

    public String getLargeDescription()
    {
        return largeDescription;
    }

    public void setLargeDescription(final String largeDescription)
    {
        this.largeDescription = largeDescription;
    }

    public DataCenter getDataCenter()
    {
        return dataCenter;
    }

    public void setDataCenter(final DataCenter dataCenter)
    {
        this.dataCenter = dataCenter;
    }

    public void setVlanNetworkParameters(final VlanNetworkParameters vlanNetworkParameters)
    {
        this.vlanNetworkParameters = vlanNetworkParameters;
    }

    public VlanNetworkParameters getVlanNetworkParameters()
    {
        return vlanNetworkParameters;
    }

    @Override
    public RackHB toPojoHB()
    {
        RackHB rackPojo = new RackHB();
        rackPojo.setIdRack(getId());
        rackPojo.setName(getName());
        rackPojo.setShortDescription(shortDescription);
        rackPojo.setLargeDescription(largeDescription);
        rackPojo.setDatacenter(dataCenter.toPojoHB());
        if (vlanNetworkParameters != null)
        {
            rackPojo.setVlan_id_max(vlanNetworkParameters.getVlan_id_max());
            rackPojo.setVlan_id_min(vlanNetworkParameters.getVlan_id_min());
            rackPojo.setVlan_per_vdc_expected(vlanNetworkParameters.getVlan_per_vdc_expected());
            rackPojo.setNRSQ(vlanNetworkParameters.getNRSQ());
            rackPojo.setVlans_id_avoided(vlanNetworkParameters.getVlans_id_avoided());
        }
        rackPojo.setHaEnabled(getHaEnabled());
        return rackPojo;
    }

    public void setHaEnabled(final Boolean haEnabled)
    {
        this.haEnabled = haEnabled;
    }

    public Boolean getHaEnabled()
    {
        return haEnabled;
    }

    public static Rack create(final RackDto dto, final DataCenter datacenter)
    {
        Rack rack = new Rack();
        rack.setDataCenter(datacenter);
        rack.setId(dto.getId());
        rack.setLargeDescription(dto.getLongDescription());
        rack.setName(dto.getName());
        rack.setShortDescription(dto.getShortDescription());
        rack.setHaEnabled(dto.isHaEnabled());
        VlanNetworkParameters vlanNetworkParameters =
            new VlanNetworkParameters(dto.getVlanIdMin(),
                dto.getVlanIdMax(),
                dto.getVlansIdAvoided(),
                dto.getNrsq(),
                dto.getVlanPerVdcReserved());
        rack.setVlanNetworkParameters(vlanNetworkParameters);

        return rack;
    }

    @Override
    public int compareTo(final Rack r2)
    {
        if (StringUtils.hasText(this.getName()) && StringUtils.hasText(r2.getName()))
        {
            return this.getName().compareTo(r2.getName());
        }
        else if (!StringUtils.hasText(this.getName()) && !StringUtils.hasText(r2.getName()))
        {
            return 0;
        }
        else if (!StringUtils.hasText(this.getName()) && StringUtils.hasText(r2.getName()))
        {
            return -1;
        }
        else
        {
            return 1;
        }
    }

}
