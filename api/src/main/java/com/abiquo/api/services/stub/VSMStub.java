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

package com.abiquo.api.services.stub;

public interface VSMStub
{

    /**
     * Monitors the physical machine
     * 
     * @param serviceUri the vsm uri
     * @param physicalMachineIP The physical machine to monitor.
     * @param physicalMachinePort the physical machine port
     * @param type The hypervisor type of the physical machine.
     * @param username The username used to connect to the hypervisor.
     * @param password The password used to connect to the hypervisor.
     */
    public void monitor(String serviceUri, String physicalMachineIP, Integer physicalMachinePort,
        String type, String username, String password);

    /**
     * Stops monitoring the physical machine
     * 
     * @param serviceUri the vsm uri
     * @param physicalMachineAddress The physical machine to monitor.
     * @param physicalMachinePort the physical machine port
     * @param type The hypervisor type of the physical machine.
     */
    public void shutdownMonitor(String serviceUri, String physicalMachineIP,
        Integer physicalMachinePort);

}
