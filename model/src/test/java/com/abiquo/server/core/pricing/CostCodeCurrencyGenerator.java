package com.abiquo.server.core.pricing;

import java.math.BigDecimal;
import java.util.List;

import com.abiquo.server.core.common.DefaultEntityGenerator;
import com.softwarementors.commons.test.SeedGenerator;
import com.softwarementors.commons.testng.AssertEx;

public class CostCodeCurrencyGenerator extends DefaultEntityGenerator<CostCodeCurrency>
{

    private CostCodeGenerator costCodeGenerator;

    private CurrencyGenerator currencyGenerator;

    public CostCodeCurrencyGenerator(final SeedGenerator seed)
    {
        super(seed);
        this.costCodeGenerator = new CostCodeGenerator(seed);
        this.currencyGenerator = new CurrencyGenerator(seed);

    }

    @Override
    public void assertAllPropertiesEqual(final CostCodeCurrency obj1, final CostCodeCurrency obj2)
    {
        AssertEx.assertPropertiesEqualSilent(obj1, obj2, CostCodeCurrency.PRICE_PROPERTY);
    }

    @Override
    public CostCodeCurrency createUniqueInstance()
    {
        CostCode costCode = costCodeGenerator.createUniqueInstance();
        Currency currency = currencyGenerator.createUniqueInstance();
        BigDecimal seedPurchase = newBigDecimal();

        return createInstance(costCode, currency, seedPurchase);
    }

    public CostCodeCurrency createInstance(final CostCode costCode, final Currency currency,
        final BigDecimal purchase)
    {
        return new CostCodeCurrency(purchase, costCode, currency);
    }

    @Override
    public void addAuxiliaryEntitiesToPersist(final CostCodeCurrency entity,
        final List<Object> entitiesToPersist)
    {
        super.addAuxiliaryEntitiesToPersist(entity, entitiesToPersist);

        CostCode costCode = entity.getCostCode();
        costCodeGenerator.addAuxiliaryEntitiesToPersist(costCode, entitiesToPersist);
        entitiesToPersist.add(entity.getCostCode());

        Currency currency = entity.getCurrency();
        currencyGenerator.addAuxiliaryEntitiesToPersist(currency, entitiesToPersist);
        entitiesToPersist.add(entity.getCurrency());

    }

}
