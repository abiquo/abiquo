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
package com.abiquo.api.common;

import static com.abiquo.testng.TestConfig.BASIC_UNIT_TESTS;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Base class for all TestNG unit tests.
 * 
 * @author ibarrera
 */
@Test(groups = BASIC_UNIT_TESTS)
public abstract class AbstractUnitTest extends AbstractGeneratorTest
{
    @BeforeMethod
    @Override
    public void setup()
    {
        // Do not remove. This method must be in this class in order to properly handle TestNG
        // groups
        super.setup();
    }

    @AfterMethod
    @Override
    public void tearDown()
    {
        // Do not remove. This method must be in this class in order to properly handle TestNG
        // groups
        super.tearDown();
    }

}
