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

import com.abiquo.abiserver.business.hibernate.pojohb.networking.IpPoolManagementHB;
import com.abiquo.abiserver.exception.PersistenceException;
import com.abiquo.abiserver.persistence.DAO;

/**
 * Specific interface to work with the
 * {@link com.abiquo.abiserver.business.hibernate.pojohb.networking.IpPoolManagementHB} Exposes all
 * the methods that this entity will need to interact with the data source
 * 
 * @author jdevesa@abiquo.com
 */
public interface IpPoolManagementDAO extends DAO<IpPoolManagementHB, Integer>
{
<<<<<<< HEAD
    // Publish all the needed extra functions //

    // TODO TBD
    boolean isVlanAssignedToDifferentVM(Integer vlan_network_id, Integer idVM);

    // TODO TBD
    List<IpPoolManagementHB> findByVirtualMachine(Integer idVm);

    /**
     * Returns if the MAC address is already created.
     * 
     * @param MACaddress MAC address to check
     * @return true if exists , false otherwise
     * @throws PersistenceException if any problem occurs
     */
    boolean existingMACAddress(String MACaddress) throws PersistenceException;

    /**
     * Identifies the {@link IpPoolManagement} object for its VLAN and IP Address.
     * 
     * @param vlankId identifer of the VLAN that stores the object.
     * @param requestedIP IP address that defines the object.
     * @return the matching object.
     * @throws PersistenceException encapsulates any database access exception.
     */
    IpPoolManagementHB getIpPoolManagementByVLANandIP(Integer vlanId, IPAddress requestedIP)
        throws PersistenceException;

    /**
     * Return the list of {@link IpPoolManagement} objects with no virtual machine assigned
     * (available) into a VLAN.
     * 
     * @param vlanId identifier of the VLAN.
     * @param offset first element to retrieve.
     * @param numElem number of elements to retrieve.
     * @param filterLike filter the search.
     * @return the list of matching results.
     * @throws PersistenceException encapsulates any database access exception.
     */
    List<IpPoolManagementHB> getNetworkPoolAvailableByVLAN(Integer vlanId, Integer offset,
        Integer numElem, String filterLike) throws PersistenceException;

    /**
     * Return the list of {@link IpPoolManagement} objects into a VLAN.
     * 
     * @param vlanId identifier of the VLAN.
     * @param offset first element to retrieve.
     * @param numElem number of elements to retrieve.
     * @param ipLike filter the search.
     * @param orderBy the order criteria.
     * @param asc related to orderBy filter: ascendant or descendant?
     * @return the list of matching results.
     * @throws PersistenceException encapsulates any database access exception.
     */
    List<IpPoolManagementHB> getNetworkPoolByVLAN(Integer vlanId, Integer offset, Integer numElem,
        String ipLike, String orderBy, Boolean asc) throws PersistenceException;

    /**
     * Return the number of {@link IpPoolManagement} objects with no virtual machine assigned
     * (available) into a VLAN.
     * 
     * @param vlanId identifier of the VLAN.
     * @param filterLike filter the search.
     * @return the number of matching results.
     * @throws PersistenceException encapsulates any database access exception.
     */
    Integer getNumberNetworkPoolAvailableByVLAN(Integer vlanId, String filterLike)
        throws PersistenceException;

    /**
     * Return the number of {@link IpPoolManagement} objects into a VLAN.
     * 
     * @param vlanId identifier of the VLAN.
     * @param ipLike filter the search.
     * @return the number of matching results.
     * @throws PersistenceException encapsulates any database access exception.
     */
    Integer getNumberNetworkPoolByVLAN(Integer vlanId, String ipLike) throws PersistenceException;
=======
>>>>>>> stable

    /**
     * Return the list of {@link IpPoolManagementHB} assigned to a virtual machine.
     * 
     * @param virtualMachineId identifier of the virtual machine.
     * @return the list of matching results.
     * @throws PersistenceException encapsulates any database access exception.
     */
    List<IpPoolManagementHB> getPrivateNICsByVirtualMachine(Integer virtualMachineId)
        throws PersistenceException;

}
