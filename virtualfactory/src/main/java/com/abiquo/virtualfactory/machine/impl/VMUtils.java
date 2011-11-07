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

package com.abiquo.virtualfactory.machine.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.abiquo.util.ExtendedAppUtil;
import com.abiquo.virtualfactory.exception.VirtualMachineException;
import com.abiquo.virtualfactory.network.VirtualNIC;
import com.vmware.vim25.ConfigTarget;
import com.vmware.vim25.DatastoreSummary;
import com.vmware.vim25.DynamicProperty;
import com.vmware.vim25.ManagedObjectReference;
import com.vmware.vim25.NetworkSummary;
import com.vmware.vim25.ObjectContent;
import com.vmware.vim25.VirtualCdrom;
import com.vmware.vim25.VirtualCdromIsoBackingInfo;
import com.vmware.vim25.VirtualDevice;
import com.vmware.vim25.VirtualDeviceConfigSpec;
import com.vmware.vim25.VirtualDeviceConfigSpecFileOperation;
import com.vmware.vim25.VirtualDeviceConfigSpecOperation;
import com.vmware.vim25.VirtualDisk;
import com.vmware.vim25.VirtualDiskFlatVer2BackingInfo;
import com.vmware.vim25.VirtualE1000;
import com.vmware.vim25.VirtualEthernetCard;
import com.vmware.vim25.VirtualEthernetCardNetworkBackingInfo;
import com.vmware.vim25.VirtualFloppy;
import com.vmware.vim25.VirtualFloppyDeviceBackingInfo;
import com.vmware.vim25.VirtualIDEController;
import com.vmware.vim25.VirtualLsiLogicController;
import com.vmware.vim25.VirtualMachineConfigOption;
import com.vmware.vim25.VirtualMachineConfigSpec;
import com.vmware.vim25.VirtualMachineDatastoreInfo;
import com.vmware.vim25.VirtualMachineFileInfo;
import com.vmware.vim25.VirtualMachineNetworkInfo;
import com.vmware.vim25.VirtualSCSISharing;

public class VMUtils
{
    public ExtendedAppUtil cb = null;

    public VMUtils(final ExtendedAppUtil argCB)
    {
        cb = argCB;
    }

    public ArrayList getVMs(final String entity, final String datacenter, final String folder,
        final String pool, final String vmname, final String host, final String[][] filter)
        throws Exception
    {
        ManagedObjectReference dsMOR = null;
        ManagedObjectReference hostMOR = null;
        ManagedObjectReference poolMOR = null;
        ManagedObjectReference vmMOR = null;
        ManagedObjectReference folderMOR = null;
        ManagedObjectReference tempMOR = null;
        ArrayList vmList = new ArrayList();
        String[][] filterData = null;

        if (datacenter != null)
        {
            dsMOR = cb.getServiceUtil3().getDecendentMoRef(null, "Datacenter", datacenter);
            if (dsMOR == null)
            {
                System.out.println("Datacenter Not found");
                return null;
            }
            else
            {
                tempMOR = dsMOR;
            }
        }
        if (folder != null)
        {
            folderMOR = cb.getServiceUtil3().getDecendentMoRef(tempMOR, "Folder", folder);
            if (folderMOR == null)
            {
                System.out.println("Folder Not found");
                return null;
            }
            else
            {
                tempMOR = folderMOR;
            }
        }
        if (pool != null)
        {
            poolMOR = cb.getServiceUtil3().getDecendentMoRef(tempMOR, "ResourcePool", pool);
            if (poolMOR == null)
            {
                System.out.println("Resource pool Not found");
                return null;
            }
            else
            {
                tempMOR = poolMOR;
            }
        }
        if (host != null)
        {
            hostMOR = cb.getServiceUtil3().getDecendentMoRef(tempMOR, "HostSystem", host);
            if (hostMOR == null)
            {
                System.out.println("Host Not found");
                return null;
            }
            else
            {
                tempMOR = hostMOR;
            }
        }

        if (vmname != null)
        {
            int i = 0;
            filterData = new String[filter.length + 1][2];
            for (i = 0; i < filter.length; i++)
            {
                filterData[i][0] = filter[i][0];
                filterData[i][1] = filter[i][1];
            }
            // Adding the vmname in the filter
            filterData[i][0] = "name";
            filterData[i][1] = vmname;
        }
        else if (vmname == null)
        {
            int i = 0;
            filterData = new String[filter.length + 1][2];
            for (i = 0; i < filter.length; i++)
            {
                filterData[i][0] = filter[i][0];
                filterData[i][1] = filter[i][1];
            }
        }
        vmList = cb.getServiceUtil3().getDecendentMoRefs(tempMOR, "VirtualMachine", filterData);
        if (vmList == null || vmList.size() == 0)
        {
            System.out.println("NO Virtual Machine found");
            return null;
        }
        return vmList;

    }

