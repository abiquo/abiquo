/**
 * Abiquo community edition
 * cloud management application for hybrid clouds
 * Copyright (C) 2008-2010 - Abiquo Holdings S.L.
 *
 * This application is free software; you can redistribute it and/or
 * modify it under the terms of the GNU LESSER GENERAL PUBLIC
 * LICENSE as published by the Free Software Foundation under
 * version 3 of the License
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * LESSER GENERAL PUBLIC LICENSE v.3 for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 */

package com.abiquo.server.core.pricing;

import static org.testng.Assert.assertEquals;

import java.math.BigDecimal;
import java.util.List;

import com.abiquo.server.core.common.DefaultEntityGenerator;
import com.softwarementors.commons.test.SeedGenerator;

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
        assertEquals(obj1.getId(), obj2.getId());
        assertEquals(obj1.getPrice().setScale(2), obj2.getPrice().setScale(2));
        assertEquals(obj1.getCostCode().getId(), obj2.getCostCode().getId());
        assertEquals(obj1.getCurrency().getId(), obj2.getCurrency().getId());
    }

    @Override
    public CostCodeCurrency createUniqueInstance()
    {
        CostCode costCode = costCodeGenerator.createUniqueInstance();
        Currency currency = currencyGenerator.createUniqueInstance();
        BigDecimal seedPurchase = newBigDecimal();

        return createInstance(costCode, currency, seedPurchase);
    }

    public CostCodeCurrency createInstance(final CostCode costCode)
    {
        BigDecimal purchase = newBigDecimal();
        Currency currency = currencyGenerator.createUniqueInstance();
        return new CostCodeCurrency(purchase, costCode, currency);
    }

    public CostCodeCurrency createInstance(final CostCode costCode, final Currency currency)
    {
        BigDecimal purchase = newBigDecimal();
        return new CostCodeCurrency(purchase, costCode, currency);
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
