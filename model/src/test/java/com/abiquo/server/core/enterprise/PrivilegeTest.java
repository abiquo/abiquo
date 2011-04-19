  package com.abiquo.server.core.enterprise;

  import com.abiquo.server.core.common.DefaultEntityTestBase;
  import com.softwarementors.bzngine.entities.test.InstanceTester;

  public class PrivilegeTest extends DefaultEntityTestBase<Privilege>
  {

      @Override
      protected InstanceTester<Privilege> createEntityInstanceGenerator()
      {
          return new PrivilegeGenerator(getSeed());
      }
  }
