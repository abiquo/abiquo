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

package com.abiquo.ovfmanager.ovf.section;

import org.dmtf.schemas.ovf.envelope._1.AbicloudNetworkType;
import org.dmtf.schemas.ovf.envelope._1.DHCPServiceType;
import org.dmtf.schemas.ovf.envelope._1.IpPoolType;
import org.dmtf.schemas.ovf.envelope._1.NetworkConfigurationType;
import org.dmtf.schemas.ovf.envelope._1.OrgNetworkType;

import com.abiquo.ovfmanager.ovf.exceptions.IdNotFoundException;
import com.abiquo.ovfmanager.ovf.exceptions.RequiredAttributeException;

/**
 * Collection of static methods for the easy-building of the CustomNetwork section element.
 * 
 * @author abiquo TODO - the exceptions should have meaningful messages
 */
public class OVFCustomNetworkUtils
{
    /**
     * Creates a new instance of AbicloudNetworkType
     * 
     * @param UUID To identify the section
     * @param VlanID The id of the VLAN associated
     * @return A new instance of AbicloudNetworkType
     * @throws RequiredAttributeException All the attributes are required, if some attribute is null
     *             the exception is emitted.
     */
    public static AbicloudNetworkType createAbicloudNetwork(String UUID)
        throws RequiredAttributeException
    {
        if (UUID != null)
        {
            AbicloudNetworkType network = new AbicloudNetworkType();

            network.setUuid(UUID);

            return network;
        }
        else
        {
            throw new RequiredAttributeException("");
        }
    }

    /**
     * Create a {@link OrgNetworkType} value with the incoming values.
     * 
     * @param networkName name of the network
     * @param vlanTag integer value (from 0-4096) which the network will be tagged.
     * @return a built {@link OrgNetworkType} object.
     * @throws RequiredAttributeException if any of the mandatory values is missing.
     */
    public static OrgNetworkType createOrgNetwork(String networkName, Integer vlanTag)
        throws RequiredAttributeException
    {
        if (networkName != null && vlanTag != null)
        {
            OrgNetworkType networkType = new OrgNetworkType();

            networkType.setNetworkName(networkName);
            networkType.setVlanTag(vlanTag);

            return networkType;
        }
        else
        {
            throw new RequiredAttributeException("");
        }
    }

    /**
     * Create the {@link NetworkConfigurationType} object which contains all the information about
     * the configuration of the network.
     * 
     * @param gateway gateway of the network
     * @param networkAddress network address that defines the network. (P.e 192.168.5.0)
     * @param mask integer value of the mask (p.e. /24)
     * @param networkMask IP value of the mask (p.e. 255.255.255.0)
     * @param primaryDNS IP value of the primary DNS
     * @param secondaryDNS IP value of the secondary DNS
     * @param sufixDNS sufix dns
     * @param fenceMode if its isolated, NAT, or bridged
     * @return a {@link NetworkConfigurationType} object.
     * @throws RequiredAttributeException if any of the mandatory values are missing.
     */
    public static NetworkConfigurationType createNetworkConfigurationType(String gateway,
        String networkAddress, Integer mask, String networkMask, String primaryDNS,
        String secondaryDNS, String sufixDNS, String fenceMode) throws RequiredAttributeException
    {
        if (gateway != null && networkAddress != null && mask != null && networkMask != null
            && fenceMode != null)
        {
            NetworkConfigurationType netConf = new NetworkConfigurationType();

            netConf.setGateway(gateway);
            netConf.setNetworkAddress(networkAddress);
            netConf.setMask(mask);
            netConf.setNetmask(networkMask);
            netConf.setPrimaryDNS(primaryDNS);
            netConf.setSecondaryDNS(secondaryDNS);
            netConf.setSufixDNS(sufixDNS);
            netConf.setFenceMode(fenceMode);

            return netConf;
        }
        else
        {
            throw new RequiredAttributeException("");
        }
    }

    /**
     * Create a {@link DHCPService} with its values.
     * 
     * @param dhcpAddress ip address where the DHCP is installed.
     * @param dhcpPort omapi port where the DHCP listens.
     * @return a built {@link DHCPService} object
     * @throws RequiredAttributeException if any of the mandatory address are null
     */
    public static DHCPServiceType createDHCPService(String dhcpAddress, Integer dhcpPort)
        throws RequiredAttributeException
    {
        if (dhcpAddress != null && dhcpPort != null)
        {
            DHCPServiceType dhcp = new DHCPServiceType();

            dhcp.setDhcpAddress(dhcpAddress);
            dhcp.setDhcpPort(dhcpPort);

            return dhcp;
        }
        else
        {
            throw new RequiredAttributeException("");
        }
    }

    /**
     * Creates a new instance of IPPoolType
     * 
     * @param ip The desired IP
     * @param mac The associated MAC
     * @param name The alias
     * @param configureGateway inform if the gateway should be configured in the dhcp rules.
     * @return A new instance of HostType with the indicated attributes
     * @throws RequiredAttributeException All the attributes are required, if some attribute is null
     *             the exception is emitted.
     */
    public static IpPoolType createIPPool(String ip, String mac, String name,
        Boolean configureGateway) throws RequiredAttributeException
    {
        if (ip != null && mac != null && name != null)
        {
            IpPoolType host = new IpPoolType();

            host.setIp(ip);
            host.setMac(mac);
            host.setName(name);
            host.setConfigureGateway(configureGateway);

            return host;
        }
        else
        {
            throw new RequiredAttributeException("");
        }
    }

    /**
     * Gets the a network by name
     * 
     * @param networkSection the network section to get the network from
     * @param networkName the network name
     * @return the network
     * @throws RequiredAttributeException
     * @throws IdNotFoundException
     */
    public static OrgNetworkType getNetworkByName(AbicloudNetworkType networkSection,
        String networkName) throws RequiredAttributeException, IdNotFoundException
    {
        if (networkSection == null || networkName == null)
        {
            throw new RequiredAttributeException("Some values are null!");
        }

        for (OrgNetworkType nType : networkSection.getNetworks())
        {
            if (nType.getNetworkName().equals(networkName))
            {
                return nType;
            }
        }

        throw new IdNotFoundException("Network name " + networkName);
    }

}
