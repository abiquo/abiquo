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

package com.abiquo.abiserver.networking;

import junit.framework.TestCase;

import org.junit.Test;

import com.abiquo.abiserver.exception.InvalidIPAddressException;

/**
 * @author abiquo
 */
public class IPAddressTest extends TestCase
{

    /**
     * Test method for {@link com.abiquo.abiserver.networking.IPAddress#nextIPAddress()}.
     * @throws InvalidIPAddressException 
     */
    @Test
    public void testNextIPAddress() throws InvalidIPAddressException
    {
        assertTrue(IPAddress.newIPAddress("0.0.0.0").nextIPAddress().toString().equalsIgnoreCase(
            "0.0.0.1"));
        assertTrue(IPAddress.newIPAddress("0.0.1.0").nextIPAddress().toString().equalsIgnoreCase(
            "0.0.1.1"));
        assertTrue(IPAddress.newIPAddress("22.33.43.55").nextIPAddress().toString()
            .equalsIgnoreCase("22.33.43.56"));
        assertTrue(IPAddress.newIPAddress("0.0.1.255").nextIPAddress().toString().equalsIgnoreCase(
            "0.0.2.0"));
        assertTrue(IPAddress.newIPAddress("0.0.255.255").nextIPAddress().toString()
            .equalsIgnoreCase("0.1.0.0"));
        assertTrue(IPAddress.newIPAddress("0.255.255.255").nextIPAddress().toString()
            .equalsIgnoreCase("1.0.0.0"));
        assertTrue(IPAddress.newIPAddress("0.255.0.255").nextIPAddress().toString()
            .equalsIgnoreCase("0.255.1.0"));
        assertTrue(IPAddress.newIPAddress("255.255.255.255").nextIPAddress().toString()
            .equalsIgnoreCase("0.0.0.0"));
        assertTrue(IPAddress.newIPAddress("0.255.0.abc").nextIPAddress().toString()
            .equalsIgnoreCase(""));
    }

    /**
     * Test method for {@link com.abiquo.abiserver.networking.IPAddress#previousIPAddress()}.
     * @throws InvalidIPAddressException 
     */
    @Test
    public void testPreviousIPAddress() throws InvalidIPAddressException
    {
        assertTrue(IPAddress.newIPAddress("0.0.0.1").previousIPAddress().toString()
            .equalsIgnoreCase("0.0.0.0"));
        assertTrue(IPAddress.newIPAddress("0.0.1.1").previousIPAddress().toString()
            .equalsIgnoreCase("0.0.1.0"));
        assertTrue(IPAddress.newIPAddress("22.33.43.56").previousIPAddress().toString()
            .equalsIgnoreCase("22.33.43.55"));
        assertTrue(IPAddress.newIPAddress("0.0.2.0").previousIPAddress().toString()
            .equalsIgnoreCase("0.0.1.255"));
        assertTrue(IPAddress.newIPAddress("0.1.0.0").previousIPAddress().toString()
            .equalsIgnoreCase("0.0.255.255"));
        assertTrue(IPAddress.newIPAddress("1.0.0.0").previousIPAddress().toString()
            .equalsIgnoreCase("0.255.255.255"));
        assertTrue(IPAddress.newIPAddress("0.255.1.0").previousIPAddress().toString()
            .equalsIgnoreCase("0.255.0.255"));
        assertTrue(IPAddress.newIPAddress("0.0.0.0").previousIPAddress().toString()
            .equalsIgnoreCase("255.255.255.255"));
        assertTrue(IPAddress.newIPAddress("0.255.0.abc").previousIPAddress().toString()
            .equalsIgnoreCase(""));
    }


}
