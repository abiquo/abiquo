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

package com.abiquo.abiserver.commands.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import com.abiquo.abiserver.business.hibernate.pojohb.infrastructure.DatacenterHB;
import com.abiquo.abiserver.business.hibernate.pojohb.networking.DHCPServiceHB;
import com.abiquo.abiserver.business.hibernate.pojohb.networking.IpPoolManagementHB;
import com.abiquo.abiserver.business.hibernate.pojohb.networking.NetworkConfigurationHB;
import com.abiquo.abiserver.business.hibernate.pojohb.networking.NetworkHB;
import com.abiquo.abiserver.business.hibernate.pojohb.networking.VlanNetworkHB;
import com.abiquo.abiserver.business.hibernate.pojohb.service.RemoteServiceHB;
import com.abiquo.abiserver.business.hibernate.pojohb.service.RemoteServiceType;
import com.abiquo.abiserver.business.hibernate.pojohb.user.EnterpriseHB;
import com.abiquo.abiserver.business.hibernate.pojohb.user.UserHB;
import com.abiquo.abiserver.business.hibernate.pojohb.virtualappliance.VirtualDataCenterHB;
import com.abiquo.abiserver.business.hibernate.pojohb.virtualappliance.VirtualappHB;
import com.abiquo.abiserver.business.hibernate.pojohb.virtualappliance.VirtualmachineHB;
import com.abiquo.abiserver.business.hibernate.pojohb.virtualhardware.ResourceAllocationSettingData;
import com.abiquo.abiserver.commands.BasicCommand;
import com.abiquo.abiserver.commands.NetworkCommand;
import com.abiquo.abiserver.config.AbiConfigManager;
import com.abiquo.abiserver.exception.InvalidIPAddressException;
import com.abiquo.abiserver.exception.NetworkCommandException;
import com.abiquo.abiserver.exception.PersistenceException;
import com.abiquo.abiserver.networking.IPAddress;
import com.abiquo.abiserver.networking.IPNetworkRang;
import com.abiquo.abiserver.networking.NetworkResolver;
import com.abiquo.abiserver.persistence.DAOFactory;
import com.abiquo.abiserver.persistence.dao.infrastructure.DataCenterDAO;
import com.abiquo.abiserver.persistence.dao.networking.DHCPServiceDAO;
import com.abiquo.abiserver.persistence.dao.networking.IpPoolManagementDAO;
import com.abiquo.abiserver.persistence.dao.networking.NetworkConfigurationDAO;
import com.abiquo.abiserver.persistence.dao.networking.NetworkDAO;
import com.abiquo.abiserver.persistence.dao.networking.VlanNetworkDAO;
import com.abiquo.abiserver.persistence.dao.user.UserDAO;
import com.abiquo.abiserver.persistence.dao.virtualappliance.VirtualApplianceDAO;
import com.abiquo.abiserver.persistence.dao.virtualappliance.VirtualDataCenterDAO;
import com.abiquo.abiserver.persistence.dao.virtualappliance.VirtualMachineDAO;
import com.abiquo.abiserver.persistence.dao.virtualhardware.ResourceAllocationSettingDataDAO;
import com.abiquo.abiserver.persistence.hibernate.HibernateDAOFactory;
import com.abiquo.abiserver.pojo.authentication.UserSession;
import com.abiquo.abiserver.pojo.networking.NetworkConfiguration;
import com.abiquo.ovfmanager.cim.CIMTypesUtils.CIMResourceTypeEnum;
import com.abiquo.tracer.ComponentType;
import com.abiquo.tracer.EventType;
import com.abiquo.tracer.SeverityType;

/**
 * This class extends all the methods of the NetworkCommand.
 * 
 * @author jdevesa@abiquo.com
 */
public class NetworkCommandImpl extends BasicCommand implements NetworkCommand
{
    /**
     * DAOFactory to create DAOs
     */
    protected DAOFactory factory;

    // Define the inner class to sort the ipPoolManagement
    class IpPoolManagementComparator implements Comparator<IpPoolManagementHB>
    {
        public int compare(IpPoolManagementHB ip1, IpPoolManagementHB ip2)
        {
            return Integer.valueOf(ip1.getRasd().getConfigurationName())
                - Integer.valueOf(ip2.getRasd().getConfigurationName());
        }
    }

    /**
     * Default constructor
     */
    public NetworkCommandImpl()
    {
        factory = HibernateDAOFactory.instance();
    }

    @Override
    public void assignDefaultNICResource(UserHB user, Integer networkId, Integer vmId)
        throws NetworkCommandException
    {
        try
        {
            // NOTE: this method needs an embedded transaction

            // Define the needed DAOs
            VlanNetworkDAO vlanNetDAO = factory.getVlanNetworkDAO();
            IpPoolManagementDAO ipPoolDAO = factory.getIpPoolManagementDAO();
            VirtualMachineDAO vmDAO = factory.getVirtualMachineDAO();
            VirtualApplianceDAO vappDAO = factory.getVirtualApplianceDAO();
            ResourceAllocationSettingDataDAO rasdDAO =
                factory.getResourceAllocationSettingDataDAO();
            // Get the default VLAN
            VlanNetworkHB vlanHB = vlanNetDAO.getDefaultVLAN(networkId);

            DHCPServiceHB dhcpServiceHB =
                (DHCPServiceHB) vlanHB.getConfiguration().getDhcpService();

            // Define the next available IP for the VLAN
            IpPoolManagementHB nextIp =
                getNextAvailableIP(dhcpServiceHB.getDhcpServiceId(), vlanHB.getConfiguration()
                    .getGateway());

            // Find the virtual machine and the virtual appliance by its ID
            VirtualmachineHB vmHB = vmDAO.findById(vmId);
            VirtualappHB vapp = vappDAO.getVirtualAppByVirtualMachine(vmId);

            // Generate the resource allocation setting data associed
            ResourceAllocationSettingData rasd =
                this.assignPrivateMACResourceRASD(nextIp.getMac(), vlanHB.getNetworkName());
            rasd.setConfigurationName("0");
            rasdDAO.makePersistent(rasd);

            // Associate the resource with the values
            nextIp.setVlanNetworkId(vlanHB.getVlanNetworkId());
            nextIp.setVirtualMachine(vmHB);
            nextIp.setVirtualApp(vapp);
            nextIp.setRasd(rasd);
            nextIp.setConfigureGateway(Boolean.TRUE);
            ipPoolDAO.makePersistent(nextIp);

        }
        catch (PersistenceException e)
        {
            factory.rollbackConnection();

            throw new NetworkCommandException(e.getMessage(), e);
        }

    }

