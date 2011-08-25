  package com.abiquo.server.core.appslibrary;

  import com.abiquo.server.core.common.DefaultEntityTestBase;
  import com.softwarementors.bzngine.entities.test.InstanceTester;

  public class OVFPackageTest extends DefaultEntityTestBase<OVFPackage>
  {

      @Override
      protected InstanceTester<OVFPackage> createEntityInstanceGenerator()
      {
          return new OVFPackageGenerator(getSeed());
      }
  }
