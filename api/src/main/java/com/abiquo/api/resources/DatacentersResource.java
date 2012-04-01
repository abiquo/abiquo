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

package com.abiquo.api.resources;

import static com.abiquo.api.resources.DatacenterResource.createPersistenceObject;
import static com.abiquo.api.resources.DatacenterResource.createTransferObject;
import static com.abiquo.api.resources.RemoteServiceResource.createPersistenceObjects;
import static com.abiquo.api.resources.RemoteServiceResource.createTransferObject;

import java.util.Collection;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;

import org.apache.wink.common.annotations.Workspace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;

import com.abiquo.api.services.DatacenterService;
import com.abiquo.api.services.EnterpriseService;
import com.abiquo.api.services.RemoteServiceService;
import com.abiquo.api.spring.security.SecurityService;
import com.abiquo.api.util.IRESTBuilder;
import com.abiquo.model.enumerator.Privileges;
import com.abiquo.server.core.enterprise.Enterprise;
import com.abiquo.server.core.infrastructure.Datacenter;
import com.abiquo.server.core.infrastructure.DatacenterDto;
import com.abiquo.server.core.infrastructure.DatacentersDto;
import com.abiquo.server.core.infrastructure.RemoteService;
import com.abiquo.server.core.infrastructure.RemoteServicesDto;

@Path(DatacentersResource.DATACENTERS_PATH)
@Controller
@Workspace(workspaceTitle = "Abiquo administration workspace", collectionTitle = "Datacenters")
public class DatacentersResource extends AbstractResource
{
    public static final String DATACENTERS_PATH = "admin/datacenters";

    // TODO get allowed datacenters on DatacentersResourcePremium

    @Autowired
    private DatacenterService service;

    @Autowired
    private RemoteServiceService remoteServiceService;

    @Autowired
    private EnterpriseService entService;

    @Autowired
    private SecurityService securityService;

    /**
     * Returns all datacenters
     * 
     * @title Retrieve a list of Datacenters
     * @param restBuilder a Context-injected object to create the links of the Dto
     * @param idEnterprise identifier of an enterprise
     * @param pricingId identifier of a pricing template
     * @return a {DatacentersDto} object with all datacenters
     * @throws Exception
     */
    @GET
    @Produces(DatacentersDto.MEDIA_TYPE)
    public DatacentersDto getDatacenters(@Context final IRESTBuilder restBuilder,
        @QueryParam(value = "idEnterprise") final String idEnterprise,
        @QueryParam("pricing") final Integer pricingId) throws Exception
    {
        Collection<Datacenter> list = null;
        if (pricingId != null)
        {
            if (!securityService.hasPrivilege(Privileges.PRICING_VIEW))
            {
                securityService.requirePrivilege(Privileges.PRICING_VIEW);
            }
        }
        if (StringUtils.hasText(idEnterprise))
        {
            Enterprise enterprise = entService.getEnterprise(new Integer(idEnterprise));
            list = service.getDatacenters(enterprise);
        }
        else
        {
            list = service.getDatacenters();
        }

        DatacentersDto datacenters = new DatacentersDto();
        for (Datacenter d : list)
        {
            DatacenterDto dcdto = createTransferObject(d, restBuilder);
            datacenters.add(dcdto);
        }

        return datacenters;
    }

    /**
     * Returns all datacenters with own remote services
     * 
     * @title Retrieve a list of datacenters with their Remote Services
     * @param restBuilder a Context-injected object to create the links of the Dto
     * @param idEnterprise identifier of an enterprise
     * @return a {DatacentersDto} object with all datacenters and own remote services
     * @throws Exception
     */
    @GET
    @Produces(DatacentersDto.RS_MEDIA_TYPE)
    public DatacentersDto getDatacentersWithRS(@Context final IRESTBuilder restBuilder,
        @QueryParam(value = "idEnterprise") final String idEnterprise) throws Exception
    {
        Collection<Datacenter> list = null;
        if (StringUtils.hasText(idEnterprise))
        {
            Enterprise enterprise = entService.getEnterprise(new Integer(idEnterprise));
            list = service.getDatacenters(enterprise);
        }
        else
        {
            list = service.getDatacenters();
        }

        DatacentersDto datacenters = new DatacentersDto();
        for (Datacenter d : list)
        {
            DatacenterDto dcdto = createTransferObject(d, restBuilder);
            dcdto.setRemoteServices(new RemoteServicesDto());

            List<RemoteService> remoteServices =
                remoteServiceService.getRemoteServicesByDatacenter(d.getId());
            if (remoteServices != null)
            {
                for (RemoteService rs : remoteServices)
                {
                    dcdto.getRemoteServices().add(createTransferObject(rs, restBuilder));
                }
            }

            datacenters.add(dcdto);
        }

        return datacenters;
    }

    /**
     * Creates a datacenter and returns it after creation
     * 
     * @title Create a new Datacenter
     * @wiki A datacenter can be created with remote services if you send them in the dto. In this
     *       case if the remote services return configuration errors, then the datacenter will be
     *       created and the response will show the configuration errors.
     * @param datacenterDto datacenter to create
     * @param restBuilder a Context-injected object to create the links of the Dto
     * @return a {DatacenterDto} object with the created datacenter
     * @throws Exception
     */
    @POST
    @Produces(DatacenterDto.MEDIA_TYPE)
    @Consumes(DatacenterDto.MEDIA_TYPE)
    public DatacenterDto postDatacenter(final DatacenterDto datacenterDto,
        @Context final IRESTBuilder restBuilder) throws Exception
    {
        // create dacenter
        Datacenter datacenter = createPersistenceObject(datacenterDto);
        Datacenter d = service.addDatacenter(datacenter);
        DatacenterDto dto = createTransferObject(d, restBuilder);

        // create remote services if any
        if (datacenterDto.getRemoteServices() != null)
        {
            RemoteServicesDto rsd =
                service.addRemoteServices(
                    createPersistenceObjects(datacenterDto.getRemoteServices()), datacenter);
            dto.setRemoteServices(rsd);
        }
        return dto;
    }
}
