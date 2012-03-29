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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.ws.rs.core.MultivaluedMap;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import com.abiquo.api.resources.appslibrary.VirtualMachineTemplateResource;
import com.abiquo.api.resources.appslibrary.VirtualMachineTemplatesResource;
import com.abiquo.api.services.DefaultApiService;
import com.abiquo.api.services.EnterpriseService;
import com.abiquo.api.services.InfrastructureService;
import com.abiquo.api.services.UserService;
import com.abiquo.api.services.stub.AMServiceStub;
import com.abiquo.api.spring.security.SecurityService;
import com.abiquo.api.util.URIResolver;
import com.abiquo.model.enumerator.DiskFormatType;
import com.abiquo.model.enumerator.HypervisorType;
import com.abiquo.model.enumerator.Privileges;
import com.abiquo.model.enumerator.StatefulInclusion;
import com.abiquo.model.rest.RESTLink;
import com.abiquo.server.core.appslibrary.AppsLibraryRep;
import com.abiquo.server.core.appslibrary.Category;
import com.abiquo.server.core.appslibrary.VirtualImageConversionDAO;
import com.abiquo.server.core.appslibrary.VirtualMachineTemplate;
import com.abiquo.server.core.appslibrary.VirtualMachineTemplateDto;
import com.abiquo.server.core.cloud.VirtualDatacenter;
import com.abiquo.server.core.cloud.VirtualDatacenterRep;
import com.abiquo.server.core.cloud.VirtualMachineRep;
import com.abiquo.server.core.enterprise.DatacenterLimits;
import com.abiquo.server.core.enterprise.Enterprise;
import com.abiquo.server.core.infrastructure.Datacenter;
import com.abiquo.server.core.infrastructure.Repository;
import com.abiquo.server.core.infrastructure.RepositoryDAO;
import com.abiquo.tracer.ComponentType;
import com.abiquo.tracer.EventType;
import com.abiquo.tracer.SeverityType;

@Service
public class VirtualMachineTemplateService extends DefaultApiService
{

    final private static Logger logger =
        LoggerFactory.getLogger(VirtualMachineTemplateService.class);

    @Autowired
    private RepositoryDAO repositoryDao;

    @Autowired
    private InfrastructureService infrastructureService;

    @Autowired
    private EnterpriseService enterpriseService;

    @Autowired
    private AppsLibraryRep appsLibraryRep;

    @Autowired
    private VirtualMachineRep virtualMachineRep;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private VirtualDatacenterRep virtualDatacenterRep;

    @Autowired
    private SecurityService securityService;

    @Autowired
    private UserService userService;

    @Autowired
    private AMServiceStub am;

    public VirtualMachineTemplateService()
    {
    }

    public VirtualMachineTemplateService(final EntityManager em)
    {
        this.repositoryDao = new RepositoryDAO(em);
        this.infrastructureService = new InfrastructureService(em);
        this.enterpriseService = new EnterpriseService(em);
        this.appsLibraryRep = new AppsLibraryRep(em);
        this.virtualMachineRep = new VirtualMachineRep(em);
        this.categoryService = new CategoryService(em);
        this.virtualDatacenterRep = new VirtualDatacenterRep(em);
    }

    /**
     * Ignoring credentinals check
     */
    @Transactional(readOnly = true)
    public Repository getDatacenterRepositoryBySystem(final Integer dcId, final Integer enterpriseId)
    {
        Datacenter datacenter = infrastructureService.getDatacenter(dcId);
        Repository repo = repositoryDao.findByDatacenter(datacenter);

        if (repo == null)
        {
            addNotFoundErrors(APIError.VIMAGE_DATACENTER_REPOSITORY_NOT_FOUND);
            flushErrors();
        }

        return repo;
    }

    @Transactional(readOnly = true)
    public Repository getDatacenterRepository(final Integer dcId, final Integer enterpriseId)
    {
        checkEnterpriseCanUseDatacenter(enterpriseId, dcId);
        return getDatacenterRepositoryBySystem(dcId, enterpriseId);
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
            try
            {
                repos.add(getDatacenterRepository(dclimit.getDatacenter().getId(), enterpriseId));
            }
            catch (Exception ex)
            {
                tracer.log(SeverityType.WARNING, ComponentType.DATACENTER,
                    EventType.APPLIANCE_MANAGER_CONFIGURATION, "appliancemanager.error", dclimit
                        .getDatacenter().getName());
            }
        }

