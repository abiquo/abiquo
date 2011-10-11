package com.abiquo.server.core.enterprise;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
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

    @SuppressWarnings("unchecked")
    public List<Event> getEvents(final FilterOptions filterOptions)
    {
        // TODO : Redo method MeterDAOHibernate.findAllByFilter()

        Query query = getEntityManager().createNamedQuery(Event.EVENT_BY_FILTER);

        // Create Dates
        Timestamp fromDateInit = new Timestamp(0);
        Timestamp toDateEnd = new Timestamp(new Date().getTime());

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

    public Event getEventById(final Integer eventId)
    {
        Criteria criteria = createCriteria(Restrictions.eq("id", eventId));
        Object obj = criteria.uniqueResult();
        return (Event) obj;
        // return findUniqueByProperty("id", eventId.toString());
    }

    private final String replaceApostrophe(final String name)
    {
        return name.replaceAll("'", "''");
    }
}
