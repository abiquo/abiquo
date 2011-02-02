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

package com.abiquo.abiserver.tracerprocessor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.abiquo.abiserver.persistence.hibernate.HibernateDAOFactory;
import com.abiquo.tracer.Datacenter;
import com.abiquo.tracer.Enterprise;
import com.abiquo.tracer.Machine;
import com.abiquo.tracer.Platform;
import com.abiquo.tracer.Rack;
import com.abiquo.tracer.StoragePool;
import com.abiquo.tracer.TracerTo;
import com.abiquo.tracer.User;
import com.abiquo.tracer.VirtualDatacenter;
import com.abiquo.tracer.VirtualStorage;
import com.abiquo.tracer.server.TracerCollectorException;
import com.abiquo.tracer.server.TracerProcessor;

/**
 * This class writes metering data to the database, using JDBC in a low level.
 * 
 * @author eros
 */
public class DBTracerProcessor implements TracerProcessor
{
    private final static Logger log = LoggerFactory.getLogger(DBTracerProcessor.class);

    @Override
    public void process(final TracerTo object) throws TracerCollectorException
    {
        try
        {
            Platform platform = object.getPlatform();

            if (platform == null)
            {
                // Default platform.
                platform = Platform.SYSTEM_PLATFORM;
            }

            // Object set to be inserted
            String datacenter = null;
            String rack = null;
            String physicalMachine = null;
            String storageSystem = null;
            String storagePool = null;
            String volume = null;
            String network = null;
            String subnet = null;
            String enterprise = null;
            String user = null;
            Integer userId = null;
            String performedBy = User.SYSTEM_USER.getName();
            String virtualDataCenter = null;
            String virtualApp = null;
            String virtualMachine = null;
            String severity = object.getSeverity().name(); // | We suppose this objects
            String actionPerformed = object.getEvent().name(); // | they are filled
            // directly
            String component = object.getComponent().name(); // |
            String stacktrace = null;

            if (object.getUser() != null)
            {
                enterprise = object.getUser().getEnterprise();
                user = object.getUser().getUsername();
                userId = new Long(object.getUser().getId()).intValue();
                performedBy = object.getUser().getUsername();
            }

            // We will insert a description in the
            // 'stacktrace' column (if not null)
            if (object.getDescription() != null)
            {
                stacktrace = object.getDescription();
            }

            // In case we have a null datacenter we insert null in the DB,
            // for every datacenter related row
            // This operation is repeated over and over again through the
            // datacenter hierarchy
            if (platform.getDatacenter() != null)
            {
                Datacenter dc = platform.getDatacenter();
                datacenter = dc.getName();

                if (dc.getRack() != null)
                {
                    Rack r = dc.getRack();
                    rack = r.getName();

                    if (r.getMachine() != null)
                    {
                        Machine m = r.getMachine();
                        physicalMachine = m.getName();

                        if (m.getVirtualMachine() != null)
                        {
                            virtualMachine = m.getVirtualMachine().getName();
                        }
                    }
                }

                if (dc.getVirtualStorage() != null)
                {
                    VirtualStorage vs = dc.getVirtualStorage();
                    storageSystem = dc.getVirtualStorage().getName();

                    if (vs.getStoragePool() != null)
                    {
                        StoragePool sp = vs.getStoragePool();
                        storagePool = dc.getVirtualStorage().getStoragePool().getName();

                        if (sp.getVolume() != null)
                        {
                            volume = dc.getVirtualStorage().getStoragePool().getVolume().getName();
                        }
                    }
                }

                if (dc.getNetwork() != null)
                {
                    network = dc.getNetwork().getName();
                }
            }

            // In case we have a null enterprise we insert "NULL" in the DB,
            // for every enterprise related row
            // This operation is repeated over and over again through the
            // enterprise hierarchy
            if (platform.getEnterprise() != null)
            {
                Enterprise en = platform.getEnterprise();
                enterprise = en.getName();

                if (en.getUser() != null)
                {
                    User u = en.getUser();
                    user = u.getName();
                }

                if (en.getVirtualDatacenter() != null)
                {
                    VirtualDatacenter vdc = en.getVirtualDatacenter();
                    virtualDataCenter = vdc.getName();

                    if (vdc.getVirtualAppliance() != null)
                    {
                        virtualApp = vdc.getVirtualAppliance().getName();
                    }
                }
            } // TODO what happens with the rest of the hierarchy?

            // The MySQL query

            String insert =
                "insert delayed into metering(datacenter, rack, physicalMachine, storageSystem,"
                    + " storagePool, volume, network, subnet, enterprise, idUser, user, virtualDatacenter,"
                    + " virtualApp, virtualmachine, severity, performedby, actionperformed, component, stacktrace)"
                    + " values (:datacenter, :rack, :machine, :storage, :storagePool, :volume, :network, :subnet,"
                    + " :enterprise, :userId, :user, :virtualDatacenter, :virtualApp, :virtualMachine, :severity,"
                    + " :performedBy, :actionPerformed, :component, :stacktrace)";

            HibernateDAOFactory.instance().beginConnection();

            log.debug("Query to write: " + insert);
            HibernateDAOFactory.getSessionFactory().getCurrentSession().createSQLQuery(insert)
                .setParameter("datacenter", datacenter).setParameter("rack", rack).setParameter(
                    "machine", physicalMachine).setParameter("storage", storageSystem)
                .setParameter("storagePool", storagePool).setParameter("volume", volume)
                .setParameter("network", network).setParameter("subnet", subnet).setParameter(
                    "enterprise", enterprise).setParameter("userId", userId).setParameter("user",
                    user).setParameter("virtualDatacenter", virtualDataCenter).setParameter(
                    "virtualApp", virtualApp).setParameter("virtualMachine", virtualMachine)
                .setParameter("severity", severity).setParameter("performedBy", performedBy)
                .setParameter("actionPerformed", actionPerformed).setParameter("component",
                    component).setParameter("stacktrace", stacktrace).executeUpdate();
        }
        finally
        {
            HibernateDAOFactory.instance().endConnection();
        }
    }

    @Override
    public void destroy() throws TracerCollectorException
    {
    }
}
