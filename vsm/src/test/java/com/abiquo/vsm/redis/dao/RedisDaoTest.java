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
package com.abiquo.vsm.redis.dao;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.testng.annotations.Test;

import redis.clients.johm.JOhm;

import com.abiquo.vsm.TestBase;
import com.abiquo.vsm.model.PhysicalMachine;
import com.abiquo.vsm.model.VirtualMachine;
import com.abiquo.vsm.model.VirtualMachinesCache;

/**
 * Unit tests for the {@link RedisDao} class. The tests use one instance of the latest redis
 * version, this instance should run at localhost in the default port 6379.
 * 
 * @author eruiz@abiquo.com
 */
public class RedisDaoTest extends TestBase
{

    private RedisDao dao;

    @Override
    public void setUp() throws Exception
    {
        dao = new RedisDao();
    }

    @Test
    public void test_savePhysicalMachine()
    {
        PhysicalMachine machine = new PhysicalMachine();
        machine.setAddress("10.60.1.79");
        machine.setType("vmx-04");
        machine.setUsername("thomas.sullivan");
        machine.setPassword("magnum");

        PhysicalMachine saved = dao.save(machine);

        assertNotNull(saved);
        assertEquals(machine.getAddress(), saved.getAddress());
        assertEquals(machine.getType(), saved.getType());
        assertEquals(machine.getUsername(), saved.getUsername());
        assertEquals(machine.getPassword(), saved.getPassword());
        assertNull(machine.getVirtualMachines());

        PhysicalMachine recovered = JOhm.get(PhysicalMachine.class, saved.getId());

        assertNotNull(recovered);
        assertEquals(machine.getAddress(), recovered.getAddress());
        assertEquals(machine.getType(), recovered.getType());
        assertEquals(machine.getUsername(), recovered.getUsername());
        assertEquals(machine.getPassword(), recovered.getPassword());
        assertNull(recovered.getVirtualMachines());
    }

    @Test(expectedExceptions = RuntimeException.class)
    public void test_savePhysicalMachineForceRuntimeException()
    {
        PhysicalMachine machine0 = new PhysicalMachine();
        machine0.setAddress("10.60.1.79");
        machine0.setType("KVM");
        machine0.setUsername("thomas.sullivan");
        machine0.setPassword("magnum");

        PhysicalMachine saved = dao.save(machine0);
        assertNotNull(saved);

        PhysicalMachine machine1 = new PhysicalMachine();
        machine1.setAddress("10.60.1.79");
        machine1.setType("KVM");
        machine1.setUsername("thomas.sullivan");
        machine1.setPassword("magnum");

        saved = dao.save(machine1);
    }

    @Test
    public void test_saveVirtualMachine()
    {
        VirtualMachine vmachine = new VirtualMachine();
        vmachine.setName(UUID.randomUUID().toString());

        VirtualMachine saved = dao.save(vmachine);

        assertNotNull(saved);
        assertEquals(vmachine.getName(), saved.getName());
        assertNull(saved.getPhysicalMachine());
        assertNull(saved.getLastKnownState());

        vmachine.setLastKnownState("POWER_ON");
        saved = dao.save(vmachine);

        assertNotNull(saved);
        assertEquals(vmachine.getName(), saved.getName());
        assertEquals(vmachine.getLastKnownState(), saved.getLastKnownState());
        assertNull(saved.getPhysicalMachine());

        PhysicalMachine machine = new PhysicalMachine();
        machine.setAddress("10.60.1.79");
        machine.setType("vmx-04");
        machine.setUsername("thomas.sullivan");
        machine.setPassword("magnum");

        JOhm.save(machine);

        saved.setPhysicalMachine(machine);
        saved = dao.save(saved);

        assertNotNull(saved);
        assertEquals(vmachine.getName(), saved.getName());
        assertEquals(vmachine.getLastKnownState(), saved.getLastKnownState());
        assertEquals(machine.getId(), vmachine.getPhysicalMachine().getId());
    }

