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

package com.abiquo.api.resources.cloud;

import static com.abiquo.api.common.Assert.assertErrors;
import static com.abiquo.api.common.UriTestResolver.resolveVirtualApplianceDeployURI;
import static com.abiquo.api.common.UriTestResolver.resolveVirtualApplianceStateURI;
import static com.abiquo.api.common.UriTestResolver.resolveVirtualApplianceURI;
import static com.abiquo.api.common.UriTestResolver.resolveVirtualApplianceUndeployURI;
import static com.abiquo.api.common.UriTestResolver.resolveVirtualMachineDeployURI;
import static com.abiquo.api.common.UriTestResolver.resolveVirtualMachineResetURI;
import static com.abiquo.api.common.UriTestResolver.resolveVirtualMachineStateURI;
import static com.abiquo.api.common.UriTestResolver.resolveVirtualMachineURI;
import static com.abiquo.api.common.UriTestResolver.resolveVirtualMachineUndeployURI;
import static org.testng.Assert.assertEquals;
import junit.framework.Assert;

import org.apache.wink.client.ClientResponse;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.abiquo.api.exceptions.APIError;
import com.abiquo.api.resources.AbstractJpaGeneratorIT;
import com.abiquo.model.enumerator.HypervisorType;
import com.abiquo.model.transport.AcceptedRequestDto;
import com.abiquo.server.core.cloud.VirtualAppliance;
import com.abiquo.server.core.cloud.VirtualApplianceDto;
import com.abiquo.server.core.cloud.VirtualApplianceState;
import com.abiquo.server.core.cloud.VirtualApplianceStateDto;
import com.abiquo.server.core.cloud.VirtualDatacenter;
import com.abiquo.server.core.cloud.VirtualMachine;
import com.abiquo.server.core.cloud.VirtualMachineDto;
import com.abiquo.server.core.cloud.VirtualMachineState;
import com.abiquo.server.core.cloud.VirtualMachineStateDto;
import com.abiquo.server.core.cloud.VirtualMachineTaskDto;
import com.abiquo.server.core.common.EnvironmentGenerator;

/**
 * Integration tests to verify virtual machine locking logic.
 * 
 * @author Ignasi Barrera
 */
public class VirtualMachineLockIT extends AbstractJpaGeneratorIT
{
    private EnvironmentGenerator env;

    private VirtualDatacenter vdc;

    private VirtualAppliance vapp;

    private VirtualMachine vm;

    @BeforeMethod
    @Override
    public void setup()
    {
        super.setup();

        env = new EnvironmentGenerator(seed);
        env.generateEnterprise();
        env.generateInfrastructure();
        env.generateVirtualDatacenter();
        env.generateAllocatedVirtualMachine();

        setup(env.getEnvironment().toArray());

        vdc = env.get(VirtualDatacenter.class);
        vapp = env.get(VirtualAppliance.class);
        vm = env.get(VirtualMachine.class);
    }

    @Test
    public void testDeleteVirtualMachineReturns409IfLocked()
    {
        vm.setState(VirtualMachineState.LOCKED);
        update(vm);

        String uri = resolveVirtualMachineURI(vdc.getId(), vapp.getId(), vm.getId());

        ClientResponse response =
            delete(uri, EnvironmentGenerator.SYSADMIN, EnvironmentGenerator.SYSADMIN);

        assertErrors(response, 409, APIError.VIRTUAL_MACHINE_INVALID_STATE_DELETE);
    }

    @Test
    public void testDeleteVirtualMachineIfNotAllocated()
    {
        vm.setState(VirtualMachineState.NOT_ALLOCATED);
        update(vm);

        String uri = resolveVirtualMachineURI(vdc.getId(), vapp.getId(), vm.getId());

        ClientResponse response =
            delete(uri, EnvironmentGenerator.SYSADMIN, EnvironmentGenerator.SYSADMIN);

        assertEquals(response.getStatusCode(), 204);
        assertGetVmResponseIs(404);
    }

