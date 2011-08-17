package com.abiquo.server.core.appslibrary;

import java.util.List;

import com.abiquo.server.core.common.DefaultEntityGenerator;
import com.abiquo.server.core.enterprise.Enterprise;
import com.abiquo.server.core.enterprise.EnterpriseGenerator;
import com.softwarementors.bzngine.entities.PersistentEntity;
import com.softwarementors.commons.test.SeedGenerator;
import com.softwarementors.commons.testng.AssertEx;

public class AppsLibraryGenerator extends DefaultEntityGenerator<AppsLibrary>
{

    EnterpriseGenerator enterpriseGenerator;

    public AppsLibraryGenerator(final SeedGenerator seed)
    {
        super(seed);

        enterpriseGenerator = new EnterpriseGenerator(seed);

    }

    @Override
    public void assertAllPropertiesEqual(final AppsLibrary obj1, final AppsLibrary obj2)
    {
        AssertEx.assertPropertiesEqualSilent(obj1, obj2, PersistentEntity.ID_PROPERTY);
    }

    @Override
    public AppsLibrary createUniqueInstance()
    {
        // FIXME: Write here how to create the pojo

        AppsLibrary appsLibrary = new AppsLibrary();

        Enterprise n1 = enterpriseGenerator.createUniqueInstance();
        appsLibrary.setEnterprise(n1);

        return appsLibrary;
    }

    @Override
    public void addAuxiliaryEntitiesToPersist(final AppsLibrary entity,
        final List<Object> entitiesToPersist)
    {
        super.addAuxiliaryEntitiesToPersist(entity, entitiesToPersist);

        Enterprise n1 = entity.getEnterprise();
        enterpriseGenerator.addAuxiliaryEntitiesToPersist(n1, entitiesToPersist);
        entitiesToPersist.add(n1);

    }

}
