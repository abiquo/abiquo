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

import static com.abiquo.virtualfactory.utils.hyperv.HyperVUtils.enumToJIVariantArray;
import static org.jinterop.dcom.impls.JIObjectFactory.narrowObject;

import java.util.ArrayList;
import java.util.List;

import org.jinterop.dcom.common.JIException;
import org.jinterop.dcom.core.JIArray;
import org.jinterop.dcom.core.JIString;
import org.jinterop.dcom.core.JIVariant;
import org.jinterop.dcom.impls.JIObjectFactory;
import org.jinterop.dcom.impls.automation.IJIDispatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.abiquo.virtualfactory.exception.HypervisorException;
import com.abiquo.virtualfactory.exception.VirtualMachineException;
import com.abiquo.virtualfactory.hypervisor.impl.HyperVHypervisor;
import com.abiquo.virtualfactory.model.AbiCloudModel;
import com.abiquo.virtualfactory.model.AbsVirtualMachine;
import com.abiquo.virtualfactory.model.State;
import com.abiquo.virtualfactory.model.VirtualDisk;
import com.abiquo.virtualfactory.model.VirtualDiskType;
import com.abiquo.virtualfactory.model.config.Configuration;
import com.abiquo.virtualfactory.model.config.HyperVHypervisorConfiguration;
import com.abiquo.virtualfactory.model.config.VirtualMachineConfiguration;
import com.abiquo.virtualfactory.network.VirtualNIC;
//import com.abiquo.virtualfactory.utils.hyperv.CIMDataFile;
import com.abiquo.virtualfactory.utils.hyperv.HyperVConstants;
import com.abiquo.virtualfactory.utils.hyperv.HyperVUtils;
import com.abiquo.virtualfactory.utils.hyperv.MsvmImageManagementService;
import com.abiquo.virtualfactory.utils.hyperv.MsvmVirtualSwitchManagementServiceExtended;
import com.abiquo.virtualfactory.utils.hyperv.MsvmVirtualSystemManagementServiceExtended;
import com.hyper9.jwbem.SWbemObjectSet;
import com.hyper9.jwbem.SWbemServices;
import com.hyper9.jwbem.msvm.virtualsystem.MsvmComputerSystem;

/**
 * Hyper-v virtual machine implementation using DCOM through WMI
 * 
 * @author pnavarro
 */
public abstract class AbsHyperVMachine extends AbsVirtualMachine
{

    /** The logger */
    private static final Logger logger = LoggerFactory.getLogger(AbsHyperVMachine.class);

    /** The generic virtual machine configuration. */
    protected VirtualMachineConfiguration vmConfig;

    /** The Hyper-v hypervisor specific configuration. */
    private final HyperVHypervisorConfiguration hyperVConfig;

    /** The Hyper-v hypervisor */
    protected HyperVHypervisor hyperVHypervisor;

    /** The machine ID */
    private final String machineId;

    /**
     * The virtual machine dispatch
     */
    protected IJIDispatch vmDispatch;

    /**
     * The virtual disk file path
     */
    private String destinationImagePath;

    /**
     * The source virtual disk file path
     */
    private String sourceImagePath;

    /**
     * IDE slots used for extended disks
     */
    public int ideSlotsUsed = 0;

    /**
     * machine name
     */
    private String machineName;
    
  

    /**
     * Instantiates a new Hyper-v machine.
     * 
     * @param config the config
     * @throws VirtualMachineException the virtual machine exception
     */
    public AbsHyperVMachine(final VirtualMachineConfiguration configuration)
        throws VirtualMachineException
    {
        super(configuration);
        if (config.getHyper() == null)
        {
            final String msgErr = "Null hypervisor";
            throw new VirtualMachineException(msgErr);
        }
        vmConfig = configuration;
        AbiCloudModel model = AbiCloudModel.getInstance();
        Configuration mainConfig = model.getConfigManager().getConfiguration();
        hyperVConfig = mainConfig.getHypervConfig();
        machineName = configuration.getMachineName();
        machineId = configuration.getMachineId().toString();
        hyperVHypervisor = (HyperVHypervisor) configuration.getHyper();

        if (!config.getVirtualDiskBaseList().isEmpty())
        {
            if (config.getVirtualDiskBase().getDiskType() == VirtualDiskType.STANDARD)
            {
                initDiskPathLocations();
            }
        }
    }

    /**
     * Private helper to initialize the virtual disk source and destination paths for cloning
     * purposes
     */
    private void initDiskPathLocations()
    {
        AbiCloudModel model = AbiCloudModel.getInstance();
        Configuration mainConfig = model.getConfigManager().getConfiguration();
        HyperVHypervisorConfiguration hypervConfig = mainConfig.getHypervConfig();

        String imagePathTemp = config.getVirtualDiskBase().getImagePath().replace("/", "\\");

        String destinationPath = hypervConfig.getDestinationRepositoryPath();

        destinationPath = destinationPath.endsWith("/") ? destinationPath : destinationPath + "/";

        sourceImagePath = destinationPath.replace("/", "\\") + imagePathTemp;

        String localRepositoryPath = config.getVirtualDiskBase().getTargetDatastore();

        localRepositoryPath = localRepositoryPath.replace('/', '\\');

        localRepositoryPath =
            localRepositoryPath.endsWith("\\") ? localRepositoryPath : localRepositoryPath + "\\";

        String destinationTemp = localRepositoryPath.replace("\\", "\\\\"); // fixes path if
                                                                            // necessary for
                                                                            // deleting vhd

        destinationImagePath = destinationTemp + machineName + ".vhd";

    }

    /**
     * Private helper to remove a char
     * 
     * @param s the string to remove the char from
     * @param c the char to remove
     * @return the new string with the char c removed
     */
    public static String removeChar(final String s, final char c)
    {
        String r = "";
        for (int i = 0; i < s.length(); i++)
        {
            if (s.charAt(i) != c)
                r += s.charAt(i);
        }
        return r;
    }

    /**
     * @see com.abiquo.virtualfactory.model.AbsVirtualMachine#bundleVirtualMachine(java.lang.String)
     */
    @Override
    public void bundleVirtualMachine(final String sourcePath, final String destinationPath,
        final String snapshotName, final boolean isManaged) throws VirtualMachineException
    {
        // Bundlelling the image through the image service conversion method

        // Getting the bundle destination directory

        try
        {
            hyperVHypervisor.reconnect();

            String bundleDirectory = destinationPath;

            if (bundleDirectory == null)
            {
                int pathLastSlash = sourceImagePath.lastIndexOf("\\");
                bundleDirectory = sourceImagePath.substring(0, pathLastSlash);
            }
            else
            {
                String destinationRepositoryPath = hyperVConfig.getDestinationRepositoryPath();

                destinationRepositoryPath =
                    destinationRepositoryPath.endsWith("/") ? destinationRepositoryPath
                        : destinationRepositoryPath + "/";
                bundleDirectory = destinationRepositoryPath + '\\' + bundleDirectory;
            }
            bundleDirectory = bundleDirectory.replace('/', '\\');

            String destinationBundlePath = bundleDirectory + "\\" + snapshotName;

            // updating source path variable

            String sourceBundlePath = null;

            if (isManaged)
            {
                sourceBundlePath = destinationImagePath;
            }
            else
            {
                sourceBundlePath = sourcePath;

                destinationImagePath = sourcePath.replace("\\", "\\\\");
            }

            MsvmImageManagementService imageManagementService =
                MsvmImageManagementService.getManagementService(hyperVHypervisor
                    .getVirtualizationService());
            imageManagementService.convertVirtualHardDisk(sourceBundlePath, destinationBundlePath,
                MsvmImageManagementService.Dynamic);
        }
        catch (Exception e)
        {
            throw new VirtualMachineException(e);
        }
        finally
        {
            hyperVHypervisor.logout();
        }

    }

