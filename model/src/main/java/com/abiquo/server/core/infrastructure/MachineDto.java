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

package com.abiquo.server.core.infrastructure;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.abiquo.model.enumerator.HypervisorType;
import com.abiquo.model.transport.SingleResourceTransportDto;
import com.abiquo.server.core.infrastructure.Machine.State;

@XmlRootElement(name = "machine")
public class MachineDto extends SingleResourceTransportDto implements Serializable
{
    private static final long serialVersionUID = -4971248626582072165L;

    private Integer id;

    private String name, description;

    private Integer realRamInMb, virtualRamInMb, virtualRamUsedInMb;

    private Integer realCpuCores, virtualCpuCores, virtualCpusUsed, virtualCpusPerCore;

    private Long realHardDiskInMb, virtualHardDiskInMb, virtualHardDiskUsedInMb;

    private State state;

    private String virtualSwitch;
    
    private Integer port;

    private String ip;
    
    private String ipService;

    private HypervisorType type;

    private String user;
    
    private String password;
    
    private DatastoresDto datastores;
   

    /**
     * @return the port
     */
    public Integer getPort()
    {
        return port;
    }

    /**
     * @param port the port to set
     */
    public void setPort(Integer port)
    {
        this.port = port;
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
    public void setIp(String ip)
    {
        this.ip = ip;
    }

    /**
     * @return the ipService
     */
    public String getIpService()
    {
        return ipService;
    }

    /**
     * @param ipService the ipService to set
     */
    public void setIpService(String ipService)
    {
        this.ipService = ipService;
    }

    /**
     * @return the type
     */
    public HypervisorType getType()
    {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(HypervisorType type)
    {
        this.type = type;
    }

    /**
     * @return the user
     */
    public String getUser()
    {
        return user;
    }

    /**
     * @param user the user to set
     */
    public void setUser(String user)
    {
        this.user = user;
    }

    /**
     * @return the password
     */
    public String getPassword()
    {
        return password;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password)
    {
        this.password = password;
    }

    public Integer getId()
    {
        return id;
    }

    public void setId(Integer id)
    {
        this.id = id;
    }

    public State getState()
    {
        return state;
    }

    public void setState(State state)
    {
        this.state = state;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    @XmlElement(name = "realRam")
    public Integer getRealRamInMb()
    {
        return getDefaultMb(realRamInMb).intValue();
    }

    public void setRealRamInMb(Integer realRamInMb)
    {
        this.realRamInMb = realRamInMb;
    }

    @XmlElement(name = "ram")
    public Integer getVirtualRamInMb()
    {
        return getDefaultMb(virtualRamInMb).intValue();
    }

    public void setVirtualRamInMb(Integer virtualRamInMb)
    {
        this.virtualRamInMb = virtualRamInMb;
    }

    @XmlElement(name = "ramUsed")
    public Integer getVirtualRamUsedInMb()
    {
        return getDefaultMb(virtualRamUsedInMb).intValue();
    }

    public void setVirtualRamUsedInMb(Integer virtualRamUsedInMb)
    {
        this.virtualRamUsedInMb = virtualRamUsedInMb;
    }

    @XmlElement(name = "realCpu")
    public Integer getRealCpuCores()
    {
        return (Integer) getDefaultMb(realCpuCores);
    }

    public void setRealCpuCores(Integer realCpuCores)
    {
        this.realCpuCores = realCpuCores;
    }

    @XmlElement(name = "cpu")
    public Integer getVirtualCpuCores()
    {
        return (Integer) getDefaultMb(virtualCpuCores);
    }

    public void setVirtualCpuCores(Integer virtualCpuCores)
    {
        this.virtualCpuCores = virtualCpuCores;
    }

    @XmlElement(name = "cpuUsed")
    public Integer getVirtualCpusUsed()
    {
        return (Integer) getDefaultMb(virtualCpusUsed);
    }

    public void setVirtualCpusUsed(Integer virtualCpusUsed)
    {
        this.virtualCpusUsed = virtualCpusUsed;
    }

    @XmlElement(name = "cpuRatio")
    public Integer getVirtualCpusPerCore()
    {
        return (Integer) getDefaultMb(virtualCpusPerCore);
    }

    public void setVirtualCpusPerCore(Integer virtualCpusPerCore)
    {
        this.virtualCpusPerCore = virtualCpusPerCore;
    }

    @XmlElement(name = "realHd")
    public Long getRealHardDiskInMb()
    {
        return getDefaultMb(realHardDiskInMb).longValue();
    }

    public void setRealHardDiskInMb(Long realHardDiskInMb)
    {
        this.realHardDiskInMb = realHardDiskInMb;
    }

    @XmlElement(name = "hd")
    public Long getVirtualHardDiskInMb()
    {
        return (Long) getDefaultMb(virtualHardDiskInMb);
    }

    public void setVirtualHardDiskInMb(Long virtualHardDiskInMb)
    {
        this.virtualHardDiskInMb = virtualHardDiskInMb;
    }

    @XmlElement(name = "hdUsed")
    public Long getVirtualHardDiskUsedInMb()
    {
        return getDefaultMb(virtualHardDiskUsedInMb).longValue();
    }

    public void setVirtualHardDiskUsedInMb(Long virtualHardDiskUsedInMb)
    {
        this.virtualHardDiskUsedInMb = virtualHardDiskUsedInMb;
    }

    private Number getDefaultMb(Number mb)
    {
        return mb == null ? 1 : mb;
    }

    public String getVirtualSwitch()
    {
        return virtualSwitch;
    }

    public void setVirtualSwitch(String virtualSwitch)
    {
        this.virtualSwitch = virtualSwitch;
    }

    /**
     * @param datastores the datastores to set
     */
    public void setDatastores(DatastoresDto datastores)
    {
        this.datastores = datastores;
    }

    /**
     * @return the datastores
     */
    public DatastoresDto getDatastores()
    {
        if (datastores == null)
        {
            datastores = new DatastoresDto();
        }
        return datastores;
    }

}
