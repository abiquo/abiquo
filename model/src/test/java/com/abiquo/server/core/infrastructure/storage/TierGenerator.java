package com.abiquo.server.core.infrastructure.storage;

import static org.testng.Assert.assertEquals;

import java.util.List;

import com.abiquo.server.core.common.DefaultEntityGenerator;
import com.softwarementors.commons.test.SeedGenerator;
import com.softwarementors.commons.testng.AssertEx;

public class TierGenerator extends DefaultEntityGenerator<Tier>
{

    

    public TierGenerator(SeedGenerator seed)
    {
        super(seed);
        
    }

    @Override
    public void assertAllPropertiesEqual(Tier obj1, Tier obj2)
    {
      AssertEx.assertPropertiesEqualSilent(obj1, obj2, Tier.NAME_PROPERTY,Tier.DESCRIPTION_PROPERTY);
    }

    @Override
    public Tier createUniqueInstance()
    {

        Tier tier = new Tier();
        
        tier.setName("Default tier");
        tier.setDescription("Default Tier description");        

        return tier;
    }

    @Override
    public void addAuxiliaryEntitiesToPersist(Tier entity, List<Object> entitiesToPersist)
    {
        super.addAuxiliaryEntitiesToPersist(entity, entitiesToPersist);
        
        
    }

}
