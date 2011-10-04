/**
 * Abiquo community edition
 * cloud management application for hybrid clouds
 * Copyright (C) 2008-2010 - Abiquo Holdings S.L.
 *
 * This application is free software; you can redistribute it and/or
 * modify it under the terms of the GNU LESSER GENERAL PUBLIC
 * LICENSE as published by the Free Software Foundation under
 * version 3 of the License
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * LESSER GENERAL PUBLIC LICENSE v.3 for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 */

/**
 * 
 */
package com.abiquo.abiserver.persistence.hibernate;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import com.abiquo.abiserver.exception.PersistenceException;
import com.abiquo.abiserver.persistence.DAO;

/**
 * This class implements the Crudable interface with all the common methods implemented by
 * {@link GenericHibernateDAO} but without session and transaction managing, that will be managed by
 * the HibernateDAOfactory
 * 
 * @author jdevesa@abiquo.com
 */
public class HibernateDAO<T, ID extends Serializable> implements DAO<T, ID>
{
    /**
     * Class to work with
     */
    protected Class<T> persistentClass;

    /**
     * Hibernate session
     */
    private Session session;

    @SuppressWarnings("unchecked")
    @Override
    public List<T> findAll() throws PersistenceException
    {
        Criteria criteria = getSession().createCriteria(getPersistentClass());
        return criteria.list();
    }

    @SuppressWarnings("unchecked")
    @Override
    public T findById(final ID id)
    {
        return (T) getSession().get(getPersistentClass(), id);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<T> findByNamedQuery(final String name)
    {
        Query query = getSession().getNamedQuery(name);
        return query.list();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<T> findByNamedQuery(final String name, final Map<String, ? > params)
    {
        Query query = getSession().getNamedQuery(name);

        if (params != null)
        {
            for (Map.Entry<String, ? > param : params.entrySet())
            {
                if (param.getValue() instanceof Collection)
                {
                    query.setParameterList(param.getKey(), (Collection) param.getValue());
                }
                else
                {
                    query.setParameter(param.getKey(), param.getValue());
                }
            }
        }

        return query.list();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<T> findByNamedQuery(final String name, final Object... params)
    {
        Query query = getSession().getNamedQuery(name);

        if (params != null)
        {
            int i = 0;
            for (Object param : params)
            {
                query.setParameter(i++, param);
            }
        }

        return query.list();
    }

    @SuppressWarnings("unchecked")
    @Override
    public T findUniqueByNamedQuery(final String name)
    {
        Query query = getSession().getNamedQuery(name);
        return (T) query.uniqueResult();
    }

    @SuppressWarnings("unchecked")
    @Override
    public T findUniqueByNamedQuery(final String name, final Map<String, ? > params)
    {
        Query query = getSession().getNamedQuery(name);

        if (params != null)
        {
            for (Map.Entry<String, ? > param : params.entrySet())
            {
                if (param.getValue() instanceof Collection)
                {
                    query.setParameterList(param.getKey(), (Collection) param.getValue());
                }
                else
                {
                    query.setParameter(param.getKey(), param.getValue());
                }
            }
        }

        return (T) query.uniqueResult();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<T> findByProperty(final String property, final Object value)
    {
        Criteria c = getSession().createCriteria(persistentClass);
        c.add(Restrictions.eq(property, value));
        return c.list();
    }

    @SuppressWarnings("unchecked")
    @Override
    public T findUniqueByProperty(final String property, final Object value)
    {
        Criteria c = getSession().createCriteria(persistentClass);
        c.add(Restrictions.eq(property, value));
        return (T) c.uniqueResult();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<T> findByProperties(final String[] properties, final Object[] values)
    {
        Criteria c = getSession().createCriteria(persistentClass);

        for (int i = 0; i < properties.length; i++)
        {
            c.add(Restrictions.eq(properties[i], values[i]));
        }

        return c.list();
    }

    @SuppressWarnings("unchecked")
    @Override
    public T findUniqueByProperties(final String[] properties, final Object[] values)
    {
        Criteria c = getSession().createCriteria(persistentClass);

        for (int i = 0; i < properties.length; i++)
        {
            c.add(Restrictions.eq(properties[i], values[i]));
        }

        return (T) c.uniqueResult();
    }

    @Override
    public T makePersistent(final T entity) throws PersistenceException
    {
        getSession().saveOrUpdate(entity);
        return entity;

    }

    @Override
    public T makePersistent(final String entityName, final T entity) throws PersistenceException
    {
        getSession().saveOrUpdate(entityName, entity);
        return entity;

    }

    @Override
    public void makeTransient(final T entity) throws PersistenceException
    {
        getSession().delete(entity);
    }

    @Override
    public void makeTransient(final String entityName, final T entity) throws PersistenceException
    {
        getSession().delete(entityName, entity);
    }

    /**
     * @return the persistentClass
     */
    public Class<T> getPersistentClass()
    {
        return persistentClass;
    }

    /**
     * @return the session
     */
    public Session getSession()
    {
        return session;
    }

    /**
     * @param persistentClass the persistentClass to set
     */
    public void setPersistentClass(final Class<T> persistentClass)
    {
        this.persistentClass = persistentClass;
    }

    /**
     * @param session the session to set
     */
    public void setSession(final Session session)
    {
        this.session = session;
    }

    /**
     * @see com.abiquo.abiserver.persistence.Crudable#merge(java.lang.Object)
     * @see org.hibernate.Session#merge(Object)
     */
    @Override
    public T merge(final T entity) throws PersistenceException
    {
        return (T) getSession().merge(entity);
    }

    /**
     * @see com.abiquo.abiserver.persistence.Crudable#merge(java.lang.String, java.lang.Object)
     * @see org.hibernate.Session#merge(String, Object)
     */
    @Override
    public T merge(final String entityName, final T entity) throws PersistenceException
    {
        return (T) getSession().merge(entityName, entity);
    }

    /**
     * @see com.abiquo.abiserver.persistence.Crudable#refresh(java.lang.Object)
     * @see org.hibernate.Session#refresh(Object)
     */
    @Override
    public void refresh(final T entity) throws PersistenceException
    {
        getSession().refresh(entity);
    }
}
