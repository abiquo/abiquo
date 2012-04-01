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
package com.abiquo.api.resources.config;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;

import org.apache.wink.common.annotations.Parent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.abiquo.api.exceptions.APIError;
import com.abiquo.api.exceptions.NotFoundException;
import com.abiquo.api.resources.AbstractResource;
import com.abiquo.api.services.config.SystemPropertyService;
import com.abiquo.api.util.IRESTBuilder;
import com.abiquo.server.core.config.SystemProperty;
import com.abiquo.server.core.config.SystemPropertyDto;

@Parent(SystemPropertiesResource.class)
@Path(SystemPropertyResource.SYSTEM_PROPERTY_PARAM)
@Controller
public class SystemPropertyResource extends AbstractResource
{
    public static final String SYSTEM_PROPERTY = "property";

    public static final String SYSTEM_PROPERTY_PARAM = "{" + SYSTEM_PROPERTY + "}";

    @Autowired
    private SystemPropertyService service;

    /**
     * Returns a system property
     * 
     * @title Retrieve a system property
     * @param propertyId identifier of the system property
     * @param restBuilder a Context-injected object to create the links of the Dto
     * @return a {SystemPropertyDto} object with the requested system property
     * @throws Exception
     */
    @GET
    @Produces(SystemPropertyDto.MEDIA_TYPE)
    public SystemPropertyDto getSystemProperty(
        @PathParam(SYSTEM_PROPERTY) final Integer propertyId,
        @Context final IRESTBuilder restBuilder) throws Exception
    {
        SystemProperty property = service.getSystemProperty(propertyId);
        return createTransferObject(property, restBuilder);
    }

    /**
     * Modifies a system property
     * 
     * @title Modify a system property
     * @param systemProperty system property to modify
     * @param propertyId identifier of the system property
     * @param restBuilder a Context-injected object to create the links of the Dto
     * @return a {SystemPropertyDto} with the modified system property
     * @throws Exception
     */
    @PUT
    @Consumes(SystemPropertyDto.MEDIA_TYPE)
    @Produces(SystemPropertyDto.MEDIA_TYPE)
    public SystemPropertyDto modifySystemProperty(final SystemPropertyDto systemProperty,
        @PathParam(SYSTEM_PROPERTY) final Integer propertyId,
        @Context final IRESTBuilder restBuilder) throws Exception
    {
        SystemProperty property = service.getSystemProperty(propertyId);

        property = service.modifySystemProperty(propertyId, systemProperty);

        return createTransferObject(property, restBuilder);
    }

    /**
     * Deletes a system property
     * 
     * @title Delete a system property
     * @param propertyId identifier of the system property
     */
    @DELETE
    public void deleteSystemProperty(@PathParam(SYSTEM_PROPERTY) final Integer propertyId)
    {
        SystemProperty property = service.getSystemProperty(propertyId);

        if (property == null)
        {
            throw new NotFoundException(APIError.NON_EXISTENT_SYSTEM_PROPERTY);
        }

        service.removeSystemProperty(propertyId);
    }

    public static SystemPropertyDto createTransferObject(final SystemProperty systemProperty,
        final IRESTBuilder builder) throws Exception
    {
        SystemPropertyDto dto = new SystemPropertyDto();
        dto.setName(systemProperty.getName());
        dto.setDescription(systemProperty.getDescription());
        dto.setId(systemProperty.getId());
        dto.setValue(systemProperty.getValue());

        dto = addLinks(builder, dto);

        return dto;
    }

    public static SystemProperty createPersistenceObject(final SystemPropertyDto dto)
        throws Exception
    {
        SystemProperty systemProperty = new SystemProperty(dto.getName(), dto.getValue());
        systemProperty.setDescription(dto.getDescription());
        return systemProperty;
    }

    private static SystemPropertyDto addLinks(final IRESTBuilder builder,
        final SystemPropertyDto systemProperty)
    {
        systemProperty.setLinks(builder.buildSystemPropertyLinks(systemProperty));

        return systemProperty;
    }
}
