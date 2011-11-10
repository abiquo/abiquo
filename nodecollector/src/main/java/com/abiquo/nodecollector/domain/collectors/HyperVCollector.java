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
package com.abiquo.nodecollector.domain.collectors;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import javax.xml.xpath.XPathExpressionException;

import jcifs.smb.SmbException;

import org.apache.commons.lang.StringUtils;
import org.jinterop.dcom.common.JIException;
import org.jinterop.dcom.core.JIArray;
import org.jinterop.dcom.core.JIString;
import org.jinterop.dcom.core.JIVariant;
import org.jinterop.dcom.impls.JIObjectFactory;
import org.jinterop.dcom.impls.automation.IJIDispatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.abiquo.model.enumerator.HypervisorType;
import com.abiquo.nodecollector.aim.WsmanCollector;
import com.abiquo.nodecollector.aim.impl.WsmanCollectorImpl;
import com.abiquo.nodecollector.constants.MessageValues;
import com.abiquo.nodecollector.domain.Collector;
import com.abiquo.nodecollector.domain.collectors.hyperv.HyperVConstants;
import com.abiquo.nodecollector.domain.collectors.hyperv.HyperVState;
import com.abiquo.nodecollector.domain.collectors.hyperv.HyperVUtils;
import com.abiquo.nodecollector.domain.collectors.hyperv.MsvmImageManagementService;
import com.abiquo.nodecollector.domain.collectors.hyperv.Win32Process;
import com.abiquo.nodecollector.domain.collectors.hyperv.WindowsRegistry;
import com.abiquo.nodecollector.exception.CollectorException;
import com.abiquo.nodecollector.exception.ConnectionException;
import com.abiquo.nodecollector.exception.LoginException;
import com.abiquo.nodecollector.exception.NoManagedException;
import com.abiquo.nodecollector.exception.libvirt.WsmanException;
import com.abiquo.nodecollector.utils.ResourceComparator;
import com.abiquo.nodecollector.utils.XPathUtils;
import com.abiquo.server.core.infrastructure.nodecollector.HostDto;
import com.abiquo.server.core.infrastructure.nodecollector.HostStatusEnumType;
import com.abiquo.server.core.infrastructure.nodecollector.ResourceEnumType;
import com.abiquo.server.core.infrastructure.nodecollector.ResourceType;
import com.abiquo.server.core.infrastructure.nodecollector.VirtualDiskEnumType;
import com.abiquo.server.core.infrastructure.nodecollector.VirtualSystemCollectionDto;
import com.abiquo.server.core.infrastructure.nodecollector.VirtualSystemDto;
import com.abiquo.server.core.infrastructure.nodecollector.VirtualSystemStatusEnumType;
import com.hyper9.jwbem.SWbemLocator;
import com.hyper9.jwbem.SWbemServices;
import com.hyper9.jwbem.msvm.virtualsystem.MsvmSummaryInformation;
import com.hyper9.jwbem.msvm.virtualsystem.MsvmVirtualSystemSettingData;
import com.hyper9.jwbem.msvm.virtualsystemmanagement.MsvmVirtualSystemManagementService;

/**
 * Collects information of an Hyper-V node.
 * 
 * @author ibarrera
 */
@Collector(type = HypervisorType.HYPERV_301, order = 2)
public class HyperVCollector extends AbstractCollector
{
    /** The logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(HyperVCollector.class);

    /** The SWbem service for the virtualization namespace. */
    private transient SWbemServices virtService;

    /** The SWbem service for the CIM namespace. */
    private transient SWbemServices cimService;

    /** The Windows Registry management service. */
    private transient WindowsRegistry registry;

    /** Connection user. */
    private String hyperVuser;

    /** Connection password. */
    private String hyperVpassword;

    // Could this be at AbstractCollector? (XenServerIncompatible)
    /** Folder mark perfix. */
    private static String DATASTORE_UUID_MARK = "abq_datastoreuuid_";

    // Could this be at AbstractCollector? (XenServerIncompatible)
    /** Pattern to match with the mark folder. */
    private static String DATASTORE_UUID_MARK_PATTERN = DATASTORE_UUID_MARK + "*";