    @Test(expectedExceptions = RuntimeException.class)
    public void test_saveVirtualMachineForceRuntimeException()
    {
        VirtualMachine vmachine0 = new VirtualMachine();
        vmachine0.setName(UUID.randomUUID().toString());

        VirtualMachine saved = dao.save(vmachine0);
        assertNotNull(saved);

        VirtualMachine vmachine1 = new VirtualMachine();
        vmachine1.setName(vmachine0.getName());

        saved = dao.save(vmachine1);
    }

    @Test
    public void test_deletePhysicalMachine()
    {
        PhysicalMachine machine = new PhysicalMachine();
        machine.setAddress("10.60.1.79");
        machine.setType("vmx-04");
        machine.setUsername("thomas.sullivan");
        machine.setPassword("magnum");

        PhysicalMachine saved = dao.save(machine);

        dao.delete(saved);
        saved = JOhm.get(PhysicalMachine.class, saved.getId());
        assertNull(saved);

        saved = dao.findPhysicalMachineByAddress("10.60.1.79");
        assertNull(saved);
    }

    @Test
    public void test_deleteVirtualMachine()
    {
        PhysicalMachine machine = new PhysicalMachine();
        machine.setAddress("10.60.1.79");
        machine.setType("vmx-04");
        machine.setUsername("thomas.sullivan");
        machine.setPassword("magnum");

        machine = dao.save(machine);
        assertNotNull(machine);

        String uuid = UUID.randomUUID().toString();

        VirtualMachine vmachine = new VirtualMachine();
        vmachine.setName(uuid);
        vmachine.setPhysicalMachine(machine);
        vmachine.setLastKnownState("POWER_OFF");

        VirtualMachine saved = dao.save(vmachine);

        dao.delete(saved);
        saved = JOhm.get(VirtualMachine.class, saved.getId());
        assertNull(saved);

        PhysicalMachine m = JOhm.get(PhysicalMachine.class, machine.getId());
        assertNotNull(m);

        VirtualMachine p = dao.findVirtualMachineByName(uuid);
        assertNull(p);
    }

    @Test
    public void test_deleteReference()
    {
        PhysicalMachine machine = new PhysicalMachine();
        machine.setAddress("10.60.1.79");
        machine.setType("vmx-04");
        machine.setUsername("thomas.sullivan");
        machine.setPassword("magnum");

        machine = dao.save(machine);
        assertNotNull(machine);

        VirtualMachine vmachine = new VirtualMachine();
        vmachine.setName(UUID.randomUUID().toString());
        vmachine.setPhysicalMachine(machine);
        vmachine.setLastKnownState("POWER_OFF");

        VirtualMachine saved = dao.save(vmachine);

        dao.delete(machine);

        assertNull(JOhm.get(PhysicalMachine.class, machine.getId()));
        assertNotNull(JOhm.get(VirtualMachine.class, saved.getId()));
    }

    @Test
    public void test_findAllVirtualMachines()
    {
        Set<VirtualMachine> machines = dao.findAllVirtualMachines();
        assertNotNull(machines);
        assertTrue(machines.isEmpty());

        PhysicalMachine machine = new PhysicalMachine();
        machine.setAddress("10.60.1.79");
        machine.setType("vmx-04");
        machine.setUsername("thomas.sullivan");
        machine.setPassword("magnum");

        machine = dao.save(machine);
        assertNotNull(machine);

        int n = 100;
        for (int i = 0; i < n; i++)
        {
            VirtualMachine vmachine = new VirtualMachine();
            vmachine.setName(UUID.randomUUID().toString());
            vmachine.setPhysicalMachine(machine);
            vmachine.setLastKnownState("POWER_OFF");

            dao.save(vmachine);
        }

        machines = dao.findAllVirtualMachines();
        assertNotNull(machines);
        assertFalse(machines.isEmpty());
        assertEquals(machines.size(), n);
    }

