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

package com.abiquo.abiserver.services.flex;

import java.util.ArrayList;
import java.util.List;

import com.abiquo.abiserver.abicloudws.IVirtualApplianceWS;
import com.abiquo.abiserver.abicloudws.VirtualApplianceWS;
import com.abiquo.abiserver.business.BusinessDelegateProxy;
import com.abiquo.abiserver.business.hibernate.pojohb.networking.IpPoolManagementHB;
import com.abiquo.abiserver.business.hibernate.pojohb.user.EnterpriseHB;
import com.abiquo.abiserver.commands.NetworkCommand;
import com.abiquo.abiserver.business.BusinessDelegateProxy;
import com.abiquo.abiserver.commands.NetworkCommand;
import com.abiquo.abiserver.commands.impl.NetworkCommandImpl;
import com.abiquo.abiserver.networking.IPAddress;
import com.abiquo.abiserver.pojo.authentication.UserSession;
import com.abiquo.abiserver.pojo.networking.IpPoolManagement;
import com.abiquo.abiserver.pojo.networking.NetworkConfiguration;
import com.abiquo.abiserver.pojo.networking.VlanNetwork;
import com.abiquo.abiserver.pojo.result.BasicResult;
import com.abiquo.abiserver.pojo.result.DataResult;
import com.abiquo.abiserver.pojo.result.ListRequest;
import com.abiquo.abiserver.pojo.result.ListResponse;
import com.abiquo.abiserver.pojo.user.Enterprise;

/**
 * This class defines all services related to Networking
 * 
 * @author Oliver
 */

public class NetworkingService
{

    /**
     * The command related to this service
     */
    NetworkCommand networkCommand;

    /**
     * Constructor The implemention of the BasicCommand and the ResourceLocator to be used is
     * defined here
     */
    public NetworkingService()
    {
        networkCommand = new NetworkCommandImpl();
    }

    /**
     * Create a new VLAN.
     * 
     * @param userSession UserSession object with the information of the user that called this
     *            method
     * @param networkId identifer of the network that the VLAN will belong to.
     * @param vlanName name of the Vlan. It should be unique by network.
     * @param configuration configuration of the network
     * @param defaultNetwork if the network is default or not. If its set to 'true' it will replace
     *            the previous default network.
     * @return a Data Result containing the created VLAN.
     */
    public BasicResult createVLAN(UserSession userSession, Integer networkId, String vlanName,
        NetworkConfiguration configuration, Boolean defaultNetwork)
    {
        DataResult<VlanNetwork> dataResult = new DataResult<VlanNetwork>();

        try
        {

            NetworkCommand proxy =
                BusinessDelegateProxy.getInstance(userSession, instantiateNetworkCommand(),
                    NetworkCommand.class);
            dataResult.setData(proxy.createPrivateVlanNetwork(userSession, vlanName, networkId,
                configuration.toPojoHB(), defaultNetwork).toPojo());
            dataResult.setSuccess(Boolean.TRUE);
        }
        catch (Exception e)
        {
            dataResult.setSuccess(Boolean.FALSE);
            dataResult.setMessage(e.getMessage());
        }

        return dataResult;
    }

    private NetworkCommand instantiateNetworkCommand()
    {
        NetworkCommand netComm;
        try
        {
            netComm =
                (NetworkCommand) Thread.currentThread().getContextClassLoader()
                    .loadClass("com.abiquo.abiserver.commands.impl.NetworkingCommandPremiumImpl")
                    .newInstance();
        }
        catch (Exception e)
        {
            netComm = new NetworkCommandImpl();
        }

        return netComm;
    }

    /**
     * Delete a VLAN. If there are machines with IPs of the VLAN used, it will be impossible to
     * delete it before to release them.
     * 
     * @param userSession user who performs the action.
     * @param vlanNetworkId identifier of the network.
     * @return a {@link BasicResult} object.
     */
    public BasicResult deleteVLAN(UserSession userSession, Integer vlanNetworkId)
    {
        BasicResult basicResult = new BasicResult();

        try
        {
            NetworkCommand proxy =
                BusinessDelegateProxy
                    .getInstance(userSession, networkCommand, NetworkCommand.class);
            proxy.deleteVlanNetwork(userSession, vlanNetworkId);
            basicResult.setSuccess(Boolean.TRUE);
        }
        catch (Exception e)
        {
            basicResult.setSuccess(Boolean.FALSE);
            basicResult.setMessage(e.getMessage());
        }

        return basicResult;
    }

