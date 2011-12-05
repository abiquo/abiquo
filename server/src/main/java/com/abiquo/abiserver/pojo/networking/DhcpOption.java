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

package com.abiquo.abiserver.pojo.networking;

import com.abiquo.abiserver.business.hibernate.pojohb.networking.DhcpOptionHB;
import com.abiquo.abiserver.pojo.IPojo;
import com.abiquo.server.core.infrastructure.network.DhcpOptionDto;

public class DhcpOption implements IPojo<DhcpOptionHB>
{

    /* ------------- Public atributes ------------- */
    private int id;

    /**
     * Dhcp option number.
     */

    private int option;

    /**
     * The IP address of the gateway.
     */
    private String gateway;

    /**
     * The network that defines the address.
     */
    private String networkAddress;

    /**
     * The mask value in the integer way (/24)
     */
    private Integer mask;

    /**
     * The mask value in IP way (255.255.255.0)
     */
    private String netmask;

    public int getId()
    {
        return id;
    }

    public void setId(final int id)
    {
        this.id = id;
    }

    public int getOption()
    {
        return option;
    }

    public void setOption(final int option)
    {
        this.option = option;
    }

    public String getGateway()
    {
        return gateway;
    }

    public void setGateway(final String gateway)
    {
        this.gateway = gateway;
    }

    public String getNetworkAddress()
    {
        return networkAddress;
    }

    public void setNetworkAddress(final String networkAddress)
    {
        this.networkAddress = networkAddress;
    }

    public Integer getMask()
    {
        return mask;
    }

    public void setMask(final Integer mask)
    {
        this.mask = mask;
    }

    public String getNetmask()
    {
        return netmask;
    }

    public void setNetmask(final String netmask)
    {
        this.netmask = netmask;
    }

    @Override
    public DhcpOptionHB toPojoHB()
    {
        DhcpOptionHB dhcpOptionHB = new DhcpOptionHB();

        dhcpOptionHB.setIdDhcpOption(id);
        dhcpOptionHB.setMask(mask);
        dhcpOptionHB.setNetmask(netmask);
        dhcpOptionHB.setNetworkAddress(networkAddress);
        dhcpOptionHB.setOption(option);
        dhcpOptionHB.setGateway(gateway);

        return dhcpOptionHB;
    }

    public static DhcpOption create(final DhcpOptionDto dto)
    {
        DhcpOption dhcpOption = new DhcpOption();
        dhcpOption.setId(dto.getId());
        dhcpOption.setMask(dto.getMask());
        dhcpOption.setNetmask(dto.getNetmask());
        dhcpOption.setNetworkAddress(dto.getNetworkAddress());
        dhcpOption.setOption(dto.getOption());
        dhcpOption.setGateway(dto.getGateway());

        return dhcpOption;
    }

}
