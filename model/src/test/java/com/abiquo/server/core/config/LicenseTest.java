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
package com.abiquo.server.core.config;

import org.testng.annotations.Test;

import com.abiquo.server.core.common.DefaultEntityTestBase;
import com.softwarementors.bzngine.entities.test.InstanceTester;

public class LicenseTest extends DefaultEntityTestBase<License>
{

    @Override
    protected InstanceTester<License> createEntityInstanceGenerator()
    {
        return new LicenseGenerator(getSeed());
    }

    @Test
    public void test_isActive()
    {
        assertFalse(isActive("2000-01-01 00:00:00"));
        assertFalse(isActive("2010-01-01 00:00:00"));
        assertTrue(isActive("2060-01-01 00:00:00"));
    }

    @Test(expectedExceptions = RuntimeException.class)
    public void test_isActiveInvalid()
    {
        isActive("2000-01-01");
    }

    private boolean isActive(String expiration)
    {
        LicenseDto dto = new LicenseDto();
        dto.setExpiration(expiration);
        return License.isActive(dto);
    }
}
