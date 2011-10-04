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

package com.abiquo.api.services.appslibrary.event;

import static com.abiquo.tracer.Enterprise.enterprise;
import static com.abiquo.tracer.Platform.platform;
import static com.abiquo.tracer.User.user;

import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.abiquo.api.services.InfrastructureService;
import com.abiquo.appliancemanager.client.ApplianceManagerResourceStubImpl;
import com.abiquo.appliancemanager.transport.OVFPackageInstanceDto;
import com.abiquo.commons.amqp.impl.am.AMCallback;
import com.abiquo.commons.amqp.impl.am.domain.OVFPackageInstanceStatusEvent;
import com.abiquo.model.enumerator.RemoteServiceType;
import com.abiquo.server.core.cloud.VirtualImage;
import com.abiquo.server.core.infrastructure.Repository;
import com.abiquo.tracer.ComponentType;
import com.abiquo.tracer.Platform;
import com.abiquo.tracer.SeverityType;
import com.abiquo.tracer.UserInfo;
import com.abiquo.tracer.client.TracerFactory;

/**
 * Receives events from the ApplianceManager indicating new available {@link OVFPackageInstanceDto}
 * and create new {@link VirtualImage}
 */
@Service
public class OVFPackageInstanceStatusEventProcessor implements AMCallback
{
    private final static Logger logger = LoggerFactory
        .getLogger(OVFPackageInstanceStatusEventProcessor.class);

    static Platform platform = platform("abicloud").enterprise(
        enterprise("abiCloud").user(user("SYSTEM")));

    static UserInfo ui = new UserInfo("SYSTEM");

    @Autowired
    private InfrastructureService infService;

    @Autowired
    private OVFPackageInstanceToVirtualImage ovfToVimage;

    @Override
    public void onDownload(OVFPackageInstanceStatusEvent event)
    {
        logger.debug("VirtualImage [{}] added", event.getOvfId());

        try
        {
            processDownload(event);

            TracerFactory.getTracer().log(
                SeverityType.INFO,
                ComponentType.APPLIANCE_MANAGER,
                com.abiquo.tracer.EventType.VI_ADD,
                String.format("Virtual image [%s] added to repository [%s]", event.getOvfId(),
                    event.getRepositoryLocation()), ui, platform);
        }
        catch (Exception e)
        {
            TracerFactory.getTracer().log(
                SeverityType.NORMAL,
                ComponentType.APPLIANCE_MANAGER,
                com.abiquo.tracer.EventType.VI_ADD,
                String.format("Virtual image [%s] can not be added to repository [%s]: %s",
                    event.getOvfId(), event.getRepositoryLocation(), e.getMessage()),
                new UserInfo("SYSTEM"), platform);
        }

    }

    protected void processDownload(final OVFPackageInstanceStatusEvent evnt)
    {
        final String ovfId = evnt.getOvfId();
        final String idEnterp = evnt.getEnterpriseId();
        final String repoLocation = evnt.getRepositoryLocation();

        final Repository repository = infService.getRepositoryFromLocation(repoLocation);
        final Integer dcId = repository.getDatacenter().getId();
        final String amServiceUri =
            infService.getRemoteService(dcId, RemoteServiceType.APPLIANCE_MANAGER).getUri();

        ApplianceManagerResourceStubImpl amStub =
            new ApplianceManagerResourceStubImpl(amServiceUri);

        OVFPackageInstanceDto packageInstance = amStub.getOVFPackageInstance(idEnterp, ovfId);

        ovfToVimage.insertVirtualImages(Collections.singletonList(packageInstance),
            repository);
    }

    @Override
    public void onNotDownload(OVFPackageInstanceStatusEvent event)
    {
        logger.debug("VirtualImage [{}] canceled/deleted ", event.getOvfId());

        TracerFactory.getTracer().log(
            SeverityType.INFO,
            ComponentType.APPLIANCE_MANAGER,
            com.abiquo.tracer.EventType.VI_DELETE,
            String.format("Virtual image [%s] deleted from repository [%s]", event.getOvfId(),
                event.getRepositoryLocation()), ui, platform);
    }

    @Override
    public void onError(OVFPackageInstanceStatusEvent event)
    {
        final String errorCause = event.getErrorCause();

        logger.error("VirtualImage download error :" + errorCause);

        TracerFactory.getTracer().log(
            SeverityType.CRITICAL,
            ComponentType.APPLIANCE_MANAGER,
            com.abiquo.tracer.EventType.VI_DOWNLOAD,
            String.format("Error during the virtual image [%s] download to repository [%s]: %s ",
                event.getOvfId(), event.getRepositoryLocation(), errorCause), ui, platform);
    }

    @Override
    public void onDownloading(OVFPackageInstanceStatusEvent event)
    {
        logger.debug("Downloading VirtualImage [{}]", event.getOvfId());
    }

}
