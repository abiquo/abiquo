  package com.abiquo.server.core.infrastructure.network;

  import com.abiquo.server.core.common.DefaultEntityTestBase;
  import com.softwarementors.bzngine.entities.test.InstanceTester;

  public class DhcpOptionTest extends DefaultEntityTestBase<DhcpOption>
  {

      @Override
      protected InstanceTester<DhcpOption> createEntityInstanceGenerator()
      {
          return new DhcpOptionGenerator(getSeed());
      }
  }
