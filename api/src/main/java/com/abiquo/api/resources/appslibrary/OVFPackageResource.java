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

import javax.annotation.Resource;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response.Status;

import org.apache.wink.common.annotations.Parent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.abiquo.api.exceptions.APIError;
import com.abiquo.api.exceptions.NotFoundException;
import com.abiquo.api.resources.AbstractResource;
import com.abiquo.api.resources.EnterpriseResource;
import com.abiquo.api.services.InfrastructureService;
import com.abiquo.api.services.appslibrary.OVFPackageService;
import com.abiquo.api.services.stub.ApplianceManagerStub;
import com.abiquo.api.transformer.AppsLibraryTransformer;
import com.abiquo.api.util.IRESTBuilder;
import com.abiquo.appliancemanager.transport.OVFPackageInstanceStatusDto;
import com.abiquo.appliancemanager.transport.OVFPackageInstanceStatusType;
import com.abiquo.model.enumerator.RemoteServiceType;
import com.abiquo.server.core.appslibrary.OVFPackage;
import com.abiquo.server.core.appslibrary.OVFPackageDto;
import com.abiquo.server.core.infrastructure.RemoteService;

@Parent(OVFPackagesResource.class)
@Path(OVFPackageResource.OVF_PACKAGE_PARAM)
@Controller
public class OVFPackageResource extends AbstractResource
{

    public static final String OVF_PACKAGE = "ovfpackage";

    public static final String OVF_PACKAGE_PARAM = "{" + OVF_PACKAGE + "}";

    public static final String INSTALL_ACTION = "actions/install";

    public static final String INSTALL_TARGET_QUERY_PARAM = "iddatacenter";

    /** Used to know where the AM is located on the current datacenter. */
    // @Autowired
    @Resource(name = "infrastructureService")
    InfrastructureService remoteServices;

    /** Used to consume the remote service Appliance Manager. */
    @Autowired
    ApplianceManagerStub amStub;

    /** Internal logic. */
    @Autowired
    OVFPackageService service;

    /** Can not be used ModelTransformer duet Category, Icon and Format. */
    @Autowired
    AppsLibraryTransformer transformer;

    @GET
    public OVFPackageDto getOVFPackage(@PathParam(OVF_PACKAGE) final Integer ovfPackageId,
        @Context final IRESTBuilder restBuilder) throws Exception
    {
        OVFPackage ovfPackage = service.getOVFPackage(ovfPackageId);
        if (ovfPackage == null)
        {
            throw new NotFoundException(APIError.NON_EXISTENT_OVF_PACKAGE);
        }

        return transformer.createTransferObject(ovfPackage, restBuilder);
    }

    @PUT
    public OVFPackageDto modifyOVFPackage(final OVFPackageDto ovfPackage,
        @PathParam(OVF_PACKAGE) final Integer ovfPackageId,
        @PathParam(EnterpriseResource.ENTERPRISE) final Integer idEnterprise,
        @Context final IRESTBuilder restBuilder) throws Exception
    {
        OVFPackage d = transformer.createPersistenceObject(ovfPackage);

        d = service.modifyOVFPackage(ovfPackageId, d, idEnterprise);

        return transformer.createTransferObject(d, restBuilder);
    }

    @DELETE
    public void deleteOVFPackage(@PathParam(OVF_PACKAGE) final Integer ovfPackageId)
    {
        service.removeOVFPackage(ovfPackageId);
    }

    @POST
    @Path(OVFPackageResource.INSTALL_ACTION)
    public Response installOVFPackageOnDatacenter(@Context final UriInfo uriInfo,
        @Context final IRESTBuilder restBuilder,
        @PathParam(EnterpriseResource.ENTERPRISE) final Integer idEnterprise,
        @PathParam(OVF_PACKAGE) final Integer ovfPackageId,
        @QueryParam(INSTALL_TARGET_QUERY_PARAM) final String idDatacenter) throws Exception
    {
        Response response;

        final String amUri = getApplianceManagerAddressOnDatacenter(Integer.valueOf(idDatacenter));
        final String ovfLocation = service.getOVFPackage(ovfPackageId).getUrl();

        OVFPackageInstanceStatusDto stat =
            amStub.installOVFPackage(amUri, String.valueOf(idEnterprise), ovfLocation);

        OVFPackageInstanceStatusType status = stat.getOvfPackageStatus();
        switch (status)
        {
            case NOT_DOWNLOAD:
                response = Response.status(Status.NOT_FOUND).build();
                break;
            case DOWNLOAD:
                response = Response.status(Status.CREATED).build();
                break;
            case DOWNLOADING:
                final String progress = String.valueOf(stat.getProgress());
                response = Response.status(Status.ACCEPTED).entity(progress).build();
                break;
            case ERROR:
                final String error = stat.getErrorCause();
                response = Response.status(Status.INTERNAL_SERVER_ERROR).entity(error).build();
                break;

            default:
                final String cause =
                    String.format("Can not determine the OVFPackage status [%s]", status.name());
                response = Response.status(Status.INTERNAL_SERVER_ERROR).entity(cause).build();
                break;
        }

        return response;
    }

    private String getApplianceManagerAddressOnDatacenter(final Integer idDatacenter)
    {
        RemoteService rs =
            remoteServices.getRemoteService(idDatacenter, RemoteServiceType.APPLIANCE_MANAGER);

        if (rs == null)
        {
            final String cause =
                String.format("The provided Datacenter [id %s]"
                    + " do not have any ApplianceManager remote service configured", idDatacenter);
            final Response resp = Response.status(Status.PRECONDITION_FAILED).entity(cause).build();
            throw new WebApplicationException(resp);
        }

        return rs.getUri();
    }

}
