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

package com.abiquo.api.services;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.abiquo.server.core.cloud.VirtualDatacenter;
import com.abiquo.server.core.cloud.VirtualDatacenterRep;
import com.abiquo.server.core.infrastructure.network.VLANNetwork;

@Service
@Transactional(readOnly = true)
public class PrivateNetworkService
{
    @Autowired
    VirtualDatacenterRep repo;

    @Autowired
    VirtualDatacenterRep virtualDataCenterRep;

    public Collection<VLANNetwork> getNetworks()
    {
        return repo.findAllVlans();
    }

    public Collection<VLANNetwork> getNetworksByVirtualDatacenter(final Integer virtualDatacenterId)
    {
        VirtualDatacenter virtualDatacenter = virtualDataCenterRep.findById(virtualDatacenterId);
        Collection<VLANNetwork> networks = null;

        if (virtualDatacenter != null)
        {
            networks = repo.findVlansByVirtualDatacener(virtualDatacenter);
        }

        return networks;
    }

    public VLANNetwork getNetwork(Integer id)
    {
        return repo.findVlanById(id);
    }

    public boolean isAssignedTo(final Integer virtualDatacenterId, final Integer networkId)
    {
        VLANNetwork nw = repo.findVlanById(networkId);
        VirtualDatacenter vdc = virtualDataCenterRep.findById(virtualDatacenterId);

        return nw != null && vdc != null
            && nw.getNetwork().getId().equals(vdc.getNetwork().getId());
    }
}
