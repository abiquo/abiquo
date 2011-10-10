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

package com.abiquo.am.services.notify;

import java.io.IOException;

import com.abiquo.am.services.EnterpriseRepositoryService;
import com.abiquo.am.services.OVFPackageInstanceFileSystem;
import com.abiquo.appliancemanager.config.AMConfigurationManager;
import com.abiquo.appliancemanager.exceptions.EventException;
import com.abiquo.appliancemanager.transport.OVFPackageInstanceStatusType;
import com.abiquo.commons.amqp.impl.am.AMProducer;
import com.abiquo.commons.amqp.impl.am.domain.OVFPackageInstanceStatusEvent;
import com.abiquo.ovfmanager.ovf.exceptions.IdNotFoundException;

public class AMNotifier extends AMProducer// BasicProducer<AMConfiguration,
// OVFPackageInstanceStatusEvent>
{

    // used on AMSink to discrimitate the Datacenter it belongs to .
    private final static String REPO_LOCATION = AMConfigurationManager.getInstance()
        .getAMConfiguration().getRepositoryLocation();

    /**
     * Change the status for the provided OVF package Id.
     * 
     * @param ovfId, the OVF package identifier.
     * @param status, the new status to set on the Description.
     * @throws IdNotFoundException
     * @throws RepositoryException
     * @throws EventException
     */
    public void setOVFStatus(final String erId, final String ovfId,
        OVFPackageInstanceStatusType status) throws EventException
    {
        assert status != OVFPackageInstanceStatusType.ERROR;

        final String enterpriseRepositoryPath =
            EnterpriseRepositoryService.getRepo(erId).getEnterpriseRepositoryPath();
        OVFPackageInstanceFileSystem.createOVFStatusMarks(enterpriseRepositoryPath, ovfId, status,
            null);

        notifyOVFStatusEvent(erId, ovfId, status, null);
    }

    /**
     * Change the status for the provided OVF package Id to ERROR with the given error message.
     * 
     * @param ovfId, the OVF package identifier.
     * @param errorMessage, the cause of the error.
     * @throws IdNotFoundException
     * @throws EventException
     * @throws RepositoryException
     */
    public void setOVFStatusError(final String erId, final String ovfId, final String errorMessage)
        throws EventException
    {
        assert errorMessage != null;

        final String enterpriseRepositoryPath =
            EnterpriseRepositoryService.getRepo(erId).getEnterpriseRepositoryPath();
        OVFPackageInstanceFileSystem.createOVFStatusMarks(enterpriseRepositoryPath, ovfId,
            OVFPackageInstanceStatusType.ERROR, errorMessage);

        notifyOVFStatusEvent(erId, ovfId, OVFPackageInstanceStatusType.ERROR, errorMessage);
    }

    private void notifyOVFStatusEvent(final String erId, final String ovfId,
        final OVFPackageInstanceStatusType status, final String errorMsg) throws EventException
    {
        assert status != OVFPackageInstanceStatusType.ERROR || errorMsg != null;

        OVFPackageInstanceStatusEvent event = new OVFPackageInstanceStatusEvent();
        event.setOvfId(ovfId);
        event.setStatus(status.name());
        event.setEnterpriseId(erId);
        event.setErrorCause(errorMsg);
        event.setRepositoryLocation(REPO_LOCATION);

        // TODO discriminate by queue
        // event.setRepositoryLocation(repositoryLocation);
        // event.setOvfId(ovfId);
        // event.setOvfPackageStatus(status);

        synchronized (this)
        {
            try
            {
                publish(event);
            }
            catch (IOException e)
            {
                throw new EventException(e);
            }
        }
    }
}