    @Test
    public void test_findAllPhysicalMachines()
    {
        Set<PhysicalMachine> machines = dao.findAllPhysicalMachines();
        assertNotNull(machines);
        assertTrue(machines.isEmpty());

        int n = 100;
        for (int i = 0; i < n; i++)
        {
            PhysicalMachine machine = new PhysicalMachine();
            machine.setAddress("10.60.1." + i);
            machine.setType("vmx-04");
            machine.setUsername("thomas.sullivan");
            machine.setPassword("magnum");

            dao.save(machine);
        }

        machines = dao.findAllPhysicalMachines();
        assertNotNull(machines);
        assertFalse(machines.isEmpty());
        assertEquals(machines.size(), n);
    }

    @Test
    public void test_findVirtualMachineByInexistentName()
    {
        assertNull(dao.findVirtualMachineByName("FAKE"));
    }

    @Test(expectedExceptions = RuntimeException.class)
    public void test_findVirtualMachineByNameForceRuntimeException()
    {
        VirtualMachine vmachine0 = new VirtualMachine();
        vmachine0.setName(UUID.randomUUID().toString());

        VirtualMachine vmachine1 = new VirtualMachine();
        vmachine1.setName(vmachine0.getName());

        assertNotNull(JOhm.save(vmachine0));
        assertNotNull(JOhm.save(vmachine1));

        dao.findVirtualMachineByName(vmachine0.getName());
    }

    @Test
    public void test_findVirtualMachineByName()
    {
        VirtualMachine vmachine0 = new VirtualMachine();
        vmachine0.setName(UUID.randomUUID().toString());

        VirtualMachine vmachine1 = new VirtualMachine();
        vmachine1.setName(UUID.randomUUID().toString());

        assertNotNull(JOhm.save(vmachine0));
        assertNotNull(JOhm.save(vmachine1));

        VirtualMachine recovered = dao.findVirtualMachineByName(vmachine1.getName());

        assertNotNull(recovered);
        assertEquals(vmachine1.getName(), recovered.getName());
        assertEquals(vmachine1.getId(), recovered.getId());
    }

    @Test
    public void test_findPhysicalMachineByInexistentAddress()
    {
        assertNull(dao.findPhysicalMachineByAddress("FAKE"));
    }

    @Test(expectedExceptions = RuntimeException.class)
    public void test_findPhysicalMachineByAddressForceRuntimeException()
    {
        PhysicalMachine machine0 = new PhysicalMachine();
        machine0.setAddress("10.60.1.79");

        PhysicalMachine machine1 = new PhysicalMachine();
        machine1.setAddress(machine0.getAddress());

        assertNotNull(JOhm.save(machine0));
        assertNotNull(JOhm.save(machine1));

        dao.findPhysicalMachineByAddress(machine0.getAddress());
    }

    @Test
    public void test_findPhysicalMachineByAddress()
    {
        PhysicalMachine machine0 = new PhysicalMachine();
        machine0.setAddress("10.60.1.79");
        machine0.setType("vmx-04");
        machine0.setUsername("thomas.sullivan");
        machine0.setPassword("magnum");

        machine0 = dao.save(machine0);

        PhysicalMachine machine1 = new PhysicalMachine();
        machine1.setAddress("10.60.1.80");
        machine1.setType("vmx-04");
        machine1.setUsername("thomas.sullivan");
        machine1.setPassword("magnum");

        machine1 = dao.save(machine1);

        PhysicalMachine recovered = dao.findPhysicalMachineByAddress("10.60.1.80");

        assertNotNull(recovered);
        assertEquals(machine1.getAddress(), recovered.getAddress());
        assertEquals(machine1.getId(), recovered.getId());
        assertEquals(machine1.getPassword(), recovered.getPassword());
        assertEquals(machine1.getType(), recovered.getType());
        assertEquals(machine1.getUsername(), recovered.getUsername());
        assertNull(recovered.getVirtualMachines());
    }

