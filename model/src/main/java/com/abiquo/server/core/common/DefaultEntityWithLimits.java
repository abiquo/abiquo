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

package com.abiquo.server.core.common;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import org.hibernate.validator.constraints.Range;

import com.abiquo.model.validation.LimitRange;
import com.softwarementors.validation.constraints.Required;

@MappedSuperclass
public abstract class DefaultEntityWithLimits extends DefaultEntityBase
{
    public static final long NO_LIMIT = 0;

    public final static String RAM_SOFT_LIMIT_IN_MB_PROPERTY = "ramSoftLimitInMb";

    /* package */final static String RAM_SOFT_LIMIT_IN_MB_COLUMN = "ramSoft";

    /* package */final static long RAM_SOFT_LIMIT_IN_MB_MIN = NO_LIMIT;

    /* package */final static long RAM_SOFT_LIMIT_IN_MB_MAX = Long.MAX_VALUE;

    /* package */final static boolean RAM_SOFT_LIMIT_IN_MB_REQUIRED = true;

    @Column(name = RAM_SOFT_LIMIT_IN_MB_COLUMN, nullable = false)
    @Range(min = RAM_SOFT_LIMIT_IN_MB_MIN, max = RAM_SOFT_LIMIT_IN_MB_MAX)
    private Long ramSoftLimitInMb;

    @Required(value = RAM_SOFT_LIMIT_IN_MB_REQUIRED)
    public Long getRamSoftLimitInMb()
    {
        return this.ramSoftLimitInMb;
    }

    private void setRamSoftLimitInMb(Long ramSoftLimitInMb)
    {
        this.ramSoftLimitInMb = ramSoftLimitInMb;
    }

    public final static String CPU_COUNT_SOFT_LIMIT_PROPERTY = "cpuCountSoftLimit";

    /* package */final static String CPU_COUNT_SOFT_LIMIT_COLUMN = "cpuSoft";

    /* package */final static long CPU_COUNT_SOFT_LIMIT_MIN = NO_LIMIT;

    /* package */final static long CPU_COUNT_SOFT_LIMIT_MAX = Long.MAX_VALUE;

    /* package */final static boolean CPU_COUNT_SOFT_LIMIT_REQUIRED = true;

    @Column(name = CPU_COUNT_SOFT_LIMIT_COLUMN, nullable = false)
    @Range(min = CPU_COUNT_SOFT_LIMIT_MIN, max = CPU_COUNT_SOFT_LIMIT_MAX)
    private Long cpuCountSoftLimit;

    @Required(value = CPU_COUNT_SOFT_LIMIT_REQUIRED)
    public Long getCpuCountSoftLimit()
    {
        return cpuCountSoftLimit;
    }

    private void setCpuCountSoftLimit(Long cpuCountSoftLimit)
    {
        this.cpuCountSoftLimit = cpuCountSoftLimit;
    }

    public final static String HD_SOFT_LIMIT_IN_MB_PROPERTY = "hdSoftLimitInMb";

    /* package */final static String HD_SOFT_LIMIT_IN_MB_COLUMN = "hdSoft";

    /* package */final static long HD_SOFT_LIMIT_IN_MB_MIN = NO_LIMIT;

    /* package */final static long HD_SOFT_LIMIT_IN_MB_MAX = Long.MAX_VALUE;

    /* package */final static boolean HD_SOFT_LIMIT_IN_MB_REQUIRED = true;

    @Column(name = HD_SOFT_LIMIT_IN_MB_COLUMN, nullable = false)
    @Range(min = HD_SOFT_LIMIT_IN_MB_MIN, max = HD_SOFT_LIMIT_IN_MB_MAX)
    private long hdSoftLimitInMb;

    @Required(value = HD_SOFT_LIMIT_IN_MB_REQUIRED)
    public long getHdSoftLimitInMb()
    {
        return this.hdSoftLimitInMb;
    }

    private void setHdSoftLimitInMb(long hdSoftLimitInMb)
    {
        this.hdSoftLimitInMb = hdSoftLimitInMb;
    }

    public final static String RAM_HARD_LIMIT_IN_MB_PROPERTY = "ramHardLimitInMb";

    /* package */final static String RAM_HARD_LIMIT_IN_MB_COLUMN = "ramHard";

    /* package */final static long RAM_HARD_LIMIT_IN_MB_MIN = NO_LIMIT;

    /* package */final static long RAM_HARD_LIMIT_IN_MB_MAX = Integer.MAX_VALUE;

    /* package */final static boolean RAM_HARD_LIMIT_IN_MB_REQUIRED = true;

