package com.abiquo.server.core.enterprise;

import com.abiquo.server.core.common.DefaultEntityTestBase;
import com.softwarementors.bzngine.entities.test.InstanceTester;

public class ApprovalTest extends DefaultEntityTestBase<Approval>
{

    @Override
    protected InstanceTester<Approval> createEntityInstanceGenerator()
    {
        return new ApprovalGenerator(getSeed());
    }
}
