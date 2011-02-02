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

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * OVF conformance test driver. You should read the attached README file for the proper
 * understanding of this tests.
 * 
 * @author pnavarro
 */
public class DriverTest
{

    // the tests themselves
    private List<Test> tests;

    private static final Logger logger = LoggerFactory.getLogger(DriverTest.class);

    private Statistics stats = new Statistics();

    /**
     * @param testFile
     * @throws Exception
     */
    public DriverTest(String testFile) throws Exception
    {

        logger.info("Loading test descriptor file...");
        loadTests(testFile);
    }

    /**
     * Private helper that loads the tree of test cases
     */
    private void loadTests(String testFile) throws Exception
    {
        // load the test file
        Node root = XMLHelper.getInstance().getRootElement(testFile);
        NamedNodeMap attributes = root.getAttributes();
        Node abicloud_WS_locationNode = attributes.getNamedItem("abicloud_WS_location");
        String abicloud_WS_location;
        if (abicloud_WS_locationNode != null)
        {
            abicloud_WS_location = abicloud_WS_locationNode.getNodeValue();
        }
        else
        {
            throw new Exception("The abicloud WS locations could not been found in the tests file");
        }
        Node request_timeoutNode = attributes.getNamedItem("request_timeout");
        long request_timeout;
        if (request_timeoutNode != null)
        {
            request_timeout = Long.parseLong(request_timeoutNode.getNodeValue());
        }
        else
        {
            logger
                .warn("The request time out could not be found, taking the default value: 6000000 ms");
            request_timeout = 6000000;

        }
        Node sleep_timeNode = attributes.getNamedItem("sleep_time");
        long sleep_time;
        if (sleep_timeNode != null)
        {
            sleep_time = Long.parseLong(sleep_timeNode.getNodeValue());
        }
        else
        {
            logger.warn("The wait time could not be found, taking the default value: 1 minute");
            sleep_time = 60000;

        }
        ConfigurationTest testConfig =
            new ConfigurationTest(abicloud_WS_location, request_timeout, sleep_time);
        // go through each of the top-level tests, and handle as appropriate
        NodeList children = root.getChildNodes();
        tests = new ArrayList<Test>();
        for (int i = 0; i < children.getLength(); i++)
        {
            if (children.item(i).getNodeType() != Node.ELEMENT_NODE)
                continue;
            Element child = (Element) children.item(i);
            String childName = child.getNodeName();

            if (childName.equals("group"))
                tests.add(BasicGroupTest.getInstance(testConfig, child, "", stats));
            else if (childName.equals("test"))
                tests.add(BasicTest.getInstance(testConfig, child, "", stats));
        }
    }

    /**
     * Runs the tests, in order, using the given location of the test data.
     * 
     * @param prefix the root directory of all the conformance test cases
     */
    public void runTests(String dirPrefix)
    {
        Iterator it = tests.iterator();

        logger.info("STARTING TESTS at " + new Date());

        while (it.hasNext())
        {
            Test test = (Test) (it.next());
            test.start(dirPrefix);
        }

        logger.info("FINISHED TESTS at " + new Date());
        logger.info("Runned: " + stats.getRunnedTestCount());
        logger.info("Succeeded: " + stats.getSucceededTestCount());
        logger.info("Failed: " + stats.getFailedTestCount());

        Iterator<String> failedTestsIt = stats.getFailedTests().iterator();
        StringBuffer buf = new StringBuffer();
        while (failedTestsIt.hasNext())
        {
            buf.append("\n");
            buf.append(failedTestsIt.next());
        }
        logger.info("Failed tests: " + buf.toString());
    }

    /**
     * Main-line. The first argument is the file contaning the tests to run, the second argument is
     * the location of the conformance tests Both arguments are required
     */
    public static void main(String[] args)
    {

        if (args.length != 2)
        {
            System.out.println("Usage: <tests descriptor file> <tests location>");
            System.exit(1);
        }

        try
        {
            DriverTest testDriver = new DriverTest(args[0]);
            testDriver.runTests(args[1] + "/");
        }
        catch (FileNotFoundException e)
        {
            System.out.println("Error. file does not exist: " + e.getMessage());
        }
        catch (Exception e)
        {
            logger.error("{}", e);
            System.out.println("EXCEPTION: [" + e.getClass() + "]: " + e.getMessage());
        }
    }

}
