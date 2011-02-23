package com.abiquo.server.core.infrastructure.storage;

import com.abiquo.server.core.common.GenericEntityTestBase;
import com.softwarementors.bzngine.entities.test.InstanceTester;

public class StoragePoolTest extends GenericEntityTestBase<String, StoragePool>
{

    @Override
    protected InstanceTester<StoragePool> createEntityInstanceGenerator()
    {
        return new StoragePoolGenerator(getSeed());
    }
}
