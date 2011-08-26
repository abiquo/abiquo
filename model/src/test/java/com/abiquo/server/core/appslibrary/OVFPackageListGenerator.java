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
