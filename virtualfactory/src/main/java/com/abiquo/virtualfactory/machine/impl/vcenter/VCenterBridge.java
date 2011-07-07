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

package com.abiquo.virtualfactory.machine.impl.vcenter;

import java.net.URL;
import java.rmi.RemoteException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.abiquo.virtualfactory.exception.VirtualMachineException;
import com.abiquo.virtualfactory.network.VirtualNIC;
import com.vmware.vim25.HostListSummary;
import com.vmware.vim25.InvalidLogin;
import com.vmware.vim25.VirtualMachineConnectionState;
import com.vmware.vim25.mo.DistributedVirtualPortgroup;
import com.vmware.vim25.mo.Folder;
import com.vmware.vim25.mo.HostSystem;
import com.vmware.vim25.mo.InventoryNavigator;
import com.vmware.vim25.mo.ServiceInstance;
import com.vmware.vim25.mo.Task;
import com.vmware.vim25.mo.VirtualMachine;

/**
 * Use this class to provide a proxy functionallity between the ESXi plugin and the vCenter. Most of
 * the logic functions are implemented by the class {@link DistributedPortGroupActions}.
 * 
 * @author jdevesa@abiquo.com
 */
public class VCenterBridge
{

    /**
     * IP address to vCenter.
     */
    private String vCenterIP;

    /**
     * Maintain the object of the vCenter connection.
     */
    private ServiceInstance vCenterServiceInstance;

    /**
     * Logger class
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(VCenterBridge.class);
    
    /**
     * Private constructor. It is only called by the
     * {@link VCenterBridge#createVCenterBridge(ServiceInstance) function} and it must be this way.
     * 
     * @param vCenterIP ip address of the vCenter.
     */
    private VCenterBridge(final String vCenterIP)
    {
        this.vCenterIP = vCenterIP;
    }

    /**
     * Static method to implement the logic to discover the vCenter IP from the Host address.
     * 
     * @param hostServiceInstance this object is the instance to the Host connection.
     * @return an instance of the object {@link VCenterBridge} if there is a vCenter informed, null otherwise
     */
    public static VCenterBridge createVCenterBridge(ServiceInstance hostServiceInstance)
    {
        try
        {
            Folder fold = hostServiceInstance.getRootFolder();
            HostSystem host =
                (HostSystem) new InventoryNavigator(fold).searchManagedEntities("HostSystem")[0];

            HostListSummary summary = host.getSummary();
            String serverIp = summary.getManagementServerIp();
            if (serverIp == null)
            {
                return null;
            }
            return new VCenterBridge(serverIp);
        }
        catch (RemoteException e)
        {
            return null;
        }
    }

    /**
     * Create a port group with name "networkName_vlanTag" in a Distributed Virtual Switch.
     * 
     * @param vSwitchName name of the distributed virtual switch where to create the port group.
     * @param networkName name of the network in Abiquo.
     * @param vlanTag tag to use in the port group.
     * @throws VirtualMachineException encapsulate any exception
     */
    public void createPortGroupInVCenter(String vSwitchName, String networkName, Integer vlanTag)
        throws VirtualMachineException
    {
        String portGroupName = networkName + "_" + vlanTag;
        connect();
        DistrubutedPortGroupActions vdsCreation =
            new DistrubutedPortGroupActions(vCenterServiceInstance);
        if (vdsCreation.getPortGroup(vSwitchName, portGroupName) == null)
        {
            // create port group because it does not exist.
            vdsCreation.createPortGroupInDVS(vSwitchName, networkName, vlanTag);
        }
        disconnect();
    }

    /**
     * Attach a virtual machine NICs to the corresponding port groups.
     * 
     * @param nameVM name of the virtual machine.
     * @param vnicList list of NICs with the port groups to attach to.
     * @throws VirtualMachineException encapsulate any exception.
     */
    public void attachVMToPortGroup(String nameVM, List<VirtualNIC> vnicList)
        throws VirtualMachineException
    {
        connect();
        DistrubutedPortGroupActions dpgActions =
            new DistrubutedPortGroupActions(vCenterServiceInstance);
        for (VirtualNIC vnic : vnicList)
        {
            dpgActions.attachVirtualMachineNICToPortGroup(nameVM, vnic.getNetworkName() + "_"
                + vnic.getVlanTag(), vnic.getMacAddress());
        }
        disconnect();
    }

