package com.abiquo.server.core.enterprise;

import com.abiquo.server.core.common.DefaultEntityTestBase;
import com.softwarementors.bzngine.entities.test.InstanceTester;

public class OneTimeTokenSessionTest extends DefaultEntityTestBase<OneTimeTokenSession>
{
    /**
     * @see com.softwarementors.bzngine.entities.test.EntityTestBase#createEntityInstanceGenerator()
     */
    @Override
    protected InstanceTester<OneTimeTokenSession> createEntityInstanceGenerator()
    {
        return new OneTimeTokenSessionGenerator(getSeed());
    }
}
