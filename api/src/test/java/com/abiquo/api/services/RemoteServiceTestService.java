package com.abiquo.api.services;

import org.springframework.stereotype.Service;

import com.abiquo.server.core.infrastructure.RemoteService;

@Service
public class RemoteServiceTestService extends RemoteServiceService
{
    @Override
    public void checkRemoteServiceStatusBeforeRemoving(final RemoteService remoteService)
    {
        // During tests the target remote service may not be up and running
        // Do not return errors to simulate a normal behavior
    }

}
