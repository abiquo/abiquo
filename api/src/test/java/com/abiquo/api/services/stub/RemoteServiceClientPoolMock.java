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
