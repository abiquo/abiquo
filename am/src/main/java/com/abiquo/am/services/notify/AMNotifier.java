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

import org.springframework.stereotype.Component;

import com.abiquo.am.data.AMRedisDao;
import com.abiquo.am.exceptions.AMError;
import com.abiquo.am.services.ErepoFactory;
import com.abiquo.am.services.filesystem.TemplateFileSystem;
import com.abiquo.appliancemanager.config.AMConfiguration;
import com.abiquo.appliancemanager.exceptions.AMException;
import com.abiquo.appliancemanager.exceptions.EventException;
import com.abiquo.appliancemanager.transport.TemplateStatusEnumType;
import com.abiquo.commons.amqp.impl.am.AMProducer;
import com.abiquo.commons.amqp.impl.am.domain.TemplateStatusEvent;
import com.abiquo.ovfmanager.ovf.exceptions.IdNotFoundException;

@Component
public class AMNotifier extends AMProducer
{
    public AMNotifier()
    {

    }

    // used on AMSink to discrimitate the Datacenter it belongs to .
    private final static String REPO_LOCATION = AMConfiguration.getRepositoryLocation();

    /**
     * Change the status for the provided OVF package Id.
     * 
     * @param ovfId, the OVF package identifier.
     * @param status, the new status to set on the Description.
     * @throws IdNotFoundException
     * @throws RepositoryException
     * @throws EventException
     */
    public void setTemplateStatus(final String erId, final String ovfId,
        final TemplateStatusEnumType status)
    {
        final String enterpriseRepositoryPath = ErepoFactory.getRepo(erId).path();

        TemplateFileSystem.createTemplateStatusMarks(enterpriseRepositoryPath, ovfId, status, null);

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
    public void setTemplateStatusError(final String erId, final String ovfId,
        final String errorMessage)
    {
        final String enterpriseRepositoryPath = ErepoFactory.getRepo(erId).path();

        TemplateFileSystem.createTemplateStatusMarks(enterpriseRepositoryPath, ovfId,
            TemplateStatusEnumType.ERROR, errorMessage);

        notifyOVFStatusEvent(erId, ovfId, TemplateStatusEnumType.ERROR, errorMessage);
    }

    private void notifyOVFStatusEvent(final String erId, final String ovfId,
        final TemplateStatusEnumType status, final String errorMsg)
    {
        AMRedisDao dao = AMRedisDao.getDao();
        try
        {
            if (status == TemplateStatusEnumType.ERROR)
            {
                dao.setError(erId, ovfId, errorMsg);
            }
            else
            {
                dao.setState(erId, ovfId, status);
            }
        }
        finally
        {
            AMRedisDao.returnDao(dao);
        }

        TemplateStatusEvent event = new TemplateStatusEvent();
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
                openChannel();
                publish(event);
                closeChannel();
            }
            catch (IOException e)
            {
                throw new AMException(AMError.AM_NOTIFICATION, e);
            }
        }
    }
}
