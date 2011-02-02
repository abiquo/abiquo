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

package com.abiquo.vsm.migration;

import static org.testng.Assert.assertEquals;
import static org.testng.AssertJUnit.assertNotNull;
import static org.testng.AssertJUnit.assertNull;
import static org.testng.AssertJUnit.assertTrue;

import java.util.UUID;

import org.testng.annotations.Test;

import com.abiquo.vsm.TestBase;
import com.abiquo.vsm.model.PhysicalMachine;
import com.abiquo.vsm.model.VirtualMachine;
import com.abiquo.vsm.redis.dao.RedisDao;
import com.abiquo.vsm.redis.dao.RedisDaoFactory;
import com.abiquo.vsm.redis.dao.RedisTestDaoFactory;

public class MigrationTest extends TestBase
{
    RedisDao dao;

    @Override
    protected void setUp() throws Exception
    {
        dao = RedisDaoFactory.getInstance();
    }

    @Test(enabled = false)
    public void test_migration()
    {
        String es = "http://uknwon:8080/es";

        String uuid0 = randomUUID();
        String uuid1 = randomUUID();
        String uuid2 = randomUUID();
        String uuid3 = randomUUID();

        RedisWrapper wrapper = new RedisWrapper(TEST_HOST, TEST_PORT, TEST_DATABASE);

        wrapper.insertSubscription(uuid0, es, "10.60.1.79", "KVM", "user0", "password0");
        wrapper.insertSubscription(uuid1, es, "10.60.1.80", "vmx-04", "user1", "password1");
        wrapper.insertSubscription(uuid2, es, "10.60.1.80", "vmx-04", "user1", "password1");
        wrapper.insertSubscription(uuid3, es, "10.60.1.80", "vmx-04", "user1", "password1");

        new Migration(TEST_HOST, TEST_PORT, TEST_DATABASE).migrate();

        assertTrue(wrapper.getAllSubscriptionIds().isEmpty());

        RedisTestDaoFactory.selectDatabase(TEST_HOST, TEST_PORT, TEST_DATABASE);

        assertEquals(dao.findAllPhysicalMachines().size(), 2);
        assertEquals(dao.findAllVirtualMachines().size(), 4);

        PhysicalMachine machine79 = dao.findPhysicalMachineByAddress("10.60.1.79");
        PhysicalMachine machine80 = dao.findPhysicalMachineByAddress("10.60.1.80");

        assertNotNull(machine79);
        assertNotNull(machine80);

        assertEquals(machine79.getAddress(), "10.60.1.79");
        assertEquals(machine79.getType(), "KVM");
        assertEquals(machine79.getUsername(), "user0");
        assertEquals(machine79.getPassword(), "password0");

        assertEquals(machine80.getAddress(), "10.60.1.80");
        assertEquals(machine80.getType(), "vmx-04");
        assertEquals(machine80.getUsername(), "user1");
        assertEquals(machine80.getPassword(), "password1");

        VirtualMachine vmachine0 = dao.findVirtualMachineByName(uuid0);
        VirtualMachine vmachine1 = dao.findVirtualMachineByName(uuid1);
        VirtualMachine vmachine2 = dao.findVirtualMachineByName(uuid2);
        VirtualMachine vmachine3 = dao.findVirtualMachineByName(uuid3);

        assertNotNull(vmachine0);
        assertNotNull(vmachine1);
        assertNotNull(vmachine2);
        assertNotNull(vmachine3);

        assertEquals(vmachine0.getName(), uuid0);
        assertEquals(vmachine0.getPhysicalMachine().getId(), machine79.getId());
        assertNull(vmachine0.getLastKnownState());

        assertEquals(vmachine1.getName(), uuid1);
        assertEquals(vmachine1.getPhysicalMachine().getId(), machine80.getId());
        assertNull(vmachine1.getLastKnownState());

        assertEquals(vmachine2.getName(), uuid2);
        assertEquals(vmachine2.getPhysicalMachine().getId(), machine80.getId());
        assertNull(vmachine2.getLastKnownState());

        assertEquals(vmachine3.getName(), uuid3);
        assertEquals(vmachine3.getPhysicalMachine().getId(), machine80.getId());
        assertNull(vmachine3.getLastKnownState());
    }

    private String randomUUID()
    {
        return UUID.randomUUID().toString();
    }
}
