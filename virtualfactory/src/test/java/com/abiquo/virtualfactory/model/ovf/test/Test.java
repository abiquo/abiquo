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

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * Simple interface that defines what a single test does.
 * 
 * @author pnavarro
 */
public abstract class Test
{

    /** The Constant logger. */
    private static final Logger logger = LoggerFactory.getLogger(Test.class);

    /** The stats. */
    private Statistics stats;

    /** The name. */
    protected String name;

    /** The experimental. */
    private boolean experimental;

    /** The error expected. */
    protected boolean errorExpected;

    /** The start time. */
    private Date startTime;

    /**
     * Instantiates a new test.
     * 
     * @param testNode the test node
     * @param testPrefix the test prefix
     * @param stats the stats
     */
    protected Test(Element testNode, String testPrefix, Statistics stats)
    {

        // the name is required...
        this.name = testPrefix + testNode.getAttribute("name");

        // ...but the other two aren't
        NamedNodeMap map = testNode.getAttributes();
        this.errorExpected = isAttrTrue(map, "errorExpected");
        this.experimental = isAttrTrue(map, "experimental");

        this.stats = stats;
    }

    /**
     * Private helper that reads a attribute to see if it's set, and if so if its value is "true".
     * 
     * @param map the map
     * @param attrName the attr name
     * 
     * @return true, if checks if is attr true
     */
    private static boolean isAttrTrue(NamedNodeMap map, String attrName)
    {
        Node attrNode = map.getNamedItem(attrName);

        if (attrNode == null)
            return false;

        return attrNode.getNodeValue().equals("true");
    }

    /**
     * Runs the test.
     * 
     * @param dirPrefix the dir prefix
     * 
     * @return the number of failures that occured
     */
    protected abstract void run(String dirPrefix);

    /**
     * Returns the name of this test.
     * 
     * @return the name
     */
    public String getName()
    {
        return this.name;
    }

    /**
     * Returns whether an error is expected to happen (and is therefore a success case).
     * 
     * @return true if an error is expected, false otherwise
     */
    public boolean isErrorExpected()
    {
        return this.errorExpected;
    }

    /**
     * Returns whether or not this is an experimental test, and therefore not part of the required
     * test cases.
     * 
     * @return true if this test is experimental, false otherwise
     */
    public boolean isExperimental()
    {
        return this.experimental;
    }

    /**
     * Reports that the test failed.
     */
    public void fail()
    {
        this.stats.testFailed(this.name);
        logger.trace("Test " + name + " FAILED " + getElapsedTime());
    }

    /**
     * Reports that the test failed.
     * 
     * @param e the throwable that cause the failure
     */
    public void fail(Throwable e)
    {
        this.stats.testFailed(this.name);
        logger.trace("Test " + name + ": unexpected EXCEPTION " + getElapsedTime(), e);
    }

    /**
     * Reports that the test was successful
     */
    public void succeed()
    {
        this.stats.testSucceeded(this.name);
        logger.trace("Test " + name + " SUCCEEDED " + getElapsedTime());
    }

    /**
     * Start the test
     * 
     * @param dirPrefix the dir prefix
     */
    public void start(String dirPrefix)
    {
        logger.trace("--------------------------------------------------------");
        logger.trace("Starting test: " + name);
        startTime = new Date();

        run(dirPrefix);
    }

    /**
     * Gets the elapsed time.
     * 
     * @return the elapsed time
     */
    private String getElapsedTime()
    {
        long end = new Date().getTime();
        long elapsed = end - startTime.getTime();
        return "(" + elapsed + " ms)";
    }

    /**
     * Returns the number of tests that this tests includes. By default, this test is atomic so this
     * method always returns 1.
     * 
     * @return the test count
     */
    protected int getTestCount()
    {
        return 1;
    }

}
