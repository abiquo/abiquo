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
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.abiquo.server.core.common.DefaultEntityCurrentUsed;
import com.abiquo.server.core.common.persistence.DefaultDAOBase;
import com.abiquo.server.core.infrastructure.network.IpPoolManagement;
import com.abiquo.server.core.infrastructure.network.IpPoolManagementDAO;
import com.abiquo.server.core.infrastructure.network.VLANNetwork;
import com.abiquo.server.core.infrastructure.network.VLANNetworkDAO;
import com.abiquo.server.core.infrastructure.storage.StorageRep;
import com.abiquo.server.core.infrastructure.storage.VolumeManagement;
import com.abiquo.server.core.pricing.PricingTemplate;
import com.abiquo.server.core.util.PagedList;
import com.softwarementors.bzngine.entities.PersistentEntity;

@Repository("jpaEnterpriseDAO")
class EnterpriseDAO extends DefaultDAOBase<Integer, Enterprise>
{
    @Autowired
    private StorageRep storageRep;

    @Autowired
    private VLANNetworkDAO vlanNetDAO;

    @Autowired
    private IpPoolManagementDAO ipPoolDao;

    public EnterpriseDAO()
    {
        super(Enterprise.class);
    }

    public EnterpriseDAO(final EntityManager entityManager)
    {
        super(Enterprise.class, entityManager);

        this.storageRep = new StorageRep(entityManager);
        this.vlanNetDAO = new VLANNetworkDAO(entityManager);
        this.ipPoolDao = new IpPoolManagementDAO(entityManager);
    }

    @Override
    public List<Enterprise> findAll()
    {
        return createCriteria().list();
    }

    public List<Enterprise> findAll(Integer firstElem, final Integer numResults)
    {

        // Check if the page requested is bigger than the last one
        Criteria criteria = createCriteria();
        Long total = count();

        if (firstElem >= total.intValue())
        {
            firstElem = total.intValue() - numResults;
        }

        criteria.setFirstResult(firstElem);
        criteria.setMaxResults(numResults);

        List<Enterprise> result = getResultList(criteria);

        com.abiquo.server.core.util.PagedList<Enterprise> page = new PagedList<Enterprise>(result);
        page.setCurrentElement(firstElem);
        page.setPageSize(numResults);
        page.setTotalResults(total.intValue());

        return page;
    }

    public List<Enterprise> findByNameAnywhere(final String name)
    {
        assert name != null;

        Criteria criteria = createCriteria(nameLikeAnywhere(name));
        criteria.addOrder(Order.asc(Enterprise.NAME_PROPERTY));
        List<Enterprise> result = getResultList(criteria);
        return result;
    }

    public List<Enterprise> findByPricingTemplate(Integer firstElem, final PricingTemplate pt,
        final boolean included, final String filterName, final Integer numResults,
        final Integer idEnterprise)
    {
        // Check if the page requested is bigger than the last one

        Criteria criteria = createCriteria(pt, included, filterName, idEnterprise);

        Long total = count(criteria);

        if (firstElem >= total.intValue())
        {
            firstElem = total.intValue() - numResults;
        }

        criteria = createCriteria(pt, included, filterName, idEnterprise);
        criteria.setFirstResult(firstElem);
        criteria.setMaxResults(numResults);

        List<Enterprise> result = getResultList(criteria);

        PagedList<Enterprise> page = new PagedList<Enterprise>();
        page.addAll(result);
        page.setCurrentElement(firstElem);
        page.setPageSize(numResults);
        page.setTotalResults(total.intValue());

        return page;

    }

    private static Criterion nameLikeAnywhere(final String name)
    {
        assert name != null;

        if (StringUtils.isEmpty(name))
        {
            return null;
        }
        return Restrictions.ilike(Enterprise.NAME_PROPERTY, name, MatchMode.ANYWHERE);
    }

    @Override
    public void persist(final Enterprise enterprise)
    {
        assert enterprise != null;
        assert !isManaged(enterprise);
        assert !existsAnyWithName(enterprise.getName());

        super.persist(enterprise);
    }

    public boolean existsAnyWithName(final String name)
    {
        assert !StringUtils.isEmpty(name);

        return existsAnyByCriterions(nameEqual(name));
    }

    private Criterion nameEqual(final String name)
    {
        assert name != null;

        return Restrictions.eq(Enterprise.NAME_PROPERTY, name);
    }

    public boolean existsAnyOtherWithName(final Enterprise enterprise, final String name)
    {
        assert enterprise != null;
        assert isManaged(enterprise);
        assert !StringUtils.isEmpty(name);

        return existsAnyOtherByCriterions(enterprise, nameEqual(name));
    }

    private Criterion differentPricingTemplateOrNull(final PricingTemplate pricingTemplate)
    {
        Disjunction filterDisjunction = Restrictions.disjunction();
        filterDisjunction.add(Restrictions.ne(Enterprise.PRICING_PROPERTY, pricingTemplate));
        filterDisjunction.add(Restrictions.isNull(Enterprise.PRICING_PROPERTY));
        return filterDisjunction;
        // return Restrictions.eq(Enterprise.PRICING_PROPERTY, pricingTemplate);

    }

