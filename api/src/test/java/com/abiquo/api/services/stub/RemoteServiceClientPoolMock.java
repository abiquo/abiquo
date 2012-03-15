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

import static org.mockito.Mockito.mock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.abiquo.api.exceptions.APIError;
import com.abiquo.api.pools.RemoteServiceClientPool;
import com.abiquo.server.core.infrastructure.RemoteService;
import com.abiquo.vsm.client.VSMClient;

/**
 * Mock class for the {@link RemoteServiceClientPool}
 * 
 * @author Ignasi Barrera
 */
@Service
public class RemoteServiceClientPoolMock extends RemoteServiceClientPool
{
    /** The logger. */
    private final static Logger LOGGER = LoggerFactory.getLogger(RemoteServiceClientPoolMock.class);

    @Override
    public Object getClientFor(final RemoteService remoteService)
    {
        Object client = null;
        switch (remoteService.getType())
        {
            case VIRTUAL_SYSTEM_MONITOR:
                client = mock(VSMClient.class);
                break;
            default:
                LOGGER.error(APIError.REMOTE_SERVICE_NON_POOLABLE.getMessage());
                addUnexpectedErrors(APIError.REMOTE_SERVICE_NON_POOLABLE);
                flushErrors();
                break;
        }
        return client;
    }

    @Override
    public void releaseClientFor(final RemoteService remoteService, final Object client)
    {
        // Do nothing
    }

}
