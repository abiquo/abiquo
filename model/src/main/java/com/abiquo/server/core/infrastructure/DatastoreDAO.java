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

package com.abiquo.server.core.infrastructure;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.abiquo.server.core.common.persistence.DefaultDAOBase;
import com.softwarementors.bzngine.entities.PersistentEntity;

@Repository("jpaDatastoreDAO")
public class DatastoreDAO extends DefaultDAOBase<Integer, Datastore>
{
    public DatastoreDAO()
    {
        super(Datastore.class);
    }

    public DatastoreDAO(final EntityManager entityManager)
    {
        super(Datastore.class, entityManager);
    }

    public List<Datastore> findMachineDatastores(final Machine machine)
    {
        assert machine != null;
        assert isManaged2(machine);

        Criteria criteria = inMachine(machine);
        // createCriteria().createCriteria(Datastore.MACHINES_PROPERTY).add(
        // Restrictions.in(Machine.ID_PROPERTY, new Integer[] {machine.getId()}));

        criteria.addOrder(Order.asc(Datastore.NAME_PROPERTY));
        List<Datastore> result = getResultList(criteria);
        return result;
    }

    /**
     * Use the ''datastoreUUID'' property in order to return all the representations of a shared
     * datastore.
     * 
     * @return all the datastores with the same ''datastore uuid'' INCLUDING the provided datastore.
     */
    public List<Datastore> findShares(final Datastore datastore)
    {
        if (datastore.getDatastoreUUID() == null)
        {
            throw new PersistenceException("Datastore doesn't have set the UUID");
        }

        Criteria crit = createCriteria(sharedUuid(datastore.getDatastoreUUID()));

        return getResultList(crit);
    }

    /***
     * Get the datastore with the provided UUID mounted on the target machine. (this call is
     * expected after ''datastoreSelection'' so the target machine will have the datastore)
     */
    public Datastore findDatastore(final String uuid, final Machine machine)
    {
        Criteria criteria = inMachine(machine, uuid);
        return getSingleResult(criteria);
    }

    private Criteria inMachine(final Machine machine)
    {
        return createCriteria().createCriteria(Datastore.MACHINES_PROPERTY).add(
            Restrictions.in(PersistentEntity.ID_PROPERTY, new Integer[] {machine.getId()}));

    }

    private Criteria inMachine(final Machine machine, final String uuid)
    {
        return createCriteria(sharedUuid(uuid)).createCriteria(Datastore.MACHINES_PROPERTY).add(
            Restrictions.in(PersistentEntity.ID_PROPERTY, new Integer[] {machine.getId()}));

    }

    private static Criterion sharedUuid(final String uuid)
    {
        return Restrictions.eq(Datastore.DATASTORE_UUID_PROPERTY, uuid);
    }

    private static Criterion equalName(final String name)
    {
        assert !StringUtils.isEmpty(name);

        return Restrictions.eq(Datastore.NAME_PROPERTY, name);
    }

    public boolean existsAnyWithName(final String name)
    {
        assert !StringUtils.isEmpty(name);

        return this.existsAnyByCriterions(equalName(name));
    }

    public boolean existsAnyOtherWithName(final Datastore entity, final String name)
    {
        assert entity != null;
        assert isManaged(entity);
        assert !StringUtils.isEmpty(name);

        return this.existsAnyOtherByCriterions(entity, equalName(name));
    }

    private static Criterion equalDirectory(final String directory)
    {
        assert !StringUtils.isEmpty(directory);

        return Restrictions.eq(Datastore.DIRECTORY_PROPERTY, directory);
    }

    public boolean existsAnyWithDirectory(final String directory)
    {
        assert !StringUtils.isEmpty(directory);

        return this.existsAnyByCriterions(equalDirectory(directory));
    }

    public boolean existsAnyOtherWithDirectory(final Datastore entity, final String directory)
    {
        assert entity != null;
        assert isManaged(entity);
        assert !StringUtils.isEmpty(directory);

        return this.existsAnyOtherByCriterions(entity, equalDirectory(directory));
    }
}