    /**
     * Perform the virtual image cloning. Creates a copy of the original image and put it on where
     * the current hypervisor expects to load it.
     */
    protected void cloneVirtualDisk() throws Exception
    {

        // Cloning the image through the image service conversion method
        logger.info("Cloning the virtual disk from: {} to {} ", sourceImagePath,
            destinationImagePath);
        MsvmImageManagementService imageManagementService =
            MsvmImageManagementService.getManagementService(hyperVHypervisor
                .getVirtualizationService());
        imageManagementService.convertVirtualHardDisk(sourceImagePath, destinationImagePath,
            MsvmImageManagementService.Dynamic);

    }

    /**
     * Private helper to delete the disks
     * 
     * @throws Exception
     */
    protected void deleteBaseDisk()
    {
        try
        {
            IJIDispatch fileToDelete = getCIMDataFile(destinationImagePath);

            JIVariant[] res = fileToDelete.callMethodA("Delete", null);
            int result = res[0].getObjectAsInt();

            if (result == 0)
            {
                logger.debug(destinationImagePath + " deleted successfuly");
            }
            else
            {
                logger.warn("An error was occurred when deleting the disk {} in the hypervisor",
                    destinationImagePath + ". Error code " + result);
            }
        }
        catch (Exception e)
        {
            logger.warn(
                "An error was occurred when deleting the disk {} in the hypervisor, Exception {}",
                destinationImagePath, e.getMessage());
        }

        // ArrayList<IJIDispatch> rasdList = getRASDAssociatedToSystemSettingData();
        // for (IJIDispatch ijiDispatch : rasdList)
        // {
        // String resourceSubTupe = ijiDispatch.get("ResourceSubType").getObjectAsString2();
        //
        // if (HyperVConstants.VHD.equals(resourceSubTupe))
        // {
        // String vhdPath = HyperVUtils.getStringArray(ijiDispatch, "Connection")[0];
        // }
        //
        // }

    }

    @Override
    public void deployMachine() throws VirtualMachineException
    {
        try
        {
            hyperVHypervisor.reconnect();

            if (!isVMAlreadyCreated())
            {
                if (config.getVirtualDiskBase().getDiskType() == VirtualDiskType.STANDARD)
                {
                    // Just clones the image if the virtual disk is standard
                    // Cloning the virtual disk
                    if (!config.getVirtualDiskBase().isHa())
                    {
                        cloneVirtualDisk();
                    }
                }

                createVirtualMachine();

                configureVirtualMachine();

            }
            else
            {
                this.vmDispatch = getVmDispatch(machineName);
            }

            hyperVHypervisor.logout();

            checkIsCancelled();
        }
        catch (Exception e)
        {
            logger.error("Failed to deploy machine :", e);
            // Doing roll back in the virtual machine
            logger.info("Rolling back the hypevisor state");
            rollBackVirtualMachine();
            state = State.CANCELLED;
            throw new VirtualMachineException(e);
        }
        finally
        {
            hyperVHypervisor.logout();
        }

        logger.info("Created hyper machine name:" + config.getMachineName() + "\t ID:"
            + config.getMachineId().toString() + "\t " + "using hypervisor connection at "
            + config.getHyper().getAddress().toString());

        state = State.DEPLOYED;

    }

    /**
     * Private helper to configure the virtual machine basic resources
     * 
     * @throws Exception
     */
    private void configureVirtualMachine() throws Exception
    {
        configureBasicResources();

        configureVirtualDiskResources();

        configureNetwork();
    }

    /**
     * Private helper to configure the basic resources, like RAM and CPU
     * 
     * @throws Exception
     */
    private void configureBasicResources() throws Exception
    {

        logger.debug("Configuring Basic resources");

        // Configure memory RAM

        configureMemory(vmConfig.getMemoryRAM() / (1024 * 1024));

        // Configure processor cores

        configureProcessorCount(vmConfig.getCpuNumber());

        // configure boot order

        // IJIDispatch settingDataDispatcher = getVirtualSystemSettingData();
        //
        // settingDataDispatcher.put("BootOrder", new JIVariant(new JIArray(new JIVariant[] {
        // new JIVariant(2), new JIVariant(1), new JIVariant(3), new JIVariant(0)})));
        //
        // SWbemServices service = hyperVHypervisor.getVirtualizationService();
        //
        // MsvmVirtualSystemManagementServiceExtended virtualSysteManagementServiceExt =
        // MsvmVirtualSystemManagementServiceExtended.getManagementServiceExtended(service);
        //
        // virtualSysteManagementServiceExt.modifyVirtualSystem(vmDispatch, settingDataDispatcher);

    }

