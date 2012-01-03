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

import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import com.abiquo.server.core.cloud.VirtualDatacenter;
import com.abiquo.server.core.cloud.VirtualMachine;
import com.abiquo.server.core.common.persistence.DefaultDAOBase;

@Repository("jpaDiskManagementDAO")
public class DiskManagementDAO extends DefaultDAOBase<Integer, DiskManagement>
{
    public static final String GET_DISK_INTO_VIRTUALMACHINE =
        " SELECT disk FROM DiskManagement disk" + " WHERE disk.virtualMachine.id = :idVm "
            + " AND disk.id = :idDisk ";

    public static final String GET_DISKS_INTO_VIRTUALMACHINE =
        " SELECT disk FROM DiskManagement disk" + " WHERE disk.virtualMachine.id = :idVm "
            + " ORDER BY disk.rasd.generation";

    public static final String GET_DISKS_INTO_VIRTUALDATACENTER =
        " SELECT disk FROM DiskManagement disk" + " WHERE disk.virtualDatacenter.id = :idVdc "
            + " ORDER BY disk.rasd.generation";

    public static final String GET_DISK_INTO_VIRTUALDATACENTER =
        " SELECT disk FROM DiskManagement disk" + " WHERE disk.virtualDatacenter.id = :idVdc "
            + " AND disk.id = :idDisk";

    public DiskManagementDAO()
    {
        super(DiskManagement.class);
    }

    public DiskManagementDAO(final EntityManager entityManager)
    {
        super(DiskManagement.class, entityManager);
    }

    public List<DiskManagement> findHardDisksByVirtualDatacenter(final VirtualDatacenter vdc)
    {
        Query finalQuery = getSession().createQuery(GET_DISKS_INTO_VIRTUALDATACENTER);
        finalQuery.setParameter("idVdc", vdc.getId());

        return finalQuery.list();
    }

    public DiskManagement findHardDiskByVirtualDatacenter(final VirtualDatacenter vdc,
        final Integer diskId)
    {
        Query finalQuery = getSession().createQuery(GET_DISK_INTO_VIRTUALDATACENTER);
        finalQuery.setParameter("idVdc", vdc.getId());
        finalQuery.setParameter("idDisk", diskId);

        return (DiskManagement) finalQuery.uniqueResult();
    }

    /**
     * Returns the list of disk by a virtual machine.
     * 
     * @param vm virtual machine object.
     * @return the list of disks.
     */
    @SuppressWarnings("unchecked")
    public List<DiskManagement> findHardDisksByVirtualMachine(final VirtualMachine vm)
    {
        Query finalQuery = getSession().createQuery(GET_DISKS_INTO_VIRTUALMACHINE);
        finalQuery.setParameter("idVm", vm.getId());

        return finalQuery.list();
    }

    /**
     * Returns the unique object from a virtual machine.
     * 
     * @param vm virtual machine that has the disk
     * @param diskOrder sequence order inside the virtual machine of the disk
     * @return the found {@link DiskManagement}
     */
    public DiskManagement findHardDiskByVirtualMachine(final VirtualMachine vm,
        final Integer diskId)
    {
        Query finalQuery = getSession().createQuery(GET_DISK_INTO_VIRTUALMACHINE);
        finalQuery.setParameter("idVm", vm.getId());
        finalQuery.setParameter("idDisk", diskId);

        return (DiskManagement) finalQuery.uniqueResult();
    }

}
