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

package com.abiquo.nodecollector.client;

import java.net.SocketTimeoutException;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import org.apache.wink.client.ClientConfig;
import org.apache.wink.client.ClientResponse;
import org.apache.wink.client.ClientRuntimeException;
import org.apache.wink.client.Resource;
import org.apache.wink.client.RestClient;
import org.apache.wink.common.internal.utils.UriHelper;

import com.abiquo.model.enumerator.HypervisorType;
import com.abiquo.model.transport.error.ErrorDto;
import com.abiquo.nodecollector.domain.HypervisorCollector;
import com.abiquo.nodecollector.exception.BadRequestException;
import com.abiquo.nodecollector.exception.CannotExecuteException;
import com.abiquo.nodecollector.exception.CollectorException;
import com.abiquo.nodecollector.exception.ConnectionException;
import com.abiquo.nodecollector.exception.LoginException;
import com.abiquo.nodecollector.exception.ServiceUnavailableException;
import com.abiquo.nodecollector.exception.UnprovisionedException;
import com.abiquo.server.core.infrastructure.nodecollector.HostDto;
import com.abiquo.server.core.infrastructure.nodecollector.HostStatusEnumType;
import com.abiquo.server.core.infrastructure.nodecollector.HypervisorEnumTypeDto;
import com.abiquo.server.core.infrastructure.nodecollector.VirtualSystemCollectionDto;
import com.abiquo.server.core.infrastructure.nodecollector.VirtualSystemDto;

/**
 * An abstract layer to all the calls to the nodecollector.
 * 
 * @author jdevesa@abiquo.com
 */
public class NodeCollectorRESTClient
{
    /**
     * Encapsulated Wink Client REST object.
     */
    protected RestClient client;

    // Paths of the resource
    /**
     * Path Value to the {@link HypervisorEnumTypeDto} resource.
     */
    protected static String hypervisorPath = "hypervisor";

    /**
     * Path value to the {@link HostDto} resource.
     */
    protected static String hostPath = "host";

    /**
     * Path value to the {@link VirtualSystemDto} resource.
     */
    protected static String virtualSystemPath = "virtualsystem";

    // Query parameters.
    /**
     * Query parameter key for the Hypervisor.
     */
    protected static String hypervisorKey = "hyp";

    /**
     * Query parameter key for the User.
     */
    protected static String userKey = "user";

    /**
     * Query parameter key for the password.
     */
    protected static String passwordKey = "passwd";

    /**
     * The CHECK uri
     */
    public static final String checkResource = "check";

    /**
     * Query parameter key for the aim port.
     */
    public static final String AIMPORT = "aimport";

    // Definition of the hypervisor specific values.
    /**
     * URI of the remote service
     */
    protected String remoteServiceURI;

    /**
     * Standard message error for unreachable discovery manager.
     */
    protected static final String UNREACHABLE = "Discovery Manager unreachable. ";

    /**
     * Standard message for timeout reached
     */
    protected static final String TIMEOUT = "Time out has reached, cannot access the machine. ";

    /** STONITH URI CONSTANTS */

    protected static final String STONITH_PATH = "stonith";

    protected static final String STONITH_UP_PATH = "up";

    protected static final String HOST_PARAM = "host";

    protected static final String PORT_PARAM = "port";

    protected static final String USERNAME_PARAM = "user";

    protected static final String PASSWORD_PARAM = "password";

    /**
     * Constructor of the REST client.
     * 
     * @param remoteServiceURI IP Address where the NodeCollector is deployed.
     */
    public NodeCollectorRESTClient(final String remoteServiceURI)
    {
        ClientConfig c = new ClientConfig();
        c.readTimeout(Integer.parseInt(System.getProperty("abiquo.nodecollector.timeout", "0")));
        c.connectTimeout(Integer.parseInt(System.getProperty("abiquo.nodecollector.timeout", "0")));
        client = new RestClient(c);
        this.remoteServiceURI = remoteServiceURI;
    }

    /**
     * Construct of the REST client with the timeouts
     * 
     * @param remoteServiceURI IP Address where the NodeCollector is deployed.
     * @param readTimeout timeout of the connection in reading response.
     * @param connectTimeout timout in connection-time.
     */
    public NodeCollectorRESTClient(final String remoteServiceURI, final Integer readTimeout,
        final Integer connectTimeout)
    {
        ClientConfig c = new ClientConfig();
        c.readTimeout(readTimeout == null ? 0 : readTimeout);
        c.connectTimeout(connectTimeout == null ? 0 : connectTimeout);
        client = new RestClient(c);
        this.remoteServiceURI = remoteServiceURI;
    }

