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

package com.abiquo.nodecollector.domain;

import com.abiquo.model.enumerator.HypervisorType;
import com.abiquo.nodecollector.exception.CollectorException;
import com.abiquo.nodecollector.exception.ConnectionException;
import com.abiquo.nodecollector.exception.LoginException;
import com.abiquo.server.core.infrastructure.nodecollector.HostDto;
import com.abiquo.server.core.infrastructure.nodecollector.VirtualSystemCollectionDto;

/**
 * This is the basic interface of this module. Exposes the methods to retrieve the needed values
 * from remote nodes. Each Hypervisor should have its own implementation of this interface.
 * 
 * @author jdevesa
 */
public interface HypervisorCollector
{

    /**
     * Connect with the Hypervisor running in a remote machine.
     * 
     * @param user user to connect to the Hypervisor.
     * @param password password to authenticate the user.
     * @throws ConnectionException if the Hypervisor Collector implementation doesn't respond.
     * @throws LoginException if the provided user and password are wrong.
     */
    public void connect(final String user, final String password) throws ConnectionException,
        LoginException;

    /**
     * Finises the connection.
     * 
     * @throws CollectorException if any problem occurs disconnecting
     */
    public void disconnect() throws CollectorException;

    /**
     * Get the physical capabilities of a remote node.
     * 
     * @return a {@link HostInfo} instance with the physical values.
     * @throws CollectorException if any problem occurs collecting the information.
     */
    public HostDto getHostInfo() throws CollectorException;

    /**
     * @return the Hypervisor type.
     */
    public HypervisorType getHypervisorType();

    /**
     * Get the virtual capabilities running in a remote node.
     * 
     * @return a list of {@link VirtualSystem} deployed in the node.
     * @throws CollectorException if any problem occurs collecting the information.
     */
    public VirtualSystemCollectionDto getVirtualMachines() throws CollectorException;

    /**
     * Set the IP address where to the Hypervisor will try to connect
     */
    public void setIpAddress(String ipAddress);

    /**
     * Get the IP address
     */
    public String getIpAddress();

    /**
     * Set the port where to connect the Abiquo AIM
     * 
     * @param aimPort
     */
    public void setAimPort(Integer aimPort);

    /**
     * @return the AIM port.
     */
    public Integer getAimPort();

}
