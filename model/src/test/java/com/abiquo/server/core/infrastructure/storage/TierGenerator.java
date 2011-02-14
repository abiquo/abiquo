package com.abiquo.server.core.infrastructure.storage;

import java.util.List;

import com.abiquo.server.core.common.DefaultEntityGenerator;
import com.abiquo.server.core.infrastructure.Datacenter;
import com.abiquo.server.core.infrastructure.DatacenterGenerator;
import com.softwarementors.commons.test.SeedGenerator;
import com.softwarementors.commons.testng.AssertEx;

public class TierGenerator extends DefaultEntityGenerator<Tier>
{

    
      DatacenterGenerator datacenterGenerator;
    

    public TierGenerator(SeedGenerator seed)
    {
        super(seed);
        
          datacenterGenerator = new DatacenterGenerator(seed);
        
    }

    @Override
    public void assertAllPropertiesEqual(Tier obj1, Tier obj2)
    {
      AssertEx.assertPropertiesEqualSilent(obj1, obj2, Tier.NAME_PROPERTY,Tier.ENABLED_PROPERTY,Tier.DESCRIPTION_PROPERTY);
    }

    @Override
    public Tier createUniqueInstance()
    {
        // FIXME: Write here how to create the pojo

        Tier tier = new Tier();

        
        Datacenter datacenter = datacenterGenerator.createUniqueInstance();
        tier.setDatacenter(datacenter);
        tier.setName("LoPutoTier");
        tier.setDescription("LoPutoTier Description");
        tier.setEnabled(true);
        return tier;
    }

    @Override
    public void addAuxiliaryEntitiesToPersist(Tier entity, List<Object> entitiesToPersist)
    {
        super.addAuxiliaryEntitiesToPersist(entity, entitiesToPersist);
        
        
          Datacenter datacenter = entity.getDatacenter();
          datacenterGenerator.addAuxiliaryEntitiesToPersist(datacenter, entitiesToPersist);
          entitiesToPersist.add(datacenter);
        
    }

}
