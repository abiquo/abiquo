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

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.abiquo.server.core.appslibrary.VirtualImageConversion;
import com.abiquo.server.core.cloud.stateful.DiskStatefulConversion;
import com.abiquo.server.core.cloud.stateful.DiskStatefulConversionDAO;
import com.abiquo.server.core.cloud.stateful.NodeVirtualImageStatefulConversion;
import com.abiquo.server.core.cloud.stateful.NodeVirtualImageStatefulConversionDAO;
import com.abiquo.server.core.cloud.stateful.VirtualApplianceStatefulConversion;
import com.abiquo.server.core.cloud.stateful.VirtualApplianceStatefulConversionDAO;
import com.abiquo.server.core.common.DefaultRepBase;

@Repository
public class VirtualApplianceRep extends DefaultRepBase
{
    @Autowired
    private VirtualApplianceDAO virtualApplianceDao;

    @Autowired
    private NodeVirtualImageDAO nodeVirtualImageDao;

    @Autowired
    private VirtualApplianceStatefulConversionDAO vAppStatefulConversionDao;

    @Autowired
    private NodeVirtualImageStatefulConversionDAO nodeVirtualImageStatefulConversioDao;

    @Autowired
    private DiskStatefulConversionDAO diskStatefulConversionDao;

    @Autowired
    private VirtualImageConversionDAO virtualImageConversionDao;

    public VirtualApplianceRep()
    {

    }

    public VirtualApplianceRep(final EntityManager em)
    {
        this.entityManager = em;

        this.virtualApplianceDao = new VirtualApplianceDAO(em);
        this.nodeVirtualImageDao = new NodeVirtualImageDAO(em);
        this.vAppStatefulConversionDao = new VirtualApplianceStatefulConversionDAO(em);
        this.nodeVirtualImageStatefulConversioDao = new NodeVirtualImageStatefulConversionDAO(em);
        this.diskStatefulConversionDao = new DiskStatefulConversionDAO(em);
        this.virtualImageConversionDao = new VirtualImageConversionDAO(em);
    }

    public VirtualAppliance findVirtualApplianceByVirtualMachine(final VirtualMachine virtualMachine)
    {
        return nodeVirtualImageDao.findVirtualAppliance(virtualMachine);
    }

    public VirtualAppliance findById(final Integer id)
    {
        return virtualApplianceDao.findById(id);
    }

    public void updateVirtualAppliance(final VirtualAppliance virtualAppliance)
    {
        this.virtualApplianceDao.flush();
    }

    public VirtualApplianceStatefulConversion findConversionById(final Integer id)
    {
        return vAppStatefulConversionDao.findById(id);
    }

    public NodeVirtualImageStatefulConversion findNodeVirtualImageStatefulConversionById(
        final Integer id)
    {
        return nodeVirtualImageStatefulConversioDao.findById(id);
    }

    public Collection<NodeVirtualImageStatefulConversion> findNodeVirtualImageStatefulConversionsByVirtualImageConversion(
        final VirtualImageConversion virtualImageConversion)
    {
        return nodeVirtualImageStatefulConversioDao
            .findByVirtualImageConversion(virtualImageConversion);
    }

    public DiskStatefulConversion insertDiskStatefulConversion(final DiskStatefulConversion dsc)
    {
        diskStatefulConversionDao.persist(dsc);
        diskStatefulConversionDao.flush();

        return dsc;
    }

    public void updateNodeVirtualImageStatefulConversion(
        final NodeVirtualImageStatefulConversion nvisc)
    {
        nodeVirtualImageStatefulConversioDao.flush();
    }

    public VirtualImageConversion findVirtualImageConversionById(final Integer id)
    {
        return virtualImageConversionDao.findById(id);
    }

    public void updateVirtualImageConversion(final VirtualImageConversion vic)
    {
        virtualImageConversionDao.flush();
    }

    public Collection<NodeVirtualImageStatefulConversion> findNodeVirtualImageConversionByVirtualAppliance(
        final VirtualAppliance virtualAppliance)
    {
        return nodeVirtualImageStatefulConversioDao.findByVirtualAppliance(virtualAppliance);
    }

    public Collection<NodeVirtualImageStatefulConversion> findNodeVirtualImageConversionByVirtualApplianceStatefulConversion(
        final VirtualApplianceStatefulConversion virtualApplianceStategulConversion)
    {

        return nodeVirtualImageStatefulConversioDao
            .findByVirtualApplianceStatefulConversion(virtualApplianceStategulConversion);
    }

    public void deleteDiskStatefulConversion(final DiskStatefulConversion diskStatefulConversion)
    {
        diskStatefulConversionDao.remove(diskStatefulConversion);
    }

    public DiskStatefulConversion findDiskStatefulConversionById(final Integer id)
    {
        return diskStatefulConversionDao.findById(id);
    }

    public void deleteVirtualImageConversion(final VirtualImageConversion virtualImageConversion)
    {
        virtualImageConversionDao.remove(virtualImageConversion);
    }

    public void deleteNodeVirtualImageStatefulConversion(
        final NodeVirtualImageStatefulConversion nodeVirtualImageStatefulConversion)
    {
        nodeVirtualImageStatefulConversioDao.remove(nodeVirtualImageStatefulConversion);
    }

    public void deleteVirtualApplianceStatefulConversion(
        final VirtualApplianceStatefulConversion virtualApplianceStatefulConversion)
    {
        vAppStatefulConversionDao.remove(virtualApplianceStatefulConversion);
    }

    public void updateNodeVirtualImage(final NodeVirtualImage nodeVirtualImage)
    {
        nodeVirtualImageDao.flush();
    }

    public void deleteVirtualAppliance(final VirtualAppliance virtualAppliance)
    {
        virtualApplianceDao.remove(virtualAppliance);
    }

}
