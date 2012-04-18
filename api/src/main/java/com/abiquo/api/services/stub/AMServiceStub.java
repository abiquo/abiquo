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

package com.abiquo.api.services.stub;

import java.net.SocketTimeoutException;
import java.util.LinkedList;
import java.util.List;

import org.apache.wink.client.ClientRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.abiquo.api.exceptions.APIError;
import com.abiquo.api.pools.impl.AMClientPool;
import com.abiquo.api.services.DefaultApiService;
import com.abiquo.api.services.EnterpriseService;
import com.abiquo.api.services.InfrastructureService;
import com.abiquo.appliancemanager.client.AMClient;
import com.abiquo.appliancemanager.client.AMClientException;
import com.abiquo.appliancemanager.transport.EnterpriseRepositoryDto;
import com.abiquo.appliancemanager.transport.TemplateDto;
import com.abiquo.appliancemanager.transport.TemplateIdDto;
import com.abiquo.appliancemanager.transport.TemplateIdsDto;
import com.abiquo.appliancemanager.transport.TemplateStateDto;
import com.abiquo.appliancemanager.transport.TemplateStatusEnumType;
import com.abiquo.appliancemanager.transport.TemplatesStateDto;
import com.abiquo.model.enumerator.RemoteServiceType;
import com.abiquo.model.transport.error.CommonError;
import com.abiquo.server.core.enterprise.DatacenterLimits;

@Service
public class AMServiceStub extends DefaultApiService
{
    private final static Logger LOGGER = LoggerFactory.getLogger(AMServiceStub.class);

    @Autowired
    protected AMClientPool clientPool;

    @Autowired
    protected InfrastructureService infService;

    @Autowired
    protected EnterpriseService entService;

    public TemplateDto getTemplate(final Integer datacenterId, final Integer enterpriseId,
        final String ovfId)
    {
        final AMClient amClient = getAMClient(datacenterId, enterpriseId, false);

        try
        {
            return amClient.getTemplate(enterpriseId, ovfId);
        }
        catch (Exception e)
        {
            reportError(e);
            return null;// unreachable
        }
        finally
        {
            returnClientToPool(amClient);
        }
    }

    public TemplateDto getTemplateBySystem(final Integer datacenterId, final Integer enterpriseId,
        final String ovfId)
    {
        final AMClient amClient = getAMClientBySystem(datacenterId, enterpriseId, false);

        try
        {
            return amClient.getTemplate(enterpriseId, ovfId);
        }
        catch (Exception e)
        {
            reportError(e);
            return null;// unreachable
        }
        finally
        {
            returnClientToPool(amClient);
        }

    }

    public void delete(final Integer datacenterId, final Integer enterpriseId, final String ovfId)
    {
        final AMClient amClient = getAMClient(datacenterId, enterpriseId, false);

        try
        {
            amClient.deleteTemplate(enterpriseId, ovfId);
        }
        catch (Exception e)
        {
            reportError(e);
        }
        finally
        {
            returnClientToPool(amClient);
        }
    }

    public String preBundleTemplate(final Integer datacenterId, final Integer enterpriseId,
        final String instanceName)
    {
        final AMClient amClient = getAMClient(datacenterId, enterpriseId, false);

        try
        {
            return amClient.preBundleTemplate(enterpriseId, instanceName);
        }
        catch (Exception e)
        {
            reportError(e);
            return null;// unreachable
        }
        finally
        {
            returnClientToPool(amClient);
        }
    }

    public EnterpriseRepositoryDto getRepository(final Integer datacenterId,
        final Integer enterpriseId)
    {
        final AMClient amClient = getAMClient(datacenterId, enterpriseId, false);

        try
        {
            return amClient.getRepository(enterpriseId);
        }
        catch (Exception e)
        {
            reportError(e);
            return null;// unreachable
        }
        finally
        {
            returnClientToPool(amClient);
        }
    }

    public TemplatesStateDto getTemplatesState(final Integer datacenterId,
        final Integer enterpriseId, final String... ovfIds)
    {
        final AMClient amClient = getAMClient(datacenterId, enterpriseId, false);

        try
        {
            return amClient.getTemplatesState(enterpriseId, templateIds(ovfIds));
        }
        catch (Exception e)
        {
            reportError(e);
            return null;// unreachable
        }
        finally
        {
            returnClientToPool(amClient);
        }
    }

    public TemplateStateDto getTemplateState(final Integer datacenterId,
        final Integer enterpriseId, final String id)
    {
        final AMClient amClient = getAMClient(datacenterId, enterpriseId, false);

        try
        {
            return amClient.getTemplateStatus(enterpriseId, id);
        }
        catch (Exception e)
        {
            TemplateStateDto st = new TemplateStateDto();
            st.setOvfId(id);
            st.setStatus(TemplateStatusEnumType.DOWNLOADING);
            st.setDownloadingProgress(0.0);

            LOGGER.warn("Can't update the download progress of {} caused by: {}", id,
                e.getMessage());
            return st;
            // reportError(e);
            // return null;// unreachable
        }
        finally
        {
            returnClientToPool(amClient);
        }
    }

