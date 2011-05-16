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

package com.abiquo.model.util;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Unit tests for the Addressing validation.
 * 
 * @author ibarrera
 */
public class AddressingValidationTest
{
    @Test
    public void testAddressingIP() throws Exception
    {
        // Valid IPs
        Assert.assertTrue(AddressingUtils.isValidIP("0.0.0.0"));
        Assert.assertTrue(AddressingUtils.isValidIP("255.255.255.255"));
        Assert.assertTrue(AddressingUtils.isValidIP("10.60.1.0"));
        Assert.assertTrue(AddressingUtils.isValidIP("83.15.161.97"));

        // Invalid IPs
        Assert.assertFalse(AddressingUtils.isValidIP(""));
        Assert.assertFalse(AddressingUtils.isValidIP("0.0.0"));
        Assert.assertFalse(AddressingUtils.isValidIP("0.0.256.0"));
        Assert.assertFalse(AddressingUtils.isValidIP("a.b.c.d"));
        Assert.assertFalse(AddressingUtils.isValidIP("invalid.ip"));
        Assert.assertFalse(AddressingUtils.isValidIP("-1.10.60.15"));
        Assert.assertFalse(AddressingUtils.isValidIP(".23.16.56"));
        Assert.assertFalse(AddressingUtils.isValidIP("10.23..56"));
    }

    @Test
    public void testAddressingPort() throws Exception
    {
        // Valid Ports
        Assert.assertTrue(AddressingUtils.isValidPort("1"));
        Assert.assertTrue(AddressingUtils.isValidPort("10"));
        Assert.assertTrue(AddressingUtils.isValidPort("7777"));
        Assert.assertTrue(AddressingUtils.isValidPort("65535"));

        // Invalid Ports
        Assert.assertFalse(AddressingUtils.isValidPort("0"));
        Assert.assertFalse(AddressingUtils.isValidPort("00000"));
        Assert.assertFalse(AddressingUtils.isValidPort("65536"));
        Assert.assertFalse(AddressingUtils.isValidPort("123456"));
        Assert.assertFalse(AddressingUtils.isValidPort("-1"));
        Assert.assertFalse(AddressingUtils.isValidPort("abc"));
    }

    @Test
    public void testAddressingPortal() throws Exception
    {
        // Valid Portals
        Assert.assertTrue(AddressingUtils.isValidPortal("10.60.1.0:3260"));
        Assert.assertTrue(AddressingUtils.isValidPortal("83.15.161.97:65535"));

        // Invalid Portals
        Assert.assertFalse(AddressingUtils.isValidPortal("83.15.161.97"));
        Assert.assertFalse(AddressingUtils.isValidPortal("83.15.161.97:"));
        Assert.assertFalse(AddressingUtils.isValidPortal("83.15.161.97:0"));
        Assert.assertFalse(AddressingUtils.isValidPortal("83.15.161.97:-1"));
        Assert.assertFalse(AddressingUtils.isValidPortal("83.15.161.97:65536"));
    }

    @Test
    public void testAddressingIQN() throws Exception
    {
        // Valid IQNs
        Assert.assertTrue(AddressingUtils.isValidIQN("iqn.2001-04.com.acme"));
        Assert.assertTrue(AddressingUtils.isValidIQN("iqn.1992-08.com.netapp:sn.99929383"));
        Assert.assertTrue(AddressingUtils.isValidIQN("iqn.2001-04.com.acme:storage.tape.sys1.xyz"));
        Assert.assertTrue(AddressingUtils.isValidIQN("iqn.1993-08.org.debian:01:d7c03b8ec50"));
        Assert.assertTrue(AddressingUtils
            .isValidIQN("iqn.1986-03.com.sun:02:c62066ca-870c-6bf5-f419-b38a24940468"));

        // Invalid IQNs
        Assert.assertFalse(AddressingUtils.isValidIQN(""));
        Assert.assertFalse(AddressingUtils.isValidIQN("iqn.2001-04.com.acme:"));
        Assert.assertFalse(AddressingUtils.isValidIQN("iqn.1993-08.:01:d7c03b8ec50"));
        Assert.assertFalse(AddressingUtils.isValidIQN("iqn-1986-03.com.sun:02:c62066ca-870c"));
        Assert.assertFalse(AddressingUtils.isValidIQN("iqn-1986-03.com.sun:02::::c62066ca-870c"));
    }

