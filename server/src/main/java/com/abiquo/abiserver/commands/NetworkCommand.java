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

package com.abiquo.abiserver.commands;

import com.abiquo.abiserver.business.hibernate.pojohb.user.EnterpriseHB;
import com.abiquo.abiserver.business.hibernate.pojohb.user.UserHB;
import com.abiquo.abiserver.business.hibernate.pojohb.virtualappliance.VirtualDataCenterHB;
import com.abiquo.abiserver.exception.NetworkCommandException;
import com.abiquo.abiserver.pojo.authentication.UserSession;

/**
 * This command collects all actions related to Networking features
 * 
 * @author jdevesa@abiquo.com
 */
public interface NetworkCommand
{
    /**
     * Assigns an arbitrary NIC resource into the default VLAN to the virtual machine.
     * 
     * @param user user who performs the action.
     * @param networkId the identifier of the network.
     * @param vmId identifier of the virtual machine.
     */
    public void assignDefaultNICResource(UserHB user, Integer networkId, Integer vmId)
        throws NetworkCommandException;

    /**
     * The same functionality than the previous method. But this one creates a new transaction.
     * 
     * @param userSession user who performs the action.
     * @param networkId the identifier of the network.
     * @param vmId identifier of the virtual machine.
     */
    public void assignDefaultNICResource(UserSession userSession, Integer networkId, Integer vmId)
        throws NetworkCommandException;

    /**
     * Check the private VLAN limits. (this is also called before try to create a virtua
     * datacenter).
     * 
     * @throws NetworkCommandException if the Hard Limit is exceeded.
     */
    public void checkPrivateVlan(VirtualDataCenterHB vdc, Integer datacenterId, EnterpriseHB enter,
        UserSession userSession) throws NetworkCommandException;
}