    /**
     * Edit an existing private VLAN.
     * 
     * @param userSession UserSession object with the information of the user that called this
     *            method
     * @param vlanNetworkId network to edit.
     * @param vlanName name of the VLAN.
     * @param configuration configuration of the network
     * @param defaultNetwork if the network is default or not. If its set to 'true' it will replace
     *            the previous default network.
     * @return a Data Result containing the created VLAN.
     */
    public BasicResult editVLAN(UserSession userSession, Integer vlanNetworkId, String vlanName,
        NetworkConfiguration configuration, Boolean defaultNetwork)
    {
        DataResult<VlanNetwork> dataResult = new DataResult<VlanNetwork>();

        try
        {
            NetworkCommand proxy =
                BusinessDelegateProxy
                    .getInstance(userSession, networkCommand, NetworkCommand.class);
            dataResult.setData(proxy.editPrivateVlanNetwork(userSession, vlanName, vlanNetworkId,
                configuration.toPojoHB(), defaultNetwork).toPojo());
            dataResult.setSuccess(Boolean.TRUE);
        }
        catch (Exception e)
        {
            dataResult.setSuccess(Boolean.FALSE);
            dataResult.setMessage(e.getMessage());
        }

        return dataResult;

    }

    /**
     * Get the available IPs of the given VLAN.
     * 
     * @param userSession UserSession object with the information of the user that called this
     *            method
     * @param vlanId identifier of the VLAN.
     * @param listRequest object that stores the options to filter the search.
     * @return a DataResult containing the list of available IPs in its Data.
     */
    public DataResult<ListResponse<IpPoolManagement>> getAvailableVirtualMachineNICsByVLAN(
        UserSession userSession, Integer vlanId, ListRequest listRequest)
    {
        DataResult<ListResponse<IpPoolManagement>> dataResult =
            new DataResult<ListResponse<IpPoolManagement>>();

        try
        {
            NetworkCommand proxy =
                BusinessDelegateProxy
                    .getInstance(userSession, networkCommand, NetworkCommand.class);
            ListResponse<IpPoolManagement> listResult = new ListResponse<IpPoolManagement>();
            List<IpPoolManagementHB> listPoolAvailable =
                proxy.getListNetworkPoolAvailableByVLAN(userSession, vlanId,
                    listRequest.getOffset(), listRequest.getNumberOfNodes(),
                    listRequest.getFilterLike());
            Integer listPoolNumberAvailable =
                proxy.getNumberNetworkPoolAvailableByVLAN(userSession, vlanId,
                    listRequest.getFilterLike());

            List<IpPoolManagement> listOfAddress = new ArrayList<IpPoolManagement>();
            for (IpPoolManagementHB ipPool : listPoolAvailable)
            {
                listOfAddress.add(ipPool.toPojo());
            }

            listResult.setList(listOfAddress);
            listResult.setTotalNumEntities(listPoolNumberAvailable);

            dataResult.setData(listResult);
            dataResult.setSuccess(Boolean.TRUE);

        }
        catch (Exception e)
        {
            dataResult.setSuccess(Boolean.FALSE);
            dataResult.setMessage(e.getMessage());
        }

        return dataResult;
    }

    public BasicResult getInfoDHCPServer(UserSession userSession, Integer vdcId)
    {
        DataResult<String> dataResult = new DataResult<String>();

        Object[] argsInfo = new Object[2];
        argsInfo[0] = userSession;
        argsInfo[1] = vdcId;

        try
        {
            NetworkCommand proxy =
                BusinessDelegateProxy
                    .getInstance(userSession, networkCommand, NetworkCommand.class);
            dataResult.setData(proxy.getInfoDHCPServer(userSession, vdcId));
            dataResult.setSuccess(Boolean.TRUE);
        }
        catch (Exception e)
        {
            dataResult.setSuccess(Boolean.FALSE);
            dataResult.setMessage(e.getMessage());
        }

        return dataResult;
    }

