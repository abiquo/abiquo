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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.anyString;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.abiquo.commons.amqp.impl.tarantino.domain.DiskSnapshot;
import com.abiquo.commons.amqp.impl.tarantino.domain.VirtualMachineDefinition;
import com.abiquo.commons.amqp.impl.tarantino.domain.builder.VirtualMachineDescriptionBuilder;
import com.abiquo.server.core.cloud.VirtualMachine;
import com.abiquo.server.core.cloud.VirtualMachineState;
import com.abiquo.server.core.cloud.VirtualMachineStateTransition;
import com.abiquo.server.core.cloud.VirtualAppliance;

/**
 * Mock class to simulate {@link TarantinoService} behavior.
 * 
 * @author Ignasi Barrera
 */
@Service
public class TarantinoServiceMock extends TarantinoService
{
    /** The target mock object. */
    private TarantinoService mock = createMock();

    /**
     * Mocks the {@link TarantinoService} to return random task ids when invoking the methods that
     * would perform a call to Tarantino.
     */
    private static TarantinoService createMock()
    {
        TarantinoService mock = mock(TarantinoService.class);

        when(mock.applyVirtualMachineState(anyVM(), anyDesc(), anyTransition())).thenReturn(
            randomTaskId());
        when(mock.deployVirtualMachine(anyVM(), anyDesc())).thenReturn(randomTaskId());
        when(mock.deployVirtualMachineHA(anyVM(), anyDesc(), anyBoolean())).thenReturn(
            randomTaskId());
        when(mock.reconfigureVirtualMachine(anyVM(), anyDesc(), anyDesc())).thenReturn(
            randomTaskId());
        when(mock.snapshotVirtualMachine(anyVirtualAppliance(), anyVM(), anyState(), 
            anyString())).thenReturn(randomTaskId());
        when(mock.snapshotVirtualMachine(anyVirtualAppliance(), anyVM(), anyState(),
            anyString(), anyString(), anyString())).thenReturn(randomTaskId());
        when(mock.undeployVirtualMachine(anyVM(), anyDesc(), anyState()))
            .thenReturn(randomTaskId());

        return mock;
    }

    // Overridden methods delegate to the mock object

    @Override
    public String reconfigureVirtualMachine(final VirtualMachine vm,
        final VirtualMachineDescriptionBuilder originalConfig,
        final VirtualMachineDescriptionBuilder newConfig)
    {
        return mock.reconfigureVirtualMachine(vm, originalConfig, newConfig);
    }

    @Override
    public String deployVirtualMachine(final VirtualMachine virtualMachine,
        final VirtualMachineDescriptionBuilder virtualMachineDesciptionBuilder)
    {
        return mock.deployVirtualMachine(virtualMachine, virtualMachineDesciptionBuilder);
    }

    @Override
    public String deployVirtualMachineHA(final VirtualMachine virtualMachine,
        final VirtualMachineDescriptionBuilder virtualMachineDesciptionBuilder,
        final boolean originalVMStateON)
    {
        return mock.deployVirtualMachineHA(virtualMachine, virtualMachineDesciptionBuilder,
            originalVMStateON);
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

    public String snapshotVirtualMachine(final VirtualAppliance virtualAppliance,
        final VirtualMachine virtualMachine, final VirtualMachineState originalState,
        final String snapshotName, final String snapshotPath, final String snapshotFilename)
    {
        return mock.snapshotVirtualMachine(virtualAppliance, virtualMachine, originalState, 
            snapshotName, snapshotPath, snapshotFilename);
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

    private static VirtualMachineDefinition anyDef()
    {
        return (VirtualMachineDefinition) any();
    }

    private static DiskSnapshot anyDiskSnapshot()
    {
        return (DiskSnapshot) any();
    }

    private static VirtualAppliance anyVirtualAppliance()
    {
        return (VirtualAppliance) any();
    }

    private static String randomTaskId()
    {
        return UUID.randomUUID().toString();
    }
}