    /**
     * Private helper to configure the the VM's NIC, connect them to the external network and attach
     * them to the VM
     * 
     * @throws Exception
     */
    private void configureNetwork() throws Exception
    {
        logger.debug("Configuring network");

        SWbemServices service = hyperVHypervisor.getVirtualizationService();

        MsvmVirtualSwitchManagementServiceExtended switchService =
            MsvmVirtualSwitchManagementServiceExtended.getManagementService(service);

        // First Find the Msvm_ExternalEthernetPort instance associated to a physical network
        // adapter

        /*
         * String physicalAdapterQuery = "SELECT * FROM msvm_ExternalEthernetPort"; JIVariant[]
         * paQueryResult = execQuery(physicalAdapterQuery); JIVariant[][] paSet =
         * enumToJIVariantArray(paQueryResult); // Attention!! a private agreement is being done,
         * the first physical ethernet port will be // chose for the network connection IJIDispatch
         * paDispatch = getFirstResource(paSet); String vswitchPath =
         * switchService.createSwitch("external network", "n_mola", 1024);
         */
        int portCount = 1;

        for (VirtualNIC vnic : config.getVnicList())
        {
            String externalSwitchQuery =
                "SELECT * FROM Msvm_VirtualSwitch WHERE ElementName='" + vnic.getVSwitchName()
                    + "'";

            JIVariant[] externalSwitchQueryResult = execQuery(externalSwitchQuery);

            JIVariant[][] externalVswitchSet = enumToJIVariantArray(externalSwitchQueryResult);

            if (externalVswitchSet.length == 0)
            {
                String msg =
                    "External network: " + vnic.getVSwitchName()
                        + " not found. The networking resources couldn't be configured";
                // logger
                // .error(
                // "External network not found. The VM NIC with MAC address: {} will have no connectivity",
                // vnic.getMacAddress());
                throw new VirtualMachineException(msg);

            }

            IJIDispatch externalVswitchDispatcher =
                (IJIDispatch) JIObjectFactory.narrowObject(externalVswitchSet[0][0]
                    .getObjectAsComObject().queryInterface(IJIDispatch.IID));

            // Getting the path of the virtual switch

            IJIDispatch externalVSPathDispatcher =
                (IJIDispatch) JIObjectFactory.narrowObject(externalVswitchDispatcher.get("Path_")
                    .getObjectAsComObject().queryInterface(IJIDispatch.IID));

            // Getting the virtual machine path
            String virtualSwitchPath = externalVSPathDispatcher.get("Path").getObjectAsString2();

            // Create the private switch ports for the VM NICS
            String firstNicPortName = "PortName" + portCount + machineName;
            String firtshSwitchPortPath =
                switchService.createSwitchPort(virtualSwitchPath, firstNicPortName,
                    firstNicPortName, null);

            // Creating the Ethernet port

            MsvmVirtualSystemManagementServiceExtended virtualSysteManagementServiceExt =
                MsvmVirtualSystemManagementServiceExtended.getManagementServiceExtended(service);

            // The synthethic Ethernet port is recommended but the guest additions must be
            // installed, so
            // we are using the emulated port

            // String virtualEthernetQuery = "SELECT * FROM Msvm_SyntheticEthernetPortSettingData";
            String virtualEthernetQuery = "SELECT * FROM Msvm_EmulatedEthernetPortSettingData";

            JIVariant[] veQueryResult = execQuery(virtualEthernetQuery);

            JIVariant[][] veSet = enumToJIVariantArray(veQueryResult);

            IJIDispatch veDefaultDispatcher = getResourceAllocationSettingDataDefault(veSet);

            JIVariant[] clonedVEResult = veDefaultDispatcher.callMethodA("Clone_", null);

            IJIDispatch clonedVEDispatcher =
                (IJIDispatch) JIObjectFactory.narrowObject(clonedVEResult[0].getObjectAsComObject()
                    .queryInterface(IJIDispatch.IID));

            String addressFirst = vnic.getMacAddress();
            clonedVEDispatcher.put("StaticMacAddress", new JIVariant(true));
            clonedVEDispatcher.put("Address", new JIVariant(addressFirst));
            clonedVEDispatcher.put("Connection",
                new JIVariant(new JIArray(new JIString[] {new JIString(firtshSwitchPortPath)})));
            // clonedVEDispatcher
            // .put(
            // "VirtualSystemIdentifiers",
            // new JIVariant(new JIArray(new JIString[] {new
            // JIString(UUID.randomUUID().toString())})));

            virtualSysteManagementServiceExt.addVirtualSystemResourcesVoid(vmDispatch,
                clonedVEDispatcher);

            // Tagging private port switch with VLAN

            String vlanesdexport =
                "SELECT * FROM Msvm_VLANEndpointSettingData WHERE ElementName='"
                    + vnic.getVSwitchName() + "_ExternalPort" + "'";

            JIVariant[] vlanesdexportQueryResult = execQuery(vlanesdexport);

            JIVariant[][] vlanesdSetExport = enumToJIVariantArray(vlanesdexportQueryResult);

            if (vlanesdSetExport.length == 0)
            {
                String msg =
                    "External network: " + vnic.getVSwitchName()
                        + " not connected. The networking resources couldn't be configured";
                throw new VirtualMachineException(msg);

            }

            IJIDispatch vlanesdexportDispatch =
                (IJIDispatch) JIObjectFactory.narrowObject(vlanesdSetExport[0][0]
                    .getObjectAsComObject().queryInterface(IJIDispatch.IID));

            JIArray trunkedList = vlanesdexportDispatch.get("TrunkedVLANList").getObjectAsArray();
            JIVariant[] trunkArray = (JIVariant[]) trunkedList.getArrayInstance();
            JIVariant[] modifiedTrunkArray = new JIVariant[trunkArray.length + 1];
            System.arraycopy(trunkArray, 0, modifiedTrunkArray, 0, trunkArray.length);
            modifiedTrunkArray[trunkArray.length] = new JIVariant(vnic.getVlanTag());
            vlanesdexportDispatch.put("TrunkedVLANList",
                new JIVariant(new JIArray(modifiedTrunkArray)));
            vlanesdexportDispatch.callMethodA("Put_", null);
            // service.getObjectDispatcher().callMethodA("Put", vlanesdexportDispatch.)

            String vlanesd =
                "SELECT * FROM Msvm_VLANEndpointSettingData WHERE ElementName='" + firstNicPortName
                    + "'";

            JIVariant[] vlanesdQueryResult = execQuery(vlanesd);

            JIVariant[][] vlanesdSet = enumToJIVariantArray(vlanesdQueryResult);

            IJIDispatch vlanesdDispatch =
                (IJIDispatch) JIObjectFactory.narrowObject(vlanesdSet[0][0].getObjectAsComObject()
                    .queryInterface(IJIDispatch.IID));

            vlanesdDispatch.put("AccessVLAN", new JIVariant(vnic.getVlanTag()));
            vlanesdDispatch.callMethodA("Put_", null);

            portCount++;
        }

        // String vlane = "SELECT * FROM Msvm_VLANEndpoint WHERE Name='" + firstNicPortName + "'";
        //
        // JIVariant[] vlaneQueryResult = execQuery(vlane);
        //
        // JIVariant[][] vlaneSet = enumToJIVariantArray(vlaneQueryResult);
        //
        // IJIDispatch vlanedDispatch =
        // (IJIDispatch) JIObjectFactory.narrowObject(vlaneSet[0][0].getObjectAsComObject()
        // .queryInterface(IJIDispatch.IID));
        // int operationalMode = vlanedDispatch.get("OperationalEndpointMode").getObjectAsInt();
    }

    /**
     * Private helper to create a virtual machine template from the open virtualization format
     * parameters
     * 
     * @throws Exception
     */
    private void createVirtualMachine() throws VirtualMachineException
    {
        try
        {
            SWbemServices service = hyperVHypervisor.getVirtualizationService();

            IJIDispatch globalSettingDispatcher =
                HyperVUtils.createNewInstance(service.getObjectDispatcher(),
                    "Msvm_VirtualSystemGlobalSettingData");

            globalSettingDispatcher.put("ElementName", new JIVariant(new JIString(machineName)));

            // ExternalDataRoot: The fully-qualified path to the root directory of an external data store. 
            // This is set to default value to avoid problems with accessing from a networkdrive
//            if (config.getVirtualDiskBase().getDiskType() == VirtualDiskType.STANDARD)
//            {                
//                VirtualDisk diskBase = config.getVirtualDiskBase();
//                globalSettingDispatcher.put("ExternalDataRoot",
////                    new JIVariant(new JIString(getDatastore(diskBase))));
//                    new JIVariant(new JIString("C:\\")));
//            }

            String globalSettingDataText =
                globalSettingDispatcher.callMethodA("GetText_", new Object[] {new Integer(1)})[0]
                    .getObjectAsString2();

            MsvmVirtualSystemManagementServiceExtended virtualSysteManagementServiceExt =
                MsvmVirtualSystemManagementServiceExtended.getManagementServiceExtended(service);

            virtualSysteManagementServiceExt.defineVirtualSystem(globalSettingDataText);

            // Create the dispatcher
            this.vmDispatch = getVmDispatch(machineName);
        }
        catch (Exception e)
        {
            throw new VirtualMachineException(e);
        }

    }

