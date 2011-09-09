package com.abiquo.server.core.enterprise;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.abiquo.server.core.common.persistence.DefaultDAOBase;
import com.abiquo.server.core.util.FilterOptions;
import com.abiquo.server.core.util.PagedList;

@Repository("jpaEventDAO")
public class EventDAO extends DefaultDAOBase<Integer, Event>
{
    public EventDAO()
    {
        super(Event.class);
    }

    public EventDAO(final EntityManager entityManager)
    {
        super(Event.class, entityManager);
    }

    public List<Event> getEventByFilter(final FilterOptions filterOptions)
    {
        // TODO : Redo method MeterDAOHibernate.findAllByFilter()

        Query query = getEntityManager().createNamedQuery(Event.EVENT_BY_FILTER);

        // Create Dates
        String fromDateInit = new Timestamp(0).toString();
        String toDateEnd = new Timestamp(new Date().getTime()).toString();

        query.setParameter("timestampInit", fromDateInit);
        query.setParameter("timestampEnd", toDateEnd);
        query.setParameter("enterprise", replaceApostrophe("Abiquo"));

        Integer size = query.getResultList().size();

        query.setFirstResult(filterOptions.getStartwith());
        query.setMaxResults(filterOptions.getLimit());

        PagedList<Event> eventsList = new PagedList<Event>(query.getResultList());
        eventsList.setTotalResults(size);
        eventsList.setPageSize(filterOptions.getLimit() > size ? size : filterOptions.getLimit());
        eventsList.setCurrentElement(filterOptions.getStartwith());

        return eventsList;
    }

    private final String replaceApostrophe(final String name)
    {
        return name.replaceAll("'", "''");
    }
}
