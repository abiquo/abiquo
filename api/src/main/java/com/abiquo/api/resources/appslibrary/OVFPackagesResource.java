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

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;

import org.apache.wink.common.annotations.Parent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.abiquo.api.resources.AbstractResource;
import com.abiquo.api.resources.EnterpriseResource;
import com.abiquo.api.services.appslibrary.OVFPackageService;
import com.abiquo.api.transformer.AppsLibraryTransformer;
import com.abiquo.api.util.IRESTBuilder;
import com.abiquo.server.core.appslibrary.OVFPackage;
import com.abiquo.server.core.appslibrary.OVFPackageDto;
import com.abiquo.server.core.appslibrary.OVFPackagesDto;

@Parent(EnterpriseResource.class)
@Path(OVFPackagesResource.OVF_PACKAGES_PATH)
@Controller
public class OVFPackagesResource extends AbstractResource
{
    public static final String OVF_PACKAGES_PATH = "appslib/ovfpackages";

    @Autowired
    private OVFPackageService service;

    @Autowired
    private AppsLibraryTransformer transformer;

    @GET
    public OVFPackagesDto getOVFPackages(
        @PathParam(EnterpriseResource.ENTERPRISE) final Integer idEnterprise,
        @Context final IRESTBuilder restBuilder) throws Exception
    {
        List<OVFPackage> all = service.getOVFPackagesByEnterprise(idEnterprise);

        OVFPackagesDto ovfPackages = new OVFPackagesDto();
        if (all != null && !all.isEmpty())
        {
            for (OVFPackage d : all)
            {
                ovfPackages.add(transformer.createTransferObject(d, restBuilder));
            }
        }

        return ovfPackages;
    }

    @POST
    public OVFPackageDto postOVFPackage(
        @PathParam(EnterpriseResource.ENTERPRISE) final Integer idEnterprise, final OVFPackageDto ovfPackage,
        @Context final IRESTBuilder restBuilder) throws Exception
    {
        OVFPackage opl = transformer.createPersistenceObject(ovfPackage);
        opl = service.addOVFPackage(opl, idEnterprise);
        return transformer.createTransferObject(opl, restBuilder);
    }

}
