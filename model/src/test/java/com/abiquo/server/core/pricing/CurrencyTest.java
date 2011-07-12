  package com.abiquo.server.core.pricing;

  import com.abiquo.server.core.common.DefaultEntityTestBase;
  import com.softwarementors.bzngine.entities.test.InstanceTester;

  public class CurrencyTest extends DefaultEntityTestBase<Currency>
  {

      @Override
      protected InstanceTester<Currency> createEntityInstanceGenerator()
      {
          return new CurrencyGenerator(getSeed());
      }
  }
