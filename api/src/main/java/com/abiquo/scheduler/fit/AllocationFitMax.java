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
 * Basic Virtual Machine Unit refers to BASE_CPU and BASE_RAM. Each resource type is equally
 * combined.
 */
public class AllocationFitMax implements IAllocationFit
{

    /** Standard virtual machine CPU utilization (1 virtual Cpu). */
    private final long BASE_CPU = 1;

    /** Standard virtual machine RAM utilization (1/2 Gb). */
    private final long BASE_RAM = 512;

    @Override
    public int computeRanking(final Machine machine)
    {
        int rank;
        double pRam, pCpu;

        pCpu = (machine.getVirtualCpuCores() - machine.getVirtualCpusUsed()) / BASE_CPU;
        pRam = (machine.getVirtualRamInMb() - machine.getVirtualRamUsedInMb()) / BASE_RAM;

        rank = -((int) ((pCpu + pRam) * 100 / 2));

        // log.debug("RANK for machine [" + machine.getName() + "] is \t[" + rank
        // + "] \t (pCpu[{}] pRam[{}] pHd[" + pHd + "])", pCpu, pRam);

        return rank;
    }

    public static void main(String[] args)
    {
        double pCpu = (4 - 2) / 1;
        double pRam = (4085 - 512) / 512;
        double rank = -((int) ((pCpu + pRam) * 100 / 2));

        System.err.println(String.format("cpu %s\t ram %s \t : %s",
            Double.valueOf(pCpu).toString(), Double.valueOf(pRam).toString(), Double.valueOf(rank)
                .toString()));

        pCpu = (4 - 3) / 1;
        pRam = (4085 - 384) / 512;

        rank = -((int) ((pCpu + pRam) * 100 / 2));

        System.err.println(String.format("cpu %s\t ram %s \t : %s",
            Double.valueOf(pCpu).toString(), Double.valueOf(pRam).toString(), Double.valueOf(rank)
                .toString()));
    }
}
