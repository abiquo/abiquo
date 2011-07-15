package com.abiquo.server.core.pricing;

import java.util.List;

import com.abiquo.server.core.common.DefaultEntityGenerator;
import com.abiquo.server.core.enterprise.Role;
import com.softwarementors.commons.test.SeedGenerator;
import com.softwarementors.commons.testng.AssertEx;

public class CurrencyGenerator extends DefaultEntityGenerator<Currency>
{

    public CurrencyGenerator(final SeedGenerator seed)
    {
        super(seed);

    }

    @Override
    public void assertAllPropertiesEqual(final Currency obj1, final Currency obj2)
    {
        AssertEx.assertPropertiesEqualSilent(obj1, obj2, Currency.NAME_PROPERTY,
            Currency.SYMBOL_PROPERTY, Role.BLOCKED_PROPERTY);
    }

    @Override
    public Currency createUniqueInstance()
    {
        String name = newString(nextSeed(), 0, 255);
        String symbol = newString(nextSeed(), 0, 5);

        Currency currency = new Currency(name, symbol);

        return currency;
    }

    @Override
    public void addAuxiliaryEntitiesToPersist(final Currency entity,
        final List<Object> entitiesToPersist)
    {
        super.addAuxiliaryEntitiesToPersist(entity, entitiesToPersist);

    }

}
