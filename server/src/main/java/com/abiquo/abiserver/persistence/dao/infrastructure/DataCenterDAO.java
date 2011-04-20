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

package com.abiquo.abiserver.persistence.dao.infrastructure;

import java.util.ArrayList;
import java.util.List;

import com.abiquo.abiserver.business.hibernate.pojohb.infrastructure.DatacenterHB;
import com.abiquo.abiserver.business.hibernate.pojohb.infrastructure.RackHB;
import com.abiquo.abiserver.exception.PersistenceException;
import com.abiquo.abiserver.persistence.DAO;

/**
 * Specific interface to work with the {@link
 * com.abiquo.abiserver.business.hibernate.pojohb.interface.DatacenterHB} Exposes all the methods
 * that this entity will need to interact with the data source
 * 
 * @author jdevesa@abiquo.com
 */
public interface DataCenterDAO extends DAO<DatacenterHB, Integer>
{
    // Publish all the needed extra functions

    /**
     * Return the number of virtual datacenters associated to a physical one.
     * 
     * @param idDatacenter physical datacenter
     * @return number of virtual datacenters
     */
    Long getNumberVirtualDatacentersByDatacenter(Integer idDatacenter);

    /**
     * Return the number of storage devices associated to a physical one.
     * 
     * @param idDatacenter physical datacenter
     * @return number of storage devices
     */
    Long getNumberStorageDevicesByDatacenter(Integer idDatacenter);

    /**
     * Retrieves the Datacenter for the private network id.
     * 
     * @param networkId network identifier
     * @return a {@link DatacenterHB} object.
     * @throws PersistenceException if there is any problem accessing to data store.
     */
    DatacenterHB getDatacenterWhereThePrivateNetworkStays(Integer networkId)
        throws PersistenceException;

    /**
     * Retrieves the Datacenter for the public network id.
     * 
     * @param networkId network identifier
     * @return a {@link DatacenterHB} object.
     * @throws PersistenceException if there is any problem accessing to data store.
     */
    DatacenterHB getDatacenterWhereThePublicNetworkStays(Integer idNetwork)
        throws PersistenceException;

    /**
     * This method update all the used resources of all the physicalMachines of a datacenter
     * 
     * @param idDatacenter physical datacenter
     */
    void updateUsedResourcesByDatacenter(Integer idDatacenter);

    /**
     * Finds DataCentery matching given name.
     * 
     * @param name
     * @return
     */
    DatacenterHB findByName(String name) throws PersistenceException;

    /**
     * Lists all DataCenter Ids available.
     * 
     * @return
     */
    public List<Integer> findAllIds();

    /**
     * Gets all the allowed datacenters for the provided enterprise
     */
    List<DatacenterHB> getAllowedDatacenters(int idEnterprise);

    /**
     * Return all the racks registered into a datacenter. It can filter the rack by name, the
     * datacenter by name and if an enterprise is associated to any physical machine, the enterprise
     * by name
     * 
     * @param datacenterId identifier of the datacenter
     * @param filters filters the search
     * @return all the matching rack elements.
     */
    ArrayList<RackHB> getRacks(Integer datacenterId, String filters);

    long getCurrentStorageAllocated(int idEnterprise, int idDatacenter);

    long getCurrentPublicIpAllocated(int idEnterprise, int idDatacenter);

}
