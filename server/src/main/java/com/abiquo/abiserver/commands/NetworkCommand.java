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

package com.abiquo.abiserver.commands;

import java.util.List;

import com.abiquo.abiserver.business.hibernate.pojohb.infrastructure.DatacenterHB;
import com.abiquo.abiserver.business.hibernate.pojohb.networking.IpPoolManagementHB;
import com.abiquo.abiserver.business.hibernate.pojohb.networking.NetworkConfigurationHB;
import com.abiquo.abiserver.business.hibernate.pojohb.networking.VlanNetworkHB;
import com.abiquo.abiserver.business.hibernate.pojohb.user.EnterpriseHB;
import com.abiquo.abiserver.business.hibernate.pojohb.user.UserHB;
import com.abiquo.abiserver.business.hibernate.pojohb.virtualappliance.VirtualDataCenterHB;
import com.abiquo.abiserver.exception.NetworkCommandException;
import com.abiquo.abiserver.networking.IPAddress;
import com.abiquo.abiserver.pojo.authentication.UserSession;
import com.abiquo.abiserver.pojo.networking.IpPoolManagement;

/**
 * This command collects all actions related to Networking features
 * 
 * @author jdevesa@abiquo.com
 */
public interface NetworkCommand
{
    /**
     * Assigns an arbitrary NIC resource into the default VLAN to the virtual machine.
     * 
     * @param user user who performs the action.
     * @param networkId the identifier of the network.
     * @param vmId identifier of the virtual machine.
     */
    public void assignDefaultNICResource(UserHB user, Integer networkId, Integer vmId)
        throws NetworkCommandException;

    /**
     * The same functionality than the previous method. But this one creates a new transaction.
     * 
     * @param userSession user who performs the action.
     * @param networkId the identifier of the network.
     * @param vmId identifier of the virtual machine.
     */
    public void assignDefaultNICResource(UserSession userSession, Integer networkId, Integer vmId)
        throws NetworkCommandException;

    /**
     * Creates a new VLAN network with private ranges.
     * 
     * @param userSession UserSession object with the information of the user that called this
     *            method
     * @param networkName the name of the network;
     * @param idNetwork identifier of the network that the vlan belongs to.
     * @param configuration configuration of the network.
     * @param defaultNetwork define if its a default network or not.
     * @return a new created {@link VlanNetworkHB} object.
     * @throws NetworkCommandException for encapsulate any non-runtime exception.
     */
    public VlanNetworkHB createPrivateVlanNetwork(UserSession userSession, String networkName,
        Integer idNetwork, NetworkConfigurationHB configuration, Boolean defaultNetwork)
        throws NetworkCommandException;

    /**
     * Deletes an existing VLAN.
     * 
     * @param userSession UserSession object with the information of the user that called this
     *            method
     * @param vlanNetworkId identifier of the vlan to delete
     * @return nothing.
     * @throws NetworkCommandException for encapsulate any non-runtime exception.
     */
    public void deleteVlanNetwork(UserSession userSession, Integer vlanNetworkId)
        throws NetworkCommandException;

    /**
     * Edits an existing VLAN.
     * 
     * @param networkName the name of the network;
     * @param vlanNetworkId identifier of the VLAN to edit.
     * @param configuration configuration of the network.
     * @param defaultNetwork define if its a default network or not.
     * @return a edited {@link VlanNetworkHB} object.
     * @throws NetworkCommandException for encapsulate any non-runtime exception.
     */
    public VlanNetworkHB editPrivateVlanNetwork(UserSession userSession, String networkName,
        Integer vlanNetworkId, NetworkConfigurationHB configuration, Boolean defaultNetwork)
        throws NetworkCommandException;

    /**
     * Return the list of gateways you can choose by a virtual machine.
     * 
     * @param userSession user who performs the action.
     * @param vmId identifier of the virtual machine.
     * @return a list of {@link IPAddress} as a result.
     * @throws NetworkCommandException to encapsulate any non-runtime exception.
     */
    public List<IPAddress> getListGatewaysByVirtualMachine(UserSession userSession, Integer vmId)
        throws NetworkCommandException;

    /**
     * Return the list of available network resources of a given VLAN.
     * 
     * @param userSession UserSession object with the information of the user that called this
     *            method
     * @param vlanId identifier of the VLAN.
     * @param offset first element to return.
     * @param numElem number of elements to return.
     * @param filterLike filters the search by similar values. Use it if you want to retrieve a
     *            similar IPAddress, MAC address or VLAN name.
     * @return a list of {@link IpPoolManagement} that matches the search.
     * @throws NetworkCommandException for encapsulate any non-runtime exception.
     */
    public List<IpPoolManagementHB> getListNetworkPoolAvailableByVLAN(UserSession userSession,
        Integer vlanId, Integer offset, Integer numElem, String filterLike)
        throws NetworkCommandException;

    /**
     * Return the list of network resources of a given VLAN.
     * 
     * @param userSession UserSession object with the information of the user that called this
     *            method
     * @param vlanId VLAN identifier.
     * @param offset first element to return.
     * @param numElem number of elements to return
     * @param filterLike filters the search by similar values. Use it if you want to retrieve a
     *            similar IPAddress, MAC address, VLAN name.
     * @param orderBy the order preferences of the query.
     * @param asc tell him if we want to order ascendant or descendant
     * @return a list of {@link IpPoolManagement} that matches the search.
     * @throws NetworkCommandException for encapsulate any non-runtime exception.
     */
    public List<IpPoolManagementHB> getListNetworkPoolByVLAN(UserSession userSession,
        Integer vlanId, Integer offset, Integer numElem, String filterLike, String orderBy,
        Boolean asc) throws NetworkCommandException;