    /**
     * This method returns the contents of the hostFolder property from the supplied Datacenter
     * MoRef
     * 
     * @param dcmor MoRef to the Datacenter
     * @return MoRef to a Folder returned by the hostFolder property or null if dcmor is NOT a MoRef
     *         to a Datacenter or if the hostFolder doesn't exist
     * @throws Exception
     */
    public ManagedObjectReference getHostFolder(final ManagedObjectReference dcmor)
        throws Exception
    {
        ManagedObjectReference hfmor = cb.getServiceUtil3().getMoRefProp(dcmor, "hostFolder");
        return hfmor;
    }

    /**
     * This method returns a MoRef to the HostSystem with the supplied name under the supplied
     * Folder. If hostname is null, it returns the first HostSystem found under the supplied Folder
     * 
     * @param hostFolderMor MoRef to the Folder to look in
     * @param hostname Name of the HostSystem you are looking for
     * @return MoRef to the HostSystem or null if not found
     * @throws Exception
     */
    public ManagedObjectReference getHost(final ManagedObjectReference hostFolderMor,
        final String hostname) throws Exception
    {
        ManagedObjectReference hostmor = null;

        if (hostname != null)
        {
            hostmor = cb.getServiceUtil3().getDecendentMoRef(hostFolderMor, "HostSystem", hostname);
        }
        else
        {
            hostmor = cb.getServiceUtil3().getFirstDecendentMoRef(hostFolderMor, "HostSystem");
        }

        return hostmor;
    }

    public void browseMOR(final ManagedObjectReference MOR)
    {
        try
        {
            ObjectContent[] ocary = cb.getServiceUtil3().getContentsRecursively(MOR, true);
            ObjectContent oc = null;
            ManagedObjectReference mor = null;
            DynamicProperty[] pcary = null;
            DynamicProperty pc = null;
            if (ocary != null)
            {
                for (ObjectContent element : ocary)
                {
                    oc = element;
                    mor = oc.getObj();
                    pcary = oc.getPropSet();
                    if (pcary != null)
                    {
                        for (DynamicProperty element2 : pcary)
                        {
                            pc = element2;
                            if (pc.getName().equalsIgnoreCase("name"))
                            {
                                System.out.println(pc.getVal().toString());
                            }

                        }
                    }
                }
            }

        }
        catch (Exception e)
        {
            System.out.println("ClassCastException");
            e.printStackTrace();
        }
    }

    public void browseArrayList(final ArrayList arrList)
    {
        try
        {

            Iterator iterator = arrList.iterator();
            while (iterator.hasNext())
            {
                ObjectContent[] ocary =
                    cb.getServiceUtil3().getContentsRecursively(
                        (ManagedObjectReference) iterator.next(), true);
                ObjectContent oc = null;
                ManagedObjectReference mor = null;
                DynamicProperty[] pcary = null;
                DynamicProperty pc = null;
                if (ocary != null)
                {
                    for (ObjectContent element : ocary)
                    {
                        oc = element;
                        mor = oc.getObj();
                        pcary = oc.getPropSet();
                        if (pcary != null)
                        {
                            for (DynamicProperty element2 : pcary)
                            {
                                pc = element2;
                                if (pc.getName().equalsIgnoreCase("name"))
                                {
                                    System.out.println(pc.getVal());
                                }
                            }
                        }
                    }
                }
            }
        }
        catch (Exception e)
        {
            System.out.println(" Exceptions ");
            e.printStackTrace();
        }
    }

