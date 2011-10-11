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

import static com.abiquo.am.services.OVFPackageConventions.ovfUrl;

import java.net.URI;
import java.net.URISyntaxException;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.lang.StringUtils;
import org.apache.wink.common.annotations.Parent;
import org.dmtf.schemas.ovf.envelope._1.EnvelopeType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.abiquo.am.exceptions.AMError;
import com.abiquo.am.services.EnterpriseRepositoryService;
import com.abiquo.am.services.OVFPackageInstanceFileSystem;
import com.abiquo.am.services.OVFPackageInstanceService;
import com.abiquo.appliancemanager.exceptions.AMException;
import com.abiquo.appliancemanager.exceptions.DownloadException;
import com.abiquo.appliancemanager.transport.OVFPackageInstanceDto;
import com.abiquo.appliancemanager.transport.OVFPackageInstanceStatusDto;
import com.abiquo.appliancemanager.transport.OVFPackageInstanceStatusType;

@Parent(OVFPackageInstancesResource.class)
@Path(OVFPackageInstanceResource.OVFPI_PATH)
@Controller(value = "ovfPackageInstanceResource")
public class OVFPackageInstanceResource extends AbstractResource
{

    public static final String OVFPI = ApplianceManagerPaths.OVFPI;

    /**
     * The resource parameter matching configuration.
     * <p>
     * Must override default regular expression in order to be able to match complete URIs as the
     * OVFPackageInstance identifier.
     */
    public static final String OVFPI_PARAM = "{" + OVFPI + ": .*}"; // FIXME take care of .*

    /** The resource path. */
    public static final String OVFPI_PATH = OVFPI_PARAM;

    private final static String HEADER_PROGRESS = "progress";

    private final static String QUERY_PARAM_GET_FORMAT = "format";

    @Autowired
    OVFPackageInstanceService service;

    @HEAD
    public Response getOVFPackageDeployProgress(
        @PathParam(EnterpriseRepositoryResource.ENTERPRISE_REPOSITORY) String idEnterprise,
        @PathParam(OVFPackageInstanceResource.OVFPI) String ovfIdIn) throws DownloadException
    {
        final String ovfId = ovfUrl(ovfIdIn);

        OVFPackageInstanceStatusDto status =
            service.getOVFPackageStatusIncludeProgress(ovfId, idEnterprise);

        switch (status.getOvfPackageStatus())
        {
            case NOT_DOWNLOAD:
                return Response.status(Status.NOT_FOUND).build();

            case DOWNLOAD:
                return Response.status(Status.CREATED).build(); // TODO location(arg0);

            case DOWNLOADING:
                final String progress = String.valueOf(status.getProgress());
                return Response.status(Status.ACCEPTED).header(HEADER_PROGRESS, progress).build();

            case ERROR:
                return Response.status(Status.INTERNAL_SERVER_ERROR).entity(status.getErrorCause())
                    .build();

                // XXX deleted
            default:
                return Response.status(Status.NOT_FOUND)
                    .entity("UNKNOW STATUS:" + status.getOvfPackageStatus().name()).build();
        }
    }

    @GET
    public Response getOVFPackageInstance(@Context UriInfo uriInfo,
        @PathParam(EnterpriseRepositoryResource.ENTERPRISE_REPOSITORY) String idEnterprise,
        @PathParam(OVFPackageInstanceResource.OVFPI) String ovfIdIn,
        @QueryParam(QUERY_PARAM_GET_FORMAT) String format)
    {
        // XXX can specify the media type

        final String ovfId = ovfUrl(ovfIdIn);

        if (format == null || format.isEmpty() || format.equals("ovfpi"))
        {
            return Response.ok(getOVFPackageInstance(idEnterprise, ovfId)).build();
        }
        else if (format.equals("status"))
        {
            return evalStatus(idEnterprise, ovfId);
        }
        else if (format.equals("envelope"))
        {
            return Response.ok(getOVFEnvelope(idEnterprise, ovfId)).build();
        }
        else if (format.equals("diskFile"))
        {
            String diskFilePath = getOVFPackageInstance(idEnterprise, ovfId).getDiskFilePath();

            String baseUrl = uriInfo.getBaseUri().toASCIIString();

            String diskFileUrl = baseUrl + "files/" + diskFilePath;

            try
            {
                return Response.seeOther(new URI(diskFileUrl)).build();
            }
            catch (URISyntaxException e)
            {
                throw new WebApplicationException(e);
            }
        }
        else
        {
            return Response.ok(getOVFEnvelope(idEnterprise, ovfId)).build();
        }
    }