    @Test
    public void testDeleteVirtualMachineIfDeployed()
    {
        vm.setState(VirtualMachineState.ON);
        update(vm);

        String uri = resolveVirtualMachineURI(vdc.getId(), vapp.getId(), vm.getId());

        ClientResponse response =
            delete(uri, EnvironmentGenerator.SYSADMIN, EnvironmentGenerator.SYSADMIN);

        assertEquals(response.getStatusCode(), 204);
        assertVmState(VirtualMachineState.LOCKED);
    }

    @Test
    public void testDeployVirtualMachineReturns409IfInvalidState()
    {
        vm.setState(VirtualMachineState.OFF);
        update(vm);

        String uri = resolveVirtualMachineDeployURI(vdc.getId(), vapp.getId(), vm.getId());

        ClientResponse response =
            post(uri, null, EnvironmentGenerator.SYSADMIN, EnvironmentGenerator.SYSADMIN,
                AcceptedRequestDto.MEDIA_TYPE, null);

        assertErrors(response, 409, APIError.VIRTUAL_MACHINE_INVALID_STATE_DEPLOY);
    }

    @Test
    public void testDeployVirtualMachine()
    {
        vm.setState(VirtualMachineState.NOT_ALLOCATED);
        update(vm);

        String uri = resolveVirtualMachineDeployURI(vdc.getId(), vapp.getId(), vm.getId());

        ClientResponse response =
            post(uri, null, EnvironmentGenerator.SYSADMIN, EnvironmentGenerator.SYSADMIN,
                AcceptedRequestDto.MEDIA_TYPE, null);

        assertEquals(response.getStatusCode(), 202);
        assertVmState(VirtualMachineState.LOCKED);
    }

    @Test
    public void testDeployVirtualMachineWithForce()
    {
        vm.setState(VirtualMachineState.NOT_ALLOCATED);
        update(vm);

        String uri = resolveVirtualMachineDeployURI(vdc.getId(), vapp.getId(), vm.getId());

        VirtualMachineTaskDto dto = new VirtualMachineTaskDto();
        dto.setForceEnterpriseSoftLimits(true);

        ClientResponse response =
            post(uri, dto, EnvironmentGenerator.SYSADMIN, EnvironmentGenerator.SYSADMIN,
                AcceptedRequestDto.MEDIA_TYPE, VirtualMachineTaskDto.MEDIA_TYPE);

        assertEquals(response.getStatusCode(), 202);
        assertVmState(VirtualMachineState.LOCKED);
    }

    @Test
    public void testUndeployVirtualMachine()
    {
        vm.setState(VirtualMachineState.ON);
        update(vm);

        String uri = resolveVirtualMachineUndeployURI(vdc.getId(), vapp.getId(), vm.getId());

        VirtualMachineTaskDto dto = new VirtualMachineTaskDto();
        dto.setForceUndeploy(false);

        ClientResponse response =
            post(uri, dto, EnvironmentGenerator.SYSADMIN, EnvironmentGenerator.SYSADMIN,
                AcceptedRequestDto.MEDIA_TYPE, VirtualMachineTaskDto.MEDIA_TYPE);

        assertEquals(response.getStatusCode(), 202);
        assertVmState(VirtualMachineState.LOCKED);
    }

    @Test
    public void testUndeployVirtualMachineReturns202IfNotInHypervisor()
    {
        vm.setState(VirtualMachineState.NOT_ALLOCATED);
        update(vm);

        String uri = resolveVirtualMachineUndeployURI(vdc.getId(), vapp.getId(), vm.getId());

        VirtualMachineTaskDto dto = new VirtualMachineTaskDto();
        dto.setForceUndeploy(false);

        ClientResponse response =
            post(uri, dto, EnvironmentGenerator.SYSADMIN, EnvironmentGenerator.SYSADMIN,
                AcceptedRequestDto.MEDIA_TYPE, VirtualMachineTaskDto.MEDIA_TYPE);

        Assert.assertEquals(response.getStatusCode(), 202);
    }

