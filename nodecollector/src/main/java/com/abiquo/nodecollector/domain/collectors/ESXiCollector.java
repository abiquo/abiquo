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

import java.lang.reflect.Method;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.abiquo.model.enumerator.HypervisorType;
import com.abiquo.nodecollector.constants.MessageValues;
import com.abiquo.nodecollector.domain.Collector;
import com.abiquo.nodecollector.exception.CollectorException;
import com.abiquo.nodecollector.exception.ConnectionException;
import com.abiquo.nodecollector.exception.LoginException;
import com.abiquo.nodecollector.exception.NoManagedException;
import com.abiquo.nodecollector.utils.ResourceComparator;
import com.abiquo.server.core.infrastructure.nodecollector.HostDto;
import com.abiquo.server.core.infrastructure.nodecollector.HostStatusEnumType;
import com.abiquo.server.core.infrastructure.nodecollector.ResourceEnumType;
import com.abiquo.server.core.infrastructure.nodecollector.ResourceType;
import com.abiquo.server.core.infrastructure.nodecollector.VirtualDiskEnumType;
import com.abiquo.server.core.infrastructure.nodecollector.VirtualSystemCollectionDto;
import com.abiquo.server.core.infrastructure.nodecollector.VirtualSystemDto;
import com.abiquo.server.core.infrastructure.nodecollector.VirtualSystemStatusEnumType;
import com.vmware.vim25.ArrayOfHostHostBusAdapter;
import com.vmware.vim25.DatastoreSummary;
import com.vmware.vim25.DynamicProperty;
import com.vmware.vim25.FileFault;
import com.vmware.vim25.FileInfo;
import com.vmware.vim25.FileQuery;
import com.vmware.vim25.FolderFileQuery;
import com.vmware.vim25.HostConfigInfo;
import com.vmware.vim25.HostConnectInfo;
import com.vmware.vim25.HostDatastoreBrowserSearchResults;
import com.vmware.vim25.HostDatastoreBrowserSearchSpec;
import com.vmware.vim25.HostDatastoreConnectInfo;
import com.vmware.vim25.HostHardwareInfo;
import com.vmware.vim25.HostHostBusAdapter;
import com.vmware.vim25.HostInternetScsiHba;
import com.vmware.vim25.HostNetworkInfo;
import com.vmware.vim25.HostProxySwitch;
import com.vmware.vim25.HostVirtualSwitch;
import com.vmware.vim25.InvalidLogin;
import com.vmware.vim25.KeyAnyValue;
import com.vmware.vim25.LicenseManagerLicenseInfo;
import com.vmware.vim25.ManagedObjectReference;
import com.vmware.vim25.ObjectContent;
import com.vmware.vim25.ObjectSpec;
import com.vmware.vim25.OptionValue;
import com.vmware.vim25.PhysicalNic;
import com.vmware.vim25.PropertyFilterSpec;
import com.vmware.vim25.PropertySpec;
import com.vmware.vim25.RuntimeFault;
import com.vmware.vim25.SelectionSpec;
import com.vmware.vim25.ServiceContent;
import com.vmware.vim25.TaskInfoState;
import com.vmware.vim25.TraversalSpec;
import com.vmware.vim25.VimPortType;
import com.vmware.vim25.VirtualDevice;
import com.vmware.vim25.VirtualDeviceFileBackingInfo;
import com.vmware.vim25.VirtualDisk;
import com.vmware.vim25.VirtualDiskFlatVer2BackingInfo;
import com.vmware.vim25.VirtualDiskRawDiskMappingVer1BackingInfo;
import com.vmware.vim25.VirtualDiskRawDiskVer2BackingInfo;
import com.vmware.vim25.VirtualDiskSparseVer2BackingInfo;
import com.vmware.vim25.VirtualMachineConfigInfo;
import com.vmware.vim25.VirtualMachineRuntimeInfo;
import com.vmware.vim25.mo.Datacenter;
import com.vmware.vim25.mo.HostDatastoreBrowser;
import com.vmware.vim25.mo.HostSystem;
import com.vmware.vim25.mo.InventoryNavigator;
import com.vmware.vim25.mo.ManagedEntity;
import com.vmware.vim25.mo.ServiceInstance;
import com.vmware.vim25.mo.Task;

/**
 * Collector implementation for ESXi hypervisors.
 * 
 * @author jdevesa@abiquo.com
 */
@Collector(type = HypervisorType.VMX_04, order = 0)
public class ESXiCollector extends AbstractCollector
{

    private static final Integer TWELVE = 12;

    private static final Integer THIRTEEN = 13;

    private static final String POWERED_OFF = "poweredOff";

    private static final String POWERED_ON = "poweredOn";

    private static final Integer KBYTE = 1024;

    private static final Integer MEGABYTE = 1048576;

    /** The logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ESXiCollector.class);

    /** Folder mark perfix. */
    private static String DATASTORE_UUID_MARK = "datastoreuuid.";

    /** Pattern to match with the mark folder. */
    private static String DATASTORE_UUID_MARK_PATTERN = DATASTORE_UUID_MARK + "*";

    /**
     * The api version of the Hypervisor.
     */
    private String apiVersion;

    private ManagedObjectReference rootFolder;

    private static SelectionSpec[] selectionSpecs;

    private static ManagedObjectReference siMoref;

    private static PropertySpec[] hostSystemSpec;

    private static PropertySpec[] virtualMachineSpec;

    private ObjectSpec[] rootObjSpecs;

    private ServiceInstance serviceInstance;

    static
    {
        // Create an instance of the ServiceInstance managed object
        siMoref = new ManagedObjectReference();
        siMoref.setType("ServiceInstance");
        siMoref.set_value("ServiceInstance");

        // Set the transactionSpec route to Host. We build the whole inventory tree
        // to search any 'managedObjectType'
        selectionSpecs = buildFullTraversal();

        // Set the property spec to specify which object I want to retrieve
        hostSystemSpec = new PropertySpec[] {new PropertySpec()};
        hostSystemSpec[0].setType("HostSystem");
        hostSystemSpec[0].setAll(true);

        virtualMachineSpec = new PropertySpec[] {new PropertySpec()};
        virtualMachineSpec[0].setType("VirtualMachine");
        virtualMachineSpec[0].setAll(true);
    }

