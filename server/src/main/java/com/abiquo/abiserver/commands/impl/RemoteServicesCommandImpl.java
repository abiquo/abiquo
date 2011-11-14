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
package com.abiquo.abiserver.commands.impl;

import static com.abiquo.abiserver.business.hibernate.pojohb.service.RemoteServiceType.getCommunityServices;
import static com.abiquo.abiserver.business.hibernate.pojohb.service.RemoteServiceType.valueOf;

import java.util.ArrayList;
import java.util.List;

import com.abiquo.abiserver.abicloudws.RemoteServiceClient;
import com.abiquo.abiserver.appslibrary.AppsLibraryRecovery;
import com.abiquo.abiserver.business.hibernate.pojohb.service.RemoteServiceHB;
import com.abiquo.abiserver.commands.BasicCommand;
import com.abiquo.abiserver.commands.RemoteServicesCommand;
import com.abiquo.abiserver.commands.stub.APIStubFactory;
import com.abiquo.abiserver.commands.stub.RemoteServicesResourceStub;
import com.abiquo.abiserver.commands.stub.impl.RemoteServicesResourceStubImpl;
import com.abiquo.abiserver.exception.InfrastructureCommandException;
import com.abiquo.abiserver.exception.PersistenceException;
import com.abiquo.abiserver.exception.RemoteServiceException;
import com.abiquo.abiserver.persistence.DAOFactory;
import com.abiquo.abiserver.persistence.dao.infrastructure.RemoteServiceDAO;
import com.abiquo.abiserver.persistence.hibernate.HibernateDAOFactory;
import com.abiquo.abiserver.pojo.authentication.UserSession;
import com.abiquo.abiserver.pojo.result.DataResult;
import com.abiquo.abiserver.pojo.service.RemoteService;
import com.abiquo.abiserver.pojo.service.RemoteServiceType;
import com.abiquo.tracer.ComponentType;
import com.abiquo.tracer.EventType;
import com.abiquo.tracer.SeverityType;
import com.abiquo.util.AbiCloudError;

@SuppressWarnings("unchecked")
public class RemoteServicesCommandImpl extends BasicCommand implements RemoteServicesCommand
{
    /** The logger. */
    // private static final Logger LOGGER =
    // LoggerFactory.getLogger(RemoteServicesCommandImpl.class);

    /**
     * DAOFactory to create DAOs
     */
    protected DAOFactory factory;

    protected AppsLibraryRecovery recovery;

    /**
     * Creates a new {@link RemoteServicesCommand} and instantiates the DAO factory;
     */
    public RemoteServicesCommandImpl()
    {
        factory = HibernateDAOFactory.instance();
        recovery = new AppsLibraryRecovery();

    }

    @Override
    public DataResult<RemoteService> addRemoteService(final UserSession userSession,
        final RemoteService rs)
    {
        RemoteServicesResourceStub proxy =
            APIStubFactory.getInstance(userSession, new RemoteServicesResourceStubImpl(),
                RemoteServicesResourceStub.class);

        return proxy.addRemoteService(rs);
    }

    @Override
    public boolean checkRemoteService(final UserSession userSession, final Integer id)
        throws InfrastructureCommandException
    {
        factory.beginConnection();

        RemoteServiceHB rs = factory.getRemoteServiceDAO().findById(id);

        if (rs == null)
        {
            throw new InfrastructureCommandException("Remote service not found: " + id,
                AbiCloudError.INFRASTRUCTURE_ERROR);
        }

        factory.endConnection();

        boolean oldStatus = rs.getStatus().intValue() == 0 ? false : true;
        boolean newStatus = checkRemoteService(userSession, rs.getUri(), rs.getRemoteServiceType());

        // TODO: Enabled only for test purposes
        if (newStatus != oldStatus)
        {
            // Update New Status for this Remote Service
            rs.setStatus(newStatus ? new Integer(1) : new Integer(0));
            factory.beginConnection();
            factory.getRemoteServiceDAO().makePersistent(rs);
            factory.endConnection();
        }

        return newStatus;
    }

    @Override
    public DataResult<Boolean> deleteRemoteService(final UserSession userSession,
        final RemoteService remoteService)
    {
        RemoteServicesResourceStub proxy =
            APIStubFactory.getInstance(userSession, new RemoteServicesResourceStubImpl(),
                RemoteServicesResourceStub.class);

        return proxy.deleteRemoteService(remoteService);
    }

