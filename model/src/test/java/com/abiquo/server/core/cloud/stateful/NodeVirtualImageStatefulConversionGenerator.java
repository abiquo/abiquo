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

    private NodeVirtualImageGenerator nodeVirtualImageGenerator;

    private TierGenerator tierGenerator;

    private VirtualApplianceStatefulConversionGenerator virtualApplianceStatefulConversionGenerator;

    public NodeVirtualImageStatefulConversionGenerator(final SeedGenerator seed)
    {
        super(seed);

        nodeVirtualImageGenerator = new NodeVirtualImageGenerator(seed);

        tierGenerator = new TierGenerator(seed);

        virtualApplianceStatefulConversionGenerator =
            new VirtualApplianceStatefulConversionGenerator(seed);
    }

    @Override
    public void assertAllPropertiesEqual(final NodeVirtualImageStatefulConversion obj1,
        final NodeVirtualImageStatefulConversion obj2)
    {
        AssertEx.assertPropertiesEqualSilent(obj1, obj2,
            NodeVirtualImageStatefulConversion.NEW_NAME_PROPERTY);

        nodeVirtualImageGenerator.assertAllPropertiesEqual(obj1.getNodeVirtualImage(),
            obj2.getNodeVirtualImage());
        tierGenerator.assertAllPropertiesEqual(obj1.getTier(), obj2.getTier());
        virtualApplianceStatefulConversionGenerator.assertAllPropertiesEqual(
            obj1.getVirtualApplianceStatefulConversion(),
            obj2.getVirtualApplianceStatefulConversion());
    }

    @Override
    public NodeVirtualImageStatefulConversion createUniqueInstance()
    {
        String newName =
            newString(nextSeed(), NodeVirtualImageStatefulConversion.NEW_NAME_LENGTH_MIN,
                NodeVirtualImageStatefulConversion.NEW_NAME_LENGTH_MAX);
        NodeVirtualImage nodeVirtualImage = nodeVirtualImageGenerator.createUniqueInstance();
        VirtualApplianceStatefulConversion virtualApplianceStatefulConversion =
            virtualApplianceStatefulConversionGenerator.createUniqueInstance();
        Tier tier = tierGenerator.createUniqueInstance();

        NodeVirtualImageStatefulConversion nodeVirtualImageStatefulConversion =
            new NodeVirtualImageStatefulConversion(newName,
                virtualApplianceStatefulConversion,
                nodeVirtualImage,
                tier);

        return nodeVirtualImageStatefulConversion;
    }

    public NodeVirtualImageStatefulConversion createInstance(
        final NodeVirtualImage nodeVirtualImage,
        final VirtualApplianceStatefulConversion virtualApplianceStatefulConversion)
    {
        String newName =
            newString(nextSeed(), NodeVirtualImageStatefulConversion.NEW_NAME_LENGTH_MIN,
                NodeVirtualImageStatefulConversion.NEW_NAME_LENGTH_MAX);
        Tier tier = tierGenerator.createUniqueInstance();

        NodeVirtualImageStatefulConversion nodeVirtualImageStatefulConversion =
            new NodeVirtualImageStatefulConversion(newName,
                virtualApplianceStatefulConversion,
                nodeVirtualImage,
                tier);

        return nodeVirtualImageStatefulConversion;
    }

    @Override
    public void addAuxiliaryEntitiesToPersist(final NodeVirtualImageStatefulConversion entity,
        final List<Object> entitiesToPersist)
    {
        super.addAuxiliaryEntitiesToPersist(entity, entitiesToPersist);

        NodeVirtualImage nodeVirtualImage = entity.getNodeVirtualImage();
        nodeVirtualImageGenerator
            .addAuxiliaryEntitiesToPersist(nodeVirtualImage, entitiesToPersist);
        entitiesToPersist.add(nodeVirtualImage);

        Tier tier = entity.getTier();
        tierGenerator.addAuxiliaryEntitiesToPersist(tier, entitiesToPersist);
        entitiesToPersist.add(tier);

        VirtualApplianceStatefulConversion virtualApplianceStatefulConversion =
            entity.getVirtualApplianceStatefulConversion();
        virtualApplianceStatefulConversionGenerator.addAuxiliaryEntitiesToPersist(
            virtualApplianceStatefulConversion, entitiesToPersist);
        entitiesToPersist.add(virtualApplianceStatefulConversion);
    }

}
