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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import com.abiquo.ovfmanager.ovf.section.DiskFormat;
import com.abiquo.virtualfactory.hypervisor.impl.KVMHypervisor;
import com.abiquo.virtualfactory.model.IHypervisor;
import com.abiquo.virtualfactory.model.VirtualDisk;
import com.abiquo.virtualfactory.model.VirtualDiskType;
import com.abiquo.virtualfactory.model.config.VirtualMachineConfiguration;
import com.abiquo.virtualfactory.network.VirtualNIC;

/**
 * KVM Machine test case
 * 
 * @author pnavarro
 */
public class KVMMachineTest extends AbsMachineTest
{
    private final static String rdmIQN = "192.168.1.76|iqn.2009-03.thunder06:sanpete";

    /**
     * It creates the configuration for statefull images
     */
    @Override
    protected VirtualMachineConfiguration createConfiguration()
    {
        String diskLocation = rdmIQN; // XXX EBS "[" + diskRepository + "]" + diskImagePath;

        String targetDatastore = "/var/lib/virt";

        VirtualDisk virtualDisk =
            new VirtualDisk(diskId,
                diskLocation,
                diskCapacity,
                VirtualDiskType.ISCSI,
                targetDatastore,
                "",
                DiskFormat.VMDK_FLAT.getDiskFormatUri()); // XXX

        List<VirtualDisk> disks = new LinkedList<VirtualDisk>();
        disks.add(virtualDisk);

        List<VirtualNIC> vnicList = new ArrayList<VirtualNIC>();
        vnicList.add(new VirtualNIC(vswitchName, macAddress, vlanTag, networkName, 0));
        vnicList.add(new VirtualNIC(vswitchName2, macAddress2, vlanTag2, networkName2, 1));

        VirtualMachineConfiguration conf =
            new VirtualMachineConfiguration(id,
                name,
                disks,
                rdPort,
                ramAllocationUnits,
                cpuNumber,
                vnicList);

        conf.setHypervisor(hypervisor);

        return conf;
    }

    /**
     * Standard test constructor.
     */
    public KVMMachineTest()
    {
        // HYPERVISOR configuration properties
        hvURL = "http://192.168.1.73:18083";
        hvUser = "";
        hvPassword = "";

        // MACHINE configuration properties
        deployVirtualMachine = true;
        // id = UUID.fromString("10000000-1000-1000-1000-100000000000");
        id = UUID.randomUUID();
        name = id.toString();
        rdPort = 3390;
        ramAllocationUnits = 128 * 1024 * 1024;
        cpuNumber = 1;
        macAddress = "52:54:00:d7:63:3d";
        macAddress2 = "52:54:00:d7:63:3a";

        // DISK configuration properties
        diskRepository = ""; // XXX EBS unused "nfsrepository";
        diskImagePath = "http___192_168_6_45_5050_rs/namenode/namenode-flat.vmdk";
        diskId = "50000000-5000-5000-5000-500000000000";
        diskCapacity = 0;// XXX EBS unused Long.parseLong("2147483648");

        // iSCSI
        iscsiTestLocation =
            "192.168.1.222/iqn.1986-03.com.sun:02:f0ad49ea-0767-409c-9b82-b86650fd1e5f";
        iscsiUUID = "80000000-8000-8000-8000-800000000000";
    }

    @Override
    public IHypervisor instantiateHypervisor()
    {
        return new KVMHypervisor();
    }

    /**
     * Test launcher.
     * 
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception
    {
        KVMMachineTest test = new KVMMachineTest();
        test.setUp();
        // test.testPowerOnMachine();
        // test.testAddRemoveISCSI();
    }

}
