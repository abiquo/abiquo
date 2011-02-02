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

package com.abiquo.abiserver.commands.stub;

import java.util.Collection;

import com.abiquo.abiserver.business.hibernate.pojohb.networking.NetworkConfigurationHB;
import com.abiquo.abiserver.pojo.infrastructure.DataCenter;
import com.abiquo.abiserver.pojo.result.BasicResult;
import com.abiquo.abiserver.pojo.result.DataResult;
import com.abiquo.abiserver.pojo.user.Enterprise;
import com.abiquo.abiserver.pojo.virtualappliance.VirtualDataCenter;
import com.abiquo.util.resources.ResourceManager;

public interface VirtualDatacenterResourceStub
{
    public DataResult<VirtualDataCenter> createVirtualDatacenter(VirtualDataCenter vdc,
        String networkName, NetworkConfigurationHB netConfig, ResourceManager resourceManager);

    public BasicResult updateVirtualDatacenter(VirtualDataCenter vdc,
        ResourceManager resourceManager);

    public BasicResult deleteVirtualDatacenter(VirtualDataCenter vdc,
        ResourceManager resourceManager);

    public DataResult<Collection<VirtualDataCenter>> getVirtualDatacenters(Enterprise enterprise,
        DataCenter datacenter);

}
