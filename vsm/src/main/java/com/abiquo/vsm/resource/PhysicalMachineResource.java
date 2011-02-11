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
package com.abiquo.vsm.resource;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response.Status;

import org.apache.wink.common.annotations.Workspace;

import com.abiquo.vsm.VSMService;
import com.abiquo.vsm.exception.VSMException;
import com.abiquo.vsm.model.PhysicalMachine;
import com.abiquo.vsm.model.transport.PhysicalMachineDto;
import com.abiquo.vsm.model.transport.PhysicalMachinesDto;
import com.abiquo.vsm.redis.dao.RedisDao;
import com.abiquo.vsm.redis.dao.RedisDaoFactory;

/**
 * Resource with information about monitored cloud nodes.
 * 
 * @author ibarrera
 */
@Path(PhysicalMachineResource.PHYSICALMACHINES_PATH)
@Workspace(workspaceTitle = "Abiquo VSM workspace", collectionTitle = "Physical Machines")
public class PhysicalMachineResource extends AbstractResource
{
    /** The resource path. */
    public static final String PHYSICALMACHINES_PATH = "physicalmachines";

    /** The physical machine parameter name. */
    public static final String PHYSICALMACHINE_PARAM = "pm";

    /** The physical machine path. */
    public static final String PHYSICALMACHINE_PATH = "{" + PHYSICALMACHINE_PARAM + "}";

    /** The virtual machine param. */
    public static final String VIRTUALMACHINE_PARAM = "vm";

    /** The virtual machine path. */
    public static final String VIRTUALMACHINE_PATH =
        PHYSICALMACHINE_PATH + "/virtualmachine/{" + VIRTUALMACHINE_PARAM + "}";

    /** The query parameter used to specify the physical machine address. */
    private static final String ADDRESS_QUERY_PARAM = "address";

    /** The VSM service. */
    protected VSMService vsmService;

    /** The Redis DAO. */
    private RedisDao dao;

    /**
     * Creates the resource.
     */
    public PhysicalMachineResource()
    {
        vsmService = VSMService.getInstance();
        dao = RedisDaoFactory.getInstance();
    }

    /**
     * Get the monitored machines.
     * <p>
     * This method returns the list of all monitored machines, or the details of a single machine if
     * the {@link #ADDRESS_QUERY_PARAM} is specified.
     * 
     * @return The list of monitored machines.
     */
    @GET
    public PhysicalMachinesDto getMonitoredMachines(
        @QueryParam(ADDRESS_QUERY_PARAM) String physicalMachineAddress)
    {
        checkSystem();

        Set<PhysicalMachine> pms = null;

        if (physicalMachineAddress == null)
        {
            pms = dao.findAllPhysicalMachines();
        }
        else
        {
            PhysicalMachine pm = dao.findPhysicalMachineByAddress(physicalMachineAddress);
            if (pm == null)
            {
                throw new VSMException(Status.NOT_FOUND,
                    "There is no monitored machine with address " + physicalMachineAddress);
            }

            pms = new HashSet<PhysicalMachine>();
            pms.add(pm);
        }

        PhysicalMachinesDto dto = new PhysicalMachinesDto();

        for (PhysicalMachine pm : pms)
        {
            dto.add(toDto(pm));
        }

        return dto;
    }

    /**
     * Start monitoring a physical machine.
     * 
     * @param physicalMachine The physical machine data.
     * @param auth The authentication details for the given physical machine.
     * @return The monitored physical machine.
     */
    @POST
    public PhysicalMachineDto monitor(PhysicalMachineDto physicalMachine,
        @HeaderParam(AUTH_HEADER) String auth)
    {
        checkSystem();

        String[] credentials = getBasicAuthCredentials(auth);

        PhysicalMachine pm;
        if (credentials.length != 0)
        {
        	pm = vsmService.monitor(physicalMachine.getAddress(), physicalMachine.getType(),
                credentials[0], credentials[1]);
        }
        else
        {
        	pm = vsmService.monitor(physicalMachine.getAddress(), physicalMachine.getType());
        }
        
        return toDto(pm);
    }

    /**
     * Stops monitoring the given physical machine.
     * 
     * @param physicalMachineId The id of the physical machine.
     */
    @DELETE
    @Path(PHYSICALMACHINE_PATH)
    public void shutdown(@PathParam(PHYSICALMACHINE_PARAM) String physicalMachineId)
    {
        checkSystem();

        PhysicalMachine pm = dao.getPhysicalMachine(Integer.valueOf(physicalMachineId));
        if (pm == null)
        {
            throw new VSMException(Status.NOT_FOUND, "There is no monitored machine with id "
                + physicalMachineId);
        }

        vsmService.shutdown(pm.getAddress(), pm.getType());
    }

    /**
     * Get the current state of the given physical machine.
     * 
     * @param physicalMachineId The id of the physical machine
     * @param virtualMachineName The name of the virtual machine.
     */
    @GET
    @Path(VIRTUALMACHINE_PATH)
    public void getState(@PathParam(PHYSICALMACHINE_PARAM) String physicalMachineId,
        @PathParam(VIRTUALMACHINE_PARAM) String virtualMachineName)
    {
        checkSystem();

        PhysicalMachine pm = dao.getPhysicalMachine(Integer.valueOf(physicalMachineId));
        if (pm == null)
        {
            throw new VSMException(Status.NOT_FOUND, "There is no monitored machine with id "
                + physicalMachineId);
        }

        vsmService.getState(pm.getAddress(), pm.getType(), virtualMachineName);
    }
}
