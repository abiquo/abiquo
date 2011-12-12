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
package com.abiquo.api.eventing;

import java.util.Collection;
import java.util.List;

import org.hibernate.HibernateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.abiquo.api.services.RemoteServiceService;
import com.abiquo.api.services.stub.VsmServiceStub;
import com.abiquo.api.tracer.TracerLogger;
import com.abiquo.model.enumerator.RemoteServiceType;
import com.abiquo.server.core.cloud.VirtualMachine;
import com.abiquo.server.core.cloud.VirtualMachineDAO;
import com.abiquo.server.core.cloud.VirtualMachineRep;
import com.abiquo.server.core.infrastructure.Datacenter;
import com.abiquo.server.core.infrastructure.InfrastructureRep;
import com.abiquo.server.core.infrastructure.RemoteService;

//TODO @Task 
//@Task(interval = 1, timeUnit = TimeUnit.MINUTES) -> Only once at the beggining
//@Service("vsmSubscriber")
@Task
@Transactional(readOnly = true, propagation = Propagation.REQUIRED)
public class VSMSubscriber
{

    /** The logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(VSMSubscriber.class);

    @Autowired
    protected TracerLogger tracer;

    @Autowired
    protected VirtualMachineDAO vMachineDAO;

    @Autowired
    protected VirtualMachineRep vMachineRep; // TODO: include findVirtualMachinesByDatacenter

    @Autowired
    protected InfrastructureRep infraRep;

    @Autowired
    protected VsmServiceStub vsmStub;

    @Autowired
    private RemoteServiceService remoteServiceService;

    /**
     * Attempts an VSM subscription for each VA. If subscription succeeds the task is unsheduled.
     * Gets all VM Deployed and subscribes to VSM
     */
    public void subscribe()
    {
        try
        {
            LOGGER.info("Refreshing Virtual System Monitor subscriptions");

            // Subscriptions should work. If process fails, the task will be rescheduled.
            // XXX TaskServiceFactory.getService().unschedule(VSMSubscriber.class);


            // Get the Virtual appliance list
            // OPTIMIZED -> we recover all VMs by Datacenter? or PMachine?
            Collection<Datacenter> allDCs = infraRep.findAll();
            for (Datacenter datacenter : allDCs)
            {
                RemoteService remoteService =
                    remoteServiceService.getRemoteService(datacenter.getId(),
                        RemoteServiceType.VIRTUAL_SYSTEM_MONITOR);
                List<VirtualMachine> vMachines =
                    vMachineDAO.findVirtualMachinesByDatacenter(datacenter.getId());
                for (VirtualMachine vMachine : vMachines)
                {
                    if (vMachine.isDeployed())
                    {
                        vsmStub.subscribe(remoteService, vMachine);
                    }
                }

            }

        }
        catch (HibernateException e)
        {
            LOGGER.error("An error was occurred when refreshing the VSM subscriptions caused by:",
                e);
            // tracer.log(Seve, component, event, message, args)
            // Task should be re-scheduled
        }
    }
}