    @Override
    public void assignDefaultNICResource(UserSession userSession, Integer networkId, Integer vmId)
        throws NetworkCommandException
    {
        try
        {
            factory.beginConnection();

            UserDAO userDAO = factory.getUserDAO();
            UserHB user = userDAO.findUserHBByName(userSession.getUser());

            assignDefaultNICResource(user, networkId, vmId);

            factory.endConnection();
        }
        catch (PersistenceException e)
        {
            factory.rollbackConnection();

            throw new NetworkCommandException(e.getMessage(), e);
        }

    }

    public void checkPrivateVlan(VirtualDataCenterHB vdc, Integer datacenterId, EnterpriseHB enter)
        throws NetworkCommandException
    {
        NetworkHB netHB = vdc.getNetwork();
        if (netHB.getNetworks().size() >= AbiConfigManager.getInstance().getAbiConfig()
            .getVlanPerVDC())
        {
            throw new NetworkCommandException("You have reached the maximum number of VLANs for this VirtualDataCenter. "
                + "Can not create more!");
        }
    }

    @Override
    public VlanNetworkHB createPrivateVlanNetwork(UserSession userSession, String networkName,
        Integer idNetwork, NetworkConfigurationHB configuration, Boolean defaultNetwork)
        throws NetworkCommandException
    {

        try
        {
            factory.beginConnection();
            VirtualDataCenterDAO vdcDAO = factory.getVirtualDataCenterDAO();
            VirtualDataCenterHB vdc = vdcDAO.getVirtualDatacenterFromNetworkId(idNetwork);

            UserHB currentUser = factory.getUserDAO().findUserHBByName(userSession.getUser());
            DatacenterHB dc = factory.getDataCenterDAO().findById(vdc.getIdDataCenter());

            // Before anything, check if we have reached the maxim number of VLAN for the network.
            checkPrivateVlan(vdc, vdc.getIdDataCenter(), currentUser.getEnterpriseHB());

            // Retrieve the list of the IPAddress that will compose the IP pool.
            List<IPAddress> listIPs =
                IPNetworkRang.calculateWholeRange(
                    IPAddress.newIPAddress(configuration.getNetworkAddress()),
                    configuration.getMask());

            // Check the number of
            checkVlanEntryValues(configuration, networkName, listIPs);
            // Check there is not already another vlan network with the same name in the network
            NetworkDAO networkDAO = factory.getNetworkDAO();

            if (networkDAO.findVlanWithName(idNetwork, networkName) != null)
            {
                throw new NetworkCommandException("You have already a Network with this name");
            }

            // Pass the interaction with the database to the 'standard' method.
            // This method is valid for private networks and public networks.
            VlanNetworkHB vlan =
                createVlanNetwork(networkName, idNetwork, configuration, defaultNetwork, null,
                    listIPs, vdc);

            // Once the work is done, register it to execute the tracer..

            // Log the event
            traceLog(SeverityType.INFO, ComponentType.VIRTUAL_DATACENTER, EventType.VLAN_CREATED,
                userSession, dc.toPojo(), vdc.getName(),
                "A new VLAN with in a private range with name '" + vlan.getNetworkName()
                    + "' has been created in " + vdc.getName(), null, null, null,
                userSession.getUser(), userSession.getEnterpriseName());

            factory.endConnection();

            return vlan;
        }
        catch (PersistenceException e)
        {
            traceLog(SeverityType.CRITICAL, ComponentType.VIRTUAL_DATACENTER,
                EventType.VLAN_CREATED, userSession, null, null,
                "Can not create the vlan caused by a Database exception '", null, null, null,
                userSession.getUser(), userSession.getEnterpriseName());

            factory.rollbackConnection();

            throw new NetworkCommandException("Error in Database while trying to create the VLAN.");
        }
        catch (NetworkCommandException ne)
        {
            traceLog(SeverityType.WARNING, ComponentType.VIRTUAL_DATACENTER,
                EventType.VLAN_CREATED, userSession, null, null,
                "Can not create the vlan, invalid values '", null, null, null,
                userSession.getUser(), userSession.getEnterpriseName());

            factory.rollbackConnection();
            throw ne;
        }

    }

    @Override
    public void deleteVlanNetwork(UserSession userSession, Integer vlanNetworkId)
        throws NetworkCommandException
    {
        VlanNetworkHB vlanHB = new VlanNetworkHB();

        try
        {
            factory.beginConnection();

            NetworkDAO netDAO = factory.getNetworkDAO();
            VlanNetworkDAO vlanDAO = factory.getVlanNetworkDAO();
            NetworkConfigurationDAO netConfDAO = factory.getNetworkConfigurationDAO();
            DHCPServiceDAO dhcpServiceDAO = factory.getDHCPServiceDAO();

            // Retrieve the VLAN to edit.
            vlanHB = vlanDAO.findById(vlanNetworkId);
            NetworkHB netHB = netDAO.findById(vlanHB.getNetworkId());

            if (vlanHB == null)
            {
                throw new NetworkCommandException("Can not find the VLAN in database. Check your request!");
            }

            // Control the defaultNetwork.
            if (vlanHB.getDefaultNetwork())
            {
                // Can not delete the default vlan.
                throw new NetworkCommandException("There must be at least one default VLAN in each Virtual Datacenter and you are trying to delete it.");
            }

            // Check there is any ip that belongs to this vlan that is currently used.
            if (vlanDAO.howManyUsedIPs(vlanNetworkId) > 0L)
            {
                throw new NetworkCommandException("Can not delete vlan. Some virtual machines are using its private IPs");
            }

            if (!vlanDAO.isPrivateVLAN(vlanNetworkId))
            {
                if (vlanDAO.howManyVDCs(vlanNetworkId) > 0L)
                {
                    throw new NetworkCommandException("Can not delete vlan. Some virtual datacenters have bought the public IPs");
                }
            }

            NetworkConfigurationHB netConf = (NetworkConfigurationHB) vlanHB.getConfiguration();
            DHCPServiceHB dhcpService = (DHCPServiceHB) netConf.getDhcpService();

            // Ready to delete
            dhcpServiceDAO.makeTransient(dhcpService);
            vlanDAO.makeTransient(vlanHB);
            netConfDAO.makeTransient(netConf);            
            

            netHB.getNetworks().remove(vlanHB);
            netDAO.makePersistent(netHB);

            traceLog(SeverityType.INFO, ComponentType.VIRTUAL_DATACENTER, EventType.VAPP_DELETE,
                userSession, null, null, "The VLAN with name " + vlanHB.getNetworkName()
                    + "' has been deleted", null, null, null, userSession.getUser(),
                userSession.getEnterpriseName());

            factory.endConnection();
        }
        catch (PersistenceException e)
        {
            traceLog(SeverityType.CRITICAL, ComponentType.VIRTUAL_DATACENTER,
                EventType.VAPP_DELETE, userSession, null, null,
                "Can not delete the VLAN with name " + vlanHB.getNetworkName()
                    + "' caused by a Database error", null, null, null, userSession.getUser(),
                userSession.getEnterpriseName());

            factory.rollbackConnection();
            throw new NetworkCommandException("Error in Database while trying to delete the VLAN.");
        }

    }

