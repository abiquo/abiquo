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

import java.util.Arrays;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.TypedQuery;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.abiquo.server.core.appslibrary.VirtualMachineTemplate;
import com.abiquo.server.core.cloud.VirtualMachine.OrderByEnum;
import com.abiquo.server.core.common.persistence.DefaultDAOBase;
import com.abiquo.server.core.common.persistence.JPAConfiguration;
import com.abiquo.server.core.enterprise.Enterprise;
import com.abiquo.server.core.enterprise.User;
import com.abiquo.server.core.infrastructure.Datacenter;
import com.abiquo.server.core.util.PagedList;
import com.softwarementors.bzngine.entities.PersistentEntity;

@Repository("jpaVirtualMachineDAO")
public class VirtualMachineDAO extends DefaultDAOBase<Integer, VirtualMachine>
{
    public static final String BY_VAPP_AND_ID =
        "SELECT nvi.virtualMachine " + "FROM NodeVirtualImage nvi "
            + "WHERE nvi.virtualAppliance.id = :vapp_id AND nvi.virtualMachine.id = :vm_id";

    private static Criterion equalName(final String name)
    {
        assert !StringUtils.isEmpty(name);

        return Restrictions.eq(Datacenter.NAME_PROPERTY, name);
    }

    private static Criterion sameEnterprise(final Enterprise enterprise)
    {
        assert enterprise != null;

        return Restrictions.eq(VirtualMachine.ENTERPRISE_PROPERTY, enterprise);
    }

    private static Criterion sameHypervisor(final Hypervisor hypervisor)
    {
        assert hypervisor != null;

        return Restrictions.eq(VirtualMachine.HYPERVISOR_PROPERTY, hypervisor);
    }

    private static Criterion sameUser(final User user)
    {
        return Restrictions.eq(VirtualMachine.USER_PROPERTY, user);
    }

    public VirtualMachineDAO()
    {
        super(VirtualMachine.class);
    }

    public VirtualMachineDAO(final EntityManager entityManager)
    {
        super(VirtualMachine.class, entityManager);
    }

    public List<VirtualMachine> getNotManagedVirtualMachines(final Hypervisor hypervisor)
    {
        Criteria criteria = createCriteria(sameHypervisor(hypervisor), notManaged());
        return getResultList(criteria);
    }

    public boolean existsAnyWithName(final String name)
    {
        assert !StringUtils.isEmpty(name);

        return this.existsAnyByCriterions(equalName(name));
    }

    public VirtualMachine findByIdByVirtualApp(final VirtualAppliance vapp, final Integer vmId)
    {
        Query finalQuery = getSession().createQuery(BY_VAPP_AND_ID);
        finalQuery.setParameter("vapp_id", vapp.getId());
        finalQuery.setParameter("vm_id", vmId);

        return (VirtualMachine) finalQuery.uniqueResult();
    }

    public VirtualMachine findByName(final String name)
    {
        return findUniqueByProperty(VirtualMachine.NAME_PROPERTY, name);
    }

    public VirtualMachine findByUUID(final String uuid)
    {
        return findUniqueByProperty(VirtualMachine.UUID_PROPERTY, uuid);
    }

    public List<VirtualMachine> findManagedVirtualMachines(final Hypervisor hypervisor)
    {
        assert hypervisor != null;
        assert isManaged2(hypervisor);

        Criteria criteria = createCriteria(sameHypervisor(hypervisor), managed());
        criteria.addOrder(Order.asc(VirtualMachine.NAME_PROPERTY));
        List<VirtualMachine> result = getResultList(criteria);
        return result;
    }

    public VirtualMachine findVirtualMachineByHypervisor(final Hypervisor hypervisor,
        final Integer virtualMachineId)
    {
        Criteria criteria =
            createCriteria(sameHypervisor(hypervisor), Restrictions.eq(
                PersistentEntity.ID_PROPERTY, virtualMachineId));
        return (VirtualMachine) criteria.uniqueResult();
    }

