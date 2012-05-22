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

package com.abiquo.server.core.statistics;

import java.math.BigDecimal;

import javax.persistence.EntityManager;

import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.springframework.stereotype.Repository;

import com.abiquo.server.core.common.persistence.DefaultDAOBase;
import com.softwarementors.bzngine.engines.hibernate.HibernateEntityManagerHelper;

@Repository("jpaCloudUsageDAO")
public class CloudUsageDAO extends DefaultDAOBase<Integer, CloudUsage>
{

    public CloudUsageDAO()
    {
        super(CloudUsage.class);
    }

    public CloudUsageDAO(EntityManager entityManager)
    {
        super(CloudUsage.class, entityManager);
    }

    /**
     * Gets generated statistics information and adds oversubscription calculated cpu / ram values
     * 
     * @param idDatacenter -1 -> calculate total
     * @return
     */
    public CloudUsage calculateCloudUsage(int idDatacenter)
    {

        CloudUsage result = new CloudUsage();

        String oversubscriptionQuery = CloudUsageDAO.CALCULATE_CLOUD_USAGE_QUERY;

        if (idDatacenter == -1)
        {
            // Total
            result = sumTotalCloudUsage();

        }
        else
        {
            // By Datacenter
            result = findById(idDatacenter);
            oversubscriptionQuery += CloudUsageDAO.FILTER_BY_DATACENTER_PARAM;
        }
        oversubscriptionQuery += CloudUsageDAO.GROUP_BY_DATACENTER_CLAUSE;

        // Add oversubscription calculated cpu / ram statistics by QUERY

        SQLQuery sqlQuery = getSession().createSQLQuery(oversubscriptionQuery);

        if (idDatacenter != -1)
        {
            sqlQuery.setParameter("idDatacenter", idDatacenter);
        }

        Object[] calculatedResources = (Object[]) sqlQuery.uniqueResult();

        if (calculatedResources != null)
        {
            Long calculatedCpu =
                calculatedResources[0] == null ? 0 : ((BigDecimal) calculatedResources[0])
                    .longValue();
            Long calculatedRam =
                calculatedResources[1] == null ? 0 : ((BigDecimal) calculatedResources[1])
                    .longValue();
            result.setVirtualCpuTotal(calculatedCpu);
            result.setVirtualMemoryTotal(calculatedRam);
        }

        return result;

    }

    private CloudUsage sumTotalCloudUsage()
    {
        // TODO: Include aggregators functionality in bzengine?
        Session ses = HibernateEntityManagerHelper.getSession(getEntityManager());

        Criteria crit = ses.createCriteria(CloudUsage.class);
        ProjectionList proList = Projections.projectionList();
        proList.add(Projections.sum(CloudUsage.SERVERS_TOTAL_PROPERTY));
        proList.add(Projections.sum(CloudUsage.SERVERS_RUNNING_PROPERTY));
        proList.add(Projections.sum(CloudUsage.STORAGE_TOTAL_PROPERTY));
        // proList.add(Projections.sum(CloudUsage.STORAGE_RESERVED_PROPERTY));
        proList.add(Projections.sum(CloudUsage.STORAGE_USED_PROPERTY));
        proList.add(Projections.sum(CloudUsage.PUBLIC_I_PS_TOTAL_PROPERTY));
        // proList.add(Projections.sum(CloudUsage.PUBLIC_I_PS_RESERVED_PROPERTY));
        proList.add(Projections.sum(CloudUsage.PUBLIC_I_PS_USED_PROPERTY));
        proList.add(Projections.sum(CloudUsage.V_MACHINES_TOTAL_PROPERTY));
        proList.add(Projections.sum(CloudUsage.V_MACHINES_RUNNING_PROPERTY));
        // proList.add(Projections.sum(CloudUsage.V_CPU_TOTAL_PROPERTY));
        // proList.add(Projections.sum(CloudUsage.V_CPU_RESERVED_PROPERTY));
        proList.add(Projections.sum(CloudUsage.V_CPU_USED_PROPERTY));
        // proList.add(Projections.sum(CloudUsage.V_MEMORY_TOTAL_PROPERTY));
        // proList.add(Projections.sum(CloudUsage.V_MEMORY_RESERVED_PROPERTY));
        proList.add(Projections.sum(CloudUsage.V_MEMORY_USED_PROPERTY));
        proList.add(Projections.sum(CloudUsage.V_STORAGE_TOTAL_PROPERTY));
        // proList.add(Projections.sum(CloudUsage.V_STORAGE_RESERVED_PROPERTY));
        proList.add(Projections.sum(CloudUsage.V_STORAGE_USED_PROPERTY));
        proList.add(Projections.sum(CloudUsage.NUM_USERS_CREATED_PROPERTY));
        proList.add(Projections.sum(CloudUsage.NUM_VDC_CREATED_PROPERTY));
        proList.add(Projections.sum(CloudUsage.NUM_ENTERPRISES_CREATED_PROPERTY));

        crit.setProjection(proList);
        Object[] obj = (Object[]) crit.uniqueResult(); // Returns Object[] ->
        CloudUsage result = new CloudUsage();

        int cont = 0;
        result.setServersTotal((Long) obj[cont++]);
        result.setServersRunning((Long) obj[cont++]);
        result.setStorageTotal((Long) obj[cont++]);
        // result.setStorageReserved((Long) obj[cont++]);
        result.setStorageUsed((Long) obj[cont++]);
        result.setPublicIPsTotal((Long) obj[cont++]);
        // result.setPublicIPsReserved((Long) obj[cont++]);
        result.setPublicIPsUsed((Long) obj[cont++]);
        result.setVirtualMachinesTotal((Long) obj[cont++]);
        result.setVirtualMachinesRunning((Long) obj[cont++]);
        // result.setVirtualCpuTotal((Long) obj[cont++]);
        // result.setVirtualCpuReserved((Long) obj[cont++]);
        result.setVirtualCpuUsed((Long) obj[cont++]);
        // result.setVirtualMemoryTotal((Long) obj[cont++]);
        // result.setVirtualMemoryReserved((Long) obj[cont++]);
        result.setVirtualMemoryUsed((Long) obj[cont++]);
        result.setVirtualStorageTotal((Long) obj[cont++]);
        // result.setVirtualStorageReserved((Long) obj[cont++]);
        result.setVirtualStorageUsed((Long) obj[cont++]);
        result.setNumUsersCreated((Long) obj[cont++]);
        result.setNumVdcCreated((Long) obj[cont++]);
        result.setNumEnterprisesCreated((Long) obj[cont++]);

        return result;
    }

