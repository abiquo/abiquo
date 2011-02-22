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

package com.abiquo.server.core.statistics;

import java.util.Collection;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.abiquo.server.core.common.DefaultRepBase;

@Repository
public class StatisticsRep extends DefaultRepBase
{
    @Autowired
    CloudUsageDAO cloudUsageDAO;

    @Autowired
    EnterpriseResourcesDAO enterpriseResourcesDAO;

    @Autowired
    DatacenterResourcesDAO dcResourcesDAO;

    @Autowired
    VirtualDatacenterResourcesDAO vdcResourcesDAO;

    @Autowired
    VirtualAppResourcesDAO vappResourcesDAO;

    public StatisticsRep()
    {

    }

    public StatisticsRep(EntityManager em)
    {
        this.entityManager = em;
        this.cloudUsageDAO = new CloudUsageDAO(em);
        this.enterpriseResourcesDAO = new EnterpriseResourcesDAO(em);
        this.dcResourcesDAO = new DatacenterResourcesDAO(em);
        this.vdcResourcesDAO = new VirtualDatacenterResourcesDAO(em);
        this.vappResourcesDAO = new VirtualAppResourcesDAO(em);
    }

    public CloudUsage findCloudUsageByDatacenter(Integer idDatacenter)
    {
        assert idDatacenter != null;

        return this.cloudUsageDAO.findById(idDatacenter);
    }

    /**
     * Total Cloud Usage has some special cases when showing limits
     * 
     * Limits are not total sum of limits by DC. 
     * Instead they must show limits defined for all the enterprises, that is CloudUsage with datacenterId = -1 
     * 
     * 
     * @return
     */
    public CloudUsage findTotalCloudUsage()
    {
    	CloudUsage cuTotal = this.cloudUsageDAO.sumTotalCloudUsage();
    	
    	CloudUsage cuLimits = this.cloudUsageDAO.findById(-1);
    	
    	cuTotal.setPublicIPsReserved(cuLimits.getPublicIPsReserved());
    	cuTotal.setStorageReserved(cuLimits.getStorageReserved());
    	cuTotal.setVirtualCpuReserved(cuLimits.getVirtualCpuReserved());
    	cuTotal.setVirtualMemoryReserved(cuLimits.getVirtualMemoryReserved());
    	cuTotal.setVirtualStorageReserved(cuLimits.getVirtualStorageReserved());
    	
    	return cuTotal;
    }

    public EnterpriseResources findTotalEnterpriseResources()
    {
        return this.enterpriseResourcesDAO.sumTotalResourcesByEnterprise();
    }

    public EnterpriseResources findResourcesByEnterprise(Integer idEnterprise)
    {
        assert idEnterprise != null;

        return this.enterpriseResourcesDAO.findById(idEnterprise);
    }

    public Collection<VirtualAppResources> findVappResourcesByEnterprise(Integer idEnterprise)
    {
        assert idEnterprise != null;

        return this.vappResourcesDAO.findByIdEnterprise(idEnterprise);
    }

    public Collection<VirtualDatacenterResources> findVDCResourcesByEnterprise(Integer idEnterprise)
    {
        assert idEnterprise != null;

        return this.vdcResourcesDAO.findByIdEnterprise(idEnterprise);
    }

    public VirtualDatacenterResources findVDCResourcesById(Integer idVirtualDatacenter)
    {
        assert idVirtualDatacenter != null;

        return this.vdcResourcesDAO.findById(idVirtualDatacenter);
    }

    public DatacenterResources findDatacenterResources(Integer idEnterprise, Integer idDatacenter)
    {
        assert idEnterprise != null;
        assert idDatacenter != null;

        return this.dcResourcesDAO.findByIdEnterpriseAndDatacenter(idEnterprise, idDatacenter);

    }

}