    @Column(name = RAM_HARD_LIMIT_IN_MB_COLUMN, nullable = false)
    @Range(min = RAM_HARD_LIMIT_IN_MB_MIN, max = RAM_HARD_LIMIT_IN_MB_MAX)
    private Long ramHardLimitInMb;

    @Required(value = RAM_HARD_LIMIT_IN_MB_REQUIRED)
    public Long getRamHardLimitInMb()
    {
        return this.ramHardLimitInMb;
    }

    private void setRamHardLimitInMb(Long ramHardLimitInMb)
    {
        this.ramHardLimitInMb = ramHardLimitInMb;
    }

    public final static String CPU_COUNT_HARD_LIMIT_PROPERTY = "cpuCountHardLimit";

    /* package */final static String CPU_COUNT_HARD_LIMIT_COLUMN = "cpuHard";

    /* package */final static long CPU_COUNT_HARD_LIMIT_MIN = NO_LIMIT;

    /* package */final static long CPU_COUNT_HARD_LIMIT_MAX = Long.MAX_VALUE;

    /* package */final static boolean CPU_COUNT_HARD_LIMIT_REQUIRED = true;

    @Column(name = CPU_COUNT_HARD_LIMIT_COLUMN, nullable = false)
    @Range(min = CPU_COUNT_HARD_LIMIT_MIN, max = CPU_COUNT_HARD_LIMIT_MAX)
    private Long cpuCountHardLimit;

    @Required(value = CPU_COUNT_HARD_LIMIT_REQUIRED)
    public Long getCpuCountHardLimit()
    {
        return this.cpuCountHardLimit;
    }

    private void setCpuCountHardLimit(Long cpuCountHardLimit)
    {
        this.cpuCountHardLimit = cpuCountHardLimit;
    }

    public final static String HD_HARD_LIMIT_IN_MB_PROPERTY = "hdHardLimitInMb";

    /* package */final static String HD_HARD_LIMIT_IN_MB_COLUMN = "hdHard";

    /* package */final static long HD_HARD_LIMIT_IN_MB_MIN = NO_LIMIT;

    /* package */final static long HD_HARD_LIMIT_IN_MB_MAX = Long.MAX_VALUE;

    /* package */final static boolean HD_HARD_LIMIT_IN_MB_REQUIRED = true;

    @Column(name = HD_HARD_LIMIT_IN_MB_COLUMN, nullable = false)
    @Range(min = HD_HARD_LIMIT_IN_MB_MIN, max = HD_HARD_LIMIT_IN_MB_MAX)
    private long hdHardLimitInMb;

    @Required(value = HD_HARD_LIMIT_IN_MB_REQUIRED)
    public long getHdHardLimitInMb()
    {
        return this.hdHardLimitInMb;
    }

    private void setHdHardLimitInMb(long hdHardLimitInMb)
    {
        this.hdHardLimitInMb = hdHardLimitInMb;
    }

    public final static String STORAGE_HARD_PROPERTY = "storageHard";

    /* package */final static String STORAGE_HARD_COLUMN = "storageHard";

    /* package */final static long STORAGE_HARD_MIN = Long.MIN_VALUE;

    /* package */final static long STORAGE_HARD_MAX = Long.MAX_VALUE;

    /* package */final static boolean STORAGE_HARD_REQUIRED = true;

    @Column(name = STORAGE_HARD_COLUMN, nullable = false)
    @Range(min = STORAGE_HARD_MIN, max = STORAGE_HARD_MAX)
    private long storageHard;

    @Required(value = STORAGE_HARD_REQUIRED)
    public long getStorageHard()
    {
        return this.storageHard;
    }

    private void setStorageHard(long storageHard)
    {
        this.storageHard = storageHard;
    }

    public final static String STORAGE_SOFT_PROPERTY = "storageSoft";

    /* package */final static String STORAGE_SOFT_COLUMN = "storageSoft";

    /* package */final static long STORAGE_SOFT_MIN = Long.MIN_VALUE;

    /* package */final static long STORAGE_SOFT_MAX = Long.MAX_VALUE;

    /* package */final static boolean STORAGE_SOFT_REQUIRED = true;

    @Column(name = STORAGE_SOFT_COLUMN, nullable = false)
    @Range(min = STORAGE_SOFT_MIN, max = STORAGE_SOFT_MAX)
    private long storageSoft;

    @Required(value = STORAGE_SOFT_REQUIRED)
    public long getStorageSoft()
    {
        return this.storageSoft;
    }

