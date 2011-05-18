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

import com.abiquo.model.transport.SingleResourceTransportDto;

@XmlRootElement(name = "virtualMachine")
public class VirtualMachineDto extends SingleResourceTransportDto
{
    private Integer id;

    public Integer getId()
    {
        return id;
    }

    public void setId(Integer id)
    {
        this.id = id;
    }

    private String name;

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    private String description;

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    private int ram;

    public int getRam()
    {
        return ram;
    }

    public void setRam(int ram)
    {
        this.ram = ram;
    }

    private int cpu;

    public int getCpu()
    {
        return cpu;
    }

    public void setCpu(int cpu)
    {
        this.cpu = cpu;
    }

    private int hd;

    public int getHd()
    {
        return hd;
    }

    public void setHd(int hd)
    {
        this.hd = hd;
    }

    private int vdrpPort;

    public int getVdrpPort()
    {
        return vdrpPort;
    }

    public void setVdrpPort(int vdrpPort)
    {
        this.vdrpPort = vdrpPort;
    }

    private String vdrpIp;

    public String getVdrpIP()
    {
        return vdrpIp;
    }

    public void setVdrpIP(String vdrpIp)
    {
        this.vdrpIp = vdrpIp;
    }

    private int idState;

    public int getIdState()
    {
        return idState;
    }

    public void setIdState(int idState)
    {
        this.idState = idState;
    }

    private int highDisponibility;

    public int getHighDisponibility()
    {
        return highDisponibility;
    }

    public void setHighDisponibility(int highDisponibility)
    {
        this.highDisponibility = highDisponibility;
    }

    private int idType;

    public int getIdType()
    {
        return idType;
    }

    public void setIdType(int idType)
    {
        this.idType = idType;
    }

    private String ipmiIp;

    public String getIpmiIp()
    {
        return ipmiIp;
    }

    public void setIpmiIp(final String ipmiIp)
    {
        this.ipmiIp = ipmiIp;
    }

    private Integer ipmiPort;

    public Integer getIpmiPort()
    {
        return ipmiPort;
    }

    public void setIpmiPort(final Integer ipmiPort)
    {
        this.ipmiPort = ipmiPort;
    }

    private String ipmiUser;

    public String getIpmiUser()
    {
        return ipmiUser;
    }

    public void setIpmiUser(final String ipmiUser)
    {
        this.ipmiUser = ipmiUser;
    }

    private String ipmiPassword;

    public String getIpmiPassword()
    {
        return ipmiPassword;
    }

    public void setIpmiPassword(final String ipmiPassword)
    {
        this.ipmiPassword = ipmiPassword;
    }
}
