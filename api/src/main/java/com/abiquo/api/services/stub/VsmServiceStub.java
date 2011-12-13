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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.abiquo.api.exceptions.APIError;
import com.abiquo.api.pools.impl.VSMClientPool;
import com.abiquo.api.services.DefaultApiService;
import com.abiquo.server.core.cloud.Hypervisor;
import com.abiquo.server.core.cloud.VirtualMachine;
import com.abiquo.server.core.infrastructure.RemoteService;
import com.abiquo.vsm.client.VSMClient;
import com.abiquo.vsm.client.VSMClientException;

/**
 * Implements Virtual System Monitor operations.
 * 
 * @author pnavarro
 * @author enric.ruiz@abiquo.com
 */
@Service
public class VsmServiceStub extends DefaultApiService
{
    private final static Logger LOGGER = LoggerFactory.getLogger(VsmServiceStub.class);

    @Autowired
    protected VSMClientPool vsmClientPool;

    /**
     * Builds the URI string that represents an hypervisor.
     * 
     * @param hypervisor The hypervisor to consider
     * @return The URI string
     */
    protected String buildHypervisorURI(final Hypervisor hypervisor)
    {
        return String.format("http://%s:%d/", hypervisor.getIp(), hypervisor.getPort());
    }

    /**
     * Borrows an VSMClient from the pool.
     * 
     * @return VSMClient instance
     */
    protected VSMClient getClientFromPool(RemoteService service)
    {
        VSMClient client = null;

        try
        {
            client = vsmClientPool.borrowObject(service.getUri());
        }
        catch (Exception e)
        {
            LOGGER.error(APIError.VSMCLIENTFROMPOOL_PROBLEM.getMessage(), e);
            addUnexpectedErrors(APIError.VSMCLIENTFROMPOOL_PROBLEM);
            flushErrors();
        }

        return client;
    }

    /**
     * Returns an VSMClient instance to the pool.
     * 
     * @param client instance to return to the pool
     */
    protected void returnClientToPool(VSMClient client)
    {
        try
        {
            vsmClientPool.returnObject(client);
        }
        catch (Exception e)
        {
            LOGGER.trace("Unable to return VSMClient instance to pool.", e);
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
    public void monitor(final RemoteService service, final Hypervisor hypervisor)
    {
        VSMClient client = getClientFromPool(service);

        try
        {
            client.monitor(buildHypervisorURI(hypervisor), hypervisor.getType().name(),
                hypervisor.getUser(), hypervisor.getPassword());
        }
        catch (Exception e)
        {
            LOGGER.error(APIError.MONITOR_PROBLEM.getMessage(), e);
            addUnexpectedErrors(APIError.MONITOR_PROBLEM);
            flushErrors();
        }
        finally
        {
            returnClientToPool(client);
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
        VSMClient client = getClientFromPool(service);

        try
        {
            client.shutdown(buildHypervisorURI(hypervisor));
        }
        catch (VSMClientException e)
        {
            LOGGER.error(APIError.UNMONITOR_PROBLEM.getMessage(), e);
            addUnexpectedErrors(APIError.UNMONITOR_PROBLEM);
            flushErrors();
        }
        finally
        {
            returnClientToPool(client);
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
    public void subscribe(final RemoteService service, final VirtualMachine virtualMachine)
    {
        VSMClient client = getClientFromPool(service);

        try
        {
            Hypervisor hypervisor = virtualMachine.getHypervisor();

            client.subscribe(buildHypervisorURI(hypervisor), hypervisor.getType().name(),
                virtualMachine.getName());
        }
        catch (VSMClientException e)
        {
            LOGGER.error(APIError.SUBSCRIPTION_PROBLEM.getMessage(), e);
            addUnexpectedErrors(APIError.SUBSCRIPTION_PROBLEM);
            flushErrors();
        }
        finally
        {
            returnClientToPool(client);
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
        VSMClient client = getClientFromPool(service);

        try
        {
            client.unsubscribe(virtualMachine.getName());
        }
        catch (VSMClientException e)
        {
            LOGGER.error(APIError.UNSUBSCRIPTION_PROBLEM.getMessage(), e);
            addUnexpectedErrors(APIError.UNSUBSCRIPTION_PROBLEM);
            flushErrors();
        }
        finally
        {
            returnClientToPool(client);
        }
    }

    /**
     * Publish the current state of the given virtual machine.
     * 
     * @param service The VSM uri
     * @param virtualMachine The virtual machine to query
     */
    public void refreshVirtualMachineState(final RemoteService service,
        final VirtualMachine virtualMachine)
    {
        VSMClient client = getClientFromPool(service);

        try
        {
            client.publishState(buildHypervisorURI(virtualMachine.getHypervisor()),
                virtualMachine.getName());
        }
        catch (VSMClientException e)
        {
            LOGGER.error(APIError.REFRESH_STATE_PROBLEM.getMessage(), e);
            addUnexpectedErrors(APIError.REFRESH_STATE_PROBLEM);
            flushErrors();
        }
        finally
        {
            returnClientToPool(client);
        }
    }
}
