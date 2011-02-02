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

package com.abiquo.server.core.enterprise;

import java.math.BigDecimal;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.abiquo.server.core.common.DefaultEntityCurrentUsed;
import com.abiquo.server.core.common.persistence.DefaultDAOBase;
import com.abiquo.server.core.enumerator.VirtualMachineState;
import com.abiquo.server.core.infrastructure.network.IpPoolManagement;
import com.abiquo.server.core.infrastructure.network.IpPoolManagementDAO;
import com.abiquo.server.core.infrastructure.network.VLANNetwork;
import com.abiquo.server.core.infrastructure.network.VLANNetworkDAO;
import com.abiquo.server.core.infrastructure.storage.VolumeManagement;
import com.abiquo.server.core.infrastructure.storage.VolumeManagementDAO;
import com.abiquo.server.core.util.PagedList;

@Repository("jpaEnterpriseDAO")
class EnterpriseDAO extends DefaultDAOBase<Integer, Enterprise>
{

    // TODO at repo
    @Autowired
    VolumeManagementDAO volumenManDAO;

    @Autowired
    VLANNetworkDAO vlanNetDAO;

    @Autowired
    IpPoolManagementDAO ipPoolDao;

    public EnterpriseDAO()
    {
        super(Enterprise.class);
    }

    public EnterpriseDAO(EntityManager entityManager)
    {
        super(Enterprise.class, entityManager);
    }

    @Override
    public List<Enterprise> findAll()
    {
        return createCriteria().list();
    }

    public List<Enterprise> findAll(Integer offset, Integer numResults)
    {
        Criteria criteria = createCriteria();
        Long total = count();

        criteria.setFirstResult(offset * numResults);
        criteria.setMaxResults(numResults);

        List<Enterprise> result = getResultList(criteria);

        com.abiquo.server.core.util.PagedList<Enterprise> page = new PagedList<Enterprise>(result);
        page.setCurrentPage(offset);
        page.setPageSize(numResults);
        page.setTotalResults(total.intValue());

        return page;
    }

    public List<Enterprise> findByNameAnywhere(String name)
    {
        assert name != null;

        Criteria criteria = createCriteria(nameLikeAnywhere(name));
        criteria.addOrder(Order.asc(Enterprise.NAME_PROPERTY));
        List<Enterprise> result = getResultList(criteria);
        return result;
    }

    private static Criterion nameLikeAnywhere(String name)
    {
        assert name != null;

        if (StringUtils.isEmpty(name))
        {
            return null;
        }
        return Restrictions.ilike(Enterprise.NAME_PROPERTY, name, MatchMode.ANYWHERE);
    }

    @Override
    public void persist(Enterprise enterprise)
    {
        assert enterprise != null;
        assert !isManaged(enterprise);
        assert !existsAnyWithName(enterprise.getName());

        super.persist(enterprise);
    }

    public boolean existsAnyWithName(String name)
    {
        assert !StringUtils.isEmpty(name);

        return existsAnyByCriterions(nameEqual(name));
    }

    private Criterion nameEqual(String name)
    {
        assert name != null;

        return Restrictions.eq(Enterprise.NAME_PROPERTY, name);
    }

    public boolean existsAnyOtherWithName(Enterprise enterprise, String name)
    {
        assert enterprise != null;
        assert isManaged(enterprise);
        assert !StringUtils.isEmpty(name);

        return existsAnyOtherByCriterions(enterprise, nameEqual(name));
    }

    private static final String SUM_VM_RESOURCES =
        "select sum(vm.cpu), sum(vm.ram), sum(vm.hd) from virtualmachine vm, hypervisor hy, physicalmachine pm "
            + " where hy.id = vm.idHypervisor and pm.idPhysicalMachine = hy.idPhysicalMachine "
            + " and vm.idEnterprise = :enterpriseId and STRCMP(vm.state, :not_deployed) != 0";