    /**
     * For any DataCenter, lists all the enterprises that are using its networks.
     * 
     * @param userSession UserSession object with the information of the user that called this
     *            method
     * @param datacenterId identifier of the DataCenter.
     * @param listRequest object that stores the options to filter the search.
     * @return a DataResult containing the list of enterprises in its Data.
     */
    public BasicResult getEnterprisesWithNetworkInDataCenter(UserSession userSession,
        Integer datacenterId, ListRequest listRequest)
    {
        DataResult<ListResponse<Enterprise>> dataResult =
            new DataResult<ListResponse<Enterprise>>();

        try
        {
            NetworkCommand proxy =
                BusinessDelegateProxy
                    .getInstance(userSession, networkCommand, NetworkCommand.class);

            // Execute the commands
            List<EnterpriseHB> response =
                proxy.getEnterprisesWithNetworksByDatacenter(userSession, datacenterId,
                    listRequest.getOffset(), listRequest.getNumberOfNodes(),
                    listRequest.getFilterLike());
            Integer responseNumber =
                proxy.getNumberEnterprisesWithNetworksByDatacenter(userSession, datacenterId,
                    listRequest.getFilterLike());

            // Prepare the transfer objects
            List<Enterprise> listEnt = new ArrayList<Enterprise>();
            for (EnterpriseHB ent : response)
            {
                listEnt.add(ent.toPojo());
            }

            // Prepare the data
            ListResponse<Enterprise> listResponse = new ListResponse<Enterprise>();
            listResponse.setList(listEnt);
            listResponse.setTotalNumEntities(responseNumber);

            dataResult.setData(listResponse);
            dataResult.setSuccess(Boolean.TRUE);
        }
        catch (Exception e)
        {
            dataResult.setSuccess(Boolean.FALSE);
            dataResult.setMessage(e.getMessage());
        }

        return dataResult;

    }

    /**
     * Return the list of Gateways we can choose from a Virtual Machine.
     * 
     * @param userSession user who performs the action.
     * @param vmId identifier of the virtual machine.
     * @return a DataResult containing a list of Virtual Machines.
     */
    public BasicResult getGatewayListByVirtualMachine(UserSession userSession, Integer vmId)
    {
        DataResult<List<IPAddress>> dataResult = new DataResult<List<IPAddress>>();

        try
        {
            NetworkCommand proxy =
                BusinessDelegateProxy
                    .getInstance(userSession, networkCommand, NetworkCommand.class);

            dataResult.setData(proxy.getListGatewaysByVirtualMachine(userSession, vmId));
            dataResult.setSuccess(Boolean.TRUE);

        }
        catch (Exception e)
        {
            dataResult.setSuccess(Boolean.FALSE);
            dataResult.setMessage(e.getMessage());
        }

        return dataResult;
    }

    /**
     * Return the list of Gateways we have chosen for a Virtual Machine.
     * 
     * @param userSession user who performs the action.
     * @param vmId identifier of the virtual machine.
     * @return a DataResult containing a list of Virtual Machines.
     */
    public BasicResult getGatewayUsedByVirtualMachine(UserSession userSession, Integer vmId)
    {
        DataResult<IPAddress> dataResult = new DataResult<IPAddress>();

        try
        {
            NetworkCommand proxy =
                BusinessDelegateProxy
                    .getInstance(userSession, networkCommand, NetworkCommand.class);

            dataResult.setData(proxy.getUsedGatewayByVirtualMachine(userSession, vmId));
            dataResult.setSuccess(Boolean.TRUE);

        }
        catch (Exception e)
        {
            dataResult.setSuccess(false);
            dataResult.setMessage(e.getMessage());
        }

        return dataResult;
    }

