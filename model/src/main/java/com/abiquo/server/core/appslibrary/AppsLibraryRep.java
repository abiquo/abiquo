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
package com.abiquo.server.core.appslibrary;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.abiquo.model.enumerator.DiskFormatType;
import com.abiquo.model.enumerator.HypervisorType;
import com.abiquo.model.enumerator.StatefulInclusion;
import com.abiquo.server.core.cloud.VirtualDatacenter;
import com.abiquo.server.core.common.DefaultRepBase;
import com.abiquo.server.core.enterprise.Enterprise;
import com.abiquo.server.core.infrastructure.Datacenter;

@Repository
public class AppsLibraryRep extends DefaultRepBase
{
    @Autowired
    private VirtualMachineTemplateDAO virtualMachineTemplateDAO;

    @Autowired
    private VirtualImageConversionDAO conversionDAO;

    @Autowired
    private CategoryDAO categoryDAO;

    public AppsLibraryRep()
    {

    }

    public AppsLibraryRep(final EntityManager em)
    {
        this.entityManager = em;
        this.virtualMachineTemplateDAO = new VirtualMachineTemplateDAO(em);
        this.categoryDAO = new CategoryDAO(em);
        this.conversionDAO = new VirtualImageConversionDAO(em);
    }

    // Category

    public void insertCategory(final Category category)
    {
        categoryDAO.persist(category);
    }

    public void updateCategory(final Category category)
    {
        categoryDAO.flush();
    }

    /**
     * If if being used by any {@link VirtualMachineTemplate} its changed to the DEFAULT category;
     */
    public void deleteCategory(final Category category)
    {
        for (VirtualMachineTemplate templ : virtualMachineTemplateDAO.findBy(category))
        {
            templ.setCategory(categoryDAO.findDefault());
        }

        categoryDAO.remove(category);
    }

    public Category getDefaultCategory()
    {
        return categoryDAO.findDefault();
    }

    public List<Category> findAllCategories(final Integer idEnterprise, final boolean onlyLocal)
    {
        List<Category> result = new ArrayList<Category>();
        if (idEnterprise != 0)
        {
            if (onlyLocal)
            {
                return categoryDAO.findLocalCategories(idEnterprise);
            }
            result.addAll(categoryDAO.findLocalCategories(idEnterprise));
        }

        result.addAll(categoryDAO.findGlobalCategories());

        return result;
    }

    public Category findCategoryById(final Integer idCategory)
    {
        return categoryDAO.findById(idCategory);
    }

    public Category findCategoryByName(final String name, final Enterprise enterprise)
    {
        return categoryDAO.findByNameAndEnterprise(name, enterprise);
    }

    public Category findByCategoryNameOrCreateNew(final String categoryName)
    {
        if (categoryName == null || categoryName.isEmpty())
        {
            return getDefaultCategory();
        }

        Category cat = findCategoryByName(categoryName, null);

        if (cat == null)
        {
            cat = new Category(categoryName);
            insertCategory(cat);
        }

        return cat;
    }

    public boolean existAnyWithName(final String name)
    {
        return categoryDAO.existsAnyWithName(name);
    }

    public boolean existAnyOtherWithName(final Category category, final String name)
    {
        return categoryDAO.existsAnyOtherWithName(category, name);
    }

    // Virtual Machine Template

    public void insertVirtualMachineTemplate(final VirtualMachineTemplate virtualMachineTemplate)
    {
        virtualMachineTemplateDAO.persist(virtualMachineTemplate);
    }

    public void updateVirtualMachineTemplate(final VirtualMachineTemplate virtualMachineTemplate)
    {
        virtualMachineTemplateDAO.flush();
    }

    public void deleteVirtualMachineTemplate(final VirtualMachineTemplate virtualMachineTemplate)
    {
        virtualMachineTemplateDAO.remove(virtualMachineTemplate);
    }

    public List<VirtualMachineTemplate> findAllVirtualMachineTemplates()
    {
        return virtualMachineTemplateDAO.findAll();
    }

    public VirtualMachineTemplate findVirtualMachineTemplateById(
        final Integer virtualMachineTemplate)
    {
        return virtualMachineTemplateDAO.findById(virtualMachineTemplate);
    }

    public VirtualMachineTemplate findVirtualMachineTemplateByName(final String name)
    {
        return virtualMachineTemplateDAO.findByName(name);
    }

    public List<VirtualMachineTemplate> findVirtualMachineTemplatesByEnterprise(
        final Enterprise enterprise)
    {
        return virtualMachineTemplateDAO.findByEnterprise(enterprise);
    }

    public List<VirtualMachineTemplate> findVirtualMachineTemplatesByEnterpriseAndRepository(
        final Enterprise enterprise,
        final com.abiquo.server.core.infrastructure.Repository repository)
    {
        return virtualMachineTemplateDAO.findByEnterpriseAndRepository(enterprise, repository);
    }

    public List<VirtualMachineTemplate> findImportedVirtualMachineTemplatesByEnterprise(
        final Enterprise enterprise)
    {
        return virtualMachineTemplateDAO.findImportedByEnterprise(enterprise);
    }

