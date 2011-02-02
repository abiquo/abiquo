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

package com.abiquo.vsm.exception.mapper;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.apache.wink.common.internal.ResponseImpl.ResponseBuilderImpl;

import com.abiquo.vsm.model.transport.ErrorDto;

/**
 * Mapper for generic exceptions.
 * 
 * @author ibarrera
 * @param <T> The exception thrown.
 */
@Provider
public class GenericExceptionMapper<T extends Throwable> implements ExceptionMapper<T>
{
    @Override
    public Response toResponse(T exception)
    {
        Status status = getResponseStatus(exception);

        ErrorDto error = new ErrorDto();
        error.setCode(status.name());
        error.setMessage(exception.getMessage());

        ResponseBuilder builder = new ResponseBuilderImpl();
        builder.entity(error);
        builder.status(status);

        return builder.build();
    }

    /**
     * Get the response status.
     * 
     * @param exception The exception thrown.
     * @return The response status.
     */
    protected Status getResponseStatus(T exception)
    {
        if (exception instanceof WebApplicationException)
        {
            WebApplicationException wex = (WebApplicationException) exception;
            return Status.fromStatusCode(wex.getResponse().getStatus());
        }
        else
        {
            return Status.INTERNAL_SERVER_ERROR;
        }
    }
}