    /**
     * Get the list of {@link NetworkManagement} objects associated by a Enterprise. Through this
     * method you can control which IPs are used and which not.
     * 
     * @param userSession UserSession object with the information of the user that called this
     *            method.
     * @param enterpriseId identifier of the enterprise.
     * @param listRequest object that stores the options to filter the search.
     * @return a DataResult containing all the IPs managed by an Enterprise
     */
    public BasicResult getNetworkPoolInfoByEnterprise(UserSession userSession,
        Integer enterpriseId, ListRequest listRequest)
    {
        DataResult<ListResponse<IpPoolManagement>> dataResult =
            new DataResult<ListResponse<IpPoolManagement>>();

        try
        {
            NetworkCommand proxy =
                BusinessDelegateProxy
                    .getInstance(userSession, networkCommand, NetworkCommand.class);

            String orderBy =
                (listRequest.getOrderBy() != null) ? listRequest.getOrderBy() : new String();

            // Get the responses from the command (proxy var)
            List<IpPoolManagementHB> listPoolAvailable =
                proxy.getListNetworkPoolByEnterprise(userSession, enterpriseId,
                    listRequest.getOffset(), listRequest.getNumberOfNodes(),
                    listRequest.getFilterLike(), orderBy, listRequest.getAsc());
            Integer netmanValues =
                proxy.getNumberNetworkPoolByEnterprise(userSession, enterpriseId,
                    listRequest.getFilterLike());

            // Build the response
            List<IpPoolManagement> listResp = new ArrayList<IpPoolManagement>();
            for (IpPoolManagementHB currentNetman : listPoolAvailable)
            {
                listResp.add(currentNetman.toPojo());
            }

            ListResponse<IpPoolManagement> listResult = new ListResponse<IpPoolManagement>();
            listResult.setList(listResp);
            listResult.setTotalNumEntities(netmanValues);

            dataResult.setData(listResult);
            dataResult.setSuccess(Boolean.TRUE);
        }
        catch (Exception e)
        {
            dataResult.setSuccess(Boolean.FALSE);
            dataResult.setMessage(e.getMessage());
        }

        return dataResult;
    }

    /**
     * Get the list of {@link NetworkManagement} objects associated by a Virtual DataCenter. Through
     * this method you can control which IPs are used and which not.
     * 
     * @param userSession UserSession object with the information of the user that called this
     *            method
     * @param virtualDataCenterId identifier of the Virtual DataCenter
     * @param listRequest object that stores the options to filter the search.
     * @return a list of all the IPs managed by a Virtual DataCenter.
     */
    public BasicResult getNetworkPoolInfoByVDC(UserSession userSession,
        Integer virtualDataCenterId, ListRequest listRequest)
    {
        DataResult<ListResponse<IpPoolManagement>> dataResult =
            new DataResult<ListResponse<IpPoolManagement>>();

        try
        {
            NetworkCommand proxy =
                BusinessDelegateProxy
                    .getInstance(userSession, networkCommand, NetworkCommand.class);

            String orderBy =
                (listRequest.getOrderBy() != null) ? listRequest.getOrderBy() : new String();

            // Get the responses from the command (proxy var)
            List<IpPoolManagementHB> listPoolAvailable =
                proxy.getListNetworkPoolByVDC(userSession, virtualDataCenterId,
                    listRequest.getOffset(), listRequest.getNumberOfNodes(),
                    listRequest.getFilterLike(), orderBy, listRequest.getAsc());
            Integer netmanValues =
                proxy.getNumberNetworkPoolByVDC(userSession, virtualDataCenterId,
                    listRequest.getFilterLike());

            // Build the response
            List<IpPoolManagement> listResp = new ArrayList<IpPoolManagement>();
            for (IpPoolManagementHB currentNetman : listPoolAvailable)
            {
                listResp.add(currentNetman.toPojo());
            }

            ListResponse<IpPoolManagement> listResult = new ListResponse<IpPoolManagement>();
            listResult.setList(listResp);
            listResult.setTotalNumEntities(netmanValues);

            dataResult.setData(listResult);
            dataResult.setSuccess(Boolean.TRUE);
        }
        catch (Exception e)
        {
            dataResult.setSuccess(Boolean.FALSE);
            dataResult.setMessage(e.getMessage());
        }

        return dataResult;

    }

