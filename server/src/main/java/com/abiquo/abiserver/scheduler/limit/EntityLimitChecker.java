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

package com.abiquo.abiserver.scheduler.limit;

import java.util.LinkedList;
import java.util.List;

import com.abiquo.abiserver.business.hibernate.pojohb.infrastructure.DatacenterHB;
import com.abiquo.abiserver.business.hibernate.pojohb.user.EnterpriseHB;
import com.abiquo.abiserver.business.hibernate.pojohb.virtualappliance.VirtualDataCenterHB;
import com.abiquo.abiserver.business.hibernate.pojohb.virtualhardware.LimitHB;
import com.abiquo.abiserver.business.hibernate.pojohb.virtualhardware.ResourceAllocationLimitHB;
import com.abiquo.abiserver.scheduler.limit.ResourceLimitStatus.ResourceLimitRange;
import com.abiquo.abiserver.scheduler.limit.exception.HardLimitExceededException;
import com.abiquo.abiserver.scheduler.limit.exception.LimitExceededException;
import com.abiquo.abiserver.scheduler.limit.exception.SoftLimitExceededException;
import com.abiquo.abiserver.scheduler.workload.core.exception.ResourceAllocationException;
import com.abiquo.tracer.ComponentType;
import com.abiquo.tracer.EventType;
import com.abiquo.tracer.SeverityType;
import com.abiquo.tracer.client.TracerFactory;

/**
 * Check the current range for the total resource allocation limits on a provided entity, indicating
 * when exceed the soft or hard limit.
 * <p>
 * 
 * @param CHECK_ENTITY {@link EnterpriseHB}, {@link DatacenterHB} or {@link VirtualDataCenterHB}
 */
public abstract class EntityLimitChecker<CHECK_ENTITY extends Object>
{

    /**
     * Gets the limits defined on this entity.
     * 
     * @param entity, the entity to obtain its resource allocation limits.
     * @return null it haven't limits (so, don't check)
     */
    protected abstract ResourceAllocationLimitHB getLimit(CHECK_ENTITY entity);

    /**
     * Get the sum of all the resources allocated by a given entity.
     * <p>
     * 
     * @param entity, the target entity of the allocation count.
     * @return an ResourceAllocationLimit holding the total resource allocation on its <strong>hard
     *         limits</strong>, if some resource do not apply to the current entity set it to 0.
     */
    protected abstract ResourceAllocationLimitHB getActualAllocation(CHECK_ENTITY entity);

    /**
     * Discard limits not applied on the current entity.
     * 
     * @param limitStatus, the full list of resource limits.
     * @return the ResourceLimitStatus that applies to the current entity.
     */
    protected abstract List<ResourceLimitStatus> getFilterResourcesStatus(
        List<ResourceLimitStatus> limitStatus);

    /**
     * Check the being use resource utilization for the given entity.
     * 
     * @param entity, the entity to check its limit status.
     * @throws ResourceAllocationException, if can not obtain the total resource allocation or
     *             LimitExceededException, only {@link HardLimitExceededException}, actually force
     *             the soft limit.
     */
    public void checkCurrentLimits(final CHECK_ENTITY entity)
    {
        checkLimits(entity, new VirtualMachineRequirementsEmpty(), true);
    }

    /**
     * Check the resource allocation limits on the current entity when adding new resource
     * requirements.
     * 
     * @param entity, the entity to obtain its resource allocation limits.
     * @param required, new target resource requirements.
     * @param force, indicating if the soft limit reached should be reported as exception or only a
     *            <strong>event trace<strong> is created and the machine selection continues.
     * @throws LimitExceededException, {@link HardLimitExceededException} it the resource allocation
     *             hard limit is exceeded. {@link SoftLimitExceededException}, on force = false and
     *             the soft limit is exceeded.
     */
    public void checkLimits(final CHECK_ENTITY entity, final VirtualMachineRequirements required,
        final boolean force)
    {

        final ResourceAllocationLimitHB limits = getLimit(entity);

        if (limits == null || allNoLimits(limits))
        {
            return;
        }

        final ResourceAllocationLimitHB actualAllocated = getActualAllocation(entity);

        List<ResourceLimitStatus> entityResourceStatus =
            getResourcesStatus(entity, limits, actualAllocated, required);

        entityResourceStatus = getFilterResourcesStatus(entityResourceStatus);

        checkResourceLimits(force, entityResourceStatus, entity, required, actualAllocated);
    }

