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
