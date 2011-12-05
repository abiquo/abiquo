package com.abiquo.server.core.enterprise;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import javax.persistence.EntityManager;

import org.hibernate.Criteria;
import org.hibernate.criterion.MatchMode;
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

    // @SuppressWarnings("unchecked")
    // public List<Event> getEventos(final FilterOptions filterOptions,
    // final HashMap<String, String> filters)
    // {
    // // TODO : Redo method MeterDAOHibernate.findAllByFilter()
    //
    // Query query = getEntityManager().createNamedQuery(Event.EVENT_BY_FILTER);
    //
    // // Add parameters
    // query = setParameters(query, filters);
    //
    // Integer size = query.getResultList().size();
    //
    // query.setFirstResult(filterOptions.getStartwith());
    // query.setMaxResults(filterOptions.getLimit());
    //
    // PagedList<Event> eventsList = new PagedList<Event>(query.getResultList());
    // eventsList.setTotalResults(size);
    // eventsList.setPageSize(filterOptions.getLimit() > size ? size : filterOptions.getLimit());
    // eventsList.setCurrentElement(filterOptions.getStartwith());
    //
    // return eventsList;
    // }

    public List<Event> getEvents(final FilterOptions filterOptions,
        final HashMap<String, String> filters)
    {
        Criteria crit = createCriteria();

        // Create Dates
        Timestamp dateFrom =
            filters.containsKey("datefrom") && !"%".equals(filters.get("datefrom")) ? convertDate(
                filters.get("datefrom"), 0) : new Timestamp(0);

        Timestamp dateTo =
            filters.containsKey("dateto") && !"%".equals(filters.get("dateto")) ? convertDate(
                filters.get("dateto"), 86400) : new Timestamp(new Date().getTime());

        crit.add(Restrictions.between(Event.TIMESTAMP_PROPERTY, dateFrom, dateTo));

        // Remove the dates filters from the Map to avoid adding them again later
        filters.remove("datefrom");
        filters.remove("dateto");

        // Add all others filters
        for (Entry<String, String> filter : filters.entrySet())
        {
            if (!filter.getValue().equals("%"))
            {
                crit.add(Restrictions.like(filter.getKey(), replaceApostrophe(filter.getValue()),
                    MatchMode.ANYWHERE));
            }
        }

        Integer size = crit.list().size();

        crit.setFirstResult(filterOptions.getStartwith());
        crit.setMaxResults(filterOptions.getLimit());

        PagedList<Event> eventsList = new PagedList<Event>(crit.list());
        eventsList.setTotalResults(size);
        eventsList.setPageSize(filterOptions.getLimit() > size ? size : filterOptions.getLimit());
        eventsList.setCurrentElement(filterOptions.getStartwith());

        return getResultList(crit);
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
