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

import com.abiquo.server.core.cloud.VirtualMachine.OrderByEnum;
import com.abiquo.server.core.cloud.chef.RunlistElement;
import com.abiquo.server.core.cloud.chef.RunlistElementDAO;
import com.abiquo.server.core.common.DefaultRepBase;
import com.abiquo.server.core.common.persistence.JPAConfiguration;
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
    private RunlistElementDAO chefDao;

    @Autowired
    private NodeVirtualImageDAO nodeVirtualImageDAO;

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
        this.nodeVirtualImageDAO = new NodeVirtualImageDAO(entityManager);
    }

    public Collection<VirtualMachine> findByHypervisor(final Hypervisor hypervisor)
    {
        assert hypervisor != null;
        return dao.findVirtualMachines(hypervisor);
    }

    public Collection<VirtualMachine> findManagedByHypervisor(final Hypervisor hypervisor)
    {
        return dao.findManagedVirtualMachines(hypervisor);
    }

    public Collection<VirtualMachine> findVirtualMachinesNotAllocatedCompatibleHypervisor(
        final Hypervisor hypervisor)
    {
        assert hypervisor != null;
        return dao.findVirtualMachinesNotAllocatedCompatibleHypervisor(hypervisor);
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

    public List<VirtualMachine> findVirtualMachinesByVirtualAppliance(final Integer vappId,
        final Integer startwith, final Integer limit, final String filter,
        final OrderByEnum orderByEnum, final Boolean descOrAsc)
    {
        return dao.findVirtualMachinesByVirtualAppliance(vappId, startwith, limit, filter,
            orderByEnum, descOrAsc);
    }

    public List<VirtualMachine> findByVirtualMachineTemplate(final Integer virtualMachineTemplateId)
    {
        return dao.findByVirtualMachineTemplate(virtualMachineTemplateId);
    }

    public boolean hasVirtualMachineTemplate(final Integer virtualMachineTemplateId)
    {
        return dao.hasVirtualMachineTemplate(virtualMachineTemplateId);
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

    /**
     * * Sets the {@link VirtualMachine#setState(VirtualMachineState)} to
     * {@link VirtualMachineState#UNKNOWN}.
     * 
     * @param vmId id void
     */
    public void setVirtualMachineToUnknown(final Integer vmId)
    {
        dao.unknownState(vmId);
    }

    public VirtualMachine findVirtualMachineByHypervisor(final Hypervisor hypervisor,
        final Integer virtualMachineId)
    {
        return dao.findVirtualMachineByHypervisor(hypervisor, virtualMachineId);
    }

    public void update(final VirtualMachine vm)
    {
        dao.flush();
    }

    public void refreshLock(final VirtualMachine vm)
    {
        dao.refreshLock(vm);
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

    public NodeVirtualImage insertNodeVirtualImage(final NodeVirtualImage nodeVirtualImage)
    {
        nodeVirtualImageDAO.persist(nodeVirtualImage);
        return nodeVirtualImage;
    }

    public NodeVirtualImage findNodeVirtualImageByVm(final VirtualMachine virtualMachine)
    {

        return nodeVirtualImageDAO.findByVirtualMachine(virtualMachine);

    }

    public List<NodeVirtualImage> findNodeVirtualImagesByVirtualAppliance(
        final VirtualAppliance virtualAppliance)
    {

        return nodeVirtualImageDAO.findByVirtualAppliance(virtualAppliance);

    }

    public void updateNodeVirtualImage(final NodeVirtualImage nodeVirtualImage)
    {
        nodeVirtualImageDAO.flush();
    }

    public void deleteNodeVirtualImage(final NodeVirtualImage nodeVirtualImage)
    {
        nodeVirtualImageDAO.remove(nodeVirtualImage);
    }

    public VirtualMachine findBackup(final VirtualMachine vmachine)
    {
        return dao.findBackup(vmachine);
    }

    public void refresh(final VirtualMachine vmachine)
    {
        dao.refresh(vmachine);
    }

    public void detachHypervisor(final VirtualMachine vm)
    {
        dao.detachHypervisor(vm);
    }

    public void detachVirtualMachine(final VirtualMachine vm)
    {
        dao.detachVirtualMachine(vm);
    }

    public void setDefaultFilters()
    {
        JPAConfiguration.enableDefaultFilters(this.entityManager);
    }

    public boolean existsVirtualMachineFromTemplate(final Integer virtualMachineTemplateId)
    {
        return dao.hasVirtualMachineTemplate(virtualMachineTemplateId);
    }
}
