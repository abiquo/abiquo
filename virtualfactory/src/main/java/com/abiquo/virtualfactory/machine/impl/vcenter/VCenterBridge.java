package com.abiquo.virtualfactory.machine.impl.vcenter;

import java.net.URL;
import java.rmi.RemoteException;
import java.util.List;

import com.abiquo.virtualfactory.exception.VirtualMachineException;
import com.abiquo.virtualfactory.network.VirtualNIC;
import com.vmware.vim25.HostListSummary;
import com.vmware.vim25.InvalidProperty;
import com.vmware.vim25.RuntimeFault;
import com.vmware.vim25.mo.DistributedVirtualPortgroup;
import com.vmware.vim25.mo.DistributedVirtualSwitch;
import com.vmware.vim25.mo.Folder;
import com.vmware.vim25.mo.HostSystem;
import com.vmware.vim25.mo.InventoryNavigator;
import com.vmware.vim25.mo.ServiceInstance;
import com.vmware.vim25.mo.VirtualMachine;

/**
 * @author jdevesa
 */
public class VCenterBridge
{

    private String vCenterIP;

    private ServiceInstance vCenterServiceInstance;

    private VCenterBridge(final String vCenterIP)
    {
        this.vCenterIP = vCenterIP;
    }

    public static VCenterBridge createVCenterBridge(ServiceInstance hostServiceInstance)
        throws VirtualMachineException
    {
        try
        {
            Folder fold = hostServiceInstance.getRootFolder();
            HostSystem host =
                (HostSystem) new InventoryNavigator(fold).searchManagedEntities("HostSystem")[0];

            HostListSummary summary = host.getSummary();
            String serverIp = summary.getManagementServerIp();
            return new VCenterBridge(serverIp);
        }
        catch (RemoteException e)
        {
            throw new VirtualMachineException("Unexpected exception getting the remote vCenter IP.");
        }

    }

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

    public void attachVMToPortGroup(String nameVM, List<VirtualNIC> vnicList)
        throws VirtualMachineException
    {
        connect();
        DistrubutedPortGroupActions dpgActions =
            new DistrubutedPortGroupActions(vCenterServiceInstance);
        for (VirtualNIC vnic : vnicList)
        {
            dpgActions.attachVirtualMachineToPortGroup(nameVM, vnic);
        }
        disconnect();
    }

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

    public void unregisterVM(String machineName) throws VirtualMachineException
    {
        connect();
        Folder fold = vCenterServiceInstance.getRootFolder();
        try
        {
            VirtualMachine vm =
                (VirtualMachine) new InventoryNavigator(fold).searchManagedEntity(
                    "VirtualMachine", machineName);
            
            vm.destroy_Task();
        }
        catch (RemoteException e)
        {
            throw new VirtualMachineException(e);
        }
        
        disconnect();
        
    }
    
    private void connect() throws VirtualMachineException
    {
        System.setProperty("org.apache.axis.components.net.SecureSocketFactory",
            "org.apache.axis.components.net.SunFakeTrustSocketFactory");

        String user = System.getProperty("abiquo.experimentaldvs.vcenter.user");
        String password = System.getProperty("abiquo.experimentaldvs.vcenter.password");

        try
        {
            vCenterServiceInstance =
                new ServiceInstance(new URL("https://" + vCenterIP + "/sdk"), user, password, true);
        }
        catch (Exception e)
        {
            // TODO: tratar temas de connexion o user/password incorrectos
            throw new VirtualMachineException(e.getMessage());
        }
    }

    private void disconnect() throws VirtualMachineException
    {
        if (vCenterServiceInstance != null && vCenterServiceInstance.getServerConnection() != null)
        {
            vCenterServiceInstance.getServerConnection().logout();
        }
    }

}
