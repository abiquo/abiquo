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

package com.abiquo.api.transformer;

import static com.abiquo.testng.TestConfig.ALL_UNIT_TESTS;
import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

import com.abiquo.model.util.ModelTransformer;
import com.abiquo.server.core.infrastructure.Datacenter;
import com.abiquo.server.core.infrastructure.DatacenterDto;

@Test(groups = ALL_UNIT_TESTS)
public class ModelTransformerTest
{

    @Test
    public void transformPersistence() throws Exception
    {
        Datacenter obj = new Datacenter("test", "situation test");

        DatacenterDto dto = ModelTransformer.transportFromPersistence(DatacenterDto.class, obj);

        assertEquals(obj.getId(), dto.getId());
        assertEquals(obj.getName(), dto.getName());
        assertEquals(obj.getLocation(), dto.getLocation());
    }

}
