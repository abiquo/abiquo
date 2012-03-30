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

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;

import org.apache.commons.collections.CollectionUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinFragment;
import org.springframework.stereotype.Repository;

import com.abiquo.model.enumerator.ConversionState;
import com.abiquo.model.enumerator.DiskFormatType;
import com.abiquo.model.enumerator.HypervisorType;
import com.abiquo.model.enumerator.StatefulInclusion;
import com.abiquo.model.enumerator.VolumeState;
import com.abiquo.server.core.cloud.VirtualDatacenter;
import com.abiquo.server.core.cloud.VirtualMachine;
import com.abiquo.server.core.common.persistence.DefaultDAOBase;
import com.abiquo.server.core.enterprise.Enterprise;
import com.abiquo.server.core.infrastructure.Datacenter;
import com.abiquo.server.core.infrastructure.management.RasdManagement;
import com.abiquo.server.core.infrastructure.storage.StorageDevice;
import com.abiquo.server.core.infrastructure.storage.StoragePool;
import com.abiquo.server.core.infrastructure.storage.VolumeManagement;

@Repository("jpaVirtualMachineTemplateDAO")
/* package */class VirtualMachineTemplateDAO extends
    DefaultDAOBase<Integer, VirtualMachineTemplate>
{

    private final String FIND_ICONS_BY_ENTERPRISE = " SELECT distinct vtd.iconUrl "//
        + "FROM com.abiquo.server.core.appslibrary.VirtualMachineTemplate vtd "//
        + "WHERE vtd.enterprise.id = :enterpriseId";

    public VirtualMachineTemplateDAO()
    {
        super(VirtualMachineTemplate.class);
    }

    public VirtualMachineTemplateDAO(final EntityManager entityManager)
    {
        super(VirtualMachineTemplate.class, entityManager);
    }

    public List<VirtualMachineTemplate> findByEnterprise(final Enterprise enterprise)
    {
        Criteria criteria = createCriteria(sameEnterpriseOrShared(enterprise));
        criteria.addOrder(Order.asc(VirtualMachine.NAME_PROPERTY));
        return getResultList(criteria);
    }

    public List<VirtualMachineTemplate> findByEnterpriseAndRepository(final Enterprise enterprise,
        final com.abiquo.server.core.infrastructure.Repository repository)
    {
        Criteria criteria = createCriteria(sameEnterpriseOrSharedInRepo(enterprise, repository));
        criteria.addOrder(Order.asc(VirtualMachine.NAME_PROPERTY));
        return getResultList(criteria);
    }

    public List<VirtualMachineTemplate> findImportedByEnterprise(final Enterprise enterprise)
    {
        Criteria criteria = createCriteria(importedVirtualMachineTemplate(enterprise));
        criteria.addOrder(Order.asc(VirtualMachine.NAME_PROPERTY));
        return getResultList(criteria);
    }

    public List<VirtualMachineTemplate> findBy(final Enterprise enterprise,
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

        // TODO
        // criteria.setProjection(Projections.distinct(Projections.id()));
        // criteria.setResultTransformer(DistinctResultTransformer.INSTANCE);
        criteria.addOrder(Order.asc(VirtualMachineTemplate.NAME_PROPERTY));

        List<VirtualMachineTemplate> result = getResultList(criteria);
        return distinct(result);
    }

    public List<VirtualMachineTemplate> findBy(final Category category)
    {
        Criteria criteria = createCriteria(sameCategory(category));
        criteria.addOrder(Order.asc(VirtualMachineTemplate.NAME_PROPERTY));

        List<VirtualMachineTemplate> result = getResultList(criteria);
        return result;
    }

    private List<VirtualMachineTemplate> distinct(final List<VirtualMachineTemplate> ins)
    {
        List<VirtualMachineTemplate> outs = new LinkedList<VirtualMachineTemplate>();
        Set<Integer> ids = new HashSet<Integer>();
        for (VirtualMachineTemplate in : ins)
        {
            if (!ids.contains(in.getId()))
            {
                ids.add(in.getId());
                outs.add(in);
            }
        }
        return outs;
    }

    public List<VirtualMachineTemplate> findImportedBy(final Enterprise enterprise,
        final Category category, final HypervisorType hypervisor)
    {
        Criteria criteria = createCriteria(importedVirtualMachineTemplate(enterprise));

        if (category != null)
        {
            criteria.add(sameCategory(category));
        }

        if (hypervisor != null)
        {
            criteria.add(compatibleOrConversions(hypervisor, criteria));
        }

        criteria.addOrder(Order.asc(VirtualMachine.NAME_PROPERTY));
        List<VirtualMachineTemplate> result = getResultList(criteria);
        return result;
    }

    private Criterion importedVirtualMachineTemplate(final Enterprise enterprise)
    {
        return Restrictions.and(sameEnterprise(enterprise), repositoryNull());
    }

    /** Virtual Machine Template compatible or some conversion compatible. */
    private Criterion compatibleOrConversions(final HypervisorType hypervisorType,
        final Criteria criteria)
    {
        return Restrictions.or(compatible(hypervisorType.compatibleFormats), //
            compatibleConversions(hypervisorType.compatibleFormats, criteria));
    }

    /** Virtual Machine Template is compatible. */
    private Criterion compatible(final Collection<DiskFormatType> types)
    {
        return Restrictions.in(VirtualMachineTemplate.DISKFORMAT_TYPE_PROPERTY, types);
    }

    /**
     * If (finished) conversions check some compatible. Left join to {@link VirtualImageConversion}
     */
    private Criterion compatibleConversions(final Collection<DiskFormatType> types,
        final Criteria criteria)
    {
        criteria.createAlias(VirtualMachineTemplate.CONVERSIONS_PROPERTY, "conversions",
            JoinFragment.LEFT_OUTER_JOIN);

        Criterion finished =
            Restrictions.eq("conversions." + VirtualImageConversion.STATE_PROPERTY,
                ConversionState.FINISHED);

        Criterion compatible =
            Restrictions.in("conversions." + VirtualImageConversion.TARGET_TYPE_PROPERTY, types);

        return Restrictions.and(finished, compatible);
    }

    /** ######### */

    public VirtualMachineTemplate findByName(final String name)
    {
        return findUniqueByProperty(VirtualMachineTemplate.NAME_PROPERTY, name);
    }

    public VirtualMachineTemplate findByPath(final Enterprise enterprise,
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
        List<VirtualMachineTemplate> result = getResultList(criteria);

        return CollectionUtils.isEmpty(result) ? false : true;
    }

    public List<VirtualMachineTemplate> findStatefuls()
    {
        Criteria criteria = createCriteria();
        criteria.add(statefulVirtualMachineTemplate(StatefulInclusion.ALL, criteria));
        criteria.addOrder(Order.asc(VirtualMachine.NAME_PROPERTY));
        return getResultList(criteria);
    }

    public List<VirtualMachineTemplate> findStatefulsByDatacenter(final Datacenter datacenter,
        final StatefulInclusion stateful)
    {
        return findStatefulsByDatacenter(datacenter, null, stateful);
    }

    public List<VirtualMachineTemplate> findStatefulsByDatacenter(final Datacenter datacenter,
        final VirtualDatacenter vdc, final StatefulInclusion stateful)
    {
        Criteria crit = criteriaWithStatefulNavigation();
        crit.add(statefulVirtualMachineTemplate(stateful, crit));
        crit.add(sameStatefulDatacenter(datacenter));
        if (vdc != null)
        {
            crit.add(sameStatefulVirtualDatacenter(vdc));
        }
        crit.addOrder(Order.asc(VirtualMachineTemplate.NAME_PROPERTY));
        return getResultList(crit);
    }

    public List<VirtualMachineTemplate> findStatefulsByCategoryAndDatacenter(
        final Category category, final Datacenter datacenter, final StatefulInclusion stateful)
    {
        return findStatefulsByCategoryAndDatacenter(category, datacenter, null, stateful);
    }

    public List<VirtualMachineTemplate> findStatefulsByCategoryAndDatacenter(
        final Category category, final Datacenter datacenter, final VirtualDatacenter vdc,
        final StatefulInclusion stateful)
    {
        Criteria crit = criteriaWithStatefulNavigation();
        crit.add(statefulVirtualMachineTemplate(stateful, crit));
        crit.add(sameCategory(category));
        crit.add(sameStatefulDatacenter(datacenter));
        if (vdc != null)
        {
            crit.add(sameStatefulVirtualDatacenter(vdc));
        }
        crit.addOrder(Order.asc(VirtualMachineTemplate.NAME_PROPERTY));
        return getResultList(crit);
    }

    public List<VirtualMachineTemplate> findByMaster(final VirtualMachineTemplate master)
    {
        Criteria criteria = createCriteria(sameMaster(master));
        return getResultList(criteria);
    }

    public boolean isMaster(final VirtualMachineTemplate vmtemplate)
    {
        Criteria criteria = createCriteria(sameMaster(vmtemplate));
        return CollectionUtils.isEmpty(getResultList(criteria)) ? false : true;
    }

    private static Criterion sameCategory(final Category category)
    {
        return Restrictions.eq(VirtualMachineTemplate.CATEGORY_PROPERTY, category);
    }

    private static Criterion sameEnterprise(final Enterprise enterprise)
    {
        return Restrictions.eq(VirtualMachineTemplate.ENTERPRISE_PROPERTY, enterprise);
    }

    private static Criterion sameRepositoryAndNotStatefull(
        final com.abiquo.server.core.infrastructure.Repository repository)
    {
        // return Restrictions.and(Restrictions.eq(VirtualMachineTemplate.STATEFUL_PROPERTY, false),
        // Restrictions.eq(VirtualMachineTemplate.REPOSITORY_PROPERTY, repository));
        return Restrictions.eq(VirtualMachineTemplate.REPOSITORY_PROPERTY, repository);

    }

    private static Criterion repositoryNull()
    {
        return Restrictions.isNull(VirtualMachineTemplate.REPOSITORY_PROPERTY);
    }

    private static Criterion sharedVirtualMachineTemplate()
    {
        return Restrictions.eq(VirtualMachineTemplate.SHARED_PROPERTY, true);
    }

    private static Criterion statefulVirtualMachineTemplate(final StatefulInclusion stateful,
        final Criteria criteria)
    {
        Criterion cri = Restrictions.eq(VirtualMachineTemplate.STATEFUL_PROPERTY, true);

        switch (stateful)
        {
            case ALL:
                Restrictions.and(cri,
                    Restrictions.isNotNull(VirtualMachineTemplate.VOLUME_PROPERTY));
                break;
            case USED:
                // use function criteriaWithStatefulNavigation before
                return Restrictions.and(cri,
                    Restrictions.eq("vl." + VolumeManagement.STATE_PROPERTY, VolumeState.ATTACHED));
            case NOTUSED:
                // use function criteriaWithStatefulNavigation before
                return Restrictions.and(cri,
                    Restrictions.eq("vl." + VolumeManagement.STATE_PROPERTY, VolumeState.DETACHED));
        }
        return cri;
    }

    private static Criterion sameEnterpriseOrShared(final Enterprise enterprise)
    {
        return Restrictions.or(sameEnterprise(enterprise), sharedVirtualMachineTemplate());
    }

    private static Criterion sameEnterpriseOrSharedInRepo(final Enterprise enterprise,
        final com.abiquo.server.core.infrastructure.Repository repository)
    {
        return Restrictions.and(sameRepositoryAndNotStatefull(repository),
            Restrictions.or(sameEnterprise(enterprise), sharedVirtualMachineTemplate()));
    }

    private static Criterion sameEnterpriseOrSharedInRepo(final Enterprise enterprise,
        final com.abiquo.server.core.infrastructure.Repository repository, final String path)
    {
        Criterion sameEnterpriseOrSharedInRepo =
            Restrictions.and(sameRepositoryAndNotStatefull(repository),
                Restrictions.or(sameEnterprise(enterprise), sharedVirtualMachineTemplate()));

        return Restrictions.and(Restrictions.eq(VirtualMachineTemplate.PATH_PROPERTY, path),
            sameEnterpriseOrSharedInRepo);
    }

    private static Criterion sameMaster(final VirtualMachineTemplate vmtemplate)
    {
        return Restrictions.eq(VirtualMachineTemplate.MASTER_PROPERTY, vmtemplate);
    }

    private static Criterion sameStatefulDatacenter(final Datacenter datacenter)
    {
        return Restrictions.eq("device." + StorageDevice.DATACENTER_PROPERTY, datacenter);
    }

    private static Criterion sameStatefulVirtualDatacenter(final VirtualDatacenter virtualDatacenter)
    {
        return Restrictions.eq("vl." + RasdManagement.VIRTUAL_DATACENTER_PROPERTY,
            virtualDatacenter);
    }

    public List<String> findIconsByEnterprise(final Integer enterpriseId)
    {
        Query query = getSession().createQuery(FIND_ICONS_BY_ENTERPRISE);
        return query.setParameter("enterpriseId", enterpriseId).list();
    }

    private Criteria criteriaWithStatefulNavigation()
    {
        Criteria crit = createCriteria();
        crit.createAlias(VirtualMachineTemplate.VOLUME_PROPERTY, "vl");
        crit.createAlias("vl." + VolumeManagement.STORAGE_POOL_PROPERTY, "pool");
        crit.createAlias("pool." + StoragePool.DEVICE_PROPERTY, "device");
        return crit;
    }

}