    @Test
    public void testAddressingPath() throws Exception
    {
        // Valid Paths
        checkValidPath("ip-10.60.1.26:3260-iscsi-iqn.1986-03.com.sun:02:bc3833ad-f8d1-4fcd-ce84-f450c40f166c-lun-0");
        checkValidPath("ip-10.60.1.26:3260-iscsi-iqn.1986-03.com.sun:02:bc3833ad-f8d1-4fcd-ce84-f450c40f166c-lun-0-part4");
        checkValidPath("ip-10.60.1.26:3260-iscsi-iqn.2001-04.com.acme-lun-27");
        checkValidPath("ip-10.60.1.26:3260-iscsi-iqn.2001-04.com.acme-lun-27-part512");

        // Invalid Paths
        checkInvalidPath("ip-10.60.1.26:3260-iscsi-iqn.1986-03.com.sun:02:bc3833ad-f8d1-4fcd-ce84-f450c40f166c-lun-");
        checkInvalidPath("ip-10.60.1.26:3260-iscsi-iqn.1986-03.com.sun:02:bc3833ad-f8d1-4fcd-ce84-f450c40f166c-lun--1");
        checkInvalidPath("ip-10.60.1.26:3260-iscsi-iqn.1986-03.com.sun:02:bc3833ad-f8d1-4fcd-ce84-f450c40f166c-lun-a");
        checkInvalidPath("ip-10.60.1.26:3260-iscsi-iqn.1986-03.com.sun:02:bc3833ad-f8d1-4fcd-ce84-f450c40f166c-lun-0-part");
        checkInvalidPath("ip-10.60.1.26:3260-iscsi-iqn.1986-03.com.sun:02:bc3833ad-f8d1-4fcd-ce84-f450c40f166c-lun-0-pat4");
        checkInvalidPath("ip-10.60.1.26:3260-iqn.2001-04.com.acme-lun-27");
        checkInvalidPath("ip-10.60.1.26-iscsi-iqn.2001-04.com.acme-lun-27");
        checkInvalidPath("ip-10.60.1.26:-iscsi-iqn.2001-04.com.acme-lun-27");
    }

    @Test
    public void testAddressingDeviceId() throws Exception
    {
        // Valid Device IDs
        Assert.assertTrue(AddressingUtils.isValidDeviceId("00000000000000000000000000000000"));
        Assert.assertTrue(AddressingUtils.isValidDeviceId("600144f03fe8460000004c7e14e50016"));
        Assert.assertTrue(AddressingUtils.isValidDeviceId("99999999999999999999999999999999"));

        // Invalid Device IDs
        Assert.assertFalse(AddressingUtils
            .isValidDeviceId("scsi-3600144f03fe8460000004c7e14e50016"));
        Assert.assertFalse(AddressingUtils
            .isValidDeviceId("scsi-3600144f03fe8460000004c7e14e50016-part4"));
        Assert.assertFalse(AddressingUtils.isValidDeviceId("600144f03fe8460000004c7e14e5001"));
        Assert.assertFalse(AddressingUtils.isValidDeviceId("600144f03fe8460000004c7e14e500161"));
    }

    private void checkValidPath(final String path) throws Exception
    {
        Assert.assertTrue(AddressingUtils.isValidPath(path));
    }

    private void checkInvalidPath(final String path) throws Exception
    {
        Assert.assertFalse(AddressingUtils.isValidPath(path));
    }
}