    @Test
    public void testPowerStateVirtualMachine()
    {
        vm.setState(VirtualMachineState.ON);
        update(vm);

        String uri = resolveVirtualMachineStateURI(vdc.getId(), vapp.getId(), vm.getId());

        VirtualMachineStateDto dto = new VirtualMachineStateDto();
        dto.setState(VirtualMachineState.OFF);

        ClientResponse response =
            put(uri, dto, EnvironmentGenerator.SYSADMIN, EnvironmentGenerator.SYSADMIN,
                AcceptedRequestDto.MEDIA_TYPE, VirtualMachineStateDto.MEDIA_TYPE);

        assertEquals(response.getStatusCode(), 202);
        assertVmState(VirtualMachineState.LOCKED);
    }

    @Test
    public void testPowerStateVirtualMachineReturns409IfInvalidTransition()
    {
        vm.setState(VirtualMachineState.ON);
        update(vm);

        String uri = resolveVirtualMachineStateURI(vdc.getId(), vapp.getId(), vm.getId());

        VirtualMachineStateDto dto = new VirtualMachineStateDto();
        dto.setState(VirtualMachineState.ON);

        ClientResponse response =
            put(uri, dto, EnvironmentGenerator.SYSADMIN, EnvironmentGenerator.SYSADMIN,
                AcceptedRequestDto.MEDIA_TYPE, VirtualMachineStateDto.MEDIA_TYPE);

        assertErrors(response, 409, APIError.VIRTUAL_MACHINE_STATE_CHANGE_ERROR);
    }

    @Test
    public void testPowerStateVirtualMachineReturns409IfPauseAndXen()
    {
        vm.setState(VirtualMachineState.ON);
        vm.getHypervisor().setType(HypervisorType.XEN_3);
        update(vm.getHypervisor(), vm);

        String uri = resolveVirtualMachineStateURI(vdc.getId(), vapp.getId(), vm.getId());

        VirtualMachineStateDto dto = new VirtualMachineStateDto();
        dto.setState(VirtualMachineState.PAUSED);

        ClientResponse response =
            put(uri, dto, EnvironmentGenerator.SYSADMIN, EnvironmentGenerator.SYSADMIN,
                AcceptedRequestDto.MEDIA_TYPE, VirtualMachineStateDto.MEDIA_TYPE);

        assertErrors(response, 409, APIError.VIRTUAL_MACHINE_PAUSE_UNSUPPORTED);
    }

    @Test
    public void testPowerStateVirtualMachineReturns409IfNotAllocated()
    {
        vm.setState(VirtualMachineState.NOT_ALLOCATED);
        update(vm);

        String uri = resolveVirtualMachineStateURI(vdc.getId(), vapp.getId(), vm.getId());

        VirtualMachineStateDto dto = new VirtualMachineStateDto();
        dto.setState(VirtualMachineState.PAUSED);

        ClientResponse response =
            put(uri, dto, EnvironmentGenerator.SYSADMIN, EnvironmentGenerator.SYSADMIN,
                AcceptedRequestDto.MEDIA_TYPE, VirtualMachineStateDto.MEDIA_TYPE);

        assertErrors(response, 409, APIError.VIRTUAL_MACHINE_UNALLOCATED_STATE);
    }

    @Test
    public void testPowerStateVirtualMachinePauseAndNotXen()
    {
        vm.setState(VirtualMachineState.ON);
        vm.getHypervisor().setType(HypervisorType.VMX_04);
        update(vm.getHypervisor(), vm);

        String uri = resolveVirtualMachineStateURI(vdc.getId(), vapp.getId(), vm.getId());

        VirtualMachineStateDto dto = new VirtualMachineStateDto();
        dto.setState(VirtualMachineState.PAUSED);

        ClientResponse response =
            put(uri, dto, EnvironmentGenerator.SYSADMIN, EnvironmentGenerator.SYSADMIN,
                AcceptedRequestDto.MEDIA_TYPE, VirtualMachineStateDto.MEDIA_TYPE);

        assertEquals(response.getStatusCode(), 202);
        assertVmState(VirtualMachineState.LOCKED);
    }

