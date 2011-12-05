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
package com.abiquo.api.services.config;

import java.util.Collection;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.abiquo.api.exceptions.APIError;
import com.abiquo.api.services.DefaultApiService;
import com.abiquo.server.core.config.SystemProperty;
import com.abiquo.server.core.config.SystemPropertyDto;
import com.abiquo.server.core.config.SystemPropertyRep;

@Service
@Transactional(readOnly = true)
public class SystemPropertyService extends DefaultApiService
{
    @Autowired
    private SystemPropertyRep repo;
    
   
    public SystemPropertyService()
    {
        
    }
    public SystemPropertyService(final EntityManager em)
    {
        this.repo = new SystemPropertyRep(em);
    }

    public Collection<SystemProperty> getSystemProperties()
    {
        return repo.findAll();
    }

    public SystemProperty getSystemProperty(final Integer id)
    {
        SystemProperty property = repo.findById(id);
        if (property == null)
        {
            addNotFoundErrors(APIError.NON_EXISTENT_SYSTEM_PROPERTY);
            flushErrors();
        }
        return property;
    }

    public SystemProperty findByName(final String name)
    {
        return repo.findByName(name);
    }

    public Collection<SystemProperty> findByComponent(final String component)
    {
        return repo.findByComponent(component);
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public SystemProperty addSystemProperty(final SystemPropertyDto dto)
    {
        if (repo.existsAnyWithName(dto.getName()))
        {
            addValidationErrors(APIError.SYSTEM_PROPERTIES_DUPLICATED_NAME);
            flushErrors();
        }

        SystemProperty systemProperty = new SystemProperty(dto.getName(), dto.getValue());
        systemProperty.setDescription(dto.getDescription());

        isValidSystemProperty(systemProperty);
        repo.insert(systemProperty);

        return systemProperty;
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public SystemProperty modifySystemProperty(final Integer propertyId, final SystemPropertyDto dto)
    {
        SystemProperty old = getSystemProperty(propertyId);

        if (repo.existsAnyOtherWithName(old, dto.getName()))
        {
            addConflictErrors(APIError.SYSTEM_PROPERTIES_DUPLICATED_NAME);
            flushErrors();
        }

        old.setName(dto.getName());
        old.setValue(dto.getValue());
        old.setDescription(dto.getDescription());

        isValidSystemProperty(old);

        repo.update(old);

        return old;
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public Collection<SystemProperty> modifySystemProperties(
        final Collection<SystemProperty> properties)
    {
        // Validate input to show all possible errors in a unique response
        for (SystemProperty property : properties)
        {
            addValidationErrors(property.getValidationErrors());
        }
        flushErrors();

        // Remove existing properties
        for (SystemProperty existing : getSystemProperties())
        {
            removeSystemProperty(existing.getId());
        }

        // Create the new ones
        saveProperties(properties);

        return properties;
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public Collection<SystemProperty> modifySystemProperties(
        final Collection<SystemProperty> properties, final String component)
    {
        // Validate input to show all possible errors in a unique response
        for (SystemProperty property : properties)
        {
            addValidationErrors(property.getValidationErrors());
        }
        flushErrors();

        // Remove existing properties
        for (SystemProperty existing : findByComponent(component))
        {
            removeSystemProperty(existing.getId());
        }

        // Create the new ones
        saveProperties(properties);

        return properties;
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public void removeSystemProperty(final Integer propertyId)
    {
        SystemProperty property = getSystemProperty(propertyId);
        repo.delete(property);
    }

    private void saveProperties(final Collection<SystemProperty> properties)
    {
        for (SystemProperty property : properties)
        {
            // Check that there are no duplicate properties
            if (repo.existsAnyWithName(property.getName()))
            {
                addConflictErrors(APIError.SYSTEM_PROPERTIES_DUPLICATED_NAME);
                flushErrors();
            }

            repo.insert(property);
        }
    }

    private void isValidSystemProperty(final SystemProperty systemProperty)
    {
        if (!systemProperty.isValid())
        {
            addValidationErrors(systemProperty.getValidationErrors());
        }
        flushErrors();
    }

}
