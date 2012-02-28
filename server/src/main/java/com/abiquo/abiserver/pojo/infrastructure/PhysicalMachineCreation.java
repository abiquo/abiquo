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
import java.util.HashSet;

import com.abiquo.abiserver.business.hibernate.pojohb.infrastructure.PhysicalmachineHB;
import com.abiquo.abiserver.infrastructure.Resource;
import com.abiquo.model.enumerator.HypervisorType;
import com.abiquo.server.core.infrastructure.DatastoreDto;
import com.abiquo.server.core.infrastructure.DatastoresDto;
import com.abiquo.server.core.infrastructure.MachineDto;
import com.abiquo.server.core.infrastructure.nodecollector.ResourceEnumType;

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
        else if (pm.getHypervisor() != null)
        {
            h = pm.getHypervisor();
        }

        dto.setId(pm.getId());
        dto.setDescription(pm.getDescription());
        dto.setIpmiIP(pm.getIpmiIp());
        dto.setIpmiPassword(pm.getIpmiPassword());
        dto.setIpmiPort(pm.getIpmiPort());
        dto.setIpmiUser(pm.getIpmiUser());
        dto.setName(pm.getName());
        dto.setState(PhysicalmachineHB.transportIntegerToState(pm.getIdState()));
        dto.setInitiatorIQN(pm.getInitiatorIQN());

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
        dto.setVirtualCpusUsed(pm.getCpuUsed());

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

    public static PhysicalMachineCreation create(final MachineDto dto)
    {
        PhysicalMachineCreation machineCreation = new PhysicalMachineCreation();
        PhysicalMachine machine = new PhysicalMachine();

        machine.setId(dto.getId());
        machine.setDescription(dto.getDescription());
        machine.setIpmiIp(dto.getIpmiIP());
        machine.setIpmiPassword(dto.getIpmiPassword());
        machine.setIpmiPort(dto.getIpmiPort());
        machine.setIpmiUser(dto.getIpmiUser());
        machine.setName(dto.getName());
        machine.setVswitchName(dto.getVirtualSwitch());
        machine.setIdState(PhysicalmachineHB.transportStateToInteger(dto.getState()));
        machine.setCpu(dto.getVirtualCpuCores());
        machine.setCpuUsed(dto.getVirtualCpusUsed());
        machine.setRam(dto.getVirtualRamInMb());
        machine.setRamUsed(dto.getVirtualRamUsedInMb());
        machine.setInitiatorIQN(dto.getInitiatorIQN());

        HyperVisor hypervisor = new HyperVisor();
        hypervisor.setIp(dto.getIp());
        hypervisor.setIpService(dto.getIpService());
        hypervisor.setPassword(dto.getPassword());
        hypervisor.setPort(dto.getPort() == null ? 0 : dto.getPort());
        hypervisor.setType(new HyperVisorType(dto.getType()));
        hypervisor.setUser(dto.getUser());

        if (dto.getDatastores() != null && !dto.getDatastores().isEmpty())
        {
            machine.setDatastores(new HashSet<Datastore>());
            for (DatastoreDto d : dto.getDatastores().getCollection())
            {
                machine.getDatastores().add(Datastore.fromDto(d));
            }
        }

        if (dto.getVirtualSwitch() != null)
        {
            machineCreation.setResources(new ArrayList<Resource>());
            String[] vss = dto.getVirtualSwitch().split("/");
            for (String s : vss)
            {
                Resource resource = new Resource();
                resource.setResourcetype(ResourceEnumType.VSWITCH.value());
                resource.setElementName(s);
                machineCreation.getResources().add(resource);
            }
            dto.setVirtualSwitch(null);
        }

        machineCreation.setPhysicalMachine(machine);
        machineCreation.setHypervisors(new ArrayList<HyperVisor>());
        machineCreation.getHypervisors().add(hypervisor);

        return machineCreation;
    }
}
