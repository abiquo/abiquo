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

package com.abiquo.server.core.appslibrary;

import java.util.List;

import com.abiquo.server.core.common.DefaultEntityGenerator;
import com.softwarementors.commons.test.SeedGenerator;
import com.softwarementors.commons.testng.AssertEx;

public class IconGenerator extends DefaultEntityGenerator<Icon>
{

    public IconGenerator(final SeedGenerator seed)
    {
        super(seed);

    }

    @Override
    public void assertAllPropertiesEqual(final Icon obj1, final Icon obj2)
    {
        AssertEx.assertPropertiesEqualSilent(obj1, obj2, Icon.NAME_PROPERTY, Icon.PATH_PROPERTY);
    }

    @Override
    public Icon createUniqueInstance()
    {
        // FIXME: Write here how to create the pojo

        Icon icon = new Icon();
        icon.setName(newString(nextSeed(), 0, 20));
        icon.setPath(newString(nextSeed(), 0, 20));

        return icon;
    }

    @Override
    public void addAuxiliaryEntitiesToPersist(final Icon entity,
        final List<Object> entitiesToPersist)
    {
        super.addAuxiliaryEntitiesToPersist(entity, entitiesToPersist);

    }

}