    private String getDatastore(final VirtualDisk disk)
    {
//        String datastore = disk.getTargetDatastore();
//        if (!datastore.endsWith("/"))
//        {
//            datastore += "/";
//        }
//        return datastore;
        return disk.getTargetDatastore();
    }

    /**
     * Private helper to configure the virtual disks adapters
     * 
     * @throws Exception
     */
    private void configureSCSIVirtualDiskResources() throws Exception
    {
        SWbemServices service = hyperVHypervisor.getVirtualizationService();
        // Preparing the query

        // Getting the SCSI controller
        String query =
            "Select * From Msvm_ResourceAllocationSettingData Where ResourceSubType='"
                + HyperVConstants.PARALLELSCSIHBA + "'";

        JIVariant[] scsiQueryResult = execQuery(query);

        JIVariant[][] tmpSetScsi = enumToJIVariantArray(scsiQueryResult);

        IJIDispatch scsiDispatcher = getResourceAllocationSettingDataDefault(tmpSetScsi);

        JIVariant[] cloneSCSIDefaultResult = scsiDispatcher.callMethodA("Clone_", null);

        IJIDispatch clonedSCSIDispatcher =
            (IJIDispatch) JIObjectFactory.narrowObject(cloneSCSIDefaultResult[0]
                .getObjectAsComObject().queryInterface(IJIDispatch.IID));

        MsvmVirtualSystemManagementServiceExtended virtualSysteManagementServiceExt =
            MsvmVirtualSystemManagementServiceExtended.getManagementServiceExtended(service);

        // Adding SCSI controller since it's not created by default
        virtualSysteManagementServiceExt
            .addVirtualSystemResources(vmDispatch, clonedSCSIDispatcher);

        ArrayList<IJIDispatch> rasdList = getRASDAssociatedToSystemSettingData();

        IJIDispatch scsiResourceAdded = null;

        for (IJIDispatch ijiDispatch : rasdList)
        {
            int rtype = ijiDispatch.get("ResourceType").getObjectAsInt();
            if (rtype == HyperVConstants.PARALLELSCSIHBARTYPE)
            {
                String rsubType = ijiDispatch.get("ResourceSubType").getObjectAsString2();
                if (rsubType.contains(HyperVConstants.PARALLELSCSIHBA))
                {
                    scsiResourceAdded = ijiDispatch;
                }

            }
        }

        // Getting the disk default drive
        String diskDefaultQuery =
            "Select * From Msvm_ResourceAllocationSettingData Where ResourceSubType='"
                + HyperVConstants.DISKSYNTHETIC + "'";

        JIVariant[] diskDefaultQueryResult = execQuery(diskDefaultQuery);

        JIVariant[][] tmpSetDiskDefault = enumToJIVariantArray(diskDefaultQueryResult);

        IJIDispatch diskDispatcher = getResourceAllocationSettingDataDefault(tmpSetDiskDefault);

        JIVariant[] cloneDiskDefaultResult = diskDispatcher.callMethodA("Clone_", null);

        IJIDispatch clonedDiskDispatcher =
            (IJIDispatch) JIObjectFactory.narrowObject(cloneDiskDefaultResult[0]
                .getObjectAsComObject().queryInterface(IJIDispatch.IID));

        // Getting the SCSI controller path

        // Getting the dispatcher of the scsi resource added Path
        IJIDispatch scsiPathDispatcher =
            (IJIDispatch) JIObjectFactory.narrowObject(scsiResourceAdded.get("Path_")
                .getObjectAsComObject().queryInterface(IJIDispatch.IID));

        // Getting the virtual machine path
        String scsiControllerPath = scsiPathDispatcher.get("Path").getObjectAsString2();

        clonedDiskDispatcher.put("Parent", new JIVariant(scsiControllerPath));
        clonedDiskDispatcher.put("Address", new JIVariant(0));

        String diskResourcePath =
            virtualSysteManagementServiceExt.addVirtualSystemResources(this.vmDispatch,
                clonedDiskDispatcher);

        String vhdQuery =
            "Select * From Msvm_ResourceAllocationSettingData Where ResourceSubType='"
                + HyperVConstants.VHD + "'";

        JIVariant[] vhdDefaultQueryResult = execQuery(vhdQuery);

        JIVariant[][] tmpSetVHDDiskDefault = enumToJIVariantArray(vhdDefaultQueryResult);

        IJIDispatch vhdDispatch = getResourceAllocationSettingDataDefault(tmpSetVHDDiskDefault);

        JIVariant[] clonevhdDiskDefaultResult = vhdDispatch.callMethodA("Clone_", null);

        IJIDispatch clonedvhdDispatcher =
            (IJIDispatch) JIObjectFactory.narrowObject(clonevhdDiskDefaultResult[0]
                .getObjectAsComObject().queryInterface(IJIDispatch.IID));

        clonedvhdDispatcher.put("Parent", new JIVariant(diskResourcePath));
        clonedvhdDispatcher.put("Connection",
            new JIVariant(new JIArray(new JIString[] {new JIString(destinationImagePath)})));

        virtualSysteManagementServiceExt.addVirtualSystemResources(this.vmDispatch,
            clonedvhdDispatcher);
    }

    /**
     * Private helper to configure the booting virtual disks resources
     * 
     * @throws Exception
     */
    public abstract void configureVirtualDiskResources() throws Exception;

    /**
     * Configures the extended disk resources
     * 
     * @throws Exception
     */
    public abstract void configureExtendedDiskResources(VirtualMachineConfiguration vmConfig)
        throws Exception;

    /**
     * Configures the virtual hard disk resource to attache the VHD cloned file
     * 
     * @param controllerPath the controller path to use
     * @param addressSlot the address slot to attach the resource in the controller
     * @throws Exception
     */
    protected void configureVHDDisk(final String controllerPath, final int addressSlot)
        throws Exception
    {
        // Getting the disk default drive
        String diskDefaultQuery =
            "Select * From Msvm_ResourceAllocationSettingData Where ResourceSubType='"
                + HyperVConstants.DISKSYNTHETIC + "'";

        JIVariant[] diskDefaultQueryResult = execQuery(diskDefaultQuery);

        JIVariant[][] tmpSetDiskDefault = enumToJIVariantArray(diskDefaultQueryResult);

        IJIDispatch diskDispatcher = getResourceAllocationSettingDataDefault(tmpSetDiskDefault);

        JIVariant[] cloneDiskDefaultResult = diskDispatcher.callMethodA("Clone_", null);

        IJIDispatch clonedDiskDispatcher =
            (IJIDispatch) JIObjectFactory.narrowObject(cloneDiskDefaultResult[0]
                .getObjectAsComObject().queryInterface(IJIDispatch.IID));

        clonedDiskDispatcher.put("Parent", new JIVariant(controllerPath));
        clonedDiskDispatcher.put("Address", new JIVariant(addressSlot));

        SWbemServices service = hyperVHypervisor.getVirtualizationService();

        MsvmVirtualSystemManagementServiceExtended virtualSysteManagementServiceExt =
            MsvmVirtualSystemManagementServiceExtended.getManagementServiceExtended(service);

        String diskResourcePath =
            virtualSysteManagementServiceExt.addVirtualSystemResources(this.vmDispatch,
                clonedDiskDispatcher);

        String vhdQuery =
            "Select * From Msvm_ResourceAllocationSettingData Where ResourceSubType='"
                + HyperVConstants.VHD + "'";

        JIVariant[] vhdDefaultQueryResult = execQuery(vhdQuery);

        JIVariant[][] tmpSetVHDDiskDefault = enumToJIVariantArray(vhdDefaultQueryResult);

        IJIDispatch vhdDispatch = getResourceAllocationSettingDataDefault(tmpSetVHDDiskDefault);

        JIVariant[] clonevhdDiskDefaultResult = vhdDispatch.callMethodA("Clone_", null);

        IJIDispatch clonedvhdDispatcher =
            (IJIDispatch) JIObjectFactory.narrowObject(clonevhdDiskDefaultResult[0]
                .getObjectAsComObject().queryInterface(IJIDispatch.IID));

        clonedvhdDispatcher.put("Parent", new JIVariant(diskResourcePath));
        clonedvhdDispatcher.put("Connection",
            new JIVariant(new JIArray(new JIString[] {new JIString(destinationImagePath)})));

        virtualSysteManagementServiceExt.addVirtualSystemResources(this.vmDispatch,
            clonedvhdDispatcher);
    }

