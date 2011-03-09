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

package com.abiquo.abiserver.persistence.dao.virtualappliance;

import java.util.Collection;
import java.util.List;

import com.abiquo.abiserver.business.hibernate.pojohb.virtualappliance.NodeVirtualImageHB;
import com.abiquo.abiserver.persistence.DAO;
import com.abiquo.abiserver.pojo.virtualappliance.NodeVirtualImage;

/**
 * Specific interface to work with the
 * {@link com.abiquo.abiserver.business.hibernate.pojohb.virtualappliance.NodeVirtualImageHB}
 * Exposes all the methods that this entity will need to interact with the data source
 * 
 * @author jdevesa@abiquo.com
 */
public interface NodeVirtualImageDAO extends DAO<NodeVirtualImageHB, Integer>
{
    public Collection<NodeVirtualImage> getNodesDecorated(Collection<Integer> nodeIds);

    public Collection<NodeVirtualImageHB> getNodes(Collection<Integer> nodeIds);

    public void refresh(NodeVirtualImageHB node);

    public List<NodeVirtualImageHB> findByImage(final Integer idImage);
}
