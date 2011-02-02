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

package com.abiquo.abiserver.abicloudws;

import org.apache.wink.client.ClientConfig;
import org.apache.wink.client.ClientResponse;
import org.apache.wink.client.ClientRuntimeException;
import org.apache.wink.client.Resource;
import org.apache.wink.client.RestClient;
import org.apache.wink.common.internal.utils.UriHelper;

import com.abiquo.abiserver.exception.RemoteServiceException;

/**
 * This class encapsulates all the possibly requests to a remote service
 * 
 * @author pnavarro
 */
public class RemoteServiceClient
{
    private static int CONNECTION_TIMEOUT = 30000; // milliseconds

    private static final String CHECK_RESOURCE = "check";

    private String url;

    /**
     * Constructor of the class.
     * 
     * @param uri the address of the remote service to send requests and get responses
     */
    public RemoteServiceClient(final String uri)
    {
        url = uri;
    }

    /**
     * Execute a simple GET method to make sure the remote service exists in the given URI of the
     * Constructor
     * 
     * @throws RemoteServiceConnection if the remote service doesn't exists or it is not available
     */
    public void ping() throws RemoteServiceException
    {
        // Instantiate and call the NFS Manager module
        // Copy opperation may consume a long time; timeout is disabled
        ClientConfig clientConfig =
            new ClientConfig().connectTimeout(CONNECTION_TIMEOUT).readTimeout(CONNECTION_TIMEOUT);
        RestClient restClient = new RestClient(clientConfig);
        Resource checkResource =
            restClient.resource(UriHelper.appendPathToBaseUri(url, CHECK_RESOURCE));

        try
        {
            ClientResponse response = checkResource.get();

            if (response.getStatusCode() != 200)
            {
                throw new RemoteServiceException("Ping exception when trying to contact " + url);
            }
        }
        catch (ClientRuntimeException e)
        {
            // Mostly caused by ConnectException
            throw new RemoteServiceException("Ping exception when trying to contact " + url + ": "
                + e.getMessage());
        }
    }
}
