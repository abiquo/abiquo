  package com.abiquo.server.core.infrastructure;

  import com.abiquo.server.core.common.DefaultEntityTestBase;
  import com.softwarementors.bzngine.entities.test.InstanceTester;

  public class UcsRackTest extends DefaultEntityTestBase<UcsRack>
  {

      @Override
      protected InstanceTester<UcsRack> createEntityInstanceGenerator()
      {
          return new UcsRackGenerator(getSeed());
      }
  }
