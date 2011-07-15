package com.abiquo.server.core.pricing;

import java.util.List;

import com.abiquo.server.core.common.DefaultEntityGenerator;
import com.softwarementors.commons.test.SeedGenerator;
import com.softwarementors.commons.testng.AssertEx;

public class CostCodeGenerator extends DefaultEntityGenerator<CostCode>
{

    public CostCodeGenerator(final SeedGenerator seed)
    {
        super(seed);

    }

    @Override
    public void assertAllPropertiesEqual(final CostCode obj1, final CostCode obj2)
    {
        AssertEx.assertPropertiesEqualSilent(obj1, obj2, CostCode.VARIABLE_PROPERTY);
    }

    @Override
    public CostCode createUniqueInstance()
    {
        String name = newString(nextSeed(), 0, 255);

        CostCode costeCode = new CostCode(name);

        return costeCode;
    }

    @Override
    public void addAuxiliaryEntitiesToPersist(final CostCode entity,
        final List<Object> entitiesToPersist)
    {
        super.addAuxiliaryEntitiesToPersist(entity, entitiesToPersist);

    }

}
