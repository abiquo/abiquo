package com.abiquo.server.core.infrastructure.storage;

import static org.testng.Assert.assertEquals;

import java.util.List;

import com.abiquo.server.core.common.DefaultEntityGenerator;
import com.softwarementors.commons.test.SeedGenerator;
import com.softwarementors.commons.testng.AssertEx;

public class StoragePoolGenerator extends DefaultEntityGenerator<StoragePool>
{

    
      TierGenerator tierGenerator;
    
      CabinetGenerator cabinetGenerator;
    

    public StoragePoolGenerator(SeedGenerator seed)
    {
        super(seed);
        
          tierGenerator = new TierGenerator(seed);
        
          cabinetGenerator = new CabinetGenerator(seed);
        
    }

    @Override
    public void assertAllPropertiesEqual(StoragePool obj1, StoragePool obj2)
    {
      AssertEx.assertPropertiesEqualSilent(obj1, obj2, StoragePool.NAME_PROPERTY);
    }

    @Override
    public StoragePool createUniqueInstance()
    {
        StoragePool storagePool = new StoragePool();

        
        Tier tier = tierGenerator.createUniqueInstance();
        storagePool.setTier(tier);
        
        Cabinet cabinet = cabinetGenerator.createUniqueInstance();
        storagePool.setCabinet(cabinet);
        
        storagePool.setName("LoPutoStorage");
        
        return storagePool;
    }

    @Override
    public void addAuxiliaryEntitiesToPersist(StoragePool entity, List<Object> entitiesToPersist)
    {
        super.addAuxiliaryEntitiesToPersist(entity, entitiesToPersist);
        
        
          Tier tier = entity.getTier();
          tierGenerator.addAuxiliaryEntitiesToPersist(tier, entitiesToPersist);
          entitiesToPersist.add(tier);
        
          Cabinet cabinet = entity.getCabinet();
          cabinetGenerator.addAuxiliaryEntitiesToPersist(cabinet, entitiesToPersist);
          entitiesToPersist.add(cabinet);
        
    }

}
