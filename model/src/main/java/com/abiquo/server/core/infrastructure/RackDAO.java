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

package com.abiquo.server.core.infrastructure;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.abiquo.server.core.common.persistence.DefaultDAOBase;
import com.softwarementors.bzngine.entities.PersistentEntity;

@Repository("jpaRackDAO")
/* package */class RackDAO extends DefaultDAOBase<Integer, Rack>
{

    public RackDAO()
    {
        super(Rack.class);
    }

    public RackDAO(final EntityManager entityManager)
    {
        super(Rack.class, entityManager);
    }

    private static Criterion sameDatacenter(final Datacenter datacenter)
    {
        assert datacenter != null;

        return Restrictions.eq(Rack.DATACENTER_PROPERTY, datacenter);
    }

    private static Criterion equalName(final String name)
    {
        assert !StringUtils.isEmpty(name);

        return Restrictions.eq(Rack.NAME_PROPERTY, name);
    }

    public List<Rack> findRacks(final Datacenter datacenter)
    {
        return findRacks(datacenter, null);
    }

    private final static String QUERY_GET_FILTERED_RACKS = //
        "SELECT  r.idRack, r.idDataCenter, r.name, r.shortDescription, r.largeDescription, r.vlan_id_min, r.vlan_id_max, "
            + "r.vlans_id_avoided, r.vlan_per_vdc_expected, r.nrsq, r.haEnabled, r.version_c FROM " //
            + "rack r LEFT OUTER JOIN datacenter dc ON r.idDatacenter = dc.idDatacenter "
            + "WHERE dc.idDatacenter = :idDatacenter " //
            + "AND (r.name like :filter "
            + "OR r.idRack in (SELECT pms.idRack FROM physicalmachine pms LEFT OUTER JOIN enterprise ent ON pms.idEnterprise = ent.idEnterprise "
            + "WHERE ent.name like :filter OR pms.name like :filter ) " + ")";

    public List<Rack> findRacks(final Datacenter datacenter, final String filter)
    {
        assert datacenter != null;
        assert isManaged2(datacenter);

        if (filter != null && !filter.isEmpty())
        {
            Query query = getSession().createSQLQuery(QUERY_GET_FILTERED_RACKS);
            query.setParameter("idDatacenter", datacenter.getId());
            query.setString("filter", "%" + filter + "%");

            List<Rack> racks = getSQLQueryResults(getSession(), query, Rack.class, 0);
            return racks;

        }
        Criteria criteria = createCriteria(sameDatacenter(datacenter));
        criteria.addOrder(Order.asc(Rack.NAME_PROPERTY));
        List<Rack> result = getResultList(criteria);
        return result;
    }

    @SuppressWarnings("unchecked")
    private <T> List<T> getSQLQueryResults(final Session session, final Query query,
        final Class<T> objectClass, final int idFieldPosition)
    {
        List<T> result = new ArrayList<T>();
        List<Object[]> sqlResult = query.list();

        if (sqlResult != null && !sqlResult.isEmpty())
        {
            for (Object[] res : sqlResult)
            {
                T obj = (T) session.get(objectClass, (Integer) res[idFieldPosition]);
                result.add(obj);
            }
        }

        return result;
    }

    public List<Rack> findRacksWithHAEnabled(final Datacenter datacenter)
    {
        Criteria criteria = createCriteria(sameDatacenter(datacenter));
        criteria.add(Restrictions.eq(Rack.HAENABLED_PROPERTY, true));
        criteria.addOrder(Order.asc(Rack.NAME_PROPERTY));

        List<Rack> result = getResultList(criteria);

        return result;
    }

    public boolean existsAnyWithDatacenterAndName(final Datacenter datacenter, final String name)
    {
        assert datacenter != null;
        assert !StringUtils.isEmpty(name);

        return existsAnyByCriterions(sameDatacenter(datacenter), equalName(name));
    }

    public boolean existsAnyOtherWithDatacenterAndName(final Rack rack, final String name)
    {
        assert rack != null;
        assert !StringUtils.isEmpty(name);

        return existsAnyOtherByCriterions(rack, sameDatacenter(rack.getDatacenter()),
            equalName(name));
    }

    private final static String SQL_RACK_IDS_BY_MIN_VLAN_COUNT = //
        //
        "SELECT rack_filtered_dc.idRack FROM "
            + //
            "(SELECT r.idRack, r.idDatacenter, r.vlan_id_min, r.vlan_id_max, r.vlan_per_vdc_expected, r.nrsq, count(vn.id) as vlans_used "
            + //
            "FROM rack r LEFT JOIN vlan_network_assignment vn ON r.idRack = vn.idRack GROUP BY r.idRack ) as rack_filtered_dc "
            + //
            "WHERE rack_filtered_dc.idDataCenter = :idDatacenter AND rack_filtered_dc.vlans_used + rack_filtered_dc.vlan_per_vdc_expected + (((rack_filtered_dc.vlan_id_max - rack_filtered_dc.vlan_id_min +1 ) * (rack_filtered_dc.nrsq)) / 100) <= ((rack_filtered_dc.vlan_id_max - rack_filtered_dc.vlan_id_min) + 1) "
            + //
            "ORDER BY rack_filtered_dc.vlans_used + rack_filtered_dc.vlan_per_vdc_expected ASC";

    private final static String SQL_RACK_IDS_BY_MIN_VLAN_COUNT_LITE = //
        //
        "SELECT r.idRack " + //
            "FROM rack r, virtualapp va, virtualdatacenter vdc, datacenter dc " + //
            "WHERE va.idVirtualApp = :idVApp " + //
            "AND vdc.idVirtualDataCenter = va.idVirtualDataCenter " + //
            "AND vdc.idDataCenter = r.idDatacenter";//

    /**
     * Obtains the racks (prefiltered by target datacenter and virtualdatacenter) with minimal VLANS
     * // and with vms deployed
     */
    public List<Integer> getRackIdByMinVLANCount(final int idDatacenter)
    {
        SQLQuery query = getSession().createSQLQuery(SQL_RACK_IDS_BY_MIN_VLAN_COUNT);
        query.setInteger("idDatacenter", idDatacenter);

        return query.list();
    }

    private final static String COUNT_DEPLOYED_VLA = //
        "SELECT COUNT(vn.id) " + //
            "FROM NetworkAssignment vn WHERE " + //
            "vn.rack.id = :idRack";

    public Long getNumberOfDeployedVlanNetworks(final Integer rackId)
    {
        Query query = getSession().createQuery(COUNT_DEPLOYED_VLA);
        query.setInteger("idRack", rackId);

        Long numberOfDeployedNetworks = (Long) query.uniqueResult();

        return numberOfDeployedNetworks;
    }

    /**
     * Return the Rack by datacenter id and rack id.
     * 
     * @param datacenterId
     * @param rackId
     * @return
     */
    public Rack findByIds(final Integer datacenterId, final Integer rackId)
    {
        return findUniqueByCriterions(Restrictions.eq("datacenter.id", datacenterId),
            Restrictions.eq(PersistentEntity.ID_PROPERTY, rackId));
    }

    private final static String HQL_NOT_MANAGED_RACKS_BY_DATACENTER = //
        "SELECT nmr " + //
            "FROM Rack nmr WHERE " + //
            "nmr.datacenter.id = :idDatacenter and nmr.class = " + Rack.class.getName();

    /**
     * Returns all not managed racks.
     * 
     * @param datacenter
     * @return List<Rack>
     */
    public List<Rack> findAllNotManagedRacksByDatacenter(final Integer datacenterId)
    {
        return findAllNotManagedRacksByDatacenter(datacenterId, null);
    }

    public List<Rack> findAllNotManagedRacksByDatacenter(final Integer datacenterId,
        final String filter)
    {
        String hql = HQL_NOT_MANAGED_RACKS_BY_DATACENTER;
        if (filter != null && !filter.isEmpty())
        {
            hql += " AND nmr.name like :filter";
        }

        Query q = getSession().createQuery(hql);
        q.setInteger("idDatacenter", datacenterId);
        if (filter != null && !filter.isEmpty())
        {
            q.setString("filter", "%" + filter + "%");
        }

        return q.list();
    }

    private final static String HQL_EMPTY_OFF_MACHINES_IN_RACK = "select h.machine "
        + "from Hypervisor h " + "where h.machine.rack.id = :rackId " + "and h.machine.state = "
        + Machine.State.HALTED_FOR_SAVE.ordinal();

    private final static String HQL_EMPTY_ON_MACHINES_IN_RACK = "select h.machine "
        + "from Hypervisor h inner join h.machine m where m.rack.id = :rackId and h not in "
        + "(select vm.hypervisor from VirtualMachine vm) " + "and h.machine.state = "
        + Machine.State.MANAGED.ordinal();

    /**
     * Return all machines in a rack that are empty of VM and powered off.
     * 
     * @param rackId rack.
     * @return Integer
     */
    public Integer getEmptyOffMachines(final Integer rackId)
    {
        Query q =
            getSession().createQuery(HQL_EMPTY_OFF_MACHINES_IN_RACK).setInteger("rackId", rackId);
        return q.list().size();
    }

    /**
     * Return all machines in a rack that are empty of VM.
     * 
     * @param rackId rack.
     * @return Integer
     */
    public Integer getEmptyOnMachines(final Integer rackId)
    {
        Query q =
            getSession().createQuery(HQL_EMPTY_ON_MACHINES_IN_RACK).setInteger("rackId", rackId);
        return q.list().size();
    }

    /**
     * Returns any machine that is in the rack in HALTED_FOR_SAVE.
     * 
     * @param rackId rack.
     * @return Machine
     */
    public List<Machine> getRandomMachinesToStartFromRack(final Integer rackId,
        final Integer howMany)
    {

        Query q =
            getSession().createQuery(HQL_EMPTY_OFF_MACHINES_IN_RACK).setInteger("rackId", rackId);

        List<Machine> machines = q.list();
        if (machines.isEmpty())
        {
            return null;
        }
        return machines.subList(0, howMany < machines.size() ? howMany : machines.size() - 1);
    }

    /**
     * Returns any machine that is in the rack in MANAGED.
     * 
     * @param rackId rack.
     * @return Machine
     */
    public List<Machine> getRandomMachinesToShutDownFromRack(final Integer rackId,
        final Integer howMany)
    {
        if (howMany <= 0)
        {
            return null;
        }
        Query q =
            getSession().createQuery(HQL_EMPTY_ON_MACHINES_IN_RACK).setInteger("rackId", rackId);

        List<Machine> machines = q.list();
        if (machines.isEmpty())
        {
            return null;
        }
        return machines.subList(0, howMany < machines.size() ? howMany : machines.size() - 1);
    }
}
