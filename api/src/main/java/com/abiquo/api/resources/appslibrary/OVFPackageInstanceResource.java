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
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;

import org.apache.wink.common.annotations.Parent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.abiquo.api.resources.AbstractResource;
import com.abiquo.api.resources.DatacenterResource;
import com.abiquo.api.resources.EnterpriseResource;
import com.abiquo.api.services.InfrastructureService;
import com.abiquo.api.services.stub.ApplianceManagerStub;
import com.abiquo.api.util.IRESTBuilder;
import com.abiquo.appliancemanager.transport.OVFPackageInstanceDto;
import com.abiquo.appliancemanager.transport.OVFPackageInstanceStatusDto;

@Parent(OVFPackageInstancesResource.class)
@Path(OVFPackageInstanceResource.OVF_PACKAGE_INSTANCE_PARAM)
@Controller
public class OVFPackageInstanceResource extends AbstractResource
{
    public static final String OVF_PACKAGE_INSTANCE = "ovfpackageinstance";

    public static final String OVF_PACKAGE_INSTANCE_PARAM = "{" + OVF_PACKAGE_INSTANCE + "}";

    public static final String BUNDLE_ACTION = "bundle";

    // public static final String CANCEL_DOWNLOAD_ACTION = "cancelDownload";

    @Autowired
    private ApplianceManagerStub am;

    @Autowired
    private InfrastructureService r;

    /*
     * REST methods
     */

    @GET
    @Produces("application/ovfpackageenvelope-xml")
    public OVFPackageInstanceDto getOVFPackageInstance(
        @PathParam(DatacenterResource.DATACENTER) Integer datacenterId,
        @PathParam(AMResource.AM_SERVICE_TYPE) String serviceType,
        @PathParam(EnterpriseResource.ENTERPRISE) String idEnterprise,
        @PathParam(OVF_PACKAGE_INSTANCE) String ovfUrl, @Context IRESTBuilder restBuilder)
        throws Exception
    {
        System.out.println("getOVFPackageInstance application/ovfpackageenvelope-xml");

        // OVFPackageInstanceDto instance =
        // am.getOVFPackageInstance(getValidRemoteServiceByDatacenter(serviceType, datacenterId)
        // .getUri(), idEnterprise, ovfUrl);

        OVFPackageInstanceDto instance = new OVFPackageInstanceDto();
        instance.setDescription("desc");

        return instance;
    }

    /**
     * Returns downloaded data file
     * 
     * @return
     */
    @GET
    @Produces("application/ovfpackage-octet-stream")
    public String downloadOVFPackageInstance(
        @PathParam(DatacenterResource.DATACENTER) Integer datacenterId,
        @PathParam(AMResource.AM_SERVICE_TYPE) String serviceType,
        @PathParam(EnterpriseResource.ENTERPRISE) String idEnterprise,
        @PathParam(OVF_PACKAGE_INSTANCE) String ovfUrl, @Context IRESTBuilder restBuilder)
    {
        System.out.println("downloadOVFPackageInstance application/ovfpackage-octet-stream");

        StringBuilder sb =
            new StringBuilder(AMResource.getValidAMRemoteService(r, serviceType, datacenterId)
                .getUri());

        // Returns redirection for actual available file (AM URL)
        OVFPackageInstanceDto instance =
            am.getOVFPackageInstance(sb.toString(), idEnterprise, ovfUrl);

        // TODO: Gets actual filepath and downloads content
        sb.append("/");
        sb.append(instance.getDiskFilePath());

        return sb.toString();
    }

    /**
     * Returns the status of OVFPackageInstance
     * 
     * @return
     */
    @GET
    @Produces("application/ovfpackagestatus-xml")
    public OVFPackageInstanceStatusDto getOVFPackageInstanceStatus(
        @PathParam(DatacenterResource.DATACENTER) Integer datacenterId,
        @PathParam(AMResource.AM_SERVICE_TYPE) String serviceType,
        @PathParam(EnterpriseResource.ENTERPRISE) String idEnterprise,
        @PathParam(OVF_PACKAGE_INSTANCE) String ovfUrl, @Context IRESTBuilder restBuilder)
    {

        System.out.println("getOVFPackageInstanceStatus application/ovfpackagestatus-xml");

        OVFPackageInstanceStatusDto dto =
            am.getOVFPackageStatus(AMResource.getValidAMRemoteService(r, serviceType, datacenterId)
                .getUri(), idEnterprise, ovfUrl);

        return dto;
    }

    /**
     * Removes this OVF Instance from Datacenter
     * 
     * @param datacenterId
     * @param serviceType
     * @param idEnterprise
     * @param ovfUrl
     * @param restBuilder
     */
    @DELETE
    public OVFPackageInstanceStatusDto deleteOVFPackageInstance(
        @PathParam(DatacenterResource.DATACENTER) Integer datacenterId,
        @PathParam(AMResource.AM_SERVICE_TYPE) String serviceType,
        @PathParam(EnterpriseResource.ENTERPRISE) String idEnterprise,
        @PathParam(OVF_PACKAGE_INSTANCE) String ovfUrl, @Context IRESTBuilder restBuilder)
    {
        OVFPackageInstanceStatusDto deletedDto =
            am.deleteOVFPackageInstance(
                AMResource.getValidAMRemoteService(r, serviceType, datacenterId).getUri(),
                idEnterprise, ovfUrl);

        return deletedDto;
    }

    @POST
    @Path(BUNDLE_ACTION)
    public OVFPackageInstanceDto createBundle(
        @PathParam(DatacenterResource.DATACENTER) Integer datacenterId,
        @PathParam(AMResource.AM_SERVICE_TYPE) String serviceType,
        @PathParam(EnterpriseResource.ENTERPRISE) String idEnterprise,
        @PathParam(OVF_PACKAGE_INSTANCE) String ovfUrl, @Context IRESTBuilder restBuilder)
    {
        OVFPackageInstanceDto bundledDto =
            am.bundleOVFPackage(AMResource.getValidAMRemoteService(r, serviceType, datacenterId)
                .getUri(), idEnterprise, ovfUrl);

        // OVFPackageInstanceDto bundledDto =
        // am.bundleOVFPackage(getValidRemoteServiceByDatacenter(serviceType, datacenterId)
        // .getUri(), idEnterprise, ovfUrl);
        //
        // return bundledDto;

        return null;
    }

    // @POST
    // @Path(ENTERPRISE_REPO_RESOURCE_PATH + "{idEnterprise}/" + OVF_PACKAGE_RESOURCE_PATH
    // + "cancelDeploy/")
    // public void cancelOVFDownLoad(String ovfId, // @QueryParam("ovfID")
    // @PathParam("idEnterprise") String idEnterprise);

}
