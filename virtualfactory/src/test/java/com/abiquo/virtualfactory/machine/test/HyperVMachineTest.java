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
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import org.jinterop.dcom.core.JIString;
import org.jinterop.dcom.core.JIVariant;
import org.jinterop.dcom.impls.JIObjectFactory;
import org.jinterop.dcom.impls.automation.IJIDispatch;

import com.abiquo.ovfmanager.ovf.section.DiskFormat;
import com.abiquo.virtualfactory.hypervisor.impl.HyperVHypervisor;
import com.abiquo.virtualfactory.model.IHypervisor;
import com.abiquo.virtualfactory.model.VirtualDisk;
import com.abiquo.virtualfactory.model.config.VirtualMachineConfiguration;
import com.abiquo.virtualfactory.network.VirtualNIC;
import com.abiquo.virtualfactory.utils.hyperv.CIMDataFile;
import com.abiquo.virtualfactory.utils.hyperv.HyperVUtils;
import com.hyper9.jwbem.SWbemObjectSet;
import com.hyper9.jwbem.SWbemServices;

public class HyperVMachineTest extends AbsMachineTest
{
    @Override
    protected VirtualMachineConfiguration createConfiguration()
    {
        String diskLocation = "[" + diskRepository + "]" + diskImagePath;

        targetDatastore = "C:\\localRepository\\";

        VirtualDisk virtualDisk =
            new VirtualDisk(diskId,
                diskLocation,
                diskCapacity,
                targetDatastore,
                "",
                DiskFormat.VHD_FLAT.getDiskFormatUri()); // XXX: Disk format ?
        // normal disk type

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

        // VirtualDisk extVirtualDisk =
        // new VirtualDisk(diskId, rdmIQN2, diskCapacity, VirtualDiskType.ISCSI); // XXX EBS
        //
        // List<VirtualDisk> extDisks = new LinkedList<VirtualDisk>();
        // extDisks.add(extVirtualDisk);
        // conf.getExtendedVirtualDiskList().addAll(extDisks);

        return conf;
    }

    public HyperVMachineTest()
    {
        // HYPERVISOR configuration properties
        hvURL = "http://10.60.1.152";
        hvUser = "Administrador";
        hvPassword = "Windowssucks0!";

        // MACHINE configuration properties
        deployVirtualMachine = true;
        // id = UUID.fromString("10000000-1000-1000-1000-100000000000");
        id = UUID.randomUUID();
        name = UUID.randomUUID().toString();
        rdPort = 3390;
        ramAllocationUnits = 256 * 1024 * 1024;
        cpuNumber = 1;
        macAddress = "00155D929002";
        macAddress2 = "00155D929001";

        // DISK configuration properties
        diskRepository = "192.168.1.45:/opt/vm_repository/";
        diskImagePath = "1/httprs.bcn.abiquo.com/centos_vhd/centos.vhd";
        diskId = "50000000-5000-5000-5000-500000000000";
        diskCapacity = Long.parseLong("2147483648");

        // iSCSI
        iscsiTestLocation =
            "192.168.1.222/iqn.1986-03.com.sun:02:f0ad49ea-0767-409c-9b82-b86650fd1e5f";
        iscsiUUID = "80000000-8000-8000-8000-800000000000";
    }

    @Override
    public IHypervisor instantiateHypervisor()
    {
        return new HyperVHypervisor();
    }

    public void testDeleteFile() throws Exception
    {
        hypervisor = instantiateHypervisor();

        hypervisor.init(new URL(hvURL), hvUser, hvPassword);
        hypervisor.login(hvUser, hvPassword);
        hypervisor.connect(new URL(hvURL));
        HyperVHypervisor hyperV = (HyperVHypervisor) hypervisor;
        deleteFile(hyperV.getCIMService(),
            "C:\\localRepository\\cf53c7eb-55a0-4528-9c87-5c331b4ab8f1.vhd");

    }

    public void initTest() throws Exception
    {
        hypervisor = instantiateHypervisor();

        hypervisor.init(new URL(hvURL), hvUser, hvPassword);
        hypervisor.login(hvUser, hvPassword);
        hypervisor.connect(new URL(hvURL));
    }

    /**
     * Gets the the file to execute operations
     * 
     * @param file the file to get
     * @return an instance of {@link CIMDataFile}
     * @throws Exception
     */
    public void deleteFile(SWbemServices cimService, String file) throws Exception
    {
        // Preparing the query
        String query =
            "SELECT * FROM CIM_DataFile WHERE Name='" + file.toLowerCase().replace("\\", "\\\\")
                + "'";

        SWbemObjectSet<CIMDataFile> fileSetOld = cimService.execQuery(query, CIMDataFile.class);
        fileSetOld.iterator().next().delete();

        // Object[] inParams =
        // new Object[] {new JIString(query), JIVariant.OPTIONAL_PARAM(),
        // JIVariant.OPTIONAL_PARAM(), JIVariant.OPTIONAL_PARAM(),};

        JIVariant[] res =
            cimService.getObjectDispatcher().callMethodA("ExecQuery",
                new Object[] {new JIString(query)});

        JIVariant[][] fileSet = HyperVUtils.enumToJIVariantArray(res);

        if (fileSet.length != 1)
        {
            throw new Exception("Cannot identify the vhd to delete: " + file);
        }
        IJIDispatch fileDispatch =
            (IJIDispatch) JIObjectFactory.narrowObject(fileSet[0][0].getObjectAsComObject()
                .queryInterface(IJIDispatch.IID));
        res = fileDispatch.callMethodA("Delete", null);
        int result = res[0].getObjectAsInt();

        // IJIDispatch objectDispatcher = hyperVHypervisor.getCIMService().getObjectDispatcher();
        //
        // JIVariant[] results = objectDispatcher.callMethodA("ExecQuery", inParams);
        // IJIComObject co = results[0].getObjectAsComObject();
        // IJIDispatch dispatch = (IJIDispatch) JIObjectFactory.narrowObject(co);
        // return new CIMDataFile(dispatch, service);

    }

    public static void main(String[] args) throws Exception
    {
        HyperVMachineTest test = new HyperVMachineTest();
        // test.testExecuteRemoteProcess();
        test.setUp();
        // test.tearDown();
        // test.testInitiator();
        // test.testDeleteFile();
        // test.testAddRemoveISCSI();
    }

}
