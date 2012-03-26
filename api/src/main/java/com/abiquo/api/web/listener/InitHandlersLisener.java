package com.abiquo.api.web.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.springframework.web.context.support.WebApplicationContextUtils;

import com.abiquo.api.handlers.SecurityRequestHandler;
import com.abiquo.api.services.UserService;
import com.abiquo.api.services.cloud.VirtualDatacenterService;

public class InitHandlersLisener implements ServletContextListener
{
    @Override
    public void contextInitialized(final ServletContextEvent sce)
    {
        SecurityRequestHandler srHandler = SecurityRequestHandler.getInstance();
        srHandler.setUserService(WebApplicationContextUtils.getRequiredWebApplicationContext(
            sce.getServletContext()).getBean("userService", UserService.class));
        srHandler.setVirtualDatacenterService(WebApplicationContextUtils
            .getRequiredWebApplicationContext(sce.getServletContext()).getBean(
                "virtualDatacenterService", VirtualDatacenterService.class));
    }

    @Override
    public void contextDestroyed(final ServletContextEvent sce)
    {
        // Nothing to do
    }
}
