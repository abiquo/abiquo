  package com.abiquo.server.core.enterprise;

  import com.abiquo.server.core.common.DefaultEntityTestBase;
  import com.softwarementors.bzngine.entities.test.InstanceTester;

  public class RoleLdapTest extends DefaultEntityTestBase<RoleLdap>
  {

      @Override
      protected InstanceTester<RoleLdap> createEntityInstanceGenerator()
      {
          return new RoleLdapGenerator(getSeed());
      }
  }
