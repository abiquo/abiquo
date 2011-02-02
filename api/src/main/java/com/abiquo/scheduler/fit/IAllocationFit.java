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

/**
 * Implements an heuristic strategy pattern for physical machine election.
 * 
 * @author apuig
 */
public interface IAllocationFit
{
    /**
     * Compute the goodness of the machine.
     * 
     * @param machine, the physical machine to compute its rank.
     * @return an heuristic of the machine goodness (greater rank, better machine).
     *         <p>
     *         TODO use Double
     */
    public int computeRanking(Machine machine);
}