    public List<VirtualMachine> findVirtualMachines(final Hypervisor hypervisor)
    {
        assert hypervisor != null;
        assert isManaged2(hypervisor);

        Criteria criteria = createCriteria(sameHypervisor(hypervisor));
        criteria.addOrder(Order.asc(VirtualMachine.NAME_PROPERTY));
        List<VirtualMachine> result = getResultList(criteria);
        return result;
    }

    // without hypervisor
    public List<VirtualMachine> findVirtualMachinesNotAllocatedCompatibleHypervisor(
        final Hypervisor hypervisor)
    {
        Criteria criteria = createCriteria();
        criteria.createAlias(VirtualMachine.VIRTUAL_MACHINE_TEMPLATE_PROPERTY, "template");
        Restrictions.and(Restrictions.eq(VirtualMachine.HYPERVISOR_PROPERTY, null), Restrictions
            .in("template." + VirtualMachineTemplate.DISKFORMAT_TYPE_PROPERTY, Arrays
                .asList(hypervisor.getType().compatibleFormats)));
        criteria.addOrder(Order.asc(VirtualMachine.NAME_PROPERTY));
        List<VirtualMachine> result = getResultList(criteria);
        return result;
    }

    public List<VirtualMachine> findVirtualMachinesByDatacenter(final Integer datacenterId)
    {
        List<VirtualMachine> vmList = null;
        TypedQuery<VirtualMachine> query =
            getEntityManager().createNamedQuery("VIRTUAL_MACHINE.BY_DC", VirtualMachine.class);
        query.setParameter("datacenterId", datacenterId);
        vmList = query.getResultList();

        return vmList;
    }

    public List<VirtualMachine> findVirtualMachinesByEnterprise(final Enterprise enterprise)
    {
        assert enterprise != null;
        assert isManaged2(enterprise);

        Criteria criteria = createCriteria(sameEnterprise(enterprise));
        criteria.addOrder(Order.asc(VirtualMachine.NAME_PROPERTY));
        List<VirtualMachine> result = getResultList(criteria);
        return result;
    }

    public List<VirtualMachine> findVirtualMachinesByUser(final Enterprise enterprise,
        final User user)
    {
        Criteria criteria = createCriteria(sameUser(user)).add(sameEnterprise(enterprise));
        criteria.addOrder(Order.asc(VirtualMachine.NAME_PROPERTY));
        List<VirtualMachine> result = getResultList(criteria);
        return result;
    }

    public List<VirtualMachine> findVirtualMachinesByVirtualAppliance(final Integer vappId,
        Integer startwith, Integer limit, final String filter, final OrderByEnum orderby,
        final boolean asc)
    {
        // List<VirtualMachine> vmList = null;

        // TypedQuery<VirtualMachine> query =
        // getEntityManager().createNamedQuery("VIRTUAL_MACHINE.BY_VAPP", VirtualMachine.class);
        // query.setParameter("vapp_id", vappId);
        // vmList = query.getResultList();
        String orderBy = defineOrderBy(orderby.getColumnHQL(), asc);
        Query query = getSession().getNamedQuery("VIRTUAL_MACHINE.BY_VAPP");

        String req = query.getQueryString() + orderBy;
        // Add order filter to the query
        Query queryWithOrder = getSession().createQuery(req);
        queryWithOrder.setInteger("vapp_id", vappId);
        queryWithOrder.setString("filterLike", filter.isEmpty() ? "%" : "%" + filter + "%");

        Integer size = queryWithOrder.list().size();

        // Limit 0 means no size filter
        if (limit == 0)
        {
            limit = size;
            startwith = 0;
        }

        queryWithOrder.setFirstResult(startwith);
        queryWithOrder.setMaxResults(limit);

        PagedList<VirtualMachine> vmList = new PagedList<VirtualMachine>(queryWithOrder.list());
        vmList.setTotalResults(size);
        vmList.setPageSize(limit > size ? size : limit);
        vmList.setCurrentElement(startwith);

        return vmList;
    }

