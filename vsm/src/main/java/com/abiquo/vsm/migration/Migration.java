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

package com.abiquo.vsm.migration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.abiquo.vsm.model.PhysicalMachine;
import com.abiquo.vsm.model.VirtualMachine;
import com.abiquo.vsm.model.VirtualMachinesCache;
import com.abiquo.vsm.redis.dao.RedisDao;
import com.abiquo.vsm.redis.dao.RedisDaoFactory;

public class Migration
{
    private final static Logger logger = LoggerFactory.getLogger(Migration.class);

    private String host;

    private int port;

    private int database;

    public Migration(String host, int port, int database)
    {
        this.host = host;
        this.port = port;
        this.database = database;
    }

    public void migrate()
    {
        RedisWrapper wrapper = new RedisWrapper(host, port, database);
        RedisDao dao = RedisDaoFactory.getInstance();

        int migratedMachines = 0;
        int migratedSubscriptions = 0;

        for (String id : wrapper.getAllSubscriptionIds())
        {
            String address = wrapper.getHypervisorUrl(id);
            String type = wrapper.getHypervisorType(id);
            String username = wrapper.getUser(id);
            String password = wrapper.getPassword(id);
            String virtualMachineName = wrapper.getVirtualSystemId(id);

            PhysicalMachine machine = dao.findPhysicalMachineByAddress(address);

            if (machine == null)
            {
                VirtualMachinesCache cache = new VirtualMachinesCache();
                dao.save(cache);

                machine = new PhysicalMachine();

                machine.setAddress(address);
                machine.setType(type);
                machine.setUsername(username);
                machine.setPassword(password);
                machine.setVirtualMachines(cache);

                dao.save(machine);

                logger.info("Physical machine migrated: {} {}", address, type);
                migratedMachines++;
            }

            VirtualMachine virtualMachine = dao.findVirtualMachineByName(virtualMachineName);

            if (virtualMachine == null)
            {
                virtualMachine = new VirtualMachine();
                virtualMachine.setName(virtualMachineName);
                virtualMachine.setPhysicalMachine(machine);

                dao.save(virtualMachine);

                logger.info("Subscription migrated: {}", virtualMachineName);
                migratedSubscriptions++;
            }

            wrapper.deleteSubscription(id);
        }

        logger.info("Number of migrated physical machines: {}", migratedMachines);
        logger.info("Number of migrated subscriptions: {}", migratedSubscriptions);
    }

    public static void main(String[] args)
    {
        String host = getProperty("abiquo.redis.host", "localhost");
        int port = Integer.valueOf(getProperty("abiquo.redis.port", "6379"));

        logger.info("Migrating from 1.6.8 to 1.7 data model on redis located at {}:{}", host, port);

        new Migration(host, port, 0).migrate();
        System.exit(0);
    }

    private static String getProperty(String name, String defaultValue)
    {
        String value = System.getProperty(name);
        return value == null ? defaultValue : value;
    }
}
