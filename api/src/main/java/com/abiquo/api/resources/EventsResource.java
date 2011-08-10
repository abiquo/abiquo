package com.abiquo.api.resources;

import java.util.List;

import javax.validation.constraints.Min;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.wink.common.annotations.Workspace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.abiquo.api.services.EventService;
import com.abiquo.api.util.IRESTBuilder;
import com.abiquo.server.core.enterprise.EventDto;
import com.abiquo.server.core.enterprise.EventsDto;

@Path(EventsResource.EVENTS_PATH)
@Controller
@Workspace(workspaceTitle = "Abiquo administration workspace", collectionTitle = "Events")
public class EventsResource extends AbstractResource
{
    private static final Logger LOGGER = LoggerFactory.getLogger(EventsResource.class);

    public static final String EVENTS_PATH = "admin/events";

    @Autowired
    private EventService eventService;

    @GET
    @Produces(MediaType.APPLICATION_XML)
    public EventsDto getEvents(
        @QueryParam(LIMIT) @DefaultValue(DEFAULT_PAGE_LENGTH_STRING) @Min(0) final Integer limit,
        @Context final IRESTBuilder restBuilder) throws Exception
    {
        List<EventDto> events = eventService.getEvents(40);

        EventsDto transferEvents = new EventsDto();
        if (events != null)
        {
            for (EventDto currentEventDto : events)
            {
                transferEvents.getCollection().add(currentEventDto);
            }
        }

        return transferEvents;
    }
}
