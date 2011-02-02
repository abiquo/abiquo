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

import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.abiquo.server.core.cloud.Hypervisor;
import com.abiquo.server.core.cloud.VirtualAppliance;
import com.abiquo.server.core.cloud.VirtualMachine;
import com.abiquo.server.core.cloud.VirtualMachineRep;
import com.abiquo.server.core.enterprise.Enterprise;
import com.abiquo.server.core.enterprise.User;

@Repository
@Transactional(readOnly = true)
public class VirtualMachineService
{
    @Autowired
    VirtualMachineRep repo;

    public VirtualMachineService()
    {

    }

    public VirtualMachineService(EntityManager em)
    {
        this.repo = new VirtualMachineRep(em);
    }

    public Collection<VirtualMachine> findByHypervisor(Hypervisor hypervisor)
    {
        assert hypervisor != null;
        return repo.findByHypervisor(hypervisor);
    }

    public Collection<VirtualMachine> findByEnterprise(Enterprise enterprise)
    {
        assert enterprise != null;
        return repo.findByEnterprise(enterprise);
    }

    public Collection<VirtualMachine> findVirtualMachinesByUser(Enterprise enterprise, User user)
    {
        return repo.findVirtualMachinesByUser(enterprise, user);
    }

    public List<VirtualMachine> findByVirtualAppliance(VirtualAppliance vapp)
    {
        return repo.findVirtualMachinesByVirtualAppliance(vapp.getId());
    }

    public VirtualMachine getVirtualMachine(Integer vmId)
    {
        return repo.findVirtualMachineById(vmId);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void updateVirtualMachine(VirtualMachine vm)
    {
        repo.update(vm);
    }

    /**
     * Check if a virtual machine is defined into a virtual appliance.
     * 
     * @param vmId identifier of the virtual machine
     * @param vappId identifier of the virtual appliance
     * @return True if it is, false otherwise.
     */
    public boolean isAssignedTo(Integer vmId, Integer vappId)
    {
        List<VirtualMachine> vms = repo.findVirtualMachinesByVirtualAppliance(vappId);
        for (VirtualMachine vm : vms)
        {
            if (vm.getId().equals(vmId))
            {
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteNotManagedVirtualMachines(Hypervisor hypervisor)
    {
        repo.deleteNotManagedVirtualMachines(hypervisor);
    }

}