    /**
     * @param rdmIQN, the IQN of the primary volume, if not null its an statefull image.
     */
    public VirtualMachineConfigSpec createVmConfigSpec(final String vmName, String datastoreName,
        final long diskSize, final ManagedObjectReference computeResMor,
        final ManagedObjectReference hostMor, final List<VirtualNIC> vnicList, final String rdmIQN,
        final VmwareMachineDisk disks) throws Exception
    {

        ConfigTarget configTarget = getConfigTargetForHost(computeResMor, hostMor);
        VirtualDevice[] defaultDevices = getDefaultDevices(computeResMor, hostMor);
        VirtualMachineConfigSpec configSpec = new VirtualMachineConfigSpec();
        if (configTarget.getNetwork() != null)
        {
            for (int i = 0; i < configTarget.getNetwork().length; i++)
            {
                VirtualMachineNetworkInfo netInfo = configTarget.getNetwork()[i];
                NetworkSummary netSummary = netInfo.getNetwork();
                if (netSummary.isAccessible())
                {
                    break;
                }
            }
        }
        ManagedObjectReference datastoreRef = null;
        if (datastoreName != null)
        {
            boolean flag = false;
            for (int i = 0; i < configTarget.getDatastore().length; i++)
            {
                VirtualMachineDatastoreInfo vdsInfo = configTarget.getDatastore()[i];
                DatastoreSummary dsSummary = vdsInfo.getDatastore();
                if (dsSummary.getName().equals(datastoreName))
                {
                    flag = true;
                    if (dsSummary.isAccessible())
                    {
                        datastoreName = dsSummary.getName();
                        datastoreRef = dsSummary.getDatastore();
                    }
                    else
                    {
                        throw new Exception("Specified Datastore is not accessible");
                    }
                    break;
                }
            }
            if (!flag)
            {
                throw new Exception("Specified Datastore is not Found");
            }
        }
        else
        {
            boolean flag = false;
            for (int i = 0; i < configTarget.getDatastore().length; i++)
            {
                VirtualMachineDatastoreInfo vdsInfo = configTarget.getDatastore()[i];
                DatastoreSummary dsSummary = vdsInfo.getDatastore();
                if (dsSummary.isAccessible())
                {
                    datastoreName = dsSummary.getName();
                    datastoreRef = dsSummary.getDatastore();
                    flag = true;
                    break;
                }
            }
            if (!flag)
            {
                throw new Exception("No Datastore found on host");
            }
        }
        String datastoreVolume = getVolumeName(datastoreName);
        VirtualMachineFileInfo vmfi = new VirtualMachineFileInfo();
        vmfi.setVmPathName(datastoreVolume);
        configSpec.setFiles(vmfi);

        // Add a scsi controller
        int diskCtlrKey = 1;
        VirtualDeviceConfigSpec scsiCtrlSpec = new VirtualDeviceConfigSpec();
        scsiCtrlSpec.setOperation(VirtualDeviceConfigSpecOperation.add);

        VirtualLsiLogicController scsiCtrl = new VirtualLsiLogicController();
        // XXX scsiCtrl.setConnectable();

        scsiCtrl.setBusNumber(0);
        scsiCtrlSpec.setDevice(scsiCtrl);

        scsiCtrl.setKey(diskCtlrKey);

        // scsiCtrl.setSharedBus(VirtualSCSISharing.physicalSharing);
        scsiCtrl.setSharedBus(VirtualSCSISharing.noSharing); // XXX EBS

        String ctlrType = scsiCtrl.getClass().getName();
        ctlrType = ctlrType.substring(ctlrType.lastIndexOf(".") + 1);

        // Add a scsi controller
        /*
         * int diskCtlrKey2 = 2; VirtualDeviceConfigSpec scsiCtrlSpec2 = new
         * VirtualDeviceConfigSpec();
         * scsiCtrlSpec2.setOperation(VirtualDeviceConfigSpecOperation.add);
         * VirtualLsiLogicController scsiCtrl2 = new VirtualLsiLogicController();
         * scsiCtrl2.setBusNumber(1); scsiCtrlSpec2.setDevice(scsiCtrl2);
         * scsiCtrl2.setKey(diskCtlrKey2);
         * scsiCtrl2.setSharedBus(VirtualSCSISharing.physicalSharing); String ctlrType2 =
         * scsiCtrl2.getClass().getName(); ctlrType2 =
         * ctlrType2.substring(ctlrType2.lastIndexOf(".") + 1);
         */

        // Find the IDE controller
        VirtualDevice ideCtlr = null;
        for (VirtualDevice defaultDevice : defaultDevices)
        {
            if (defaultDevice instanceof VirtualIDEController)
            {
                ideCtlr = defaultDevice;
                break;
            }
        }

        // Add a floppy
        VirtualDeviceConfigSpec floppySpec = new VirtualDeviceConfigSpec();
        floppySpec.setOperation(VirtualDeviceConfigSpecOperation.add);
        VirtualFloppy floppy = new VirtualFloppy();
        VirtualFloppyDeviceBackingInfo flpBacking = new VirtualFloppyDeviceBackingInfo();
        flpBacking.setDeviceName("/dev/fd0");
        floppy.setBacking(flpBacking);
        floppy.setKey(3);
        floppySpec.setDevice(floppy);

        // Add a cdrom based on a physical device
        VirtualDeviceConfigSpec cdSpec = null;

        if (ideCtlr != null)
        {
            cdSpec = new VirtualDeviceConfigSpec();
            cdSpec.setOperation(VirtualDeviceConfigSpecOperation.add);
            VirtualCdrom cdrom = new VirtualCdrom();
            VirtualCdromIsoBackingInfo cdDeviceBacking = new VirtualCdromIsoBackingInfo();
            cdDeviceBacking.setDatastore(datastoreRef);
            cdDeviceBacking.setFileName(datastoreVolume + "testcd.iso");
            cdrom.setBacking(cdDeviceBacking);
            cdrom.setKey(20);
            cdrom.setControllerKey(new Integer(ideCtlr.getKey()));
            cdrom.setUnitNumber(new Integer(0));
            cdSpec.setDevice(cdrom);
        }

        // Create a new disk - file based - for the vm
        VirtualDeviceConfigSpec diskSpec = null;

        // stateless
        if (rdmIQN == null)
        {
            diskSpec = createVirtualDisk(datastoreName, diskCtlrKey, datastoreRef, diskSize);
        }

        // //////////////////////////////
        // XXX VirtualDeviceConfigSpec diskSpec2 = null;
        /*
         * diskSpec2 = createVirtualDisk_1("[datastore1] VMWareTest/VMWareTest_1.vmdk", diskCtlrKey,
         * datastoreRef, diskSize);
         */
        /*
         * XXX diskSpec2 = createRawDeviceMapping(datastoreName, diskCtlrKey, datastoreRef,
         * "/vmfs/devices/disks/vmhba34:5:0:0",
         * "0200000000600144f04a324a7900000c296262e000534f4c415249", Long.parseLong("3221225472"));
         */

        // Add a NIC. the network Name must be set as the device name to create the NIC.//AQUI!!!
        List<VirtualDeviceConfigSpec> nicSpecList = configureNetworkInterfaces(vnicList);

        List<VirtualDeviceConfigSpec> configSpecList = new LinkedList<VirtualDeviceConfigSpec>();
        configSpecList.add(scsiCtrlSpec);
        configSpecList.add(floppySpec);

        configSpecList.add(diskSpec);

        if (ideCtlr != null)
        {
            configSpecList.add(cdSpec);
        }

        configSpecList.addAll(nicSpecList);

        //
        // if (ideCtlr != null)
        // {
        // deviceConfigSpec = new VirtualDeviceConfigSpec[6]; // XXX[6]
        //
        // deviceConfigSpec[3] = cdSpec;
        // deviceConfigSpec[4] = nicSpecPrivate;
        // if (assignPublicInterface)
        // {
        // deviceConfigSpec[5] = nicSpecPublic;
        // }
        //
        // }
        // else
        // {
        // deviceConfigSpec = new VirtualDeviceConfigSpec[5];
        // deviceConfigSpec[3] = nicSpecPrivate;
        // if (assignPublicInterface)
        // {
        // deviceConfigSpec[4] = nicSpecPublic;
        // }
        // }
        //
        // deviceConfigSpec[0] = scsiCtrlSpec;
        // deviceConfigSpec[1] = floppySpec;
        // deviceConfigSpec[2] = diskSpec;

        // /////////////////////////////////////
        // XXX deviceConfigSpec[5] = diskSpec2;
        // /////////////////////////////////////

        configSpec.setDeviceChange(configSpecList.toArray(new VirtualDeviceConfigSpec[] {}));
        return configSpec;
    }

