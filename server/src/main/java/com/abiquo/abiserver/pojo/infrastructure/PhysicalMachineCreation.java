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

package com.abiquo.abiserver.pojo.infrastructure;

import java.util.ArrayList;

import com.abiquo.abiserver.business.hibernate.pojohb.infrastructure.PhysicalmachineHB;
import com.abiquo.abiserver.infrastructure.Resource;
import com.abiquo.model.enumerator.HypervisorType;
import com.abiquo.server.core.infrastructure.DatastoresDto;
import com.abiquo.server.core.infrastructure.MachineDto;

/**
 * Auxiliary class This class is used to create a new Physical Machine, along with its Hypervisors
 * And to retrieve the result of a physical machine creation
 * 
 * @author Oliver
 */
public class PhysicalMachineCreation
{
    private PhysicalMachine physicalMachine;

    private ArrayList<HyperVisor> hypervisors;

    private ArrayList<Resource> resources;

    public PhysicalMachine getPhysicalMachine()
    {
        return physicalMachine;
    }

    public void setPhysicalMachine(final PhysicalMachine physicalMachine)
    {
        this.physicalMachine = physicalMachine;
    }

    public ArrayList<HyperVisor> getHypervisors()
    {
        return hypervisors;
    }

    public void setHypervisors(final ArrayList<HyperVisor> hypervisors)
    {
        this.hypervisors = hypervisors;
    }

    public void setResources(final ArrayList<Resource> resources)
    {
        this.resources = resources;
    }

    public ArrayList<Resource> getResources()
    {
        return resources;
    }

    public MachineDto toMachineDto()
    {
        MachineDto dto = new MachineDto();

        PhysicalMachine pm = this.getPhysicalMachine();
        HyperVisor h = null;

        if (this.getHypervisors() != null && !this.getHypervisors().isEmpty())
        {
            h = this.getHypervisors().get(0);
        }

        dto.setId(pm.getId());
        dto.setDescription(pm.getDescription());
        dto.setIpmiIp(pm.getIpmiIp());
        dto.setIpmiPassword(pm.getIpmiPassword());
        dto.setIpmiPort(pm.getIpmiPort());
        dto.setIpmiUser(pm.getIpmiUser());
        dto.setName(pm.getName());
        dto.setRealCpuCores(pm.getRealCpu());
        dto.setRealHardDiskInMb(pm.getRealStorage());
        dto.setRealRamInMb(pm.getRealRam());
        dto.setState(PhysicalmachineHB.transportIntegerToState(pm.getIdState()));

        if (h != null)
        {
            dto.setIp(h.getIp());
            dto.setIpService(h.getIpService());
            dto.setPassword(h.getPassword());
            dto.setPort(h.getPort());
            dto.setType(HypervisorType.fromValue(h.getType().getName()));
            dto.setUser(h.getUser());
        }

        dto.setVirtualCpuCores(pm.getCpu());
        dto.setVirtualCpusPerCore(pm.getCpuRatio());
        dto.setVirtualCpusUsed(pm.getCpuUsed());

        dto.setVirtualHardDiskInMb(pm.getHd());
        dto.setVirtualHardDiskUsedInMb(pm.getHdUsed());

        dto.setVirtualRamInMb(pm.getRam());
        dto.setVirtualRamUsedInMb(pm.getRamUsed());

        dto.setVirtualSwitch(pm.getVswitchName());

        if (pm.getDatastores() != null && !pm.getDatastores().isEmpty())
        {
            DatastoresDto dss = new DatastoresDto();
            for (Datastore d : pm.getDatastores())
            {
                dss.add(d.toDto());
            }
            dto.setDatastores(dss);
        }

        return dto;
    }
}
