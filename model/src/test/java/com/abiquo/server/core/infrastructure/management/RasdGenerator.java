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

package com.abiquo.server.core.infrastructure.management;

import java.util.List;

import com.abiquo.server.core.common.GenericEntityGenerator;
import com.softwarementors.commons.test.SeedGenerator;
import com.softwarementors.commons.testng.AssertEx;

public class RasdGenerator extends GenericEntityGenerator<Rasd>
{

    public RasdGenerator(SeedGenerator seed)
    {
        super(seed);

    }

    @Override
    public void assertAllPropertiesEqual(Rasd obj1, Rasd obj2)
    {
        AssertEx.assertPropertiesEqualSilent(obj1, obj2, Rasd.ADDRESS_ON_PARENT_PROPERTY,
            Rasd.ADDRESS_PROPERTY, Rasd.PARENT_PROPERTY, Rasd.VIRTUAL_QUANTITY_PROPERTY,
            Rasd.HOST_RESOURCE_PROPERTY, Rasd.GENERATION_PROPERTY,
            Rasd.CHANGEABLE_TYPE_PROPERTY, Rasd.AUTOMATIC_ALLOCATION_PROPERTY,
            Rasd.RESOURCE_SUB_TYPE_PROPERTY, Rasd.RESERVATION_PROPERTY,
            Rasd.POOL_ID_PROPERTY, Rasd.CONNECTION_PROPERTY,
            Rasd.CONFIGURATION_NAME_PROPERTY, Rasd.WEIGHT_PROPERTY,
            Rasd.OTHER_RESOURCE_TYPE_PROPERTY, Rasd.MAPPING_BEHAVIOUR_PROPERTY,
            Rasd.AUTOMATIC_DEALLOCATION_PROPERTY, Rasd.CAPTION_PROPERTY,
            Rasd.ALLOCATION_UNITS_PROPERTY, Rasd.ELEMENT_NAME_PROPERTY,
            Rasd.DESCRIPTION_PROPERTY, Rasd.CONSUMER_VISIBILITY_PROPERTY,
            Rasd.LIMIT_PROPERTY, Rasd.RESOURCE_TYPE_PROPERTY); // RasdRaw.INSTANCE_ID_PROPERTY,
    }

    @Override
    public Rasd createUniqueInstance()
    {
        String id = newString(nextSeed(), Rasd.ID_LENGTH_MIN, Rasd.ID_LENGTH_MAX);

        String elementName =
            newString(nextSeed(), Rasd.ELEMENT_NAME_LENGTH_MIN, Rasd.ELEMENT_NAME_LENGTH_MAX);

        int resourceType = newBigDecimal().intValue() % 64; // TODO maxvalue

        return new Rasd(id, elementName, resourceType);
    }

    @Override
    public void addAuxiliaryEntitiesToPersist(Rasd entity, List<Object> entitiesToPersist)
    {
        super.addAuxiliaryEntitiesToPersist(entity, entitiesToPersist);
    }

}
