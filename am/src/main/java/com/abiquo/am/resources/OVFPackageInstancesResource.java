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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Providers;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.wink.common.annotations.Parent;
import org.apache.wink.common.model.multipart.InMultiPart;
import org.apache.wink.common.model.multipart.InPart;
import org.dmtf.schemas.ovf.envelope._1.EnvelopeType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.abiquo.am.exceptions.AMError;
import com.abiquo.am.services.ErepoFactory;
import com.abiquo.am.services.OVFPackageConventions;
import com.abiquo.am.services.OVFPackageInstanceService;
import com.abiquo.am.services.download.OVFDocumentFetch;
import com.abiquo.am.services.notify.AMNotifier;
import com.abiquo.am.services.ovfformat.OVFPackageInstanceFromOVFEnvelope;
import com.abiquo.appliancemanager.exceptions.AMException;
import com.abiquo.appliancemanager.exceptions.EventException;
import com.abiquo.appliancemanager.transport.OVFPackageInstanceDto;
import com.abiquo.appliancemanager.transport.OVFPackageInstancesStateDto;
import com.abiquo.appliancemanager.transport.OVFStatusEnumType;

@Parent(EnterpriseRepositoryResource.class)
@Path(OVFPackageInstancesResource.OVFPI_PATH)
@Controller
public class OVFPackageInstancesResource
{
    private final static Logger LOG = LoggerFactory.getLogger(OVFPackageInstancesResource.class);

    public static final String OVFPI_PATH = ApplianceManagerPaths.OVFPI_PATH;

    @Autowired
    AMNotifier notifier;

    @Autowired
    OVFPackageInstanceService ovfService;

    @Autowired
    OVFDocumentFetch validate;

    /**
     * include bundles <br>
     * XXX do not include DOWNLOADING or ERROR status
     */
    @GET
    public OVFPackageInstancesStateDto getOVFPackageInstancesStatus(
        @PathParam(EnterpriseRepositoryResource.ENTERPRISE_REPOSITORY) final String idEnterprise)
    {
        // TODO add only DOWNLOADED

        OVFPackageInstancesStateDto list = new OVFPackageInstancesStateDto();
        list.getCollection().addAll(ErepoFactory.getRepo(idEnterprise).getOVFStates());

        return list;
    }

    /**
     * Never return error. Use GET_STATUS to see errors
     */
    @POST
    public void downloadOVFPackage(
        @PathParam(EnterpriseRepositoryResource.ENTERPRISE_REPOSITORY) final String erId,
        final String ovfId)
    {
        LOG.debug("[deploy] {}", ovfId);

        if (!OVFPackageConventions.isValidOVFLocation(ovfId))
        {
            throw new AMException(AMError.OVF_INVALID_LOCATION);
        }

        switch (ovfService.getOVFPackageStatusIncludeProgress(ovfId, erId).getStatus())
        {
            case DOWNLOADING:
            case DOWNLOAD:
                throw new AMException(AMError.OVF_INSTALL_ALREADY);

            case ERROR:
                ovfService.delete(erId, ovfId);
                break;
            default:
                break;
        }

        if (ovfId.startsWith("upload"))
        {
            throw new AMException(AMError.OVF_UPLOAD, String.format(
                "Can not deply an uploaded package %s", ovfId));
        }

        try
        {
            ovfService.startDownload(erId, ovfId);
        }
        catch (AMException e)
        {
            notifier.setOVFStatusError(erId, ovfId, e.toString());

            // XXX the request ends successfully but the ovf package status is ERROR
            // throw new AMException(Status.BAD_REQUEST, cause);
        }
    }