    @Override
    public void connect(final String user, final String password) throws ConnectionException,
        LoginException
    {
        hyperVuser = user;
        hyperVpassword = password;

        registry = new WindowsRegistry();

        try
        {
            final URL urlAddress = new URL("http://" + getIpAddress());
            final SWbemLocator loc = new SWbemLocator();
            cimService =
                loc.connect(urlAddress.getHost(), "127.0.0.1", HyperVConstants.CIM_NS, hyperVuser,
                    hyperVpassword);
        }
        catch (UnknownHostException e)
        {
            throw new ConnectionException(MessageValues.CONN_EXCP_I, e);
        }
        catch (JIException e)
        {
            // Error code '5' means the access is not allowed.
            if (e.getErrorCode() == 5)
            {
                throw new LoginException(MessageValues.LOG_EXCP, e);
            }
            throw new ConnectionException(MessageValues.CONN_EXCP_I, e);
        }
        catch (MalformedURLException e)
        {
            throw new ConnectionException(MessageValues.CONN_EXCP_I, e);
        }

    }

    @Override
    public void disconnect()
    {
        LOGGER.info("Disconnecting...");

        // The cimService can be null if the previous session was not authenticated.
        if (cimService != null)
        {
            cimService.getLocator().disconnect();
            if (virtService != null)
            {
                virtService.getLocator().disconnect();
            }
        }
    }

    @Override
    public HostDto getHostInfo() throws CollectorException
    {
        LOGGER.info("Getting physical information in HyperV collector...");
        final HostDto hostInfo = new HostDto();
        try
        {
            final List<IJIDispatch> results =
                HyperVUtils.execQuery("Select * from Win32_ComputerSystem", cimService);
            final IJIDispatch dispatch = results.get(0);

            // Must produce only one result final
            JIVariant name = dispatch.get("DNSHostName");
            final JIVariant memory = dispatch.get("TotalPhysicalMemory");
            hostInfo.setName(name.getObjectAsString2());
            hostInfo.setRam(Long.decode(memory.getObjectAsString2()));
            hostInfo.setCpu(getNumberOfCores());
            hostInfo.setHypervisor(getHypervisorType().getValue());
            hostInfo.setVersion(getVersion());
            hostInfo.setInitiatorIQN(getInitiatorIQN(name.getObjectAsString2()));

            // Uncomment if you want to return physical
            // interfaces
            // final List<IJIDispatch> resultsIfaces =
            // HyperVUtils.execQuery("Select * from Win32_NetworkAdapter", cimService);
            // final List<IJIDispatch> resultsIfaces
            // =HyperVUtils.execQuery("Select * from Msvm_ExternalEthernetPort", cimService);
            // hostInfo.getResources().addAll(filterInterfaceList(resultsIfaces));

            URL urlAddress = new URL("http://" + getIpAddress());
            SWbemLocator loc = new SWbemLocator();
            virtService =
                loc.connect(urlAddress.getHost(), "127.0.0.1", HyperVConstants.VIRTUALIZATION_NS,
                    hyperVuser, hyperVpassword);

            hostInfo.getResources().addAll(getHostResources());

            try
            {
                checkPhysicalState();
                hostInfo.setStatus(HostStatusEnumType.MANAGED);
            }
            catch (NoManagedException e)
            {
                hostInfo.setStatus(HostStatusEnumType.NOT_MANAGED);
                hostInfo.setStatusInfo(e.getMessage());
            }
        }
        catch (Exception ex)
        {
            if (ex.getCause() instanceof SmbException)
            {
                LOGGER.error(MessageValues.COLL_EXCP_SMB);
                throw new CollectorException(MessageValues.COLL_EXCP_SMB, ex);
            }
            LOGGER.error(MessageValues.COLL_EXCP_PH);
            throw new CollectorException(MessageValues.COLL_EXCP_PH, ex);
        }
        return hostInfo;
    }

