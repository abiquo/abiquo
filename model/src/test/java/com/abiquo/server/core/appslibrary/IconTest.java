  package com.abiquo.server.core.appslibrary;

  import com.abiquo.server.core.common.DefaultEntityTestBase;
  import com.softwarementors.bzngine.entities.test.InstanceTester;

  public class IconTest extends DefaultEntityTestBase<Icon>
  {

      @Override
      protected InstanceTester<Icon> createEntityInstanceGenerator()
      {
          return new IconGenerator(getSeed());
      }
  }
