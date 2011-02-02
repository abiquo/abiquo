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
package com.abiquo.virtualfactory.network;

/**
 * This class represents a virtual NIC object of a Virtual Machine
 * 
 * @author pnavarro
 */
public class VirtualNIC
{

    /**
     * corresponds to the virtual switch name where the Virtual Machine NIC will be connected
     */
    private String vSwitchName;

    /**
     * The MAC address
     */
    private String macAddress;

    /**
     * The network name
     */
    private String networkName;

    /**
     * VLAN tag
     */
    private int vlanTag;
    
    /**
     * Order inside the virtualmachine
     */
    private int order;

    /**
     * Basic constructor
     * 
     * @param vswitchName the virtual switch name that corresponds to the vswitch name where the
     *            Virtual NIC will be connected
     * @param macAddress the mac Address of the virtual NICK
     * @param vlanTag the VLAN tag
     * @param networkName the network name
     */
    public VirtualNIC(String vswitchName, String macAddress, int vlanTag, String networkName, Integer order)
    {
        this.vSwitchName = vswitchName;
        this.macAddress = macAddress;
        this.vlanTag = vlanTag;
        this.networkName = networkName;
        this.order = order;
    }

    /**
     * Gets the vSwtitch Name that corresponds to the virtual Switch where the NIC will be connected
     * 
     * @return the networkName
     */
    public String getVSwitchName()
    {
        return vSwitchName;
    }

    /**
     * Sets the virtual switch name
     * 
     * @param vSwitchName the networkName to set
     */
    public void setVSwitchName(String vSwitchName)
    {
        this.vSwitchName = vSwitchName;
    }

    /**
     * Gets the MAC address
     * 
     * @return the macAddress
     */
    public String getMacAddress()
    {
        return macAddress;
    }

    /**
     * Sets the MAC address
     * 
     * @param macAddress the macAddress to set
     */
    public void setMacAddress(String macAddress)
    {
        this.macAddress = macAddress;
    }

    /**
     * Sets the VLAN tag
     * 
     * @param vlanTag the vlanTag to set
     */
    public void setVlanTag(int vlanTag)
    {
        this.vlanTag = vlanTag;
    }

    /**
     * Gets the VLAN tag
     * 
     * @return the vlanTag
     */
    public int getVlanTag()
    {
        return vlanTag;
    }

    /**
     * Sets the network name
     * 
     * @param networkName the networkName to set
     */
    public void setNetworkName(String networkName)
    {
        this.networkName = networkName;
    }

    /**
     * Gets the network name
     * 
     * @return the networkName
     */
    public String getNetworkName()
    {
        return networkName;
    }

    /**
     * @param order the order to set
     */
    public void setOrder(int order)
    {
        this.order = order;
    }

    /**
     * @return the order
     */
    public int getOrder()
    {
        return order;
    }

}
