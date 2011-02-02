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
package com.abiquo.abiserver.commands.stub.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.wink.client.ClientResponse;

import com.abiquo.abiserver.commands.stub.AbstractAPIStub;
import com.abiquo.abiserver.commands.stub.SystemPropertyResourceStub;
import com.abiquo.abiserver.pojo.config.SystemProperty;
import com.abiquo.abiserver.pojo.result.DataResult;
import com.abiquo.server.core.config.SystemPropertiesDto;
import com.abiquo.server.core.config.SystemPropertyDto;
import com.abiquo.util.URIResolver;

/**
 * Stub to connect to the System properties API functionallity.
 * 
 * @author ibarrera
 */
public class SystemPropertyResourceStubImpl extends AbstractAPIStub implements
    SystemPropertyResourceStub
{
    @Override
    public DataResult<Collection<SystemProperty>> getSystemProperties(String component)
    {
        // Build API request URI
        Map<String, String[]> queryparams = new HashMap<String, String[]>();
        queryparams.put("component", new String[] {component});

        String uri =
            URIResolver.resolveURI(apiUri, "config/properties", new HashMap<String, String>(),
                queryparams);

        // Perform API call
        ClientResponse response = get(uri);

        // Process result
        DataResult<Collection<SystemProperty>> dataResult =
            new DataResult<Collection<SystemProperty>>();

        if (response.getStatusCode() != 200)
        {
            populateErrors(response, dataResult, "getSystemProperties");
        }
        else
        {
            dataResult.setSuccess(true);

            Collection<SystemProperty> systemProperties = new ArrayList<SystemProperty>();
            SystemPropertiesDto responseDto = response.getEntity(SystemPropertiesDto.class);

            for (SystemPropertyDto systempropertyDto : responseDto.getCollection())
            {
                systemProperties.add(fromDto(systempropertyDto));
            }

            dataResult.setData(systemProperties);
        }

        return dataResult;
    }

    @Override
    public DataResult<Collection<SystemProperty>> modifySystemProperties(String component,
        Collection<SystemProperty> properties)
    {
        // Build the DTO
        SystemPropertiesDto dto = new SystemPropertiesDto();
        for (SystemProperty property : properties)
        {
            dto.add(toDto(property));
        }

        // Build API request URI
        Map<String, String[]> queryparams = new HashMap<String, String[]>();
        queryparams.put("component", new String[] {component});

        String uri =
            URIResolver.resolveURI(apiUri, "config/properties", new HashMap<String, String>(),
                queryparams);

        ClientResponse response = put(uri, dto);

        // Process result
        DataResult<Collection<SystemProperty>> dataResult =
            new DataResult<Collection<SystemProperty>>();

        if (response.getStatusCode() != 200)
        {
            populateErrors(response, dataResult, "modifySystemProperties");
        }
        else
        {
            dataResult.setSuccess(true);

            Collection<SystemProperty> systemProperties = new ArrayList<SystemProperty>();
            SystemPropertiesDto responseDto = response.getEntity(SystemPropertiesDto.class);

            for (SystemPropertyDto systempropertyDto : responseDto.getCollection())
            {
                systemProperties.add(fromDto(systempropertyDto));
            }

            dataResult.setData(systemProperties);
        }

        return dataResult;
    }

    private static SystemProperty fromDto(SystemPropertyDto dto)
    {
        SystemProperty systemProperty = new SystemProperty();

        systemProperty.setId(dto.getId());
        systemProperty.setName(dto.getName());
        systemProperty.setValue(dto.getValue());
        systemProperty.setDescription(dto.getDescription());

        return systemProperty;
    }

    private static SystemPropertyDto toDto(SystemProperty systemProperty)
    {
        SystemPropertyDto dto = new SystemPropertyDto();

        dto.setId(systemProperty.getId());
        dto.setName(systemProperty.getName());
        dto.setValue(systemProperty.getValue());
        dto.setDescription(systemProperty.getDescription());

        return dto;
    }
}
