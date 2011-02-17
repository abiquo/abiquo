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

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;

import com.abiquo.vsm.model.PhysicalMachine;
import com.abiquo.vsm.model.VirtualMachine;
import com.abiquo.vsm.model.VirtualMachinesCache;
import com.abiquo.vsm.monitor.Monitor.Type;
import com.abiquo.vsm.redis.dao.RedisDao;
import com.abiquo.vsm.redis.dao.RedisDaoFactory;

public class Migrator
{
    private final static Logger logger = LoggerFactory.getLogger(Migrator.class);

    public final static String MachineListKey = "tomigrate";

    private String host;

    private int port;

    private int database;

    private int machinesCount;

    private int subscriptionsCount;

    private Map<String, Integer> ports;

    private Map<String, Type> hypervisors;

    public Migrator(String host, int port, int database)
    {
        this.host = host;
        this.port = port;
        this.database = database;

        this.machinesCount = 0;
        this.subscriptionsCount = 0;

        this.ports = new HashMap<String, Integer>();
        this.ports.put(Type.VMX_04.name(), 443);
        this.ports.put(Type.HYPERV_301.name(), 5985);
        this.ports.put(Type.KVM.name(), 8889);
        this.ports.put(Type.VBOX.name(), 8889);
        this.ports.put(Type.XEN_3.name(), 8889);
        this.ports.put(Type.XENSERVER.name(), 9363);

        this.hypervisors = new HashMap<String, Type>();
        this.hypervisors.put("vmx-04", Type.VMX_04);
        this.hypervisors.put("xenserver", Type.XENSERVER);
        this.hypervisors.put("xen-3", Type.XEN_3);
        this.hypervisors.put("hyperv-301", Type.HYPERV_301);
    }

    public void migratePersistedModel()
    {
        RedisWrapper wrapper = new RedisWrapper(host, port, database);
        RedisDao dao = RedisDaoFactory.getInstance();

        for (String id : wrapper.getAllSubscriptionIds())
        {
            String address = wrapper.getHypervisorUrl(id);
            String type = wrapper.getHypervisorType(id);
            String username = wrapper.getUser(id);
            String password = wrapper.getPassword(id);
            String virtualMachineName = wrapper.getVirtualSystemId(id);

            PhysicalMachine machine = insertMachine(dao, address, type, username, password);

            if (machine != null)
            {
                VirtualMachine virtualMachine = dao.findVirtualMachineByName(virtualMachineName);

                if (virtualMachine == null)
                {
                    virtualMachine = new VirtualMachine();
                    virtualMachine.setName(virtualMachineName);
                    virtualMachine.setPhysicalMachine(machine);

                    dao.save(virtualMachine);

                    this.subscriptionsCount++;
                    logger.info("Subscription migrated: {}", virtualMachineName);
                }

                wrapper.deleteSubscription(id);
            }
        }
    }

    public void migrateNonPersistedModelFromFile(final File file) throws IOException
    {
        LineIterator iterator = FileUtils.lineIterator(file);
        RedisDao dao = RedisDaoFactory.getInstance();

        try
        {
            while (iterator.hasNext())
            {
                String line = iterator.nextLine();
                insertMachineFromCSVLine(dao, line);
            }
        }
        finally
        {
            LineIterator.closeQuietly(iterator);
        }
    }

    public void migrateNonPersistedModelFromRedis() throws UnknownHostException, IOException
    {
        RedisDao dao = RedisDaoFactory.getInstance();
        Jedis jedis = new Jedis(host, port);
        jedis.connect();

        jedis.select(database);

        long len = jedis.llen(MachineListKey);

        for (int i = 0; i < len; i++)
        {
            insertMachineFromCSVLine(dao, jedis.lindex(MachineListKey, i));
        }

        jedis.del(MachineListKey);

        jedis.disconnect();
    }

