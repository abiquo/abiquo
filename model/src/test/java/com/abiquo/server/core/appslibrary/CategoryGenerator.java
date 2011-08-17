package com.abiquo.server.core.appslibrary;

import java.util.List;

import com.abiquo.server.core.common.DefaultEntityGenerator;
import com.softwarementors.commons.test.SeedGenerator;
import com.softwarementors.commons.testng.AssertEx;

public class CategoryGenerator extends DefaultEntityGenerator<Category>
{

    public CategoryGenerator(final SeedGenerator seed)
    {
        super(seed);

    }

    @Override
    public void assertAllPropertiesEqual(final Category obj1, final Category obj2)
    {
        AssertEx.assertPropertiesEqualSilent(obj1, obj2, Category.NAME_PROPERTY,
            Category.IS_DEFAULT_PROPERTY, Category.IS_ERASABLE_PROPERTY);
    }

    @Override
    public Category createUniqueInstance()
    {

        Category category = new Category();
        category.setName(newString(nextSeed(), 0, 30));
        category.setIsDefault(0);
        category.setIsErasable(1);
        return category;
    }

    @Override
    public void addAuxiliaryEntitiesToPersist(final Category entity,
        final List<Object> entitiesToPersist)
    {
        super.addAuxiliaryEntitiesToPersist(entity, entitiesToPersist);

    }

}
