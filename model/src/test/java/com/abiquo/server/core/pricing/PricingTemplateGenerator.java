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
import com.abiquo.server.core.enterprise.Enterprise;
import com.abiquo.server.core.enterprise.EnterpriseGenerator;
import com.softwarementors.commons.test.SeedGenerator;
import com.softwarementors.commons.testng.AssertEx;

public class PricingTemplateGenerator extends DefaultEntityGenerator<PricingTemplate>
{

    EnterpriseGenerator enterpriseGenerator;

    CurrencyGenerator currencyGenerator;

    public PricingTemplateGenerator(final SeedGenerator seed)
    {
        super(seed);
        enterpriseGenerator = new EnterpriseGenerator(seed);
        currencyGenerator = new CurrencyGenerator(seed);

    }

    @Override
    public void assertAllPropertiesEqual(final PricingTemplate obj1, final PricingTemplate obj2)
    {
        AssertEx.assertPropertiesEqualSilent(obj1, obj2, PricingTemplate.NAME_PROPERTY,
            PricingTemplate.HD_GB_PROPERTY, PricingTemplate.STANDING_CHARGE_PERIOD_PROPERTY,
            PricingTemplate.LIMIT_MAXIMUM_DEPLOYED_CHARGED_PROPERTY, PricingTemplate.VLAN_PROPERTY,
            PricingTemplate.SHOW_MINIMUM_CHARGE_PROPERTY, PricingTemplate.CHARGING_PERIOD_PROPERTY,
            PricingTemplate.MINIMUM_CHARGE_PERIOD_PROPERTY,
            PricingTemplate.SHOW_CHANGES_BEFORE_PROPERTY, PricingTemplate.MINIMUM_CHARGE_PROPERTY,
            PricingTemplate.PUBLIC_IP_PROPERTY, PricingTemplate.V_CPU_PROPERTY,
            PricingTemplate.MEMORY_MB_PROPERTY);
    }

    @Override
    public PricingTemplate createUniqueInstance()
    {
        // Enterprise enterprise = enterpriseGenerator.createUniqueInstance();
        return createInstance(null);
    }

    public PricingTemplate createInstance(final Enterprise enterprise)
    {
        Currency currency = currencyGenerator.createUniqueInstance();

        return createInstance(enterprise, currency);
    }

    public PricingTemplate createInstance(final Enterprise enterprise, final Currency currency)
    {
        BigDecimal seed = newBigDecimal();
        int seedint = nextSeed();

        final String name = newString(nextSeed(), 0, 255);
        final BigDecimal hdGb = seed;
        final BigDecimal standingChargePeriod = seed;
        final BigDecimal limitMaximumDeployedCharged = seed;
        final BigDecimal vlan = seed;
        final boolean showMinimumCharge = true;
        final int chargingPeriod = seedint;
        final BigDecimal minimumChargePeriod = seed;
        final boolean showChangesBefore = true;
        final int minimumCharge = seedint;
        final BigDecimal publicIp = seed;
        final BigDecimal vCpu = seed;
        final BigDecimal memoryMb = seed;

        PricingTemplate pricingTemplate =
            new PricingTemplate(name,
                hdGb,
                standingChargePeriod,
                enterprise,
                limitMaximumDeployedCharged,
                vlan,
                showMinimumCharge,
                chargingPeriod,
                minimumChargePeriod,
                showChangesBefore,
                minimumCharge,
                currency,
                publicIp,
                vCpu,
                memoryMb);

        return pricingTemplate;
    }

    @Override
    public void addAuxiliaryEntitiesToPersist(final PricingTemplate entity,
        final List<Object> entitiesToPersist)
    {
        super.addAuxiliaryEntitiesToPersist(entity, entitiesToPersist);

        // Enterprise enterprise = entity.getEnterprise();
        // enterpriseGenerator.addAuxiliaryEntitiesToPersist(enterprise, entitiesToPersist);
        // entitiesToPersist.add(enterprise);

        Currency currency = entity.getCurrency();
        currencyGenerator.addAuxiliaryEntitiesToPersist(currency, entitiesToPersist);
        entitiesToPersist.add(currency);

    }

}
