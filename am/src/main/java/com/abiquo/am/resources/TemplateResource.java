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

import static com.abiquo.am.services.TemplateConventions.ovfUrl;

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
import com.abiquo.am.services.ErepoFactory;
import com.abiquo.am.services.TemplateService;
import com.abiquo.am.services.filesystem.TemplateFileSystem;
import com.abiquo.api.resource.AbstractResource;
import com.abiquo.appliancemanager.exceptions.AMException;
import com.abiquo.appliancemanager.exceptions.DownloadException;
import com.abiquo.appliancemanager.transport.TemplateDto;
import com.abiquo.appliancemanager.transport.TemplateStateDto;
import com.abiquo.appliancemanager.transport.TemplateStatusEnumType;

@Parent(TemplatesResource.class)
@Path(TemplateResource.TEMPLATE_PATH)
@Controller
public class TemplateResource extends AbstractResource
{
    public static final String TEMPLATE = ApplianceManagerPaths.TEMPLATE;

    /**
     * The resource parameter matching configuration.
     * <p>
     * Must override default regular expression in order to be able to match complete URIs as the
     * OVFPackageInstance identifier.
     */
    public static final String TEMPLATE_PARAM = "{" + TEMPLATE + ": .*}"; // FIXME take care of .*

    public static final String TEMPLATE_PATH = TEMPLATE_PARAM;

    /** The resource path. */

    private final static String HEADER_PROGRESS = "progress";

    private final static String QUERY_PARAM_GET_FORMAT = "format";

    @Autowired
    TemplateService service;

    @HEAD
    public Response getTemplateDeployProgress(
        @PathParam(EnterpriseRepositoryResource.ENTERPRISE_REPOSITORY) final String idEnterprise,
        @PathParam(TemplateResource.TEMPLATE) final String ovfIdIn) throws DownloadException
    {
        final String ovfId = ovfUrl(ovfIdIn);

        TemplateStateDto status = service.getTemplateStatusIncludeProgress(ovfId, idEnterprise);

        switch (status.getStatus())
        {
            case NOT_DOWNLOAD:
                return Response.status(Status.NOT_FOUND).build();

            case DOWNLOAD:
                return Response.status(Status.CREATED).build(); // TODO location(arg0);

            case DOWNLOADING:
                final String progress = String.valueOf(status.getDownloadingProgress());
                return Response.status(Status.ACCEPTED).header(HEADER_PROGRESS, progress).build();

            case ERROR:
                return Response.status(Status.INTERNAL_SERVER_ERROR).entity(status.getErrorCause())
                    .build();

                // XXX deleted
            default:
                return Response.status(Status.NOT_FOUND)
                    .entity("UNKNOW STATUS:" + status.getStatus().name()).build();
        }
    }

    @GET
    public Response getTemplate(@Context final UriInfo uriInfo,
        @PathParam(EnterpriseRepositoryResource.ENTERPRISE_REPOSITORY) final String idEnterprise,
        @PathParam(TemplateResource.TEMPLATE) final String ovfIdIn,
        @QueryParam(QUERY_PARAM_GET_FORMAT) final String format)
    {
        // XXX can specify the media type

        final String ovfId = ovfUrl(ovfIdIn);

        if (format == null || format.isEmpty() || format.equals("ovfpi"))
        {
            return Response.ok(getTemplate(idEnterprise, ovfId)).build();
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
            String diskFilePath = getTemplate(idEnterprise, ovfId).getDiskFilePath();

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
    private Response evalStatus(final String idEnterprise, final String ovfId)
    {
        TemplateStateDto templateStatus = getTemplateStatus(idEnterprise, ovfId);

        if (templateStatus == null)
        {
            return Response.status(Status.NOT_FOUND).build();
        }
        if (!StringUtils.isBlank(templateStatus.getErrorCause()))
        {
            return Response.ok(templateStatus).build();
        }
        if (TemplateStatusEnumType.NOT_DOWNLOAD.equals(templateStatus.getStatus()))
        {
            templateStatus.setDownloadingProgress(0d);
            return Response.ok(templateStatus).build();
        }
        if (TemplateStatusEnumType.ERROR.equals(templateStatus.getStatus()))
        {
            templateStatus.setDownloadingProgress(0d);
            return Response.ok(templateStatus).build();
        }

        if (TemplateStatusEnumType.DOWNLOADING.equals(templateStatus.getStatus()))
        {
            return Response.ok(templateStatus).build();
        }

        templateStatus.setDownloadingProgress(100d);
        return Response.ok(templateStatus).build();
    }

    /**
     * create bundle methods
     */

    @POST
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public Response preBundleTemplate(
        @PathParam(EnterpriseRepositoryResource.ENTERPRISE_REPOSITORY) final String idEnterprise,
        final String name)
    {
        final String ovfId = ErepoFactory.getRepo(idEnterprise).prepareBundle(name);

        return Response.status(Status.ACCEPTED).entity(ovfId).type(MediaType.TEXT_PLAIN).build();
    }

    @POST
    public Response bundleTemplate(
        @PathParam(EnterpriseRepositoryResource.ENTERPRISE_REPOSITORY) final String idEnterprise,
        @PathParam(TemplateResource.TEMPLATE) final String snapshot, final TemplateDto diskInfo)
    {
        // TODO check diskInfo.getEnvelopeId is equals to idEnterprise

        String bundleOVFId = null;
        URI bundleUri;
        try
        {
            bundleOVFId = service.createTemplateBundle(diskInfo, snapshot);
            bundleUri = new URI(bundleOVFId);
        }
        catch (URISyntaxException e)
        {
            final String cause = String.format("The Bundle URI is not valid [%s]", bundleOVFId);
            throw new AMException(AMError.TEMPLATE_BOUNDLE, cause, e);
        }

        return Response.created(bundleUri).type(MediaType.TEXT_PLAIN).build(); // XXX location
    }

    /**
     * delete
     */
    @DELETE
    public void deleteTemplate(
        @PathParam(EnterpriseRepositoryResource.ENTERPRISE_REPOSITORY) final String idEnterprise,
        @PathParam(TemplateResource.TEMPLATE) final String ovfIdIn)
    {
        final String ovfId = ovfUrl(ovfIdIn);

        service.delete(idEnterprise, ovfId);
    }

    /*
     * NOT EXPOSED *
     */
    // @Produces({MediaType.APPLICATION_ATOM_XML, MediaType.APPLICATION_JSON})
    private TemplateStateDto getTemplateStatus(final String idEnterprise, final String ovfId)
    {
        return service.getTemplateStatusIncludeProgress(ovfId, idEnterprise);
    }

    private TemplateDto getTemplate(final String idEnterprise, final String ovfId)
    {
        return service.getTemplate(idEnterprise, ovfId);
    }

    private EnvelopeType getOVFEnvelope(final String idEnterprise, final String ovfId)
    {
        return TemplateFileSystem.getEnvelope(ErepoFactory.getRepo(idEnterprise).path(), ovfId);
    }
}
