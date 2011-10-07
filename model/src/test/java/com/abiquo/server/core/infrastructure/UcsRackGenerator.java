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

package com.abiquo.server.core.infrastructure;

import java.util.List;
import java.util.Random;

import com.abiquo.server.core.common.DefaultEntityGenerator;
import com.softwarementors.commons.test.SeedGenerator;
import com.softwarementors.commons.testng.AssertEx;

public class UcsRackGenerator extends DefaultEntityGenerator<UcsRack>
{

    private DatacenterGenerator datacenterGenerator;

    public UcsRackGenerator(final SeedGenerator seed)
    {
        super(seed);
        this.datacenterGenerator = new DatacenterGenerator(seed);
    }

    @Override
    public void assertAllPropertiesEqual(final UcsRack obj1, final UcsRack obj2)
    {
        AssertEx.assertPropertiesEqualSilent(obj1, obj2, UcsRack.PORT_PROPERTY,
            UcsRack.IP_PROPERTY, UcsRack.PASSWORD_PROPERTY, UcsRack.USER_PROPERTY,
            UcsRack.DEFAULT_TEMPLATE_PROPERTY, UcsRack.MAX_MACHINES_ON_PROPERTY);
    }

    @Override
    public UcsRack createUniqueInstance()
    {
        Datacenter datacenter = this.datacenterGenerator.createUniqueInstance();
        return createInstance(datacenter);
    }

    public UcsRack createInstance(final Datacenter datacenter)
    {
        int seed = nextSeed();
        final String shortDescription =
            newString(seed, Rack.SHORT_DESCRIPTION_LENGTH_MIN, Rack.SHORT_DESCRIPTION_LENGTH_MAX);
        final String longDescription =
            newString(seed, Rack.LONG_DESCRIPTION_LENGTH_MIN, Rack.LONG_DESCRIPTION_LENGTH_MAX);

        Integer vlan_id_min = 2;
        Integer vlan_id_max = 4096;
        Integer nrsq = 80;
        String vlans_id_avoided =
            newString(this.nextSeed(), Rack.VLANS_ID_AVOIDED_LENGTH_MIN,
                Rack.VLANS_ID_AVOIDED_LENGTH_MAX);
        Integer vlan_per_vdc_expected = 8;

        UcsRack ucsRack =
            new UcsRack("rack" + new Random().nextInt(),
                datacenter,
                vlan_id_min,
                vlan_id_max,
                vlan_per_vdc_expected,
                nrsq,
                "10.60.1.28",
                80,
                "user",
                "password",
                "org-root/ls-" + new Random().nextInt(),
                0);
        ucsRack.setVlansIdAvoided(vlans_id_avoided);
        ucsRack.setShortDescription(shortDescription);
        ucsRack.setLongDescription(longDescription);

        return ucsRack;
    }

    @Override
    public void addAuxiliaryEntitiesToPersist(final UcsRack entity,
        final List<Object> entitiesToPersist)
    {
        super.addAuxiliaryEntitiesToPersist(entity, entitiesToPersist);

        Datacenter datacenter = entity.getDatacenter();

        this.datacenterGenerator.addAuxiliaryEntitiesToPersist(datacenter, entitiesToPersist);
        entitiesToPersist.add(entity.getDatacenter());

    }

}
