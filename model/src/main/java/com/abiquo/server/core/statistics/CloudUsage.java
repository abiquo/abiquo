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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.validator.constraints.Range;

import com.abiquo.server.core.common.DefaultEntityBase;

@Entity
@Table(name = CloudUsage.TABLE_NAME)
@org.hibernate.annotations.Table(appliesTo = CloudUsage.TABLE_NAME)
public class CloudUsage extends DefaultEntityBase
{
    public static final String TABLE_NAME = "cloud_usage_stats";

    protected CloudUsage()
    {
    }

    private final static String ID_COLUMN = "idDataCenter";

    @Id
    @GeneratedValue
    @Column(name = ID_COLUMN, nullable = false)
    private Integer id;

    public Integer getId()
    {
        return this.id;
    }

    // public void setIdDatacenter(int id)
    // {
    // this.id = id;
    // }

    public final static String SERVERS_TOTAL_PROPERTY = "serversTotal";

    private final static String SERVERS_TOTAL_COLUMN = "serversTotal";

    private final static long SERVERS_TOTAL_MIN = Long.MIN_VALUE;

    private final static long SERVERS_TOTAL_MAX = Long.MAX_VALUE;

    @Column(name = SERVERS_TOTAL_COLUMN, nullable = false)
    @Range(min = SERVERS_TOTAL_MIN, max = SERVERS_TOTAL_MAX)
    private long serversTotal;

    public long getServersTotal()
    {
        return this.serversTotal;
    }

    public void setServersTotal(long serversTotal)
    {
        this.serversTotal = serversTotal;
    }

    public final static String V_STORAGE_TOTAL_PROPERTY = "virtualStorageTotal";

    private final static String V_STORAGE_TOTAL_COLUMN = "vStorageTotal";

    private final static long V_STORAGE_TOTAL_MIN = Long.MIN_VALUE;

    private final static long V_STORAGE_TOTAL_MAX = Long.MAX_VALUE;

    @Column(name = V_STORAGE_TOTAL_COLUMN, nullable = false)
    @Range(min = V_STORAGE_TOTAL_MIN, max = V_STORAGE_TOTAL_MAX)
    private long virtualStorageTotal;

    public long getVirtualStorageTotal()
    {
        return this.virtualStorageTotal;
    }

    public void setVirtualStorageTotal(long virtualStorageTotal)
    {
        this.virtualStorageTotal = virtualStorageTotal;
    }

    public final static String V_MEMORY_RESERVED_PROPERTY = "virtualMemoryReserved";

    private final static String V_MEMORY_RESERVED_COLUMN = "vMemoryReserved";

    private final static long V_MEMORY_RESERVED_MIN = Long.MIN_VALUE;

    private final static long V_MEMORY_RESERVED_MAX = Long.MAX_VALUE;

    @Column(name = V_MEMORY_RESERVED_COLUMN, nullable = false)
    @Range(min = V_MEMORY_RESERVED_MIN, max = V_MEMORY_RESERVED_MAX)
    private long virtualMemoryReserved;

    public long getVirtualMemoryReserved()
    {
        return this.virtualMemoryReserved;
    }

    public void setVirtualMemoryReserved(long virtualMemoryReserved)
    {
        this.virtualMemoryReserved = virtualMemoryReserved;
    }

    public final static String STORAGE_TOTAL_PROPERTY = "storageTotal";

    private final static String STORAGE_TOTAL_COLUMN = "storageTotal";

    private final static long STORAGE_TOTAL_MIN = Long.MIN_VALUE;

    private final static long STORAGE_TOTAL_MAX = Long.MAX_VALUE;

    @Column(name = STORAGE_TOTAL_COLUMN, nullable = false)
    @Range(min = STORAGE_TOTAL_MIN, max = STORAGE_TOTAL_MAX)
    private long storageTotal;

    public long getStorageTotal()
    {
        return this.storageTotal;
    }

    public void setStorageTotal(long storageTotal)
    {
        this.storageTotal = storageTotal;
    }

    public final static String V_MEMORY_USED_PROPERTY = "virtualMemoryUsed";

    private final static String V_MEMORY_USED_COLUMN = "vMemoryUsed";

    private final static long V_MEMORY_USED_MIN = Long.MIN_VALUE;

    private final static long V_MEMORY_USED_MAX = Long.MAX_VALUE;

    @Column(name = V_MEMORY_USED_COLUMN, nullable = false)
    @Range(min = V_MEMORY_USED_MIN, max = V_MEMORY_USED_MAX)
    private long virtualMemoryUsed;

    public long getVirtualMemoryUsed()
    {
        return this.virtualMemoryUsed;
    }

    public void setVirtualMemoryUsed(long virtualMemoryUsed)
    {
        this.virtualMemoryUsed = virtualMemoryUsed;
    }

