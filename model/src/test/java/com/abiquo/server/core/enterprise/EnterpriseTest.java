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

package com.abiquo.server.core.enterprise;

import org.testng.annotations.Test;

import com.abiquo.server.core.common.DefaultEntityTestBase;
import com.abiquo.server.core.common.Limit;
import com.softwarementors.bzngine.entities.test.InstanceTester;

public class EnterpriseTest extends DefaultEntityTestBase<Enterprise>
{

    @Override
    protected InstanceTester<Enterprise> createEntityInstanceGenerator()
    {
        return new EnterpriseGenerator(getSeed());
    }

    @Test
    public void test_setRamLimitsInMb()
    {
        Enterprise entity = createUniqueEntity();
        entity.setRamLimitsInMb(new Limit(1L, 3L));
        assertEquals(Enterprise.LimitStatus.OK, entity.checkRamStatus((int) Enterprise.NO_LIMIT));
    }

    @Test
    public void test_setHdLimitsInMb()
    {
        Enterprise entity = createUniqueEntity();
        entity.setHdLimitsInMb(new Limit(1L, 3L));
        assertEquals(Enterprise.LimitStatus.OK, entity.checkHdStatus(Enterprise.NO_LIMIT));
    }

    @Test
    public void test_setCpuCountLimitsInMb()
    {
        Enterprise entity = createUniqueEntity();
        entity.setCpuCountLimits(new Limit(1L, 3L));
        assertEquals(Enterprise.LimitStatus.OK, entity.checkCpuStatus((int) Enterprise.NO_LIMIT));
    }

    @Test
    public void test_isValidLimitRange()
    {
        assertTrue(Enterprise.isValidLimitRange(Enterprise.NO_LIMIT, Enterprise.NO_LIMIT));
        assertTrue(Enterprise.isValidLimitRange(4, Enterprise.NO_LIMIT));
        assertTrue(Enterprise.isValidLimitRange(3, 4));
        assertTrue(Enterprise.isValidLimitRange(4, 4));
        assertFalse(Enterprise.isValidLimitRange(Enterprise.NO_LIMIT, 4));
        assertFalse(Enterprise.isValidLimitRange(5, 4));
        assertFalse(Enterprise.isValidLimitRange(-3, 4));
        assertFalse(Enterprise.isValidLimitRange(3, -1));
    }

    @Test
    public void test_checkLimitStatus()
    {
        assertEquals(Enterprise.LimitStatus.OK, Enterprise.checkLimitStatus(Enterprise.NO_LIMIT,
            Enterprise.NO_LIMIT, 3));
        assertEquals(Enterprise.LimitStatus.OK, Enterprise.checkLimitStatus(2, Enterprise.NO_LIMIT,
            1));
        assertEquals(Enterprise.LimitStatus.OK, Enterprise.checkLimitStatus(2, 3, 1));

        assertEquals(Enterprise.LimitStatus.SOFT_LIMIT, Enterprise.checkLimitStatus(2, 4, 2));
        assertEquals(Enterprise.LimitStatus.SOFT_LIMIT, Enterprise.checkLimitStatus(2, 4, 3));
        assertEquals(Enterprise.LimitStatus.SOFT_LIMIT, Enterprise.checkLimitStatus(2, 4, 4));
        
        assertEquals(Enterprise.LimitStatus.HARD_LIMIT, Enterprise.checkLimitStatus(2, 4, 5));
        assertEquals(Enterprise.LimitStatus.HARD_LIMIT, Enterprise.checkLimitStatus(2, 4, 6));
    }
}
