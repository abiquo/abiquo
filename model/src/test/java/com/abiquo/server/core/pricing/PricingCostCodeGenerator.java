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
