package com.abiquo.server.core.pricing;

import static org.testng.Assert.assertEquals;

import java.util.List;

import com.abiquo.server.core.common.DefaultEntityGenerator;
import com.softwarementors.commons.test.SeedGenerator;
import com.softwarementors.commons.testng.AssertEx;

public class CurrencyGenerator extends DefaultEntityGenerator<Currency>
{

    

    public CurrencyGenerator(SeedGenerator seed)
    {
        super(seed);
        
    }

    @Override
    public void assertAllPropertiesEqual(Currency obj1, Currency obj2)
    {
      AssertEx.assertPropertiesEqualSilent(obj1, obj2, Currency.NAME_PROPERTY,Currency.SIMBOL_PROPERTY);
    }

    @Override
    public Currency createUniqueInstance()
    {
        // FIXME: Write here how to create the pojo

        Currency currency = new Currency();

        

        return currency;
    }

    @Override
    public void addAuxiliaryEntitiesToPersist(Currency entity, List<Object> entitiesToPersist)
    {
        super.addAuxiliaryEntitiesToPersist(entity, entitiesToPersist);
        
        
    }

}
