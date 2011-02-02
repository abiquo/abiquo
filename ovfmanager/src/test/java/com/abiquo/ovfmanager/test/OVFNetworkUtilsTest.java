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

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.dmtf.schemas.ovf.envelope._1.NetworkSectionType;
import org.dmtf.schemas.ovf.envelope._1.NetworkSectionType.Network;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.abiquo.ovfmanager.ovf.OVFEnvelopeUtils;
import com.abiquo.ovfmanager.ovf.exceptions.RequiredAttributeException;
import com.abiquo.ovfmanager.ovf.exceptions.XMLException;
import com.abiquo.ovfmanager.ovf.xml.OVFSerializer;

public class OVFNetworkUtilsTest
{
    
    NetworkSectionType networkSectionType;

    @Before
    public void setUp() throws Exception
    {
        networkSectionType = new NetworkSectionType();
    }

    @After
    public void tearDown() throws Exception
    {
    }

    @SuppressWarnings("static-access")
    @Test
    public void testNetworkUtils()
    {
        //Correct creating sections.
        try
        {
            Network netOne = OVFEnvelopeUtils.networkSection.createNetwork("eth0", "Network Interface number one");
            Network netTwo = OVFEnvelopeUtils.networkSection.createNetwork("eth1", null);
            assertTrue (netOne.getName().equalsIgnoreCase("eth0"));
            assertTrue (netOne.getDescription().getValue().equalsIgnoreCase("Network Interface Number One"));
            assertTrue (netTwo.getName().equalsIgnoreCase("eth1"));
        }
        catch (RequiredAttributeException e)
        {
            e.printStackTrace();
            fail();
        }
        
        //Incorrect creating section
         try
        {
            OVFEnvelopeUtils.networkSection.createNetwork(null, null);
            fail();
        }
        catch (RequiredAttributeException e)
        {
            //Do nothing
        }
        
        NetworkSectionType networkSection = new NetworkSectionType();

        OVFSerializer serializer = OVFSerializer.getInstance();
        try
        {
            serializer.writeXML(networkSection, System.out);
        }
        catch (XMLException e)
        {
            e.printStackTrace();
            fail();
        }
    }

}