    /**
     * Return the Hypervisor running in the given IP.
     * 
     * @param hypervisorIP IP which we want to collect is Hypervisor.
     * @return {@link HypervisorEnumTypeDto} object.
     * @throws BadRequestException if any of the parameters is wrong or missed.
     * @throws ConnectionException if any machine responds in the given IP address.
     * @throws UnprovisionedException if there is a machine in the given IP, but it has not any
     *             Hypervisor running.
     * @throws CollectorException for unexpected exceptions.
     * @throws ServiceUnavailableException
     * @throws CannotExecuteException
     */
    public HypervisorType getRemoteHypervisorType(final String hypervisorIP)
        throws BadRequestException, ConnectionException, UnprovisionedException,
        CollectorException, ServiceUnavailableException, CannotExecuteException
    {
        String uri = appendPathToBaseUri(remoteServiceURI, hypervisorIP, hypervisorPath);

        try
        {

            Resource resource = client.resource(uri);
            ClientResponse response = resource.accept(MediaType.APPLICATION_XML_TYPE).get();

            if (response.getStatusCode() != Status.OK.getStatusCode())
            {
                try
                {
                    throwAppropiateException(response);
                }
                catch (LoginException e)
                {
                    // It will never happen, so we must encapsulate in the non-expected exceptions.
                    throw new CollectorException(e.getMessage(), e);
                }
            }

            return HypervisorType.fromValue(response.getEntity(String.class));

        }

        catch (ClientRuntimeException e)
        {

            if (e.getCause().getCause() instanceof SocketTimeoutException)
            {
                throw new ServiceUnavailableException(NodeCollectorRESTClient.TIMEOUT);
            }
            // Mostly caused by ConnectException
            throw new ServiceUnavailableException(NodeCollectorRESTClient.UNREACHABLE);
        }

    }

    /**
     * Get the Host information for a remote machine.
     * 
     * @param hypervisorIP IP of the remote machine.
     * @param hypervisorType {@link HypervisorEnumTypeDto} object containgin Hypervisor is running
     *            remotely.
     * @param user user to login to the Hypervisor.
     * @param password password to authenticate to the Hypervisor.
     * @param aimport port of the aim.
     * @param repositoryLocation this parameter is for define the {@link HostStatusEnumType}.
     *            Corresponding {@link HypervisorCollector} implementation will check if there is
     *            any Datastore mounted in the remote machine which matches withe this parameter.
     * @return the {@link HostDto} object with the machine information.
     * @throws BadRequestException if any parameter is missing, wrong or null.
     * @throws LoginException if the provided user and password don't match with any Hypervisor
     *             user.
     * @throws ConnectionException if the remote machine doesn't run the provided hypervisorType
     *             parameter.
     * @throws UnprovisionedException if the machine doesn't respond.
     * @throws CollectorException for unexpected exceptions.
     * @throws ServiceUnavailableException
     * @throws CannotExecuteException
     */
    public HostDto getRemoteHostInfo(final String hypervisorIP,
        final HypervisorType hypervisorType, final String user, final String password,
        final Integer aimport) throws BadRequestException, LoginException, ConnectionException,
        UnprovisionedException, CollectorException, ServiceUnavailableException,
        CannotExecuteException
    {
        String uri = appendPathToBaseUri(remoteServiceURI, hypervisorIP, hostPath);

        Resource resource =
            client.resource(uri).queryParam(hypervisorKey, hypervisorType.getValue())
                .queryParam(userKey, user).queryParam(passwordKey, password);

        if (aimport != null)
        {
            resource.queryParam(AIMPORT, aimport);
        }

        try
        {

            ClientResponse response = resource.accept(MediaType.APPLICATION_XML_TYPE).get();

            if (response.getStatusCode() != Status.OK.getStatusCode())
            {
                throwAppropiateException(response);
            }

            return response.getEntity(HostDto.class);
        }
        catch (ClientRuntimeException e)
        {

            if (e.getCause().getCause() instanceof SocketTimeoutException)
            {
                throw new ServiceUnavailableException(NodeCollectorRESTClient.TIMEOUT);
            }
            // Mostly caused by ConnectException
            throw new ServiceUnavailableException(NodeCollectorRESTClient.UNREACHABLE);
        }
    }

