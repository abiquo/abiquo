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

package com.abiquo.api.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.abiquo.server.core.cloud.State;
import com.abiquo.server.core.cloud.VirtualMachine;

/**
 * also change the the virtual machine state to RUNNING and mark on the vmachine description the
 * target machine.
 * 
 * @author apuig
 */
@Service
@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
public class VirtualMachineAllocatorTestService extends VirtualMachineAllocatorService
{

    @Override
    public VirtualMachine allocateVirtualMachine(final Integer virtualMachineId,
        final Integer idVirtualApp, final Boolean foreceEnterpriseSoftLimits)
    {
        VirtualMachine vmachine =
            super
                .allocateVirtualMachine(virtualMachineId, idVirtualApp, foreceEnterpriseSoftLimits);

        vmachine.setDescription(vmachine.getHypervisor().getMachine().getName());

        vmachineDao.flush();

        vmachineDao.updateVirtualMachineState(virtualMachineId, State.ON);

        return vmachineDao.findById(virtualMachineId);
    }

}
