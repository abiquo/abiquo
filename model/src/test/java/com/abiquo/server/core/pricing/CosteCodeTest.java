  package com.abiquo.server.core.pricing;

  import com.abiquo.server.core.common.DefaultEntityTestBase;
  import com.softwarementors.bzngine.entities.test.InstanceTester;

  public class CosteCodeTest extends DefaultEntityTestBase<CosteCode>
  {

      @Override
      protected InstanceTester<CosteCode> createEntityInstanceGenerator()
      {
          return new CosteCodeGenerator(getSeed());
      }
  }
