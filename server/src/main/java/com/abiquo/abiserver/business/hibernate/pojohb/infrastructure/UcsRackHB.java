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

package com.abiquo.abiserver.business.hibernate.pojohb.infrastructure;

import com.abiquo.abiserver.pojo.infrastructure.UcsRack;
import com.abiquo.abiserver.pojo.networking.VlanNetworkParameters;

public class UcsRackHB extends RackHB
{
    /**
     * UID.
     */
    private static final long serialVersionUID = -3220031077433445895L;

    /* ------------- Public atributes ------------- */
    private int port;

    private String ip;

    private String password;

    private String user;

    /* ------------- Constructor ------------- */
    public UcsRackHB()
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
    public UcsRack toPojo()
    {
        UcsRack rackPojo = new UcsRack();
        rackPojo.setPort(port);
        rackPojo.setIp(ip);
        rackPojo.setUser(user);
        rackPojo.setPassword(password);
        rackPojo.setDataCenter(getDatacenter().toPojo());
        rackPojo.setId(getIdRack());
        rackPojo.setLargeDescription(getLargeDescription());
        rackPojo.setName(getName());
        rackPojo.setShortDescription(getShortDescription());
        VlanNetworkParameters vlanNetworkParameters =
            new VlanNetworkParameters(getVlan_id_min(),
                getVlan_id_max(),
                getVlans_id_avoided(),
                getNRSQ(),
                getVlan_per_vdc_expected());
        vlanNetworkParameters.setVlans_id_avoided(getVlans_id_avoided());
        rackPojo.setVlanNetworkParameters(vlanNetworkParameters);

        return rackPojo;
    }

}
