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
package com.abiquo.virtualfactory.repositorymanager;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import org.apache.wink.client.ClientConfig;
import org.apache.wink.client.ClientResponse;
import org.apache.wink.client.Resource;
import org.apache.wink.client.RestClient;

/**
 * Client class to connect to the NFS Manager module.
 * 
 * @author ibarrera
 */
public class RepositoryManagerStub
{
    /** The path to access the Disk File resource. */
    private static final String DISK_FILE_RESOURCE_PATH = "diskfile";

    /** The Repository Manager address. */
    private String repositoryManagerAddress;

    /**
     * Creates a new <code>RepositoryManagerStub</code> pointing to the given Repository Manager
     * address.
     * 
     * @param repositoryManagerAddress The address of the Repository Manager.
     */
    public RepositoryManagerStub(final String repositoryManagerAddress)
    {
        this.repositoryManagerAddress = repositoryManagerAddress;

        if (!this.repositoryManagerAddress.endsWith("/"))
        {
            this.repositoryManagerAddress = this.repositoryManagerAddress + "/";
        }
    }

    /**
     * Copies a file in the NFS repository from one location to another one.
     * 
     * @param source The source path of the file to copy.
     * @param destination The target path of the file to copy.
     * @throws NFSManagerMException If an error occurs.
     */
    public void copy(final String source, final String destination)
        throws RepositoryManagerException
    {
        // Create the transfer object
        DiskFile virtualImage = new DiskFile();
        virtualImage.setLocation(destination);

        // Instantiate and call the NFS Manager module
        // Copy opperation may consume a long time; timeout is disabled
        ClientConfig clientConfig = new ClientConfig().readTimeout(0);
        RestClient client = new RestClient(clientConfig);

        Resource resource =
            client.resource(repositoryManagerAddress + DISK_FILE_RESOURCE_PATH + "/" + source);

        ClientResponse response =
            resource.accept(MediaType.APPLICATION_XML).contentType(MediaType.APPLICATION_XML).put(
                virtualImage);

        // Throw an exception if HTTP response code is not a 200 OK
        if (response.getStatusType().getStatusCode() != Status.OK.getStatusCode())
        {
            if (response.getStatusType().getStatusCode() == Status.NOT_FOUND.getStatusCode())
            {
                String destinationPath = destination.substring(0, destination.lastIndexOf('/'));

                String msg =
                    String.format("Could not copy the image [%s] to the destination path [%s]."
                        + " Please, verify that both exist in the repository.", source,
                        destinationPath);

                throw new RepositoryManagerException(msg);
            }
            else
            {
                throw new RepositoryManagerException(response.getMessage());
            }
        }
    }
}
