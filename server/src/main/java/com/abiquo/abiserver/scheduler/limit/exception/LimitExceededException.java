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

import com.abiquo.abiserver.business.hibernate.pojohb.virtualhardware.LimitHB;
import com.abiquo.abiserver.scheduler.limit.EntityLimitChecker.LimitResource;
import com.abiquo.abiserver.scheduler.workload.exception.AllocatorException;

public class LimitExceededException extends AllocatorException
{
    private static final long serialVersionUID = 1052785278699629101L;

    /**
     * Resource limit exceeded for entity {Enterprise, Datacenter or VirtualDatacenter}
     */
    private final Object entity; // TODO IPojoHB<IPojo< ? >>

    private final long required;

    private final long actual;

    private final LimitHB limit;

    private final LimitResource resource;

    public LimitExceededException(final Object entity, final long required, final long actual,
        final LimitHB limit, final LimitResource resource, final boolean editLimits)
    {

        super(formatMessage(actual, limit, resource, editLimits));

        this.entity = entity;
        this.required = required;
        this.actual = actual;
        this.limit = limit;
        this.resource = resource;
    }

    private static String formatMessage(final long actual, final LimitHB limit,
        final LimitResource resource, final boolean editLimits)
    {
        String message =
            String
                .format(
                    "It's not possible to reserve a new %s, your hard limit it's defined to %d, your soft limit is %d and you have %d %s allocated. ",
                    resource.getEntityName(), limit.getHard(), limit.getSoft(), actual,
                    resource.getEntityName());
        if (editLimits)
        {
            message =
                String
                    .format(
                        "Cannot edit %s limits to define hard limit of %d and soft limit of %d. You already have %d %s allocated.",
                        resource.getEntityName(), limit.getHard(), limit.getSoft(), actual,
                        resource.getEntityName());
        }

        return message;
    }

    public Object getEntity()
    {
        return entity;
    }

    public long getRequired()
    {
        return required;
    }

    public long getActual()
    {
        return actual;
    }

    public LimitHB getLimit()
    {
        return limit;
    }

    public LimitResource getResource()
    {
        return resource;
    }
}
