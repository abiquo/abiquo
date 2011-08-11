package com.abiquo.api.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.abiquo.api.exceptions.APIError;
import com.abiquo.server.core.enterprise.EnterpriseRep;
import com.abiquo.server.core.enterprise.EventDto;
import com.abiquo.server.core.enterprise.User;
import com.abiquo.server.core.util.FilterOptions;

@Service
@Transactional(readOnly = true)
public class EventService extends DefaultApiService
{
    @Autowired
    private UserService userService;

    @Autowired
    private EnterpriseRep enterpriseRep;

    public EventDto getEvent(final Integer eventId)
    {
        EventDto event = new EventDto();

        event = null;
        if (event == null)
        {
            // TODO : change API error to NON_EXISTENT_EVENT
            addNotFoundErrors(APIError.NON_EXISTENT_VOLUME);
            flushErrors();
        }

        return event;
    }

    public List<EventDto> getEvents(final FilterOptions filterOptions)
    {
        List<EventDto> listOfEvents = new ArrayList<EventDto>();

        // List<String> listOfUsers =
        // userService.getUsersByEnterprise(enterpriseId, filterOptions.getFilter(), filterOptions
        // .getOrderBy(), filterOptions.getAsc());

        User currentUser = userService.getCurrentUser();

        return listOfEvents;
    }
}
