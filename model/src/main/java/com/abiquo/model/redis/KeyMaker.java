package com.abiquo.model.redis;

/**
 * Helper class to build keys for redis models.
 * 
 * @author eruiz
 */
public class KeyMaker
{
    protected String namespace;

    public KeyMaker(String namespace)
    {
        this.namespace = namespace;
    }

    public KeyMaker(Class< ? > clazz)
    {
        this.namespace = clazz.getSimpleName();
    }

    public String make(String... namespaces)
    {
        StringBuilder builder = new StringBuilder(this.namespace);

        for (String name : namespaces)
        {
            builder.append(":").append(name);
        }

        return builder.toString();
    }
}
