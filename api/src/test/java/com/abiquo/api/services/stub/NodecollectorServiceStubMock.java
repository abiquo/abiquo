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

/**
 * Abiquo premium edition
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

/**
 * 
 */
package com.abiquo.api.services.stub;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.springframework.stereotype.Service;

import com.abiquo.api.exceptions.APIError;
import com.abiquo.model.enumerator.HypervisorType;
import com.abiquo.nodecollector.client.NodeCollectorRESTClient;
import com.abiquo.nodecollector.exception.BadRequestException;
import com.abiquo.nodecollector.exception.CollectorException;
import com.abiquo.nodecollector.exception.ConnectionException;
import com.abiquo.nodecollector.exception.LoginException;
import com.abiquo.nodecollector.exception.ServiceUnavailableException;
import com.abiquo.nodecollector.exception.UnprovisionedException;
import com.abiquo.server.core.infrastructure.RemoteService;
import com.abiquo.server.core.infrastructure.nodecollector.HostDto;
import com.abiquo.server.core.infrastructure.nodecollector.HostStatusEnumType;
import com.abiquo.server.core.infrastructure.nodecollector.HostsDto;
import com.abiquo.server.core.infrastructure.nodecollector.ResourceEnumType;
import com.abiquo.server.core.infrastructure.nodecollector.ResourceType;

/**
 * Mocks the behaviour of the nodecollector REST client.
 * 
 * @author jdevesa@abiquo.com
 */
@Service
public class NodecollectorServiceStubMock extends NodecollectorServiceStub
{
    public static final String IP_CORRECT_1 = "87.23.151.2";

    public static final String IP_LOGIN_EXCEPTION = "87.23.151.3";

    public static final String IP_UNPROVISIONED_EXCEPTION = "87.23.151.4";

    public static final String IP_CORRECT_2 = "87.23.151.5";

    public static final String IP_CONNECTION_EXCEPTION = "87.23.151.6";

    public static final String IP_CORRECT_3 = "87.23.151.7";

    public static final String IP_BAD_REQUEST_EXCEPTION = "87.23.151.8";

    public static final String IP_COLLECTOR_EXCEPTION = "87.23.151.9";

    public static final String IP_SERVICE_UNAVAILABLE_EXCEPTION = "87.23.151.10";

    public static final String IP_DISCOVER_FIRST = "192.168.4.2";

    public static final String IP_DISCOVER_LAST = "192.168.4.3";

    public NodecollectorServiceStubMock()
    {
        // Do nothing
    }

    @Override
    public NodeCollectorRESTClient initializeRESTClient(final RemoteService nodecollector)
    {
        NodeCollectorRESTClient mockClient = mock(NodeCollectorRESTClient.class);

        getHostInfoBehaviour(mockClient);
        getHypervisorTypeInfoBehaviour(mockClient);

        return mockClient;
    }

    private void getHypervisorTypeInfoBehaviour(final NodeCollectorRESTClient mockClient)
    {
        try
        {
            when(mockClient.getRemoteHypervisorType("10.1.1.1")).thenReturn(HypervisorType.VMX_04);
            when(mockClient.getRemoteHypervisorType(anyString())).thenReturn(HypervisorType.VMX_04);
        }
        catch (Exception e)
        {
            addUnexpectedErrors(APIError.INTERNAL_SERVER_ERROR);
            flushErrors();
        }
    }

    private void getHostInfoBehaviour(final NodeCollectorRESTClient mockClient)
    {
        try
        {

            HostDto host1 = completeHostDto();

            HostDto host2 = completeHostDto();
            host2.setName("HostMockito2");

            HostDto host3 = completeHostDto();
            host2.setName("HostMockito3");

            // correct behaviour.
            when(
                mockClient.getRemoteHostInfo(eq(IP_CORRECT_1), (HypervisorType) anyObject(),
                    anyString(), anyString(), anyInt())).thenReturn(host1);

            doThrow(new LoginException("Login exception")).when(mockClient).getRemoteHostInfo(
                eq(IP_LOGIN_EXCEPTION), (HypervisorType) anyObject(), anyString(), anyString(),
                anyInt());

            doThrow(new UnprovisionedException("Unprovisioned exception")).when(mockClient)
                .getRemoteHostInfo(eq(IP_UNPROVISIONED_EXCEPTION), (HypervisorType) anyObject(),
                    anyString(), anyString(), anyInt());

            when(
                mockClient.getRemoteHostInfo(eq(IP_CORRECT_2), (HypervisorType) anyObject(),
                    anyString(), anyString(), anyInt())).thenReturn(host2);

            doThrow(new ConnectionException("Connection exception")).when(mockClient)
                .getRemoteHostInfo(eq(IP_CONNECTION_EXCEPTION), (HypervisorType) anyObject(),
                    anyString(), anyString(), anyInt());

            doThrow(new BadRequestException("BadRequest exception")).when(mockClient)
                .getRemoteHostInfo(eq(IP_BAD_REQUEST_EXCEPTION), (HypervisorType) anyObject(),
                    anyString(), anyString(), anyInt());

            doThrow(new CollectorException("Collector exception")).when(mockClient)
                .getRemoteHostInfo(eq(IP_COLLECTOR_EXCEPTION), (HypervisorType) anyObject(),
                    anyString(), anyString(), anyInt());

            when(
                mockClient.getRemoteHostInfo(eq(IP_CORRECT_3), (HypervisorType) anyObject(),
                    anyString(), anyString(), anyInt())).thenReturn(host3);

            doThrow(new ServiceUnavailableException("Service Unavailable")).when(mockClient)
                .getRemoteHostInfo(eq(IP_SERVICE_UNAVAILABLE_EXCEPTION),
                    (HypervisorType) anyObject(), anyString(), anyString(), anyInt());

            // discover machines
            HostDto hostDiscover1 = completeHostDiscover1();
            HostDto hostDiscover2 = completeHostDiscover2();

            when(
                mockClient.getRemoteHostInfo(eq(IP_DISCOVER_FIRST), (HypervisorType) anyObject(),
                    anyString(), anyString(), anyInt())).thenReturn(hostDiscover1);
            when(
                mockClient.getRemoteHostInfo(eq(IP_DISCOVER_LAST), (HypervisorType) anyObject(),
                    anyString(), anyString(), anyInt())).thenReturn(hostDiscover2);

        }
        catch (Exception e)
        {
            addUnexpectedErrors(APIError.INTERNAL_SERVER_ERROR);
            flushErrors();
        }
    }

