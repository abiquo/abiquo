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
package com.abiquo.api.services.stub;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.wink.client.ClientResponse;
import org.apache.wink.client.Resource;
import org.apache.wink.client.RestClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.abiquo.appliancemanager.transport.EnterpriseRepositoryDto;
import com.abiquo.appliancemanager.transport.OVFPackageInstanceDto;
import com.abiquo.appliancemanager.transport.OVFPackageInstanceStatusDto;
import com.abiquo.appliancemanager.transport.OVFPackageInstanceStatusListDto;
import com.abiquo.appliancemanager.transport.OVFPackageInstanceStatusType;
import com.abiquo.appliancemanager.transport.OVFPackageInstancesDto;
import com.abiquo.server.core.enterprise.EnterprisesDto;

@Service
@Transactional
public class ApplianceManagerStubImpl implements ApplianceManagerStub
{
    /**
     * Match the com.abiquo.appliancemanager.ApplianceManagerResource CONFIGURATION_RESOURCE_PATH
     * Path segment.
     */
    public final static String CONFIGURATION_RESOURCE_PATH = "config/";

    /**
     * Match the com.abiquo.appliancemanager.ApplianceManagerResource ENTERPRISE_REPO_RESOURCE_PATH
     * Path segment.
     */
    public final static String ENTERPRISE_REPO_RESOURCE_PATH = "er/";

    /**
     * Match the com.abiquo.appliancemanager.ApplianceManagerResource OVF_PACKAGE_RESOURCE_PATH Path
     * segment.
     */
    public final static String OVF_PACKAGE_RESOURCE_PATH = "ovf/";

    /**
     * Match the com.abiquo.appliancemanager.ApplianceManagerResource OVF_REF_FILE_RESOURCE_PATH
     * Path segment.
     */
    public final static String OVF_REF_FILE_RESOURCE_PATH = "diskFile/";

    /**
     * HTTP header on the ''getOVFDeployProgress'' to indicate the DOWNLOADING progress.
     */
    private final static String PROGRESS_HEADER = "progress";

    private RestClient client;

    public ApplianceManagerStubImpl()
    {
        super();
        this.client = new RestClient();
    }

    /**
     * @Path(ENTERPRISE_REPO_RESOURCE_PATH + "{idEnterprise}/" + OVF_PACKAGE_RESOURCE_PATH +
     *                                     "deployProgress/")
     * @param @QueryParam("ovfID")
     * @param @PathParam("idEnterprise")
     */
    private Resource createResourceGetOVFPackageStatus(final String serviceUri,
        final String idEnterprise, final String idOVF)
    {
        final String path =
            ENTERPRISE_REPO_RESOURCE_PATH + idEnterprise + "/" + OVF_PACKAGE_RESOURCE_PATH
                + "deployProgress/";
        Resource resource = client.resource(serviceUri + "/" + path);
        resource.queryParam("ovfID", idOVF);

        return resource;
    }

    /**
     * @Path(ENTERPRISE_REPO_RESOURCE_PATH + "{idEnterprise}/" + OVF_PACKAGE_RESOURCE_PATH +
     *                                     "deploy/")
     * @param @PathParam("idEnterprise") String idEnterprise);
     */
    private Resource createResourceInstallOVFPackage(final String serviceUri,
        final String idEnterprise)// , final String idOVF)
    {
        final String path =
            ENTERPRISE_REPO_RESOURCE_PATH + idEnterprise + "/" + OVF_PACKAGE_RESOURCE_PATH
                + "deploy/";
        return client.resource(serviceUri + "/" + path);
    }

    /**
     * @Path(ENTERPRISE_REPO_RESOURCE_PATH + "{idEnterprise}/" + OVF_PACKAGE_RESOURCE_PATH +
     *                                     "availables/")
     * @param @PathParam("idEnterprise") String idEnterprise);
     */
    private Resource createResourceGetOVFPackageInstances(final String serviceUri,
        final String idEnterprise)
    {
        final String path =
            ENTERPRISE_REPO_RESOURCE_PATH + idEnterprise + "/" + OVF_PACKAGE_RESOURCE_PATH
                + "deployProgress/";
        return client.resource(serviceUri + "/" + path);
    }

