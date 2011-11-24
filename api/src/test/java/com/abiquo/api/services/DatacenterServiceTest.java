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
 * 
 */
package com.abiquo.api.services;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.fail;

import javax.persistence.EntityManager;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.abiquo.api.exceptions.APIError;
import com.abiquo.api.exceptions.BadRequestException;
import com.abiquo.api.exceptions.ConflictException;
import com.abiquo.api.exceptions.InternalServerErrorException;
import com.abiquo.api.exceptions.ServiceUnavailableException;
import com.abiquo.api.resources.AbstractJpaGeneratorIT;
import com.abiquo.api.resources.DatacenterResourceIT;
import com.abiquo.api.services.stub.NodecollectorServiceStubMock;
import com.abiquo.model.enumerator.HypervisorType;
import com.abiquo.model.enumerator.RemoteServiceType;
import com.abiquo.model.transport.error.CommonError;
import com.abiquo.nodecollector.client.NodeCollectorRESTClient;
import com.abiquo.nodecollector.exception.CollectorException;
import com.abiquo.nodecollector.exception.ConnectionException;
import com.abiquo.nodecollector.exception.LoginException;
import com.abiquo.nodecollector.exception.UnprovisionedException;
import com.abiquo.server.core.infrastructure.Datacenter;
import com.abiquo.server.core.infrastructure.Machine;
import com.abiquo.server.core.infrastructure.RemoteService;
import com.abiquo.server.core.util.network.IPAddress;

/**
 * Test the functionality you can execute from {@link DatacenterResourceIT} but from the
 * service layer.
 * 
 * @author jdevesa@abiquo.com
 */
public class DatacenterServiceTest extends AbstractJpaGeneratorIT
{
    private InfrastructureService service;

    private Datacenter datacenter;

    private EntityManager em;

    @Override
    @BeforeMethod
    public void setup()
    {
        datacenter = datacenterGenerator.createUniqueInstance();
        RemoteService rs =
            remoteServiceGenerator.createInstance(RemoteServiceType.NODE_COLLECTOR, datacenter);
        setup(datacenter, rs);

        em = getEntityManagerWithAnActiveTransaction();
        service = new InfrastructureService(em, new NodecollectorServiceStubMock());
    }

    /**
     * Test the discover machine functionality for a correct behaviour. As you see, here we use the
     * {@link NodecollectorServiceStubMock} mock class. Because we don't test the
     * nodecollector behaviour, but the service behaviour in front of a correct response.
     * 
     * @throws Exception
     */
    @Test
    public void discoverMachineFunctionality() throws Exception
    {
        Machine machine =
            service.discoverRemoteHypervisor(datacenter.getId(),
                IPAddress.newIPAddress(NodecollectorServiceStubMock.IP_CORRECT_1),
                HypervisorType.VMX_04, "user", "password", 8889);

        assertNotNull(machine);
    }

    /**
     * The {@link NodeCollectorRESTClient} mock we have throws a Nodecollector's
     * {@link LoginException}. Test we can handle it and return an API exception
     * 
     * @throws Exception
     */
    @Test
    public void discoverMachineFunctionalityCanHandleLoginException() throws Exception
    {
        try
        {
            service.discoverRemoteHypervisor(datacenter.getId(),
                IPAddress.newIPAddress(NodecollectorServiceStubMock.IP_LOGIN_EXCEPTION),
                HypervisorType.VMX_04, "user", "password", 8889);

            fail();
        }
        catch (ConflictException e)
        {
            CommonError ce = e.getErrors().iterator().next();
            assertEquals(ce.getCode(), APIError.NC_BAD_CREDENTIALS_TO_MACHINE.getCode());
            assertEquals(ce.getMessage(), APIError.NC_BAD_CREDENTIALS_TO_MACHINE.getMessage());
            return;
        }

        fail();
    }

    /**
     * The {@link NodeCollectorRESTClient} mock we have throws a Nodecollector's
     * {@link BadRequestException}. Test we can handle it and return an API exception
     * 
     * @throws Exception
     */
    @Test
    public void discoverMachineFunctionalityCanHandleBadRequestException() throws Exception
    {
        try
        {
            service.discoverRemoteHypervisor(datacenter.getId(), IPAddress
                .newIPAddress(NodecollectorServiceStubMock.IP_BAD_REQUEST_EXCEPTION),
                HypervisorType.VMX_04, "user", "password", 8889);

            fail();
        }
        catch (InternalServerErrorException e)
        {
            CommonError ce = e.getErrors().iterator().next();
            assertEquals(ce.getCode(), APIError.NC_UNEXPECTED_EXCEPTION.getCode());
            assertEquals(ce.getMessage(), APIError.NC_UNEXPECTED_EXCEPTION.getMessage());
            return;
        }

        fail();
    }

