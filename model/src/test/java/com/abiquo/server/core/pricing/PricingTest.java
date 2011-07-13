  package com.abiquo.server.core.pricing;

  import com.abiquo.server.core.common.DefaultEntityTestBase;
  import com.softwarementors.bzngine.entities.test.InstanceTester;

  public class PricingTest extends DefaultEntityTestBase<Pricing>
  {

      @Override
      protected InstanceTester<Pricing> createEntityInstanceGenerator()
      {
          return new PricingGenerator(getSeed());
      }
  }