    /**
     * Default constructor.
     * 
     * @param ipAddress address where to locate the Hypervisor.
     */
    public ESXiCollector()
    {
        // ignore certs
        System.setProperty("org.apache.axis.components.net.SecureSocketFactory",
            "org.apache.axis.components.net.SunFakeTrustSocketFactory");
    }

    // TODO DELME
    public static void main(final String[] args) throws Exception
    {
        ESXiCollector coll = new ESXiCollector();
        coll.setIpAddress("10.60.1.120");
        coll.connect("root", "temporal");
        coll.getHostInfo();
    }

    @Override
    public void connect(final String user, final String password) throws ConnectionException,
        LoginException
    {
        try
        {
            serviceInstance =
                new ServiceInstance(new URL("https://" + getIpAddress() + "/sdk"),
                    user,
                    password,
                    true);

        }
        catch (InvalidLogin e)
        {
            LOGGER.warn("Invalid credentials for hypervisor {} at cloud node ", this
                .getHypervisorType().name(), getIpAddress());
            throw new LoginException(MessageValues.LOG_EXCP, e);
        }
        catch (Exception e)
        {
            LOGGER.warn("Could not connect at hypervisor {} at cloud node {}", this
                .getHypervisorType().name(), getIpAddress());
            throw new ConnectionException(MessageValues.CONN_EXCP_I, e);
        }
    }

    @Override
    public void disconnect() throws CollectorException
    {
        if (serviceInstance != null && serviceInstance.getServerConnection() != null)
        {
            serviceInstance.getServerConnection().logout();
        }
    }

    /**
     * Retrieve a single object.
     * 
     * @param mor Managed Object Reference to get contents for
     * @param propertyName of the object to retrieve
     * @return retrieved object
     * @throws Exception any encapsulated exception.
     */
    public Object getDynamicProperty(final ManagedObjectReference mor, final String propertyName)
        throws Exception
    {
        ObjectContent[] objContent = getObjectProperties(null, mor, new String[] {propertyName});

        Object propertyValue = null;
        if (objContent != null)
        {
            DynamicProperty[] dynamicProperty = objContent[0].getPropSet();
            if (dynamicProperty != null)
            {
                // Check the dynamic propery for ArrayOfXXX object
                Object dynamicPropertyVal = dynamicProperty[0].getVal();
                String dynamicPropertyName = dynamicPropertyVal.getClass().getName();
                if (dynamicPropertyName.indexOf("ArrayOf") != -1)
                {
                    String methodName =
                        dynamicPropertyName.substring(dynamicPropertyName.indexOf("ArrayOf")
                            + "ArrayOf".length(), dynamicPropertyName.length());
                    /*
                     * If object is ArrayOfX object, then get the X by invoking getX() on the
                     * object. For Ex: ArrayOfManagedObjectReference.getManagedObjectReference()
                     * returns ManagedObjectReference[] array.
                     */
                    if (methodExists(dynamicPropertyVal, "get" + methodName, null))
                    {
                        methodName = "get" + methodName;
                    }
                    else
                    {
                        /*
                         * Construct methodName for ArrayOf primitive types Ex: For ArrayOfInt,
                         * methodName is get_int
                         */
                        methodName = "get_" + methodName.toLowerCase();
                    }
                    Method getMorMethod =
                        dynamicPropertyVal.getClass().getDeclaredMethod(methodName, (Class[]) null);
                    propertyValue = getMorMethod.invoke(dynamicPropertyVal, (Object[]) null);
                }
                else
                {
                    propertyValue = dynamicPropertyVal;
                }
            }
        }
        return propertyValue;
    }

    @Override
    public HostDto getHostInfo() throws CollectorException
    {

        final HostHardwareInfo hardwareInfo;
        final HostDto physicalInfo = new HostDto();
        // Check the license of the ESXi
        try
        {
            hasValidLicense();
        }
        catch (NoManagedException e)
        {
            physicalInfo.setStatus(HostStatusEnumType.NOT_MANAGED);
            physicalInfo.setStatusInfo(e.getMessage());
            return physicalInfo;
        }

        // We take the first one because we use ESXi not VirtualCenter, and there is only one host
        // managed
        ObjectContent hostSystem;
        try
        {
            hostSystem = getManagedObjectReferencesFromInventory(hostSystemSpec)[0];
        }
        catch (RemoteException e)
        {
            LOGGER.error("Unexpected exception:", e);
            throw new CollectorException(MessageValues.COLL_EXCP_PH, e);
        }

        // Please check the following url to understand the DynamicProperty indexation in this code
        // http://www.vmware.com/support/developer/vc-sdk/visdk25pubs/ReferenceGuide/
        // The order of the properties in the web, are the same here.
        hardwareInfo = (HostHardwareInfo) hostSystem.getPropSet()[TWELVE].getVal();

        physicalInfo.setName((String) hostSystem.getPropSet()[THIRTEEN].getVal());
        physicalInfo.setCpu(Long.valueOf(((Short) hardwareInfo.getCpuInfo().getNumCpuCores())
            .toString()));
        physicalInfo.setRam(hardwareInfo.getMemorySize());
        physicalInfo.setHypervisor(getHypervisorType().getValue());
        physicalInfo.setVersion(getApiVersion());
        physicalInfo.getResources().addAll(getHostResources(hostSystem));

        try
        {
            ManagedObjectReference storageSystemMor = getStorageSystem(hostSystem);

            final String initiatorIQN = getInternetSCSIInitiatorIQN(hostSystem, storageSystemMor);

            physicalInfo.setInitiatorIQN(initiatorIQN);
        }
        catch (CollectorException e)
        {
            // XXX add the cause ??
            LOGGER.warn(MessageValues.WARN_INITIATOR_IQN);
        }

        physicalInfo.setStatus(HostStatusEnumType.MANAGED);

        return physicalInfo;
    }

