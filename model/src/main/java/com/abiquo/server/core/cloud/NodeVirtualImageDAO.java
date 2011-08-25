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
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.abiquo.server.core.common.persistence.DefaultDAOBase;
import com.abiquo.server.core.enterprise.Enterprise;
import com.softwarementors.bzngine.entities.PersistentEntity;

@Repository("jpaNodeVirtualImageDAO")
public class NodeVirtualImageDAO extends DefaultDAOBase<Integer, NodeVirtualImage>
{
    public NodeVirtualImageDAO()
    {
        super(NodeVirtualImage.class);
    }

    public NodeVirtualImageDAO(final EntityManager entityManager)
    {
        super(NodeVirtualImage.class, entityManager);
    }

    private Criteria sameVirtualMachine(final VirtualMachine vmachine)
    {
        Criteria crit = createNestedCriteria(NodeVirtualImage.VIRTUAL_MACHINE_PROPERTY);
        crit.add(Restrictions.eq(PersistentEntity.ID_PROPERTY, vmachine.getId()));
        return crit;
    }

    public VirtualAppliance findVirtualAppliance(final VirtualMachine vmachine)
    {
        NodeVirtualImage node = findByVirtualMachine(vmachine);

        return node != null ? node.getVirtualAppliance() : null;
    }

    public NodeVirtualImage findByVirtualMachine(final VirtualMachine vmachine)
    {
        Criteria criteria = sameVirtualMachine(vmachine);
        return (NodeVirtualImage) criteria.uniqueResult();
    }

    //
    private Criteria sameEnterprise(final Enterprise enterprise)
    {
        Criteria crit =
            createNestedCriteria(NodeVirtualImage.VIRTUAL_MACHINE_PROPERTY,
                VirtualMachine.ENTERPRISE_PROPERTY);
        crit.add(Restrictions.eq(PersistentEntity.ID_PROPERTY, enterprise.getId()));
        return crit;
    }

    // select nvi.* from nodevirtualimage nvi where nvi.idVM in
    // (Select vm.idVM from virtualmachine vm where vm.idEnterprise=entID);
    public List<NodeVirtualImage> findByEnterprise(final Enterprise enterprise)
    {
        Criteria criteria = sameEnterprise(enterprise);
        List<NodeVirtualImage> result = getResultList(criteria);
        return result;

    }

    public List<NodeVirtualImage> findByVirtualImage(final VirtualImage virtualImage)
    {
        Criteria criteria = sameVirtualImage(virtualImage);
        return getResultList(criteria);
    }

    private Criteria sameVirtualImage(final VirtualImage virtualImage)
    {
        Criteria crit = createNestedCriteria(NodeVirtualImage.VIRTUAL_IMAGE_PROPERTY);
        crit.add(Restrictions.eq(PersistentEntity.ID_PROPERTY, virtualImage.getId()));
        return crit;
    }
}
