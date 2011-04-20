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
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.abiquo.server.core.cloud.VirtualDatacenter;
import com.abiquo.server.core.common.persistence.DefaultDAOBase;
import com.abiquo.server.core.util.FilterOptions;
import com.abiquo.server.core.util.PagedList;

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
        "       select volman.idManagement as idman, vdc.name as vdcname, virtualapp.name as vaname, "
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
            + "and vi.idEnterprise = :idEnterprise " + "and ( "
            + "rasd.elementName like :filterLike " + "or tier.name like :filterLike " + ")";

    public List<VolumeManagement> getVolumesFromEnterprise(final Integer idEnterprise)
        throws PersistenceException
    {
        Query query =
            getSession().createSQLQuery(
                SQL_VOLUME_MANAGEMENT_GET_VOLUMES_FROM_ENTERPRISE);
        query.setParameter("idEnterprise", idEnterprise);
        query.setParameter("filterLike", "%");

        return getSQLQueryResults(getSession(), query, VolumeManagement.class, 0);
    }

    public List<VolumeManagement> getVolumesByPool(final StoragePool sp)
    {
        Criteria criteria = createCriteria(Restrictions.eq("storagePool", sp));
        return getResultList(criteria);
    }

    public List<VolumeManagement> getVolumesByVirtualDatacenter(final VirtualDatacenter vdc,
        final FilterOptions filterOptions)
    {
        Criteria criteria = createCriteria(Restrictions.eq("virtualDatacenter", vdc));

        if (filterOptions.getAsc() == true)
        {
            criteria.addOrder(Property.forName(filterOptions.getOrderBy()).asc());
        }
        else
        {
            criteria.addOrder(Property.forName(filterOptions.getOrderBy()).desc());
        }

        // Get the total number of entries before filtering the query
        int total = criteria.list().size();

        // Set a rank of results
        criteria.setFirstResult(filterOptions.getStartwith() * filterOptions.getLimit());
        criteria.setMaxResults(filterOptions.getLimit());

        List<VolumeManagement> result = getResultList(criteria);

        PagedList<VolumeManagement> volumeList = new PagedList<VolumeManagement>(result);
        volumeList.setCurrentElement(filterOptions.getStartwith());
        volumeList.setPageSize(filterOptions.getLimit());
        volumeList.setTotalResults(total);

        return volumeList;
    }

    @SuppressWarnings("unchecked")
    public List<VolumeManagement> getVolumesByVirtualDatacenter(final VirtualDatacenter vdc)
    {
        Criteria criteria = createCriteria(Restrictions.eq("virtualDatacenter", vdc));
        return getResultList(criteria);
    }

    public VolumeManagement getVolumeByVirtualDatacenter(final VirtualDatacenter vdc,
        final Integer volumeId)
    {
        Criteria criteria =
            createCriteria(Restrictions.eq("virtualDatacenter", vdc)).add(
                Restrictions.eq("id", volumeId));
        return (VolumeManagement) criteria.uniqueResult();
    }

    public List<VolumeManagement> getVolumesByEnterprise(final Integer id, final FilterOptions filters)
    {
        // Check if the orderBy element is actually one of the available ones
        VolumeManagement.OrderByEnum orderByEnum =
            VolumeManagement.OrderByEnum.fromValue(filters.getOrderBy());
        if (orderByEnum == null)
        {
            return null;
        }
        
        Query query =
            getSession().createSQLQuery(SQL_VOLUME_MANAGEMENT_GET_VOLUMES_FROM_ENTERPRISE + defineOrderBySQL(orderByEnum, filters.getAsc()));
        query.setParameter("idEnterprise", id);
        query.setParameter("filterLike", (filters.getFilter().isEmpty())? "%" : "%" + filters.getFilter() + "%");

        Integer size = getSQLQueryResults(getSession(), query, VolumeManagement.class, 0).size();
        
        query.setFirstResult(filters.getStartwith());
        query.setMaxResults(filters.getLimit());
        PagedList<VolumeManagement> volumes = new PagedList<VolumeManagement>(getSQLQueryResults(getSession(), query, VolumeManagement.class, 0));
        volumes.setTotalResults(size);
        volumes.setPageSize((filters.getLimit() > size)? size: filters.getLimit());
        volumes.setCurrentElement(filters.getStartwith());
        
        return volumes;
        
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

    private String defineOrderBySQL(final VolumeManagement.OrderByEnum orderBy, final Boolean asc)
    {
        StringBuilder queryString = new StringBuilder();
        queryString.append(" order by ");
        switch (orderBy)
        {
            case NAME:
            {
                queryString.append("elementname ");
                break;
            }
            case ID:
            {
                queryString.append("idman ");
                break;
            }
            case TIER:
            {
                queryString.append("tier ");
                break;
            }
            case VIRTUALDATACENTER:
            {
                queryString.append("vdcname ");
                break;
            }
            case VIRTUALMACHINE:
            {
                queryString.append("vmname ");
                break;
            }
            case VIRTUALAPPLIANCE:
            {
                queryString.append("vaname ");
                break;
            }
            case TOTALSIZE:
            {
                queryString.append("size ");
                break;
            }
            case AVAILABLESIZE:
            {
                queryString.append("size-used ");
                break;
            }
            case USEDSIZE:
            {
                queryString.append("used ");
                break;
            }
        }

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

}