    /**
     * Gets the associated resource allocation setting data of
     * 
     * @return
     * @throws JIException
     */
    private ArrayList<IJIDispatch> getRASDAssociatedToSystemSettingData() throws JIException
    {
        IJIDispatch vssdDispatch = getVirtualSystemSettingData();
        ArrayList<IJIDispatch> res = new ArrayList<IJIDispatch>();
        JIVariant[] tmp =
            execQuery("Associators of {Msvm_VirtualSystemSettingData.InstanceID='"
                + vssdDispatch.get(("InstanceID")).getObjectAsString2()
                + "'} Where ResultClass= Msvm_ResourceAllocationSettingData");
        JIVariant[][] rasdSet = enumToJIVariantArray(tmp);
        for (JIVariant[] element : rasdSet)
        {
            try
            {
                res.add((IJIDispatch) JIObjectFactory.narrowObject(element[0]
                    .getObjectAsComObject().queryInterface(IJIDispatch.IID)));
            }
            catch (IndexOutOfBoundsException e)
            {
                logger
                    .warn("An error occured while determining the virtual system setting data of "
                        + this.machineName);
            }
        }
        return res;
    }

    /**
     * Gets the VirtualSystemSettingDataComponent
     * 
     * @return
     * @throws JIException
     */
    private ArrayList<IJIDispatch> getVSSDComponentAssociatedToSystemSettingData()
        throws JIException
    {
        IJIDispatch vssdDispatch = getVirtualSystemSettingData();
        // Getting the dispatcher of the VSSD Path
        IJIDispatch vssdPathDispatcher =
            (IJIDispatch) JIObjectFactory.narrowObject(vssdDispatch.get("Path_")
                .getObjectAsComObject().queryInterface(IJIDispatch.IID));

        // Getting the virtual machine path
        String vssdPath = vssdPathDispatcher.get("Path").getObjectAsString2();
        ArrayList<IJIDispatch> res = new ArrayList<IJIDispatch>();
        // JIVariant[] tmp =
        // execQuery("SELECT * FROM Msvm_VirtualSystemSettingDataComponent WHERE (GroupComponent='"
        // + vssdPath + "')");
        JIVariant[] tmp = execQuery("SELECT * FROM Msvm_VirtualSystemSettingDataComponent");
        JIVariant[][] rasdSet = enumToJIVariantArray(tmp);

        for (JIVariant[] element : rasdSet)
        {
            try
            {
                IJIDispatch resDispatch =
                    (IJIDispatch) JIObjectFactory.narrowObject(element[0].getObjectAsComObject()
                        .queryInterface(IJIDispatch.IID));
                String partComponent = resDispatch.get("PartComponent").getObjectAsString2();
                String groupComponent = resDispatch.get("GroupComponent").getObjectAsString2();
                if (groupComponent.equals(vssdPath))
                {
                    res.add(resDispatch);
                }

            }
            catch (IndexOutOfBoundsException e)
            {
                logger
                    .warn("An error occured while determining the virtual system setting data of "
                        + this.machineName);
            }
        }
        return res;
    }

    /**
     * Configures the memory in the VM
     * 
     * @param memoryRam the new memory RAM
     * @throws Exception
     */
    private void configureMemory(final long memoryRam) throws Exception
    {
        IJIDispatch memorySettingDataDispatch = getRASDByClass("Msvm_MemorySettingData");

        memorySettingDataDispatch.put("VirtualQuantity", new JIVariant(memoryRam));
        // memorySettingDataDispatch.put("Reservation", new JIVariant(memoryRam));
        // memorySettingDataDispatch.put("Limit", new JIVariant(100000));

        SWbemServices service = hyperVHypervisor.getVirtualizationService();

        MsvmVirtualSystemManagementServiceExtended virtualSysteManagementServiceExt =
            MsvmVirtualSystemManagementServiceExtended.getManagementServiceExtended(service);

        virtualSysteManagementServiceExt.modifyVirtualSystemResources(this.vmDispatch,
            memorySettingDataDispatch);

    }

    /**
     * Configures the processor in the VM
     * 
     * @param processorCount the new processor count
     * @throws Exception
     */
    private void configureProcessorCount(final int processorCount) throws Exception
    {
        IJIDispatch processorSettingDataDispatch = getRASDByClass("Msvm_ProcessorSettingData");

        processorSettingDataDispatch
            .put("VirtualQuantity", new JIVariant(new Long(processorCount)));
        // processorSettingDataDispatch.put("Reservation", new JIVariant(new Long(processorCount)));
        // processorSettingDataDispatch.put("Limit", new JIVariant(new Long(processorCount)));
        processorSettingDataDispatch.put("Limit", new JIVariant(100000));

        SWbemServices service = hyperVHypervisor.getVirtualizationService();

        MsvmVirtualSystemManagementServiceExtended virtualSysteManagementServiceExt =
            MsvmVirtualSystemManagementServiceExtended.getManagementServiceExtended(service);

        virtualSysteManagementServiceExt.modifyVirtualSystemResources(this.vmDispatch,
            processorSettingDataDispatch);
    }

    private IJIDispatch getRASDByClass(final String className) throws Exception
    {
        // Getting the virtual machine path
        String vmPath = getDispatchPath(this.vmDispatch);

        JIVariant[] tmp =
            execQuery("Associators of {" + vmPath
                + "} Where ResultClass=Msvm_VirtualSystemSettingData");
        JIVariant[][] vssdVariantArray = enumToJIVariantArray(tmp);
        IJIDispatch vmSettingDispatch =
            (IJIDispatch) JIObjectFactory.narrowObject(vssdVariantArray[0][0]
                .getObjectAsComObject().queryInterface(IJIDispatch.IID));

        String vmSettingPath = getDispatchPath(vmSettingDispatch);

        JIVariant[] tmp2 =
            execQuery("Associators of {" + vmSettingPath + "} Where ResultClass=" + className
                + " AssocClass=Msvm_VirtualSystemSettingDataComponent "
                + "ResultRole = PartComponent " + "Role = GroupComponent");
        JIVariant[][] resourceSettingVariantArray = enumToJIVariantArray(tmp2);

        return (IJIDispatch) JIObjectFactory.narrowObject(resourceSettingVariantArray[0][0]
            .getObjectAsComObject().queryInterface(IJIDispatch.IID));
    }

