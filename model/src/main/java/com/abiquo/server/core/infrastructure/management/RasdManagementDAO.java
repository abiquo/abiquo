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

package com.abiquo.server.core.infrastructure.management;

import java.util.List;

import javax.persistence.EntityManager;

import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.abiquo.server.core.cloud.VirtualDatacenter;
import com.abiquo.server.core.cloud.VirtualMachine;
import com.abiquo.server.core.common.persistence.DefaultDAOBase;
import com.abiquo.server.core.infrastructure.storage.DiskManagement;
import com.abiquo.server.core.infrastructure.storage.VolumeManagement;

@Repository("jpaRasdManagementDAO")
public class RasdManagementDAO extends DefaultDAOBase<Integer, RasdManagement>
{
    public RasdManagementDAO()
    {
        super(RasdManagement.class);
    }

    public RasdManagementDAO(final EntityManager entityManager)
    {
        super(RasdManagement.class, entityManager);
    }

    private static Criterion sameResourceType(final String idResourceType)
    {
        return Restrictions.eq(RasdManagement.ID_RESOURCE_TYPE_PROPERTY, idResourceType);
    }

    private static Criterion sameVirtualDatacenter(final VirtualDatacenter virtualDatacenter)
    {
        return Restrictions.eq(RasdManagement.VIRTUAL_DATACENTER_PROPERTY, virtualDatacenter);
    }

    private static Criterion sameVirtualMachine(final VirtualMachine virtualMachine)
    {
        return Restrictions.eq(RasdManagement.VIRTUAL_MACHINE_PROPERTY, virtualMachine);
    }

    @SuppressWarnings("unchecked")
    public List<RasdManagement> findByVirtualDatacenterAndResourceType(
        final VirtualDatacenter virtualDatacenter, final String resourceType)
    {
        return createCriteria(sameVirtualDatacenter(virtualDatacenter),
            sameResourceType(resourceType)).list();
    }

    public List<RasdManagement> findByVirtualMachine(final VirtualMachine virtualMachine)
    {
        return getResultList(createCriteria(sameVirtualMachine(virtualMachine)));
    }

    public List<RasdManagement> findDisksAndVolumesByVirtualMachine(
        final VirtualMachine virtualMachine)
    {
        Criteria crit = createCriteria();
        crit.createAlias(RasdManagement.RASD_PROPERTY, "rasd");

        // Add disk resource type filter
        crit.add(Restrictions.in(RasdManagement.ID_RESOURCE_TYPE_PROPERTY, new String[] {
        VolumeManagement.DISCRIMINATOR, DiskManagement.DISCRIMINATOR}));

        // Add virtual machine filter
        crit.add(sameVirtualMachine(virtualMachine));

        // Order by generation (attachment order)
        crit.addOrder(Order.asc("rasd." + Rasd.GENERATION_PROPERTY));

        return getResultList(crit);
    }
}
