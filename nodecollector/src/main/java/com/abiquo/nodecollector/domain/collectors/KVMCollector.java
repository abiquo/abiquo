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
package com.abiquo.nodecollector.domain.collectors;

import org.libvirt.Connect;
import org.libvirt.LibvirtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.abiquo.model.enumerator.HypervisorType;
import com.abiquo.nodecollector.aim.impl.AimCollectorImpl;
import com.abiquo.nodecollector.constants.MessageValues;
import com.abiquo.nodecollector.domain.Collector;
import com.abiquo.nodecollector.exception.CollectorException;
import com.abiquo.nodecollector.exception.ConnectionException;
import com.abiquo.nodecollector.exception.LoginException;
import com.abiquo.nodecollector.exception.libvirt.AimException;

/**
 * Special connection for KVM Hypervisor. Uses <LibvirtCollector> abstract class to collect
 * information.
 * 
 * @author jdevesa
 */
@Collector(type = HypervisorType.KVM, order = 5)
public class KVMCollector extends AbstractLibvirtCollector
{
    private static final Logger LOGGER = LoggerFactory.getLogger(KVMCollector.class);

    @Override
    public void connect(final String user, final String password) throws ConnectionException,
        LoginException
    {
        try
        {
            setConn(new Connect("qemu+tcp://" + getIpAddress() + "/system?no_tty=1"));
            try
            {
                aimcollector = new AimCollectorImpl(getIpAddress(), getAimPort());
                // plugin call to see if we are authenticated
                aimcollector.checkAIM();
            }
            catch (AimException e)
            {
                try
                {
                    this.disconnect();
                }
                catch (CollectorException e1)
                {
                    LOGGER.error("Error freeing libvirt connection to address " + getIpAddress(),
                        e1);
                    return;
                }
                throw new ConnectionException(MessageValues.CONN_EXCP_IV, e);
            }
        }
        catch (LibvirtException e)
        {
            try
            {
                this.disconnect();
            }
            catch (CollectorException e1)
            {
                // Do nothing.
                LOGGER.error("Error freeing libvirt connection to address " + getIpAddress(), e1);
            }

            LOGGER.warn("Could not connect at hypervisor {} at cloud node {}", this
                .getHypervisorType().name(), getIpAddress());
            throw new ConnectionException(MessageValues.CONN_EXCP_I, e);
        }

    }

}
