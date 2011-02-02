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

    public Collection<SystemProperty> getSystemProperties()
    {
        return repo.findAll();
    }

    public SystemProperty getSystemProperty(Integer id)
    {
        return repo.findById(id);
    }

    public SystemProperty findByName(String name)
    {
        return repo.findByName(name);
    }

    public Collection<SystemProperty> findByComponent(String component)
    {
        return repo.findByComponent(component);
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public SystemProperty addSystemProperty(SystemPropertyDto dto)
    {
        if (repo.existsAnyWithName(dto.getName()))
        {
            errors.add(APIError.SYSTEM_PROPERTIES_DUPLICATED_NAME);
            flushErrors();
        }

        SystemProperty systemProperty = new SystemProperty(dto.getName(), dto.getValue());
        systemProperty.setDescription(dto.getDescription());

        isValidSystemProperty(systemProperty);
        repo.insert(systemProperty);

        return systemProperty;
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public SystemProperty modifySystemProperty(Integer propertyId, SystemPropertyDto dto)
    {
        SystemProperty old = getSystemProperty(propertyId);

        if (repo.existsAnyOtherWithName(old, dto.getName()))
        {
            errors.add(APIError.SYSTEM_PROPERTIES_DUPLICATED_NAME);
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
    public Collection<SystemProperty> modifySystemProperties(Collection<SystemProperty> properties)
    {
        // Validate input to show all possible errors in a unique response
        for (SystemProperty property : properties)
        {
            validationErrors.addAll(property.getValidationErrors());
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
    public Collection<SystemProperty> modifySystemProperties(Collection<SystemProperty> properties,
        String component)
    {
        // Validate input to show all possible errors in a unique response
        for (SystemProperty property : properties)
        {
            validationErrors.addAll(property.getValidationErrors());
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
    public void removeSystemProperty(Integer propertyId)
    {
        SystemProperty property = getSystemProperty(propertyId);
        repo.delete(property);
    }

    private void saveProperties(Collection<SystemProperty> properties)
    {
        for (SystemProperty property : properties)
        {
            // Check that there are no duplicate properties
            if (repo.existsAnyWithName(property.getName()))
            {
                errors.add(APIError.SYSTEM_PROPERTIES_DUPLICATED_NAME);
                flushErrors();
            }

            repo.insert(property);
        }
    }

    private void isValidSystemProperty(SystemProperty systemProperty)
    {
        if (!systemProperty.isValid())
        {
            validationErrors.addAll(systemProperty.getValidationErrors());
        }
        flushErrors();
    }

}
