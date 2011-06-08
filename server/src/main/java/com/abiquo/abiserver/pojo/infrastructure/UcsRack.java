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

import com.abiquo.abiserver.business.hibernate.pojohb.infrastructure.UcsRackHB;
import com.abiquo.server.core.infrastructure.UcsRackDto;

public class UcsRack extends Rack
{
    /* ------------- Public atributes ------------- */
    private int port;

    private String ip;

    private String password;

    private String user;

    /* ------------- Constructor ------------- */
    public UcsRack()
    {
        super();
        this.port = 0; // redudant since its default value is 0. Just to make it explicit.
        this.ip = "";
        this.user = "";
        this.password = "";
    }

    public int getPort()
    {
        return port;
    }

    public void setPort(int port)
    {
        this.port = port;
    }

    public String getIp()
    {
        return ip;
    }

    public void setIp(String ip)
    {
        this.ip = ip;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public String getUser()
    {
        return user;
    }

    public void setUser(String user)
    {
        this.user = user;
    }

    @Override
    public UcsRackHB toPojoHB()
    {
        UcsRackHB rackPojo = new UcsRackHB();
        rackPojo.setPort(port);
        rackPojo.setIp(ip);
        rackPojo.setUser(user);
        rackPojo.setPassword(password);
        rackPojo.setIdRack(getId());
        rackPojo.setName(getName());
        rackPojo.setShortDescription(getShortDescription());
        rackPojo.setLargeDescription(getLargeDescription());
        rackPojo.setDatacenter(getDataCenter().toPojoHB());
        if (getVlanNetworkParameters() != null)
        {
            rackPojo.setVlan_id_max(getVlanNetworkParameters().getVlan_id_max());
            rackPojo.setVlan_id_min(getVlanNetworkParameters().getVlan_id_min());
            rackPojo
                .setVlan_per_vdc_expected(getVlanNetworkParameters().getVlan_per_vdc_expected());
            rackPojo.setNRSQ(getVlanNetworkParameters().getNRSQ());
            rackPojo.setVlans_id_avoided(getVlanNetworkParameters().getVlans_id_avoided());
        }
        return rackPojo;
    }

    public static UcsRack create(UcsRackDto dto, DataCenter datacenter)
    {
        UcsRack rack = new UcsRack();
        rack.setDataCenter(datacenter);
        rack.setId(dto.getId());
        rack.setLargeDescription(dto.getLongDescription());
        rack.setName(dto.getName());
        rack.setShortDescription(dto.getShortDescription());
        rack.setPort(dto.getPort());
        rack.setIp(dto.getIp());
        rack.setUser(dto.getUser());
        rack.setPassword(dto.getPassword());

        return rack;
    }
}
