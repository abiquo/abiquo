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
 * abiCloud  community version
 * cloud management application for hybrid clouds
 * Copyright (C) 2008-2010 - Soluciones Grid SL
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

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.abiquo.server.core.common.persistence.DefaultDAOBase;

@Repository("jpaDatastoreDAO")
public class DatastoreDAO extends DefaultDAOBase<Integer, Datastore>
{
    public DatastoreDAO()
    {
        super(Datastore.class);
    }

    public DatastoreDAO(EntityManager entityManager)
    {
        super(Datastore.class, entityManager);
    }

    public List<Datastore> findMachineDatastores(Machine machine)
    {
        assert machine != null;
        assert isManaged2(machine);

        Criteria criteria =
            createCriteria().createCriteria(Datastore.MACHINES_PROPERTY).add(
                Restrictions.in(Machine.ID_PROPERTY, new Integer[] {machine.getId()}));

        criteria.addOrder(Order.asc(Datastore.NAME_PROPERTY));
        List<Datastore> result = getResultList(criteria);
        return result;
    }

    private static Criterion equalName(String name)
    {
        assert !StringUtils.isEmpty(name);

        return Restrictions.eq(Datastore.NAME_PROPERTY, name);
    }

    public boolean existsAnyWithName(String name)
    {
        assert !StringUtils.isEmpty(name);

        return this.existsAnyByCriterions(equalName(name));
    }

    public boolean existsAnyOtherWithName(Datastore entity, String name)
    {
        assert entity != null;
        assert isManaged(entity);
        assert !StringUtils.isEmpty(name);

        return this.existsAnyOtherByCriterions(entity, equalName(name));
    }

    private static Criterion equalDirectory(String directory)
    {
        assert !StringUtils.isEmpty(directory);

        return Restrictions.eq(Datastore.DIRECTORY_PROPERTY, directory);
    }

    public boolean existsAnyWithDirectory(String directory)
    {
        assert !StringUtils.isEmpty(directory);

        return this.existsAnyByCriterions(equalDirectory(directory));
    }

    public boolean existsAnyOtherWithDirectory(Datastore entity, String directory)
    {
        assert entity != null;
        assert isManaged(entity);
        assert !StringUtils.isEmpty(directory);

        return this.existsAnyOtherByCriterions(entity, equalDirectory(directory));
    }
}
