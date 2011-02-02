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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.abiquo.api.exceptions.APIError;
import com.abiquo.api.services.DefaultApiService;
import com.abiquo.api.services.MachineService;
import com.abiquo.server.core.cloud.Hypervisor;
import com.abiquo.server.core.infrastructure.DatacenterRep;
import com.abiquo.server.core.infrastructure.Machine;

@Repository
@Transactional(readOnly = true)
public class HypervisorService extends DefaultApiService
{
    @Autowired
    DatacenterRep repo;

    @Autowired
    MachineService machineService;

    /*
    @Transactional(propagation = Propagation.REQUIRED)
    public Hypervisor addHypervisor(Integer machineId, HypervisorDto dto)
    {
        Machine machine = repo.findMachineById(machineId);

        if (repo.existAnyHypervisorWithIp(dto.getIp()))
        {
            errors.add(APIError.HYPERVISOR_EXIST_IP);
        }

        if (repo.existAnyHypervisorWithIpService(dto.getIpService()))
        {
            errors.add(APIError.HYPERVISOR_EXIST_SERVICE_IP);
        }

        Hypervisor hypervisor =
            machine.createHypervisor(dto.getType(), "", dto.getIp(), dto.getIpService(), dto
                .getPort(), dto.getUser(), dto.getPassword());

        if (!hypervisor.isValid())
        {
            validationErrors.addAll(hypervisor.getValidationErrors());
        }
        flushErrors();

        repo.insertHypervisor(hypervisor);
        return hypervisor;
    }

    public boolean isAssignedTo(Integer datacenterId, Integer rackId, Integer machineId)
    {
        return machineService.isAssignedTo(datacenterId, rackId, machineId);
    }*/
}
