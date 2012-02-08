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

import java.util.UUID;

import javax.persistence.EntityManager;

import org.springframework.stereotype.Service;

import com.abiquo.commons.amqp.impl.tarantino.domain.builder.VirtualMachineDescriptionBuilder;
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

    public TarantinoServiceMock(EntityManager em)
    {
        super(em);
        mock = mockTarantinoService(TarantinoService.class);
    }

    public static <T extends TarantinoService> T mockTarantinoService(Class<T> mockClass)
    {
        T mock = mock(mockClass);

        when(mock.applyVirtualMachineState(anyVM(), anyDesc(), anyTransition())).thenReturn(
            randomTaskId());
        when(mock.deployVirtualMachine(anyVM(), anyDesc())).thenReturn(randomTaskId());
        when(mock.deployVirtualMachineHA(anyVM(), anyDesc(), anyBoolean(), null)).thenReturn(
            randomTaskId());
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

    private static String randomTaskId()
    {
        return UUID.randomUUID().toString();
    }

    // Delegate methods

    public String deployVirtualMachine(VirtualMachine virtualMachine,
        VirtualMachineDescriptionBuilder virtualMachineDesciptionBuilder)
    {
        return mock.deployVirtualMachine(virtualMachine, virtualMachineDesciptionBuilder);
    }

    public String reconfigureVirtualMachine(VirtualMachine vm,
        VirtualMachineDescriptionBuilder originalConfig, VirtualMachineDescriptionBuilder newConfig)
    {
        return mock.reconfigureVirtualMachine(vm, originalConfig, newConfig);
    }

    public String deployVirtualMachineHA(VirtualMachine virtualMachine,
        VirtualMachineDescriptionBuilder virtualMachineDesciptionBuilder, boolean originalVMStateON)
    {
        return mock.deployVirtualMachineHA(virtualMachine, virtualMachineDesciptionBuilder,
            originalVMStateON, null);
    }

    public String undeployVirtualMachine(VirtualMachine virtualMachine,
        VirtualMachineDescriptionBuilder virtualMachineDesciptionBuilder,
        VirtualMachineState currentState)
    {
        return mock.undeployVirtualMachine(virtualMachine, virtualMachineDesciptionBuilder,
            currentState);
    }

    public String undeployVirtualMachineAndDelete(VirtualMachine virtualMachine,
        VirtualMachineDescriptionBuilder virtualMachineDesciptionBuilder,
        VirtualMachineState currentState)
    {
        return mock.undeployVirtualMachineAndDelete(virtualMachine,
            virtualMachineDesciptionBuilder, currentState);
    }

    public String applyVirtualMachineState(VirtualMachine virtualMachine,
        VirtualMachineDescriptionBuilder virtualMachineDesciptionBuilder,
        VirtualMachineStateTransition machineStateTransition)
    {
        return mock.applyVirtualMachineState(virtualMachine, virtualMachineDesciptionBuilder,
            machineStateTransition);
    }

    public String snapshotVirtualMachine(VirtualAppliance virtualAppliance,
        VirtualMachine virtualMachine, VirtualMachineState originalState, String snapshotName)
    {
        return mock.snapshotVirtualMachine(virtualAppliance, virtualMachine, originalState,
            snapshotName);
    }

    public String snapshotVirtualMachine(VirtualAppliance virtualAppliance,
        VirtualMachine virtualMachine, VirtualMachineState originalState, String snapshotName,
        String snapshotPath, String snapshotFilename)
    {
        return mock.snapshotVirtualMachine(virtualAppliance, virtualMachine, originalState,
            snapshotName, snapshotPath, snapshotFilename);
    }

    public String instanceStatefulVirtualMachine(VirtualAppliance virtualAppliance,
        VirtualMachine virtualMachine, VirtualMachineState originalState, String snapshotName)
    {
        return mock.instanceStatefulVirtualMachine(virtualAppliance, virtualMachine, originalState,
            snapshotName);
    }

}
