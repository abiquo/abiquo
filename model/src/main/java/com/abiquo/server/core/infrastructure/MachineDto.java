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

import com.abiquo.appliancemanager.transport.MachineState;
import com.abiquo.model.enumerator.HypervisorType;
import com.abiquo.model.transport.SingleResourceTransportDto;

@XmlRootElement(name = "machine")
public class MachineDto extends SingleResourceTransportDto implements Serializable
{
    private static final long serialVersionUID = -4971248626582072165L;

    private Integer id;

    private String name, description;

    private Integer realRamInMb, virtualRamInMb, virtualRamUsedInMb;

    private Integer realCpuCores, virtualCpuCores, virtualCpusUsed, virtualCpusPerCore;

    private Long realHardDiskInMb, virtualHardDiskInMb, virtualHardDiskUsedInMb;

    private MachineState state;

    private String virtualSwitch;

    private Integer port;

    private String ip;

    private String ipService;

    private HypervisorType type;

    private String user;

    private String password;

    private DatastoresDto datastores;

    private String ipmiIp;

    private Integer ipmiPort;

    private String ipmiUser;

    private String ipmiPassword;

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
    public void setPort(final Integer port)
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
    public void setIp(final String ip)
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
    public void setIpService(final String ipService)
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
    public void setType(final HypervisorType type)
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
    public void setUser(final String user)
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
    public void setPassword(final String password)
    {
        this.password = password;
    }

    public Integer getId()
    {
        return id;
    }

    public void setId(final Integer id)
    {
        this.id = id;
    }

    public MachineState getState()
    {
        return state;
    }

    public void setState(final MachineState state)
    {
        this.state = state;
    }

    public String getName()
    {
        return name;
    }

    public void setName(final String name)
    {
        this.name = name;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(final String description)
    {
        this.description = description;
    }

    @XmlElement(name = "realRam")
    public Integer getRealRamInMb()
    {
        return getDefaultMb(realRamInMb).intValue();
    }

    public void setRealRamInMb(final Integer realRamInMb)
    {
        this.realRamInMb = realRamInMb;
    }

    @XmlElement(name = "ram")
    public Integer getVirtualRamInMb()
    {
        return getDefaultMb(virtualRamInMb).intValue();
    }

    public void setVirtualRamInMb(final Integer virtualRamInMb)
    {
        this.virtualRamInMb = virtualRamInMb;
    }

    @XmlElement(name = "ramUsed")
    public Integer getVirtualRamUsedInMb()
    {
        return getDefaultMb(virtualRamUsedInMb).intValue();
    }

    public void setVirtualRamUsedInMb(final Integer virtualRamUsedInMb)
    {
        this.virtualRamUsedInMb = virtualRamUsedInMb;
    }

    @XmlElement(name = "realCpu")
    public Integer getRealCpuCores()
    {
        return (Integer) getDefaultMb(realCpuCores);
    }

    public void setRealCpuCores(final Integer realCpuCores)
    {
        this.realCpuCores = realCpuCores;
    }

    @XmlElement(name = "cpu")
    public Integer getVirtualCpuCores()
    {
        return (Integer) getDefaultMb(virtualCpuCores);
    }

    public void setVirtualCpuCores(final Integer virtualCpuCores)
    {
        this.virtualCpuCores = virtualCpuCores;
    }

    @XmlElement(name = "cpuUsed")
    public Integer getVirtualCpusUsed()
    {
        return (Integer) getDefaultMb(virtualCpusUsed);
    }

    public void setVirtualCpusUsed(final Integer virtualCpusUsed)
    {
        this.virtualCpusUsed = virtualCpusUsed;
    }

    @XmlElement(name = "cpuRatio")
    public Integer getVirtualCpusPerCore()
    {
        return (Integer) getDefaultMb(virtualCpusPerCore);
    }

    public void setVirtualCpusPerCore(final Integer virtualCpusPerCore)
    {
        this.virtualCpusPerCore = virtualCpusPerCore;
    }

    @XmlElement(name = "realHd")
    public Long getRealHardDiskInMb()
    {
        return getDefaultMb(realHardDiskInMb).longValue();
    }

    public void setRealHardDiskInMb(final Long realHardDiskInMb)
    {
        this.realHardDiskInMb = realHardDiskInMb;
    }

    @XmlElement(name = "hd")
    public Long getVirtualHardDiskInMb()
    {
        return (Long) getDefaultMb(virtualHardDiskInMb);
    }

    public void setVirtualHardDiskInMb(final Long virtualHardDiskInMb)
    {
        this.virtualHardDiskInMb = virtualHardDiskInMb;
    }

    @XmlElement(name = "hdUsed")
    public Long getVirtualHardDiskUsedInMb()
    {
        return getDefaultMb(virtualHardDiskUsedInMb).longValue();
    }

    public void setVirtualHardDiskUsedInMb(final Long virtualHardDiskUsedInMb)
    {
        this.virtualHardDiskUsedInMb = virtualHardDiskUsedInMb;
    }

    private Number getDefaultMb(final Number mb)
    {
        return mb == null ? 1 : mb;
    }

    public String getVirtualSwitch()
    {
        return virtualSwitch;
    }

    public void setVirtualSwitch(final String virtualSwitch)
    {
        this.virtualSwitch = virtualSwitch;
    }

    /**
     * @param datastores the datastores to set
     */
    public void setDatastores(final DatastoresDto datastores)
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

    public String getIpmiIp()
    {
        return ipmiIp;
    }

    public void setIpmiIp(final String ipmiIp)
    {
        this.ipmiIp = ipmiIp;
    }

    public Integer getIpmiPort()
    {
        return ipmiPort;
    }

    public void setIpmiPort(final Integer ipmiPort)
    {
        this.ipmiPort = ipmiPort;
    }

    public String getIpmiUser()
    {
        return ipmiUser;
    }

    public void setIpmiUser(final String ipmiUser)
    {
        this.ipmiUser = ipmiUser;
    }

    public String getIpmiPassword()
    {
        return ipmiPassword;
    }

    public void setIpmiPassword(final String ipmiPassword)
    {
        this.ipmiPassword = ipmiPassword;
    }
}
