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

package com.abiquo.server.core.statistics;

import javax.xml.bind.annotation.XmlRootElement;

import com.abiquo.model.transport.SingleResourceTransportDto;

@XmlRootElement(name = "enterpriseresources")
public class EnterpriseResourcesDto extends SingleResourceTransportDto
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

    private long repositoryReserved;

    public long getRepositoryReserved()
    {
        return repositoryReserved;
    }

    public void setRepositoryReserved(long repositoryReserved)
    {
        this.repositoryReserved = repositoryReserved;
    }

    private long memoryReserved;

    public long getMemoryReserved()
    {
        return memoryReserved;
    }

    public void setMemoryReserved(long memoryReserved)
    {
        this.memoryReserved = memoryReserved;
    }

    private long vlanReserved;

    public long getVlanReserved()
    {
        return vlanReserved;
    }

    public void setVlanReserved(long vlanReserved)
    {
        this.vlanReserved = vlanReserved;
    }

    private long publicIPsUsed;

    public long getPublicIPsUsed()
    {
        return publicIPsUsed;
    }

    public void setPublicIPsUsed(long publicIPsUsed)
    {
        this.publicIPsUsed = publicIPsUsed;
    }

    private long publicIPsReserved;

    public long getPublicIPsReserved()
    {
        return publicIPsReserved;
    }

    public void setPublicIPsReserved(long publicIPsReserved)
    {
        this.publicIPsReserved = publicIPsReserved;
    }

    private long localStorageReserved;

    public long getLocalStorageReserved()
    {
        return localStorageReserved;
    }

    public void setLocalStorageReserved(long localStorageReserved)
    {
        this.localStorageReserved = localStorageReserved;
    }

    private long extStorageUsed;

    public long getExtStorageUsed()
    {
        return extStorageUsed;
    }

    public void setExtStorageUsed(long extStorageUsed)
    {
        this.extStorageUsed = extStorageUsed;
    }

    private long vlanUsed;

    public long getVlanUsed()
    {
        return vlanUsed;
    }

    public void setVlanUsed(long vlanUsed)
    {
        this.vlanUsed = vlanUsed;
    }

    private long extStorageReserved;

    public long getExtStorageReserved()
    {
        return extStorageReserved;
    }

    public void setExtStorageReserved(long extStorageReserved)
    {
        this.extStorageReserved = extStorageReserved;
    }

    private long localStorageUsed;

    public long getLocalStorageUsed()
    {
        return localStorageUsed;
    }

    public void setLocalStorageUsed(long localStorageUsed)
    {
        this.localStorageUsed = localStorageUsed;
    }

    private long virtualCpuUsed;

    public long getVirtualCpuUsed()
    {
        return virtualCpuUsed;
    }

    public void setVirtualCpuUsed(long virtualCpuUsed)
    {
        this.virtualCpuUsed = virtualCpuUsed;
    }

    private long virtualCpuReserved;

    public long getVirtualCpuReserved()
    {
        return virtualCpuReserved;
    }

    public void setVirtualCpuReserved(long virtualCpuReserved)
    {
        this.virtualCpuReserved = virtualCpuReserved;
    }

    private long repositoryUsed;

    public long getRepositoryUsed()
    {
        return repositoryUsed;
    }

    public void setRepositoryUsed(long repositoryUsed)
    {
        this.repositoryUsed = repositoryUsed;
    }

    private long memoryUsed;

    public long getMemoryUsed()
    {
        return memoryUsed;
    }

    public void setMemoryUsed(long memoryUsed)
    {
        this.memoryUsed = memoryUsed;
    }

}