    @Override
    public VlanNetworkHB editPrivateVlanNetwork(UserSession userSession, String networkName,
        Integer vlanNetworkId, NetworkConfigurationHB newConfiguration, Boolean defaultNetwork)
        throws NetworkCommandException
    {

        VlanNetworkHB vlanHB = new VlanNetworkHB();

        try
        {

            factory.beginConnection();
            VlanNetworkDAO vlanDAO = factory.getVlanNetworkDAO();
            NetworkConfigurationDAO configDAO = factory.getNetworkConfigurationDAO();
            IpPoolManagementDAO ipManDAO = factory.getIpPoolManagementDAO();

            // Retrieve the VLAN to edit.
            vlanHB = vlanDAO.findById(vlanNetworkId);
            NetworkConfigurationHB configuration =
                (NetworkConfigurationHB) vlanHB.getConfiguration();

            if (vlanHB == null)
            {
                throw new NetworkCommandException("Can not find the VLAN in database. Check your request!");
            }

            // Control the defaultNetwork.
            if (vlanHB.getDefaultNetwork())
            {
                if (!defaultNetwork)
                {
                    // If the VLAN is the default one and we want to set as non-default, throw an
                    // exception.
                    // Its mandatory to have at least one default network.
                    throw new NetworkCommandException("There must be at least one default VLAN in each Virtual Datacenter");
                }
            }
            else
            {
                if (defaultNetwork)
                {
                    // We change the default network. Set the previous one to non-default.
                    VlanNetworkHB vlanDefault = vlanDAO.getDefaultVLAN(vlanHB.getNetworkId());
                    vlanDefault.setDefaultNetwork(false);
                    vlanDAO.makePersistent(vlanDefault);
                }
            }

            // Retrieve the list of the IPAddress that will compose the IP pool.
            List<IPAddress> listIPs =
                IPNetworkRang.calculateWholeRange(
                    IPAddress.newIPAddress(configuration.getNetworkAddress()),
                    configuration.getMask());

            // Check the entry values.
            checkVlanEntryValues(newConfiguration, networkName, listIPs);

            // Check if there is already another vlan with the same name
            if (!vlanHB.getNetworkName().equalsIgnoreCase(networkName))
            {
                NetworkDAO networkDAO = factory.getNetworkDAO();
                if (networkDAO.findVlanWithName(vlanHB.getNetworkId(), networkName) != null)
                {
                    throw new NetworkCommandException("Vlan name already exists in this network");
                }

                // Change the name of all the {@link IpPoolManagementHB} objects
                List<IpPoolManagementHB> listOfPools =
                    ipManDAO.getNetworkPoolByVLAN(vlanHB.getVlanNetworkId(), 0, null, "", null,
                        null);
                for (IpPoolManagementHB pool : listOfPools)
                {
                    pool.setVlanNetworkName(networkName);
                    ipManDAO.makePersistent(pool);
                }

                // Change also the gateway, because the previous IP didn't return the IP related to
                // the Gateway !
                IpPoolManagementHB gatewayIp =
                    ipManDAO.getIpPoolManagementByVLANandIP(vlanHB.getVlanNetworkId(),
                        IPAddress.newIPAddress(vlanHB.getConfiguration().getGateway()));
                gatewayIp.setVlanNetworkName(networkName);
                ipManDAO.makePersistent(gatewayIp);
            }

            // Get if the new gateway IP is used by any VM
            IpPoolManagementHB ipHBGateway =
                ipManDAO.getIpPoolManagementByVLANandIP(vlanHB.getVlanNetworkId(),
                    IPAddress.newIPAddress(newConfiguration.getGateway()));
            if (ipHBGateway == null)
            {
                throw new NetworkCommandException("Gateway must be an IP inside the network range");
            }

            // Proceed to change the values.
            configuration.setGateway(newConfiguration.getGateway());
            configuration.setPrimaryDNS(newConfiguration.getPrimaryDNS());
            configuration.setSecondaryDNS(newConfiguration.getSecondaryDNS());
            configuration.setSufixDNS(newConfiguration.getSufixDNS());

            vlanHB.setNetworkName(networkName);
            vlanHB.setDefaultNetwork(defaultNetwork);

            configDAO.makePersistent(configuration);
            vlanDAO.makePersistent(vlanHB);

            traceLog(SeverityType.INFO, ComponentType.VIRTUAL_DATACENTER, EventType.VLAN_EDITED,
                userSession, null, null, "VLAN " + vlanHB.getNetworkName()
                    + "' has modified its configuration", null, null, null, userSession.getUser(),
                userSession.getEnterpriseName());

            factory.endConnection();

            return vlanHB;
        }
        catch (PersistenceException e)
        {
            traceLog(SeverityType.CRITICAL, ComponentType.VIRTUAL_DATACENTER,
                EventType.VLAN_EDITED, userSession, null, null,
                "Can not edit the vlan caused by a Database exception '", null, null, null,
                userSession.getUser(), userSession.getEnterpriseName());

            factory.rollbackConnection();
            throw new NetworkCommandException("Error in Database while trying to edit the VLAN.");
        }
        catch (NetworkCommandException ne)
        {
            traceLog(SeverityType.WARNING, ComponentType.VIRTUAL_DATACENTER, EventType.VLAN_EDITED,
                userSession, null, null, "Can not edit the vlan, invalid values '", null, null,
                null, userSession.getUser(), userSession.getEnterpriseName());

            factory.rollbackConnection();
            throw ne;
        }

    }

    @Override
    public List<EnterpriseHB> getEnterprisesWithNetworksByDatacenter(UserSession userSession,
        Integer datacenterId, Integer offset, Integer numElem, String ipLike)
        throws NetworkCommandException
    {
        List<EnterpriseHB> enterpriseList = new ArrayList<EnterpriseHB>();

        try
        {
            factory.beginConnection();
            IpPoolManagementDAO netManDAO = factory.getIpPoolManagementDAO();
            enterpriseList =
                netManDAO.getEnterprisesWithNetworksByDatacenter(datacenterId, offset, numElem,
                    ipLike);
            factory.endConnection();
        }
        catch (PersistenceException e)
        {
            factory.rollbackConnection();
            throw new NetworkCommandException(e.getMessage(), e);
        }

        return enterpriseList;
    }

