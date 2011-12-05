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

import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;

import org.apache.wink.common.annotations.Parent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.abiquo.api.resources.AbstractResource;
import com.abiquo.api.resources.EnterpriseResource;
import com.abiquo.api.services.InfrastructureService;
import com.abiquo.api.services.appslibrary.DatacenterRepositoryService;
import com.abiquo.api.services.appslibrary.VirtualMachineTemplateService;
import com.abiquo.api.util.IRESTBuilder;
import com.abiquo.model.enumerator.RemoteServiceType;
import com.abiquo.model.rest.RESTLink;
import com.abiquo.model.util.ModelTransformer;
import com.abiquo.server.core.appslibrary.DatacenterRepositoryDto;
import com.abiquo.server.core.enterprise.Enterprise;
import com.abiquo.server.core.enterprise.EnterpriseRep;
import com.abiquo.server.core.infrastructure.Datacenter;
import com.abiquo.server.core.infrastructure.Repository;

/**
 * NOTE: DatacenterRepositoryId is the same as its associated Datacenter.
 */
@Parent(DatacenterRepositoriesResource.class)
@Path(DatacenterRepositoryResource.DATACENTER_REPOSITORY_PARAM)
@Controller
public class DatacenterRepositoryResource extends AbstractResource
{
    public final static String DATACENTER_REPOSITORY = "datacenterrepository";

    public final static String DATACENTER_REPOSITORY_PARAM = "{" + DATACENTER_REPOSITORY + "}";

    public final static String DATACENTER_REPOSITORY_REFRESH_PATH = "/actions/refresh";

    public final static String DATACENTER_REPOSITORY_GET_REFRESH_QUERY_PARAM = "refresh";

    public final static String DATACENTER_REPOSITORY_GET_USAGE_QUERY_PARAM = "usage";

    @Autowired
    private DatacenterRepositoryService repoService;

    @Autowired
    private VirtualMachineTemplateService vmtemplateService;

    @Autowired
    private InfrastructureService infService;

    @Autowired
    private EnterpriseRep enterpRep;

    /**
     * Return the remote repository if exists.
     * 
     * @param refresh, communicate with the EnterpriseRepositoryResource (ApplianceManager
     *            datacenter service) to add vmtemplates include in the repository filesystem.
     * @param includeUsage, communicate with the EnterpriseRepositoryResource (ApplianceManager
     *            datacenter service) in order to include the repository filesytem usage.
     */
    @GET
    public DatacenterRepositoryDto getDatacenterRepository(
        @PathParam(EnterpriseResource.ENTERPRISE) final Integer enterpId,
        @PathParam(DatacenterRepositoryResource.DATACENTER_REPOSITORY) final Integer dcId,
        @QueryParam(DATACENTER_REPOSITORY_GET_REFRESH_QUERY_PARAM) final boolean refresh,
        @QueryParam(DATACENTER_REPOSITORY_GET_USAGE_QUERY_PARAM) final boolean includeUsage,
        @Context final IRESTBuilder restBuilder) throws Exception
    {
        // TODO check enterprise can use the datacenter

        Repository repo = vmtemplateService.getDatacenterRepository(dcId);

        final String amUri =
            infService.getRemoteService(dcId, RemoteServiceType.APPLIANCE_MANAGER).getUri();

        if (refresh)
        {
            refreshDatacenterRepository(enterpId, dcId, restBuilder);
        }

        DatacenterRepositoryDto repositoryDto =
            createTransferObject(repo, enterpId, amUri, restBuilder);

        if (includeUsage)
        {
            return repoService.includeRepositoryUsageFromAm(repositoryDto, enterpId, dcId);
        }

        return repositoryDto;
    }

    @PUT
    @Path(DATACENTER_REPOSITORY_REFRESH_PATH)
    public void refreshDatacenterRepository(
        @PathParam(EnterpriseResource.ENTERPRISE) final Integer enterpId,
        @PathParam(DatacenterRepositoryResource.DATACENTER_REPOSITORY) final Integer dcId,
        @Context final IRESTBuilder restBuilder) throws Exception
    {
        // TODO check enterprise can use the datacenter

        Enterprise enterprise = enterpRep.findById(enterpId);
        Datacenter datacenter = infService.getDatacenter(dcId);

        repoService.synchronizeDatacenterRepository(datacenter, enterprise);
    }

    /**
     * Return the {@link DatacenterRepositoryDto}o object from the POJO {@link Repository}
     */
    protected static DatacenterRepositoryDto createTransferObject(final Repository repo,
        final Integer enterpId, final String amUri, final IRESTBuilder builder) throws Exception
    {
        DatacenterRepositoryDto dto =
            ModelTransformer.transportFromPersistence(DatacenterRepositoryDto.class, repo);

        dto.setRepositoryLocation(repo.getUrl());

        // use datacenterId as self id
        final Integer dcId = repo.getDatacenter().getId();
        dto = addLinks(builder, dto, enterpId, dcId, dcId, amUri);

        return dto;
    }

    private static DatacenterRepositoryDto addLinks(final IRESTBuilder builder,
        final DatacenterRepositoryDto dto, final Integer enterpriseId, final Integer dcId,
        final Integer repoId, final String amUri)
    {
        dto.setLinks(builder.buildDatacenterRepositoryLinks(enterpriseId, dcId, repoId));
        dto.addLink(applianceManagerRepositoryAddress(amUri, enterpriseId));

        return dto;
    }

    private static RESTLink applianceManagerRepositoryAddress(final String amUri,
        final Integer enterpriseId)
    {
        // FIXME buggy
        String erepoUri = String.format("%s/erepos/%s", amUri, enterpriseId.toString());
        return new RESTLink("applianceManagerRepositoryUri", erepoUri);
    }

}
