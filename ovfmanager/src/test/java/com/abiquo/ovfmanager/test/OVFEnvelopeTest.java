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

package com.abiquo.ovfmanager.test;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigInteger;
import java.net.URI;

import junit.framework.TestCase;

import org.dmtf.schemas.ovf.envelope._1.EnvelopeType;
import org.dmtf.schemas.ovf.envelope._1.FileType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.abiquo.ovfmanager.ovf.OVFEnvelopeUtils;
import com.abiquo.ovfmanager.ovf.exceptions.IdAlreadyExistsException;
import com.abiquo.ovfmanager.ovf.exceptions.IdNotFoundException;
import com.abiquo.ovfmanager.ovf.xml.OVFSerializer;

/**
 * TODO for each test: save/load and test again. TODO read parameters for expected for the OVF
 * example in order to test against different OVF.
 */
public class OVFEnvelopeTest extends TestCase
{
    private final static Logger log = LoggerFactory.getLogger(OVFEnvelopeTest.class);

    private final static URI testOVFPath = new File("src/test/resources/myservice.ovf").toURI();

    private EnvelopeType envelope;

    public void testFileReferences()
    {
        assertEquals(5, OVFEnvelopeUtils.fileReference.getAllReferencedFile(envelope).size());

        try
        {
            OVFEnvelopeUtils.fileReference.changeFileLocation(envelope, "icon", "newIcon.png");
        }
        catch (IdNotFoundException e)
        {
            e.printStackTrace();
            fail("file references id not exist");
        }

        
        
        assertEquals(5, OVFEnvelopeUtils.fileReference.getAllReferencedFile(envelope).size());
        try
        {
            assertNotNull(OVFEnvelopeUtils.fileReference.getReferencedFile(envelope, "icon"));
        }
        catch (IdNotFoundException e1)
        {
            e1.printStackTrace();
            fail();
        }
        assertFalse(OVFEnvelopeUtils.fileReference.getAllReferencedFileLocations(envelope).contains("icon.png"));

        FileType file;
        try
        {
            
            file = OVFEnvelopeUtils.fileReference.createFileType("newFile", "newFile.iso", BigInteger.valueOf(100000), null, null);
            
            OVFEnvelopeUtils.fileReference.addFile( envelope.getReferences(),file);
            
        }
        catch (IdAlreadyExistsException e)
        {

            e.printStackTrace();
            fail("file references id already exist");
        }

        try
        {
            file = OVFEnvelopeUtils.fileReference.createFileType("newFile", "newFile.iso", BigInteger.valueOf(100000), null, null);
            OVFEnvelopeUtils.fileReference.addFile(envelope.getReferences() ,file);
         
            fail("file id already exist");
        }
        catch (IdAlreadyExistsException e)
        {
            assertNotNull(e);
        }

        assertEquals(6, OVFEnvelopeUtils.fileReference.getAllReferencedFile(envelope).size());

        try
        {
            assertNotNull(OVFEnvelopeUtils.fileReference.getReferencedFile(envelope,"newFile"));
        }
        catch (IdNotFoundException e)
        {
            fail();
        }
    }

    /*
    public void testGetVirtualSystems()
    {
        final String msg = "there is more than one virtual system";

        if (OVFEnvelopeUtils.isOneVirtualSystem(envelope))
        {
            fail(msg);
        }
        else
        {
            assertTrue(msg, true);
        }

        Set<VirtualSystemType> vs = OVFEnvelopeUtils.getAllVirtualSystems(envelope);

        assertEquals("how many virtual systems ", 3, vs.size());

        assertNotNull("there is a virtual system collection ", OVFEnvelopeUtils
            .getVirtualSystemCollection(envelope));

        vs =
            OVFEnvelopeUtils.getVirtualSystemsFromCollection(OVFEnvelopeUtils
                .getVirtualSystemCollection(envelope));

        assertEquals("how many virtual systems on the collection ", 3, vs.size());
    }

    public void testAddVirtualSystem()
    {
        VirtualSystemType vsystem1 = new VirtualSystemType();
        vsystem1.setId("test1System");

        VirtualSystemType vsystem2 = new VirtualSystemType();
        vsystem2.setId("test2System");

        VirtualSystemType vsystem3 = new VirtualSystemType();
        vsystem3.setId("test2System");

        VirtualSystemCollectionType vscollection = new VirtualSystemCollectionType();
        vscollection.setId("testCollection");

        // TODO require add the mandatory sections.

        OVFEnvelopeUtils.addVirtualSystem(envelope, vsystem1);
        // TODO fail if try to add the same VS.
        // TODO test when the envelope only have one vs (created a vs collection)

        assertEquals("how many virtual systems after add ", 4, OVFEnvelopeUtils.getAllVirtualSystems(
            envelope).size());

        try
        {
            OVFEnvelopeUtils.addVirtualSystem(envelope, vsystem2, "notAnyVSCollectionId");
            fail("id not found");
        }
        catch (IdNotFound e)
        {
            assertTrue("expected exception idnotfound", true);
        }

        assertEquals("how many virtual systems after add on NOT present collection ", 4,
            OVFEnvelopeUtils.getAllVirtualSystems(envelope).size());

        try
        {
            OVFEnvelopeUtils.addVirtualSystem(envelope, vsystem2, OVFEnvelopeUtils
                .getVirtualSystemCollection(envelope).getId());
            assertTrue("expected id found", true);
        }
        catch (IdNotFound e)
        {
            fail("id not found");
        }

        assertEquals("how many virtual systems after add on present collection ", 5, OVFEnvelopeUtils
            .getAllVirtualSystems(envelope).size());

        OVFEnvelopeUtils.addVirtualSystem(vscollection, vsystem3);

        assertEquals("a collection with one system ", 1, OVFEnvelopeUtils
            .getVirtualSystemsFromCollection(vscollection).size());

        OVFEnvelopeUtils.addVirtualSystemCollection(envelope, vscollection);

        assertEquals("how many virtual systems after add collection ", 6, OVFEnvelopeUtils
            .getAllVirtualSystems(envelope).size());

        // TODO EnvelopeUtils.addVirtualSystemCollection(envelope, vscollection,
        // "present/NOTpresent");
    }*/

    public void setUp()
    {
        File envFile = new File(testOVFPath);

        try
        {
            envelope = OVFSerializer.getInstance().readXMLEnvelope(new FileInputStream(envFile));
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail("can create envelope");
        }
    }

    public void tearDown()
    {
        envelope = null;
    }

    /*
    // see where is <Name>
    public  void printName()
    {
        Map<QName, String> htother;
        //= envelope.getOtherAttributes(); NOT ON THE ENVELOPE
        
        // htother = EnvelopeUtils.getVirtualSystemCollection(envelope).getOtherAttributes(); NOT ON COLLECTION
        
        for(VirtualSystemType vs : OVFEnvelopeUtils.getAllVirtualSystems(envelope))
        {
            htother = vs.getOtherAttributes();
            for(QName an : htother.keySet())
            {
                log.debug("key: "+an.toString()+" : "+htother.get(an));
            }
        }  
    }*/
    
    
    public static void main(String[] args)
    {
        OVFEnvelopeTest test = new OVFEnvelopeTest();
        test.setUp();
        // test.testFileReferences();
        //test.testGetVirtualSystems();
        //test.testAddVirtualSystem();

        //test.printName();
    }
}
