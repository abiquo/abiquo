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

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.abiquo.model.enumerator.HypervisorType;
import com.abiquo.nodecollector.constants.MessageValues;
import com.abiquo.nodecollector.domain.HypervisorCollector;
import com.abiquo.nodecollector.domain.PluginLoader;
import com.abiquo.nodecollector.exception.ConnectionException;
import com.abiquo.nodecollector.exception.LoginException;
import com.abiquo.nodecollector.exception.NodecollectorException;
import com.abiquo.nodecollector.service.HypervisorService;
import com.abiquo.nodecollector.utils.ProvisioningUtils;

/**
 * @author jdevesa
 */
public class HypervisorServiceImpl implements HypervisorService
{

    private static final Logger LOGGER = LoggerFactory.getLogger(HypervisorServiceImpl.class);
    
    @Override
    public HypervisorType discoverHypervisor(final String ip, final Integer aimport)
        throws NodecollectorException
    {
        HypervisorCollector collector = null;

        try
        {
            // Checks if at least the machine is NOT_MANAGED
            ProvisioningUtils.provisioningCheck(ip);

            final List<HypervisorCollector> collectorsList = PluginLoader.getInstance().getAllPlugins();

            // Default user and passord. We only want to know if there is an Hypervisor out there,
            // not logging it successfully. So, we invent a user and password.
            String fakeUser = "user";
            String fakePassword = "password";

            if (collectorsList != null)
            {
                LOGGER.info("Discovering hypervisors to cloud node {}...", ip);

                for (HypervisorCollector col : collectorsList)
                {
                    try
                    {
                        LOGGER.info("Trying " + col.getHypervisorType().name());
                        col.setAimPort((aimport == null) ? 8889 : aimport);
                        col.setIpAddress(ip);
                        col.connect(fakeUser, fakePassword);
                        LOGGER.info("Discovered hypervisor: {} at cloud node {}", col.getHypervisorType().toString(), ip);
                        collector = col;
                        return col.getHypervisorType();
                    }
                    catch (LoginException le)
                    {
                        LOGGER.info("Discovered hypervisor: {} at cloud node {}", col.getHypervisorType().toString(), ip);
                        return col.getHypervisorType();
                    }
                    catch (ConnectionException e)
                    {
                        continue;
                    }
                    catch (NoClassDefFoundError e)
                    {
                        LOGGER.error("FATAL ERROR: Libvirt package not found in nodecollector machine");
                        continue;
                    }
                    catch (UnsatisfiedLinkError e)
                    {
                        LOGGER.error("FATAL ERROR: Libvirt package not found in nodecollector machine");
                        continue;
                    }

                }

            }
            
            LOGGER.info("No Hypervisors found.");
            throw new ConnectionException(MessageValues.NOHYP_EXCP);
            
        }
        finally
        {
            if (collector != null)
            {
                collector.disconnect();
            }
        }

    }

}
