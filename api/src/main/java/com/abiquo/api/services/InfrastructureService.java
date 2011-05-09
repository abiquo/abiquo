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
import com.abiquo.server.core.infrastructure.Datacenter;
import com.abiquo.server.core.infrastructure.InfrastructureRep;
import com.abiquo.server.core.infrastructure.Rack;

/*
 *  THIS CLASS RESOURCE IS USED AS THE DEFAULT ONE TO DEVELOP THE REST AND 
 *  FOR THIS REASON IS OVER-COMMENTED AND DOESN'T HAVE JAVADOC! PLEASE DON'T COPY-PASTE ALL OF THIS
 *  COMMENTS BECAUSE IS WILL BE SO UGLY TO MAINTAIN THE CODE IN THE API!
 *
 */

// Annotate it as a @Service and set the default @Transactional method attributes.
@Service
@Transactional(readOnly = true, propagation = Propagation.REQUIRED)
public class InfrastructureService extends DefaultApiService
{
    // Declare the Repo. It only should use ONE repo.
    @Autowired
    InfrastructureRep repo;

    // GET the list of Racks by Datacenter.
    public List<Rack> getRacksByDatacenter(final Integer datacenterId)
    {
        // get the datacenter.
        Datacenter datacenter = this.getDatacenter(datacenterId);
        return repo.findRacks(datacenter);
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public Rack addRack(Rack rack, Integer datacenterId)
    {
        Datacenter datacenter = this.getDatacenter(datacenterId);

        // Check if there is a rack with the same name in the Datacenter
        if (repo.existsAnyRackWithName(datacenter, rack.getName()))
        {
            addConflictErrors(APIError.RACK_DUPLICATED_NAME);
            flushErrors();
        }

        // Set the default values if they are not initialized.
        if (rack.getVlanIdMin() == null)
        {
            rack.setVlanIdMin(Rack.VLAN_ID_MIN_DEFAULT_VALUE);
        }
        if (rack.getVlanIdMax() == null)
        {
            rack.setVlanIdMax(Rack.VLAN_ID_MAX_DEFAULT_VALUE);
        }
        if (rack.getVlanPerVdcExpected() == null)
        {
            rack.setVlanPerVdcExpected(Rack.VLAN_PER_VDC_EXPECTED_DEFAULT_VALUE);
        }
        if (rack.getNrsq() == null)
        {
            rack.setNrsq(Rack.NRSQ_DEFAULT_VALUE);
        }
        if (rack.getNrsq() == null)
        {
            rack.setNrsq(Rack.NRSQ_DEFAULT_VALUE);
        }
        
        
        // Set the datacenter that belongs to
        rack.setDatacenter(datacenter);
            
        // Call the inherited 'validate' function in the DefaultApiService
        validate(rack);
        repo.insertRack(rack);

        return rack;
    }

    // Return a rack.
    public Rack getRack(final Integer datacenterId, Integer rackId)
    {
        // Find the rack by itself and by its datacenter.
        Rack rack = repo.findRackByIds(datacenterId, rackId);
        if (rack == null)
        {
            addNotFoundErrors(APIError.NON_EXISTENT_RACK);
            flushErrors();
        }
        return rack;
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public Rack modifyRack(final Integer datacenterId, final Integer rackId, final Rack rack)
    {
        Rack old = getRack(datacenterId, rackId);

        // Check 
        if (repo.existsAnyOtherRackWithName(old, rack.getName()))
        {
            addConflictErrors(APIError.RACK_DUPLICATED_NAME);
            flushErrors();
        }

        old.setName(rack.getName());
        old.setShortDescription(rack.getShortDescription());
        old.setLongDescription(rack.getLongDescription());

        validate(old);
        repo.updateRack(old);
        return old;
    }

    public void removeRack(final Integer datacenterId, final Integer rackId)
    {
        Rack rack = getRack(datacenterId, rackId);
        repo.deleteRack(rack);
    }

    public boolean isAssignedTo(final Integer datacenterId, final Integer rackId)
    {
        Rack rack = getRack(datacenterId, rackId);

        return isAssignedTo(datacenterId, rack);
    }

    public boolean isAssignedTo(final Integer datacenterId, final Rack rack)
    {
        return rack != null && rack.getDatacenter().getId().equals(datacenterId);
    }

    /*
     * Get the Datacenter and check if it exists.
     */
    private Datacenter getDatacenter(Integer datacenterId)
    {
        Datacenter datacenter = repo.findById(datacenterId);

        if (datacenter == null)
        {
            // Adding the NON_EXISTENT_DATACENTER to the list of NotFoundErrors and flush them.
            addNotFoundErrors(APIError.NON_EXISTENT_DATACENTER);
            flushErrors();
        }

        return datacenter;
    }
}