    /**
     * Return the number of available network resources of a given VLAN. Used by paging purposes.
     * 
     * @param userSession UserSession object with the information of the user that called this
     *            method.
     * @param vlanId identifier of the VLAN.
     * @param filterLike filters the search by similar values. Use it if you want to retrieve a
     *            similar IPAddress, MAC address or VLAN name.
     * @return a number of {@link IpPoolManagement} that matches the search.
     * @throws NetworkCommandException for encapsulate any non-runtime exception.
     */
    public Integer getNumberNetworkPoolAvailableByVLAN(UserSession userSession, Integer vlanId,
        String filterLike) throws NetworkCommandException;

    /**
     * Return the number of network resources of a given VLAN.
     * 
     * @param userSession UserSession object with the information of the user that called this
     *            method
     * @param vlanId VLAN identifier.
     * @param filterLike filters the search by similar values. Use it if you want to retrieve a
     *            similar IPAddress, MAC address, VLAN name.
     * @return a list of {@link IpPoolManagement} that matches the search.
     * @throws NetworkCommandException for encapsulate any non-runtime exception.
     */
    public Integer getNumberNetworkPoolByVLAN(UserSession userSession, Integer vlanId,
        String filterLike) throws NetworkCommandException;

    /**
     * Lists all the NICs used by a Virtual Machine.
     * 
     * @param userSession UserSession object with the information of the user that called this
     *            method
     * @param virtualMachineId identifier of the Virtual Machine
     * @return a list of {@link IpPoolManagement} that point to the given Virtual Machine.
     * @throws NetworkCommandException for encapsulate any non-runtime exception.
     */
    public List<IpPoolManagementHB> getPrivateNICsByVirtualMachine(UserSession userSession,
        Integer virtualMachineId) throws NetworkCommandException;

    /**
     * Return the current gateway of a virtual machine.
     * 
     * @param userSession user who performs the action.
     * @param vmId identifier of the virtual machine.
     * @return the {@link IPAddress} object which defines the Gateway.
     * @throws NetworkCommandException to encapsulate any non-runtime exception.
     */
    public IPAddress getUsedGatewayByVirtualMachine(UserSession userSession, Integer vmId)
        throws NetworkCommandException;

    /**
     * Release a NIC resource from a Virtual Machine.
     * 
     * @param userSession UserSession object with the information of the user that called this
     *            method
     * @param ipPoolManagementId identifier of the resource.
     * @return null.
     * @throws NetworkCommandException for encapsulate any non-runtime exception.
     */
    public void releaseNICFromVirtualMachine(UserSession userSession, Integer ipPoolManagementId)
        throws NetworkCommandException;

    /**
     * Assigns the default gateway to the virtual machine.
     * 
     * @param userSession user who performs the action.
     * @param vmId identifier of the virtual machine.
     * @param gateway IP address of the gateway.
     * @return null.
     * @throws NetworkCommandException
     */
    public void requestGatewayForVirtualMachine(UserSession userSession, Integer vmId,
        IPAddress gateway) throws NetworkCommandException;

    /**
     * Assign a NIC resource to a Virtual Machine. The Resource is identified as its IPAddress and
     * the VLAN that stores it.
     * 
     * @param userSession UserSession object with the information of the user that called this
     *            method
     * @param vmId identifier of the Virtual Machine where the NIC will be stored.
     * @param ipPoolManagementId identifier of the ipPoolManagement.
     * @return null.
     * @throws NetworkCommandException for encapsulate any non-runtime exception.
     */
    public void requestNICforVirtualMachine(UserSession userSession, Integer vmId,
        Integer ipPoolManagementId) throws NetworkCommandException;

    /**
     * Helper method that return all the available masks for a class Type.
     * 
     * @param userSession userSession UserSession object with the information of the user that
     *            called this method
     * @param networkClass a String that identifies the class type. Only "A", "B" and "C" are
     *            accepted.
     * @throws NetworkCommandException for encapsulate any non-runtime exception.
     */
    public List<String> resolveNetworkMaskFromClassType(String classType)
        throws NetworkCommandException;

    /**
     * Helper method that retrieves all the available lists of possible networks for a given class
     * type and Network mask.
     * 
     * @param userSession userSession UserSession object with the information of the user that
     *            called this method
     * @param networkClass a String that identifies the class type. Only "A", "B" and "C" are
     *            accepted.
     * @param netmask mask of the network in the String way ("255.255.255.0" for instance).
     * @return a List of Lists of Strings that contains all the combinations of possible networks.
     * @throws NetworkCommandException for encapsulate any non-runtime exception.
     */
    public List<List<String>> resolveNetworksFromClassTypeAndMask(String classType, IPAddress mask)
        throws NetworkCommandException;

    /**
     * The NICs into a Virtual Machine are ordered. The order value represents the NICs eth0, eth1,
     * eth2 and so on when the machine is deployed. This method reorders the NICs giving the new
     * order of a single NIC.
     * 
     * @param userSession user object to register who performs the action.
     * @param ipPoolManagementId identifier of the object that stores the info of the NIC
     */
    public void reorderNICintoVM(UserSession userSession, Integer newOrder,
        Integer ipPoolManagementId) throws NetworkCommandException;

    /**
     * Check the private VLAN limits. (this is also called before try to create a virtua
     * datacenter).
     * 
     * @throws NetworkCommandException if the Hard Limit is exceeded.
     */
    public void checkPrivateVlan(VirtualDataCenterHB vdc, Integer datacenterId, EnterpriseHB enter,
        UserSession userSession) throws NetworkCommandException;
}
