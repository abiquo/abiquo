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

package com.abiquo.model.transport;

import javax.xml.bind.annotation.XmlElement;

public abstract class SingleResourceWithLimitsDto extends SingleResourceTransportDto
{
    private static final long serialVersionUID = 1L;

    private int ramSoftLimitInMb;

    private int ramHardLimitInMb;

    private int cpuCountSoftLimit;

    private int cpuCountHardLimit;

    private long hdSoftLimitInMb;

    private long hdHardLimitInMb;

    private long storageSoft;

    private long storageHard;

    private long vlansSoft;

    private long vlansHard;

    private long publicIpsSoft;

    private long publicIpsHard;

    @XmlElement(name = "ramSoft")
    public int getRamSoftLimitInMb()
    {
        return ramSoftLimitInMb;
    }

    public void setRamSoftLimitInMb(final int ramSoftLimitInMb)
    {
        this.ramSoftLimitInMb = ramSoftLimitInMb;
    }

    @XmlElement(name = "ramHard")
    public int getRamHardLimitInMb()
    {
        return ramHardLimitInMb;
    }

    public void setRamHardLimitInMb(final int ramHardLimitInMb)
    {
        this.ramHardLimitInMb = ramHardLimitInMb;
    }

    @XmlElement(name = "cpuSoft")
    public int getCpuCountSoftLimit()
    {
        return cpuCountSoftLimit;
    }

    public void setCpuCountSoftLimit(final int cpuCountSoftLimit)
    {
        this.cpuCountSoftLimit = cpuCountSoftLimit;
    }

    @XmlElement(name = "cpuHard")
    public int getCpuCountHardLimit()
    {
        return cpuCountHardLimit;
    }

    public void setCpuCountHardLimit(final int cpuCountHardLimit)
    {
        this.cpuCountHardLimit = cpuCountHardLimit;
    }

    @XmlElement(name = "hdSoft")
    public long getHdSoftLimitInMb()
    {
        return hdSoftLimitInMb;
    }

    public void setHdSoftLimitInMb(final long hdSoftLimitInMb)
    {
        this.hdSoftLimitInMb = hdSoftLimitInMb;
    }

    @XmlElement(name = "hdHard")
    public long getHdHardLimitInMb()
    {
        return hdHardLimitInMb;
    }

    public void setHdHardLimitInMb(final long hdHardLimitInMb)
    {
        this.hdHardLimitInMb = hdHardLimitInMb;
    }

    public long getStorageSoft()
    {
        return storageSoft;
    }

    public void setStorageSoft(final long storageSoft)
    {
        this.storageSoft = storageSoft;
    }

    public long getStorageHard()
    {
        return storageHard;
    }

    public void setStorageHard(final long storageHard)
    {
        this.storageHard = storageHard;
    }

    public long getVlansSoft()
    {
        return vlansSoft;
    }

    public void setVlansSoft(final long vlansSoft)
    {
        this.vlansSoft = vlansSoft;
    }

    public long getVlansHard()
    {
        return vlansHard;
    }

    public void setVlansHard(final long vlansHard)
    {
        this.vlansHard = vlansHard;
    }

    public long getPublicIpsSoft()
    {
        return publicIpsSoft;
    }

    public void setPublicIpsSoft(final long publicIpsSoft)
    {
        this.publicIpsSoft = publicIpsSoft;
    }

    public long getPublicIpsHard()
    {
        return publicIpsHard;
    }

    public void setPublicIpsHard(final long publicIpsHard)
    {
        this.publicIpsHard = publicIpsHard;
    }

    public void setStorageLimits(final long softLimit, final long hardLimit)
    {
        setStorageSoft(softLimit);
        setStorageHard(hardLimit);
    }

    public void setVlansLimits(final long softLimit, final long hardLimit)
    {
        setVlansSoft(softLimit);
        setVlansHard(hardLimit);
    }

    public void setPublicIPLimits(final long softLimit, final long hardLimit)
    {
        setPublicIpsSoft(softLimit);
        setPublicIpsHard(hardLimit);
    }

    public void setRamLimitsInMb(final int softLimit, final int hardLimit)
    {
        setRamSoftLimitInMb(softLimit);
        setRamHardLimitInMb(hardLimit);
    }

    public void setCpuCountLimits(final int softLimit, final int hardLimit)
    {
        setCpuCountSoftLimit(softLimit);
        setCpuCountHardLimit(hardLimit);
    }

    public void setHdLimitsInMb(final long softLimit, final long hardLimit)
    {
        setHdSoftLimitInMb(softLimit);
        setHdHardLimitInMb(hardLimit);
    }
}
