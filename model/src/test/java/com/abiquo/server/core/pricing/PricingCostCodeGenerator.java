package com.abiquo.server.core.pricing;

import java.math.BigDecimal;
import java.util.List;

import com.abiquo.server.core.common.DefaultEntityGenerator;
import com.softwarementors.commons.test.SeedGenerator;
import com.softwarementors.commons.testng.AssertEx;

public class PricingCostCodeGenerator extends DefaultEntityGenerator<PricingCostCode>
{

    private CostCodeGenerator costCodeGenerator;

    private PricingTemplateGenerator pricingTemplateGenerator;

    public PricingCostCodeGenerator(final SeedGenerator seed)
    {
        super(seed);

        this.costCodeGenerator = new CostCodeGenerator(seed);
        this.pricingTemplateGenerator = new PricingTemplateGenerator(seed);

    }

    @Override
    public void assertAllPropertiesEqual(final PricingCostCode obj1, final PricingCostCode obj2)
    {
        AssertEx.assertPropertiesEqualSilent(obj1, obj2, PricingCostCode.PRICE_PROPERTY);
    }

    @Override
    public PricingCostCode createUniqueInstance()
    {
        CostCode costCode = costCodeGenerator.createUniqueInstance();
        PricingTemplate pt = pricingTemplateGenerator.createUniqueInstance();
        BigDecimal seedPurchase = newBigDecimal();

        return createInstance(costCode, pt, seedPurchase);
    }

    public PricingCostCode createInstance(final CostCode costCode, final PricingTemplate pt)
    {
        BigDecimal price = newBigDecimal();
        return new PricingCostCode(price, pt, costCode);
    }

    public PricingCostCode createInstance(final CostCode costCode, final PricingTemplate pt,
        final BigDecimal price)
    {
        return new PricingCostCode(price, pt, costCode);
    }

    @Override
    public void addAuxiliaryEntitiesToPersist(final PricingCostCode entity,
        final List<Object> entitiesToPersist)
    {
        super.addAuxiliaryEntitiesToPersist(entity, entitiesToPersist);

        CostCode costCode = entity.getCostCode();
        costCodeGenerator.addAuxiliaryEntitiesToPersist(costCode, entitiesToPersist);
        entitiesToPersist.add(entity.getCostCode());

        PricingTemplate pt = entity.getPricingTemplate();
        pricingTemplateGenerator.addAuxiliaryEntitiesToPersist(pt, entitiesToPersist);
        entitiesToPersist.add(entity.getPricingTemplate());

    }

}
