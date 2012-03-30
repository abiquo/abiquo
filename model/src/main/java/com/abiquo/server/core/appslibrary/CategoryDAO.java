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

import org.hibernate.Query;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.abiquo.server.core.common.persistence.DefaultDAOBase;
import com.abiquo.server.core.enterprise.Enterprise;

@Repository("jpaCategoryDAO")
/* package */class CategoryDAO extends DefaultDAOBase<Integer, Category>
{

    public static final String BY_NAME_AND_ENTERPRISE =
        " SELECT c from Category c, Enterprise e where c.enterprise.id = :idEnterprise and c.name = :name";

    public static final String BY_NAME_AND_NO_ENTERPRISE =
        " select c from Category c  where c.enterprise is NULL and c.name=:name";

    public static final String BY_GLOBAL = " select c from Category c  where c.enterprise is NULL";

    public static final String BY_LOCAL =
        " select c from Category c  where c.enterprise.id = :idEnterprise";

    public CategoryDAO()
    {
        super(Category.class);
    }

    public CategoryDAO(final EntityManager entityManager)
    {
        super(Category.class, entityManager);
    }

    public Category findDefault()
    {
        return getSingleResult(createCriteria(isDefault()));
    }

    public ArrayList<Category> findGlobalCategories()
    {

        ArrayList<Category> result = new ArrayList<Category>();
        Query query = getSession().createQuery(BY_GLOBAL);
        result = (ArrayList<Category>) query.list();
        return result;
    }

    public ArrayList<Category> findLocalCategories(final Integer idEnterprise)
    {

        ArrayList<Category> result = new ArrayList<Category>();
        Query query = getSession().createQuery(BY_LOCAL);
        query.setParameter("idEnterprise", idEnterprise);
        result = (ArrayList<Category>) query.list();
        return result;

    }

    public Category findByNameAndEnterprise(final String categoryName, final Enterprise enterprise)
    {

        Query query;
        if (enterprise == null)
        {
            query = getSession().createQuery(BY_NAME_AND_NO_ENTERPRISE);
        }
        else
        {
            query = getSession().createQuery(BY_NAME_AND_ENTERPRISE);
            query.setParameter("idEnterprise", enterprise.getId());
        }
        query.setParameter("name", categoryName);
        List<Category> results = query.list();
        if (!results.isEmpty())
        {
            return results.get(0);
        }
        return null;
    }

    public boolean existsAnyWithName(final String name)
    {
        return existsAnyByCriterions(equalsName(name));
    }

    public boolean existsAnyOtherWithName(final Category category, final String name)
    {
        return existsAnyOtherByCriterions(category, equalsName(name));
    }

    private static Criterion equalsName(final String name)
    {
        return Restrictions.eq(Category.NAME_PROPERTY, name);
    }

    private static Criterion sameEnterprise(final Enterprise enterprise)
    {
        return Restrictions.eq(Category.ENTERPRISE_PROPERTY, enterprise);
    }

    private Criterion isDefault()
    {
        return Restrictions.eq(Category.DEFAULT_PROPERTY, true);
    }

}
