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

package com.abiquo.abiserver.services.flex;

import static com.abiquo.abiserver.business.hibernate.pojohb.service.RemoteServiceType.getCommunityServices;

import java.util.ArrayList;
import java.util.List;

import com.abiquo.abiserver.commands.stub.APIStubFactory;
import com.abiquo.abiserver.commands.stub.RemoteServicesResourceStub;
import com.abiquo.abiserver.commands.stub.impl.RemoteServicesResourceStubImpl;
import com.abiquo.abiserver.pojo.authentication.UserSession;
import com.abiquo.abiserver.pojo.result.BasicResult;
import com.abiquo.abiserver.pojo.result.DataResult;
import com.abiquo.abiserver.pojo.service.RemoteService;
import com.abiquo.abiserver.pojo.service.RemoteServiceType;

/**
 * Flex client entry point for managing RemoteServices Configuration Service.
 * 
 * @author destevez
 */

public class RemoteServicesService
{
    protected RemoteServicesResourceStub proxyStub(final UserSession userSession)
    {
        return APIStubFactory.getInstance(userSession, new RemoteServicesResourceStubImpl(),
            RemoteServicesResourceStub.class);
    }

    /**
     * Creates a new remote service
     * 
     * @param userSession
     * @param rs
     * @return
     */
    public BasicResult addRemoteService(final UserSession userSession, final RemoteService rs)
    {
        return proxyStub(userSession).addRemoteService(rs);
    }

    /**
     * Updates a remote service's configuration data
     * 
     * @param userSession
     * @param rs
     * @return
     */
    public BasicResult updateRemoteService(final UserSession userSession, final RemoteService rs)
    {
        return proxyStub(userSession).modifyRemoteService(rs);
    }

    /**
     * Lists all remote services defined for a datacenter for a given type.
     * 
     * @param userSession
     * @param idDataCenter
     * @param idRemoteServiceType
     * @return
     */
    public DataResult<List<RemoteService>> getRemoteServicesByType(final UserSession userSession,
        final Integer idDataCenter, final String remoteServiceType)
    {
        return proxyStub(userSession).getRemoteServices(idDataCenter, remoteServiceType);
    }

    /**
     * Checks an existing remote service's availability. The availability is saved for further
     * consults.
     * 
     * @param userSession
     * @param id
     * @return
     */
    public DataResult<Boolean> checkRemoteService(final UserSession userSession,
        final Integer datacenterId, final String type)
    {
        return proxyStub(userSession).checkRemoteService(datacenterId, type);
    }

    /**
     * Checks a remote service availability, asking the user for its location (URI)
     * 
     * @param userSession
     * @param protocol
     * @param domainName
     * @param port
     * @param serviceMapping
     * @return
     */
    public DataResult<Boolean> checkRemoteService(final UserSession userSession,
        final Integer datacenterId, final String protocol, final String domainName,
        final Integer port, final String serviceMapping, final String remoteServiceType)
    {

        return proxyStub(userSession).checkRemoteService(datacenterId, remoteServiceType,
            RemoteService.getFullUri(protocol, domainName, port, serviceMapping));
    }

    /**
     * Removes a remote service from the system
     * 
     * @param userSession
     * @param id
     * @return
     */
    public DataResult<Boolean> deleteRemoteService(final UserSession userSession,
        final RemoteService remoteService)
    {
        return proxyStub(userSession).deleteRemoteService(remoteService);
    }

    /**
     * Lists remote service types available
     * 
     * @param userSession
     * @return
     */
    public DataResult<List<RemoteServiceType>> getRemoteServiceTypes(final UserSession userSession)
    {
        DataResult<List<RemoteServiceType>> dataResult = new DataResult<List<RemoteServiceType>>();

        List<RemoteServiceType> remoteServicesTypes = new ArrayList<RemoteServiceType>();

        for (com.abiquo.abiserver.business.hibernate.pojohb.service.RemoteServiceType rst : getServiceTypes())
        {
            remoteServicesTypes.add(new RemoteServiceType(rst));
        }

        dataResult.setData(remoteServicesTypes);
        dataResult.setSuccess(true);

        return dataResult;
    }

    protected com.abiquo.abiserver.business.hibernate.pojohb.service.RemoteServiceType[] getServiceTypes()
    {
        return getCommunityServices();
    }
}
