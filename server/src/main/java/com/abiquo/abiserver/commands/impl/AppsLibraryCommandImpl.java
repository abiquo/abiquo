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
import java.util.LinkedList;
import java.util.List;

import javax.ws.rs.WebApplicationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.abiquo.abiserver.appslibrary.AppsLibraryRecovery;
import com.abiquo.abiserver.appslibrary.stub.AppsLibraryStub;
import com.abiquo.abiserver.appslibrary.stub.AppsLibraryStubImpl;
import com.abiquo.abiserver.business.hibernate.pojohb.service.RemoteServiceHB;
import com.abiquo.abiserver.business.hibernate.pojohb.user.UserHB;
import com.abiquo.abiserver.business.hibernate.pojohb.virtualappliance.VirtualappHB;
import com.abiquo.abiserver.business.hibernate.pojohb.virtualimage.CategoryHB;
import com.abiquo.abiserver.business.hibernate.pojohb.virtualimage.IconHB;
import com.abiquo.abiserver.business.hibernate.pojohb.virtualimage.RepositoryHB;
import com.abiquo.abiserver.business.hibernate.pojohb.virtualimage.StateConversionEnum;
import com.abiquo.abiserver.business.hibernate.pojohb.virtualimage.VirtualImageConversionsHB;
import com.abiquo.abiserver.business.hibernate.pojohb.virtualimage.VirtualimageHB;
import com.abiquo.abiserver.commands.AppsLibraryCommand;
import com.abiquo.abiserver.commands.BasicCommand;
import com.abiquo.abiserver.config.AbiConfigManager;
import com.abiquo.abiserver.exception.AppsLibraryCommandException;
import com.abiquo.abiserver.exception.PersistenceException;
import com.abiquo.abiserver.persistence.DAOFactory;
import com.abiquo.abiserver.persistence.hibernate.HibernateDAOFactory;
import com.abiquo.abiserver.pojo.authentication.UserSession;
import com.abiquo.abiserver.pojo.virtualimage.VirtualImage;
import com.abiquo.appliancemanager.client.ApplianceManagerResourceStubImpl;
import com.abiquo.appliancemanager.transport.EnterpriseRepositoryDto;
import com.abiquo.appliancemanager.transport.OVFPackageInstanceStatusDto;
import com.abiquo.appliancemanager.transport.OVFPackageInstanceStatusListDto;
import com.abiquo.appliancemanager.transport.OVFPackageInstanceStatusType;
import com.abiquo.model.enumerator.DiskFormatType;
import com.abiquo.model.enumerator.HypervisorType;
import com.abiquo.server.core.appslibrary.OVFPackage;
import com.abiquo.server.core.appslibrary.OVFPackageDto;
import com.abiquo.server.core.appslibrary.OVFPackageListDto;
import com.abiquo.server.core.appslibrary.OVFPackagesDto;
import com.abiquo.server.core.infrastructure.Repository;

public class AppsLibraryCommandImpl extends BasicCommand implements AppsLibraryCommand
{

    private static final Logger logger = LoggerFactory.getLogger(AppsLibraryCommandImpl.class);

    /**
     * DAOFactory to create DAOs
     */

    protected AppsLibraryRecovery recovery = new AppsLibraryRecovery();

    private final static String defaultRepositorySpace =
        AbiConfigManager.getInstance().getAbiConfig().getDefaultRepositorySpace();

    @Override
    public List<com.abiquo.abiserver.pojo.virtualimage.DiskFormatType> getDiskFormatTypes(
        final UserSession userSession)
    {
        List<com.abiquo.abiserver.pojo.virtualimage.DiskFormatType> diskFormats =
            new ArrayList<com.abiquo.abiserver.pojo.virtualimage.DiskFormatType>();

        for (DiskFormatType type : DiskFormatType.values())
        {
            diskFormats.add(new com.abiquo.abiserver.pojo.virtualimage.DiskFormatType(type));
        }

        return diskFormats;
    }