    private void setStorageSoft(long storageSoft)
    {
        this.storageSoft = storageSoft;
    }

    public final static String PUBLIC_VLAN_HARD_PROPERTY = "vlanHard";

    /* package */final static String PUBLIC_VLAN_HARD_COLUMN = "vlanHard";

    /* package */final static long PUBLIC_VLAN_HARD_MIN = Long.MIN_VALUE;

    /* package */final static long PUBLIC_VLAN_HARD_MAX = Long.MAX_VALUE;

    /* package */final static boolean PUBLIC_VLAN_HARD_REQUIRED = true;

    @Column(name = PUBLIC_VLAN_HARD_COLUMN, nullable = false)
    @Range(min = PUBLIC_VLAN_HARD_MIN, max = PUBLIC_VLAN_HARD_MAX)
    private long vlanHard;

    @Required(value = PUBLIC_VLAN_HARD_REQUIRED)
    public long getVlanHard()
    {
        return this.vlanHard;
    }

    private void setVlanHard(long vlanHard)
    {
        this.vlanHard = vlanHard;
    }

    public final static String PUBLIC_VLAN_SOFT_PROPERTY = "vlanSoft";

    /* package */final static String PUBLIC_VLAN_SOFT_COLUMN = "vlanSoft";

    /* package */final static long PUBLIC_VLAN_SOFT_MIN = Long.MIN_VALUE;

    /* package */final static long PUBLIC_VLAN_SOFT_MAX = Long.MAX_VALUE;

    /* package */final static boolean PUBLIC_VLAN_SOFT_REQUIRED = true;

    @Column(name = PUBLIC_VLAN_SOFT_COLUMN, nullable = false)
    @Range(min = PUBLIC_VLAN_SOFT_MIN, max = PUBLIC_VLAN_SOFT_MAX)
    private long vlanSoft;

    @Required(value = PUBLIC_VLAN_SOFT_REQUIRED)
    public long getVlanSoft()
    {
        return this.vlanSoft;
    }

    private void setVlanSoft(long vlanSoft)
    {
        this.vlanSoft = vlanSoft;
    }

    public final static String PUBLIC_IP_HARD_PROPERTY = "publicIpsHard";

    /* package */final static String PUBLIC_IP_HARD_COLUMN = "publicIpHard";

    /* package */final static long PUBLIC_IP_HARD_MIN = Long.MIN_VALUE;

    /* package */final static long PUBLIC_IP_HARD_MAX = Long.MAX_VALUE;

    /* package */final static boolean PUBLIC_IP_HARD_REQUIRED = true;

    @Column(name = PUBLIC_IP_HARD_COLUMN, nullable = false)
    @Range(min = PUBLIC_IP_HARD_MIN, max = PUBLIC_IP_HARD_MAX)
    private long publicIpsHard;

    @Required(value = PUBLIC_IP_HARD_REQUIRED)
    public long getPublicIpsHard()
    {
        return this.publicIpsHard;
    }

    private void setPublicIpsHard(long publicIPHard)
    {
        this.publicIpsHard = publicIPHard;
    }

    public final static String PUBLIC_IP_SOFT_PROPERTY = "publicIpsSoft";

    /* package */final static String PUBLIC_IP_SOFT_COLUMN = "publicIpSoft";

    /* package */final static long PUBLIC_IP_SOFT_MIN = Long.MIN_VALUE;

    /* package */final static long PUBLIC_IP_SOFT_MAX = Long.MAX_VALUE;

    /* package */final static boolean PUBLIC_IP_SOFT_REQUIRED = true;

    @Column(name = PUBLIC_IP_SOFT_COLUMN, nullable = false)
    @Range(min = PUBLIC_IP_SOFT_MIN, max = PUBLIC_IP_SOFT_MAX)
    private long publicIpsSoft;

    @Required(value = PUBLIC_IP_SOFT_REQUIRED)
    public long getPublicIpsSoft()
    {
        return this.publicIpsSoft;
    }

    private void setPublicIpsSoft(long publicIPSoft)
    {
        this.publicIpsSoft = publicIPSoft;
    }

    @LimitRange(type = "storage")
    public Limit getStorageLimits()
    {
        return new Limit(storageSoft, storageHard);
    }

    public void setStorageLimits(Limit limit)
    {
        setStorageLimits(limit.soft, limit.hard);
    }

    private void setStorageLimits(long softLimit, long hardLimit)
    {
        setStorageSoft(softLimit);
        setStorageHard(hardLimit);
    }

