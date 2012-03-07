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

import com.abiquo.model.enumerator.NetworkType;
import com.abiquo.server.core.cloud.VirtualDatacenter;
import com.abiquo.server.core.common.persistence.DefaultDAOBase;
import com.abiquo.server.core.enterprise.Enterprise;
import com.abiquo.server.core.infrastructure.Datacenter;
import com.abiquo.server.core.infrastructure.Rack;
import com.softwarementors.bzngine.entities.PersistentEntity;

@Repository
public class VLANNetworkDAO extends DefaultDAOBase<Integer, VLANNetwork>
{
    private static Criterion sameNetwork(final Network network)
    {
        return Restrictions.eq(VLANNetwork.NETWORK_PROPERTY, network);
    }

    private static Criterion sameNetwork(final VirtualDatacenter virtualDatacenter)
    {
        return sameNetwork(virtualDatacenter.getNetwork());
    }

    private final String FIND_BY_DATACENTER =
        " Select vlan "
            + "FROM com.abiquo.server.core.infrastructure.network.VLANNetwork vlan, "
            + "com.abiquo.server.core.cloud.VirtualDatacenter vdc "
            + "INNER JOIN vdc.network net WHERE vdc.datacenter.id = :datacenterId and vlan.network.id = net.id";

    private final String FIND_BY_ENTERPRISE = " SELECT vlan "//
        + "FROM com.abiquo.server.core.infrastructure.network.VLANNetwork vlan, "//
        + "com.abiquo.server.core.cloud.VirtualDatacenter vdc "//
        + "WHERE vlan.network.id = vdc.network.id "//
        + "and vdc.enterprise.id = :enterpriseId";

    private final String GET_VLAN_DATACENTER =
        "SELECT dc " //
            + "FROM com.abiquo.server.core.infrastructure.Datacenter dc " //
            + "inner join dc.network net, com.abiquo.server.core.infrastructure.network.VLANNetwork vlan " //
            + "WHERE net.id = vlan.network.id AND vlan.id = :id";

    public VLANNetworkDAO()
    {
        super(VLANNetwork.class);
    }

    public VLANNetworkDAO(final EntityManager em)
    {
        super(VLANNetwork.class, em);
    }

    public boolean existsAnyWithName(final Network network, final String name)
    {
        assert !StringUtils.isEmpty(name);

        return existsAnyByCriterions(sameNetwork(network), nameEqual(name));
    }

    public List<VLANNetwork> findByEnterprise(final int enterpriseId)
    {
        Query query = getSession().createQuery(FIND_BY_ENTERPRISE);
        query.setParameter("enterpriseId", enterpriseId);

        return query.list();
    }

    public VLANNetwork findExternalVlanByEnterprise(final Enterprise ent, final Integer vlanId)
    {
        return findUniqueByCriterions(Restrictions.eq(VLANNetwork.ENTERPRISE_PROPERTY, ent),
            Restrictions.eq(PersistentEntity.ID_PROPERTY, vlanId));
    }

    public VLANNetwork findExternalVlanByEnterpriseInDatacenter(final Enterprise ent,
        final Datacenter datacenter, final Integer vlanId)
    {
        return findUniqueByCriterions(Restrictions.eq(VLANNetwork.ENTERPRISE_PROPERTY, ent),
            sameNetwork(datacenter.getNetwork()),
            Restrictions.eq(PersistentEntity.ID_PROPERTY, vlanId));
    }

    public List<VLANNetwork> findExternalVlansByEnterprise(final Enterprise ent)
    {
        return findByCriterions(Restrictions.eq(VLANNetwork.ENTERPRISE_PROPERTY, ent));
    }

    public List<VLANNetwork> findExternalVlansByEnterpriseInDatacenter(final Enterprise ent,
        final Datacenter datacenter)
    {
        return findByCriterions(Restrictions.eq(VLANNetwork.ENTERPRISE_PROPERTY, ent),
            sameNetwork(datacenter.getNetwork()));
    }

