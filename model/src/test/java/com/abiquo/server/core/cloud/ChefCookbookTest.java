  package com.abiquo.server.core.cloud;

  import com.abiquo.server.core.common.DefaultEntityTestBase;
  import com.softwarementors.bzngine.entities.test.InstanceTester;

  public class ChefCookbookTest extends DefaultEntityTestBase<ChefCookbook>
  {

      @Override
      protected InstanceTester<ChefCookbook> createEntityInstanceGenerator()
      {
          return new ChefCookbookGenerator(getSeed());
      }
  }
