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

import java.io.Serializable;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.abiquo.model.enumerator.NetworkType;
import com.abiquo.model.transport.SingleResourceTransportDto;
import com.abiquo.model.validation.Desc;
import com.abiquo.model.validation.Output;

@XmlRootElement(name = "network")
public class VLANNetworkDto extends SingleResourceTransportDto implements Serializable
{
    public static final String BASE_MEDIA_TYPE = "application/vnd.abiquo.vlan+xml";

    public static final String MEDIA_TYPE = BASE_MEDIA_TYPE + "; version=" + API_VERSION;

    private Integer id;

    private String name;

    private Integer tag;

    private String gateway;

    private String address;

    private Integer mask;

    private String primaryDNS;

    private String secondaryDNS;

    private String sufixDNS;

    private Boolean defaultNetwork;

    private Boolean unmanaged;

    private NetworkType type;

    private DhcpOptionsDto dhcpOptions;

    @Desc("Identifier of the entity")
    public Integer getId()
    {
        return id;
    }

    public void setId(final Integer id)
    {
        this.id = id;
    }

    @NotNull
    @Desc(value = "Name of the VLAN")
    @XmlElement(defaultValue = "")
    public String getName()
    {
        return name;
    }

    public void setName(final String name)
    {
        this.name = name;
    }

    @Desc(value = "Tag of the VLAN. It will need an input value for PUBLIC, EXTERNAL and UNMANAGED networks")
    public Integer getTag()
    {
        return tag;
    }

    public void setTag(final Integer tag)
    {
        this.tag = tag;
    }

    @NotNull
    @Desc(value = "Gateway of the VLAN")
    public String getGateway()
    {
        return gateway;
    }

    public void setGateway(final String gateway)
    {
        this.gateway = gateway;
    }

    @NotNull
    @Desc(value = "Network Address of the VLAN.")
    public String getAddress()
    {
        return address;
    }

    public void setAddress(final String address)
    {
        this.address = address;
    }

    @NotNull
    @Desc(value = "Numerical value of the VLAN")
    public Integer getMask()
    {
        return mask;
    }

    public void setMask(final Integer mask)
    {
        this.mask = mask;
    }

    @Desc(value = "Primary DNS address")
    public String getPrimaryDNS()
    {
        return primaryDNS;
    }

    public void setPrimaryDNS(final String primaryDNS)
    {
        this.primaryDNS = primaryDNS;
    }

    @Desc(value = "Secondary DNS value")
    public String getSecondaryDNS()
    {
        return secondaryDNS;
    }

    public void setSecondaryDNS(final String secondaryDNS)
    {
        this.secondaryDNS = secondaryDNS;
    }

    @Desc(value = "Suffix DNS value")
    public String getSufixDNS()
    {
        return sufixDNS;
    }

    public void setSufixDNS(final String sufixDNS)
    {
        this.sufixDNS = sufixDNS;
    }

    @Output
    @Desc(value = "Return if it is used as Default Network. Read only.")
    public Boolean getDefaultNetwork()
    {
        return defaultNetwork;
    }

    public void setDefaultNetwork(final Boolean defaultNetwork)
    {
        this.defaultNetwork = defaultNetwork;
    }

    @Output
    @Desc(value = "Kind of network: PUBLIC, INTERNAL, EXTERNAL and UNMANAGED")
    public NetworkType getType()
    {
        return type;
    }

    public void setType(final NetworkType type)
    {
        this.type = type;
    }

    public DhcpOptionsDto getDhcpOptions()
    {
        return dhcpOptions;
    }

    public void setDhcpOptions(final DhcpOptionsDto dhcpOptions)
    {
        this.dhcpOptions = dhcpOptions;
    }

    @Output
    @Desc(value = "Return if the VLAN is unmanaged")
    public Boolean getUnmanaged()
    {
        return unmanaged;
    }

    public void setUnmanaged(final Boolean unmanaged)
    {
        this.unmanaged = unmanaged;
    }

    @Override
    public String getMediaType()
    {
        return VLANNetworkDto.MEDIA_TYPE;
    }

    @Override
    public String getBaseMediaType()
    {
        return BASE_MEDIA_TYPE;
    }

}
