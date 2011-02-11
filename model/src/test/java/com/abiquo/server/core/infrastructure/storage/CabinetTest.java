  package com.abiquo.server.core.infrastructure.storage;

  import com.abiquo.server.core.common.DefaultEntityTestBase;
  import com.softwarementors.bzngine.entities.test.InstanceTester;

  public class CabinetTest extends DefaultEntityTestBase<Cabinet>
  {

      @Override
      protected InstanceTester<Cabinet> createEntityInstanceGenerator()
      {
          return new CabinetGenerator(getSeed());
      }
  }
