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

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;

import org.apache.wink.common.annotations.Parent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.abiquo.api.resources.AbstractResource;
import com.abiquo.api.resources.DatacenterResource;
import com.abiquo.api.services.InfrastructureService;
import com.abiquo.api.services.stub.ApplianceManagerStub;
import com.abiquo.api.util.IRESTBuilder;
import com.abiquo.server.core.enterprise.EnterprisesDto;
import com.abiquo.server.core.infrastructure.RemoteService;

@Parent(AMResource.class)
@Path(EnterpriseRepositoriesResource.ENTERPRISE_REP_PATH)
@Controller
public class EnterpriseRepositoriesResource extends AbstractResource
{
    public static final String ENTERPRISE_REP_PATH = "er";

    @Autowired
    private InfrastructureService r;

    @Autowired
    private ApplianceManagerStub am;

    @GET
    public EnterprisesDto getEnterpriseRepositories(
        @PathParam(DatacenterResource.DATACENTER) Integer datacenterId,
        @PathParam(AMResource.AM_SERVICE_TYPE) String serviceType, @Context IRESTBuilder restBuilder)
        throws Exception
    {

        RemoteService service = AMResource.getValidAMRemoteService(r, serviceType, datacenterId);

        EnterprisesDto erList = am.getEnterpriseRepositoriesByDC(service.getUri());

        return erList;

    }

}
