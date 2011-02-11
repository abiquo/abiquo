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

package com.abiquo.scheduler.limit;

import java.util.Map;

import com.abiquo.scheduler.limit.EntityLimitChecker.LimitResource;
import com.abiquo.scheduler.workload.AllocatorException;
import com.abiquo.server.core.common.DefaultEntityCurrentUsed;
import com.abiquo.server.core.common.DefaultEntityWithLimits;
import com.abiquo.server.core.common.DefaultEntityWithLimits.LimitStatus;

public class LimitExceededException extends AllocatorException
{
    private static final long serialVersionUID = 1052785278699629101L;

    private Map<LimitResource, LimitStatus> status;

    /**
     * Resource limit exceeded for entity {Enterprise, Datacenter or VirtualDatacenter}
     */
    private DefaultEntityWithLimits entity;

    private VirtualMachineRequirements requir;

    private DefaultEntityCurrentUsed actual;

    private String entityId;

    public LimitExceededException(Map<LimitResource, LimitStatus> resourcesStatus,
        DefaultEntityWithLimits entity, VirtualMachineRequirements requirements,
        DefaultEntityCurrentUsed actual, String entityId)
    {
        super("Limit exceeded");
        this.entity = entity;
        this.status = resourcesStatus;
        this.requir = requirements;
        this.actual = actual;
        this.entityId = entityId;
    }

    public String getEntityId()
    {
        return entityId;
    }

    public Map<LimitResource, LimitStatus> getResourcesStatus()
    {
        return status;
    }

    public DefaultEntityWithLimits getEntity()
    {
        return entity;
    }

    public VirtualMachineRequirements getRequirements()
    {
        return requir;
    }

    public DefaultEntityCurrentUsed getActual()
    {
        return actual;
    }

    @Override
    public String toString()
    {
        return String.format(
            //
            "%s ; \n" + //
                "USED\t       CPU=%d \t RAM=%d \t HD=%d \t Storage=%d \t VLAN=%d \t IP=%d \t ; \n" + //
                "REQUIRED\t   CPU=%d \t RAM=%d \t HD=%d \t Storage=%d \t VLAN=%d \t IP=%d \t ; \n" + //
                "SOFT LIMIT\t CPU=%d \t RAM=%d \t HD=%d \t Storage=%d \t VLAN=%d \t IP=%d \t ; \n" + //
                "HARD LIMIT\t CPU=%d \t RAM=%d \t HD=%d \t Storage=%d \t VLAN=%d \t IP=%d \t ; \n" + //
                "STATUS\t     CPU=%s \t RAM=%s \t HD=%s \t Storage=%s \t VLAN=%s \t IP=%s \t ; \n", //
            entityId, //
            actual.getCpu(),
            actual.getRamInMb(),
            actual.getHdInMb(),
            actual.getStorage(),
            actual.getVlanCount(),
            actual.getPublicIp(), //
            requir.getCpu(),
            requir.getRam(),
            requir.getHd(),
            requir.getStorage(),
            requir.getPublicVLAN(),
            requir.getPublicIP(), //
            entity.getCpuCountSoftLimit(),
            entity.getRamSoftLimitInMb(),
            entity.getHdSoftLimitInMb(),
            entity.getStorageSoft(),
            entity.getVlanSoft(),
            entity.getPublicIpsSoft(), //
            entity.getCpuCountHardLimit(), entity.getRamHardLimitInMb(),
            entity.getHdHardLimitInMb(),
            entity.getStorageHard(),
            entity.getVlanHard(),
            entity.getPublicIpsHard(), //
            status.get(LimitResource.CPU),
            status.get(LimitResource.RAM),
            status.get(LimitResource.HD), //
            status.get(LimitResource.STORAGE), status.get(LimitResource.VLAN),
            status.get(LimitResource.PUBLICIP) //
            );
    }
}
