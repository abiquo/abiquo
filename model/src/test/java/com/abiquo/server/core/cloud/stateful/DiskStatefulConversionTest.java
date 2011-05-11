  package com.abiquo.server.core.cloud.stateful;

  import com.abiquo.server.core.common.DefaultEntityTestBase;
  import com.softwarementors.bzngine.entities.test.InstanceTester;

  public class DiskStatefulConversionTest extends DefaultEntityTestBase<DiskStatefulConversion>
  {

      @Override
      protected InstanceTester<DiskStatefulConversion> createEntityInstanceGenerator()
      {
          return new DiskStatefulConversionGenerator(getSeed());
      }
  }
