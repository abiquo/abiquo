package com.abiquo.model.redis;

public class RedisEntityUtils
{
    public static long getUnixtime()
    {
        return System.currentTimeMillis() / 1000L;
    }

    public static String getEntityKey(Class< ? > clazz, String id)
    {
        KeyMaker maker = new KeyMaker(clazz.getSimpleName());
        return maker.make(id);
    }
}
