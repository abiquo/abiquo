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

package com.abiquo.nodecollector.aim.impl;

import java.util.LinkedList;
import java.util.List;

import org.apache.thrift.TException;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.abiquo.aimstub.Aim.Iface;
import com.abiquo.aimstub.Datastore;
import com.abiquo.aimstub.NetInterface;
import com.abiquo.aimstub.RimpException;
import com.abiquo.aimstub.TTransportProxy;
import com.abiquo.nodecollector.aim.AimCollector;
import com.abiquo.nodecollector.constants.MessageValues;
import com.abiquo.nodecollector.exception.libvirt.AimException;
import com.abiquo.server.core.infrastructure.nodecollector.ResourceEnumType;
import com.abiquo.server.core.infrastructure.nodecollector.ResourceType;

/**
 * {@link AimCollector} implementation.
 */
public class AimCollectorImpl implements AimCollector
{
    /** The constant logger object. */
    private static final Logger LOG = LoggerFactory.getLogger(AimCollectorImpl.class);

    String host;

    Integer aimport;

    Iface aimclient;

    final int KBYTE = 1024;

    public AimCollectorImpl(final String host, final Integer aimport) throws AimException
    {
        aimclient = TTransportProxy.getInstance(host, aimport);
        this.host = host;
        this.aimport = aimport;
        pingAIM();
    }

    @Override
    public void pingAIM() throws AimException
    {
        TTransport transport = new TSocket(host, aimport);

        try
        {
            transport.open();

            if (transport.isOpen())
            {
                transport.close();

                LOG.debug("AIM service running at [{}:{}]", host, aimport);
            }
            else
            {
                LOG.error(MessageValues.AIM_NO_PING);
                throw new AimException(MessageValues.AIM_NO_PING);
            }
        }
        catch (TTransportException e)
        {
            LOG.error(MessageValues.AIM_NO_PING, e);
            throw new AimException(MessageValues.AIM_NO_PING, e);
        }
    }

    @Override
    public void checkAIM() throws AimException
    {
        /**
         * TODO VAGENT and VLAN plugins should be checked ???
         */

        try
        {
            aimclient.checkRimpConfiguration();
        }
        catch (RimpException e)
        {
            final String cause = String.format(MessageValues.AIM_CHECK, "RIMP", host);
            LOG.error(cause, e);
            throw new AimException(cause, e);
        }
        catch (TException e)
        {
            LOG.error(MessageValues.AIM_NO_COMM, e);
            throw new AimException(MessageValues.AIM_NO_COMM, e);
        }

    }

    @Override
    public List<ResourceType> getDatastores() throws AimException
    {
        List<Datastore> datastores;

        try
        {
            datastores = aimclient.getDatastores();
        }
        catch (RimpException e)
        {
            final String cause = String.format("Can not obtain the datastores on [%s]", host);
            LOG.error(cause, e);
            throw new AimException(cause, e);
        }
        catch (TException e)
        {
            LOG.error(MessageValues.AIM_NO_COMM, e);
            throw new AimException(MessageValues.AIM_NO_COMM, e);
        }

        if (datastores == null || datastores.size() == 0)
        {
            final String cause = String.format(MessageValues.AIM_ANY_DATASTORE, host);
            LOG.error(cause);
            throw new AimException(cause);
        }

        List<ResourceType> resources = new LinkedList<ResourceType>();
        for (Datastore ds : datastores)
        {
            resources.add(datastoreToResource(ds));
        }

        return resources;
    }

    @Override
    public Long getDiskFileSize(final String diskFilePath) throws AimException
    {
        try
        {
            long diskSize = aimclient.getDiskFileSize(diskFilePath);

            return diskSize * KBYTE;
        }
        catch (RimpException e)
        {
            final String cause = String.format(MessageValues.AIM_GET_DISK_SIZE, diskFilePath, host);
            LOG.error(cause, e);
            throw new AimException(cause, e);
        }
        catch (TException e)
        {
            LOG.error(MessageValues.AIM_NO_COMM, e);
            throw new AimException(MessageValues.AIM_NO_COMM, e);
        }
    }

    @Override
    public List<ResourceType> getNetInterfaces() throws AimException
    {
        List<NetInterface> netifaces;

        try
        {
            netifaces = aimclient.getNetInterfaces();
        }
        catch (RimpException e)
        {
            final String cause =
                String.format("Can not obtain the network interfaces on [%s]", host);
            LOG.error(cause, e);
            throw new AimException(cause, e);
        }
        catch (TException e)
        {
            LOG.error(MessageValues.AIM_NO_COMM, e);
            throw new AimException(MessageValues.AIM_NO_COMM, e);
        }

        if (netifaces == null || netifaces.size() == 0)
        {
            final String cause = String.format(MessageValues.AIM_ANY_NETIFACE, host);
            LOG.error(cause);
            throw new AimException(cause);
        }

        List<ResourceType> resources = new LinkedList<ResourceType>();
        for (NetInterface ni : netifaces)
        {
            resources.add(netInterfaceToResource(ni));
        }

        return resources;
    }

    /**
     * @return <UL>
     *         <li>"resourceType" as STORAGE_DISK constant.</li>
     *         <li>"connection" as the Datastore UUID mark.</li>
     *         <li>XXX"resourceSubType" as the Datastore kind (nfs, ext3 ...).</li>
     *         <li>"elementName" as the Datastore device (/dev/sd1, nfs:/opt/export ...).</li>
     *         <li>"address" as the Datastore moutn point path (/, /opt/nfs-testing ...).</li>
     *         <li>"units" as the total size (used plus available) of the datastore expresed on
     *         Bytes (298696808, 1548152 ...).</li>
     *         </UL>
     */
    protected ResourceType datastoreToResource(final Datastore ds)
    {
        ResourceType rt = new ResourceType();

        rt.setResourceType(ResourceEnumType.STORAGE_DISK);
        rt.setConnection(ds.getType()); // datastore uuid
        rt.setElementName(ds.getDevice());
        rt.setAddress(ds.getPath());
        rt.setUnits(ds.getTotalSize() * KBYTE);
        rt.setAvailableUnits(ds.getUsableSize() * KBYTE);

        return rt;
    }

    /**
     * @return <UL>
     *         <li>"resourceType" as NETWORK_INTERFACE constant.</li>
     *         <li>"elementName" as the network interface device (lo, eth0, wlan0...).</li>
     *         <li>"address" as the network interface hardware address (MAC).</li>
     *         </UL>
     */
    protected ResourceType netInterfaceToResource(final NetInterface net)
    {
        ResourceType rt = new ResourceType();

        rt.setResourceType(ResourceEnumType.NETWORK_INTERFACE);
        rt.setAddress(net.getPhysicalAddress());
        rt.setElementName(net.getName());

        return rt;
    }

    @Override
    public String getInitiatorIQN() throws AimException
    {
        String iqn;

        try
        {
            iqn = aimclient.getInitiatorIQN();
        }
        catch (TException e)
        {
            LOG.error(MessageValues.AIM_NO_COMM, e);
            throw new AimException(MessageValues.AIM_NO_COMM, e);
        }

        return iqn.length() == 0 ? null : iqn;
    }
}
