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

package com.abiquo.api.services.cloud;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.abiquo.server.core.cloud.VirtualMachine;
import com.abiquo.server.core.cloud.VirtualMachineRep;
import com.abiquo.server.core.cloud.VirtualMachineState;

/**
 * {@link VirtualMachine} lock and unlock functionality.
 * <p>
 * The operation this class executes must be atomic, to ensure that the lock will be effective at
 * the moment it is requested. To achieve this every operation must be executed in an isolated
 * transaction.
 * 
 * @author Ignasi Barrera
 */

// This class is visible only at package level. It should only be accessed from the
// VirtualMachineService#lockVirtualMachine method

@Service
class VirtualMachineLock
{
    @Autowired
    private VirtualMachineRep repo;

    VirtualMachineLock()
    {

    }

    VirtualMachineLock(final EntityManager em)
    {
        this.repo = new VirtualMachineRep(em);
    }

    /**
     * Lock the given virtual machine.
     * <p>
     * This method gets the ID of the virtual machine as a parameter instead of the object itself to
     * force refreshing the VM from the database to avoid session issues due to objects loaded in
     * different transactions.
     * 
     * @param vmId The ID of the virtual machine to lock.
     */
    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    void lock(final Integer vmId)
    {
        VirtualMachine vm = repo.findVirtualMachineById(vmId);
        vm.setState(VirtualMachineState.LOCKED);
        repo.update(vm);
    }
}
