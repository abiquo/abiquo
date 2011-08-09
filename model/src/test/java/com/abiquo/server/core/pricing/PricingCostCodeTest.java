  package com.abiquo.server.core.pricing;

  import com.abiquo.server.core.common.DefaultEntityTestBase;
  import com.softwarementors.bzngine.entities.test.InstanceTester;

  public class PricingCostCodeTest extends DefaultEntityTestBase<PricingCostCode>
  {

      @Override
      protected InstanceTester<PricingCostCode> createEntityInstanceGenerator()
      {
          return new PricingCostCodeGenerator(getSeed());
      }
  }