    @Override
    public String getInfoDHCPServer(UserSession userSession, Integer vdcId)
        throws NetworkCommandException
    {

        StringBuilder formattedData = new StringBuilder();

        try
        {
            factory.beginConnection();

            IpPoolManagementDAO ipPoolDAO = factory.getIpPoolManagementDAO();
            VirtualDataCenterDAO vdcDAO = factory.getVirtualDataCenterDAO();

            List<IpPoolManagementHB> ipPools =
                ipPoolDAO.getNetworkPoolByVDC(vdcId, 0, null, "", null, null);
            VirtualDataCenterHB vdc = vdcDAO.findById(vdcId);

            formattedData.append("## AbiCloud DHCP configuration for network "
                + vdc.getNetwork().getUuid() + "\n");
            formattedData
                .append("## Please copy and paste the following lines into your DHCP server\n");

            for (IpPoolManagementHB ipPool : ipPools)
            {

                formattedData.append("host " + ipPool.getName() + " {\n");

                // VirtualBox mac format
                if (!ipPool.getMac().contains(":"))
                {
                    String unformattedMA = ipPool.getMac();
                    StringBuilder formattedMA =
                        new StringBuilder(unformattedMA.substring(0, 2) + ":");
                    formattedMA.append(unformattedMA.substring(2, 4) + ":");
                    formattedMA.append(unformattedMA.substring(4, 6) + ":");
                    formattedMA.append(unformattedMA.substring(6, 8) + ":");
                    formattedMA.append(unformattedMA.substring(8, 10) + ":");
                    formattedMA.append(unformattedMA.substring(10, 12));
                    formattedData.append("\thardware ethernet " + formattedMA + ";\n");

                }
                else
                {
                    formattedData.append("\thardware ethernet " + ipPool.getMac() + ";\n");
                }
                formattedData.append("\tfixed-address " + ipPool.getIp() + ";\n");
                formattedData.append("}\n\n");

            }

            factory.endConnection();
        }
        catch (PersistenceException e)
        {
            factory.rollbackConnection();
            throw new NetworkCommandException(e.getMessage(), e);
        }

        return formattedData.toString();
    }

    @Override
    public List<IPAddress> getListGatewaysByVirtualMachine(UserSession userSession, Integer vmId)
        throws NetworkCommandException
    {
        try
        {
            List<IPAddress> listResult = new ArrayList<IPAddress>();

            factory.beginConnection();

            IpPoolManagementDAO poolDAO = factory.getIpPoolManagementDAO();
            VlanNetworkDAO vlanDAO = factory.getVlanNetworkDAO();

            // Get the list of NICs of a Virtual Machines
            List<IpPoolManagementHB> listPool = poolDAO.getPrivateNICsByVirtualMachine(vmId);
            for (IpPoolManagementHB pool : listPool)
            {
                // From its NICs, we can recover the VLAN.
                VlanNetworkHB vlanHB = vlanDAO.findById(pool.getVlanNetworkId());

                // Put the gateway related to this VLAN in the return list.
                if (!listResult.contains(IPAddress.newIPAddress(vlanHB.getConfiguration()
                    .getGateway())))
                {
                    listResult.add(IPAddress.newIPAddress(vlanHB.getConfiguration().getGateway()));
                }
            }

            factory.endConnection();

            return listResult;
        }
        catch (PersistenceException e)
        {
            factory.rollbackConnection();
            throw new NetworkCommandException(e.getMessage(), e);
        }
    }

    @Override
    public List<IpPoolManagementHB> getListNetworkPoolAvailableByVLAN(UserSession userSession,
        Integer vlanId, Integer offset, Integer numElem, String ipLike)
        throws NetworkCommandException
    {
        List<IpPoolManagementHB> ipList = new ArrayList<IpPoolManagementHB>();

        try
        {
            factory.beginConnection();
            IpPoolManagementDAO netManDAO = factory.getIpPoolManagementDAO();
            ipList = netManDAO.getNetworkPoolAvailableByVLAN(vlanId, offset, numElem, ipLike);
            factory.endConnection();
        }
        catch (PersistenceException e)
        {
            factory.rollbackConnection();
            throw new NetworkCommandException(e.getMessage(), e);
        }

        return ipList;
    }

    @Override
    public List<IpPoolManagementHB> getListNetworkPoolByEnterprise(UserSession userSession,
        Integer enterpriseId, Integer offset, Integer numElem, String ipLike, String orderBy,
        Boolean asc) throws NetworkCommandException
    {
        List<IpPoolManagementHB> ipList = new ArrayList<IpPoolManagementHB>();

        try
        {
            factory.beginConnection();
            IpPoolManagementDAO netManDAO = factory.getIpPoolManagementDAO();
            ipList =
                netManDAO.getNetworkPoolByEnterprise(enterpriseId, offset, numElem, ipLike,
                    orderBy, asc);
            factory.endConnection();
        }
        catch (PersistenceException e)
        {
            factory.rollbackConnection();
            throw new NetworkCommandException(e.getMessage(), e);
        }

        return ipList;
    }

    @Override
    public List<IpPoolManagementHB> getListNetworkPoolByVDC(UserSession userSession, Integer vdcId,
        Integer offset, Integer numElem, String stringLike, String orderBy, Boolean asc)
        throws NetworkCommandException
    {
        List<IpPoolManagementHB> ipList = new ArrayList<IpPoolManagementHB>();

        try
        {
            factory.beginConnection();
            IpPoolManagementDAO netManDAO = factory.getIpPoolManagementDAO();
            ipList =
                netManDAO.getNetworkPoolByVDC(vdcId, offset, numElem, stringLike, orderBy, asc);
            factory.endConnection();
        }
        catch (PersistenceException e)
        {
            factory.rollbackConnection();
            throw new NetworkCommandException(e.getMessage(), e);
        }

        return ipList;
    }

    @Override
    public List<IpPoolManagementHB> getListNetworkPoolByVLAN(UserSession userSession,
        Integer vlanId, Integer offset, Integer numElem, String ipLike, String orderBy, Boolean asc)
        throws NetworkCommandException
    {
        List<IpPoolManagementHB> ipList = new ArrayList<IpPoolManagementHB>();

        try
        {
            factory.beginConnection();
            IpPoolManagementDAO netManDAO = factory.getIpPoolManagementDAO();
            ipList = netManDAO.getNetworkPoolByVLAN(vlanId, offset, numElem, ipLike, orderBy, asc);
            factory.endConnection();
        }
        catch (PersistenceException e)
        {
            factory.rollbackConnection();
            throw new NetworkCommandException(e.getMessage(), e);
        }

        return ipList;
    }

    @Override
    public Integer getNumberEnterprisesWithNetworksByDatacenter(UserSession userSession,
        Integer datacenterId, String filterLike) throws NetworkCommandException
    {
        try
        {
            factory.beginConnection();
            IpPoolManagementDAO netManDAO = factory.getIpPoolManagementDAO();
            Integer numberOfIPs =
                netManDAO.getNumberEnterprisesWithNetworkPoolByDatacenter(datacenterId, filterLike);
            factory.endConnection();

            return numberOfIPs;
        }
        catch (PersistenceException e)
        {
            factory.rollbackConnection();
            throw new NetworkCommandException(e.getMessage(), e);
        }
    }

