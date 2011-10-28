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

import com.abiquo.abiserver.business.hibernate.pojohb.networking.IpPoolManagementHB;
import com.abiquo.abiserver.business.hibernate.pojohb.virtualhardware.ResourceManagementHB;
import com.abiquo.abiserver.pojo.IPojo;
import com.abiquo.abiserver.pojo.virtualhardware.ResourceManagement;

/**
 * Transfer object to return to the client.
 * 
 * @author jdevesa@abiquo.com
 */
public class IpPoolManagement extends ResourceManagement implements Serializable,
    IPojo<ResourceManagementHB>
{

    /**
     * Generated serial version.
     */
    private static final long serialVersionUID = -1825515015832427714L;

    /**
     * Identifier of its dhcp service definition.
     */
    private Integer dhcpServiceId;

    /**
     * MAC address of the resource
     */
    protected String mac;

    /**
     * Name of the DHCP rule
     */
    protected String name;

    /**
     * IP address asigned.
     */
    protected String ip;

    /**
     * The name of the vlan network where the Resource belongs.
     */
    private String vlanNetworkName;

    /**
     * The id of the vlan network where the resource belongs to.
     */
    private Integer vlanNetworkId;

    /**
     * Name of the enterprise.
     */
    private String enterpriseName;

    /**
     * Identifier of the enterprise.
     */
    private Integer enterpriseId;

    /**
     * Should we configure the gateway?
     */
    private Boolean configureGateway;

    /**
     * The IP is in quarantine?
     */
    private Boolean quarantine;

    /**
     * The IP is available?.
     */
    private Boolean available;

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
    public void setDhcpServiceId(final Integer dhcpServiceId)
    {
        this.dhcpServiceId = dhcpServiceId;
    }

    /**
     * @return the mac
     */
    public String getMac()
    {
        return mac;
    }

    /**
     * @param mac the mac to set
     */
    public void setMac(final String mac)
    {
        this.mac = mac;
    }

    /**
     * @return the name
     */
    public String getName()
    {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(final String name)
    {
        this.name = name;
    }

    /**
     * @return the ip
     */
    public String getIp()
    {
        return ip;
    }

    /**
     * @param ip the ip to set
     */
    public void setIp(final String ip)
    {
        this.ip = ip;
    }

    /**
     * @return the configureGateway
     */
    public Boolean getConfigureGateway()
    {
        return configureGateway;
    }

    /**
     * @param configureGateway the configureGateway to set
     */
    public void setConfigureGateway(final Boolean configureGateway)
    {
        this.configureGateway = configureGateway;
    }

    /**
     * @param vlanNetworkName the vlanNetworkName to set
     */
    public void setVlanNetworkName(final String vlanNetworkName)
    {
        this.vlanNetworkName = vlanNetworkName;
    }

    /**
     * @return the vlanNetworkName
     */
    public String getVlanNetworkName()
    {
        return vlanNetworkName;
    }

    /**
     * @return the enterpriseName
     */
    public String getEnterpriseName()
    {
        return enterpriseName;
    }

    /**
     * @param enterpriseName the enterpriseName to set
     */
    public void setEnterpriseName(final String enterpriseName)
    {
        this.enterpriseName = enterpriseName;
    }

    /**
     * @return the enterpriseId
     */
    public Integer getEnterpriseId()
    {
        return enterpriseId;
    }

    /**
     * @param enterpriseId the enterpriseId to set
     */
    public void setEnterpriseId(final Integer enterpriseId)
    {
        this.enterpriseId = enterpriseId;
    }

    /**
     * @param quarantine the quarantine to set
     */
    public void setQuarantine(final Boolean quarantine)
    {
        this.quarantine = quarantine;
    }

    /**
     * @return the quarantine
     */
    public Boolean getQuarantine()
    {
        return quarantine;
    }

    @Override
    public IpPoolManagementHB toPojoHB()
    {
        IpPoolManagementHB ipPoolHB = new IpPoolManagementHB();

        ipPoolHB.setConfigureGateway(getConfigureGateway());
        ipPoolHB.setDhcpServiceId(getDhcpServiceId());
        ipPoolHB.setIdManagement(getIdManagement());
        ipPoolHB.setIdResourceType(getIdResourceType());
        ipPoolHB.setIp(getIp());
        ipPoolHB.setMac(getMac());
        ipPoolHB.setName(getMac());
        ipPoolHB.setVlanNetworkName(getVlanNetworkName());
        ipPoolHB.setQuarantine(getQuarantine());

        return ipPoolHB;
    }

    public Integer getVlanNetworkId()
    {
        return vlanNetworkId;
    }

    public void setVlanNetworkId(final Integer vlanNetworkId)
    {
        this.vlanNetworkId = vlanNetworkId;
    }

    public Boolean getAvailable()
    {
        return available;
    }

    public void setAvailable(final Boolean available)
    {
        this.available = available;
    }

}
