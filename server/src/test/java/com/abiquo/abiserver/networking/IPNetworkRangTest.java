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

/**
 * 
 */
package com.abiquo.abiserver.networking;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.Test;

import com.abiquo.abiserver.exception.InvalidIPAddressException;

/**
 * @author abiquo
 */
public class IPNetworkRangTest
{
    /**
     * Test method for
     * {@link com.abiquo.abiserver.networking.IPNetworkRang#masktoNumberOfNodes(java.lang.Integer)}.
     */
    @Test
    public void testMasktoNumberOfNodes()
    {

        // 32 bits mask
        assertEquals((Integer) 1, IPNetworkRang.masktoNumberOfNodes(32));

        // 24 bits mask
        assertEquals((Integer) 256, IPNetworkRang.masktoNumberOfNodes(24));

        // 16 bits mask
        assertEquals((Integer) 65536, IPNetworkRang.masktoNumberOfNodes(16));

        // 8 bits mask
        assertEquals((Integer) 16777216, IPNetworkRang.masktoNumberOfNodes(8));

        // 0 bits mask
        assertEquals((Integer) 2147483647, IPNetworkRang.masktoNumberOfNodes(0));
    }

    /**
     * Test method for
     * {@link com.abiquo.abiserver.networking.IPNetworkRang#numberOfNodesToMask(java.lang.Integer)}.
     */
    @Test
    public void testNumberOfNodesToMask()
    {
        // 1 node
        assertEquals((Integer) 32, IPNetworkRang.numberOfNodesToMask(1));

        // 256 nodes
        assertEquals((Integer) 24, IPNetworkRang.numberOfNodesToMask(256));

        // 65536 nodes
        assertEquals((Integer) 16, IPNetworkRang.numberOfNodesToMask(65536));

        // 16777216 nodes
        assertEquals((Integer) 8, IPNetworkRang.numberOfNodesToMask(16777216));

        // 2147483647 nodes
        assertEquals((Integer) 2, IPNetworkRang.numberOfNodesToMask(2147483647));
    }

    /**
     * Test method for {@link com.abiquo.abiserver.networking.IPAddress#previousIPAddress()}.
     * 
     * @throws InvalidIPAddressException
     */
    @Test
    public void testLastIPAddressWithNumNodes() throws InvalidIPAddressException
    {
        IPAddress beginIP = IPAddress.newIPAddress("23.23.23.23");
        assertTrue(IPNetworkRang.lastIPAddressWithNumNodes(beginIP, 20).toString()
            .equalsIgnoreCase("23.23.23.42"));
        assertTrue(IPNetworkRang.lastIPAddressWithNumNodes(beginIP, 1293879).toString()
            .equalsIgnoreCase("23.42.213.77"));
        assertTrue(IPNetworkRang.lastIPAddressWithNumNodes(beginIP, 1).toString()
            .equalsIgnoreCase("23.23.23.23"));
        assertTrue(IPNetworkRang.lastIPAddressWithNumNodes(beginIP, 871287).toString()
            .equalsIgnoreCase("23.36.98.141"));
        assertTrue(IPNetworkRang.lastIPAddressWithNumNodes(beginIP, 4294967).toString()
            .equalsIgnoreCase("23.88.160.77"));
        assertNull(IPNetworkRang.lastIPAddressWithNumNodes(beginIP, 0));
        assertTrue(IPNetworkRang.lastIPAddressWithNumNodes(beginIP, 1).toString()
            .equalsIgnoreCase("23.23.23.23"));
        assertTrue(IPNetworkRang.lastIPAddressWithNumNodes(beginIP, 2).toString()
            .equalsIgnoreCase("23.23.23.24"));
        assertTrue(IPNetworkRang.lastIPAddressWithNumNodes(beginIP, 233).toString()
            .equalsIgnoreCase("23.23.23.255"));
        assertTrue(IPNetworkRang.lastIPAddressWithNumNodes(beginIP, 12343).toString()
            .equalsIgnoreCase("23.23.71.77"));
        assertTrue(IPNetworkRang.lastIPAddressWithNumNodes(beginIP, 78).toString()
            .equalsIgnoreCase("23.23.23.100"));
        assertTrue(IPNetworkRang.lastIPAddressWithNumNodes(beginIP, 123).toString()
            .equalsIgnoreCase("23.23.23.145"));
        assertTrue(IPNetworkRang.lastIPAddressWithNumNodes(beginIP, 16777216).toString()
            .equalsIgnoreCase("24.23.23.22"));
        assertTrue(IPNetworkRang.lastIPAddressWithNumNodes(beginIP, 65536).toString()
            .equalsIgnoreCase("23.24.23.22"));
        assertTrue(IPNetworkRang.lastIPAddressWithNumNodes(beginIP, 256).toString()
            .equalsIgnoreCase("23.23.24.22"));
        assertTrue(IPNetworkRang.lastIPAddressWithNumNodes(beginIP, 34).toString()
            .equalsIgnoreCase("23.23.23.56"));

        beginIP = IPAddress.newIPAddress("10.0.0.0");
        assertTrue(IPNetworkRang.lastIPAddressWithNumNodes(beginIP, 256).toString()
            .equalsIgnoreCase("10.0.0.255"));
    }
}