    /**
     * @return the myConn
     */
    public VimPortType getMyConn()
    {
        return serviceInstance.getServerConnection().getVimService();
    }

    /**
     * Retrieve contents for a single object based on the property collector registered with the
     * service.
     * 
     * @param collector Property collector registered with service.
     * @param mobj Managed Object Reference to get contents for.
     * @param properties names of properties of object to retrieve.
     * @return retrieved object contents.
     * @throws Exception for any encapsulated exception.
     */
    public ObjectContent[] getObjectProperties(final ManagedObjectReference collector,
        final ManagedObjectReference mobj, final String[] properties) throws Exception
    {
        if (mobj == null)
        {
            return null;
        }

        ManagedObjectReference usecoll = collector;
        if (usecoll == null)
        {
            usecoll = getServiceContent().getPropertyCollector();
        }

        PropertyFilterSpec filter = new PropertyFilterSpec();

        PropertySpec propertySpec = new PropertySpec();
        propertySpec.setAll(Boolean.valueOf(properties == null || properties.length == 0));
        propertySpec.setType(mobj.getType());
        propertySpec.setPathSet(properties);

        filter.setPropSet(new PropertySpec[] {propertySpec});

        ObjectSpec objectSpec = new ObjectSpec();
        objectSpec.setObj(mobj);
        objectSpec.setSkip(Boolean.FALSE);
        filter.setObjectSet(new ObjectSpec[] {objectSpec});

        return getMyConn().retrieveProperties(usecoll, new PropertyFilterSpec[] {filter});
    }

    /**
     * @return the serviceContent
     */
    public ServiceContent getServiceContent()
    {
        return serviceInstance.getServiceContent();
    }

    private ManagedObjectReference getRootFolder()
    {
        if (rootFolder == null)
        {
            rootFolder = getServiceContent().getRootFolder();
        }
        return rootFolder;
    }

    @Override
    public VirtualSystemCollectionDto getVirtualMachines() throws CollectorException
    {

        final VirtualSystemCollectionDto virtualMachines = new VirtualSystemCollectionDto();

        ObjectContent[] esxiMachines;
        try
        {
            esxiMachines = getManagedObjectReferencesFromInventory(virtualMachineSpec);
        }
        catch (RemoteException e)
        {
            LOGGER.error("Unexpected exception:", e);
            throw new CollectorException(MessageValues.COLL_EXCP_VM, e);
        }

        if (esxiMachines != null)
        {
            for (ObjectContent esxiMachine : esxiMachines)
            {
                String machineName = null;

                try
                {
                    // get the machine name for logging.
                    machineName = (String) getDynamicProperty(esxiMachine, "name");
                    // Get the virtual machine configuration
                    Object obj = getDynamicProperty(esxiMachine, "config");

                    if (obj != null)
                    {
                        VirtualMachineConfigInfo vmConfig = (VirtualMachineConfigInfo) obj;

                        VirtualSystemDto vSys = new VirtualSystemDto();
                        vSys.setName(vmConfig.getName());
                        vSys.setStatus(getStateFromESXiMachine(esxiMachine));
                        vSys.setUuid(vmConfig.getUuid());
                        vSys.setCpu(Long.valueOf(vmConfig.getHardware().getNumCPU()));
                        vSys.setRam(Long.valueOf(vmConfig.getHardware().getMemoryMB()) * MEGABYTE);
                        vSys.setVport(getVPortFromExtraConfig(vmConfig.getExtraConfig()));

                        // Recover the list of disks for each virtual system
                        for (VirtualDevice device : vmConfig.getHardware().getDevice())
                        {
                            if (device instanceof VirtualDisk)
                            {
                                vSys.getResources().add(createDiskFromVirtualDevice(device));
                            }
                        }

                        virtualMachines.getVirtualSystems().add(vSys);
                    }
                    else
                    {
                        LOGGER.warn("Could not retrieve virtual machine " + machineName
                            + " because it is inaccessible");
                    }
                }
                catch (Exception e)
                {
                    LOGGER.error("Could not retrieve virtual machine " + machineName
                        + " information. Cause: ", e);
                }
            }
        }

        return virtualMachines;
    }

    /**
     * @param apiVersion the apiVersion to set
     */
    public void setApiVersion(final String apiVersion)
    {
        this.apiVersion = apiVersion;
    }

    /**
     * Determines of a method 'methodName' exists for the Object 'obj'.
     * 
     * @param obj The Object to check
     * @param methodName The method name
     * @param parameterTypes Array of Class objects for the parameter types
     * @return true if the method exists, false otherwise
     */
    boolean methodExists(final Object obj, final String methodName,
        final Class< ? >[] parameterTypes)
    {
        boolean exists = false;
        try
        {
            Method method = obj.getClass().getMethod(methodName, parameterTypes);
            if (method != null)
            {
                exists = true;
            }
        }
        catch (Exception e)
        {
            return exists;
        }
        return exists;
    }

    /**
     * This method creates a SelectionSpec[] to traverses the entire inventory tree starting at a
     * Folder.
     * 
     * @return The SelectionSpec[]
     */
    private static SelectionSpec[] buildFullTraversal()
    {

        SelectionSpec rpToVmSpec = createSelectionSpec("rpToVm");
        SelectionSpec rpToRpSpec = createSelectionSpec("rpToRp");

        // ResourcePool to itself
        TraversalSpec rpToRp =
            createTraversalSpec("rpToRp", "ResourcePool", "resourcePool", rpToRpSpec, rpToVmSpec);

        // ResourcePool to Vm
        TraversalSpec rpToVm =
            createTraversalSpec("rpToVm", "ResourcePool", "vm", new SelectionSpec[] {});

        // ComputerResource to ResourcePool
        TraversalSpec crToRp =
            createTraversalSpec("crToRp", "ComputeResource", "resourcePool", rpToRpSpec, rpToVmSpec);

        // ComputerResource to Host
        TraversalSpec crToH =
            createTraversalSpec("crToH", "ComputeResource", "host", new SelectionSpec[] {});

        SelectionSpec visitFoldersSpec = createSelectionSpec("visitFolders");

        // Datacenter to hostFolder
        TraversalSpec dcToHf =
            createTraversalSpec("dcToHf", "Datacenter", "hostFolder", visitFoldersSpec);

        // Datacenter to vm Folder
        TraversalSpec dcToVmf =
            createTraversalSpec("dcToVmf", "Datacenter", "vmFolder", visitFoldersSpec);

        // Host to Vm
        TraversalSpec hToVm = createTraversalSpec("HToVm", "HostSystem", "vm", visitFoldersSpec);

        // Root folder to others
        TraversalSpec visitFolders =
            createTraversalSpec("visitFolders", "Folder", "childEntity", visitFoldersSpec,
                createSelectionSpec("dcToHf"), createSelectionSpec("dcToVmf"),
                createSelectionSpec("crToH"), createSelectionSpec("crToRp"),
                createSelectionSpec("HToVm"), rpToVmSpec);

        return new SelectionSpec[] {visitFolders, dcToVmf, dcToHf, crToH, crToRp, rpToRp, hToVm,
        rpToVm};
    }

