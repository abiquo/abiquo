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
package com.abiquo.virtualfactory.machine.test;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import junit.framework.TestCase;

import org.junit.Test;

import com.abiquo.virtualfactory.hypervisor.impl.XenServerHypervisor;
import com.abiquo.virtualfactory.machine.impl.XenServerMachine;
import com.abiquo.virtualfactory.model.IHypervisor;
import com.abiquo.virtualfactory.model.VirtualDisk;
import com.abiquo.virtualfactory.model.VirtualDiskType;
import com.abiquo.virtualfactory.model.config.VirtualMachineConfiguration;
import com.abiquo.virtualfactory.network.VirtualNIC;

/**
 * Dummy test cases for XEN hypervisor.
 * 
 * @author ibarrera
 */
public class XenServerMachineTest extends TestCase
{
    private static final String HYPERVISOR_HOST = "10.60.1.77";

    private static final String MACHINE_NAME = "B7373641-6a6f-43c0-a8c1-0f2e4fd31fe6";

    private static final String REMOTE_REPOSITORY = "nfs-devel:/opt/vm_repository";

    private static final String IMAGE_PATH =
        "1/httprs.bcn.abiquo.com/ubuntu-server-karmic-x86_64/formats/"
            + "87d81738-e9a0-4419-8d73-a42d9c8c13e9-VHD_SPARSE-ubuntu-server-karmic-amd64.vhd";

    private static final String BUNDLE_PATH = "1/httprs.bcn.abiquo.com/ubuntu-server-karmic-x86_64";

    private static final String BUNDLE_NAME =
        "927d057b-2cc4-41e3-bb1b-ef0e914a012c-snapshot-ubuntu-910-server-x86_64-vmdk5.vmdk";

    private static final String user = "user";

    private static final String password = "password";

    protected IHypervisor hypervisor;

    protected XenServerMachine machine;

    @Override
    protected void setUp() throws Exception
    {
        hypervisor = getHypervisor();
        hypervisor.init(new URL("http://" + HYPERVISOR_HOST), user, password);
        hypervisor.connect(hypervisor.getAddress());

        // Networking configuration
        List<VirtualNIC> vlans = new ArrayList<VirtualNIC>();
        vlans.add(new VirtualNIC("eth0", "12:12:12:13:13:13", 1, "VLAN", 0));
        vlans.add(new VirtualNIC("eth0", "2A:FF:FF:FF:FF:FF", 2, "VLAN", 1));

        // Storage configuration
        List<VirtualDisk> disks = new ArrayList<VirtualDisk>();

        VirtualDisk vdBase = new VirtualDisk();
        vdBase.setDiskType(VirtualDiskType.STANDARD);
        vdBase.setRepository(REMOTE_REPOSITORY);
        vdBase.setImagePath(IMAGE_PATH);
        disks.add(vdBase);

        VirtualMachineConfiguration config = new VirtualMachineConfiguration(UUID.randomUUID(), // UUID
            MACHINE_NAME, // Name
            disks, // Virtual disks
            9999, // RD Port
            512 * 1024 * 1024, // RAM (in bytes)
            1, // CPUs
            vlans); // VLANS

        config.setHypervisor(hypervisor);
        configureExternalStorage(config);

        machine = (XenServerMachine) hypervisor.createMachine(config);
    }

    protected IHypervisor getHypervisor()
    {
        return new XenServerHypervisor();
    }

    protected void configureExternalStorage(VirtualMachineConfiguration config)
    {
        // Do nothing
    }

    @Test
    public void test0Deploy() throws Exception
    {
        machine.deployMachine();
    }
    /*
     * @Test public void test1powerOn() throws Exception { machine.powerOnMachine(); }
     * @Test public void test2reset() throws Exception { machine.resetMachine(); }
     * @Test public void test3pause() throws Exception { machine.pauseMachine(); }
     * @Test public void test4resume() throws Exception { machine.resumeMachine(); }
     * @Test public void test5powerOff() throws Exception { machine.powerOffMachine(); }
     * @Test public void test6delete() throws Exception { machine.deleteMachine(); }
     */

}
