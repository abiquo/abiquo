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

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.abiquo.server.core.common.persistence.DefaultDAOBase;

@Repository("jpaRepositoryDAO")
public class RepositoryDAO extends
    DefaultDAOBase<Integer, com.abiquo.server.core.infrastructure.Repository>
{
    public RepositoryDAO()
    {
        super(com.abiquo.server.core.infrastructure.Repository.class);
    }

    public RepositoryDAO(EntityManager entityManager)
    {
        super(com.abiquo.server.core.infrastructure.Repository.class, entityManager);
    }

    private static Criterion thisLocation(String repositoryLocation)
    {
        return Restrictions.eq(com.abiquo.server.core.infrastructure.Repository.URL_PROPERTY,
            repositoryLocation);
    }

    private static Criterion thisDatacenter(Datacenter datacenter)
    {
        return Restrictions.eq(
            com.abiquo.server.core.infrastructure.Repository.DATACENTER_PROPERTY, datacenter);
    }

    public com.abiquo.server.core.infrastructure.Repository findByDatacenter(Datacenter datacenter)
    {
        Criteria criteria = createCriteria(thisDatacenter(datacenter));

        return (com.abiquo.server.core.infrastructure.Repository) criteria.uniqueResult();// getSingleResult(criteria);
    }

    public boolean existRepositoryInOtherDatacenter(Datacenter datacenter, String repositoryLocation)
    {
        Criterion notDatacenter = Restrictions.not(thisDatacenter(datacenter));
        Criteria criteria = createCriteria(notDatacenter, thisLocation(repositoryLocation));

        criteria.setProjection(Projections.projectionList().add(Projections.rowCount()));

        Long count = (Long) criteria.uniqueResult();
        return count != null && count.intValue() > 0;
    }

    public com.abiquo.server.core.infrastructure.Repository findByRepositoryLocation(
        String repositoryLocation)
    {
        Criteria criteria = createCriteria(thisLocation(repositoryLocation));

        return (com.abiquo.server.core.infrastructure.Repository) criteria.uniqueResult(); // getSingleResult(criteria);
    }

    private final static String FIND_VIRTUAL_APPS_BY_USED_VIRTUAL_IMAGE_ON_REPOSITORY =
        "SELECT va.id FROM " + //
            "VirtualAppliance as va " + //
            "inner join va.nodesVirtualImage as n, " + //
            "NodeVirtualImage as nvi " + //
            "inner join nvi.virtualImage as vi " + //
            "WHERE " + //
            "nvi.id = n.id " + //
            "AND vi.repository.id=:idRepo";

    public boolean isBeingUsed(Datacenter datacenter)
    {
        com.abiquo.server.core.infrastructure.Repository repo = findByDatacenter(datacenter);

        if (repo == null)
        {
            return false;
        }

        Query query =
            getSession().createQuery(FIND_VIRTUAL_APPS_BY_USED_VIRTUAL_IMAGE_ON_REPOSITORY);

        query.setParameter("idRepo", repo.getId());

        List<Integer> vappIds = query.list();

        return !(vappIds == null || vappIds.isEmpty());
    }

    public void updateRepositoryLocation(Datacenter datacenter, String url)
    {
        // XXX assert !isBeingUsed(datacenter)

        com.abiquo.server.core.infrastructure.Repository repo = findByDatacenter(datacenter);

        if (repo == null)
        {
            repo = new com.abiquo.server.core.infrastructure.Repository(datacenter, url);
            persist(repo);
        }
        else
        {
            repo.setUrl(url);
            flush();
        }
    }

    public void removeByDatacenter(Datacenter datacenter)
    {
        com.abiquo.server.core.infrastructure.Repository repo = findByDatacenter(datacenter);

        if (repo != null)
        {
            remove(repo);
        }
    }

}
