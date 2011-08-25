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
            OVFPackage.PRODUCT_NAME_PROPERTY, OVFPackage.DISK_SIZE_MB_PROPERTY,
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

        Icon icon = entity.getIcon();
        iconGenerator.addAuxiliaryEntitiesToPersist(icon, entitiesToPersist);
        entitiesToPersist.add(icon);

    }
}
