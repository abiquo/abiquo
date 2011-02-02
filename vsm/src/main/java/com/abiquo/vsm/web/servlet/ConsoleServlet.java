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

package com.abiquo.vsm.web.servlet;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.abiquo.commons.amqp.impl.vsm.VSMConfiguration;
import com.abiquo.commons.amqp.util.RabbitMQUtils;
import com.abiquo.vsm.VSMManager;
import com.abiquo.vsm.redis.dao.RedisDao;
import com.abiquo.vsm.redis.dao.RedisDaoFactory;
import com.abiquo.vsm.redis.util.RedisUtils;

public class ConsoleServlet extends HttpServlet
{
    private static final long serialVersionUID = 5424477177953465654L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException
    {
        // Read configuration
        VSMManager vsmManager = VSMManager.getInstance();
        String redisHost = vsmManager.getRedisHost();
        int redisPort = vsmManager.getRedisPort();

        // Publish check status
        Map<String, Boolean> checks = new HashMap<String, Boolean>();
        checks.put("VSM check result", vsmManager.checkSystem());
        checks.put("Redis listening", RedisUtils.ping(redisHost, redisPort));
        checks.put("RabbitMQ listening", RabbitMQUtils.pingRabbitMQ());

        // Publish configuration values
        Map<String, Object> config = new TreeMap<String, Object>(String.CASE_INSENSITIVE_ORDER);

        config.put("Redis host", vsmManager.getRedisHost());
        config.put("Redis port", vsmManager.getRedisPort());

        config.put("RabbitMQ host", VSMConfiguration.getInstance().getRabbitMQHost());
        config.put("RabbitMQ port", VSMConfiguration.getInstance().getRabbitMQPort());

        request.setAttribute("checks", checks);
        request.setAttribute("config", config);

        // Extended report
        if (request.getParameterMap().containsKey("extended"))
        {
            RedisDao dao = RedisDaoFactory.getInstance();
            request.setAttribute("extended", Boolean.TRUE);
            request.setAttribute("pms", dao.findAllPhysicalMachines());
            request.setAttribute("vms", dao.findAllVirtualMachines());
        }

        getServletContext().getRequestDispatcher("/jsp/console.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException
    {
        doGet(req, resp);
    }
}
