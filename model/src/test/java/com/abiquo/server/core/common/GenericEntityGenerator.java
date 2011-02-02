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

import com.softwarementors.bzngine.entities.test.PersistentInstanceTesterBase;
import com.softwarementors.commons.test.SeedGenerator;

public abstract class GenericEntityGenerator<T extends GenericEnityBase< ? >> extends
    PersistentInstanceTesterBase<T>
{

    protected GenericEntityGenerator(SeedGenerator seed)
    {
        super(seed);
    }

    public static <T extends Enum<T>> T newEnum(Class<T> enumClass, int seed)
    {
        assert enumClass != null;

        T[] values = enumClass.getEnumConstants();
        int valueCount = values.length - 1;
        int ordinal = 0;
        if (seed != 0)
            ordinal = valueCount % seed;

        assert ordinal >= 0 && ordinal < values.length;

        return values[ordinal];
    }

}