    @Override
    public VirtualSystemCollectionDto getVirtualMachines() throws CollectorException
    {
        LOGGER.info("Getting virtual machine information in HyperV collector...");

        VirtualSystemCollectionDto vms = new VirtualSystemCollectionDto();
        try
        {
            URL urlAddress = new URL("http://" + getIpAddress());
            SWbemLocator loc = new SWbemLocator();
            virtService =
                loc.connect(urlAddress.getHost(), "127.0.0.1", HyperVConstants.VIRTUALIZATION_NS,
                    hyperVuser, hyperVpassword);
            List<IJIDispatch> results =
                HyperVUtils.execQuery(
                    "Select * from Msvm_ComputerSystem where ElementName <> Name", virtService);
            for (IJIDispatch dispatch : results)
            {
                String uuid = dispatch.get("Name").getObjectAsString2();
                String name = dispatch.get("ElementName").getObjectAsString2();
                LOGGER.info("Found virtual machine {}. Getting info...", name);

                int status = dispatch.get("EnabledState").getObjectAsInt();
                VirtualSystemStatusEnumType vmStatus = HyperVUtils.translateState(status);
                if (vmStatus != null)
                {
                    final VirtualSystemDto vm = new VirtualSystemDto();
                    vm.setUuid(uuid);
                    vm.setName(name);
                    vm.getResources().addAll(getVirtualDisks(dispatch));
                    vm.setCpu(getVirtualProcessors(dispatch));
                    vm.setVport(0L);
                    vm.setStatus(vmStatus);
                    long virtualMemory = getVirtualMemory(uuid);
                    if (virtualMemory != -1)
                    {
                        vm.setRam(virtualMemory);
                    }
                    vms.getVirtualSystems().add(vm);
                }
                else
                {
                    LOGGER.info("Ignoring found virtual machine {}. State {} not recognized.",
                        name, status);
                }
            }
        }
        catch (Exception ex)
        {
            throw new CollectorException(MessageValues.COLL_EXCP_VM, ex);
        }
        return vms;
    }

    /**
     * Gets the physical state of the node.
     * 
     * @throws CollectorException When the Node Collector cannot connect to the Hypervisor.
     * @throws NoManagedException if the HyperV is running but not prepared for manage with abicloud
     */

    public void checkPhysicalState() throws CollectorException, NoManagedException
    {
        try
        {
            URL urlAddress = new URL("http://" + getIpAddress());
            SWbemLocator loc = new SWbemLocator();
            final WsmanCollector wsmanColl =
                new WsmanCollectorImpl(hyperVuser, hyperVpassword, 5985);
            wsmanColl.pingWsmanService(urlAddress.getHost());

            virtService =
                loc.connect(urlAddress.getHost(), "127.0.0.1", HyperVConstants.VIRTUALIZATION_NS,
                    hyperVuser, hyperVpassword);
            List<IJIDispatch> results =
                HyperVUtils.execQuery("Select * from Msvm_ComputerSystem where Name = ElementName",
                    virtService);
            if (results == null || results.isEmpty())
            {
                // State is PROVISIONED
                throw new CollectorException(MessageValues.HYP_CONN_EXCP);
            }
            IJIDispatch dispatch = results.get(0);
            int rawState = dispatch.get("EnabledState").getObjectAsInt();
            HyperVState state = HyperVState.fromValue(rawState);
            if (state != HyperVState.POWER_ON)
            {
                // State is PROVISIONED
                throw new CollectorException(MessageValues.HYP_CONN_EXCP);
            }
        }
        catch (JIException ex)
        {
            throw new CollectorException(MessageValues.COLL_EXCP_DC, ex);
        }
        catch (UnknownHostException e)
        {
            throw new CollectorException(MessageValues.HYP_CONN_EXCP, e);
        }
        catch (MalformedURLException e)
        {
            throw new CollectorException(MessageValues.COLL_EXCP_DC, e);
        }
        catch (WsmanException e)
        {
            throw new NoManagedException(e.getMessage(), e);
        }
    }