    @Test
    public void test_getPhysicalMachine()
    {
        PhysicalMachine pm = new PhysicalMachine();
        pm.setAddress("10.60.1.79");
        pm.setType("vmx-04");
        pm.setUsername("thomas.sullivan");
        pm.setPassword("magnum");

        pm = dao.save(pm);

        PhysicalMachine unexisting = dao.getPhysicalMachine(Integer.MAX_VALUE);
        PhysicalMachine existing = dao.getPhysicalMachine(pm.getId());

        assertNull(unexisting);
        assertNotNull(existing);
        assertEquals(pm.getAddress(), existing.getAddress());
        assertEquals(pm.getId(), existing.getId());
        assertEquals(pm.getPassword(), existing.getPassword());
        assertEquals(pm.getType(), existing.getType());
        assertEquals(pm.getUsername(), existing.getUsername());
        assertNull(existing.getVirtualMachines());
    }

    @Test
    public void test_getVirtualMachine()
    {
        VirtualMachine vm = new VirtualMachine();
        vm.setName(UUID.randomUUID().toString());

        vm = dao.save(vm);

        VirtualMachine unexisting = dao.getVirtualMachine(Integer.MAX_VALUE);
        VirtualMachine existing = dao.getVirtualMachine(vm.getId());

        assertNull(unexisting);
        assertNotNull(existing);
        assertEquals(vm.getId(), existing.getId());
        assertEquals(vm.getName(), existing.getName());
        assertEquals(vm.getLastKnownState(), existing.getLastKnownState());
    }

    @Test
    public void test_saveVirtualMachinesCache()
    {
        VirtualMachinesCache cache = new VirtualMachinesCache();
        dao.save(cache);

        cache.getCache().add("bloblo");

        VirtualMachinesCache savedCache = JOhm.get(VirtualMachinesCache.class, cache.getId());

        assertNotNull(savedCache);
        assertEquals(cache.getId(), savedCache.getId());
        assertEquals(savedCache.getCache().size(), 1);
        assertTrue(savedCache.getCache().contains("bloblo"));
    }

    @Test
    public void test_saveAndGetPhysicalMachineWithCache()
    {
        VirtualMachinesCache cache = new VirtualMachinesCache();
        dao.save(cache);

        PhysicalMachine pm = new PhysicalMachine();
        pm.setAddress("10.60.1.79");
        pm.setType("vmx-04");
        pm.setUsername("thomas.sullivan");
        pm.setPassword("magnum");
        pm.setVirtualMachines(cache);

        pm = dao.save(pm);

        PhysicalMachine saved = JOhm.get(PhysicalMachine.class, pm.getId());

        assertNotNull(saved);
        assertNotNull(saved.getVirtualMachines());
        assertEquals(saved.getVirtualMachines().getCache().size(), 0);

        String uuid1 = UUID.randomUUID().toString();
        saved.getVirtualMachines().getCache().add(uuid1);

        saved = JOhm.get(PhysicalMachine.class, pm.getId());

        assertNotNull(saved);
        assertNotNull(saved.getVirtualMachines());
        assertEquals(saved.getVirtualMachines().getCache().size(), 1);
        assertTrue(saved.getVirtualMachines().getCache().contains(uuid1));

        String uuid2 = UUID.randomUUID().toString();
        saved.getVirtualMachines().getCache().add(uuid2);

        saved = JOhm.get(PhysicalMachine.class, pm.getId());

        assertNotNull(saved);
        assertNotNull(saved.getVirtualMachines());
        assertEquals(saved.getVirtualMachines().getCache().size(), 2);
        assertTrue(saved.getVirtualMachines().getCache().contains(uuid1));
        assertTrue(saved.getVirtualMachines().getCache().contains(uuid2));

        List<String> items = new ArrayList<String>();
        items.add(UUID.randomUUID().toString());

        VirtualMachinesCache cache2 = saved.getVirtualMachines();
        cache2.getCache().clear();
        cache2.getCache().addAll(items);

        saved = JOhm.get(PhysicalMachine.class, pm.getId());

        assertNotNull(saved);
        assertNotNull(saved.getVirtualMachines());
        assertEquals(saved.getVirtualMachines().getCache().size(), 1);
    }
}
