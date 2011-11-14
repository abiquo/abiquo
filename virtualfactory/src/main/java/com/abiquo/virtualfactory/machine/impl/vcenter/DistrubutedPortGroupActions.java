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

/**
 * 
 */
package com.abiquo.virtualfactory.machine.impl.vcenter;

import java.rmi.RemoteException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.abiquo.virtualfactory.exception.VirtualMachineException;
import com.vmware.vim25.DVPortgroupConfigInfo;
import com.vmware.vim25.DVPortgroupConfigSpec;
import com.vmware.vim25.DistributedVirtualPortgroupPortgroupType;
import com.vmware.vim25.DistributedVirtualSwitchPortConnection;
import com.vmware.vim25.DynamicProperty;
import com.vmware.vim25.HostNotConnected;
import com.vmware.vim25.ManagedObjectReference;
import com.vmware.vim25.ObjectContent;
import com.vmware.vim25.ObjectSpec;
import com.vmware.vim25.PropertyFilterSpec;
import com.vmware.vim25.PropertySpec;
import com.vmware.vim25.StringPolicy;
import com.vmware.vim25.VMwareDVSPortSetting;
import com.vmware.vim25.VMwareDVSPortgroupPolicy;
import com.vmware.vim25.VimPortType;
import com.vmware.vim25.VirtualDeviceConfigSpec;
import com.vmware.vim25.VirtualDeviceConfigSpecOperation;
import com.vmware.vim25.VirtualE1000;
import com.vmware.vim25.VirtualEthernetCard;
import com.vmware.vim25.VirtualEthernetCardDistributedVirtualPortBackingInfo;
import com.vmware.vim25.VirtualMachineConfigSpec;
import com.vmware.vim25.VirtualMachineConnectionState;
import com.vmware.vim25.VmwareDistributedVirtualSwitchVlanIdSpec;
import com.vmware.vim25.VmwareUplinkPortTeamingPolicy;
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

    private final static String TEAMING_POLICY_DEFAULT = "loadbalance_srcid";

    private final static Integer PORTS_BY_PORT_GROUP_DEFAULT = 128;

    private final static Integer MAX_PORTS_BY_PORT_GROUP_DEFAULT = 3192;

    /**
     * Public construction.
     * 
     * @param serviceInstance serviceInstance connection to a VDC.
     */
    public DistrubutedPortGroupActions(final ServiceInstance serviceInstance)
    {
        this.serviceInstance = serviceInstance;
    }

    /**
     * Create a binding from a virtual machine NIC to a port group.
     * 
     * @param nameVM name of the virtual machine to use.
     * @param portGroupName port group name to associate
     * @param macAddress mac address of the NIC
     * @throws VirtualMachineException encapsulate any exception
     */
    public void attachVirtualMachineNICToPortGroup(final String nameVM, final String portGroupName,
        final String macAddress) throws VirtualMachineException
    {
        Folder fold = serviceInstance.getRootFolder();

        try
        {
            InventoryNavigator navigator = new InventoryNavigator(fold);
            // Get the VirtualMachine
            VirtualMachine vm;
            VirtualMachineConnectionState state;

            do
            {
                // TODO: This is dangerous!!! Think in something else.
                // retrieve the virtual machine while the vcenter refreshes the state.
                vm = (VirtualMachine) navigator.searchManagedEntity("VirtualMachine", nameVM);
            }
            while (vm == null || vm.getConfig() == null);

            state = vm.getRuntime().connectionState;

            if (!state.name().equalsIgnoreCase("connected"))
            {
                String message =
                    "The virtual machine "
                        + nameVM
                        + " it has an inconsistent state in this point of the deployment. Current state: "
                        + state.name()
                        + ". Only 'connected' state available. It may happens due the incompatibility between Abiquo's HA and vCenter. Aborting";
                throw new VirtualMachineException(message);
            }

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

            // Define the NIC driver
            VirtualEthernetCard virtualNICSpec = new VirtualE1000();
            virtualNICSpec.setAddressType("manual");
            virtualNICSpec.setMacAddress(macAddress);
            virtualNICSpec.setBacking(nicBacking);
            virtualNICSpec.setKey(4);

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
                        + "' to port group '" + portGroupName + "'.";
                throw new VirtualMachineException(message + "vSphere exception: ");
            }

        }
        catch (HostNotConnected e)
        {
            String message =
                "Could not associate virtual machine with name '"
                    + nameVM
                    + "' to port group '"
                    + portGroupName
                    + "' because the host that stores the virtual machine '"
                    + nameVM
                    + "' is not connected. It may happens due the incompatibility between Abiquo's HA and vCenter.";
            LOGGER.error(message);
            throw new VirtualMachineException(message);
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
    public String createPortGroupInDVS(final String dvsName, final String networkName,
        final Integer vlanTag) throws VirtualMachineException
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

            VmwareUplinkPortTeamingPolicy uplinkPolicy = new VmwareUplinkPortTeamingPolicy();
            StringPolicy sp = new StringPolicy();
            sp.setValue(getTeamingPolicy());
            uplinkPolicy.policy = sp;

            VMwareDVSPortSetting dvsPortSettings = new VMwareDVSPortSetting();
            dvsPortSettings.setVlan(vlanSpec);
            dvsPortSettings.uplinkTeamingPolicy = uplinkPolicy;

            // Define the port group
            DVPortgroupConfigSpec dvPortGroup = new DVPortgroupConfigSpec();
            dvPortGroup.setName(portGroupName);
            dvPortGroup.setNumPorts(getNumPorts());
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
     * Delete a {@link DistributedVirtualPortgroup}.
     * 
     * @param portGroup
     * @throws VirtualMachineException
     * @throws Exception
     */
    public void deletePortGroup(final DistributedVirtualPortgroup portGroup)
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
     * Get the port group into a Distributed Virtual Switch.
     * 
     * @param dvsName name of the Distributed Virtual Switch.
     * @param portGroupName PortGroup name we search for.
     * @return a {@link DistributedVirtualPortgroup} instance.
     * @throws VirtualMachineException
     * @throws Exception
     */
    public DistributedVirtualPortgroup getPortGroup(final String dvsName, final String portGroupName)
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

    /**
     * @return
     */
    private Integer getNumPorts()
    {
        if (System.getProperty("abiquo.dvs.portgroup.maxsize") != null)
        {
            try
            {
                Integer numPorts =
                    Integer.valueOf(System.getProperty("abiquo.dvs.portgroup.maxsize").trim());

                if (numPorts < 1 || numPorts > 3192)
                {
                    LOGGER
                        .info("Property 'abiquo.dvs.portgroup.maxsize' to configure new DVS's port group invalid. Valid range in [1, 3192]. "
                            + "Using default value '" + PORTS_BY_PORT_GROUP_DEFAULT + "'");
                    return PORTS_BY_PORT_GROUP_DEFAULT;
                }

                return numPorts;
            }
            catch (Exception e)
            {
                LOGGER
                    .info("Property 'abiquo.dvs.portgroup.maxsize' to configure new DVS's port group invalid. Using default value '"
                        + PORTS_BY_PORT_GROUP_DEFAULT + "'");
                return PORTS_BY_PORT_GROUP_DEFAULT;
            }
        }
        else
        {
            LOGGER
                .info("Property 'abiquo.dvs.portgroup.maxsize' to configure new DVS's port group does not exist. Using default value '"
                    + PORTS_BY_PORT_GROUP_DEFAULT + "'");
            return PORTS_BY_PORT_GROUP_DEFAULT;
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
     * Get the teaming policy from system properties. If the property is null or invalid, use the
     * default one.
     * 
     * @return the teming policy.
     */
    private String getTeamingPolicy()
    {
        if (System.getProperty("abiquo.dvs.portgroup.loadsharingmechanism") != null)
        {
            String sharingMechanism =
                System.getProperty("abiquo.dvs.portgroup.loadsharingmechanism").trim();
            if (!sharingMechanism.equals("loadbalance_ip")
                && !sharingMechanism.equals(TEAMING_POLICY_DEFAULT))
            {
                LOGGER
                    .info("Property 'abiquo.dvs.portgroup.loadsharingmechanism' to configure new DVS's port group invalid. Using default value '"
                        + TEAMING_POLICY_DEFAULT + "'");
                return TEAMING_POLICY_DEFAULT;
            }
            return sharingMechanism;
        }
        else
        {
            LOGGER
                .info("Property 'abiquo.dvs.portgroup.loadsharingmechanism' to configure new DVS's port group does not exist. Using default value '"
                    + TEAMING_POLICY_DEFAULT + "'");
            return TEAMING_POLICY_DEFAULT;
        }
    }

}