    /**
     * Get the hypervisor version.
     * 
     * @return The hypervisor version.
     * @throws JIException If version cannot be retrieved.
     */
    private String getVersion() throws JIException
    {
        // Get Operating System object
        List<IJIDispatch> results =
            HyperVUtils.execQuery("Select * from Win32_OperatingSystem", cimService);
        IJIDispatch dispatch = results.get(0);
        // Find System directory and build Virtual Machine Management executable path
        String systemDirectory = dispatch.get("SystemDirectory").getObjectAsString2();
        String vmmsLoc = systemDirectory.replace("\\", "\\\\") + "\\\\vmms.exe";
        // Get Virtual Machine executable file data, to get version property
        results =
            HyperVUtils.execQuery("Select * from CIM_DataFile Where Name = '" + vmmsLoc + "'",
                cimService);
        dispatch = results.get(0);
        return dispatch.get("Version").getObjectAsString2();
    }

    /**
     * Gets the number of cores of the target node.
     * 
     * @return The number of cores of the target node.
     * @throws JIException If the number of cores cannot be retrieved.
     */
    private long getNumberOfCores() throws JIException
    {
        long numCores = 0;
        List<IJIDispatch> results =
            HyperVUtils.execQuery("Select * from Win32_Processor", cimService);
        for (IJIDispatch dispatch : results)
        {
            JIVariant numCoresRaw = dispatch.get("NumberOfCores");
            numCores += numCoresRaw.getObjectAsInt();
        }
        return numCores;
    }

    /**
     * Gets the virtual memory for the specified virtual system.
     * 
     * @param virtualSystemID The ID of the virtual system.
     * @return The amount of virtual memory available to the virtual system.
     * @throws JIException If virtual memory cannot be retrieved.
     */

    private long getVirtualMemory(final String virtualSystemID) throws JIException
    {
        List<IJIDispatch> results =
            HyperVUtils.execQuery("Select * from Msvm_MemorySettingData", virtService);
        for (IJIDispatch dispatch : results)
        {
            String instanceId = dispatch.get("InstanceID").getObjectAsString2();
            if (instanceId.startsWith("Microsoft:" + virtualSystemID))
            {
                // Virtual quantity is stored in MB see AllocationUnits documentation at
                // http://msdn.microsoft.com/en-us/library/cc136856%28VS.85%29.aspx#properties
                String virtualQty = dispatch.get("VirtualQuantity").getObjectAsString2();
                return mbTobyte(Long.parseLong(virtualQty));
            }
        }
        LOGGER.warn("Could not get Virtual Memory for Virtual System: {}", virtualSystemID);
        return -1;
    }

    /**
     * Gets the number of the virtual processors for the specified virtual system.
     * 
     * @param virtualSystem The virtual system.
     * @return The number of virtual processors.
     * @throws JIException If the number of virtual processors cannot be retrieved.
     * @throws CollectorException If the number of virtual processors cannot be retrieved.
     */

    private long getVirtualProcessors(final IJIDispatch virtualSystem) throws JIException,
        CollectorException
    {
        // Get virtual machine settings
        String virtualSystemPath = HyperVUtils.getDispatchPath(virtualSystem);
        IJIDispatch settings = getVirtualSystemSettings(virtualSystemPath, virtService);
        try
        {
            MsvmVirtualSystemManagementService msManService =
                MsvmVirtualSystemManagementService.getManagementService(virtService);
            MsvmVirtualSystemSettingData msSysSetData =
                new MsvmVirtualSystemSettingData(settings, virtService);
            MsvmSummaryInformation summ =
                msManService.getSummaryInformation(
                    new MsvmVirtualSystemSettingData[] {msSysSetData},
                    new Integer[] {HyperVConstants.NUMBER_OF_PROCESSORS_FIELD});
            return summ.getProperties().getItem("NumberOfProcessors").getValueAsLong();
        }
        catch (Exception ex)
        {
            throw new CollectorException("Could not get the number of processors of virtual system: "
                + virtualSystemPath,
                ex);
        }
    }