    /**
     * Eval the current status. *
     * 
     * @param idEnterprise Id of Enterprise to which this OVFPackage belongs.
     * @param idRepository Id of the Repository to which the OVFPackage belongs.
     * @return DataResult<OVFPackageInstanceStatus>@return Response
     */
    private Response evalStatus(String idEnterprise, String ovfId)
    {
        OVFPackageInstanceStatusDto ovfPackageInstanceStatus =
            getOVFPackageInstanceStatus(idEnterprise, ovfId);

        if (ovfPackageInstanceStatus == null)
        {
            return Response.status(Status.NOT_FOUND).build();
        }
        if (!StringUtils.isBlank(ovfPackageInstanceStatus.getErrorCause()))
        {
            return Response.ok(ovfPackageInstanceStatus).build();
        }
        if (OVFPackageInstanceStatusType.NOT_DOWNLOAD.equals(ovfPackageInstanceStatus
            .getOvfPackageStatus()))
        {
            ovfPackageInstanceStatus.setProgress(0d);
            return Response.ok(ovfPackageInstanceStatus).build();
        }
        if (OVFPackageInstanceStatusType.ERROR.equals(ovfPackageInstanceStatus
            .getOvfPackageStatus()))
        {
            ovfPackageInstanceStatus.setProgress(0d);
            return Response.ok(ovfPackageInstanceStatus).build();
        }

        if (OVFPackageInstanceStatusType.DOWNLOADING.equals(ovfPackageInstanceStatus
            .getOvfPackageStatus()))
        {
            return Response.ok(ovfPackageInstanceStatus).build();
        }

        ovfPackageInstanceStatus.setProgress(100d);
        return Response.ok(ovfPackageInstanceStatus).build();
    }

    /**
     * create bundle methods
     */

    @POST
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public Response preBundleOVFPackage(
        @PathParam(EnterpriseRepositoryResource.ENTERPRISE_REPOSITORY) String idEnterprise,
        String name)
    {
        EnterpriseRepositoryService enterpriseRepository =
            EnterpriseRepositoryService.getRepo(idEnterprise);

        final String ovfId = enterpriseRepository.prepareBundle(name);

        return Response.status(Status.ACCEPTED).entity(ovfId).type(MediaType.TEXT_PLAIN).build();
    }

    @POST
    public Response bundleOVFPackage(
        @PathParam(EnterpriseRepositoryResource.ENTERPRISE_REPOSITORY) String idEnterprise,
        @PathParam(OVFPackageInstanceResource.OVFPI) String snapshot, OVFPackageInstanceDto diskInfo)
    {
        // TODO check diskInfo.getEnvelopeId is equals to idEnterprise

        String bundleOVFId = null;
        URI bundleUri;
        try
        {
            bundleOVFId = service.createOVFBundle(diskInfo, snapshot);
            bundleUri = new URI(bundleOVFId);
        }
        catch (URISyntaxException e)
        {
            final String cause = String.format("The Bundle URI is not valid [%s]", bundleOVFId);
            throw new AMException(AMError.OVF_BOUNDLE, cause, e);
        }

        return Response.created(bundleUri).type(MediaType.TEXT_PLAIN).build(); // XXX location
    }

    /**
     * delete
     */
    @DELETE
    public void deleteOVF(
        @PathParam(EnterpriseRepositoryResource.ENTERPRISE_REPOSITORY) String idEnterprise,
        @PathParam(OVFPackageInstanceResource.OVFPI) String ovfIdIn)
    {
        final String ovfId = ovfUrl(ovfIdIn);

        service.delete(idEnterprise, ovfId);
    }

    /*
     * NOT EXPOSED *
     */
    // @Produces({MediaType.APPLICATION_ATOM_XML, MediaType.APPLICATION_JSON})
    private OVFPackageInstanceStatusDto getOVFPackageInstanceStatus(String idEnterprise,
        String ovfId)
    {
        return service.getOVFPackageStatusIncludeProgress(ovfId, idEnterprise);
    }

    private OVFPackageInstanceDto getOVFPackageInstance(String idEnterprise, String ovfId)
    {
        return service.getOVFPackage(idEnterprise, ovfId);
    }

    private EnvelopeType getOVFEnvelope(String idEnterprise, String ovfId)
    {
        EnterpriseRepositoryService enterpriseRepository =
            EnterpriseRepositoryService.getRepo(idEnterprise);

        return OVFPackageInstanceFileSystem.getEnvelope(
            enterpriseRepository.getEnterpriseRepositoryPath(), ovfId);
    }
}