    /**
     * Get the list of {@link NetworkManagement} objects associated by a VLAN. Through this method
     * you can control which IPs are used and which not.
     * 
     * @param userSession UserSession object with the information of the user that called this
     *            method
     * @param vlanId identifier of the Virtual DataCenter
     * @param listRequest object that stores the options to filter the search.
     * @return a list of all the IPs managed by a Virtual DataCenter.
     */
    public DataResult<ListResponse<IpPoolManagement>> getNetworkPoolInfoByVLAN(
        UserSession userSession, Integer vlanId, ListRequest listRequest)
    {
        DataResult<ListResponse<IpPoolManagement>> dataResult =
            new DataResult<ListResponse<IpPoolManagement>>();

        try
        {
            NetworkCommand proxy =
                BusinessDelegateProxy
                    .getInstance(userSession, networkCommand, NetworkCommand.class);

            String orderBy =
                (listRequest.getOrderBy() != null) ? listRequest.getOrderBy() : new String();

            // Get the responses from the command (proxy var)
            List<IpPoolManagementHB> listPoolAvailable =
                proxy.getListNetworkPoolByVLAN(userSession, vlanId, listRequest.getOffset(),
                    listRequest.getNumberOfNodes(), listRequest.getFilterLike(), orderBy,
                    listRequest.getAsc());
            Integer netmanValues =
                proxy.getNumberNetworkPoolByVLAN(userSession, vlanId, listRequest.getFilterLike());

            // Build the response
            List<IpPoolManagement> listResp = new ArrayList<IpPoolManagement>();
            for (IpPoolManagementHB currentNetman : listPoolAvailable)
            {
                listResp.add(currentNetman.toPojo());
            }

            ListResponse<IpPoolManagement> listResult = new ListResponse<IpPoolManagement>();
            listResult.setList(listResp);
            listResult.setTotalNumEntities(netmanValues);

            dataResult.setData(listResult);
            dataResult.setSuccess(Boolean.TRUE);
        }
        catch (Exception e)
        {
            dataResult.setSuccess(Boolean.FALSE);
            dataResult.setMessage(e.getMessage());
        }

        return dataResult;
    }

    /**
     * Lists all the NICs used by a Virtual Machine.
     * 
     * @param userSession UserSession object with the information of the user that called this
     *            method
     * @param virtualMachineId identifier of the Virtual Machine
     * @return a {@link DataResult} object with a list of {@link IpPoolManagement} objects in its
     *         data.
     */
    public DataResult<List<IpPoolManagement>> getNICsByVirtualMachine(UserSession userSession,
        Integer virtualMachineId)
    {
        DataResult<List<IpPoolManagement>> dataResult = new DataResult<List<IpPoolManagement>>();

        try
        {
            NetworkCommand proxy =
                BusinessDelegateProxy
                    .getInstance(userSession, networkCommand, NetworkCommand.class);

            List<IpPoolManagementHB> ipPoolHB =
                proxy.getPrivateNICsByVirtualMachine(userSession, virtualMachineId);

            List<IpPoolManagement> ipPool = new ArrayList<IpPoolManagement>();
            for (IpPoolManagementHB ip : ipPoolHB)
            {
                ipPool.add(ip.toPojo());
            }

            dataResult.setData(ipPool);
            dataResult.setSuccess(Boolean.TRUE);
        }
        catch (Exception e)
        {
            dataResult.setSuccess(Boolean.FALSE);
            dataResult.setMessage(e.getMessage());
        }

        return dataResult;

    }

    /**
     * Release a NIC resource from a Virtual Machine.
     * 
     * @param userSession UserSession object with the information of the user that called this
     *            method
     * @param ipPoolManagementId identifier of the resource.
     * @return a {@link BasicResult} object just saying if the method has successfully finished.
     */
    public BasicResult releaseNICfromVirtualMachine(UserSession userSession,
        Integer ipPoolManagementId)
    {
        BasicResult dataResult = new BasicResult();

        try
        {
            NetworkCommand proxy =
                BusinessDelegateProxy
                    .getInstance(userSession, networkCommand, NetworkCommand.class);
            proxy.releaseNICFromVirtualMachine(userSession, ipPoolManagementId);

            dataResult.setSuccess(Boolean.TRUE);
        }
        catch (Exception e)
        {
            dataResult.setSuccess(Boolean.FALSE);
            dataResult.setMessage(e.getMessage());
        }

        return dataResult;
    }

    /**
     * Assign a new gateway for a virtual machine.
     * 
     * @param userSession user who performs the action.
     * @param vmId
     * @param gateway
     * @return
     */
    public BasicResult requestGatewayForVirtualMachine(UserSession userSession, Integer vmId,
        IPAddress gateway)
    {

        BasicResult dataResult = new BasicResult();

        try
        {
            NetworkCommand proxy =
                BusinessDelegateProxy
                    .getInstance(userSession, networkCommand, NetworkCommand.class);

            IPAddress gatewayAddress =
                (gateway == null) ? IPAddress.newIPAddress("0.0.0.0") : gateway;

            proxy.requestGatewayForVirtualMachine(userSession, vmId, gatewayAddress);

            dataResult.setSuccess(Boolean.TRUE);
        }
        catch (Exception e)
        {
            dataResult.setSuccess(Boolean.FALSE);
            dataResult.setMessage(e.getMessage());
        }

        return dataResult;
    }

