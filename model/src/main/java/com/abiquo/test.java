package com.abiquo;

import java.util.HashMap;
import java.util.Map;

import com.abiquo.server.core.enterprise.EnterpriseProperties;

public class test
{

    /**
     * @param args
     */
    public static void main(String[] args)
    {
        EnterpriseProperties ep = new EnterpriseProperties();
        Map<String, String> map = new HashMap<String, String>();
        map.put("Support e-mail", "support@abiquo.com");

        ep.setProperties(map);

        ep.isValid();

    }
}
