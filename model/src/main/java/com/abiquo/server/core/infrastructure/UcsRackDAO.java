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

package com.abiquo.server.core.infrastructure;

import java.util.List;

import javax.persistence.EntityManager;

import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.abiquo.server.core.common.persistence.DefaultDAOBase;

@Repository("jpaUcsRackDAO")
public class UcsRackDAO extends DefaultDAOBase<Integer, UcsRack>
{
    public UcsRackDAO()
    {
        super(UcsRack.class);
    }

    public UcsRackDAO(final EntityManager entityManager)
    {
        super(UcsRack.class, entityManager);
    }

    /**
     * Return all {@links UcsRack} associated to a
     * 
     * @param datacenterId id.
     * @return List<UcsRack> with all {@links UcsRack} associated to the given {@link Datacenter}.
     */
    public List<UcsRack> findAllUcsRacksByDatacenter(final Datacenter datacenter)
    {
        return findAllUcsRacksByDatacenter(datacenter, null);
    }

    public List<UcsRack> findAllUcsRacksByDatacenter(final Datacenter datacenter,
        final String filter)
    {
        Criteria criteria = createCriteria(sameDatacenter(datacenter));

        if (filter != null && !filter.isEmpty())
        {
            criteria.add(filterBy(filter));
        }

        criteria.addOrder(Order.asc(Rack.NAME_PROPERTY));

        return criteria.list();
    }

    /**
     * Criterion with same {@link Datacenter}.
     * 
     * @param datacenterId {@link Datacenter}.
     * @return Criterion
     */
    private Criterion sameDatacenter(final Datacenter datacenterId)
    {
        return Restrictions.eq(Rack.DATACENTER_PROPERTY, datacenterId);
    }

    /**
     * The ip is unique in DB.
     * 
     * @param ip ip.
     * @return boolean
     */
    public boolean existAnyOtherWithIP(final String ip)
    {
        return existsAnyByCriterions(Restrictions.eq(UcsRack.IP_PROPERTY, ip));
    }

    public Criterion filterBy(final String filter)
    {
        Disjunction filterDisjunction = Restrictions.disjunction();

        filterDisjunction.add(Restrictions.like(UcsRack.NAME_PROPERTY, '%' + filter + '%'));

        return filterDisjunction;
    }

}