    @Test
    public void testResetVirtualMachine()
    {
        vm.setState(VirtualMachineState.ON);
        update(vm);

        String uri = resolveVirtualMachineResetURI(vdc.getId(), vapp.getId(), vm.getId());

        ClientResponse response =
            post(uri, null, EnvironmentGenerator.SYSADMIN, EnvironmentGenerator.SYSADMIN);

        assertEquals(response.getStatusCode(), 202);
        assertVmState(VirtualMachineState.LOCKED);
    }

    @Test
    public void testResetVirtualMachineReturns409IfInvalidState()
    {
        vm.setState(VirtualMachineState.OFF);
        update(vm);

        String uri = resolveVirtualMachineResetURI(vdc.getId(), vapp.getId(), vm.getId());

        ClientResponse response =
            post(uri, null, EnvironmentGenerator.SYSADMIN, EnvironmentGenerator.SYSADMIN);

        assertErrors(response, 409, APIError.VIRTUAL_MACHINE_INVALID_STATE_RESET);
    }

    @Test
    public void testResetVirtualMachineReturns409IfNotAllocated()
    {
        vm.setState(VirtualMachineState.NOT_ALLOCATED);
        update(vm);

        String uri = resolveVirtualMachineResetURI(vdc.getId(), vapp.getId(), vm.getId());

        ClientResponse response =
            post(uri, null, EnvironmentGenerator.SYSADMIN, EnvironmentGenerator.SYSADMIN);

        assertErrors(response, 409, APIError.VIRTUAL_MACHINE_UNALLOCATED_STATE);
    }

    @Test
    public void testDeployVirtualAppliance()
    {
        vm.setState(VirtualMachineState.NOT_ALLOCATED);
        update(vm);

        String uri = resolveVirtualApplianceDeployURI(vdc.getId(), vapp.getId());

        VirtualMachineTaskDto dto = new VirtualMachineTaskDto();
        dto.setForceEnterpriseSoftLimits(false);

        ClientResponse response =
            post(uri, dto, EnvironmentGenerator.SYSADMIN, EnvironmentGenerator.SYSADMIN,
                AcceptedRequestDto.MEDIA_TYPE, VirtualMachineTaskDto.MEDIA_TYPE);

        assertEquals(response.getStatusCode(), 202);
        assertVappState(VirtualApplianceState.LOCKED);
    }

    @Test
    public void testDeployVirtualApplianceReturns409IfInvalidState()
    {
        vm.setState(VirtualMachineState.ON);
        update(vm);

        String uri = resolveVirtualApplianceDeployURI(vdc.getId(), vapp.getId());

        VirtualMachineTaskDto dto = new VirtualMachineTaskDto();
        dto.setForceEnterpriseSoftLimits(false);

        ClientResponse response =
            post(uri, dto, EnvironmentGenerator.SYSADMIN, EnvironmentGenerator.SYSADMIN,
                AcceptedRequestDto.MEDIA_TYPE, VirtualMachineTaskDto.MEDIA_TYPE);

        assertErrors(response, 409, APIError.VIRTUAL_MACHINE_INVALID_STATE_DEPLOY);
    }

    @Test
    public void testUndeployVirtualAppliance()
    {
        vm.setState(VirtualMachineState.ON);
        update(vm);

        String uri = resolveVirtualApplianceUndeployURI(vdc.getId(), vapp.getId());

        VirtualMachineTaskDto dto = new VirtualMachineTaskDto();
        dto.setForceEnterpriseSoftLimits(false);

        ClientResponse response =
            post(uri, dto, EnvironmentGenerator.SYSADMIN, EnvironmentGenerator.SYSADMIN,
                AcceptedRequestDto.MEDIA_TYPE, VirtualMachineTaskDto.MEDIA_TYPE);

        assertEquals(response.getStatusCode(), 202);
        assertVappState(VirtualApplianceState.LOCKED);
    }

