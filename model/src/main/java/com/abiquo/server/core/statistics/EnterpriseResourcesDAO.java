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

package com.abiquo.server.core.statistics;

import javax.persistence.EntityManager;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.springframework.stereotype.Repository;

import com.abiquo.server.core.common.persistence.DefaultDAOBase;
import com.softwarementors.bzngine.engines.hibernate.HibernateEntityManagerHelper;

@Repository("jpaEnterpriseResourcesDAO")
public class EnterpriseResourcesDAO extends DefaultDAOBase<Integer, EnterpriseResources>
{
    public EnterpriseResourcesDAO()
    {
        super(EnterpriseResources.class);
    }

    public EnterpriseResourcesDAO(EntityManager entityManager)
    {
        super(EnterpriseResources.class, entityManager);
    }

    public EnterpriseResources sumTotalResourcesByEnterprise()
    {
        // TODO: Include aggregators functionality in bzengine?
        Session ses = HibernateEntityManagerHelper.getSession(getEntityManager());

        Criteria crit = ses.createCriteria(EnterpriseResources.class);
        ProjectionList proList = Projections.projectionList();
        proList.add(Projections.sum(EnterpriseResources.V_CPU_RESERVED_PROPERTY));
        proList.add(Projections.sum(EnterpriseResources.V_CPU_USED_PROPERTY));
        proList.add(Projections.sum(EnterpriseResources.MEMORY_RESERVED_PROPERTY));
        proList.add(Projections.sum(EnterpriseResources.MEMORY_USED_PROPERTY));
        proList.add(Projections.sum(EnterpriseResources.LOCAL_STORAGE_RESERVED_PROPERTY));
        proList.add(Projections.sum(EnterpriseResources.LOCAL_STORAGE_USED_PROPERTY));
        proList.add(Projections.sum(EnterpriseResources.PUBLIC_I_PS_RESERVED_PROPERTY));
        proList.add(Projections.sum(EnterpriseResources.PUBLIC_I_PS_USED_PROPERTY));
        proList.add(Projections.sum(EnterpriseResources.VLAN_RESERVED_PROPERTY));
        proList.add(Projections.sum(EnterpriseResources.VLAN_USED_PROPERTY));
        proList.add(Projections.sum(EnterpriseResources.EXT_STORAGE_RESERVED_PROPERTY));
        proList.add(Projections.sum(EnterpriseResources.EXT_STORAGE_USED_PROPERTY));
        proList.add(Projections.sum(EnterpriseResources.REPOSITORY_RESERVED_PROPERTY));
        proList.add(Projections.sum(EnterpriseResources.REPOSITORY_USED_PROPERTY));
        proList.add(Projections.sum(EnterpriseResources.REPOSITORY_USED_PROPERTY));

        crit.setProjection(proList);
        Object[] obj = (Object[]) crit.uniqueResult(); // Returns Object[] ->
        EnterpriseResources result = new EnterpriseResources();

        int cont = 0;
        result.setVirtualCpuReserved((Long) obj[cont++]);
        result.setVirtualCpuUsed((Long) obj[cont++]);
        result.setMemoryReserved((Long) obj[cont++]);
        result.setMemoryUsed((Long) obj[cont++]);
        result.setLocalStorageReserved((Long) obj[cont++]);
        result.setLocalStorageUsed((Long) obj[cont++]);
        result.setPublicIPsReserved((Long) obj[cont++]);
        result.setPublicIPsUsed((Long) obj[cont++]);
        result.setVlanReserved((Long) obj[cont++]);
        result.setVlanUsed((Long) obj[cont++]);
        result.setExtStorageReserved((Long) obj[cont++]);
        result.setExtStorageUsed((Long) obj[cont++]);
        result.setRepositoryReserved((Long) obj[cont++]);
        result.setRepositoryUsed((Long) obj[cont++]);

        return result;
    }

}
