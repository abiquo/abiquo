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

package com.abiquo.api.resources;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

import org.apache.wink.common.annotations.Parent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.abiquo.api.exceptions.APIError;
import com.abiquo.api.exceptions.APIException;
import com.abiquo.api.exceptions.ConflictException;
import com.abiquo.api.exceptions.NotFoundException;
import com.abiquo.api.services.DatacenterService;
import com.abiquo.api.services.InfrastructureService;
import com.abiquo.api.services.NetworkService;
import com.abiquo.api.util.IRESTBuilder;
import com.abiquo.model.enumerator.HypervisorType;
import com.abiquo.model.enumerator.RemoteServiceType;
import com.abiquo.model.rest.RESTLink;
import com.abiquo.model.util.ModelTransformer;
import com.abiquo.model.validation.Hypervisor;
import com.abiquo.model.validation.Ip;
import com.abiquo.model.validation.Port;
import com.abiquo.server.core.cloud.HypervisorTypesDto;
import com.abiquo.server.core.enterprise.Enterprise;
import com.abiquo.server.core.enterprise.EnterprisesDto;
import com.abiquo.server.core.infrastructure.Datacenter;
import com.abiquo.server.core.infrastructure.DatacenterDto;
import com.abiquo.server.core.infrastructure.Machine;
import com.abiquo.server.core.infrastructure.MachineDto;
import com.abiquo.server.core.infrastructure.MachinesDto;
import com.abiquo.server.core.infrastructure.Rack;
import com.abiquo.server.core.util.PagedList;
import com.abiquo.server.core.util.network.IPAddress;

@Parent(DatacentersResource.class)
@Path(DatacenterResource.DATACENTER_PARAM)
@Controller
public class DatacenterResource extends AbstractResource
{

    public static final String DATACENTER = "datacenter";

    public static final String DATACENTER_PARAM = "{" + DATACENTER + "}";

    public static final String HYPERVISORS_PATH = "hypervisors";

    public static final String ENTERPRISES = "enterprises";

    public static final String ENTERPRISES_PATH = "action/enterprises";

    public static final String UPDATE_RESOURCES = "updateusedresources";

    public static final String UPDATE_RESOURCES_PATH = "action/updateusedresources";

    public static final String NETWORK = "network";

    public static final String ACTION_DISCOVER_HYPERVISOR_TYPE = "action/hypervisor";

    public static final String ACTION_DISCOVER_HYPERVISOR_TYPE_REL = "hypervisor";

    public static final String ACTION_DISCOVER_SINGLE = "action/discoversingle";

    public static final String ACTION_DISCOVER_REL = "discoversingle";

    public static final String ACTION_DISCOVER_MULTIPLE = "action/discovermultiple";

    public static final String ACTION_DISCOVER_MULTIPLE_REL = "discovermultiple";

    public static final String IP = "ip";

    public static final String HYPERVISOR = "hypervisor";

    public static final String USER = "user";

    public static final String PASSWORD = "password";

    public static final String PORT = "port";

    public static final String IP_FROM = "ipFrom";

    public static final String IP_TO = "ipTo";

    public static final String VSWITCH = "vswitch";

    @Autowired
    DatacenterService service;

    @Autowired
    InfrastructureService infraService;

    @Autowired
    NetworkService netService;

    @Context
    UriInfo uriInfo;

    @GET
    public DatacenterDto getDatacenter(@PathParam(DATACENTER) final Integer datacenterId,
        @Context final IRESTBuilder restBuilder) throws Exception
    {
        Datacenter datacenter = service.getDatacenter(datacenterId);

        return createTransferObject(datacenter, restBuilder);
    }

    @PUT
    public DatacenterDto modifyDatacenter(final DatacenterDto datacenterDto,
        @PathParam(DATACENTER) final Integer datacenterId, @Context final IRESTBuilder restBuilder)
        throws Exception
    {
        Datacenter datacenter = createPersistenceObject(datacenterDto);
        datacenter = service.modifyDatacenter(datacenterId, datacenter);

        return createTransferObject(datacenter, restBuilder);
    }

    @GET
    @Path(ENTERPRISES_PATH)
    public EnterprisesDto getEnterprises(@PathParam(DATACENTER) final Integer datacenterId,
        @QueryParam(START_WITH) @Min(0) final Integer startwith,
        @QueryParam(NETWORK) Boolean network,
        @QueryParam(LIMIT) @DefaultValue(DEFAULT_PAGE_LENGTH_STRING) @Min(1) final Integer limit,
        @Context final IRESTBuilder restBuilder) throws Exception

