package com.abiquo.server.core.pricing;

import static org.testng.Assert.assertEquals;

import java.util.List;

import com.abiquo.server.core.common.DefaultEntityGenerator;
import com.softwarementors.commons.test.SeedGenerator;
import com.softwarementors.commons.testng.AssertEx;

public class CostCodeGenerator extends DefaultEntityGenerator<CostCode>
{

    

    public CostCodeGenerator(SeedGenerator seed)
    {
        super(seed);
        
    }

    @Override
    public void assertAllPropertiesEqual(CostCode obj1, CostCode obj2)
    {
      AssertEx.assertPropertiesEqualSilent(obj1, obj2, CostCode.VARIABLE_PROPERTY);
    }

    @Override
    public CostCode createUniqueInstance()
    {
        // FIXME: Write here how to create the pojo

        CostCode costeCode = new CostCode();

        

        return costeCode;
    }

    @Override
    public void addAuxiliaryEntitiesToPersist(CostCode entity, List<Object> entitiesToPersist)
    {
        super.addAuxiliaryEntitiesToPersist(entity, entitiesToPersist);
        
        
    }

}