    /**
     * Gets the list of compatible(*) virtual machine templates available in the provided enterprise
     * and repository.
     * 
     * @param category null indicate all categories (no filter)
     * @param hypervisor (*) null indicate no filter compatibles, else return templates compatibles
     *            or with compatible conversions. @see {@link VirtualImageConversionDAO}
     */
    public List<VirtualMachineTemplate> findVirtualMachineTemplates(final Enterprise enterprise,
        final com.abiquo.server.core.infrastructure.Repository repository, final Category category,
        final HypervisorType hypervisor)
    {
        if (category == null && hypervisor == null)
        {
            return findVirtualMachineTemplatesByEnterpriseAndRepository(enterprise, repository);
        }

        return virtualMachineTemplateDAO.findBy(enterprise, repository, category, hypervisor);
    }

    public List<VirtualMachineTemplate> findImportedVirtualMachineTemplates(
        final Enterprise enterprise, final Integer datacenterId, final Category category,
        final HypervisorType hypervisor)
    {
        if (category == null && hypervisor == null)
        {
            return virtualMachineTemplateDAO.findImportedByEnterprise(enterprise);
        }

        return virtualMachineTemplateDAO.findImportedBy(enterprise, category, hypervisor);
    }

    public boolean existVirtualMachineTemplateWithSamePath(final Enterprise enterprise,
        final com.abiquo.server.core.infrastructure.Repository repository, final String path)
    {
        return virtualMachineTemplateDAO.existWithSamePath(enterprise, repository, path);
    }

    public VirtualMachineTemplate findVirtualMachineTemplateByPath(final Enterprise enterprise,
        final com.abiquo.server.core.infrastructure.Repository repository, final String path)
    {
        return virtualMachineTemplateDAO.findByPath(enterprise, repository, path);
    }

    public List<VirtualMachineTemplate> findAllStatefulVirtualMachineTemplates()
    {
        return virtualMachineTemplateDAO.findStatefuls();
    }

    public List<VirtualMachineTemplate> findStatefulVirtualMachineTemplatesByDatacenter(
        final Datacenter datacenter, final StatefulInclusion stateful)
    {
        return virtualMachineTemplateDAO.findStatefulsByDatacenter(datacenter, stateful);
    }

    public List<VirtualMachineTemplate> findStatefulVirtualMachineTemplatesByDatacenterAndVirtualDatacenter(
        final Datacenter datacenter, final VirtualDatacenter virtualDatacenter,
        final StatefulInclusion stateful)
    {
        return virtualMachineTemplateDAO.findStatefulsByDatacenter(datacenter, virtualDatacenter,
            stateful);
    }

    public List<VirtualMachineTemplate> findStatefulVirtualMachineTemplatesByCategoryAndDatacenter(
        final Category category, final Datacenter datacenter, final StatefulInclusion stateful)
    {
        return virtualMachineTemplateDAO.findStatefulsByCategoryAndDatacenter(category, datacenter,
            stateful);
    }

    public List<VirtualMachineTemplate> findStatefulVirtualMachineTemplatesByCategoryAndDatacenterandVirutalDatacenter(
        final Category category, final Datacenter datacenter,
        final VirtualDatacenter virtualdatacenter, final StatefulInclusion stateful)
    {
        return virtualMachineTemplateDAO.findStatefulsByCategoryAndDatacenter(category, datacenter,
            virtualdatacenter, stateful);
    }

    public boolean isMaster(final VirtualMachineTemplate vmtemplae)
    {
        return virtualMachineTemplateDAO.isMaster(vmtemplae);
    }

    public List<VirtualMachineTemplate> findVirtualMachineTemplatesByMaster(
        final VirtualMachineTemplate master)
    {
        return virtualMachineTemplateDAO.findByMaster(master);
    }

    /**
     * @see com.abiquo.server.core.appslibrary.VirtualImageConversionDAO#compatilbeConversions(com.abiquo.server.core.cloud.VirtualMachineTemplate,
     *      com.abiquo.model.enumerator.HypervisorType)
     */
    public List<VirtualImageConversion> compatilbeConversions(
        final VirtualMachineTemplate vmtemplate, final HypervisorType hypervisorType)
    {
        return conversionDAO.compatilbeConversions(vmtemplate, hypervisorType);
    }

    public boolean isVirtualMachineTemplateConverted(final VirtualMachineTemplate vmtemplate,
        final DiskFormatType targetType)
    {
        return conversionDAO.isConverted(vmtemplate, targetType);
    }

    public void addConversion(final VirtualImageConversion conversion)
    {
        conversionDAO.persist(conversion);
    }

    public boolean existDuplicatedConversion(final VirtualImageConversion conversion)
    {
        return conversionDAO.existDuplicatedConversion(conversion);
    }

    public List<String> findIconsByEnterprise(final Integer enterpriseId)
    {
        return virtualMachineTemplateDAO.findIconsByEnterprise(enterpriseId);
    }
}