    {
        Integer firstElem = startwith == null ? 0 : startwith;
        Integer numElem = limit == null ? DEFAULT_PAGE_LENGTH : limit;
        if (network == null)
        {
            network = false;
        }

        Datacenter datacenter = service.getDatacenter(datacenterId);
        List<Enterprise> enterprises =
            service
                .findEnterprisesByDatacenterWithNetworks(datacenter, network, firstElem, numElem);
        EnterprisesDto enterprisesDto = new EnterprisesDto();
        for (Enterprise e : enterprises)
        {
            enterprisesDto.add(EnterpriseResource.createTransferObject(e, restBuilder));
        }
        enterprisesDto.setTotalSize(((PagedList) enterprises).getTotalResults());
        enterprisesDto.addLinks(buildEnterprisesLinks(uriInfo.getAbsolutePath().toString(),
            (PagedList) enterprises, network, numElem));
        return enterprisesDto;

    }

    @GET
    @Path(HYPERVISORS_PATH)
    public HypervisorTypesDto getAvailableHypervisors(
        @PathParam(DATACENTER) final Integer datacenterId, @Context final IRESTBuilder restBuilder)
        throws Exception
    {
        Datacenter datacenter = service.getDatacenter(datacenterId);

        Set<HypervisorType> types = service.getHypervisorTypes(datacenter);

        HypervisorTypesDto dto = new HypervisorTypesDto();
        dto.setCollection(new ArrayList<HypervisorType>(types));

        return dto;
    }

    // FIXME: Not allowed right now
    @DELETE
    public void deleteDatacenter(@PathParam(DATACENTER) final Integer datacenterId)
    {
        service.removeDatacenter(datacenterId);
    }

    @PUT
    @Path(UPDATE_RESOURCES_PATH)
    public void updateUsedResources(@PathParam(DATACENTER) final Integer datacenterId)
    {
        Datacenter datacenter = service.getDatacenter(datacenterId);
        List<Rack> racks = service.getRacks(datacenter);
        for (Rack rack : racks)
        {
            List<Machine> machines = infraService.getMachines(rack);
            for (Machine machine : machines)
            {
                infraService.updateUsedResourcesByMachine(machine);
            }
        }

    }

    /**
     * Get the hypervisor type of the specified cloud node.
     * 
     * @param datacenterId The ID of the datacenter where this remote service is assigned.
     * @param ip The IP of the target cloud node.
     * @return The Hypervisor Type.
     * @throws Exception If the hypervisor type information cannot be retrieved.
     */
    @GET
    @Path(ACTION_DISCOVER_HYPERVISOR_TYPE)
    public String getHypervisorType(
        @PathParam(DatacenterResource.DATACENTER) final Integer datacenterId,
        @QueryParam(IP) @NotNull final String ip)
    {
        validatePathParameters(datacenterId);

        try
        {
            return infraService.discoverRemoteHypervisorType(datacenterId, ip).getValue();
        }
        catch (Exception e)
        {
            throw translateException(e);
        }
    }

    /**
     * Returns back the parameters to create a physical machine. It doesn't create it!
     * 
     * @param datacenterId identifier of the datacenter. It is useful to search the uri of the
     *            DiscoveryManager to call.
     * @param ip ip address of the hypevisor
     * @param hypervisorType {@link HypervisorType}
     * @param user user to log in.
     * @param password password to log in.
     * @param port port address to query the request of the hypervisor. Only useful in KVM and XEN
     *            hypervisor types.
     * @param restBuilder injected context REST link builder.
     * @return a MachineDto
     * @throws Exception
     */
    @GET
    @Path(ACTION_DISCOVER_SINGLE)
    public MachineDto discoverSingleMachine(
        @PathParam(DATACENTER) @NotNull @Min(1) final Integer datacenterId,
        @QueryParam(IP) @Ip final String ip,
        @QueryParam(HYPERVISOR) @Hypervisor final String hypervisorType,
        @QueryParam(USER) @NotNull final String user,
        @QueryParam(PASSWORD) @NotNull final String password,
        @QueryParam(PORT) @Port @DefaultValue("8889") final Integer port,
        @Context final IRESTBuilder restBuilder) throws Exception
    {
        Machine machine =
            infraService.discoverRemoteHypervisor(datacenterId, IPAddress.newIPAddress(ip),
                HypervisorType.fromValue(hypervisorType), user, password, port);
        return MachineResource.createTransferObject(machine, restBuilder);
    }