    /**
     * Delete a port group.
     * 
     * @param vnicList list of vnicLists which contains port groups to be deleted.
     * @throws VirtualMachineException encapsulate any exception.
     */
    public void deconfigureNetwork(List<VirtualNIC> vnicList) throws VirtualMachineException
    {
        connect();
        for (VirtualNIC vnic : vnicList)
        {
            DistrubutedPortGroupActions dpgActions =
                new DistrubutedPortGroupActions(vCenterServiceInstance);
            DistributedVirtualPortgroup port =
                dpgActions.getPortGroup(vnic.getVSwitchName(),
                    vnic.getNetworkName() + "_" + vnic.getVlanTag());

            // Delete the distributed virtual port group if there are no virtual machines
            // associated to it.
            if (port.getVms().length == 0)
            {
                dpgActions.deletePortGroup(port);
            }

        }
        disconnect();
    }

    /**
     * Delete a virtual machine in vCenter. Virtual Machines are actually deleted in ESXi instead of
     * vCenter. But they become 'orphaned' in vCenter. This method is used to unregister the
     * 'orphaned' machines.
     * 
     * @param machineName name of the virtual machine to delete.
     * @throws VirtualMachineException encapsulate any exception.
     */
    public void unregisterVM(String machineName) throws VirtualMachineException
    {
        connect();
        Folder fold = vCenterServiceInstance.getRootFolder();
        try
        {
            VirtualMachine vm =
                (VirtualMachine) new InventoryNavigator(fold).searchManagedEntity("VirtualMachine",
                    machineName);
            
            if (vm == null)
            {
                // the machine is not there, so return it.
                return;
            }
                
            // wait until detects the virtual machine is 'orphaned'. This is because the 'metadata'
            // delay between the ESXi and the vCenter.
            VirtualMachineConnectionState state;
            do 
            {
                state = vm.getRuntime().connectionState;
                
            } while (!state.name().equalsIgnoreCase("orphaned"));
            
            vm.unregisterVM();
                        
            LOGGER.info("Orphaned machine '" + machineName + "' has been unregistered from vCenter '"
                + vCenterIP + "'.");
        }
        catch (RemoteException e)
        {
            LOGGER.error("Orphaned machine '" + machineName + "' could not be unregistered from vCenter '"
                + vCenterIP + "'. ");
            throw new VirtualMachineException(e);
        }

        disconnect();

    }

    /**
     * Connect with the vCenter and stablish the class member 'vCenterServiceInstance'.
     * 
     * @throws VirtualMachineException encapsulate any exception.
     */
    private void connect() throws VirtualMachineException
    {
        System.setProperty("org.apache.axis.components.net.SecureSocketFactory",
            "org.apache.axis.components.net.SunFakeTrustSocketFactory");

        String user = System.getProperty("abiquo.dvs.vcenter.user");
        String password = System.getProperty("abiquo.dvs.vcenter.password");

        if (user == null || user.isEmpty())
        {
            String message = "Invalid user value to connect to vCenter " + vCenterIP + ". Please check your abiquo.properties file.";
            LOGGER.error(message);
            throw new VirtualMachineException(message);
        }
        
        if (password == null)
        {
            String message = "Invalid password value to connect to vCenter " + vCenterIP + ". Please check your abiquo.properties file.";
            LOGGER.error(message);
            throw new VirtualMachineException(message);
        }
        
        try
        {
            vCenterServiceInstance =
                new ServiceInstance(new URL("https://" + vCenterIP + "/sdk"), user, password, true);
        }
        catch (InvalidLogin e)
        {
            String message = "Invalid credentials for logging in vCenter " + vCenterIP; 
            LOGGER.error(message);
            throw new VirtualMachineException(message);
        }
        catch (Exception e)
        {
            String message = "Could not connect at vCenter " + vCenterIP + " . Cause: " + e.getMessage();
            LOGGER.error(message);
            throw new VirtualMachineException(message);
        }
    }

    /**
     * Disconnect from vCenter.
     */
    private void disconnect()
    {
        if (vCenterServiceInstance != null && vCenterServiceInstance.getServerConnection() != null)
        {
            vCenterServiceInstance.getServerConnection().logout();
        }
    }

}