    @Test
    public void testUndeployVirtualApplianceReturns202IfNotInHypervisor()
    {
        vm.setState(VirtualMachineState.NOT_ALLOCATED);
        update(vm);

        String uri = resolveVirtualApplianceUndeployURI(vdc.getId(), vapp.getId());

        VirtualMachineTaskDto dto = new VirtualMachineTaskDto();
        dto.setForceEnterpriseSoftLimits(false);

        ClientResponse response =
            post(uri, dto, EnvironmentGenerator.SYSADMIN, EnvironmentGenerator.SYSADMIN,
                AcceptedRequestDto.MEDIA_TYPE, VirtualMachineTaskDto.MEDIA_TYPE);

        Assert.assertEquals(response.getStatusCode(), 202);
    }

    @Test
    public void testDeleteVirtualApplianceIfNotDeployed()
    {
        vm.setState(VirtualMachineState.NOT_ALLOCATED);
        update(vm);

        String uri = resolveVirtualApplianceURI(vdc.getId(), vapp.getId());

        ClientResponse response =
            delete(uri, EnvironmentGenerator.SYSADMIN, EnvironmentGenerator.SYSADMIN);

        assertEquals(response.getStatusCode(), 204);
        assertGetVappResponseIs(404);
    }

    @Test
    public void testDeleteVirtualApplianceReturns409IfDeployed()
    {
        vm.setState(VirtualMachineState.ON);
        update(vm);

        String uri = resolveVirtualApplianceURI(vdc.getId(), vapp.getId());

        ClientResponse response =
            delete(uri, EnvironmentGenerator.SYSADMIN, EnvironmentGenerator.SYSADMIN);

        assertErrors(response, 409, APIError.VIRTUALAPPLIANCE_INVALID_STATE_DELETE);
    }

    @Test
    public void testDeleteVirtualApplianceReturns409IfLocked()
    {
        vm.setState(VirtualMachineState.LOCKED);
        update(vm);

        String uri = resolveVirtualApplianceURI(vdc.getId(), vapp.getId());

        ClientResponse response =
            delete(uri, EnvironmentGenerator.SYSADMIN, EnvironmentGenerator.SYSADMIN);

        assertErrors(response, 409, APIError.VIRTUALAPPLIANCE_INVALID_STATE_DELETE);
    }

    private void assertVmState(final VirtualMachineState state)
    {
        String uri = resolveVirtualMachineStateURI(vdc.getId(), vapp.getId(), vm.getId());
        ClientResponse response =
            get(uri, EnvironmentGenerator.SYSADMIN, EnvironmentGenerator.SYSADMIN,
                VirtualMachineStateDto.MEDIA_TYPE);
        assertEquals(response.getStatusCode(), 200);

        VirtualMachineStateDto dto = response.getEntity(VirtualMachineStateDto.class);
        assertEquals(dto.getState(), state);
    }

    private void assertVappState(final VirtualApplianceState state)
    {
        String uri = resolveVirtualApplianceStateURI(vdc.getId(), vapp.getId());
        ClientResponse response =
            get(uri, EnvironmentGenerator.SYSADMIN, EnvironmentGenerator.SYSADMIN,
                VirtualApplianceStateDto.MEDIA_TYPE);
        assertEquals(response.getStatusCode(), 200);

        VirtualApplianceStateDto dto = response.getEntity(VirtualApplianceStateDto.class);
        assertEquals(dto.getPower(), state);
    }

    private void assertGetVmResponseIs(final int responseCode)
    {
        String uri = resolveVirtualMachineURI(vdc.getId(), vapp.getId(), vm.getId());
        ClientResponse response =
            get(uri, EnvironmentGenerator.SYSADMIN, EnvironmentGenerator.SYSADMIN,
                VirtualMachineDto.MEDIA_TYPE);
        assertEquals(response.getStatusCode(), responseCode);
    }

    private void assertGetVappResponseIs(final int responseCode)
    {
        String uri = resolveVirtualApplianceURI(vdc.getId(), vapp.getId());
        ClientResponse response =
            get(uri, EnvironmentGenerator.SYSADMIN, EnvironmentGenerator.SYSADMIN,
                VirtualApplianceDto.MEDIA_TYPE);
        assertEquals(response.getStatusCode(), responseCode);
    }

}
