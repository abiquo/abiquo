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
import java.util.Set;
import java.util.UUID;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.abiquo.api.exceptions.APIError;
import com.abiquo.api.exceptions.NotFoundException;
import com.abiquo.api.transformer.ModelTransformer;
import com.abiquo.server.core.enumerator.HypervisorType;
import com.abiquo.server.core.infrastructure.Datacenter;
import com.abiquo.server.core.infrastructure.DatacenterDto;
import com.abiquo.server.core.infrastructure.DatacenterRep;
import com.abiquo.server.core.infrastructure.RemoteServiceDto;
import com.abiquo.server.core.infrastructure.RemoteServicesDto;
import com.abiquo.server.core.infrastructure.network.Network;
import com.abiquo.server.core.infrastructure.storage.Tier;

@Service
@Transactional(readOnly = true)
public class DatacenterService extends DefaultApiService
{
    @Autowired
    DatacenterRep repo;

    @Autowired
    RemoteServiceService remoteServiceService;

    public DatacenterService()
    {

    }

    public DatacenterService(EntityManager em)
    {
        repo = new DatacenterRep(em);
        remoteServiceService = new RemoteServiceService(em);
    }

    public Collection<Datacenter> getDatacenters()
    {
        return repo.findAll();
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public DatacenterDto addDatacenter(DatacenterDto dto) throws Exception
    {
        if (repo.existsAnyDatacenterWithName(dto.getName()))
        {
            errors.add(APIError.DATACENTER_DUPLICATED_NAME);
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
        for (int i=1; i<=4; i++)
        {
        	Tier tier = new Tier("Default Tier " + i, "Description of the default tier " + i, datacenter);
        	repo.insertTier(tier);
        }
        
        // Add the Remote Services in database in case are informed in the request
        if (dto.getRemoteServices() != null)
        {
            RemoteServicesDto responseRemoteService = new RemoteServicesDto();
            for (RemoteServiceDto rsd : dto.getRemoteServices().getCollection())
            {
                RemoteServiceDto rsDto =
                    remoteServiceService.addRemoteService(rsd, datacenter.getId());
                responseRemoteService.add(rsDto);
            }
            responseDto.setRemoteServices(responseRemoteService);
        }

        return responseDto;
    }

    public Datacenter getDatacenter(Integer id)
    {
        Datacenter datacenter = repo.findById(id);

        if (datacenter == null)
        {
            throw new NotFoundException(APIError.NON_EXISTENT_DATACENTER);
        }

        return datacenter;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Datacenter modifyDatacenter(Integer datacenterId, DatacenterDto dto)
    {
        Datacenter old = getDatacenter(datacenterId);

        if (repo.existsAnyOtherWithName(old, dto.getName()))
        {
            errors.add(APIError.DATACENTER_DUPLICATED_NAME);
            flushErrors();
        }

        old.setName(dto.getName());
        old.setLocation(dto.getLocation());

        isValidDatacenter(old);

        repo.update(old);
        return old;
    }

    public Set<HypervisorType> getHypervisorTypes(Datacenter datacenter)
    {
        return repo.findHypervisors(datacenter);
    }

    private void isValidDatacenter(Datacenter datacenter)
    {
        if (!datacenter.isValid())
        {
            validationErrors.addAll(datacenter.getValidationErrors());
        }
        flushErrors();
    }

    // FIXME: Delete is now allowed right now
    // public void removeDatacenter(Integer id)
    // {
    // Datacenter datacenter = dao.findById(id);
    // dao.makeTransient(datacenter);
    // }
}
