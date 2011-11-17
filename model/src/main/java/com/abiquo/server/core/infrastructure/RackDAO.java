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

import java.util.List;

import javax.persistence.EntityManager;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
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
        assert datacenter != null;
        assert isManaged2(datacenter);

        Criteria criteria = createCriteria(sameDatacenter(datacenter));
        criteria.addOrder(Order.asc(Rack.NAME_PROPERTY));
        List<Rack> result = getResultList(criteria);
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
        Query q = getSession().createQuery(HQL_NOT_MANAGED_RACKS_BY_DATACENTER);
        q.setInteger("idDatacenter", datacenterId);
        return q.list();
    }

    private final String QUERY_USED_VDRP = "SELECT vm.vdrpPort " + //
        "FROM com.abiquo.server.core.cloud.VirtualMachine vm " + //
        "WHERE vm.hypervisor.machine.rack = :rack ";

    @SuppressWarnings("unchecked")
    public List<Integer> findUsedVrdpPorts(final Rack rack)
    {
        Query query = getSession().createQuery(QUERY_USED_VDRP);
        query.setParameter("rack", rack);
        return query.list();
    }
}
