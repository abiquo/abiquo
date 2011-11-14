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

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.wink.common.annotations.Parent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.abiquo.api.resources.AbstractResource;
import com.abiquo.api.resources.EnterpriseResource;
import com.abiquo.api.services.appslibrary.OVFPackageListService;
import com.abiquo.api.transformer.AppsLibraryTransformer;
import com.abiquo.api.util.IRESTBuilder;
import com.abiquo.appliancemanager.transport.OVFPackageInstanceStateDto;
import com.abiquo.appliancemanager.transport.OVFPackageInstancesStateDto;
import com.abiquo.server.core.appslibrary.OVFPackage;
import com.abiquo.server.core.appslibrary.OVFPackageList;
import com.abiquo.server.core.appslibrary.OVFPackageListDto;

@Parent(OVFPackageListsResource.class)
@Path(OVFPackageListResource.OVF_PACKAGE_LIST_PARAM)
@Controller
public class OVFPackageListResource extends AbstractResource
{

    public static final String OVF_PACKAGE_LIST = "ovfPackageList";

    public static final String OVF_PACKAGE_LIST_PARAM = "{" + OVF_PACKAGE_LIST + "}";

    public static final String OVF_PACKAGE_LIST_REPOSITORY_STATUS_PATH = "actions/repositoryStatus";

    public static final String OVF_PACKAGE_LIST_REPOSITORY_STATUS_DATACENTER_QUERY_PARAM =
        "datacenterId";

    @Autowired
    protected OVFPackageListService service;

    @Autowired
    protected AppsLibraryTransformer transformer;

    @GET
    public OVFPackageListDto getOVFPackageList(
        @PathParam(OVF_PACKAGE_LIST) final Integer ovfPackageListId,
        @Context final IRESTBuilder restBuilder) throws Exception
    {
        OVFPackageList ovfPackageList = service.getOVFPackageList(ovfPackageListId);

        return transformer.createTransferObject(ovfPackageList, restBuilder);
    }

    @PUT
    public OVFPackageListDto modifyOVFPackageList(final OVFPackageListDto ovfPackageList,
        @PathParam(OVF_PACKAGE_LIST) final Integer ovfPackageListId,
        @PathParam(EnterpriseResource.ENTERPRISE) final Integer idEnterprise,
        @Context final IRESTBuilder restBuilder) throws Exception
    {
        OVFPackageList d = transformer.createPersistenceObject(ovfPackageList);

        d = service.modifyOVFPackageList(ovfPackageListId, d, idEnterprise);

        return transformer.createTransferObject(d, restBuilder);
    }

    @PUT
    @Consumes(MediaType.TEXT_PLAIN)
    public OVFPackageListDto refreshOVFPackageList(
        @PathParam(OVF_PACKAGE_LIST) final Integer ovfPackageListId,
        @PathParam(EnterpriseResource.ENTERPRISE) final Integer idEnterprise,
        @Context final IRESTBuilder restBuilder) throws Exception
    {
        OVFPackageList d;

        d = service.updateOVFPackageList(idEnterprise, ovfPackageListId);

        return transformer.createTransferObject(d, restBuilder);
    }

    @DELETE
    public void deleteOVFPackageList(@PathParam(OVF_PACKAGE_LIST) final Integer ovfPackageListId)
    {
        service.removeOVFPackageList(ovfPackageListId);
    }

    /**
     * Get the all {@link OVFPackageInstanceStateDto} in the provided
     * {@link DatacenterRepositoryResource} for all the {@link OVFPackage} in the current list.
     */
    @GET
    @Path(OVFPackageListResource.OVF_PACKAGE_LIST_REPOSITORY_STATUS_PATH)
    public OVFPackageInstancesStateDto getOVFPackageListStatus(
        @PathParam(OVF_PACKAGE_LIST) final Integer ovfPackageListId,
        @PathParam(EnterpriseResource.ENTERPRISE) final Integer idEnterprise,
        @QueryParam(OVF_PACKAGE_LIST_REPOSITORY_STATUS_DATACENTER_QUERY_PARAM) final Integer datacenterId,
        @Context final IRESTBuilder restBuilder) throws Exception
    {
        return service
            .getOVFPackageListInstanceStatus(ovfPackageListId, datacenterId, idEnterprise);
    }
}
