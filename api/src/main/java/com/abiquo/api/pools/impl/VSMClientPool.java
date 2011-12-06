package com.abiquo.api.pools.impl;

import org.apache.commons.pool.BasePoolableObjectFactory;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.springframework.stereotype.Component;

import com.abiquo.vsm.client.VSMClient;

/**
 * @author enric.ruiz@abiquo.com
 */
@Component(value = "vSMClientPool")
public class VSMClientPool extends GenericObjectPool
{
    public VSMClientPool()
    {
        super(new PoolableVSMClientFactory());
    }

    public VSMClient borrowObject(String uri) throws Exception
    {
        VSMClient client = (VSMClient) super.borrowObject();
        return client.initialize(uri);
    }

    private static class PoolableVSMClientFactory extends BasePoolableObjectFactory
    {
        @Override
        public Object makeObject() throws Exception
        {
            return new VSMClient();
        }
    }
}
