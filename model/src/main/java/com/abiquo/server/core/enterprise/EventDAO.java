package com.abiquo.server.core.enterprise;

import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.stereotype.Repository;

import com.abiquo.server.core.common.persistence.DefaultDAOBase;

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

    @Override
    public List<Event> findAll()
    {
        // TODO : Redo method MeterDAOHibernate.findAllByFilter()

        return null;
    }
}
