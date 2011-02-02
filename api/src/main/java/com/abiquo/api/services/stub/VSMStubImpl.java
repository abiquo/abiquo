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
import com.abiquo.api.exceptions.InternalServerErrorException;
import com.abiquo.vsm.client.VSMClient;

/**
 * Implements Virtual System Monitor operations.
 * 
 * @author pnavarro
 */
@Service("vsmStub")
public class VSMStubImpl implements VSMStub
{

    private final static Logger log = LoggerFactory.getLogger(VSMStubImpl.class);

    /*
     * (non-Javadoc)
     * @see com.abiquo.api.services.stub.VsmStub#monitor(java.lang.String, java.lang.String,
     * java.lang.Integer, java.lang.String, java.lang.String, java.lang.String)
     */
    public void monitor(String serviceUri, String physicalMachineIP, Integer physicalMachinePort,
        String type, String username, String password)
    {
        VSMClient vsmClient = new VSMClient(serviceUri);
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
            throw new InternalServerErrorException(APIError.MONITOR_PROBLEM);
        }
    }

    /*
     * (non-Javadoc)
     * @see com.abiquo.api.services.stub.VsmStub#shutdownMonitor(java.lang.String, java.lang.String,
     * java.lang.Integer, java.lang.String)
     */
    public void shutdownMonitor(String serviceUri, String physicalMachineIP,
        Integer physicalMachinePort)
    {
        VSMClient vsmClient = new VSMClient(serviceUri);
        try
        {
            URL pmURL = new URL("http", physicalMachineIP, physicalMachinePort, "");
            vsmClient.shutdown(pmURL.toString());
        }
        catch (Exception e)
        {
            throw new InternalServerErrorException(APIError.UNMONITOR_PROBLEM);
        }

    }
}
