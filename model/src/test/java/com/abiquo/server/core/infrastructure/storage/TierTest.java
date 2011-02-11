  package com.abiquo.server.core.infrastructure.storage;

  import com.abiquo.server.core.common.DefaultEntityTestBase;
  import com.softwarementors.bzngine.entities.test.InstanceTester;

  public class TierTest extends DefaultEntityTestBase<Tier>
  {

      @Override
      protected InstanceTester<Tier> createEntityInstanceGenerator()
      {
          return new TierGenerator(getSeed());
      }
  }