    /**
     * Gets the virtual disks attached to the virtual machine.
     * 
     * @param virtualSystem The virtual machine.
     * @return The list of virtual disks.
     * @throws JIException If the list of virtual disks cannot be retrieved.
     * @throws CollectorException If disk information cannot be retrieved.
     */
    private List<ResourceType> getVirtualDisks(final IJIDispatch virtualSystem) throws JIException,
        CollectorException
    {
        List<ResourceType> disks = new ArrayList<ResourceType>();
        String virtualSystemPath = HyperVUtils.getDispatchPath(virtualSystem);
        MsvmImageManagementService imageManagementService =
            MsvmImageManagementService.getManagementService(virtService);
        List<IJIDispatch> diskSettings = getDiskSettings(virtualSystemPath, virtService);

        for (IJIDispatch dispatch : diskSettings)
        {

            // Get disk image path
            JIArray connection = dispatch.get("Connection").getObjectAsArray();
            JIVariant[] array = (JIVariant[]) connection.getArrayInstance();
            String imagePath = array[0].getObjectAsString2();
            imagePath = imagePath.replace("\\\\", "\\");
            ResourceType disk = new ResourceType();
            disk.setAddress(imagePath);
            disk.setResourceType(ResourceEnumType.HARD_DISK);
            disk.setConnection(getDatastoreFromFile(imagePath));
            try
            {
                // Must be one element disk.setImagePath(imagePath);
                // Get image size
                String info = imageManagementService.getVirtualHardDiskInfo(imagePath);

                String imageSize = XPathUtils.getValue("//PROPERTY[@NAME='FileSize']/VALUE", info);
                String typeString = XPathUtils.getValue("//PROPERTY[@NAME='Type']/VALUE", info);
                int type = Integer.parseInt(typeString);

                switch (type)
                {
                    case 2:
                        disk.setResourceSubType(VirtualDiskEnumType.VHD_FLAT.value());
                        break;

                    case 3:
                        disk.setResourceSubType(VirtualDiskEnumType.VHD_SPARSE.value());
                        break;

                    case 4:
                        disk.setResourceSubType(VirtualDiskEnumType.INCOMPATIBLE.value());
                        break;

                    default:
                        disk.setResourceSubType(VirtualDiskEnumType.UNKNOWN.value());
                        break;

                }
                disk.setUnits(Long.parseLong(imageSize));

            }
            catch (XPathExpressionException ex)
            {
                throw new CollectorException("Could not get virtual disk size of virtual system: "
                    + virtualSystemPath);
            }
            catch (CollectorException ex)
            {
                // Just print
                LOGGER.error("Could not retrieve virtual disk info from virtual image path "
                    + imagePath + ". Cause:", ex);
                // This defaults values are set in case we are recovering disks that are shared in a
                // cluster shared value
                // TODO Maybe there is a way to get the disk info in a form of Cim_Datafile
                LOGGER.debug("Setting Default size and type values");
                disk.setResourceSubType(VirtualDiskEnumType.VHD_SPARSE.value());
                disk.setUnits(new Long(0));
            }
            disks.add(disk);

        }

        return disks;

    }

    /**
     * Parses the fileName to get the datastore name
     * 
     * @param fileName the file name to parse
     * @return the datastore directory
     */
    private String getDatastoreFromFile(final String fileName)
    {
        int indexEndDirectory = fileName.lastIndexOf('\\');

        // If the images are copied in C:\ avoid to return 'C:' and put
        // the index in the next position
        if (indexEndDirectory == 2)
        {
            indexEndDirectory = 3;
        }

        return fileName.substring(0, indexEndDirectory);
    }

    /**
     * Gets the resource allocation settings for the specified Virtual System.
     * 
     * @param virtualSystemPath The virtual System.
     * @param service The service to use to run the queries.
     * @return The resource allocation settings.
     * @throws JIException If settings cannot be retrieved.
     * @throws CollectorException If settings cannot be retrieved.
     */
    private List<IJIDispatch> getDiskSettings(final String virtualSystemPath,
        final SWbemServices service) throws JIException, CollectorException
    {
        // Get virtual machine settings
        IJIDispatch settings = getVirtualSystemSettings(virtualSystemPath, virtService);
        String settingPath = HyperVUtils.getDispatchPath(settings);

        // Get virtual machine settings
        String settingsQuery =
            "Associators of {" + settingPath + "} Where "
                + "AssocClass = Msvm_VirtualSystemSettingDataComponent "
                + "ResultClass = Msvm_ResourceAllocationSettingData";
        List<IJIDispatch> results = HyperVUtils.execQuery(settingsQuery, service);
        // Filter virtual hard disks
        for (Iterator<IJIDispatch> it = results.iterator(); it.hasNext();)
        {
            if (!HyperVUtils.isVirtualHardDisk(it.next()))
            {
                it.remove();
            }
        }
        return results;
    }

