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
 * Boston, MA 02111-1307, USA. */
package com.abiquo.server.core.infrastructure;

import java.util.List;

import org.testng.Assert;

import com.abiquo.server.core.common.DefaultEntityGenerator;
import com.softwarementors.commons.test.SeedGenerator;

public class RackGenerator extends DefaultEntityGenerator<Rack>
{

    private DatacenterGenerator datacenterGenerator;

    public RackGenerator(final SeedGenerator seed)
    {
        super(seed);
        this.datacenterGenerator = new DatacenterGenerator(seed);
    }

    @Override
    public void assertAllPropertiesEqual(final Rack arg0, final Rack arg1)
    {
        Assert.assertEquals(arg0.getName(), arg1.getName());
    }

    @Override
    public Rack createUniqueInstance()
    {
        Datacenter datacenter = this.datacenterGenerator.createUniqueInstance();

        return createInstance(datacenter);
    }

    public Rack createInstance(final Datacenter datacenter)
    {
        final String name = newString(nextSeed(), Rack.NAME_LENGTH_MIN, Rack.NAME_LENGTH_MAX);

        return createInstance(datacenter, name);
    }

    public Rack createInstance(final Datacenter datacenter, final String name)
    {
        int seed = nextSeed();
        final String shortDescription =
            newString(seed, Rack.SHORT_DESCRIPTION_LENGTH_MIN, Rack.SHORT_DESCRIPTION_LENGTH_MAX);
        final String longDescription =
            newString(seed, Rack.LONG_DESCRIPTION_LENGTH_MIN, Rack.LONG_DESCRIPTION_LENGTH_MAX);

        Integer vlan_id_min = Rack.VLAN_ID_MIN_DEFAULT_VALUE;
        Integer vlan_id_max = Rack.VLAN_ID_MAX_DEFAULT_VALUE;
        Integer nrsq = Rack.NRSQ_DEFAULT_VALUE;
        String vlans_id_avoided = Rack.VLANS_ID_AVOIDED_DEFAULT_VALUE;
        Integer vlan_per_vdc_expected = Rack.VLAN_PER_VDC_EXPECTED_DEFAULT_VALUE;
        Rack result =
            new Rack(name, datacenter, vlan_id_min, vlan_id_max, vlan_per_vdc_expected, nrsq);
        result.setVlansIdAvoided(vlans_id_avoided);
        result.setShortDescription(shortDescription);
        result.setLongDescription(longDescription);

        return result;
    }

    public Rack createInstanceDefaultNetwork(final Datacenter datacenter, final String name)
    {
        int seed = nextSeed();
        final String shortDescription =
            newString(seed, Rack.SHORT_DESCRIPTION_LENGTH_MIN, Rack.SHORT_DESCRIPTION_LENGTH_MAX);
        final String longDescription =
            newString(seed, Rack.LONG_DESCRIPTION_LENGTH_MIN, Rack.LONG_DESCRIPTION_LENGTH_MAX);

        Integer vlan_id_min = Rack.VLAN_ID_MIN_DEFAULT_VALUE;
        Integer vlan_id_max = Rack.VLAN_ID_MAX_DEFAULT_VALUE;
        Integer nrsq = Rack.NRSQ_DEFAULT_VALUE;
        String vlans_id_avoided = Rack.VLANS_ID_AVOIDED_DEFAULT_VALUE;
        Integer vlan_per_vdc_expected = Rack.VLAN_PER_VDC_EXPECTED_DEFAULT_VALUE;

        Rack result =
            new Rack(name, datacenter, vlan_id_min, vlan_id_max, vlan_per_vdc_expected, nrsq);
        result.setVlansIdAvoided(vlans_id_avoided);
        result.setShortDescription(shortDescription);
        result.setLongDescription(longDescription);

        return result;
    }

    @Override
    public void addAuxiliaryEntitiesToPersist(final Rack entity,
        final List<Object> entitiesToPersist)
    {
        super.addAuxiliaryEntitiesToPersist(entity, entitiesToPersist);

        Datacenter datacenter = entity.getDatacenter();

        this.datacenterGenerator.addAuxiliaryEntitiesToPersist(datacenter, entitiesToPersist);
        entitiesToPersist.add(entity.getDatacenter());
    }

}
