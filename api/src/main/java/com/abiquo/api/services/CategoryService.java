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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.abiquo.server.core.config.Category;
import com.abiquo.server.core.config.CategoryRep;

@Service
public class CategoryService extends DefaultApiService
{

    @Autowired
    CategoryRep repo;

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
}
