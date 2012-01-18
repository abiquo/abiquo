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
import com.abiquo.server.core.scheduler.VirtualMachineRequirements;

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

    private final static Long BYTES_TO_MB = 1024l * 1024l;

    private boolean isHardLimit;

    public LimitExceededException(final boolean isHardLimit,
        final Map<LimitResource, LimitStatus> resourcesStatus,
        final DefaultEntityWithLimits entity, final VirtualMachineRequirements requirements,
        final DefaultEntityCurrentUsed actual, final String entityId)
    {
        super("Limit exceeded");
        this.entity = entity;
        this.status = resourcesStatus;
        this.requir = requirements;
        this.actual = actual;
        this.entityId = entityId;
        this.isHardLimit = isHardLimit;
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
            "%s ; \n"
                + //
                "USED\t       CPU=%d \t RAM=%d MB \t HD=%d MB \t Storage=%d MB \t VLAN=%d \t IP=%d \t ; \n"
                + //
                "REQUIRED\t   CPU=%d \t RAM=%d MB \t HD=%d MB \t Storage=%d MB \t VLAN=%d \t IP=%d \t ; \n"
                + //
                "SOFT LIMIT\t CPU=%d \t RAM=%d MB \t HD=%d MB \t Storage=%d MB \t VLAN=%d \t IP=%d \t ; \n"
                + //
                "HARD LIMIT\t CPU=%d \t RAM=%d MB \t HD=%d MB \t Storage=%d MB \t VLAN=%d \t IP=%d \t ; \n"
                + //
                "STATUS\t     CPU=%s \t RAM=%s \t HD=%s \t Storage=%s \t VLAN=%s \t IP=%s \t ; \n", //
            entityId, //
            actual.getCpu(),
            actual.getRamInMb(),
            (actual.getHdInMb() / BYTES_TO_MB),
            (actual.getStorage() / BYTES_TO_MB),
            actual.getVlanCount(),
            actual.getPublicIp(), //
            requir.getCpu(),
            requir.getRam(),
            (requir.getHd() / BYTES_TO_MB),
            (requir.getStorage() / BYTES_TO_MB),
            requir.getPublicVLAN(),
            requir.getPublicIP(), //
            entity.getCpuCountSoftLimit(),
            entity.getRamSoftLimitInMb(),
            (entity.getHdSoftLimitInMb() / BYTES_TO_MB),
            (entity.getStorageSoft() / BYTES_TO_MB),
            entity.getVlanSoft(),
            entity.getPublicIpsSoft(), //
            entity.getCpuCountHardLimit(), entity.getRamHardLimitInMb(),
            (entity.getHdHardLimitInMb() / BYTES_TO_MB),
            (entity.getStorageHard() / BYTES_TO_MB),
            entity.getVlanHard(),
            entity.getPublicIpsHard(), //
            status.get(LimitResource.CPU),
            status.get(LimitResource.RAM),
            status.get(LimitResource.HD), //
            status.get(LimitResource.STORAGE), status.get(LimitResource.VLAN),
            status.get(LimitResource.PUBLICIP) //
            );
    }

    public boolean isHardLimit()
    {
        return isHardLimit;
    }
}
