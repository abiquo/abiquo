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

import com.abiquo.abiserver.business.hibernate.pojohb.networking.VlanNetworkHB;
import com.abiquo.abiserver.pojo.IPojo;
import com.abiquo.abiserver.pojo.user.Enterprise;

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
     * If this network is the default network; The default network is the one which a new node will
     * request an available IP.
     */
    private Boolean defaultNetwork;

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
    public void setVlanNetworkId(Integer vlanNetworkId)
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
    public void setNetworkId(Integer networkId)
    {
        this.networkId = networkId;
    }

    /**
     * @return the defaultNetwork
     */
    public Boolean getDefaultNetwork()
    {
        return defaultNetwork;
    }

    /**
     * @param defaultNetwork the defaultNetwork to set
     */
    public void setDefaultNetwork(Boolean defaultNetwork)
    {
        this.defaultNetwork = defaultNetwork;
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
    public void setNetworkName(String networkName)
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
    public void setVlanTag(Integer vlanTag)
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
    public void setConfiguration(NetworkConfiguration configuration)
    {
        this.configuration = configuration;
    }

   
    @Override
    public VlanNetworkHB toPojoHB()
    {
        VlanNetworkHB vnetHB = new VlanNetworkHB();
        
        vnetHB.setNetworkId(getNetworkId());
        vnetHB.setVlanNetworkId(getVlanNetworkId());
        vnetHB.setDefaultNetwork(getDefaultNetwork());
        vnetHB.setNetworkName(getNetworkName());
        vnetHB.setVlanTag(getVlanTag());
                
        if (getConfiguration() != null)
        {
            vnetHB.setConfiguration(getConfiguration().toPojoHB());
        }        
       
        return vnetHB;
    }

}
