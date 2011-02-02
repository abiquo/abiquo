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
@Table(name = EnterpriseResources.TABLE_NAME)
@org.hibernate.annotations.Table(appliesTo = EnterpriseResources.TABLE_NAME)
public class EnterpriseResources extends DefaultEntityBase
{
    public static final String TABLE_NAME = "enterprise_resources_stats";

    protected EnterpriseResources()
    {
    }

    private final static String ID_COLUMN = "idEnterprise";

    @Id
    @GeneratedValue
    @Column(name = ID_COLUMN, nullable = false)
    private Integer id;

    public Integer getId()
    {
        return this.id;
    }

    public final static String REPOSITORY_RESERVED_PROPERTY = "repositoryReserved";

    private final static String REPOSITORY_RESERVED_COLUMN = "repositoryReserved";

    private final static long REPOSITORY_RESERVED_MIN = Long.MIN_VALUE;

    private final static long REPOSITORY_RESERVED_MAX = Long.MAX_VALUE;

    @Column(name = REPOSITORY_RESERVED_COLUMN, nullable = true)
    @Range(min = REPOSITORY_RESERVED_MIN, max = REPOSITORY_RESERVED_MAX)
    private long repositoryReserved;

    public long getRepositoryReserved()
    {
        return this.repositoryReserved;
    }

    public void setRepositoryReserved(long repositoryReserved)
    {
        this.repositoryReserved = repositoryReserved;
    }

    public final static String MEMORY_RESERVED_PROPERTY = "memoryReserved";

    private final static String MEMORY_RESERVED_COLUMN = "memoryReserved";

    private final static long MEMORY_RESERVED_MIN = Long.MIN_VALUE;

    private final static long MEMORY_RESERVED_MAX = Long.MAX_VALUE;

    @Column(name = MEMORY_RESERVED_COLUMN, nullable = true)
    @Range(min = MEMORY_RESERVED_MIN, max = MEMORY_RESERVED_MAX)
    private long memoryReserved;

    public long getMemoryReserved()
    {
        return this.memoryReserved;
    }

    public void setMemoryReserved(long memoryReserved)
    {
        this.memoryReserved = memoryReserved;
    }

    public final static String VLAN_RESERVED_PROPERTY = "vlanReserved";

    private final static String VLAN_RESERVED_COLUMN = "vlanReserved";

    private final static long VLAN_RESERVED_MIN = Long.MIN_VALUE;

    private final static long VLAN_RESERVED_MAX = Long.MAX_VALUE;

    @Column(name = VLAN_RESERVED_COLUMN, nullable = true)
    @Range(min = VLAN_RESERVED_MIN, max = VLAN_RESERVED_MAX)
    private long vlanReserved;

    public long getVlanReserved()
    {
        return this.vlanReserved;
    }

    public void setVlanReserved(long vlanReserved)
    {
        this.vlanReserved = vlanReserved;
    }

    public final static String PUBLIC_I_PS_USED_PROPERTY = "publicIPsUsed";

    private final static String PUBLIC_I_PS_USED_COLUMN = "publicIPsUsed";

    private final static long PUBLIC_I_PS_USED_MIN = Long.MIN_VALUE;

    private final static long PUBLIC_I_PS_USED_MAX = Long.MAX_VALUE;

    @Column(name = PUBLIC_I_PS_USED_COLUMN, nullable = true)
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

    @Column(name = PUBLIC_I_PS_RESERVED_COLUMN, nullable = true)
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

    public final static String LOCAL_STORAGE_RESERVED_PROPERTY = "localStorageReserved";

    private final static String LOCAL_STORAGE_RESERVED_COLUMN = "localStorageReserved";

    private final static long LOCAL_STORAGE_RESERVED_MIN = Long.MIN_VALUE;

    private final static long LOCAL_STORAGE_RESERVED_MAX = Long.MAX_VALUE;

    @Column(name = LOCAL_STORAGE_RESERVED_COLUMN, nullable = true)
    @Range(min = LOCAL_STORAGE_RESERVED_MIN, max = LOCAL_STORAGE_RESERVED_MAX)
    private long localStorageReserved;

    public long getLocalStorageReserved()
    {
        return this.localStorageReserved;
    }

    public void setLocalStorageReserved(long localStorageReserved)
    {
        this.localStorageReserved = localStorageReserved;
    }

    public final static String EXT_STORAGE_USED_PROPERTY = "extStorageUsed";

    private final static String EXT_STORAGE_USED_COLUMN = "extStorageUsed";

