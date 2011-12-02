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

package com.abiquo.api.services.appslibrary;

import static com.abiquo.api.util.URIResolver.buildPath;

import java.util.LinkedList;
import java.util.List;

import javax.ws.rs.core.MultivaluedMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.abiquo.api.exceptions.APIError;
import com.abiquo.api.resources.EnterpriseResource;
import com.abiquo.api.resources.EnterprisesResource;
import com.abiquo.api.resources.appslibrary.CategoriesResource;
import com.abiquo.api.resources.appslibrary.CategoryResource;
import com.abiquo.api.resources.appslibrary.DatacenterRepositoriesResource;
import com.abiquo.api.resources.appslibrary.DatacenterRepositoryResource;
import com.abiquo.api.resources.appslibrary.IconResource;
import com.abiquo.api.resources.appslibrary.IconsResource;
import com.abiquo.api.resources.appslibrary.VirtualImageResource;
import com.abiquo.api.resources.appslibrary.VirtualImagesResource;
import com.abiquo.api.services.EnterpriseService;
import com.abiquo.api.services.InfrastructureService;
import com.abiquo.api.util.URIResolver;
import com.abiquo.appliancemanager.client.ApplianceManagerResourceStubImpl;
import com.abiquo.model.enumerator.DiskFormatType;
import com.abiquo.model.enumerator.HypervisorType;
import com.abiquo.model.rest.RESTLink;
import com.abiquo.server.core.appslibrary.AppsLibraryRep;
import com.abiquo.server.core.appslibrary.Category;
import com.abiquo.server.core.appslibrary.Icon;
import com.abiquo.server.core.appslibrary.VirtualImage;
import com.abiquo.server.core.appslibrary.VirtualImageConversionDAO;
import com.abiquo.server.core.appslibrary.VirtualImageDto;
import com.abiquo.server.core.enterprise.DatacenterLimits;
import com.abiquo.server.core.enterprise.Enterprise;
import com.abiquo.server.core.infrastructure.Datacenter;
import com.abiquo.server.core.infrastructure.Repository;
import com.abiquo.server.core.infrastructure.RepositoryDAO;
import com.abiquo.tracer.ComponentType;
import com.abiquo.tracer.EventType;
import com.abiquo.tracer.SeverityType;

@Service
public class VirtualImageService extends DefaultApiServiceWithApplianceManagerClient
{
    @Autowired
    private RepositoryDAO repositoryDao;

    @Autowired
    private InfrastructureService infrastructureService;

    @Autowired
    private EnterpriseService enterpriseService;

    @Autowired
    private AppsLibraryRep appsLibraryRep;

    @Autowired
    private CategoryService categoryService;

    @Transactional(readOnly = true)
    public Repository getDatacenterRepository(final Integer dcId)
    {
        Datacenter datacenter = infrastructureService.getDatacenter(dcId);
        return repositoryDao.findByDatacenter(datacenter);
    }

    /**
     * Get repository for allowed (limits defined) datacenters for the provided enterprise.
     */
    @Transactional(readOnly = true)
    public List<Repository> getDatacenterRepositories(final Integer enterpriseId)
    {
        List<Repository> repos = new LinkedList<Repository>();

        for (DatacenterLimits dclimit : enterpriseService.findLimitsByEnterprise(enterpriseId))
        {
            repos.add(getDatacenterRepository(dclimit.getDatacenter().getId()));
        }

        return repos;
    }

    @Transactional(readOnly = true)
    public VirtualImage getVirtualImage(final Integer enterpriseId, final Integer datacenterId,
        final Integer virtualImageId)
    {
        // Check that the enterprise can use the datacenter (also checks enterprise and datacenter
        // exists)
        checkEnterpriseCanUseDatacenter(enterpriseId, datacenterId);

        VirtualImage virtualImage = appsLibraryRep.findVirtualImageById(virtualImageId);
        if (virtualImage == null)
        {
            addNotFoundErrors(APIError.NON_EXISTENT_VIRTUALIMAGE);
            flushErrors();
        }

        return virtualImage;
    }

