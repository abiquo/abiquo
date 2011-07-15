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

import static org.testng.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;

import org.dmtf.schemas.ovf.envelope._1.EnvelopeType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.abiquo.ovfmanager.ovf.xml.OVFSerializer;

public class OVFSerializerTest
{
    private final static Logger log = LoggerFactory.getLogger(OVFSerializerTest.class);

    private final static URI testOVFPath = new File("src/test/resources/myservice.ovf").toURI();

    private EnvelopeType envelope;

    @BeforeMethod
    public void setUp() throws Exception
    {
    }

    @AfterMethod
    public void tearDown() throws Exception
    {
    }

    @Test
    public void testWriteXMLEnvelopeTypeOutputStream() // throws IOException
    {

        testReadXMLEnvelope();

        File envFile = new File("src/test/resources/testWrite.xml");
        try
        {
            envFile.createNewFile();
        }
        catch (IOException e1)
        {
            e1.printStackTrace();
            fail();
        }

        try
        {
            OVFSerializer.getInstance().writeXML(envelope, new FileOutputStream(envFile));
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail("can write the envelope");
        }
    }

    @Test
    public void testReadXMLEnvelope()
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

}