    private final static String CALCULATE_CLOUD_USAGE_QUERY = //
        "SELECT SUM(mm.cpu * COALESCE(over.cpuLoadPercentage/100, 1)), SUM(mm.ram * COALESCE(over.ramLoadPercentage/100, 1)) "
            + //
            "FROM physicalmachine mm LEFT OUTER JOIN "
            + //
            "(select m.idDatacenter, m.idPhysicalMachine, l.id load_rule, m.cpu, m.ram, l.cpuLoadPercentage, l.ramLoadPercentage "
            + //
            "FROM physicalmachine m "
            + //
            "INNER JOIN workload_machine_load_rule l ON m.idPhysicalMachine = l.idMachine "
            + //
            "UNION ALL "
            + //
            "SELECT m.idDatacenter, m.idPhysicalMachine, l.id, m.cpu, m.ram, l.cpuLoadPercentage, l.ramLoadPercentage "
            + //
            "FROM rack r "
            + //
            "INNER JOIN workload_machine_load_rule l ON r.idRack = l.idRack AND l.idMachine IS NULL "
            + //
            "INNER JOIN physicalmachine m ON r.idRack = m.idRack "
            + //
            "WHERE m.idPhysicalMachine NOT IN (SELECT ll.idMachine FROM workload_machine_load_rule ll WHERE ll.idMachine IS NOT NULL) "
            + //
            "UNION ALL "
            + //
            "SELECT m.idDatacenter, m.idPhysicalMachine, l.id, m.cpu, m.ram, l.cpuLoadPercentage, l.ramLoadPercentage "
            + //
            "FROM datacenter d "
            + //
            "INNER JOIN workload_machine_load_rule l ON d.idDataCenter = l.idDatacenter AND l.idMachine IS NULL AND l.idRack IS NULL "
            + //
            "INNER JOIN physicalmachine m ON d.idDatacenter = m.idDatacenter "
            + //
            "WHERE m.idPhysicalMachine NOT IN (select ll.idMachine from workload_machine_load_rule ll where ll.idMachine IS NOT NULL) "
            + //
            "AND m.idRack NOT IN (select ll.idRack from workload_machine_load_rule ll where ll.idRack IS NOT NULL) "
            + //
            ") over ON mm.idPhysicalMachine = over.idPhysicalMachine ";

    private final static String FILTER_BY_DATACENTER_PARAM =
        " WHERE mm.idDatacenter = :idDatacenter ";

    private final static String GROUP_BY_DATACENTER_CLAUSE = " GROUP BY mm.idDatacenter;";

}