    private Criterion samePricingTemplate(final PricingTemplate pricingTemplate)
    {
        Disjunction filterDisjunction = Restrictions.disjunction();
        filterDisjunction.add(Restrictions.eq(Enterprise.PRICING_PROPERTY, pricingTemplate));

        return filterDisjunction;
        // return Restrictions.eq(Enterprise.PRICING_PROPERTY, pricingTemplate);

    }

    private Criterion withPricingTemplate()
    {
        Disjunction filterDisjunction = Restrictions.disjunction();
        filterDisjunction.add(Restrictions.isNotNull(Enterprise.PRICING_PROPERTY));

        return filterDisjunction;
    }

    private Criterion withoutPricingTemplate()
    {
        Disjunction filterDisjunction = Restrictions.disjunction();
        filterDisjunction.add(Restrictions.isNull(Enterprise.PRICING_PROPERTY));

        return filterDisjunction;
    }

    private Criterion filterBy(final String filter)
    {
        Disjunction filterDisjunction = Restrictions.disjunction();

        filterDisjunction.add(Restrictions.like(Role.NAME_PROPERTY, '%' + filter + '%'));

        return filterDisjunction;
    }

    private static final String SUM_VM_RESOURCES =
        "select sum(vm.cpu), sum(vm.ram), sum(vm.hd) from virtualmachine vm, hypervisor hy, physicalmachine pm "
            + " where hy.id = vm.idHypervisor and pm.idPhysicalMachine = hy.idPhysicalMachine "// and
            // pm.idState
            // !=
            // 7"
            // //
            // not
            // HA_DISABLED
            + " and vm.idEnterprise = :enterpriseId and vm.state != 'NOT_ALLOCATED' and vm.idHypervisor is not null";

    private static final String SUM_EXTRA_HD_RESOURCES =
        "select sum(r.limitResource) from rasd r, rasd_management rm, virtualdatacenter vdc, virtualmachine vm where r.instanceID = rm.idResource "
            + "and rm.idResourceType = '17' and rm.idVirtualDatacenter = vdc.idVirtualDatacenter and vdc.idEnterprise=:enterpriseId "
            + "and  rm.idVM = vm.idVM and vm.state != 'NOT_ALLOCATED' and vm.idHypervisor is not null";

    public DefaultEntityCurrentUsed getEnterpriseResourceUsage(final int enterpriseId)
    {
        Object[] vmResources =
            (Object[]) getSession().createSQLQuery(SUM_VM_RESOURCES).setParameter("enterpriseId",
                enterpriseId).uniqueResult();

        Long cpu = vmResources[0] == null ? 0 : ((BigDecimal) vmResources[0]).longValue();
        Long ram = vmResources[1] == null ? 0 : ((BigDecimal) vmResources[1]).longValue();
        Long hd = vmResources[2] == null ? 0 : ((BigDecimal) vmResources[2]).longValue();

        BigDecimal extraHd =
            (BigDecimal) getSession().createSQLQuery(SUM_EXTRA_HD_RESOURCES).setParameter(
                "enterpriseId", enterpriseId).uniqueResult();
        Long hdTot = extraHd == null ? hd : hd + extraHd.longValue() * 1024 * 1024;

        Long storage = getStorageUsage(enterpriseId) * 1024 * 1024; // Storage usage is stored in MB
        Long publiIp = getPublicIPUsage(enterpriseId);
        Long vlanCount = getVLANUsage(enterpriseId);

        // TODO repository

        // XXX checking null resource utilization (if any resource allocated)
        DefaultEntityCurrentUsed used = new DefaultEntityCurrentUsed(cpu.intValue(), ram, hdTot);

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
        final List<VolumeManagement> volumes = storageRep.getVolumesByEnterprise(idEnterprise);

        long usedStorage = 0L;
        for (final VolumeManagement vol : volumes)
        {
            usedStorage += vol.getSizeInMB();
        }

        return usedStorage;
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
            ipPoolDao.getPublicNetworkPoolPurchasedByEnterprise(idEnterprise);
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

    public boolean existAnyEnterpriseWithPricingTemplate(final PricingTemplate pricingTemplate)
    {
        return existsAnyByCriterions(samePricingTemplate(pricingTemplate));
    }

    private Criteria createCriteria(final PricingTemplate pricingTemplate, final boolean included,
        final String filter, final Integer enterpriseId)
    {
        Criteria criteria = createCriteria();

        if (included && pricingTemplate != null)
        {

            criteria.add(samePricingTemplate(pricingTemplate));

        }
        else if (included && pricingTemplate == null)
        {

            criteria.add(withPricingTemplate());

        }
        else if (!included && pricingTemplate != null)
        {
            criteria.add(differentPricingTemplateOrNull(pricingTemplate));
        }
        else if (!included && pricingTemplate == null)
        {
            criteria.add(withoutPricingTemplate());
        }
        if (enterpriseId != null)
        {
            criteria.add(Restrictions.eq(PersistentEntity.ID_PROPERTY, enterpriseId));
        }

        if (!StringUtils.isEmpty(filter))
        {
            criteria.add(filterBy(filter));
        }

        return criteria;
    }
}