    /**
     * Configures the network interfaces from a virtual nic list
     * 
     * @param vnicList the virtual nic list to configure
     * @return
     * @throws Exception
     */
    public List<VirtualDeviceConfigSpec> configureNetworkInterfaces(final List<VirtualNIC> vnicList)
        throws Exception
    {
        try
        {
            List<VirtualDeviceConfigSpec> nicSpecList = new ArrayList<VirtualDeviceConfigSpec>();
            VmwareMachineUtils utils = new VmwareMachineUtils(cb);
            for (VirtualNIC vnic : vnicList)
            {
                VirtualDeviceConfigSpec nicSpec = new VirtualDeviceConfigSpec();
                nicSpec.setOperation(VirtualDeviceConfigSpecOperation.add);
                VirtualEthernetCard nic2 = new VirtualE1000();
                VirtualEthernetCardNetworkBackingInfo nicBacking =
                    new VirtualEthernetCardNetworkBackingInfo();
                // Try to find if a group corresponding the network name is found. If not create it.
                String networkName = vnic.getNetworkName() + "_" + vnic.getVlanTag();
                ManagedObjectReference networkMor = utils.getNetwork(networkName);
                nicBacking.setDeviceName(networkName);
                nicBacking.setNetwork(networkMor);
                nic2.setAddressType("manual");
                nic2.setMacAddress(vnic.getMacAddress());
                nic2.setBacking(nicBacking);
                nic2.setKey(4);
                nicSpec.setDevice(nic2);
                nicSpecList.add(nicSpec);
            }
            return nicSpecList;
        }
        catch (VirtualMachineException e)
        {
            throw new Exception(e);
        }
    }

