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

package com.abiquo.api.services.appslibrary;

import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.abiquo.api.exceptions.APIError;
import com.abiquo.api.services.DefaultApiService;
import com.abiquo.api.services.EnterpriseService;
import com.abiquo.api.services.InfrastructureService;
import com.abiquo.server.core.appslibrary.AppsLibraryRep;
import com.abiquo.server.core.appslibrary.Category;
import com.abiquo.server.core.appslibrary.VirtualImage;
import com.abiquo.server.core.enterprise.DatacenterLimits;
import com.abiquo.server.core.enterprise.Enterprise;
import com.abiquo.server.core.infrastructure.Datacenter;
import com.abiquo.server.core.infrastructure.Repository;
import com.abiquo.server.core.infrastructure.RepositoryDAO;

@Service
public class VirtualImageService extends DefaultApiService
{
    @Autowired
    private RepositoryDAO repositoryDao;

    @Autowired
    private InfrastructureService infrastructureService;

    @Autowired
    private EnterpriseService enterpriseService;

    @Autowired
    private AppsLibraryRep appsLibraryRep;

    @Autowired
    private CategoryService categoryService;

    @Transactional(readOnly = true)
    public Repository getDatacenterRepository(final Integer dcId)
    {
        Datacenter datacenter = infrastructureService.getDatacenter(dcId);
        return repositoryDao.findByDatacenter(datacenter);
    }

    /**
     * Get repository for allowed (limits defined) datacenters for the provided enterprise.
     */
    @Transactional(readOnly = true)
    public List<Repository> getDatacenterRepositories(final Integer enterpriseId)
    {
        List<Repository> repos = new LinkedList<Repository>();

        for (DatacenterLimits dclimit : enterpriseService.findLimitsByEnterprise(enterpriseId))
        {
            repos.add(getDatacenterRepository(dclimit.getDatacenter().getId()));
        }

        return repos;
    }

    @Transactional(readOnly = true)
    public VirtualImage getVirtualImage(final Integer enterpriseId, final Integer datacenterId,
        final Integer virtualImageId)
    {
        // Check that the enterprise can use the datacenter (also checks enterprise and datacenter
        // exists)
        checkEnterpriseCanUseDatacenter(enterpriseId, datacenterId);

        VirtualImage virtualImage = appsLibraryRep.findVirtualImageById(virtualImageId);
        if (virtualImage == null)
        {
            addNotFoundErrors(APIError.NON_EXISTENT_VIRTUALIMAGE);
            flushErrors();
        }

        return virtualImage;
    }

    @Transactional(readOnly = true)
    public List<VirtualImage> getVirtualImages(final Integer enterpriseId,
        final Integer datacenterId)
    {
        checkEnterpriseCanUseDatacenter(enterpriseId, datacenterId);

        Enterprise enterprise = enterpriseService.getEnterprise(enterpriseId);
        Datacenter datacenter = infrastructureService.getDatacenter(datacenterId);

        Repository repository = infrastructureService.getRepository(datacenter);

        return findVirtualImagesByEnterpriseAndRepository(enterprise, repository);
    }

    @Transactional(readOnly = true)
    public List<VirtualImage> findVirtualImageByEnterprise(final Enterprise enterprise)
    {
        return appsLibraryRep.findVirtualImagesByEnterprise(enterprise);
    }

    @Transactional(readOnly = true)
    public List<VirtualImage> findVirtualImagesByEnterpriseAndRepository(
        final Enterprise enterprise, final Repository repository)
    {
        return appsLibraryRep.findVirtualImagesByEnterpriseAndRepository(enterprise, repository);
    }

    @Transactional(readOnly = true)
    public List<VirtualImage> findStatefulVirtualImagesByDatacenter(final Integer enterpriseId,
        final Integer datacenterId)
    {
        checkEnterpriseCanUseDatacenter(enterpriseId, datacenterId);

        Datacenter datacenter = infrastructureService.getDatacenter(datacenterId);
        return appsLibraryRep.findStatefulVirtualImagesByDatacenter(datacenter);
    }

    @Transactional(readOnly = true)
    public List<VirtualImage> findStatefulVirtualImagesByCategoryAndDatacenter(
        final Integer enterpriseId, final Integer datacenterId, final Integer categoryId)
    {
        checkEnterpriseCanUseDatacenter(enterpriseId, datacenterId);

        Datacenter datacenter = infrastructureService.getDatacenter(datacenterId);
        Category category = categoryService.getCategory(categoryId);

        return appsLibraryRep
            .findStatefulVirtualImagesByCategoryAndDatacenter(category, datacenter);
    }

    /**
     * Checks the enterprise and datacenter exists and have a limits relation (datacenter allowed by
     * enterprise).
     */
    private void checkEnterpriseCanUseDatacenter(final Integer enterpriseId,
        final Integer datacenterId)
    {
        DatacenterLimits limits =
            enterpriseService.findLimitsByEnterpriseAndDatacenter(enterpriseId, datacenterId);
        if (limits == null)
        {
            addConflictErrors(APIError.ENTERPRISE_NOT_ALLOWED_DATACENTER);
            flushErrors();
        }
    }
}
