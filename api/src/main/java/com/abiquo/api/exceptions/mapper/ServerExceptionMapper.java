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

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.apache.wink.common.internal.ResponseImpl.ResponseBuilderImpl;

import com.abiquo.api.exceptions.APIError;
import com.abiquo.model.transport.error.ErrorDto;
import com.abiquo.model.transport.error.ErrorsDto;

@Provider
public class ServerExceptionMapper<T extends Throwable> implements ExceptionMapper<T>
{
    public static APIError DEFAULT_SERVER_ERROR = APIError.CONSTRAINT_VIOLATION;

    @Override
    public Response toResponse(T exception)
    {
        ErrorsDto errors = new ErrorsDto();
        ErrorDto error = new ErrorDto();
        ResponseBuilder builder = new ResponseBuilderImpl();
        if (exception instanceof WebApplicationException)
        {
            WebApplicationException webException = (WebApplicationException) exception;
            switch (webException.getResponse().getStatus())
            {
                case 400:
                    error.setCode(APIError.STATUS_BAD_REQUEST.getCode());
                    error.setMessage(APIError.STATUS_BAD_REQUEST.getMessage());
                    builder.status(Status.BAD_REQUEST);
                    break;
                case 401:
                    error.setCode(APIError.STATUS_UNAUTHORIZED.getCode());
                    error.setMessage(APIError.STATUS_UNAUTHORIZED.getMessage());
                    builder.status(Status.UNAUTHORIZED);
                    break;
                case 403:
                    error.setCode(APIError.STATUS_FORBIDDEN.getCode());
                    error.setMessage(APIError.STATUS_FORBIDDEN.getMessage());
                    builder.status(Status.FORBIDDEN);
                    break;
                case 404:
                    error.setCode(APIError.STATUS_NOT_FOUND.getCode());
                    error.setMessage(APIError.STATUS_NOT_FOUND.getMessage());
                    builder.status(Status.NOT_FOUND);
                    break;
//                case 405:
//                    error.setCode(APIError.STATUS_METHOD_NOT_ALLOWED.getCode());
//                    error.setMessage(APIError.STATUS_METHOD_NOT_ALLOWED.getMessage());
//                    builder.status(Status.);
//                    break;
                case 415:
                    error.setCode(APIError.STATUS_UNSUPPORTED_MEDIA_TYPE.getCode());
                    error.setMessage(APIError.STATUS_UNSUPPORTED_MEDIA_TYPE.getMessage());
                    builder.status(Status.UNSUPPORTED_MEDIA_TYPE);
                    break;
                default:
                    error.setCode(APIError.STATUS_INTERNAL_SERVER_ERROR.getCode());
                    error.setMessage(APIError.STATUS_INTERNAL_SERVER_ERROR.getMessage());
                    builder.status(Status.INTERNAL_SERVER_ERROR);
                    break;
                    
            }
        }
        else
        {
            error.setCode(APIError.STATUS_INTERNAL_SERVER_ERROR.getCode());
            error.setMessage(APIError.STATUS_INTERNAL_SERVER_ERROR.getMessage());
            builder.status(Status.INTERNAL_SERVER_ERROR);
            
            exception.printStackTrace();            
        }
        
        errors.getCollection().add(error);       
        builder.entity(errors).type(MediaType.APPLICATION_XML_TYPE);
        
        return builder.build();
    }

    protected Status getResponseStatus(T exception)
    {
        return Status.INTERNAL_SERVER_ERROR;
    }

}