    /**
     * This method returns the ConfigTarget for a HostSystem
     * 
     * @param computeResMor A MoRef to the ComputeResource used by the HostSystem
     * @param hostMor A MoRef to the HostSystem
     * @return Instance of ConfigTarget for the supplied HostSystem/ComputeResource
     * @throws Exception When no ConfigTarget can be found
     */
    public ConfigTarget getConfigTargetForHost(final ManagedObjectReference computeResMor,
        final ManagedObjectReference hostMor) throws Exception
    {
        ManagedObjectReference envBrowseMor =
            cb.getServiceUtil3().getMoRefProp(computeResMor, "environmentBrowser");

        ConfigTarget configTarget =
            cb.getServiceUtil().getVimService().queryConfigTarget(envBrowseMor, hostMor);

        if (configTarget == null)
        {
            throw new Exception("No ConfigTarget found in ComputeResource");
        }

        return configTarget;
    }

    /**
     * The method returns the default devices from the HostSystem
     * 
     * @param computeResMor A MoRef to the ComputeResource used by the HostSystem
     * @param hostMor A MoRef to the HostSystem
     * @return Array of VirtualDevice containing the default devices for the HostSystem
     * @throws Exception
     */
    public VirtualDevice[] getDefaultDevices(final ManagedObjectReference computeResMor,
        final ManagedObjectReference hostMor) throws Exception
    {
        ManagedObjectReference envBrowseMor =
            cb.getServiceUtil3().getMoRefProp(computeResMor, "environmentBrowser");

        VirtualMachineConfigOption cfgOpt =
            cb.getServiceUtil().getVimService().queryConfigOption(envBrowseMor, null, hostMor);

        VirtualDevice[] defaultDevs = null;

        if (cfgOpt == null)
        {
            throw new Exception("No VirtualHardwareInfo found in ComputeResource");
        }
        else
        {
            defaultDevs = cfgOpt.getDefaultDevice();
            if (defaultDevs == null)
            {
                throw new Exception("No Datastore found in ComputeResource");
            }
        }

        return defaultDevs;
    }