    @Override
    public RepositoryHB getDatacenterRepository(final UserSession userSession,
        final Integer idDatacenter, final Integer idEnterprise) throws AppsLibraryCommandException
    {
        final DAOFactory factory = HibernateDAOFactory.instance();

        /** check images on the repository */
        try
        {
            recovery.initializeApplianceManager(idDatacenter);
        }
        catch (Exception e)
        {
            logger.error("Can not update virtual images on the datacenter " + idDatacenter);
        }

        RepositoryHB repository;

        try
        {
            factory.beginConnection();

            repository = factory.getRepositoryDAO().findByDatacenter(idDatacenter);

            if (repository == null)
            {
                throw new PersistenceException("There isn't a valid respository");
            }

            factory.endConnection();
        }
        catch (final PersistenceException e)
        {
            String cause;

            try
            {
                final String datacenterName =
                    factory.getDataCenterDAO().findById(idDatacenter).getName();
                cause =
                    String.format("The Datacenter [%s] do not have any Repository associated",
                        datacenterName);
            }
            catch (final PersistenceException e1)
            {
                cause =
                    String.format("Can not obtain the datacenter with id [%s]", idDatacenter
                        .toString());
            }

            factory.rollbackConnection();

            throw new AppsLibraryCommandException(cause, e);
        }

        try
        {
            final EnterpriseRepositoryDto enterpriseRepo =
                getEnterpriseRepository(idDatacenter, String.valueOf(idEnterprise));

            repository.setRepositoryCapacityMb(enterpriseRepo.getRepositoryCapacityMb());
            repository
                .setRepositoryEnterpriseUsedMb(enterpriseRepo.getRepositoryEnterpriseUsedMb());
            repository.setRepositoryRemainingMb(enterpriseRepo.getRepositoryRemainingMb());

        }
        catch (AppsLibraryCommandException e)
        {
            logger.warn("{}",e);
            
            repository.setRepositoryCapacityMb(0l);
            repository.setRepositoryEnterpriseUsedMb(0l);
            repository.setRepositoryRemainingMb(0l);
        }

        return repository;
    }

    // /**
    // * It it is not subscribed (the am on database after a server reinit) there is subscribed and
    // * the virtual image library recovered
    // */
    // private void checkApplianceManagerListener(final Integer idDatacenter)
    // throws RemoteServiceException
    // {
    // final String eventingError = AMConsumer.getInstance(idDatacenter).getInitializeError();
    //
    // if (eventingError != null)
    // {
    // final String cause =
    // String
    // .format("The Appliance Manager is not properly configured: %s", eventingError);
    // throw new RemoteServiceException(cause);
    // }
    //
    // }

    private EnterpriseRepositoryDto getEnterpriseRepository(final Integer idDatacenter,
        final String idEnterprise) throws AppsLibraryCommandException
    {
        final String amServiceUri;
        try
        {
            amServiceUri = getApplianceManagerUriOnDatacenter(idDatacenter);

            ApplianceManagerResourceStubImpl amStub =
                new ApplianceManagerResourceStubImpl(amServiceUri);

            return amStub.getRepository(idEnterprise);

            // return amClient.getRepository(amServiceUri, idEnterprise);
        }
        catch (final Exception e)
        {
            final String cause =
                String.format("Can not obtain the repository usage info "
                    + "of the Datacenter [%s] for the Enterprise [%s]. "
                    + "NFS could be bussy (check it later).", idDatacenter, idEnterprise);

            final String detail = e.getMessage();

            throw new AppsLibraryCommandException(cause + "\n" + detail, e);

        }
    }

    /** Category */
    @Override
    public CategoryHB createCategory(final UserSession userSession, final Integer idEnterprise,
        final String categoryName) throws AppsLibraryCommandException
    {
        CategoryHB category;
        final DAOFactory factory = HibernateDAOFactory.instance();
        try
        {

            factory.beginConnection();

            category = factory.getCategoryDAO().findByName(categoryName);

            if (category != null)
            {
                factory.rollbackConnection();

                final String cause = String.format("Category [%s] already exist", categoryName);
                throw new AppsLibraryCommandException(cause);
            }

            category = new CategoryHB();
            category.setName(categoryName);
            category.setIsDefault(0);
            category.setIsErasable(1);

            category = factory.getCategoryDAO().makePersistent(category);

            factory.endConnection();
        }
        catch (final PersistenceException e)
        {
            factory.rollbackConnection();

            final String cause = String.format("Can not create Category [%s]", categoryName);
            throw new AppsLibraryCommandException(cause, e);
        }

        return category;
    }

