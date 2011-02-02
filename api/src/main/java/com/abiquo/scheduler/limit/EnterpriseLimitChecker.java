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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.abiquo.server.core.common.DefaultEntityCurrentUsed;
import com.abiquo.server.core.common.DefaultEntityWithLimits.LimitStatus;
import com.abiquo.server.core.enterprise.Enterprise;
import com.abiquo.server.core.enterprise.EnterpriseRep;

@Component
public class EnterpriseLimitChecker extends EntityLimitChecker<Enterprise>
{
    @Autowired
    EnterpriseRep enterpriseRep;

    @Override
    protected String getEntityIdentifier(Enterprise entity)
    {
        return String.format("Enterprise [%s]", entity.getName());
    }

    @Override
    protected DefaultEntityCurrentUsed getCurrentUsed(Enterprise entity)
    {
        return enterpriseRep.getEnterpriseResourceUsage(entity.getId());
    }

    @Override
    protected Map<LimitResource, LimitStatus> getFilterResourcesStatus(
        Map<LimitResource, LimitStatus> limitStatus)
    {
        // enterprise uses all resources
        return limitStatus;
    }
}
