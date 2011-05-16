/**
 * 
 */
package com.abiquo.virtualfactory.machine.impl.vcenter;

import java.rmi.RemoteException;

import com.abiquo.virtualfactory.exception.VirtualMachineException;
import com.abiquo.virtualfactory.network.VirtualNIC;
import com.vmware.vim25.InvalidProperty;
import com.vmware.vim25.ManagedObjectReference;
import com.vmware.vim25.RuntimeFault;
import com.vmware.vim25.VirtualDeviceConfigSpec;
import com.vmware.vim25.VirtualDeviceConfigSpecOperation;
import com.vmware.vim25.VirtualE1000;
import com.vmware.vim25.VirtualEthernetCard;
import com.vmware.vim25.VirtualEthernetCardNetworkBackingInfo;
import com.vmware.vim25.VirtualMachineConfigSpec;
import com.vmware.vim25.mo.DistributedVirtualSwitch;
import com.vmware.vim25.mo.Folder;
import com.vmware.vim25.mo.InventoryNavigator;
import com.vmware.vim25.mo.Network;
import com.vmware.vim25.mo.ServiceInstance;
import com.vmware.vim25.mo.VirtualMachine;

/**
 * @author jdevesa
 */
public class VCenterVMAttachment
{

    private ServiceInstance serviceInstance;

    /**
     * Public construction.
     * 
     * @param serviceInstance serviceInstance connection to a VDC.
     */
    public VCenterVMAttachment(ServiceInstance serviceInstance)
    {
        this.serviceInstance = serviceInstance;
    }

    public void attachVirtualMachine(String nameVM, VirtualNIC vnic) throws VirtualMachineException
    {
        Folder fold = serviceInstance.getRootFolder();
        try
        {
            InventoryNavigator navigator = new InventoryNavigator(fold);
            // Get the VirtualMachine
            VirtualMachine vm =
                (VirtualMachine) navigator.searchManagedEntity("VirtualMachine", nameVM);

            // Get the Network
            String networkName = vnic.getNetworkName() + "_" + vnic.getVlanTag();
            Network network = (Network) navigator.searchManagedEntity("Network", networkName);  


            VirtualEthernetCardNetworkBackingInfo nicBacking =
                new VirtualEthernetCardNetworkBackingInfo();
            nicBacking.setDeviceName(networkName);
            nicBacking.setNetwork(network.getMOR());
            
            // Define the
            VirtualEthernetCard virtualNICSpec = new VirtualE1000();
            virtualNICSpec.setAddressType("manual");
            virtualNICSpec.setMacAddress(vnic.getMacAddress());
            virtualNICSpec.setBacking(nicBacking);
            virtualNICSpec.setKey(4);

            VirtualDeviceConfigSpec nicSpec = new VirtualDeviceConfigSpec();
            nicSpec.setOperation(VirtualDeviceConfigSpecOperation.edit);
            nicSpec.setDevice(virtualNICSpec);

            VirtualMachineConfigSpec vmConfig = new VirtualMachineConfigSpec();
            vmConfig.setDeviceChange(new VirtualDeviceConfigSpec[] {nicSpec});
            
            vm.reconfigVM_Task(vmConfig);
        }
        catch (RemoteException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
