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

import com.abiquo.abiserver.business.hibernate.pojohb.networking.DHCPServiceHB;
import com.abiquo.abiserver.business.hibernate.pojohb.networking.IpPoolManagementHB;
import com.abiquo.abiserver.exception.PersistenceException;
import com.abiquo.abiserver.persistence.DAO;

/**
 * Specific interface to work with the
 * {@link com.abiquo.abiserver.business.hibernate.pojohb.networking.DHCPServiceHB} Exposes all the
 * methods that this entity will need to interact with the data source
 * 
 * @author jdevesa@abiquo.com
 */
public interface DHCPServiceDAO extends DAO<DHCPServiceHB, Integer>
{

    /**
     * Search for the next available {@link IpPoolManagement} object in the list of resources.
     * 
     * @param dhcpServiceId dhcp service identifier
     * @param gateway gateway IP of the vlan
     * @return an available {@link IpPoolManagement} object.
     * @throws PersistenceException if there is any problem trying to access to database.
     */
    public IpPoolManagementHB getNextAvailableIp(Integer dhcpServiceId, String gateway)
        throws PersistenceException;

}
