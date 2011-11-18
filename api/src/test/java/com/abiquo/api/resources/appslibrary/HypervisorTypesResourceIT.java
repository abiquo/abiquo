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

import static com.abiquo.api.common.UriTestResolver.resolveHypervisorTypesURI;
import static org.testng.Assert.assertEquals;

import org.apache.wink.client.ClientResponse;
import org.testng.annotations.Test;

import com.abiquo.api.resources.AbstractJpaGeneratorIT;
import com.abiquo.model.enumerator.HypervisorType;
import com.abiquo.server.core.cloud.HypervisorTypesDto;

public class HypervisorTypesResourceIT extends AbstractJpaGeneratorIT
{
    @Test
    public void getHypervisorTypes() throws Exception
    {
        String hypervisorTypesURI = resolveHypervisorTypesURI();
        ClientResponse response = get(hypervisorTypesURI);

        HypervisorTypesDto dtos = response.getEntity(HypervisorTypesDto.class);
        assertEquals(dtos.getCollection().size(), HypervisorType.getIdMax());
    }

}