        return repos;
    }

    @Transactional(readOnly = true)
    public VirtualMachineTemplate getVirtualMachineTemplate(final Integer enterpriseId,
        final Integer datacenterId, final Integer virtualMachineTemplateId)
    {
        // When shared all enterprises can retrieve it (as long as the privileges are met)
        VirtualMachineTemplate virtualMachineTemplate =
            appsLibraryRep.findVirtualMachineTemplateById(virtualMachineTemplateId);

        // We can't disclose whether the virtual machine template exists to user without privilege
        if (virtualMachineTemplate != null && virtualMachineTemplate.isShared())
        {
            checkEnterpriseCanUseVMTShared(enterpriseId, datacenterId);
        }
        else
        {
            // Check that the enterprise can use the datacenter (also checks enterprise and
            // datacenter exists)
            checkEnterpriseCanUseDatacenter(enterpriseId, datacenterId);

            if (virtualMachineTemplate == null)
            {
                addNotFoundErrors(APIError.NON_EXISTENT_VIRTUAL_MACHINE_TEMPLATE);
                flushErrors();
            }
        }
        return virtualMachineTemplate;
    }

    /**
     * Gets the list of compatible(*) virtual machine templates available in the provided enterprise
     * and repository.
     * 
     * @param category null indicate all categories (no filter)
     * @param connection (*) null indicate no filter compatibles, else return machine templates
     *            compatibles or with compatible conversions. @see {@link VirtualImageConversionDAO}
     */
    @Transactional(readOnly = true)
    public List<VirtualMachineTemplate> getVirtualMachineTemplates(final Integer enterpriseId,
        final Integer datacenterId, final String categoryName, final String hypervisorName,
        final Boolean imported)
    {
        Enterprise enterprise = enterpriseService.getEnterprise(enterpriseId);
        Repository repository = getDatacenterRepository(datacenterId, enterpriseId);

        Category category = null;
        HypervisorType hypervisor = null;
        if (categoryName != null)
        {
            category = appsLibraryRep.findCategoryByName(categoryName, null);
            if (category == null)
            {
                category = appsLibraryRep.findCategoryByName(categoryName, enterprise);
            }
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

        List<VirtualMachineTemplate> templates =
            appsLibraryRep
                .findVirtualMachineTemplates(enterprise, repository, category, hypervisor);

        if (imported)
        {
            // adds the virtual machine templates from imported virtual machines. aka: they are not
            // in the repository.
            templates.addAll(appsLibraryRep.findImportedVirtualMachineTemplates(enterprise,
                datacenterId, category, hypervisor));
        }

        return templates;
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public VirtualMachineTemplate updateVirtualMachineTemplate(final Integer enterpriseId,
        final Integer datacenterId, final Integer virtualMachineTemplateId,
        final VirtualMachineTemplateDto virtualMachineTemplate)
    {
        VirtualMachineTemplate old =
            getVirtualMachineTemplate(enterpriseId, datacenterId, virtualMachineTemplateId);

        // If shared and with instances then those instance cannot access to the template anymore
        if (old.isShared() && !virtualMachineTemplate.isShared()
            && virtualMachineRep.existsVirtualMachineFromTemplate(virtualMachineTemplateId))
        {
            tracer.log(SeverityType.CRITICAL, ComponentType.VIRTUAL_APPLIANCE, EventType.VI_UPDATE,
                "vmtemplate.modified.notshared.instance", new Object[] {virtualMachineTemplateId,
                old.getName()});
            addConflictErrors(APIError.VMTEMPLATE_TEMPLATE_USED_BY_VIRTUAL_MACHINES_CANNOT_BE_UNSHARED);
            flushErrors();
        }

        if (!virtualMachineTemplate.getIconUrl().isEmpty()
            && virtualMachineTemplate.getIconUrl() != null
            && !validURI(virtualMachineTemplate.getIconUrl()))
        {
            addConflictErrors(APIError.VIMAGE_MALFORMED_ICON_URI);
            flushErrors();

        }

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
        old.setIconUrl(virtualMachineTemplate.getIconUrl());

        // retrieve the links
        RESTLink categoryLink = virtualMachineTemplate.searchLink(CategoryResource.CATEGORY);
        RESTLink enterpriseLink = virtualMachineTemplate.searchLink(EnterpriseResource.ENTERPRISE);
        RESTLink datacenterRepositoryLink =
            virtualMachineTemplate.searchLink(DatacenterRepositoryResource.DATACENTER_REPOSITORY);
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

        // the cases when the master was null or not null but the new master template is null
        // allowed
        if (masterLink == null)
        {
            if (old.getMaster() != null)
            {
                if (tracer != null)
                {
                    tracer.log(SeverityType.INFO, ComponentType.DATACENTER, EventType.VI_UPDATE,
                        "virtualMachineTemplate.convertedToMaster", old.getName());
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

        // check if any virtual appliance is using the template
        if (virtualMachineRep.hasVirtualMachineTemplate(virtualMachineTemplateId))
        {
            addConflictErrors(APIError.VMTEMPLATE_TEMPLATE_USED_BY_VIRTUAL_MACHINES_CANNOT_BE_DELETED);
            flushErrors();
        }

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
            // moreover check if the current user doesn't have the privelige to impersonate between
            // enterprises
            if (!vmtemplateToDelete.getEnterprise().getId().equals(ent.getId())
                && !securityService.hasPrivilege(Privileges.ENTERPRISE_ADMINISTER_ALL, userService
                    .getCurrentUser()))
            {
                addConflictErrors(APIError.VMTEMPLATE_SHARED_TEMPLATE_FROM_OTHER_ENTERPRISE);
                flushErrors();
            }
        }
        // if the virtual machine template is shared only the users from same enterprise can delete
        // check if the user is for the same enterprise otherwise deny allegating permissions

        String viOvf = vmtemplateToDelete.getOvfid();

        if (StringUtils.isEmpty(viOvf))
        {
            // this is a bundle of an imported virtual machine (it havent OVF)
            viOvf = codifyBundleImportedOVFid(vmtemplateToDelete.getPath());
        }

        am.delete(datacenterId, enterpriseId, viOvf);

        // delete
        appsLibraryRep.deleteVirtualMachineTemplate(vmtemplateToDelete);

        if (tracer != null)
        {
            tracer.log(SeverityType.INFO, ComponentType.DATACENTER, EventType.VI_DELETE,
                "virtualMachineTemplate.deleted", vmtemplateToDelete.getName());
        }

    }

    private String codifyBundleImportedOVFid(final String vipath)
    {
        return String.format("http://bundle-imported/%s", vipath);
    }

    @Transactional(readOnly = true)
    public List<VirtualMachineTemplate> findStatefulVirtualMachineTemplatesByDatacenter(
        final Integer enterpriseId, final Integer datacenterId, final StatefulInclusion stateful)
    {
        return findStatefulVirtualMachineTemplatesByDatacenter(enterpriseId, datacenterId, null,
            stateful);
    }

    @Transactional(readOnly = true)
    public List<String> findIconsByEnterprise(final Integer enterpriseId)
    {
        return appsLibraryRep.findIconsByEnterprise(enterpriseId);
    }

    @Transactional(readOnly = true)
    public List<VirtualMachineTemplate> findStatefulVirtualMachineTemplatesByDatacenter(
        final Integer enterpriseId, final Integer datacenterId, final Integer virtualdatacenterId,
        final StatefulInclusion stateful)
    {
        checkEnterpriseCanUseDatacenter(enterpriseId, datacenterId);

        Datacenter datacenter = infrastructureService.getDatacenter(datacenterId);

        if (virtualdatacenterId == null)
        {
            return appsLibraryRep.findStatefulVirtualMachineTemplatesByDatacenter(datacenter,
                stateful);
        }
        else
        {
            VirtualDatacenter virtualdatacenter =
                virtualDatacenterRep.findById(virtualdatacenterId);
            return appsLibraryRep
                .findStatefulVirtualMachineTemplatesByDatacenterAndVirtualDatacenter(datacenter,
                    virtualdatacenter, stateful);
        }
    }

    @Transactional(readOnly = true)
    public List<VirtualMachineTemplate> findStatefulVirtualMachineTemplatesByCategoryAndDatacenter(
        final Integer enterpriseId, final Integer datacenterId, final String categoryName,
        final StatefulInclusion stateful)
    {
        return findStatefulVirtualMachineTemplatesByCategoryAndDatacenter(enterpriseId,
            datacenterId, null, categoryName, stateful);
    }

    @Transactional(readOnly = true)
    public List<VirtualMachineTemplate> findStatefulVirtualMachineTemplatesByCategoryAndDatacenter(
        final Integer enterpriseId, final Integer datacenterId, final Integer virtualdatacenterId,
        final String categoryName, final StatefulInclusion stateful)
    {

        checkEnterpriseCanUseDatacenter(enterpriseId, datacenterId);

        Datacenter datacenter = infrastructureService.getDatacenter(datacenterId);
        Enterprise enterprise = userService.getCurrentUser().getEnterprise();
        Category category =
            categoryService.getCategoryByNameAndEnterprise(categoryName, enterprise);

        if (virtualdatacenterId == null)
        {
            return appsLibraryRep.findStatefulVirtualMachineTemplatesByCategoryAndDatacenter(
                category, datacenter, stateful);
        }
        else
        {
            VirtualDatacenter virtualdatacenter =
                virtualDatacenterRep.findById(virtualdatacenterId);
            return appsLibraryRep
                .findStatefulVirtualMachineTemplatesByCategoryAndDatacenterandVirutalDatacenter(
                    category, datacenter, virtualdatacenter, stateful);
        }
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public void insertVirtualMachineTemplate(final VirtualMachineTemplate template)
    {
        appsLibraryRep.insertVirtualMachineTemplate(template);
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

    /**
     * Checks the enterprise and datacenter exists and have a limits relation (datacenter allowed by
     * enterprise). Retrieve shared virtual machine templates
     */
    private void checkEnterpriseCanUseVMTShared(final Integer enterpriseId,
        final Integer datacenterId)
    {
        DatacenterLimits limits =
            enterpriseService.findLimitsByEnterpriseVMTShared(enterpriseId, datacenterId);
        if (limits == null)
        {
            addConflictErrors(APIError.ENTERPRISE_NOT_ALLOWED_DATACENTER);
            flushErrors();
        }
    }

    private boolean validURI(final String uri)
    {
        try
        {
            new URL(uri);
            return true;
        }
        catch (MalformedURLException e)
        {
            return false;
        }
    }
}
