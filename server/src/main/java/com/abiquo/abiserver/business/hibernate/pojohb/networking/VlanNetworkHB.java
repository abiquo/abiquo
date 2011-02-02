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

package com.abiquo.abiserver.business.hibernate.pojohb.networking;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.abiquo.abiserver.business.hibernate.pojohb.IPojoHB;
import com.abiquo.abiserver.business.hibernate.pojohb.user.EnterpriseHB;
import com.abiquo.abiserver.pojo.networking.VlanNetwork;
import org.dmtf.schemas.ovf.envelope._1.OrgNetworkType;

/**
 * Represents an vlan structure.
 * 
 * @author jdevesa@abiquo.com
 */
public class VlanNetworkHB extends OrgNetworkType implements IPojoHB<VlanNetwork>
{
    /**
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
     * The enterprise to which the Vlan is reserved for
     */
    private EnterpriseHB enterpriseHB;
    
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
     * @return the enterpriseHB
     */
    public EnterpriseHB getEnterpriseHB()
    {
        return enterpriseHB;
    }

    /**
     * @param enterpriseHB the enterpriseHB to set
     */
    public void setEnterpriseHB(EnterpriseHB enterpriseHB)
    {
        this.enterpriseHB = enterpriseHB;
    }
   
    @Override
    public VlanNetwork toPojo()
    {
        VlanNetwork vnet = new VlanNetwork();
        
        vnet.setNetworkId(getNetworkId());
        vnet.setNetworkName(getNetworkName());
        vnet.setVlanNetworkId(getVlanNetworkId());
        vnet.setVlanTag(getVlanTag());
        vnet.setDefaultNetwork(getDefaultNetwork());
        
        if (getConfiguration() != null)
        {
            vnet.setConfiguration(((NetworkConfigurationHB) getConfiguration()).toPojo());
        }
        
        return vnet;
    }

}
