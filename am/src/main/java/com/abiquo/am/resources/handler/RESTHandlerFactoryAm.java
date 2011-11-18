package com.abiquo.am.resources.handler;

import java.util.Arrays;
import java.util.List;

import org.apache.wink.server.handlers.HandlersFactory;
import org.apache.wink.server.handlers.RequestHandler;

/**
 * Adds the {@link CheckRepositoryHandler} as {@link RequestHandler}
 */
public class RESTHandlerFactoryAm extends HandlersFactory
{
    @Override
    public List< ? extends RequestHandler> getRequestHandlers()
    {
        return Arrays.asList(new CheckRepositoryHandler());
    }
}
