/**
 * 
 */
package com.abiquo.virtualfactory.machine.impl.vcenter;

import java.rmi.RemoteException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.abiquo.virtualfactory.exception.VirtualMachineException;
import com.abiquo.virtualfactory.network.VirtualNIC;
import com.vmware.vim25.DVPortgroupConfigInfo;
import com.vmware.vim25.DVPortgroupConfigSpec;
import com.vmware.vim25.DistributedVirtualPortgroupPortgroupType;
import com.vmware.vim25.DistributedVirtualSwitchPortConnection;
import com.vmware.vim25.DynamicProperty;
import com.vmware.vim25.ManagedObjectReference;
import com.vmware.vim25.ObjectContent;
import com.vmware.vim25.ObjectSpec;
import com.vmware.vim25.PropertyFilterSpec;
import com.vmware.vim25.PropertySpec;
import com.vmware.vim25.VMwareDVSPortSetting;
import com.vmware.vim25.VMwareDVSPortgroupPolicy;
import com.vmware.vim25.VimPortType;
import com.vmware.vim25.VirtualDeviceConfigSpec;
import com.vmware.vim25.VirtualDeviceConfigSpecOperation;
import com.vmware.vim25.VirtualEthernetCardDistributedVirtualPortBackingInfo;
import com.vmware.vim25.VirtualMachineConfigSpec;
import com.vmware.vim25.VirtualVmxnet3;
import com.vmware.vim25.VmwareDistributedVirtualSwitchVlanIdSpec;
import com.vmware.vim25.mo.DistributedVirtualPortgroup;
import com.vmware.vim25.mo.DistributedVirtualSwitch;
import com.vmware.vim25.mo.Folder;
import com.vmware.vim25.mo.InventoryNavigator;
import com.vmware.vim25.mo.ServiceInstance;
import com.vmware.vim25.mo.Task;
import com.vmware.vim25.mo.VirtualMachine;

/**
 * This class manages the creation, retrieve and deletion (edition not yet) of port groups into a
 * distributed virtual switch. It needs an opened session into a vCenter in the attribute
 * 'serviceInstance' obtained in the constructor.
 * 
 * @author jdevesa@abiquo.com
 */
public class DistrubutedPortGroupActions
{

    private static final Logger LOGGER = LoggerFactory.getLogger(DistrubutedPortGroupActions.class);

    private ServiceInstance serviceInstance;

    /**
     * Public construction.
     * 
     * @param serviceInstance serviceInstance connection to a VDC.
     */
    public DistrubutedPortGroupActions(ServiceInstance serviceInstance)
    {
        this.serviceInstance = serviceInstance;
    }

