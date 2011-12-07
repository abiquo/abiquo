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

package com.abiquo.abiserver.healthcheck;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.hibernate.HibernateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.abiquo.abicloud.taskservice.exception.TaskServiceException;
import com.abiquo.abicloud.taskservice.factory.TaskServiceFactory;
import com.abiquo.abicloud.taskservice.model.Task;
import com.abiquo.abicloud.taskservice.model.TaskMethod;
import com.abiquo.abiserver.abicloudws.RemoteServiceUtils;
import com.abiquo.abiserver.business.hibernate.pojohb.virtualappliance.VirtualappHB;
import com.abiquo.abiserver.exception.PersistenceException;
import com.abiquo.abiserver.exception.RemoteServiceException;
import com.abiquo.abiserver.persistence.dao.virtualappliance.VirtualApplianceDAO;
import com.abiquo.abiserver.persistence.hibernate.HibernateDAOFactory;

/**
 * Performs VSM subscription upon server startup and schedules a retry if subscription fails.
 * 
 * @author ibarrera
 */
@Task(interval = 1, timeUnit = TimeUnit.MINUTES)
public class VSMSubscriber
{
    /** The logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(VSMSubscriber.class);

    /**
     * Attempts an VSM subscription for each VA. If subscription succeeds the task is unsheduled.
     */
    @TaskMethod
    public void subscribe()
    {
        try
        {
            LOGGER.info("Refreshing Virtual System Monitor subscriptions");

            // Subscriptions should work. If process fails, the task will be rescheduled.
            TaskServiceFactory.getService().unschedule(VSMSubscriber.class);

            // Get the Virtual appliance list
            VirtualApplianceDAO vaDao = HibernateDAOFactory.instance().getVirtualApplianceDAO();
            HibernateDAOFactory.instance().beginConnection();

            List<VirtualappHB> vaDeployedList = vaDao.findAllDeployed();

            for (VirtualappHB virtualappHB : vaDeployedList)
            {
                // For each VirtualApp, we recover the VirtualSystemMonitorAddress.
                String virtualSystemMonitor =
                    RemoteServiceUtils.getVirtualSystemMonitorFromVA(virtualappHB.toPojo());

                // try
                // {
                // TODO Refresh the subscriptions for each virtual appliance
                // EventingSupport.subscribeToAllVA(virtualappHB.toPojo(),
                // virtualSystemMonitor);
                // }
                // catch (EventingException e)
                // {
                // if (e.getCause() instanceof IOException)
                // {
                // LOGGER
                // .error(
                // "[FATAL] The server can not refresh VSM subsciptions at [{}] : caused by {}",
                // virtualSystemMonitor, e);
                // TaskServiceFactory.getService().schedule(VSMSubscriber.class);
                // }
                // else
                // {
                // LOGGER
                // .debug(
                // "An error was occurred when refreshing the VSM subscriptions caused by:",
                // e);
                // }
                // }
            }

            HibernateDAOFactory.instance().endConnection();
        }
        catch (HibernateException e)
        {
            LOGGER.error("An error was occurred when refreshing the VSM subscriptions caused by:",
                e);
        }
        catch (PersistenceException e)
        {
            LOGGER.error("An error was occurred when refreshing the VSM subscriptions caused by:",
                e);
        }
        catch (RemoteServiceException e)
        {
            LOGGER.error("An error was occurred when refreshing the VSM subscriptions caused by:",
                e);
        }
        catch (TaskServiceException e)
        {
            LOGGER
                .error(
                    "An error was occurred when rescheduling the VSM subscription renew task caused by:",
                    e);
        }
    }
}
