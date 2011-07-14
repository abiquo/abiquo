  package com.abiquo.server.core.pricing;

  import com.abiquo.server.core.common.DefaultEntityTestBase;
  import com.softwarementors.bzngine.entities.test.InstanceTester;

  public class PricingTemplateTest extends DefaultEntityTestBase<PricingTemplate>
  {

      @Override
      protected InstanceTester<PricingTemplate> createEntityInstanceGenerator()
      {
          return new PricingTemplateGenerator(getSeed());
      }
  }