    public List<VLANNetwork> findPrivateVLANNetworksByDatacenter(final Datacenter datacenter)
    {
        Query query = getSession().createQuery(FIND_BY_DATACENTER);
        query.setParameter("datacenterId", datacenter.getId());

        return query.list();
    }

    public VLANNetwork findPublicVlanByDatacenter(final Datacenter dc, final Integer vlanId)
    {
        return findUniqueByCriterions(sameNetwork(dc.getNetwork()),
            Restrictions.eq(PersistentEntity.ID_PROPERTY, vlanId));
    }

    public List<VLANNetwork> findPublicVLANNetworksByDatacenter(final Datacenter datacenter,
        final NetworkType netType)
    {

        Criterion inNetwork =
            Restrictions.eq(VLANNetwork.NETWORK_PROPERTY, datacenter.getNetwork());
        Criteria criteria = getSession().createCriteria(VLANNetwork.class).add(inNetwork);
        if (netType != null)
        {
            if (netType.equals(NetworkType.PUBLIC))
            {

                criteria.add(Restrictions.eq(VLANNetwork.TYPE_PROPERTY, NetworkType.PUBLIC));
                // criteria.add(Restrictions.isNull(VLANNetwork.ENTERPRISE_PROPERTY));
            }
            else if (netType.equals(NetworkType.EXTERNAL_UNMANAGED))
            {
                criteria.add(Restrictions.or(
                    Restrictions.eq(VLANNetwork.TYPE_PROPERTY, NetworkType.EXTERNAL),
                    Restrictions.eq(VLANNetwork.TYPE_PROPERTY, NetworkType.UNMANAGED)));
                // criteria.add(Restrictions.isNotNull(VLANNetwork.ENTERPRISE_PROPERTY));
            }
            else if (netType.equals(NetworkType.EXTERNAL))
            {
                criteria.add(Restrictions.eq(VLANNetwork.TYPE_PROPERTY, NetworkType.EXTERNAL));
                // criteria.add(Restrictions.isNotNull(VLANNetwork.ENTERPRISE_PROPERTY));
            }
            else if (netType.equals(NetworkType.UNMANAGED))
            {
                criteria.add(Restrictions.eq(VLANNetwork.TYPE_PROPERTY, NetworkType.UNMANAGED));
            }
            else if (netType.equals(NetworkType.INTERNAL))
            {
                criteria.add(Restrictions.eq(VLANNetwork.TYPE_PROPERTY, NetworkType.INTERNAL));
                // criteria.add(Restrictions.isNotNull(VLANNetwork.ENTERPRISE_PROPERTY));
            }
        }

        return criteria.list();
    }

    public VLANNetwork findVlanByNameInNetwork(final Network network, final String name)
    {
        return findUniqueByCriterions(sameNetwork(network),
            Restrictions.eq(VLANNetwork.NAME_PROPERTY, name));

    }

    public VLANNetwork findVlanByVirtualDatacenterId(final VirtualDatacenter virtualDatacenter,
        final Integer vlanId)
    {
        return findUniqueByCriterions(
            Restrictions.eq(VLANNetwork.NETWORK_PROPERTY, virtualDatacenter.getNetwork()),
            Restrictions.eq(PersistentEntity.ID_PROPERTY, vlanId));
    }

    public List<VLANNetwork> findVlanNetworks(final VirtualDatacenter virtualDatacenter)
    {
        assert virtualDatacenter != null;

        Criteria criteria = createCriteria(sameNetwork(virtualDatacenter));
        criteria.addOrder(Order.asc(VLANNetwork.NAME_PROPERTY));
        List<VLANNetwork> result = getResultList(criteria);

        return result;
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

    public boolean isPublic(final VLANNetwork vlan)
    {
        Query query = getSession().createQuery(GET_VLAN_DATACENTER);
        query.setParameter("id", vlan.getId());

        return query.uniqueResult() != null;
    }

    private Criterion nameEqual(final String name)
    {
        assert name != null;

        return Restrictions.eq(VLANNetwork.NAME_PROPERTY, name);
    }

}
