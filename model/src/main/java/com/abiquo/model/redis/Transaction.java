package com.abiquo.model.redis;

import java.util.List;

public class Transaction extends redis.clients.jedis.Transaction
{
    protected boolean discardTransaction = true;

    @Override
    public List<Object> exec()
    {
        List<Object> result = super.exec();
        discardTransaction = false;
        return result;
    }

    public void discardIfNeeded()
    {
        if (discardTransaction)
        {
            super.discard();
        }
    }
}