    /**
     * Gets the settings for the specified Virtual System.
     * 
     * @param virtualSystemPath The virtual System.
     * @param service The service to use to run the queries.
     * @return The virtual system settings.
     * @throws JIException If settings cannot be retrieved.
     * @throws CollectorException If settings cannot be retrieved.
     */

    private IJIDispatch getVirtualSystemSettings(final String virtualSystemPath,
        final SWbemServices service) throws JIException, CollectorException
    {
        // Get virtual machine settings
        String settingsQuery =
            "Associators of {" + virtualSystemPath + "} Where "
                + "AssocClass = Msvm_SettingsDefineState "
                + "ResultClass = Msvm_VirtualSystemSettingData";

        List<IJIDispatch> results = HyperVUtils.execQuery(settingsQuery, service);
        if (results == null || results.isEmpty())
        {
            throw new CollectorException("Could not get Virtual System settings of virtual system: "
                + virtualSystemPath);
        }
        return results.get(0);
    }

    /**
     * Converts MB to bytes.
     * 
     * @param mbytes The MB to convert.
     * @return The converted result.
     */

    private static long mbTobyte(final long mbytes)
    {
        final int MB_TO_BYTE = 1048576;
        return mbytes * MB_TO_BYTE;
    }

    /**
     * Retrieves the list of resources of the host.
     * 
     * @throws CollectorException for collector exceptions.
     * @throws JIException thrown by any JI* object.
     * @return the list of ResourceType objects.
     */
    private List<ResourceType> getHostResources() throws JIException, CollectorException
    {
        // Returning vswitch list
        List<ResourceType> resources = new ArrayList<ResourceType>();
        resources.addAll(filterNetworkList());
        resources.addAll(getPhysicalDisks());
        resources.addAll(getMappedLogicalDisks());

        return resources;
    }

    /**
     * Private helper to filter the network list (vSwitch) list.
     * 
     * @return the resource list
     * @throws JIException thrown by any JI* Object.
     */
    private List<ResourceType> filterNetworkList() throws JIException
    {
        final List<IJIDispatch> resultsVswitchs =
            HyperVUtils.execQuery("Select * from Msvm_VirtualSwitch", virtService);
        String macs = getMacs();
        List<ResourceType> filteredNetworks = new ArrayList<ResourceType>();
        for (IJIDispatch networkDispatch : resultsVswitchs)
        {
            String networkName = networkDispatch.get("ElementName").getObjectAsString2();
            ResourceType resource = new ResourceType();
            resource.setAddress(macs);
            resource.setElementName(networkName);
            resource.setResourceType(ResourceEnumType.NETWORK_INTERFACE);
            filteredNetworks.add(resource);

        }

        Collections.sort(filteredNetworks, new ResourceComparator());

        return filteredNetworks;
    }

    /**
     * Retrieve the mac and add them as a resources. Ignore interfaces with no address. If no macs
     * then return an empty String. Don't care about except. Just append mac \ mac \ ...
     * 
     * @param networks all win32 interfaces.
     * @return all interfaces as a physical resources. mac1\mac2\...
     */
    private String getMacs()
    {
        List<IJIDispatch> networks;
        StringBuilder macs = new StringBuilder("");
        try
        {
            networks = HyperVUtils.execQuery("Select * from Win32_NetworkAdapter", cimService);
            for (IJIDispatch resourceDispatch : networks)
            {
                try
                {
                    String rawMac = resourceDispatch.get("MACAddress").getObjectAsString2();
                    if (StringUtils.isBlank(rawMac))
                    {
                        continue;
                    }
                    macs.append(rawMac).append("\\");
                }
                catch (Exception ex)
                {
                    LOGGER.debug("This interface has no mac");
                }
            }
        }
        catch (Exception ex)
        {
            LOGGER.debug("There are no interface with mac");
        }
        return macs.length() > 1 ? macs.substring(0, macs.length() - 1) : macs.toString();

    }

