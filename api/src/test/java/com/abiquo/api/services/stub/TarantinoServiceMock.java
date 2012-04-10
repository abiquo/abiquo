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

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Map;
import java.util.UUID;

import javax.persistence.EntityManager;

import org.springframework.stereotype.Service;

import com.abiquo.commons.amqp.impl.tarantino.domain.builder.VirtualMachineDescriptionBuilder;
import com.abiquo.server.core.cloud.Hypervisor;
import com.abiquo.server.core.cloud.VirtualAppliance;
import com.abiquo.server.core.cloud.VirtualMachine;
import com.abiquo.server.core.cloud.VirtualMachineState;
import com.abiquo.server.core.cloud.VirtualMachineStateTransition;

/**
 * Mock class to simulate {@link TarantinoService} behavior.
 * 
 * @author Ignasi Barrera
 */
@Service
public class TarantinoServiceMock extends TarantinoService
{
    protected TarantinoService mock;

    public TarantinoServiceMock()
    {
        super();
        mock = mockTarantinoService(TarantinoService.class);
    }

    public TarantinoServiceMock(final EntityManager em)
    {
        super(em);
        mock = mockTarantinoService(TarantinoService.class);
    }

    public static <T extends TarantinoService> T mockTarantinoService(final Class<T> mockClass)
    {
        T mock = mock(mockClass);

        when(mock.applyVirtualMachineState(anyVM(), anyDesc(), anyTransition())).thenReturn(
            randomTaskId());
        when(mock.deployVirtualMachine(anyVM(), anyDesc())).thenReturn(randomTaskId());
        when(mock.deployVirtualMachineHA(anyVM(), anyDesc(), anyBoolean(), anyExtraData()))
            .thenReturn(randomTaskId());
        when(mock.reconfigureVirtualMachine(anyVM(), anyDesc(), anyDesc())).thenReturn(
            randomTaskId());
        when(mock.snapshotVirtualMachine(anyVirtualAppliance(), anyVM(), anyState(), anyString()))
            .thenReturn(randomTaskId());
        when(
            mock.snapshotVirtualMachine(anyVirtualAppliance(), anyVM(), anyState(), anyString(),
                anyString(), anyString())).thenReturn(randomTaskId());
        when(
            mock.instanceStatefulVirtualMachine(anyVirtualAppliance(), anyVM(), anyState(),
                anyString())).thenReturn(randomTaskId());
        when(mock.undeployVirtualMachine(anyVM(), anyDesc(), anyState()))
            .thenReturn(randomTaskId());
        when(mock.undeployVirtualMachineHA(anyVM(), anyDesc(), anyState(), anyHypervisor()))
            .thenReturn(randomTaskId());
        when(mock.refreshVirtualMachineResources(anyVM(), anyVirtualAppliance())).thenReturn(
            randomTaskId());

        return mock;
    }

    // Helper methods

    private static VirtualMachine anyVM()
    {
        return (VirtualMachine) any();
    }

    private static VirtualMachineStateTransition anyTransition()
    {
        return (VirtualMachineStateTransition) any();
    }

    private static VirtualMachineState anyState()
    {
        return (VirtualMachineState) any();
    }

    private static VirtualMachineDescriptionBuilder anyDesc()
    {
        return (VirtualMachineDescriptionBuilder) any();
    }

    private static VirtualAppliance anyVirtualAppliance()
    {
        return (VirtualAppliance) any();
    }

    private static Hypervisor anyHypervisor()
    {
        return (Hypervisor) any();
    }

    @SuppressWarnings("unchecked")
    private static Map<String, String> anyExtraData()
    {
        return (Map<String, String>) any();
    }

    private static String randomTaskId()
    {
        return UUID.randomUUID().toString();
    }

    // Delegate methods

    @Override
    public String deployVirtualMachine(final VirtualMachine virtualMachine,
        final VirtualMachineDescriptionBuilder virtualMachineDesciptionBuilder)
    {
        return mock.deployVirtualMachine(virtualMachine, virtualMachineDesciptionBuilder);
    }

    @Override
    public String reconfigureVirtualMachine(final VirtualMachine vm,
        final VirtualMachineDescriptionBuilder originalConfig,
        final VirtualMachineDescriptionBuilder newConfig)
    {
        return mock.reconfigureVirtualMachine(vm, originalConfig, newConfig);
    }

    @Override
    public String undeployVirtualMachine(final VirtualMachine virtualMachine,
        final VirtualMachineDescriptionBuilder virtualMachineDesciptionBuilder,
        final VirtualMachineState currentState)
    {
        return mock.undeployVirtualMachine(virtualMachine, virtualMachineDesciptionBuilder,
            currentState);
    }

    @Override
    public String undeployVirtualMachineHA(final VirtualMachine virtualMachine,
        final VirtualMachineDescriptionBuilder virtualMachineDesciptionBuilder,
        final VirtualMachineState currentState, final Hypervisor originalHypervisor)
    {
        return mock.undeployVirtualMachineHA(virtualMachine, virtualMachineDesciptionBuilder,
            currentState, originalHypervisor);
    }

    @Override
    public String undeployVirtualMachineAndDelete(final VirtualMachine virtualMachine,
        final VirtualMachineDescriptionBuilder virtualMachineDesciptionBuilder,
        final VirtualMachineState currentState)
    {
        return mock.undeployVirtualMachineAndDelete(virtualMachine,
            virtualMachineDesciptionBuilder, currentState);
    }

    @Override
    public String applyVirtualMachineState(final VirtualMachine virtualMachine,
        final VirtualMachineDescriptionBuilder virtualMachineDesciptionBuilder,
        final VirtualMachineStateTransition machineStateTransition)
    {
        return mock.applyVirtualMachineState(virtualMachine, virtualMachineDesciptionBuilder,
            machineStateTransition);
    }

    @Override
    public String snapshotVirtualMachine(final VirtualAppliance virtualAppliance,
        final VirtualMachine virtualMachine, final VirtualMachineState originalState,
        final String snapshotName)
    {
        return mock.snapshotVirtualMachine(virtualAppliance, virtualMachine, originalState,
            snapshotName);
    }

    @Override
    public String snapshotVirtualMachine(final VirtualAppliance virtualAppliance,
        final VirtualMachine virtualMachine, final VirtualMachineState originalState,
        final String snapshotName, final String snapshotPath, final String snapshotFilename)
    {
        return mock.snapshotVirtualMachine(virtualAppliance, virtualMachine, originalState,
            snapshotName, snapshotPath, snapshotFilename);
    }

    @Override
    public String instanceStatefulVirtualMachine(final VirtualAppliance virtualAppliance,
        final VirtualMachine virtualMachine, final VirtualMachineState originalState,
        final String snapshotName)
    {
        return mock.instanceStatefulVirtualMachine(virtualAppliance, virtualMachine, originalState,
            snapshotName);
    }

    @Override
    public String refreshVirtualMachineResources(final VirtualMachine virtualMachine,
        final VirtualAppliance virtualAppliance)
    {
        return mock.refreshVirtualMachineResources(virtualMachine, virtualAppliance);
    }

}
