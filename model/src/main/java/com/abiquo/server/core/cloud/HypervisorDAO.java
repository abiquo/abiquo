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

package com.abiquo.server.core.cloud;

import javax.persistence.EntityManager;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.abiquo.server.core.common.persistence.DefaultDAOBase;

@Repository("jpaHypervisorDAO")
public class HypervisorDAO extends DefaultDAOBase<Integer, Hypervisor>
{
    public HypervisorDAO()
    {
        super(Hypervisor.class);
    }

    public HypervisorDAO(final EntityManager entityManager)
    {
        super(Hypervisor.class, entityManager);
    }

    public boolean existsAnyWithIp(final String ip)
    {
        assert !StringUtils.isEmpty(ip);

        return existsAnyByCriterions(sameIp(ip, Hypervisor.IP_PROPERTY));
    }

    public boolean existsAnyWithIpService(final String ip)
    {
        assert !StringUtils.isEmpty(ip);

        return existsAnyByCriterions(sameIp(ip, Hypervisor.IP_SERVICE_PROPERTY));
    }

    private Criterion sameIp(final String ip, final String propertyName)
    {
        assert !StringUtils.isEmpty(ip);
        return Restrictions.eq(propertyName, ip);
    }

    private final String QUERY_USED_VDRP = "SELECT vm.vdrpPort " + //
        "FROM com.abiquo.server.core.cloud.VirtualMachine vm, " + //
        "com.abiquo.server.core.cloud.Hypervisor h " + //
        "WHERE vm.hypervisor.id = :idHyper ";

    public List<Integer> getUsedPorts(final int idHyper)
    {
        Query query = getSession().createQuery(QUERY_USED_VDRP);
        query.setParameter("idHyper", idHyper);

        return query.list();
    }

    /**
     * Returns {@link Hypervisor} with same ip and in the same datacenter.
     */
    private final String QUERY_SAME_IP_DATACENTER = "SELECT h " + //
        "FROM com.abiquo.server.core.cloud.Hypervisor h " + //
        "WHERE h.ip = :ip AND h.machine.datacenter.id = :datacenterId";

    /**
     * {@link Hypervisor} with same ip and in the same datacenter.
     * 
     * @param ip {@link Hypervisor} ip.
     * @param datacenterId {@link Hypervisor} machines datacenter.
     * @return false is there is no other {@link Hypervisor} with same ip and same datacenter
     *         boolean
     */
    public boolean existsAnyWithIpAndDatacenter(final String ip, final Integer datacenterId)
    {
        Query query = getSession().createQuery(QUERY_SAME_IP_DATACENTER);
        query.setParameter("ip", ip);
        query.setParameter("datacenterId", datacenterId);

        return !query.list().isEmpty();
    }

    /**
     * Returns {@link Hypervisor} with same ipService and in the same datacenter.
     */
    private final String QUERY_SAME_IP_SERVICE_DATACENTER = "SELECT h " + //
        "FROM com.abiquo.server.core.cloud.Hypervisor h " + //
        "WHERE h.ipService = :ip AND h.machine.datacenter.id = :datacenterId";

    /**
     * {@link Hypervisor} with same ipService and in the same datacenter.
     * 
     * @param ip {@link Hypervisor} ip.
     * @param datacenterId {@link Hypervisor} machines datacenter.
     * @return false is there is no other {@link Hypervisor} with same ipService and same datacenter
     *         boolean
     */
    public boolean existsAnyWithIpServiceAndDatacenter(final String ip, final Integer datacenterId)
    {
        Query query = getSession().createQuery(QUERY_SAME_IP_SERVICE_DATACENTER);
        query.setParameter("ip", ip);
        query.setParameter("datacenterId", datacenterId);

        return !query.list().isEmpty();
    }
}