    public void install(final Integer datacenterId, final Integer enterpriseId, final String id)
    {
        final AMClient amClient = getAMClient(datacenterId, enterpriseId, false);

        try
        {
            amClient.installTemplateDefinition(enterpriseId, id);
        }
        catch (Exception e)
        {
            reportError(e);
        }
        finally
        {
            returnClientToPool(amClient);
        }
    }

    public List<TemplateDto> refreshRepository(final Integer enterpriseId,
        final Integer datacenterId, final String currentRepositoryLocation)
    {
        final AMClient amClient = getAMClient(datacenterId, enterpriseId, false);

        String repositoryLocation = null;
        try
        {
            repositoryLocation = amClient.getRepositoryConfiguration().getLocation();
        }
        catch (AMClientException e1)
        {
            returnClientToPool(amClient);

            addConflictErrors(APIError.VMTEMPLATE_SYNCH_DC_REPO);
            reportError(e1);
        }

        if (!currentRepositoryLocation.equalsIgnoreCase(repositoryLocation))
        {
            addConflictErrors(APIError.VMTEMPLATE_REPOSITORY_CHANGED);
            flushErrors();
        }

        try
        {
            amClient.refreshRepository(enterpriseId);

            List<TemplateStateDto> downloads =
                amClient.getTemplatesState(enterpriseId, TemplateStatusEnumType.DOWNLOAD)
                    .getCollection();
            List<TemplateDto> disks = new LinkedList<TemplateDto>();
            for (TemplateStateDto download : downloads)
            {
                try
                {
                    disks.add(amClient.getTemplate(enterpriseId, download.getOvfId()));
                }
                catch (Exception e)
                {
                    LOGGER.error("Can't initialize template {}", download.getOvfId(), e);
                }
            }
            return disks;
        }
        catch (Exception e)
        {
            addConflictErrors(APIError.VMTEMPLATE_SYNCH_DC_REPO);
            reportError(e);
            return null;// unreachable
        }
        finally
        {
            returnClientToPool(amClient);
        }
    }

    private TemplateIdsDto templateIds(final String... ovfids)
    {
        TemplateIdsDto ids = new TemplateIdsDto();
        for (String id : ovfids)
        {
            TemplateIdDto tId = new TemplateIdDto();
            tId.setOvfId(id);
            ids.add(tId);
        }
        return ids;
    }

    /**
     * CLIENT
     */

    private AMClient getAMClient(final Integer dcId, final Integer enterpriseId,
        final boolean withTimeout)
    {
        // Check that the enterprise can use the datacenter
        DatacenterLimits limits =
            entService.findLimitsByEnterpriseAndDatacenter(enterpriseId, dcId);
        if (limits == null)
        {
            addConflictErrors(APIError.ENTERPRISE_NOT_ALLOWED_DATACENTER);
            flushErrors();
        }

        final String amUri =
            infService.getRemoteService(dcId, RemoteServiceType.APPLIANCE_MANAGER).getUri();

        try
        {
            return clientPool.borrowObject(amUri, withTimeout);
        }
        catch (Exception e)
        {
            LOGGER.error(APIError.AM_CLIENT.getMessage(), e);

            addUnexpectedErrors(APIError.AM_CLIENT);
            flushErrors();
            return null;
        }
    }

    /**
     * Do not check the UserSession as it is called by AMEventProcessor
     */
    private AMClient getAMClientBySystem(final Integer dcId, final Integer enterpriseId,
        final boolean withTimeout)
    {
        final String amUri =
            infService.getRemoteService(dcId, RemoteServiceType.APPLIANCE_MANAGER).getUri();

        try
        {
            return clientPool.borrowObject(amUri, withTimeout);
        }
        catch (Exception e)
        {
            LOGGER.error(APIError.AM_CLIENT.getMessage(), e);

            addUnexpectedErrors(APIError.AM_CLIENT);
            flushErrors();
            return null;
        }
    }

    private void returnClientToPool(final AMClient client)
    {
        try
        {
            clientPool.returnObject(client);
        }
        catch (Exception e)
        {
            LOGGER.trace("Unable to return AMClient instance to pool.", e);
        }
    }

    private void reportError(final Exception e)
    {
        if (e instanceof AMClientException)
        {
            LOGGER.error(APIError.AM_FAILED_REQUEST.getMessage(), e.getMessage());
            addServiceUnavailableErrors(new CommonError(APIError.AM_FAILED_REQUEST.getCode(),
                APIError.AM_FAILED_REQUEST.getMessage() + '\n' + e.getMessage()));
        }
        else if (isATimeout(e))
        {
            LOGGER.warn(APIError.AM_TIMEOUT.getMessage(), e.getMessage());
            addServiceUnavailableErrors(APIError.AM_TIMEOUT);
        }
        else
        {
            LOGGER.error(APIError.AM_UNAVAILABE.getMessage(), e.getMessage());
            addServiceUnavailableErrors(APIError.AM_UNAVAILABE);
        }

        flushErrors();
    }

    private boolean isATimeout(final Exception e)
    {
        return e instanceof ClientRuntimeException
            && e.getCause() instanceof SocketTimeoutException
            || //
            e instanceof ClientRuntimeException && e.getCause().getCause() != null
            && e.getCause().getCause() instanceof SocketTimeoutException;
    }

}