    /**
     * The {@link NodeCollectorRESTClient} mock we have throws a Nodecollector's
     * {@link CollectorException}. Test we can handle it and return an API exception
     * 
     * @throws Exception
     */
    @Test
    public void discoverMachineFunctionalityCanHandleCollectorException() throws Exception
    {
        try
        {
            service.discoverRemoteHypervisor(datacenter.getId(),
                IPAddress.newIPAddress(NodecollectorServiceStubMock.IP_COLLECTOR_EXCEPTION),
                HypervisorType.VMX_04, "user", "password", 8889);

            fail();
        }
        catch (InternalServerErrorException e)
        {
            CommonError ce = e.getErrors().iterator().next();
            assertEquals(ce.getCode(), APIError.NC_UNEXPECTED_EXCEPTION.getCode());
            assertEquals(ce.getMessage(), APIError.NC_UNEXPECTED_EXCEPTION.getMessage());
            return;
        }

        fail();
    }

    /**
     * The {@link NodeCollectorRESTClient} mock we have throws a Nodecollector's
     * {@link ServiceUnavailableException}. Test we can handle it and return an API exception
     * 
     * @throws Exception
     */
    @Test
    public void discoverMachineFunctionalityCanHandleServiceUnavailableException() throws Exception
    {
        try
        {
            service
                .discoverRemoteHypervisor(
                    datacenter.getId(),
                    IPAddress
                        .newIPAddress(NodecollectorServiceStubMock.IP_SERVICE_UNAVAILABLE_EXCEPTION),
                    HypervisorType.VMX_04, "user", "password", 8889);

            fail();
        }
        catch (ServiceUnavailableException e)
        {
            CommonError ce = e.getErrors().iterator().next();
            assertEquals(ce.getCode(), APIError.NC_UNAVAILABLE_EXCEPTION.getCode());
            assertEquals(ce.getMessage(), APIError.NC_UNAVAILABLE_EXCEPTION.getMessage());
            return;
        }

        fail();
    }

    /**
     * The {@link NodeCollectorRESTClient} mock we have throws a Nodecollector's
     * {@link UnprovisionedException}. Test we can handle it and return an API exception
     * 
     * @throws Exception
     */
    @Test
    public void discoverMachineFunctionalityCanHandleUnprovisionedException() throws Exception
    {
        try
        {
            service.discoverRemoteHypervisor(datacenter.getId(), IPAddress
                .newIPAddress(NodecollectorServiceStubMock.IP_UNPROVISIONED_EXCEPTION),
                HypervisorType.VMX_04, "user", "password", 8889);

            fail();
        }
        catch (ConflictException e)
        {
            CommonError ce = e.getErrors().iterator().next();
            assertEquals(ce.getCode(), APIError.NC_NOT_FOUND_EXCEPTION.getCode());
            assertEquals(ce.getMessage(), APIError.NC_NOT_FOUND_EXCEPTION.getMessage());
            return;
        }

        fail();
    }

    /**
     * The {@link NodeCollectorRESTClient} mock we have throws a Nodecollector's
     * {@link ConnectionException}. Test we can handle it and return an API exception
     * 
     * @throws Exception
     */
    @Test
    public void discoverMachineFunctionalityCanHandleConnectionException() throws Exception
    {
        try
        {
            service
                .discoverRemoteHypervisor(datacenter.getId(), IPAddress
                    .newIPAddress(NodecollectorServiceStubMock.IP_CONNECTION_EXCEPTION),
                    HypervisorType.VMX_04, "user", "password", 8889);

            fail();
        }
        catch (ConflictException e)
        {
            CommonError ce = e.getErrors().iterator().next();
            assertEquals(ce.getCode(), APIError.NC_CONNECTION_EXCEPTION.getCode());
            assertEquals(ce.getMessage(), APIError.NC_CONNECTION_EXCEPTION.getMessage());
            return;
        }

        fail();
    }

}
