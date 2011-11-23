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

package com.abiquo.nodecollector.resource;

import javax.validation.constraints.NotNull;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

import org.apache.wink.common.annotations.Workspace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.abiquo.model.enumerator.HypervisorType;
import com.abiquo.model.validation.Ip;
import com.abiquo.model.validation.Port;
import com.abiquo.nodecollector.constants.MessageValues;
import com.abiquo.nodecollector.exception.BadRequestException;
import com.abiquo.nodecollector.exception.CollectorException;
import com.abiquo.nodecollector.exception.ConnectionException;
import com.abiquo.nodecollector.exception.LoginException;
import com.abiquo.nodecollector.exception.NodecollectorException;
import com.abiquo.nodecollector.service.HostService;
import com.abiquo.nodecollector.service.HypervisorService;
import com.abiquo.nodecollector.service.VirtualSystemService;
import com.abiquo.nodecollector.service.impl.HostServiceImpl;
import com.abiquo.server.core.infrastructure.nodecollector.HostDto;
import com.abiquo.server.core.infrastructure.nodecollector.HypervisorEnumTypeDto;
import com.abiquo.server.core.infrastructure.nodecollector.VirtualSystemCollectionDto;
import com.abiquo.server.core.infrastructure.nodecollector.VirtualSystemDto;

/**
 * The resource is responsible of route the functionality to retrieve the physical capabilities of
 * the host.
 * 
 * @author jdevesa@abiquo.com
 */
@Path("{ip}")
@Controller
@Workspace(collectionTitle = "Remote standalone hypervisors", workspaceTitle = "Nodes")
public class NodeResource
{
    public static final String HOST_IP = "ip";

    public static final String AIMPORT = "aimport";

    public static final String HYPERVISOR = "hypervisor";

    public static final String HOST = "host";

    public static final String VIRTUAL_SYSTEM = "virtualsystem";

    @Autowired
    private HypervisorService hypervisorService;

    @Autowired
    private HostService hostService;

    @Autowired
    private VirtualSystemService virtualSystemService;

    private static final Logger LOGGER = LoggerFactory.getLogger(NodeResource.class);
    
    /**
     * Returns the current Hypervisor running in the Host.
     * 
     * @param ip IP address of the Host.
     * @return a {@link HypervisorEnumTypeDto} object containing the Hypervisor serialized.
     * @throws NoHypervisorException
     * @throws CollectorException
     */
    @GET
    @Path(HYPERVISOR)
    public String getHypervisorType(@PathParam(HOST_IP) @NotNull @Ip final String ip,
        @QueryParam(AIMPORT) @DefaultValue("8889") @Port final Integer aimport)
        throws NodecollectorException
    {

    	Long time = System.currentTimeMillis();
        String hypType = hypervisorService.discoverHypervisor(ip, aimport).getValue();
        time = System.currentTimeMillis() - time;
        LOGGER.debug("Discovering hypervisor type took " + time + " miliseconds.");
        return hypType;
    }

    /**
     * Returns the physical information of a remote node.
     * 
     * @return a {@link HostDto} instance
     * @throws NoHypervisorException
     * @throws CollectorException
     * @throws LoginException
     * @throws ConnectionException
     */
    @GET
    @Path(HOST)
    public HostDto getHostInfo(@PathParam("ip") @NotNull @Ip final String ip,
        @QueryParam("hyp") @NotNull final String hypervisorType,
        @QueryParam("user") @NotNull final String user,
        @QueryParam("passwd") @NotNull final String password,
        @QueryParam(AIMPORT) @DefaultValue("8889") @Port final Integer aimport)
        throws NodecollectorException
    {

    	Long time = System.currentTimeMillis();
        HypervisorType hypType;
        try
        {
            hypType = HypervisorType.fromValue(hypervisorType);
        }
        catch (IllegalArgumentException e)
        {
            throw new BadRequestException(MessageValues.UNKNOWN_HYPERVISOR);
        }
        HostDto dto = hostService.getHostInfo(ip, hypType, user, password, aimport);
        
        time = System.currentTimeMillis() - time;
        LOGGER.debug("Retrieving host info took " + time + " miliseconds.");
        return dto;

    }

    /**
     * Routes the rest of the URI to {@link VirtualSystemResource}.
     * 
     * @param ipAddress IP address of the host
     * @return the {@link VirtualSystemResource} object
     */
    @GET
    @Path(VIRTUAL_SYSTEM)
    public VirtualSystemCollectionDto getVirtualSystemCollectionInfo(
        @PathParam("ip") @NotNull @Ip final String ip,
        @QueryParam("hyp") @NotNull final String hypervisorType,
        @QueryParam("user") @NotNull final String user,
        @QueryParam("passwd") @NotNull final String password,
        @QueryParam(AIMPORT) @DefaultValue("8889") @Port final Integer aimport)
        throws NodecollectorException
    {

    	Long time = System.currentTimeMillis();
        HypervisorType hypType;
        try
        {
            hypType = HypervisorType.fromValue(hypervisorType);
        }
        catch (IllegalArgumentException e)
        {
            throw new BadRequestException(MessageValues.UNKNOWN_HYPERVISOR);
        }
        VirtualSystemCollectionDto dtos = virtualSystemService.getVirtualSystemList(ip, hypType, user, password, aimport);
        time = System.currentTimeMillis() - time;
        LOGGER.debug("Retrieving virtual system collection info took " + time + " miliseconds.");      
        return dtos;
    }

    /**
     * Routes the rest of the URI to {@link VirtualSystemResource}.
     * 
     * @param ipAddress IP address of the host
     * @return the {@link VirtualSystemResource} object
     */
    @GET
    @Path(VIRTUAL_SYSTEM + "/{uuid}")
    public VirtualSystemDto getVirtualSystem(@PathParam("ip") @NotNull @Ip final String ip,
        @PathParam("uuid") @NotNull final String uuid,
        @QueryParam("hyp") @NotNull final String hypervisorType,
        @QueryParam("user") @NotNull final String user,
        @QueryParam("passwd") @NotNull final String password,
        @QueryParam(AIMPORT) @DefaultValue("8889") @Port final Integer aimport)
        throws NodecollectorException
    {


    	Long time = System.currentTimeMillis();
        HypervisorType hypType;
        try
        {
            hypType = HypervisorType.fromValue(hypervisorType);
        }
        catch (IllegalArgumentException e)
        {
            throw new BadRequestException(MessageValues.UNKNOWN_HYPERVISOR);
        }

        VirtualSystemDto dto = virtualSystemService.getVirtualSystem(ip, hypType, user, password, aimport, uuid);
        
        time = System.currentTimeMillis() - time;
        LOGGER.debug("Retrieving virtual system info took " + time + " miliseconds.");  
        return dto;

    }
}
