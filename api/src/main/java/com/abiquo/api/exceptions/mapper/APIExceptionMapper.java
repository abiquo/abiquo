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

import javax.validation.ConstraintViolation;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.apache.wink.common.internal.ResponseImpl.ResponseBuilderImpl;

import com.abiquo.api.exceptions.APIError;
import com.abiquo.api.exceptions.APIException;
import com.abiquo.api.exceptions.ExtendedAPIException;
import com.abiquo.model.transport.error.ErrorDto;
import com.abiquo.model.transport.error.ErrorsDto;
import com.abiquo.scheduler.limit.LimitExceededException;

@Provider
public class APIExceptionMapper implements ExceptionMapper<APIException>
{
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
        }
        else
        {
            errors.add(createError(exception.getCode(), exception.getMessage()));
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

}
