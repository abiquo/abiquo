  package com.abiquo.server.core.appslibrary;

  import com.abiquo.server.core.common.DefaultEntityTestBase;
  import com.softwarementors.bzngine.entities.test.InstanceTester;

  public class OVFPackageListTest extends DefaultEntityTestBase<OVFPackageList>
  {

      @Override
      protected InstanceTester<OVFPackageList> createEntityInstanceGenerator()
      {
          return new OVFPackageListGenerator(getSeed());
      }
  }