    /**
     * Assign a NIC resource to a Virtual Machine. The Resource is identified as its IPAddress and
     * the VLAN that stores it.
     * 
     * @param userSession UserSession object with the information of the user that called this
     *            method
     * @param vlanId identifier of the VLAN.
     * @param vmId identifier of the Virtual Machine where the NIC will be stored.
     * @param requestedIP IP address of the resource.
     * @return a {@link BasicResult} object just saying if the method has successfully finished.
     */
    public BasicResult requestNICforVirtualMachine(UserSession userSession, Integer vmId,
        Integer ipPoolManagementId)
    {
        BasicResult dataResult = new BasicResult();

        try
        {
            NetworkCommand proxy =
                BusinessDelegateProxy
                    .getInstance(userSession, networkCommand, NetworkCommand.class);

            proxy.requestNICforVirtualMachine(userSession, vmId, ipPoolManagementId);

            dataResult.setSuccess(Boolean.TRUE);
        }
        catch (Exception e)
        {
            dataResult.setSuccess(Boolean.FALSE);
            dataResult.setMessage(e.getMessage());
        }

        return dataResult;
    }

    /**
     * Helper method that return all the available masks for a class Type.
     * 
     * @param userSession userSession UserSession object with the information of the user that
     *            called this method
     * @param networkClass a String that identifies the class type. Only "A", "B" and "C" are
     *            accepted.
     * @return a {@link BasicResult} object just saying if the method has successfully finished.
     */
    public BasicResult resolveMaskForNetworkClass(UserSession userSession, String networkClass)
    {
        DataResult<List<String>> dataResult = new DataResult<List<String>>();

        try
        {
            NetworkCommand proxy =
                BusinessDelegateProxy
                    .getInstance(userSession, networkCommand, NetworkCommand.class);

            dataResult.setData(proxy.resolveNetworkMaskFromClassType(networkClass));

            dataResult.setSuccess(Boolean.TRUE);
        }
        catch (Exception e)
        {
            dataResult.setSuccess(Boolean.FALSE);
            dataResult.setMessage(e.getMessage());
        }

        return dataResult;
    }

    /**
     * Helper method that retrieves all the available lists of possible networks for a given class
     * type and Network mask.
     * 
     * @param userSession userSession UserSession object with the information of the user that
     *            called this method
     * @param networkClass a String that identifies the class type. Only "A", "B" and "C" are
     *            accepted.
     * @param netmask mask of the network in the String way ("255.255.255.0" for instance).
     * @return a {@link DataResult} with a List of Lists of Strings that contains all the
     *         combinations of possible networks.
     */
    public BasicResult resolvePossibleNetworks(UserSession userSession, String networkClass,
        IPAddress netmask)
    {
        DataResult<List<List<String>>> dataResult = new DataResult<List<List<String>>>();
        try
        {
            NetworkCommand proxy =
                BusinessDelegateProxy
                    .getInstance(userSession, networkCommand, NetworkCommand.class);

            dataResult.setData(proxy.resolveNetworksFromClassTypeAndMask(networkClass, netmask));
            dataResult.setSuccess(Boolean.TRUE);
        }
        catch (Exception e)
        {
            dataResult.setSuccess(Boolean.FALSE);
            dataResult.setMessage(e.getMessage());
        }

        return dataResult;
    }

    /**
     * The NICs into a Virtual Machine are ordered. The order value represents the NICs eth0, eth1,
     * eth2 and so on when the machine is deployed. This method reorders the NICs giving the new
     * order of a single NIC.
     * 
     * @param userSession user object to register who performs the action.
     * @param ipPoolManagementId identifier of the object that stores the info of the NIC
     */
    public BasicResult reorderNICintoVM(UserSession userSession, Integer newOrder,
        Integer ipPoolManagementId)
    {
        BasicResult basicResult = new BasicResult();

        try
        {
            NetworkCommand netComm =
                BusinessDelegateProxy.getInstance(userSession, new NetworkCommandImpl(),
                    NetworkCommand.class);
            netComm.reorderNICintoVM(userSession, newOrder, ipPoolManagementId);
            basicResult.setSuccess(Boolean.TRUE);
        }
        catch (Exception e)
        {
            basicResult.setSuccess(Boolean.FALSE);
            basicResult.setMessage(e.getMessage());
        }

        return basicResult;
    }
}
