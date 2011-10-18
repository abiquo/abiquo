  package com.abiquo.server.core.cloud;

  import com.abiquo.server.core.common.DefaultEntityTestBase;
  import com.softwarementors.bzngine.entities.test.InstanceTester;

  public class VirtualImageConversionTest extends DefaultEntityTestBase<VirtualImageConversion>
  {

      @Override
      protected InstanceTester<VirtualImageConversion> createEntityInstanceGenerator()
      {
          return new VirtualImageConversionGenerator(getSeed());
      }
  }
