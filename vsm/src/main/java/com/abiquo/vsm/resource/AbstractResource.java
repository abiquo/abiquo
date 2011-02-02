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
package com.abiquo.vsm.resource;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.codec.binary.Base64;
import org.apache.wink.common.internal.ResponseImpl.ResponseBuilderImpl;
import org.apache.wink.server.handlers.MessageContext;

import com.abiquo.vsm.VSMManager;
import com.abiquo.vsm.exception.VSMException;
import com.abiquo.vsm.model.PhysicalMachine;
import com.abiquo.vsm.model.VirtualMachine;
import com.abiquo.vsm.model.transport.PhysicalMachineDto;
import com.abiquo.vsm.model.transport.VirtualMachineDto;

/**
 * Base class for all REST resources.
 * 
 * @author ibarrera
 */
public class AbstractResource
{
    /** The authentication header name. */
    public static final String AUTH_HEADER = "Authorization";

    /** The list of http methods that the resources may implement. */
    private static Collection<Class< ? >> REST = new ArrayList<Class< ? >>()
    {
        private static final long serialVersionUID = 1L;

        {
            add(GET.class);
            add(POST.class);
            add(PUT.class);
            add(DELETE.class);
            add(OPTIONS.class);
        }
    };

    /**
     * Get the allowed methods for the current resource.
     * 
     * @param context The Message context with information about the request.
     * @return The list of operations that can be performed on the current resource.
     */
    @OPTIONS
    public Response options(@Context MessageContext context)
    {
        ResponseBuilder builder = new ResponseBuilderImpl();
        String methodsAllowed = getMethodsAllowed();
        builder.header("Allow", methodsAllowed);
        return builder.build();
    }

    /**
     * Get the allowed http methods for the current resource.
     * 
     * @return The allowed http methods for the current resource.
     */
    protected String getMethodsAllowed()
    {
        Collection<String> allowed = new LinkedHashSet<String>();
        for (Method method : this.getClass().getMethods())
        {
            for (Annotation annotation : method.getAnnotations())
            {
                if (REST.contains(annotation.annotationType()))
                {
                    allowed.add(annotation.annotationType().getSimpleName());
                    break;
                }
            }
        }

        return allowed.toString();
    }

    /**
     * Checks the status of the system and guarantees that the request can be handled.
     * 
     * @throws VSMException If the system is not properly configured and the request cannot be
     *             handled.
     */
    protected void checkSystem() throws VSMException
    {
        if (!VSMManager.getInstance().checkSystem())
        {
            throw new VSMException(Status.SERVICE_UNAVAILABLE,
                "The system is not properly configured and the request cannot be handled");
        }
    }

    /**
     * Decode the authentication credentials from a Basic Auth string.
     * 
     * @param auth The Basic Auth string.
     * @return The authentication credentials.
     */
    protected String[] getBasicAuthCredentials(String auth)
    {
        if (auth == null)
        {
            throw new VSMException(Status.UNAUTHORIZED, "Missing authotization header");
        }

        String[] tokens = auth.split(" ");

        if (!tokens[0].equals("Basic"))
        {
            throw new VSMException(Status.UNAUTHORIZED, "Missing authotization header");
        }

        String credentials = new String(Base64.decodeBase64(tokens[1].getBytes()));
        return credentials.split(":");
    }

    /**
     * Convert the given virtual machine to a transport object.
     * 
     * @param pm The virtual machine to convert.
     * @return The transport object.
     */
    protected static VirtualMachineDto toDto(VirtualMachine vm)
    {
        VirtualMachineDto dto = new VirtualMachineDto();
        dto.setId(vm.getId());
        dto.setName(vm.getName());
        dto.setLastKnownState(vm.getLastKnownState());
        dto.setPhysicalMachine(toDto(vm.getPhysicalMachine()));
        return dto;
    }

    /**
     * Convert the given physical machine to a transport object.
     * 
     * @param pm The physical machine to convert.
     * @return The transport object.
     */
    protected static PhysicalMachineDto toDto(PhysicalMachine pm)
    {
        PhysicalMachineDto dto = new PhysicalMachineDto();
        dto.setId(pm.getId());
        dto.setAddress(pm.getAddress());
        dto.setType(pm.getType());
        return dto;
    }
}
