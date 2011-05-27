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

@Repository("jpaRackDAO")
/* package */class RackDAO extends DefaultDAOBase<Integer, Rack>
{

    public RackDAO()
    {
        super(Rack.class);
    }

    public RackDAO(EntityManager entityManager)
    {
        super(Rack.class, entityManager);
    }
    
    private static Criterion sameDatacenter(Datacenter datacenter)
    {
        assert datacenter != null;

        return Restrictions.eq(Rack.DATACENTER_PROPERTY, datacenter);
    }

    private static Criterion equalName(String name)
    {
        assert !StringUtils.isEmpty(name);

        return Restrictions.eq(Rack.NAME_PROPERTY, name);
    }

    public List<Rack> findRacks(Datacenter datacenter)
    {
        assert datacenter != null;
        assert isManaged2(datacenter);

        Criteria criteria = createCriteria(sameDatacenter(datacenter));
        criteria.addOrder(Order.asc(Rack.NAME_PROPERTY));
        List<Rack> result = getResultList(criteria);
        return result;
    }

    public boolean existsAnyWithDatacenterAndName(Datacenter datacenter, String name)
    {
        assert datacenter != null;
        assert !StringUtils.isEmpty(name);

        return existsAnyByCriterions(sameDatacenter(datacenter), equalName(name));
    }

    public boolean existsAnyOtherWithDatacenterAndName(Rack rack, String name)
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
    public List<Integer> getRackIdByMinVLANCount(int idDatacenter)
    {
        SQLQuery query = getSession().createSQLQuery(SQL_RACK_IDS_BY_MIN_VLAN_COUNT);
        query.setInteger("idDatacenter", idDatacenter);

        return query.list();
    }

    private final static String COUNT_DEPLOYED_VLA = //
        "SELECT COUNT(vn.id) " + //
            "FROM NetworkAssignment vn WHERE " + //
            "vn.rack.id = :idRack";

    public Long getNumberOfDeployedVlanNetworks(Integer rackId)
    {
        Query query = getSession().createQuery(COUNT_DEPLOYED_VLA);
        query.setInteger("idRack", rackId);

        Long numberOfDeployedNetworks = (Long) query.uniqueResult();

        return numberOfDeployedNetworks;
    }

    /**
     * Return the Rack by datacenter id and rack id.
     * @param datacenterId
     * @param rackId
     * @return
     */
    public Rack findByIds(Integer datacenterId, Integer rackId)
    {
        return findUniqueByCriterions(Restrictions.eq("datacenter.id", datacenterId),
            Restrictions.eq(Rack.ID_PROPERTY, rackId));
    }
}
