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

package com.abiquo.server.core.infrastructure.storage;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.abiquo.model.enumerator.StorageTechnologyType;
import com.abiquo.model.enumerator.VolumeState;
import com.abiquo.server.core.cloud.VirtualDatacenter;
import com.abiquo.server.core.common.persistence.DefaultDAOBase;
import com.abiquo.server.core.infrastructure.management.Rasd;
import com.abiquo.server.core.util.FilterOptions;
import com.abiquo.server.core.util.PagedList;
import com.abiquo.server.core.cloud.VirtualMachine;

@Repository("jpaVolumeManagementDAO")
/* package */class VolumeManagementDAO extends DefaultDAOBase<Integer, VolumeManagement>
{
    public VolumeManagementDAO()
    {
        super(VolumeManagement.class);
    }

    public VolumeManagementDAO(final EntityManager entityManager)
    {
        super(VolumeManagement.class, entityManager);
    }

    /**
     * HQL does not support UNION, but since Volume_management is only related to the Enterprise via
     * the VirtualDataCenter, two queries are needed: - First one retrieves all volumes assigned to
     * any VirtualDataCenter of the specified Enterprise; - Second one retrieves all volumes of a
     * stateful image that is not in use (does not have a VirtualDataCenter) filtering by the
     * enterprise of the stateful image.
     **/
    private final String SQL_VOLUME_MANAGEMENT_GET_VOLUMES_FROM_ENTERPRISE =
        "select volman.idManagement as idman, vdc.name as vdcname, virtualapp.name as vaname, "
            + "virtualmachine.name as vmname, rasd.limitResource as size, "
            + "rasd.reservation as available, volman.usedSize as used, "
            + "rasd.elementName as elementname, volman.state as state, tier.name as tier "
            + "from (volume_management volman, virtualdatacenter vdc, rasd, rasd_management rasdm) "
            + "left join virtualmachine on rasdm.idVM = virtualmachine.idVM "
            + "left join virtualapp on rasdm.idVirtualApp = virtualapp.idVirtualApp "
            + "left join storage_pool on volman.idStorage = storage_pool.idStorage "
            + "left join tier on storage_pool.idTier = tier.id "
            + "where "
            + "volman.idManagement = rasdm.idManagement "
            + "and rasdm.idResource = rasd.instanceID "
            + "and rasdm.idVirtualDataCenter = vdc.idVirtualDataCenter "
            + "and vdc.idEnterprise = :idEnterprise "
            + "and ( "
            + "rasd.elementName like :filterLike "
            + "or virtualmachine.name like :filterLike "
            + "or virtualapp.name like :filterLike "
            + "or vdc.name like :filterLike "
            + "or tier.name like :filterLike "
            + ") "
            + "union "
            + "select volman.idManagement as idman, '' as vdcname, '' as vaname, '' as vmname, "
            + "rasd.limitResource as size, rasd.reservation as available, volman.usedSize as used, "
            + "rasd.elementName as elementname, volman.state as state, tier.name as tier "
            + "from (volume_management volman, virtualimage vi, rasd, rasd_management rasdm) "
            + "left join storage_pool on volman.idStorage = storage_pool.idStorage "
            + "left join tier on storage_pool.idTier = tier.id " + "where "
            + "volman.idImage = vi.idImage " + "and volman.idManagement = rasdm.idManagement "
            + "and rasdm.idResource = rasd.instanceID " + "and rasdm.idVirtualDataCenter is null "
            + "and rasdm.idVirtualApp is null " + "and rasdm.idVM is null "
            + "and vi.idEnterprise = :idEnterprise " + "and (rasd.elementName like :filterLike "
            + "or tier.name like :filterLike )";

    public List<VolumeManagement> getVolumesByPool(final StoragePool sp)
    {
        Criteria criteria = createCriteria(samePool(sp));
        return getResultList(criteria);
    }

    public List<VolumeManagement> getVolumesByVirtualDatacenter(final VirtualDatacenter vdc)
    {
        Criteria criteria = createCriteria(sameVirtualDatacenter(vdc));
        return getResultList(criteria);
    }

    public VolumeManagement getVolumeByVirtualDatacenter(final VirtualDatacenter vdc,
        final Integer volumeId)
    {
        Criteria criteria = createCriteria(sameId(volumeId), sameVirtualDatacenter(vdc));

        return getSingleResultOrNull(criteria);
    }

    public VolumeManagement getVolumeByRasd(final Rasd rasd)
    {
        Criteria criteria = createCriteria(sameRasd(rasd));
        return getSingleResultOrNull(criteria);
    }

    public List<VolumeManagement> getStatefulCandidates(final VirtualDatacenter vdc)
    {
        // Filters on the VolumeManagement entity
        Criteria crit = createCriteria();
        crit.createAlias(VolumeManagement.STORAGE_POOL_PROPERTY, "pool");
        crit.createAlias("pool." + StoragePool.DEVICE_PROPERTY, "device");

        crit.add(sameVirtualDatacenter(vdc));
        crit.add(Restrictions.isNull(VolumeManagement.VIRTUAL_IMAGE_PROPERTY));
        crit.add(Restrictions.eq(VolumeManagement.STATE_PROPERTY, VolumeState.DETACHED));

        crit.add(Restrictions.eq("device." + StorageDevice.STORAGE_TECHNOLOGY_PROPERTY,
            StorageTechnologyType.GENERIC_ISCSI));

        return getResultList(crit);
    }

    public List<VolumeManagement> getVolumesFromEnterprise(final Integer idEnterprise)
        throws PersistenceException
    {
        Query query =
            getSession().createSQLQuery(SQL_VOLUME_MANAGEMENT_GET_VOLUMES_FROM_ENTERPRISE);
        query.setParameter("idEnterprise", idEnterprise);
        query.setParameter("filterLike", "%");

        return getSQLQueryResults(getSession(), query, VolumeManagement.class, 0);
    }

    @SuppressWarnings("unchecked")
    public List<VolumeManagement> getVolumesByPool(final StoragePool sp, final FilterOptions filters)
        throws Exception
    {
        // Check if the orderBy element is actually one of the available ones
        VolumeManagement.OrderByEnum orderByEnum = null;

        try
        {
            orderByEnum = VolumeManagement.OrderByEnum.valueOf(filters.getOrderBy().toUpperCase());
        }
        catch (Exception ex)
        {
            throw new Exception(ex.getMessage());
        }

        String orderBy = defineOrderBy(orderByEnum.getColumnHQL(), filters.getAsc());

        Query query = getSession().getNamedQuery("VOLUMES_BY_POOL");

        String req = query.getQueryString() + orderBy;
        // Add order filter to the query
        Query queryWithOrder = getSession().createQuery(req);
        queryWithOrder.setString("poolId", sp.getId());
        queryWithOrder.setString("filterLike", (filters.getFilter().isEmpty()) ? "%" : "%"
            + filters.getFilter() + "%");

        Integer size = queryWithOrder.list().size();

        queryWithOrder.setFirstResult(filters.getStartwith());
        queryWithOrder.setMaxResults(filters.getLimit());

        PagedList<VolumeManagement> volumesList =
            new PagedList<VolumeManagement>(queryWithOrder.list());
        volumesList.setTotalResults(size);
        volumesList.setPageSize((filters.getLimit() > size) ? size : filters.getLimit());
        volumesList.setCurrentElement(filters.getStartwith());

        return volumesList;
    }

    @SuppressWarnings("unchecked")
    public List<VolumeManagement> getVolumesByVirtualDatacenter(final VirtualDatacenter vdc,
        final FilterOptions filters) throws Exception
    {
        // Check if the orderBy element is actually one of the available ones
        VolumeManagement.OrderByEnum orderByEnum = null;

        try
        {
            orderByEnum = VolumeManagement.OrderByEnum.valueOf(filters.getOrderBy().toUpperCase());
        }
        catch (Exception ex)
        {
            throw new Exception(ex.getMessage());
        }

        String orderBy = defineOrderBy(orderByEnum.getColumnHQL(), filters.getAsc());

        Query query = getSession().getNamedQuery(VolumeManagement.VOLUMES_BY_VDC);

        String req = query.getQueryString() + orderBy;
        // Add order filter to the query
        Query queryWithOrder = getSession().createQuery(req);
        queryWithOrder.setInteger("vdcId", vdc.getId());
        queryWithOrder.setString("filterLike", (filters.getFilter().isEmpty()) ? "%" : "%"
            + filters.getFilter() + "%");

        Integer size = queryWithOrder.list().size();

        queryWithOrder.setFirstResult(filters.getStartwith());
        queryWithOrder.setMaxResults(filters.getLimit());

        PagedList<VolumeManagement> volumesList =
            new PagedList<VolumeManagement>(queryWithOrder.list());
        volumesList.setTotalResults(size);
        volumesList.setPageSize((filters.getLimit() > size) ? size : filters.getLimit());
        volumesList.setCurrentElement(filters.getStartwith());

        return volumesList;
    }

    public List<VolumeManagement> getVolumesByEnterprise(final Integer id,
        final FilterOptions filters)
    {
        // Check if the orderBy element is actually one of the available ones
        VolumeManagement.OrderByEnum orderByEnum = null;

        try
        {
            orderByEnum = VolumeManagement.OrderByEnum.valueOf(filters.getOrderBy().toUpperCase());
        }
        catch (Exception ex)
        {
            // If order is invalid, return null;
            return null;
        }

        Query query =
            getSession().createSQLQuery(
                SQL_VOLUME_MANAGEMENT_GET_VOLUMES_FROM_ENTERPRISE
                    + defineOrderBy(orderByEnum.getColumnSQL(), filters.getAsc()));
        query.setParameter("idEnterprise", id);
        query.setParameter("filterLike",
            (filters.getFilter().isEmpty()) ? "%" : "%" + filters.getFilter() + "%");

        Integer size = getSQLQueryResults(getSession(), query, VolumeManagement.class, 0).size();

        query.setFirstResult(filters.getStartwith());
        query.setMaxResults(filters.getLimit());
        PagedList<VolumeManagement> volumes =
            new PagedList<VolumeManagement>(getSQLQueryResults(getSession(), query,
                VolumeManagement.class, 0));
        volumes.setTotalResults(size);
        volumes.setPageSize((filters.getLimit() > size) ? size : filters.getLimit());
        volumes.setCurrentElement(filters.getStartwith());

        return volumes;

    }

    public VolumeManagement getVolumeFromImage(final Integer idImage)
    {
        Criteria criteria = createCriteria(Restrictions.eq("virtualImage.id", idImage));
        Object obj = criteria.uniqueResult();
        return (VolumeManagement) obj;
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

    private String defineOrderBy(final String orderBy, final Boolean asc)
    {
        StringBuilder queryString = new StringBuilder();
        
        queryString.append(" order by ");
        if(orderBy.equalsIgnoreCase("vol.id")) queryString.append("vol.rasd.id");
        else queryString.append(orderBy);
        queryString.append(" ");

        if (asc)
        {
            queryString.append("asc");
        }
        else
        {
            queryString.append("desc");
        }

        return queryString.toString();
    }

    private static Criterion sameVirtualDatacenter(final VirtualDatacenter vdc)
    {
        return Restrictions.eq(VolumeManagement.VIRTUAL_DATACENTER_PROPERTY, vdc);
    }

    private static Criterion samePool(final StoragePool pool)
    {
        return Restrictions.eq(VolumeManagement.STORAGE_POOL_PROPERTY, pool);
    }

    private static Criterion sameRasd(final Rasd rasd)
    {
        return Restrictions.eq(VolumeManagement.RASD_PROPERTY, rasd);
    }

    private static Criterion sameId(final Integer id)
    {
        return Restrictions.eq(VolumeManagement.ID_PROPERTY, id);
    }
    
     public List<VolumeManagement> getVolumesByVirtualMachine(final VirtualMachine vm)
     {
         Criteria criteria = createCriteria(sameVirtualMachine(vm));
         return getResultList(criteria);
     }
     private static Criterion sameVirtualMachine(final VirtualMachine vm)
     {
         return Restrictions.eq(VolumeManagement.VIRTUAL_MACHINE_PROPERTY, vm);
     }
}
