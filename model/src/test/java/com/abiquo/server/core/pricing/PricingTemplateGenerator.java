package com.abiquo.server.core.pricing;

import java.util.List;

import com.abiquo.server.core.common.DefaultEntityGenerator;
import com.softwarementors.commons.test.SeedGenerator;
import com.softwarementors.commons.testng.AssertEx;

public class PricingTemplateGenerator extends DefaultEntityGenerator<PricingTemplate>
{

    public PricingTemplateGenerator(final SeedGenerator seed)
    {
        super(seed);

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
        // FIXME: Write here how to create the pojo

        PricingTemplate pricingTemplate = new PricingTemplate();

        return pricingTemplate;
    }

    @Override
    public void addAuxiliaryEntitiesToPersist(final PricingTemplate entity,
        final List<Object> entitiesToPersist)
    {
        super.addAuxiliaryEntitiesToPersist(entity, entitiesToPersist);

    }

}
