  package com.abiquo.server.core.pricing;

  import com.abiquo.server.core.common.DefaultEntityTestBase;
  import com.softwarementors.bzngine.entities.test.InstanceTester;

  public class CostCodeTest extends DefaultEntityTestBase<CostCode>
  {

      @Override
      protected InstanceTester<CostCode> createEntityInstanceGenerator()
      {
          return new CostCodeGenerator(getSeed());
      }
  }
