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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.abiquo.commons.amqp.impl.vsm.VSMConfiguration;
import com.abiquo.commons.amqp.util.RabbitMQUtils;
import com.abiquo.vsm.VSMManager;
import com.abiquo.vsm.model.PhysicalMachine;
import com.abiquo.vsm.model.VirtualMachine;
import com.abiquo.vsm.redis.dao.RedisDao;
import com.abiquo.vsm.redis.dao.RedisDaoFactory;
import com.abiquo.vsm.redis.util.RedisUtils;

public class ConsoleServlet extends HttpServlet
{
    private static final long serialVersionUID = 5424477177953465654L;

    protected class PhysicalMachineComparator implements Comparator<PhysicalMachine>
    {
        @Override
        public int compare(PhysicalMachine o1, PhysicalMachine o2)
        {
            return o1.getAddress().compareTo(o2.getAddress());
        }
    }

    protected class VirtualMachineComparator implements Comparator<VirtualMachine>
    {
        @Override
        public int compare(VirtualMachine o1, VirtualMachine o2)
        {
            return o1.getName().compareTo(o2.getName());
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException
    {
        // Read configuration
        VSMManager vsmManager = VSMManager.getInstance();
        String redisHost = vsmManager.getRedisHost();
        int redisPort = vsmManager.getRedisPort();

        // Publish check status
        Map<String, Boolean> checks = new LinkedHashMap<String, Boolean>();
        checks.put("VSM check result", vsmManager.checkSystem());
        checks.put("Redis listening", RedisUtils.ping(redisHost, redisPort));
        checks.put("RabbitMQ listening", RabbitMQUtils.pingRabbitMQ());

        request.setAttribute("checks", checks);

        // Publish configuration values
        Map<String, Object> config = new LinkedHashMap<String, Object>();

        config.put("Redis host", redisHost);
        config.put("Redis port", redisPort);
        config.put("RabbitMQ host", VSMConfiguration.getInstance().getRabbitMQHost());
        config.put("RabbitMQ port", VSMConfiguration.getInstance().getRabbitMQPort());

        request.setAttribute("config", config);

        // Extended report
        if (request.getParameterMap().containsKey("extended"))
        {
            RedisDao dao = RedisDaoFactory.getInstance();

            List<PhysicalMachine> pms = new ArrayList(dao.findAllPhysicalMachines());
            Collections.sort(pms, new PhysicalMachineComparator());

            List<VirtualMachine> vms = new ArrayList(dao.findAllVirtualMachines());
            Collections.sort(vms, new VirtualMachineComparator());

            request.setAttribute("extended", Boolean.TRUE);
            request.setAttribute("pms", pms);
            request.setAttribute("vms", vms);

            // Duplicate VMs information
            Map<String, List<String>> duplicates = new HashMap<String, List<String>>();
            Set<String> allCaches = new HashSet<String>();
            for (PhysicalMachine pm : pms)
            {
                for (String cache : pm.getVirtualMachines().getCache())
                {
                    if (!allCaches.contains(cache))
                    {
                        allCaches.add(cache);
                    }
                    else
                    {
                        // Add duplicate
                        List<String> vmDuplicates = duplicates.get(cache);
                        if (vmDuplicates == null)
                        {
                            vmDuplicates = new LinkedList<String>();
                        }
                        vmDuplicates.add(pm.getAddress());
                        duplicates.put(cache, vmDuplicates);
                    }
                }
            }

            request.setAttribute("duplicates", duplicates);
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
