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

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;

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

import com.abiquo.am.services.EnterpriseRepositoryService;
import com.abiquo.am.services.OVFPackageInstanceService;
import com.abiquo.appliancemanager.exceptions.AMException;
import com.abiquo.appliancemanager.exceptions.DownloadException;
import com.abiquo.appliancemanager.exceptions.RepositoryException;
import com.abiquo.appliancemanager.transport.OVFPackageInstanceDto;
import com.abiquo.appliancemanager.transport.OVFPackageInstanceStatusDto;
import com.abiquo.ovfmanager.ovf.exceptions.IdNotFoundException;

@Parent(OVFPackageInstancesResource.class)
@Path(OVFPackageInstanceResource.OVFPI_PARAM)
@Controller(value = "ovfPackageInstanceResource")
public class OVFPackageInstanceResource extends AbstractResource
{

    public static final String OVFPI = ApplianceManagerPaths.OVFPI;

    public static final String OVFPI_PARAM = "{" + OVFPI + "}";

    private final static String HEADER_PROGRESS = "progress";

    private final static String QUERY_PARAM_GET_FORMAT = "format";

    @Autowired
    OVFPackageInstanceService service;

    @HEAD
    public Response getOVFPackageDeployProgress(
        @PathParam(EnterpriseRepositoryResource.ENTERPRISE_REPOSITORY) String idEnterprise,
        @PathParam(OVFPackageInstanceResource.OVFPI) String ovfIdIn) throws DownloadException
    {
        String ovfId1;
        String ovfId;
        try
        {
            // FIXME ABICLOUDPREMIUM-1798
                ovfId1 = URLDecoder.decode(ovfIdIn, "UTF-8");                
                ovfId = URLDecoder.decode(ovfId1, "UTF-8");
        }
        catch (UnsupportedEncodingException e)
        {
            throw new WebApplicationException(Response.status(Status.BAD_REQUEST)
                .entity("Malformed URL of the ovfid " + ovfIdIn).build());
        }
        
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

        String ovfId1;
        String ovfId;
        try
        {
            // FIXME ABICLOUDPREMIUM-1798
                ovfId1 = URLDecoder.decode(ovfIdIn, "UTF-8");                
                ovfId = URLDecoder.decode(ovfId1, "UTF-8");
        }
        catch (UnsupportedEncodingException e)
        {
            throw new WebApplicationException(Response.status(Status.BAD_REQUEST)
                .entity("Malformed URL of the ovfid " + ovfIdIn).build());
        }

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
        if(ovfPackageInstanceStatus == null) {
            return Response.status(Status.NOT_FOUND).build();
        }
        if (!StringUtils.isBlank(ovfPackageInstanceStatus.getErrorCause()))
        {
            return Response.ok(ovfPackageInstanceStatus).build();
        }
        ovfPackageInstanceStatus.setProgress(100d);
        return Response.ok(ovfPackageInstanceStatus).build();
    }

    /**
     * 
     * */
    /*
     * create methods
     */
    /**
     * 
     * */

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
        catch (RepositoryException e)
        {
            throw new AMException(e);
        }
        catch (URISyntaxException e)
        {
            final String cause = String.format("The Bundle URI is not valid [%s]", bundleOVFId);
            throw new AMException(Status.NOT_FOUND, cause, e);
        }

