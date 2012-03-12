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

package com.abiquo.scheduler;

import org.springframework.stereotype.Component;

import com.abiquo.server.core.cloud.NodeVirtualImage;
import com.abiquo.server.core.cloud.VirtualAppliance;
import com.abiquo.server.core.cloud.VirtualMachine;
import com.abiquo.server.core.infrastructure.storage.DiskManagement;
import com.abiquo.server.core.scheduler.VirtualMachineRequirements;

@Component
public class VirtualMachineRequirementsFactory
{

    public VirtualMachineRequirements createVirtualMachineRequirements(final VirtualMachine vmachine)
    {
        long cpu = vmachine.getCpu();
        long ram = vmachine.getRam();
        // template + hard disks
        long hd = vmachine.getHdInBytes() + getDisksSizeInBytes(vmachine);
        long repository = vmachine.getVirtualMachineTemplate().getDiskFileSize(); // XXX or
                                                                                  // conversion
        // TODO get publicIps number
        // TODO get storage size
        // TODO get volume size
        return new VirtualMachineRequirements(cpu, ram, hd, repository, 0l, 0l, 0l);
    }

    public VirtualMachineRequirements createVirtualMachineRequirements(final VirtualAppliance vapp)
    {
        VirtualMachineRequirements allRequired =
            new VirtualMachineRequirements(0l, 0l, 0l, 0l, 0l, 0l, 0l);

        for (NodeVirtualImage nvi : vapp.getNodes())
        {
            VirtualMachineRequirements reqvm =
                createVirtualMachineRequirements(nvi.getVirtualMachine());

            allRequired.addRequirement(reqvm);
        }

        return allRequired;
    }

    public VirtualMachineRequirements createVirtualMachineRequirements(
        final VirtualMachine vmachine, final VirtualMachine newVmRequirements)
    {
        long cpu = (long) newVmRequirements.getCpu() - vmachine.getCpu();
        long ram = (long) newVmRequirements.getRam() - vmachine.getRam();
        // TODO hd and repository
        long hd = 0l + getDiskSizeInBytes(vmachine, newVmRequirements);
        long repository = 0l;
        // TODO get publicIps number
        // TODO get storage size
        // TODO get volume size

        return new VirtualMachineRequirements(cpu, ram, hd, repository, 0l, 0l, 0l);
    }

    private long getDisksSizeInBytes(final VirtualMachine vm)
    {
        long size = 0;
        if (vm.getDisks() != null && !vm.getDisks().isEmpty())
        {
            for (DiskManagement disk : vm.getDisks())
            {
                // bytes
                size += disk.getSizeInMb() * 1024 * 1024;
            }
        }
        return size;
    }

    private long getDiskSizeInBytes(final VirtualMachine vmachine,
        final VirtualMachine newVmRequirements)
    {
        long totalDisksSize = getDisksSizeInBytes(vmachine);
        long newTotalDisksSize = getDisksSizeInBytes(newVmRequirements);
        return newTotalDisksSize - totalDisksSize;
    }
}