    @Override
    public Integer getNumberNetworkPoolAvailableByVLAN(UserSession userSession, Integer vlanId,
        String ipLike) throws NetworkCommandException
    {
        try
        {
            factory.beginConnection();
            IpPoolManagementDAO netManDAO = factory.getIpPoolManagementDAO();
            Integer numberOfIPs = netManDAO.getNumberNetworkPoolAvailableByVLAN(vlanId, ipLike);
            factory.endConnection();

            return numberOfIPs;
        }
        catch (PersistenceException e)
        {
            factory.rollbackConnection();
            throw new NetworkCommandException(e.getMessage(), e);
        }
    }

    @Override
    public Integer getNumberNetworkPoolByEnterprise(UserSession userSession, Integer enterpriseId,
        String ipLike) throws NetworkCommandException
    {
        try
        {
            factory.beginConnection();
            IpPoolManagementDAO netManDAO = factory.getIpPoolManagementDAO();
            Integer numberOfIPs = netManDAO.getNumberNetworkPoolByEnterprise(enterpriseId, ipLike);
            factory.endConnection();

            return numberOfIPs;
        }
        catch (PersistenceException e)
        {
            factory.rollbackConnection();
            throw new NetworkCommandException(e.getMessage(), e);
        }
    }

    @Override
    public Integer getNumberNetworkPoolByVDC(UserSession userSession, Integer vdcId, String ipLike)
        throws NetworkCommandException
    {
        try
        {
            factory.beginConnection();
            IpPoolManagementDAO netManDAO = factory.getIpPoolManagementDAO();
            Integer numberOfIPs = netManDAO.getNumberNetworkPoolByVDC(vdcId, ipLike);
            factory.endConnection();

            return numberOfIPs;
        }
        catch (PersistenceException e)
        {
            factory.rollbackConnection();
            throw new NetworkCommandException(e.getMessage(), e);
        }
    }

    @Override
    public Integer getNumberNetworkPoolByVLAN(UserSession userSession, Integer vlanId, String ipLike)
        throws NetworkCommandException
    {
        try
        {
            factory.beginConnection();
            IpPoolManagementDAO netManDAO = factory.getIpPoolManagementDAO();
            Integer numberOfIPs = netManDAO.getNumberNetworkPoolByVLAN(vlanId, ipLike);
            factory.endConnection();

            return numberOfIPs;
        }
        catch (PersistenceException e)
        {
            factory.rollbackConnection();
            throw new NetworkCommandException(e.getMessage(), e);
        }
    }

    @Override
    public List<IpPoolManagementHB> getPrivateNICsByVirtualMachine(UserSession userSession,
        Integer virtualMachineId) throws NetworkCommandException
    {
        try
        {
            factory.beginConnection();

            IpPoolManagementDAO netManDAO = factory.getIpPoolManagementDAO();

            List<IpPoolManagementHB> listPool =
                netManDAO.getPrivateNICsByVirtualMachine(virtualMachineId);

            Collections.sort(listPool, new IpPoolManagementComparator());
            factory.endConnection();

            return listPool;
        }
        catch (PersistenceException e)
        {
            factory.rollbackConnection();
            throw new NetworkCommandException(e.getMessage(), e);
        }
    }

    @Override
    public IPAddress getUsedGatewayByVirtualMachine(UserSession userSession, Integer vmId)
        throws NetworkCommandException
    {
        try
        {
            factory.beginConnection();

            IpPoolManagementDAO poolDAO = factory.getIpPoolManagementDAO();
            VlanNetworkDAO vlanDAO = factory.getVlanNetworkDAO();

            List<IpPoolManagementHB> listPool = poolDAO.getPrivateNICsByVirtualMachine(vmId);
            for (IpPoolManagementHB pool : listPool)
            {
                if (pool.getConfigureGateway())
                {
                    VlanNetworkHB vlanHB = vlanDAO.findById(pool.getVlanNetworkId());
                    IPAddress gatewayIP =
                        IPAddress.newIPAddress(vlanHB.getConfiguration().getGateway());

                    factory.endConnection();

                    return gatewayIP;
                }

            }

            factory.endConnection();

            return null;
        }
        catch (PersistenceException e)
        {
            factory.rollbackConnection();
            throw new NetworkCommandException(e.getMessage(), e);
        }
    }

    @Override
    public void releaseNICFromVirtualMachine(UserSession userSession, Integer ipPoolManagementId)
        throws NetworkCommandException
    {
        IpPoolManagementHB ipPoolHB = new IpPoolManagementHB();
        VlanNetworkHB vlanHB = new VlanNetworkHB();
        Boolean hadGateway = Boolean.FALSE;
        Integer vmId;

        try
        {
            factory.beginConnection();

            IpPoolManagementDAO ipPoolDAO = factory.getIpPoolManagementDAO();
            DataCenterDAO dcDAO = factory.getDataCenterDAO();
            VlanNetworkDAO vlanNetworkDAO = factory.getVlanNetworkDAO();
            VirtualApplianceDAO vappDAO = factory.getVirtualApplianceDAO();

            ResourceAllocationSettingDataDAO rasdDAO =
                factory.getResourceAllocationSettingDataDAO();

            ipPoolHB = ipPoolDAO.findById(ipPoolManagementId);
            vlanHB = vlanNetworkDAO.findById(ipPoolHB.getVlanNetworkId());

            vmId = ipPoolHB.getVirtualMachine().getIdVm();
            VirtualappHB vapp = vappDAO.getVirtualAppByVirtualMachine(vmId);

            // Get the ethernet value before to release
            ResourceAllocationSettingData rasd = ipPoolHB.getRasd();
            Integer ethernetValue = Integer.valueOf(rasd.getConfigurationName());
            rasdDAO.makeTransient(rasd);
            if (ipPoolHB.getConfigureGateway())
            {
                // if it was the IP which has the configure gateway,
                hadGateway = Boolean.TRUE;
            }

            ipPoolHB.setConfigureGateway(Boolean.FALSE);
            ipPoolHB.setVirtualApp(null);
            ipPoolHB.setVirtualMachine(null);
            ipPoolHB.setRasd(null);

            ipPoolDAO.makePersistent(ipPoolHB);

            // Update the rest of ethernet values
            List<IpPoolManagementHB> listOfNICs = getListOfNICs(vmId);
            for (IpPoolManagementHB nic : listOfNICs)
            {
                if (nic.getIdManagement() == ipPoolManagementId)
                {
                    nic = null;
                    continue;
                }
                Integer eth = Integer.valueOf(nic.getRasd().getConfigurationName());
                if (eth > ethernetValue)
                {
                    nic.getRasd().setConfigurationName(String.valueOf(eth - 1));
                    ipPoolDAO.makePersistent(nic);
                }
            }

            // Check if we must assign the configure Gateway to another ip.
            if (hadGateway)
            {
                List<IpPoolManagementHB> listPools =
                    ((DHCPServiceHB) vlanHB.getConfiguration().getDhcpService())
                        .getIpPoolManagement();
                for (IpPoolManagementHB ipPool : listPools)
                {
                    if (ipPool.getVirtualMachine() != null
                        && ipPool.getVirtualMachine().getIdVm() == vmId)
                    {
                        ipPool.setConfigureGateway(Boolean.TRUE);
                        ipPoolDAO.makePersistent(ipPool);

                        break;
                    }
                }
            }

            VirtualDataCenterHB vdc = ipPoolHB.getVirtualDataCenter();
            DatacenterHB dc = dcDAO.findById(vdc.getIdDataCenter());

            // Log the event
            traceLog(SeverityType.INFO, ComponentType.VIRTUAL_APPLIANCE,
                EventType.NIC_RELEASED_VIRTUAL_MACHINE, userSession, dc.toPojo(), vdc.getName(),
                "IP Address " + ipPoolHB.getIp() + " from VLAN " + vlanHB.getNetworkName()
                    + "has been released ", vapp.toPojo(), null, null, userSession.getUser(),
                userSession.getEnterpriseName());

            factory.endConnection();

        }
        catch (PersistenceException e)
        {
            traceLog(SeverityType.CRITICAL, ComponentType.VIRTUAL_APPLIANCE,
                EventType.NIC_ASSIGNED_VIRTUAL_MACHINE, userSession, null, null, "IP Address "
                    + ipPoolHB.getIp() + " from VLAN " + vlanHB.getNetworkName()
                    + "can not be released for Database errors ", null, null, null,
                userSession.getUser(), userSession.getEnterpriseName());

            factory.rollbackConnection();
            throw new NetworkCommandException("Can not release the NIC for Database errors");
        }

    }

