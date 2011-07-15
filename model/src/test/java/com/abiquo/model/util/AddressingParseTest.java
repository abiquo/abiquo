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

import static com.abiquo.testng.TestConfig.BASIC_UNIT_TESTS;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Unit tests for the Addressing parsing.
 * 
 * @author ibarrera
 */
@Test(groups = BASIC_UNIT_TESTS)
public class AddressingParseTest
{
    @Test
    public void testAddressingGetIP() throws Exception
    {
        checkIP("10.60.1.26", "ip-10.60.1.26:3260-iscsi-iqn.2001-04.com.acme-lun-27");
        checkIP("10.60.1.26", "ip-10.60.1.26:3260-iscsi-iqn.2001-04.com.acme-lun-27-part512");
        checkIP("10.60.1.26",
            "ip-10.60.1.26:3260-iscsi-iqn.1986-03.com.sun:02:bc3833ad-f8d1-4fcd-ce84-f450c40f166c-lun-0-part4");
    }

    @Test
    public void testAddressingGetPort() throws Exception
    {
        checkPort("3260", "ip-10.60.1.26:3260-iscsi-iqn.2001-04.com.acme-lun-27");
        checkPort("3260", "ip-10.60.1.26:3260-iscsi-iqn.2001-04.com.acme-lun-27-part512");
        checkPort("3260",
            "ip-10.60.1.26:3260-iscsi-iqn.1986-03.com.sun:02:bc3833ad-f8d1-4fcd-ce84-f450c40f166c-lun-0-part4");
    }

    @Test
    public void testAddressingGetPortal() throws Exception
    {
        checkPortal("10.60.1.26:3260", "ip-10.60.1.26:3260-iscsi-iqn.2001-04.com.acme-lun-27");
        checkPortal("10.60.1.26:3260",
            "ip-10.60.1.26:3260-iscsi-iqn.2001-04.com.acme-lun-27-part512");
        checkPortal("10.60.1.26:3260",
            "ip-10.60.1.26:3260-iscsi-iqn.1986-03.com.sun:02:bc3833ad-f8d1-4fcd-ce84-f450c40f166c-lun-0-part4");
    }

    @Test
    public void testAddressingGetPortalFromURL() throws Exception
    {
        checkPortalFromURL("10.60.1.24:3260", "http://10.60.1.24:3260/fs_rest");
        checkPortalFromURL("10.60.1.24:8080", "http://10.60.1.24:8080/fs_rest");
    }

    @Test
    public void testAddressingGetIQN() throws Exception
    {
        checkIQN("iqn.2001-04.com.acme", "ip-10.60.1.26:3260-iscsi-iqn.2001-04.com.acme-lun-27");
        checkIQN("iqn.1986-03.com.sun:02:bc3833ad-f8d1-4fcd-ce84-f450c40f166c",
            "ip-10.60.1.26:3260-iscsi-iqn.1986-03.com.sun:02:bc3833ad-f8d1-4fcd-ce84-f450c40f166c-lun-0");
    }

    @Test
    public void testAddressingGetLUN() throws Exception
    {
        checkLUN("27", "ip-10.60.1.26:3260-iscsi-iqn.2001-04.com.acme-lun-27");
        checkLUN("27", "ip-10.60.1.26:3260-iscsi-iqn.2001-04.com.acme-lun-27-part512");
        checkLUN("0",
            "ip-10.60.1.26:3260-iscsi-iqn.1986-03.com.sun:02:bc3833ad-f8d1-4fcd-ce84-f450c40f166c-lun-0-part4");
    }

    @Test
    public void testAddressingGetPartition() throws Exception
    {
        checkPartition(null, "ip-10.60.1.26:3260-iscsi-iqn.2001-04.com.acme-lun-27");
        checkPartition("512", "ip-10.60.1.26:3260-iscsi-iqn.2001-04.com.acme-lun-27-part512");
        checkPartition("4",
            "ip-10.60.1.26:3260-iscsi-iqn.1986-03.com.sun:02:bc3833ad-f8d1-4fcd-ce84-f450c40f166c-lun-0-part4");
    }

