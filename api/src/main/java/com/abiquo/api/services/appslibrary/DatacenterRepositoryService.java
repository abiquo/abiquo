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

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.abiquo.api.services.DefaultApiService;
import com.abiquo.api.services.InfrastructureService;
import com.abiquo.api.services.appslibrary.event.TemplateFactory;
import com.abiquo.api.services.stub.AMServiceStub;
import com.abiquo.appliancemanager.transport.EnterpriseRepositoryDto;
import com.abiquo.appliancemanager.transport.TemplateDto;
import com.abiquo.server.core.appslibrary.DatacenterRepositoryDto;
import com.abiquo.server.core.appslibrary.VirtualMachineTemplate;
import com.abiquo.server.core.enterprise.Enterprise;
import com.abiquo.server.core.infrastructure.Datacenter;
import com.abiquo.server.core.infrastructure.Repository;

@Service
public class DatacenterRepositoryService extends DefaultApiService
{
    public static final Logger logger = LoggerFactory.getLogger(DatacenterRepositoryService.class);

    @Autowired
    private TemplateFactory toVmtemplate;

    @Autowired
    private AMServiceStub amService;

    @Autowired
    private InfrastructureService infraService;

    /**
     * Request the DOWNLOAD {@link TemplateDto} available in the ApplianceManager and update the
     * {@link VirtualMachineTemplate} repository with new virtual machine templates.
     */
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public void synchronizeDatacenterRepository(final Datacenter datacenter,
        final Enterprise enterprise)
    {
        logger.debug("synchronizing datacenter repository (AM refresh)");

        Repository repo = infraService.getRepository(datacenter);

        List<TemplateDto> disks =
            amService.refreshRepository(enterprise.getId(), datacenter.getId(), repo.getUrl());

        List<VirtualMachineTemplate> insertedVmtemplates =
            toVmtemplate.insertVirtualMachineTemplates(disks, repo);

        // Process existing vmtemplates
        processExistingVirtualMachineTemplates(insertedVmtemplates, datacenter);
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
            EnterpriseRepositoryDto erepoDto = amService.getRepository(datacenterId, enterpriseId);

            repoDto.setRepositoryCapacityMb(erepoDto.getCapacityMb());
            repoDto.setRepositoryRemainingMb(erepoDto.getRemainingMb());
            // TODO enterprise utilization
        }
        catch (Exception e)
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

    /**
     * Post process AM existing images.
     * <p>
     * This method may be overriden in enterprise version to manage virtual image conversions.
     * 
     * @param images The existing images.
     */
    protected void processExistingVirtualMachineTemplates(
        final List<VirtualMachineTemplate> vmtemplates, final Datacenter datacenter)
    {
        if (vmtemplates.isEmpty())
        {
            return;
        }

        if (Boolean.valueOf(System.getProperty("am.conversions.skip", "false")) == Boolean.FALSE)
        {
            toVmtemplate.generateConversions(vmtemplates, datacenter);
        }
        else
        {
            logger.warn("VirtualMachine template conversion avoid after refresh "
                + "(see ''am.conversions.skip'' property)");
        }
    }

}