    @Override
    public void requestGatewayForVirtualMachine(UserSession userSession, Integer vmId,
        IPAddress gateway) throws NetworkCommandException
    {
        try
        {
            factory.beginConnection();

            IpPoolManagementDAO poolDAO = factory.getIpPoolManagementDAO();
            VlanNetworkDAO vlanDAO = factory.getVlanNetworkDAO();

            List<IpPoolManagementHB> listPool = poolDAO.getPrivateNICsByVirtualMachine(vmId);

            // First, get the current gateway and set it to FALSE.
            for (IpPoolManagementHB pool : listPool)
            {
                if (pool.getConfigureGateway())
                {
                    pool.setConfigureGateway(Boolean.FALSE);
                    poolDAO.makePersistent(pool);
                }

            }

            // Go through the list again to put the default gateway
            for (IpPoolManagementHB pool : listPool)
            {
                VlanNetworkHB vlanHB = vlanDAO.findById(pool.getVlanNetworkId());
                if (vlanHB.getConfiguration().getGateway().equalsIgnoreCase(gateway.toString()))
                {
                    pool.setConfigureGateway(Boolean.TRUE);
                    poolDAO.makePersistent(pool);
                    break;
                }
            }

            factory.endConnection();

        }
        catch (PersistenceException e)
        {
            factory.rollbackConnection();
            throw new NetworkCommandException(e.getMessage(), e);
        }
    }

    @Override
    public void requestNICforVirtualMachine(UserSession userSession, Integer vmId,
        Integer ipPoolManagement) throws NetworkCommandException
    {
        IpPoolManagementHB ipPoolHB = new IpPoolManagementHB();
        VlanNetworkHB vlan = new VlanNetworkHB();

        try
        {
            factory.beginConnection();

            // Calls the method that executes all the business logic.
            requestNICforVirtualMachine(vmId, ipPoolManagement);

            DataCenterDAO dcDAO = factory.getDataCenterDAO();

            VirtualDataCenterHB vdc = ipPoolHB.getVirtualDataCenter();
            DatacenterHB dc = dcDAO.findById(vdc.getIdDataCenter());

            traceLog(SeverityType.INFO, ComponentType.VIRTUAL_APPLIANCE,
                EventType.NIC_ASSIGNED_VIRTUAL_MACHINE, userSession, dc.toPojo(), vdc.getName(),
                "IP Address " + ipPoolHB.getIp() + " from VLAN " + vlan.getNetworkName()
                    + " has been assigned to Virtual Machine " + vmId, null, null, null,
                userSession.getUser(), userSession.getEnterpriseName());

            factory.endConnection();
        }
        catch (PersistenceException e)
        {
            traceLog(SeverityType.CRITICAL, ComponentType.VIRTUAL_APPLIANCE,
                EventType.NIC_ASSIGNED_VIRTUAL_MACHINE, userSession, null, null, "IP Address "
                    + ipPoolHB.getIp() + " from VLAN " + vlan.getNetworkName()
                    + "can not be asigned for Database errors ", null, null, null,
                userSession.getUser(), userSession.getEnterpriseName());

            factory.rollbackConnection();
            throw new NetworkCommandException("Can not assign the NIC for Database errors");
        }
    }

    @Override
    public List<String> resolveNetworkMaskFromClassType(String classType)
        throws NetworkCommandException
    {
        NetworkResolver netResolver = new NetworkResolver();
        return netResolver.resolveMask(classType);

    }

    @Override
    public List<List<String>> resolveNetworksFromClassTypeAndMask(String classType, IPAddress mask)
        throws NetworkCommandException
    {
        NetworkResolver netResolver = new NetworkResolver();
        return netResolver.resolvePossibleNetworks(classType, mask);
    }

    @Override
    public void reorderNICintoVM(UserSession userSession, Integer newOrder,
        Integer ipPoolManagementId) throws NetworkCommandException
    {
        try
        {
            factory.beginConnection();

            IpPoolManagementDAO ipPoolDAO = factory.getIpPoolManagementDAO();
            ResourceAllocationSettingDataDAO rasdDAO =
                factory.getResourceAllocationSettingDataDAO();
            IpPoolManagementHB ipPoolHB = ipPoolDAO.findById(ipPoolManagementId);
            ResourceAllocationSettingData rasd = ipPoolHB.getRasd();

            List<IpPoolManagementHB> listOfNICs =
                getListOfNICs(ipPoolHB.getVirtualMachine().getIdVm());

            // Stablish the previous and next order
            Integer order = newOrder;
            if (listOfNICs.size() - 1 < newOrder)
            {
                // if the place is bigger than the size, we just put it in the last place
                order = listOfNICs.size() - 1;
            }
            Integer previousOrder = Integer.parseInt(rasd.getConfigurationName());

            if (order == previousOrder)
            {
                return;
            }

            // Set the new order to the NIC
            rasd.setConfigurationName(String.valueOf(order));
            rasdDAO.makePersistent(rasd);

            // Update the rest of ethernet values. The goal of this bucle is shift all
            // the ethernet values between the new order and the previous one. Depending
            // on the new order is bigger or smaller than the previous one, the shift
            // will be right to left or left to right.

            // The shift of the new values are leftToRight or rightToLeft?
            Boolean leftToRight = Boolean.TRUE;
            if (order > previousOrder)
            {
                leftToRight = Boolean.FALSE;
            }

            for (IpPoolManagementHB nic : listOfNICs)
            {
                if (nic.getIdManagement() == ipPoolManagementId)
                {
                    nic = null;
                    continue;
                }
                Integer eth = Integer.valueOf(nic.getRasd().getConfigurationName());
                if (leftToRight && eth >= order && eth < previousOrder)
                {
                    nic.getRasd().setConfigurationName(String.valueOf(eth + 1));
                    rasdDAO.makePersistent(nic.getRasd());
                }
                else if (!leftToRight && eth <= order && eth > previousOrder)
                {
                    nic.getRasd().setConfigurationName(String.valueOf(eth - 1));
                    rasdDAO.makePersistent(nic.getRasd());
                }

            }

            factory.endConnection();
        }
        catch (PersistenceException e)
        {
            factory.rollbackConnection();
            throw new NetworkCommandException("Can not assign the NIC for Database errors");
        }

    }

