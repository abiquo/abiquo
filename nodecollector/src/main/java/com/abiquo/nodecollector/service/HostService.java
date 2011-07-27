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
import com.abiquo.server.core.infrastructure.nodecollector.HostDto;
/**
 * This class exposes all business logic of the module.
 * 
 * @author jdevesa@abiquo.com
 */
public interface HostService
{


    /**
     * @param ipAddress
     * @param hypervisor
     * @param user
     * @param password
     * @param port
     * @return
     * @throws NodecollectorException
     */
    public HostDto getHostInfo(final String ipAddress, final HypervisorType hypervisor,
        final String user, final String password, final Integer port)
        throws NodecollectorException;

}
