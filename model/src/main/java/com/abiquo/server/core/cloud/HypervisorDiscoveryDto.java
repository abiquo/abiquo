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

package com.abiquo.server.core.cloud;

import javax.xml.bind.annotation.XmlRootElement;

import com.abiquo.model.enumerator.HypervisorType;
import com.abiquo.model.transport.SingleResourceTransportDto;

@XmlRootElement(name = "hypervisor")
public class HypervisorDiscoveryDto extends SingleResourceTransportDto
{
    
    public static final String MEDIA_TYPE = "application/discovery+xml";
    
    private Integer port;

    private String ipFrom;
    
    private String ipTo;

    private HypervisorType type;

    private String user;
    
    private String password;
    
    private String vSwitch;

    public Integer getPort()
    {
        return port;
    }

    public void setPort(Integer port)
    {
        this.port = port;
    }

    public String getIpFrom()
    {
        return ipFrom;
    }

    public void setIpFrom(String ipFrom)
    {
        this.ipFrom = ipFrom;
    }

    public String getIpTo()
    {
        return ipTo;
    }

    public void setIpTo(String ipTo)
    {
        this.ipTo = ipTo;
    }

    public HypervisorType getType()
    {
        return type;
    }

    public void setType(HypervisorType type)
    {
        this.type = type;
    }

    public String getUser()
    {
        return user;
    }

    public void setUser(String user)
    {
        this.user = user;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public String getvSwitch()
    {
        return vSwitch;
    }

    public void setvSwitch(String vSwitch)
    {
        this.vSwitch = vSwitch;
    }

}
