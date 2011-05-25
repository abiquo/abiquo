package com.abiquo.vsm.resource;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

public class ResourceUtils
{
    public static String decodeParameter(final String parameter)
    {
        try
        {
            return URLDecoder.decode(parameter, "UTF-8");
        }
        catch (UnsupportedEncodingException e)
        {
            // TODO
            return parameter;
        }
    }
}
