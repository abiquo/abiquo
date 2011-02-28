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

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.abiquo.api.exceptions.APIError;
import com.abiquo.api.exceptions.NotFoundException;
import com.abiquo.server.core.infrastructure.Datacenter;
import com.abiquo.server.core.infrastructure.DatacenterRep;
import com.abiquo.server.core.infrastructure.Rack;
import com.abiquo.server.core.infrastructure.RackDto;

@Service
@Transactional(readOnly = true)
public class RackService extends DefaultApiService
{
    @Autowired
    DatacenterRep repo;

    public List<Rack> getRacksByDatacenter(final Integer datacenterId)
    {
        Datacenter datacenter = repo.findById(datacenterId);

        if (datacenter == null)
        {
            throw new NotFoundException(APIError.NON_EXISTENT_DATACENTER);
        }

        return repo.findRacks(datacenter);
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public Rack addRack(RackDto rackDto, Integer datacenterId)
    {
        if (datacenterId == 0)
        {
            errors.add(APIError.NON_EXISTENT_DATACENTER);
        }

        Datacenter datacenter = repo.findById(datacenterId);

        if (repo.existsAnyRackWithName(datacenter, rackDto.getName()))
        {
            errors.add(APIError.RACK_DUPLICATED_NAME);
        }
        flushErrors();
        Integer vlanIdMax,vlanIdMin,vlanPerVdcExpected,nrsq;
        vlanIdMax = rackDto.getVlanIdMax();
        vlanIdMin = rackDto.getVlanIdMin();
        vlanPerVdcExpected = rackDto.getVlanPerVdcExpected();
        nrsq = rackDto.getNrsq();
        String vlansIdAvoided = rackDto.getVlansIdAvoided();
        if(rackDto.getVlanIdMax() == null)
        {
        	vlanIdMax = Rack.VLAN_ID_MAX_DEFAULT_VALUE;
        }
        if(rackDto.getVlanIdMin() == null)
        {
        	vlanIdMin = Rack.VLAN_ID_MIN_DEFAULT_VALUE;
        }
        if(rackDto.getVlanPerVdcExpected() == null)
        {
        	vlanPerVdcExpected =Rack.VLAN_PER_VDC_EXPECTED_DEFAULT_VALUE;
        }
        if((rackDto.getNrsq() == null) || (rackDto.getNrsq() > 100))
        {
        	nrsq = Rack.NRSQ_DEFAULT_VALUE;
        }
        if(rackDto.getVlansIdAvoided() == null)
        {
        	vlansIdAvoided = Rack.VLANS_ID_AVOIDED_DEFAULT_VALUE;
        }
        Rack rack = datacenter.createRack(rackDto.getName(), vlanIdMin,
                vlanIdMax, vlanPerVdcExpected,nrsq);
        rack.setShortDescription(rackDto.getShortDescription());
        rack.setLongDescription(rackDto.getLongDescription());
        rack.setVlansIdAvoided(vlansIdAvoided);

        isValidRack(rack);
        repo.insertRack(rack);

        return rack;
    }

    public Rack getRack(Integer id)
    {
        return repo.findRackById(id);
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public Rack modifyRack(Integer rackId, RackDto rackDto)
    {
        Rack old = getRack(rackId);

        if (repo.existsAnyOtherRackWithName(old, rackDto.getName()))
        {
            errors.add(APIError.RACK_DUPLICATED_NAME);
            flushErrors();
        }

        old.setName(rackDto.getName());
        old.setShortDescription(rackDto.getShortDescription());
        old.setLongDescription(rackDto.getLongDescription());

        isValidRack(old);
        repo.updateRack(old);
        return old;
    }

    // FIXME: Remote is not rupported right now
    public void removeRack(Integer id)
    {
        // Rack rack = rackDao.findById(id);
        // rackDao.makeTransient(rack);
    }

    public boolean isAssignedTo(final Integer datacenterId, final Integer rackId)
    {
        Rack rack = getRack(rackId);

        return isAssignedTo(datacenterId, rack);
    }

    public boolean isAssignedTo(final Integer datacenterId, final Rack rack)
    {
        return rack != null && rack.getDatacenter().getId().equals(datacenterId);
    }

    protected void isValidRack(Rack rack)
    {
        if (!rack.isValid())
        {
            validationErrors.addAll(rack.getValidationErrors());
        }

        flushErrors();
    }
}
