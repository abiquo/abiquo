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

package com.abiquo.vsm.client;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.codec.binary.Base64;
import org.apache.wink.client.ClientConfig;
import org.apache.wink.client.ClientResponse;
import org.apache.wink.client.Resource;
import org.apache.wink.client.RestClient;

import com.abiquo.vsm.model.transport.ErrorDto;
import com.abiquo.vsm.model.transport.PhysicalMachineDto;
import com.abiquo.vsm.model.transport.PhysicalMachinesDto;
import com.abiquo.vsm.model.transport.VirtualMachineDto;
import com.abiquo.vsm.model.transport.VirtualMachinesDto;

/**
 * Client stub to connect to the VSM module.
 * 
 * @author ibarrera
 */
public class VSMClient
{
    /** The default context path of the VSM module. */
    private static final String DEFAULT_CONTEXT_PATH = "vsm/api";

    /** The REST client. */
    private RestClient client;

    /** The default path of the VSM module. */
    private String basePath;

    /**
     * Creates a new client for the given host and port.
     * 
     * @param host The target host.
     * @param port The target port.
     */
    public VSMClient(final String host, final int port)
    {
        ClientConfig clientConfig = new ClientConfig();
        clientConfig.readTimeout(0);
        clientConfig.connectTimeout(0);

        client = new RestClient(clientConfig);
        basePath = "http://" + host + ":" + port + "/" + DEFAULT_CONTEXT_PATH;

        if (!isValidURI(basePath))
        {
            throw new IllegalArgumentException("The provided parameters do not conform a valid URL.");
        }
    }

    /**
     * Creates a new client for the given URI.
     * 
     * @param uri The target URI.
     */
    public VSMClient(String uri)
    {
        if (!isValidURI(uri))
        {
            throw new IllegalArgumentException("The provided parameter is not a valid URL.");
        }

        ClientConfig clientConfig = new ClientConfig();
        clientConfig.readTimeout(0);
        clientConfig.connectTimeout(0);

        client = new RestClient(clientConfig);

        String target = uri.substring(uri.indexOf("://") + 3);
        int slash = target.indexOf('/');
        target = target.substring(0, slash != -1 ? slash : target.length());

        basePath = "http://" + target + "/" + DEFAULT_CONTEXT_PATH;

        if (!isValidURI(basePath))
        {
            throw new IllegalArgumentException("The provided parameter is not a valid URL.");
        }
    }

    /**
     * Get the list of monitored physical machines.
     * 
     * @return The list of monitored physical machines.
     * @throws VSMClientException If the list of monitored machines can not be retrieved.
     */
    public PhysicalMachinesDto getMonitoredMachines() throws VSMClientException
    {
        Resource resource = client.resource(basePath + "/physicalmachines");
        ClientResponse response = resource.accept(MediaType.APPLICATION_XML_TYPE).get();
        checkResponseErrors(response);
        return response.getEntity(PhysicalMachinesDto.class);
    }

    /**
     * Gets the details of a monitored machine given its address.
     * 
     * @param physicalMachineAddress The physical machine address.
     * @return The monitored physical machine.
     * @throws VSMClientException If there is no monitored machine with the given address.
     */
    public PhysicalMachineDto getMonitoredMachine(String physicalMachineAddress)
        throws VSMClientException
    {
        Resource resource =
            client.resource(basePath + "/physicalmachines?address="
                + encodeUTF8(physicalMachineAddress));
        ClientResponse response = resource.accept(MediaType.APPLICATION_XML_TYPE).get();
        checkResponseErrors(response);
        return response.getEntity(PhysicalMachinesDto.class).getCollection().get(0);
    }