    /**
     * Gets the list of compatible(*) virtual images available in the provided enterprise and
     * repository.
     * 
     * @param category null indicate all categories (no filter)
     * @param hypervisor (*) null indicate no filter compatibles, else return images compatibles or
     *            with compatible conversions. @see {@link VirtualImageConversionDAO}
     */
    @Transactional(readOnly = true)
    public List<VirtualImage> getVirtualImages(final Integer enterpriseId,
        final Integer datacenterId, final String categoryName, final String hypervisorName)
    {
        checkEnterpriseCanUseDatacenter(enterpriseId, datacenterId);

        Enterprise enterprise = enterpriseService.getEnterprise(enterpriseId);
        Datacenter datacenter = infrastructureService.getDatacenter(datacenterId);
        Repository repository = infrastructureService.getRepository(datacenter);

        Category category = null;
        HypervisorType hypervisor = null;
        if (categoryName != null)
        {
            category = appsLibraryRep.findCategoryByName(categoryName);
        }
        if (hypervisorName != null)
        {
            try
            {
                hypervisor = HypervisorType.fromValue(hypervisorName);
            }
            catch (Exception ex)
            {
                // Validate the hypervisor type
                addValidationErrors(APIError.INVALID_HYPERVISOR_TYPE);
                flushErrors();
            }
        }

        return appsLibraryRep.findVirtualImages(enterprise, repository, category, hypervisor);
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public VirtualImage updateVirtualImage(final Integer enterpriseId, final Integer datacenterId,
        final Integer virtualImageId, final VirtualImageDto virtualImage)
    {
        VirtualImage old = getVirtualImage(enterpriseId, datacenterId, virtualImageId);

        old.setCostCode(virtualImage.getCostCode());
        old.setCpuRequired(virtualImage.getCpuRequired());
        old.setDescription(virtualImage.getDescription());
        old.setDiskFileSize(virtualImage.getDiskFileSize());

        DiskFormatType type = DiskFormatType.fromURI(virtualImage.getDiskFormatType());

        old.setDiskFormatType(type);
        old.setHdRequiredInBytes(virtualImage.getHdRequired());
        old.setName(virtualImage.getName());
        old.setPath(virtualImage.getPath());
        old.setRamRequired(virtualImage.getRamRequired());
        old.setShared(virtualImage.isShared());
        old.setChefEnabled(virtualImage.isChefEnabled());

        // retrieve the links
        RESTLink categoryLink = virtualImage.searchLink(CategoryResource.CATEGORY);
        RESTLink enterpriseLink = virtualImage.searchLink(EnterpriseResource.ENTERPRISE);
        RESTLink datacenterRepositoryLink =
            virtualImage.searchLink(DatacenterRepositoryResource.DATACENTER_REPOSITORY);
        RESTLink iconLink = virtualImage.searchLink(IconResource.ICON);
        RESTLink masterLink = virtualImage.searchLink("master");

        // check the links
        if (enterpriseLink != null)
        {
            String buildPath =
                buildPath(EnterprisesResource.ENTERPRISES_PATH, EnterpriseResource.ENTERPRISE_PARAM);
            MultivaluedMap<String, String> map =
                URIResolver.resolveFromURI(buildPath, enterpriseLink.getHref());

            if (map == null || !map.containsKey(EnterpriseResource.ENTERPRISE))
            {
                addValidationErrors(APIError.INVALID_ENTERPRISE_LINK);
                flushErrors();
            }
            Integer enterpriseIdFromLink =
                Integer.parseInt(map.getFirst(EnterpriseResource.ENTERPRISE));
            if (!enterpriseIdFromLink.equals(enterpriseId))
            {
                addConflictErrors(APIError.VIMAGE_ENTERPRISE_CANNOT_BE_CHANGED);
                flushErrors();
            }
        }

        if (datacenterRepositoryLink != null)
        {
            String buildPath =
                buildPath(EnterprisesResource.ENTERPRISES_PATH,
                    EnterpriseResource.ENTERPRISE_PARAM,
                    DatacenterRepositoriesResource.DATACENTER_REPOSITORIES_PATH,
                    DatacenterRepositoryResource.DATACENTER_REPOSITORY_PARAM);
            MultivaluedMap<String, String> map =
                URIResolver.resolveFromURI(buildPath, datacenterRepositoryLink.getHref());

            if (map == null || !map.containsKey(DatacenterRepositoryResource.DATACENTER_REPOSITORY))
            {
                addValidationErrors(APIError.INVALID_DATACENTER_RESPOSITORY_LINK);
                flushErrors();
            }
            Integer datacenterRepositoryId =
                Integer.parseInt(map.getFirst(DatacenterRepositoryResource.DATACENTER_REPOSITORY));
            if (!datacenterRepositoryId.equals(old.getRepository().getDatacenter().getId()))
            {
                addConflictErrors(APIError.VIMAGE_DATACENTER_REPOSITORY_CANNOT_BE_CHANGED);
                flushErrors();
            }
        }

        if (categoryLink != null)
        {
            String buildPath =
                buildPath(CategoriesResource.CATEGORIES_PATH, CategoryResource.CATEGORY_PARAM);
            MultivaluedMap<String, String> map =
                URIResolver.resolveFromURI(buildPath, categoryLink.getHref());

            if (map == null || !map.containsKey(CategoryResource.CATEGORY))
            {
                addValidationErrors(APIError.INVALID_CATEGORY_LINK);
                flushErrors();
            }
            Integer categoryId = Integer.parseInt(map.getFirst(CategoryResource.CATEGORY));
            if (!categoryId.equals(old.getCategory().getId()))
            {
                Category category = appsLibraryRep.findCategoryById(categoryId);
                if (category == null)
                {
                    addConflictErrors(APIError.NON_EXISTENT_CATEGORY);
                    flushErrors();
                }
                old.setCategory(category);
            }
        }

        if (iconLink != null)
        {
            String buildPath = buildPath(IconsResource.ICONS_PATH, IconResource.ICON_PARAM);
            MultivaluedMap<String, String> map =
                URIResolver.resolveFromURI(buildPath, iconLink.getHref());

            if (map == null || !map.containsKey(IconResource.ICON))
            {
                addValidationErrors(APIError.INVALID_ICON_LINK);
                flushErrors();
            }
            Integer iconId = Integer.parseInt(map.getFirst(IconResource.ICON));
            if (old.getIcon() == null || !iconId.equals(old.getIcon().getId()))
            {
                Icon icon = appsLibraryRep.findIconById(iconId);
                if (icon == null)
                {
                    addConflictErrors(APIError.NON_EXISTENT_ICON);
                    flushErrors();
                }
                old.setIcon(icon);
            }
        }

        // the cases when the master was null or not null but the new master image is null
        // allowed
        if (masterLink == null)
        {
            if (old.getMaster() != null)
            {
                if (tracer != null)
                {
                    String messageTrace =
                        "Virtual Image '" + old.getName()
                            + "' has been converted to a master image '";
                    tracer.log(SeverityType.INFO, ComponentType.DATACENTER, EventType.VI_UPDATE,
                        messageTrace);
                }
            }
            old.setMaster(null);
        }

        // case when the new master isn't null and the old can be null or the same image or a new
        // image
        else
        {
            String buildPath =
                buildPath(EnterprisesResource.ENTERPRISES_PATH,
                    EnterpriseResource.ENTERPRISE_PARAM,
                    DatacenterRepositoriesResource.DATACENTER_REPOSITORIES_PATH,
                    DatacenterRepositoryResource.DATACENTER_REPOSITORY_PARAM,
                    VirtualImagesResource.VIRTUAL_IMAGES_PATH,
                    VirtualImageResource.VIRTUAL_IMAGE_PARAM);
            MultivaluedMap<String, String> map =
                URIResolver.resolveFromURI(buildPath, masterLink.getHref());

            if (map == null || !map.containsKey(VirtualImageResource.VIRTUAL_IMAGE))
            {
                addValidationErrors(APIError.INVALID_VIMAGE_LINK);
                flushErrors();
            }

            Integer masterId = Integer.parseInt(map.getFirst(VirtualImageResource.VIRTUAL_IMAGE));

            if (old.getMaster() == null || !masterId.equals(old.getMaster().getId()))
            {
                addConflictErrors(APIError.VIMAGE_MASTER_IMAGE_CANNOT_BE_CHANGED);
                flushErrors();
            }
            // if its the same no change is necessary

        }

        appsLibraryRep.updateVirtualImage(old);

        return old;

    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public void deleteVirtualImage(final Integer enterpriseId, final Integer datacenterId,
        final Integer virtualImageId)
    {
        VirtualImage vimageToDelete = getVirtualImage(enterpriseId, datacenterId, virtualImageId);

        Enterprise ent = enterpriseService.getEnterprise(enterpriseId);

        // all the checks to delete the virtual image

        // TODO check if any virtual appliance is using the image

        if (appsLibraryRep.isMaster(vimageToDelete))
        {
            addConflictErrors(APIError.VIMAGE_MASTER_IMAGE_CANNOT_BE_DELETED);
            flushErrors();
        }

        if (vimageToDelete.isStateful())
        {
            addConflictErrors(APIError.VIMAGE_STATEFUL_IMAGE_CANNOT_BE_DELETED);
            flushErrors();
        }

        if (vimageToDelete.isShared())
        {
            // assert if the enterprise is the enterprise of the virtual image
            if (!vimageToDelete.getEnterprise().getId().equals(ent.getId()))
            {
                addConflictErrors(APIError.VIMAGE_SHARED_IMAGE_FROM_OTHER_ENTERPRISE);
                flushErrors();
            }
        }
        // if the virtual image is shared only the users from same enterprise can delete
        // check if the user is for the same enterprise otherwise deny allegating permissions

        String viOvf = vimageToDelete.getOvfid();

        if (viOvf == null)
        {
            // this is a bundle of an imported virtual machine (it havent OVF)
            viOvf = codifyBundleImportedOVFid(vimageToDelete.getPath());
        }

        final ApplianceManagerResourceStubImpl amClient = getApplianceManagerClient(datacenterId);
        amClient.delete(enterpriseId.toString(), viOvf);

        // delete
        appsLibraryRep.deleteVirtualImage(vimageToDelete);

        if (tracer != null)
        {
            String messageTrace =
                "Virtual Image '" + vimageToDelete.getName() + "' has been deleted '";
            tracer.log(SeverityType.INFO, ComponentType.DATACENTER, EventType.VI_DELETE,
                messageTrace);
        }

    }

    private String codifyBundleImportedOVFid(final String vipath)
    {
        return String.format("http://bundle-imported/%s", vipath);
    }

    @Transactional(readOnly = true)
    public List<VirtualImage> findStatefulVirtualImagesByDatacenter(final Integer enterpriseId,
        final Integer datacenterId)
    {
        checkEnterpriseCanUseDatacenter(enterpriseId, datacenterId);

        Datacenter datacenter = infrastructureService.getDatacenter(datacenterId);
        return appsLibraryRep.findStatefulVirtualImagesByDatacenter(datacenter);
    }

    @Transactional(readOnly = true)
    public List<VirtualImage> findStatefulVirtualImagesByCategoryAndDatacenter(
        final Integer enterpriseId, final Integer datacenterId, final String categoryName)
    {
        checkEnterpriseCanUseDatacenter(enterpriseId, datacenterId);

        Datacenter datacenter = infrastructureService.getDatacenter(datacenterId);
        Category category = categoryService.getCategoryByName(categoryName);

        return appsLibraryRep
            .findStatefulVirtualImagesByCategoryAndDatacenter(category, datacenter);
    }

    /**
     * Checks the enterprise and datacenter exists and have a limits relation (datacenter allowed by
     * enterprise).
     */
    private void checkEnterpriseCanUseDatacenter(final Integer enterpriseId,
        final Integer datacenterId)
    {
        DatacenterLimits limits =
            enterpriseService.findLimitsByEnterpriseAndDatacenter(enterpriseId, datacenterId);
        if (limits == null)
        {
            addConflictErrors(APIError.ENTERPRISE_NOT_ALLOWED_DATACENTER);
            flushErrors();
        }
    }
}