        return Response.created(bundleUri).type(MediaType.TEXT_PLAIN).build(); // XXX location
    }

    /**
     * 
     * */
    /*
     * delete
     */
    /**
     * 
     * */

    @DELETE
    public void deleteOVF(
        @PathParam(EnterpriseRepositoryResource.ENTERPRISE_REPOSITORY) String idEnterprise,
        @PathParam(OVFPackageInstanceResource.OVFPI) String ovfIdIn)
    {

        String ovfId1;
        String ovfId;
        try
        {
            // FIXME ABICLOUDPREMIUM-1798
                ovfId1 = URLDecoder.decode(ovfIdIn, "UTF-8");                
                ovfId = URLDecoder.decode(ovfId1, "UTF-8");
        }
        catch (UnsupportedEncodingException e)
        {
            throw new WebApplicationException(Response.status(Status.BAD_REQUEST)
                .entity("Malformed URL of the ovfid " + ovfIdIn).build());
        }
        

        try
        {
            service.delete(idEnterprise, ovfId);
        }
        catch (IdNotFoundException e)
        {
            throw new AMException(Status.NOT_FOUND, e.getLocalizedMessage());
        }
        catch (RepositoryException e)
        {
            throw new AMException(Status.PRECONDITION_FAILED, e.getLocalizedMessage());
        }
    }

    /*
     * NOT EXPOSED *
     */
    // @GET
    // @Produces({MediaType.APPLICATION_ATOM_XML, MediaType.APPLICATION_JSON})
    // public OVFPackageInstanceStatusDto getOVFPackageInstanceStatus(
    // @PathParam(EnterpriseRepositoryResource.ENTERPRISE_REPOSITORY) String idEnterprise,
    // @PathParam(OVFPackageInstanceResource.OVFPI) String ovfId)
    //
    OVFPackageInstanceStatusDto getOVFPackageInstanceStatus(String idEnterprise, String ovfId)

    {

        try
        {
            return service.getOVFPackageStatusIncludeProgress(ovfId, idEnterprise);
        }
        catch (DownloadException e)
        {
            throw new AMException(Status.NOT_FOUND, "OVF Package Instance not found or "
                + "can't obtain the status", e);
        }
    }

    // OVFPackageInstanceDto getOVFPackageInstance(
    // @PathParam(EnterpriseRepositoryResource.ENTERPRISE_REPOSITORY) String idEnterprise,
    // @PathParam(OVFPackageInstanceResource.OVFPI) String ovfId)

    OVFPackageInstanceDto getOVFPackageInstance(String idEnterprise, String ovfId)
    {

        try
        {
            return service.getOVFPackage(idEnterprise, ovfId);
        }
        catch (IdNotFoundException e)
        {
            final String cause =
                String.format("Can not obtain the OVF Package Instance of OVF [%s]", ovfId);
            throw new AMException(Status.NOT_FOUND, cause, e);
        }
        //
        // EnvelopeType envelope = getOVFEnvelope(ovfId, idEnterprise);
        //
        // OVFPackageInstanceDto packDto = OVFPackageInstanceFromOVFEnvelope.getDiskInfo(ovfId,
        // idEnterprise, envelope);
        // packDto.setIdEnterprise(Integer.valueOf(idEnterprise));
        //
        // return packDto;
    }

    // @GET
    // @Produces("xml/ovfenvelope")
    // public EnvelopeType getOVFEnvelope(
    // @PathParam(EnterpriseRepositoryResource.ENTERPRISE_REPOSITORY) String idEnterprise,
    // @PathParam(OVFPackageInstanceResource.OVFPI) String ovfId)
    public EnvelopeType getOVFEnvelope(String idEnterprise, String ovfId)

    {
        EnterpriseRepositoryService enterpriseRepository =
            EnterpriseRepositoryService.getRepo(idEnterprise);

        try
        {
            final EnvelopeType envelop = enterpriseRepository.getEnvelope(ovfId);

            return envelop;
        }
        catch (IdNotFoundException e)
        {
            final String cause =
                String.format("Can not obtain the Envelope document of OVF [%s]", ovfId);
            throw new AMException(Status.NOT_FOUND, cause, e);
        }
    }

    // @GET
    // @Produces(MediaType.APPLICATION_OCTET_STREAM)
    // public File downloadOVFDiskFile(
    // @PathParam(EnterpriseRepositoryResource.ENTERPRISE_REPOSITORY) String idEnterprise,
    // @PathParam(OVFPackageInstanceResource.OVFPI) String ovfId)
    // {
    //
    // try
    // {
    // ovfId = URLDecoder.decode(ovfId, "UTF-8");
    // }
    // catch (UnsupportedEncodingException e)
    // {
    // throw new WebApplicationException(Response.status(Status.BAD_REQUEST)
    // .entity("Malformed URL of the ovfid " + ovfId).build());
    // }
    //
    //
    // // FIXME
    // //
    // http://www.jarvana.com/jarvana/view/org/apache/wink/apache-wink/1.0-incubating/apache-wink-1.0-incubating-src.tar.gz!/apache-wink-1.0-incubating-src/wink-common/src/test/java/org/apache/wink/common/internal/providers/multipart/TestMultiPartProvider.java?format=ok
    // // FIXME
    // //
    // http://svn.apache.org/repos/asf/incubator/wink/tags/wink-1.1.2-incubating/wink-examples/ext/MultiPart/src/main/java/org/apache/wink/example/multipart/MultiPartResource.java
    // EnterpriseRepositoryService enterpriseRepository =
    // EnterpriseRepositoryService.getRepo(idEnterprise);
    // try
    // {
    // return enterpriseRepository.getOVFDiskFile(ovfId);
    //
    // // final String enterpriseRepositoryPath =
    // // enterpriseRepository.getEnterpriseRepositoryPath();
    // // final String relativeDiskFilePath =
    // // diskFile.getAbsolutePath().substring(enterpriseRepositoryPath.length());
    // //
    // // FileInputStream diskFileStream = new FileInputStream(diskFile);
    // // BufferedInputStream bis = new BufferedInputStream(diskFileStream);
    // //
    // // return new Attachment(bis, AMHTTPHeadersUtils.createAttachmentHeaders(
    // // relativeDiskFilePath, diskFile.length()));
    // }
    // catch (Exception e)// IdNotFound or FileNotFoundException
    // {
    // final String cause = String.format("Can not obtain the disk file for OVF [%s]", ovfId);
    // throw new AMException(Status.NOT_FOUND, cause, e);
    // }
    // }
    //
    // public class FileOutPart extends OutPart
    // {
    // String resource;
    //
    // public FileOutPart(String resource)
    // {
    // this.resource = resource;
    // }
    //
    // // @Override
    // // void writePart(OutputStream os, Providers providers) {
    // // InputStream in = getClass().getResourceAsStream(resource);
    // // int b;
    // // while ((b = in.read()) != -1) {
    // // os.write(b);
    // // }
    // // }
    // }
}