    public final static String V_CPU_TOTAL_PROPERTY = "virtualCpuTotal";

    private final static String V_CPU_TOTAL_COLUMN = "vCpuTotal";

    private final static long V_CPU_TOTAL_MIN = Long.MIN_VALUE;

    private final static long V_CPU_TOTAL_MAX = Long.MAX_VALUE;

    @Column(name = V_CPU_TOTAL_COLUMN, nullable = false)
    @Range(min = V_CPU_TOTAL_MIN, max = V_CPU_TOTAL_MAX)
    private long virtualCpuTotal;

    public long getVirtualCpuTotal()
    {
        return this.virtualCpuTotal;
    }

    public void setVirtualCpuTotal(long virtualCpuTotal)
    {
        this.virtualCpuTotal = virtualCpuTotal;
    }

    public final static String PUBLIC_I_PS_USED_PROPERTY = "publicIPsUsed";

    private final static String PUBLIC_I_PS_USED_COLUMN = "publicIPsUsed";

    private final static long PUBLIC_I_PS_USED_MIN = Long.MIN_VALUE;

    private final static long PUBLIC_I_PS_USED_MAX = Long.MAX_VALUE;

    @Column(name = PUBLIC_I_PS_USED_COLUMN, nullable = false)
    @Range(min = PUBLIC_I_PS_USED_MIN, max = PUBLIC_I_PS_USED_MAX)
    private long publicIPsUsed;

    public long getPublicIPsUsed()
    {
        return this.publicIPsUsed;
    }

    public void setPublicIPsUsed(long publicIPsUsed)
    {
        this.publicIPsUsed = publicIPsUsed;
    }

    public final static String PUBLIC_I_PS_RESERVED_PROPERTY = "publicIPsReserved";

    private final static String PUBLIC_I_PS_RESERVED_COLUMN = "publicIPsReserved";

    private final static long PUBLIC_I_PS_RESERVED_MIN = Long.MIN_VALUE;

    private final static long PUBLIC_I_PS_RESERVED_MAX = Long.MAX_VALUE;

    @Column(name = PUBLIC_I_PS_RESERVED_COLUMN, nullable = false)
    @Range(min = PUBLIC_I_PS_RESERVED_MIN, max = PUBLIC_I_PS_RESERVED_MAX)
    private long publicIPsReserved;

    public long getPublicIPsReserved()
    {
        return this.publicIPsReserved;
    }

    public void setPublicIPsReserved(long publicIPsReserved)
    {
        this.publicIPsReserved = publicIPsReserved;
    }

    public final static String STORAGE_USED_PROPERTY = "storageUsed";

    private final static String STORAGE_USED_COLUMN = "storageUsed";

    private final static long STORAGE_USED_MIN = Long.MIN_VALUE;

    private final static long STORAGE_USED_MAX = Long.MAX_VALUE;

    @Column(name = STORAGE_USED_COLUMN, nullable = false)
    @Range(min = STORAGE_USED_MIN, max = STORAGE_USED_MAX)
    private long storageUsed;

    public long getStorageUsed()
    {
        return this.storageUsed;
    }

    public void setStorageUsed(long storageUsed)
    {
        this.storageUsed = storageUsed;
    }

    public final static String STORAGE_RESERVED_PROPERTY = "storageReserved";

    private final static String STORAGE_RESERVED_COLUMN = "storageReserved";

    private final static long STORAGE_RESERVED_MIN = Long.MIN_VALUE;

    private final static long STORAGE_RESERVED_MAX = Long.MAX_VALUE;

    @Column(name = STORAGE_RESERVED_COLUMN, nullable = false)
    @Range(min = STORAGE_RESERVED_MIN, max = STORAGE_RESERVED_MAX)
    private long storageReserved;

    public long getStorageReserved()
    {
        return this.storageReserved;
    }

    public void setStorageReserved(long storageReserved)
    {
        this.storageReserved = storageReserved;
    }

    public final static String PUBLIC_I_PS_TOTAL_PROPERTY = "publicIPsTotal";

    private final static String PUBLIC_I_PS_TOTAL_COLUMN = "publicIPsTotal";

    private final static long PUBLIC_I_PS_TOTAL_MIN = Long.MIN_VALUE;

    private final static long PUBLIC_I_PS_TOTAL_MAX = Long.MAX_VALUE;

    @Column(name = PUBLIC_I_PS_TOTAL_COLUMN, nullable = false)
    @Range(min = PUBLIC_I_PS_TOTAL_MIN, max = PUBLIC_I_PS_TOTAL_MAX)
    private long publicIPsTotal;

    public long getPublicIPsTotal()
    {
        return this.publicIPsTotal;
    }

    public void setPublicIPsTotal(long publicIPsTotal)
    {
        this.publicIPsTotal = publicIPsTotal;
    }

