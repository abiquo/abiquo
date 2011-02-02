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

import java.util.List;

import com.abiquo.abiserver.business.BusinessDelegateProxy;
import com.abiquo.abiserver.business.UserSessionException;
import com.abiquo.abiserver.commands.RemoteServicesCommand;
import com.abiquo.abiserver.commands.impl.RemoteServicesCommandImpl;
import com.abiquo.abiserver.exception.InfrastructureCommandException;
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
    protected RemoteServicesCommand remoteCommand;

    public RemoteServicesService()
    {
        remoteCommand = new RemoteServicesCommandImpl();
    }

    protected RemoteServicesCommand proxyCommand(UserSession userSession)
    {
        return BusinessDelegateProxy.getInstance(userSession, remoteCommand,
            RemoteServicesCommand.class);
    }

    /**
     * Creates a new remote service
     * 
     * @param userSession
     * @param rs
     * @return
     */
    public BasicResult addRemoteService(UserSession userSession, RemoteService rs)
    {
        RemoteServicesCommand proxied = proxyCommand(userSession);

        try
        {
            return proxied.addRemoteService(userSession, rs);
        }
        catch (UserSessionException e)
        {
            return e.getResult();
        }
    }

    /**
     * Updates a remote service's configuration data
     * 
     * @param userSession
     * @param rs
     * @return
     */
    public BasicResult updateRemoteService(UserSession userSession, RemoteService rs)
    {
        RemoteServicesCommand proxied = proxyCommand(userSession);

        try
        {
            return proxied.updateRemoteService(userSession, rs);
        }
        catch (UserSessionException e)
        {
            return e.getResult();
        }
    }

    /**
     * Gets a remote service's configuration data
     * 
     * @param userSession
     * @param id
     * @return
     */
    public DataResult<RemoteService> getRemoteService(UserSession userSession, Integer id)
    {
        DataResult<RemoteService> dataResult = new DataResult<RemoteService>();

        RemoteServicesCommand proxied = proxyCommand(userSession);

        try
        {
            RemoteService rs = proxied.getRemoteService(userSession, id);
            dataResult.setData(rs);
            dataResult.setSuccess(true);
        }
        catch (InfrastructureCommandException e)
        {
            dataResult.setMessage(e.getCause().getMessage());
        }
        catch (UserSessionException e)
        {
            dataResult.setSuccess(false);
            dataResult.setMessage(e.getMessage());
            dataResult.setResultCode(e.getResult().getResultCode());
        }

        return dataResult;

    }

    /**
     * Lists all remote services defined for a datacenter for a given type.
     * 
     * @param userSession
     * @param idDataCenter
     * @param idRemoteServiceType
     * @return
     */
    public DataResult<List<RemoteService>> getRemoteServicesByType(UserSession userSession,
        Integer idDataCenter, String remoteServiceType)
    {
        DataResult<List<RemoteService>> dataResult = new DataResult<List<RemoteService>>();

        RemoteServicesCommand proxied = proxyCommand(userSession);

        try
        {
            List<RemoteService> rsList =
                proxied.getRemoteServicesByType(userSession, idDataCenter, remoteServiceType);
            dataResult.setData(rsList);
            dataResult.setSuccess(true);
        }
        catch (InfrastructureCommandException e)
        {
            dataResult.setMessage(e.getCause().getMessage());
        }
        catch (UserSessionException e)
        {
            dataResult.setSuccess(false);
            dataResult.setMessage(e.getMessage());
            dataResult.setResultCode(e.getResult().getResultCode());
        }

        return dataResult;

    }

    /**
     * Lists all remote services defined for a datacenter
     * 
     * @param userSession
     * @param idDataCenter
     * @return
     */
    public DataResult<List<RemoteService>> getAllRemoteServices(UserSession userSession,
        Integer idDataCenter)
    {
        DataResult<List<RemoteService>> dataResult = new DataResult<List<RemoteService>>();

        RemoteServicesCommand proxied = proxyCommand(userSession);

        try
        {
            List<RemoteService> rsList = proxied.getAllRemoteServices(userSession, idDataCenter);
            dataResult.setData(rsList);
            dataResult.setSuccess(true);
        }
        catch (InfrastructureCommandException e)
        {
            dataResult.setMessage(e.getCause().getMessage());
        }
        catch (UserSessionException e)
        {
            dataResult.setSuccess(false);
            dataResult.setMessage(e.getMessage());
            dataResult.setResultCode(e.getResult().getResultCode());
        }

        return dataResult;

    }

    /**
     * Checks an existing remote service's availability. The availability is saved for further
     * consults.
     * 
     * @param userSession
     * @param id
     * @return
     */
    public DataResult<Boolean> checkRemoteService(UserSession userSession, Integer id)
    {
        DataResult<Boolean> dataResult = new DataResult<Boolean>();

        RemoteServicesCommand proxied = proxyCommand(userSession);

        try
        {
            boolean success = proxied.checkRemoteService(userSession, id);
            dataResult.setData(new Boolean(success));
            dataResult.setSuccess(true);
        }
        catch (InfrastructureCommandException e)
        {
            dataResult.setMessage(e.getCause().getMessage());
        }
        catch (UserSessionException e)
        {
            dataResult.setSuccess(false);
            dataResult.setMessage(e.getMessage());
            dataResult.setResultCode(e.getResult().getResultCode());
        }
        return dataResult;

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
    public DataResult<Boolean> checkRemoteService(UserSession userSession, String protocol,
        String domainName, Integer port, String serviceMapping, String remoteServiceType)
    {
        DataResult<Boolean> dataResult = new DataResult<Boolean>();

        RemoteServicesCommand proxied = proxyCommand(userSession);

        try
        {
            boolean success =
                proxied.checkRemoteService(userSession, RemoteService.getFullUri(protocol,
                    domainName, port, serviceMapping),
                    com.abiquo.abiserver.business.hibernate.pojohb.service.RemoteServiceType
                        .valueOf(remoteServiceType));

            dataResult.setData(new Boolean(success));
            dataResult.setSuccess(true);
        }
        catch (UserSessionException e)
        {
            dataResult.setSuccess(false);
            dataResult.setMessage(e.getMessage());
            dataResult.setResultCode(e.getResult().getResultCode());
        }

        return dataResult;

    }

    /**
     * Removes a remote service from the system
     * 
     * @param userSession
     * @param id
     * @return
     */
    public DataResult<Boolean> deleteRemoteService(UserSession userSession,
        RemoteService remoteService)
    {
        RemoteServicesCommand proxied = proxyCommand(userSession);

        return proxied.deleteRemoteService(userSession, remoteService);
    }

    /**
     * Lists remote service types available
     * 
     * @param userSession
     * @return
     */
    public DataResult<List<RemoteServiceType>> getRemoteServiceTypes(UserSession userSession)
    {
        DataResult<List<RemoteServiceType>> dataResult = new DataResult<List<RemoteServiceType>>();

        RemoteServicesCommand proxied = proxyCommand(userSession);

        try
        {
            List<RemoteServiceType> rsList = proxied.getRemoteServiceTypes(userSession);
            dataResult.setData(rsList);
            dataResult.setSuccess(true);
        }
        catch (InfrastructureCommandException e)
        {
            dataResult.setMessage(e.getCause().getMessage());
        }
        catch (UserSessionException e)
        {
            dataResult.setSuccess(false);
            dataResult.setMessage(e.getMessage());
            dataResult.setResultCode(e.getResult().getResultCode());
        }

        return dataResult;
    }

}
