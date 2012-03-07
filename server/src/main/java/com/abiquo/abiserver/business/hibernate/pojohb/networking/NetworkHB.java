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
import java.util.List;

import org.dmtf.schemas.ovf.envelope._1.AbicloudNetworkType;
import org.dmtf.schemas.ovf.envelope._1.OrgNetworkType;

import com.abiquo.abiserver.business.hibernate.pojohb.IPojoHB;
import com.abiquo.abiserver.pojo.networking.Network;
import com.abiquo.abiserver.pojo.networking.VlanNetwork;

/**
 * POJO class that defines an standard network.
 * 
 * @author jdevesa@abiquo.com.
 */
public class NetworkHB extends AbicloudNetworkType implements IPojoHB<Network>
{
    /**
     * Just a simple identifier.
     */
    private Integer networkId;

    /**
     * @param networkId the networkId to set
     */
    public void setNetworkId(Integer networkId)
    {
        this.networkId = networkId;
    }

    /**
     * @return the networkId
     */
    public Integer getNetworkId()
    {
        return networkId;
    }

    @Override
    public Network toPojo()
    {
        Network network = new Network();

        network.setNetworkId(getNetworkId());
        network.setUuid(getUuid());
        network.setNetworks(new ArrayList<VlanNetwork>());
        for (OrgNetworkType vlanHB : getNetworks())
        {
            if (vlanHB != null)
            {
                network.getNetworks().add(((VlanNetworkHB) vlanHB).toPojo());
            }
        }

        return network;
    }

    public void setNetworks(List<VlanNetworkHB> networks)
    {
        if (this.networks == null)
        {
            this.networks = new ArrayList<OrgNetworkType>();
        }
        this.networks.clear();
        if (networks != null)
        {
            this.networks.addAll(networks);
        }
    }
}