    /**
     * Returns back the list of physical machines that match the request of @hypervisorType, @user
     * and @password inside the values @ipFrom and @ipTo.
     * 
     * @param datacenterId identifier of the datacenter to search the corresponding Discovery
     *            Manager.
     * @param ipFrom first ip to look for
     * @param ipTo last ip to look for
     * @param hypervisorType kind of hypervisor.
     * @param user user to log in
     * @param password password to log in.
     * @param port port address to query the request of the hypervisor. Only useful in KVM and XEN
     *            hypervisor types.
     * @param restBuilder injected context REST link builder.
     * @return a {@link MachinesDto} wrapper object with all the physical machines.
     * @throws Exception
     */
    @GET
    @Path(ACTION_DISCOVER_MULTIPLE)
    public MachinesDto discoverMultipleMachine(
        @PathParam(DATACENTER) @NotNull @Min(1) final Integer datacenterId,
        @QueryParam(IP_FROM) @Ip final String ipFrom, @QueryParam(IP_TO) @Ip final String ipTo,
        @QueryParam(HYPERVISOR) @Hypervisor final String hypervisorType,
        @QueryParam(USER) @NotNull final String user,
        @QueryParam(PASSWORD) @NotNull final String password,
        @QueryParam(PORT) @Port @DefaultValue("8889") final Integer port,
        @QueryParam(VSWITCH) final String vswitch, @Context final IRESTBuilder restBuilder)
        throws Exception
    {
        List<Machine> machines =
            infraService.discoverRemoteHypevisors(datacenterId, IPAddress.newIPAddress(ipFrom),
                IPAddress.newIPAddress(ipTo), HypervisorType.fromValue(hypervisorType), user,
                password, port, vswitch);
        return MachinesResource.transformMachinesDto(restBuilder, machines);
    }

    // no resources response

    public static DatacenterDto addLinks(final IRESTBuilder builder, final DatacenterDto datacenter)
    {
        datacenter.setLinks(builder.buildDatacenterLinks(datacenter));

        return datacenter;
    }

    public static DatacenterDto createTransferObject(final Datacenter datacenter,
        final IRESTBuilder builder) throws Exception
    {
        DatacenterDto dto =
            ModelTransformer.transportFromPersistence(DatacenterDto.class, datacenter);
        dto = addLinks(builder, dto);
        return dto;
    }

    // Create the persistence object.
    public static Datacenter createPersistenceObject(final DatacenterDto datacenter)
        throws Exception
    {
        return ModelTransformer.persistenceFromTransport(Datacenter.class, datacenter);
    }

    // not exposed methods

    /**
     * Translates the Node Collector client exception into a {@link WebApplicationException}.
     * 
     * @param e The Exception to transform.
     * @return The transformed Exception.
     */
    private APIException translateException(final Exception e)
    {
        return new ConflictException(APIError.NODECOLLECTOR_ERROR);
    }

    /**
     * Checks if the remote service is assigned to the specified datacenter.
     * 
     * @param datacenterId The provided ID of the datacenter.
     * @param serviceType The unique type of this remote service.
     * @throws NotFoundException If the remote service is not assigned to the provided datacenter.
     */
    private void validatePathParameters(final Integer datacenterId) throws NotFoundException
    {
        if (!service.isAssignedTo(datacenterId, RemoteServiceType.NODE_COLLECTOR))
        {
            throw new NotFoundException(APIError.NOT_ASSIGNED_REMOTE_SERVICE_DATACENTER);
        }
    }

    private List<RESTLink> buildEnterprisesLinks(final String Path, final PagedList< ? > list,
        final Boolean network, final Integer numElem)
    {
        List<RESTLink> links = new ArrayList<RESTLink>();

        links.add(new RESTLink("first", Path));

        if (list.getCurrentElement() != 0)
        {
            Integer previous = list.getCurrentElement() - list.getPageSize();
            previous = previous < 0 ? 0 : previous;

            links.add(new RESTLink("prev", Path + "?" + NETWORK + "=" + network.toString() + '&'
                + AbstractResource.START_WITH + "=" + previous + '&' + AbstractResource.LIMIT + "="
                + numElem));
        }
        Integer next = list.getCurrentElement() + list.getPageSize();
        if (next < list.getTotalResults())
        {
            links.add(new RESTLink("next", Path + "?" + NETWORK + "=" + network.toString() + '&'
                + AbstractResource.START_WITH + "=" + next + '&' + AbstractResource.LIMIT + "="
                + numElem));
        }

        Integer last = list.getTotalResults() - list.getPageSize();
        if (last < 0)
        {
            last = 0;
        }
        links.add(new RESTLink("last", Path + "?" + NETWORK + "=" + network.toString() + '&'
            + AbstractResource.START_WITH + "=" + last + '&' + AbstractResource.LIMIT + "="
            + numElem));
        return links;
    }

}
