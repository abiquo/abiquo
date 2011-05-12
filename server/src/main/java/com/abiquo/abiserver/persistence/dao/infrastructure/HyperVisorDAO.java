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

import java.util.List;

import com.abiquo.abiserver.business.hibernate.pojohb.infrastructure.DatacenterHB;
import com.abiquo.abiserver.business.hibernate.pojohb.infrastructure.HypervisorHB;
import com.abiquo.abiserver.exception.PersistenceException;
import com.abiquo.abiserver.persistence.DAO;
import com.abiquo.model.enumerator.HypervisorType;

/**
 * Specific interface to work with the {@link
 * com.abiquo.abiserver.business.hibernate.pojohb.interface.HiperVisorHB} Exposes all the methods
 * that this entity will need to interact with the data source
 * 
 * @author xfernandez@abiquo.com
 */
public interface HyperVisorDAO extends DAO<HypervisorHB, Integer>
{
    // Publish all the needed extra functions
    /**
     * This method recovers all the list of available Hypervisors technologies in 1 datacenter
     * 
     * @param dataCenter the dataCenter object
     * @return the list of Hypervisor technologies
     * @throws PersistenceException if exists any problem with dataBase
     */
    List<HypervisorType> getHypervisorsTypeByDataCenter(DatacenterHB dataCenter)
        throws PersistenceException;

    /**
     * Return the Hypervisor object related to the physicalmachine.
     * 
     * @param pmId identifier of the physicalmachine
     * @return the {@link HypervisorHB} object that maches the search
     */
    HypervisorHB getHypervisorFromPhysicalMachine(Integer pmId);

    /**
     * Obtain the VDRP to be used on the machine (related to the hypervisor type). TODO do not use
     * idPhysicalMachine
     */
    List<Integer> getUsedPortsFromDB(Integer idHypervisor, Integer idPhysicalMachine);
}
