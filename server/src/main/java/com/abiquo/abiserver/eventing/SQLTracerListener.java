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

package com.abiquo.abiserver.eventing;

import com.abiquo.abiserver.persistence.hibernate.HibernateDAOFactory;
import com.abiquo.commons.amqp.impl.tracer.TracerCallback;
import com.abiquo.commons.amqp.impl.tracer.domain.Trace;

/**
 * It receives tracing messages from remote modules and updates the metering table.
 */
public class SQLTracerListener implements TracerCallback
{
    private static final long serialVersionUID = 1L;

    @Override
    public void onTrace(Trace trace)
    {
        try
        {
            String insert =
                "INSERT DELAYED INTO metering(datacenter, rack, physicalMachine, storageSystem,"
                    + " storagePool, volume, network, subnet, enterprise, idUser, user, virtualDatacenter,"
                    + " virtualApp, virtualmachine, severity, performedby, actionperformed, component, stacktrace)"
                    + " VALUES (:datacenter, :rack, :machine, :storage, :storagePool, :volume, :network, :subnet,"
                    + " :enterprise, :userId, :user, :virtualDatacenter, :virtualApp, :virtualMachine, :severity,"
                    + " :performedBy, :actionPerformed, :component, :stacktrace)";

            HibernateDAOFactory.instance().beginConnection();

            HibernateDAOFactory.getSessionFactory().getCurrentSession().createSQLQuery(insert)
                .setParameter("datacenter", null).setParameter("rack", null)
                .setParameter("machine", null).setParameter("storage", null)
                .setParameter("storagePool", null).setParameter("volume", null)
                .setParameter("network", null).setParameter("subnet", null)
                .setParameter("enterprise", trace.getEnterpriseName())
                .setParameter("userId", trace.getUserId())
                .setParameter("user", trace.getUsername()).setParameter("virtualDatacenter", null)
                .setParameter("virtualApp", null).setParameter("virtualMachine", null)
                .setParameter("severity", trace.getSeverity())
                .setParameter("performedBy", trace.getUsername())
                .setParameter("actionPerformed", trace.getEvent())
                .setParameter("component", trace.getComponent())
                .setParameter("stacktrace", trace.getHierarchy()).executeUpdate();
        }
        finally
        {
            HibernateDAOFactory.instance().endConnection();
        }
    }
}
