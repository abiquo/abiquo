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

import javax.validation.constraints.Min;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.abiquo.api.exceptions.APIError;
import com.abiquo.api.exceptions.BadRequestException;
import com.abiquo.api.resources.cloud.PrivateNetworkResource;
import com.abiquo.api.resources.cloud.VirtualDatacenterResource;
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
public class IpAddressService extends DefaultApiService
{
    private static final Logger LOGGER = LoggerFactory.getLogger(IpAddressService.class);
    
    @Autowired
    VirtualDatacenterRep repo;

    public List<IpPoolManagement> getListIpPoolManagementByVLAN(final Integer vdcId,
            final Integer vlanId, final Integer startwith, final String orderBy,
            final String filter,  final Integer limit, final Boolean descOrAsc, final Boolean available)
    {        
        // Check if the orderBy element is actually one of the available ones
        IpPoolManagement.OrderByEnum orderByEnum = IpPoolManagement.OrderByEnum.fromValue(orderBy);
        if (orderByEnum == null)
        {
            LOGGER.info("Bad parameter 'by' in request to get the private ips by virtualdatacenter.");
            addValidationErrors(APIError.QUERY_INVALID_PARAMETER);
            flushErrors();
        }
        return repo.findIpsByPrivateVLAN(vdcId, vlanId, startwith, limit, filter, orderByEnum, descOrAsc, available);
    }

    public List<IpPoolManagement> getListIpPoolManagementByVdc(final Integer vdcId,
        final Integer firstElem, final Integer numElem, String has, String orderBy, Boolean asc)
    {
        // Check if the orderBy element is actually one of the available ones
        IpPoolManagement.OrderByEnum orderByEnum = IpPoolManagement.OrderByEnum.fromValue(orderBy);
        if (orderByEnum == null)
        {
            LOGGER.info("Bad parameter 'by' in request to get the private ips by virtualdatacenter.");
            addValidationErrors(APIError.QUERY_INVALID_PARAMETER);
            flushErrors();
        }
        return repo.findIpsByVdc(vdcId, firstElem, numElem, has, orderByEnum, asc);
    }

    public List<IpPoolManagement> getListIpPoolManagementByEnterprise(final Integer entId,
        final Integer firstElem, final Integer numElem, String has, String orderBy, Boolean asc)
    {

        // Check if the orderBy element is actually one of the available ones
        IpPoolManagement.OrderByEnum orderByEnum = IpPoolManagement.OrderByEnum.fromValue(orderBy);
        if (orderByEnum == null)
        {
            LOGGER.info("Bad parameter 'by' in request to get the private ips by enterprise.");
            addValidationErrors(APIError.QUERY_INVALID_PARAMETER);
            flushErrors();
        }
        return repo.findIpsByEnterprise(entId, firstElem, numElem, has, orderByEnum, asc);
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
