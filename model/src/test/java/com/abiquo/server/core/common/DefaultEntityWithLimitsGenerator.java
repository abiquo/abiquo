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

package com.abiquo.server.core.common;

import com.softwarementors.commons.test.SeedGenerator;

public abstract class DefaultEntityWithLimitsGenerator<T extends DefaultEntityWithLimits> extends
    GenericEntityGenerator<T>
{

    protected DefaultEntityWithLimitsGenerator(SeedGenerator seed)
    {
        super(seed);
    }

    protected void setDefaultLimits(T entityWithLimits)
    {
        entityWithLimits.setCpuCountLimits(new Limit(0L, 0L));
        entityWithLimits.setHdLimitsInMb(new Limit(0L, 0L));
        entityWithLimits.setPublicIPLimits(new Limit(0L, 0L));
        entityWithLimits.setRamLimitsInMb(new Limit(0L, 0L));
        entityWithLimits.setStorageLimits(new Limit(0L, 0L));
        entityWithLimits.setVlansLimits(new Limit(0L, 0L));
    }

}
