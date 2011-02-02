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

package com.abiquo.abiserver.eventing;

import static com.abiquo.tracer.Enterprise.enterprise;
import static com.abiquo.tracer.Platform.platform;
import static com.abiquo.tracer.User.user;

import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.abiquo.abiserver.appslibrary.OVFPackageInstanceToVirtualImage;
import com.abiquo.abiserver.appslibrary.VirtualImageException;
import com.abiquo.abiserver.business.hibernate.pojohb.service.RemoteServiceHB;
import com.abiquo.abiserver.business.hibernate.pojohb.service.RemoteServiceType;
import com.abiquo.abiserver.business.hibernate.pojohb.virtualimage.RepositoryHB;
import com.abiquo.abiserver.exception.InfrastructureCommandException;
import com.abiquo.abiserver.exception.PersistenceException;
import com.abiquo.abiserver.persistence.DAOFactory;
import com.abiquo.abiserver.persistence.hibernate.HibernateDAOFactory;
import com.abiquo.appliancemanager.client.ApplianceManagerResourceStubImpl;
import com.abiquo.appliancemanager.transport.OVFPackageInstanceDto;
import com.abiquo.commons.amqp.impl.am.AMCallback;
import com.abiquo.commons.amqp.impl.am.domain.OVFPackageInstanceStatusEvent;
import com.abiquo.tracer.ComponentType;
import com.abiquo.tracer.Platform;
import com.abiquo.tracer.SeverityType;
import com.abiquo.tracer.UserInfo;
import com.abiquo.tracer.client.TracerFactory;

public class AMSink implements AMCallback
{
    private final static Logger logger = LoggerFactory.getLogger(AMSink.class);

    static Platform platform = platform("abicloud").enterprise(
        enterprise("abiCloud").user(user("SYSTEM")));

    static UserInfo ui = new UserInfo("SYSTEM");

    // sprivate ApplianceManagerStub amClient = new ApplianceManagerStubImpl();

    @Override
    public void onDownload(OVFPackageInstanceStatusEvent event)
    {
        logger.debug("VirtualImage [{}] added", event.getOvfId());

        try
        {
            processDownload(event);

            TracerFactory.getTracer().log(
                SeverityType.INFO,
                ComponentType.APPLIANCE_MANAGER,
                com.abiquo.tracer.EventType.VI_ADD,
                String.format("Virtual image [%s] added to repository [%s]", event.getOvfId(),
                    event.getRepositoryLocation()), ui, platform);
        }
        catch (Exception e)
        {
            TracerFactory.getTracer().log(
                SeverityType.NORMAL,
                ComponentType.APPLIANCE_MANAGER,
                com.abiquo.tracer.EventType.VI_ADD,
                String.format("Virtual image [%s] can not be added to repository [%s]: %s",
                    event.getOvfId(), event.getRepositoryLocation(), e.getMessage()),
                new UserInfo("SYSTEM"), platform);
        }

    }

    @Override
    public void onNotDownload(OVFPackageInstanceStatusEvent event)
    {
        logger.debug("VirtualImage [{}] canceled/deleted ", event.getOvfId());

        TracerFactory.getTracer().log(
            SeverityType.INFO,
            ComponentType.APPLIANCE_MANAGER,
            com.abiquo.tracer.EventType.VI_DELETE,
            String.format("Virtual image [%s] deleted from repository [%s]", event.getOvfId(),
                event.getRepositoryLocation()), ui, platform);
    }

    @Override
    public void onError(OVFPackageInstanceStatusEvent event)
    {
        final String errorCause = event.getErrorCause();

        logger.error("VirtualImage download error :" + errorCause);

        TracerFactory.getTracer().log(
            SeverityType.CRITICAL,
            ComponentType.APPLIANCE_MANAGER,
            com.abiquo.tracer.EventType.VI_DOWNLOAD,
            String.format("Error during the virtual image [%s] download to repository [%s]: %s ",
                event.getOvfId(), event.getRepositoryLocation(), errorCause), ui, platform);
    }

    @Override
    public void onDownloading(OVFPackageInstanceStatusEvent event)
    {
        logger.debug("Downloading VirtualImage [{}]", event.getOvfId());
    }

    protected List<OVFPackageInstanceDto> processDownload(final OVFPackageInstanceStatusEvent evnt)
        throws VirtualImageException
    {
        final String ovfId = evnt.getOvfId();
        final String idEnterp = evnt.getEnterpriseId();
        final String repoLocation = evnt.getRepositoryLocation();

        RepositoryHB repository = getRepositoryFromLocation(repoLocation);

        final Integer idDatacenter = repository.getDatacenter().getIdDataCenter();
        String amServiceUri;

        try
        {
            amServiceUri = getApplianceManagerAddress(idDatacenter);
        }
        catch (InfrastructureCommandException e)
        {
            throw new VirtualImageException(e);
        }

        ApplianceManagerResourceStubImpl amStub =
            new ApplianceManagerResourceStubImpl(amServiceUri);

        OVFPackageInstanceDto packageInstance = amStub.getOVFPackageInstance(idEnterp, ovfId);
        // amClient.getOVFPackageInstance(amServiceUri, idEnterp, ovfId);

        List<OVFPackageInstanceDto> disks = new LinkedList<OVFPackageInstanceDto>();
        disks.add(packageInstance);

        // List<VirtualimageHB> insertedImages =
        OVFPackageInstanceToVirtualImage.insertVirtualDiskOnDatabase(disks, repository);

        return disks;
    }

    protected RepositoryHB getRepositoryFromLocation(final String repositoryLocation)
        throws VirtualImageException
    {
        RepositoryHB repository = null;
        DAOFactory daoF = HibernateDAOFactory.instance();

        try
        {
            daoF.beginConnection();

            repository = daoF.getRepositoryDAO().findByLocation(repositoryLocation);

            daoF.endConnection();
        }
        catch (PersistenceException e)
        {
            daoF.rollbackConnection();

            final String cause =
                String.format("Can not determine the Repository "
                    + "with exported repositoryLocation URL [%s]", repositoryLocation);
            throw new VirtualImageException(cause);
        }

        if (repository == null)
        {
            final String cause =
                String.format("Can not determine the Repository "
                    + "with exported repositoryLocation URL [%s]", repositoryLocation);
            throw new VirtualImageException(cause);
        }

        return repository;
    }

    protected String getApplianceManagerAddress(final Integer idDatacenter)
        throws InfrastructureCommandException

    {
        DAOFactory factory = HibernateDAOFactory.instance();
        List<RemoteServiceHB> remotes;
        try
        {

            factory.beginConnection();

            remotes =
                factory.getRemoteServiceDAO().getRemoteServicesByType(idDatacenter,
                    RemoteServiceType.APPLIANCE_MANAGER);

            if (remotes == null || remotes.size() != 1)
            {
                final String cause =
                    String.format("The datacenter [%id] have any ApplianceManager "
                        + "configured as remote service", idDatacenter);
                throw new InfrastructureCommandException(cause);
            }

            factory.endConnection();

        }
        catch (PersistenceException e)
        {
            factory.rollbackConnection();

            final String cause =
                String.format(
                    "Can not obtain the ApplianceManager remote service on Datacenter [%s]",
                    idDatacenter);

            throw new InfrastructureCommandException(cause);
        }
        return remotes.get(0).getUri();
    }

}
