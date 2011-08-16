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

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.abiquo.model.enumerator.VirtualMachineState;
import com.abiquo.server.core.common.persistence.DefaultDAOBase;
import com.abiquo.server.core.enterprise.Enterprise;
import com.abiquo.server.core.enterprise.User;

@Repository("jpaVirtualMachineDAO")
public class VirtualMachineDAO extends DefaultDAOBase<Integer, VirtualMachine>
{
    public VirtualMachineDAO()
    {
        super(VirtualMachine.class);
    }

    public VirtualMachineDAO(final EntityManager entityManager)
    {
        super(VirtualMachine.class, entityManager);
    }

    private static Criterion sameHypervisor(final Hypervisor hypervisor)
    {
        assert hypervisor != null;

        return Restrictions.eq(VirtualMachine.HYPERVISOR_PROPERTY, hypervisor);
    }

    private static Criterion sameEnterprise(final Enterprise enterprise)
    {
        assert enterprise != null;

        return Restrictions.eq(VirtualMachine.ENTERPRISE_PROPERTY, enterprise);
    }

    private static Criterion sameUser(final User user)
    {
        return Restrictions.eq(VirtualMachine.USER_PROPERTY, user);
    }

    private Criterion notManaged()
    {
        return Restrictions.eq(VirtualMachine.ID_TYPE_PROPERTY, VirtualMachine.NOT_MANAGED);
    }

    private Criterion managed()
    {
        return Restrictions.eq(VirtualMachine.ID_TYPE_PROPERTY, VirtualMachine.MANAGED);
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

    public List<VirtualMachine> findManagedVirtualMachines(final Hypervisor hypervisor)
    {
        assert hypervisor != null;
        assert isManaged2(hypervisor);

        Criteria criteria = createCriteria(sameHypervisor(hypervisor), managed());
        criteria.addOrder(Order.asc(VirtualMachine.NAME_PROPERTY));
        List<VirtualMachine> result = getResultList(criteria);
        return result;
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

    public VirtualMachine findByName(final String name)
    {
        return findUniqueByProperty(VirtualMachine.NAME_PROPERTY, name);
    }

    public VirtualMachine findByUUID(final String uuid)
    {
        return findUniqueByProperty(VirtualMachine.UUID_PROPERTY, uuid);
    }

    public List<VirtualMachine> findVirtualMachinesByVirtualAppliance(final Integer vappId)
    {
        List<VirtualMachine> vmList = null;
        TypedQuery<VirtualMachine> query =
            getEntityManager().createNamedQuery("VIRTUAL_MACHINE.BY_VAPP", VirtualMachine.class);
        query.setParameter("vapp_id", vappId);
        vmList = query.getResultList();

        return vmList;
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

    public List<VirtualMachine> findVirtualMachinesByDatacenter(final Integer datacenterId)
    {
        List<VirtualMachine> vmList = null;
        TypedQuery<VirtualMachine> query =
            getEntityManager().createNamedQuery("VIRTUAL_MACHINE.BY_DC", VirtualMachine.class);
        query.setParameter("datacenterId", datacenterId);
        vmList = query.getResultList();

        return vmList;
    }

    public void updateVirtualMachineState(final Integer vmachineId, final VirtualMachineState state)
    {
        VirtualMachine vmachine = findById(vmachineId);

        vmachine.setState(state);

        flush();
    }
}
