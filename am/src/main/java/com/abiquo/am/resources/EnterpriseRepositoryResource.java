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
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

import org.apache.wink.common.annotations.Parent;
import org.springframework.stereotype.Controller;

import com.abiquo.am.services.ErepoFactory;
import com.abiquo.am.services.filesystem.EnterpriseRepositoryFileSystem;
import com.abiquo.api.resource.AbstractResource;
import com.abiquo.appliancemanager.config.AMConfigurationManager;
import com.abiquo.appliancemanager.transport.EnterpriseRepositoryDto;

@Parent(EnterpriseRepositoriesResource.class)
@Path(EnterpriseRepositoryResource.ENTERPRISE_REPOSITORY_PARAM)
@Controller
public class EnterpriseRepositoryResource extends AbstractResource
{
    public static final String ENTERPRISE_REPOSITORY = ApplianceManagerPaths.ENTERPRISE_REPOSITORY;

    public static final String ENTERPRISE_REPOSITORY_PARAM = "{" + ENTERPRISE_REPOSITORY + "}";

    private static final String REPOSITORY_LOCATION = AMConfigurationManager.getInstance()
        .getAMConfiguration().getRepositoryLocation();

    @POST
    public void refreshEnterpriseRepository(
        @PathParam(EnterpriseRepositoryResource.ENTERPRISE_REPOSITORY) final String erId)
    {
        ErepoFactory.refreshRepo(erId);
    }

    @GET
    public EnterpriseRepositoryDto getEnterpriseRepository(
        @PathParam(EnterpriseRepositoryResource.ENTERPRISE_REPOSITORY) final String erId,
        @QueryParam("checkCanWrite") final boolean checkCanWrite)
    {
        CheckResource.validate();

        EnterpriseRepositoryDto repo = new EnterpriseRepositoryDto();

        repo.setId(Integer.valueOf(erId));
        repo.setRepositoryLocation(REPOSITORY_LOCATION);
        repo.setRepositoryCapacityMb(EnterpriseRepositoryFileSystem.getCapacityMb());
        repo.setRepositoryRemainingMb(EnterpriseRepositoryFileSystem.getFreeMb());

        repo.setRepositoryEnterpriseUsedMb(ErepoFactory.getRepo(erId).getUsedMb());

        return repo;
    }
}
