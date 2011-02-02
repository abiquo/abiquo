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

package com.abiquo.abiserver.persistence.dao.virtualhardware;

import com.abiquo.abiserver.business.hibernate.pojohb.virtualappliance.VirtualmachineHB;
import com.abiquo.abiserver.business.hibernate.pojohb.virtualhardware.ResourceManagementHB;
import com.abiquo.abiserver.exception.PersistenceException;
import com.abiquo.abiserver.persistence.DAO;

/**
 * Class that implements the extra DAO functions for the
 * {@link com.abiquo.abiserver.persistence.dao.virtualhardware.ResourceManagementHB} interface
 * 
 * @author jdevesa@abiquo.com
 */
public interface ResourceManagementDAO extends DAO<ResourceManagementHB, Integer>
{

    // Implement interface extra functions

    /**
     * Gets the Resource related to the given MAC address
     * 
     * @param vm Virtual Machine to look for all its MAC address
     * @return A ResourceManagementHB object containing the result of the Query. Should be a unique
     *         result
     * @throws PersistenceException if any problem occurs
     */
    ResourceManagementHB getResourceManagementPrivateEthernetByVS(VirtualmachineHB vm)
        throws PersistenceException;
}
