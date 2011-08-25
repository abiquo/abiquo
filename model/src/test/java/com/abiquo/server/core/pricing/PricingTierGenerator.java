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
import com.abiquo.server.core.infrastructure.storage.Tier;
import com.abiquo.server.core.infrastructure.storage.TierGenerator;
import com.softwarementors.commons.test.SeedGenerator;
import com.softwarementors.commons.testng.AssertEx;

public class PricingTierGenerator extends DefaultEntityGenerator<PricingTier>
{

    private TierGenerator tierGenerator;

    private CurrencyGenerator currencyGenerator;

    private PricingTemplateGenerator pricingTemplateGenerator;

    public PricingTierGenerator(final SeedGenerator seed)
    {
        super(seed);

        this.tierGenerator = new TierGenerator(seed);
        this.pricingTemplateGenerator = new PricingTemplateGenerator(seed);

    }

    @Override
    public void assertAllPropertiesEqual(final PricingTier obj1, final PricingTier obj2)
    {
        AssertEx.assertPropertiesEqualSilent(obj1, obj2, PricingTier.PRICE_PROPERTY);
    }

    @Override
    public PricingTier createUniqueInstance()
    {
        Tier tier = tierGenerator.createUniqueInstance();
        PricingTemplate pt = pricingTemplateGenerator.createUniqueInstance();
        BigDecimal seedPurchase = newBigDecimal();

        return createInstance(tier, pt, seedPurchase);
    }

    public PricingTier createInstance(final Tier tier, final PricingTemplate pt)
    {
        BigDecimal price = newBigDecimal();
        return new PricingTier(price, pt, tier);
    }

    public PricingTier createInstance(final Tier tier, final PricingTemplate pt,
        final BigDecimal price)
    {
        return new PricingTier(price, pt, tier);
    }

    @Override
    public void addAuxiliaryEntitiesToPersist(final PricingTier entity,
        final List<Object> entitiesToPersist)
    {
        super.addAuxiliaryEntitiesToPersist(entity, entitiesToPersist);

        Tier tier = entity.getTier();
        tierGenerator.addAuxiliaryEntitiesToPersist(tier, entitiesToPersist);
        entitiesToPersist.add(entity.getTier());

        PricingTemplate pt = entity.getPricingTemplate();
        pricingTemplateGenerator.addAuxiliaryEntitiesToPersist(pt, entitiesToPersist);
        entitiesToPersist.add(entity.getPricingTemplate());

    }

    public PricingTier createInstance(final PricingTemplate pricingTemplate, final Tier tier)
    {
        BigDecimal price = newBigDecimal();
        return new PricingTier(price, pricingTemplate, tier);
    }

}
