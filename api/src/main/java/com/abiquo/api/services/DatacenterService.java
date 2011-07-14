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

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.abiquo.api.exceptions.APIError;
import com.abiquo.api.transformer.ModelTransformer;
import com.abiquo.model.enumerator.HypervisorType;
import com.abiquo.server.core.enterprise.Enterprise;
import com.abiquo.server.core.infrastructure.Datacenter;
import com.abiquo.server.core.infrastructure.DatacenterDto;
import com.abiquo.server.core.infrastructure.InfrastructureRep;
import com.abiquo.server.core.infrastructure.Machine;
import com.abiquo.server.core.infrastructure.Rack;
import com.abiquo.server.core.infrastructure.RemoteServiceDto;
import com.abiquo.server.core.infrastructure.RemoteServicesDto;
import com.abiquo.server.core.infrastructure.network.Network;
import com.abiquo.server.core.infrastructure.storage.Tier;

@Service
@Transactional(readOnly = true)
public class DatacenterService extends DefaultApiService
{
    @Autowired
    InfrastructureRep repo;

    @Autowired
    InfrastructureService infrastructureService;

    public DatacenterService()
    {

    }

    public DatacenterService(final EntityManager em)
    {
        repo = new InfrastructureRep(em);
        infrastructureService = new InfrastructureService(em);
    }

    public Collection<Datacenter> getDatacenters()
    {
        return repo.findAll();
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public DatacenterDto addDatacenter(final DatacenterDto dto) throws Exception
    {
        if (repo.existsAnyDatacenterWithName(dto.getName()))
        {
            addConflictErrors(APIError.DATACENTER_DUPLICATED_NAME);
            flushErrors();
        }

        // Create the public network before the datacenter
        // This network will store all the public VLANs of the datacenter.
        Network network = new Network(UUID.randomUUID().toString());
        repo.insertNetwork(network);

        Datacenter datacenter = new Datacenter(dto.getName(), dto.getLocation());
        isValidDatacenter(datacenter);
        datacenter.setNetwork(network);
        repo.insert(datacenter);

        DatacenterDto responseDto =
            ModelTransformer.transportFromPersistence(DatacenterDto.class, datacenter);

        // Add the default tiers
        for (int i = 1; i <= 4; i++)
        {
            Tier tier =
                new Tier("Default Tier " + i, "Description of the default tier " + i, datacenter);
            repo.insertTier(tier);
        }

        // Add the Remote Services in database in case are informed in the request
        if (dto.getRemoteServices() != null)
        {
            RemoteServicesDto responseRemoteService = new RemoteServicesDto();
            for (RemoteServiceDto rsd : dto.getRemoteServices().getCollection())
            {
                RemoteServiceDto rsDto =
                    infrastructureService.addRemoteService(rsd, datacenter.getId());
                responseRemoteService.add(rsDto);
            }
            responseDto.setRemoteServices(responseRemoteService);
        }

        return responseDto;
    }

    public Datacenter getDatacenter(final Integer id)
    {
        Datacenter datacenter = repo.findById(id);

        if (datacenter == null)
        {
            addNotFoundErrors(APIError.NON_EXISTENT_DATACENTER);
            flushErrors();
        }

        return datacenter;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Datacenter modifyDatacenter(final Integer datacenterId, final DatacenterDto dto)
    {
        Datacenter old = getDatacenter(datacenterId);

        if (repo.existsAnyOtherWithName(old, dto.getName()))
        {
            addConflictErrors(APIError.DATACENTER_DUPLICATED_NAME);
            flushErrors();
        }

        old.setName(dto.getName());
        old.setLocation(dto.getLocation());

        isValidDatacenter(old);

        repo.update(old);
        return old;
    }

    public Set<HypervisorType> getHypervisorTypes(final Datacenter datacenter)
    {
        return repo.findHypervisors(datacenter);
    }

    public List<Enterprise> findEnterprisesByDatacenterWithNetworks(final Datacenter datacenter,
        final Boolean network, final Integer firstElem, final Integer numElem)
    {
        return repo.findEnterprisesByDataCenter(datacenter, network, firstElem, numElem);

    }

    private void isValidDatacenter(final Datacenter datacenter)
    {
        if (!datacenter.isValid())
        {
            addValidationErrors(datacenter.getValidationErrors());
        }
        flushErrors();
    }

    public List<Rack> getRacks(Datacenter datacenter)
    {
        return repo.findRacks(datacenter);
    }

    public List<Rack> getRacksWithHAEnabled(Datacenter datacenter)
    {
        return repo.findRacksWithHAEnabled(datacenter);
    }

    public List<Machine> getMachines(Rack rack)
    {
        return repo.findRackMachines(rack);
    }

    public List<Machine> getEnabledMachines(Rack rack)
    {
        return repo.findRackEnabledForHAMachines(rack);
    }

    // FIXME: Delete is now allowed right now
    // public void removeDatacenter(Integer id)
    // {
    // Datacenter datacenter = dao.findById(id);
    // dao.makeTransient(datacenter);
    // }
}