    private String getVolumeName(final String volName)
    {
        String volumeName = null;
        if (volName != null && volName.length() > 0)
        {
            volumeName = "[" + volName + "]";
        }
        else
        {
            volumeName = "[Local]";
        }

        return volumeName;
    }

    /**
     * XXX deviceName = "/vmfs/devices/disks/vmhba34:5:0:0" lunUuid =
     * "0200000000600144f04a324a7900000c296262e000534f4c415249" public VirtualDeviceConfigSpec
     * createRawDeviceMapping(String volName, int diskCtlrKey, ManagedObjectReference datastoreRef,
     * String deviceName, String lunUuid, long diskSize) { System.err.println("CREATE RDM using");
     * System.err.println("volname "+volName); System.err.println("diskCtrKey "+diskCtlrKey); String
     * volumeName = getVolumeName(volName); VirtualDeviceConfigSpec diskSpec = new
     * VirtualDeviceConfigSpec();
     * diskSpec.setFileOperation(VirtualDeviceConfigSpecFileOperation.create);
     * diskSpec.setOperation(VirtualDeviceConfigSpecOperation.add); VirtualDisk disk = new
     * VirtualDisk(); VirtualDiskRawDiskMappingVer1BackingInfo diskfileBacking = new
     * VirtualDiskRawDiskMappingVer1BackingInfo(); diskfileBacking.setDatastore(datastoreRef);
     * diskfileBacking.setDeviceName(deviceName); diskfileBacking.setLunUuid(lunUuid);
     * diskfileBacking.setCompatibilityMode(VirtualDiskCompatibilityMode._physicalMode);
     * diskfileBacking.setDiskMode(VirtualDiskMode._independent_persistent);
     * diskfileBacking.setFileName(volumeName); disk.setKey(new Integer(0));
     * disk.setControllerKey(new Integer(diskCtlrKey)); disk.setUnitNumber(new Integer(1));
     * disk.setBacking(diskfileBacking); disk.setCapacityInKB(diskSize / 1024);
     * diskSpec.setDevice(disk); return diskSpec; }
     */

    public VirtualDeviceConfigSpec createVirtualDisk(final String volName, final int diskCtlrKey,
        final ManagedObjectReference datastoreRef, final long diskSize)
    {

        String volumeName = getVolumeName(volName);
        VirtualDeviceConfigSpec diskSpec = new VirtualDeviceConfigSpec();

        diskSpec.setFileOperation(VirtualDeviceConfigSpecFileOperation.create);
        diskSpec.setOperation(VirtualDeviceConfigSpecOperation.add);

        VirtualDisk disk = new VirtualDisk();

        VirtualDiskFlatVer2BackingInfo diskfileBacking = new VirtualDiskFlatVer2BackingInfo();
        diskfileBacking.setFileName(volumeName);
        diskfileBacking.setDiskMode("persistent");

        // System.err.println("it is using volumename "+volumeName);
        // System.err.println("diskCtrlKey "+diskCtlrKey);

        disk.setKey(new Integer(0));
        disk.setControllerKey(new Integer(diskCtlrKey));
        disk.setUnitNumber(new Integer(0));

        disk.setBacking(diskfileBacking);
        disk.setCapacityInKB(diskSize / 1024);

        diskSpec.setDevice(disk);

        return diskSpec;
    }
}
