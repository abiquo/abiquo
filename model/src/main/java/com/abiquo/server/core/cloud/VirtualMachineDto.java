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

import org.springframework.util.StringUtils;

import com.abiquo.model.transport.SingleResourceTransportDto;

@XmlRootElement(name = "virtualMachine")
public class VirtualMachineDto extends SingleResourceTransportDto implements
    Comparable<VirtualMachineDto>
{
    private Integer id;

    public Integer getId()
    {
        return id;
    }

    public void setId(final Integer id)
    {
        this.id = id;
    }

    private String name;

    public String getName()
    {
        return name;
    }

    public void setName(final String name)
    {
        this.name = name;
    }

    private String description;

    public String getDescription()
    {
        return description;
    }

    public void setDescription(final String description)
    {
        this.description = description;
    }

    private int ram;

    public int getRam()
    {
        return ram;
    }

    public void setRam(final int ram)
    {
        this.ram = ram;
    }

    private int cpu;

    public int getCpu()
    {
        return cpu;
    }

    public void setCpu(final int cpu)
    {
        this.cpu = cpu;
    }

    private long hd;

    public long getHd()
    {
        return hd;
    }

    public void setHd(final long hd)
    {
        this.hd = hd;
    }

    private int vdrpPort;

    public int getVdrpPort()
    {
        return vdrpPort;
    }

    public void setVdrpPort(final int vdrpPort)
    {
        this.vdrpPort = vdrpPort;
    }

    private String vdrpIp;

    public String getVdrpIP()
    {
        return vdrpIp;
    }

    public void setVdrpIP(final String vdrpIp)
    {
        this.vdrpIp = vdrpIp;
    }

    private int idState;

    public int getIdState()
    {
        return idState;
    }

    public void setIdState(final int idState)
    {
        this.idState = idState;
    }

    // private VirtualMachineState state;
    //
    // public void setState(final VirtualMachineState state)
    // {
    // this.state = state;
    // }
    //
    // public VirtualMachineState getState()
    // {
    // return state;
    // }

    private int highDisponibility;

    public int getHighDisponibility()
    {
        return highDisponibility;
    }

    public void setHighDisponibility(final int highDisponibility)
    {
        this.highDisponibility = highDisponibility;
    }

    private int idType;

    public int getIdType()
    {
        return idType;
    }

    public void setIdType(final int idType)
    {
        this.idType = idType;
    }

    private String password;

    public String getPassword()
    {
        return password;
    }

    public void setPassword(final String password)
    {
        this.password = password;
    }

    private String uuid;

    public String getUuid()
    {
        return uuid;
    }

    public void setUuid(final String uuid)
    {
        this.uuid = uuid;
    }

    @Override
    public int compareTo(final VirtualMachineDto vm2)
    {
        if (StringUtils.hasText(this.getName()) && StringUtils.hasText(vm2.getName()))
        {
            return this.getName().compareTo(vm2.getName());
        }
        else if (!StringUtils.hasText(this.getName()) && !StringUtils.hasText(vm2.getName()))
        {
            return 0;
        }
        else if (!StringUtils.hasText(this.getName()) && StringUtils.hasText(vm2.getName()))
        {
            return -1;
        }
        else
        {
            return 1;
        }
    }

}
