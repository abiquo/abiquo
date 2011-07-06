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
import java.util.List;

import javax.annotation.Resource;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.Providers;

import org.apache.commons.lang.StringUtils;
import org.apache.wink.common.annotations.Parent;
import org.apache.wink.common.model.multipart.InMultiPart;
import org.apache.wink.common.model.multipart.InPart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import com.abiquo.am.services.EnterpriseRepositoryService;
import com.abiquo.am.services.OVFPackageInstanceService;
import com.abiquo.am.services.notify.AMNotifierFactory;
import com.abiquo.appliancemanager.exceptions.AMException;
import com.abiquo.appliancemanager.exceptions.EventException;
import com.abiquo.appliancemanager.exceptions.RepositoryException;
import com.abiquo.appliancemanager.transport.OVFPackageInstanceDto;
import com.abiquo.appliancemanager.transport.OVFPackageInstanceStatusDto;
import com.abiquo.appliancemanager.transport.OVFPackageInstanceStatusListDto;
import com.abiquo.appliancemanager.transport.OVFPackageInstanceStatusType;
import com.abiquo.ovfmanager.ovf.exceptions.IdNotFoundException;

@Parent(EnterpriseRepositoryResource.class)
@Path(OVFPackageInstancesResource.OVFPI_PATH)
@Service(value = "ovfPackageInstancesResource")
// @ContextConfiguration(locations = "classpath:springresources/applicationContext.xml")
public class OVFPackageInstancesResource // implements ApplicationContextAware
{
    // @Autowired
    static OVFPackageInstanceService service;// TODO null

    @Resource(name = "ovfPackageInstanceService")
    public void setService(OVFPackageInstanceService service)
    {
        this.service = service;
    }

    //
    ApplicationContext ctx;

    public void setApplicationContext(org.springframework.context.ApplicationContext ctx)
    {
        this.ctx = ctx;
    }

    //

    private final static Logger logger = LoggerFactory.getLogger(OVFPackageInstancesResource.class);

    public static final String OVFPI_PATH = ApplianceManagerPaths.OVFPI_PATH;

    /*
     * 
     * 
     * 
     */

    /**
     * include bundles <br>
     * XXX do not include DOWNLOADING or ERROR status
     */
    @GET
    public OVFPackageInstanceStatusListDto getOVFPackageInstancesStatus(
        @PathParam(EnterpriseRepositoryResource.ENTERPRISE_REPOSITORY) String idEnterprise)
    {

        EnterpriseRepositoryService enterpriseRepository =
            EnterpriseRepositoryService.getRepo(idEnterprise);

        List<String> availables = enterpriseRepository.getAllOVF(false);

        OVFPackageInstanceStatusListDto list = new OVFPackageInstanceStatusListDto();

        for (String ovf : availables)
        {
            OVFPackageInstanceStatusDto stat = new OVFPackageInstanceStatusDto();
            stat.setOvfId(ovf);
            stat.setOvfPackageStatus(OVFPackageInstanceStatusType.DOWNLOAD);

            list.getOvfPackageInstancesStatus().add(stat);
        }

        return list;
    }

    @POST
    public void downloadOVFPackage(
        @PathParam(EnterpriseRepositoryResource.ENTERPRISE_REPOSITORY) String idEnterprise,
        String ovfId)
    {
        logger.debug("[deploy] {}", ovfId);

        if (ovfId.startsWith("upload"))
        {
            final String cause = "Can not deply an uploaded package [" + ovfId + "]";
            throw new AMException(Status.BAD_REQUEST, cause);
        }

        try
        {
            service.startDownload(idEnterprise, ovfId);
        }
        catch (Exception e) // Download or Repository
        {
            try
            {
                AMNotifierFactory.getInstance().setOVFStatusError(idEnterprise, ovfId,
                    e.getLocalizedMessage());
            }
            catch (Exception eStatus) // IdNotFoundException RepositoryException EventException
            {
                logger.error("Can not notify error ", eStatus);
            }

            // XXX the request ends successfully but the ovf package status is ERROR
            // throw new AMException(Status.BAD_REQUEST, cause);
        }
    }

    
    @POST
    @Consumes("multipart/form-data")
    public Response uploadOVFPackage(@Context HttpHeaders headers,
        @PathParam(EnterpriseRepositoryResource.ENTERPRISE_REPOSITORY) String idEnterprise,
        InMultiPart mp, @Context Providers providers) throws RepositoryException, IOException,
        IdNotFoundException,
        EventException
    {
        OVFPackageInstanceDto diskInfo = null;
        String errorMsg = null;
        String json = "";
        try
        {
            InPart diskInfoPart = mp.next();

            fixMediaType(diskInfoPart);

            json = diskInfoPart.getBody(String.class, null);
            // we replace the \ with / because a fail parsing strings with \ followed by a char that
            // might resemble a control char. (C:\f... ends up as C:[ctrl-L]...)
            String json2 = removeFakePath(removeControlChar(json));
            
            diskInfo =
                providers.getMessageBodyReader(OVFPackageInstanceDto.class, null, null,
                MediaType.APPLICATION_JSON_TYPE).readFrom(OVFPackageInstanceDto.class, null, null,
                MediaType.APPLICATION_JSON_TYPE, headers.getRequestHeaders(),
                    new ByteArrayInputStream(json2.getBytes()));
            
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
            
            diskInfo.setDiskFilePath(EnterpriseRepositoryService.OVF_STATUS_ERROR_MARK);
        }

        InPart diskFilePart = mp.next();

        InputStream isDiskFile = diskFilePart.getBody(InputStream.class, null);
        File diskFile = new File("/tmp/" + diskInfo.getDiskFilePath());

        copy(isDiskFile, diskFile);

        /**
         * TODO check OVFid is in this hostname ..
         */

        diskInfo.setDiskFileSize(diskFile.length());
        service.upload(diskInfo, diskFile, errorMsg);

        return Response.created(URI.create(diskInfo.getOvfUrl())).build();
    }

<<<<<<< HEAD
    /**
     * This Function is needed as long as the HTML 5 states:
     * http://people.w3.org/mike/diffs/html5/spec/Overview.diff.html#common-input-element-apis
     * browsers prepend C:\fakepath\
     * 
     * @param in string.
     * @return with no '\' characters.
     */
    private String removeFakePath(String in)
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
    private String removeControlChar(String in) {
        StringBuilder sb = new StringBuilder();
        for (char c : in.toCharArray())
        {
            if(!Character.isISOControl(c)) {
                sb.append(c);
            }
        }
        return sb.toString();
    }
    // CaseInsensitiveMultivaluedMap [map=[Content-Disposition=form-data; name="diskInfo";
    // filename="diskInfo.json",Content-Type=application/json]]
    private void fixMediaType(InPart diskInfoPart)
    {
        if (diskInfoPart.getHeaders().getFirst(HttpHeaders.CONTENT_TYPE) == null)
        {
            diskInfoPart.getHeaders().add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);
        }
    }

    private void copy(InputStream fin, File destFile) throws IOException
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
