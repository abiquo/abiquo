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

import java.util.LinkedList;
import java.util.List;

import javax.annotation.Resource;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

import org.apache.wink.common.annotations.Parent;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import com.abiquo.am.services.EnterpriseRepositoryFileSystem;
import com.abiquo.am.services.EnterpriseRepositoryService;
import com.abiquo.am.services.OVFPackageInstanceService;
import com.abiquo.appliancemanager.config.AMConfigurationManager;
import com.abiquo.appliancemanager.transport.OVFPackageInstanceDto;
import com.abiquo.server.core.appslibrary.OVFPackageDto;
import com.abiquo.server.core.appslibrary.OVFPackageListDto;

/*
 * FIXME proof of concept WIP
 */
@Parent(EnterpriseRepositoryResource.class)
@Path(OVFPackageListResource.OVFLIST_PATH)
@Service(value = "ovfPackageListResource")
public class OVFPackageListResource
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

    public static final String OVFLIST_PATH = ApplianceManagerPaths.OVFLIST_PATH;

    private final static String REPO_LOCATION = AMConfigurationManager.getInstance()
        .getAMConfiguration().getRepositoryLocation();

    @GET
    public OVFPackageListDto getAsOVFRepository(
        @PathParam(EnterpriseRepositoryResource.ENTERPRISE_REPOSITORY) String idEnterprise,
        @Context UriInfo contextUri)
    {
        List<String> availables =
            EnterpriseRepositoryFileSystem.getAllOVF(
                EnterpriseRepositoryService.getRepo(idEnterprise).getEnterpriseRepositoryPath(),
                false);

        List<OVFPackageInstanceDto> lst = new LinkedList<OVFPackageInstanceDto>();
        for (String id : availables)
        {
            lst.add(service.getOVFPackage(idEnterprise, id));
        }

        OVFPackageListDto ovflist = new OVFPackageListDto();
        ovflist.setOvfPackages(new LinkedList<OVFPackageDto>());

        for (OVFPackageInstanceDto ovfpi : lst)
        {
            // final String ovfurl = String.format("http://%s:%d/files/%s",
            // contextUri.getBaseUri().getHost(),);
            ovfpi.setUrl("myurl");
            ovflist.getOvfPackages().add(ovfpi);
        }

        ovflist.setName(String.format("Enterprise %s at %s", idEnterprise, REPO_LOCATION));
        ovflist.setUrl(contextUri.getAbsolutePath().toASCIIString());

        return ovflist;
    }

}
