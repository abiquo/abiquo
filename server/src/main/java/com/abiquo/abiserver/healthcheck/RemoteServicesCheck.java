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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.abiquo.abicloud.taskservice.model.Task;
import com.abiquo.abicloud.taskservice.model.TaskMethod;
import com.abiquo.abiserver.business.hibernate.pojohb.service.RemoteServiceHB;
import com.abiquo.abiserver.commands.RemoteServicesCommand;
import com.abiquo.abiserver.commands.impl.RemoteServicesCommandImpl;
import com.abiquo.abiserver.exception.InfrastructureCommandException;
import com.abiquo.abiserver.exception.PersistenceException;
import com.abiquo.abiserver.persistence.DAOFactory;
import com.abiquo.abiserver.persistence.dao.infrastructure.RemoteServiceDAO;
import com.abiquo.abiserver.persistence.hibernate.HibernateDAOFactory;
import com.abiquo.abiserver.pojo.authentication.UserSession;
import com.abiquo.tracer.ComponentType;
import com.abiquo.tracer.EventType;
import com.abiquo.tracer.SeverityType;
import com.abiquo.tracer.client.TracerFactory;

/**
 * Check Remote Services status.
 * 
 * @author ibarrera
 */
@Task(interval = 4, timeUnit = TimeUnit.MINUTES)
public class RemoteServicesCheck
{
    /**
     * The logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(RemoteServicesCheck.class);

    /**
     * Check the state of the Remote Services.
     */
    @TaskMethod
    public void checkRemoteServices()
    {
        DAOFactory factory = HibernateDAOFactory.instance();
        RemoteServiceDAO rsDAO = factory.getRemoteServiceDAO();
        RemoteServicesCommand rsCommand = new RemoteServicesCommandImpl();

        // Create a system user session
        UserSession systemSession = new UserSession();
        systemSession.setUser("admin");

        LOGGER.debug("Running RemoteService Check...");

        // Find all remote services
        List<RemoteServiceHB> remoteServices = null;
        try
        {
            factory.beginConnection();
            remoteServices = rsDAO.findAll();
            factory.endConnection();
        }
        catch (PersistenceException ex)
        {
            factory.rollbackConnection();

            LOGGER.error("An error occured while retrieving remote services", ex);

            TracerFactory.getTracer().log(SeverityType.CRITICAL, ComponentType.DATACENTER,
                EventType.REMOTE_SERVICES_CHECK,
                "An error occured while retrieving remote services");
        }

        // Check all remote services
        if (remoteServices != null && !remoteServices.isEmpty())
        {
            for (RemoteServiceHB remoteService : remoteServices)
            {
                LOGGER.debug("Checking Remote Service [{}] of datacenter [{}]", remoteService
                    .getRemoteServiceType().getName(), remoteService.getIdDataCenter());

                try
                {
                    if (remoteService.getRemoteServiceType().canBeChecked())
                    {
                        rsCommand.checkRemoteService(systemSession,
                            remoteService.getIdRemoteService());
                    }
                }
                catch (InfrastructureCommandException ex)
                {
                    LOGGER.error(
                        "An error occured while checking remote service: "
                            + remoteService.getRemoteServiceType() + " for datacenter "
                            + remoteService.getIdDataCenter(), ex);

                    TracerFactory.getTracer().log(
                        SeverityType.CRITICAL,
                        ComponentType.DATACENTER,
                        EventType.REMOTE_SERVICES_CHECK,
                        "An error occured while checking remote service ["
                            + remoteService.getRemoteServiceType().getName() + "[ of datacenter ["
                            + remoteService.getIdDataCenter() + "]");
                }
            }
        }

        LOGGER.debug("Finished RemoteService Check...");
    }
}
