  package com.abiquo.server.core.cloud.stateful;

  import com.abiquo.server.core.common.DefaultEntityTestBase;
  import com.softwarementors.bzngine.entities.test.InstanceTester;

  public class VirtualApplicanceStatefulConversionTest extends DefaultEntityTestBase<VirtualApplicanceStatefulConversion>
  {

      @Override
      protected InstanceTester<VirtualApplicanceStatefulConversion> createEntityInstanceGenerator()
      {
          return new VirtualApplicanceStatefulConversionGenerator(getSeed());
      }
  }
