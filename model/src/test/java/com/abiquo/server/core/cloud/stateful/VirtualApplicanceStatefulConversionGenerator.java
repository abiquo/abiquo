package com.abiquo.server.core.cloud.stateful;

import java.util.List;
import java.util.Random;

import com.abiquo.server.core.cloud.State;
import com.abiquo.server.core.cloud.VirtualAppliance;
import com.abiquo.server.core.cloud.VirtualApplianceGenerator;
import com.abiquo.server.core.common.DefaultEntityGenerator;
import com.softwarementors.commons.test.SeedGenerator;
import com.softwarementors.commons.testng.AssertEx;

public class VirtualApplicanceStatefulConversionGenerator extends
    DefaultEntityGenerator<VirtualApplicanceStatefulConversion>
{
    VirtualApplianceGenerator virtualApplianceGenerator;

    public VirtualApplicanceStatefulConversionGenerator(final SeedGenerator seed)
    {
        super(seed);

        virtualApplianceGenerator = new VirtualApplianceGenerator(seed);
    }

    @Override
    public void assertAllPropertiesEqual(final VirtualApplicanceStatefulConversion obj1,
        final VirtualApplicanceStatefulConversion obj2)
    {
        AssertEx.assertPropertiesEqualSilent(obj1, obj2,
            VirtualApplicanceStatefulConversion.ID_PROPERTY,
            VirtualApplicanceStatefulConversion.ID_USER_PROPERTY,
            VirtualApplicanceStatefulConversion.VIRTUAL_APP_PROPERTY,
            VirtualApplicanceStatefulConversion.SUB_STATE_PROPERTY,
            VirtualApplicanceStatefulConversion.STATE_PROPERTY);
    }

    @Override
    public VirtualApplicanceStatefulConversion createUniqueInstance()
    {
        State state = newEnum(State.class, nextSeed());
        VirtualAppliance virtualAppliance = virtualApplianceGenerator.createUniqueInstance();

        VirtualApplicanceStatefulConversion virtualApplicanceStatefulConversion =
            new VirtualApplicanceStatefulConversion(new Random().nextInt(),
                state,
                state,
                virtualAppliance);

        return virtualApplicanceStatefulConversion;
    }

    @Override
    public void addAuxiliaryEntitiesToPersist(final VirtualApplicanceStatefulConversion entity,
        final List<Object> entitiesToPersist)
    {
        super.addAuxiliaryEntitiesToPersist(entity, entitiesToPersist);

        VirtualAppliance virtualAppliance = entity.getVirtualAppliance();
        virtualApplianceGenerator
            .addAuxiliaryEntitiesToPersist(virtualAppliance, entitiesToPersist);
        entitiesToPersist.add(virtualAppliance);
    }

}