    /**
     * Gets the list of all resources with his limit status.
     */
    private List<ResourceLimitStatus> getResourcesStatus(final CHECK_ENTITY entity,
        final ResourceAllocationLimitHB limits, final ResourceAllocationLimitHB actualAllocated,
        final VirtualMachineRequirements required)
    {
        final List<ResourceLimitStatus> limitStatus = new LinkedList<ResourceLimitStatus>();

        final Long actualCpu = actualAllocated.getCpu().getHard();
        final Long actualRam = actualAllocated.getRam().getHard();
        final Long actualHd = actualAllocated.getHd().getHard();
        final Long actualStorage = actualAllocated.getStorage().getHard();
        final Long actualRepository = actualAllocated.getRepository().getHard();
        final Long actualPublicIp = actualAllocated.getPublicIP().getHard();
        final Long actualPublicVlan = actualAllocated.getVlan().getHard();

        limitStatus.add(limitStatus(actualCpu, required.getCpu(), limits.getCpu(), "CPU"));
        limitStatus.add(limitStatus(actualRam, required.getRam(), limits.getRam(), "RAM"));
        limitStatus.add(limitStatus(actualHd, required.getHd(), limits.getHd(), "HD"));

        limitStatus.add(limitStatus(actualStorage, required.getStorage(), limits.getStorage(),
            "Storage"));
        limitStatus.add(limitStatus(actualRepository, required.getRepository(),
            limits.getRepository(), "Repository"));

        limitStatus.add(limitStatus(actualPublicIp, required.getPublicIP(), limits.getPublicIP(),
            "Public IP"));
        limitStatus.add(limitStatus(actualPublicVlan, required.getPrivateVLAN(), limits.getVlan(),
            "Private VLAN"));

        return limitStatus;
    }

    /**
     * @return true if all the soft and hard limits are set to 0 (unlimited)
     */
    private boolean allNoLimits(final ResourceAllocationLimitHB limit)
    {
        return (limit.getCpu().getHard() == 0
            && limit.getCpu().getSoft() == 0
            && limit.getRam().getHard() == 0
            && limit.getRam().getSoft() == 0
            && limit.getHd().getHard() == 0
            && limit.getHd().getSoft() == 0
            && (limit.getRepository() == null || (limit.getRepository().getSoft() == 0 && limit
                .getRepository().getHard() == 0))
            && (limit.getPublicIP() == null || (limit.getPublicIP().getSoft() == 0 && limit
                .getPublicIP().getHard() == 0))
            && (limit.getVlan() == null || (limit.getVlan().getSoft() == 0 && limit.getVlan()
                .getHard() == 0)) && (limit.getStorage() == null || (limit.getStorage().getSoft() == 0 && limit
            .getStorage().getHard() == 0)));
    }

    /**
     * Determine the {@link ResourceLimitRange} for the current actual resource utilization, the new
     * resource requirements and the defined limits.
     * 
     * @param actual, current being used resource count
     * @param requried, new resource requirements
     * @param limit, the entity limit
     * @param type, the entity type
     * @return a range indicating where the resource limit is.
     */
    protected ResourceLimitStatus limitStatus(final long actual, final long required,
        final LimitHB limit, final String type)
    {
        final long current = actual + required;

        ResourceLimitRange range;

        if (current >= limit.getSoft())
        {
            if (current > limit.getHard() && limit.getHard() != 0)
            {
                range = ResourceLimitRange.HARD;
            }
            else if (limit.getSoft() != 0)
            {
                range = ResourceLimitRange.SOFT;
            }
            else
            {
                range = ResourceLimitRange.OK;
            }
        }
        else
        {
            range = ResourceLimitRange.OK;
        }

        return new ResourceLimitStatus(range, type);
    }

    /**
     * Throws an exception if some of the considered resource limit status exceed some limit.
     */
    private void checkResourceLimits(final boolean force,
        final List<ResourceLimitStatus> resourcesStatus, final CHECK_ENTITY entity,
        final VirtualMachineRequirements requirements, final ResourceAllocationLimitHB actual)
        throws SoftLimitExceededException, HardLimitExceededException
    {

        String entityName = getEntityName(entity);

        String softExceed = new String();
        String hardExceed = new String();

        for (final ResourceLimitStatus resource : resourcesStatus)
        {
            if (resource.getLimit() == ResourceLimitRange.HARD)
            {
                hardExceed += resource.getType() + "\n";

                TracerFactory.getTracer().log(SeverityType.MAJOR, ComponentType.WORKLOAD,
                    EventType.WORKLOAD_HARD_LIMIT_EXCEEDED,
                    String.format("%s exceed %s", entityName, resource.getType()));
            }
            else if (resource.getLimit() == ResourceLimitRange.SOFT)
            {
                if (force)
                {
                    TracerFactory.getTracer().log(SeverityType.WARNING, ComponentType.WORKLOAD,
                        EventType.WORKLOAD_SOFT_LIMIT_EXCEEDED,
                        String.format("%s exceed %s", entityName, resource.getType()));
                }
                else
                {
                    softExceed += resource.getType() + "\n";
                }
            }
        } // all resources

        if (!hardExceed.isEmpty())
        {
            final String cause = String.format("%s exceed %s hard limits", entityName, hardExceed);

            throw new HardLimitExceededException(cause,
                resourcesStatus,
                entity,
                requirements,
                actual);
        }

        if (!softExceed.isEmpty())
        {
            final String cause = String.format("%s exceed % soft limits", entityName, softExceed);

            throw new SoftLimitExceededException(cause,
                resourcesStatus,
                entity,
                requirements,
                actual);
        }

    }

    abstract String getEntityName(CHECK_ENTITY entity);

}
