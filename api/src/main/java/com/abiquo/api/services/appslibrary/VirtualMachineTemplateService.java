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

import javax.persistence.EntityManager;
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
import com.abiquo.api.resources.appslibrary.VirtualMachineTemplateResource;
import com.abiquo.api.resources.appslibrary.VirtualMachineTemplatesResource;
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
import com.abiquo.server.core.appslibrary.VirtualImageConversionDAO;
import com.abiquo.server.core.appslibrary.VirtualMachineTemplate;
import com.abiquo.server.core.appslibrary.VirtualMachineTemplateDto;
import com.abiquo.server.core.enterprise.DatacenterLimits;
import com.abiquo.server.core.enterprise.Enterprise;
import com.abiquo.server.core.infrastructure.Datacenter;
import com.abiquo.server.core.infrastructure.Repository;
import com.abiquo.server.core.infrastructure.RepositoryDAO;
import com.abiquo.tracer.ComponentType;
import com.abiquo.tracer.EventType;
import com.abiquo.tracer.SeverityType;

@Service
public class VirtualMachineTemplateService extends DefaultApiServiceWithApplianceManagerClient
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

    public VirtualMachineTemplateService()
    {
    }

    public VirtualMachineTemplateService(final EntityManager em)
    {
        this.repositoryDao = new RepositoryDAO(em);
        this.infrastructureService = new InfrastructureService(em);
        this.enterpriseService = new EnterpriseService(em);
        this.appsLibraryRep = new AppsLibraryRep(em);
        this.categoryService = new CategoryService(em);
    }

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
    public VirtualMachineTemplate getVirtualMachineTemplate(final Integer enterpriseId,
        final Integer datacenterId, final Integer virtualMachineTemplateId)
    {
        // Check that the enterprise can use the datacenter (also checks enterprise and datacenter
        // exists)
        checkEnterpriseCanUseDatacenter(enterpriseId, datacenterId);

        VirtualMachineTemplate virtualMachineTemplate =
            appsLibraryRep.findVirtualMachineTemplateById(virtualMachineTemplateId);
        if (virtualMachineTemplate == null)
        {
            addNotFoundErrors(APIError.NON_EXISTENT_VIRTUAL_MACHINE_TEMPLATE);
            flushErrors();
        }

        return virtualMachineTemplate;
    }

    /**
     * Gets the list of compatible(*) virtual machine templates available in the provided enterprise
     * and repository.
     * 
     * @param category null indicate all categories (no filter)
     * @param hypervisor (*) null indicate no filter compatibles, else return machine templates
     *            compatibles or with compatible conversions. @see {@link VirtualImageConversionDAO}
     */
    @Transactional(readOnly = true)
    public List<VirtualMachineTemplate> getVirtualMachineTemplates(final Integer enterpriseId,
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

        return appsLibraryRep.findVirtualMachineTemplates(enterprise, repository, category,
            hypervisor);
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public VirtualMachineTemplate updateVirtualMachineTemplate(final Integer enterpriseId,
        final Integer datacenterId, final Integer virtualMachineTemplateId,
        final VirtualMachineTemplateDto virtualMachineTemplate)
    {
        VirtualMachineTemplate old =
            getVirtualMachineTemplate(enterpriseId, datacenterId, virtualMachineTemplateId);

        old.setCostCode(virtualMachineTemplate.getCostCode());
        old.setCpuRequired(virtualMachineTemplate.getCpuRequired());
        old.setDescription(virtualMachineTemplate.getDescription());
        old.setDiskFileSize(virtualMachineTemplate.getDiskFileSize());

        DiskFormatType type = DiskFormatType.fromValue(virtualMachineTemplate.getDiskFormatType());

        old.setDiskFormatType(type);
        old.setHdRequiredInBytes(virtualMachineTemplate.getHdRequired());
        old.setName(virtualMachineTemplate.getName());
        old.setPath(virtualMachineTemplate.getPath());
        old.setRamRequired(virtualMachineTemplate.getRamRequired());
        old.setShared(virtualMachineTemplate.isShared());
        old.setChefEnabled(virtualMachineTemplate.isChefEnabled());

        // retrieve the links
        RESTLink categoryLink = virtualMachineTemplate.searchLink(CategoryResource.CATEGORY);
        RESTLink enterpriseLink = virtualMachineTemplate.searchLink(EnterpriseResource.ENTERPRISE);
        RESTLink datacenterRepositoryLink =
            virtualMachineTemplate.searchLink(DatacenterRepositoryResource.DATACENTER_REPOSITORY);
        RESTLink iconLink = virtualMachineTemplate.searchLink(IconResource.ICON);
        RESTLink masterLink = virtualMachineTemplate.searchLink("master");

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
                addConflictErrors(APIError.VMTEMPLATE_ENTERPRISE_CANNOT_BE_CHANGED);
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
                addConflictErrors(APIError.VMTEMPLATE_DATACENTER_REPOSITORY_CANNOT_BE_CHANGED);
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

        // the cases when the master was null or not null but the new master template is null
        // allowed
        if (masterLink == null)
        {
            if (old.getMaster() != null)
            {
                if (tracer != null)
                {
                    String messageTrace =
                        "Virtual Machine Template '" + old.getName()
                            + "' has been converted to a master template '";
                    tracer.log(SeverityType.INFO, ComponentType.DATACENTER, EventType.VI_UPDATE,
                        messageTrace);
                }
            }
            old.setMaster(null);
        }

        // case when the new master isn't null and the old can be null or the same template or a new
        // template
        else
        {
            String buildPath =
                buildPath(EnterprisesResource.ENTERPRISES_PATH,
                    EnterpriseResource.ENTERPRISE_PARAM,
                    DatacenterRepositoriesResource.DATACENTER_REPOSITORIES_PATH,
                    DatacenterRepositoryResource.DATACENTER_REPOSITORY_PARAM,
                    VirtualMachineTemplatesResource.VIRTUAL_MACHINE_TEMPLATES_PATH,
                    VirtualMachineTemplateResource.VIRTUAL_MACHINE_TEMPLATE_PARAM);
            MultivaluedMap<String, String> map =
                URIResolver.resolveFromURI(buildPath, masterLink.getHref());

            if (map == null
                || !map.containsKey(VirtualMachineTemplateResource.VIRTUAL_MACHINE_TEMPLATE))
            {
                addValidationErrors(APIError.INVALID_VMTEMPLATE_LINK);
                flushErrors();
            }

            Integer masterId =
                Integer.parseInt(map
                    .getFirst(VirtualMachineTemplateResource.VIRTUAL_MACHINE_TEMPLATE));

            if (old.getMaster() == null || !masterId.equals(old.getMaster().getId()))
            {
                addConflictErrors(APIError.VMTEMPLATE_MASTER_TEMPLATE_CANNOT_BE_CHANGED);
                flushErrors();
            }
            // if its the same no change is necessary

        }

        appsLibraryRep.updateVirtualMachineTemplate(old);

        return old;

    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public void deleteVirtualMachineTemplate(final Integer enterpriseId,
        final Integer datacenterId, final Integer virtualMachineTemplateId)
    {
        VirtualMachineTemplate vmtemplateToDelete =
            getVirtualMachineTemplate(enterpriseId, datacenterId, virtualMachineTemplateId);

        Enterprise ent = enterpriseService.getEnterprise(enterpriseId);

        // all the checks to delete the virtual machine template

        // TODO check if any virtual appliance is using the template

        if (appsLibraryRep.isMaster(vmtemplateToDelete))
        {
            addConflictErrors(APIError.VMTEMPLATE_MASTER_TEMPLATE_CANNOT_BE_DELETED);
            flushErrors();
        }

        if (vmtemplateToDelete.isStateful())
        {
            addConflictErrors(APIError.VMTEMPLATE_STATEFUL_TEMPLATE_CANNOT_BE_DELETED);
            flushErrors();
        }

        if (vmtemplateToDelete.isShared())
        {
            // assert if the enterprise is the enterprise of the virtual machine template
            if (!vmtemplateToDelete.getEnterprise().getId().equals(ent.getId()))
            {
                addConflictErrors(APIError.VMTEMPLATE_SHARED_TEMPLATE_FROM_OTHER_ENTERPRISE);
                flushErrors();
            }
        }
        // if the virtual machine template is shared only the users from same enterprise can delete
        // check if the user is for the same enterprise otherwise deny allegating permissions

        String viOvf = vmtemplateToDelete.getOvfid();

        if (viOvf == null)
        {
            // this is a bundle of an imported virtual machine (it havent OVF)
            viOvf = codifyBundleImportedOVFid(vmtemplateToDelete.getPath());
        }

        final ApplianceManagerResourceStubImpl amClient = getApplianceManagerClient(datacenterId);
        amClient.delete(enterpriseId.toString(), viOvf);

        // delete
        appsLibraryRep.deleteVirtualMachineTemplate(vmtemplateToDelete);

        if (tracer != null)
        {
            String messageTrace =
                "Virtual Machine Template '" + vmtemplateToDelete.getName()
                    + "' has been deleted '";
            tracer.log(SeverityType.INFO, ComponentType.DATACENTER, EventType.VI_DELETE,
                messageTrace);
        }

    }

    private String codifyBundleImportedOVFid(final String vipath)
    {
        return String.format("http://bundle-imported/%s", vipath);
    }

    @Transactional(readOnly = true)
    public List<VirtualMachineTemplate> findStatefulVirtualMachineTemplatesByDatacenter(
        final Integer enterpriseId, final Integer datacenterId)
    {
        checkEnterpriseCanUseDatacenter(enterpriseId, datacenterId);

        Datacenter datacenter = infrastructureService.getDatacenter(datacenterId);
        return appsLibraryRep.findStatefulVirtualMachineTemplatesByDatacenter(datacenter);
    }

    @Transactional(readOnly = true)
    public List<VirtualMachineTemplate> findStatefulVirtualMachineTemplatesByCategoryAndDatacenter(
        final Integer enterpriseId, final Integer datacenterId, final String categoryName)
    {
        checkEnterpriseCanUseDatacenter(enterpriseId, datacenterId);

        Datacenter datacenter = infrastructureService.getDatacenter(datacenterId);
        Category category = categoryService.getCategoryByName(categoryName);

        return appsLibraryRep
            .findStatefulVirtualMachineTemplatesByCategoryAndDatacenter(category, datacenter);
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
