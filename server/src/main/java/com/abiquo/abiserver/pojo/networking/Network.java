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

import java.io.Serializable;
import java.util.List;

import com.abiquo.abiserver.business.hibernate.pojohb.networking.NetworkHB;
import com.abiquo.abiserver.pojo.IPojo;

/**
 * Transfer Pojo Network.
 * 
 * @author jdevesa@abiquo.com
 */
public class Network implements Serializable, IPojo<NetworkHB>
{
    /**
     * Generated serial version.
     */
    private static final long serialVersionUID = -1757795665811340593L;

    /**
     * Just a simple identifier.
     */
    private Integer networkId;

    /**
     * UUID.
     */
    protected String uuid;

    /**
     * List of private networks.
     */
    private List<VlanNetwork> networks;

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
     * @return the uuid
     */
    public String getUuid()
    {
        return uuid;
    }

    /**
     * @param uuid the uuid to set
     */
    public void setUuid(String uuid)
    {
        this.uuid = uuid;
    }

    /**
     * @return the networks
     */
    public List<VlanNetwork> getNetworks()
    {
        return networks;
    }

    /**
     * @param networks the networks to set
     */
    public void setNetworks(List<VlanNetwork> networks)
    {
        this.networks = networks;
    }

    @Override
    public NetworkHB toPojoHB()
    {
        NetworkHB networkHB = new NetworkHB();
        
        networkHB.setNetworkId(getNetworkId());
        networkHB.setUuid(getUuid());
        
        for (VlanNetwork vlan : getNetworks())
        {
            networkHB.getNetworks().add(vlan.toPojoHB());
        }
        
        return null;
    }

}
