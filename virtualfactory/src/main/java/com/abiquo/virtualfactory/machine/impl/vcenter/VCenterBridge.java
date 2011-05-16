package com.abiquo.virtualfactory.machine.impl.vcenter;

import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;

import com.abiquo.virtualfactory.exception.VirtualMachineException;
import com.vmware.vim25.HostListSummary;
import com.vmware.vim25.mo.Folder;
import com.vmware.vim25.mo.HostSystem;
import com.vmware.vim25.mo.InventoryNavigator;
import com.vmware.vim25.mo.ServiceInstance;

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
        VCenterDVSCreation vdsCreation = new VCenterDVSCreation(vCenterServiceInstance);
        if (vdsCreation.getPortGroup(vSwitchName, portGroupName) == null)
        {
            // create port group because it does not exist.
            vdsCreation.createPortGroupInDVS(vSwitchName, networkName, vlanTag);
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
