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

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.abiquo.server.core.cloud.stateful.VirtualApplianceStatefulConversion;
import com.abiquo.server.core.cloud.stateful.VirtualApplianceStatefulConversionDAO;
import com.abiquo.server.core.common.DefaultRepBase;

@Repository
public class VirtualApplianceRep extends DefaultRepBase
{
    @Autowired
    private VirtualApplianceDAO virtualApplianceDao;

    @Autowired
    private VirtualMachineDAO virtualMachineDao;

    @Autowired
    private NodeVirtualImageDAO nodeVirtualImageDao;

    private VirtualApplianceStatefulConversionDAO vAppSConversionDao;

    public VirtualApplianceRep()
    {

    }

    public VirtualApplianceRep(final EntityManager em)
    {
        this.entityManager = em;

        this.virtualApplianceDao = new VirtualApplianceDAO(em);
        this.virtualMachineDao = new VirtualMachineDAO(em);
        this.nodeVirtualImageDao = new NodeVirtualImageDAO(em);
        this.vAppSConversionDao = new VirtualApplianceStatefulConversionDAO(em);
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
        return vAppSConversionDao.findById(id);
    }
}
