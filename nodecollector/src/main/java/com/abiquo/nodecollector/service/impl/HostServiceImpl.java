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
import org.springframework.stereotype.Service;

import com.abiquo.model.enumerator.HypervisorType;
import com.abiquo.nodecollector.constants.MessageValues;
import com.abiquo.nodecollector.domain.HypervisorCollector;
import com.abiquo.nodecollector.domain.PluginLoader;
import com.abiquo.nodecollector.exception.ConnectionException;
import com.abiquo.nodecollector.exception.NodecollectorException;
import com.abiquo.nodecollector.service.HostService;
import com.abiquo.nodecollector.utils.ProvisioningUtils;
import com.abiquo.server.core.infrastructure.nodecollector.HostDto;

/**
 * This class implements the business logic of collecting the information of the running machines.
 * 
 * @author jdevesa@abiquo.com
 */
@Service("collectorService")
public class HostServiceImpl implements HostService
{
    private static final Logger LOGGER = LoggerFactory.getLogger(HostServiceImpl.class);

    @Override
    public HostDto getHostInfo(final String ipAddress, final HypervisorType hypervisorType,
        final String user, final String password, final Integer aimport) throws NodecollectorException
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
            LOGGER.info("Connected to hypervisor {} at cloud node {} ", col.getHypervisorType().toString(), ipAddress);
            return col.getHostInfo();

        }
        finally
        {
            if (col != null)
            {
                col.disconnect();
            }
        }

    }

}
