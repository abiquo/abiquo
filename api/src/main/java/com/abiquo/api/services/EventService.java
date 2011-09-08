package com.abiquo.api.services;

import java.util.ArrayList;
import java.util.Collection;
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
            addNotFoundErrors(APIError.NON_EXISTENT_EVENT);
            flushErrors();
        }

        return event;
    }

    public List<EventDto> getEvents(final FilterOptions filterOptions)
    {
        List<EventDto> listOfEvents = new ArrayList<EventDto>();

        User currentUser = userService.getCurrentUser();

        Collection<User> listOfUsers =
            userService.getUsersByEnterprise(currentUser.getEnterprise().getId().toString(),
                filterOptions.getFilter(), filterOptions.getOrderBy(), filterOptions.getAsc());

        System.out.println(currentUser.getName());

        String listEvents = userService.getEvents();

        return listOfEvents;
    }
}