    /**
     * To get the VirtualSystemSettingData associated to this virtual machine
     * 
     * @return the {@link IJIDispatch} of this vm's Msvm_VirtualSystemSettingData
     * @throws JIException
     */
    private IJIDispatch getVirtualSystemSettingData() throws JIException
    {
        // Getting the dispatcher of the VM Path
        IJIDispatch vmPathDispatcher =
            (IJIDispatch) JIObjectFactory.narrowObject(this.vmDispatch.get("Path_")
                .getObjectAsComObject().queryInterface(IJIDispatch.IID));

        // Getting the virtual machine path
        String vmPath = vmPathDispatcher.get("Path").getObjectAsString2();

        JIVariant[] tmp =
            execQuery("Associators of {"
                + vmPath
                + "} Where AssocClass=Msvm_SettingsDefineState ResultClass=Msvm_VirtualSystemSettingData");
        JIVariant[][] vssdVariantArray = enumToJIVariantArray(tmp);
        return (IJIDispatch) JIObjectFactory.narrowObject(vssdVariantArray[0][0]
            .getObjectAsComObject().queryInterface(IJIDispatch.IID));
    }

    /**
     * Gets the path of the dispatch
     * 
     * @param dispatch the dispatch to get the path from
     * @return the dispatch path
     * @throws JIException
     */
    protected String getDispatchPath(final IJIDispatch dispatch) throws JIException
    {
        // Getting the dispatcher of the Path
        IJIDispatch pathDispatcher =
            (IJIDispatch) JIObjectFactory.narrowObject(dispatch.get("Path_").getObjectAsComObject()
                .queryInterface(IJIDispatch.IID));

        // Getting the path
        return pathDispatcher.get("Path").getObjectAsString2();
    }

    /**
     * Get the virtual machine object with the given virtual machine name.
     * 
     * @param vmName The name of the virtual machine to find.
     * @return The virtual machine object.
     * @throws JIException If the vmDispatch cannot eb retrieved.
     */
    protected IJIDispatch getVmDispatch(final String vmName) throws JIException
    {
        JIVariant[] tmp =
            execQuery("Select * From Msvm_ComputerSystem Where ElementName='" + vmName + "'");
        JIVariant[][] tmpSetVM = enumToJIVariantArray(tmp);

        return (IJIDispatch) JIObjectFactory.narrowObject(tmpSetVM[0][0].getObjectAsComObject()
            .queryInterface(IJIDispatch.IID));
    }

    /**
     * Gets the first resource of the resource set. Maybe used when getting the first ethernet port.
     * 
     * @param resourceSet the resource set to get the resource
     * @return the {@link IJIDispatch} of the first resource
     * @throws JIException
     */
    private IJIDispatch getFirstResource(final JIVariant[][] resourceSet) throws JIException
    {
        IJIDispatch resource =
            (IJIDispatch) JIObjectFactory.narrowObject(resourceSet[0][0].getObjectAsComObject()
                .queryInterface(IJIDispatch.IID));

        return resource;
    }

    /**
     * Gets the IDE controller of a resource set by address
     * 
     * @param address the address of the controller to get
     * @param rasdSet the resource allocation setting data set
     * @return the default resource allocation setting data
     * @throws JIException
     */
    protected IJIDispatch getIdeControllerByAddress(final int address) throws JIException
    {
        // Getting the SCSI controller
        String query =
            "Select * From Msvm_ResourceAllocationSettingData Where ResourceSubType='"
                + HyperVConstants.IDECONTROLLER + "'";

        JIVariant[] ideQueryResult = execQuery(query);

        JIVariant[][] tmpSetIde = enumToJIVariantArray(ideQueryResult);

        IJIDispatch resourceDispatch = null;
        for (JIVariant[] element : tmpSetIde)
        {
            try
            {
                IJIDispatch resource =
                    (IJIDispatch) JIObjectFactory.narrowObject(element[0].getObjectAsComObject()
                        .queryInterface(IJIDispatch.IID));
                if (String.valueOf(address).equals(resource.get("Address").getObjectAsString2()))
                {
                    resourceDispatch = resource;
                }
            }
            catch (IndexOutOfBoundsException e)
            {
                logger
                    .warn("An error occured while determining the virtual system setting data of "
                        + this.machineName);
            }
        }

        return resourceDispatch;
    }

    /**
     * Gets the RASD default of a resource set
     * 
     * @param rasdSet the resource allocation setting data set
     * @return the default resource allocation setting data
     * @throws JIException
     */
    protected IJIDispatch getResourceAllocationSettingDataDefault(final JIVariant[][] rasdSet)
        throws JIException
    {
        IJIDispatch resourceDispatch = null;
        for (JIVariant[] element : rasdSet)
        {
            try
            {
                IJIDispatch resource =
                    (IJIDispatch) JIObjectFactory.narrowObject(element[0].getObjectAsComObject()
                        .queryInterface(IJIDispatch.IID));
                if (resource.get("InstanceID").getObjectAsString2().contains("Default"))
                {
                    resourceDispatch = resource;
                }
            }
            catch (IndexOutOfBoundsException e)
            {
                logger
                    .warn("An error occured while determining the virtual system setting data of "
                        + this.machineName);
            }
        }

        return resourceDispatch;
    }

    /**
     * @see com.abiquo.virtualfactory.model.AbsVirtualMachine#isVMAlreadyCreated()
     */
    @Override
    public boolean isVMAlreadyCreated() throws VirtualMachineException
    {
        try
        {
            // Preparing the query
            String query =
                "Select * From Msvm_VirtualSystemGlobalSettingData Where ElementName='"
                    + machineName + "'";

            JIVariant[] result = execQuery(query);

            JIVariant[][] tmpSet = enumToJIVariantArray(result);
            if (tmpSet.length > 0)
                return true;

            return false;
        }
        catch (JIException e)
        {
            throw new VirtualMachineException(e);
        }

    }

    /**
     * @see com.abiquo.virtualfactory.model.AbsVirtualMachine#pauseMachine()
     */
    @Override
    public void pauseMachine() throws VirtualMachineException
    {
        try
        {
            hyperVHypervisor.reconnect();

            if (!checkState(State.PAUSE))
                changeVirtualMachineState(HyperVConstants.PAUSED);

        }
        catch (Exception e)
        {
            throw new VirtualMachineException(e);
        }
        finally
        {
            hyperVHypervisor.logout();
        }

    }

    /**
     * @see com.abiquo.virtualfactory.model.AbsVirtualMachine#powerOffMachine()
     */
    @Override
    public void powerOffMachine() throws VirtualMachineException
    {
        try
        {
            hyperVHypervisor.reconnect();

            if (!checkState(State.POWER_OFF))
                changeVirtualMachineState(HyperVConstants.POWER_OFF);

        }
        catch (Exception e)
        {
            throw new VirtualMachineException(e);
        }
        finally
        {
            hyperVHypervisor.logout();
        }

    }

