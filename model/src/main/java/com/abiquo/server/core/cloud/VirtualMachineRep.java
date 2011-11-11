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

import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.abiquo.server.core.cloud.chef.RunlistElement;
import com.abiquo.server.core.cloud.chef.RunlistElementDAO;
import com.abiquo.server.core.common.DefaultRepBase;
import com.abiquo.server.core.enterprise.Enterprise;
import com.abiquo.server.core.enterprise.User;
import com.abiquo.server.core.infrastructure.management.RasdManagement;
import com.abiquo.server.core.infrastructure.management.RasdManagementDAO;

@Repository
public class VirtualMachineRep extends DefaultRepBase
{

    /* package: test only */static final String BUG_INSERT_NAME_MUST_BE_UNIQUE =
        "ASSERT- insert: virtual machine name must be unique";

    @Autowired
    private VirtualMachineDAO dao;

    @Autowired
    private RasdManagementDAO rasdDao;

    @Autowired
    private VirtualImageDAO imageDao;

    @Autowired
    private RunlistElementDAO chefDao;

    public VirtualMachineRep()
    {

    }

    public VirtualMachineRep(final EntityManager entityManager)
    {
        assert entityManager != null;
        assert entityManager.isOpen();

        this.entityManager = entityManager;

        this.dao = new VirtualMachineDAO(entityManager);
        this.rasdDao = new RasdManagementDAO(entityManager);
        this.chefDao = new RunlistElementDAO(entityManager);
    }

    public Collection<VirtualMachine> findByHypervisor(final Hypervisor hypervisor)
    {
        assert hypervisor != null;
        return dao.findVirtualMachines(hypervisor);
    }

    public Collection<VirtualMachine> findManagedByHypervisor(final Hypervisor hypervisor)
    {
        assert hypervisor != null;
        return dao.findManagedVirtualMachines(hypervisor);
    }

    public Collection<VirtualMachine> findByEnterprise(final Enterprise enterprise)
    {
        assert enterprise != null;
        return dao.findVirtualMachinesByEnterprise(enterprise);
    }

    public List<VirtualMachine> findVirtualMachinesByUser(final Enterprise enterprise,
        final User user)
    {
        return dao.findVirtualMachinesByUser(enterprise, user);
    }

    public List<VirtualMachine> findVirtualMachinesByVirtualAppliance(final Integer vappId)
    {
        return dao.findVirtualMachinesByVirtualAppliance(vappId);
    }

    public VirtualMachine findByUUID(final String uuid)
    {
        return dao.findByUUID(uuid);
    }

    public VirtualMachine findByName(final String name)
    {
        return dao.findByName(name);
    }

    public VirtualMachine findVirtualMachineById(final Integer vmId)
    {
        return dao.findById(vmId);
    }

    public VirtualMachine findVirtualMachineByHypervisor(final Hypervisor hypervisor,
        final Integer virtualMachineId)
    {
        return dao.findVirtualMachineByHypervisor(hypervisor, virtualMachineId);
    }

    public void deleteNotManagedVirtualMachines(final Hypervisor hypervisor)
    {
        dao.deleteNotManagedVirtualMachines(hypervisor);
    }

    public void update(final VirtualMachine vm)
    {
        dao.flush();
    }

    public Collection<RasdManagement> findRasdManagementByVirtualMachine(
        final VirtualMachine virtualMachine)
    {
        return rasdDao.findByVirtualMachine(virtualMachine);
    }

    /**
     * Delete a {@link VirtualMachine}.
     * 
     * @param virtualMachine to delete. void
     */
    public void deleteVirtualMachine(final VirtualMachine virtualMachine)
    {
        this.dao.remove(virtualMachine);
    }

    /**
     * Persists a {@link VirtualMachine}.
     * 
     * @param virtualMachine to create. void
     */
    public VirtualMachine createVirtualMachine(final VirtualMachine virtualMachine)
    {
        this.dao.persist(virtualMachine);
        return virtualMachine;
    }

    /**
     * Retrieve a {@link VirtualImage}.
     * 
     * @param virtualImage id.
     * @return
     */
    public VirtualImage getVirtualImage(final Integer id)
    {

        return this.imageDao.findById(id);
    }

    public void insert(final VirtualMachine virtualMachine)
    {
        assert virtualMachine != null;
        assert !this.dao.isManaged(virtualMachine);
        assert !this.dao.existsAnyWithName(virtualMachine.getName()) : BUG_INSERT_NAME_MUST_BE_UNIQUE;

        this.dao.persist(virtualMachine);
        this.dao.flush();
    }

    public RunlistElement findRunlistElementById(final Integer id)
    {
        return chefDao.findById(id);
    }

    public void insertRunlistElement(final RunlistElement runlistElement)
    {
        chefDao.persist(runlistElement);
    }

    public void updateRunlistElements()
    {
        chefDao.flush();
    }

    public void deleteRunlistElement(final RunlistElement runlistElement)
    {
        chefDao.remove(runlistElement);
    }

    public List<RunlistElement> findRunlistByVirtualMachine(final VirtualMachine virtualMachine)
    {
        return chefDao.findByVirtualMachine(virtualMachine);
    }

    public void clearVirtualMachineRunlist(final VirtualMachine virtualMachine)
    {
        chefDao.clearVirtualMachineRunlist(virtualMachine);
    }

}
