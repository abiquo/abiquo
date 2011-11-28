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

import com.abiquo.model.enumerator.PricingPeriod;
import com.abiquo.server.core.common.DefaultEntityGenerator;
import com.softwarementors.commons.test.SeedGenerator;

public class PricingTemplateGenerator extends DefaultEntityGenerator<PricingTemplate>
{

    CurrencyGenerator currencyGenerator;

    public PricingTemplateGenerator(final SeedGenerator seed)
    {
        super(seed);
        currencyGenerator = new CurrencyGenerator(seed);

    }

    @Override
    public void assertAllPropertiesEqual(final PricingTemplate obj1, final PricingTemplate obj2)
    {

        // AssertEx.assertPropertiesEqualSilent(obj1, obj2, PricingTemplate.NAME_PROPERTY,
        // PricingTemplate.HD_GB_PROPERTY, PricingTemplate.STANDING_CHARGE_PERIOD_PROPERTY,
        // PricingTemplate.VLAN_PROPERTY, PricingTemplate.SHOW_MINIMUM_CHARGE_PROPERTY,
        // PricingTemplate.CHARGING_PERIOD_PROPERTY,
        // PricingTemplate.MINIMUM_CHARGE_PERIOD_PROPERTY,
        // PricingTemplate.SHOW_CHANGES_BEFORE_PROPERTY, PricingTemplate.MINIMUM_CHARGE_PROPERTY,
        // PricingTemplate.PUBLIC_IP_PROPERTY, PricingTemplate.V_CPU_PROPERTY,
        // PricingTemplate.MEMORY_MB_PROPERTY);

        assertEquals(obj1.getId(), obj2.getId());
        assertEquals(obj1.getName(), obj2.getName());
        assertEquals(obj1.getHdGB().setScale(2), obj2.getHdGB().setScale(2));
        assertEquals(obj1.getVlan().setScale(2), obj2.getVlan().setScale(2));
        assertEquals(obj1.getChargingPeriod().id(), obj2.getChargingPeriod().id());
        assertEquals(obj1.getMinimumChargePeriod().setScale(2), obj2.getMinimumChargePeriod()
            .setScale(2));
        assertEquals(obj1.getPublicIp().setScale(2), obj2.getPublicIp().setScale(2));
        assertEquals(obj1.getMemoryMB().setScale(2), obj2.getMemoryMB().setScale(2));
        assertEquals(obj1.getCurrency().getId(), obj2.getCurrency().getId());
    }

    @Override
    public PricingTemplate createUniqueInstance()
    {
        Currency currency = currencyGenerator.createUniqueInstance();
        final String name = newString(nextSeed(), 0, 255);
        return createInstance(name, currency);
    }

    public PricingTemplate createInstance(final String name)
    {
        Currency currency = currencyGenerator.createUniqueInstance();

        return createInstance(name, currency);
    }

    public PricingTemplate createInstance(final String name, final Currency currency)
    {
        BigDecimal seed = newBigDecimal(12);

        final BigDecimal hdGb = seed;
        final BigDecimal standingChargePeriod = seed;
        final BigDecimal vlan = seed;
        final PricingPeriod chargingPeriod = PricingPeriod.DAY;
        final BigDecimal minimumChargePeriod = newBigDecimal(3);
        final boolean showChangesBefore = true;
        final PricingPeriod minimumCharge = PricingPeriod.WEEK;
        final BigDecimal publicIp = seed;
        final BigDecimal vCpu = seed;
        final BigDecimal memoryMb = seed;
        final String description = newString(nextSeed(), 0, 255);

        PricingTemplate pricingTemplate =
            new PricingTemplate(name,
                hdGb,
                standingChargePeriod,
                vlan,
                chargingPeriod,
                minimumChargePeriod,
                showChangesBefore,
                minimumCharge,
                currency,
                publicIp,
                vCpu,
                memoryMb,
                false,
                description);

        return pricingTemplate;
    }

    @Override
    public void addAuxiliaryEntitiesToPersist(final PricingTemplate entity,
        final List<Object> entitiesToPersist)
    {
        super.addAuxiliaryEntitiesToPersist(entity, entitiesToPersist);

        Currency currency = entity.getCurrency();
        currencyGenerator.addAuxiliaryEntitiesToPersist(currency, entitiesToPersist);
        entitiesToPersist.add(currency);

    }

}
