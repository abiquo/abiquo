package com.abiquo.server.core.enterprise;

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
 * Boston, MA 02111-1307, USA. */

import java.util.List;

import com.abiquo.server.core.common.DefaultEntityGenerator;
import com.softwarementors.commons.test.SeedGenerator;

public class EnterpriseGenerator extends DefaultEntityGenerator<Enterprise>
{

    public EnterpriseGenerator(SeedGenerator seed)
    {
        super(seed);
    }

    @Override
    public void assertAllPropertiesEqual(Enterprise obj1, Enterprise obj2)
    {

    }

    @Override
    public Enterprise createUniqueInstance()
    {
        int seed = nextSeed();

        final String name = newString(seed, Enterprise.NAME_LENGTH_MIN, Enterprise.NAME_LENGTH_MAX);
        return createInstance(name);
    }

    public Enterprise createInstance(String name)
    {
        int seed = nextSeed();

        final int ramSoftLimitInMb = seed;
        final int cpuCountSoftLimit = seed;
        final int hdSoftLimitInMb = seed;
        final int ramHardLimitInMb = seed;
        final int cpuCountHardLimit = seed;
        final int hdHardLimitInMb = seed;

        return new Enterprise(name,
            ramSoftLimitInMb,
            cpuCountSoftLimit,
            hdSoftLimitInMb,
            ramHardLimitInMb,
            cpuCountHardLimit,
            hdHardLimitInMb);
    }

    public Enterprise createInstanceNoLimits(String name)
    {
        return new Enterprise(name, 0, 0, 0, 0, 0, 0);
    }

    @Override
    public void addAuxiliaryEntitiesToPersist(Enterprise entity, List<Object> entitiesToPersist)
    {
        super.addAuxiliaryEntitiesToPersist(entity, entitiesToPersist);
    }

}