    private static SelectionSpec createSelectionSpec(final String name)
    {
        SelectionSpec s = new SelectionSpec();
        s.setName(name);
        return s;
    }

    private static TraversalSpec createTraversalSpec(final String name, final String type,
        final String path, final SelectionSpec... selectSet)
    {
        TraversalSpec traversalSpec = new TraversalSpec();
        traversalSpec.setName(name);
        traversalSpec.setType(type);
        traversalSpec.setPath(path);
        traversalSpec.setSkip(false);
        traversalSpec.setSelectSet(selectSet);

        return traversalSpec;
    }

    /**
     * Create a {@link Disk} object from a virtual device.
     * 
     * @param device device of remote virtual system
     * @return a built object.
     * @throws CollectorException
     * @throws Exception
     */
    private ResourceType createDiskFromVirtualDevice(final VirtualDevice device)
        throws CollectorException
    {

        try
        {
            VirtualDisk diskDevice = (VirtualDisk) device;
            ResourceType hardDisk = new ResourceType();
            hardDisk.setUnits(diskDevice.getCapacityInKB() * KBYTE);
            hardDisk.setResourceType(ResourceEnumType.STORAGE_DISK);

            if (diskDevice.getBacking() instanceof VirtualDeviceFileBackingInfo)
            {
                VirtualDeviceFileBackingInfo backing =
                    (VirtualDeviceFileBackingInfo) diskDevice.getBacking();
                String fileName = backing.getFileName();
                VirtualDiskEnumType vdet = resolveDiskFileType(backing);

                hardDisk.setResourceSubType(vdet.value());
                if (vdet == VirtualDiskEnumType.VMDK_FLAT)
                {
                    hardDisk.setAddress(parseImagePathAddingFlat(fileName));
                }
                else
                {
                    hardDisk.setAddress(fileName);
                }
                ManagedObjectReference datastore = backing.getDatastore();
                String datastoreName = (String) getDynamicProperty(datastore, "name");
                hardDisk.setConnection(datastoreName);

            }
            // The disk belong to a device. The correct statement here would be
            // 'diskDevice.getBacking() instanceof VirtualDeviceDeviceBackingInfo' because
            // in the hierarchy has
            // the same level of VirtualDeviceFileBackingInfo, but the only subclass of
            // VirtualDevicefileBackingInfo we can treat is
            // VirtualDiskRawDiskVer2BackingInfo. So, in order to
            // have a clearer code, we put it directly.
            // In the future, if we can recover disk information from Floppy or CDRoom, we
            // should change this
            // condition
            else if (diskDevice.getBacking() instanceof VirtualDiskRawDiskVer2BackingInfo)
            {
                VirtualDiskRawDiskVer2BackingInfo backing =
                    (VirtualDiskRawDiskVer2BackingInfo) diskDevice.getBacking();
                hardDisk.setAddress(backing.getDescriptorFileName());
                hardDisk.setResourceSubType(VirtualDiskEnumType.RAW);
                hardDisk.setConnection("unknown");
            }
            // Other disk types not used.
            else
            {
                hardDisk.setAddress("unknown");
                hardDisk.setResourceSubType(VirtualDiskEnumType.VMDK_FLAT);
                hardDisk.setConnection("unknown");
            }

            return hardDisk;
        }
        catch (Exception e)
        {
            throw new CollectorException(MessageValues.COLL_EXCP_VM, e);
        }
    }

    /**
     * @return the api version of the esxi hypervisor.
     */
    private String getApiVersion()
    {
        return apiVersion;
    }

