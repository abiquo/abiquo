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
import com.abiquo.model.transport.error.CommonError;
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
    UserService userService;

    @Autowired
    IAllocator allocator;

    /**
     * Only perform checks if the resources are increased. Checks the resource limits, check the
     * target machine can hold the new resource requirements and update the target machine usage.
     */
    public void checkAllocate(final Integer idVirtualApp, final Integer virtualMachineId,
        final VirtualMachineDto newVmRequirements, final boolean foreceEnterpriseSoftLimits)
    {

        VirtualMachine vmachine = vmachineDao.findById(virtualMachineId);
        userService.checkCurrentEnterpriseForPostMethods(vmachine.getEnterprise());

        if (vmachine.getHypervisor() == null || vmachine.getHypervisor().getMachine() == null)
        {
            addConflictErrors(APIError.CHECK_EDIT_NO_TARGET_MACHINE);
            flushErrors();
        }

        try
        {
            allocator.checkEditVirtualMachineResources(idVirtualApp, virtualMachineId,
                newVmRequirements, foreceEnterpriseSoftLimits);
        }
        catch (NotEnoughResourcesException e)
        {
            addConflictErrors(createErrorWithExceptionDetails(APIError.NOT_ENOUGH_RESOURCES,
                virtualMachineId, e));
        }
        catch (LimitExceededException limite)
        {
            addConflictErrors(new CommonError(APIError.LIMIT_EXCEEDED.name(), limite.toString()));
        }
        catch (ResourceAllocationException e)
        {
            addConflictErrors(createErrorWithExceptionDetails(APIError.NOT_ENOUGH_RESOURCES,
                virtualMachineId, e));
        }
        catch (AllocatorException e)
        {
            addConflictErrors(createErrorWithExceptionDetails(APIError.ALLOCATOR_ERROR,
                virtualMachineId, e));
        }
        catch (Exception e)
        {
            addUnexpectedErrors(createErrorWithExceptionDetails(APIError.ALLOCATOR_ERROR,
                virtualMachineId, e));
        }
        finally
        {
            flushErrors();
        }

        final int cpuIncrease = newVmRequirements.getCpu() - vmachine.getCpu();
        final int ramIncrease = newVmRequirements.getRam() - vmachine.getRam();

        upgradeUse.updateUsed(vmachine.getHypervisor().getMachine(), cpuIncrease, ramIncrease);

    }

    public VirtualMachine allocateVirtualMachine(final Integer virtualMachineId,
        final Integer idVirtualApp, final Boolean foreceEnterpriseSoftLimits)
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
            addConflictErrors(createErrorWithExceptionDetails(APIError.NOT_ENOUGH_RESOURCES,
                virtualMachineId, e));
        }
        catch (LimitExceededException limite)
        {
            addConflictErrors(new CommonError(APIError.LIMIT_EXCEEDED.name(), limite.toString()));
        }
        catch (ResourceAllocationException e)
        {
            addConflictErrors(createErrorWithExceptionDetails(APIError.NOT_ENOUGH_RESOURCES,
                virtualMachineId, e));
        }
        catch (AllocatorException e)
        {
            addConflictErrors(createErrorWithExceptionDetails(APIError.ALLOCATOR_ERROR,
                virtualMachineId, e));
        }
        catch (Exception e)
        {
            addUnexpectedErrors(createErrorWithExceptionDetails(APIError.ALLOCATOR_ERROR,
                virtualMachineId, e));            
        }
        finally
        {
            flushErrors();
        }

        return vmachine;
    }


    private CommonError createErrorWithExceptionDetails(APIError apiError,
        Integer virtualMachineId, Exception e)
    {
        final String msg =
            String.format("%s (%s)\n%s", apiError.getMessage(),
                virtualMachineInfo(virtualMachineId), e.getMessage());

        return new CommonError(apiError.getCode(), msg);
    }

    
    public void updateVirtualMachineUse(final Integer idVirtualApp, final VirtualMachine vMachine)
    {
        // UPGRADE PHYSICAL MACHINE USE
        try
        {
            upgradeUse.updateUse(idVirtualApp, vMachine);
        }
        catch (ResourceUpgradeUseException e)
        {
            APIError error = APIError.NOT_ENOUGH_RESOURCES;
            error.addCause(String.format("%s\n%s", virtualMachineInfo(vMachine.getId()),
                e.getMessage()));
            addConflictErrors(error);
        }
        finally
        {
            flushErrors();
        }
    }

    public void deallocateVirtualMachine(final Integer idVirtualMachine)
    {
        VirtualMachine vmachine = vmachineDao.findById(idVirtualMachine);
        userService.checkCurrentEnterpriseForPostMethods(vmachine.getEnterprise());

        try
        {
            upgradeUse.rollbackUse(vmachine);
        }
        catch (ResourceUpgradeUseException e)
        {
            APIError error = APIError.NOT_ENOUGH_RESOURCES;
            error.addCause(String.format("%s\n%s", virtualMachineInfo(idVirtualMachine),
                e.getMessage()));
            addConflictErrors(error);
        }
        finally
        {
            flushErrors();
        }
    }

    private String virtualMachineInfo(final Integer vmid)
    {
        VirtualMachine vm = vmachineDao.findById(vmid);

        return String.format("Virtual Machine id:%d name:%s UUID:%s.", vm.getId(), vm.getName(),
            vm.getUuid());
    }

}
