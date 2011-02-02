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
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response.Status;

import org.apache.wink.common.annotations.Workspace;

import com.abiquo.vsm.VSMService;
import com.abiquo.vsm.exception.VSMException;
import com.abiquo.vsm.model.VirtualMachine;
import com.abiquo.vsm.model.transport.VirtualMachineDto;
import com.abiquo.vsm.model.transport.VirtualMachinesDto;
import com.abiquo.vsm.redis.dao.RedisDao;
import com.abiquo.vsm.redis.dao.RedisDaoFactory;

/**
 * Resource with information about virtual machine subscriptions.
 * 
 * @author ibarrera
 */
@Path(SubscriptionResource.SUBSCRIPTIONS_PATH)
@Workspace(workspaceTitle = "Abiquo VSM workspace", collectionTitle = "Subscriptions")
public class SubscriptionResource extends AbstractResource
{
    /** The resource path. */
    public static final String SUBSCRIPTIONS_PATH = "subscriptions";

    /** The subscription parameter name. */
    public static final String SUBSCRIPTION_PARAM = "subs";

    /** The subscription path. */
    public static final String SUBSCRIPTION_PATH = "{" + SUBSCRIPTION_PARAM + "}";

    /** The query parameter used to specify the virtual machine name. */
    private static final String VIRTUAL_MACHINE_NAME_QUERY_PARAM = "virtualmachine";

    /** The VSM service. */
    protected VSMService vsmService;

    /** The Redis DAO. */
    private RedisDao dao;

    /**
     * Creates the resource.
     */
    public SubscriptionResource()
    {
        vsmService = VSMService.getInstance();
        dao = RedisDaoFactory.getInstance();
    }

    /**
     * Get the list of subscriptions.
     * <p>
     * This method returns the list of all subscriptions, or the details of a single subscription if
     * the {@link #VIRTUAL_MACHINE_NAME_QUERY_PARAM} is specified.
     * 
     * @return The list of monitored machines.
     */
    @GET
    public VirtualMachinesDto getSubscriptions(
        @QueryParam(VIRTUAL_MACHINE_NAME_QUERY_PARAM) String virtualMachineName)
    {
        checkSystem();

        Set<VirtualMachine> vms = null;

        if (virtualMachineName == null)
        {
            vms = dao.findAllVirtualMachines();
        }
        else
        {
            VirtualMachine vm = dao.findVirtualMachineByName(virtualMachineName);
            if (vm == null)
            {
                throw new VSMException(Status.NOT_FOUND,
                    "There is no subscription for virtual machine " + virtualMachineName);
            }

            vms = new HashSet<VirtualMachine>();
            vms.add(vm);
        }

        VirtualMachinesDto dto = new VirtualMachinesDto();

        for (VirtualMachine vm : vms)
        {
            dto.add(toDto(vm));
        }

        return dto;
    }

    /**
     * Subscribe to changes to the given virtual machine.
     * 
     * @param virtualMachine The virtual machine to subscribe to.
     * @return The subscription details.
     */
    @POST
    public VirtualMachineDto subscribe(VirtualMachineDto virtualMachine)
    {
        checkSystem();

        VirtualMachine vm =
            vsmService.subscribe(virtualMachine.getPhysicalMachine().getAddress(), virtualMachine
                .getPhysicalMachine().getType(), virtualMachine.getName());

        return toDto(vm);
    }

    /**
     * Unsubscribes from changes to the given virtual machine.
     * 
     * @param subscriptionId The id of the virtual machine.
     */
    @DELETE
    @Path(SUBSCRIPTION_PATH)
    public void unsubscribe(@PathParam(SUBSCRIPTION_PARAM) String subscriptionId)
    {
        checkSystem();

        VirtualMachine vm = dao.getVirtualMachine(Integer.valueOf(subscriptionId));
        if (vm == null)
        {
            throw new VSMException(Status.NOT_FOUND, "There is no subscription with id "
                + subscriptionId);
        }

        vsmService.unsubscribe(vm.getPhysicalMachine().getAddress(), vm.getPhysicalMachine()
            .getType(), vm.getName());
    }
}
