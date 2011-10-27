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

package com.abiquo.nodecollector.service;

import com.abiquo.model.enumerator.HypervisorType;
import com.abiquo.nodecollector.exception.NodecollectorException;

/**
 * Hypervisor Service class contain all the business logic to get the kind of Hypervisor we are
 * looking for.
 * 
 * @author jdevesa@abiquo.com
 */
public interface HypervisorService
{
    /**
     * Discover which Hypervisor is running in the given IP address.
     * 
     * @param ip IP address to check
     * @return the {@link HypervisorEnumTypeDto} object.
     * @throws NoHypervisorException if no Hypervisor has been found.
     */
    public HypervisorType discoverHypervisor(String ip, Integer aimport)
        throws NodecollectorException;
}
