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

import java.util.Collection;

import javax.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.abiquo.api.exceptions.APIError;
import com.abiquo.api.resources.appslibrary.CategoryResource;
import com.abiquo.api.services.DefaultApiService;
import com.abiquo.server.core.appslibrary.AppsLibraryRep;
import com.abiquo.server.core.appslibrary.Category;
import com.abiquo.tracer.ComponentType;
import com.abiquo.tracer.EventType;
import com.abiquo.tracer.SeverityType;

@Service
public class CategoryService extends DefaultApiService
{
    private static final Logger logger = LoggerFactory.getLogger(CategoryResource.class);

    @Autowired
    private AppsLibraryRep appslibraryRep;

    public CategoryService()
    {

    }

    public CategoryService(final EntityManager em)
    {
        appslibraryRep = new AppsLibraryRep(em);
    }

    @Transactional(readOnly = true)
    public Collection<Category> getCategories()
    {
        return appslibraryRep.findAllCategories();
    }

    @Transactional(readOnly = true)
    public Category getCategory(final Integer categoryId)
    {
        Category category = appslibraryRep.findCategoryById(categoryId);
        if (category == null)
        {
            addNotFoundErrors(APIError.NON_EXISTENT_CATEGORY);
            flushErrors();
        }
        return category;
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public Category addCategory(final Category category)
    {
        validate(category);

        // there is one default category already created by the system
        // so user cannot manage this property currently
        category.setDefaultCategory(false);

        // Check if there is a category with the same name
        if (appslibraryRep.existAnyOtherCategoryWithName(category.getName()))
        {
            addConflictErrors(APIError.CATEGORY_DUPLICATED_NAME);
            flushErrors();
        }

        appslibraryRep.insertCategory(category);

        return category;

    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public Category modifyCategory(final Category category, final Integer categoryId)
    {
        Category old = appslibraryRep.findCategoryById(categoryId);
        if (old == null)
        {
            addNotFoundErrors(APIError.NON_EXISTENT_CATEGORY);
            flushErrors();
        }

        old.setName(category.getName());
        old.setErasable(category.isErasable());

        validate(old);
        appslibraryRep.updateCategory(old);

        logger.debug("Updating category :" + category.getName());

        tracer.log(SeverityType.INFO, ComponentType.WORKLOAD, EventType.CATEGORY_MODIFIED,
            "Category " + category.getName() + " updated ");

        return old;
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public void removeCategory(final Integer categoryId)
    {
        Category category = appslibraryRep.findCategoryById(categoryId);
        if (category == null)
        {
            addNotFoundErrors(APIError.NON_EXISTENT_CATEGORY);
            flushErrors();
        }

        if (!category.isErasable())
        {
            addConflictErrors(APIError.CATEGORY_NOT_ERASABLE);
            flushErrors();
        }

        appslibraryRep.deleteCategory(category);

        logger.debug("Removing category :" + category.getName());

        tracer.log(SeverityType.INFO, ComponentType.WORKLOAD, EventType.CATEGORY_DELETED,
            "Category " + category.getName() + " removed ");
    }
}
