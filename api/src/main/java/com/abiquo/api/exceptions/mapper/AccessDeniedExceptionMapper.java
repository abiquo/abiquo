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

package com.abiquo.api.exceptions.mapper;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.apache.wink.common.internal.ResponseImpl.ResponseBuilderImpl;
import org.springframework.security.AccessDeniedException;
import org.springframework.security.BadCredentialsException;
import org.springframework.security.SpringSecurityException;

import com.abiquo.api.exceptions.APIError;
import com.abiquo.model.transport.error.ErrorDto;
import com.abiquo.model.transport.error.ErrorsDto;

@Provider
public class AccessDeniedExceptionMapper implements ExceptionMapper<SpringSecurityException>
{

    @Override
    public Response toResponse(SpringSecurityException exception)
    {
        ErrorsDto errors = new ErrorsDto();
        if (exception instanceof AccessDeniedException)
        {
            ErrorDto error = new ErrorDto();
            error.setCode(APIError.FORBIDDEN.getCode());
            error.setMessage(APIError.FORBIDDEN.getMessage());
            errors.add(error);

            ResponseBuilder builder = new ResponseBuilderImpl();
            builder.status(Response.Status.FORBIDDEN);
            builder.entity(errors);
            builder.type(MediaType.APPLICATION_XML);
            return builder.build();
        }
        else if (exception instanceof BadCredentialsException)
        {
            ErrorDto error = new ErrorDto();
            error.setCode(APIError.INVALID_CREDENTIALS.getCode());
            error.setMessage(APIError.INVALID_CREDENTIALS.getMessage());
            errors.add(error);

            ResponseBuilder builder = new ResponseBuilderImpl();
            builder.status(Response.Status.UNAUTHORIZED);
            builder.entity(errors);
            builder.type(MediaType.APPLICATION_XML);
            return builder.build();
        }
        else
        {
            ErrorDto error = new ErrorDto();
            error.setCode(APIError.INTERNAL_SERVER_ERROR.getCode());
            error.setMessage(APIError.INTERNAL_SERVER_ERROR.getMessage());
            errors.add(error);

            ResponseBuilder builder = new ResponseBuilderImpl();
            builder.status(Response.Status.INTERNAL_SERVER_ERROR);
            builder.entity(errors);
            builder.type(MediaType.APPLICATION_XML);
            return builder.build();
        }
    }

}
