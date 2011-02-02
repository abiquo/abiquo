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
 * Inverse {@link AllocationFitMax}
 * <p>
 * {@link FitPolicy.PROGRESSIVE} implementation. Highest rank for machines with more resource
 * utilization.
 * <p>
 * A ''Basic Virtual Machine Unit'' is used in order compute how many of these ''Basic Unit VM''
 * fills on the current machine.
 * <p>
 * Basic Virtual Machine Unit refers to BASE_CPU, BASE_RAM and BASE_HD sizes. Each resource type is
 * equally combined.
 */
public class AllocationFitMin extends AllocationFitMax
{
    @Override
    public int computeRanking(final Machine machine)
    {
        return -super.computeRanking(machine);
    }
}
