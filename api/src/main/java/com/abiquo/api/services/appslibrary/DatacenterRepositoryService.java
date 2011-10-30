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

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.abiquo.api.exceptions.APIError;
import com.abiquo.api.services.appslibrary.event.OVFPackageInstanceToVirtualImage;
import com.abiquo.appliancemanager.client.ApplianceManagerResourceStubImpl;
import com.abiquo.appliancemanager.client.ApplianceManagerResourceStubImpl.ApplianceManagerStubException;
import com.abiquo.appliancemanager.transport.EnterpriseRepositoryDto;
import com.abiquo.appliancemanager.transport.OVFPackageInstanceDto;
import com.abiquo.appliancemanager.transport.OVFPackageInstanceStateDto;
import com.abiquo.appliancemanager.transport.OVFPackageInstancesStateDto;
import com.abiquo.appliancemanager.transport.OVFStatusEnumType;
import com.abiquo.server.core.appslibrary.DatacenterRepositoryDto;
import com.abiquo.server.core.appslibrary.VirtualImage;
import com.abiquo.server.core.enterprise.Enterprise;
import com.abiquo.server.core.infrastructure.Datacenter;
import com.abiquo.server.core.infrastructure.Repository;

@Service
public class DatacenterRepositoryService extends DefaultApiServiceWithApplianceManagerClient
{
    public static final Logger logger = LoggerFactory.getLogger(DatacenterRepositoryService.class);

    @Autowired
    private OVFPackageInstanceToVirtualImage toimage;

    /**
     * Request the DOWNLOAD {@link OVFPackageInstanceDto} available in the ApplianceManager and
     * update the {@link VirtualImage} repository with new images.
     */
    public void synchronizeDatacenterRepository(final Datacenter datacenter,
        final Enterprise enterprise)
    {
        logger.debug("synchronizing datacenter repository (AM refresh)");

        ApplianceManagerResourceStubImpl amStub = getApplianceManagerClient(datacenter.getId());

        Repository repo = checkRepositoryLocation(datacenter, amStub);
        refreshRepository(enterprise.getId(), repo, amStub);

    }

    /**
     * Request to the AM EnterpriseRepositoryResource the usage of the repository filesystem
     * <p>
     * TODO usage shared accros all enterprises
     */
    public DatacenterRepositoryDto includeRepositoryUsageFromAm(DatacenterRepositoryDto repoDto,
        final Integer enterpriseId, final Integer datacenterId)
    {
        try
        {
            ApplianceManagerResourceStubImpl amStub = getApplianceManagerClient(datacenterId);
            EnterpriseRepositoryDto erepoDto = amStub.getRepository(String.valueOf(enterpriseId));

            repoDto.setRepositoryCapacityMb(erepoDto.getRepositoryCapacityMb());
            repoDto.setRepositoryRemainingMb(erepoDto.getRepositoryRemainingMb());
            // TODO enterprise utilization
        }
        catch (Exception e) // TODO only if timeout getting usage
        {
            // TODO trace or propagate the exception
            logger.error(String.format("Can not obtain the repository usage info "
                + "of the Datacenter [%s] for the Enterprise [%s]. "
                + "NFS could be busy (check it later).", datacenterId, enterpriseId), e);

            repoDto.setRepositoryCapacityMb(0);
            repoDto.setRepositoryRemainingMb(0);
        }

        return repoDto;
    }

    private Repository checkRepositoryLocation(final Datacenter datacenter,
        ApplianceManagerResourceStubImpl amStub)
    {
        final String repositoryLocation = amStub.getRepositoryConfiguration().getRepositoryLocation();

        final Repository repo = infService.getRepository(datacenter);

        if (!repo.getUrl().equalsIgnoreCase(repositoryLocation))
        {
            addConflictErrors(APIError.VIMAGE_REPOSITORY_CHANGED);
            flushErrors();
        }

        return repo;
    }

    private void refreshRepository(final Integer idEnterprise, final Repository repo,
        final ApplianceManagerResourceStubImpl amStub)
    {

        List<OVFPackageInstanceDto> disks = new LinkedList<OVFPackageInstanceDto>();
        for (String ovfid : getAvailableOVFPackageInstance(idEnterprise, amStub))
        {
            try
            {
                OVFPackageInstanceDto packageInstance =
                    amStub.getOVFPackageInstance(String.valueOf(idEnterprise), ovfid);
                disks.add(packageInstance);
            }
            catch (ApplianceManagerStubException e)
            {
                logger.error("Can not initialize VirtualImage from ovf [{}]", ovfid);
            }
        }

        List<VirtualImage> insertedImages = toimage.insertVirtualImages(disks, repo);

        // Process existing images
        processExistingImages(insertedImages);
    }

    /**
     * Returns OVF ids of the DOWNLOADED {@link OVFPackageInstanceDto} in the enterprise repository
     */
    private List<String> getAvailableOVFPackageInstance(final Integer idEnterprise,
        ApplianceManagerResourceStubImpl amStub)
    {
        List<String> ovfids = new LinkedList<String>();

        try
        {
            OVFPackageInstancesStateDto list =
                amStub.getOVFPackagInstanceStatusList(idEnterprise.toString());

            for (OVFPackageInstanceStateDto status : list.getCollection())
            {
                if (status.getStatus() == OVFStatusEnumType.DOWNLOAD)
                {
                    ovfids.add(status.getOvfId());
                }
            }
        }
        catch (ApplianceManagerStubException e)
        {
            addConflictErrors(APIError.VIMAGE_SYNCH_DC_REPO);
            flushErrors();
        }

        return ovfids;
    }

    /**
     * Post process AM existing images.
     * <p>
     * This method may be overriden in enterprise version to manage virtual image conversions.
     * 
     * @param images The existing images.
     */
    protected void processExistingImages(final Collection<VirtualImage> images)
    {
        // Do nothing
    }

}
