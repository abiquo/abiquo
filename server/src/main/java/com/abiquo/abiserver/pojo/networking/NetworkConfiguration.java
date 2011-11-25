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

import com.abiquo.abiserver.business.hibernate.pojohb.networking.NetworkConfigurationHB;
import com.abiquo.abiserver.pojo.IPojo;

/**
 * The values that to configure a network in the transfer Flex object.
 * 
 * @author jdevesa@abiquo.com
 */
public class NetworkConfiguration implements Serializable, IPojo<NetworkConfigurationHB>
{

    /**
     * Generated serial version
     */
    private static final long serialVersionUID = -8090483025241191224L;

    /**
     * Identifer of the configuration.
     */
    private Integer networkConfigurationId;

    /**
     * The IP address of the gateway.
     */
    protected String gateway;

    /**
     * The network that defines the address.
     */
    protected String networkAddress;

    /**
     * The mask value in the integer way (/24)
     */
    protected Integer mask;

    /**
     * The mask value in IP way (255.255.255.0)
     */
    protected String netmask;

    /**
     * the value of the dns
     */
    protected String primaryDNS;

    /**
     * The value of the secondary DNS
     */
    protected String secondaryDNS;

    /**
     * The value of the sufix DNS.
     */
    protected String sufixDNS;

    /**
     * Fencemode (Bridged as default).
     */
    protected String fenceMode;

    /**
     * @return the networkConfigurationId
     */
    public Integer getNetworkConfigurationId()
    {
        return networkConfigurationId;
    }

    /**
     * @param networkConfigurationId the networkConfigurationId to set
     */
    public void setNetworkConfigurationId(Integer networkConfigurationId)
    {
        this.networkConfigurationId = networkConfigurationId;
    }

    /**
     * @return the gateway
     */
    public String getGateway()
    {
        return gateway;
    }

    /**
     * @param gateway the gateway to set
     */
    public void setGateway(String gateway)
    {
        this.gateway = gateway;
    }

    /**
     * @return the networkAddress
     */
    public String getNetworkAddress()
    {
        return networkAddress;
    }

    /**
     * @param networkAddress the networkAddress to set
     */
    public void setNetworkAddress(String networkAddress)
    {
        this.networkAddress = networkAddress;
    }

    /**
     * @return the mask
     */
    public Integer getMask()
    {
        return mask;
    }

    /**
     * @param mask the mask to set
     */
    public void setMask(Integer mask)
    {
        this.mask = mask;
    }

    /**
     * @return the netmask
     */
    public String getNetmask()
    {
        return netmask;
    }

    /**
     * @param netmask the netmask to set
     */
    public void setNetmask(String netmask)
    {
        this.netmask = netmask;
    }

    /**
     * @return the primaryDNS
     */
    public String getPrimaryDNS()
    {
        return primaryDNS;
    }

    /**
     * @param primaryDNS the primaryDNS to set
     */
    public void setPrimaryDNS(String primaryDNS)
    {
        this.primaryDNS = primaryDNS;
    }

    /**
     * @return the secondaryDNS
     */
    public String getSecondaryDNS()
    {
        return secondaryDNS;
    }

    /**
     * @param secondaryDNS the secondaryDNS to set
     */
    public void setSecondaryDNS(String secondaryDNS)
    {
        this.secondaryDNS = secondaryDNS;
    }

    /**
     * @return the sufixDNS
     */
    public String getSufixDNS()
    {
        return sufixDNS;
    }

    /**
     * @param sufixDNS the sufixDNS to set
     */
    public void setSufixDNS(String sufixDNS)
    {
        this.sufixDNS = sufixDNS;
    }

    /**
     * @return the fenceMode
     */
    public String getFenceMode()
    {
        return fenceMode;
    }

    /**
     * @param fenceMode the fenceMode to set
     */
    public void setFenceMode(String fenceMode)
    {
        this.fenceMode = fenceMode;
    }

    @Override
    public NetworkConfigurationHB toPojoHB()
    {
        NetworkConfigurationHB nconfHB = new NetworkConfigurationHB();

        nconfHB.setNetworkConfigurationId(getNetworkConfigurationId());
        nconfHB.setGateway(getGateway());
        nconfHB.setMask(getMask());
        nconfHB.setNetmask(getNetmask());
        nconfHB.setNetworkAddress(getNetworkAddress());
        nconfHB.setPrimaryDNS(getPrimaryDNS());
        nconfHB.setSecondaryDNS(getSecondaryDNS());
        nconfHB.setSufixDNS(getSufixDNS());
        nconfHB.setFenceMode(getFenceMode());

        return nconfHB;
    }

}
