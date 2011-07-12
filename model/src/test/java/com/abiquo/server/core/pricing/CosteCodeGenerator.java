package com.abiquo.server.core.pricing;

import static org.testng.Assert.assertEquals;

import java.util.List;

import com.abiquo.server.core.common.DefaultEntityGenerator;
import com.softwarementors.commons.test.SeedGenerator;
import com.softwarementors.commons.testng.AssertEx;

public class CosteCodeGenerator extends DefaultEntityGenerator<CosteCode>
{

    

    public CosteCodeGenerator(SeedGenerator seed)
    {
        super(seed);
        
    }

    @Override
    public void assertAllPropertiesEqual(CosteCode obj1, CosteCode obj2)
    {
      AssertEx.assertPropertiesEqualSilent(obj1, obj2, CosteCode.VARIABLE_PROPERTY);
    }

    @Override
    public CosteCode createUniqueInstance()
    {
        // FIXME: Write here how to create the pojo

        CosteCode costeCode = new CosteCode();

        

        return costeCode;
    }

    @Override
    public void addAuxiliaryEntitiesToPersist(CosteCode entity, List<Object> entitiesToPersist)
    {
        super.addAuxiliaryEntitiesToPersist(entity, entitiesToPersist);
        
        
    }

}
