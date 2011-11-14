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
import javax.ws.rs.Path;

import org.apache.wink.common.annotations.Workspace;
import org.springframework.stereotype.Controller;

import com.abiquo.api.resource.AbstractResource;
import com.abiquo.appliancemanager.config.AMConfiguration;
import com.abiquo.appliancemanager.config.AMConfigurationManager;
import com.abiquo.appliancemanager.transport.RepositoryConfigurationDto;

@Path(EnterpriseRepositoriesResource.ENTERPRISE_REPOSITORY_PATH)
@Controller
@Workspace(workspaceTitle = "Appliance Manager enterprise repository", collectionTitle = "EnterpriseRepositories")
public class EnterpriseRepositoriesResource extends AbstractResource
{
    public static final String ENTERPRISE_REPOSITORY_PATH =
        ApplianceManagerPaths.ENTERPRISE_REPOSITORY_PATH;

    @GET
    public RepositoryConfigurationDto getConfig()
    {
        CheckResource.validate();

        AMConfiguration config = AMConfigurationManager.getInstance().getAMConfiguration();

        RepositoryConfigurationDto configDto = new RepositoryConfigurationDto();
        //configDto.setBrokerUrl(config.getBrokerUrl());
        configDto.setRepositoryLocation(config.getRepositoryLocation());

        return configDto;
    }

}