    private final static long EXT_STORAGE_USED_MIN = Long.MIN_VALUE;

    private final static long EXT_STORAGE_USED_MAX = Long.MAX_VALUE;

    @Column(name = EXT_STORAGE_USED_COLUMN, nullable = true)
    @Range(min = EXT_STORAGE_USED_MIN, max = EXT_STORAGE_USED_MAX)
    private long extStorageUsed;

    public long getExtStorageUsed()
    {
        return this.extStorageUsed;
    }

    public void setExtStorageUsed(long extStorageUsed)
    {
        this.extStorageUsed = extStorageUsed;
    }

    public final static String VLAN_USED_PROPERTY = "vlanUsed";

    private final static String VLAN_USED_COLUMN = "vlanUsed";

    private final static long VLAN_USED_MIN = Long.MIN_VALUE;

    private final static long VLAN_USED_MAX = Long.MAX_VALUE;

    @Column(name = VLAN_USED_COLUMN, nullable = true)
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

    public final static String EXT_STORAGE_RESERVED_PROPERTY = "extStorageReserved";

    private final static String EXT_STORAGE_RESERVED_COLUMN = "extStorageReserved";

    private final static long EXT_STORAGE_RESERVED_MIN = Long.MIN_VALUE;

    private final static long EXT_STORAGE_RESERVED_MAX = Long.MAX_VALUE;

    @Column(name = EXT_STORAGE_RESERVED_COLUMN, nullable = true)
    @Range(min = EXT_STORAGE_RESERVED_MIN, max = EXT_STORAGE_RESERVED_MAX)
    private long extStorageReserved;

    public long getExtStorageReserved()
    {
        return this.extStorageReserved;
    }

    public void setExtStorageReserved(long extStorageReserved)
    {
        this.extStorageReserved = extStorageReserved;
    }

    public final static String LOCAL_STORAGE_USED_PROPERTY = "localStorageUsed";

    private final static String LOCAL_STORAGE_USED_COLUMN = "localStorageUsed";

    private final static long LOCAL_STORAGE_USED_MIN = Long.MIN_VALUE;

    private final static long LOCAL_STORAGE_USED_MAX = Long.MAX_VALUE;

    @Column(name = LOCAL_STORAGE_USED_COLUMN, nullable = true)
    @Range(min = LOCAL_STORAGE_USED_MIN, max = LOCAL_STORAGE_USED_MAX)
    private long localStorageUsed;

    public long getLocalStorageUsed()
    {
        return this.localStorageUsed;
    }

    public void setLocalStorageUsed(long localStorageUsed)
    {
        this.localStorageUsed = localStorageUsed;
    }

    public final static String V_CPU_USED_PROPERTY = "virtualCpuUsed";

    private final static String V_CPU_USED_COLUMN = "vCpuUsed";

    private final static long V_CPU_USED_MIN = Long.MIN_VALUE;

    private final static long V_CPU_USED_MAX = Long.MAX_VALUE;

    @Column(name = V_CPU_USED_COLUMN, nullable = true)
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

    @Column(name = V_CPU_RESERVED_COLUMN, nullable = true)
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

    public final static String REPOSITORY_USED_PROPERTY = "repositoryUsed";

    private final static String REPOSITORY_USED_COLUMN = "repositoryUsed";

    private final static long REPOSITORY_USED_MIN = Long.MIN_VALUE;

    private final static long REPOSITORY_USED_MAX = Long.MAX_VALUE;

    @Column(name = REPOSITORY_USED_COLUMN, nullable = true)
    @Range(min = REPOSITORY_USED_MIN, max = REPOSITORY_USED_MAX)
    private long repositoryUsed;

    public long getRepositoryUsed()
    {
        return this.repositoryUsed;
    }

    public void setRepositoryUsed(long repositoryUsed)
    {
        this.repositoryUsed = repositoryUsed;
    }

    public final static String MEMORY_USED_PROPERTY = "memoryUsed";

    private final static String MEMORY_USED_COLUMN = "memoryUsed";

    private final static long MEMORY_USED_MIN = Long.MIN_VALUE;

    private final static long MEMORY_USED_MAX = Long.MAX_VALUE;

    @Column(name = MEMORY_USED_COLUMN, nullable = true)
    @Range(min = MEMORY_USED_MIN, max = MEMORY_USED_MAX)
    private long memoryUsed;

    public long getMemoryUsed()
    {
        return this.memoryUsed;
    }

    public void setMemoryUsed(long memoryUsed)
    {
        this.memoryUsed = memoryUsed;
    }

}
