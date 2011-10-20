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

import com.abiquo.server.core.cloud.VirtualMachine;
import com.abiquo.server.core.common.persistence.DefaultDAOBase;
import com.abiquo.server.core.infrastructure.management.RasdManagement;

@Repository("jpaDiskManagementDAO")
public class DiskManagementDAO extends DefaultDAOBase<Integer, DiskManagement>
{
    public DiskManagementDAO()
    {
        super(DiskManagement.class);
    }

    public DiskManagementDAO(final EntityManager entityManager)
    {
        super(DiskManagement.class, entityManager);
    }

    @SuppressWarnings("unchecked")
    public List<DiskManagement> findHardDisksByVirtualMachine(final VirtualMachine vm)
    {
        Criteria crit =
            createCriteria(Restrictions.eq(RasdManagement.VIRTUAL_MACHINE_PROPERTY, vm));
        return crit.list();
    }

}