    public DefaultEntityCurrentUsed getEnterpriseResourceUsage(int enterpriseId)
    {
        Object[] vmResources =
            (Object[]) getSession().createSQLQuery(SUM_VM_RESOURCES)
                .setParameter("enterpriseId", enterpriseId)
                .setParameter("not_deployed", VirtualMachineState.NOT_DEPLOYED.toString())
                .uniqueResult();

        Long cpu = vmResources[0] == null ? 0 : ((BigDecimal) vmResources[0]).longValue();
        Long ram = vmResources[1] == null ? 0 : ((BigDecimal) vmResources[1]).longValue();
        Long hd = vmResources[2] == null ? 0 : ((BigDecimal) vmResources[2]).longValue();

        Long storage = getStorageUsage(enterpriseId);
        Long publiIp = getPublicIPUsage(enterpriseId);
        Long vlanCount = getVLANUsage(enterpriseId);

        // TODO repository

        // XXX checking null resource utilization (if any resource allocated)
        DefaultEntityCurrentUsed used = new DefaultEntityCurrentUsed(cpu.intValue(), ram, hd);

        used.setStorage(storage);
        used.setPublicIp(publiIp);
        used.setVlanCount(vlanCount);

        return used;
    }

    /**
     * Gets the storage usage for the given enterprise.
     * 
     * @param idEnterprise The enterprise being checked.
     * @return The amount of used storage.
     * @throws PersistenceException If an error occurs.
     */
    private Long getStorageUsage(final Integer idEnterprise)
    {
        // TODO 
        // final List<VolumeManagement> volumes =
        // volumenManDAO.getVolumesFromEnterprise(idEnterprise);
        //
        // long usedStorage = 0L;
        // for (final VolumeManagement vol : volumes)
        // {
        // // TODO sum
        // usedStorage += vol.getRasdRaw().getLimit();
        // }

        return 0l;
    }

    /**
     * Gets the vlan usage for the given enterprise.
     * 
     * @param idEnterprise The enterprise being checked.
     * @return The amount of used vlans.
     * @throws PersistenceException If an error occurs.
     */
    private Long getVLANUsage(final Integer idEnterprise)
    {
        final List<VLANNetwork> vlans = vlanNetDAO.findByEnterprise(idEnterprise);
        // TODO count
        return Long.valueOf(vlans.size());
    }

    /**
     * Gets the public IP usage for the given enterprise.
     * 
     * @param idEnterprise The enterprise being checked.
     * @return The amount of used public IPs.
     * @throws PersistenceException If an error occurs.
     */
    private Long getPublicIPUsage(final Integer idEnterprise)
    {

        final List<IpPoolManagement> publicIPs =
            ipPoolDao.getNetworkPoolPurchasedByEnterprise(idEnterprise);
        // TODO count
        return Long.valueOf(publicIPs.size());
    }

    /**
     * Gets the repository usage for the given enterprise.
     * 
     * @param idEnterprise The enterprise being checked.
     * @return The amount of used repository space.
     * @throws PersistenceException If an error occurs.
     */
    private Long getRepositoryUsage(final Integer idEnterprise)
    {
        // TODO unimplemented

        return 0l;

        // @Autowired
        // DatacenterRep datacenterRep;
        //
        // @Autowired
        // RemoteServiceDAO remoteServiceDao;

        // long repoUsed = 0L;
        //
        // final List<Datacenter> datacenters = datacenterRep.findAll();
        //
        // for (final Datacenter dc : datacenters)
        // {
        //
        // try
        // {
        // final String amUrl =
        // remoteServiceDao.getRemoteServiceUri(dc, RemoteServiceType.APPLIANCE_MANAGER);
        //
        // final EnterpriseRepositoryDto repoData =
        // amStub.getRepository(am.getUri(), String.valueOf(idEnterprise));
        //
        // repoUsed += repoData.getRepositoryEnterpriseUsedMb();
        //
        // }
        // catch (Exception e) // am not defined on this datacenter
        // {
        //
        // }
        // }
        //
        // return repoUsed;
    }
}
