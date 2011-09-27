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
import com.abiquo.nodecollector.exception.CollectorException;
import com.abiquo.nodecollector.exception.LoginException;
import com.abiquo.nodecollector.exception.NodecollectorException;
import com.abiquo.server.core.infrastructure.nodecollector.VirtualSystemCollectionDto;
import com.abiquo.server.core.infrastructure.nodecollector.VirtualSystemDto;

/**
 * @author jdevesa
 */
public interface VirtualSystemService
{

    /**
     * Get the remote deployed virtual systems.
     * 
     * @param ipAddress ip address of the virtual system
     * @throws LoginException if the configured login credentials are wrong
     * @throws NoHypervisorException if there is any Hypervisor in the given IP
     * @throws CollectorException if the collecting task throws any exception
     * @return a list of {@link VirtualSystem} objects
     */
    public VirtualSystemCollectionDto getVirtualSystemList(final String ipAddress,
        final HypervisorType hypervisorType, String user, String password, final Integer port)
        throws NodecollectorException;

    /**
     * Get a unique virtual system based on its UUID
     * 
     * @param ip address of the virtual system
     * @param hypervisorType
     * @param user
     * @param password
     * @param uuid
     * @return
     */
    public VirtualSystemDto getVirtualSystemByUUID(String ip, HypervisorType hypervisorType,
        String user, String password, final Integer port, String uuid)
        throws NodecollectorException;

    /**
     * Get a unique virtual system based on its name.
     * 
     * @param ip address of the hypervisor where the virtual system is deployed
     * @param hypervisorType kind of {@link HypervisorType} it is used.
     * @param user user to log in
     * @param password password to authenticate
     * @param port port to attach to get info (libvirt only)
     * @param name name of the Virtual System.
     * @return the found Virtual System.
     * @throws NodecollectorException
     */
    public VirtualSystemDto getVirtualSystemByName(String ip, HypervisorType hypervisorType,
        String user, String password, final Integer port, String name)
        throws NodecollectorException;

}
