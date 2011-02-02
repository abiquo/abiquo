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

package com.abiquo.abiserver.appslibrary;

import static com.abiquo.tracer.Enterprise.enterprise;
import static com.abiquo.tracer.Platform.platform;
import static com.abiquo.tracer.User.user;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.ws.rs.WebApplicationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.abiquo.abiserver.business.hibernate.pojohb.infrastructure.DatacenterHB;
import com.abiquo.abiserver.business.hibernate.pojohb.service.RemoteServiceHB;
import com.abiquo.abiserver.business.hibernate.pojohb.service.RemoteServiceType;
import com.abiquo.abiserver.business.hibernate.pojohb.virtualimage.RepositoryHB;
import com.abiquo.abiserver.business.hibernate.pojohb.virtualimage.VirtualimageHB;
import com.abiquo.abiserver.exception.InfrastructureCommandException;
import com.abiquo.abiserver.exception.PersistenceException;
import com.abiquo.abiserver.listener.ContextListener;
import com.abiquo.abiserver.persistence.DAOFactory;
import com.abiquo.abiserver.persistence.hibernate.HibernateDAOFactory;
import com.abiquo.appliancemanager.client.ApplianceManagerResourceStubImpl;
import com.abiquo.appliancemanager.transport.OVFPackageInstanceDto;
import com.abiquo.appliancemanager.transport.OVFPackageInstanceStatusDto;
import com.abiquo.appliancemanager.transport.OVFPackageInstanceStatusListDto;
import com.abiquo.appliancemanager.transport.OVFPackageInstanceStatusType;
import com.abiquo.tracer.ComponentType;
import com.abiquo.tracer.Platform;
import com.abiquo.tracer.SeverityType;
import com.abiquo.tracer.UserInfo;
import com.abiquo.tracer.client.TracerFactory;

public class AppsLibraryRecovery
{

    public static final Logger logger = LoggerFactory.getLogger(ContextListener.class);

    private DAOFactory daoF;

    public void initializeApplianceManager(final Integer idDatacenter)
        throws InfrastructureCommandException
    {
        final String amServiceUri = getApplianceManagerAddress(idDatacenter);

        try
        {
            ApplianceManagerResourceStubImpl amStub =
                new ApplianceManagerResourceStubImpl(amServiceUri);
            amStub.checkService();
            // amClient.check(amServiceUri);

            List<Integer> enterpriseIds = getAllEnterprisesId();
            RepositoryHB repo = getRepositoryOrInitialize(idDatacenter, amServiceUri);

            for (Integer idEnterprise : enterpriseIds)
            {
                initializeAMRepositoryVirtualImages(idDatacenter, idEnterprise, repo, amServiceUri);
            }

        }
        catch (Exception e1) // WebApplication or ConfigurationException
        {
            // e1.printStackTrace();
            logger.error(e1.getLocalizedMessage());

            // A user named "SYSTEM" is created
            UserInfo ui = new UserInfo("SYSTEM");

            Platform platform =
                platform("abicloud").enterprise(enterprise("abiCloud").user(user("SYSTEM")));

            TracerFactory.getTracer().log(SeverityType.CRITICAL, ComponentType.APPLIANCE_MANAGER,
                com.abiquo.tracer.EventType.UNKNOWN, e1.getLocalizedMessage(), ui, platform);

            final String cause =
                String.format("The appliance manager at [%s] is not well configured", amServiceUri);
            throw new InfrastructureCommandException(cause);
        }
    }

    /**
     * Post process AM existing images.
     * <p>
     * This method may be overriden in enterprise version to manage virtual image conversions.
     * 
     * @param images The existing images.
     */
    protected void processExistingImages(final Collection<VirtualimageHB> images)
    {
        // Do nothing
    }

    protected RepositoryHB getRepositoryOrInitialize(final Integer idDatacenter,
        final String amServiceUri) throws ConfigurationException, InfrastructureCommandException
    {

        ApplianceManagerResourceStubImpl amStub =
            new ApplianceManagerResourceStubImpl(amServiceUri);

        final String repositoryLocation = amStub.getAMConfiguration().getRepositoryLocation();

        daoF = HibernateDAOFactory.instance();

        RepositoryHB repo;

        try
        {
            daoF.beginConnection();

            repo = daoF.getRepositoryDAO().findByLocation(repositoryLocation);

            daoF.endConnection();
        }
        catch (PersistenceException e)
        {
            daoF.rollbackConnection();
            throw new ConfigurationException("Can not determine the Datacenter Repository");
        }

        if (repo != null)
        {
            final int oldIdDatacenter = repo.getDatacenter().getIdDataCenter();

            if (oldIdDatacenter != idDatacenter)
            {
                final String cause =
                    String.format(
                        "The repository location [%s] is already being used on datacenter [%s]",
                        repositoryLocation, repo.getDatacenter().getName());

                throw new InfrastructureCommandException(cause);
            }
        }

        try
        {
            daoF.beginConnection();

            repo = daoF.getRepositoryDAO().findByDatacenter(idDatacenter);

            daoF.endConnection();
        }
        catch (PersistenceException e)
        {
            daoF.rollbackConnection();
            throw new ConfigurationException("Can not determine the Datacenter Repository");
        }

        if (repo == null)
        {
            repo = createRepository(idDatacenter, repositoryLocation);
        }
        else
        {
            final String oldLocation = repo.getUrl();

            if (!oldLocation.equalsIgnoreCase(repositoryLocation))
            {
                // change the datacenter repository
                try
                {
                    daoF.beginConnection();
                    daoF.getRepositoryDAO().makeTransient(repo);
                    daoF.endConnection();
                }
                catch (PersistenceException e)
                {
                    daoF.rollbackConnection();
                    throw new ConfigurationException("Can not determine the Datacenter Repository");
                }

                repo = createRepository(idDatacenter, repositoryLocation);
            }
        }

        return repo;
    }

