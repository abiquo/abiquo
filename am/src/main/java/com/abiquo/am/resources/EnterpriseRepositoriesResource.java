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

package com.abiquo.am.resources;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import org.apache.wink.common.annotations.Workspace;
import org.dmtf.schemas.ovf.envelope._1.EnvelopeType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.abiquo.am.services.download.OVFDocumentFetch;
import com.abiquo.am.services.filesystem.EnterpriseRepositoryFileSystem;
import com.abiquo.am.services.ovfformat.TemplateFromOVFEnvelope;
import com.abiquo.api.resource.AbstractResource;
import com.abiquo.appliancemanager.config.AMConfiguration;
import com.abiquo.appliancemanager.transport.RepositoryConfigurationDto;
import com.abiquo.appliancemanager.transport.TemplateDto;

@Path(EnterpriseRepositoriesResource.ENTERPRISE_REPOSITORY_PATH)
@Controller
@Workspace(workspaceTitle = "Appliance Manager enterprise repository", collectionTitle = "EnterpriseRepositories")
public class EnterpriseRepositoriesResource extends AbstractResource
{
    public static final String ENTERPRISE_REPOSITORY_PATH =
        ApplianceManagerPaths.ENTERPRISE_REPOSITORY_PATH;

    @Autowired
    OVFDocumentFetch validate;

    @GET
    public RepositoryConfigurationDto getConfig()
    {
        RepositoryConfigurationDto configDto = new RepositoryConfigurationDto();
        configDto.setLocation(AMConfiguration.getRepositoryLocation());
        configDto.setCapacityMb(EnterpriseRepositoryFileSystem.getCapacityMb());
        configDto.setRemainingMb(EnterpriseRepositoryFileSystem.getFreeMb());

        return configDto;
    }

    @POST
    @Path("/actions/validate")
    public TemplateDto validate(final EnvelopeType envelope)
    {
        return TemplateFromOVFEnvelope.createTemplateDto("http://am/validation/OK.ovf",
            validate.checkEnvelopeIsValid(envelope));
    }
}