    /**
     * Create a new port group with port settings IdSpec.
     * 
     * @param dvsName name of the switch were the port group will be attached.
     * @param networkName distinctive name of the port group.
     * @param vlanTag tag that the port group will use.
     * @return the port group name.
     * @throws VirtualMachineException
     * @throws Exception
     */
    public String createPortGroupInDVS(String dvsName, String networkName, Integer vlanTag)
        throws VirtualMachineException
    {
        String portGroupName = networkName + "_" + vlanTag;

        try
        {

            // Check if the port group already exists
            DistributedVirtualPortgroup aux = this.getPortGroup(dvsName, portGroupName);
            if (aux != null)
            {
                throw new VirtualMachineException("The distributed port group with name '"
                    + portGroupName + "' already exists.");
            }

            Folder fold = serviceInstance.getRootFolder();
            DistributedVirtualSwitch dvs =
                (DistributedVirtualSwitch) new InventoryNavigator(fold).searchManagedEntity(
                    "DistributedVirtualSwitch", dvsName);

            if (dvs == null)
            {
                throw new VirtualMachineException("The dvSwitch with name '" + dvsName
                    + "' does not exist.");
            }

            // Allow VLAN overriding
            VMwareDVSPortgroupPolicy portGroupPolicy = new VMwareDVSPortgroupPolicy();
            portGroupPolicy.setBlockOverrideAllowed(Boolean.TRUE);
            portGroupPolicy.setPortConfigResetAtDisconnect(Boolean.TRUE);

            // Set the VLAN Tag
            VmwareDistributedVirtualSwitchVlanIdSpec vlanSpec =
                new VmwareDistributedVirtualSwitchVlanIdSpec();
            vlanSpec.setInherited(Boolean.FALSE);
            vlanSpec.setVlanId(vlanTag);

            VMwareDVSPortSetting dvsPortSettings = new VMwareDVSPortSetting();
            dvsPortSettings.setVlan(vlanSpec);

            // Define the port group
            DVPortgroupConfigSpec dvPortGroup = new DVPortgroupConfigSpec();
            dvPortGroup.setName(portGroupName);
            dvPortGroup.setNumPorts(128);
            dvPortGroup.setType(DistributedVirtualPortgroupPortgroupType.earlyBinding.name());
            dvPortGroup.setPolicy(portGroupPolicy);
            dvPortGroup.setDefaultPortConfig(dvsPortSettings);

            DVPortgroupConfigSpec[] portGroups = new DVPortgroupConfigSpec[] {dvPortGroup};

            // Attach the port grup to the DVS.
            serviceInstance.getServerConnection().getVimService()
                .addDVPortgroup_Task(dvs.getMOR(), portGroups);

            LOGGER.info("Port group '" + portGroupName + "' created into dvSwitch with name '"
                + dvsName + "'.");
            return portGroupName;
        }
        catch (Exception e)
        {
            String message =
                "Could not create the port group '" + portGroupName + "' into dvSwitch with name '"
                    + dvsName + "' because of:" + e.getMessage();

            LOGGER.error(message);
            throw new VirtualMachineException(message, e);
        }

    }

    /**
     * Get the port group into a Distributed Virtual Switch.
     * 
     * @param dvsName name of the Distributed Virtual Switch.
     * @param portGroupName PortGroup name we search for.
     * @return a {@link DistributedVirtualPortgroup} instance.
     * @throws VirtualMachineException
     * @throws Exception
     */
    public DistributedVirtualPortgroup getPortGroup(String dvsName, String portGroupName)
        throws VirtualMachineException
    {
        try
        {
            Folder fold = serviceInstance.getRootFolder();

            DistributedVirtualPortgroup dvPortGroup =
                (DistributedVirtualPortgroup) new InventoryNavigator(fold).searchManagedEntity(
                    "DistributedVirtualPortgroup", portGroupName);

            if (dvPortGroup == null)
            {
                return null;
            }

            DVPortgroupConfigInfo portGroupInfo = dvPortGroup.getConfig();

            // Get its DistributedVirtualSwitch ManagedObjectReference and check if the
            // name is the same than the informed one.
            ManagedObjectReference dvSwitch = portGroupInfo.distributedVirtualSwitch;
            ObjectContent dvSwitchContent = getObjectProperties(null, dvSwitch, new String[] {})[0];
            String dvSwitchName = (String) getDynamicProperty(dvSwitchContent, "name");

            if (!dvSwitchName.equalsIgnoreCase(dvsName))
            {
                String message =
                    "The port group with name '" + portGroupName
                        + "' does not exist into the dvSwitch '" + dvsName
                        + "' but in the dvSwitch with name '" + dvSwitchName + "'.";
                LOGGER.error(message);
                throw new VirtualMachineException(message);
            }

            return dvPortGroup;
        }
        catch (Exception e)
        {
            String message =
                "Could not retrieve the port group '" + portGroupName
                    + "' into dvSwitch with name '" + dvsName + "' because of: " + e.getMessage();

            LOGGER.error(message);
            throw new VirtualMachineException(message, e);
        }
    }

    /**
     * Delete a {@link DistributedVirtualPortgroup}.
     * 
     * @param portGroup
     * @throws VirtualMachineException
     * @throws Exception
     */
    public void deletePortGroup(DistributedVirtualPortgroup portGroup)
        throws VirtualMachineException
    {
        try
        {
            portGroup.destroy_Task();
            LOGGER.info("Port group with name '" + portGroup.getName() + "' deleted.");
        }
        catch (Exception e)
        {
            String message =
                "Could not delete the port group '" + portGroup.getName() + "' because of: "
                    + e.getMessage();

            LOGGER.error(message);
            throw new VirtualMachineException(message, e);
        }
    }

