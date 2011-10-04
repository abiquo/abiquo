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

import com.abiquo.server.core.cloud.VirtualImage;
import com.abiquo.server.core.enterprise.DatacenterLimits;
import com.abiquo.server.core.enterprise.Enterprise;
import com.abiquo.server.core.enterprise.EnterpriseRep;
import com.abiquo.server.core.infrastructure.Datacenter;
import com.abiquo.server.core.infrastructure.DatacenterDAO;
import com.abiquo.server.core.infrastructure.InfrastructureRep;
import com.abiquo.server.core.infrastructure.Repository;
import com.abiquo.server.core.infrastructure.RepositoryDAO;

@Service
@Transactional(readOnly = true)
public class VirtualImageService
{

    @Autowired
    RepositoryDAO repositoryDao;

    @Autowired
    InfrastructureRep infRepo;

    @Autowired
    EnterpriseRep enterpRep;

    public Repository getDatacenterRepository(final Integer dcId)
    {
        Datacenter datacenter = infRepo.findById(dcId);
        return repositoryDao.findByDatacenter(datacenter);
    }

    /**
     * Get repository for allowed (limits defined) datacenters for the provided enterprise.
     */
    public List<Repository> getDatacenterRepositories(final Integer enterpriseId)
    {
        List<Repository> repos = new LinkedList<Repository>();

        Enterprise enterprise = enterpRep.findById(enterpriseId);
        for (DatacenterLimits dclimit : enterpRep.findLimitsByEnterprise(enterprise))
        {
            repos.add(getDatacenterRepository(dclimit.getDatacenter().getId()));
        }

        return repos;
    }

    public VirtualImage getVirtualImage(final Integer vimageId)
    {
        return enterpRep.findVirtualImageById(vimageId);
    }

    public List<VirtualImage> getVirtualImages(final Integer enterpriseId, final Integer dcId)
    {
        Enterprise enterprise = enterpRep.findById(enterpriseId);
        Datacenter datacenter = infRepo.findById(dcId);
        Repository repository = infRepo.findRepositoryByDatacenter(datacenter);

        return enterpRep.findVirtualImagesByEnterpriseAndRepository(enterprise, repository);
    }
}
