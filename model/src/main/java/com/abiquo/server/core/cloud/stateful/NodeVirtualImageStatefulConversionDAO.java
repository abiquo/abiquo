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

package com.abiquo.server.core.cloud.stateful;

import java.util.Collection;

import javax.persistence.EntityManager;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.abiquo.server.core.appslibrary.VirtualImageConversion;
import com.abiquo.server.core.cloud.VirtualAppliance;
import com.abiquo.server.core.common.persistence.DefaultDAOBase;

@Repository("jpaNodeVirtualImageStatefulConversionDAO")
public class NodeVirtualImageStatefulConversionDAO extends
    DefaultDAOBase<Integer, NodeVirtualImageStatefulConversion>
{
    public NodeVirtualImageStatefulConversionDAO()
    {
        super(NodeVirtualImageStatefulConversion.class);
    }

    public NodeVirtualImageStatefulConversionDAO(final EntityManager entityManager)
    {
        super(NodeVirtualImageStatefulConversion.class, entityManager);
    }

    public static Criterion sameVIConversion(final VirtualImageConversion vic)
    {
        Disjunction filterDisjunction = Restrictions.disjunction();

        filterDisjunction.add(Restrictions.eq(
            NodeVirtualImageStatefulConversion.VIRTUAL_IMAGE_CONVERSION_PROPERTY, vic));

        return filterDisjunction;
    }

    public static Criterion sameVASConversion(final VirtualApplianceStatefulConversion vasc)
    {
        Disjunction filterDisjunction = Restrictions.disjunction();

        filterDisjunction.add(Restrictions
            .eq(NodeVirtualImageStatefulConversion.VIRTUAL_APPLIANCE_STATEFUL_CONVERSION_PROPERTY,
                vasc));

        return filterDisjunction;
    }

    public Collection<NodeVirtualImageStatefulConversion> findByVirtualImageConversion(
        final VirtualImageConversion virtualImageConversion)
    {
        Criteria criteria = createCriteria();
        criteria.add(sameVIConversion(virtualImageConversion));
        return getResultList(criteria);
    }

    public Collection<NodeVirtualImageStatefulConversion> findByVirtualApplianceStatefulConversion(
        final VirtualApplianceStatefulConversion virtualApplianceStatefulConversion)
    {
        Criteria criteria = createCriteria();
        criteria.add(sameVASConversion(virtualApplianceStatefulConversion));
        return getResultList(criteria);
    }

    private final String QUERY_BY_VIRTUAL_APPLIANCE = "SELECT nvisc "
        + "FROM com.abiquo.server.core.cloud.stateful.NodeVirtualImageStatefulConversion nvisc "
        + ",com.abiquo.server.core.cloud.stateful.VirtualApplianceStatefulConversion vasc "
        + "WHERE nvisc.virtualApplianceStatefulConversion.id = vasc.id "
        + "AND vasc.virtualAppliance.id = :idVirtualAppliance";

    public Collection<NodeVirtualImageStatefulConversion> findByVirtualAppliance(
        final VirtualAppliance virtualAppliance)
    {
        Query query = getSession().createQuery(QUERY_BY_VIRTUAL_APPLIANCE);
        query.setParameter("idVirtualAppliance", virtualAppliance.getId());

        return query.list();
    }

}
