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
    public void monitor(final String serviceUri, final String physicalMachineIP,
        final Integer physicalMachinePort, final String type, final String username,
        final String password)
    {
        VSMClient vsmClient = initializeVSMClient(serviceUri);
        try
        {
            URL pmURL = new URL("http", physicalMachineIP, physicalMachinePort, "");
            String urlString = pmURL.toString();

            if (!urlString.endsWith("/"))
            {
                urlString += "/";
            }

            vsmClient.monitor(urlString, type, username, password);
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
    public void shutdownMonitor(final String serviceUri, final String physicalMachineIP,
        final Integer physicalMachinePort)
    {
        VSMClient vsmClient = initializeVSMClient(serviceUri);
        try
        {
            URL pmURL = new URL("http", physicalMachineIP, physicalMachinePort, "");
            String urlString = pmURL.toString();
            if (!urlString.endsWith("/"))
            {
                urlString += "/";
            }
            vsmClient.shutdown(urlString);
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
}
