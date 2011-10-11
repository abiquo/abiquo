package com.abiquo.server.core.enterprise;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

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
    public List<Event> getEvents(final FilterOptions filterOptions,
        final HashMap<String, String> filters)
    {
        // TODO : Redo method MeterDAOHibernate.findAllByFilter()

        Query query = getEntityManager().createNamedQuery(Event.EVENT_BY_FILTER);

        // Add parameters
        query = setParameters(query, filters);

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
    }

    private Query setParameters(final Query query, final HashMap<String, String> filters)
    {
        Query queryWithParams = query;

        // Create Dates
        Timestamp dateFrom =
            filters.containsKey("datefrom") && !"%".equals(filters.get("datefrom")) ? convertDate(
                filters.get("datefrom"), 0) : new Timestamp(0);

        Timestamp dateTo =
            filters.containsKey("dateto") && !"%".equals(filters.get("dateto")) ? convertDate(
                filters.get("dateto"), 86400) : new Timestamp(new Date().getTime());

        queryWithParams.setParameter("datefrom", dateFrom);
        queryWithParams.setParameter("dateto", dateTo);

        filters.remove("datefrom");
        filters.remove("dateto");

        for (Entry<String, String> filter : filters.entrySet())
        {
            queryWithParams.setParameter(filter.getKey(), replaceApostrophe(filter.getValue()));
        }

        return queryWithParams;
    }

    private final String replaceApostrophe(final String name)
    {
        return name.replaceAll("'", "''");
    }

    private Timestamp convertDate(final String date, final Integer sec)
    {
        return new Timestamp(Long.valueOf(date) * sec);
    }
}
