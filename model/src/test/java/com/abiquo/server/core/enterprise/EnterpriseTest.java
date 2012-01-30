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
import com.abiquo.server.core.common.DefaultEntityWithLimits;
import com.abiquo.server.core.common.Limit;
import com.softwarementors.bzngine.entities.test.InstanceTester;

public class EnterpriseTest extends DefaultEntityTestBase<Enterprise>
{

    @Override
    protected InstanceTester<Enterprise> createEntityInstanceGenerator()
    {
        return new EnterpriseGenerator(getSeed());
    }

    @Override
    protected InstanceTester<Enterprise> eg()
    {
        return super.eg();
    }

    @Test
    public void test_setRamLimitsInMb()
    {
        Enterprise entity = createUniqueEntity();
        entity.setRamLimitsInMb(new Limit(1L, 3L));
        assertEquals(Enterprise.LimitStatus.OK,
            entity.checkRamStatus((int) DefaultEntityWithLimits.NO_LIMIT));
    }

    @Test
    public void test_setHdLimitsInMb()
    {
        Enterprise entity = createUniqueEntity();
        entity.setHdLimitsInMb(new Limit(1L, 3L));
        assertEquals(Enterprise.LimitStatus.OK,
            entity.checkHdStatus(DefaultEntityWithLimits.NO_LIMIT));
    }

    @Test
    public void test_setCpuCountLimitsInMb()
    {
        Enterprise entity = createUniqueEntity();
        entity.setCpuCountLimits(new Limit(1L, 3L));
        assertEquals(Enterprise.LimitStatus.OK,
            entity.checkCpuStatus((int) DefaultEntityWithLimits.NO_LIMIT));
    }

    @Test
    public void test_isValidLimitRange()
    {
        assertTrue(DefaultEntityWithLimits.isValidLimitRange(DefaultEntityWithLimits.NO_LIMIT,
            DefaultEntityWithLimits.NO_LIMIT));
        assertTrue(DefaultEntityWithLimits.isValidLimitRange(4, DefaultEntityWithLimits.NO_LIMIT));
        assertTrue(DefaultEntityWithLimits.isValidLimitRange(3, 4));
        assertTrue(DefaultEntityWithLimits.isValidLimitRange(4, 4));
        assertFalse(DefaultEntityWithLimits.isValidLimitRange(DefaultEntityWithLimits.NO_LIMIT, 4));
        assertFalse(DefaultEntityWithLimits.isValidLimitRange(5, 4));
        assertFalse(DefaultEntityWithLimits.isValidLimitRange(-3, 4));
        assertFalse(DefaultEntityWithLimits.isValidLimitRange(3, -1));
    }

    @Test
    public void test_checkLimitStatus()
    {
        assertEquals(Enterprise.LimitStatus.OK, DefaultEntityWithLimits.checkLimitStatus(
            DefaultEntityWithLimits.NO_LIMIT, DefaultEntityWithLimits.NO_LIMIT, 3));
        assertEquals(Enterprise.LimitStatus.OK,
            DefaultEntityWithLimits.checkLimitStatus(2, DefaultEntityWithLimits.NO_LIMIT, 1));
        assertEquals(Enterprise.LimitStatus.OK, DefaultEntityWithLimits.checkLimitStatus(2, 3, 1));

        assertEquals(Enterprise.LimitStatus.SOFT_LIMIT,
            DefaultEntityWithLimits.checkLimitStatus(2, 4, 2));
        assertEquals(Enterprise.LimitStatus.SOFT_LIMIT,
            DefaultEntityWithLimits.checkLimitStatus(2, 4, 3));
        assertEquals(Enterprise.LimitStatus.SOFT_LIMIT,
            DefaultEntityWithLimits.checkLimitStatus(2, 4, 4));

        assertEquals(Enterprise.LimitStatus.HARD_LIMIT,
            DefaultEntityWithLimits.checkLimitStatus(2, 4, 5));
        assertEquals(Enterprise.LimitStatus.HARD_LIMIT,
            DefaultEntityWithLimits.checkLimitStatus(2, 4, 6));
    }

    @Test
    public void test_isChefEnabled()
    {
        Enterprise nochef = createUniqueEntity();
        assertFalse(nochef.isChefEnabled());

        Enterprise chef = ((EnterpriseGenerator) eg()).createChefInstance();
        assertTrue(chef.isChefEnabled());
    }
}
