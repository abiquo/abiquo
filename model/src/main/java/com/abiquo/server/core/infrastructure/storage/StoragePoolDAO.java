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
package com.abiquo.server.core.infrastructure.storage;

import java.util.List;

import javax.persistence.EntityManager;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.abiquo.server.core.common.persistence.DefaultDAOBase;
import com.abiquo.server.core.infrastructure.Datacenter;
import com.abiquo.server.core.infrastructure.RemoteService;

@Repository("jpaStorgePoolDAO")
public class StoragePoolDAO extends DefaultDAOBase<String, StoragePool>
{
    public StoragePoolDAO()
    {
        super(StoragePool.class);
    }

    public StoragePoolDAO(EntityManager entityManager)
    {
        super(StoragePool.class, entityManager);
    }

    public void updateStoragePool(StoragePool pool)
    {
        flush(); // XXX: bzngine does it like this!
    }

    public List<StoragePool> findByDatacenter(Integer datacenterId)
    {
        Order order = Order.asc(StoragePool.NAME_PROPERTY);
        Criteria criteria = equalsDatacenter(datacenterId, order);
        return getResultList(criteria);
    }

    public List<StoragePool> findByRemoteService(Integer remoteServiceId)
    {
        Order order = Order.asc(StoragePool.NAME_PROPERTY);
        Criteria criteria = equalsRemoteService(remoteServiceId, order);
        return getResultList(criteria);
    }

    private Criteria equalsDatacenter(Integer datacenterId, Order order)
    {
        Criteria crit =
            createNestedCriteria(order, StoragePool.REMOTE_SERVICE_PROPERTY,
                RemoteService.DATACENTER_PROPERTY);
        crit.add(Restrictions.eq(Datacenter.ID_PROPERTY, datacenterId));
        return crit;
    }

    private Criteria equalsRemoteService(Integer remoteServiceId, Order order)
    {
        Criteria crit = createNestedCriteria(order, StoragePool.REMOTE_SERVICE_PROPERTY);
        crit.add(Restrictions.eq(RemoteService.ID_PROPERTY, remoteServiceId));
        return crit;
    }
}