    /**
     * Create a binding from a virtual machine NIC to a port group.
     * 
     * @param nameVM name of the virtual machine to use.
     * @param portGroupName port group name to associate
     * @param macAddress mac address of the NIC
     * @throws VirtualMachineException encapsulate any exception
     */
    public void attachVirtualMachineNICToPortGroup(String nameVM, String portGroupName,
        String macAddress) throws VirtualMachineException
    {
        Folder fold = serviceInstance.getRootFolder();

        try
        {
            InventoryNavigator navigator = new InventoryNavigator(fold);
            // Get the VirtualMachine
            VirtualMachine vm;
            do
            {
                // TODO: This is dangerous!!! Think in something else.
                // retrieve the virtual machine while the vcenter refreshes the state.
                vm = (VirtualMachine) navigator.searchManagedEntity("VirtualMachine", nameVM);

            }
            while (vm == null);

            // Get the information we need to create the binding to a DVS.
            DistributedVirtualPortgroup dvPortGroup =
                (DistributedVirtualPortgroup) new InventoryNavigator(fold).searchManagedEntity(
                    "DistributedVirtualPortgroup", portGroupName);
            ManagedObjectReference dvSwitch = dvPortGroup.getConfig().distributedVirtualSwitch;
            ObjectContent dvSwitchContent = getObjectProperties(null, dvSwitch, new String[] {})[0];
            String dvSwitchUUID = (String) getDynamicProperty(dvSwitchContent, "uuid");

            // Stablish the switch connection.
            DistributedVirtualSwitchPortConnection switchConnection =
                new DistributedVirtualSwitchPortConnection();
            switchConnection.setPortgroupKey(dvPortGroup.getKey());
            switchConnection.setSwitchUuid(dvSwitchUUID);

            VirtualEthernetCardDistributedVirtualPortBackingInfo nicBacking =
                new VirtualEthernetCardDistributedVirtualPortBackingInfo();
            nicBacking.setPort(switchConnection);

            // Define the
            VirtualVmxnet3 virtualNICSpec = new VirtualVmxnet3();
            virtualNICSpec.setAddressType("manual");
            virtualNICSpec.setMacAddress(macAddress);
            virtualNICSpec.setBacking(nicBacking);

            VirtualDeviceConfigSpec nicSpec = new VirtualDeviceConfigSpec();
            nicSpec.setOperation(VirtualDeviceConfigSpecOperation.add);
            nicSpec.setDevice(virtualNICSpec);

            VirtualMachineConfigSpec vmConfig = new VirtualMachineConfigSpec();
            vmConfig.setDeviceChange(new VirtualDeviceConfigSpec[] {nicSpec});

            Task task = vm.reconfigVM_Task(vmConfig);

            if (task.waitForMe() == Task.SUCCESS)
            {
                String message =
                    "Virtual machine with name '" + nameVM
                        + "' associated to distributed port group '" + portGroupName + "'";

                LOGGER.info(message);
            }
            else
            {
                String message =
                    "Could not associate virtual machine with name '" + nameVM
                        + "' to port group '" + portGroupName + "'";
                throw new VirtualMachineException(message);
            }

        }
        catch (RemoteException e)
        {
            String message =
                "Could not associate virtual machine with name '" + nameVM + "' to port group '"
                    + portGroupName + "'";
            LOGGER.error(message);
            throw new VirtualMachineException(message);
        }
    }

    private ObjectContent[] getObjectProperties(final ManagedObjectReference collector,
        final ManagedObjectReference mobj, final String[] properties) throws RemoteException
    {
        if (mobj == null)
        {
            return null;
        }

        ManagedObjectReference usecoll = collector;
        if (usecoll == null)
        {
            usecoll = serviceInstance.getServiceContent().getPropertyCollector();
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

        VimPortType conn = serviceInstance.getServerConnection().getVimService();
        return conn.retrieveProperties(usecoll, new PropertyFilterSpec[] {filter});

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