    public List<VirtualMachine> findByVirtualMachineTemplate(final Integer virtualMachineTemplateId)
    {
        List<VirtualMachine> vmList = null;
        TypedQuery<VirtualMachine> query =
            getEntityManager().createNamedQuery("VIRTUAL_MACHINE.BY_VMT", VirtualMachine.class);
        query.setParameter("virtualMachineTplId", virtualMachineTemplateId);
        vmList = query.getResultList();

        return vmList;
    }

    public boolean hasVirtualMachineTemplate(final Integer virtualMachineTemplateId)
    {

        TypedQuery<Long> query =
            getEntityManager().createNamedQuery("VIRTUAL_MACHINE.HAS_VMT", Long.class);
        query.setParameter("virtualMachineTplId", virtualMachineTemplateId);
        return query.getResultList().get(0) > 0;
    }

    public void updateVirtualMachineState(final Integer vmachineId, final VirtualMachineState state)
    {
        VirtualMachine vmachine = findById(vmachineId);

        vmachine.setState(state);

        flush();
    }

    /**
     * Sets the {@link VirtualMachine#setState(VirtualMachineState)} to
     * {@link VirtualMachineState#UNKNOWN}.
     * 
     * @param vmachineId id void
     */
    public void unknownState(final Integer vmachineId)
    {
        VirtualMachine vmachine = findById(vmachineId);

        vmachine.setState(VirtualMachineState.UNKNOWN);
    }

    private Criterion managed()
    {
        return Restrictions.eq(VirtualMachine.ID_TYPE_PROPERTY, VirtualMachine.MANAGED);
    }

    private Criterion notManaged()
    {
        return Restrictions.eq(VirtualMachine.ID_TYPE_PROPERTY, VirtualMachine.NOT_MANAGED);
    }

    private Criteria temporalVirtualMachine(final Integer vmachineId)
    {
        return createCriteria(Restrictions.eq(VirtualMachine.TEMPORAL_PROPERTY, vmachineId));
    }

    public VirtualMachine findBackup(final VirtualMachine vmachine)
    {

        try
        {
            JPAConfiguration.enableOnlyTemporalFilters(getEntityManager());

            return getSingleResult(temporalVirtualMachine(vmachine.getId()));
        }
        finally
        {
            JPAConfiguration.enableDefaultFilters(getEntityManager());
        }
    }

    public void refreshLock(final VirtualMachine vm)
    {
        // We force the refresh from database (PESSIMISTIC) and increment the version of the object
        // (INCREMENT) to ensure that no other code will be using the "unlocked" object.
        getEntityManager().refresh(vm, LockModeType.PESSIMISTIC_FORCE_INCREMENT);
    }

    public void refresh(final VirtualMachine vm)
    {
        getEntityManager().refresh(vm);
    }

    public void detachHypervisor(final VirtualMachine vm)
    {
        getEntityManager().detach(vm.getHypervisor());
    }

    public void detachVirtualMachine(final VirtualMachine vm)
    {
        getEntityManager().detach(vm);
    }

    public void deleteNotManagedVirtualMachines(final Hypervisor hypervisor)
    {
        Criteria criteria = createCriteria(sameHypervisor(hypervisor), notManaged());
        List<VirtualMachine> notManaged = getResultList(criteria);

        for (VirtualMachine vm : notManaged)
        {
            remove(vm);
        }
        flush();
    }

    private String defineOrderBy(final String orderBy, final Boolean asc)
    {
        StringBuilder queryString = new StringBuilder();

        queryString.append(" order by ");
        queryString.append(orderBy);
        queryString.append(" ");

        if (asc)
        {
            queryString.append("asc");
        }
        else
        {
            queryString.append("desc");
        }

        return queryString.toString();
    }
}