    /**
     * Start monitoring the given physical machine.
     * 
     * @param physicalMachineAddress The physical machine to monitor.
     * @param type The hypervisor type of the physical machine.
     * @param username The username used to connect to the hypervisor.
     * @param password The password used to connect to the hypervisor.
     * @return The just added physical machine.
     * @throws VSMClientException If the monitor operation cannot be completed.
     */
    public PhysicalMachineDto monitor(String physicalMachineAddress, String type, String username,
        String password) throws VSMClientException
    {
        PhysicalMachineDto pm = new PhysicalMachineDto();
        pm.setAddress(physicalMachineAddress);
        pm.setType(type);

        Resource resource = client.resource(basePath + "/physicalmachines");
        String authHeader = "Basic " + toBasicAuth(username, password);

        ClientResponse response =
            resource.header("Authorization", authHeader).accept(MediaType.APPLICATION_XML_TYPE)
                .contentType(MediaType.APPLICATION_XML_TYPE).post(pm);

        checkResponseErrors(response);
        return response.getEntity(PhysicalMachineDto.class);
    }

    /**
     * Stop monitoring the given physical machine.
     * 
     * @param physicalMachineAddress The physical machine to monitor.
     * @throws VSMClientException If the shutdown operation cannot be completed.
     */
    public void shutdown(String physicalMachineAddress) throws VSMClientException
    {
        PhysicalMachineDto pm = getMonitoredMachine(physicalMachineAddress);
        Resource resource = client.resource(basePath + "/physicalmachines/" + pm.getId());
        ClientResponse response = resource.accept(MediaType.APPLICATION_XML_TYPE).delete();
        checkResponseErrors(response);
    }

    /**
     * Publish the current state of the given virtual machine.
     * 
     * @param physicalMachineAddress The physical machine where the virtual machine is deployed.
     * @param virtualMachineName The name of the virtual machine.
     * @throws VSMClientException If the state of the virtual machine cannot be retrieved.
     */
    public void publishState(String physicalMachineAddress, String virtualMachineName)
        throws VSMClientException
    {
        PhysicalMachineDto pm = getMonitoredMachine(physicalMachineAddress);

        String path =
            basePath + "/physicalmachines/" + pm.getId() + "/virtualmachine/" + virtualMachineName;

        Resource resource = client.resource(path);
        ClientResponse response = resource.accept(MediaType.APPLICATION_XML_TYPE).get();
        checkResponseErrors(response);
    }

    /**
     * Get the list of current subscriptions.
     * 
     * @return The list of current subscriptions.
     * @throws VSMClientException If the list of current subscriptions cannot be retrieved.
     */
    public VirtualMachinesDto getSubscriptions() throws VSMClientException
    {
        Resource resource = client.resource(basePath + "/subscriptions");
        ClientResponse response = resource.accept(MediaType.APPLICATION_XML_TYPE).get();
        checkResponseErrors(response);
        return response.getEntity(VirtualMachinesDto.class);
    }

    /**
     * Get the subscription for the given virtual machine.
     * 
     * @param virtualMachineName The name of the virtual machine.
     * @return The subscription for the virtual machine.
     * @throws VSMClientException If there is no subscription for the given virtual machine.
     * @throws UnsupportedEncodingException
     */
    public VirtualMachineDto getSubscription(String virtualMachineName) throws VSMClientException
    {

        Resource resource =
            client.resource(basePath + "/subscriptions?virtualmachine="
                + encodeUTF8(virtualMachineName));
        ClientResponse response = resource.accept(MediaType.APPLICATION_XML_TYPE).get();
        checkResponseErrors(response);

        return response.getEntity(VirtualMachinesDto.class).getCollection().get(0);
    }

    /**
     * Returns true if the subscription exists.
     * 
     * @param virtualMachineName The name of the virtual machine.
     * @return True if the subscription exists. Otherwise false.
     */
    public boolean isSubscribed(final String virtualMachineName)
    {
        try
        {
            getSubscription(virtualMachineName);
            return true;
        }
        catch (Exception e)
        {
            return false;
        }
    }

