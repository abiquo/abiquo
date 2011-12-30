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

package com.abiquo.server.core.infrastructure.network;

import javax.xml.bind.annotation.XmlRootElement;

import com.abiquo.model.transport.SingleResourceTransportDto;

@XmlRootElement(name = "dhcpoption")
public class DhcpOptionDto extends SingleResourceTransportDto
{

    public DhcpOptionDto(final Integer id, final int option, final String gateway,
        final String networkAddress, final Integer mask, final String netmask)
    {
        super();
        this.id = id;
        this.option = option;
        this.gateway = gateway;
        this.networkAddress = networkAddress;
        this.mask = mask;
        this.netmask = netmask;
    }

    public DhcpOptionDto()
    {
        // TODO Auto-generated constructor stub
    }

    private Integer id;

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

    public Integer getId()
    {
        return id;
    }

    public void setId(final Integer id)
    {
        this.id = id;
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

    public int getOption()
    {
        return option;
    }

    public void setOption(final int option)
    {
        this.option = option;
    }

}
