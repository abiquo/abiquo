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

package com.abiquo.api.services.appslibrary;

import static com.abiquo.tracer.Enterprise.enterprise;
import static com.abiquo.tracer.Platform.platform;
import static com.abiquo.tracer.User.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.abiquo.api.exceptions.APIError;
import com.abiquo.api.services.DefaultApiService;
import com.abiquo.api.services.InfrastructureService;
import com.abiquo.appliancemanager.client.ApplianceManagerResourceStubImpl;
import com.abiquo.appliancemanager.client.ApplianceManagerResourceStubImpl.ApplianceManagerStubException;
import com.abiquo.model.enumerator.RemoteServiceType;
import com.abiquo.tracer.ComponentType;
import com.abiquo.tracer.Platform;
import com.abiquo.tracer.SeverityType;
import com.abiquo.tracer.UserInfo;
import com.abiquo.tracer.client.TracerFactory;

@Service
public class DefaultApiServiceWithApplianceManagerClient extends DefaultApiService
{
    final private static Logger logger = LoggerFactory
        .getLogger(DefaultApiServiceWithApplianceManagerClient.class);

    @Autowired
    protected InfrastructureService infService;

    protected ApplianceManagerResourceStubImpl getApplianceManagerClient(final Integer dcId)
    {
        final String amUri =
            infService.getRemoteService(dcId, RemoteServiceType.APPLIANCE_MANAGER).getUri();
        ApplianceManagerResourceStubImpl amStub = new ApplianceManagerResourceStubImpl(amUri);

        try
        {
            amStub.checkService();
        }
        catch (ApplianceManagerStubException e)
        {
            logger.error("ApplianceManager configuration error", e);

            // A user named "SYSTEM" is created
            UserInfo ui = new UserInfo("SYSTEM");
            Platform platform =
                platform("abicloud").enterprise(enterprise("abiCloud").user(user("SYSTEM")));
            TracerFactory.getTracer().log(SeverityType.CRITICAL, ComponentType.APPLIANCE_MANAGER,
                com.abiquo.tracer.EventType.UNKNOWN, e.getLocalizedMessage(), ui, platform);

            addConflictErrors(APIError.VIMAGE_AM_DOWN);
            flushErrors();
        }

        return amStub;
    }
}
