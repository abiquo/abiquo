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

package com.abiquo.abiserver.persistence.dao.virtualappliance;

import java.util.Collection;

import com.abiquo.abiserver.business.hibernate.pojohb.virtualappliance.VirtualDataCenterHB;
import com.abiquo.abiserver.business.hibernate.pojohb.virtualhardware.ResourceAllocationLimitHB;
import com.abiquo.abiserver.exception.PersistenceException;
import com.abiquo.abiserver.persistence.DAO;

/**
 * Specific interface to work with the
 * {@link com.abiquo.abiserver.business.hibernate.pojohb.virtualappliance.VirtualappHB} Exposes all
 * the methods that this entity will need to interact with the data source
 * 
 * @author jdevesa@abiquo.com
 */
public interface VirtualDataCenterDAO extends DAO<VirtualDataCenterHB, Integer>
{
    // Publish all the needed extra functions
    VirtualDataCenterHB findByIdNamed(Integer id);

    /**
     * Get the VDC from its network.
     * 
     * @param idNetwork identifer of the network.
     * @return a {@link VirtualDataCenterHB} object.
     */
    VirtualDataCenterHB getVirtualDatacenterFromNetworkId(Integer idNetwork)
        throws PersistenceException;

    /**
     * Searches the virtualdatacenter that stores a given virtual appliance
     * 
     * @param vappId virtual appliance identifier.
     * @return a {@link VirtualDataCenterHB} object.
     * @throws PersistencException if any problem occurs trying to access to database
     */
    VirtualDataCenterHB getVirtualDatacenterFromVirtualAppliance(Integer vappId)
        throws PersistenceException;

    Collection<VirtualDataCenterHB> getVirtualDatacentersFromEnterprise(Integer enterpriseId);

    Collection<VirtualDataCenterHB> getVirtualDatacentersFromEnterpriseAndDatacenter(
        Integer enterpriseId, Integer datacenterId);

    public ResourceAllocationLimitHB getCurrentResourcesAllocated(int virtualDatacenterId);
}