    @Override
    public List<RemoteService> getAllRemoteServices(final UserSession userSession,
        final Integer idDataCenter)
    {
        List<RemoteService> remoteServices = new ArrayList<RemoteService>();

        RemoteServiceDAO remoteServiceDAO = factory.getRemoteServiceDAO();

        factory.beginConnection();

        List<RemoteServiceHB> remoteServicesHB =
            remoteServiceDAO.getAllRemoteServices(idDataCenter);

        for (RemoteServiceHB rs : remoteServicesHB)
        {
            remoteServices.add(rs.toPojo());
        }

        factory.endConnection();

        return remoteServices;
    }

    @Override
    public RemoteService getRemoteService(final UserSession userSession, final Integer id)
        throws InfrastructureCommandException
    {
        RemoteServiceHB remoteServiceHB = new RemoteServiceHB();

        RemoteServiceDAO remoteServiceDAO = factory.getRemoteServiceDAO();

        try
        {
            factory.beginConnection();

            remoteServiceHB = remoteServiceDAO.findById(id);

            factory.endConnection();
        }
        catch (PersistenceException ex)
        {
            factory.rollbackConnection();
            throw new InfrastructureCommandException(ex.getMessage(),
                ex,
                AbiCloudError.INFRASTRUCTURE_ERROR);
        }

        return remoteServiceHB.toPojo();
    }

    @Override
    public List<RemoteService> getRemoteServicesByType(final UserSession userSession,
        final Integer idDataCenter, final String remoteServiceType)
    {
        List<RemoteService> remoteServices = new ArrayList<RemoteService>();

        RemoteServiceDAO remoteServiceDAO = factory.getRemoteServiceDAO();

        factory.beginConnection();

        List<RemoteServiceHB> remoteServicesHB =
            remoteServiceDAO.getRemoteServicesByType(idDataCenter, valueOf(remoteServiceType));

        for (RemoteServiceHB rs : remoteServicesHB)
        {
            remoteServices.add(rs.toPojo());
        }

        factory.endConnection();

        return remoteServices;
    }

    @Override
    public List<RemoteService> getRemoteServicesByUrl(final UserSession userSession,
        final String uri)
    {
        List<RemoteService> remoteServices = new ArrayList<RemoteService>();

        RemoteServiceDAO remoteServiceDAO = factory.getRemoteServiceDAO();
        List<RemoteServiceHB> remoteServicesHB = remoteServiceDAO.getRemoteServicesByUrl(uri);

        for (RemoteServiceHB rs : remoteServicesHB)
        {
            remoteServices.add(rs.toPojo());
        }

        return remoteServices;
    }

    @Override
    public DataResult<RemoteService> updateRemoteService(final UserSession userSession,
        final RemoteService rs)
    {
        RemoteServicesResourceStub proxy =
            APIStubFactory.getInstance(userSession, new RemoteServicesResourceStubImpl(),
                RemoteServicesResourceStub.class);

        return proxy.modifyRemoteService(rs);
    }

    @Override
    public boolean checkRemoteService(final UserSession userSession, final String serviceUri,
        final com.abiquo.abiserver.business.hibernate.pojohb.service.RemoteServiceType rsType)
    {

        // If a Remote Service cannot be checked by definition, we assume it is
        // OK
        if (!rsType.canBeChecked())
        {
            return true;
        }

        boolean status = false;

        if (serviceUri != null)
        {
            RemoteServiceClient remoteServiceClient = new RemoteServiceClient(serviceUri);
            try
            {
                remoteServiceClient.ping();
                status = true;
            }
            catch (RemoteServiceException e)
            {
                traceLog(SeverityType.MINOR, ComponentType.DATACENTER, EventType.DC_CREATE,
                    userSession, null, null, e.getMessage(), null, null, null, null, null);
                status = false;
            }
        }

        return status;
    }

    @Override
    public List<RemoteServiceType> getRemoteServiceTypes(final UserSession userSession)
        throws InfrastructureCommandException
    {
        List<RemoteServiceType> remoteServicesTypes = new ArrayList<RemoteServiceType>();

        for (com.abiquo.abiserver.business.hibernate.pojohb.service.RemoteServiceType rst : getServiceTypes())
        {
            remoteServicesTypes.add(new RemoteServiceType(rst));
        }

        return remoteServicesTypes;
    }

    protected com.abiquo.abiserver.business.hibernate.pojohb.service.RemoteServiceType[] getServiceTypes()
    {
        return getCommunityServices();
    }
}
