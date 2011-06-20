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
package com.abiquo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestResult;
import org.testng.TestListenerAdapter;

public class TestListenerOutput extends TestListenerAdapter
{
    private static final Logger LOGGER = LoggerFactory.getLogger(TestListenerOutput.class);

    @Override
    public void onTestFailure(final ITestResult tr)
    {
        LOGGER.info(String.format("FAIL %s [%s] Cause: %s", testMethod(tr), execTime(tr), tr
            .getThrowable().getMessage()));
    }

    @Override
    public void onTestSkipped(final ITestResult tr)
    {
        LOGGER.info("SKIP {} [{}]", testMethod(tr), execTime(tr));
    }

    @Override
    public void onTestSuccess(final ITestResult tr)
    {
        LOGGER.info("OK   {} [{}]", testMethod(tr), execTime(tr));
    }

    private static String testMethod(final ITestResult tr)
    {
        return tr.getTestClass().getRealClass().getSimpleName() + "."
            + tr.getMethod().getMethodName();
    }

    private static String execTime(final ITestResult tr)
    {
        return (tr.getEndMillis() - tr.getStartMillis()) + " ms";
    }

}
