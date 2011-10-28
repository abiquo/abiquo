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
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
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
import com.abiquo.api.services.InfrastructureService;
import com.abiquo.api.services.appslibrary.OVFPackageService;
import com.abiquo.api.transformer.AppsLibraryTransformer;
import com.abiquo.api.util.IRESTBuilder;
import com.abiquo.appliancemanager.transport.OVFPackageInstanceStateDto;
import com.abiquo.server.core.appslibrary.OVFPackage;
import com.abiquo.server.core.appslibrary.OVFPackageDto;

@Parent(OVFPackagesResource.class)
@Path(OVFPackageResource.OVF_PACKAGE_PARAM)
@Controller
public class OVFPackageResource extends AbstractResource
{

    public static final String OVF_PACKAGE = "ovfPackage";

    public static final String OVF_PACKAGE_PARAM = "{" + OVF_PACKAGE + "}";

    public static final String OVF_PACKAGE_INSTALL_ACTION_PATH = "actions/repositoryInstall";

    public static final String OVF_PACKAGE_UN_INSTALL_ACTION_PATH = "actions/repositoryUninstall";

    public static final String OVF_PACKAGE_REPOSITORY_STATUS_PATH = "actions/repositoryStatus";

    public static final String OVF_PACKAGE_REPOSITORY_STATUS_DATACENTER_QUERY_PARAM =
        "datacenterId";

    /** Used to know where the AM is located on the current datacenter. */
    // @Autowired
    @Resource(name = "infrastructureService")
    InfrastructureService remoteServices;

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
        OVFPackage ovfpackage = service.getOVFPackage(ovfPackageId);
        return transformer.createTransferObject(ovfpackage, restBuilder);
    }

    @GET
    @Path(OVF_PACKAGE_REPOSITORY_STATUS_PATH)
    public OVFPackageInstanceStateDto getOVFPackageState(
        @PathParam(OVF_PACKAGE) final Integer ovfPackageId,
        @PathParam(EnterpriseResource.ENTERPRISE) final Integer idEnterprise,
        @QueryParam(OVF_PACKAGE_REPOSITORY_STATUS_DATACENTER_QUERY_PARAM) final Integer datacenterId,

        @Context final IRESTBuilder restBuilder) throws Exception
    {
        // TODO check enterprise can use repository
        return service.getOVFPackageState(ovfPackageId, datacenterId, idEnterprise);
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

    /**
     * TODO use the datacenter URI on the post
     */
    @POST
    @Consumes(MediaType.TEXT_PLAIN)
    @Path(OVFPackageResource.OVF_PACKAGE_INSTALL_ACTION_PATH)
    public Void installOVFPackageOnDatacenterRepository(
        @PathParam(EnterpriseResource.ENTERPRISE) final Integer idEnterprise,
        @PathParam(OVF_PACKAGE) final Integer ovfPackageId, final String datacenterId,
        @Context final IRESTBuilder restBuilder) throws Exception

    {
        service.installOVFPackage(ovfPackageId, Integer.valueOf(datacenterId), idEnterprise);
        return null;
    }

    /**
     * TODO use the datacenter URI on the post
     */
    @POST
    @Consumes(MediaType.TEXT_PLAIN)
    @Path(OVFPackageResource.OVF_PACKAGE_UN_INSTALL_ACTION_PATH)
    public Void uninstallOVFPackageOnDatacenterRepository(
        @PathParam(EnterpriseResource.ENTERPRISE) final Integer idEnterprise,
        @PathParam(OVF_PACKAGE) final Integer ovfPackageId, final String datacenterId,
        @Context final IRESTBuilder restBuilder) throws Exception

    {
        service.uninstallOVFPackage(ovfPackageId, Integer.valueOf(datacenterId), idEnterprise);
        return null;
    }
}
