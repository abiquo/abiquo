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

import org.dmtf.schemas.ovf.envelope._1.NetworkConfigurationType;

import com.abiquo.abiserver.business.hibernate.pojohb.IPojoHB;
import com.abiquo.abiserver.pojo.networking.NetworkConfiguration;

/**
 * Defines all the values that configures a network.
 * 
 * @author jdevesa@abiquo.com
 */
public class NetworkConfigurationHB extends NetworkConfigurationType implements IPojoHB<NetworkConfiguration>
{
    /**
     * Identifer of the configuration.
     */
    private Integer networkConfigurationId;
    
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

    @Override
    public NetworkConfiguration toPojo()
    {
        NetworkConfiguration nconf = new NetworkConfiguration();
        
        nconf.setNetworkConfigurationId(getNetworkConfigurationId());
        nconf.setGateway(getGateway());
        nconf.setMask(getMask());
        nconf.setNetmask(getNetmask());
        nconf.setNetworkAddress(getNetworkAddress());
        nconf.setPrimaryDNS(getPrimaryDNS());
        nconf.setSecondaryDNS(getSecondaryDNS());
        nconf.setSufixDNS(getSufixDNS());
        nconf.setFenceMode(getFenceMode());
        
        return nconf;
    }
    
    
}