    /**
     * Gets the physical disk list of the host.
     * 
     * @return The physical disk list
     * @throws JIException If disk size cannot be retrieved.
     * @throws CollectorException If Physical drive cannot be retrieved.
     */
    private List<ResourceType> getPhysicalDisks() throws JIException, CollectorException
    {
        List<IJIDispatch> results =
            HyperVUtils.execQuery("Select * from Win32_LogicalDisk", cimService);

        if (results == null || results.isEmpty())
        {
            throw new CollectorException("Could not get physical drive for virtual machine disks");
        }

        List<ResourceType> disksResources = new ArrayList<ResourceType>();
        for (IJIDispatch logicalDiskDispatch : results)
        {
            int driveType = logicalDiskDispatch.get("DriveType").getObjectAsInt();
            // Ignoring removable disks (2), compact disks(5) and mappedlogicaldrives(4)
            if (driveType == 2 || driveType == 5 || driveType == 4)
            {
                continue;
            }
            String logicalDiskName = logicalDiskDispatch.get("DeviceID").getObjectAsString2();
            String size = logicalDiskDispatch.get("Size").getObjectAsString2();
            String availableSize = logicalDiskDispatch.get("FreeSpace").getObjectAsString2();
            // String datastoreUuidMark = getDatastoreUuidMark(logicalDiskName);

            ResourceType resource = new ResourceType();
            resource.setAddress(logicalDiskName + "\\");
            resource.setElementName(logicalDiskName);
            resource.setResourceType(ResourceEnumType.HARD_DISK);
            resource.setUnits(Long.valueOf(size));
            resource.setAvailableUnits(Long.valueOf(availableSize));
            // resource.setConnection(datastoreUuidMark);
            disksResources.add(resource);

        }

        Collections.sort(disksResources, new ResourceComparator());

        return disksResources;
    }

    /**
     * Gets Mapped network drives in Windows machines: these are the only candidates to be
     * considered as shared datastores by Abiquo Datastores must be created in Win32 host machines
     * NOT associated with any user session.
     * 
     * @return
     * @throws JIException
     * @throws CollectorException
     */
    private List<ResourceType> getMappedLogicalDisks() throws JIException, CollectorException
    {
        List<IJIDispatch> results =
            HyperVUtils.execQuery("Select * from Win32_MappedLogicalDisk", cimService);

        // This query can return repeated Win32_MappedLogicalDisk instances, we should remove them
        // This is caused by Disks being associated to more than one SessionId
        List<String> deviceIds = new ArrayList<String>();

        List<ResourceType> disksResources = new ArrayList<ResourceType>();
        for (IJIDispatch logicalDiskDispatch : results)
        {
            String logicalDiskName = logicalDiskDispatch.get("DeviceID").getObjectAsString2();
            if (!deviceIds.contains(logicalDiskName))
            { // Repeated Win32_MappedLogicalDisk instances are not included
                String size = logicalDiskDispatch.get("Size").getObjectAsString2();
                String availableSize = logicalDiskDispatch.get("FreeSpace").getObjectAsString2();
                String datastoreUuidMark = getDatastoreUuidMark(logicalDiskName);

                ResourceType resource = new ResourceType();
                resource.setAddress(logicalDiskName + "\\");
                resource.setElementName(logicalDiskName);
                resource.setResourceType(ResourceEnumType.HARD_DISK);
                resource.setUnits(Long.valueOf(size));
                resource.setAvailableUnits(Long.valueOf(availableSize));
                resource.setConnection(datastoreUuidMark);
                disksResources.add(resource);

                deviceIds.add(logicalDiskName);
            }

        }

        Collections.sort(disksResources, new ResourceComparator());

        return disksResources;
    }

