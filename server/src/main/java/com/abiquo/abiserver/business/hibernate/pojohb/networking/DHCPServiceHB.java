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

import java.util.List;

import org.dmtf.schemas.ovf.envelope._1.DHCPServiceType;

/**
 * Class that defines which dhcp should we connect and its rules to provide the desired networking
 * configuration
 * 
 * @author jdevesa@abiquo.com
 */
public class DHCPServiceHB extends DHCPServiceType
{
    /**
     * Unique identifier inside the database
     */
    private Integer dhcpServiceId;

    /**
     * Identifier of the RemoteService.
     */
    private Integer dhcpRemoteServiceId;

    /**
     * Value that establish the relation between this class and its network configuration.
     */
    private Integer networkConfigurationId;

    /**
     * List of management ips.
     */
    private List<IpPoolManagementHB> ipPoolManagement;

    /**
     * @return the dhcpServiceId
     */
    public Integer getDhcpServiceId()
    {
        return dhcpServiceId;
    }

    /**
     * @param dhcpServiceId the dhcpServiceId to set
     */
    public void setDhcpServiceId(Integer dhcpServiceId)
    {
        this.dhcpServiceId = dhcpServiceId;
    }

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
     * @return the ipPoolManagement
     */
    public List<IpPoolManagementHB> getIpPoolManagement()
    {
        return ipPoolManagement;
    }

    /**
     * @param ipPoolManagement the ipPoolManagement to set
     */
    public void setIpPoolManagement(List<IpPoolManagementHB> ipPoolManagement)
    {
        this.ipPoolManagement = ipPoolManagement;
    }

    /**
     * @param dhcpRemoteServiceId the dhcpRemoteServiceId to set
     */
    public void setDhcpRemoteServiceId(Integer dhcpRemoteServiceId)
    {
        this.dhcpRemoteServiceId = dhcpRemoteServiceId;
    }

    /**
     * @return the dhcpRemoteServiceId
     */
    public Integer getDhcpRemoteServiceId()
    {
        return dhcpRemoteServiceId;
    }
}
