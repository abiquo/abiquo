  package com.abiquo.server.core.pricing;

  import com.abiquo.server.core.common.DefaultEntityTestBase;
  import com.softwarementors.bzngine.entities.test.InstanceTester;

  public class CostCodeCurrencyTest extends DefaultEntityTestBase<CostCodeCurrency>
  {

      @Override
      protected InstanceTester<CostCodeCurrency> createEntityInstanceGenerator()
      {
          return new CostCodeCurrencyGenerator(getSeed());
      }
  }
