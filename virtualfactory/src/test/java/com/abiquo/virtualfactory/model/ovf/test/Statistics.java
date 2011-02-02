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

package com.abiquo.virtualfactory.model.ovf.test;

import java.util.ArrayList;
import java.util.List;

// TODO: Auto-generated Javadoc
/**
 * The Class Statistics.
 */
public class Statistics
{

    /** The failed tests. */
    protected List<String> failedTests = new ArrayList<String>();

    /** The runned tests. */
    protected int runnedTests = 0;

    /**
     * Test failed.
     * 
     * @param name the name
     */
    public void testFailed(String name)
    {
        testFailed(name, "");
    }

    /**
     * Test failed.
     * 
     * @param name the name
     * @param reason the reason
     */
    public void testFailed(String name, String reason)
    {
        runnedTests++;
        failedTests.add(name + (reason == null ? "" : " - " + reason));
    }

    /**
     * Test succeeded.
     * 
     * @param name the name
     */
    public void testSucceeded(String name)
    {
        runnedTests++;
    }

    /**
     * Gets the runned test count.
     * 
     * @return the runned test count
     */
    public int getRunnedTestCount()
    {
        return this.runnedTests;
    }

    /**
     * Gets the failed test count.
     * 
     * @return the failed test count
     */
    public int getFailedTestCount()
    {
        return this.failedTests.size();
    }

    /**
     * Gets the succeeded test count.
     * 
     * @return the succeeded test count
     */
    public int getSucceededTestCount()
    {
        return this.runnedTests - this.failedTests.size();
    }

    /**
     * Gets the failed tests.
     * 
     * @return the failed tests
     */
    public List<String> getFailedTests()
    {
        return this.failedTests;
    }

}
