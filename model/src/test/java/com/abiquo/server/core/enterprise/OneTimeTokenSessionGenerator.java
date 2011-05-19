package com.abiquo.server.core.enterprise;

import java.util.List;

import com.abiquo.server.core.common.DefaultEntityGenerator;
import com.softwarementors.commons.test.SeedGenerator;
import com.softwarementors.commons.testng.AssertEx;

/**
 * @author ssedano
 */
public class OneTimeTokenSessionGenerator extends DefaultEntityGenerator<OneTimeTokenSession>
{

    public OneTimeTokenSessionGenerator(SeedGenerator seed)
    {
        super(seed);

    }

    @Override
    public void assertAllPropertiesEqual(OneTimeTokenSession obj1, OneTimeTokenSession obj2)
    {
        AssertEx.assertPropertiesEqualSilent(obj1, obj2, OneTimeTokenSession.TOKEN_PROPERTY);
    }

    @Override
    public OneTimeTokenSession createUniqueInstance()
    {
        return createUniqueInstance(this.newString(this.nextSeed(),
            OneTimeTokenSession.TOKEN_LENGTH_MIN, OneTimeTokenSession.TOKEN_LENGTH_MAX));
    }

    public OneTimeTokenSession createUniqueInstance(String token)
    {
        return new OneTimeTokenSession(token);
    }

    @Override
    public void addAuxiliaryEntitiesToPersist(OneTimeTokenSession entity,
        List<Object> entitiesToPersist)
    {
        super.addAuxiliaryEntitiesToPersist(entity, entitiesToPersist);

    }

}
