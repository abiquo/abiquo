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

@XmlRootElement(name = "cloudusage")
public class CloudUsageDto extends SingleResourceTransportDto
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

    private long virtualStorageTotal;

    public long getVirtualStorageTotal()
    {
        return virtualStorageTotal;
    }

    public void setVirtualStorageTotal(long virtualStorageTotal)
    {
        this.virtualStorageTotal = virtualStorageTotal;
    }

    private long virtualMemoryReserved;

    public long getVirtualMemoryReserved()
    {
        return virtualMemoryReserved;
    }

    public void setVirtualMemoryReserved(long virtualMemoryReserved)
    {
        this.virtualMemoryReserved = virtualMemoryReserved;
    }

    private long storageTotal;

    public long getStorageTotal()
    {
        return storageTotal;
    }

    public void setStorageTotal(long storageTotal)
    {
        this.storageTotal = storageTotal;
    }

    private long virtualMemoryUsed;

    public long getVirtualMemoryUsed()
    {
        return virtualMemoryUsed;
    }

    public void setVirtualMemoryUsed(long virtualMemoryUsed)
    {
        this.virtualMemoryUsed = virtualMemoryUsed;
    }

    private long virtualCpuTotal;

    public long getVirtualCpuTotal()
    {
        return virtualCpuTotal;
    }

    public void setVirtualCpuTotal(long virtualCpuTotal)
    {
        this.virtualCpuTotal = virtualCpuTotal;
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

    private long storageUsed;

    public long getStorageUsed()
    {
        return storageUsed;
    }

    public void setStorageUsed(long storageUsed)
    {
        this.storageUsed = storageUsed;
    }

    private long storageReserved;

    public long getStorageReserved()
    {
        return storageReserved;
    }

    public void setStorageReserved(long storageReserved)
    {
        this.storageReserved = storageReserved;
    }

    private long serversTotal;

    public long getServersTotal()
    {
        return serversTotal;
    }

    public void setServersTotal(long serversTotal)
    {
        this.serversTotal = serversTotal;
    }

    private long publicIPsTotal;

    public long getPublicIPsTotal()
    {
        return publicIPsTotal;
    }

    public void setPublicIPsTotal(long publicIPsTotal)
    {
        this.publicIPsTotal = publicIPsTotal;
    }

    private long virtualStorageUsed;

    public long getVirtualStorageUsed()
    {
        return virtualStorageUsed;
    }

    public void setVirtualStorageUsed(long virtualStorageUsed)
    {
        this.virtualStorageUsed = virtualStorageUsed;
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

    private long numVdcCreated;

    public long getNumVdcCreated()
    {
        return numVdcCreated;
    }

    public void setNumVdcCreated(long numVdcCreated)
    {
        this.numVdcCreated = numVdcCreated;
    }

    private long numEnterprisesCreated;

    public long getNumEnterprisesCreated()
    {
        return numEnterprisesCreated;
    }

    public void setNumEnterprisesCreated(long numEnterprisesCreated)
    {
        this.numEnterprisesCreated = numEnterprisesCreated;
    }

    private long virtualStorageReserved;

    public long getVirtualStorageReserved()
    {
        return virtualStorageReserved;
    }

    public void setVirtualStorageReserved(long virtualStorageReserved)
    {
        this.virtualStorageReserved = virtualStorageReserved;
    }

    private long virtualMemoryTotal;

    public long getVirtualMemoryTotal()
    {
        return virtualMemoryTotal;
    }

    public void setVirtualMemoryTotal(long virtualMemoryTotal)
    {
        this.virtualMemoryTotal = virtualMemoryTotal;
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

    private long virtualMachinesTotal;

    public long getVirtualMachinesTotal()
    {
        return virtualMachinesTotal;
    }

    public void setVirtualMachinesTotal(long virtualMachinesTotal)
    {
        this.virtualMachinesTotal = virtualMachinesTotal;
    }

    private long serversRunning;

    public long getServersRunning()
    {
        return serversRunning;
    }

    public void setServersRunning(long serversRunning)
    {
        this.serversRunning = serversRunning;
    }

    private long numUsersCreated;

    public long getNumUsersCreated()
    {
        return numUsersCreated;
    }

    public void setNumUsersCreated(long numUsersCreated)
    {
        this.numUsersCreated = numUsersCreated;
    }

    private long virtualMachinesRunning;

    public long getVirtualMachinesRunning()
    {
        return virtualMachinesRunning;
    }

    public void setVirtualMachinesRunning(long virtualMachinesRunning)
    {
        this.virtualMachinesRunning = virtualMachinesRunning;
    }

}
