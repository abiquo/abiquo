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

import java.util.HashMap;
import java.util.Map;

import javax.jms.ResourceAllocationException;

import com.abiquo.server.core.cloud.VirtualDatacenter;
import com.abiquo.server.core.common.DefaultEntityCurrentUsed;
import com.abiquo.server.core.common.DefaultEntityWithLimits;
import com.abiquo.server.core.common.DefaultEntityWithLimits.LimitStatus;
import com.abiquo.server.core.enterprise.Enterprise;
import com.abiquo.server.core.infrastructure.Datacenter;
import com.abiquo.tracer.ComponentType;
import com.abiquo.tracer.EventType;
import com.abiquo.tracer.Platform;
import com.abiquo.tracer.SeverityType;
import com.abiquo.tracer.client.TracerFactory;

/**
 * Check the current range for the total resource allocation limits on a provided entity, indicating
 * when exceed the soft or hard limit.
 * <p>
 * 
 * @param T {@link Enterprise}, {@link Datacenter} or {@link VirtualDataCenter}
 */
public abstract class EntityLimitChecker<T extends DefaultEntityWithLimits>
{

    enum LimitResource
    {
        CPU, RAM, HD, STORAGE, VLAN, PUBLICIP;
    }

    /**
     * Gets the limits defined on this entity.
     * 
     * @param entity, the entity to obtain its resource allocation limits.
     * @return null it haven't limits (so, don't check)<br>
     *         protected abstract ResourceAllocationLimitHB getLimit(CHECK_ENTITY entity);
     */

    /**
     * Get the sum of all the resources allocated by a given entity.
     * <p>
     * 
     * @param entity, the target entity of the allocation count.
     * @return an ResourceAllocationLimit holding the total resource allocation on its <strong>hard
     *         limits</strong>, if some resource do not apply to the current entity set it to 0.
     */
    protected abstract DefaultEntityCurrentUsed getCurrentUsed(T entity);

    /**
     * Create the entity identifier (used to trace)
     */
    protected abstract String getEntityIdentifier(T entity);

    /**
     * Discard limits not applied on the current entity.
     * 
     * @param limitStatus, the full list of resource limits.
     * @return the ResourceLimitStatus that applies to the current entity.
     */
    protected abstract Map<LimitResource, LimitStatus> getFilterResourcesStatus(
        Map<LimitResource, LimitStatus> limitStatus);

    /**
     * Check the being use resource utilization for the given entity.
     * 
     * @param entity, the entity to check its limit status.
     * @throws ResourceAllocationException, if can not obtain the total resource allocation or
     *             LimitExceededException, only {@link HardLimitExceededException}, actually force
     *             the soft limit.
     */
    public void checkCurrentLimits(final T entity)
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
    public void checkLimits(final T entity, final VirtualMachineRequirements required,
        final boolean force) throws LimitExceededException
    {
        if (allNoLimits(entity))
        {
            return;
        }

        final DefaultEntityCurrentUsed actualAllocated = getCurrentUsed(entity);

        Map<LimitResource, LimitStatus> entityResourceStatus =
            getResourcesLimit(entity, actualAllocated, required);

        entityResourceStatus = getFilterResourcesStatus(entityResourceStatus);

        checkResourceLimits(force, entityResourceStatus, entity, required, actualAllocated);
    }

    /**
     * Gets the list of all resources with his limit status.
     */
    private Map<LimitResource, LimitStatus> getResourcesLimit(final T limits,
        final DefaultEntityCurrentUsed actualAllocated, final VirtualMachineRequirements required)
    {

        Map<LimitResource, LimitStatus> limitStatus =
            new HashMap<EntityLimitChecker.LimitResource, DefaultEntityWithLimits.LimitStatus>();

        int actualAndRequiredCpu = (int) (actualAllocated.getCpu() + required.getCpu());
        int actualAndRequiredRam = (int) (actualAllocated.getRamInMb() + required.getRam());
        long actualAndRequiredHd = actualAllocated.getHdInMb() + required.getHd();
        long actualAndRequiredStorage = actualAllocated.getStorage() + required.getStorage();

        limitStatus.put(LimitResource.CPU, limits.checkCpuStatus(actualAndRequiredCpu));
        limitStatus.put(LimitResource.RAM, limits.checkRamStatus(actualAndRequiredRam));
        limitStatus.put(LimitResource.HD, limits.checkHdStatus(actualAndRequiredHd));
        limitStatus.put(LimitResource.STORAGE, limits.checkStorageStatus(actualAndRequiredStorage));

        /**
         * TODO vlan and public ip is not checked there
         **/
        return limitStatus;
    }

