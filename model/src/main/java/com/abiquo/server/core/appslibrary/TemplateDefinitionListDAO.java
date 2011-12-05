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

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.abiquo.server.core.common.persistence.DefaultDAOBase;
import com.abiquo.server.core.enterprise.Enterprise;

@Repository("jpaTemplateDefinitionListDAO")
public class TemplateDefinitionListDAO extends DefaultDAOBase<Integer, TemplateDefinitionList>
{
    private final static String FIND_BY_ENTERPRISE =
        "SELECT temDeflist FROM TemplateDefinitionList temDeflist " //
            + "WHERE temDeflist.appsLibrary.enterprise.id = :enterpriseId ";

    private final static String FIND_BY_NAME_AND_ENTERPRISE =
        "SELECT temDeflist FROM TemplateDefinitionList temDeflist " //
            + "WHERE temDeflist.appsLibrary.enterprise.id = :enterpriseId and temDeflist.name = :nameEnt";

    public TemplateDefinitionListDAO()
    {
        super(TemplateDefinitionList.class);
    }

    public TemplateDefinitionListDAO(final EntityManager entityManager)
    {
        super(TemplateDefinitionList.class, entityManager);
    }

    @SuppressWarnings("unchecked")
    public List<TemplateDefinitionList> findByEnterprise(final Integer enterpriseId)
    {
        Query query = getSession().createQuery(FIND_BY_ENTERPRISE);
        query.setParameter("enterpriseId", enterpriseId);

        return query.list();
    }

    public TemplateDefinitionList findByNameAndEnterprise(final String nameEnt, final Enterprise ent)
    {
        Query query = getSession().createQuery(FIND_BY_NAME_AND_ENTERPRISE);
        query.setParameter("enterpriseId", ent.getId());
        query.setParameter("nameEnt", nameEnt);

        return (TemplateDefinitionList) query.uniqueResult();
    }

    @SuppressWarnings("unchecked")
    public List<TemplateDefinition> findByName(final String name)
    {
        Criteria criteria = createCriteria(sameName(name));
        criteria.addOrder(Order.asc(TemplateDefinitionList.NAME_PROPERTY));
        return criteria.list();
    }

    private static Criterion sameName(final String name)
    {
        return Restrictions.eq(TemplateDefinitionList.NAME_PROPERTY, name);
    }
}
