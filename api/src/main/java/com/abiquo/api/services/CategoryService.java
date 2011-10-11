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

package com.abiquo.api.services;

import java.util.Collection;

import javax.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.abiquo.api.exceptions.APIError;
import com.abiquo.api.resources.CategoryResource;
import com.abiquo.server.core.config.Category;
import com.abiquo.server.core.config.CategoryDto;
import com.abiquo.server.core.config.CategoryRep;
import com.abiquo.tracer.ComponentType;
import com.abiquo.tracer.EventType;
import com.abiquo.tracer.SeverityType;

@Service
public class CategoryService extends DefaultApiService
{

    @Autowired
    CategoryRep repo;

    private static final Logger logger = LoggerFactory.getLogger(CategoryResource.class);

    public CategoryService()
    {

    }

    public CategoryService(final EntityManager em)
    {
        repo = new CategoryRep(em);
    }

    public Collection<Category> getCategories()
    {
        return repo.findAll();
    }

    public Category getCategory(final Integer categoryId)
    {
        return repo.findCategoryById(categoryId);
    }

    public CategoryDto addCategory(final CategoryDto categoryDto)
    {
        return null;
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public Category addCategory(final Category category)
    {
        validate(category);

        // Check if there is a category with the same name
        if (repo.existAnyOtherCategoryWithName(category.getName()))
        {
            addConflictErrors(APIError.CATEGORY_DUPLICATED_NAME);
            flushErrors();
        }

        repo.insertCategory(category);

        return category;

    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public Category modifyCategory(final Category category, final Integer categoryId)
    {
        Category old = repo.findCategoryById(categoryId);

        old.setName(category.getName());
        old.setIsDefault(category.getIsDefault());
        old.setIsErasable(category.getIsErasable());

        validate(old);
        repo.updateCategory(old);

        logger.debug("Updating category :" + category.getName());

        tracer.log(SeverityType.INFO, ComponentType.WORKLOAD, EventType.CATEGORY_MODIFIED,
            "Category " + category.getName() + " updated ");

        return old;
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public void removeCategory(final Integer categoryId)
    {
        Category category = repo.findCategoryById(categoryId);
        if (category == null)
        {
            addNotFoundErrors(APIError.NON_EXISTENT_CATEGORY);
            flushErrors();
        }

        repo.removeCategory(category);

        logger.debug("Removing category :" + category.getName());

        tracer.log(SeverityType.INFO, ComponentType.WORKLOAD, EventType.CATEGORY_DELETED,
            "Category " + category.getName() + " removed ");
    }
}
