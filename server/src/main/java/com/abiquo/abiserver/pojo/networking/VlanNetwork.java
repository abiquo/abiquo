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
package com.abiquo.abiserver.pojo.networking;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import com.abiquo.abiserver.business.hibernate.pojohb.networking.DhcpOptionHB;
import com.abiquo.abiserver.business.hibernate.pojohb.networking.VlanNetworkHB;
import com.abiquo.abiserver.pojo.IPojo;
import com.abiquo.server.core.infrastructure.network.VLANNetworkDto;

/**
 * Each one of the networks in transfer object to Flex.
 * 
 * @author jdevesa
 */
public class VlanNetwork implements Serializable, IPojo<VlanNetworkHB>
{
    /**
     * 
     */
    private static final long serialVersionUID = 1324487110336737711L;

    /*
     * Identifier of the vlan.
     */
    private Integer vlanNetworkId;

    /**
     * Identifier of the network that belongs.
     */
    private Integer networkId;

    /**
     * Name of the network.
     */
    protected String networkName;

    /**
     * The tag that defines the network.
     */
    protected Integer vlanTag;

    /**
     * The configuration of the VLAN
     */
    private NetworkConfiguration configuration;

    private Boolean defaultNetwork;

    private String networkType;

    Set<DhcpOption> dhcpOptions;

    /**
     * @return the vlanNetworkId
     */
    public Integer getVlanNetworkId()
    {
        return vlanNetworkId;
    }

    /**
     * @param vlanNetworkId the vlanNetworkId to set
     */
    public void setVlanNetworkId(final Integer vlanNetworkId)
    {
        this.vlanNetworkId = vlanNetworkId;
    }

    /**
     * @return the networkId
     */
    public Integer getNetworkId()
    {
        return networkId;
    }

    /**
     * @param networkId the networkId to set
     */
    public void setNetworkId(final Integer networkId)
    {
        this.networkId = networkId;
    }

    /**
     * @return the networkName
     */
    public String getNetworkName()
    {
        return networkName;
    }

    /**
     * @param networkName the networkName to set
     */
    public void setNetworkName(final String networkName)
    {
        this.networkName = networkName;
    }

    /**
     * @return the vlanTag
     */
    public Integer getVlanTag()
    {
        return vlanTag;
    }

    /**
     * @param vlanTag the vlanTag to set
     */
    public void setVlanTag(final Integer vlanTag)
    {
        this.vlanTag = vlanTag;
    }

    /**
     * @return the configuration
     */
    public NetworkConfiguration getConfiguration()
    {
        return configuration;
    }

    /**
     * @param configuration the configuration to set
     */
    public void setConfiguration(final NetworkConfiguration configuration)
    {
        this.configuration = configuration;
    }

    public Set<DhcpOption> getDhcpOptions()
    {
        return dhcpOptions;
    }

    public void setDhcpOptions(final Set<DhcpOption> dhcpOptions)
    {
        this.dhcpOptions = dhcpOptions;
    }

    @Override
    public VlanNetworkHB toPojoHB()
    {
        VlanNetworkHB vnetHB = new VlanNetworkHB();

        vnetHB.setNetworkId(getNetworkId());
        vnetHB.setVlanNetworkId(getVlanNetworkId());
        vnetHB.setNetworkName(getNetworkName());
        vnetHB.setVlanTag(getVlanTag());

        if (getConfiguration() != null)
        {
            vnetHB.setConfiguration(getConfiguration().toPojoHB());
        }

        Set<DhcpOptionHB> dhcpOptionHB = new HashSet<DhcpOptionHB>();
        if (dhcpOptions != null)
        {
            for (DhcpOption dhcpOption : dhcpOptions)
            {
                dhcpOptionHB.add(dhcpOption.toPojoHB());
            }
        }

        vnetHB.setDhcpOptionsHB(dhcpOptionHB);

        return vnetHB;
    }

    public Boolean getDefaultNetwork()
    {
        return defaultNetwork;
    }

    public void setDefaultNetwork(final Boolean defaultNetwork)
    {
        this.defaultNetwork = defaultNetwork;
    }

    public String getNetworkType()
    {
        return networkType;
    }

    public void setNetworkType(final String networkType)
    {
        this.networkType = networkType;
    }

    public static VlanNetwork create(final VLANNetworkDto dto, final Integer networkId,
        final Boolean defaultNetwork)
    {
        VlanNetwork vlan = new VlanNetwork();

        vlan.setDefaultNetwork(defaultNetwork);

        NetworkConfiguration conf = new NetworkConfiguration();
        conf.setGateway(dto.getGateway());
        conf.setMask(dto.getMask());
        conf.setPrimaryDNS(dto.getPrimaryDNS());
        conf.setSecondaryDNS(dto.getSecondaryDNS());
        conf.setNetworkAddress(dto.getAddress());
        conf.setSufixDNS(dto.getSufixDNS());

        vlan.setConfiguration(conf);
        vlan.setNetworkName(dto.getName());
        vlan.setNetworkType(dto.getType().toString());
        vlan.setVlanNetworkId(dto.getId());
        vlan.setVlanTag(dto.getTag());
        vlan.setNetworkId(networkId);
        return vlan;
    }
}
