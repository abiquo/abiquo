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

/**
 * 
 */
package com.abiquo.server.core.infrastructure.network;

import javax.xml.bind.annotation.XmlRootElement;

import com.abiquo.model.transport.SingleResourceTransportDto;

/**
 * Configuration network for a virtual machine.
 * 
 * @author jdevesa
 */
@XmlRootElement(name = "vmnetworkconfiguration")
public class VMNetworkConfigurationDto extends SingleResourceTransportDto
{
    /**
     * Generated serial version id.
     */
    private static final long serialVersionUID = -3866622562676820662L;

    public VMNetworkConfigurationDto()
    {

    }

    private Integer id;

    private String gateway;

    private String primaryDNS;

    private String secondaryDNS;

    private String suffixDNS;

    private Boolean used;

    public Integer getId()
    {
        return this.id;
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

    public String getPrimaryDNS()
    {
        return primaryDNS;
    }

    public void setPrimaryDNS(final String primaryDNS)
    {
        this.primaryDNS = primaryDNS;
    }

    public String getSecondaryDNS()
    {
        return secondaryDNS;
    }

    public void setSecondaryDNS(final String secondaryDNS)
    {
        this.secondaryDNS = secondaryDNS;
    }

    public String getSuffixDNS()
    {
        return suffixDNS;
    }

    public void setSuffixDNS(final String suffixDNS)
    {
        this.suffixDNS = suffixDNS;
    }

    public Boolean getUsed()
    {
        return used;
    }

    public void setUsed(final Boolean used)
    {
        this.used = used;
    }

}
