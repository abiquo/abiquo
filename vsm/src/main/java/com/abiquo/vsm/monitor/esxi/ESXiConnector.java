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
package com.abiquo.vsm.monitor.esxi;

import static com.vmware.vim25.TaskInfoState.queued;
import static com.vmware.vim25.TaskInfoState.running;
import static java.lang.System.getProperty;

import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.abiquo.vsm.events.VMEventType;
import com.abiquo.vsm.exception.MonitorException;
import com.vmware.vim25.InvalidLogin;
import com.vmware.vim25.ManagedObjectReference;
import com.vmware.vim25.ObjectContent;
import com.vmware.vim25.ObjectSpec;
import com.vmware.vim25.PropertyFilterSpec;
import com.vmware.vim25.PropertySpec;
import com.vmware.vim25.SelectionSpec;
import com.vmware.vim25.ServiceContent;
import com.vmware.vim25.TaskInfoState;
import com.vmware.vim25.TraversalSpec;
import com.vmware.vim25.VimPortType;
import com.vmware.vim25.VirtualMachineConfigInfo;
import com.vmware.vim25.mo.Folder;
import com.vmware.vim25.mo.HostSystem;
import com.vmware.vim25.mo.InventoryNavigator;
import com.vmware.vim25.mo.ManagedEntity;
import com.vmware.vim25.mo.ServiceInstance;
import com.vmware.vim25.mo.Task;
import com.vmware.vim25.mo.VirtualMachine;

/**
 * WMI Connector for ESXi monitoring tasks.
 * 
 * @author ibarrera
 */
public class ESXiConnector
{
    private final static Logger LOGGER = LoggerFactory.getLogger(ESXiConnector.class);

    private ManagedObjectReference rootFolder;

    private static PropertySpec[] virtualMachineSpec;

    private ObjectSpec[] rootObjSpecs;

    private static SelectionSpec[] selectionSpecs;

    private ServiceInstance serviceInstance;

    private ServiceInstance vcenterServiceInstance;

    private static final String POWERED_OFF = "poweredOff";

    private static final String POWERED_ON = "poweredOn";

    public ESXiConnector()
    {
        super();

        // Set the transactionSpec route to Host. We build the whole inventory tree
        // to search any 'managedObjectType'
        selectionSpecs = buildFullTraversal();

        virtualMachineSpec = new PropertySpec[] {new PropertySpec()};
        virtualMachineSpec[0].setType("VirtualMachine");
        virtualMachineSpec[0].setAll(true);
    }

    public static VMEventType translateState(final String state)
    {
        if (state.equalsIgnoreCase(POWERED_OFF))
        {
            return VMEventType.POWER_OFF;
        }
        else if (state.equalsIgnoreCase(POWERED_ON))
        {
            return VMEventType.POWER_ON;
        }
        else
        {
            return VMEventType.PAUSED;
        }
    }

    /**
     * Connects to the hypervisor.
     * 
     * @param physicalMachineAddress The address of the hypervisor.
     * @param username The user name used to connect to the hypervisor.
     * @param password The password used to connect to the hypervisor.
     * @throws MonitorException If connection fails.
     */
    public void connect(final String physicalMachineAddress, final String username,
        final String password) throws MonitorException
    {
        LOGGER.debug("Connecting to ESXi host: {}", physicalMachineAddress);

        try
        {
            URL url = new URL(physicalMachineAddress);
            URL connectionURL = new URL("https://" + url.getHost() + ":" + url.getPort() + "/sdk");

            serviceInstance = new ServiceInstance(connectionURL, username, password, true);
            vcenterServiceInstance = initializeVCenterServiceInstance(serviceInstance);
        }
        catch (MalformedURLException ex)
        {
            disconnect();
            throw new MonitorException("Invalid connection URI: " + physicalMachineAddress, ex);
        }
        catch (Exception ex)
        {
            disconnect();
            throw new MonitorException("Could not connect to ESXi host: " + physicalMachineAddress,
                ex);
        }
    }

    /**
     * Disconnects from the hypervisor.
     * 
     * @param physicalMachineAddress The hypervisor address.
     */
    public void disconnect()
    {
        LOGGER.debug("Disconnecting from ESXi host");

        if (isManagedByVCenter() && vcenterServiceInstance.getServerConnection() != null)
        {
            vcenterServiceInstance.getServerConnection().logout();
            vcenterServiceInstance = null;
        }

        if (serviceInstance != null && serviceInstance.getServerConnection() != null)
        {
            serviceInstance.getServerConnection().logout();
            serviceInstance = null;
        }
    }