    @LimitRange(type = "vlans")
    public Limit getVlansLimits()
    {
        return new Limit(vlanSoft, vlanHard);
    }

    public void setVlansLimits(Limit limit)
    {
        setVlansLimits(limit.soft, limit.hard);
    }

    private void setVlansLimits(long softLimit, long hardLimit)
    {
        setVlanSoft(softLimit);
        setVlanHard(hardLimit);
    }

    @LimitRange(type = "publicIP")
    public Limit getPublicIPLimits()
    {
        return new Limit(publicIpsSoft, publicIpsHard);
    }

    public void setPublicIPLimits(Limit limit)
    {
        setPublicIPLimits(limit.soft, limit.hard);
    }

    private void setPublicIPLimits(long softLimit, long hardLimit)
    {
        setPublicIpsSoft(softLimit);
        setPublicIpsHard(hardLimit);
    }

    @LimitRange(type = "ram")
    public Limit getRamLimitsInMb()
    {
        return new Limit(ramSoftLimitInMb, ramHardLimitInMb);
    }

    public void setRamLimitsInMb(Limit limit)
    {
        setRamLimitsInMb(limit.soft, limit.hard);
    }

    private void setRamLimitsInMb(long softLimit, long hardLimit)
    {
        setRamSoftLimitInMb(softLimit);
        setRamHardLimitInMb(hardLimit);
    }

    @LimitRange(type = "cpu")
    public Limit getCpuCountLimits()
    {
        return new Limit(cpuCountSoftLimit, cpuCountHardLimit);
    }

    public void setCpuCountLimits(Limit limit)
    {
        setCpuCountLimits(limit.soft, limit.hard);
    }

    private void setCpuCountLimits(long softLimit, long hardLimit)
    {
        setCpuCountSoftLimit(softLimit);
        setCpuCountHardLimit(hardLimit);
    }

    @LimitRange(type = "hd")
    public Limit getHdLimitsInMb()
    {
        return new Limit(hdSoftLimitInMb, hdHardLimitInMb);
    }

    public void setHdLimitsInMb(Limit limit)
    {
        setHdLimitsInMb(limit.soft, limit.hard);
    }

    private void setHdLimitsInMb(long softLimit, long hardLimit)
    {
        setHdSoftLimitInMb(softLimit);
        setHdHardLimitInMb(hardLimit);
    }

    public static boolean isValidLimitRange(long softLimit, long hardLimit)
    {
        if (softLimit < 0 || hardLimit < 0)
        {
            return false;
        }

        return (softLimit == NO_LIMIT && hardLimit == NO_LIMIT)
            || (softLimit >= NO_LIMIT && hardLimit == NO_LIMIT)
            || (softLimit != NO_LIMIT && softLimit <= hardLimit);
    }

    public enum LimitStatus
    {
        OK, SOFT_LIMIT, HARD_LIMIT
    }

    // Should be private, but made package for testing purposes
    public static LimitStatus checkLimitStatus(long softLimit, long hardLimit, long current)
    {
        assert isValidLimitRange(softLimit, hardLimit);

        if (softLimit == NO_LIMIT)
            return LimitStatus.OK;
        if (hardLimit == NO_LIMIT)
            return LimitStatus.OK;

        if (current > hardLimit)
            return LimitStatus.HARD_LIMIT;
        if (softLimit <= current)
            return LimitStatus.SOFT_LIMIT;
        return LimitStatus.OK;
    }

    public LimitStatus checkRamStatus(int currentUsedRam)
    {
        return checkLimitStatus(getRamSoftLimitInMb(), getRamHardLimitInMb(), currentUsedRam);
    }

    public LimitStatus checkCpuStatus(int currentUsedCpu)
    {
        return checkLimitStatus(getCpuCountSoftLimit(), getCpuCountHardLimit(), currentUsedCpu);
    }

    public LimitStatus checkHdStatus(long currentUsedHd)
    {
        return checkLimitStatus(getHdSoftLimitInMb(), getHdHardLimitInMb(), currentUsedHd);
    }

    public LimitStatus checkPublicIpStatus(long currentUsedIps)
    {
        return checkLimitStatus(getPublicIpsSoft(), getPublicIpsHard(), currentUsedIps);
    }

    public LimitStatus checkVlanStatus(long currentUsedVlan)
    {
        return checkLimitStatus(getVlanSoft(), getVlanHard(), currentUsedVlan);
    }

    public LimitStatus checkStorageStatus(long currentUsedStorage)
    {
        return checkLimitStatus(getStorageSoft(), getStorageHard(), currentUsedStorage);
    }

}
