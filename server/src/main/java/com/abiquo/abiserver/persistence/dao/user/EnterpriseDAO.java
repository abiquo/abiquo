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

package com.abiquo.abiserver.persistence.dao.user;

import java.util.List;

import com.abiquo.abiserver.business.hibernate.pojohb.user.EnterpriseHB;
import com.abiquo.abiserver.business.hibernate.pojohb.virtualhardware.DatacenterLimitHB;
import com.abiquo.abiserver.business.hibernate.pojohb.virtualhardware.ResourceAllocationLimitHB;
import com.abiquo.abiserver.exception.PersistenceException;
import com.abiquo.abiserver.persistence.DAO;

/**
 * Specific interface to work with the
 * {@link com.abiquo.abiserver.business.hibernate.pojohb.user.EnterpriseHB} Exposes all the methods
 * that this entity will need to interact with the data source
 * 
 * @author jdevesa@abiquo.com
 */
public interface EnterpriseDAO extends DAO<EnterpriseHB, Integer>
{
    // add extra functionality

    /**
     * Get the sum of all the resources allocated by a given Enterprise.
     */
    ResourceAllocationLimitHB getTotalResourceUtilization(int idEnterprise)
        throws PersistenceException;

    List<Integer> findAllIds();

    EnterpriseHB findByVirtualAppliance(Integer idVirtualApp);

    /**
     * Gets the resource allocation limits for the current enterprise on the provided datacenter.
     * 
     * @return null if the Datacenter is not allowed for the provided enterprise
     */
    DatacenterLimitHB getDatacenterLimit(int idEnterprise, int idDatacenter);

    /**
     * From an vlanId, return the enterprise that has reserved it.
     * 
     * @param vlanId identifier of the enterprise.
     * @return the {@link EnterpriseHB} object.
     */
    EnterpriseHB getEnterpriseFromReservedVlanID(Integer vlanId) throws PersistenceException;
}