    public boolean isManagedByVCenter()
    {
        return vcenterServiceInstance != null;
    }

    private ServiceInstance initializeVCenterServiceInstance(final ServiceInstance serviceInstance)
        throws MonitorException
    {
        String vcenterIp = getHostSystem(serviceInstance).getSummary().getManagementServerIp();

        if (!StringUtils.isEmpty(vcenterIp))
        {
            String port = getProperty("abiquo.vcenter.port.connection", "443");
            String user = getProperty("abiquo.dvs.vcenter.user");
            String password = getProperty("abiquo.dvs.vcenter.password");

            if (StringUtils.isEmpty(user))
            {
                String message =
                    "Invalid user value to connect to vCenter " + vcenterIp
                        + ". Please check your abiquo.properties file.";

                LOGGER.error(message);
                throw new MonitorException(message);
            }

            if (password == null)
            {
                String message =
                    "Invalid password value to connect to vCenter " + vcenterIp
                        + ". Please check your abiquo.properties file.";

                LOGGER.error(message);
                throw new MonitorException(message);
            }

            try
            {
                URL url = new URL("https://" + vcenterIp + ":" + port + "/sdk");
                LOGGER.debug("Host connected to vCenter at " + vcenterIp);
                return new ServiceInstance(url, user, password, true);
            }
            catch (InvalidLogin e)
            {
                String message = "Invalid credentials for logging in vCenter " + vcenterIp;
                LOGGER.error(message);
                throw new MonitorException(message, e);
            }
            catch (Exception e)
            {
                throw new MonitorException("Could not connect to vCenter", e);
            }
        }

        LOGGER.debug("Host not connected to vCenter");
        return null;
    }

    private HostSystem getHostSystem(final ServiceInstance serviceInstance) throws MonitorException
    {
        ManagedEntity[] mes;

        try
        {
            mes =
                new InventoryNavigator(serviceInstance.getRootFolder())
                    .searchManagedEntities("HostSystem");
        }
        catch (final RemoteException e)
        {
            throw new MonitorException("Host System not found", e);
        }

        if (mes == null || mes.length < 1)
        {
            throw new MonitorException("Host System not found");
        }
        else if (mes.length > 1)
        {
            LOGGER.error("There are more than a single Host System, using the first.");
        }

        return (HostSystem) mes[0];
    }

    /**
     * Get the information of all virtual machines in the target physical machine.
     * 
     * @return The information of all virtual machines in the target physical machine.
     * @throws MonitorException If the list of virtual machine information cannot be obtained.
     */
    public ObjectContent[] getAllVMs() throws MonitorException
    {
        try
        {
            return getManagedObjectReferencesFromInventory(virtualMachineSpec);
        }
        catch (Exception ex)
        {
            throw new MonitorException("Could not get the list of virtual machines", ex);
        }
    }

    /**
     * Get the state of the specified virtual machine.
     * 
     * @param virtualMachineName The name of the virtual machine.
     * @return The virtual machine state
     * @throws MonitorException If an error occurs retrieving machine state.
     */
    public VMEventType getState(final String virtualMachineName) throws MonitorException
    {
        try
        {
            Folder rootFolder = serviceInstance.getRootFolder();
            VirtualMachine virtualMachine =
                (VirtualMachine) new InventoryNavigator(rootFolder).searchManagedEntity(
                    "VirtualMachine", virtualMachineName);

            if (virtualMachine == null)
            {
                return VMEventType.UNKNOWN;
            }

            return translateState(virtualMachine.getRuntime().getPowerState().name());
        }
        catch (Exception ex)
        {
            throw new MonitorException("Could not retrieve the state of machine: "
                + virtualMachineName, ex);
        }
    }

    /**
     * Checks if a virtual machine exists in the hypervisor
     * 
     * @param virtualMachineName the name of the virtual machines
     * @return True if exists, otherwise false.
     * @throws MonitorException
     */
    public boolean existVirtualMachine(final String virtualMachineName) throws MonitorException
    {
        Folder rootFolder = serviceInstance.getRootFolder();
        VirtualMachine virtualMachine = null;

        try
        {
            virtualMachine =
                (VirtualMachine) new InventoryNavigator(rootFolder).searchManagedEntity(
                    "VirtualMachine", virtualMachineName);
        }
        catch (Exception ex)
        {
            throw new MonitorException("Could not obtain the machine: " + virtualMachineName, ex);
        }

        return virtualMachine != null;
    }

