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
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.abiquo.api.exceptions.APIError;
import com.abiquo.api.services.appslibrary.event.TemplateFactory;
import com.abiquo.appliancemanager.client.ApplianceManagerResourceStubImpl;
import com.abiquo.appliancemanager.client.ApplianceManagerResourceStubImpl.ApplianceManagerStubException;
import com.abiquo.appliancemanager.transport.EnterpriseRepositoryDto;
import com.abiquo.appliancemanager.transport.TemplateDto;
import com.abiquo.appliancemanager.transport.TemplateStateDto;
import com.abiquo.appliancemanager.transport.TemplatesStateDto;
import com.abiquo.appliancemanager.transport.TemplateStatusEnumType;
import com.abiquo.server.core.appslibrary.DatacenterRepositoryDto;
import com.abiquo.server.core.appslibrary.VirtualMachineTemplate;
import com.abiquo.server.core.enterprise.Enterprise;
import com.abiquo.server.core.infrastructure.Datacenter;
import com.abiquo.server.core.infrastructure.Repository;

@Service
public class DatacenterRepositoryService extends DefaultApiServiceWithApplianceManagerClient
{
    public static final Logger logger = LoggerFactory.getLogger(DatacenterRepositoryService.class);

    @Autowired
    private TemplateFactory toVmtemplate;

    /**
     * Request the DOWNLOAD {@link TemplateDto} available in the ApplianceManager and
     * update the {@link VirtualMachineTemplate} repository with new virtual machine templates.
     */
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
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
    public DatacenterRepositoryDto includeRepositoryUsageFromAm(
        final DatacenterRepositoryDto repoDto, final Integer enterpriseId,
        final Integer datacenterId)
    {
        try
        {
            ApplianceManagerResourceStubImpl amStub = getApplianceManagerClient(datacenterId);
            EnterpriseRepositoryDto erepoDto = amStub.getRepository(String.valueOf(enterpriseId));

            repoDto.setRepositoryCapacityMb(erepoDto.getCapacityMb());
            repoDto.setRepositoryRemainingMb(erepoDto.getRemainingMb());
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
        final ApplianceManagerResourceStubImpl amStub)
    {
        final String repositoryLocation = amStub.getRepositoryConfiguration().getLocation();

        final Repository repo = infService.getRepository(datacenter);

        if (!repo.getUrl().equalsIgnoreCase(repositoryLocation))
        {
            addConflictErrors(APIError.VMTEMPLATE_REPOSITORY_CHANGED);
            flushErrors();
        }

        return repo;
    }

    private void refreshRepository(final Integer idEnterprise, final Repository repo,
        final ApplianceManagerResourceStubImpl amStub)
    {

        List<TemplateDto> disks = new LinkedList<TemplateDto>();
        for (String ovfid : getAvailableOVFPackageInstance(idEnterprise, amStub))
        {
            try
            {
                TemplateDto packageInstance =
                    amStub.getTemplate(String.valueOf(idEnterprise), ovfid);
                disks.add(packageInstance);
            }
            catch (ApplianceManagerStubException e)
            {
                logger.error("Can not initialize VirtualMachineTemplate from ovf [{}]", ovfid);
            }
        }

        List<VirtualMachineTemplate> insertedVmtemplates =
            toVmtemplate.insertVirtualMachineTemplates(disks, repo);

        // Process existing vmtemplates
        processExistingVirtualMachineTemplates(insertedVmtemplates);
    }

    /**
     * Returns OVF ids of the DOWNLOADED {@link TemplateDto} in the enterprise repository
     */
    private List<String> getAvailableOVFPackageInstance(final Integer idEnterprise,
        final ApplianceManagerResourceStubImpl amStub)
    {
        List<String> ovfids = new LinkedList<String>();

        try
        {
            TemplatesStateDto list =
                amStub.getTemplatesState(idEnterprise.toString());

            for (TemplateStateDto status : list.getCollection())
            {
                if (status.getStatus() == TemplateStatusEnumType.DOWNLOAD)
                {
                    ovfids.add(status.getOvfId());
                }
            }
        }
        catch (ApplianceManagerStubException e)
        {
            addConflictErrors(APIError.VMTEMPLATE_SYNCH_DC_REPO);
            flushErrors();
        }

        return ovfids;
    }

    /**
     * Post process AM existing vmtemplates.
     * <p>
     * This method may be overriden in enterprise version to manage virtual machine template
     * conversions.
     * 
     * @param vmtemplates The existing vmtemplates.
     */
    protected void processExistingVirtualMachineTemplates(
        final Collection<VirtualMachineTemplate> vmtemplates)
    {
        // Do nothing
    }

}
