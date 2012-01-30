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

import java.util.Collections;
import java.util.List;

import javax.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.abiquo.api.services.InfrastructureService;
import com.abiquo.api.tracer.TracerLogger;
import com.abiquo.appliancemanager.client.ApplianceManagerResourceStubImpl;
import com.abiquo.appliancemanager.transport.TemplateDto;
import com.abiquo.commons.amqp.impl.am.AMCallback;
import com.abiquo.commons.amqp.impl.am.domain.TemplateStatusEvent;
import com.abiquo.model.enumerator.RemoteServiceType;
import com.abiquo.server.core.appslibrary.VirtualMachineTemplate;
import com.abiquo.server.core.infrastructure.Repository;
import com.abiquo.tracer.ComponentType;
import com.abiquo.tracer.EventType;
import com.abiquo.tracer.SeverityType;

/**
 * Receives events from the ApplianceManager indicating new available {@link TemplateDto} and create
 * new {@link VirtualMachineTemplate}
 */
@Service
public class AMEventProcessor implements AMCallback
{
    protected final static Logger logger = LoggerFactory.getLogger(AMEventProcessor.class);

    @Autowired
    protected InfrastructureService infService;

    @Autowired
    private TemplateFactory templateFactory;

    @Autowired
    private TracerLogger tracer;

    public AMEventProcessor(final EntityManager em)
    {
    }

    public AMEventProcessor()
    {
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    public void onDownload(final TemplateStatusEvent event)
    {
        logger.debug("Virtual Machine Template [{}] added", event.getOvfId());

        try
        {
            processDownload(event);

            final String msg =
                String.format("Virtual Machine Template [%s] added to repository [%s]",
                    event.getOvfId(), event.getRepositoryLocation());
            tracer.systemLog(SeverityType.INFO, ComponentType.APPLIANCE_MANAGER, EventType.VI_ADD,
                msg);

        }
        catch (Exception e)
        {
            final String msg =
                String.format("Virtual Machine Template [%s] can not be added to repository [%s]",
                    event.getOvfId(), event.getRepositoryLocation());
            tracer.systemError(SeverityType.NORMAL, ComponentType.APPLIANCE_MANAGER,
                EventType.VI_ADD, e, msg);
        }

    }

    protected List<VirtualMachineTemplate> processDownload(final TemplateStatusEvent evnt)
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

        TemplateDto packageInstance = amStub.getTemplate(idEnterp, ovfId);

        return templateFactory.insertVirtualMachineTemplates(
            Collections.singletonList(packageInstance), repository);
    }

    @Override
    public void onNotDownload(final TemplateStatusEvent event)
    {
        logger.debug("VirtualMachineTemplate [{}] canceled/deleted ", event.getOvfId());

        final String msg =
            String.format("Virtual Machine Template [%s] deleted from repository [%s]",
                event.getOvfId(), event.getRepositoryLocation());

        tracer.systemLog(SeverityType.INFO, ComponentType.APPLIANCE_MANAGER, EventType.VI_DELETE,
            msg);
    }

    @Override
    public void onError(final TemplateStatusEvent event)
    {
        final String errorCause = event.getErrorCause();

        logger.error("VirtualMachineTemplate download error :" + errorCause);

        final String msg =
            String.format(
                "Error during the virtual machine template [%s] download to repository [%s]: %s ",
                event.getOvfId(), event.getRepositoryLocation(), errorCause);

        tracer.systemLog(SeverityType.CRITICAL, ComponentType.APPLIANCE_MANAGER,
            EventType.VI_DOWNLOAD, msg);

    }

    @Override
    public void onDownloading(final TemplateStatusEvent event)
    {
        logger.debug("Downloading VirtualMachineTemplate [{}]", event.getOvfId());
    }

}
