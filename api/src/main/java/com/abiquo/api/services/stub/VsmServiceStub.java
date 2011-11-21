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

import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.abiquo.api.exceptions.APIError;
import com.abiquo.api.services.DefaultApiService;
import com.abiquo.server.core.cloud.Hypervisor;
import com.abiquo.server.core.cloud.VirtualMachine;
import com.abiquo.server.core.infrastructure.RemoteService;
import com.abiquo.vsm.client.VSMClient;

/**
 * Implements Virtual System Monitor operations.
 * 
 * @author pnavarro
 */
@Service
public class VsmServiceStub extends DefaultApiService
{

    private final static Logger log = LoggerFactory.getLogger(VsmServiceStub.class);

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
    public void monitor(final RemoteService service, final Hypervisor hypervisor)
    {
        VSMClient vsmClient = initializeVSMClient(service.getUri());
        try
        {
            URL pmURL = new URL("http", hypervisor.getIp(), hypervisor.getPort(), "");
            String urlString = pmURL.toString();

            if (!urlString.endsWith("/"))
            {
                urlString += "/";
            }

            vsmClient.monitor(urlString, hypervisor.getType().name(), hypervisor.getUser(),
                hypervisor.getPassword());
        }
        catch (Exception e)
        {
            log.error(APIError.MONITOR_PROBLEM + e.getMessage());
            addUnexpectedErrors(APIError.MONITOR_PROBLEM);
            flushErrors();
        }
    }

    /**
     * Stops monitoring the physical machine
     * 
     * @param serviceUri the vsm uri
     * @param physicalMachineAddress The physical machine to monitor.
     * @param physicalMachinePort the physical machine port
     * @param type The hypervisor type of the physical machine.
     */
    public void shutdownMonitor(final RemoteService service, final Hypervisor hypervisor)
    {
        VSMClient vsmClient = initializeVSMClient(service.getUri());
        try
        {
            URL pmURL = new URL("http", hypervisor.getIp(), hypervisor.getPort(), "");
            vsmClient.shutdown(pmURL.toString());
        }
        catch (Exception e)
        {
            log.error(APIError.UNMONITOR_PROBLEM + e.getMessage());
            addUnexpectedErrors(APIError.UNMONITOR_PROBLEM);
            flushErrors();
        }

    }

    protected VSMClient initializeVSMClient(final String serviceURI)
    {
        return new VSMClient(serviceURI);
    }

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
    public void subscribe(final RemoteService service, final VirtualMachine virtualMachine)
    {

        VSMClient vsmClient = initializeVSMClient(service.getUri());
        try
        {
            URL pmURL =
                new URL("http", virtualMachine.getHypervisor().getIp(), virtualMachine
                    .getHypervisor().getPort(), "");
            String urlString = pmURL.toString();

            if (!urlString.endsWith("/"))
            {
                urlString += "/";
            }

            vsmClient.subscribe(urlString, virtualMachine.getHypervisor().getType().name(),
                virtualMachine.getName());
        }
        catch (Exception e)
        {
            log.error(APIError.MONITOR_PROBLEM + e.getMessage());
            addUnexpectedErrors(APIError.MONITOR_PROBLEM);
            flushErrors();
        }
    }

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
    public void unsubscribe(final RemoteService service, final VirtualMachine virtualMachine)
    {

        VSMClient vsmClient = initializeVSMClient(service.getUri());
        try
        {
            vsmClient.unsubscribe(virtualMachine.getName());
        }
        catch (Exception e)
        {
            log.error(APIError.MONITOR_PROBLEM + e.getMessage());
            addUnexpectedErrors(APIError.MONITOR_PROBLEM);
            flushErrors();
        }
    }
}
