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

package com.abiquo.nodecollector.aim;

import java.util.List;

import com.abiquo.nodecollector.exception.libvirt.AimException;
import com.abiquo.server.core.infrastructure.nodecollector.ResourceType;

/**
 * Realize a GET operation to each AIM plugin in order to check its health.
 */
public interface AimCollector
{

    /**
     * Ping the AIM service in the host machine.
     * 
     * @throws AimException if any connectivity problem occurs
     */
    void pingAIM() throws AimException;

    /**
     * Check each of the required plugins on the AIM service running on the provided machine IP.
     * 
     * @throws AimException when the communication can not be realized of any of the plugins have
     *             some problem on its check method.
     */
    void checkAIM() throws AimException;

    /**
     * Get all the defined Datastores on the provided machine IP.
     * 
     * @return a list of Datastore resources as follow:
     *         <UL>
     *         <li>"resourceType" as STORAGE_DISK constant.</li>
     *         <li>"resourceSubType" as the Datastore kind (nfs, ext3 ...).</li>
     *         <li>"elementName" as the Datastore device (/dev/sd1, nfs:/opt/export ...).</li>
     *         <li>"address" as the Datastore moutn point path (/, /opt/nfs-testing ...).</li>
     *         <li>"units" as the total size (used plus available) of the datastore expresed on
     *         Bytes (298696808, 1548152 ...).</li>
     *         </UL>
     * @throws AimException when the communication can not be realized of any problem during the
     *             operation.
     */
    List<ResourceType> getDatastores() throws AimException;

    /**
     * Get the Network interfaces (link up) on the provided machine IP (except loopback).
     * 
     * @return a list of network interfaces resources as follow:
     *         <UL>
     *         <li>"resourceType" as NETWORK_INTERFACE constant.</li>
     *         <li>"elementName" as the network interface device (lo, eth0, wlan0...).</li>
     *         <li>"address" as the network interface hardware address (MAC).</li>
     *         </UL>
     * @throws AimException when the communication can not be realized of any problem during the
     *             operation.
     */
    List<ResourceType> getNetInterfaces()
        throws AimException;

    /**
     * Get the size of the disk file on the cloud node file system.
     * @param diskFilePath the path to the disk file on the cloud node file system.
     * @return the Bytes of the defined datastore (used and available).
     * @throws AimException when the communication can not be realized of any problem during the
     *             operation.
     */
    Long getDiskFileSize(String diskFilePath)
        throws AimException;

    /**
     * Get the IQN of node's ISCSI Initiator
     * 
     * @return The initiator IQN or null if it is empty
     * @throws AimException when the communication can not be realized of any problem during the
     *             operation.
     */
    public String getInitiatorIQN() throws AimException;
}
