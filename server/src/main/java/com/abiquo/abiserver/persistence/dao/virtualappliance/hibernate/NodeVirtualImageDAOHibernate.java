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

package com.abiquo.abiserver.persistence.dao.virtualappliance.hibernate;

import java.util.Collection;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.functors.InvokerTransformer;

import com.abiquo.abiserver.business.hibernate.pojohb.virtualappliance.NodeVirtualImageHB;
import com.abiquo.abiserver.persistence.dao.virtualappliance.NodeVirtualImageDAO;
import com.abiquo.abiserver.persistence.hibernate.HibernateDAO;
import com.abiquo.abiserver.pojo.virtualappliance.NodeVirtualImage;

/**
 * Class that implements the extra DAO functions for the
 * {@link com.abiquo.abiserver.persistence.dao.virtualappliance.NodeVirtualImageDAO} interface
 * 
 * @author jdevesa@abiquo.com
 */
public class NodeVirtualImageDAOHibernate extends HibernateDAO<NodeVirtualImageHB, Integer>
    implements NodeVirtualImageDAO
{
    /**
     * Query used to find by multiple IDs.
     */
    private static final String FIND_BY_IDS =
        "from com.abiquo.abiserver.business.hibernate.pojohb.virtualappliance.NodeVirtualImageHB where idNode in (:ids)";

    // implement extra functionality

    @SuppressWarnings("unchecked")
    public Collection<NodeVirtualImage> getNodesDecorated(Collection<Integer> nodeIds)
    {
        Collection<NodeVirtualImageHB> nodes =
            getSession().createQuery(FIND_BY_IDS).setParameterList("ids", nodeIds).list();

        return CollectionUtils.collect(nodes, InvokerTransformer.getInstance("toDecorator"));
    }

    public void refresh(NodeVirtualImageHB node)
    {
        getSession().refresh(node);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Collection<NodeVirtualImageHB> getNodes(Collection<Integer> nodeIds)
    {
        return getSession().createQuery(FIND_BY_IDS).setParameterList("ids", nodeIds).list();
    }
}
