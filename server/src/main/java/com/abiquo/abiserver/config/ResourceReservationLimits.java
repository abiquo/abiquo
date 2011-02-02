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

package com.abiquo.abiserver.config;

/**
 * Define the soft and hard limits for resource allocation on the scheduler. When the scheduler try
 * to allocate a new machine whose resources requirements exceed the total soft limit will cause an
 * error (warning) message on the client, and when exceed the hard limit an exception is thrown and
 * the machine is not allocated.
 */
public class ResourceReservationLimits
{

    /** Total CPU allocation allowed before warning. */
    private long cpuSoft;

    /** Total RAM allocation allowed before warning. */
    private long ramSoft;

    /** Total HD allocation allowed before warning. */
    private long hdSoft;

    /** Total CPU allocation allowed before exception. */
    private long cpuHard;

    /** Total RAM allocation allowed before exception. */
    private long ramHard;

    /** Total HD allocation allowed before exception. */
    private long hdHard;

    /**
     * @return the cpuSoft
     */
    public long getCpuSoft()
    {
        return cpuSoft;
    }

    /**
     * @param cpuSoft the cpuSoft to set
     */
    public void setCpuSoft(long cpuSoft)
    {
        this.cpuSoft = cpuSoft;
    }

    /**
     * @return the ramSoft
     */
    public long getRamSoft()
    {
        return ramSoft;
    }

    /**
     * @param ramSoft the ramSoft to set
     */
    public void setRamSoft(long ramSoft)
    {
        this.ramSoft = ramSoft;
    }

    /**
     * @return the hdSoft
     */
    public long getHdSoft()
    {
        return hdSoft;
    }

    /**
     * @param hdSoft the hdSoft to set
     */
    public void setHdSoft(long hdSoft)
    {
        this.hdSoft = hdSoft;
    }

    /**
     * @return the cpuHard
     */
    public long getCpuHard()
    {
        return cpuHard;
    }

    /**
     * @param cpuHard the cpuHard to set
     */
    public void setCpuHard(long cpuHard)
    {
        this.cpuHard = cpuHard;
    }

    /**
     * @return the ramHard
     */
    public long getRamHard()
    {
        return ramHard;
    }

    /**
     * @param ramHard the ramHard to set
     */
    public void setRamHard(long ramHard)
    {
        this.ramHard = ramHard;
    }

    /**
     * @return the hdHard
     */
    public long getHdHard()
    {
        return hdHard;
    }

    /**
     * @param hdHard the hdHard to set
     */
    public void setHdHard(long hdHard)
    {
        this.hdHard = hdHard;
    }

    @Override
    public String toString()
    {
        return "SoftLimit\t CPU [" + getCpuSoft() + "]\t RAM [" + getRamSoft() + "]\t HD ["
            + getHdSoft() + "] \n" + "HardLimit\t CPU [" + getCpuHard() + "]\t RAM ["
            + getRamHard() + "]\t HD [" + getHdHard() + "]";
    }

}
