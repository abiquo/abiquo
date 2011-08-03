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
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.abiquo.server.core.common.persistence.DefaultDAOBase;

@Repository("jpaStoragePoolDAO")
@SuppressWarnings("unchecked")
/* package */class StoragePoolDAO extends DefaultDAOBase<String, StoragePool>
{
    public StoragePoolDAO()
    {
        super(StoragePool.class);
    }

    public StoragePoolDAO(final EntityManager entityManager)
    {
        super(StoragePool.class, entityManager);
    }

    public List<StoragePool> getPoolsByStorageDevice(final Integer deviceId)
    {
        Criteria criteria = createCriteria(Restrictions.eq("device.id", deviceId));
        return criteria.list();
    }

    public StoragePool findPoolById(final Integer deviceId, final String poolId)
    {
        Criteria criteria =
            createCriteria(Restrictions.eq("device.id", deviceId)).add(
                Restrictions.eq("idStorage", poolId));
        Object obj = criteria.uniqueResult();
        return (StoragePool) obj;
    }

    public StoragePool findPoolByName(final Integer deviceId, final String name)
    {
        Criteria criteria =
            createCriteria(Restrictions.eq("device.id", deviceId)).add(
                Restrictions.eq("name", name));
        Object obj = criteria.uniqueResult();
        return (StoragePool) obj;
    }

    public List<StoragePool> findPoolsByTier(final Tier tier)
    {
        Criteria criteria = createCriteria(Restrictions.eq("tier", tier));
        return criteria.list();
    }

}