    /**
     * Retrieve the list of Virtual Machines deployed in the remote Hypervisor.
     * 
     * @param hypervisorIP IP address of the remote machine.
     * @param hypervisorType {@link HypervisorEnumTypeDto} object containgin Hypervisor is running
     *            remotely.
     * @param user user to login to the Hypervisor.
     * @param password password to authenticate to the Hypervisor.
     * @param aimport port of the aim.
     * @return the list of Virtual Machines encapsulated inside the
     *         {@link VirtualSystemCollectionDto} object.
     * @throws BadRequestException if any parameter is missing, wrong or null.
     * @throws LoginException if the provided user and password don't match with any Hypervisor
     *             user.
     * @throws ConnectionException if the remote machine doesn't run the provided hypervisorType
     *             parameter.
     * @throws UnprovisionedException if the machine doesn't respond.
     * @throws CollectorException for unexpected exceptions.
     * @throws CannotExecuteException
     */
    public VirtualSystemCollectionDto getRemoteVirtualSystemCollection(final String hypervisorIP,
        final HypervisorType hypervisorType, final String user, final String password,
        final Integer aimport) throws BadRequestException, LoginException, ConnectionException,
        UnprovisionedException, CollectorException, CannotExecuteException
    {
        String uri = appendPathToBaseUri(remoteServiceURI, hypervisorIP, virtualSystemPath);
        Resource resource =
            client.resource(uri).queryParam(hypervisorKey, hypervisorType.getValue())
                .queryParam(userKey, user).queryParam(passwordKey, password);

        if (aimport != null)
        {
            resource.queryParam(AIMPORT, aimport);
        }

        try
        {
            ClientResponse response = resource.accept(MediaType.APPLICATION_XML_TYPE).get();

            if (response.getStatusCode() != Status.OK.getStatusCode())
            {
                throwAppropiateException(response);
            }

            return response.getEntity(VirtualSystemCollectionDto.class);
        }
        catch (ClientRuntimeException e)
        {
            if (e.getCause().getCause() instanceof SocketTimeoutException)
            {
                throw new ConnectionException(NodeCollectorRESTClient.TIMEOUT);
            }
            // Mostly caused by ConnectException
            throw new ConnectionException(NodeCollectorRESTClient.UNREACHABLE);
        }

    }

    /**
     * Get a unique and known remote Virtual Machine information.
     * 
     * @param uuid identifier of the remote virtual machine.
     * @param hypervisorIP IP address of the remote machine.
     * @param hypervisorType {@link HypervisorEnumTypeDto} object containgin Hypervisor is running
     *            remotely.
     * @param user user to login to the Hypervisor.
     * @param password password to authenticate to the Hypervisor.
     * @param aimport port of the aim
     * @return the Virtual Machine information encapsulated into the {@link VirtualSystemDto}
     *         object.
     * @throws BadRequestException if any parameter is missing, wrong or null.
     * @throws LoginException if the provided user and password don't match with any Hypervisor
     *             user.
     * @throws ConnectionException if the remote machine doesn't run the provided hypervisorType
     *             parameter.
     * @throws UnprovisionedException if the machine doesn't respond.
     * @throws CollectorException for unexpected exceptions.
     * @throws CannotExecuteException
     */
    public VirtualSystemDto getRemoteVirtualSystem(final String uuid, final String hypervisorIP,
        final HypervisorType hypervisorType, final String user, final String password,
        final Integer aimport) throws BadRequestException, LoginException, ConnectionException,
        UnprovisionedException, CollectorException, CannotExecuteException
    {
        String uri = appendPathToBaseUri(remoteServiceURI, hypervisorIP, virtualSystemPath, uuid);
        Resource resource =
            client.resource(uri).queryParam(hypervisorKey, hypervisorType.getValue())
                .queryParam(userKey, user).queryParam(passwordKey, password);

        if (aimport != null)
        {
            resource.queryParam(AIMPORT, aimport);
        }

        try
        {
            ClientResponse response = resource.accept(MediaType.APPLICATION_XML_TYPE).get();

            if (response.getStatusCode() != 200)
            {
                throwAppropiateException(response);
            }

            return response.getEntity(VirtualSystemDto.class);
        }
        catch (ClientRuntimeException e)
        {
            if (e.getCause().getCause() instanceof SocketTimeoutException)
            {
                throw new ConnectionException(NodeCollectorRESTClient.TIMEOUT);
            }
            // Mostly caused by ConnectException
            throw new ConnectionException(NodeCollectorRESTClient.UNREACHABLE);
        }
    }

