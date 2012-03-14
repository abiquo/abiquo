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

package com.abiquo.api.resources.appslibrary;

import static com.abiquo.api.resources.appslibrary.DatacenterRepositoryResource.createTransferObject;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;

import org.apache.wink.common.annotations.Parent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.abiquo.api.resources.AbstractResource;
import com.abiquo.api.resources.EnterpriseResource;
import com.abiquo.api.services.InfrastructureService;
import com.abiquo.api.services.appslibrary.VirtualMachineTemplateService;
import com.abiquo.api.util.IRESTBuilder;
import com.abiquo.model.enumerator.RemoteServiceType;
import com.abiquo.server.core.appslibrary.DatacenterRepositoriesDto;
import com.abiquo.server.core.infrastructure.Repository;

@Parent(EnterpriseResource.class)
@Path(DatacenterRepositoriesResource.DATACENTER_REPOSITORIES_PATH)
@Controller
public class DatacenterRepositoriesResource extends AbstractResource
{

    public static final String DATACENTER_REPOSITORIES_PATH = "datacenterrepositories";

    @Autowired
    private VirtualMachineTemplateService vmtemplateService;

    @Autowired
    private InfrastructureService infService;

    /**
     * Returns all datacenter repositories from an enterprise
     * 
     * @title Retrive all datacenter repositories
     * @param enterpId identifier of the enterprise
     * @param restBuilder a Context-injected object to create the links of the Dto
     * @return a {DatacenterRepositoriesDto} object with all datacenter reposritories from an
     *         enterprise
     * @throws Exception
     */
    @GET
    @Produces(DatacenterRepositoriesDto.MEDIA_TYPE)
    public DatacenterRepositoriesDto getDatacenterRepositories(
        @PathParam(EnterpriseResource.ENTERPRISE) final Integer enterpId,
        @Context final IRESTBuilder restBuilder) throws Exception
    {
        DatacenterRepositoriesDto reposDto = new DatacenterRepositoriesDto();

        List<Repository> all = vmtemplateService.getDatacenterRepositories(enterpId);

        if (all != null && !all.isEmpty())
        {
            for (Repository rep : all)
            {
                final String amUri =
                    infService.getRemoteService(rep.getDatacenter().getId(),
                        RemoteServiceType.APPLIANCE_MANAGER).getUri();

                reposDto.add(createTransferObject(rep, enterpId, amUri, restBuilder));
            }
        }

        return reposDto;
    }

}
