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

import javax.jms.ResourceAllocationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.abiquo.api.exceptions.APIError;
import com.abiquo.scheduler.IAllocator;
import com.abiquo.scheduler.ResourceUpgradeUse;
import com.abiquo.scheduler.ResourceUpgradeUseException;
import com.abiquo.scheduler.limit.LimitExceededException;
import com.abiquo.scheduler.workload.AllocatorException;
import com.abiquo.scheduler.workload.NotEnoughResourcesException;
import com.abiquo.server.core.cloud.VirtualMachine;
import com.abiquo.server.core.cloud.VirtualMachineDAO;
import com.abiquo.server.core.cloud.VirtualMachineDto;

@Service
@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
public class VirtualMachineAllocatorService extends DefaultApiService
{
    @Autowired
    VirtualMachineDAO vmachineDao;

    @Autowired
    ResourceUpgradeUse upgradeUse;

    @Autowired
    IAllocator allocator;

    /**
     * Only perform checks if the resources are increased. Checks the resource limits, check the
     * target machine can hold the new resource requirements and update the target machine usage.
     */
    public void checkAllocate(Integer idVirtualApp, Integer virtualMachineId,
        VirtualMachineDto newVmRequirements, boolean foreceEnterpriseSoftLimits)
    {
        
        VirtualMachine vmachine = vmachineDao.findById(virtualMachineId);
        
        if(vmachine.getHypervisor() == null || vmachine.getHypervisor().getMachine() == null)
        {
            errors.add(APIError.CHECK_EDIT_NO_TARGET_MACHINE);
            flushErrors();
        }
        
        try
        {
            allocator.checkEditVirtualMachineResources(idVirtualApp, virtualMachineId,
                newVmRequirements, foreceEnterpriseSoftLimits);
        }
        catch (NotEnoughResourcesException e)
        {
            APIError error = APIError.NOT_ENOUGH_RESOURCES;
            errors.add(error.addCause(String.format("%s\n%s", virtualMachineInfo(virtualMachineId),
                e.getMessage())));
        }
        catch (LimitExceededException limite)
        {
            limitExceptions.add(limite);
        }
        catch (ResourceAllocationException e)
        {
            APIError error = APIError.NOT_ENOUGH_RESOURCES;
            errors.add(error.addCause(String.format("%s\n%s", virtualMachineInfo(virtualMachineId),
                e.getMessage())));
        }
        catch (AllocatorException e)
        {
            APIError error = APIError.ALLOCATOR_ERROR;
            errors.add(error.addCause(String.format("%s\n%s", virtualMachineInfo(virtualMachineId),
                e.getMessage())));
        }
        catch (Exception e)
        {
            e.printStackTrace(); // FIXME delete

            APIError error = APIError.ALLOCATOR_ERROR;
            errors.add(error.addCause(String.format("%s\n%s", virtualMachineInfo(virtualMachineId),
                e.getMessage())));
        }
        finally
        {
            flushErrors();
        }
        
        
        final int cpuIncrease = newVmRequirements.getCpu() - vmachine.getCpu();
        final int ramIncrease = newVmRequirements.getRam() - vmachine.getRam();
        

        upgradeUse.updateUsed(vmachine.getHypervisor().getMachine(), cpuIncrease, ramIncrease);

    }

    public VirtualMachine allocateVirtualMachine(Integer virtualMachineId, Integer idVirtualApp,
        Boolean foreceEnterpriseSoftLimits)
    {
        VirtualMachine vmachine = null; // flush errors

        try
        {
            vmachine =
                allocator.allocateVirtualMachine(idVirtualApp, virtualMachineId,
                    foreceEnterpriseSoftLimits);

        }
        catch (NotEnoughResourcesException e)
        {
            APIError error = APIError.NOT_ENOUGH_RESOURCES;
            errors.add(error.addCause(String.format("%s\n%s", virtualMachineInfo(virtualMachineId),
                e.getMessage())));
        }
        catch (LimitExceededException limite)
        {
            limitExceptions.add(limite);
        }
        catch (ResourceAllocationException e)
        {
            APIError error = APIError.NOT_ENOUGH_RESOURCES;
            errors.add(error.addCause(String.format("%s\n%s", virtualMachineInfo(virtualMachineId),
                e.getMessage())));
        }
        catch (AllocatorException e)
        {
            APIError error = APIError.ALLOCATOR_ERROR;
            errors.add(error.addCause(String.format("%s\n%s", virtualMachineInfo(virtualMachineId),
                e.getMessage())));
        }
        catch (Exception e)
        {
            e.printStackTrace(); // FIXME delete

            APIError error = APIError.ALLOCATOR_ERROR;
            errors.add(error.addCause(String.format("%s\n%s", virtualMachineInfo(virtualMachineId),
                e.getMessage())));
        }
        finally
        {
            flushErrors();
        }

        return vmachine;
    }

    public void updateVirtualMachineUse(Integer idVirtualApp, VirtualMachine vMachine)
    {
        // UPGRADE PHYSICAL MACHINE USE
        try
        {
            upgradeUse.updateUse(idVirtualApp, vMachine);
        }
        catch (ResourceUpgradeUseException e)
        {
            APIError error = APIError.ALLOCATOR_ERROR;
            errors.add(error.addCause(String.format("%s\n%s", virtualMachineInfo(vMachine.getId()),
                e.getMessage())));
        }
        finally
        {
            flushErrors();
        }
    }

    public void deallocateVirtualMachine(Integer idVirtualMachine)
    {
        VirtualMachine vmachine = vmachineDao.findById(idVirtualMachine);

        try
        {
            upgradeUse.rollbackUse(vmachine);
        }
        catch (ResourceUpgradeUseException e)
        {
            APIError error = APIError.ALLOCATOR_ERROR;
            errors.add(error.addCause(String.format("%s\n%s", virtualMachineInfo(idVirtualMachine),
                e.getMessage())));
        }
        finally
        {
            flushErrors();
        }
    }

    private String virtualMachineInfo(Integer vmid)
    {
        VirtualMachine vm = vmachineDao.findById(vmid);

        return String.format("Virtual Machine id:%d name:%s UUID:%s.", vm.getId(), vm.getName(),
            vm.getUuid());
    }

}
