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
import com.abiquo.virtualfactory.utils.hyperv.MsvmImageManagementService;
import com.abiquo.virtualfactory.utils.hyperv.Win32Process;
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
        virtualDisk.setHa();

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
        // hvURL = "http://10.60.1.152";
        hvURL = "http://10.60.1.122";
        hvUser = "Administrator";
        hvPassword = "Windowssucks0!";

        // MACHINE configuration properties
        deployVirtualMachine = true;
        // id = UUID.fromString("10000000-1000-1000-1000-100000000000");
        id = UUID.randomUUID();
        name = UUID.randomUUID().toString(); // Name should come from Server if we are deploying without copying disk?
        rdPort = 3390;
        ramAllocationUnits = 256 * 1024 * 1024;
        cpuNumber = 1;
        macAddress = "00155D929002";
        macAddress2 = "00155D929001";

        // DISK configuration properties
        diskRepository = "10.60.1.72:/opt/vm_repository/";
        
        diskImagePath = "1/rs.bcn.abiquo.com/nostalgia/formats/Nostalgia-flat.vmdk-VHD_SPARSE.vhd";        
//        diskImagePath = "1/httprs.bcn.abiquo.com/centos_vhd/centos.vhd";
        diskId = "50000000-5000-5000-5000-500000000000";
        diskCapacity = Long.parseLong("2147483648");

        // iSCSI
        iscsiTestLocation =
            "192.168.1.222/iqn.1986-03.com.sun:02:f0ad49ea-0767-409c-9b82-b86650fd1e5f";
        iscsiUUID = "80000000-8000-8000-8000-800000000000";
        
        // From dev-nix.properties
        // hypervisors.hyperv.repositoryLocation=//nfs-devel.bcn.abiquo.com/vm_repository/        
        System
        .setProperty("abiquo.virtualfactory.hyperv.repositoryLocation","//10.60.1.72/vm_repository/");
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

    /**
     * Tests WQL syntax samples
     * 
     * @return an instance of {@link CIMDataFile}
     * @throws Exception
     */
    public boolean testWQL() throws Exception
    {
        boolean fileExists = false;

        hypervisor = instantiateHypervisor();

        hypervisor.init(new URL(hvURL), hvUser, hvPassword);
        hypervisor.login(hvUser, hvPassword);
        hypervisor.connect(new URL(hvURL));
        HyperVHypervisor hyperV = (HyperVHypervisor) hypervisor;
        // deleteFile(hyperV.getCIMService(),
        // "C:\\localRepository\\cf53c7eb-55a0-4528-9c87-5c331b4ab8f1.vhd");

        SWbemServices cimService = hyperV.getCIMService();

        // Preparing the query
//        String query =
//            "SELECT * FROM CIM_DataFile where FileName LIKE 'a%' and Drive = 'z:' ";
        
        String query =
        "SELECT * FROM CIM_DataFile WHERE FileName like 'abq_datastoreuuid_%' and Drive = 'Z:'"; //FileName = 'aquimismo.vhd' AND 
        
        // Path and NAme don't work with queies
        //and Path = '\\abiquo monolithic 1.7\\'"; // Only files in rootpath
//        where Name LIKE 'Z:\\a%'";
        
        
//        String query =
//            "SELECT * FROM Win32_Directory WHERE Name = '\\\\10.60.1.4\\vm_repository\\snapshots' ";//or FileName = 'carpeta2'";

//        String query =
//                   "Select * from Win32_MappedLogicalDisk WHERE SessionID = '164382' AND Caption = 'Z:'";
        // Win32_MappedLogicalDisk CAN be Found by SessionID (session in Logon)
        
//      String query =
//      "Select * from Win32_MappedLogicalDisk";

        
//        String query =
//            "Select * from CIM_StorageExtent WHERE SystemName = 'WIN-7HJFVAUQT45' AND DeviceId = 'Z:'";
        
//        String query =
//            "Select * from Win32_LogonSession";
        
//        String query =
//            "Select * from Win32_Account WHERE Name='Administrator'";
        
//        String query = 
//            "ASSOCIATORS OF {Win32_Account} "; // WHERE ClassDefsOnly 
        
//        String query =
//            "SELECT * FROM Win32_Directory WHERE Name=\"\\\\10.60.1.4\\vm_repository\\abq.datastoreuuid.toma\" ";
                
            
//        String query =
//            "SELECT * FROM CIM_DataFile WHERE Name Like \"" + file.toLowerCase().replace("\\", "\\\\")
//                + "%\"";


        List<IJIDispatch> results =
            HyperVUtils.execQuery(query, cimService);
//        RELATION with CIM_LogicalDevice ??? http://msdn.microsoft.com/en-us/library/aa394083%28v=vs.85%29.aspx#properties
        
//        List<IJIDispatch> results =
//            HyperVUtils.execQuery("ASSOCIATORS OF {Win32_Share} ", cimService);
        
//        WHERE ResultClass = Cim_Directory
        
        
        
//        
//        List<IJIDispatch> results =
//            HyperVUtils.execQuery("Select * from Win32_MappedLogicalDisk", cimService);

        if (results == null || results.isEmpty())
        {
            throw new Exception("Could not get Any Results for the query");
        }

        System.out.println("results.size() - " + results.size());
        
        for (IJIDispatch logicalDiskDispatch : results)
        {
//            System.out.println("logicalDiskDispatch.get(\"LogonId\").getObjectAsString2(); " + logicalDiskDispatch.get("LogonId").getObjectAsString2());
//            System.out.println("logicalDiskDispatch.get(\"LogonType\").getObjectAsString2(); " + logicalDiskDispatch.get("LogonType").getObjectAsInt());
//            System.out.println("logicalDiskDispatch.get(\"AuthenticationPackage\").getObjectAsString2(); " + logicalDiskDispatch.get("AuthenticationPackage").getObjectAsString2());
            
            System.out.println("logicalDiskDispatch.get(\"Path\").getObjectAsString2(); " + logicalDiskDispatch.get("Path").getObjectAsString2());
            System.out.println("logicalDiskDispatch.get(\"Name\").getObjectAsString2(); " + logicalDiskDispatch.get("Name").getObjectAsString2());
            System.out.println("logicalDiskDispatch.get(\"Drive\").getObjectAsString2(); " + logicalDiskDispatch.get("Drive").getObjectAsString2());
            
            System.out.println("logicalDiskDispatch.get(\"DeviceID\").getObjectAsString2(); " + logicalDiskDispatch.get("DeviceID").getObjectAsString2());
            System.out.println("logicalDiskDispatch.get(\"ProviderName\").getObjectAsString2(); " + logicalDiskDispatch.get("ProviderName").getObjectAsString2());
            System.out.println("logicalDiskDispatch.get(\"SessionID\").getObjectAsString2(); " + logicalDiskDispatch.get("SessionID").getObjectAsString2());
            
//            System.out.println("logicalDiskDispatch.get(\"Caption\").getObjectAsString2(); " + logicalDiskDispatch.get("Caption").getObjectAsString2());
//            System.out.println("logicalDiskDispatch.get(\"Name\").getObjectAsString2(); " + logicalDiskDispatch.get("Name").getObjectAsString2());
//            System.out.println("logicalDiskDispatch.get(\"VolumeName\").getObjectAsString2(); " + logicalDiskDispatch.get("VolumeName").getObjectAsString2());
//            System.out.println("logicalDiskDispatch.get(\"VolumeSerialNumber\").getObjectAsString2(); " + logicalDiskDispatch.get("VolumeSerialNumber").getObjectAsString2());
            
            
            
            
//            System.out.println("logicalDiskDispatch.get(\"DeviceID\").getObjectAsString2(); " + logicalDiskDispatch.get("DeviceID").getObjectAsString2());
//            System.out.println("logicalDiskDispatch.get(\"Name\").getObjectAsString2(); " + logicalDiskDispatch.get("Name").getObjectAsString2());
//            System.out.println("logicalDiskDispatch.get(\"ProviderName\").getObjectAsString2(); " + logicalDiskDispatch.get("ProviderName").getObjectAsString2());
            
//            System.out.println("logicalDiskDispatch.get(\"Drive\").getObjectAsString2(); " + logicalDiskDispatch.get("Drive").getObjectAsString2());
//            System.out.println("logicalDiskDispatch.get(\"FileName\").getObjectAsString2(); " + logicalDiskDispatch.get("FileName").getObjectAsString2());
//            System.out.println("logicalDiskDispatch.get(\"Path\").getObjectAsString2(); " + logicalDiskDispatch.get("Path").getObjectAsString2());

        } 
        
        
        JIVariant[] res =
            cimService.getObjectDispatcher().callMethodA("ExecQuery",
                new Object[] {new JIString(query)});

        JIVariant[][] fileSet = HyperVUtils.enumToJIVariantArray(res);
        
        System.out.println("fileSet.length : " + fileSet.length );
                

        if (fileSet.length != 1)
        {
            fileExists = false;
            // throw new Exception("Cannot identify the vhd to delete: " + file);
        }
        else
        {
            fileExists = true;
        }

        hypervisor.disconnect();
        hypervisor.logout();

        return fileExists;

    }
    
    /**
     * Tests if a file/folder exists
     * 
     * @return an instance of {@link CIMDataFile}
     * @throws Exception
     */
    public void detectNetworkDrives() throws Exception
    {

        hypervisor = instantiateHypervisor();

        hypervisor.init(new URL(hvURL), hvUser, hvPassword);
        hypervisor.login(hvUser, hvPassword);
        hypervisor.connect(new URL(hvURL));
        HyperVHypervisor hyperV = (HyperVHypervisor) hypervisor;

        SWbemServices cimService = hyperV.getCIMService();
        
        String query =
            "Select * from CIM_StorageExtent  WHERE SystemName = 'WIN-7HJFVAUQT45' AND Caption = 'Z:'";

        List<IJIDispatch> results =
            HyperVUtils.execQuery(query, cimService);

        if (results == null || results.isEmpty())
        {
            throw new Exception("Could not get Any Results for the query");
        }

        System.out.println(results.size());
        
        for (IJIDispatch logicalDiskDispatch : results)
        {
//            System.out.println("logicalDiskDispatch.get(\"AssocClass\").getObjectAsString2(); " + logicalDiskDispatch.get("Name").getObjectAsString2());
            
            System.out.println("logicalDiskDispatch.get(\"DeviceID\").getObjectAsString2(); " + logicalDiskDispatch.get("DeviceID").getObjectAsString2());
            System.out.println("logicalDiskDispatch.get(\"Name\").getObjectAsString2(); " + logicalDiskDispatch.get("Name").getObjectAsString2());
//            System.out.println("logicalDiskDispatch.get(\"Description\").getObjectAsString2(); " + logicalDiskDispatch.get("Description").getObjectAsString2());
//            System.out.println("logicalDiskDispatch.get(\"BlockSize\").getObjectAsString2(); " + logicalDiskDispatch.get("BlockSize").getObjectAsInt());
//            System.out.println("logicalDiskDispatch.get(\"SystemName\").getObjectAsString2(); " + logicalDiskDispatch.get("SystemName").getObjectAsString2());
            
//            String logicalDiskName = logicalDiskDispatch.get("DeviceID").getObjectAsString2();
//            String size = logicalDiskDispatch.get("Size").getObjectAsString2();
//            String availableSize = logicalDiskDispatch.get("FreeSpace").getObjectAsString2();
            
//            System.out.println("logicalDiskDispatch.get(\"Drive\").getObjectAsString2(); " + logicalDiskDispatch.get("Drive").getObjectAsString2());
//            System.out.println("logicalDiskDispatch.get(\"FileName\").getObjectAsString2(); " + logicalDiskDispatch.get("FileName").getObjectAsString2());
//            System.out.println("logicalDiskDispatch.get(\"Path\").getObjectAsString2(); " + logicalDiskDispatch.get("Path").getObjectAsString2());

        }
        
        
        JIVariant[] res =
            cimService.getObjectDispatcher().callMethodA("ExecQuery",
                new Object[] {new JIString(query)});

        JIVariant[][] fileSet = HyperVUtils.enumToJIVariantArray(res);
        
        System.out.println("fileSet.length : " + fileSet.length );
                

        hypervisor.disconnect();
        hypervisor.logout();


    }
    
    /**
     * Tests if a file/folder exists
     * 
     * @return an instance of {@link CIMDataFile}
     * @throws Exception
     */
    public boolean detectFolderInNetworkDrive(String file, String drive) throws Exception
    {
        boolean fileExists = false;

        hypervisor = instantiateHypervisor();

        hypervisor.init(new URL(hvURL), hvUser, hvPassword);
        hypervisor.login(hvUser, hvPassword);
        hypervisor.connect(new URL(hvURL));
        HyperVHypervisor hyperV = (HyperVHypervisor) hypervisor;
        // deleteFile(hyperV.getCIMService(),
        // "C:\\localRepository\\cf53c7eb-55a0-4528-9c87-5c331b4ab8f1.vhd");

        SWbemServices cimService = hyperV.getCIMService();

        String query =
            "Select * from Win32_MappedLogicalDisk";//or FileName = 'carpeta2'";
        
//        List<IJIDispatch> results =
//            HyperVUtils.execQuery("Select * from Win32_MappedLogicalDisk WHERE DeviceID = '" + drive +"'", cimService);
        
        List<IJIDispatch> results =
            HyperVUtils.execQuery("Select * from Win32_MappedLogicalDisk", cimService);

        if (results == null || results.isEmpty())
        {
            throw new Exception("Could not get Win32_MappedLogicalDisk");
        }

        for (IJIDispatch logicalDiskDispatch : results)
        {
            String subQuery =
                "Select * from Win32_Directory";//or FileName = 'carpeta2'";
            
            JIVariant[] res =
                logicalDiskDispatch.callMethodA("ExecQuery",
                    new Object[] {new JIString(query)});

            JIVariant[][] fileSet = HyperVUtils.enumToJIVariantArray(res);
            
            System.out.println(fileSet.length);
            
        }
        
        
//       
//        if (fileSet.length != 1)
//        {
//            
//            fileExists = false;
//            // throw new Exception("Cannot identify the vhd to delete: " + file);
//        }
//        else
//        {
//            fileExists = true;
//        }

        hypervisor.disconnect();
        hypervisor.logout();

        return fileExists;

    }


    /**
     * Creates a folder in a remote Win32 System by invoking a Win32_Process
     * 
     * @param folder it can include full (including drive letter) or relative path for the directory being created
     * @throws Exception
     * 
     * TODO: It doesn't work with mapped network drives, even if user is logged on.
     */
    public void createFolder(String folder) throws Exception
    {
//         if (detectFile(folder)) {
//             log.info("Folder " + folder + " already exists. ");
//             return;
//         }

        hypervisor = instantiateHypervisor();

        hypervisor.init(new URL(hvURL), hvUser, hvPassword);
        hypervisor.login(hvUser, hvPassword);
        hypervisor.connect(new URL(hvURL));
        HyperVHypervisor hyperV = (HyperVHypervisor) hypervisor;

        try
        {

            SWbemServices cimService = hyperV.getCIMService();
            
            // Sending a command to a Win32Process
            
            IJIDispatch instanceClass =
                (IJIDispatch) JIObjectFactory.narrowObject(cimService.getObjectDispatcher().callMethodA("Get",
                    new Object[] {new JIString("Win32_Process")})[0].getObjectAsComObject().queryInterface(
                    IJIDispatch.IID));
            // Win32_Process do not need to be instanced (SpawnInstance_)
            
            Win32Process proc = new Win32Process(instanceClass, cimService);
//            proc.create("cmd.exe /C mkdir " + folder);
            
            proc.create("cmd.exe /C echo toma > Z:\\fistropecadorfile");

        }
        catch (Exception e)
        {
            log.error("CreateFolder was not possible !!");
            e.printStackTrace();
        }
   
        //
        hypervisor.disconnect();
        hypervisor.logout();

    }
    
    
    /**
     * 
     * @param path
     * @throws Exception
     */
    public void createFileAsVHD(String path) throws Exception
    {
//         if (detectFile(folder)) {
//             log.info("Folder " + folder + " already exists. ");
//             return;
//         }

        hypervisor = instantiateHypervisor();

        hypervisor.init(new URL(hvURL), hvUser, hvPassword);
        hypervisor.login(hvUser, hvPassword);
        hypervisor.connect(new URL(hvURL));
        HyperVHypervisor hyperV = (HyperVHypervisor) hypervisor;

        try
        {

            SWbemServices cimService = hyperV.getCIMService();
            
            MsvmImageManagementService imageManagementService =
                MsvmImageManagementService.getManagementService(hyperV
                    .getVirtualizationService());
            
//          TRY This too ->  imageManagementService.createFixedVirtualHardDisk("C:\\aquimismo.vhd");
            
//            imageManagementService.createFixedVirtualHardDisk2("Z:\\aquimismo.vhd");
            
            imageManagementService.createFixedVirtualHardDisk2("\\\\nfs-devel.bcn.abiquo.com\\vm_repository\\aquimismo.vhd");
                       
            
//            THIS WORKS
//            imageManagementService.convertVirtualHardDisk("\\\\nfs-devel.bcn.abiquo.com\\vm_repository\\1\\rs.bcn.abiquo.com\\mw\\formats\\AbiquoMW.vdi-VHD_SPARSE.vhd", "C:\\carpeta3.vhd", 2);
            

        }
        catch (Exception e)
        {
            log.error("createFileAsVHD was not possible !!");
            e.printStackTrace();
        }
   
        //
        hypervisor.disconnect();
        hypervisor.logout();

    }
    
    /**
     * Works OK
     * 
     * @throws Exception
     */
    public void copyFolder() throws Exception{
        
//        detectFile("C:\\command.cmd");
        
        hypervisor = instantiateHypervisor();

        hypervisor.init(new URL(hvURL), hvUser, hvPassword);
        hypervisor.login(hvUser, hvPassword);
        hypervisor.connect(new URL(hvURL));
        HyperVHypervisor hyperV = (HyperVHypervisor) hypervisor;

        try
        {
            SWbemServices cimService = hyperV.getCIMService();

            // 3. Copying an existing file
            IJIDispatch disp = getCIMDataFile(hyperV, "C:\\command.cmd".toLowerCase().replace("\\", "\\\\"));
            CIMDataFile folder = new CIMDataFile(disp,cimService);
            folder.copy("C:\\carpeta2");
            // Generic failure Exception occurred. [0x80020009]

        }
        catch (Exception e)
        {
            log.error("FAIL!!");
            e.printStackTrace();
        }
   
        //
        hypervisor.disconnect();
        hypervisor.logout();
        
    }
    
    

    public static void main(String[] args) throws Exception
    {
        HyperVMachineTest test = new HyperVMachineTest();
        // test.testExecuteRemoteProcess();
        test.configureTestForDeployInHA();
         test.setUp();
         test.tearDown();
        // test.testInitiator();
        // test.testDeleteFile();
        // test.testAddRemoveISCSI();
        // System.out.println("test.detectFile(): " + test.detectFile("C:\\folder"));
        
//        test.detectFile("C:\\test45%");
//        test.detectFile("C:\\test452");
//        test.detectFile("C:\\carpeta2");
        
//        test.detectFile("\\vm_repository\\abq.datastoreuuid.toma");
        
//        test.detectFolderInNetworkDrive("abq.datastoreuuid.toma", "Z:");
//        test.detectNetworkDrives();
        
//        test.testWQL();
        
//        test.createFolder("Z:\\fistropecadordelapradera");
        
//        test.createFileAsVHD("Z:\\fistrovhd");
        
//        test.copyFolder();
    }
    
    /**
     * for HA we already have a name and a vdisk for the vmachine
     */
    private void configureTestForDeployInHA(){
        name = "ABQ_deploynocopy";
        
    }
    
    /**
     * Gets the the file to execute operations
     * 
     * @param file the file to get
     * @return an instance of {@link CIMDataFile}
     * @throws Exception
     */
    public IJIDispatch getCIMDataFile(HyperVHypervisor hyperVHypervisor, final String file) throws Exception
    {
        // Preparing the query
//        String query = "SELECT * FROM CIM_DataFile WHERE Name='" + file + "'";
        String query = "SELECT * FROM CIM_DataFile WHERE Name Like '" + file + "%'";

        JIVariant[] res =
            hyperVHypervisor.getCIMService().getObjectDispatcher().callMethodA("ExecQuery",
                new Object[] {new JIString(query)});

        JIVariant[][] fileSet = HyperVUtils.enumToJIVariantArray(res);

        if (fileSet.length != 1)
        {
            throw new Exception("Cannot identify the file to : " + file);
        }
        IJIDispatch fileDispatch =
            (IJIDispatch) JIObjectFactory.narrowObject(fileSet[0][0].getObjectAsComObject()
                .queryInterface(IJIDispatch.IID));

        return fileDispatch;

        // IJIDispatch objectDispatcher = hyperVHypervisor.getCIMService().getObjectDispatcher();
        //
        // JIVariant[] results = objectDispatcher.callMethodA("ExecQuery", inParams);
        // IJIComObject co = results[0].getObjectAsComObject();
        // IJIDispatch dispatch = (IJIDispatch) JIObjectFactory.narrowObject(co);
        // return new CIMDataFile(dispatch, service);

    }
   

}
