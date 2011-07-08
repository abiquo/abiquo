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

import com.abiquo.abiserver.business.hibernate.pojohb.infrastructure.DatacenterHB;
import com.abiquo.abiserver.business.hibernate.pojohb.user.EnterpriseHB;
import com.abiquo.abiserver.business.hibernate.pojohb.virtualappliance.VirtualDataCenterHB;
import com.abiquo.abiserver.business.hibernate.pojohb.virtualhardware.LimitHB;
import com.abiquo.abiserver.business.hibernate.pojohb.virtualhardware.ResourceAllocationLimitHB;
import com.abiquo.abiserver.commands.BasicCommand;
import com.abiquo.abiserver.persistence.DAOFactory;
import com.abiquo.abiserver.persistence.hibernate.HibernateDAOFactory;
import com.abiquo.abiserver.pojo.authentication.UserSession;
import com.abiquo.abiserver.pojo.infrastructure.DataCenter;
import com.abiquo.abiserver.scheduler.limit.exception.HardLimitExceededException;
import com.abiquo.abiserver.scheduler.limit.exception.LimitExceededException;
import com.abiquo.abiserver.scheduler.limit.exception.SoftLimitExceededException;
import com.abiquo.server.core.cloud.VirtualDatacenter;
import com.abiquo.tracer.ComponentType;
import com.abiquo.tracer.EventType;
import com.abiquo.tracer.SeverityType;

/**
 * Check the current range for the total resource allocation limits on a provided entity, indicating
 * when exceed the soft or hard limit.
 * <p>
 * 
 * @param CHECK_ENTITY {@link EnterpriseHB}, {@link DatacenterHB} or {@link VirtualDataCenterHB}
 */
public abstract class EntityLimitChecker<CHECK_ENTITY extends Object>
{

    public enum LimitResource
    {
        STORAGE("storage"), VLAN("vlan"), PUBLICIP("public ip");
        
        private String entityName;
        
        private LimitResource(String entityname)
        {
            this.entityName = entityname;
        }
        
        public String getEntityName()
        {
            return this.entityName;
        }
    }

    /**
     * Describe the total resource reservation requirements intervals.
     */
    enum ResourceLimitRange
    {
        /** the current machine will not exceed any resource reservation requirements. */
        OK,
        /** the current machine will exceed the total recommended resource reservation. */
        SOFT,
        /** the current machine will exceed the total allowed resource reservation requirements. */
        HARD
    };

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
    abstract long getActualAllocation(CHECK_ENTITY entity, LimitResource resourceType);

    abstract String getEntityName(CHECK_ENTITY entity);

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
    public void checkLimits(final CHECK_ENTITY entity, final LimitResource resourceType,
        final long required, final boolean force, final UserSession userSession,
        final VirtualDataCenterHB virtualDatacenter)
    {

        ResourceAllocationLimitHB limits = getLimit(entity);

        LimitHB limit = null;
        switch (resourceType)
        {
            case STORAGE:
                limit = limits.getStorage();
                break;

            case PUBLICIP:
                limit = limits.getPublicIP();
                break;

            case VLAN:
                limit = limits.getVlan();
                break;

            default:
                break;
        }

        if (limit == null || noLimits(limit))
        {
            return;
        }

        final Long actual = getActualAllocation(entity, resourceType);

        ResourceLimitRange status = limitStatus(actual, required, limit);

        checkResourceLimits(force, status, resourceType, entity, required, actual, limit,
            userSession, virtualDatacenter);
    }

    /**
     * @return true if all the soft and hard limits are set to 0 (unlimited)
     */
    private boolean noLimits(final LimitHB limit)
    {
        return (limit == null || limit.getSoft() == 0 && limit.getHard() == 0);
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
    protected ResourceLimitRange limitStatus(final long actual, final long required,
        final LimitHB limit)
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

        return range;
    }

    /**
     * Throws an exception if some of the considered resource limit status exceed some limit.
     */
    private void checkResourceLimits(final boolean force, final ResourceLimitRange status,
        final LimitResource resourceType, final CHECK_ENTITY entity, final long requirements,
        final long actual, final LimitHB limit, final UserSession userSession,
        final VirtualDataCenterHB virtualDatacenter) throws SoftLimitExceededException,
        HardLimitExceededException
    {

        switch (status)
        {
            case HARD:
                traceLimit(true, force, entity, new HardLimitExceededException(entity,
                    requirements,
                    actual,
                    limit,
                    resourceType), userSession, virtualDatacenter);
                break;
            case SOFT:
                traceLimit(false, force, entity, new SoftLimitExceededException(entity,
                    requirements,
                    actual,
                    limit,
                    resourceType), userSession, virtualDatacenter);
                break;
            default: // OK
                break;
        }
    }

    private void traceLimit(final boolean hard, final boolean force, final CHECK_ENTITY entity,
        final LimitExceededException except, final UserSession userSession,
        final VirtualDataCenterHB virtualDatacenter)
    {

        final String entityId = getEntityName(entity);

        final EventType etype =
            hard ? EventType.WORKLOAD_HARD_LIMIT_EXCEEDED : EventType.WORKLOAD_SOFT_LIMIT_EXCEEDED;

        DAOFactory daoF = HibernateDAOFactory.instance();
        DatacenterHB dcHB = null;
        DataCenter dc = null;
        String vdcName = (virtualDatacenter != null) ? virtualDatacenter.getName() : null;
        try
        {
            daoF.beginConnection();
            dcHB = daoF.getDataCenterDAO().findById(virtualDatacenter.getIdDataCenter());
            if (dcHB != null)
            {
                dc = dcHB.toPojo();
            }

        }
        catch (Exception e)
        {

        }
        finally
        {
            // daoF.endConnection();
        }

        String traceMessage = String.format("Not enough resources on %s", entityId);
        switch (traceSystem(entity))
        {
            case DETAIL:
                traceMessage = except.toString();
            case NO_DETAIL:
                BasicCommand.traceSystemLog(SeverityType.MAJOR, ComponentType.WORKLOAD, etype, dc,
                    vdcName, traceMessage, null, null, null);
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
                if (etype.equals(EventType.WORKLOAD_HARD_LIMIT_EXCEEDED)
                    || entity instanceof VirtualDataCenterHB)
                {

                    // TracerFactory.getTracer().log(SeverityType.MAJOR, ComponentType.WORKLOAD,
                    // etype, traceMessage);

                    BasicCommand.traceLog(SeverityType.MAJOR, ComponentType.WORKLOAD, etype,
                        userSession, dc, vdcName, traceMessage, null, null, null,
                        userSession.getUser(), userSession.getEnterpriseName());

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
                throw except; // TODO new LimitExceededExceptionNoDetail(except);

            default:
                break;
        }

    }

    enum InformationSecurity
    {
        DETAIL, NO_DETAIL, IGNORE;
    }

    private InformationSecurity traceSystem(final CHECK_ENTITY entity)
    {

        if (entity instanceof VirtualDatacenter)
        {
            return InformationSecurity.IGNORE;
        }

        return InformationSecurity.DETAIL;
    }

    private InformationSecurity traceEnterprise(final CHECK_ENTITY entity, final boolean force)
    {
        if (entity instanceof VirtualDatacenter)
        {
            return force ? InformationSecurity.DETAIL : InformationSecurity.IGNORE;
        }

        return InformationSecurity.NO_DETAIL;
    }

    private InformationSecurity returnExcption(final CHECK_ENTITY entity, final boolean hard,
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
