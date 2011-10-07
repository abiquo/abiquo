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

package com.abiquo.server.core.config;

import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.abiquo.server.core.common.DefaultRepBase;

@Repository
@Transactional
public class CategoryRep extends DefaultRepBase
{

    @Autowired
    private CategoryDAO categoryDAO;

    public CategoryRep()
    {

    }

    public CategoryRep(final EntityManager entityManager)
    {
        assert entityManager != null;
        assert entityManager.isOpen();

        this.entityManager = entityManager;
        categoryDAO = new CategoryDAO(entityManager);
    }

    public List<Category> findAll()
    {
        return categoryDAO.findAll();
    }

    public Category findCategoryById(final Integer categoryId)
    {
        return categoryDAO.findById(categoryId);
    }

}