    @Test
    public void testAddressingToPath() throws Exception
    {
        checkToPath("ip-10.60.1.26:3260-iscsi-iqn.2001-04.com.acme-lun-27", "10.60.1.26", 3260,
            "iqn.2001-04.com.acme", 27);
        checkToPath("ip-10.60.1.26:3260-iscsi-iqn.2001-04.com.acme-lun-27", "10.60.1.26:3260",
            "iqn.2001-04.com.acme", 27);
        checkToPathUnexpected("ip-10.60.1.27:3260-iscsi-iqn.2001-04.com.acme-lun-27", "10.60.1.26",
            3260, "iqn.2001-04.com.acme", 27);
        checkToPathUnexpected("ip-10.60.1.26:3260-iscsi-iqn.2001-04.com.acme-lun-27", "10.60.1.26",
            3260, "iqn.2000-05.com.acme", 27);
        checkToPathUnexpected("ip-10.60.1.26:3260-iscsi-iqn.2001-04.com.acme-lun-27", "10.60.1.26",
            3260, "iqn.2001-04.com.acme", 1);
    }

    @Test
    public void testAddressingToPathInvalid() throws Exception
    {
        checkToPathInvalid("ip-10.60.1.26:3260-iscsi-iqn.2001-04.com.acme-lun-27", "10.60.1.256",
            3260, "iqn.2001-04.com.acme", 27);
        checkToPathInvalid("ip-10.60.1.26:3260-iscsi-iqn.2001-04.com.acme-lun-27", "10.60.1.26",
            -1, "iqn.2001-04.com.acme", 27);
        checkToPathInvalid("ip-10.60.1.26:3260-iscsi-iqn.2001-04.com.acme-lun-27", "10.60.1.26",
            3260, "iqn.2001.04.com.acme", 27);
        checkToPathInvalid("ip-10.60.1.26:3260-iscsi-iqn.2001-04.com.acme-lun-27", "10.60.1.26",
            3260, "iqn.2001-04.com.acme", -3);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testAddressingGetInvalidPath() throws Exception
    {
        AddressingUtils.getIQN("ip-10.60.1.26:3260-iscsi-iqn.2001.04.com.acme-lun-27-part512");
    }

    private void checkIP(final String expectedIP, final String path)
    {
        String ip = AddressingUtils.getIP(path);
        Assert.assertEquals(expectedIP, ip);
    }

    private void checkPort(final String expectedPort, final String path)
    {
        String port = AddressingUtils.getPort(path);
        Assert.assertEquals(expectedPort, port);
    }

    private void checkPortal(final String expectedPortal, final String path)
    {
        String portal = AddressingUtils.getPortal(path);
        Assert.assertEquals(expectedPortal, portal);
    }

    private void checkPortalFromURL(final String expectedPortal, final String url)
    {
        String portal = AddressingUtils.getPortalFromURL(url);
        Assert.assertEquals(expectedPortal, portal);
    }

    private void checkIQN(final String expectedIQN, final String path)
    {
        String iqn = AddressingUtils.getIQN(path);
        Assert.assertEquals(expectedIQN, iqn);
    }

    private void checkLUN(final String expectedLUN, final String path)
    {
        String lun = AddressingUtils.getLUN(path);
        Assert.assertEquals(expectedLUN, lun);
    }

    private void checkPartition(final String expectedPartition, final String path)
    {
        String partition = AddressingUtils.getPartition(path);
        Assert.assertEquals(expectedPartition, partition);
    }

    private void checkToPath(final String expectedPath, final String ip, final int port,
        final String iqn, final int lun)
    {
        String path = AddressingUtils.toPath(ip, port, iqn, lun);
        Assert.assertEquals(expectedPath, path);
    }

    private void checkToPath(final String expectedPath, final String portal, final String iqn,
        final int lun)
    {
        String path = AddressingUtils.toPath(portal, iqn, lun);
        Assert.assertEquals(expectedPath, path);
    }

    private void checkToPathInvalid(final String expectedPath, final String ip, final int port,
        final String iqn, final int lun)
    {
        try
        {
            AddressingUtils.toPath(ip, port, iqn, lun);
            Assert.fail();
        }
        catch (IllegalArgumentException ex)
        {
            // Expected exception
        }
    }

    private void checkToPathUnexpected(final String expectedPath, final String ip, final int port,
        final String iqn, final int lun)
    {
        String path = AddressingUtils.toPath(ip, port, iqn, lun);
        Assert.assertFalse(path.equals(expectedPath));
    }

}
