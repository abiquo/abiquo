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

import java.io.FileInputStream;
import java.util.Map;

import javax.xml.namespace.QName;

import org.dmtf.schemas.ovf.envelope._1.AnnotationSectionType;
import org.dmtf.schemas.ovf.envelope._1.ContentType;
import org.dmtf.schemas.ovf.envelope._1.EnvelopeType;
import org.dmtf.schemas.ovf.envelope._1.VirtualSystemCollectionType;
import org.dmtf.schemas.ovf.envelope._1.VirtualSystemType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.abiquo.ovfmanager.ovf.OVFEnvelopeUtils;
import com.abiquo.ovfmanager.ovf.exceptions.EmptyEnvelopeException;
import com.abiquo.ovfmanager.ovf.exceptions.SectionException;
import com.abiquo.ovfmanager.ovf.exceptions.SectionNotPresentException;
import com.abiquo.ovfmanager.ovf.xml.OVFSerializer;
import com.sun.ws.management.client.Resource;
import com.sun.ws.management.client.ResourceFactory;

/**
 * A simple implementation of a single conformance test case.
 * 
 * @author pnavarro
 */
public class BasicTest extends Test
{
    private final static Logger logger = LoggerFactory.getLogger(BasicTest.class);

    private XMLHelper xmlHelper = XMLHelper.getInstance();

    private static final String RESOURCE_URI =
        "http://schemas.dmtf.org/ovf/envelope/1/virtualApplianceService/virtualApplianceResource";

    private ConfigurationTest testConfig;

    // machine State Qname
    private final static QName machineStateQname = new QName("machineStateAction");

    private final static OVFSerializer ovfSerializer = OVFSerializer.getInstance();

    // PowerUp action
    private final static String POWERUP_ACTION = "PowerUp";

    // Powerdown action
    private final static String POWERDOWN_ACTION = "PowerOff";

    // Pause action
    private final static String PAUSE_ACTION = "Pause";

    // Resume action
    private final static String RESUME_ACTION = "Resume";

    /**
     * Constructor that accepts all values associatd with a test.
     * 
     * @param testConfig
     * @param authzService the authzService that manages this test's evaluations
     * @param errorExpected true if en error is expected from a normal run
     * @param experimental true if this is an experimental test
     */
    public BasicTest(ConfigurationTest testConfig, Element testNode, String testPrefix,
        Statistics stats)
    {
        super(testNode, testPrefix, stats);
        this.testConfig = testConfig;
    }

    /**
     * Creates an instance of a test from its XML representation.
     * 
     * @param testConfig
     * @param root the root of the XML-encoded data for this test
     * @param pdp the <code>PDP</code> used by this test
     */
    public static BasicTest getInstance(ConfigurationTest testConfig, Element root,
        String testPrefix, Statistics stats)
    {
        return new BasicTest(testConfig, root, testPrefix, stats);
    }