    /* PROTECTED METHODS */

    /**
     * Helper function called by @link{
     * com.abiquo.abiserver.commands.impl.NetworkCommandImpl#assignPrivateMACResource(
     * VirtualDataCenterHB virtualDataCenter, NodeVirtualImageHB node, IPAddress privateIP) in order
     * to set the RASD related to a new MAC address
     */
    protected ResourceAllocationSettingData assignPrivateMACResourceRASD(String MACaddress,
        String networkName)
    {
        ResourceAllocationSettingData rasd = new ResourceAllocationSettingData();

        rasd.setAddress(MACaddress);
        rasd.setConnection("");
        rasd.setAllocationUnits("0");
        rasd.setParent(networkName);
        rasd.setElementName("MAC Address");
        rasd.setDescription("MAC Address asociated to private Network");
        rasd.setInstanceID(UUID.randomUUID().toString());
        rasd.setResourceType(CIMResourceTypeEnum.Ethernet_Adapter.getNumericResourceType());
        // Meaning is the the MAC address related to a private network
        rasd.setResourceSubType("0");

        return rasd;
    }

    /**
     * Check the entry values are correct. Check if the primary and secondary DNS values are empty
     * or have a correct IP address format. Also checks if the gateway is inside the network range
     * of values.
     * 
     * @param configuration {@link NetworkConfiguration} object to check.
     * @param networkName name of the network
     * @param listIPs network range of values.
     * @param networkId identifier of the network.
     */
    protected void checkVlanEntryValues(NetworkConfigurationHB configuration, String networkName,
        List<IPAddress> listIPs) throws NetworkCommandException
    {

        if (networkName.isEmpty())
        {
            throw new NetworkCommandException("Network name can not be empty");
        }

        // Check the network address
        try
        {
            IPAddress.newIPAddress(configuration.getNetworkAddress());
        }
        catch (InvalidIPAddressException e)
        {
            throw new NetworkCommandException("Invalid value PrimaryDNS", e);
        }

        // Check the gateway
        try
        {
            IPAddress.newIPAddress(configuration.getGateway());
        }
        catch (InvalidIPAddressException e)
        {
            throw new NetworkCommandException("Invalid value Gateway");
        }

        // Check the primary DNS (if exists)
        if (!configuration.getPrimaryDNS().equalsIgnoreCase(""))
        {
            try
            {
                IPAddress.newIPAddress(configuration.getPrimaryDNS());
            }
            catch (InvalidIPAddressException e)
            {
                throw new NetworkCommandException("Invalid value PrimaryDNS", e);
            }
        }

        // Check the secondary DNS (if exists)
        if (!configuration.getSecondaryDNS().equalsIgnoreCase(""))
        {
            try
            {
                IPAddress.newIPAddress(configuration.getSecondaryDNS());
            }
            catch (InvalidIPAddressException e)
            {
                throw new NetworkCommandException("Invalid value SecondaryDNS");
            }
        }

        if (!listIPs.contains(IPAddress.newIPAddress(configuration.getGateway())))
        {
            throw new NetworkCommandException("Gateway must be an IP inside the network range");
        }

    }

    /**
     * Create a vlan network. This method is used for private vlans and public lans tagged as vlans.
     * As parameters, it needs all the values to be created.
     * 
     * @param networkName The name of the network. Defined by the user.
     * @param idNetwork identifier of the network that belongs this vlan.
     * @param configuration {@link NetworkConfigurationHB} object. Contains dns values, gateway,
     *            masks.. etc.
     * @param defaultNetwork mark the network as default
     * @param vlanTag vlan value to tag the vnetwork
     * @param listOfIpsToCreate list of IPs that belong to this network.
     * @param networkType private or public.
     * @return a built {@link VlanNetworkHB} object.
     * @throws PersistenceException any problem accessing to database.
     * @throws NetworkCommandException logic problems.
     */
    protected VlanNetworkHB createVlanNetwork(String networkName, Integer idNetwork,
        NetworkConfigurationHB configuration, Boolean defaultNetwork, Integer vlanTag,
        List<IPAddress> listOfIpsToCreate, VirtualDataCenterHB vdc) throws PersistenceException,
        NetworkCommandException
    {
        final String FENCE_MODE = "bridge";

        // PLEASE NOTE: this is a protected method and it needs to have an active transaction to be
        // executed
        VlanNetworkDAO networkDAO = factory.getVlanNetworkDAO();
        NetworkConfigurationDAO netConfDAO = factory.getNetworkConfigurationDAO();

        // Modify the default network?
        if (defaultNetwork)
        {
            // release the previous default network for the vlan.
            // Every network can only have one default vlan...
            VlanNetworkHB vlanDefault = networkDAO.getDefaultVLAN(idNetwork);

            // If there is the first vlan created in the network
            if (vlanDefault != null)
            {
                vlanDefault.setDefaultNetwork(Boolean.FALSE);
                networkDAO.makePersistent(vlanDefault);
            }
        }

        DataCenterDAO dataDAO = factory.getDataCenterDAO();

        configuration.setFenceMode(FENCE_MODE);
        configuration.setNetworkConfigurationId(null);
        configuration = netConfDAO.makePersistent(configuration);

        VlanNetworkHB vlan = new VlanNetworkHB();
        vlan.setConfiguration(configuration);
        vlan.setNetworkId(idNetwork);
        vlan.setNetworkName(networkName);
        vlan.setVlanTag(vlanTag);
        vlan.setDefaultNetwork(defaultNetwork);

        networkDAO.makePersistent(vlan);
        
        // Diferences between public and private vlans.
        // · Private ones stored in virtual datacenter.
        // · Public ones stored in physical datacenter.
        // Define the DHCPService
        DHCPServiceHB dhcpService;
        if (vdc != null)
        {
            DatacenterHB datacenter = dataDAO.getDatacenterWhereThePrivateNetworkStays(idNetwork);
            dhcpService = defineTheDHCP(datacenter, listOfIpsToCreate, vdc, vlan);
        }
        else
        {
            DatacenterHB datacenter = dataDAO.getDatacenterWhereThePublicNetworkStays(idNetwork);
            dhcpService = defineTheDHCP(datacenter, listOfIpsToCreate, null, vlan);
        }
        
        configuration.setDhcpService(dhcpService);
        configuration = netConfDAO.makePersistent(configuration);

        return vlan;
    }

