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

/**
 * 
 */
package com.abiquo.abiserver.persistence.dao.metering.hibernate;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

import com.abiquo.abiserver.business.hibernate.pojohb.metering.MeterHB;
import com.abiquo.abiserver.business.hibernate.pojohb.user.RoleHB;
import com.abiquo.abiserver.exception.PersistenceException;
import com.abiquo.abiserver.persistence.dao.metering.MeterDAO;
import com.abiquo.abiserver.persistence.hibernate.HibernateDAO;
import com.abiquo.abiserver.persistence.hibernate.HibernateDAOFactory;
import com.abiquo.abiserver.security.SecurityService;

/**
 * * Class that implements the extra DAO functions for the
 * {@link com.abiquo.abiserver.persistence.dao.metering.MeterDAO} interface
 * 
 * @author jdevesa@abiquo.com
 */
public class MeterDAOHibernate extends HibernateDAO<MeterHB, Long> implements MeterDAO
{

    private static final String GET_METER_LIST_FILTERED = "GET_METER_LIST_FILTERED";

    @SuppressWarnings("unchecked")
    @Override
    public List<MeterHB> findAllByFilter(final HashMap<String, String> filter,
        final List<String> performedbyList, final Integer numrows, final RoleHB role)
        throws PersistenceException
    {
        Integer numberOfParameters = 0;
        List<MeterHB> listOfMeters = new ArrayList<MeterHB>();
        StringBuilder stringQuery =
            new StringBuilder("from com.abiquo.abiserver.business.hibernate.pojohb.metering.MeterHB ");

        // Create Dates
        String fromDateInit = new Timestamp(0).toString();
        String fromdateFilter;
        if (filter.containsKey("datefrom"))
        {
            fromdateFilter = new Timestamp(Long.valueOf(filter.get("datefrom")) * 1000).toString();
        }
        else
        {
            fromdateFilter = fromDateInit;
        }

        String toDateEnd = new Timestamp(new Date().getTime()).toString();
        String todateFilter;
        if (filter.containsKey("dateto"))
        {
            // We sum another day to include both filters
            todateFilter =
                new Timestamp((Long.valueOf(filter.get("dateto")) + 86400) * 1000).toString();
        }
        else
        {
            todateFilter = toDateEnd;
        }

        stringQuery.append("where timestamp between '").append(fromdateFilter).append("' and '")
            .append(todateFilter).append("'");

        // create all the filters from the HashMap information. Due all of them are included in the
        // query, we need to
        // insert '%' for all the filters not included in the hashmap
        if (filter.containsKey("datacenter"))
        {
            stringQuery.append(" and datacenter like '%" + filter.get("datacenter") + "%'");
        }
        if (filter.containsKey("rack"))
        {
            stringQuery.append(" and rack like '%" + filter.get("rack") + "%'");
        }
        if (filter.containsKey("physicalmachine"))
        {
            stringQuery.append(" and physicalMachine like '%" + filter.get("physicalmachine")
                + "%'");
        }
        if (filter.containsKey("storagesystem"))
        {
            stringQuery.append(" and storagesystem like '%" + filter.get("storagesystem") + "%'");
        }
        if (filter.containsKey("storagepool"))
        {
            stringQuery.append(" and storagepool like '%" + filter.get("storagepool") + "%'");
        }
        if (filter.containsKey("volume"))
        {
            stringQuery.append(" and volume like '%" + filter.get("volume") + "%'");
        }
        if (filter.containsKey("network"))
        {
            stringQuery.append(" and network like '%" + filter.get("network") + "%'");
        }
        if (filter.containsKey("subnet"))
        {
            stringQuery.append(" and subnet like '%" + filter.get("subnet") + "%'");
        }
        if (filter.containsKey("enterprise"))
        {
            stringQuery.append(" and enterprise like '%" + replaceApostrophe(filter.get("enterprise")) + "%'");
        }
        if (filter.containsKey("user"))
        {
            stringQuery.append(" and user like '%" + filter.get("user") + "%'");
        }
        if (filter.containsKey("virtualdatacenter"))
        {
            stringQuery.append(" and virtualDataCenter like '%" + filter.get("virtualdatacenter")
                + "%'");
        }
        if (filter.containsKey("virtualapp"))
        {
            stringQuery.append(" and virtualApp like '%" + filter.get("virtualapp") + "%'");
        }
        if (filter.containsKey("virtualmachine"))
        {
            stringQuery.append(" and virtualmachine like '%" + filter.get("virtualmachine") + "%'");
        }
        if (filter.containsKey("severity"))
        {
            stringQuery.append(" and severity like '" + filter.get("severity") + "%'");
        }
        if (filter.containsKey("performedby"))
        {
            stringQuery.append(" and performedby like '%" + filter.get("performedby") + "%'");
        }
        if (filter.containsKey("actionperformed"))
        {
            stringQuery
                .append(" and actionperformed like '" + filter.get("actionperformed") + "%'");
        }
        if (filter.containsKey("component"))
        {
            stringQuery.append(" and component like '%" + filter.get("component") + "%'");
        }   

        // if (role != Role.SYS_ADMIN)
        if (!SecurityService.isCloudAdmin(role.toPojo()))
        {
            if (performedbyList != null && !performedbyList.isEmpty())
            {
                // performedby filter
                stringQuery.append(" and performedby in (");
                boolean firstentry = true;
                for (String currentString : performedbyList)
                {
                    if (firstentry == false)
                    {
                        stringQuery.append(",");
                    }
                    stringQuery.append("'" + currentString + "'");
                    if (firstentry == true)
                    {
                        firstentry = false;
                    }
                }
                stringQuery.append(")");
            }
        }
        // delete last ','
        stringQuery.append(" order by timestamp desc");

        // Setting the query
        Session session = HibernateDAOFactory.getSessionFactory().getCurrentSession();
        Query meterQuery = session.createQuery(stringQuery.toString());
        meterQuery.setMaxResults(numrows);

        listOfMeters = meterQuery.list();

        return listOfMeters;
    }
    
    private final String replaceApostrophe(final String name)
    {
        return name.replaceAll("'", "''");
    }
}
