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

package com.abiquo.server.core.cloud;

import java.util.List;

import javax.persistence.EntityManager;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.abiquo.server.core.common.persistence.DefaultDAOBase;

@Repository("jpaChefCookbookDAO")
public class ChefCookbookDAO extends DefaultDAOBase<Integer, ChefCookbook>
{
    public ChefCookbookDAO()
    {
        super(ChefCookbook.class);
    }

    public ChefCookbookDAO(EntityManager entityManager)
    {
        super(ChefCookbook.class, entityManager);
    }

    private final String QUERY_COOKBOOKS_BY_VM = "SELECT cb.cookbook " + //
        "FROM com.abiquo.server.core.cloud.ChefCookbook cb" + //
        "WHERE cb.idVM.id = :idVirtualMachine ";

    /**
     * @param idVirtualMachine virtual machine ID
     * @return a list of String containing cookbooks
     */
    @SuppressWarnings("unchecked")
    public List<String> getAllCookBooksByVirtualMachine(Integer idVirtualMachine)
    {
        Query query = getSession().createQuery(QUERY_COOKBOOKS_BY_VM);
        query.setParameter("idVM", idVirtualMachine);

        return query.list();
    }

    private static Criterion sameVirtualMachine(VirtualMachine virtualmachine)
    {
        return Restrictions.eq(ChefCookbook.VIRTUALMACHINE_PROPERTY, virtualmachine);
    }

    public List<ChefCookbook> findByVirtualMachine(VirtualMachine virtualmachine)
    {
        Criteria criteria = createCriteria(sameVirtualMachine(virtualmachine));
        criteria.addOrder(Order.asc(ChefCookbook.VIRTUALMACHINE_PROPERTY));
        return getResultList(criteria);
    }
}