    protected DHCPServiceHB defineTheDHCP(DatacenterHB datacenter,
        List<IPAddress> listOfIpsToCreate, VirtualDataCenterHB vdc, VlanNetworkHB vlan)
        throws NetworkCommandException, PersistenceException
    {
        IpPoolManagementDAO ipPoolDAO = factory.getIpPoolManagementDAO();
        DHCPServiceDAO dhcpServiceDAO = factory.getDHCPServiceDAO();
        DHCPServiceHB dhcpService = new DHCPServiceHB();

        // Check if we have defined the dhcp service.
        for (RemoteServiceHB remote : datacenter.getRemoteServicesHB())
        {
            if (remote.getRemoteServiceType() == RemoteServiceType.DHCP_SERVICE)
            {
                dhcpService.setDhcpRemoteServiceId(remote.getIdRemoteService());
            }
        }
        dhcpService = dhcpServiceDAO.makePersistent(dhcpService);
        dhcpService.setIpPoolManagement(new ArrayList<IpPoolManagementHB>());

        for (IPAddress currentIP : listOfIpsToCreate)
        {
            IpPoolManagementHB nextIpPool = new IpPoolManagementHB();

            // Define the mac only in case of private vlans because the virtual datacenter is
            // already defined.
            if (vdc != null)
            {
                String nextRandomMacAddress;
                do
                {
                    nextRandomMacAddress = vdc.getHypervisorType().getRandomMacAddress();
                }
                while (ipPoolDAO.existingMACAddress(nextRandomMacAddress));

                nextIpPool.setMac(nextRandomMacAddress);
                nextIpPool.setName(nextIpPool.getMac() + "_host");
            }

            nextIpPool.setIp(currentIP.toString());
            nextIpPool.setDhcpServiceId(dhcpService.getDhcpServiceId());
            nextIpPool.setConfigureGateway(Boolean.FALSE);
            nextIpPool.setIdResourceType("10");
            nextIpPool.setRasd(null);
            nextIpPool.setVirtualApp(null);
            nextIpPool.setVirtualDataCenter(vdc);
            nextIpPool.setVlanNetworkName(vlan.getNetworkName());
            nextIpPool.setVlanNetworkId(vlan.getVlanNetworkId());
            nextIpPool.setQuarantine(Boolean.FALSE);

            // ipPoolDAO.makePersistent(nextIpPool);
            dhcpService.getIpPoolManagement().add(nextIpPool);
        }

        dhcpService = dhcpServiceDAO.makePersistent(dhcpService);

        return dhcpService;
    }

    /**
     * Return the list of NICs of a given virtual machine
     * 
     * @param vmId identifier of the virtual machine.
     * @return
     */
    protected List<IpPoolManagementHB> getListOfNICs(Integer vmId)
    {
        IpPoolManagementDAO ipPoolDAO = factory.getIpPoolManagementDAO();
        List<IpPoolManagementHB> list = ipPoolDAO.getPrivateNICsByVirtualMachine(vmId);
        Collections.sort(list, new IpPoolManagementComparator());
        return list;
    }

    /**
     * Retrieve the next available IP into a VLAN
     * 
     * @param dhcpServiceId identifier of the object which has all the IPs
     * @param gateway gateway can not be the the next available by default.
     * @return an {@link IpPoolManagementHB} instance
     */
    protected IpPoolManagementHB getNextAvailableIP(Integer dhcpServiceId, String gateway)
        throws NetworkCommandException
    {
        DHCPServiceDAO dhcpServiceDAO = factory.getDHCPServiceDAO();
        IpPoolManagementHB nextIp = dhcpServiceDAO.getNextAvailableIp(dhcpServiceId, gateway);

        if (nextIp == null)
        {
            factory.rollbackConnection();
            throw new NetworkCommandException("No more available IPs for default VLAN. ");
        }

        return nextIp;
    }

    /**
     * Inserts a new NIC to a virtual machine.
     * 
     * @param vmId identifier of the virtual machine
     * @param ipPoolManagement contains all the needed information for the NIC
     * @throws PersistenceException
     */
    protected IpPoolManagementHB requestNICforVirtualMachine(Integer vmId, Integer ipPoolManagement)
        throws PersistenceException
    {
        // NOTE: needs and embedded transaction!
        IpPoolManagementHB ipPoolHB = new IpPoolManagementHB();
        VlanNetworkHB vlan = new VlanNetworkHB();

        // Check if the IP is already used.
        IpPoolManagementDAO ipPoolDAO = factory.getIpPoolManagementDAO();
        ResourceAllocationSettingDataDAO rasdDAO = factory.getResourceAllocationSettingDataDAO();
        VirtualMachineDAO vmDAO = factory.getVirtualMachineDAO();
        VirtualApplianceDAO vappDAO = factory.getVirtualApplianceDAO();

        ipPoolHB = ipPoolDAO.findById(ipPoolManagement);
        vlan = ipPoolDAO.getVlanByIpPoolManagement(ipPoolHB.getIdManagement());

        if (ipPoolHB.getVirtualMachine() != null)
        {
            throw new NetworkCommandException("IP Address already in use in this VLAN");
        }

        // Find the virtual machine and the virtual appliance by its ID
        VirtualmachineHB vmHB = vmDAO.findById(vmId);
        VirtualappHB vapp = vappDAO.getVirtualAppByVirtualMachine(vmId);

        // Define the vLAN of a given IpPool
        // Generate the resource allocation setting data associed
        ResourceAllocationSettingData rasd =
            this.assignPrivateMACResourceRASD(ipPoolHB.getMac(), vlan.getNetworkName());

        String ethernetValue = String.valueOf(getListOfNICs(vmId).size());
        // Set the last connection value.
        rasd.setConfigurationName(ethernetValue);
        rasdDAO.makePersistent(rasd);

        // Associate the resource with the values
        ipPoolHB.setVlanNetworkId(vlan.getVlanNetworkId());
        ipPoolHB.setVlanNetworkName(vlan.getNetworkName());
        ipPoolHB.setVirtualMachine(vmHB);
        ipPoolHB.setVirtualApp(vapp);
        ipPoolHB.setRasd(rasd);
        ipPoolDAO.makePersistent(ipPoolHB);

        return ipPoolHB;
    }
}
