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

public class TemplateDefinitionListGenerator extends DefaultEntityGenerator<TemplateDefinitionList>
{

    AppsLibraryGenerator appsLibraryGenerator;

    public TemplateDefinitionListGenerator(final SeedGenerator seed)
    {
        super(seed);

        appsLibraryGenerator = new AppsLibraryGenerator(seed);

    }

    @Override
    public void assertAllPropertiesEqual(final TemplateDefinitionList obj1, final TemplateDefinitionList obj2)
    {
        AssertEx.assertPropertiesEqualSilent(obj1, obj2, TemplateDefinitionList.NAME_PROPERTY,
            TemplateDefinitionList.URL_PROPERTY);
    }

    @Override
    public TemplateDefinitionList createUniqueInstance()
    {

        TemplateDefinitionList templateDefinitionList =
            createInstance(newString(nextSeed(), 0, 30), newString(nextSeed(), 0, 30));

        AppsLibrary appsLibrary = appsLibraryGenerator.createUniqueInstance();
        templateDefinitionList.setAppsLibrary(appsLibrary);

        return templateDefinitionList;
    }

    public TemplateDefinitionList createInstance(final String name, final String url)
    {
        return new TemplateDefinitionList(name, url);
    }

    @Override
    public void addAuxiliaryEntitiesToPersist(final TemplateDefinitionList entity,
        final List<Object> entitiesToPersist)
    {
        super.addAuxiliaryEntitiesToPersist(entity, entitiesToPersist);

        AppsLibrary appsLibrary = entity.getAppsLibrary();
        appsLibraryGenerator.addAuxiliaryEntitiesToPersist(appsLibrary, entitiesToPersist);
        entitiesToPersist.add(appsLibrary);

    }

}
