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

package com.abiquo.server.core.task;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.UUID;

public class CacheEntryGenerator
{
    public CacheEntry createUniqueInstance()
    {
        CacheEntry entry = new CacheEntry(UUID.randomUUID().toString());

        for (int i = 0; i < 10; i++)
        {
            entry.getValues().add(UUID.randomUUID().toString());
        }

        return entry;
    }

    public void assertSameEntry(final CacheEntry one, final CacheEntry other)
    {
        assertEquals(one.getId(), other.getId());
        assertEquals(one.getValues().size(), other.getValues().size());

        for (String value : one.getValues())
        {
            assertTrue(other.getValues().contains(value));
        }
    }
}
