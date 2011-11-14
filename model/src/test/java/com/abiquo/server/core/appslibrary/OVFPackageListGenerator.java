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

public class OVFPackageListGenerator extends DefaultEntityGenerator<OVFPackageList>
{

    AppsLibraryGenerator appsLibraryGenerator;

    public OVFPackageListGenerator(final SeedGenerator seed)
    {
        super(seed);

        appsLibraryGenerator = new AppsLibraryGenerator(seed);

    }

    @Override
    public void assertAllPropertiesEqual(final OVFPackageList obj1, final OVFPackageList obj2)
    {
        AssertEx.assertPropertiesEqualSilent(obj1, obj2, OVFPackageList.NAME_PROPERTY,
            OVFPackageList.URL_PROPERTY);
    }

    @Override
    public OVFPackageList createUniqueInstance()
    {

        OVFPackageList oVFPackageList =
            createInstance(newString(nextSeed(), 0, 30), newString(nextSeed(), 0, 30));

        AppsLibrary appsLibrary = appsLibraryGenerator.createUniqueInstance();
        oVFPackageList.setAppsLibrary(appsLibrary);

        return oVFPackageList;
    }

    public OVFPackageList createInstance(final String name, final String url)
    {
        return new OVFPackageList(name, url);
    }

    @Override
    public void addAuxiliaryEntitiesToPersist(final OVFPackageList entity,
        final List<Object> entitiesToPersist)
    {
        super.addAuxiliaryEntitiesToPersist(entity, entitiesToPersist);

        AppsLibrary appsLibrary = entity.getAppsLibrary();
        appsLibraryGenerator.addAuxiliaryEntitiesToPersist(appsLibrary, entitiesToPersist);
        entitiesToPersist.add(appsLibrary);

    }

}
