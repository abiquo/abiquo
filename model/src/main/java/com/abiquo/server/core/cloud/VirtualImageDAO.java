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

package com.abiquo.server.core.cloud;

import java.util.Collections;
import java.util.List;

import javax.persistence.EntityManager;

import org.apache.commons.collections.CollectionUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.abiquo.server.core.common.persistence.DefaultDAOBase;
import com.abiquo.server.core.enterprise.Enterprise;

@Repository("jpaVirtualImageDAO")
public class VirtualImageDAO extends DefaultDAOBase<Integer, VirtualImage>
{
    public VirtualImageDAO()
    {
        super(VirtualImage.class);
    }

    public VirtualImageDAO(EntityManager entityManager)
    {
        super(VirtualImage.class, entityManager);
    }

    private static Criterion sameEnterprise(Enterprise enterprise)
    {
        assert enterprise != null;

        return Restrictions.eq(VirtualImage.ENTERPRISE_PROPERTY, enterprise);
    }

    private static Criterion sameRepository(
        com.abiquo.server.core.infrastructure.Repository repository)
    {
        assert repository != null;

        return Restrictions.eq(VirtualImage.ID_REPOSITORY_PROPERTY, repository);
    }

    private static Criterion sharedImage()
    {
        return Restrictions.eq(VirtualImage.SHARED_PROPERTY, 1);
    }

    private static Criterion sameEnterpriseOrShared(Enterprise enterprise)
    {
        return Restrictions.or(sameEnterprise(enterprise), sharedImage());
    }

    private static Criterion sameEnterpriseOrSharedInRepo(Enterprise enterprise,
        com.abiquo.server.core.infrastructure.Repository repository)
    {
        return Restrictions.and(sameRepository(repository),
            Restrictions.or(sameEnterprise(enterprise), sharedImage()));
    }

    private static Criterion sameEnterpriseOrSharedInRepo(Enterprise enterprise,
        com.abiquo.server.core.infrastructure.Repository repository, final String path)
    {
        Criterion sameEnterpriseOrSharedInRepo =
            Restrictions.and(sameRepository(repository),
                Restrictions.or(sameEnterprise(enterprise), sharedImage()));

        return Restrictions.and(Restrictions.eq(VirtualImage.PATH_NAME_PROPERTY, path),
            sameEnterpriseOrSharedInRepo);
    }

    public List<VirtualImage> findVirtualMachinesByEnterprise(Enterprise enterprise)
    {
        assert enterprise != null;
        assert isManaged2(enterprise);

        Criteria criteria = createCriteria(sameEnterpriseOrShared(enterprise));
        criteria.addOrder(Order.asc(VirtualMachine.NAME_PROPERTY));
        List<VirtualImage> result = getResultList(criteria);
        return result;
    }

    public List<VirtualImage> findVirtualImagesByEnterpriseAndRepository(Enterprise enterprise,
        com.abiquo.server.core.infrastructure.Repository repository)
    {
        assert enterprise != null;
        assert isManaged2(enterprise);

        Criteria criteria = createCriteria(sameEnterpriseOrSharedInRepo(enterprise, repository));
        criteria.addOrder(Order.asc(VirtualMachine.NAME_PROPERTY));
        List<VirtualImage> result = getResultList(criteria);
        return result;
    }

    public VirtualImage findByName(String name)
    {
        return findUniqueByProperty(VirtualImage.NAME_PROPERTY, name);
    }

    public VirtualImage findVirtualImageByPath(Enterprise enterprise,
        com.abiquo.server.core.infrastructure.Repository repository, String path)
    {
        assert enterprise != null;
        assert isManaged2(enterprise);

        Criteria criteria =
            createCriteria(sameEnterpriseOrSharedInRepo(enterprise, repository, path));
        criteria.addOrder(Order.asc(VirtualMachine.NAME_PROPERTY));

        return getSingleResult(criteria);
    }

    public boolean existWithSamePath(Enterprise enterprise,
        com.abiquo.server.core.infrastructure.Repository repository, String path)
    {
        assert enterprise != null;
        assert isManaged2(enterprise);

        Criteria criteria =
            createCriteria(sameEnterpriseOrSharedInRepo(enterprise, repository, path));
        criteria.addOrder(Order.asc(VirtualMachine.NAME_PROPERTY));
        List<VirtualImage> result = getResultList(criteria);

        return CollectionUtils.isEmpty(result) ? false : true;
    }
}
