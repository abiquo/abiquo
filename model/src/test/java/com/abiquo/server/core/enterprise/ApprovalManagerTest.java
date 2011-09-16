  package com.abiquo.server.core.enterprise;

  import com.abiquo.server.core.common.DefaultEntityTestBase;
  import com.softwarementors.bzngine.entities.test.InstanceTester;

  public class ApprovalManagerTest extends DefaultEntityTestBase<ApprovalManager>
  {

      @Override
      protected InstanceTester<ApprovalManager> createEntityInstanceGenerator()
      {
          return new ApprovalManagerGenerator(getSeed());
      }
  }