    private RepositoryHB createRepository(final Integer idDatacenter,
        final String repositoryLocation) throws ConfigurationException
    {
        // just create it !

        DatacenterHB dc = getDatacenter(idDatacenter);

        RepositoryHB repo = new RepositoryHB();
        repo.setName("virtutal image repo"); // TODO name
        repo.setUrl(repositoryLocation);
        repo.setDatacenter(dc);

        try
        {
            daoF.beginConnection();

            repo = daoF.getRepositoryDAO().makePersistent(repo);

            daoF.endConnection();
        }
        catch (PersistenceException e)
        {
            daoF.rollbackConnection();

            final String cause =
                String.format("Can not create Repository [%s]  on Datacenter [%s]",
                    repositoryLocation, idDatacenter);
            throw new ConfigurationException(cause);
        }

        return repo;
    }

    private List<String> getAvailableOVFPackageInstance(final Integer idDatacenter,
        final Integer idEnterprise, final String amLocation) throws ConfigurationException
    {
        List<String> ovfids = new LinkedList<String>();

        try
        {
            ApplianceManagerResourceStubImpl amStub =
                new ApplianceManagerResourceStubImpl(amLocation);

            OVFPackageInstanceStatusListDto list =
                amStub.getOVFPackagInstanceStatusList(String.valueOf(idEnterprise));
            // amClient.getOVFPackagInstances(amLocation, String.valueOf(idEnterprise));

            for (OVFPackageInstanceStatusDto status : list.getOvfPackageInstancesStatus())
            {
                if (status.getOvfPackageStatus() == OVFPackageInstanceStatusType.DOWNLOAD)
                {
                    ovfids.add(status.getOvfId());
                }
            }
        }
        catch (Exception e)
        {
            final String cause =
                String.format("Can not obtain the list of available packages "
                    + "on datacenter [%s] for enterprise [%s]", idDatacenter, idEnterprise);

            logger.error(cause);
        }

        return ovfids;
    }

    private void initializeAMRepositoryVirtualImages(final Integer idDatacenter,
        final Integer idEnterprise, final RepositoryHB repo, final String amLocation)
        throws ConfigurationException
    {
        List<OVFPackageInstanceDto> disks = new LinkedList<OVFPackageInstanceDto>();

        List<String> availableOvfs =
            getAvailableOVFPackageInstance(idDatacenter, idEnterprise, amLocation);

        for (String ovfid : availableOvfs)
        {
            try
            {

                ApplianceManagerResourceStubImpl amStub =
                    new ApplianceManagerResourceStubImpl(amLocation);
                OVFPackageInstanceDto packageInstance =
                    amStub.getOVFPackageInstance(String.valueOf(idEnterprise), ovfid);
                // amClient.getOVFPackageInstance(amLocation, String.valueOf(idEnterprise), ovfid);

                disks.add(packageInstance);
            }
            catch (WebApplicationException e)
            {
                logger.error("Can not initialize VirtualImage from ovf [{}]", ovfid);
                // XXX reportError ¿?
            }

        }

        try
        {
            List<VirtualimageHB> insertedImages =
                OVFPackageInstanceToVirtualImage.insertVirtualDiskOnDatabase(disks, repo);

            // Process existing images
            processExistingImages(insertedImages);

        }
        catch (VirtualImageException e1)
        {
            logger.error(
                "[FATAL] Can not initialize VirtualImages from the AM repository, caused by: {}",
                e1);
        }
    }

    private String getApplianceManagerAddress(final Integer idDatacenter)
        throws InfrastructureCommandException

    {
        daoF = HibernateDAOFactory.instance();
        List<RemoteServiceHB> remotes;
        try
        {

            daoF.beginConnection();

            remotes =
                daoF.getRemoteServiceDAO().getRemoteServicesByType(idDatacenter,
                    RemoteServiceType.APPLIANCE_MANAGER);

            if (remotes == null || remotes.size() != 1)
            {
                final String cause =
                    String.format("The datacenter [%id] have any ApplianceManager "
                        + "configured as remote service", idDatacenter);
                throw new InfrastructureCommandException(cause);
            }

            daoF.endConnection();

        }
        catch (PersistenceException e)
        {
            daoF.rollbackConnection();

            final String cause =
                String.format(
                    "Can not obtain the ApplianceManager remote service on Datacenter [%s]",
                    idDatacenter);

            throw new InfrastructureCommandException(cause);
        }
        return remotes.get(0).getUri();
    }

    private List<Integer> getAllEnterprisesId() throws InfrastructureCommandException

    {
        List<Integer> enterpriseIds;
        daoF = HibernateDAOFactory.instance();

        try
        {
            daoF.beginConnection();

            enterpriseIds = daoF.getEnterpriseDAO().findAllIds();

            daoF.endConnection();
        }
        catch (PersistenceException e)
        {
            daoF.rollbackConnection();

            final String cause = "Can not obtain all the Enterprise identifiers";
            throw new InfrastructureCommandException(cause);
        }

        return enterpriseIds;
    }

    private DatacenterHB getDatacenter(final Integer idDatacenter) throws ConfigurationException
    {
        DatacenterHB dc;

        try
        {
            daoF.beginConnection();

            dc = daoF.getDataCenterDAO().findById(idDatacenter);

            daoF.endConnection();
        }
        catch (PersistenceException e)
        {
            daoF.rollbackConnection();
            throw new ConfigurationException("Can not determine the Datacenter " + idDatacenter);
        }

        return dc;
    }

    protected class ConfigurationException extends Exception
    {
        public ConfigurationException(String msg)
        {
            super(msg);
        }
    }
}