    public void run(String dirPrefix)
    {
        String filePrefix = dirPrefix + name;
        boolean failurePointReached = false;
        Resource resource;
        try
        {
            logger.info("Loading the basic operations test files");
            Element createNode = xmlHelper.getRootElement(filePrefix + "Create.ovf");
            EnvelopeType baseEnvelope =
                OVFSerializer.getInstance().readXMLEnvelope(
                    new FileInputStream(filePrefix + "Create.ovf"));
            logger.info("Launching the basic operations:");
            if (createNode != null)
            {
                logger.info("Launching the CREATE operation");
                resource =
                    ResourceFactory.create(testConfig.getAbicloud_WS_location(), RESOURCE_URI,
                        testConfig.getRequest_timeout(), createNode.getOwnerDocument(),
                        ResourceFactory.LATEST);
            }
            else
            {
                String message =
                    "The create operation could not been called since the file: " + filePrefix
                        + "Create.xml" + " was not found";
                logger.error(message);
                throw new Exception(message);

            }
            if (errorExpected)
            {
                fail();
            }
            else
            {
                failurePointReached = true;
                logger.info("Waiting {} seconds between operations",
                    testConfig.getSleep_time() / 1000);
                Thread.sleep(testConfig.getSleep_time());

                 logger.info("Launching the POWER UP operation");
                 EnvelopeType powerUpEnvelope =
                 changeStateVirtualMachine(baseEnvelope, POWERUP_ACTION);
                 Document powerUpDoc = ovfSerializer.bindToDocument(powerUpEnvelope, false);
                 resource.put(powerUpDoc);
                 logger.info("Waiting {} seconds.....", testConfig.getSleep_time() / 1000);
                 Thread.sleep(testConfig.getSleep_time());
                
                 logger.info("Launching the PAUSE operation");
                 EnvelopeType pauseEnvelope = changeStateVirtualMachine(baseEnvelope,
                 PAUSE_ACTION);
                 Document pauseDoc = ovfSerializer.bindToDocument(pauseEnvelope, false);
                 resource.put(pauseDoc);
                 logger.info("Waiting {} seconds.....", testConfig.getSleep_time() / 1000);
                 Thread.sleep(testConfig.getSleep_time());
                
                 logger.info("Launching the RESUME operation");
                 EnvelopeType resumeEnvelope =
                 changeStateVirtualMachine(baseEnvelope, RESUME_ACTION);
                 Document resumeDoc = ovfSerializer.bindToDocument(resumeEnvelope, false);
                 resource.put(resumeDoc);
                 logger.info("Waiting {} seconds.....", testConfig.getSleep_time() / 1000);
                 Thread.sleep(testConfig.getSleep_time());
                
                 logger.info("Launching the POWEROFF operation");
                 EnvelopeType poweroffEnvelope =
                 changeStateVirtualMachine(baseEnvelope, POWERDOWN_ACTION);
                 Document poweroffDoc = ovfSerializer.bindToDocument(poweroffEnvelope, false);
                 resource.put(poweroffDoc);
                 logger.info("Waiting {} seconds.....", testConfig.getSleep_time() / 1000);
                 Thread.sleep(testConfig.getSleep_time());

                // TODO Reactivate reconfiguration operations

                logger.info("Launching the delete operation");
                resource.delete();

                logger.info("Operations complete");
                succeed();
            }
        }
        catch (Exception e)
        {
            // any errors happen as exceptions, and may be successes if we're
            // supposed to fail and we haven't reached the failure point yet
            if (!failurePointReached && errorExpected)
            {
                succeed();
            }
            else
            {
                fail(e);
                logger.error("{}", e);
            }
        }

    }

    /**
     * Private helper to change the state
     * 
     * @param enveloe
     * @return
     * @throws EmptyEnvelopeException
     * @throws SectionException
     * @throws SectionNotPresentException
     */
    private EnvelopeType changeStateVirtualMachine(EnvelopeType envelope, String newState)
        throws EmptyEnvelopeException, SectionException
    {
        ContentType entityInstance = OVFEnvelopeUtils.getTopLevelVirtualSystemContent(envelope);

        if (entityInstance instanceof VirtualSystemType)
        {
            // Getting state property
            AnnotationSectionType annotationSection =
                OVFEnvelopeUtils.getSection(entityInstance, AnnotationSectionType.class);

            Map<QName, String> attributes = annotationSection.getOtherAttributes();
            attributes.put(machineStateQname, newState);

        }
        else if (entityInstance instanceof VirtualSystemCollectionType)
        {
            VirtualSystemCollectionType virtualSystemCollectionType =
                (VirtualSystemCollectionType) entityInstance;

            for (ContentType subVirtualSystem : OVFEnvelopeUtils
                .getVirtualSystemsFromCollection(virtualSystemCollectionType))
            {
                AnnotationSectionType annotationSection =
                    OVFEnvelopeUtils.getSection(subVirtualSystem, AnnotationSectionType.class);

                Map<QName, String> attributes = annotationSection.getOtherAttributes();
                attributes.put(machineStateQname, newState);
            }
        }

        return envelope;
    }

}