    /**
     * Checks if a virtual machine exists in the associated vCenter
     * 
     * @param virtualMachineName the name of the virtual machine
     * @return True if exists, otherwise false.
     * @throws MonitorException
     */
    public boolean existVirtualMachineInVCenter(final String virtualMachineName)
        throws MonitorException
    {
        VirtualMachine virtualMachine = null;

        try
        {
            Folder rootFolder = vcenterServiceInstance.getRootFolder();
            virtualMachine =
                (VirtualMachine) new InventoryNavigator(rootFolder).searchManagedEntity(
                    "VirtualMachine", virtualMachineName);
        }
        catch (Exception ex)
        {
            throw new MonitorException("Could not obtain from vCenter the machine: "
                + virtualMachineName, ex);
        }

        return virtualMachine != null;
    }

    /**
     * Checks through vCenter if a virtual machine has tasks in progress.
     * 
     * @param virtualMachineName the name of the virtual machine
     * @return true if the virtual machine has tasks in progress, otherwise false.
     */
    public boolean hasTasksInProgressInVCenter(final String virtualMachineName)
        throws MonitorException
    {
        try
        {
            Folder rootFolder = vcenterServiceInstance.getRootFolder();
            VirtualMachine virtualMachine =
                (VirtualMachine) new InventoryNavigator(rootFolder).searchManagedEntity(
                    "VirtualMachine", virtualMachineName);

            return hasTasksInProgress(virtualMachine);
        }
        catch (Exception ex)
        {
            throw new MonitorException("Could not obtain from vCenter the machine: "
                + virtualMachineName, ex);
        }
    }

    /**
     * Checks if a virtual machine has tasks in progress.
     * 
     * @param virtualMachineName the name of the virtual machine
     * @return true if the virtual machine has tasks in progress, otherwise false.
     */
    public boolean hasTasksInProgress(final String virtualMachineName) throws MonitorException
    {
        try
        {
            Folder rootFolder = serviceInstance.getRootFolder();
            VirtualMachine virtualMachine =
                (VirtualMachine) new InventoryNavigator(rootFolder).searchManagedEntity(
                    "VirtualMachine", virtualMachineName);

            return hasTasksInProgress(virtualMachine);
        }
        catch (Exception ex)
        {
            throw new MonitorException("Could not obtain the machine: " + virtualMachineName, ex);
        }
    }

    /**
     * Checks if a virtual machine has tasks in progress.
     * 
     * @param virtualMachine the {@link VirtualMachine} to query about
     * @return true if the {@link VirtualMachine} has tasks in progress, otherwise false.
     */
    private boolean hasTasksInProgress(final VirtualMachine virtualMachine) throws MonitorException
    {
        try
        {
            if (virtualMachine == null)
            {
                return false;
            }

            if (virtualMachine.getRecentTasks() == null)
            {
                return false;
            }

            boolean tasksInProgress = false;

            for (Task task : virtualMachine.getRecentTasks())
            {
                if (task.getTaskInfo() == null)
                {
                    continue;
                }

                TaskInfoState state = task.getTaskInfo().getState();

                if (state != null)
                {
                    if (state.equals(queued) || state.equals(running))
                    {
                        tasksInProgress = true;
                        break;
                    }
                }
            }

            return tasksInProgress;
        }
        catch (Exception ex)
        {
            throw new MonitorException("Could not obtain the tasks", ex);
        }
    }

    /**
     * Encapsulates the call to get the {@link VirtualMachineConfigInfo} object.
     * 
     * @param esxiMachine remote deployed machine
     * @return the configuration of the deployed machine or null if is not an instance of
     *         VirtualMachineConfigInfo.
     */
    public VirtualMachineConfigInfo getVMConfigFromObjectContent(final ObjectContent esxiMachine)
    {
        Object value = esxiMachine.getPropSet()[2].getVal();
        VirtualMachineConfigInfo configuration = null;

        if (value instanceof VirtualMachineConfigInfo)
        {
            configuration = (VirtualMachineConfigInfo) esxiMachine.getPropSet()[2].getVal();
        }

        return configuration;
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

    /**
     * @return the myConn
     */
    public VimPortType getMyConn()
    {
        return serviceInstance.getServerConnection().getVimService();
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

    private ManagedObjectReference getRootFolder()
    {
        if (rootFolder == null)
        {
            rootFolder = getServiceContent().getRootFolder();
        }
        return rootFolder;
    }

    /**
     * @return the serviceContent
     */
    public ServiceContent getServiceContent()
    {
        return serviceInstance.getServiceContent();
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

}