    /**
     * @see com.abiquo.virtualfactory.model.AbsVirtualMachine#powerOnMachine()
     */
    @Override
    public void powerOnMachine() throws VirtualMachineException
    {
        try
        {
            hyperVHypervisor.reconnect();

            if (!checkState(State.POWER_UP))
                changeVirtualMachineState(HyperVConstants.POWER_ON);

        }
        catch (Exception e)
        {
            throw new VirtualMachineException(e);
        }
        finally
        {
            hyperVHypervisor.logout();
        }

    }

    /**
     * Private helper to change the virtual machine state
     * 
     * @param state the state to change
     * @throws VirtualMachineException
     */
    private void changeVirtualMachineState(final int state) throws VirtualMachineException
    {
        try
        {
            // Preparing the query
            String format = "SELECT * FROM Msvm_ComputerSystem WHERE ElementName='%s'";
            String query = String.format(format, machineName);

            JIVariant[] queryResult = execQuery(query);

            SWbemObjectSet<MsvmComputerSystem> compObjectSet =
                hyperVHypervisor.getVirtualizationService().execQuery(query,
                    MsvmComputerSystem.class);

            MsvmComputerSystem virtualMachine = compObjectSet.iterator().next();
            if (virtualMachine == null)
            {
                String message =
                    "We couldn't get the state of the virtual machine since it doesn't exist";
                logger.error(message);
                throw new VirtualMachineException(message);
            }

            JIVariant[][] machineSetArray = HyperVUtils.enumToJIVariantArray(queryResult);
            IJIDispatch machineDispatch =
                (IJIDispatch) narrowObject(machineSetArray[0][0].getObjectAsComObject());
            Object[] params = new Object[] {new Integer(state), JIVariant.EMPTY_BYREF(), null};
            JIVariant[] res = machineDispatch.callMethodA("RequestStateChange", params);
            int result = res[0].getObjectAsInt();
            if (result == 0)
            {
                logger.debug(this.machineName + "State changed succesfully");
            }
            else
            {
                if (result == 4096)
                {
                    logger.debug("State changed to " + state + " on...");
                    try
                    {
                        String jobPath = res[1].getObjectAsVariant().getObjectAsString2();
                        HyperVUtils.monitorJob(jobPath, hyperVHypervisor.getVirtualizationService()
                            .getObjectDispatcher());
                        logger.debug(this.machineName + "State changed succesfully");
                    }
                    catch (Exception e)
                    {
                        String message =
                            "An exception occured while monitoring " + this.machineName
                                + "this state changement" + state + e;
                        throw new VirtualMachineException(message);
                    }
                }
                else
                {
                    String message =
                        "Failed at powering " + this.machineName + " on. Error code: " + result;
                    throw new VirtualMachineException(message);
                }
            }

        }
        catch (Exception e)
        {
            throw new VirtualMachineException(e);
        }
    }

    /**
     * @see com.abiquo.virtualfactory.model.AbsVirtualMachine#reconfigVM(com.abiquo.virtualfactory.model.config.VirtualMachineConfiguration)
     */
    @Override
    public void reconfigVM(final VirtualMachineConfiguration newConfiguration)
        throws VirtualMachineException
    {
        try
        {
            hyperVHypervisor.reconnect();

            // Load the VM in the hypervisor to be used in the called methods
            if (this.vmDispatch == null)
            {
                this.vmDispatch = getVmDispatch(machineName);
            }

            // Setting the new Ram value
            if (newConfiguration.isRam_set())
            {
                logger.info("Reconfiguring The Virtual Machine For Memory Update " + machineName);

                configureMemory(newConfiguration.getMemoryRAM() / 1048576);

            }

            // Setting the number cpu value
            if (newConfiguration.isCpu_number_set())
            {
                logger.info("Reconfiguring The Virtual Machine For CPU Update " + machineName);

                configureProcessorCount(newConfiguration.getCpuNumber());
            }

        }
        catch (Exception e)
        {
            throw new VirtualMachineException(e);
        }
    }

    /**
     * It deletes the switch port created and it erases the VLAN tag from the external network trunk
     * if is not being VLAN list
     * 
     * @throws VirtualMachineException
     */
    protected void deconfigureNetwork() throws Exception
    {
        try
        {
            logger.debug("Deconfiguring network");
            int portCount = 1;
            for (VirtualNIC vnic : config.getVnicList())
            {
                logger.debug("Deconfiguring networking for machine: " + machineId);
                // Checking if this VLAN is used by other VM
                String vlanUsedCheckQuery =
                    "SELECT * FROM Msvm_VLANEndpointSettingData WHERE AccessVLAN="
                        + vnic.getVlanTag();

                JIVariant[] vlancheckQueryResult = execQuery(vlanUsedCheckQuery);

                JIVariant[][] vlanCheckQuerySet = enumToJIVariantArray(vlancheckQueryResult);

                if (vlanCheckQuerySet.length != 0)
                {
                    String vlanesdexport =
                        "SELECT * FROM Msvm_VLANEndpointSettingData WHERE ElementName='"
                            + vnic.getVSwitchName() + "_ExternalPort" + "'";

                    JIVariant[] vlanesdexportQueryResult = execQuery(vlanesdexport);

                    JIVariant[][] vlanesdSetExport = enumToJIVariantArray(vlanesdexportQueryResult);

                    IJIDispatch vlanesdexportDispatch =
                        (IJIDispatch) JIObjectFactory.narrowObject(vlanesdSetExport[0][0]
                            .getObjectAsComObject().queryInterface(IJIDispatch.IID));

                    JIArray trunkedList =
                        vlanesdexportDispatch.get("TrunkedVLANList").getObjectAsArray();
                    JIVariant[] trunkArray = (JIVariant[]) trunkedList.getArrayInstance();
                    JIVariant[] modifiedArray = eraseVLANFromArray(trunkArray, vnic.getVlanTag());
                    vlanesdexportDispatch.put("TrunkedVLANList",
                        new JIVariant(new JIArray(modifiedArray)));
                    vlanesdexportDispatch.callMethodA("Put_", null);
                }

                // Deleting the switch ports

                String nicPortName = "PortName" + portCount + machineName;

                String nicPortSwitch =
                    "SELECT * FROM Msvm_SwitchPort WHERE Name='" + nicPortName + "'";

                JIVariant[] nicPortSwitchResult = execQuery(nicPortSwitch);

                JIVariant[][] nicPortSwitchSet = enumToJIVariantArray(nicPortSwitchResult);

                IJIDispatch nicSwitchPortDispatch =
                    (IJIDispatch) JIObjectFactory.narrowObject(nicPortSwitchSet[0][0]
                        .getObjectAsComObject().queryInterface(IJIDispatch.IID));

                // Getting the dispatcher of the VM Path
                IJIDispatch nicSwitchPortDispatcher =
                    (IJIDispatch) JIObjectFactory.narrowObject(nicSwitchPortDispatch.get("Path_")
                        .getObjectAsComObject().queryInterface(IJIDispatch.IID));

                // Getting the virtual machine path
                String nicSwitchPortPath = nicSwitchPortDispatcher.get("Path").getObjectAsString2();

                SWbemServices service = hyperVHypervisor.getVirtualizationService();

                MsvmVirtualSwitchManagementServiceExtended switchService =
                    MsvmVirtualSwitchManagementServiceExtended.getManagementService(service);

                switchService.deleteSwitchPort(nicSwitchPortPath);

                // Create the private switch ports for the VM NICS
                /*
                 * String portName = "PortName" + portCount + machineName; String vlanesd =
                 * "SELECT * FROM Msvm_VLANEndpointSettingData WHERE ElementName='" + portName +
                 * "'"; JIVariant[] vlanesdQueryResult = execQuery(vlanesd); JIVariant[][]
                 * vlanesdSet = enumToJIVariantArray(vlanesdQueryResult); if (vlanesdSet.length !=
                 * 0) { IJIDispatch vlanesdDispatch = (IJIDispatch)
                 * JIObjectFactory.narrowObject(vlanesdSet[0][0]
                 * .getObjectAsComObject().queryInterface(IJIDispatch.IID));
                 * logger.debug("Deleting the VLAN EndpointSettingData" + portName);
                 * vlanesdDispatch.callMethodA("Delete_", null); } else {
                 * logger.debug("The Msvm_VLANEndpointSettingData for: " + portName +
                 * "is not found"); } String vlane = "SELECT * FROM Msvm_VLANEndpoint WHERE Name='"
                 * + portName + "'"; JIVariant[] vlaneQueryResult = execQuery(vlane); JIVariant[][]
                 * vlaneSet = enumToJIVariantArray(vlaneQueryResult); if (vlaneSet.length != 0) {
                 * IJIDispatch vlanedDispatch = (IJIDispatch)
                 * JIObjectFactory.narrowObject(vlaneSet[0][0]
                 * .getObjectAsComObject().queryInterface(IJIDispatch.IID));
                 * logger.debug("Deleting the VLAN Endpoint" + portName);
                 * vlanedDispatch.callMethodA("Delete_", null); } else {
                 * logger.debug("The Msvm_VLANEndpoint for: " + portName + "is not found"); }
                 */

                portCount++;
            }
        }
        catch (JIException e)
        {
            throw new VirtualMachineException("The Virtual machine network resources can not be deconfigured",
                e);
        }

    }

