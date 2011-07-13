package com.abiquo.server.core.pricing;

import static org.testng.Assert.assertEquals;

import java.util.List;

import com.abiquo.server.core.common.DefaultEntityGenerator;
import com.softwarementors.commons.test.SeedGenerator;
import com.softwarementors.commons.testng.AssertEx;

public class PricingGenerator extends DefaultEntityGenerator<Pricing>
{

    
      N1Generator n1Generator;
    
      N1Generator n1Generator;
    

    public PricingGenerator(SeedGenerator seed)
    {
        super(seed);
        
          n1Generator = new N1Generator(seed);
        
          n1Generator = new N1Generator(seed);
        
    }

    @Override
    public void assertAllPropertiesEqual(Pricing obj1, Pricing obj2)
    {
      AssertEx.assertPropertiesEqualSilent(obj1, obj2, Pricing.STANDING_CHARGE_PERIOD_PROPERTY,Pricing.LIMIT_MAXIMUM_DEPLOYED_CHARGED_PROPERTY,Pricing.VLAN_PROPERTY,Pricing.SHOW_MINIMUM_CHARGE_PROPERTY,Pricing.CHARGING_PERIOD_PROPERTY,Pricing.MINIMUM_CHARGE_PERIOD_PROPERTY,Pricing.MINIMUM_CHARGE_PROPERTY,Pricing.SHOW_CHANGES_BEFORE_DEPLOYEMENT_PROPERTY,Pricing.PUBLIC_IP_PROPERTY,Pricing.V_CPU_PROPERTY,Pricing.MEMORY_MB_PROPERTY);
    }

    @Override
    public Pricing createUniqueInstance()
    {
        // FIXME: Write here how to create the pojo

        Pricing pricing = new Pricing();

        
        N1 n1 = n1Generator.createUniqueInstance();
        pricing.setN1(n1);
        
        N1 n1 = n1Generator.createUniqueInstance();
        pricing.setN1(n1);
        

        return pricing;
    }

    @Override
    public void addAuxiliaryEntitiesToPersist(Pricing entity, List<Object> entitiesToPersist)
    {
        super.addAuxiliaryEntitiesToPersist(entity, entitiesToPersist);
        
        
          N1 n1 = entity.getN1();
          n1Generator.addAuxiliaryEntitiesToPersist(n1, entitiesToPersist);
          entitiesToPersist.add(n1);
        
          N1 n1 = entity.getN1();
          n1Generator.addAuxiliaryEntitiesToPersist(n1, entitiesToPersist);
          entitiesToPersist.add(n1);
        
    }

}
