package com.abiquo.server.core.cloud.stateful;

import java.util.List;

import com.abiquo.server.core.cloud.NodeVirtualImage;
import com.abiquo.server.core.cloud.NodeVirtualImageGenerator;
import com.abiquo.server.core.common.DefaultEntityGenerator;
import com.abiquo.server.core.infrastructure.storage.Tier;
import com.abiquo.server.core.infrastructure.storage.TierGenerator;
import com.softwarementors.commons.test.SeedGenerator;
import com.softwarementors.commons.testng.AssertEx;

public class NodeVirtualImageStatefulConversionGenerator extends
    DefaultEntityGenerator<NodeVirtualImageStatefulConversion>
{
    VirtualApplicanceStatefulConversionGenerator virtualApplicanceStatefulConversionGenerator;

    NodeVirtualImageGenerator nodeVirtualImageGenerator;

    TierGenerator tierGenerator;

    public NodeVirtualImageStatefulConversionGenerator(final SeedGenerator seed)
    {
        super(seed);

        virtualApplicanceStatefulConversionGenerator =
            new VirtualApplicanceStatefulConversionGenerator(seed);

        nodeVirtualImageGenerator = new NodeVirtualImageGenerator(seed);

        tierGenerator = new TierGenerator(seed);
    }

    @Override
    public void assertAllPropertiesEqual(final NodeVirtualImageStatefulConversion obj1,
        final NodeVirtualImageStatefulConversion obj2)
    {
        AssertEx.assertPropertiesEqualSilent(obj1, obj2,
            NodeVirtualImageStatefulConversion.DISK_STATEFUL_CONVERSION_PROPERTY,
            NodeVirtualImageStatefulConversion.NEW_NAME_PROPERTY,
            NodeVirtualImageStatefulConversion.TIER_PROPERTY,
            NodeVirtualImageStatefulConversion.VIRTUAL_APPLIANCE_STATEFUL_CONVERSION_PROPERTY,
            NodeVirtualImageStatefulConversion.ID_PROPERTY,
            NodeVirtualImageStatefulConversion.NODE_VIRTUAL_IMAGE_PROPERTY,
            NodeVirtualImageStatefulConversion.VIRTUAL_IMAGE_CONVERSION_PROPERTY);
    }

    @Override
    public NodeVirtualImageStatefulConversion createUniqueInstance()
    {
        String newName = newString(nextSeed(), 0, 255);
        VirtualApplicanceStatefulConversion vappStatefulConversion =
            virtualApplicanceStatefulConversionGenerator.createUniqueInstance();
        NodeVirtualImage nodeVirtualImage = nodeVirtualImageGenerator.createUniqueInstance();
        Tier tier = tierGenerator.createUniqueInstance();

        NodeVirtualImageStatefulConversion nodeVirtualImageStatefulConversion =
            new NodeVirtualImageStatefulConversion(newName,
                vappStatefulConversion,
                nodeVirtualImage,
                tier);

        return nodeVirtualImageStatefulConversion;
    }

    @Override
    public void addAuxiliaryEntitiesToPersist(final NodeVirtualImageStatefulConversion entity,
        final List<Object> entitiesToPersist)
    {
        super.addAuxiliaryEntitiesToPersist(entity, entitiesToPersist);

        VirtualApplicanceStatefulConversion vappStatefulConversion =
            entity.getVirtualApplianceStatefulConversion();
        virtualApplicanceStatefulConversionGenerator.addAuxiliaryEntitiesToPersist(
            vappStatefulConversion, entitiesToPersist);
        entitiesToPersist.add(vappStatefulConversion);

        NodeVirtualImage nodeVirtualImage = entity.getNodeVirtualImage();
        nodeVirtualImageGenerator
            .addAuxiliaryEntitiesToPersist(nodeVirtualImage, entitiesToPersist);
        entitiesToPersist.add(nodeVirtualImage);

        Tier tier = entity.getTier();
        tierGenerator.addAuxiliaryEntitiesToPersist(tier, entitiesToPersist);
        entitiesToPersist.add(tier);
    }

}
