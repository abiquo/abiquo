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

import java.util.Collection;
import java.util.List;

import com.abiquo.abiserver.appslibrary.AppsLibraryRecovery;
import com.abiquo.abiserver.business.hibernate.pojohb.service.RemoteServiceHB;
import com.abiquo.abiserver.business.hibernate.pojohb.user.UserHB;
import com.abiquo.abiserver.business.hibernate.pojohb.virtualappliance.VirtualappHB;
import com.abiquo.abiserver.business.hibernate.pojohb.virtualimage.CategoryHB;
import com.abiquo.abiserver.business.hibernate.pojohb.virtualimage.IconHB;
import com.abiquo.abiserver.business.hibernate.pojohb.virtualimage.RepositoryHB;
import com.abiquo.abiserver.business.hibernate.pojohb.virtualimage.VirtualimageHB;
import com.abiquo.abiserver.commands.AppsLibraryCommand;
import com.abiquo.abiserver.commands.BasicCommand;
import com.abiquo.abiserver.exception.AppsLibraryCommandException;
import com.abiquo.abiserver.exception.PersistenceException;
import com.abiquo.abiserver.persistence.DAOFactory;
import com.abiquo.abiserver.persistence.hibernate.HibernateDAOFactory;
import com.abiquo.abiserver.pojo.authentication.UserSession;
import com.abiquo.appliancemanager.client.ApplianceManagerResourceStubImpl;
import com.abiquo.model.enumerator.DiskFormatType;

public class AppsLibraryCommandImpl extends BasicCommand implements AppsLibraryCommand
{

    /**
     * DAOFactory to create DAOs
     */

    protected AppsLibraryRecovery recovery = new AppsLibraryRecovery();

    private void assignDefaultCategoryToVirtualImagesWithCategory(final Integer idCategory)
        throws AppsLibraryCommandException
    {
        final DAOFactory factory = HibernateDAOFactory.instance();
        try
        {
            factory.beginConnection();

            final Collection<VirtualimageHB> viCurrentCategory =
                factory.getVirtualImageDAO().findByCategory(idCategory);

            if (viCurrentCategory != null && viCurrentCategory.size() > 0)
            {
                // Setting all the virtual images that belong to this category to the default
                // category
                final CategoryHB defaultCategoryHB = factory.getCategoryDAO().findDefault();

                for (final VirtualimageHB virtualImageHB : viCurrentCategory)
                {
                    virtualImageHB.setCategory(defaultCategoryHB);

                    factory.getVirtualImageDAO().makePersistent(virtualImageHB);
                }
            }

            factory.endConnection();
        }
        catch (final PersistenceException e)
        {
            factory.rollbackConnection();

            final String cause =
                String.format("Can not update VirtualImages with the current category id [%s]",
                    idCategory);
            throw new AppsLibraryCommandException(cause);
        }
    }

    private List<VirtualappHB> getVirtualAppliancesWith(final Integer idVirtualImage)
        throws AppsLibraryCommandException
    {
        List<VirtualappHB> vapps;
        final DAOFactory factory = HibernateDAOFactory.instance();
        try
        {
            factory.beginConnection();

            vapps =
                factory.getVirtualApplianceDAO().findByUsingVirtualImage(
                    String.valueOf(idVirtualImage));

            factory.endConnection();
        }
        catch (final PersistenceException e)
        {
            factory.rollbackConnection();

            final String cause =
                String.format("Can not obtain the list of virtual appliances "
                    + "using the virtual image [%s]", idVirtualImage);
            throw new AppsLibraryCommandException(cause, e);
        }

        return vapps;
    }

    /** #################### FIXME delete virtual image requires #################### */

    private String getApplianceManagerUriOnDatacenter(final Integer idDatacenter)
        throws AppsLibraryCommandException
    {
        final DAOFactory factory = HibernateDAOFactory.instance();
        final String cause =
            String.format("Can not obtain the ApplianceManager remote service on datacenter [%s]",
                idDatacenter);
        List<RemoteServiceHB> amRemoteServices;

        try
        {
            factory.beginConnection();

            amRemoteServices =
                factory
                    .getRemoteServiceDAO()
                    .getRemoteServicesByType(
                        idDatacenter,
                        com.abiquo.abiserver.business.hibernate.pojohb.service.RemoteServiceType.APPLIANCE_MANAGER);

            factory.endConnection();
        }
        catch (final PersistenceException e)
        {
            factory.rollbackConnection();

            throw new AppsLibraryCommandException(cause, e);
        }

        if (amRemoteServices == null || amRemoteServices.size() != 1)
        {
            throw new AppsLibraryCommandException(cause);
        }

        return amRemoteServices.get(0).getUri();
    }

}