    /**
     * Retrieves the list of resources of the host.
     * 
     * @param hostSystem {@link ObjectContent} object. It is enough to reach all the information we
     *            need.
     * @param repositoryLocation the repository location is the Abiquo image repository and we don't
     *            want to retrieve it.
     * @return a list of Resources
     * @throws CollectorException
     * @throws RemoteException
     * @throws RuntimeFault
     */
    private List<ResourceType> getHostResources(final ObjectContent hostSystem)
        throws CollectorException
    {
        List<ResourceType> resources = new ArrayList<ResourceType>();
        HostConfigInfo config = (HostConfigInfo) hostSystem.getPropSet()[2].getVal();
        String repositoryLocation =
            System.getProperty("abiquo.appliancemanager.repositoryLocation");

        // Network resouces
        HostNetworkInfo network = config.getNetwork();
        if (network.getVswitch() != null)
        {
            for (HostVirtualSwitch vswitch : network.getVswitch())
            {
                if (vswitch.pnic != null)
                {
                    ResourceType resource = new ResourceType();
                    resource.setResourceType(ResourceEnumType.NETWORK_INTERFACE);
                    String[] pnics = vswitch.getPnic();
                    String address = "";
                    if (pnics.length > 0)
                    {
                        for (PhysicalNic nic : network.getPnic())
                        {
                            if (nic.getKey().equalsIgnoreCase(pnics[0]))
                            {
                                address = nic.getMac();
                            }
                        }
                    }
                    resource.setAddress(address);
                    resource.setElementName(vswitch.getName());
                    resources.add(resource);
                }
            }
        }

        // If the dvs is enabled, retrieve the list of Distributed Virtual Switch
        Boolean dvsEnabled = Boolean.valueOf(System.getProperty("abiquo.dvs.enabled"));
        if (dvsEnabled && network.getProxySwitch() != null)
        {
            for (HostProxySwitch dvswitch : network.getProxySwitch())
            {
                // Add it only if it has any physical NIC attached and its name starts with 'dvs'
                if (dvswitch.dvsName.toLowerCase().startsWith("dvs") && dvswitch.pnic != null)
                {
                    ResourceType resource = new ResourceType();
                    resource.setResourceType(ResourceEnumType.NETWORK_INTERFACE);
                    String[] pnics = dvswitch.getPnic();
                    String address = "";
                    if (pnics.length > 0)
                    {
                        for (PhysicalNic nic : network.getPnic())
                        {
                            if (nic.getKey().equalsIgnoreCase(pnics[0]))
                            {
                                address = nic.getMac();
                            }
                        }
                    }
                    resource.setAddress(address);
                    resource.setElementName(dvswitch.getDvsName());
                    resources.add(resource);
                }
            }
        }

        // Datastores resources
        HostConnectInfo hostInfo;
        HostDatastoreBrowser dsBrowser;
        Datacenter dc;

        try
        {
            hostInfo = getMyConn().queryHostConnectionInfo(hostSystem.getObj());

            dsBrowser = getHostSystem().getDatastoreBrowser();
            dc = getDatacenter();
        }
        catch (RuntimeFault e1)
        {
            LOGGER.error("Unexpected exception:", e1);
            throw new CollectorException(MessageValues.COLL_EXCP_PH);
        }
        catch (RemoteException e1)
        {
            LOGGER.error("Unexpected exception:", e1);
            throw new CollectorException(MessageValues.COLL_EXCP_PH);
        }

        for (HostDatastoreConnectInfo datastoreConnect : hostInfo.getDatastore())
        {
            Long size;
            Long freeSize;
            DatastoreSummary datastoreSummary = datastoreConnect.getSummary();
            if (datastoreSummary.isAccessible())
            {
                if (datastoreSummary.getType().equalsIgnoreCase("NFS"))
                {
                    if (checkNFS(repositoryLocation, datastoreSummary.getDatastore().get_value()))
                    {
                        continue;
                    }
                }

                // datastoreSummary.getDatastore()

                String dsName = String.format("[%s]", datastoreSummary.getName());
                String datastoreUuidMark = getDatastoreUuidMark(dsName, dsBrowser, dc);

                size = datastoreSummary.getCapacity();
                freeSize = datastoreSummary.getFreeSpace();

                ResourceType resource = new ResourceType();

                resource.setResourceType(ResourceEnumType.STORAGE_DISK);
                resource.setAddress(datastoreSummary.getName());
                resource.setElementName(datastoreSummary.getName());
                resource.setUnits(size);
                resource.setAvailableUnits(freeSize);
                resource.setConnection(datastoreUuidMark);

                resources.add(resource);

            } // accessible
        }// datastores

        Collections.sort(resources, new ResourceComparator());

        return resources;
    }

    /**
     * Gets the Host System on the Root Folder. Assume single Host System and named using its IP.
     */
    private HostSystem getHostSystem() throws CollectorException
    {
        // hostname match the ip address
        String hostname = getIpAddress();

        HostSystem host = null;
        ManagedEntity[] hosts;

        try
        {
            hosts =
                new InventoryNavigator(serviceInstance.getRootFolder())
                    .searchManagedEntities("HostSystem");

            // .searchManagedEntity("HostSystem", hostname);
        }
        catch (Exception e)
        {
            throw new CollectorException(MessageValues.CONN_EXCP_I);
        }

        if (hosts == null || hosts.length != 1)
        {
            LOGGER.error("Host System {} not found.", hostname);
            throw new CollectorException(MessageValues.HOST_SYSTEM_NOT_FOUND);
        }

        host = (HostSystem) hosts[0];

        return host;
    }

    /**
     * Gets the vSpherer Datatacenter. Assume its named ''ha-datacenter''.
     */
    private Datacenter getDatacenter() throws CollectorException
    {
        Datacenter dc;

        try
        {
            dc =
                (Datacenter) new InventoryNavigator(serviceInstance.getRootFolder())
                    .searchManagedEntity("Datacenter", "ha-datacenter");
        }
        catch (Exception e)
        {
            throw new CollectorException(MessageValues.CONN_EXCP_I);
        }

        if (dc == null)
        {
            LOGGER.error("Datacenter ''ha-datacenter'' not found.");
            throw new CollectorException(MessageValues.DATACENTER_NOT_FOUND);
        }

        return dc;
    }

    /** Reused Query specification to locate the folder mark (UUID) */
    private HostDatastoreBrowserSearchSpec createQueryDatastoreFolderMark()
    {
        HostDatastoreBrowserSearchSpec querySpec = new HostDatastoreBrowserSearchSpec();
        querySpec.setQuery(new FileQuery[] {new FolderFileQuery()}); // VmDiskFileQuery() FileQuery
        querySpec.setSearchCaseInsensitive(false);
        querySpec.setMatchPattern(new String[] {DATASTORE_UUID_MARK_PATTERN});

        // FileQueryFlags fqf = new FileQueryFlags();
        // fqf.setFileSize(true);
        // fqf.setModification(true);
        // querySpec.setDetails(fqf);
        return querySpec;

    }

