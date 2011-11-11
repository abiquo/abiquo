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

import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.abiquo.model.enumerator.HypervisorType;
import com.abiquo.server.core.cloud.VirtualImageConversionDAO;
import com.abiquo.server.core.common.DefaultRepBase;
import com.abiquo.server.core.enterprise.Enterprise;
import com.abiquo.server.core.infrastructure.Datacenter;

@Repository
public class AppsLibraryRep extends DefaultRepBase
{
    @Autowired
    private VirtualImageDAO virtualImageDAO;

    @Autowired
    private CategoryDAO categoryDAO;

    @Autowired
    private IconDAO iconDAO;

    public AppsLibraryRep()
    {

    }

    public AppsLibraryRep(final EntityManager em)
    {
        this.entityManager = em;
        this.virtualImageDAO = new VirtualImageDAO(em);
        this.categoryDAO = new CategoryDAO(em);
        this.iconDAO = new IconDAO(em);
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

    public void deleteCategory(final Category category)
    {
        categoryDAO.remove(category);
    }

    public Category getDefaultCategory()
    {
        return categoryDAO.findDefault();
    }

    public List<Category> findAllCategories()
    {
        return categoryDAO.findAll();
    }

    public Category findCategoryById(final Integer idCategory)
    {
        return categoryDAO.findById(idCategory);
    }

    public Category findCategoryByName(final String name)
    {
        return categoryDAO.findByName(name);
    }

    public Category findByCategoryNameOrCreateNew(final String categoryName)
    {
        if (categoryName == null || categoryName.isEmpty())
        {
            return getDefaultCategory();
        }

        Category cat = findCategoryByName(categoryName);

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

    // Icon

    public void insertIcon(final Icon icon)
    {
        iconDAO.persist(icon);
    }

    public void updateIcon(final Icon icon)
    {
        iconDAO.flush();
    }

    public void deleteIcon(final Icon icon)
    {
        iconDAO.remove(icon);
    }

    public List<Icon> findAllIcons()
    {
        return iconDAO.findAll();
    }

    public Icon findIconById(final Integer icon)
    {
        return iconDAO.findById(icon);
    }

    public Icon findIconByPath(final String path)
    {
        return iconDAO.findByPath(path);
    }

    public Icon findByIconPathOrCreateNew(final String iconPath)
    {
        if (iconPath == null)
        {
            return null;
        }

        Icon icon = findIconByPath(iconPath);

        if (icon == null)
        {
            icon = new Icon("A name", iconPath); // TODO: name

            insertIcon(icon);
        }

        return icon;
    }

    public boolean isIconInUseByVirtualImages(final Icon icon)
    {
        return iconDAO.iconInUseByVirtualImages(icon);
    }

    // Virtual Image

    public void insertVirtualImage(final VirtualImage virtualImage)
    {
        virtualImageDAO.persist(virtualImage);
    }

    public void updateVirtualImage(final VirtualImage virtualImage)
    {
        virtualImageDAO.flush();
    }

    public void deleteVirtualImage(final VirtualImage virtualImage)
    {
        virtualImageDAO.remove(virtualImage);
    }

    public List<VirtualImage> findAllVirtualImages()
    {
        return virtualImageDAO.findAll();
    }

    public VirtualImage findVirtualImageById(final Integer virtualImage)
    {
        return virtualImageDAO.findById(virtualImage);
    }

    public VirtualImage findVirtualImageByName(final String name)
    {
        return virtualImageDAO.findByName(name);
    }

    public List<VirtualImage> findVirtualImagesByEnterprise(final Enterprise enterprise)
    {
        return virtualImageDAO.findByEnterprise(enterprise);
    }

    public List<VirtualImage> findVirtualImagesByEnterpriseAndRepository(
        final Enterprise enterprise,
        final com.abiquo.server.core.infrastructure.Repository repository)
    {
        return virtualImageDAO.findByEnterpriseAndRepository(enterprise, repository);
    }

    /**
     * Gets the list of compatible(*) virtual images available in the provided enterprise and
     * repository.
     * 
     * @param category null indicate all categories (no filter)
     * @param hypervisor (*) null indicate no filter compatibles, else return images compatibles or
     *            with compatible conversions. @see {@link VirtualImageConversionDAO}
     */
    public List<VirtualImage> findVirtualImages(final Enterprise enterprise,
        final com.abiquo.server.core.infrastructure.Repository repository, final Category category,
        final HypervisorType hypervisor)
    {
        if (category == null && hypervisor == null)
        {
            return findVirtualImagesByEnterpriseAndRepository(enterprise, repository);
        }

        return virtualImageDAO.findBy(enterprise, repository, category, hypervisor);
    }

    public boolean existImageWithSamePath(final Enterprise enterprise,
        final com.abiquo.server.core.infrastructure.Repository repository, final String path)
    {
        return virtualImageDAO.existWithSamePath(enterprise, repository, path);
    }

    public VirtualImage findVirtualImageByPath(final Enterprise enterprise,
        final com.abiquo.server.core.infrastructure.Repository repository, final String path)
    {
        return virtualImageDAO.findByPath(enterprise, repository, path);
    }

    public List<VirtualImage> findAllStatefulVirtualImages()
    {
        return virtualImageDAO.findStatefuls();
    }

    public List<VirtualImage> findStatefulVirtualImagesByDatacenter(final Datacenter datacenter)
    {
        return virtualImageDAO.findStatefulsByDatacenter(datacenter);
    }

    public List<VirtualImage> findStatefulVirtualImagesByCategoryAndDatacenter(
        final Category category, final Datacenter datacenter)
    {
        return virtualImageDAO.findStatefulsByCategoryAndDatacenter(category, datacenter);
    }

    public boolean isMaster(final VirtualImage vImage)
    {
        return virtualImageDAO.isMaster(vImage);
    }
}
