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

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
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
import com.abiquo.server.core.appslibrary.OVFPackageList;
import com.abiquo.server.core.appslibrary.OVFPackageListDto;
import com.abiquo.server.core.appslibrary.OVFPackageListsDto;

@Parent(EnterpriseResource.class)
@Path(OVFPackageListsResource.OVF_PACKAGE_LISTS_PATH)
@Controller
public class OVFPackageListsResource extends AbstractResource
{
    public static final String OVF_PACKAGE_LISTS_PATH = "appslib/ovfpackagelists";

    @Autowired
    private OVFPackageListService service;

    @Autowired
    private AppsLibraryTransformer transformer;

    @GET
    public OVFPackageListsDto getOVFPackageLists(
        @PathParam(EnterpriseResource.ENTERPRISE) final Integer idEnterprise,
        @Context final IRESTBuilder restBuilder) throws Exception
    {
        List<OVFPackageList> all = service.getOVFPackageListsByEnterprise(idEnterprise);

        OVFPackageListsDto ovfPackageListsDto = new OVFPackageListsDto();

        Integer totalSize = 0;
        if (all != null && !all.isEmpty())
        {
            for (OVFPackageList r : all)
            {
                ovfPackageListsDto.add(transformer.createTransferObject(r, restBuilder));
            }
            totalSize = all.size();
        }

        ovfPackageListsDto.setTotalSize(totalSize);

        return ovfPackageListsDto;
    }

    /**
     * if OVF_PACKAGE_POST_QUERY_PARM is set do not use the content body OVFPackageListDto.
     */
    @POST
    @Consumes(MediaType.APPLICATION_XML)
    public OVFPackageListDto postOVFPackageList(
        @PathParam(EnterpriseResource.ENTERPRISE) final Integer idEnterprise,
        final OVFPackageListDto ovfPackageList, @Context final IRESTBuilder restBuilder)
        throws Exception
    {

        OVFPackageList opl = transformer.createPersistenceObject(ovfPackageList);
        opl = service.addOVFPackageList(opl, idEnterprise);

        return transformer.createTransferObject(opl, restBuilder);
    }

    /**
     * if OVF_PACKAGE_POST_QUERY_PARM is set do not use the content body OVFPackageListDto.
     */
    @POST
    @Consumes(MediaType.TEXT_PLAIN)
    public OVFPackageListDto postOVFPackageListWithOVFIndex(
        @PathParam(EnterpriseResource.ENTERPRISE) final Integer idEnterprise,
        final String ovfindexURL, @Context final IRESTBuilder restBuilder) throws Exception
    {
        OVFPackageList opl = service.addOVFPackageList(ovfindexURL, idEnterprise);

        return transformer.createTransferObject(opl, restBuilder);
    }
}
