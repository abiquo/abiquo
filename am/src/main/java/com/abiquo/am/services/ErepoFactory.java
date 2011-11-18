package com.abiquo.am.services;

import java.util.HashMap;
import java.util.Map;

public class ErepoFactory
{
    /** Immutable singelton instances base on its Enterprise Identifier. */
    private static Map<String, EnterpriseRepositoryService> enterpriseHandlers =
        new HashMap<String, EnterpriseRepositoryService>();

    /**
     * Factory method, maitains a single object reference for each enterprise identifier.
     */
    public static synchronized EnterpriseRepositoryService getRepo(final String erId)
    {
        if (!enterpriseHandlers.containsKey(erId))
        {
            enterpriseHandlers.put(erId, new EnterpriseRepositoryService(erId));
        }

        return enterpriseHandlers.get(erId);
    }

    /** recreate the repository index */
    public static void refreshRepo(final String erId)
    {
        enterpriseHandlers.remove(erId);
        getRepo(erId);
    }
}
