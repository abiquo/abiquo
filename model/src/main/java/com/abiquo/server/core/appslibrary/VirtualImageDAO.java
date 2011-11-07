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

import java.util.Arrays;
import java.util.List;

import javax.persistence.EntityManager;

import org.apache.commons.collections.CollectionUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinFragment;
import org.springframework.stereotype.Repository;

import com.abiquo.model.enumerator.ConversionState;
import com.abiquo.model.enumerator.DiskFormatType;
import com.abiquo.model.enumerator.HypervisorType;
import com.abiquo.server.core.cloud.VirtualMachine;
import com.abiquo.server.core.common.persistence.DefaultDAOBase;
import com.abiquo.server.core.enterprise.Enterprise;
import com.abiquo.server.core.infrastructure.Datacenter;
import com.abiquo.server.core.infrastructure.storage.StorageDevice;
import com.abiquo.server.core.infrastructure.storage.StoragePool;
import com.abiquo.server.core.infrastructure.storage.VolumeManagement;

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
        return getResultList(criteria);
    }

    public List<VirtualImage> findByEnterpriseAndRepository(final Enterprise enterprise,
        final com.abiquo.server.core.infrastructure.Repository repository)
    {
        Criteria criteria = createCriteria(sameEnterpriseOrSharedInRepo(enterprise, repository));
        criteria.addOrder(Order.asc(VirtualMachine.NAME_PROPERTY));
        return getResultList(criteria);
    }

    public List<VirtualImage> findBy(final Enterprise enterprise,
        final com.abiquo.server.core.infrastructure.Repository repository, final Category category,
        final HypervisorType hypervisor)
    {
        Criteria criteria = createCriteria(sameEnterpriseOrSharedInRepo(enterprise, repository));

        if (category != null)
        {
            criteria.add(sameCategory(category));
        }

        if (hypervisor != null)
        {
            criteria.add(compatibleOrConversions(hypervisor, criteria));
        }

        criteria.addOrder(Order.asc(VirtualMachine.NAME_PROPERTY));
        List<VirtualImage> result = getResultList(criteria);
        return result;
    }

    /** Virtual image compatible or some conversion compatible. */
    private Criterion compatibleOrConversions(final HypervisorType hypervisorType,
        final Criteria criteria)
    {
        return Restrictions.or(compatible(Arrays.asList(hypervisorType.compatibilityTable)), //
            compatibleConversions(Arrays.asList(hypervisorType.compatibilityTable), criteria));
    }

    /** Virtual image is compatible. */
    private Criterion compatible(final List<DiskFormatType> types)
    {
        return Restrictions.in(VirtualImage.DISKFORMAT_TYPE_PROPERTY, types);
    }

    /**
     * If (finished) conversions check some compatible. Left join to {@link VirtualImageConversion}
     */
    private Criterion compatibleConversions(final List<DiskFormatType> types,
        final Criteria criteria)
    {
        criteria.createAlias(VirtualImage.CONVERSIONS_PROPERTY, "conversions",
            JoinFragment.LEFT_OUTER_JOIN);

        Criterion finished =
            Restrictions.eq("conversions." + VirtualImageConversion.STATE_PROPERTY,
                ConversionState.FINISHED);

        Criterion compatible =
            Restrictions.in("conversions." + VirtualImageConversion.TARGET_TYPE_PROPERTY, types);

        return Restrictions.and(finished, compatible);
    }

    /** ######### */

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

    public List<VirtualImage> findStatefuls()
    {
        Criteria criteria = createCriteria(statefulImage());
        criteria.addOrder(Order.asc(VirtualMachine.NAME_PROPERTY));
        return getResultList(criteria);
    }

    public List<VirtualImage> findStatefulsByDatacenter(final Datacenter datacenter)
    {
        Criteria crit = criteriaWithStatefulNavigation();
        crit.add(statefulImage());
        crit.add(sameStatefulDatacenter(datacenter));
        crit.addOrder(Order.asc(VirtualImage.NAME_PROPERTY));
        return getResultList(crit);
    }

    public List<VirtualImage> findStatefulsByCategoryAndDatacenter(final Category category,
        final Datacenter datacenter)
    {
        Criteria crit = criteriaWithStatefulNavigation();
        crit.add(statefulImage());
        crit.add(sameCategory(category));
        crit.add(sameStatefulDatacenter(datacenter));
        crit.addOrder(Order.asc(VirtualImage.NAME_PROPERTY));
        return getResultList(crit);
    }

    private static Criterion sameCategory(final Category category)
    {
        return Restrictions.eq(VirtualImage.CATEGORY_PROPERTY, category);
    }

    private static Criterion sameEnterprise(final Enterprise enterprise)
    {
        return Restrictions.eq(VirtualImage.ENTERPRISE_PROPERTY, enterprise);
    }

    private static Criterion sameRepository(
        final com.abiquo.server.core.infrastructure.Repository repository)
    {
        return Restrictions.eq(VirtualImage.REPOSITORY_PROPERTY, repository);
    }

    private static Criterion sharedImage()
    {
        return Restrictions.eq(VirtualImage.SHARED_PROPERTY, true);
    }

    private static Criterion statefulImage()
    {
        return Restrictions.and(Restrictions.eq(VirtualImage.STATEFUL_PROPERTY, true),
            Restrictions.isNotNull(VirtualImage.VOLUME_PROPERTY));
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

    private static Criterion sameStatefulDatacenter(final Datacenter datacenter)
    {
        return Restrictions.eq("device." + StorageDevice.DATACENTER_PROPERTY, datacenter);
    }

    private Criteria criteriaWithStatefulNavigation()
    {
        Criteria crit = createCriteria();
        crit.createAlias(VirtualImage.VOLUME_PROPERTY, "volume");
        crit.createAlias("volume." + VolumeManagement.STORAGE_POOL_PROPERTY, "pool");
        crit.createAlias("pool." + StoragePool.DEVICE_PROPERTY, "device");
        return crit;
    }
}