    /**
     * Returns true if the PhysicalMachine is already monitored.
     * 
     * @param physicalMachineAddress The address of the physical machine
     * @return True if the machine is already monitored. Otherwise false;
     */
    public boolean isMonitored(final String physicalMachineAddress)
    {
        try
        {
            PhysicalMachineDto dto = getMonitoredMachine(physicalMachineAddress);
            return true;
        }
        catch (VSMClientException e)
        {
            return false;
        }
    }

    /**
     * Subscribes to changes in the given virtual machine.
     * 
     * @param physicalMachineAddress The physical machine where the virtual machine is deployed.
     * @param type The type of the hypervisor.
     * @param virtualMachineName The name of the virtual machine.
     * @return The subscription details to the virtual machine changes.
     * @throws VSMClientException If the subscription operation cannot be completed.
     */
    public VirtualMachineDto subscribe(String physicalMachineAddress, String type,
        String virtualMachineName) throws VSMClientException
    {
        PhysicalMachineDto pm = new PhysicalMachineDto();
        pm.setAddress(physicalMachineAddress);
        pm.setType(type);

        VirtualMachineDto vm = new VirtualMachineDto();
        vm.setName(virtualMachineName);
        vm.setPhysicalMachine(pm);

        Resource resource = client.resource(basePath + "/subscriptions");
        ClientResponse response =
            resource.accept(MediaType.APPLICATION_XML_TYPE)
                .contentType(MediaType.APPLICATION_XML_TYPE).post(vm);

        checkResponseErrors(response);
        return response.getEntity(VirtualMachineDto.class);
    }

    /**
     * Unsubscribes from changes to the given virtual machine.
     * 
     * @param virtualMachineName The name of the virtual machine.
     * @throws VSMClientException if the unsubscribe operation cannot be perforned.
     */
    public void unsubscribe(String virtualMachineName) throws VSMClientException
    {
        VirtualMachineDto vm = getSubscription(virtualMachineName);
        Resource resource = client.resource(basePath + "/subscriptions/" + vm.getId());
        ClientResponse response = resource.accept(MediaType.APPLICATION_XML_TYPE).delete();
        checkResponseErrors(response);
    }

    /**
     * Encode the given user name and password in a valid Basic Authentication format.
     * 
     * @param username The user name to encode.
     * @param password The password to encode.
     * @return The Basic Authentication encoded credentials.
     */
    private String toBasicAuth(String username, String password)
    {
        String token = username + ":" + password;
        return new String(Base64.encodeBase64(token.getBytes()));
    }

    /**
     * Check if the response has errors and throw the right exception.
     * 
     * @param response The response to check.
     * @throws VSMClientException A client exception if the response contains errors.
     */
    private void checkResponseErrors(final ClientResponse response) throws VSMClientException
    {
        // All the 200 are accepted responses
        if (response.getStatusCode() / 200 != 1)
        {
            Status status = Status.fromStatusCode(response.getStatusCode());
            ErrorDto error = response.getEntity(ErrorDto.class);

            if (error != null)
            {
                throw new VSMClientException(status, error.getMessage());
            }

            throw new VSMClientException(status,
                "The Virtual System monitor is not properly configured");
        }
    }

    /**
     * Checks if the given URI is a valid URI.
     * 
     * @param uri The URI to check.
     * @return Boolean indicating if the given URI is a valid URI.
     */
    private static boolean isValidURI(final String uri)
    {
        try
        {
            new URL(uri);
            return true;
        }
        catch (MalformedURLException e)
        {
            return false;
        }
    }

    private String encodeUTF8(final String value) throws VSMClientException
    {
        try
        {
            return URLEncoder.encode(value, "UTF-8");
        }
        catch (UnsupportedEncodingException e)
        {
            throw new VSMClientException(Status.INTERNAL_SERVER_ERROR, "Can not encode '" + value
                + "'. UTF-8 is an unsupported encoding.");
        }
    }
}
