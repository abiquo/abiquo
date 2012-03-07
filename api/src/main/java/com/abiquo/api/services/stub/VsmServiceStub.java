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

import org.apache.wink.client.ClientRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.abiquo.api.exceptions.APIError;
import com.abiquo.api.pools.RemoteServiceClientPool;
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
    protected RemoteServiceClientPool clientPool;

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
        VSMClient client = (VSMClient) clientPool.getClientFor(service);

        try
        {
            client.monitor(buildHypervisorURI(hypervisor), hypervisor.getType().name(),
                hypervisor.getUser(), hypervisor.getPassword());
        }
        catch (ClientRuntimeException e)
        {
            LOGGER.error(APIError.VSM_UNAVAILABE.getMessage(), e);
            addServiceUnavailableErrors(APIError.VSM_UNAVAILABE);
            flushErrors();
        }
        catch (Exception e)
        {
            LOGGER.error(APIError.MONITOR_PROBLEM.getMessage(), e);
            addUnexpectedErrors(APIError.MONITOR_PROBLEM);
            flushErrors();
        }
        finally
        {
            clientPool.releaseClientFor(service, client);
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
        VSMClient client = (VSMClient) clientPool.getClientFor(service);

        try
        {
            client.shutdown(buildHypervisorURI(hypervisor));
        }
        catch (ClientRuntimeException e)
        {
            LOGGER.error(APIError.VSM_UNAVAILABE.getMessage(), e);
            addServiceUnavailableErrors(APIError.VSM_UNAVAILABE);
            flushErrors();
        }
        catch (VSMClientException e)
        {
            LOGGER.error(APIError.UNMONITOR_PROBLEM.getMessage(), e);
            addUnexpectedErrors(APIError.UNMONITOR_PROBLEM);
            flushErrors();
        }
        finally
        {
            clientPool.releaseClientFor(service, client);
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
    public void subscribe(final RemoteService service, final VirtualMachine virtualMachine,
        final boolean logError)
    {
        VSMClient client = (VSMClient) clientPool.getClientFor(service);

        try
        {
            Hypervisor hypervisor = virtualMachine.getHypervisor();

            client.subscribe(buildHypervisorURI(hypervisor), hypervisor.getType().name(),
                virtualMachine.getName());
        }
        catch (ClientRuntimeException e)
        {
            LOGGER.error(APIError.VSM_UNAVAILABE.getMessage(), e);
            addServiceUnavailableErrors(APIError.VSM_UNAVAILABE);
            flushErrors();
        }
        catch (VSMClientException e)
        {
            if (logError)
            {
                LOGGER.error(APIError.SUBSCRIPTION_PROBLEM.getMessage(), e);
                addUnexpectedErrors(APIError.SUBSCRIPTION_PROBLEM);
                flushErrors();
            }
        }
        finally
        {
            clientPool.releaseClientFor(service, client);
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
        subscribe(service, virtualMachine, Boolean.TRUE);
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
        VSMClient client = (VSMClient) clientPool.getClientFor(service);

        try
        {
            client.unsubscribe(virtualMachine.getName());
        }
        catch (ClientRuntimeException e)
        {
            LOGGER.error(APIError.VSM_UNAVAILABE.getMessage(), e);
            addServiceUnavailableErrors(APIError.VSM_UNAVAILABE);
            flushErrors();
        }
        catch (VSMClientException e)
        {
            LOGGER.error(APIError.UNSUBSCRIPTION_PROBLEM.getMessage(), e);
            addUnexpectedErrors(APIError.UNSUBSCRIPTION_PROBLEM);
            flushErrors();
        }
        finally
        {
            clientPool.releaseClientFor(service, client);
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
        VSMClient client = (VSMClient) clientPool.getClientFor(service);

        try
        {
            client.publishState(buildHypervisorURI(virtualMachine.getHypervisor()),
                virtualMachine.getName());
        }
        catch (ClientRuntimeException e)
        {
            LOGGER.error(APIError.VSM_UNAVAILABE.getMessage(), e);
            addServiceUnavailableErrors(APIError.VSM_UNAVAILABE);
            flushErrors();
        }
        catch (VSMClientException e)
        {
            LOGGER.error(APIError.REFRESH_STATE_PROBLEM.getMessage(), e);
            addUnexpectedErrors(APIError.REFRESH_STATE_PROBLEM);
            flushErrors();
        }
        finally
        {
            clientPool.releaseClientFor(service, client);
        }
    }

    /**
     * Invalidate the last known state of the given virtual machine.
     * 
     * @param service The VSM uri.
     * @param virtualMachine The virtual machine to query.
     */
    public void invalidateLastKnownVirtualMachineState(final RemoteService service,
        final VirtualMachine virtualMachine)
    {
        VSMClient client = (VSMClient) clientPool.getClientFor(service);

        try
        {
            client.invalidateLastKnownState(buildHypervisorURI(virtualMachine.getHypervisor()),
                virtualMachine.getName());
        }
        catch (ClientRuntimeException e)
        {
            LOGGER.error(APIError.VSM_UNAVAILABE.getMessage(), e);
            addServiceUnavailableErrors(APIError.VSM_UNAVAILABE);
            flushErrors();
        }
        catch (VSMClientException e)
        {
            LOGGER.error(APIError.INVALIDATE_STATE_PROBLEM.getMessage(), e);
            addUnexpectedErrors(APIError.INVALIDATE_STATE_PROBLEM);
            flushErrors();
        }
        finally
        {
            clientPool.releaseClientFor(service, client);
        }
    }

    /**
     * Returns true if the subscription exists.
     * 
     * @param service The VSM {@link RemoteService}
     * @param name The {@link VirtualMachine#NAME_PROPERTY}
     * @return True if the virtual machine is subscribed. False otherwise.
     */
    public boolean isVirtualMachineSubscribed(final RemoteService service, final String name)
    {
        VSMClient client = (VSMClient) clientPool.getClientFor(service);

        try
        {
            return client.isSubscribed(name);
        }
        finally
        {
            clientPool.releaseClientFor(service, client);
        }
    }

    /**
     * Returns true if the hypervisor is monitored.
     * 
     * @param service The VSM uri.
     * @param hypervisor The hypervisor to query.
     */
    public boolean isHypervisorMonitored(final RemoteService service, final Hypervisor hypervisor)
    {
        VSMClient client = (VSMClient) clientPool.getClientFor(service);

        try
        {
            return client.isMonitored(buildHypervisorURI(hypervisor));
        }
        finally
        {
            clientPool.releaseClientFor(service, client);
        }
    }
}