    public final static String V_STORAGE_USED_PROPERTY = "virtualStorageUsed";

    private final static String V_STORAGE_USED_COLUMN = "vStorageUsed";

    private final static long V_STORAGE_USED_MIN = Long.MIN_VALUE;

    private final static long V_STORAGE_USED_MAX = Long.MAX_VALUE;

    @Column(name = V_STORAGE_USED_COLUMN, nullable = false)
    @Range(min = V_STORAGE_USED_MIN, max = V_STORAGE_USED_MAX)
    private long virtualStorageUsed;

    public long getVirtualStorageUsed()
    {
        return this.virtualStorageUsed;
    }

    public void setVirtualStorageUsed(long virtualStorageUsed)
    {
        this.virtualStorageUsed = virtualStorageUsed;
    }

    public final static String VLAN_USED_PROPERTY = "vlanUsed";

    private final static String VLAN_USED_COLUMN = "vlanUsed";

    private final static long VLAN_USED_MIN = Long.MIN_VALUE;

    private final static long VLAN_USED_MAX = Long.MAX_VALUE;

    @Column(name = VLAN_USED_COLUMN, nullable = false)
    @Range(min = VLAN_USED_MIN, max = VLAN_USED_MAX)
    private long vlanUsed;

    public long getVlanUsed()
    {
        return this.vlanUsed;
    }

    public void setVlanUsed(long vlanUsed)
    {
        this.vlanUsed = vlanUsed;
    }

    public final static String NUM_VDC_CREATED_PROPERTY = "numVdcCreated";

    private final static String NUM_VDC_CREATED_COLUMN = "numVdcCreated";

    private final static long NUM_VDC_CREATED_MIN = Long.MIN_VALUE;

    private final static long NUM_VDC_CREATED_MAX = Long.MAX_VALUE;

    @Column(name = NUM_VDC_CREATED_COLUMN, nullable = false)
    @Range(min = NUM_VDC_CREATED_MIN, max = NUM_VDC_CREATED_MAX)
    private long numVdcCreated;

    public long getNumVdcCreated()
    {
        return this.numVdcCreated;
    }

    public void setNumVdcCreated(long numVdcCreated)
    {
        this.numVdcCreated = numVdcCreated;
    }

    public final static String NUM_ENTERPRISES_CREATED_PROPERTY = "numEnterprisesCreated";

    private final static String NUM_ENTERPRISES_CREATED_COLUMN = "numEnterprisesCreated";

    private final static long NUM_ENTERPRISES_CREATED_MIN = Long.MIN_VALUE;

    private final static long NUM_ENTERPRISES_CREATED_MAX = Long.MAX_VALUE;

    @Column(name = NUM_ENTERPRISES_CREATED_COLUMN, nullable = false)
    @Range(min = NUM_ENTERPRISES_CREATED_MIN, max = NUM_ENTERPRISES_CREATED_MAX)
    private long numEnterprisesCreated;

    public long getNumEnterprisesCreated()
    {
        return this.numEnterprisesCreated;
    }

    public void setNumEnterprisesCreated(long numEnterprisesCreated)
    {
        this.numEnterprisesCreated = numEnterprisesCreated;
    }

    public final static String V_STORAGE_RESERVED_PROPERTY = "virtualStorageReserved";

    private final static String V_STORAGE_RESERVED_COLUMN = "vStorageReserved";

    private final static long V_STORAGE_RESERVED_MIN = Long.MIN_VALUE;

    private final static long V_STORAGE_RESERVED_MAX = Long.MAX_VALUE;

    @Column(name = V_STORAGE_RESERVED_COLUMN, nullable = false)
    @Range(min = V_STORAGE_RESERVED_MIN, max = V_STORAGE_RESERVED_MAX)
    private long virtualStorageReserved;

    public long getVirtualStorageReserved()
    {
        return this.virtualStorageReserved;
    }

    public void setVirtualStorageReserved(long virtualStorageReserved)
    {
        this.virtualStorageReserved = virtualStorageReserved;
    }

    public final static String V_MEMORY_TOTAL_PROPERTY = "virtualMemoryTotal";

    private final static String V_MEMORY_TOTAL_COLUMN = "vMemoryTotal";

    private final static long V_MEMORY_TOTAL_MIN = Long.MIN_VALUE;

    private final static long V_MEMORY_TOTAL_MAX = Long.MAX_VALUE;

    @Column(name = V_MEMORY_TOTAL_COLUMN, nullable = false)
    @Range(min = V_MEMORY_TOTAL_MIN, max = V_MEMORY_TOTAL_MAX)
    private long virtualMemoryTotal;

    public long getVirtualMemoryTotal()
    {
        return this.virtualMemoryTotal;
    }

    public void setVirtualMemoryTotal(long virtualMemoryTotal)
    {
        this.virtualMemoryTotal = virtualMemoryTotal;
    }