    public boolean isStonithUp(final String ip, final Integer port, final String username,
        final String password) throws ConnectionException, BadRequestException
    {
        try
        {
            String uri = appendPathToBaseUri(remoteServiceURI, STONITH_PATH, STONITH_UP_PATH);

            Resource resource = client.resource(uri);
            resource.queryParam(HOST_PARAM, ip);
            resource.queryParam(USERNAME_PARAM, username);
            resource.queryParam(PASSWORD_PARAM, password);

            if (port != null)
            {
                resource.queryParam(PORT_PARAM, port.toString());
            }

            ClientResponse response = resource.accept(MediaType.APPLICATION_XML_TYPE).get();

            if (response.getStatusCode() != Status.NO_CONTENT.getStatusCode())
            {
                return false;
            }

            return true;
        }
        catch (ClientRuntimeException e)
        {
            return false;
        }
    }

    public boolean stonithNode(final String ip, final Integer port, final String username,
        final String password)
    {
        try
        {
            String uri = appendPathToBaseUri(remoteServiceURI, STONITH_PATH);

            Resource resource = client.resource(uri);
            resource.queryParam(HOST_PARAM, ip);
            resource.queryParam(USERNAME_PARAM, username);
            resource.queryParam(PASSWORD_PARAM, password);

            if (port != null)
            {
                resource.queryParam(PORT_PARAM, port.toString());
            }

            ClientResponse response = resource.accept(MediaType.APPLICATION_XML_TYPE).post(null);

            if (response.getStatusCode() != Status.NO_CONTENT.getStatusCode())
            {
                return false;
            }

            return true;
        }
        catch (ClientRuntimeException e)
        {
            return false;
        }
    }

    /**
     * Helper method to convert the HTTP response codes to Java exceptions.
     * 
     * @param error error object we get.
     * @throws BadRequestException is thrown if we get a 400 Bad Request error response.
     * @throws LoginException is thrown if we get a 401 Unauthorized error response.
     * @throws ConnectionException is thrown if we get a 412 Precondition Failed error response.
     * @throws UnprovisionedException is thrown if we get a 404 Not Found error response.
     * @throws CollectorException is thrown if we get another exception, mostly the 500 Internal
     *             Server Error.
     * @throws CannotExecuteException
     */
    protected void throwAppropiateException(final ClientResponse response)
        throws BadRequestException, LoginException, ConnectionException, UnprovisionedException,
        CollectorException, CannotExecuteException
    {
        ErrorDto error;

        if (response.getStatusCode() == Status.INTERNAL_SERVER_ERROR.getStatusCode())
        {
            throw new CollectorException("Discovery Manager " + response.getMessage());
        }

        // if we get a 404 Not-found or a 400 Bad Request in a running nodecollector the
        // ErrorResponse would be
        // deserialized.
        // If we can not deserialize it means there is a running web-server but without the
        // nodecollector. (404 for web servers with ROOT context, 400 for web servers without ROOT
        // context)
        try
        {
            error = response.getEntity(ErrorDto.class);
        }
        catch (Exception e)
        {
            // Mostly caused by ConnectException
            throw new ConnectionException(NodeCollectorRESTClient.UNREACHABLE);
        }

        switch (response.getStatusCode())
        {
            case 400:
                throw new BadRequestException(error.getMessage());
            case 401:
                throw new LoginException(error.getMessage());
            case 404:
                throw new UnprovisionedException(error.getMessage());
            case 409:
                throw new CannotExecuteException(error.getMessage());
            case 412:
                throw new ConnectionException(error.getMessage());
            default:
                throw new CollectorException(error.getMessage());
        }
    }

    /**
     * @param uri
     * @param paths
     * @return
     */
    protected String appendPathToBaseUri(String uri, final String... paths)
    {
        for (String path : paths)
        {
            uri = UriHelper.appendPathToBaseUri(uri, path);
        }

        return uri;
    }

    public String getRemoteServiceURI()
    {
        return remoteServiceURI;
    }
}
