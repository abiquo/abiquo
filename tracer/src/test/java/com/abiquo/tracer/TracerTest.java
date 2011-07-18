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

package com.abiquo.tracer;

import static com.abiquo.testng.TestConfig.BASIC_UNIT_TESTS;
import static com.abiquo.tracer.Datacenter.datacenter;
import static com.abiquo.tracer.Enterprise.enterprise;
import static com.abiquo.tracer.Machine.machine;
import static com.abiquo.tracer.Network.network;
import static com.abiquo.tracer.Platform.platform;
import static com.abiquo.tracer.Rack.rack;
import static com.abiquo.tracer.StoragePool.storagePool;
import static com.abiquo.tracer.User.user;
import static com.abiquo.tracer.VirtualAppliance.virtualAppliance;
import static com.abiquo.tracer.VirtualDatacenter.virtualDatacenter;
import static com.abiquo.tracer.VirtualMachine.virtualMachine;
import static com.abiquo.tracer.VirtualStorage.virtualStorage;
import static com.abiquo.tracer.Volume.volume;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.abiquo.tracer.client.TracerFactory;
import com.abiquo.tracer.server.LoggingTracerProcessor;
import com.abiquo.tracer.server.TracerCollector;
import com.abiquo.tracer.server.TracerCollectorFactory;

public class TracerTest
{

    @BeforeMethod
    public void setUp() throws Exception
    {
    }

    @AfterMethod
    public void tearDown() throws Exception
    {
    }

    @Test
    public void testLogSeverityTypeComponentTypeEventTypeStringUserInfo() throws Exception
    {
        TracerCollector t = TracerCollectorFactory.getTracerCollector();
        t.addListener(new LoggingTracerProcessor());
        t.init();
        for (SeverityType severity : SeverityType.values())
        {
            for (ComponentType component : ComponentType.values())
            {
                for (EventType event : EventType.values())
                {
                    TracerFactory.getTracer().log(severity, component, event, "HolaMundo", null,
                        null);
                }
            }
        }
        t.destroy();
        assert (true);
    }

    @Test(groups = BASIC_UNIT_TESTS)
    public void testFluentInterface() throws Exception
    {
        Platform platform =
            platform("MyPlatform").datacenter(
                datacenter("myDatacenter").rack(
                    rack("myRack").machine(
                        machine("myMachine").virtualMachine(virtualMachine("myVirtualMachine")))));

        Platform platform2 =
            platform("MyPlatform").datacenter(
                datacenter("myDatacenter").virtualStorage(
                    virtualStorage("myVirtualStorage").storagePool(
                        storagePool("myStoragePool").volume(volume("myVolume")))));

        Platform platform3 =
            platform("MyPlatform").datacenter(
                datacenter("myDatacenter").network(network("mynetwork")));

        Platform platform4 =
            platform("MyPlatform").enterprise(enterprise("myEnterprise").user(user("myuser")));

        Platform platform5 =
            platform("MyPlatform").enterprise(
                enterprise("myEnterprise").virtualDatacenter(
                    virtualDatacenter("myVirtualDatacenter").volume(volume("myVolume"))));

        Platform platform6 =
            platform("MyPlatform").enterprise(
                enterprise("myEnterprise").virtualDatacenter(
                    virtualDatacenter("myVirtualDatacenter").network(network("myNetwork"))));

        Platform platform7 =
            platform("MyPlatform").enterprise(
                enterprise("myEnterprise").virtualDatacenter(
                    virtualDatacenter("myVirtualDatacenter").virtualAppliance(
                        virtualAppliance("myVirtualAppliance").virtualMachine(
                            virtualMachine("myVirtualMachine")))));

        Platform platform8 =
            platform("MyPlatform").enterprise(
                enterprise("myEnterprise").virtualDatacenter(
                    virtualDatacenter("myVirtualDatacenter").virtualAppliance(
                        virtualAppliance("myVirtualAppliance").volume(volume("myVolume")))));

        Platform platform9 =
            platform("MyPlatform").enterprise(
                enterprise("myEnterprise").virtualDatacenter(
                    virtualDatacenter("myVirtualDatacenter").virtualAppliance(
                        virtualAppliance("myVirtualAppliance").network(network("myNetwork")))));

        assert (true);
    }
}