    public final static String V_CPU_USED_PROPERTY = "virtualCpuUsed";

    private final static String V_CPU_USED_COLUMN = "vCpuUsed";

    private final static long V_CPU_USED_MIN = Long.MIN_VALUE;

    private final static long V_CPU_USED_MAX = Long.MAX_VALUE;

    @Column(name = V_CPU_USED_COLUMN, nullable = false)
    @Range(min = V_CPU_USED_MIN, max = V_CPU_USED_MAX)
    private long virtualCpuUsed;

    public long getVirtualCpuUsed()
    {
        return this.virtualCpuUsed;
    }

    public void setVirtualCpuUsed(long virtualCpuUsed)
    {
        this.virtualCpuUsed = virtualCpuUsed;
    }

    public final static String V_CPU_RESERVED_PROPERTY = "virtualCpuReserved";

    private final static String V_CPU_RESERVED_COLUMN = "vCpuReserved";

    private final static long V_CPU_RESERVED_MIN = Long.MIN_VALUE;

    private final static long V_CPU_RESERVED_MAX = Long.MAX_VALUE;

    @Column(name = V_CPU_RESERVED_COLUMN, nullable = false)
    @Range(min = V_CPU_RESERVED_MIN, max = V_CPU_RESERVED_MAX)
    private long virtualCpuReserved;

    public long getVirtualCpuReserved()
    {
        return this.virtualCpuReserved;
    }

    public void setVirtualCpuReserved(long virtualCpuReserved)
    {
        this.virtualCpuReserved = virtualCpuReserved;
    }

    public final static String V_MACHINES_TOTAL_PROPERTY = "virtualMachinesTotal";

    private final static String V_MACHINES_TOTAL_COLUMN = "vMachinesTotal";

    private final static long V_MACHINES_TOTAL_MIN = Long.MIN_VALUE;

    private final static long V_MACHINES_TOTAL_MAX = Long.MAX_VALUE;

    @Column(name = V_MACHINES_TOTAL_COLUMN, nullable = false)
    @Range(min = V_MACHINES_TOTAL_MIN, max = V_MACHINES_TOTAL_MAX)
    private long virtualMachinesTotal;

    public long getVirtualMachinesTotal()
    {
        return this.virtualMachinesTotal;
    }

    public void setVirtualMachinesTotal(long virtualMachinesTotal)
    {
        this.virtualMachinesTotal = virtualMachinesTotal;
    }

    public final static String SERVERS_RUNNING_PROPERTY = "serversRunning";

    private final static String SERVERS_RUNNING_COLUMN = "serversRunning";

    private final static long SERVERS_RUNNING_MIN = Long.MIN_VALUE;

    private final static long SERVERS_RUNNING_MAX = Long.MAX_VALUE;

    @Column(name = SERVERS_RUNNING_COLUMN, nullable = false)
    @Range(min = SERVERS_RUNNING_MIN, max = SERVERS_RUNNING_MAX)
    private long serversRunning;

    public long getServersRunning()
    {
        return this.serversRunning;
    }

    public void setServersRunning(long serversRunning)
    {
        this.serversRunning = serversRunning;
    }

    public final static String NUM_USERS_CREATED_PROPERTY = "numUsersCreated";

    private final static String NUM_USERS_CREATED_COLUMN = "numUsersCreated";

    private final static long NUM_USERS_CREATED_MIN = Long.MIN_VALUE;

    private final static long NUM_USERS_CREATED_MAX = Long.MAX_VALUE;

    @Column(name = NUM_USERS_CREATED_COLUMN, nullable = false)
    @Range(min = NUM_USERS_CREATED_MIN, max = NUM_USERS_CREATED_MAX)
    private long numUsersCreated;

    public long getNumUsersCreated()
    {
        return this.numUsersCreated;
    }

    public void setNumUsersCreated(long numUsersCreated)
    {
        this.numUsersCreated = numUsersCreated;
    }

    public final static String V_MACHINES_RUNNING_PROPERTY = "virtualMachinesRunning";

    private final static String V_MACHINES_RUNNING_COLUMN = "vMachinesRunning";

    private final static long V_MACHINES_RUNNING_MIN = Long.MIN_VALUE;

    private final static long V_MACHINES_RUNNING_MAX = Long.MAX_VALUE;

    @Column(name = V_MACHINES_RUNNING_COLUMN, nullable = false)
    @Range(min = V_MACHINES_RUNNING_MIN, max = V_MACHINES_RUNNING_MAX)
    private long virtualMachinesRunning;

    public long getVirtualMachinesRunning()
    {
        return this.virtualMachinesRunning;
    }

    public void setVirtualMachinesRunning(long virtualMachinesRunning)
    {
        this.virtualMachinesRunning = virtualMachinesRunning;
    }

}
