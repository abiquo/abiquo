package com.abiquo.api.handlers;

import java.util.Properties;

import org.apache.wink.server.handlers.HandlersChain;
import org.apache.wink.server.handlers.MessageContext;
import org.apache.wink.server.handlers.RequestHandler;

import com.abiquo.api.services.UserService;
import com.abiquo.api.services.cloud.VirtualDatacenterService;

public class SecurityRequestHandler implements RequestHandler
{
    private UserService userService;

    private VirtualDatacenterService virtualDatacenterService;

    private static SecurityRequestHandler instance;

    @Override
    public void init(final Properties props)
    {
        instance = this;
    }

    @Override
    public void handleRequest(final MessageContext context, final HandlersChain chain)
        throws Throwable
    {
        // To override
    }

    public static SecurityRequestHandler getInstance()
    {
        return instance;
    }

    public UserService getUserService()
    {
        return userService;
    }

    public void setUserService(final UserService userService)
    {
        this.userService = userService;
    }

    public VirtualDatacenterService getVirtualDatacenterService()
    {
        return virtualDatacenterService;
    }

    public void setVirtualDatacenterService(final VirtualDatacenterService virtualDatacenterService)
    {
        this.virtualDatacenterService = virtualDatacenterService;
    }
}
