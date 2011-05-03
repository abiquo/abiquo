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

import java.security.InvalidParameterException;

import javax.validation.ConstraintViolation;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.apache.wink.common.internal.ResponseImpl.ResponseBuilderImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.abiquo.api.exceptions.APIError;
import com.abiquo.api.exceptions.APIException;
import com.abiquo.api.exceptions.ExtendedAPIException;
import com.abiquo.api.exceptions.InternalServerErrorException;
import com.abiquo.api.resources.EnterpriseResource;
import com.abiquo.api.exceptions.InvalidParameterConstraint;
import com.abiquo.model.rest.RESTLink;
import com.abiquo.model.transport.error.ErrorDto;
import com.abiquo.model.transport.error.ErrorsDto;
import com.abiquo.scheduler.limit.LimitExceededException;

@Provider
public class APIExceptionMapper implements ExceptionMapper<APIException>
{
    private static final Logger LOGGER = LoggerFactory.getLogger(APIExceptionMapper.class);
    
    @Override
    public Response toResponse(APIException exception)
    {
        ErrorsDto errors = new ErrorsDto();
        if (exception instanceof ExtendedAPIException)
        {
            ExtendedAPIException ext = (ExtendedAPIException) exception;
            for (APIError error : ext.getErrors())
            {
                String message =
                    error.getCause() != null ? String.format("%s\nCaused by:%s",
                        error.getMessage(), error.getCause()) : error.getMessage();

                errors.add(createError(error.getCode(), message));
            }
            
            for (ConstraintViolation< ? > error : ext.getValidationErrors())
            {
                errors.add(createError(error.getPropertyPath().toString(), error.getMessage()));
            }

            for (LimitExceededException limitex : ext.getLimitExceededExceptions())
            {
                errors.add(createError(limitex));
            }
            
            for (InvalidParameterConstraint paramEx : ext.getParamConstraints())
            {
                errors.add(createError(paramEx));
            }
        }
        else
        {
            errors.add(createError(exception.getCode(), exception.getMessage()));
        }

        if (exception instanceof InternalServerErrorException)
        {
            LOGGER.error("Unexpected exception that throws a 500 error code in API:", exception);
        }
        
        ResponseBuilder builder = new ResponseBuilderImpl();
        builder.entity(errors);
        builder.status(exception.getHttpStatus());

        return builder.build();
    }

    private ErrorDto createError(String code, String message)
    {
        ErrorDto error = new ErrorDto();
        error.setCode(code);
        error.setMessage(message);
        return error;
    }

    private ErrorDto createError(LimitExceededException limitException)
    {
        ErrorDto error = new ErrorDto();
        error.setCode(APIError.LIMIT_EXCEEDED.getCode());
        error.setMessage(limitException.toString());
        return error;
    }
    
    private ErrorDto createError(InvalidParameterConstraint paramEx)
    {
        ErrorDto error = new ErrorDto();
        error.setCode("CONSTR-" + paramEx.getAnnotation().annotationType().getSimpleName().toUpperCase());
        error.setMessage(paramEx.getMessageError());
        return error;
    }

}
