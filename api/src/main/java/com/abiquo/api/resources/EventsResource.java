package com.abiquo.api.resources;

import java.util.HashMap;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.abiquo.api.services.EventService;
import com.abiquo.api.util.IRESTBuilder;
import com.abiquo.server.core.enterprise.Event;
import com.abiquo.server.core.enterprise.EventsDto;
import com.abiquo.server.core.util.FilterOptions;

/**
 * @author vmahe
 */
@Path(EventsResource.EVENTS_PATH)
@Controller
@Workspace(workspaceTitle = "Abiquo administration workspace", collectionTitle = "Events")
public class EventsResource extends AbstractResource
{
    public static final String EVENTS_PATH = "admin/events";

    @Autowired
    private EventService eventService;

    @GET
    @Produces(MediaType.APPLICATION_XML)
    public EventsDto getEvents(
        @QueryParam(START_WITH) @DefaultValue("0") @Min(0) final Integer startwith,
        @QueryParam(BY) @DefaultValue("id") final String orderBy,
        @QueryParam(FILTER) @DefaultValue("") final String filter,
        @QueryParam(LIMIT) @DefaultValue(DEFAULT_PAGE_LENGTH_STRING) @Min(0) final Integer limit,
        @QueryParam(ASC) @DefaultValue("true") final Boolean desc_or_asc,
        @QueryParam(EventsFilters.DATACENTER) @DefaultValue("%") final String datacenter,
        @QueryParam(EventsFilters.RACK) @DefaultValue("%") final String rack,
        @QueryParam(EventsFilters.PHYSICAL_MACHINE) @DefaultValue("%") final String physicalmachine,
        @QueryParam(EventsFilters.STORAGE_SYSTEM) @DefaultValue("%") final String storagesystem,
        @QueryParam(EventsFilters.STORAGE_POOL) @DefaultValue("%") final String storagepool,
        @QueryParam(EventsFilters.VOLUME) @DefaultValue("%") final String volume,
        @QueryParam(EventsFilters.NETWORK) @DefaultValue("%") final String network,
        @QueryParam(EventsFilters.SUBNET) @DefaultValue("%") final String subnet,
        @QueryParam(EventsFilters.ENTERPRISE) @DefaultValue("%") final String enterprise,
        @QueryParam(EventsFilters.USER) @DefaultValue("%") final String user,
        @QueryParam(EventsFilters.VIRTUAL_DATACENTER) @DefaultValue("%") final String virtualdatacenter,
        @QueryParam(EventsFilters.VIRTUALAPP) @DefaultValue("%") final String virtualapp,
        @QueryParam(EventsFilters.VIRTUAL_MACHINE) @DefaultValue("%") final String virtualmachine,
        @QueryParam(EventsFilters.SEVERITY) @DefaultValue("%") final String severity,
        @QueryParam(EventsFilters.PERFORMED_BY) @DefaultValue("%") final String performedby,
        @QueryParam(EventsFilters.ACTION_PERFORMED) @DefaultValue("%") final String actionperformed,
        @QueryParam(EventsFilters.COMPONENT) @DefaultValue("%") final String component,
        @QueryParam(EventsFilters.DATE_FROM) @DefaultValue("%") final String datefrom,
        @QueryParam(EventsFilters.DATE_TO) @DefaultValue("%") final String dateto,
        @Context final IRESTBuilder restBuilder) throws Exception
    {
        FilterOptions filterOptions =
            new FilterOptions(startwith, limit, filter, orderBy, desc_or_asc);

        HashMap<String, String> filters = new HashMap<String, String>();
        filters.put(EventsFilters.DATACENTER, datacenter);
        filters.put(EventsFilters.RACK, rack);
        filters.put(EventsFilters.PHYSICAL_MACHINE, physicalmachine);
        filters.put(EventsFilters.STORAGE_SYSTEM, storagesystem);
        filters.put(EventsFilters.STORAGE_POOL, storagepool);
        filters.put(EventsFilters.VOLUME, volume);
        filters.put(EventsFilters.NETWORK, network);
        filters.put(EventsFilters.SUBNET, subnet);
        filters.put(EventsFilters.ENTERPRISE, enterprise);
        filters.put(EventsFilters.USER, user);
        filters.put(EventsFilters.VIRTUAL_DATACENTER, virtualdatacenter);
        filters.put(EventsFilters.VIRTUALAPP, virtualapp);
        filters.put(EventsFilters.VIRTUAL_MACHINE, virtualmachine);
        filters.put(EventsFilters.SEVERITY, severity);
        filters.put(EventsFilters.PERFORMED_BY, performedby);
        filters.put(EventsFilters.ACTION_PERFORMED, actionperformed);
        filters.put(EventsFilters.COMPONENT, component);
        filters.put(EventsFilters.DATE_FROM, datefrom);
        filters.put(EventsFilters.DATE_TO, dateto);

        List<Event> events = eventService.getEvents(filterOptions, filters);

        EventsDto transferEvents = new EventsDto();
        if (events != null)
        {
            for (Event currentEvent : events)
            {
                transferEvents.getCollection().add(
                    EventResource.createTransferObject(currentEvent, restBuilder));
            }
        }

        return transferEvents;
    }

    private class EventsFilters
    {
        public static final String DATACENTER = "datacenter";

        public static final String RACK = "rack";

        public static final String PHYSICAL_MACHINE = "physicalmachine";

        public static final String STORAGE_SYSTEM = "storagesystem";

        public static final String STORAGE_POOL = "storagepool";

        public static final String VOLUME = "volume";

        public static final String NETWORK = "network";

        public static final String SUBNET = "subnet";

        public static final String ENTERPRISE = "enterprise";

        public static final String USER = "user";

        public static final String VIRTUAL_DATACENTER = "virtualdatacenter";

        public static final String VIRTUALAPP = "virtualapp";

        public static final String VIRTUAL_MACHINE = "virtualmachine";

        public static final String SEVERITY = "severity";

        public static final String PERFORMED_BY = "performedby";

        public static final String ACTION_PERFORMED = "actionperformed";

        public static final String COMPONENT = "component";

        public static final String DATE_FROM = "datefrom";

        public static final String DATE_TO = "dateto";
    }
}
