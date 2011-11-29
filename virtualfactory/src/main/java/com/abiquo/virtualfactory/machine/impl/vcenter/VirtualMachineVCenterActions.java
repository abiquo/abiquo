package com.abiquo.virtualfactory.machine.impl.vcenter;

import java.rmi.RemoteException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.abiquo.virtualfactory.exception.VirtualMachineException;
import com.vmware.vim25.GenericVmConfigFault;
import com.vmware.vim25.InvalidProperty;
import com.vmware.vim25.RuntimeFault;
import com.vmware.vim25.mo.Folder;
import com.vmware.vim25.mo.InventoryNavigator;
import com.vmware.vim25.mo.ServiceInstance;
import com.vmware.vim25.mo.Task;
import com.vmware.vim25.mo.VirtualMachine;

/**
 * This class manages the virtual machine actions in a vcenter. It needs an opened session into a
 * vCenter in the attribute 'serviceInstance' obtained in the constructor.
 * 
 * @author jdevesa@abiquo.com
 */
public class VirtualMachineVCenterActions
{
    /** Log the events */
    private static final Logger LOGGER = LoggerFactory.getLogger(VirtualMachineVCenterActions.class);

    /** Service instance connection to VCenter */
    private ServiceInstance serviceInstance;
    
    /** Tasks to be required for a VM to change its state. */
    enum VMTasks
    {
        POWER_OFF, POWER_ON, DESTROY
    };

    /**
     * Public construction.
     * 
     * @param serviceInstance serviceInstance connection to a VDC.
     */
    public VirtualMachineVCenterActions(final ServiceInstance serviceInstance)
    {
        this.serviceInstance = serviceInstance;
    }

    /**
     * Returns a Virtual Machine according its name in the inventory.
     * 
     * @param name name of the virtual machine in vCenter
     * @return the VirtualMachine instance
     * @throws RemoteException
     * @throws RuntimeFault
     * @throws InvalidProperty
     */
    public VirtualMachine getVirtualMachine(String name)
    {
        try
        {
            Folder fold = serviceInstance.getRootFolder();
            InventoryNavigator navigator = new InventoryNavigator(fold);
            VirtualMachine vm =
                (VirtualMachine) navigator.searchManagedEntity("VirtualMachine", name);
            return vm;
        }
        catch (Exception e)
        {
            String message =
                "Could not know if the Virtual Machine exists in vCenter because of raising of exception "
                    + e.getClass().getCanonicalName() + ". Trace:" + e.getMessage();
            LOGGER.error(message);

            return null;
        }
    }
    
    /**
     * Starts a Virtual Machine at vCenter.
     * @param vm {@link VirtualMachine} instance.
     * @throws VirtualMachineException if any problem occurs starts the VirtualMachine.
     */
    public void startVirtualMachine(VirtualMachine vm) throws VirtualMachineException
    {
        executeTaskOnVM(vm, VMTasks.POWER_ON);
    }
    
    /**
     * Stops a Virtual Machine at vCenter.
     * 
     * @param vm {@link VirtualMachine} instance.
     * @throws VirtualMachineException if any problem occurs stopping the VirtualMachine.
     */
    public void stopVirtualMachine(VirtualMachine vm) throws VirtualMachineException
    {
        executeTaskOnVM(vm, VMTasks.POWER_OFF);
    }
    
    /**
     * Deletes a Virtual Machine at vCenter.
     * 
     * @param vm {@link VirtualMachine} instance.
     * @throws VirtualMachineException if any problem occurs destroying the VirtualMachine.
     */
    public void deleteVirtualMachine(VirtualMachine vm) throws VirtualMachineException
    {
        executeTaskOnVM(vm, VMTasks.DESTROY);
    }
    
    /**
     * Execute a task to a Virtual Machine
     * @param vm the {@link VirtualMachine} action.
     * @param taskAction the action to perform.
     * @throws VirtualMachineException
     */
    private void executeTaskOnVM(final VirtualMachine vm, final VMTasks taskAction) throws VirtualMachineException
    {
        String vmName = vm.getName();
        
        try
        {
            Task task;
            switch (taskAction)
            {
                case POWER_OFF:
                    task = vm.powerOffVM_Task();
                    break;
                case POWER_ON:
                    task = vm.powerOnVM_Task(null);
                    break;
                case DESTROY:
                    task = vm.destroy_Task();
                    break;
                default:
                    task = null;
            }

            if (task == null || task.waitForMe() == Task.SUCCESS)
            {
                LOGGER.info("[" + taskAction.name() + "] successfuly for VM [{}]", vmName);
            }
            else
            {
                final String msg = "[" + taskAction.name() + "] on " + vmName + " failed on vCenter";
                LOGGER.error(msg);
                throw new VirtualMachineException(msg);
            }
        }
        catch (Exception e)
        {
            String msg = "[" + taskAction.name() + "] on " + vmName + " failed on vCenter";
            if (e instanceof GenericVmConfigFault)
            {
                GenericVmConfigFault configFault = (GenericVmConfigFault) e;
                msg = msg + "Raison : " + configFault.getReason();
            }
            LOGGER.error(msg);
            throw new VirtualMachineException(msg);
        }
    }
}
