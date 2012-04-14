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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.abiquo.api.tracer.TracerLogger;
import com.abiquo.server.core.cloud.VirtualDatacenter;
import com.abiquo.server.core.common.DefaultEntityCurrentUsed;
import com.abiquo.server.core.common.DefaultEntityWithLimits;
import com.abiquo.server.core.common.DefaultEntityWithLimits.LimitStatus;
import com.abiquo.server.core.enterprise.Enterprise;
import com.abiquo.server.core.infrastructure.Datacenter;
import com.abiquo.server.core.scheduler.VirtualMachineRequirements;
import com.abiquo.server.core.scheduler.VirtualMachineRequirementsEmpty;
import com.abiquo.tracer.ComponentType;
import com.abiquo.tracer.EventType;
import com.abiquo.tracer.SeverityType;

/**
 * Check the current range for the total resource allocation limits on a provided entity, indicating
 * when exceed the soft or hard limit.
 * <p>
 * 
 * @param T {@link Enterprise}, {@link Datacenter} or {@link VirtualDataCenter}
 */
@Component
public abstract class EntityLimitChecker<T extends DefaultEntityWithLimits>
{
    @Autowired
    private TracerLogger tracer;

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
     * requirements. Overloaded method because en case of deploying VM is not necessary check VLAN
     * limits.
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

        checkLimits(entity, required, force, true, true);
    }

    public void checkLimits(final T entity, final VirtualMachineRequirements required,
        final boolean force, final Boolean checkVLAN) throws LimitExceededException
    {

        checkLimits(entity, required, force, checkVLAN, false);
    }

    public void checkLimits(final T entity, final VirtualMachineRequirements required,
        final boolean force, final Boolean checkVLAN, final Boolean checkIPs)
        throws LimitExceededException
    {
        if (allNoLimits(entity))
        {
            return;
        }

        final DefaultEntityCurrentUsed actualAllocated = getCurrentUsed(entity);

        Map<LimitResource, LimitStatus> entityResourceStatus =

        getResourcesLimit(entity, actualAllocated, required, checkVLAN, checkIPs);

        entityResourceStatus = getFilterResourcesStatus(entityResourceStatus);

        checkResourceLimits(force, entityResourceStatus, entity, required, actualAllocated);
    }

    /**
     * Gets the list of all resources with his limit status.
     */
    private Map<LimitResource, LimitStatus> getResourcesLimit(final T limits,
        final DefaultEntityCurrentUsed actualAllocated, final VirtualMachineRequirements required,
        final Boolean checkVLAN, final Boolean checkIPs)
    {

        Map<LimitResource, LimitStatus> limitStatus =
            new HashMap<EntityLimitChecker.LimitResource, DefaultEntityWithLimits.LimitStatus>();
        // Initialized in order to show GUI's popup values
        limitStatus.put(LimitResource.CPU, LimitStatus.OK);
        limitStatus.put(LimitResource.RAM, LimitStatus.OK);
        limitStatus.put(LimitResource.STORAGE, LimitStatus.OK);
        limitStatus.put(LimitResource.HD, LimitStatus.OK);
        limitStatus.put(LimitResource.VLAN, LimitStatus.OK);
        // limitStatus.put(LimitResource.PUBLICIP, LimitStatus.OK);
        if (required.getHd() >= 0)
        {
            long actualAndRequiredHd = actualAllocated.getHdInMb() + required.getHd();
            limitStatus.put(LimitResource.HD, limits.checkHdStatus(actualAndRequiredHd));
        }
        int actualAndRequiredCpu = (int) (actualAllocated.getCpu() + required.getCpu());
        int actualAndRequiredRam = (int) (actualAllocated.getRamInMb() + required.getRam());
        long actualAndRequiredStorage = actualAllocated.getStorage() + required.getStorage();
        if (checkVLAN)// && required.getPublicVLAN() != 0)
        {
            int actualAndRequiredVLANs =
                (int) (actualAllocated.getVlanCount() + required.getPublicVLAN());
            limitStatus.put(LimitResource.VLAN, limits.checkVlanStatus(actualAndRequiredVLANs));

        }

        if (checkIPs)// && required.getPublicIP() != 0
        {
            int actualAndRequiredIPs =
                (int) (actualAllocated.getPublicIp() + required.getPublicIP());
            limitStatus.put(LimitResource.PUBLICIP, limits
                .checkPublicIpStatus(actualAndRequiredIPs));

        }
        limitStatus.put(LimitResource.CPU, limits.checkCpuStatus(actualAndRequiredCpu));
        limitStatus.put(LimitResource.RAM, limits.checkRamStatus(actualAndRequiredRam));
        limitStatus.put(LimitResource.STORAGE, limits.checkStorageStatus(actualAndRequiredStorage));

        return limitStatus;
    }

    /**
     * @return true if all the soft and hard limits are set to 0 (unlimited)
     */
    private boolean allNoLimits(final T limit)
    {
        return limit.getCpuCountLimits().isNoLimit() //
            && limit.getRamLimitsInMb().isNoLimit() //
            && limit.getHdLimitsInMb().isNoLimit() //
            && (limit.getVlansLimits() == null || limit.getVlansLimits().isNoLimit()) //
            && (limit.getStorageLimits() == null || limit.getStorageLimits().isNoLimit()) //
            && (limit.getPublicIPLimits() == null || limit.getPublicIPLimits().isNoLimit());
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

        if (statusMap.containsValue(LimitStatus.HARD_LIMIT))
        {
            totalLimitStatus = LimitStatus.HARD_LIMIT;
        }
        else if (statusMap.containsValue(LimitStatus.SOFT_LIMIT))
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
                new LimitExceededException(totalLimitStatus == LimitStatus.HARD_LIMIT,
                    statusMap,
                    entity,
                    requirements,
                    actual,
                    getEntityIdentifier(entity));

            // don't trace anything in tests.
            traceLimit(totalLimitStatus == LimitStatus.HARD_LIMIT, force, entity, exc);
        }
    }

    private void traceLimit(final boolean hard, final boolean force, final T entity,
        final LimitExceededException except)
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
                if (tracer != null)
                {
                    tracer.systemLog(SeverityType.MAJOR, ComponentType.WORKLOAD, etype,
                        traceMessage);
                }
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
                if ((etype.equals(EventType.WORKLOAD_HARD_LIMIT_EXCEEDED) || entity instanceof VirtualDatacenter)
                    && tracer != null)
                {
                    tracer.log(SeverityType.MAJOR, ComponentType.WORKLOAD, etype, traceMessage);
                }

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

    private InformationSecurity traceSystem(final T entity)
    {

        if (entity instanceof VirtualDatacenter)
        {
            return InformationSecurity.IGNORE;
        }

        return InformationSecurity.DETAIL;
    }

    private InformationSecurity traceEnterprise(final T entity, final boolean force)
    {
        if (entity instanceof VirtualDatacenter)
        {
            return force ? InformationSecurity.DETAIL : InformationSecurity.IGNORE;
        }

        return InformationSecurity.NO_DETAIL;
    }

    private InformationSecurity returnExcption(final T entity, final boolean hard,
        final boolean force)
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
