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

import java.io.Serializable;

import com.abiquo.abiserver.business.hibernate.pojohb.IPojoHB;
import com.abiquo.abiserver.business.hibernate.pojohb.virtualhardware.ResourceManagementHB;
import com.abiquo.abiserver.pojo.networking.IpPoolManagement;
import com.abiquo.abiserver.pojo.virtualhardware.ResourceManagement;

/**
 * Each one of the static rules to send to the DHCP service to configure the relation between mac
 * and IP
 * 
 * @author jdevesa@abiquo.com
 */
public class IpPoolManagementHB extends ResourceManagementHB implements Serializable,
    IPojoHB<ResourceManagement>
{
    /**
     * Serial version ID.
     */
    private static final long serialVersionUID = -3676702157022062180L;

    /**
     * IP address of the resource.
     */
    protected String ip;

    /**
     * MAC address asigned to the ip Pool
     */
    protected String mac;

    /**
     * Name of the dhcp rule.
     */
    protected String name;

    /**
     * Should we configure the gateway?
     */
    protected Boolean configureGateway;

    /**
     * Identifier of its dhcp service definition.
     */
    private Integer dhcpServiceId;

    /**
     * The name of the vlan network where the Resource belongs.
     */
    private String vlanNetworkName;

    /**
     * Identifier of its vlan network
     */
    private Integer vlanNetworkId;
    
    /**
     * If the IP is in quarantine.
     */
    private Boolean quarantine;
    
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
    public void setIp(String ip)
    {
        this.ip = ip;
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
    public void setMac(String mac)
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
    public void setName(String name)
    {
        this.name = name;
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
    public void setConfigureGateway(Boolean configureGateway)
    {
        this.configureGateway = configureGateway;
    }

    @Override
    public void deallocateResource()
    {
        // When we deallocate a result, we set the State to 0
        setVirtualApp(null);
        setVirtualMachine(null);
    }

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
     * @return the vlanNetworkName
     */
    public String getVlanNetworkName()
    {
        return vlanNetworkName;
    }

    /**
     * @param vlanNetworkName the vlanNetworkName to set
     */
    public void setVlanNetworkName(String vlanNetworkName)
    {
        this.vlanNetworkName = vlanNetworkName;
    }

    /**
     * @param vlanNetworkId the vlanNetworkId to set
     */
    public void setVlanNetworkId(Integer vlanNetworkId)
    {
        this.vlanNetworkId = vlanNetworkId;
    }

    /**
     * @return the vlanNetworkId
     */
    public Integer getVlanNetworkId()
    {
        return vlanNetworkId;
    }

   
    /**
     * @param quarantine the quarantine to set
     */
    public void setQuarantine(Boolean quarantine)
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
    public IpPoolManagement toPojo()
    {
        IpPoolManagement ipPool = new IpPoolManagement();

        ipPool.setConfigureGateway(getConfigureGateway());
        ipPool.setDhcpServiceId(getDhcpServiceId());
        ipPool.setIdManagement(getIdManagement());
        ipPool.setIdResourceType(getIdResourceType());
        ipPool.setIp(getIp());
        ipPool.setMac(setMacAddress(getMac()));
        ipPool.setName(getName());
        ipPool.setVlanNetworkName(vlanNetworkName);
        ipPool.setQuarantine(getQuarantine());
        
        if (getVirtualApp() != null)
        {
            ipPool.setVirtualApplianceId(getVirtualApp().getIdVirtualApp());
            ipPool.setVirtualApplianceName(getVirtualApp().getName());
        }

        if (getVirtualDataCenter() != null)
        {
            ipPool.setVirtualDatacenterId(getVirtualDataCenter().getIdVirtualDataCenter());
            ipPool.setVirtualDatacenterName(getVirtualDataCenter().getName());
            ipPool.setEnterpriseId(getVirtualDataCenter().getEnterpriseHB().getIdEnterprise());
            ipPool.setEnterpriseName(getVirtualDataCenter().getEnterpriseHB().getName());
        }

        if (getVirtualMachine() != null)
        {
            ipPool.setVirtualMachineId(getVirtualMachine().getIdVm());
            ipPool.setVirtualMachineName(getVirtualMachine().getName());
        }

        return ipPool;
    }

    private String setMacAddress(String mac2)
    {
        if (mac2 != null)
        {
            if (!mac2.contains(":"))
            {
                StringBuilder formattedMA =
                    new StringBuilder(mac2.substring(0, 2) + ":");
                formattedMA.append(mac2.substring(2, 4) + ":");
                formattedMA.append(mac2.substring(4, 6) + ":");
                formattedMA.append(mac2.substring(6, 8) + ":");
                formattedMA.append(mac2.substring(8, 10) + ":");
                formattedMA.append(mac2.substring(10, 12));
            
                return formattedMA.toString();
            }
            else
            {
                return mac2;
            }
        }
        else
        {
            return null;
        }
    }

}
