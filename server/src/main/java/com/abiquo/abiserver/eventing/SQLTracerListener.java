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

import java.util.HashMap;
import java.util.Map;

import org.hibernate.Query;

import com.abiquo.abiserver.persistence.hibernate.HibernateDAOFactory;
import com.abiquo.commons.amqp.impl.tracer.TracerCallback;
import com.abiquo.commons.amqp.impl.tracer.domain.Trace;

/**
 * It receives tracing messages from remote modules and updates the metering table.
 */
public class SQLTracerListener implements TracerCallback
{
    private static final long serialVersionUID = 1L;

    /**
     * This map holds the mappings between the Hierarchy processor prefixes and the column in the
     * metering table where the trace information will be persisted.
     */
    private static final Map<String, String> parameterMappings;

    static
    {
        parameterMappings = new HashMap<String, String>();
        parameterMappings.put("datacenter", "admin/datacenters");
        parameterMappings.put("rack", "racks");
        parameterMappings.put("machine", "machines");
        parameterMappings.put("storage", "");
        parameterMappings.put("storagePool", "");
        parameterMappings.put("volume", "volumes");
        parameterMappings.put("network", "");
        parameterMappings.put("subnet", "");
        parameterMappings.put("virtualDatacenter", "cloud/virtualdatacenters");
        parameterMappings.put("virtualApp", "");
        parameterMappings.put("virtualMachine", "");

        // TODO: Uncomment this. Currently the hierarchy shows the enterprise and user who performs
        // the action. Not the enterprise/user resource where the action happens!
        // parameterMappings.put("enterprise", "admin/enterprises");
        // parameterMappings.put("user", "users");
    }

    @Override
    public void onTrace(final Trace trace)
    {
        try
        {
            String insert =
                "INSERT DELAYED INTO metering(idDatacenter, datacenter, idRack, rack, idPhysicalMachine, physicalMachine,"
                    + " idStorageSystem, storageSystem, idStoragePool, storagePool, idVolume, volume, idNetwork, network,"
                    + " idSubnet, subnet, idEnterprise, enterprise, idUser, user, idVirtualDataCenter, virtualDataCenter,"
                    + " idVirtualApp, virtualApp, idVirtualMachine, virtualmachine, severity, performedby, actionperformed,"
                    + " component, stacktrace)"
                    + " VALUES (:datacenterId, :datacenter, :rackId, :rack, :machineId, :machine, :storageId, :storage,"
                    + " :storagePoolId, :storagePool, :volumeId, :volume, :networkId, :network, :subnetId, :subnet,"
                    + " :enterpriseId, :enterprise, :userId, :user, :virtualDatacenterId, :virtualDatacenter, :virtualAppId,"
                    + " :virtualApp, :virtualMachineId, :virtualMachine, :severity, :performedBy, :actionPerformed,"
                    + " :component, :stacktrace)";

            HibernateDAOFactory.instance().beginConnection();

            Query query =
                HibernateDAOFactory.getSessionFactory().getCurrentSession().createSQLQuery(insert)
                    .setParameter("severity", trace.getSeverity()).setParameter("performedBy",
                        trace.getUsername()).setParameter("actionPerformed", trace.getEvent())
                    .setParameter("component", trace.getComponent()).setParameter("stacktrace",
                        trace.getMessage());

            // TODO: Remove these parameters. Currently the hierarchy shows the enterprise and user
            // who performs the action. Not the enterprise/user resource where the action happens!
            query.setParameter("enterpriseId", trace.getEnterpriseId());
            query.setParameter("enterprise", trace.getEnterpriseName());
            query.setParameter("userId", trace.getUserId());
            query.setParameter("user", trace.getUsername());

            addTraceParameters(trace, query);

            query.executeUpdate();
        }
        finally
        {
            HibernateDAOFactory.instance().endConnection();
        }
    }

    private void addTraceParameters(final Trace trace, final Query query)
    {
        // Only process if we have hierarchy data
        if (trace.getHierarchyData() != null)
        {
            // For each column, check if the values are present in the trace
            for (Map.Entry<String, String> entry : parameterMappings.entrySet())
            {
                String column = entry.getKey();
                String traceKey = entry.getValue();

                String parameterId = null;
                String parameterName = null;

                // Can eb empty if the processor still does not exist
                if (!traceKey.equals(""))
                {
                    String parameterData = trace.getHierarchyData().get(traceKey);

                    // If the parameter is present, parse the info; otherwise set it to null
                    if (parameterData != null)
                    {
                        int separator = parameterData.indexOf('|');
                        parameterId = parameterData.substring(0, separator);
                        parameterName = parameterData.substring(separator + 1);
                    }
                }

                query.setParameter(column + "Id", parameterId);
                query.setParameter(column, parameterName);
            }
        }
    }

}
