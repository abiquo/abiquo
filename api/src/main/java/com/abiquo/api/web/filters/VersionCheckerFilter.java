package com.abiquo.api.web.filters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.abiquo.model.transport.SingleResourceTransportDto;


/**
 * This class intercepts all the requests to the API and injects
 * the proper version parameter to content-negociation annotations.
 * 
 * @author jaume
 */
public class VersionCheckerFilter implements Filter
{

    private static final Logger LOGGER = LoggerFactory.getLogger(VersionCheckerFilter.class);
    
    @Override
    public void destroy()
    {
        LOGGER.info("VersionCheckerFilter destroyed");
    }

    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain)
        throws IOException, ServletException
    {
        
    }

    @Override
    public void init(final FilterConfig config) throws ServletException
    {
        LOGGER.info("VersionCheckerFilter loaded. Current API version is " + SingleResourceTransportDto.API_VERSION);
    }

}
