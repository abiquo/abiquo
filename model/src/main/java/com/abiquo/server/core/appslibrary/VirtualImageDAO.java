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

package com.abiquo.server.core.appslibrary;

import java.util.List;

import javax.persistence.EntityManager;

import org.apache.commons.collections.CollectionUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.abiquo.server.core.cloud.VirtualMachine;
import com.abiquo.server.core.common.persistence.DefaultDAOBase;
import com.abiquo.server.core.enterprise.Enterprise;

@Repository("jpaVirtualImageDAO")
/* package */class VirtualImageDAO extends DefaultDAOBase<Integer, VirtualImage>
{
    public VirtualImageDAO()
    {
        super(VirtualImage.class);
    }

    public VirtualImageDAO(final EntityManager entityManager)
    {
        super(VirtualImage.class, entityManager);
    }

    public List<VirtualImage> findByEnterprise(final Enterprise enterprise)
    {
        Criteria criteria = createCriteria(sameEnterpriseOrShared(enterprise));
        criteria.addOrder(Order.asc(VirtualMachine.NAME_PROPERTY));
        List<VirtualImage> result = getResultList(criteria);
        return result;
    }

    public List<VirtualImage> findByEnterpriseAndRepository(final Enterprise enterprise,
        final com.abiquo.server.core.infrastructure.Repository repository)
    {
        Criteria criteria = createCriteria(sameEnterpriseOrSharedInRepo(enterprise, repository));
        criteria.addOrder(Order.asc(VirtualMachine.NAME_PROPERTY));
        List<VirtualImage> result = getResultList(criteria);
        return result;
    }

    public VirtualImage findByName(final String name)
    {
        return findUniqueByProperty(VirtualImage.NAME_PROPERTY, name);
    }

    public VirtualImage findByPath(final Enterprise enterprise,
        final com.abiquo.server.core.infrastructure.Repository repository, final String path)
    {
        Criteria criteria =
            createCriteria(sameEnterpriseOrSharedInRepo(enterprise, repository, path));
        criteria.addOrder(Order.asc(VirtualMachine.NAME_PROPERTY));

        return getSingleResult(criteria);
    }

    public boolean existWithSamePath(final Enterprise enterprise,
        final com.abiquo.server.core.infrastructure.Repository repository, final String path)
    {
        Criteria criteria =
            createCriteria(sameEnterpriseOrSharedInRepo(enterprise, repository, path));
        criteria.addOrder(Order.asc(VirtualMachine.NAME_PROPERTY));
        List<VirtualImage> result = getResultList(criteria);

        return CollectionUtils.isEmpty(result) ? false : true;
    }

    private static Criterion sameEnterprise(final Enterprise enterprise)
    {
        assert enterprise != null;

        return Restrictions.eq(VirtualImage.ENTERPRISE_PROPERTY, enterprise);
    }

    private static Criterion sameRepository(
        final com.abiquo.server.core.infrastructure.Repository repository)
    {
        assert repository != null;

        return Restrictions.eq(VirtualImage.REPOSITORY_PROPERTY, repository);
    }

    private static Criterion sharedImage()
    {
        return Restrictions.eq(VirtualImage.SHARED_PROPERTY, true);
    }

    private static Criterion sameEnterpriseOrShared(final Enterprise enterprise)
    {
        return Restrictions.or(sameEnterprise(enterprise), sharedImage());
    }

    private static Criterion sameEnterpriseOrSharedInRepo(final Enterprise enterprise,
        final com.abiquo.server.core.infrastructure.Repository repository)
    {
        return Restrictions.and(sameRepository(repository),
            Restrictions.or(sameEnterprise(enterprise), sharedImage()));
    }

    private static Criterion sameEnterpriseOrSharedInRepo(final Enterprise enterprise,
        final com.abiquo.server.core.infrastructure.Repository repository, final String path)
    {
        Criterion sameEnterpriseOrSharedInRepo =
            Restrictions.and(sameRepository(repository),
                Restrictions.or(sameEnterprise(enterprise), sharedImage()));

        return Restrictions.and(Restrictions.eq(VirtualImage.PATH_PROPERTY, path),
            sameEnterpriseOrSharedInRepo);
    }
}
