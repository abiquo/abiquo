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

import static com.abiquo.api.resources.config.SystemPropertyResource.createPersistenceObject;
import static com.abiquo.api.resources.config.SystemPropertyResource.createTransferObject;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;

import org.apache.wink.common.annotations.Workspace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.abiquo.api.resources.AbstractResource;
import com.abiquo.api.services.config.SystemPropertyService;
import com.abiquo.api.util.IRESTBuilder;
import com.abiquo.server.core.config.SystemPropertiesDto;
import com.abiquo.server.core.config.SystemProperty;
import com.abiquo.server.core.config.SystemPropertyDto;

/**
 * @wiki The System Property resource provides access to Abiquo internal configuration parameters.
 *       This page describes the methods exposed to view and manage these System Properties.
 */
@Path(SystemPropertiesResource.SYSTEM_PROPERTIES_PATH)
@Controller
@Workspace(workspaceTitle = "Abiquo configuration workspace", collectionTitle = "Properties")
public class SystemPropertiesResource extends AbstractResource
{
    public static final String SYSTEM_PROPERTIES_PATH = "config/properties";

    public static final String NAME_QUERY_PARAM = "name";

    public static final String COMPONENT_QUERY_PARAM = "component";

    @Autowired
    private SystemPropertyService service;

    /**
     * Returns all abiquo system properties
     * 
     * @title Retrive all system properties
     * @param name name of the property
     * @param component component of the property
     * @param restBuilder a Context-injected object to create the links of the Dto
     * @return a {PropertiesDto} with all system properties of abiquo
     * @throws Exception
     */
    @GET
    @Produces(SystemPropertiesDto.MEDIA_TYPE)
    public SystemPropertiesDto getSystemProperties(@QueryParam(NAME_QUERY_PARAM) final String name,
        @QueryParam(COMPONENT_QUERY_PARAM) final String component,
        @Context final IRESTBuilder restBuilder) throws Exception
    {
        Collection<SystemProperty> all = null;

        if (name != null)
        {
            all = new LinkedList<SystemProperty>();
            SystemProperty property = service.findByName(name);

            if (property != null)
            {
                all.add(property);
            }
        }
        else if (component != null)
        {
            all = service.findByComponent(component);
        }
        else
        {
            all = service.getSystemProperties();
        }

        SystemPropertiesDto properties = new SystemPropertiesDto();
        for (SystemProperty p : all)
        {
            properties.add(createTransferObject(p, restBuilder));
        }

        return properties;
    }

    /**
     * Create a system property
     * 
     * @title Create a system property
     * @param systemProperty system property to create
     * @param restBuilder a Context-injected object to create the links of the Dto
     * @return a {SystemPropertyDto} object with the created system property
     * @throws Exception
     */
    @POST
    @Consumes(SystemPropertyDto.MEDIA_TYPE)
    @Produces(SystemPropertyDto.MEDIA_TYPE)
    public SystemPropertyDto postSystemProperty(final SystemPropertyDto systemProperty,
        @Context final IRESTBuilder restBuilder) throws Exception
    {
        SystemProperty prop = service.addSystemProperty(systemProperty);

        return createTransferObject(prop, restBuilder);
    }

    /**
     * Modifies multiple system properties
     * 
     * @title Mofify multiple system properties
     * @param systemProperties system properties to modify
     * @param component component name
     * @param restBuilder a Context-injected object to create the links of the Dto
     * @return a {systemPropertiesDto} with the modified system properties
     * @throws Exception
     */
    @PUT
    @Consumes(SystemPropertiesDto.MEDIA_TYPE)
    @Produces(SystemPropertiesDto.MEDIA_TYPE)
    public SystemPropertiesDto modifySystemProperties(final SystemPropertiesDto systemProperties,
        @QueryParam(COMPONENT_QUERY_PARAM) final String component,
        @Context final IRESTBuilder restBuilder) throws Exception
    {
        List<SystemProperty> propertiesToPersist = new LinkedList<SystemProperty>();
        for (SystemPropertyDto inputProperty : systemProperties.getCollection())
        {
            propertiesToPersist.add(createPersistenceObject(inputProperty));
        }

        Collection<SystemProperty> newProperties = null;

        if (component == null)
        {
            newProperties = service.modifySystemProperties(propertiesToPersist);
        }
        else
        {
            newProperties = service.modifySystemProperties(propertiesToPersist, component);
        }

        SystemPropertiesDto result = new SystemPropertiesDto();
        for (SystemProperty p : newProperties)
        {
            result.add(createTransferObject(p, restBuilder));
        }

        return result;
    }
}
