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

/**
 * 
 */
package com.abiquo.api.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.abiquo.server.core.cloud.VirtualAppliance;
import com.abiquo.server.core.cloud.VirtualDatacenterRep;
import com.abiquo.server.core.cloud.VirtualMachine;
import com.abiquo.server.core.infrastructure.network.IpPoolManagement;

/**
 * Business Logic for retrive information about the {@link IpPoolManagment} object
 * 
 * @author jdevesa@abiquo.com
 */
@Service
@Transactional(readOnly = true)
public class IpAddressService
{
    @Autowired
    VirtualDatacenterRep repo;

    public List<IpPoolManagement> getListIpPoolManagementByVLAN(final Integer vlanId, final Integer page, final Integer numElem)
    {
        return repo.findIpsByVLAN(vlanId, page, numElem);
    }

    public List<IpPoolManagement> getListIpPoolManagementByVdc(final Integer vdcId, final Integer page, final Integer numElem)
    {
        return repo.findIpsByVdc(vdcId, page, numElem);
    }

    public List<IpPoolManagement> getListIpPoolManagementByEnterprise(final Integer entId, final Integer page, final Integer numElem)
    {
        return repo.findIpsByEnterprise(entId, page, numElem);
    }

    public List<IpPoolManagement> getListIpPoolManagementByVirtualApp(final VirtualAppliance vapp)
    {
        return repo.findIpsByVirtualAppliance(vapp);
    }

    public List<IpPoolManagement> getListIpPoolManagementByMachine(VirtualMachine vm)
    {
        return repo.findIpsByVirtualMachine(vm);
    }

}
