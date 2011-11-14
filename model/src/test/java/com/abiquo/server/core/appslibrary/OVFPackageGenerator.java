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

public class OVFPackageGenerator extends DefaultEntityGenerator<OVFPackage>
{

    AppsLibraryGenerator appsLibraryGenerator;

    CategoryGenerator categoryGenerator;

    IconGenerator iconGenerator;

    public OVFPackageGenerator(final SeedGenerator seed)
    {
        super(seed);

        appsLibraryGenerator = new AppsLibraryGenerator(seed);

        categoryGenerator = new CategoryGenerator(seed);

        iconGenerator = new IconGenerator(seed);

    }

    @Override
    public void assertAllPropertiesEqual(final OVFPackage obj1, final OVFPackage obj2)
    {
        AssertEx.assertPropertiesEqualSilent(obj1, obj2, OVFPackage.PRODUCT_VERSION_PROPERTY,
            OVFPackage.NAME_PROPERTY, OVFPackage.PRODUCT_VENDOR_PROPERTY,
            OVFPackage.PRODUCT_URL_PROPERTY, OVFPackage.URL_PROPERTY, OVFPackage.TYPE_PROPERTY,
            OVFPackage.PRODUCT_NAME_PROPERTY, OVFPackage.DISK_FILE_SIZE_PROPERTY,
            OVFPackage.DESCRIPTION_PROPERTY);
    }

    @Override
    public OVFPackage createUniqueInstance()
    {

        AppsLibrary appsLibrary = appsLibraryGenerator.createUniqueInstance();
        Category category = categoryGenerator.createUniqueInstance();
        Icon icon = iconGenerator.createUniqueInstance();

        return createInstance(appsLibrary, category, icon);
    }

    public OVFPackage createInstance(final AppsLibrary appsLibrary, final Category category,
        final Icon icon)
    {
        OVFPackage ovfpackage =
            new OVFPackage(newString(nextSeed(), 0, 30),
                newString(nextSeed(), 0, 30),
                newString(nextSeed(), 0, 30),
                newString(nextSeed(), 0, 30),
                newString(nextSeed(), 0, 30),
                DiskFormatType.VDI_FLAT,
                newString(nextSeed(), 0, 30),
                nextSeed());
        ovfpackage.setAppsLibrary(appsLibrary);
        ovfpackage.setCategory(category);
        ovfpackage.setIcon(icon);
        return ovfpackage;

    }

    @Override
    public void addAuxiliaryEntitiesToPersist(final OVFPackage entity,
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