    /**
     * Locate or create the datastore folder mark to determine if the datastore is being shared
     * across hypervisors.
     * 
     * @return a Datastore UUID
     * @throws CollectorException
     */
    private String getDatastoreUuidMark(final String dsName, final HostDatastoreBrowser dsBrowser,
        final Datacenter dc) throws CollectorException
    {
        String uuid = null;
        Task searchtask;

        try
        {
            // search on the top of the filesystem (do not navegate subfolders)
            searchtask = dsBrowser.searchDatastore_Task(dsName, createQueryDatastoreFolderMark()); // TODO
                                                                                                   // static

            searchtask.waitForTask();

            if (searchtask.getTaskInfo().state == TaskInfoState.success)
            {
                Object objres = searchtask.getTaskInfo().getResult();

                HostDatastoreBrowserSearchResults result =
                    (HostDatastoreBrowserSearchResults) objres;

                if (result.getFile() != null)
                {
                    if (result.getFile().length != 1)
                    {
                        throw new CollectorException(MessageValues.DATASTRORE_MULTIPLE_MARKS);
                    }

                    FileInfo fi = result.getFile()[0];
                    String foldername = fi.getPath(); // path = name in the root
                    LOGGER.debug("Datastore folder mark found [{}]", foldername);

                    if (!foldername.startsWith(DATASTORE_UUID_MARK))
                    {
                        LOGGER.error("The datastore folder mark isn't the expected [{}].",
                            foldername);

                        throw new CollectorException(MessageValues.DATASTRORE_MARK);
                    }

                    uuid = foldername.substring(DATASTORE_UUID_MARK.length());
                }
                else
                {
                    // any folder mark found
                }
            }
            else
            {
                // folder mark not found
            }

        }
        catch (Exception e)
        {
            e.printStackTrace(); // delme
            LOGGER.error("Can't identify the datastore [{}] uuid", dsName);
            throw new CollectorException(MessageValues.DATASTRORE_MARK, e);
        }

        if (uuid == null)
        {
            LOGGER.info(String.format(
                "Datastore %s on Host [%s] haven't any folder mark, creating it.", dsName,
                getIpAddress()));

            uuid = createDatastoreFolderMark(dc, dsName);
        }

        LOGGER.debug("Datastore {} UUID [{}]", dsName, uuid);

        return uuid;
    }

    /**
     * Create a new datastore folder mark.
     * 
     * @return the just created UUID for the folder mark
     */
    private String createDatastoreFolderMark(final Datacenter dc, final String dsName)
        throws CollectorException
    {
        String folderUuidMark = UUID.randomUUID().toString();
        String directoryOnDatastore =
            String.format("%s %s%s", dsName, DATASTORE_UUID_MARK, folderUuidMark);

        try
        {
            // do not create parent folders (is on the root)
            serviceInstance.getFileManager().makeDirectory(directoryOnDatastore, dc, false);
        }
        catch (FileFault e)
        {
            LOGGER.error("Can not create the folder mark at [{}], caused by file fault {}", dsName,
                e);
            throw new CollectorException(MessageValues.DATASTRORE_MARK, e);
        }
        catch (Exception e)
        {
            LOGGER.error("Can not create the folder mark at [{}]\n{}", dsName, e);
            throw new CollectorException(MessageValues.DATASTRORE_MARK, e);
        }

        return folderUuidMark;
    }

    /**
     * This auxiliar method return a list of ManagedObjectReference encapsulated into an
     * ObjectContent instance. Informing the name of the managed object reference as a parameter,
     * will return the list of MORs whatever is its place into the inventory hierarchy of the VI
     * SDK.
     * 
     * @param managedObjectType name of the MOR we want to retrieve
     * @return list of ObjectContent with the result of the search.
     * @throws RemoteException if there is any problem working with the remote objects
     */
    private ObjectContent[] getManagedObjectReferencesFromInventory(final PropertySpec[] propSpec)
        throws RemoteException
    {
        // Set the PropertyFilter Spec previous to put toghether the property Spec
        // and the objectSpec
        PropertyFilterSpec propertyFilter = new PropertyFilterSpec();
        propertyFilter.setPropSet(propSpec);
        propertyFilter.setObjectSet(getRootObjectSpec());

        // Do the search! This search will return all the matching MOR of the
        // 'managedObjectType' type
        ObjectContent[] objectContent =
            getMyConn().retrieveProperties(getServiceContent().getPropertyCollector(),
                new PropertyFilterSpec[] {propertyFilter});

        return objectContent;
    }

    private ObjectSpec[] getRootObjectSpec()
    {
        if (rootObjSpecs == null)
        {
            rootObjSpecs = new ObjectSpec[] {new ObjectSpec()};
            rootObjSpecs[0].setObj(getRootFolder());
            rootObjSpecs[0].setSkip(Boolean.FALSE);
            rootObjSpecs[0].setSelectSet(selectionSpecs);
        }

        return rootObjSpecs;
    }

    /**
     * Return the state of deployed machine.
     * 
     * @param esxiMachine machine deployed
     * @return VirtualMachineStateType representing the state.
     * @throws CollectorException, if can not obtain the VirtualMachineRuntimeInfo property
     */
    private VirtualSystemStatusEnumType getStateFromESXiMachine(final ObjectContent esxiMachine)
        throws CollectorException
    {
        // TODO revise for [ABICLOUDPREMIUM-324]
        // final Integer vmRuntimeProperty;
        // if (getApiVersion().startsWith("2.5"))
        // {
        // vmRuntimeProperty = 22;
        // }
        // else
        // {
        // vmRuntimeProperty = 23;
        // }
        //
        // String stateString =
        // ((VirtualMachineRuntimeInfo) esxiMachine.getPropSet(vmRuntimeProperty).getVal())
        // .getPowerState().getValue();

        // Solve [ABICLOUDPREMIUM-321]
        VirtualMachineRuntimeInfo runInfo = null;
        boolean found = false;
        DynamicProperty[] dps = esxiMachine.getPropSet();
        for (int i = 0; i < dps.length && !found; i++)
        {
            Object dp = dps[i].getVal();
            if (dp instanceof VirtualMachineRuntimeInfo)
            {
                runInfo = (VirtualMachineRuntimeInfo) dp;
                found = true;
            }
        }

        if (runInfo == null)
        {
            final String cause = "Can not retrieve VirtualMachineRuntimeInfo of machine";
            LOGGER.error(cause);
            throw new CollectorException(cause);
        }

        String stateString = runInfo.getPowerState().name();

        VirtualSystemStatusEnumType vmStateType;
        // parametrize the state
        if (stateString.equalsIgnoreCase(POWERED_OFF))
        {
            vmStateType = VirtualSystemStatusEnumType.POWERED_OFF;
        }
        else if (stateString.equalsIgnoreCase(POWERED_ON))
        {
            vmStateType = VirtualSystemStatusEnumType.RUNNING;
        }
        else
        {
            vmStateType = VirtualSystemStatusEnumType.PAUSED;
        }
        return vmStateType;
    }

