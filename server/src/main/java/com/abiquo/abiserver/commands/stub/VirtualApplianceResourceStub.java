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
import java.util.List;

import com.abiquo.abiserver.business.hibernate.pojohb.virtualappliance.VirtualmachineHB;
import com.abiquo.abiserver.business.hibernate.pojohb.virtualhardware.ResourceManagementHB;
import com.abiquo.abiserver.pojo.authentication.UserSession;
import com.abiquo.abiserver.pojo.result.BasicResult;
import com.abiquo.abiserver.pojo.result.DataResult;
import com.abiquo.abiserver.pojo.user.Enterprise;
import com.abiquo.abiserver.pojo.virtualappliance.Node;
import com.abiquo.abiserver.pojo.virtualappliance.VirtualAppliance;
import com.abiquo.util.ErrorManager;

public interface VirtualApplianceResourceStub
{

    public DataResult deployVirtualAppliance(final Integer virtualDatacenterId,
        final Integer virtualApplianceId, Boolean forceEnterpriseLimit);

    public DataResult undeployVirtualAppliance(final Integer virtualDatacenterId,
        final Integer virtualApplianceId);

    /**
     * Queries to allocate a new virtual machine
     * 
     * @param virtualDatacenterId
     * @param virtualApplianceId
     * @param vmachineRequ
     * @param resMans
     * @param forceEnterpirseLimits
     * @param errorManager
     * @return
     */
    public VirtualmachineHB allocate(final Integer virtualDatacenterId,
        final Integer virtualApplianceId, final VirtualmachineHB vmachineRequ,
        final List<ResourceManagementHB> resMans, final boolean forceEnterpirseLimits,
        final ErrorManager errorManager);

    public BasicResult createVirtualAppliance(VirtualAppliance virtualAppliance);

    public DataResult updateVirtualApplianceNodes(final Integer virtualDatacenterId,
        final VirtualAppliance virtualAppliance);

    public DataResult<VirtualAppliance> getVirtualApplianceNodes(final Integer virtualDatacenterId,
        final Integer virtualApplianceId);

    public DataResult<List<Node>> getAppNodes(final VirtualAppliance entity);

    DataResult<Collection<VirtualAppliance>> getVirtualAppliancesByEnterprise(
        UserSession userSession, Enterprise enterprise);

    BasicResult deleteVirtualAppliance(VirtualAppliance virtualAppliance, boolean forceDelete);
}
