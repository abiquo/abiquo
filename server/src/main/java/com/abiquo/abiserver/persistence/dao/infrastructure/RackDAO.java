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

import com.abiquo.abiserver.business.hibernate.pojohb.infrastructure.PhysicalmachineHB;
import com.abiquo.abiserver.business.hibernate.pojohb.infrastructure.RackHB;
import com.abiquo.abiserver.exception.PersistenceException;
import com.abiquo.abiserver.persistence.DAO;

/**
 * Specific interface to work with the {@link
 * com.abiquo.abiserver.business.hibernate.pojohb.interface.RackHB} Exposes all the methods that
 * this entity will need to interact with the data source
 * 
 * @author jdevesa@abiquo.com
 */
public interface RackDAO extends DAO<RackHB, Integer>
{
    // add extra functionality

    /**
     * Obtains the racks (prefiltered by target datacenter and virtualdatacenter) with minimal VLANS
     * and with vms deployed from min to max VLAN count
     */
    List<Integer> getRackIdByMinVlanCount(Integer vlan_vdc, Integer idVApp);
    
    
    /**
     * Return all the physicalmachines registered into a rack. It can filter the physicalmachine by name, 
     * and if an enterprise is associated to any physical machine, the enterprise
     * by name
     * 
     * @param rackId identifier of the id
     * @param filters filters the search
     * @return all the matching physicalmachine elements.
     */
    List<PhysicalmachineHB> getPhysicalMachines(Integer rackId, String filters);

    /**
     * @param datacenterId identifier of the datacenter.
     * @return the Lowest value of the 'vlan_id_max' parameters inside the datacenter.
     * @throws PersistenceException if any problem occurs its encapsulated into this exception
     */
    Integer getLowestVlanIdMax(final Integer datacenterId) throws PersistenceException;
}

