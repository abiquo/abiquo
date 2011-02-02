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

package com.abiquo.virtualfactory.model.config;

import java.util.Map;

import com.abiquo.virtualfactory.model.IHypervisor;

/**
 * All the values to set a network configuration
 * @author abiquo
 */
public class VirtualNetworkConfiguration
{

    public static enum ForwardMode
    {
        NAT, ROUTE, NONE
    };
    
    /** The hyper. */
    protected IHypervisor hyper;
    
    /** The network name */
    protected String netName;
    
    /** the gateway of the network */
    protected String netGateway;

    /** first IP of the range */
    protected String netFirstIP;

    /** last IP of the range */
    protected String netLastIP;
    
    /** mask of the network */
    protected String netMask;
    
    /** UUID of the network */
    protected String uuid;

    /** forward mode to access outside the vlan */
    protected ForwardMode forwardMode;
    
    /** mapping between IP and MAC  */
    protected Map<String,String> macFilter;
        
    /** @return the hyper */
    public IHypervisor getHyper()
    {
        return hyper;
    }

    /** @param hyper the hyper to set */
    public void setHyper(IHypervisor hyper)
    {
        this.hyper = hyper;
    }
    
    /** @param netName the netName to set */
    public void setNetName(String netName)
    {
        this.netName = netName;
    }

    /** @return the netName */
    public String getNetName()
    {
        return netName;
    }

    /** @return the netGateway */
    public String getNetGateway()
    {
        return netGateway;
    }

    /** @param netGateway the netGateway to set */
    public void setNetGateway(String netGateway)
    {
        this.netGateway = netGateway;
    }

    /** @return the netFirstIP */
    public String getNetFirstIP()
    {
        return netFirstIP;
    }

    /** @param netFirstIP the netFirstIP to set */
    public void setNetFirstIP(String netFirstIP)
    {
        this.netFirstIP = netFirstIP;
    }

    /** @return the netLastIP */
    public String getNetLastIP()
    {
        return netLastIP;
    }

    /** @param netLastIP the netLastIP to set */
    public void setNetLastIP(String netLastIP)
    {
        this.netLastIP = netLastIP;
    }

    /**
     * @return the netMask
     */
    public String getNetMask()
    {
        return netMask;
    }

    /**
     * @param netMask the netMask to set
     */
    public void setNetMask(String netMask)
    {
        this.netMask = netMask;
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

    /** @return the forwardMode */
    public ForwardMode getForwardMode()
    {
        return forwardMode;
    }

    /** @param forwardMode the forwardMode to set */
    public void setForwardMode(ForwardMode forwardMode)
    {
        this.forwardMode = forwardMode;
    }

    /**
     * @return the macFilter
     */
    public Map<String, String> getMacFilter()
    {
        return macFilter;
    }

    /**
     * @param macFilter the macFilter to set
     */
    public void setMacFilter(Map<String, String> macFilter)
    {
        this.macFilter = macFilter;
    }

}
