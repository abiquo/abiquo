package com.abiquo.api.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.abiquo.api.exceptions.APIError;
import com.abiquo.server.core.enterprise.EventDto;
import com.abiquo.server.core.enterprise.UserRep;

@Service
@Transactional(readOnly = true)
public class EventService extends DefaultApiService
{

    @Autowired
    private UserRep userRepo;

    public EventDto getEvent(final Integer eventId)
    {
        EventDto event = new EventDto();

        if (event == null)
        {
            // TODO : change API error to NON_EXISTENT_EVENT
            addNotFoundErrors(APIError.NON_EXISTENT_VOLUME);
            flushErrors();
        }

        return event;
    }

    public List<EventDto> getEvents(final Integer numrows)
    {
        List<EventDto> listOfEvents = new ArrayList<EventDto>();
        // UserDAO userDAO = factory.getUserDAO();
        // MeterDAO meterDAO = factory.getMeterDAO();

        // try
        // {
        // We split all the users inside the string separated by "/";
        List<String> listOfUsers = new ArrayList<String>();

        // UsersResourceStub proxy =
        // APIStubFactory.getInstance(userSession, new UsersResourceStubImpl(),
        // UsersResourceStub.class);
        // DataResult<UserListResult> users = proxy.getOnlyUsers(new UserListOptions());
        //
        // if (users.getData() != null && users.getData().getUsersList() != null
        // && !users.getData().getUsersList().isEmpty())
        // {
        // for (User u : users.getData().getUsersList())
        // {
        // listOfUsers.add(u.getUser());
        //
        // }
        // }
        //
        // User user = userRepo.findUserByNameAndAuth(login, authType);
        // if (user == null)
        // {
        // addNotFoundErrors(APIError.NON_EXISTENT_VIRTUAL_DATACENTER);
        // flushErrors();
        // }
        //
        // // UserHB user =
        // // userDAO.findUserHBById(userSession.getUserIdDb(), userSession.getAuthType());
        //
        // User userList = userRepo.findUserByNameAndAuth(login, authType);
        // if (userList == null)
        // {
        // addNotFoundErrors(APIError.NON_EXISTENT_VIRTUAL_DATACENTER);
        // flushErrors();
        // }
        //
        // return repo.getVolumesByVirtualDatacenter(virtualDatacenter, filterOptions);

        // listOfUsers = meterDAO.findAllByFilter(filters, listOfUsers, numrows,
        // user.getRoleHB());
        // }

        // catch (PersistenceException e)
        // {
        // throw new MeterCommandException(e.getMessage(), e);
        // }

        return listOfEvents;
    }
}
