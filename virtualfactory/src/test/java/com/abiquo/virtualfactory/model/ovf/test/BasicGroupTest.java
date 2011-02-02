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
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * A basic implementation of a group of tests.
 * 
 * @author pnavarro
 */
public class BasicGroupTest extends Test
{

    // the name && description of this test
    private String description;

    // the tests contained in this group
    private List<Test> tests;

    private int totalTestCount = -1;

    private Statistics stats;

    private static final Log logger = LogFactory.getLog(BasicGroupTest.class.getName());

    /**
     * Constructor that accepts all the required values
     * 
     * @param testConfig
     * @param name the name of this group
     * @param experimental true if this is an experimental group
     * @param tests the groups of tests
     */
    public BasicGroupTest(ConfigurationTest testConfig, Element testNode, String testPrefix,
        Statistics stats, String description)
    {
        super(testNode, testPrefix, stats);
        this.description = description;
        this.stats = stats;

        // now get all the elements
        NodeList children = testNode.getChildNodes();
        tests = new ArrayList<Test>();

        for (int i = 0; i < children.getLength(); i++)
        {
            if (children.item(i).getNodeType() != Node.ELEMENT_NODE)
                continue;
            Element child = (Element) children.item(i);
            String nodeName = child.getNodeName();

            // if we find another group or an individual test, load it
            if (nodeName.equals("group"))
                tests.add(BasicGroupTest.getInstance(testConfig, child, name, stats));
            else if (nodeName.equals("test"))
                tests.add(BasicTest.getInstance(testConfig, child, name, stats));
        }

    }

    /**
     * Creates an instance of a group of tests from its XML representation.
     * 
     * @param testConfig
     * @param root the root of the XML-encoded data for this group
     * @param authzService the <code>AuthzService</code> used by any sub-tests
     */
    public static BasicGroupTest getInstance(ConfigurationTest testConfig, Element root,
        String testPrefix, Statistics stats)
    {

        // description
        String description = root.getAttribute("description");

        // create the new group
        return new BasicGroupTest(testConfig, root, testPrefix, stats, description);
    }

    /**
     * Returns the tests contained in this group.
     * 
     * @return a <code>List</code> of <code>Test</code>s
     */
    public List getTests()
    {
        return tests;
    }

    /**
     * Run the test
     */
    public void run(String dirPrefix)
    {
        Iterator<Test> it = tests.iterator();

        logger.trace("Running group " + name + " (" + description + ")");

        int startErrorCount = this.stats.getFailedTestCount();

        while (it.hasNext())
        {
            Test test = it.next();
            test.start(dirPrefix);
        }

        logger.trace("Finished group " + name + " [failures: "
            + (this.stats.getFailedTestCount() - startErrorCount) + "/" + getTestCount() + "]");
    }

    /**
     * Returns the number of tests that this group contains.
     */
    protected int getTestCount()
    {
        if (this.totalTestCount < 0)
        {
            this.totalTestCount = 0;
            Iterator<Test> it = this.tests.iterator();
            while (it.hasNext())
            {
                this.totalTestCount += it.next().getTestCount();
            }
        }
        return this.totalTestCount;
    }

}
