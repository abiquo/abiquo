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