    /**
     * Returns the virtual remote port if its enabled. 0 otherwise.
     * 
     * @param opts extra configuration key-value array.
     * @return a Long object with the remote session port
     */
    private Long getVPortFromExtraConfig(final OptionValue[] opts)
    {
        // default values
        boolean portIsEnabled = false;
        Long rdPort = 0L;

        // check first if the port is enabled.
        OptionValue optionValue;
        for (OptionValue opt : opts)
        {
            optionValue = opt;
            if (optionValue.getKey().equalsIgnoreCase("RemoteDisplay.vnc.enabled"))
            {
                portIsEnabled = Boolean.valueOf(optionValue.getValue().toString());
                break;
            }
        }

        // If it is enabled, search for the remote display port.
        if (portIsEnabled)
        {
            for (OptionValue opt : opts)
            {
                optionValue = opt;
                if (optionValue.getKey().equalsIgnoreCase("RemoteDisplay.vnc.port"))
                {
                    rdPort = Long.valueOf(optionValue.getValue().toString());
                    break;
                }
            }
        }

        return rdPort;
    }

    /**
     * Private helper to check if vmware license is not FREE basic.
     * 
     * @throws NoManagedException if the license is not valid.
     */
    private synchronized void hasValidLicense() throws NoManagedException
    {
        ManagedObjectReference licenseManager = getServiceContent().getLicenseManager();

        LicenseManagerLicenseInfo[] licenseInfo;
        try
        {
            licenseInfo =
                (LicenseManagerLicenseInfo[]) getDynamicProperty(licenseManager, "licenses");

            for (LicenseManagerLicenseInfo licenseManagerLicenseInfo : licenseInfo)
            {
                if (licenseManagerLicenseInfo.getEditionKey().equals("esxBasic"))
                {
                    throw new NoManagedException(MessageValues.NOMAN_ESXI_LIC);
                }

                KeyAnyValue[] properties = licenseManagerLicenseInfo.getProperties();

                Long expirationHours = new Long(0);
                Long expirationMinutes = new Long(0);
                boolean neverExpires = true;

                for (KeyAnyValue keyAnyValue : properties)
                {
                    if ("expirationHours".equals(keyAnyValue.getKey()))
                    {
                        expirationHours = (Long) keyAnyValue.getValue();
                        neverExpires = false;
                    }
                    else if ("expirationMinutes".equals(keyAnyValue.getKey()))
                    {
                        expirationMinutes = (Long) keyAnyValue.getValue();
                        neverExpires = false;
                    }
                }

                if (!neverExpires)
                {
                    if (expirationHours.intValue() == 0 || expirationMinutes.intValue() == 0)
                    {
                        throw new NoManagedException(MessageValues.NOMAN_ESXI_LIC);
                    }
                }
            }

        }
        catch (Exception e)
        {
            LOGGER.error("An error was occurred when checking the license: {}", e);
            throw new NoManagedException(MessageValues.NOMAN_ESXI_LIC);
        }

    }

    /**
     * Adds the value '-flat' to disk imagePath.
     * 
     * @param imagePath the image path
     * @return modified image path
     */
    private String parseImagePathAddingFlat(final String imagePath)
    {
        return imagePath.substring(0, imagePath.lastIndexOf(".vmdk")) + "-flat.vmdk";
    }

    /**
     * Depending on the kind of disk, we convert it into nodecollector's standard type.
     * 
     * @param diskFile file which contains a Virtual System disk
     * @return a {@link DiskType} object.
     */
    private VirtualDiskEnumType resolveDiskFileType(final VirtualDeviceFileBackingInfo diskFile)
    {
        VirtualDiskEnumType diskType;

        // first define the incompatible types which are the following:
        // · VirtualCdromIsoBackingInfo
        // · VirtualDiskFlatVer1BackingInfo
        // ·
        // check if the 'backing' variable belongs to a kind of supported disk types
        if (diskFile instanceof VirtualDiskFlatVer2BackingInfo)
        {
            // Check if its monolithic or use extents
            if (((VirtualDiskFlatVer2BackingInfo) diskFile).getSplit())
            {
                // Abicloud can not support extent files
                diskType = VirtualDiskEnumType.INCOMPATIBLE;
            }
            else
            {
                diskType = VirtualDiskEnumType.VMDK_FLAT;
            }
        }
        else if (diskFile instanceof VirtualDiskSparseVer2BackingInfo)
        {
            // Check if its monolithic or use extents
            if (((VirtualDiskSparseVer2BackingInfo) diskFile).getSplit())
            {
                // Abicloud can not support extent files
                diskType = VirtualDiskEnumType.INCOMPATIBLE;
            }
            else
            {
                diskType = VirtualDiskEnumType.VMDK_MONOLITHIC_SPARSE;
            }
        }
        else if (diskFile instanceof VirtualDiskRawDiskMappingVer1BackingInfo)
        {
            // Stateful image
            diskType = VirtualDiskEnumType.STATEFUL;
        }
        else
        {
            diskType = VirtualDiskEnumType.UNKNOWN;
        }

        return diskType;

    }