    @Override
    public OVFPackageInstanceStatusDto getOVFPackageStatus(final String serviceUri,
        final String idEnterprise, final String ovfId)
    {
        OVFPackageInstanceStatusDto packStatus = new OVFPackageInstanceStatusDto();
        packStatus.setOvfId(ovfId);

        Resource resource = createResourceGetOVFPackageStatus(serviceUri, idEnterprise, ovfId);
        ClientResponse response = resource.accept(MediaType.APPLICATION_XML).get();

        final Integer httpStatus = response.getStatusCode();

        if (httpStatus == Status.NOT_FOUND.getStatusCode())
        {
            packStatus.setOvfPackageStatus(OVFPackageInstanceStatusType.NOT_DOWNLOAD);
        }
        else if (httpStatus == Status.CREATED.getStatusCode())
        {
            packStatus.setOvfPackageStatus(OVFPackageInstanceStatusType.DOWNLOAD);
        }
        else if (httpStatus == Status.ACCEPTED.getStatusCode())
        {
            packStatus.setOvfPackageStatus(OVFPackageInstanceStatusType.DOWNLOADING);

            final String progress = response.getHeaders().getFirst(PROGRESS_HEADER);

            if (progress == null || progress.isEmpty())
            {
                final String cause =
                    "OVFPackageInstanceStatus require ''progess'' HTTP header on its response";
                throw new WebApplicationException(Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity(cause).build());
            }

            final Double dProgress = Double.valueOf(progress);

            packStatus.setProgress(dProgress);
        }
        else if (httpStatus == Status.INTERNAL_SERVER_ERROR.getStatusCode())
        {
            packStatus.setOvfPackageStatus(OVFPackageInstanceStatusType.ERROR);

            final String errorCause = response.getEntity(String.class);

            packStatus.setErrorCause(errorCause);
        }
        else
        {
            final String cause =
                String.format("Invalid HTTP response [%s] from the ApplianceManager"
                    + " remote service.", Status.fromStatusCode(httpStatus));
            final Response errorResponse =
                Response.status(Status.INTERNAL_SERVER_ERROR).entity(cause).build();
            throw new WebApplicationException(errorResponse);
        }

        return packStatus;
    }

    @Override
    public OVFPackageInstanceStatusDto installOVFPackage(final String serviceUri,
        final String idEnterprise, final String ovfId)
    {
        Resource resource = createResourceInstallOVFPackage(serviceUri, idEnterprise);// , ovfId);
        ClientResponse response = resource.accept(MediaType.APPLICATION_XML).post(ovfId);

        final Integer httpStatus = response.getStatusCode();

        if (httpStatus / 200 != 1)
        {
            throw new WebApplicationException(response(response));
        }

        return getOVFPackageStatus(serviceUri, idEnterprise, ovfId);
    }

    @Override
    public OVFPackageInstanceStatusListDto getOVFPackagInstances(final String serviceUri,
        final String idEnterprise)
    {
        Resource resource = createResourceGetOVFPackageInstances(serviceUri, idEnterprise);
        ClientResponse response = resource.accept(MediaType.APPLICATION_XML).get();

        final Integer httpStatus = response.getStatusCode();

        if (httpStatus != 200)
        {
            throw new WebApplicationException(response(response));
        }

        return response.getEntity(OVFPackageInstanceStatusListDto.class);
    }

    private static Response response(ClientResponse response)
    {
        return Response.status(response.getStatusCode()).entity(response.getEntity(String.class))
            .build();
    }

    @Override
    public OVFPackageInstanceDto getOVFPackageInstance(String serviceUri, String idEnterprise,
        String ovfUrl)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public OVFPackageInstancesDto getOVFPackageInstancesList(String serviceUri, String idEnterprise)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public OVFPackageInstanceStatusDto deleteOVFPackageInstance(String uri, String idEnterprise,
        String ovfUrl)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public EnterprisesDto getEnterpriseRepositoriesByDC(String uri)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public EnterpriseRepositoryDto getEnterpriseRepository(String uri, String idEnterprise)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public OVFPackageInstanceDto bundleOVFPackage(String serviceUri, String idEnterprise,
        String ovfUrl)
    {
        // TODO Auto-generated method stub
        return null;
    }

}
