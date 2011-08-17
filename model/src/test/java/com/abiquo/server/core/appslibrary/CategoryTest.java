  package com.abiquo.server.core.appslibrary;

  import com.abiquo.server.core.common.DefaultEntityTestBase;
  import com.softwarementors.bzngine.entities.test.InstanceTester;

  public class CategoryTest extends DefaultEntityTestBase<Category>
  {

      @Override
      protected InstanceTester<Category> createEntityInstanceGenerator()
      {
          return new CategoryGenerator(getSeed());
      }
  }
