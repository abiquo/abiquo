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

package com.abiquo.abiserver.persistence.dao.networking;

import java.util.List;

import com.abiquo.abiserver.business.hibernate.pojohb.networking.VlanNetworkHB;
import com.abiquo.abiserver.exception.PersistenceException;
import com.abiquo.abiserver.persistence.DAO;

/**
 * Specific interface to work with the
 * {@link com.abiquo.abiserver.business.hibernate.pojohb.networking.VlanNetworkHB} Exposes all the
 * methods that this entity will need to interact with the data source
 * 
 * @author jdevesa@abiquo.com
 */
public interface VlanNetworkDAO extends DAO<VlanNetworkHB, Integer>
{

    /**
     * Gets the Free VLAN tag of the networking resources assigned to the rack
     * 
     * @return NULL if there isn't enough network resources on the rack
     */
    Integer getFreeVLANTag(Integer idRack);

    /**
     * Retrieves the default VLAN for the network.
     * 
     * @param idNetwork network identifier
     * @return the VlanNetwork that is marked of default.
     * @throws PersistenceException if any problem occurs.
     */
    VlanNetworkHB getDefaultVLAN(Integer idNetwork) throws PersistenceException;

    /**
     * Return the number of IPs used by virtual machines in this VLAN.
     * 
     * @param vlanNetworkId vlan network identifier.
     * @return number of used IPs.
     */
    Long howManyUsedIPs(Integer vlanNetworkId) throws PersistenceException;

    /**
     * Check whenever the vlan belongs to a public or a private ranges.
     * 
     * @param vlanNetworkId identifier of the vlan.
     * @return true if the vlan in private
     */
    Boolean isPrivateVLAN(Integer vlanNetworkId) throws PersistenceException;

    /**
     * Into a vlan, check how many ips have virtual datacenters assigned.
     * 
     * @param vlanNetworkId identifier of the vlan
     * @return number of used IPs.
     */
    Long howManyVDCs(Integer vlanNetworkId) throws PersistenceException;

    /**
     * Find all VLANs that belong to the given Enterprise.
     * 
     * @param idEnterprise The Enteprise that owns the VLANs.
     * @return The number of VLAns that belong to the enterprise.
     */
    List<VlanNetworkHB> findByEnterprise(final Integer idEnterprise);

    /**
     * Find all VLANs that belong to the given VirtualDatacenter.
     * 
     * @param idVirtualDatacenter The VirtualDatacenter that owns the VLANs.
     * @return The number of VLAns that belong to the virtual datacenter.
     */
    List<VlanNetworkHB> findByVirtualDatacenter(final Integer idVirtualDatacenter);

    /**
     * Find all VLANs that belong to the given Enterprise and datacenter .
     * 
     * @param idEnterprise The Enteprise that owns the VLANs.
     * @return The number of VLAns that belong to the enterprise and datacenter.
     */
    List<VlanNetworkHB> findByEnterpriseAndDatacenter(final Integer idEnterpirse,
        final Integer idDatacenter);

    /**
     * Find all the private VLANs of the datacenter
     * 
     * @param idDatacenter identifier of the Datacenter
     * @return list of the VLANs.
     */
    List<VlanNetworkHB> findPrivateVLANsByDatacenter(final Integer idDatacenter)
        throws PersistenceException;

}
