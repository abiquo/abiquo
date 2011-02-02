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
package com.abiquo.abiserver.commands;

import java.util.List;

import com.abiquo.abiserver.exception.InfrastructureCommandException;
import com.abiquo.abiserver.pojo.authentication.UserSession;
import com.abiquo.abiserver.pojo.result.DataResult;
import com.abiquo.abiserver.pojo.service.RemoteService;
import com.abiquo.abiserver.pojo.service.RemoteServiceType;

/**
 * All the business logic for remote services configuration section in AbiCloud
 * 
 * @author destevez@abiquo.com
 */
public interface RemoteServicesCommand
{
    /**
     * Adds a new Remote Service to a DataCenter
     * 
     * @param userSession
     * @param rs
     * @return
     * @throws InfrastructureCommandException
     */
    public DataResult<RemoteService> addRemoteService(UserSession userSession, RemoteService rs);

    /**
     * Updates a remote service data
     * 
     * @param userSession
     * @param rs
     * @return
     * @throws InfrastructureCommandException
     */
    public DataResult<RemoteService> updateRemoteService(UserSession userSession, RemoteService rs);

    /**
     * Gets this remote service information
     * 
     * @param userSession
     * @param id
     * @return
     * @throws InfrastructureCommandException
     */
    public RemoteService getRemoteService(UserSession userSession, Integer id)
        throws InfrastructureCommandException;

    /**
     * Lists remote services by Type in a DataCenter
     * 
     * @param userSession
     * @param idDataCenter
     * @param idRemoteServiceType
     * @return
     * @throws InfrastructureCommandException
     */
    public List<RemoteService> getRemoteServicesByType(UserSession userSession,
        Integer idDataCenter, String remoteServiceType) throws InfrastructureCommandException;

    /**
     * Lists all remote services available for a DataCenter
     * 
     * @param userSession
     * @param idDataCenter
     * @return
     * @throws InfrastructureCommandException
     */
    public List<RemoteService> getAllRemoteServices(UserSession userSession, Integer idDataCenter)
        throws InfrastructureCommandException;

    /**
     * Checks an existing remote service status
     * 
     * @param userSession
     * @param id
     * @return
     * @throws InfrastructureCommandException
     */
    public boolean checkRemoteService(UserSession userSession, Integer id)
        throws InfrastructureCommandException;

    /**
     * Checks a non-yet created remote service status
     * 
     * @param userSession
     * @param serviceUri
     * @param remoteServiveTypeStr
     * @return
     */
    public boolean checkRemoteService(UserSession userSession, String serviceUri,
        com.abiquo.abiserver.business.hibernate.pojohb.service.RemoteServiceType rsType);

    /**
     * Deletes a remote service
     * 
     * @param userSession
     * @param id
     * @return
     * @throws InfrastructureCommandException
     */
    public DataResult<Boolean> deleteRemoteService(UserSession userSession,
        RemoteService remoteService);

    /**
     * @param userSession
     * @return
     * @throws InfrastructureCommandException
     */
    public List<RemoteServiceType> getRemoteServiceTypes(UserSession userSession)
        throws InfrastructureCommandException;

    /**
     * @param userSession
     * @param protocol
     * @param domainName
     * @param serviceMapping
     * @param port
     * @return
     * @throws InfrastructureCommandException
     */
    public List<RemoteService> getRemoteServicesByUrl(UserSession userSession, String uri)
        throws InfrastructureCommandException;

}
