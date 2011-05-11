  package com.abiquo.server.core.cloud.stateful;

  import com.abiquo.server.core.common.DefaultEntityTestBase;
  import com.softwarementors.bzngine.entities.test.InstanceTester;

  public class NodeVirtualImageStatefulConversionTest extends DefaultEntityTestBase<NodeVirtualImageStatefulConversion>
  {

      @Override
      protected InstanceTester<NodeVirtualImageStatefulConversion> createEntityInstanceGenerator()
      {
          return new NodeVirtualImageStatefulConversionGenerator(getSeed());
      }
  }
