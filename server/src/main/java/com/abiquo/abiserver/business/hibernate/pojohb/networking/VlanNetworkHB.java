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

import java.util.HashSet;
import java.util.Set;

import org.dmtf.schemas.ovf.envelope._1.OrgNetworkType;

import com.abiquo.abiserver.business.hibernate.pojohb.IPojoHB;
import com.abiquo.abiserver.business.hibernate.pojohb.user.EnterpriseHB;
import com.abiquo.abiserver.pojo.networking.DhcpOption;
import com.abiquo.abiserver.pojo.networking.VlanNetwork;

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
     * The enterprise to which the Vlan is reserved for
     */
    private EnterpriseHB enterpriseHB;

    private Set<DhcpOptionHB> dhcpOptionsHB;

    /**
     * @return the vlanNetworkId
     */
    public Integer getVlanNetworkId()
    {
        return vlanNetworkId;
    }

    private String networkType;

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
     * @return the enterpriseHB
     */
    public EnterpriseHB getEnterpriseHB()
    {
        return enterpriseHB;
    }

    /**
     * @param enterpriseHB the enterpriseHB to set
     */
    public void setEnterpriseHB(final EnterpriseHB enterpriseHB)
    {
        this.enterpriseHB = enterpriseHB;
    }

    public Set<DhcpOptionHB> getDhcpOptionsHB()
    {
        return dhcpOptionsHB;
    }

    public void setDhcpOptionsHB(final Set<DhcpOptionHB> dhcpOptionsHB)
    {
        this.dhcpOptionsHB = dhcpOptionsHB;
    }

    public void setNetworkType(final String networkType)
    {
        this.networkType = networkType;
    }

    public String getNetworkType()
    {
        return networkType;
    }

    @Override
    public VlanNetwork toPojo()
    {
        VlanNetwork vnet = new VlanNetwork();

        vnet.setNetworkId(getNetworkId());
        vnet.setNetworkName(getNetworkName());
        vnet.setVlanNetworkId(getVlanNetworkId());
        vnet.setVlanTag(getVlanTag());
        vnet.setNetworkType(getNetworkType());

        if (getConfiguration() != null)
        {
            vnet.setConfiguration(((NetworkConfigurationHB) getConfiguration()).toPojo());
        }

        Set<DhcpOption> dhcpOption = new HashSet<DhcpOption>();
        if (dhcpOptionsHB != null)
        {
            for (DhcpOptionHB dHB : dhcpOptionsHB)
            {
                dhcpOption.add(dHB.toPojo());
            }
        }

        vnet.setDhcpOptions(dhcpOption);

        return vnet;
    }

}