    /**
     * Gets the internet SCSI controller (Host Bus Adapter -- config.storageDevice.scsiLun)
     * initiator IQN.
     */
    protected String getInternetSCSIInitiatorIQN(final ObjectContent hostSystemOc,
        final ManagedObjectReference storageSystemMor) throws CollectorException
    {
        String[] hbasPropDesc = new String[] {"config.storageDevice.hostBusAdapter"};
        ManagedObjectReference hostSystemMor = hostSystemOc.getObj();

        HostInternetScsiHba iscsi = null;
        HostHostBusAdapter[] hbas;

        if (!isInternetSCSIEnable(hostSystemOc, storageSystemMor))
        {
            final String cause = "Can not enable the software iSCSI controller"; // internal message
            throw new CollectorException(cause);
        }

        try
        {
            ManagedObjectReference collector = null; // obtain?

            ObjectContent[] hostBusAdapters =
                getObjectProperties(collector, hostSystemMor, hbasPropDesc);

            if (hostBusAdapters == null || hostBusAdapters.length != 1)
            {
                final String cause =
                    "Can not retrieve avaiable Host Bus Adapters on the Storage Device";// internal
                // message
                LOGGER.error(cause);
                throw new CollectorException(cause);
            }

            // TODO propSet at 0 -- check by type
            ArrayOfHostHostBusAdapter arrHbas =
                (ArrayOfHostHostBusAdapter) hostBusAdapters[0].getPropSet()[0].getVal();

            hbas = arrHbas.getHostHostBusAdapter();
        }
        catch (Exception e)
        {
            final String cause =
                "Can not retrieve avaiable Host Bus Adapters on the Storage Device";// internal
            // message
            LOGGER.error(cause);
            throw new CollectorException(cause, e);
        }

        for (HostHostBusAdapter hba : hbas)
        {
            if (hba instanceof HostInternetScsiHba)
            {

                HostInternetScsiHba iscsicurrent = (HostInternetScsiHba) hba;

                LOGGER.info(String.format(
                    "[iscsi] Device:%s Driver:%s Model:%s\n\tAlias:%s Name:%s Software:%s",
                    iscsicurrent.getDevice(), iscsicurrent.getDriver(), iscsicurrent.getModel(),
                    iscsicurrent.getIScsiAlias(), iscsicurrent.getIScsiName(),
                    String.valueOf(iscsicurrent.isIsSoftwareBased())));

                if (iscsicurrent.isIsSoftwareBased()
                    && iscsicurrent.getModel().equalsIgnoreCase("iSCSI Software Adapter"))
                {
                    iscsi = iscsicurrent;
                }
            }
        }

        if (iscsi == null)
        {
            final String cause = "Can not find the iSCSI Host Bus Adapter";
            LOGGER.error(cause);
            throw new CollectorException(cause); // internal
            // message
        }

        LOGGER.info(String.format(
            "[iscsi] SELECTED :\n Device:%s Driver:%s Model:%s\n\tAlias:%s Name:%s Software:%s",
            iscsi.getDevice(), iscsi.getDriver(), iscsi.getModel(), iscsi.getIScsiAlias(),
            iscsi.getIScsiName(), String.valueOf(iscsi.isIsSoftwareBased())));

        return iscsi.getIScsiName();
    }

    /**
     * Gets the storage system reference from the host system's configuration manager.
     */
    private ManagedObjectReference getStorageSystem(final ObjectContent hostSystemOc)
        throws CollectorException
    {
        String[] storageSystemPropDesc = new String[] {"configManager.storageSystem"};
        ManagedObjectReference hostSystemMor = hostSystemOc.getObj();
        ManagedObjectReference storageSystem;

        try
        {
            ManagedObjectReference collector = null; // obtain?

            ObjectContent[] storages =
                this.getObjectProperties(collector, hostSystemMor, storageSystemPropDesc);

            // TODO storages.length == 1;

            storageSystem = storages[0].getObj();
        }
        catch (Exception e1)
        {
            final String cause = "Can not get Configuration Manager on the Host System";
            LOGGER.error(cause, e1);
            throw new CollectorException(cause, e1);// internal
            // message
        }

        return storageSystem;
    }

    /**
     * Check if host system has the internetSCSI software controller enable, if not try to enabling
     * it. TODO only look for software controller, add hardware support
     */
    private Boolean isInternetSCSIEnable(final ObjectContent hostSystemOc,
        final ManagedObjectReference storageSystemMor) throws CollectorException
    {
        Object objIscsiEnable;
        Boolean isIscsiEnable;

        ManagedObjectReference hostSystemMor = hostSystemOc.getObj();
        String iscsiEnableProp = "config.storageDevice.softwareInternetScsiEnabled";

        try
        {
            objIscsiEnable = getDynamicProperty(hostSystemMor, iscsiEnableProp);
            // propISCSIEnable = utils.getProperties(hostSystemMOR, props);
        }
        catch (Exception e)
        {
            final String cause =
                "Can not get the ''config.storageDevice.softwareInternetScsiEnabled'' property ";
            LOGGER.error(cause, e);
            throw new CollectorException(cause, e);// internal
            // message
        }

        if (objIscsiEnable == null)
        {
            final String cause = "No such softwareInternetScsiEnabled property";
            LOGGER.equals(cause);
            throw new CollectorException(cause); // internal
            // message
        }

        isIscsiEnable = (Boolean) objIscsiEnable;

        if (!isIscsiEnable)
        {
            LOGGER.debug("iSCSI software initiator is not enabled, try to setting up");

            try
            {
                getMyConn().updateSoftwareInternetScsiEnabled(storageSystemMor, true);

                // TODO check realy is enableenableInternetSCSI();
                isIscsiEnable = true;

                LOGGER.debug("iSCSI software initiator enabled");
            }
            catch (Exception e)
            {
                LOGGER.error("Can not enable the software iSCSI initiator.\n {}", e);
            }
        }

        return isIscsiEnable;
    }

    /**
     * Return the property of the ObjectContent with name 'propertyName'
     * 
     * @param obj object content to get its properties
     * @param propertyName property name to retrieve
     * @return
     * @throws Exception
     */
    private Object getDynamicProperty(final ObjectContent obj, final String propertyName)
        throws Exception
    {
        if (obj != null)
        {
            DynamicProperty[] dynamicProperties = obj.getPropSet();
            if (dynamicProperties != null)
            {
                for (DynamicProperty currentProp : dynamicProperties)
                {
                    if (currentProp.getName().equalsIgnoreCase(propertyName))
                    {
                        return currentProp.getVal();
                    }
                }
            }
        }

        return null;
    }
}
