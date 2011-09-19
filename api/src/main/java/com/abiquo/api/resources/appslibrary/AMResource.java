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

import org.apache.wink.common.annotations.Parent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.abiquo.api.exceptions.APIError;
import com.abiquo.api.exceptions.NotFoundException;
import com.abiquo.api.resources.DatacenterResource;
import com.abiquo.api.resources.RemoteServicesResource;
import com.abiquo.api.resources.StaticRemoteServiceResource;
import com.abiquo.api.services.InfrastructureService;
import com.abiquo.api.services.stub.ApplianceManagerStub;
import com.abiquo.appliancemanager.transport.AMConfigurationDto;
import com.abiquo.model.enumerator.RemoteServiceType;
import com.abiquo.server.core.infrastructure.RemoteService;

/**
 * Implements Appliance Manager specific actions:
 */
@Parent(RemoteServicesResource.class)
@Path(AMResource.AM_SERVICE_PATH)
@Controller
public class AMResource extends StaticRemoteServiceResource
{
    /** The unique type of this remote service . */
    public static final String AM_SERVICE_TYPE = "appliance_manager";

    /** The path to manage the actions of this remote service. */
    public static final String AM_SERVICE_PATH = "appliancemanager";

    // /** The path to the brief action. */
    // public static final String DOWNLOAD_DISK_FILE = "actions/downloadDiskFile";
    //
    // /** The path to the managedPools action. */
    // public static final String AVAILABLE_POOLS_ACTION = "actions/availablePools";

    /**
     * The service that contains business logic.
     */
    // @Autowired
    private InfrastructureService service;

    /**
     * The stub used to connect to the AM module
     */
    @Autowired
    private ApplianceManagerStub am;

    /* REST Methods */
    @GET
    public AMConfigurationDto getAMConfiguration(
        @PathParam(DatacenterResource.DATACENTER) final Integer datacenterId,
        @PathParam(AM_SERVICE_PATH) final String serviceType) throws Exception
    {

        validatePathParameters(datacenterId, serviceType);

        // TODO:
        // AMConfigurationDto config = am.getConfiguration();
        AMConfigurationDto config = new AMConfigurationDto();

        return config;

        // return transformer.createTransferObject(ovfPackage, restBuilder);
    }

    /**
     * 
     */
    protected static RemoteService getValidAMRemoteService(final InfrastructureService r,
        final String serviceType, final Integer datacenterId)
    {
        if (!isApplianceManagerType(serviceType))
        {
            throw new NotFoundException(APIError.WRONG_REMOTE_SERVICE_TYPE);
        }

        RemoteServiceType type = RemoteServiceType.APPLIANCE_MANAGER;

        if (!r.isAssignedTo(datacenterId, type))
        {
            throw new NotFoundException(APIError.NOT_ASSIGNED_REMOTE_SERVICE_DATACENTER);
        }

        return r.getRemoteService(datacenterId, type);
    }

    private static boolean isApplianceManagerType(final String serviceType)
    {
        RemoteServiceType type = RemoteServiceType.valueFromName(serviceType.toUpperCase());
        boolean result = false;

        if (type != null)
        {
            result = type == RemoteServiceType.APPLIANCE_MANAGER;
        }

        return result;
    }

    private void validatePathParameters(final Integer datacenterId, final String serviceType)
        throws NotFoundException
    {
        RemoteServiceType type = RemoteServiceType.valueOf(serviceType.toUpperCase());

        if (type == null)
        {
            throw new NotFoundException(APIError.NON_EXISTENT_REMOTE_SERVICE_TYPE);
        }

        if (type != RemoteServiceType.APPLIANCE_MANAGER)
        {
            throw new NotFoundException(APIError.WRONG_REMOTE_SERVICE_TYPE);
        }

        if (!service.isAssignedTo(datacenterId, type))
        {
            throw new NotFoundException(APIError.NOT_ASSIGNED_REMOTE_SERVICE_DATACENTER);
        }
    }

    @Override
    protected String getRemoteServiceType()
    {
        return AM_SERVICE_TYPE;
    }
}
