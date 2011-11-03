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

import java.util.ArrayList;
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

    @Override
    public List<com.abiquo.abiserver.pojo.virtualimage.DiskFormatType> getDiskFormatTypes(
        final UserSession userSession)
    {
        /**
         * TODO DiskFormatType API Resource
         */
        List<com.abiquo.abiserver.pojo.virtualimage.DiskFormatType> diskFormats =
            new ArrayList<com.abiquo.abiserver.pojo.virtualimage.DiskFormatType>();

        for (DiskFormatType type : DiskFormatType.values())
        {
            diskFormats.add(new com.abiquo.abiserver.pojo.virtualimage.DiskFormatType(type));
        }

        return diskFormats;
    }

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

    /** Virtual images */

    @Override
    public Void editVirtualImage(final UserSession userSession, final VirtualimageHB vimage)
        throws AppsLibraryCommandException
    {
        // TODO check userSession match idEnterprise of vimage
        final DAOFactory factory = HibernateDAOFactory.instance();
        final Integer newCategoryId = vimage.getCategory().getIdCategory();
        final Integer newRepositoryId = vimage.getRepository().getIdRepository();

        Integer newIconId = null;
        if (vimage.getIcon() != null)
        {
            newIconId = vimage.getIcon().getIdIcon();
        }

        try
        {
            factory.beginConnection();

            //
            final CategoryHB newCategory = factory.getCategoryDAO().findById(newCategoryId);
            final DiskFormatType newDiskFormat = vimage.getType();
            final RepositoryHB newRepository = factory.getRepositoryDAO().findById(newRepositoryId);

            IconHB newIcon = null;
            if (newIconId != null)
            {
                newIcon = factory.getIconDAO().findById(newIconId);
            }

            final VirtualimageHB oldVimage =
                factory.getVirtualImageDAO().findById(vimage.getIdImage());

            oldVimage.setCategory(newCategory);
            oldVimage.setIcon(newIcon);
            oldVimage.setRepository(newRepository);
            oldVimage.setType(newDiskFormat);

            oldVimage.setCpuRequired(vimage.getCpuRequired());
            oldVimage.setHdRequired(vimage.getHdRequired());
            oldVimage.setRamRequired(vimage.getRamRequired());
            oldVimage.setDiskFileSize(vimage.getDiskFileSize());

            oldVimage.setOvfId(vimage.getOvfId());
            oldVimage.setName(vimage.getName());
            oldVimage.setDescription(vimage.getDescription());
            oldVimage.setPathName(vimage.getPathName());
            oldVimage.setStateful(vimage.getStateful());
            oldVimage.setVolumePath(vimage.getVolumePath());
            oldVimage.setIdEnterprise(vimage.getIdEnterprise());
            oldVimage.setShared(vimage.getShared());
            oldVimage.setCostCode(vimage.getCostCode());
            // XXX oldVimage.setMaster(master);

            factory.getVirtualImageDAO().makePersistent(oldVimage);

            factory.endConnection();

        }
        catch (final PersistenceException e)
        {
            factory.rollbackConnection();

            final String cause =
                String.format("Can not edit the virtual image [%s]", vimage.getIdImage());
            throw new AppsLibraryCommandException(cause, e);
        }

        return null;
    }

    @Override
    public Void deleteVirtualImage(final UserSession userSession, final Integer idVirtualImage)
        throws AppsLibraryCommandException
    {
        final List<VirtualappHB> vapps = getVirtualAppliancesWith(idVirtualImage);
        final DAOFactory factory = HibernateDAOFactory.instance();

        VirtualimageHB vimage;

        UserHB user;

        try
        {
            factory.beginConnection();

            vimage = factory.getVirtualImageDAO().findById(idVirtualImage);
            user = factory.getUserDAO().findUniqueByProperty("user", userSession.getUser());

            factory.endConnection();
        }
        catch (final PersistenceException e)
        {
            factory.rollbackConnection();

            final String cause =
                String
                    .format("Can not obtain the virtual image [%s] to be deleted", idVirtualImage);
            throw new AppsLibraryCommandException(cause, e);
        }

        if (vapps != null && vapps.size() > 0)
        {
            final String cause =
                String.format("The virtual image [%s] is being used by some Virtual Appliance."
                    + " It can not be deleted", vimage.getName());

            throw new AppsLibraryCommandException(cause);
        }

        if (vimage.getShared() == 1)
        {
            Integer userEnterpriseId = user.getEnterpriseHB().getIdEnterprise();
            Integer vimageEnterpriseId = vimage.getIdEnterprise();

            if (userEnterpriseId.intValue() != vimageEnterpriseId.intValue())
            {
                final String cause =
                    String
                        .format("Only users from the original enterprise can delete a shared virtual image ");

                throw new AppsLibraryCommandException(cause);
            }
        }

        // final String formatUri = vimage.getDiskFormatTypeHB().getUri();
        String viOvf = vimage.getOvfId();

        if (viOvf == null)
        {
            // this is a bundle of an imported virtual machine (it havent OVF)
            viOvf = codifyBundleImportedOVFid(vimage.getPathName());
        }

        final Integer idEnterprise =
            vimage.getMaster() != null ? vimage.getMaster().getIdEnterprise() : vimage
                .getIdEnterprise();

        final Integer idDatacenter = vimage.getRepository().getDatacenter().getIdDataCenter();

        // TODO is a bundle, also delete its conversions
        // if (vimage.getMaster() != null)
        // { }
        // else (master) delete all the folders content

        // delete of the OVFPackageInstance on the Repository
        try
        {
            final String amServiceUri = getApplianceManagerUriOnDatacenter(idDatacenter);

            ApplianceManagerResourceStubImpl amStub =
                new ApplianceManagerResourceStubImpl(amServiceUri);
            amStub.delete(String.valueOf(idEnterprise), viOvf);

            // amClient.deleteOVFPackage(amServiceUri, String.valueOf(idEnterprise), viOvf);
        }
        catch (final Exception e)
        {
            final String remoteCause = e.getLocalizedMessage();
            final String cause =
                "Can not delet the OVFPackage instance associated to the VirtaulImage:\n"
                    + remoteCause;

            if (!remoteCause.contains("file doesn't exist"))
            {
                throw new AppsLibraryCommandException(cause, e);
            }

        }

        // The virtual image is not being used. We can safely delete it
        try
        {
            factory.beginConnection();

            factory.getVirtualImageDAO().makeTransient(vimage);

            factory.endConnection();
        }
        catch (final PersistenceException e)
        {
            factory.rollbackConnection();

            final String cause = "Can not delete the virtual image";
            throw new AppsLibraryCommandException(cause);
        }

        return null;
    }

    private String codifyBundleImportedOVFid(final String vipath)
    {
        return String.format("http://bundle-imported/%s", vipath);
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
