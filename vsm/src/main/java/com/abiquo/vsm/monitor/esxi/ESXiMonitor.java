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
package com.abiquo.vsm.monitor.esxi;

import java.net.URL;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.abiquo.vsm.events.VMEvent;
import com.abiquo.vsm.events.VMEventType;
import com.abiquo.vsm.exception.MonitorException;
import com.abiquo.vsm.model.PhysicalMachine;
import com.abiquo.vsm.monitor.AbstractMonitor;
import com.abiquo.vsm.monitor.Monitor;
import com.abiquo.vsm.monitor.Monitor.Type;
import com.abiquo.vsm.monitor.esxi.util.ExtendedAppUtil;
import com.vmware.vim25.VirtualMachinePowerState;
import com.vmware.vim25.mo.Folder;
import com.vmware.vim25.mo.InventoryNavigator;
import com.vmware.vim25.mo.ServiceInstance;
import com.vmware.vim25.mo.VirtualMachine;
import com.vmware.vim25.mo.util.OptionSpec;

/**
 * The ESXi monitor.
 * 
 * @author ibarrera
 */
@Monitor(type = Type.VMX_04)
public class ESXiMonitor extends AbstractMonitor
{
    /** The executor. */
    private ExecutorService executor;

    /** The opts entered. */
    private HashMap<String, String> optsEntered;

    /** The Constant logger. */
    private final static Logger logger = LoggerFactory.getLogger(ESXiMonitor.class);

    /** Main interface from the VMWare SDK. */
    private ExtendedAppUtil apputil;

    /** The poller. */
    private ESXiPoller poller;

    public ESXiMonitor()
    {
        executor = Executors.newSingleThreadExecutor();
        poller = new ESXiPoller(this);
        optsEntered = new HashMap<String, String>();
    }

    @Override
    public int getMaxNumberOfHypervisors()
    {
        return 1;
    }

    @Override
    public void shutdown()
    {
        executor.shutdownNow();
    }

    @Override
    public void start()
    {
        executor.submit(poller);
    }

    @Override
    public void publishState(String physicalMachineAddress, String virtualMachineName)
        throws MonitorException
    {
        super.publishState(physicalMachineAddress, virtualMachineName);

        VMEventType evenType = null;
        VMEvent eventToNotify = null;

        try
        {
            String machineName = virtualMachineName;
            URL hypervisorAddress = new URL(physicalMachineAddress);
            PhysicalMachine pm = getPhysicalMachine(physicalMachineAddress);
            String user = pm.getUsername();
            String password = pm.getPassword();
            builtinOptionsEntered(user, password);

            String connectionUrl = "https://" + hypervisorAddress.getHost() + ":443/sdk";
            this.optsEntered.put("url", connectionUrl);

            ServiceInstance serviceInstance =
                new ServiceInstance(new URL(connectionUrl), user, password, true);

            apputil = ExtendedAppUtil.init(serviceInstance, constructOptions(), optsEntered);

            Folder rootFolder = apputil.getServiceInstance().getRootFolder();

            VirtualMachine vm =
                (VirtualMachine) new InventoryNavigator(rootFolder).searchManagedEntity(
                    "VirtualMachine", machineName);

            VirtualMachinePowerState vmstate = vm.getRuntime().getPowerState();

            switch (vmstate)
            {
                case poweredOff:
                    evenType = VMEventType.POWER_OFF;
                    break;
                case poweredOn:
                    evenType = VMEventType.POWER_ON;
                    break;
                case suspended:
                    evenType = VMEventType.PAUSED;
                    break;
                default:
                    break;
            }

            eventToNotify = new VMEvent(evenType, physicalMachineAddress, virtualMachineName);

            apputil.disConnect();
        }
        catch (Exception e)
        {
            logger.error("An error was occurred when getting the virtual machine state", e);
            try
            {
                apputil.disConnect();
            }
            catch (Exception e1)
            {
                logger.error("", e1);
            }
            throw new MonitorException(e);
        }

        // Publish the event
        this.notify(eventToNotify);
    }

    /**
     * It adds parameters from the configuration file.
     * 
     * @param config the config
     * @param password
     * @param user
     */
    private void builtinOptionsEntered(String user, String password)
    {
        // optsEntered.put("url", "https://192.168.1.34/sdk");
        // optsEntered.put("url", "https://abiquo.homelinux.net/sdk");
        // optsEntered.put("username", config.getUser());
        // optsEntered.put("password", config.getPassword());
        // TODO put the paramerts from the config file
        optsEntered.put("username", user);
        optsEntered.put("password", password);
        optsEntered.put("ignorecert", "true");
        optsEntered.put("datacentername", "ha-datacenter");
        // if (config.getIgnorecert()) optsEntered.put("ignorecert", "true");

    }

    /**
     * It constructs the basic options needed to work.
     * 
     * @return the option spec[]
     */
    private static OptionSpec[] constructOptions()
    {
        OptionSpec[] useroptions = new OptionSpec[8];
        useroptions[0] = new OptionSpec("vmname", "String", 1, "Name of the virtual machine", null);
        useroptions[1] =
            new OptionSpec("datacentername", "String", 1, "Name of the datacenter", null);
        useroptions[2] = new OptionSpec("hostname", "String", 0, "Name of the host", null);
        useroptions[3] =
            new OptionSpec("guestosid", "String", 0, "Type of Guest OS", "winXPProGuest");
        useroptions[4] = new OptionSpec("cpucount", "Integer", 0, "Total CPU Count", "1");
        useroptions[5] = new OptionSpec("disksize", "Integer", 0, "Size of the Disk", "64");
        useroptions[6] =
            new OptionSpec("memorysize",
                "Integer",
                0,
                "Size of the Memory in the blocks of 1024 MB",
                "1024");
        useroptions[7] =
            new OptionSpec("datastorename", "String", 0, "Name of the datastore", null);

        return useroptions;
    }

}