    /**
     * Private helper to erase the vlanTag from the array
     * 
     * @param trunkArray the list of VLAN trunk
     * @param vlanTag the vlan tag to erase
     * @return the array without the vlan tag
     * @throws JIException
     */

    private JIVariant[] eraseVLANFromArray(final JIVariant[] trunkArray, final int vlanTag)
        throws JIException
    {
        List<JIVariant> tagList = new ArrayList<JIVariant>();
        for (int i = 0; i < trunkArray.length; i++)
        {
            JIVariant jiVariant = trunkArray[i];
            int trunkTag = jiVariant.getObjectAsInt();
            if (trunkTag != vlanTag)
            {
                tagList.add(new JIVariant(trunkTag));
            }

        }
        JIVariant[] array = tagList.toArray(new JIVariant[tagList.size()]);
        return array;

    }

    /**
     * @see com.abiquo.virtualfactory.model.AbsVirtualMachine#resetMachine()
     */
    @Override
    public void resetMachine() throws VirtualMachineException
    {
        try
        {
            hyperVHypervisor.reconnect();

            if (!checkState(State.POWER_UP))
                changeVirtualMachineState(HyperVConstants.REBOOT);

        }
        catch (Exception e)
        {
            throw new VirtualMachineException(e);
        }
        finally
        {
            hyperVHypervisor.logout();
        }

    }

    /**
     * @see com.abiquo.virtualfactory.model.AbsVirtualMachine#resumeMachine()
     */
    @Override
    public void resumeMachine() throws VirtualMachineException
    {
        try
        {
            hyperVHypervisor.reconnect();

            if (!checkState(State.POWER_UP))
                changeVirtualMachineState(HyperVConstants.POWER_ON);

        }
        catch (Exception e)
        {
            throw new VirtualMachineException(e);
        }
        finally
        {
            hyperVHypervisor.logout();
        }

    }

    /**
     * Gets the the file to execute operations
     * 
     * @param file the file to get
     * @return an instance of {@link CIMDataFile}
     * @throws Exception
     */
    public IJIDispatch getCIMDataFile(final String file) throws Exception
    {
        // Preparing the query
        String query = "SELECT * FROM CIM_DataFile WHERE Name='" + file + "'";

        JIVariant[] res =
            hyperVHypervisor.getCIMService().getObjectDispatcher()
                .callMethodA("ExecQuery", new Object[] {new JIString(query)});

        JIVariant[][] fileSet = HyperVUtils.enumToJIVariantArray(res);

        if (fileSet.length != 1)
        {
            throw new Exception("Cannot identify the vhd to delete: " + file);
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

    /**
     * Private helper to check the real state of the virtual machine
     * 
     * @param stateToCheck the state to check
     * @return true if the state in the hypervisors equals to the state as parameter, false if
     *         contrary
     */
    private boolean checkState(final State stateToCheck) throws VirtualMachineException
    {
        State actualState = getStateInHypervisor();
        if (actualState.compareTo(stateToCheck) == 0)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    @Override
    public State getStateInHypervisor()
    {
        State state;
        try
        {
            hyperVHypervisor.reconnect();
            String format = "SELECT * FROM Msvm_ComputerSystem WHERE ElementName='%s'";
            String query = String.format(format, machineName);
            state = null;

            SWbemObjectSet<MsvmComputerSystem> compObjectSet =
                hyperVHypervisor.getVirtualizationService().execQuery(query,
                    MsvmComputerSystem.class);
            MsvmComputerSystem virtualMachine = compObjectSet.iterator().next();
            if (virtualMachine == null)
            {
                String message =
                    "We couldn't get the state of the virtual machine since it doesn't exist";
                logger.error(message);
                return State.UNKNOWN;
            }
            int vmstate = virtualMachine.getEnabledState();
            switch (vmstate)
            {
                case HyperVConstants.POWER_ON:
                    state = State.POWER_UP;
                    break;
                case HyperVConstants.POWER_OFF:
                    state = State.POWER_OFF;
                    break;
                case HyperVConstants.SUSPENDED:
                    state = State.PAUSE;
                    break;
                case HyperVConstants.PAUSED:
                    state = State.PAUSE;
                    break;
                default:
                    state = State.UNKNOWN;
                    break;
            }
        }
        catch (HypervisorException e)
        {
            logger.error("An error was occurred when getting the virtual machine state", e);
            return State.UNKNOWN;
        }
        finally
        {
            hyperVHypervisor.logout();
        }

        return state;
    }

    /**
     * Private helper to execute query with the virtualization service
     * 
     * @param query the query to execute
     * @return array of results
     * @throws JIException
     */
    public JIVariant[] execQuery(final String query) throws JIException
    {
        SWbemServices service = hyperVHypervisor.getVirtualizationService();

        IJIDispatch objectDispatcher = service.getObjectDispatcher();

        Object[] inParams =
            new Object[] {new JIString(query), JIVariant.OPTIONAL_PARAM(),
            JIVariant.OPTIONAL_PARAM(), JIVariant.OPTIONAL_PARAM(),};

        JIVariant[] results = objectDispatcher.callMethodA("ExecQuery", inParams);
        return results;
    }
}
