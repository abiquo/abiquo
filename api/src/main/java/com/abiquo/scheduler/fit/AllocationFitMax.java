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

package com.abiquo.scheduler.fit;

import com.abiquo.server.core.infrastructure.Machine;
import com.abiquo.server.core.scheduler.FitPolicyRule.FitPolicy;

/**
 * {@link FitPolicy.PERFORMANCE} implementation. Highest rank for machines with less resource
 * utilization.
 * <p>
 * A ''Basic Virtual Machine Unit'' is used in order compute how many of these ''Basic Unit VM''
 * fills on the current machine.
 * <p>
 * Basic Virtual Machine Unit refers to BASE_CPU, BASE_RAM and BASE_HD sizes. Each resource type is
 * equally combined.
 */
public class AllocationFitMax implements IAllocationFit
{

    /** Standard virtual machine CPU utilization (1 virtual Cpu). */
    private final long BASE_CPU = 1;

    /** Standard virtual machine RAM utilization (1/2 Gb). */
    private final long BASE_RAM = 512;

    /** Standard virtual machine HD utilization (2 Gb). */
    private final long BASE_HD = 2048l * (1024*1024);

    @Override
    public int computeRanking(final Machine machine)
    {
        int rank;
        double pHd, pRam, pCpu;

        // log.info("cpu[{}]\tused[{}]", machine.getCpu(), machine.getCpuUsed());
        // log.info("ram [{}]\tused[{}]", machine.getRam(), machine.getRamUsed());
        // log.info("hd [{}]\tused[{}]", machine.getHd(), machine.getHdUsed());

        pCpu = (machine.getVirtualCpuCores() - machine.getVirtualCpusUsed()) / BASE_CPU;
        pRam = (machine.getVirtualRamInMb() - machine.getVirtualRamUsedInMb()) / BASE_RAM;
        pHd = (machine.getVirtualHardDiskInBytes() - machine.getVirtualHardDiskUsedInBytes()) / BASE_HD;

        rank = -((int) ((pCpu + pRam + pHd) * 100 / 3));

        // log.debug("RANK for machine [" + machine.getName() + "] is \t[" + rank
        // + "] \t (pCpu[{}] pRam[{}] pHd[" + pHd + "])", pCpu, pRam);

        return rank;
    }

}