    /**
     * Gets the iSCSI Initiator IQN of the host.
     * 
     * @param hostName The name of the host
     * @return The iSCSI Initiator IQN of the host.
     * @throws Exception if an error occurs.
     */
    private String getInitiatorIQN(final String hostName) throws Exception
    {
        String iqn = null;

        // First check if the IQN has been set manually.
        registry.connect(getIpAddress(), hyperVuser, hyperVpassword);

        try
        {
            iqn =
                registry
                    .getKeyValue(WindowsRegistry.Keys.HKEY_LOCAL_MACHINE,
                        HyperVConstants.INITIATOR_REGISTRY_PATH,
                        HyperVConstants.INITIATOR_REGISTRY_KEY);
        }
        finally
        {
            registry.disconnect();
        }

        // If the key was not found, then use the default IQN
        if (iqn == null)
        {
            iqn = HyperVConstants.DEFAULT_INITIATOR_NAME_PREFIX + hostName.toLowerCase();
        }

        return iqn;
    }

    /**
     * Locate or create the datastore folder mark to determine if the datastore is being shared
     * across hypervisors.
     * 
     * @return a Datastore UUID
     * @throws CollectorException
     */
    private String getDatastoreUuidMark(final String mappedDrive) // , HostDatastoreBrowser
                                                                  // dsBrowser,
        // Datacenter dc)
        throws CollectorException
    {
        String dsUUID = null;

        // Preparing the query
        String query =
            "SELECT * FROM CIM_DataFile WHERE FileName LIKE '" + DATASTORE_UUID_MARK
                + "%' AND Drive = '" + mappedDrive + "'";

        JIVariant[] res;
        try
        {
            res =
                cimService.getObjectDispatcher().callMethodA("ExecQuery",
                    new Object[] {new JIString(query)});
            JIVariant[][] fileSet = HyperVUtils.enumToJIVariantArray(res);
            if (fileSet.length == 1)
            {
                IJIDispatch fileDispatch =
                    (IJIDispatch) JIObjectFactory.narrowObject(fileSet[0][0].getObjectAsComObject()
                        .queryInterface(IJIDispatch.IID));
                dsUUID = fileDispatch.get("FileName").getObjectAsString2();
                // throw new Exception("Cannot identify the vhd to delete: " + file);
            }
            else if (fileSet.length > 1)
            {
                throw new CollectorException(MessageValues.DATASTRORE_MULTIPLE_MARKS);
            }
        }
        catch (JIException e)
        {
            LOGGER.error("Can not locate the folder mark at [{}]\n{}", e);
            throw new CollectorException(MessageValues.DATASTRORE_MARK, e);
        }

        if (dsUUID == null)
        {
            dsUUID = createDatastoreFolderMark(mappedDrive);
        }
        return dsUUID;
    }

    private String createDatastoreFolderMark(final String mappedDrive) throws CollectorException
    {
        String folderUuidMark = UUID.randomUUID().toString();
        String directoryOnDatastore =
            String.format("%s\\%s%s", mappedDrive, DATASTORE_UUID_MARK, folderUuidMark);
        // Should be something like Z:\\abq.datastoreuuid.343423429

        try
        {

            IJIDispatch instanceClass =
                (IJIDispatch) JIObjectFactory.narrowObject(cimService.getObjectDispatcher()
                    .callMethodA("Get", new Object[] {new JIString("Win32_Process")})[0]
                    .getObjectAsComObject().queryInterface(IJIDispatch.IID));
            // Win32_Process do not need to be instanced (SpawnInstance_)

            Win32Process proc = new Win32Process(instanceClass, cimService);
            // proc.create("cmd.exe /C mkdir " + folder);

            proc.create("cmd.exe /C echo emptydata > " + directoryOnDatastore);

            // do not create parent folders (is on the root)
            // serviceInstance.getFileManager().makeDirectory(directoryOnDatastore, dc, false);
        }
        catch (Exception e)
        {
            LOGGER.error("Can not create the folder mark at [{}]\n{}", mappedDrive, e);
            throw new CollectorException(MessageValues.DATASTRORE_MARK, e);
        }

        return folderUuidMark;
    }

}
