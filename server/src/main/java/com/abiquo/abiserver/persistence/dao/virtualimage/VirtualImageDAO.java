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

package com.abiquo.abiserver.persistence.dao.virtualimage;

import java.util.Collection;
import java.util.List;

import com.abiquo.abiserver.business.hibernate.pojohb.virtualimage.VirtualimageHB;
import com.abiquo.abiserver.persistence.DAO;

/**
 * Specific interface to work with the
 * {@link com.abiquo.abiserver.business.hibernate.pojohb.virtualimage.VirtualimageHB} Exposes all
 * the methods that this entity will need to interact with the data source
 * 
 * @author jdevesa@abiquo.com
 */
public interface VirtualImageDAO extends DAO<VirtualimageHB, Integer>
{

    /**
     * The the VirtualImages for the specified paths.
     * 
     * @param paths The paths.
     * @return The Virtual Images.
     */
    public Collection<VirtualimageHB> getImagesFromPath(Collection<String> paths);

    /**
     * Gets the virtual images available for the specified user.
     * 
     * @param username The user used to retrieve the virtual image list.
     * @return The virtual image list.
     */
    public Collection<VirtualimageHB> getImagesByUser(String username);

    /**
     * Gets the virtual images available for the specified user.
     * 
     * @param username The user used to retrieve the virtual image list.
     * @param stateful The stateful type of the virtual images to get.
     * @return The virtual image list.
     */
    public Collection<VirtualimageHB> getImagesByUser(String username, int stateful);

    // TODO TBD
    Collection<VirtualimageHB> findByCategory(final Integer idCategory);

    // TODO TBD
    Collection<VirtualimageHB> findByIcon(final Integer idIcon);

    // TODO TBD
    Collection<VirtualimageHB> getImagesByUserAndRepositoryAndCategory(final String username,
        final Integer idRepository, final Integer idCategory);

    List<VirtualimageHB> getImagesByEnterpriseAndRepositoryAndCategory(final Integer idEnterprise,
        final Integer idRepository, final Integer idCategory);

    List<VirtualimageHB> getImagesByEnterpriseAndRepository(final Integer idEnterprise,
        final Integer idRepository);

    List<VirtualimageHB> getImagesByRepository(final Integer idRepository);
}
