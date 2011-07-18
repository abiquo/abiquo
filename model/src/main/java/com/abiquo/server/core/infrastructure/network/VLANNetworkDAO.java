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
import com.abiquo.server.core.infrastructure.Datacenter;
import com.abiquo.server.core.infrastructure.Rack;

@Repository
public class VLANNetworkDAO extends DefaultDAOBase<Integer, VLANNetwork>
{
    public VLANNetworkDAO()
    {
        super(VLANNetwork.class);
    }

    public VLANNetworkDAO(final EntityManager em)
    {
        super(VLANNetwork.class, em);
    }

    public List<VLANNetwork> findVLANNetworks(final VirtualDatacenter virtualDatacenter)
    {
        assert virtualDatacenter != null;

        Criteria criteria = createCriteria(sameNetwork(virtualDatacenter));
        criteria.addOrder(Order.asc(VLANNetwork.NAME_PROPERTY));
        List<VLANNetwork> result = getResultList(criteria);

        return result;
    }

    public VLANNetwork findVlanByVirtualDatacenterId(final VirtualDatacenter virtualDatacenter,
        final Integer vlanId)
    {
        return findUniqueByCriterions(Restrictions.eq(VLANNetwork.NETWORK_PROPERTY,
            virtualDatacenter.getNetwork()), Restrictions.eq(VLANNetwork.ID_PROPERTY, vlanId));
    }

    private static Criterion sameNetwork(final VirtualDatacenter virtualDatacenter)
    {
        return sameNetwork(virtualDatacenter.getNetwork());
    }

    private static Criterion sameNetwork(final Network network)
    {
        return Restrictions.eq(VLANNetwork.NETWORK_PROPERTY, network);
    }

    public boolean existsAnyWithName(final Network network, final String name)
    {
        return StringUtils.isEmpty(name) ? false : existsAnyByCriterions(sameNetwork(network),
            nameEqual(name));
    }

    public VLANNetwork findByDefault(final VirtualDatacenter virtualDatacenter)
    {
        return findUniqueByCriterions(sameNetwork(virtualDatacenter.getNetwork()), Restrictions.eq(
            VLANNetwork.DEFAULT_PROPERTY, true));
    }

    private Criterion nameEqual(final String name)
    {
        assert name != null;

        return Restrictions.eq(VLANNetwork.NAME_PROPERTY, name);
    }

    private final String FIND_BY_ENTERPRISE = " SELECT vlan "//
        + "FROM com.abiquo.server.core.infrastructure.network.VLANNetwork vlan, "//
        + "com.abiquo.server.core.cloud.VirtualDatacenter vdc "//
        + "WHERE vlan.network.id = vdc.network.id "//
        + "and vdc.enterprise.id = :enterpriseId";

    public List<VLANNetwork> findByEnterprise(final int enterpriseId)
    {
        Query query = getSession().createQuery(FIND_BY_ENTERPRISE);
        query.setParameter("enterpriseId", enterpriseId);

        return query.list();
    }

    public List<Integer> getVLANTagsUsedInRack(final Rack rack)
    {
        String idRack = String.valueOf(rack.getId());

        Query query = getSession().createQuery("SELECT vn.tag FROM " //
            + "com.abiquo.server.core.infrastructure.network.VLANNetwork vn, " //
            + "com.abiquo.server.core.infrastructure.network.NetworkAssignment vna " //
            + "WHERE vn.id = vna.vlanNetwork.id " + //
            "AND vna.rack.id = " + idRack + " AND vn.tag IS NOT NULL");

        // FIXME
        // Query query = getSession().createQuery(VLAN_ID_TAG_USED);
        // query.setInteger("idRack", rack.getId());

        return query.list();
    }

    public List<VLANNetwork> findPublicVLANNetworksByDatacenter(final Datacenter datacenter)
    {

        Criterion inNetwork =
            Restrictions.eq(VLANNetwork.NETWORK_PROPERTY, datacenter.getNetwork());
        Criteria criteria = getSession().createCriteria(VLANNetwork.class).add(inNetwork);

        return criteria.list();
    }

    private final String GET_VLAN_DATACENTER =
        "SELECT dc " //
            + "FROM com.abiquo.server.core.infrastructure.Datacenter dc " //
            + "inner join dc.network net, com.abiquo.server.core.infrastructure.network.VLANNetwork vlan " //
            + "WHERE net.id = vlan.network.id AND vlan.id = :id";

    public boolean isPublic(final VLANNetwork vlan)
    {
        Query query = getSession().createQuery(GET_VLAN_DATACENTER);
        query.setParameter("id", vlan.getId());

        return query.uniqueResult() != null;
    }

}