    /**
     * @return true if all the soft and hard limits are set to 0 (unlimited)
     */
    private boolean allNoLimits(final T limit)
    {
        return (limit.getCpuCountLimits().isNoLimit() //
            && limit.getRamLimitsInMb().isNoLimit() //
            && limit.getHdLimitsInMb().isNoLimit() //
            && (limit.getVlansLimits() == null || limit.getVlansLimits().isNoLimit()) //
            && (limit.getStorageLimits() == null || limit.getStorageLimits().isNoLimit()) //
        && (limit.getPublicIPLimits() == null || limit.getPublicIPLimits().isNoLimit()));
    }

    /**
     * Throws an exception if some of the considered resource limit status exceed some limit. Also
     * trace when some are exceeded.
     */
    private void checkResourceLimits(final boolean force,
        final Map<LimitResource, LimitStatus> statusMap, final T entity,
        final VirtualMachineRequirements requirements, final DefaultEntityCurrentUsed actual)
        throws LimitExceededException
    {

        LimitStatus totalLimitStatus;

        if (statusMap.containsValue(LimitStatus.HARD_LIMIT))// statusMap.keySet().contains(LimitStatus.HARD_LIMIT))
        {
            totalLimitStatus = LimitStatus.HARD_LIMIT;
        }
        else if (statusMap.containsValue(LimitStatus.SOFT_LIMIT))// (statusMap.keySet().contains(LimitStatus.SOFT_LIMIT))
        {
            totalLimitStatus = LimitStatus.SOFT_LIMIT;
        }
        else
        {
            totalLimitStatus = LimitStatus.OK;
        }

        if (totalLimitStatus != LimitStatus.OK)
        {
            LimitExceededException exc =
                new LimitExceededException(statusMap,
                    entity,
                    requirements,
                    actual,
                    getEntityIdentifier(entity));

            traceLimit(totalLimitStatus == LimitStatus.HARD_LIMIT, force, entity, exc);
        }
    }

    private void traceLimit(boolean hard, boolean force, T entity, LimitExceededException except)
    {
        final String entityId = getEntityIdentifier(entity);

        final EventType etype =
            hard ? EventType.WORKLOAD_HARD_LIMIT_EXCEEDED : EventType.WORKLOAD_SOFT_LIMIT_EXCEEDED;

        String traceMessage = String.format("Not enough resources on %s", entityId);
        switch (traceSystem(entity))
        {
            case DETAIL:
                traceMessage = except.toString();
            case NO_DETAIL:
                TracerFactory.getTracer().log(SeverityType.MAJOR, ComponentType.WORKLOAD, etype,
                    traceMessage, Platform.SYSTEM_PLATFORM);
                break;
            default:
                break;
        }

        traceMessage = String.format("Not enough resources on %s", entityId);
        switch (traceEnterprise(entity, force))
        {
            case DETAIL:
                traceMessage = except.toString();
            case NO_DETAIL:
                TracerFactory.getTracer().log(SeverityType.MAJOR, ComponentType.WORKLOAD, etype,
                    traceMessage);
                break;
            default:
                break;
        }

        switch (returnExcption(entity, hard, force))
        {
            case DETAIL:
                throw except;

            case NO_DETAIL:
                throw new LimitExceededExceptionNoDetail(except);

            default:
                break;
        }

    }

    enum InformationSecurity
    {
        DETAIL, NO_DETAIL, IGNORE;
    }

    private InformationSecurity traceSystem(T entity)
    {

        if (entity instanceof VirtualDatacenter)
        {
            return InformationSecurity.IGNORE;
        }

        return InformationSecurity.DETAIL;
    }

    private InformationSecurity traceEnterprise(T entity, boolean force)
    {
        if (entity instanceof VirtualDatacenter)
        {
            return force ? InformationSecurity.DETAIL : InformationSecurity.IGNORE;
        }

        return InformationSecurity.NO_DETAIL;
    }

    private InformationSecurity returnExcption(T entity, boolean hard, boolean force)
    {

        if (entity instanceof VirtualDatacenter)
        {
            if (hard)
            {
                return InformationSecurity.DETAIL;
            }
            else
            {
                return force ? InformationSecurity.IGNORE : InformationSecurity.DETAIL;
            }
        }

        return hard ? InformationSecurity.NO_DETAIL : InformationSecurity.IGNORE;
    }
}
