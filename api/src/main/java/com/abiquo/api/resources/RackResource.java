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

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;

import org.apache.wink.common.annotations.Parent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.abiquo.api.exceptions.APIError;
import com.abiquo.api.exceptions.BadRequestException;
import com.abiquo.api.services.InfrastructureService;
import com.abiquo.api.util.IRESTBuilder;
import com.abiquo.model.util.ModelTransformer;
import com.abiquo.server.core.infrastructure.Rack;
import com.abiquo.server.core.infrastructure.RackDto;

/*
 *  THIS CLASS RESOURCE IS USED AS THE DEFAULT ONE TO DEVELOP THE REST AND 
 *  FOR THIS REASON IS OVER-COMMENTED AND DOESN'T HAVE JAVADOC! PLEASE DON'T COPY-PASTE ALL OF THIS
 *  COMMENTS BECAUSE IS WILL BE SO UGLY TO MAINTAIN THE CODE IN THE API!
 *
 */
@Parent(RacksResource.class)
@Path(RackResource.RACK_PARAM)
@Controller
public class RackResource extends AbstractResource
{
    // Define the static variables that represent the URI and the PARAM.
    public static final String RACK = "rack";

    public static final String RACK_PARAM = "{" + RACK + "}";

    public static final String RACK_ACTION_LOGICSERVERS_ASSOCIATE = "logicservers/associate";

    public static final String RACK_ACTION_LOGICSERVERS_DISSOCIATE = "logicservers/dissociate";

    public static final String RACK_ACTION_LOGICSERVERS = "logicservers";

    public static final String RACK_ACTION_LOGICSERVERS_DELETE = "logicservers/delete";

    public static final String RACK_ACTION_LOGICSERVERS_CLONE = "logicservers/clone";

    public static final String RACK_ACTION_ORGANIZATIONS = "organizations";

    public static final String RACK_ACTION_FSM = "fsm";

    public static final String RACK_ACTION_LOGICSERVERS_TEMPLATES = "lssemplates";

    public static final String RACK_ACTION_LOGICSERVERS_ASSOCIATE_TEMPLATE =
        "logicservers/assoctemplate";

    public static final String RACK_ACTION_LOGICSERVERS_ASSOCIATE_CLONE = "logicservers/assocclone";

    public static final String RACK_ACTION_LOGICSERVERS_ASSOCIATE_REL = "ls-associate";

    public static final String RACK_ACTION_LOGICSERVERS_DISSOCIATE_REL = "ls-dissociate";

    public static final String RACK_ACTION_LOGICSERVERS_REL = "logicservers";

    public static final String RACK_ACTION_LOGICSERVERS_DELETE_REL = "ls-delete";

    public static final String RACK_ACTION_LOGICSERVERS_CLONE_REL = "ls-clone";

    public static final String RACK_ACTION_ORGANIZATIONS_REL = "organizations";

    public static final String RACK_ACTION_LOGICSERVERS_TEMPLATES_REL = "ls-templates";

    public static final String RACK_ACTION_FSM_REL = "fsm";

    public static final String RACK_ACTION_LOGICSERVERS_ASSOCIATE_TEMPLATE_REL =
        "ls-associatesemplate";

    public static final String RACK_ACTION_LOGICSERVERS_ASSOCIATE_CLONE_REL = "ls-associateslone";

    // Define its service. It should only have ONE
    @Autowired
    private InfrastructureService service;

    // Get the Rack. Please note the method annotations to check the parameters can not be null
    // nor lesser than 1. You don't have to do anything with it. Only declare it. A custom handler
    // previous to this call is the responsible of manage it.
    @GET
    public RackDto getRack(
        @PathParam(DatacenterResource.DATACENTER) @NotNull @Min(1) final Integer datacenterId,
        @PathParam(RACK) @NotNull @Min(1) final Integer rackId,
        @Context final IRESTBuilder restBuilder) throws Exception
    {
        // Receive the Rack and convert it as RackDto in the 'createTransferObject' method. That's
        // enough!
        Rack rack = service.getRack(datacenterId, rackId);
        return createTransferObject(rack, restBuilder);
    }

    // Modify the Rack. Please note the method annotations to check the parameters can not be null
    // nor lesser than 1. You don't have to do anything with it. Only declare it. A custom handler
    // previous to this call is the responsible of manage it. Please note the entity Rack does not
    // have any constraint. Constraints inside the entity are checked later.
    @PUT
    public RackDto modifyRack(
        @PathParam(DatacenterResource.DATACENTER) @NotNull @Min(1) final Integer datacenterId,
        @PathParam(RACK) @NotNull @Min(1) final Integer rackId, final RackDto rackDto,
        @Context final IRESTBuilder restBuilder) throws Exception
    {
        // Check the parameter id of the rack has the same id than the rackId.
        if (!rackDto.getId().equals(rackId))
        {
            // Throw a BadRequestException!. Please, when you add a new APIError, execute the main
            // process in the class {@link APIError} and copy-paste the output at the wiki page:
            // http://wiki.abiquo.com/display/ABI{XX}/API+Error+Code+List where XX is the current
            // confluence space.
            throw new BadRequestException(APIError.INCOHERENT_IDS);
        }

        // Create the peristence object from the Dto, pass it to modify, and return back the
        // modified entity. That's important for one reason: imagine an entity that doesn't allow to
        // modify all of its attributes.
        // we need to create a feedback to tell the API Client that some fields have not been
        // changed even the response is 200OK. So we don't return the same entity that we have
        // received.
        Rack rack = createPersistenceObject(rackDto);
        // Pass the whole hierarchy ids at the service!
        rack = service.modifyRack(datacenterId, rackId, rack);
        return createTransferObject(rack, restBuilder);
    }

    // Get the Rack. Please note the method annotations to check the parameters can not be null
    // nor lesser than 1. You don't have to do anything with it. Only declare it. A custom handler
    // previous to this call is the responsible of manage it.
    @DELETE
    public void deleteRack(
        @PathParam(DatacenterResource.DATACENTER) @NotNull @Min(1) final Integer datacenterId,
        @PathParam(RACK) @NotNull @Min(1) final Integer rackId,
        @QueryParam("force") final boolean force)
    {
        // Pass the whole hierarchy ids at the service to retive the rack and remove it.
        Rack rack = service.getRack(datacenterId, rackId);
        service.removeRack(rack, force);
    }

    // Create the transfer object. ModelTransformer do the dirty work. You should only
    // create custom links depending on the entity.
    public static RackDto createTransferObject(final Rack rack, final IRESTBuilder restBuilder)
        throws Exception
    {
        RackDto dto = ModelTransformer.transportFromPersistence(RackDto.class, rack);
        // Add the links.
        dto.addLinks(restBuilder.buildRackLinks(rack.getDatacenter().getId(), dto));
        return dto;
    }

    // Create the persistence object.
    public static Rack createPersistenceObject(final RackDto rack) throws Exception
    {
        return ModelTransformer.persistenceFromTransport(Rack.class, rack);
    }
}
