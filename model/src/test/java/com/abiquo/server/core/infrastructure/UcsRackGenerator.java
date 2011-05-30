package com.abiquo.server.core.infrastructure;

import static org.testng.Assert.assertEquals;

import java.util.List;
import java.util.Random;

import com.abiquo.server.core.common.DefaultEntityGenerator;
import com.softwarementors.commons.test.SeedGenerator;
import com.softwarementors.commons.testng.AssertEx;

public class UcsRackGenerator extends DefaultEntityGenerator<UcsRack>
{

    private DatacenterGenerator datacenterGenerator;

    public UcsRackGenerator(SeedGenerator seed)
    {
        super(seed);
        this.datacenterGenerator = new DatacenterGenerator(seed);
    }

    @Override
    public void assertAllPropertiesEqual(UcsRack obj1, UcsRack obj2)
    {
        AssertEx.assertPropertiesEqualSilent(obj1, obj2, UcsRack.PORT_PROPERTY,
            UcsRack.IP_PROPERTY, UcsRack.PASSWORD_PROPERTY, UcsRack.USER_PROPERTY);
    }

    @Override
    public UcsRack createUniqueInstance()
    {
        Datacenter datacenter = this.datacenterGenerator.createUniqueInstance();   
        return createInstance(datacenter);
    }
    
    public UcsRack createInstance(Datacenter datacenter)
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
                "password");
        ucsRack.setVlansIdAvoided(vlans_id_avoided);
        ucsRack.setShortDescription(shortDescription);
        ucsRack.setLongDescription(longDescription);

        return ucsRack;
    }

    @Override
    public void addAuxiliaryEntitiesToPersist(UcsRack entity, List<Object> entitiesToPersist)
    {
        super.addAuxiliaryEntitiesToPersist(entity, entitiesToPersist);
        
        Datacenter datacenter = entity.getDatacenter();

        this.datacenterGenerator.addAuxiliaryEntitiesToPersist(datacenter, entitiesToPersist);
        entitiesToPersist.add(entity.getDatacenter());

    }

}
