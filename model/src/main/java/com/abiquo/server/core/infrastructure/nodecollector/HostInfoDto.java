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
package com.abiquo.server.core.infrastructure.nodecollector;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Encapsulates information returned by the Node Collector module for the <code>getHostInfo</code>
 * requests.
 * 
 * @author ibarrera
 */
@XmlRootElement(name = "HostInfo")
public class HostInfoDto extends ComputerSystemDto implements Serializable
{
    /** Serial UID. */
    private static final long serialVersionUID = 1L;

    /** The state of the machine. */
    private String status;

    /** The amount of memory of the hard disk. */
    private Long hd;

    /** The kind of Hypervisor used. */
    private String hypervisor;

    /** The version of the hypervisor running on host. */
    private String version;

    private String statusInfo;

    public String getStatusInfo()
    {
        return statusInfo;
    }

    public void setStatusInfo(String statusInfo)
    {
        this.statusInfo = statusInfo;
    }

    /**
     * Gets the state.
     * 
     * @return the state
     */
    public String getStatus()
    {
        return status;
    }

    /**
     * Sets the state.
     * 
     * @param state the state to set
     */
    public void setStatus(String status)
    {
        this.status = status;
    }

    /**
     * Gets the hd.
     * 
     * @return the hd
     */
    public Long getHd()
    {
        return hd;
    }

    /**
     * Sets the hd.
     * 
     * @param hd the hd to set
     */
    public void setHd(Long hd)
    {
        this.hd = hd;
    }

    /**
     * Gets the hypervisor.
     * 
     * @return the hypervisor
     */
    public String getHypervisor()
    {
        return hypervisor;
    }

    /**
     * Sets the hypervisor.
     * 
     * @param hypervisor the hypervisor to set
     */
    public void setHypervisor(String hypervisor)
    {
        this.hypervisor = hypervisor;
    }

    /**
     * Gets the version.
     * 
     * @return the version
     */
    public String getVersion()
    {
        return version;
    }

    /**
     * Sets the version.
     * 
     * @param version the version to set
     */
    public void setVersion(String version)
    {
        this.version = version;
    }

}
