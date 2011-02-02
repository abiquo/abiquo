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

package com.abiquo.abiserver.scheduler;

/**
 * Hold the resource and location requirements for a VirtualImage
 */
@Deprecated // VirtualMachineRequirements
public class ImageRequired
{
    /** VirtualImage required CPU. */
    private int cpu;

    /** VirtualImage required HD. */
    private long hd;

    /** VirtualImage required RAM. */
    private int ram;

    /** VirtualMachine required VirtaulDataCenter identifier (include virtualization technology). */
    private int idVirtualDataCenter;

    public ImageRequired(int cpu, long hd, int ram, int idVirtualDataCenter)
    {
        this.cpu = cpu;
        this.hd = hd;
        this.ram = ram;
        this.idVirtualDataCenter = idVirtualDataCenter;
    }

    public int getVirtualDataCenterId()
    {
        return idVirtualDataCenter;
    }

    public void setDataCenterId(int idVirtualDataCenter)
    {
        this.idVirtualDataCenter = idVirtualDataCenter;
    }

    public int getCpu()
    {
        return cpu;
    }

    public long getHd()
    {
        return hd;
    }

    public int getRam()
    {
        return ram;
    }

    @Override
    public String toString()
    {
        return "CPU [" + cpu + "]\t RAM [" + ram + "] \t HD [" + hd + "] \tVirtualDataCenterId ["
            + idVirtualDataCenter + "]";
    }
}
