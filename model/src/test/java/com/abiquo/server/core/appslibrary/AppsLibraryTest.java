  package com.abiquo.server.core.appslibrary;

  import com.abiquo.server.core.common.DefaultEntityTestBase;
  import com.softwarementors.bzngine.entities.test.InstanceTester;

  public class AppsLibraryTest extends DefaultEntityTestBase<AppsLibrary>
  {

      @Override
      protected InstanceTester<AppsLibrary> createEntityInstanceGenerator()
      {
          return new AppsLibraryGenerator(getSeed());
      }
  }
