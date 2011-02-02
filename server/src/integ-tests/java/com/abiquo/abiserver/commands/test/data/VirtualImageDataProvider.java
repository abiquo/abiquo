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

package com.abiquo.abiserver.commands.test.data;

import com.abiquo.abiserver.business.hibernate.pojohb.virtualimage.IconHB;
import com.abiquo.abiserver.pojo.virtualimage.Category;
import com.abiquo.abiserver.pojo.virtualimage.Repository;
import com.abiquo.abiserver.pojo.virtualimage.VirtualImage;

public final class VirtualImageDataProvider extends DataProvider
{
    /**
     * Private class constructor
     */
    private VirtualImageDataProvider()
    {
        // Private Constructor
        super();
    }

    /**
     * This method creates a Physical data Center
     * 
     * @return
     */
    public static VirtualImage createVirtualImage()
    {
        final VirtualImage virtualImage = new VirtualImage();
        virtualImage.setCpuRequired(1);
        virtualImage.setHdRequired(1);
        virtualImage.setRamRequired(1);
        
        Repository repo = new Repository();
        repo.setId(1);
        repo.setName("Main Repository");
        repo.setURL("127.0.0.1:/opt/vm_repository/");
        virtualImage.setRepository(repo);
        virtualImage.setDeleted(false);
        
        IconHB icon = new IconHB();
        icon.setIdIcon(1);
        icon.setPath("http://bestwindowssoftware.org/icon/ubuntu_icon.png");
        icon.setName("ubuntu");
        
        virtualImage.setIcon(icon.toPojo());
        virtualImage.setDescription("Test");
        virtualImage.setIdEnterprise(null);
        virtualImage.setIdMaster(null);

        return virtualImage;
    }

    /**
     * This method creates a Rack (Without dataCenter)
     * 
     * @return
     */
    public static Category createCategory()
    {
        final Category category = new Category();
        category.setIsDefault(false);
        category.setName("TestCategory");
        category.setIsErasable(true);

        return category;
    }
}
