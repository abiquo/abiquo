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

package com.abiquo.abiserver.scheduler.limit.exception;

import java.util.List;

import com.abiquo.abiserver.business.hibernate.pojohb.virtualhardware.ResourceAllocationLimitHB;
import com.abiquo.abiserver.scheduler.limit.ResourceLimitStatus;
import com.abiquo.abiserver.scheduler.limit.VirtualMachineRequirements;
import com.abiquo.abiserver.scheduler.workload.exception.AllocatorException;

public class LimitExceededException extends AllocatorException
{
    private static final long serialVersionUID = 1052785278699629101L;

    private List<ResourceLimitStatus> resourcesStatus;

    /**
     * Resource limit exceeded for entity {Enterprise, Datacenter or VirtualDatacenter}
     */
    private Object entity; // TODO IPojoHB<IPojo< ? >>

    private VirtualMachineRequirements requirements;

    private ResourceAllocationLimitHB actual;

    public LimitExceededException(String cause, List<ResourceLimitStatus> resourcesStatus,
        Object entity, VirtualMachineRequirements requirements,
        ResourceAllocationLimitHB actual)
    {
        super(cause);
        this.entity = entity;
        this.resourcesStatus = resourcesStatus;
        this.requirements = requirements;
        this.actual = actual;
    }

    public List<ResourceLimitStatus> getResourcesStatus()
    {
        return resourcesStatus;
    }

    public Object getEntity()
    {
        return entity;
    }

    public VirtualMachineRequirements getRequirements()
    {
        return requirements;
    }

    public ResourceAllocationLimitHB getActual()
    {
        return actual;
    }

}
