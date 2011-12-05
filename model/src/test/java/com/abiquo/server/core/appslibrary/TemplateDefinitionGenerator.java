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

import com.abiquo.model.enumerator.DiskFormatType;
import com.abiquo.server.core.common.DefaultEntityGenerator;
import com.softwarementors.commons.test.SeedGenerator;
import com.softwarementors.commons.testng.AssertEx;

public class TemplateDefinitionGenerator extends DefaultEntityGenerator<TemplateDefinition>
{

    AppsLibraryGenerator appsLibraryGenerator;

    CategoryGenerator categoryGenerator;

    IconGenerator iconGenerator;

    public TemplateDefinitionGenerator(final SeedGenerator seed)
    {
        super(seed);

        appsLibraryGenerator = new AppsLibraryGenerator(seed);

        categoryGenerator = new CategoryGenerator(seed);

        iconGenerator = new IconGenerator(seed);

    }

    @Override
    public void assertAllPropertiesEqual(final TemplateDefinition obj1, final TemplateDefinition obj2)
    {
        AssertEx.assertPropertiesEqualSilent(obj1, obj2, TemplateDefinition.PRODUCT_VERSION_PROPERTY,
            TemplateDefinition.NAME_PROPERTY, TemplateDefinition.PRODUCT_VENDOR_PROPERTY,
            TemplateDefinition.PRODUCT_URL_PROPERTY, TemplateDefinition.URL_PROPERTY, TemplateDefinition.TYPE_PROPERTY,
            TemplateDefinition.PRODUCT_NAME_PROPERTY, TemplateDefinition.DISK_FILE_SIZE_PROPERTY,
            TemplateDefinition.DESCRIPTION_PROPERTY);
    }

    @Override
    public TemplateDefinition createUniqueInstance()
    {

        AppsLibrary appsLibrary = appsLibraryGenerator.createUniqueInstance();
        Category category = categoryGenerator.createUniqueInstance();
        Icon icon = iconGenerator.createUniqueInstance();

        return createInstance(appsLibrary, category, icon);
    }

    public TemplateDefinition createInstance(final AppsLibrary appsLibrary, final Category category,
        final Icon icon)
    {
        TemplateDefinition templateDef =
            new TemplateDefinition(newString(nextSeed(), 0, 30),
                newString(nextSeed(), 0, 30),
                newString(nextSeed(), 0, 30),
                newString(nextSeed(), 0, 30),
                newString(nextSeed(), 0, 30),
                DiskFormatType.VDI_FLAT,
                newString(nextSeed(), 0, 30),
                nextSeed());
        templateDef.setAppsLibrary(appsLibrary);
        templateDef.setCategory(category);
        templateDef.setIcon(icon);
        return templateDef;

    }

    @Override
    public void addAuxiliaryEntitiesToPersist(final TemplateDefinition entity,
        final List<Object> entitiesToPersist)
    {
        super.addAuxiliaryEntitiesToPersist(entity, entitiesToPersist);

        AppsLibrary appslibrary = entity.getAppsLibrary();
        appsLibraryGenerator.addAuxiliaryEntitiesToPersist(appslibrary, entitiesToPersist);
        entitiesToPersist.add(appslibrary);

        Category category = entity.getCategory();
        categoryGenerator.addAuxiliaryEntitiesToPersist(category, entitiesToPersist);
        entitiesToPersist.add(category);

        if (entity.getIcon() != null)
        {
            Icon icon = entity.getIcon();
            iconGenerator.addAuxiliaryEntitiesToPersist(icon, entitiesToPersist);
            entitiesToPersist.add(icon);
        }

    }
}
