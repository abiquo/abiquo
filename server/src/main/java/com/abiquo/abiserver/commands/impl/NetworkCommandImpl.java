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

import java.util.Comparator;
import java.util.UUID;

import com.abiquo.abiserver.business.hibernate.pojohb.networking.DHCPServiceHB;
import com.abiquo.abiserver.business.hibernate.pojohb.networking.IpPoolManagementHB;
import com.abiquo.abiserver.business.hibernate.pojohb.networking.NetworkHB;
import com.abiquo.abiserver.business.hibernate.pojohb.networking.VlanNetworkHB;
import com.abiquo.abiserver.business.hibernate.pojohb.user.EnterpriseHB;
import com.abiquo.abiserver.business.hibernate.pojohb.user.UserHB;
import com.abiquo.abiserver.business.hibernate.pojohb.virtualappliance.VirtualDataCenterHB;
import com.abiquo.abiserver.business.hibernate.pojohb.virtualappliance.VirtualappHB;
import com.abiquo.abiserver.business.hibernate.pojohb.virtualappliance.VirtualmachineHB;
import com.abiquo.abiserver.business.hibernate.pojohb.virtualhardware.ResourceAllocationSettingData;
import com.abiquo.abiserver.commands.BasicCommand;
import com.abiquo.abiserver.commands.NetworkCommand;
import com.abiquo.abiserver.config.AbiConfigManager;
import com.abiquo.abiserver.exception.NetworkCommandException;
import com.abiquo.abiserver.exception.PersistenceException;
import com.abiquo.abiserver.persistence.DAOFactory;
import com.abiquo.abiserver.persistence.dao.networking.DHCPServiceDAO;
import com.abiquo.abiserver.persistence.dao.networking.IpPoolManagementDAO;
import com.abiquo.abiserver.persistence.dao.networking.VlanNetworkDAO;
import com.abiquo.abiserver.persistence.dao.user.UserDAO;
import com.abiquo.abiserver.persistence.dao.virtualappliance.VirtualApplianceDAO;
import com.abiquo.abiserver.persistence.dao.virtualappliance.VirtualMachineDAO;
import com.abiquo.abiserver.persistence.dao.virtualhardware.ResourceAllocationSettingDataDAO;
import com.abiquo.abiserver.persistence.hibernate.HibernateDAOFactory;
import com.abiquo.abiserver.pojo.authentication.UserSession;
import com.abiquo.ovfmanager.cim.CIMTypesUtils.CIMResourceTypeEnum;

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
        @Override
        public int compare(final IpPoolManagementHB ip1, final IpPoolManagementHB ip2)
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
    public void assignDefaultNICResource(final UserHB user, final Integer networkId,
        final Integer vmId) throws NetworkCommandException
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
    public void assignDefaultNICResource(final UserSession userSession, final Integer networkId,
        final Integer vmId) throws NetworkCommandException
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

    @Override
    public void checkPrivateVlan(final VirtualDataCenterHB vdc, final Integer datacenterId,
        final EnterpriseHB enter, final UserSession userSession) throws NetworkCommandException
    {
        NetworkHB netHB = vdc.getNetwork();

        if (netHB == null)
        {
            return;
        }

        if (netHB.getNetworks().size() >= AbiConfigManager.getInstance().getAbiConfig()
            .getVlanPerVDC())
        {
            throw new NetworkCommandException("You have reached the maximum number of VLANs for this VirtualDataCenter. "
                + "Can not create more!");
        }
    }

    /* PROTECTED METHODS */

    /**
     * Helper function called by @link{
     * com.abiquo.abiserver.commands.impl.NetworkCommandImpl#assignPrivateMACResource(
     * VirtualDataCenterHB virtualDataCenter, NodeVirtualImageHB node, IPAddress privateIP) in order
     * to set the RASD related to a new MAC address
     */
    protected ResourceAllocationSettingData assignPrivateMACResourceRASD(final String MACaddress,
        final String networkName)
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
     * Retrieve the next available IP into a VLAN
     * 
     * @param dhcpServiceId identifier of the object which has all the IPs
     * @param gateway gateway can not be the the next available by default.
     * @return an {@link IpPoolManagementHB} instance
     */
    protected IpPoolManagementHB getNextAvailableIP(final Integer dhcpServiceId,
        final String gateway) throws NetworkCommandException
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

}