    @Override
    public Void deleteCategory(final UserSession userSession, final Integer idCategory)
        throws AppsLibraryCommandException
    {

        // First, we have to check if there are any virtual image assigned to this category
        assignDefaultCategoryToVirtualImagesWithCategory(idCategory);

        final DAOFactory factory = HibernateDAOFactory.instance();
        try
        {
            factory.beginConnection();

            // Getting the category that will be deleted
            final CategoryHB category = factory.getCategoryDAO().findById(idCategory);

            if (category == null)
            {
                factory.rollbackConnection();

                final String cause =
                    String.format("There aren't any category with id [%s]", idCategory.toString());
                throw new AppsLibraryCommandException(cause);
            }

            
            if (category.getIsDefault() > 0)
            {
                factory.rollbackConnection();
                throw new AppsLibraryCommandException("'" + category.getName()
                    + "' is the default Category. Can not delete it");
            }

            factory.getCategoryDAO().makeTransient(category);

            factory.endConnection();
        }
        catch (final PersistenceException e)
        {
            factory.rollbackConnection();

            final String cause = String.format("Can not delete category with id [%s]", idCategory);
            throw new AppsLibraryCommandException(cause, e);
        }
        return null;
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

    @Override
    public List<CategoryHB> getCategories(final UserSession userSession, final Integer idEnterprise)
        throws AppsLibraryCommandException

    {
        List<CategoryHB> categories;
        final DAOFactory factory = HibernateDAOFactory.instance();
        try
        {
            factory.beginConnection();

            categories = factory.getCategoryDAO().findAll();

            factory.endConnection();
        }
        catch (final PersistenceException e)
        {
            factory.rollbackConnection();

            final String cause = "Can not obtain the list of ''Categories''";
            throw new AppsLibraryCommandException(cause, e);
        }

        return categories;
    }

    /**
     * Icon
     * 
     * @throws AppsLibraryCommandException
     */
    @Override
    public IconHB createIcon(final UserSession userSession, final Integer idEnterprise, IconHB icon)
        throws AppsLibraryCommandException
    {
        final DAOFactory factory = HibernateDAOFactory.instance();
        try
        {
            factory.beginConnection();

            // Since client does not support Integer null values, force here a null value to perform
            // a create instead of an update
            icon.setIdIcon(null);
            icon = factory.getIconDAO().makePersistent(icon);

            factory.endConnection();
        }
        catch (final PersistenceException e)
        {
            factory.rollbackConnection();

            final String cause = String.format("Can not create the Icon [%s]", icon.getName());
            throw new AppsLibraryCommandException(cause, e);
        }

        return icon;
    }

    @Override
    public Void editIcon(final UserSession userSession, final IconHB icon)
        throws AppsLibraryCommandException
    {
        final DAOFactory factory = HibernateDAOFactory.instance();
        try
        {
            factory.beginConnection();

            final IconHB storedIcon = factory.getIconDAO().findById(icon.getIdIcon());
            if (storedIcon == null)
            {
                factory.rollbackConnection();

                final String cause =
                    String.format("There is any icon with provided id [%s]", icon.getIdIcon());
                throw new AppsLibraryCommandException(cause);
            }

            storedIcon.setName(icon.getName());
            storedIcon.setPath(icon.getPath());

            factory.getIconDAO().makePersistent(storedIcon);

            factory.endConnection();
        }
        catch (final PersistenceException e)
        {
            factory.rollbackConnection();

            final String cause = String.format("Can not edit the Icon [%s]", icon.getName());
            throw new AppsLibraryCommandException(cause, e);
        }
        return null;
    }

    @Override
    public Void deleteIcon(final UserSession userSession, final Integer idIcon)
        throws AppsLibraryCommandException
    {
        final DAOFactory factory = HibernateDAOFactory.instance();
        try
        {
            factory.beginConnection();

            // Checking if this icon is being used by a VirtualImage
            final Collection<VirtualimageHB> viCurrentIcon =
                factory.getVirtualImageDAO().findByIcon(idIcon);

            if (viCurrentIcon != null && viCurrentIcon.size() > 0)
            {
                factory.rollbackConnection();

                final String cause =
                    "The current icon is being used on some Virtual Images, it can not be deleted";
                throw new AppsLibraryCommandException(cause);
            }

            final IconHB icon = factory.getIconDAO().findById(idIcon);

            if (icon == null)
            {
                factory.rollbackConnection();

                final String cause =
                    String.format("There is any icon with provided id [%s]", idIcon);
                throw new AppsLibraryCommandException(cause);
            }

            factory.getIconDAO().makeTransient(icon);

            factory.endConnection();
        }
        catch (final PersistenceException e)
        {
            factory.rollbackConnection();

            final String cause = String.format("Can not delete the Icon [id %s]", idIcon);
            throw new AppsLibraryCommandException(cause, e);
        }

        return null;
    }

    @Override
    public List<IconHB> getIcons(final UserSession userSession, final Integer idEnterprise)
        throws AppsLibraryCommandException
    {
        List<IconHB> icons;
        final DAOFactory factory = HibernateDAOFactory.instance();
        try
        {
            factory.beginConnection();

            icons = factory.getIconDAO().findAll();

            factory.endConnection();
        }
        catch (final PersistenceException e)
        {
            factory.rollbackConnection();

            final String cause = "Can not obtain the icon list";
            throw new AppsLibraryCommandException(cause, e);
        }

        return icons;
    }

    /** Virtual images */

    /**
     * @param idRepo, if 0, indicate stateful images
     * @param idCategory, if 0 indicate return all the categories
     */
    @Override
    public List<VirtualimageHB> getVirtualImageByCategory(final UserSession userSession,
        final Integer idEnterprise, final Integer idRepo, final Integer idCategory)
        throws AppsLibraryCommandException
    {
        // TODO check the userSession belongs to the same idEnterprise
        return getAvailableVirtualImages(idEnterprise, idRepo, idCategory);
    }

    private List<VirtualimageHB> getAvailableVirtualImages(final Integer idEnterprise,
        final Integer idRepository, final Integer idCategory) throws AppsLibraryCommandException
    {
        List<VirtualimageHB> virtualImages;
        final DAOFactory factory = HibernateDAOFactory.instance();
        try
        {
            factory.beginConnection();

            if (idCategory != null && idCategory != 0)
            {
                virtualImages =
                    factory.getVirtualImageDAO().getImagesByEnterpriseAndRepositoryAndCategory(
                        idEnterprise, idRepository, idCategory);
            }
            else
            {
                virtualImages =
                    factory.getVirtualImageDAO().getImagesByEnterpriseAndRepository(idEnterprise,
                        idRepository);
            }

            factory.endConnection();
        }
        catch (final PersistenceException e)
        {
            factory.rollbackConnection();
            final String cause =
                String.format("Can not obtain the list of available virtual images "
                    + "for Enterprise[id %s] on Repository[id %s]", idEnterprise, idRepository);
            throw new AppsLibraryCommandException(cause, e);
        }

        return virtualImages;
    }

    /**
     * @param idRepo, if 0, indicate stateful images
     * @param idCategory, if 0 indicate return all the categories
     */
    @Override
    public List<VirtualImage> getVirtualImageByCategoryAndHypervisorCompatible(
        final UserSession userSession, final Integer idEnterprise, final Integer idRepository,
        final Integer idCategory, final Integer idHypervisorType)
        throws AppsLibraryCommandException
    {
        // TODO check the userSession belongs to the same idEnterprise
        final DAOFactory factory = HibernateDAOFactory.instance();
        final List<VirtualImage> virtualImages = new LinkedList<VirtualImage>();

        final HypervisorType hypervisorType = HypervisorType.fromId(idHypervisorType);

        final Collection<VirtualimageHB> virtualImagesHB =
            getAvailableVirtualImages(idEnterprise, idRepository, idCategory);

        try
        {
            factory.beginConnection();

            for (final VirtualimageHB virtualImageHB : virtualImagesHB)
            {
                if (isVirtualImageConvertedOrCompatible(virtualImageHB, hypervisorType))
                {
                    virtualImages.add(virtualImageHB.toPojo());
                }
            }

            factory.endConnection();
        }
        catch (final PersistenceException e)
        {
            factory.rollbackConnection();

            final String cause = "Can not obtain the list of compatible virtual images";
            throw new AppsLibraryCommandException(cause, e);
        }

        return virtualImages;
    }

    /**
     * Return true the virtual image format is compatible.
     * 
     * <pre>
     * Premium: if there virtual image conversions check FINISH state.
     * </pre>
     */
    private Boolean isVirtualImageConvertedOrCompatible(final VirtualimageHB vi,
        final HypervisorType hypervisorType)
    {

        final DAOFactory factory = HibernateDAOFactory.instance();
        final DiskFormatType virtualImageFormatType = vi.getType();

        if (hypervisorType.isCompatible(virtualImageFormatType))
        {
            return true;
        }

        final Collection<VirtualImageConversionsHB> conversions =
            factory.getVirtualImageConversionsDAO().getConversion(vi, hypervisorType.baseFormat);

        // the conversion do not exist
        if (conversions == null || conversions.size() == 0)
        {
            return false;
        }

        // Conversion is the *single* conversion of the desired format
        for (final VirtualImageConversionsHB conversion : conversions)
        {
            if (conversion.getState() != StateConversionEnum.FINISHED)
            {
                return false;
            }
            else if (hypervisorType.isCompatible(conversion.getTargetType()))
            {
                return true;
            }
        }

        return false;
    }

    private DiskFormatType getBaseDiskFormatType(final Integer idHypervisorType)
    {
        return HypervisorType.fromId(idHypervisorType).baseFormat;
    }

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
            oldVimage.setTreaty(vimage.getTreaty());
            oldVimage.setStateful(vimage.getStateful());
            oldVimage.setVolumePath(vimage.getVolumePath());
            oldVimage.setIdEnterprise(vimage.getIdEnterprise());
            oldVimage.setShared(vimage.getShared());
            oldVimage.setCostCode(vimage.getCostCode());
            // oldVimage.setDeleted(deleted);
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

        final Integer idEnterprise = vimage.getIdEnterprise();
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

    private String getApplianceManagerUriOnRepository(final Integer idRepository)
        throws AppsLibraryCommandException
    {
        final DAOFactory factory = HibernateDAOFactory.instance();
        RepositoryHB repository;
        try
        {
            factory.beginConnection();

            repository = factory.getRepositoryDAO().findById(idRepository);

            factory.endConnection();
        }
        catch (final PersistenceException e)
        {
            factory.rollbackConnection();
            final String cause =
                String.format("Can not obtain the datacenter associated to repository [%s]",
                    idRepository);
            throw new AppsLibraryCommandException(cause);
        }

        final Integer idDatacenter = repository.getDatacenter().getIdDataCenter();

        return getApplianceManagerUriOnDatacenter(idDatacenter);
    }

    private Integer getDatacenterIdByRepository(final Integer idRepository)
    {
        Integer idDatacenter;

        final DAOFactory daoF = HibernateDAOFactory.instance();

        daoF.beginConnection();

        idDatacenter =
            daoF.getRepositoryDAO().findById(idRepository).getDatacenter().getIdDataCenter();

        daoF.endConnection();

        return idDatacenter;
    }

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

    /** List. */
    @Override
    public List<String> getOVFPackageListName(final UserSession userSession,
        final Integer idEnterprise) throws AppsLibraryCommandException
    {
        List<String> packageNameList;

        AppsLibraryStub appsLibClient = new AppsLibraryStubImpl(userSession);

        try
        {
            packageNameList = appsLibClient.getOVFPackageListName(idEnterprise);
        }
        catch (final WebApplicationException e)
        {
            final String remoteCause = (String) e.getResponse().getEntity();
            final String cause = "Can not obtain the list of OVFPackageList names.\n" + remoteCause;
            throw new AppsLibraryCommandException(cause, e);
        }

        if (packageNameList == null || packageNameList.size() == 0)
        {
            final String listName = addDefaultOVFPackageList(userSession, idEnterprise);

            packageNameList = new LinkedList<String>();
            packageNameList.add(listName);
        }

        return packageNameList;
    }

    private String addDefaultOVFPackageList(final UserSession userSession,
        final Integer idEnterprise)
    {
        if (defaultRepositorySpace == null || defaultRepositorySpace.isEmpty())
        {
            logger.debug("There aren't any default repository space defined");
            return null;
        }
        else
        {
            try
            {
                AppsLibraryStub appsLibClient = new AppsLibraryStubImpl(userSession);

                logger.debug("Adding default repository space at [{}]", defaultRepositorySpace);

                final OVFPackageListDto listDto =
                    appsLibClient.createOVFPackageList(idEnterprise, defaultRepositorySpace);

                return listDto.getName();
            }
            catch (final WebApplicationException e)
            {
                final String cause =
                    String.format("Can not create the default OVFPackageList at [%s]",
                        defaultRepositorySpace);
                logger.error(cause); // TODO tracer

                return null;
            }
        }
    }

    @Override
    public OVFPackageListDto getOVFPackageList(final UserSession userSession,
        final Integer idEnterprise, final String nameOVFPackageList)
        throws AppsLibraryCommandException
    {
        OVFPackageListDto packageList;

        try
        {
            AppsLibraryStub appsLibClient = new AppsLibraryStubImpl(userSession);

            packageList = appsLibClient.getOVFPackageList(idEnterprise, nameOVFPackageList);
        }
        catch (final WebApplicationException e)
        {
            final String remoteCause = (String) e.getResponse().getEntity();
            final String cause =
                String.format("Can not obtain the OVFPackageList [%s]\n%s", nameOVFPackageList,
                    remoteCause);
            throw new AppsLibraryCommandException(cause, e);
        }

        return packageList;
    }

    @Override
    public OVFPackageListDto createOVFPackageList(final UserSession userSession,
        final Integer idEnterprise, final String ovfpackageListURL)
        throws AppsLibraryCommandException
    {
        OVFPackageListDto packageList;

        try
        {
            AppsLibraryStub appsLibClient = new AppsLibraryStubImpl(userSession);

            packageList = appsLibClient.createOVFPackageList(idEnterprise, ovfpackageListURL);
        }
        catch (final WebApplicationException e)
        {
            String cause =
                String.format("Can not create the OVFPackageList [%s]", ovfpackageListURL);

            try
            {
                final String reason = (String) e.getResponse().getEntity();
                if (reason != null)
                {
                    cause = cause + "\nCaused by: " + reason;
                }
            }
            catch (final Exception e2)
            {

            }

            throw new AppsLibraryCommandException(cause, e);
        }
        catch (final Exception e)
        {
            String cause =
                String.format("Can not create the OVFPackageList [%s]", ovfpackageListURL);

            try
            {
                final String reason = e.getMessage();
                if (reason != null)
                {
                    cause = cause + "\nCaused by: " + reason;
                }
            }
            catch (final Exception e2)
            {

            }

            throw new AppsLibraryCommandException(cause, e);
        }

        return packageList;
    }

    @Override
    public OVFPackageListDto refreshOVFPackageList(final UserSession userSession,
        final Integer idEnterprise, final String nameOvfpackageList)
        throws AppsLibraryCommandException
    {
        OVFPackageListDto packageList;

        try
        {
            AppsLibraryStub appsLibClient = new AppsLibraryStubImpl(userSession);

            packageList = appsLibClient.refreshOVFPackageList(idEnterprise, nameOvfpackageList);
        }
        catch (final WebApplicationException e)
        {
            final String remoteCause = (String) e.getResponse().getEntity();
            final String cause =
                String.format("Can not refresh the OVFPackageList [%s].\n", nameOvfpackageList,
                    remoteCause);
            throw new AppsLibraryCommandException(cause, e);
        }

        return packageList;
    }

    @Override
    public Void deleteOVFPackageList(final UserSession userSession, final Integer idEnterprise,
        final String nameOvfpackageList) throws AppsLibraryCommandException
    {
        try
        {
            AppsLibraryStub appsLibClient = new AppsLibraryStubImpl(userSession);

            appsLibClient.deleteOVFPackageList(idEnterprise, nameOvfpackageList);
        }
        catch (final WebApplicationException e)
        {
            final String remoteCause = (String) e.getResponse().getEntity();
            final String cause =
                String.format("Can not delete the OVFPackageList [%s].\n", nameOvfpackageList,
                    remoteCause);
            throw new AppsLibraryCommandException(cause, e);
        }

        return null;
    }

    /** DC specific status. */

    @Override
    public Void startDownloadOVFPackage(final UserSession userSession,
        final List<String> idsOvfpackage, final Integer idEnterprise, final Integer idRepository)
        throws AppsLibraryCommandException
    {
        final String amServiceUri = getApplianceManagerUriOnRepository(idRepository);
        final String idEnterpriseSt = String.valueOf(idEnterprise);

        ApplianceManagerResourceStubImpl amStub =
            new ApplianceManagerResourceStubImpl(amServiceUri);

        boolean checkCanWrite = true;
        amStub.getRepository(idEnterpriseSt, checkCanWrite);

        for (final String ovfId : idsOvfpackage)
        {
            try
            {

                amStub.createOVFPackageInstance(idEnterpriseSt, ovfId);

                // stat = amClient.installOVFPackage(amServiceUri, idEnterpriseSt, ovfId);
                // TODO stat is not used, after the ''startDownload'' a ''getStatus'' is called.
            }
            catch (final Exception e)
            {
                final String remoteCause = e.getMessage();
                logger.error("Can not install package [{}] caused by\n{}", ovfId, remoteCause);
            }
        }

        return null;
    }

    @Override
    public OVFPackageInstanceStatusDto cancelDownloadOVFPackage(final UserSession userSession,
        final String idOvfpackage, final Integer idEnterprise, final Integer idRepository)
        throws AppsLibraryCommandException
    {
        final String amServiceUri = getApplianceManagerUriOnRepository(idRepository);
        final String idEnterpriseSt = String.valueOf(idEnterprise);

        try
        {
            ApplianceManagerResourceStubImpl amStub =
                new ApplianceManagerResourceStubImpl(amServiceUri);

            amStub.delete(idEnterpriseSt, idOvfpackage);
            // .cancelOVFPackage(amServiceUri, idEnterpriseSt, idOvfpackage);
        }
        catch (final Exception e)
        {
            final String remoteCause = e.getMessage();
            final String cause =
                String.format("Can not cancell the OVF package [%s].\n%s", idOvfpackage,
                    remoteCause);
            throw new AppsLibraryCommandException(cause, e);
        }

        final OVFPackageInstanceStatusDto status = new OVFPackageInstanceStatusDto();
        status.setOvfId(idOvfpackage);
        status.setOvfPackageStatus(OVFPackageInstanceStatusType.NOT_DOWNLOAD);

        return status;
    }

    @Override
    public OVFPackageInstanceStatusListDto refreshOVFPackageStatus(final UserSession userSession,
        final List<String> idsOvfpackage, final Integer idEnterprise, final Integer idRepository)
        throws AppsLibraryCommandException
    {
        final OVFPackageInstanceStatusListDto statusList = new OVFPackageInstanceStatusListDto();

        final String amServiceUri = getApplianceManagerUriOnRepository(idRepository);
        final String idEnterpriseSt = String.valueOf(idEnterprise);

        for (final String ovfId : idsOvfpackage)
        {
            OVFPackageInstanceStatusDto status;
            try
            {
                ApplianceManagerResourceStubImpl amStub =
                    new ApplianceManagerResourceStubImpl(amServiceUri);

                status = amStub.getOVFPackageInstanceStatus(idEnterpriseSt, ovfId);
                // status = amClient.getOVFPackageStatus(amServiceUri, idEnterpriseSt, ovfId);
            }
            catch (final Exception e)
            {
                final String errorCause =
                    String.format("Can not obtain the OVFStatus from [%s]", amServiceUri);

                status = new OVFPackageInstanceStatusDto();
                status.setOvfId(ovfId);
                status.setOvfPackageStatus(OVFPackageInstanceStatusType.ERROR);
                status.setErrorCause(errorCause);
            }

            statusList.getOvfPackageInstancesStatus().add(status);
        }

        return statusList;
    }

    @Override
    public OVFPackageInstanceStatusListDto getOVFPackageListStatus(final UserSession userSession,
        final String nameOVFPackageList, final Integer idEnterprise, final Integer idRepository)
        throws AppsLibraryCommandException
    {
        final List<String> ovfIds =
            getOVFPackageUrlOnList(userSession, idEnterprise, nameOVFPackageList);

        return refreshOVFPackageStatus(userSession, ovfIds, idEnterprise, idRepository);
    }

    private List<String> getOVFPackageUrlOnList(final UserSession userSession,
        final Integer idEnterprise, final String nameOVFPackageList)
        throws AppsLibraryCommandException
    {
        final List<String> ovfIds = new LinkedList<String>();

        OVFPackageListDto packageList;

        try
        {
            AppsLibraryStub appsLibClient = new AppsLibraryStubImpl(userSession);

            packageList = appsLibClient.getOVFPackageList(idEnterprise, nameOVFPackageList);
        }
        catch (final WebApplicationException e)
        {
            final String cause =
                String.format("Can not obtain the OVFPackageList [%s].\n%s", nameOVFPackageList);
            throw new AppsLibraryCommandException(cause, e);
        }

        for (final OVFPackageDto ovfPackage : packageList.getOvfPackages())
        {
            ovfIds.add(ovfPackage.getUrl());
        }

        return ovfIds;
    }

    /**
     * @see com.abiquo.abiserver.commands.AppsLibraryCommand#getOVFPackageInstanceStatus(com.abiquo.abiserver.pojo.authentication.UserSession,
     *      java.lang.String, java.lang.Integer, java.lang.Integer, java.lang.Integer)
     */
    @Override
    public OVFPackageInstanceStatusDto getOVFPackageInstanceStatus(UserSession userSession,
        final String nameOVFPackageList, Integer idOVFPackageName, Integer idEnterprise,
        Integer idRepository) throws AppsLibraryCommandException
    {
        final String ovfIds =
            getOVFPackageInstanceUrl(userSession, idEnterprise, nameOVFPackageList,
                idOVFPackageName);

        return refreshOVFPackageInstanceStatus(userSession, ovfIds, idEnterprise, idRepository);
    }

    /**
     * @see com.abiquo.abiserver.commands.AppsLibraryCommand#refreshOVFPackageInstanceStatus(com.abiquo.abiserver.pojo.authentication.UserSession,
     *      java.lang.String, java.lang.Integer, java.lang.Integer)
     */
    @Override
    public OVFPackageInstanceStatusDto refreshOVFPackageInstanceStatus(UserSession userSession,
        String idsOvfInstance, Integer idEnterprise, Integer idRepository)
        throws AppsLibraryCommandException
    {

        final String amServiceUri = getApplianceManagerUriOnRepository(idRepository);
        final String idEnterpriseSt = String.valueOf(idEnterprise);

        OVFPackageInstanceStatusDto status;
        try
        {
            ApplianceManagerResourceStubImpl amStub =
                new ApplianceManagerResourceStubImpl(amServiceUri);

            status = amStub.getCurrentOVFPackageInstanceStatus(idEnterpriseSt, idsOvfInstance);
        }
        catch (final Exception e)
        {
            final String errorCause =
                String.format("Can not obtain the OVFStatus from [%s]", amServiceUri);

            status = new OVFPackageInstanceStatusDto();
            status.setOvfId(idsOvfInstance);
            status.setOvfPackageStatus(OVFPackageInstanceStatusType.ERROR);
            status.setErrorCause(errorCause);
        }
        return status;

    }

    /**
     * Retorna la URL del {@link OVFPackageInstance}.
     * 
     * @param userSession Current logged.
     * @param userSession Data from the current user.
     * @param idsOvfInstance Name of the item to refresh.
     * @param idEnterprise Id of {@link Enterprise} to which this {@link OVFPackage} belongs.
     * @param idRepository Id of the {@link Repository} to which the {@link OVFPackage} belongs.
     * @return URL.
     * @throws AppsLibraryCommandException String
     */
    private String getOVFPackageInstanceUrl(final UserSession userSession,
        final Integer idEnterprise, final String nameOVFPackageList,
        final Integer nameOVFPackageInstance) throws AppsLibraryCommandException
    {
        OVFPackagesDto packageList;

        try
        {
            AppsLibraryStub appsLibClient = new AppsLibraryStubImpl(userSession);

            packageList = appsLibClient.getOVFPackages(idEnterprise, nameOVFPackageList);
        }
        catch (final WebApplicationException e)
        {
            final String cause =
                String.format("Can not obtain the OVFPackageList [%s].\n%s", nameOVFPackageList);
            throw new AppsLibraryCommandException(cause, e);
        }
        for (int i = 0; i < packageList.getTotalSize(); i++)
        {
            final OVFPackageDto ovfPackage = packageList.getCollection().get(i++);
            if (nameOVFPackageInstance.equals(ovfPackage.getId()))
            {
                return ovfPackage.getUrl();
            }
        }

        return null;
    }
}
