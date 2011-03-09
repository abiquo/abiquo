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

@Repository("jpaCabinetDAO")
public class StorageDeviceDAO extends DefaultDAOBase<Integer, StorageDevice> {

	public StorageDeviceDAO() {
		super(StorageDevice.class);
	}

	public StorageDeviceDAO(EntityManager entityManager) {
		super(StorageDevice.class, entityManager);
	}

	@SuppressWarnings("unchecked")
	public List<StorageDevice> getDevicesByDatacenter(final Integer datacenterId) {
		
		Criteria criteria = createCriteria(Restrictions.eq("datacenter.id",
				datacenterId));
		return criteria.list();
	
	}

    public StorageDevice getDeviceById(final Integer datacenterId, final Integer deviceId)
    {
        Criteria criteria =
            createCriteria(Restrictions.eq("datacenter.id", datacenterId)).add(
                Restrictions.eq("id", deviceId));
        Object obj = criteria.uniqueResult();
        return (StorageDevice) obj;
    }

    public StorageDevice findDeviceById(Integer datacenterId, String managementIp)
    {
        Criteria criteria =
            createCriteria(Restrictions.eq("datacenter.id", datacenterId)).add(
                Restrictions.eq("managementIp", managementIp));
        Object obj = criteria.uniqueResult();
        return (StorageDevice) obj;
    }

}