    private void insertMachineFromCSVLine(final RedisDao dao, final String csvLine)
    {
        String[] fields = csvLine.split(",");

        if (fields.length == 5)
        {
            String ip = fields[0];
            String port = fields[1];
            String user = fields[2];
            String pass = fields[3];
            String type = fields[4];

            insertMachine(dao, String.format("http://%s:%s/", ip, port), type, user, pass);
        }
    }

    private PhysicalMachine insertMachine(final RedisDao dao, final String address,
        final String type, final String username, final String password)
    {
        PhysicalMachine machine = dao.findPhysicalMachineByAddress(address);

        if (machine == null)
        {
            try
            {
                URL url = new URL(address);

                String finalType = type;

                if (this.hypervisors.containsKey(type))
                {
                    finalType = this.hypervisors.get(type).name();
                }

                String finalURL =
                    String.format("http://%s:%s/", url.getHost(), this.ports.get(finalType));

                machine = dao.findPhysicalMachineByAddress(finalURL);

                if (machine == null)
                {
                    VirtualMachinesCache cache = new VirtualMachinesCache();
                    dao.save(cache);

                    machine = new PhysicalMachine();

                    machine.setAddress(finalURL);
                    machine.setType(finalType);
                    machine.setUsername(username);
                    machine.setPassword(password);
                    machine.setVirtualMachines(cache);

                    dao.save(machine);

                    this.machinesCount++;
                    logger.info("Physical machine migrated: {} {}", machine.getAddress(), machine
                        .getType());
                }
            }
            catch (MalformedURLException e)
            {
                logger.error("Invalid physical machine address {}", address);
                return null;
            }

        }

        return machine;
    }

    public int getMachinesCount()
    {
        return machinesCount;
    }

    public int getSubscriptionsCount()
    {
        return subscriptionsCount;
    }

    private static Options buildOptions()
    {
        Options options = new Options();
        options.addOption("help", "help", false, "Print this usage information.");
        options.addOption("f", "file", true, "CSV file with the machines to migrate.");
        options.addOption("h", "host", true, "Redis host.");
        options.addOption("p", "port", true, "Redis port.");

        return options;
    }

    private static void printUsage()
    {
        HelpFormatter formatter = new HelpFormatter();

        formatter.printHelp("java -jar lib/vsm-migration.jar", buildOptions(), true);
    }

    public static void main(String[] args) throws UnknownHostException, IOException
    {
        String host = getProperty("abiquo.redis.host", "localhost");
        int port = Integer.valueOf(getProperty("abiquo.redis.port", "6379"));

        CommandLine command = null;
        String filename = null;

        try
        {
            // Parse the command line arguments
            command = new PosixParser().parse(buildOptions(), args);

            if (command.hasOption("help"))
            {
                printUsage();
                System.exit(0);
            }

            if (command.hasOption("f"))
            {
                filename = command.getOptionValue("f");
            }

            if (command.hasOption("h"))
            {
                host = command.getOptionValue("h");
            }

            if (command.hasOption("p"))
            {
                port = Integer.parseInt(command.getOptionValue("p"));
            }
        }
        catch (Exception e)
        {
            logger.error("Error while parsing arguments. " + e.getMessage());
            printUsage();
            System.exit(-1);
        }

        // Start migration
        Migrator migrator = new Migrator(host, port, 0);
        logger.info("Migrating from 1.6.8 to 1.7 data model on redis located at {}:{}", host, port);

        if (filename == null)
        {
            migrator.migrateNonPersistedModelFromRedis();
        }
        else
        {
            File file = new File(filename);
            migrator.migrateNonPersistedModelFromFile(file);
        }

        migrator.migratePersistedModel();

        logger.info("Number of migrated physical machines: {}", migrator.getMachinesCount());
        logger.info("Number of migrated subscriptions: {}", migrator.getSubscriptionsCount());

        System.exit(0);
    }

    private static String getProperty(String name, String defaultValue)
    {
        String value = System.getProperty(name);
        return value == null ? defaultValue : value;
    }
}
