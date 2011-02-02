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

package com.abiquo.server.core.infrastructure.network;

import java.util.List;

import javax.persistence.EntityManager;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.abiquo.server.core.cloud.VirtualDatacenter;
import com.abiquo.server.core.common.persistence.DefaultDAOBase;
import com.abiquo.server.core.infrastructure.Rack;

@Repository
public class VLANNetworkDAO extends DefaultDAOBase<Integer, VLANNetwork>
{
    public VLANNetworkDAO()
    {
        super(VLANNetwork.class);
    }

    public VLANNetworkDAO(EntityManager em)
    {
        super(VLANNetwork.class, em);
    }

    public List<VLANNetwork> findVLANNetworks(VirtualDatacenter virtualDatacenter)
    {
        assert virtualDatacenter != null;

        Criteria criteria = createCriteria(sameNetwork(virtualDatacenter));
        criteria.addOrder(Order.asc(VLANNetwork.NAME_PROPERTY));
        List<VLANNetwork> result = getResultList(criteria);

        return result;
    }

    private static Criterion sameNetwork(VirtualDatacenter virtualDatacenter)
    {
        return sameNetwork(virtualDatacenter.getNetwork());
    }

    private static Criterion sameNetwork(Network network)
    {
        return Restrictions.eq(VLANNetwork.NETWORK_PROPERTY, network);
    }

    public boolean existsAnyWithName(Network network, String name)
    {
        assert !StringUtils.isEmpty(name);

        return existsAnyByCriterions(sameNetwork(network), nameEqual(name));
    }

    private Criterion nameEqual(String name)
    {
        assert name != null;

        return Restrictions.eq(VLANNetwork.NAME_PROPERTY, name);
    }

    private final String FIND_BY_ENTERPRISE = " SELECT vlan "//
        + "FROM com.abiquo.server.core.infrastructure.network.VLANNetwork vlan, "//
        + "com.abiquo.server.core.cloud.VirtualDatacenter vdc "//
        + "WHERE vlan.id = vdc.network.id "//
        + "and vdc.enterprise.id = :enterpriseId";

    public List<VLANNetwork> findByEnterprise(int enterpriseId)
    {
        Query query = getSession().createQuery(FIND_BY_ENTERPRISE);
        query.setParameter("enterpriseId", enterpriseId);

        return query.list();
    }

    // private final static String VLAN_TAG_USED = "SELECT vlan_tag " //
    // + "FROM vlan_network vn, " //
    // + "vlan_network_assignment vna " //
    // + "WHERE vn.vlan_network_id = vna.vlan_network_id " + //
    // "AND vna.idRack = :idRack";

    // FIXME named parameter idRack is not working !!!!
    private final static String VLAN_TAG_USED = "SELECT vn FROM " //
        + "com.abiquo.server.core.infrastructure.network.VLANNetwork vn, " //
        + "com.abiquo.server.core.infrastructure.network.NetworkAssignment vna " //
        + "WHERE vn.id = vna.vlanNetwork.id " + //
        "AND vna.rack.id = :idRack ";

    // FIXME named parameter idRack is not working !!!!
    private final static String VLAN_ID_TAG_USED = "SELECT vn.id FROM " //
        + "com.abiquo.server.core.infrastructure.network.VLANNetwork vn, " //
        + "com.abiquo.server.core.infrastructure.network.NetworkAssignment vna " //
        + "WHERE vna.rack.id = :idRack " + //

        "AND vn.id = vna.vlanNetwork.id ";

    public List<Integer> getVLANsIdUsedInRack(Rack rack)
    {
        String idRack = String.valueOf(rack.getId());

        Query query = getSession().createQuery("SELECT vn.id FROM " //
            + "com.abiquo.server.core.infrastructure.network.VLANNetwork vn, " //
            + "com.abiquo.server.core.infrastructure.network.NetworkAssignment vna " //
            + "WHERE vn.id = vna.vlanNetwork.id " + //
            "AND vna.rack.id = " + idRack);

        // FIXME
        // Query query = getSession().createQuery(VLAN_ID_TAG_USED);
        // query.setInteger("idRack", rack.getId());

        return query.list();
    }

    private final static String PUBLIC_VLAN_NETWORKS_BY_RACK = "SELECT vlan " //
        + "FROM com.abiquo.server.core.infrastructure.network.VLANNetwork vlan, " //
        + "com.abiquo.server.core.infrastructure.Rack rack " //
        + "WHERE vlan.vlan_network_id = rack.datacenter.network.networkId " //
        + "AND rack.idRack = :idRack";

    public List<VLANNetwork> findPublicVLANNetworksByRack(Rack rack)
    {

        String idRack = String.valueOf(rack.getId());

        Query query = getSession().createQuery("SELECT vn FROM " //
            + "com.abiquo.server.core.infrastructure.network.VLANNetwork vn, " //
            + "com.abiquo.server.core.infrastructure.network.NetworkAssignment vna " //
            + "WHERE vn.id = vna.vlanNetwork.id " + //
            "AND vna.rack.id = " + idRack);

        // FIXME
        // Query query = getSession().createQuery(VLAN_TAG_USED);
        // query.setParameter("idRack", rack.getId());

        return query.list();
    }

}