    @POST
    @Consumes("multipart/form-data")
    public Response uploadOVFPackage(@Context final HttpHeaders headers,
        @PathParam(EnterpriseRepositoryResource.ENTERPRISE_REPOSITORY) final String erId,
        final InMultiPart mp, @Context final Providers providers) throws IOException,
        EventException
    {
        OVFPackageInstanceDto diskInfo = null;
        String errorMsg = null;

        try
        {
            InPart diskInfoPart = mp.next();
            diskInfo = readOVFPackageInstanceDtoFromMultipart(diskInfoPart, headers, providers);
        }
        catch (Exception e)
        {
            if (!StringUtils.isBlank(e.getMessage()))
            {
                errorMsg = e.getMessage();
            }
            else
            {
                errorMsg = "Error uploading the image";
            }

            diskInfo.setDiskFilePath(OVFPackageConventions.OVF_STATUS_ERROR_MARK);
        }

        // XXX notify DOWNLOADING

        final String ovfId = diskInfo.getUrl();
        if (ovfService.getOVFPackageStatusIncludeProgress(ovfId, erId).getStatus() == OVFStatusEnumType.ERROR)
        {
            ovfService.delete(erId, ovfId);
        }

        InPart diskFilePart = mp.next();

        InputStream isDiskFile = diskFilePart.getBody(InputStream.class, null);
        File diskFile = new File("/tmp/" + diskInfo.getDiskFilePath());

        copy(isDiskFile, diskFile);

        /**
         * TODO check OVFid is in this hostname ..
         */

        diskInfo.setDiskFileSize(diskFile.length());
        ovfService.upload(diskInfo, diskFile, errorMsg);

        return Response.created(URI.create(diskInfo.getUrl())).build();
    }

    @POST
    @Path("actions/validate")
    public OVFPackageInstanceDto validate(final EnvelopeType envelope)
    {
        return OVFPackageInstanceFromOVFEnvelope.getDiskInfo("http://am/validation/OK.ovf",
            validate.checkEnvelopeIsValid(envelope));
    }

    private OVFPackageInstanceDto readOVFPackageInstanceDtoFromMultipart(final InPart diskInfoPart,
        final HttpHeaders headers, final Providers providers) throws Exception
    {
        fixMediaType(diskInfoPart);

        String json = diskInfoPart.getBody(String.class, null);
        // we replace the \ with / because a fail parsing strings with \ followed by a char that
        // might resemble a control char. (C:\f... ends up as C:[ctrl-L]...)
        String json2 = removeFakePath(removeControlChar(json));
        json2 = temporalJsonNameHack(json2);

        return providers.getMessageBodyReader(OVFPackageInstanceDto.class, null, null,
            MediaType.APPLICATION_JSON_TYPE).readFrom(OVFPackageInstanceDto.class, null, null,
            MediaType.APPLICATION_JSON_TYPE, headers.getRequestHeaders(),
            new ByteArrayInputStream(json2.getBytes()));

        // return diskInfoPart.getBody(OVFPackageInstanceDto.class, null);
        // return providers.getMessageBodyReader(OVFPackageInstanceDto.class, null, null,
        // MediaType.APPLICATION_JSON_TYPE).readFrom(OVFPackageInstanceDto.class, null, null,
        // MediaType.APPLICATION_JSON_TYPE, headers.getRequestHeaders(),
        // diskInfoPart.getInputStream());
    }

    /**
     * Duet the flex client now sends 'OVFPackageInstanceDto' instead of 'ovfInstance', this will be
     * removed before the 2.0 release
     */
    @Deprecated
    private String temporalJsonNameHack(final String jsonin)
    {
        return jsonin.replaceAll("ovfPackageInstanceDto", "ovfInstance").replaceAll("ovfUrl",
            "ovfId");
    }

    /**
     * This Function is needed as long as the HTML 5 states:
     * http://people.w3.org/mike/diffs/html5/spec/Overview.diff.html#common-input-element-apis
     * browsers prepend C:\fakepath\
     * 
     * @param in string.
     * @return with no '\' characters.
     */
    private String removeFakePath(final String in)
    {
        // TODO this is a hack as the server adds the fake path somehow
        return in.replace("C:\\fakepath\\", "").replace("\\", "/");
    }

    /**
     * The parse fails if any.
     * 
     * @param in a String that might contain control caracters.
     * @return same String that does not contains any control caracters.
     */
    private String removeControlChar(final String in)
    {
        StringBuilder sb = new StringBuilder();
        for (char c : in.toCharArray())
        {
            if (!Character.isISOControl(c))
            {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    // CaseInsensitiveMultivaluedMap [map=[Content-Disposition=form-data; name="diskInfo";
    // filename="diskInfo.json",Content-Type=application/json]]
    private void fixMediaType(final InPart diskInfoPart)
    {
        if (diskInfoPart.getHeaders().getFirst(HttpHeaders.CONTENT_TYPE) == null)
        {
            diskInfoPart.getHeaders().add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);
        }
    }

    private void copy(final InputStream fin, final File destFile) throws IOException
    {
        OutputStream fout = new FileOutputStream(destFile);
        try
        {
            IOUtils.copy(fin, fout);
        }
        finally
        {
            fout.close();
        }
    }
}
