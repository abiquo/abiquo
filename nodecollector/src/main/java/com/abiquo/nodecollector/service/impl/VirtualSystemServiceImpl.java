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
package com.abiquo.nodecollector.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.abiquo.model.enumerator.HypervisorType;
import com.abiquo.nodecollector.constants.MessageValues;
import com.abiquo.nodecollector.domain.HypervisorCollector;
import com.abiquo.nodecollector.domain.PluginLoader;
import com.abiquo.nodecollector.exception.CollectorException;
import com.abiquo.nodecollector.exception.ConnectionException;
import com.abiquo.nodecollector.exception.LoginException;
import com.abiquo.nodecollector.exception.UnprovisionedException;
import com.abiquo.nodecollector.service.VirtualSystemService;
import com.abiquo.nodecollector.utils.ProvisioningUtils;
import com.abiquo.server.core.infrastructure.nodecollector.VirtualSystemCollectionDto;
import com.abiquo.server.core.infrastructure.nodecollector.VirtualSystemDto;

/**
 * @author jdevesa
 */
public class VirtualSystemServiceImpl implements VirtualSystemService
{
    private static final Logger LOGGER = LoggerFactory.getLogger(VirtualSystemServiceImpl.class);

    @Override
    public VirtualSystemCollectionDto getVirtualSystemList(final String ipAddress,
        final HypervisorType hypervisorType, final String user, final String password,
        final Integer aimport) throws CollectorException, LoginException, ConnectionException,
        UnprovisionedException
    {
        // Checks if at least the machine is NOT_MANAGED
        ProvisioningUtils.provisioningCheck(ipAddress);

        // Get the Hypervisor and set parameters.
        final HypervisorCollector col = PluginLoader.getInstance().getPlugin(hypervisorType);
        if (col == null)
        {
            throw new ConnectionException(MessageValues.UNLOADED_PLUGIN);
        }
        col.setIpAddress(ipAddress);
        col.setAimPort(aimport);

        try
        {
            // Connect to it and retrieve information
            col.connect(user, password);
            LOGGER.info("Connected to hypervisor {} at cloud node {} ", col.getHypervisorType()
                .toString(), ipAddress);

            return col.getVirtualMachines();
        }
        finally
        {
            if (col != null)
            {
                col.disconnect();
            }
        }
    }

    @Override
    public VirtualSystemDto getVirtualSystemByUUID(final String ip,
        final HypervisorType hypervisorType, final String user, final String password,
        final Integer aimport, final String uuid) throws CollectorException, LoginException,
        ConnectionException, UnprovisionedException
    {

        VirtualSystemCollectionDto listOfVS =
            this.getVirtualSystemList(ip, hypervisorType, user, password, aimport);
        for (VirtualSystemDto vs : listOfVS.getVirtualSystems())
        {
            if (vs.getUuid().equalsIgnoreCase(uuid))
            {
                return vs;
            }
        }

        LOGGER.info("Could not find the virtual system {} at cloud node {}", uuid, ip);
        throw new UnprovisionedException(MessageValues.NOVS_EXCP);
    }

    @Override
    public VirtualSystemDto getVirtualSystemByName(final String ip,
        final HypervisorType hypervisorType, final String user, final String password,
        final Integer aimport, final String name) throws CollectorException, LoginException,
        ConnectionException, UnprovisionedException
    {

        VirtualSystemCollectionDto listOfVS =
            this.getVirtualSystemList(ip, hypervisorType, user, password, aimport);
        for (VirtualSystemDto vs : listOfVS.getVirtualSystems())
        {
            if (vs.getName().equalsIgnoreCase(name))
            {
                return vs;
            }
        }

        LOGGER.info("Could not find the virtual system {} at cloud node {}", name, ip);
        throw new UnprovisionedException(MessageValues.NOVS_EXCP);
    }
}
