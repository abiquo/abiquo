/**
 * Abiquo community edition
 * cloud management application for hybrid clouds
 * Copyright (C) 2008-2010 - Abiquo Holdings S.L.
 *
 * This application is free software; you can redistribute it and/or
 * modify it under the terms of the GNU LESSER GENERAL PUBLIC
 * LICENSE as published by the Free Software Foundation under
 * version 3 of the License
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * LESSER GENERAL PUBLIC LICENSE v.3 for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 */

package com.abiquo.api.web.filters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.abiquo.tracer.ComponentType;
import com.abiquo.tracer.EventType;
import com.abiquo.tracer.Platform;
import com.abiquo.tracer.SeverityType;
import com.abiquo.tracer.client.TracerFactory;

/**
 * Traces Request, Response and Exception thrown by API Resources.
 * 
 * @author eruiz
 */
public class TraceFilter implements Filter
{
    /** The logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(TraceFilter.class);

    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response,
        final FilterChain chain) throws IOException, ServletException
    {
        HttpServletRequest req = (HttpServletRequest) request;

        // Encapsulate response to keep trace of status code
        StatusExposingServletResponse res =
            new StatusExposingServletResponse((HttpServletResponse) response);

        // Code before servlet
        traceRequest(req.getMethod(), req.getRequestURI(), req.getQueryString());

        chain.doFilter(request, res);

        // Code after servlet
        traceResponse(req.getMethod(), req.getRequestURI(), req.getQueryString(), res.getStatus());
    }

    /**
     * Logs the incoming API request in the log files and tracer.
     * 
     * @param method Represents a HTTP Method (like GET)
     * @param path Request's URL
     * @param query Query string contained in request's URL
     */
    private void traceRequest(final String method, final String path, final String query)
    {
        String message = String.format("Method: %s, Path: %s, Query: %s", method, path, query);

        LOGGER.trace("Incoming API request. " + message);
        // traceInfo(EventType.API_REQUEST, message);
    }

    /**
     * Logs the outcoming API request in the log files and tracer.
     * 
     * @param method Represents a HTTP Method (like GET)
     * @param path Request's URL
     * @param query Query string contained in request's URL
     * @param status The HTTP response code
     */
    private void traceResponse(final String method, final String path, final String query,
        final int status)
    {
        String message =
            String.format("Method: %s, Path: %s, Query: %s, Status code: %d", method, path, query,
                status);

        LOGGER.trace("Outcoming API request. " + message);
        // traceInfo(EventType.API_RESPONSE, message);
    }

    /**
     * Logs a trace in the Tracer component.
     * 
     * @param event An EventType field
     * @param message The message to log
     */
    private void traceInfo(final EventType event, final String message)
    {
        TracerFactory.getTracer().log(SeverityType.INFO, ComponentType.API, event, message,
            Platform.SYSTEM_PLATFORM);
    }

    @Override
    public void init(final FilterConfig config) throws ServletException
    {
        LOGGER.info("TraceFilter loaded");
    }

    @Override
    public void destroy()
    {
        LOGGER.info("TraceFilter destroyed");
    }

    /**
     * HttpServletResponseWrapper to encapsulate response to keep trace of status code.
     */
    private static class StatusExposingServletResponse extends HttpServletResponseWrapper
    {
        private int httpStatus;

        public StatusExposingServletResponse(final HttpServletResponse response)
        {
            super(response);
        }

        @Override
        public void sendError(final int sc) throws IOException
        {
            httpStatus = sc;
            super.sendError(sc);
        }

        @Override
        public void sendError(final int sc, final String msg) throws IOException
        {
            httpStatus = sc;
            super.sendError(sc, msg);
        }

        @Override
        public void setStatus(final int sc)
        {
            httpStatus = sc;
            super.setStatus(sc);
        }

        public int getStatus()
        {
            return httpStatus;
        }
    }
}
