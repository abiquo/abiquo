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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import javax.persistence.EntityManager;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.abiquo.model.enumerator.VirtualMachineState;
import com.abiquo.server.core.common.DefaultEntityCurrentUsed;
import com.abiquo.server.core.common.persistence.DefaultDAOBase;
import com.abiquo.server.core.enterprise.Enterprise;
import com.abiquo.server.core.util.PagedList;

@Repository("jpaDatacenterDAO")
public class DatacenterDAO extends DefaultDAOBase<Integer, Datacenter>
{

    public DatacenterDAO()
    {
        super(Datacenter.class);
    }

    public DatacenterDAO(final EntityManager entityManager)
    {
        super(Datacenter.class, entityManager);
    }

    private static Criterion equalName(final String name)
    {
        assert !StringUtils.isEmpty(name);

        return Restrictions.eq(Datacenter.NAME_PROPERTY, name);
    }

    public boolean existsAnyWithName(final String name)
    {
        assert !StringUtils.isEmpty(name);

        return this.existsAnyByCriterions(equalName(name));
    }

    public boolean existsAnyOtherWithName(final Datacenter datacenter, final String name)
    {
        assert datacenter != null;
        assert isManaged(datacenter);
        assert !StringUtils.isEmpty(name);

        return this.existsAnyOtherByCriterions(datacenter, equalName(name));
    }

    /**
     * TODO: create queries
     * 
     * @param datacenterId
     * @param enterpriseId
     * @return
     */
    public DefaultEntityCurrentUsed getCurrentResourcesAllocated(final int datacenterId,
        final int enterpriseId)
    {
        Object[] vmResources =
            (Object[]) getSession().createSQLQuery(SUM_VM_RESOURCES).setParameter("datacenterId",
                datacenterId).setParameter("enterpriseId", enterpriseId).setParameter(
                "not_deployed", VirtualMachineState.NOT_DEPLOYED.toString()).uniqueResult();

        Long cpu = vmResources[0] == null ? 0 : ((BigDecimal) vmResources[0]).longValue();
        Long ram = vmResources[1] == null ? 0 : ((BigDecimal) vmResources[1]).longValue();
        Long hd = vmResources[2] == null ? 0 : ((BigDecimal) vmResources[2]).longValue();

        BigDecimal storage =
            (BigDecimal) getSession().createSQLQuery(SUM_STORAGE_RESOURCES).setParameter(
                "datacenterId", datacenterId).setParameter("enterpriseId", enterpriseId)
                .uniqueResult();

        BigInteger publicIps =
            (BigInteger) getSession().createSQLQuery(COUNT_IP_RESOURCES).setParameter(
                "datacenterId", datacenterId).setParameter("enterpriseId", enterpriseId)
                .uniqueResult();

        BigInteger vlan =
            (BigInteger) getSession().createSQLQuery(COUNT_VLAN_RESOURCES).setParameter(
                "datacenterId", datacenterId).setParameter("enterpriseId", enterpriseId)
                .uniqueResult();

        DefaultEntityCurrentUsed used = new DefaultEntityCurrentUsed(cpu.intValue(), ram, hd);

        used.setStorage(storage == null ? 0 : storage.longValue());
        used.setPublicIp(publicIps == null ? 0 : publicIps.longValue());
        used.setVlanCount(vlan == null ? 0 : vlan.longValue());
        return used;
    }

    public List<Enterprise> findEnterprisesByDatacenters(final Datacenter datacenter,
        final Integer firstElem, final Integer numElem)
    {

        // Get the query that counts the total results.
        Query finalQuery = getSession().createQuery(BY_ENT);
        finalQuery.setParameter("datacenter_id", datacenter.getId());
        Integer totalResults = finalQuery.list().size();

        // Get the list of elements
        finalQuery.setFirstResult(firstElem);
        finalQuery.setMaxResults(numElem);

        PagedList<Enterprise> entList = new PagedList<Enterprise>(finalQuery.list());
        entList.setTotalResults(totalResults);
        entList.setPageSize(numElem);
        entList.setCurrentElement(firstElem);

        return entList;
    }

    public static final String BY_ENT =
        " SELECT ent FROM  VirtualDatacenter vdc, " + " VLANNetwork vn, "
            + " DatacenterLimits dcl join dcl.enterprise ent join dcl.datacenter dc"
            + " WHERE vn.network.id = vdc.network.id" + " AND vdc.enterprise.id = ent.id"
            + " AND dc.id = :datacenter_id ";

    private static final String SUM_VM_RESOURCES =
        "select sum(vm.cpu), sum(vm.ram), sum(vm.hd) from virtualmachine vm, hypervisor hy, physicalmachine pm "
            + " where hy.id = vm.idHypervisor and pm.idPhysicalMachine = hy.idPhysicalMachine "
            + " and pm.idDatacenter = :datacenterId and vm.idEnterprise = :enterpriseId and STRCMP(vm.state, :not_deployed) != 0";

    private static final String SUM_STORAGE_RESOURCES =
        "select sum(r.limitResource) "
            + "from volume_management vm, storage_pool sp, storage_device sd, rasd_management rm, virtualdatacenter vdc, rasd r "
            + "where " + "vm.idManagement = rm.idManagement " + "and rm.idResource = r.instanceID "
            + "and vm.idStorage = sp.idStorage " + "and sp.idStorageDevice = sd.id "
            + "and sd.idDataCenter = :datacenterId "
            + "and rm.idVirtualDataCenter = vdc.idVirtualDataCenter "
            + "and vdc.idEnterprise = :enterpriseId";

    private static final String COUNT_IP_RESOURCES =
        "select count(*) from ip_pool_management ipm, network_configuration nc, vlan_network vn, datacenter dc, rasd_management rm, virtualdatacenter vdc "
            + " where ipm.dhcp_service_id=nc.dhcp_service_id and vn.network_configuration_id = nc.network_configuration_id and vn.network_id = dc.network_id and rm.idManagement = ipm.idManagement "
            + " and ipm.mac is not null "
            + " and rm.idVM is not null " /* reserved + use */
            + " and rm.idVirtualDataCenter = vdc.idVirtualDataCenter "
            + " and dc.idDataCenter = :datacenterId and vdc.idEnterprise = :enterpriseId";

    private static final String COUNT_VLAN_RESOURCES =
        "select count(*) from vlan_network vn, datacenter dc, virtualdatacenter vdc "
            + " where vn.network_id= dc.network_id and vdc.networktypeID = vn.network_id "
            + " and dc.idDataCenter = :datacenterId  and vdc.idEnterprise = :enterpriseId";

}
