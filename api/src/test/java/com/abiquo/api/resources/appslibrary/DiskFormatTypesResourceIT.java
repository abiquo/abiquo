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

package com.abiquo.api.resources.appslibrary;

import static com.abiquo.api.common.UriTestResolver.resolveDiskFormatTypesURI;
import static org.testng.Assert.assertEquals;

import org.apache.wink.client.ClientResponse;
import org.testng.annotations.Test;

import com.abiquo.api.resources.AbstractJpaGeneratorIT;
import com.abiquo.model.enumerator.DiskFormatType;
import com.abiquo.server.core.appslibrary.DiskFormatTypesDto;

public class DiskFormatTypesResourceIT extends AbstractJpaGeneratorIT
{
    protected DiskFormatType diskFormatType;

    @Test
    public void getDiskFormatTypes() throws Exception
    {
        String diskFormatTypesURI = resolveDiskFormatTypesURI();
        ClientResponse response = get(diskFormatTypesURI);

        DiskFormatTypesDto dtos = response.getEntity(DiskFormatTypesDto.class);
        assertEquals(dtos.getCollection().size(), DiskFormatType.getIdMax() + 1);
    }

    /*
     * @Test public void postCategory() throws Exception { Category cat1 =
     * categoryGenerator.createUniqueInstance(); CategoryDto dto = createCategoryDto(cat1); String
     * href = resolveCategoriesURI(); // Assert response code is OK ClientResponse response =
     * post(href, dto); assertEquals(response.getStatusCode(), 201); // Ensure all the fields are
     * the same than before but with an id assigned dto = response.getEntity(CategoryDto.class);
     * assertNotNull(dto.getId()); assertEquals(dto.getName(), cat1.getName()); }
     * @Test public void postCategoryRaises409WhenDuplicatedName() { CategoryDto cat =
     * createCategoryDto(category); String href = resolveCategoriesURI(); ClientResponse response =
     * post(href, cat); assertErrors(response, 409, APIError.CATEGORY_DUPLICATED_NAME); } //
     * ********************* Helper methods ************************ private CategoryDto
     * createCategoryDto(final Category category) { CategoryDto dto = new CategoryDto();
     * dto.setId(category.getId()); dto.setName(category.getName());
     * dto.setErasable(category.isErasable()); dto.setDefaultCategory(category.isDefaultCategory());
     * return dto; }
     */

}
