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

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import com.abiquo.server.core.common.DefaultEntityBase;
import com.softwarementors.validation.constraints.LeadingOrTrailingWhitespace;
import com.softwarementors.validation.constraints.Required;

@Entity
@Table(name = VirtualDatacenterResources.TABLE_NAME)
@org.hibernate.annotations.Table(appliesTo = VirtualDatacenterResources.TABLE_NAME)
public class VirtualDatacenterResources extends DefaultEntityBase
{
    public static final String TABLE_NAME = "vdc_enterprise_stats";

    protected VirtualDatacenterResources()
    {
        setPublicIPsUsed(0);
        setPublicIPsReserved(0);
        setVlanUsed(0);
        setVlanReserved(0);
    }

    private final static String ID_COLUMN = "idVirtualDataCenter";

    @Id
    @GeneratedValue
    @Column(name = ID_COLUMN, nullable = false)
    private Integer id;

    public Integer getId()
    {
        return this.id;
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

    public final static String VDC_NAME_PROPERTY = "vdcName";

    private final static boolean VDC_NAME_REQUIRED = false;

    private final static int VDC_NAME_LENGTH_MIN = 0;

    private final static int VDC_NAME_LENGTH_MAX = 255;

    private final static boolean VDC_NAME_LEADING_OR_TRAILING_WHITESPACES_ALLOWED = false;

    private final static String VDC_NAME_COLUMN = "vdcName";

    @Column(name = VDC_NAME_COLUMN, nullable = !VDC_NAME_REQUIRED, length = VDC_NAME_LENGTH_MAX)
    private String vdcName;

    @Required(value = VDC_NAME_REQUIRED)
    @Length(min = VDC_NAME_LENGTH_MIN, max = VDC_NAME_LENGTH_MAX)
    @LeadingOrTrailingWhitespace(allowed = VDC_NAME_LEADING_OR_TRAILING_WHITESPACES_ALLOWED)
    public String getVdcName()
    {
        return this.vdcName;
    }

    public void setVdcName(String vdcName)
    {
        this.vdcName = vdcName;
    }

    public final static String VOL_CREATED_PROPERTY = "volCreated";

    private final static String VOL_CREATED_COLUMN = "volCreated";

    private final static int VOL_CREATED_MIN = Integer.MIN_VALUE;

    private final static int VOL_CREATED_MAX = Integer.MAX_VALUE;

    @Column(name = VOL_CREATED_COLUMN, nullable = true)
    @Range(min = VOL_CREATED_MIN, max = VOL_CREATED_MAX)
    private int volCreated;

    public int getVolCreated()
    {
        return this.volCreated;
    }

    public void setVolCreated(int volCreated)
    {
        this.volCreated = volCreated;
    }

    public final static String VLAN_RESERVED_PROPERTY = "vlanReserved";

    private final static String VLAN_RESERVED_COLUMN = "vlanReserved";

    private final static long VLAN_RESERVED_MIN = Integer.MIN_VALUE;

    private final static long VLAN_RESERVED_MAX = Integer.MAX_VALUE;

    @Column(name = VLAN_RESERVED_COLUMN, nullable = true)
    @Range(min = VLAN_RESERVED_MIN, max = VLAN_RESERVED_MAX)
    private Integer vlanReserved;

    public Integer getVlanReserved()
    {
        return this.vlanReserved;
    }

    public void setVlanReserved(Integer vlanReserved)
    {
        this.vlanReserved = vlanReserved;
    }

    public final static String PUBLIC_I_PS_USED_PROPERTY = "publicIPsUsed";

    private final static String PUBLIC_I_PS_USED_COLUMN = "publicIPsUsed";

    private final static long PUBLIC_I_PS_USED_MIN = Integer.MIN_VALUE;

    private final static long PUBLIC_I_PS_USED_MAX = Integer.MAX_VALUE;

    @Column(name = PUBLIC_I_PS_USED_COLUMN, nullable = true)
    @Range(min = PUBLIC_I_PS_USED_MIN, max = PUBLIC_I_PS_USED_MAX)
    private Integer publicIPsUsed;

    public Integer getPublicIPsUsed()
    {
        return this.publicIPsUsed;
    }

    public void setPublicIPsUsed(Integer publicIPsUsed)
    {
        this.publicIPsUsed = publicIPsUsed;
    }

    public final static String PUBLIC_I_PS_RESERVED_PROPERTY = "publicIPsReserved";

    private final static String PUBLIC_I_PS_RESERVED_COLUMN = "publicIPsReserved";

    private final static long PUBLIC_I_PS_RESERVED_MIN = Integer.MIN_VALUE;

    private final static long PUBLIC_I_PS_RESERVED_MAX = Integer.MAX_VALUE;

    @Column(name = PUBLIC_I_PS_RESERVED_COLUMN, nullable = true)
    @Range(min = PUBLIC_I_PS_RESERVED_MIN, max = PUBLIC_I_PS_RESERVED_MAX)
    private Integer publicIPsReserved;

    public Integer getPublicIPsReserved()
    {
        return this.publicIPsReserved;
    }

    public void setPublicIPsReserved(Integer publicIPsReserved)
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

    public final static String VOL_ATTACHED_PROPERTY = "volAttached";

    private final static String VOL_ATTACHED_COLUMN = "volAttached";

    private final static int VOL_ATTACHED_MIN = Integer.MIN_VALUE;

    private final static int VOL_ATTACHED_MAX = Integer.MAX_VALUE;

    @Column(name = VOL_ATTACHED_COLUMN, nullable = true)
    @Range(min = VOL_ATTACHED_MIN, max = VOL_ATTACHED_MAX)
    private int volAttached;

    public int getVolAttached()
    {
        return this.volAttached;
    }

    public void setVolAttached(int volAttached)
    {
        this.volAttached = volAttached;
    }

    public final static String VLAN_USED_PROPERTY = "vlanUsed";

    private final static String VLAN_USED_COLUMN = "vlanUsed";

    private final static long VLAN_USED_MIN = Integer.MIN_VALUE;

    private final static long VLAN_USED_MAX = Integer.MAX_VALUE;

    @Column(name = VLAN_USED_COLUMN, nullable = true)
    @Range(min = VLAN_USED_MIN, max = VLAN_USED_MAX)
    private Integer vlanUsed;

    public Integer getVlanUsed()
    {
        return this.vlanUsed;
    }

    public void setVlanUsed(Integer vlanUsed)
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

    public final static String VM_CREATED_PROPERTY = "vmCreated";

    private final static String VM_CREATED_COLUMN = "vmCreated";

    private final static int VM_CREATED_MIN = Integer.MIN_VALUE;

    private final static int VM_CREATED_MAX = Integer.MAX_VALUE;

    @Column(name = VM_CREATED_COLUMN, nullable = true)
    @Range(min = VM_CREATED_MIN, max = VM_CREATED_MAX)
    private int vmCreated;

    public int getVmCreated()
    {
        return this.vmCreated;
    }

    public void setVmCreated(int vmCreated)
    {
        this.vmCreated = vmCreated;
    }

    public final static String VOL_ASSOCIATED_PROPERTY = "volAssociated";

    private final static String VOL_ASSOCIATED_COLUMN = "volAssociated";

    private final static int VOL_ASSOCIATED_MIN = Integer.MIN_VALUE;

    private final static int VOL_ASSOCIATED_MAX = Integer.MAX_VALUE;

    @Column(name = VOL_ASSOCIATED_COLUMN, nullable = true)
    @Range(min = VOL_ASSOCIATED_MIN, max = VOL_ASSOCIATED_MAX)
    private int volAssociated;

    public int getVolAssociated()
    {
        return this.volAssociated;
    }

    public void setVolAssociated(int volAssociated)
    {
        this.volAssociated = volAssociated;
    }

    public final static String VM_ACTIVE_PROPERTY = "vmActive";

    private final static String VM_ACTIVE_COLUMN = "vmActive";

    private final static int VM_ACTIVE_MIN = Integer.MIN_VALUE;

    private final static int VM_ACTIVE_MAX = Integer.MAX_VALUE;

    @Column(name = VM_ACTIVE_COLUMN, nullable = true)
    @Range(min = VM_ACTIVE_MIN, max = VM_ACTIVE_MAX)
    private int vmActive;

    public int getVmActive()
    {
        return this.vmActive;
    }

    public void setVmActive(int vmActive)
    {
        this.vmActive = vmActive;
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

    public final static String ID_ENTERPRISE_PROPERTY = "idEnterprise";

    private final static String ID_ENTERPRISE_COLUMN = "idEnterprise";

    private final static int ID_ENTERPRISE_MIN = Integer.MIN_VALUE;

    private final static int ID_ENTERPRISE_MAX = Integer.MAX_VALUE;

    @Column(name = ID_ENTERPRISE_COLUMN, nullable = true)
    @Range(min = ID_ENTERPRISE_MIN, max = ID_ENTERPRISE_MAX)
    private int idEnterprise;

    public int getIdEnterprise()
    {
        return this.idEnterprise;
    }

    public void setIdEnterprise(int idEnterprise)
    {
        this.idEnterprise = idEnterprise;
    }

}
