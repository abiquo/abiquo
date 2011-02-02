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

package com.abiquo.abiserver.commands.test;

import static org.junit.Assert.*;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.abiquo.abiserver.commands.test.data.InfrastructureDataProvider;
import com.abiquo.abiserver.commands.test.data.LoginProvider;
import com.abiquo.abiserver.commands.test.data.VirtualImageDataProvider;
import com.abiquo.abiserver.pojo.infrastructure.DataCenter;
import com.abiquo.abiserver.pojo.result.BasicResult;
import com.abiquo.abiserver.pojo.result.DataResult;
import com.abiquo.abiserver.pojo.virtualimage.Category;
import com.abiquo.abiserver.pojo.virtualimage.VirtualImage;
import com.abiquo.abiserver.services.flex.InfrastructureService;
import com.abiquo.abiserver.services.flex.VirtualImageService;

/**
 * This basic Test allows to assure the basic Virtual Image
 * functionality:
 *  - Create a category
 *  - Create an image
 *  - Modify the image
 *  - Delete the image
 *  - And finally delete the category
 *  
 * @author xfernandez
 *
 */
public class VirtualImageCommandIT {
	
    /**
     * VirtualImage object used on the class.
     */
    private static VirtualImage virtualImage = null;
    
    /**
     * Category object used on the class.
     */
    private static Category category = null;
    
    /**
     * BasicResult object.
     */
    private BasicResult basicResult = new BasicResult();

    /**
     * Infrastructure services
     */
    private static VirtualImageService vImageService = null;
    
    /**
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass()
    {
        LoginProvider.doLogin();
        virtualImage = VirtualImageDataProvider.createVirtualImage();
        category = VirtualImageDataProvider.createCategory();

        vImageService = new VirtualImageService();
    }
    
    /**
     * @throws java.lang.Exception
     */
    @AfterClass
    public static void tearDownAfterClass()
    {
        LoginProvider.doLogout();
    }
    
	@Test
	public void testCreateVirtualImage() {
        final DataResult<VirtualImage> dataResult =
            (DataResult<VirtualImage>) vImageService.createVirtualImage(
                LoginProvider.getUser(), virtualImage);

        virtualImage = dataResult.getData();

        assertTrue("VirtualImage created correctly", dataResult.getSuccess());
	}

	@Test
	public void testCreateVirtualImageDownloadedFromRepositorySpace() {
		//TODO: Complete
	}

	@Test
	public void testEditVirtualImage() {
		virtualImage.setIdEnterprise(1);
		virtualImage.setIdMaster(virtualImage.getId());
        final DataResult<VirtualImage> dataResult =
            (DataResult<VirtualImage>) vImageService.editVirtualImage(
                LoginProvider.getUser(), virtualImage);
        
        virtualImage = dataResult.getData();

        assertTrue("VirtualImage modified correctly", dataResult.getSuccess());
	}

	@Test
	public void testDeleteVirtualImage() {
		virtualImage.setIdEnterprise(1);
		virtualImage.setIdMaster(virtualImage.getId());
        final DataResult<VirtualImage> dataResult =
            (DataResult<VirtualImage>) vImageService.deleteVirtualImage(
                LoginProvider.getUser(), virtualImage);

        assertTrue("VirtualImage deleted correctly", dataResult.getSuccess());
	}

	@Test
	public void testCreateCategory() {
        final DataResult<Category> dataResult =
            (DataResult<Category>) vImageService.createCategory(
                LoginProvider.getUser(), category);

        category = dataResult.getData();

        assertTrue("Category created correctly", dataResult.getSuccess());
	}

	@Test
	public void testDeleteCategory() {
        final DataResult<Category> dataResult =
            (DataResult<Category>) vImageService.deleteCategory(
                LoginProvider.getUser(), category);

        assertTrue("Category deleted correctly", dataResult.getSuccess());
	}

}
