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

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.wink.common.annotations.Parent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.abiquo.api.exceptions.APIError;
import com.abiquo.api.exceptions.NotFoundException;
import com.abiquo.api.services.EventService;
import com.abiquo.api.util.IRESTBuilder;
import com.abiquo.model.util.ModelTransformer;
import com.abiquo.server.core.enterprise.Event;
import com.abiquo.server.core.enterprise.EventDto;

/**
 * @author vmahe
 */
@Parent(EventsResource.class)
@Path(EventResource.EVENT_PARAM)
@Controller
public class EventResource extends AbstractResource
{
    public static final String EVENT = "event";

    public static final String EVENT_PARAM = "{" + EVENT + "}";

    @Autowired
    private EventService eventService;

    @GET
    @Produces(MediaType.APPLICATION_XML)
    public EventDto getEvent(@PathParam(EVENT) final Integer eventId,
        @Context final IRESTBuilder restBuilder) throws Exception
    {
        Event event = eventService.getEvent(eventId);
        if (event == null)
        {
            throw new NotFoundException(APIError.NON_EXISTENT_EVENT);
        }

        return createTransferObject(event, restBuilder);
    }

    public static EventDto createTransferObject(final Event event, final IRESTBuilder restBuilder)
        throws Exception
    {
        EventDto dto = ModelTransformer.transportFromPersistence(EventDto.class, event);

        // dto.setLinks(restBuilder.buildVolumeCloudLinks(event));
        // dto.addLinks(restBuilder.buildRasdLinks(event));

        return dto;
    }
}