    private HostDto completeHostDiscover1()
    {

        HostDto host = new HostDto();
        host.setName("kvm_name");
        host.setStatus(HostStatusEnumType.MANAGED);
        host.setCpu(0l);
        host.setRam(0l);

        ResourceType resourceEth0 = new ResourceType();
        resourceEth0.setResourceType(ResourceEnumType.NETWORK_INTERFACE);
        // This addres matches
        resourceEth0.setAddress("52:50:00:23:12:13");
        resourceEth0.setElementName("vSwitch0");
        host.getResources().add(resourceEth0);

        return host;
    }

    private HostDto completeHostDiscover2()
    {
        HostDto host = new HostDto();
        host.setName("kvm_name_2");
        host.setStatus(HostStatusEnumType.MANAGED);
        host.setCpu(0l);
        host.setRam(0l);

        ResourceType resourceEth0 = new ResourceType();
        resourceEth0.setResourceType(ResourceEnumType.NETWORK_INTERFACE);
        // This addres matches
        resourceEth0.setAddress("52:50:00:23:12:19");
        resourceEth0.setElementName("vSwitch0");
        host.getResources().add(resourceEth0);

        return host;
    }

    /**
     * Return a list of hosts for the call to racks.
     * 
     * @return
     */
    private HostsDto listOfMockHosts()
    {
        HostsDto hosts = new HostsDto();

        for (int i = 0; i < 5; i++)
        {
            HostDto host = new HostDto();
            host.setName("/sys/chassis-1/blade-" + i);
            if (i % 2 == 0)
            {
                host.setStatus(HostStatusEnumType.PROVISIONED);
            }
            else
            {
                host.setStatus(HostStatusEnumType.STOPPED);
            }
            host.setCpu(0l);
            host.setRam(0l);
            hosts.getHost().add(host);

            ResourceType resourceEth0 = new ResourceType();
            resourceEth0.setResourceType(ResourceEnumType.NETWORK_INTERFACE);
            resourceEth0.setAddress("52:50:00:23:12:1" + i);
            resourceEth0.setElementName("vSwitch0");
            host.getResources().add(resourceEth0);
        }

        return hosts;
    }

    /**
     * HostDto object that return a correct HostDto object.
     * 
     * @return
     */
    private HostDto completeHostDto()
    {
        HostDto hostDto = new HostDto();
        hostDto.setName("MockitoHost");
        hostDto.setHypervisor(HypervisorType.KVM.name());
        hostDto.setRam(536870912);
        hostDto.setCpu(4l);
        hostDto.setStatus(HostStatusEnumType.MANAGED);
        hostDto.setInitiatorIQN("iqn.1998-01.com.vmware:esx02-42b0f47e");

        ResourceType resourceEth0 = new ResourceType();
        resourceEth0.setResourceType(ResourceEnumType.NETWORK_INTERFACE);
        resourceEth0.setAddress("52:50:00:23:12:12");
        resourceEth0.setElementName("vSwitch0");

        ResourceType resourceEth1 = new ResourceType();
        resourceEth1.setResourceType(ResourceEnumType.NETWORK_INTERFACE);
        resourceEth1.setAddress("52:50:00:23:12:14");
        resourceEth1.setElementName("vSwitch1");

        ResourceType resourceDatastore0 = new ResourceType();
        resourceDatastore0.setResourceType(ResourceEnumType.HARD_DISK);
        resourceDatastore0.setAddress("Datastore1");
        resourceDatastore0.setElementName("Datastore1");
        resourceDatastore0.setUnits(30000000l);
        resourceDatastore0.setAvailableUnits(21340002l);

        ResourceType resourceDatastore1 = new ResourceType();
        resourceDatastore1.setResourceType(ResourceEnumType.HARD_DISK);
        resourceDatastore1.setAddress("nfs-devel");
        resourceDatastore1.setElementName("nfs-devel");
        resourceDatastore1.setUnits(30000000l);
        resourceDatastore1.setAvailableUnits(21340002l);

        hostDto.getResources().add(resourceEth0);
        hostDto.getResources().add(resourceEth1);
        hostDto.getResources().add(resourceDatastore0);
        hostDto.getResources().add(resourceDatastore1);

        return hostDto;
    }
}
