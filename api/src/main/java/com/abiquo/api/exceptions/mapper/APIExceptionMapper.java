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

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.apache.wink.common.internal.ResponseImpl.ResponseBuilderImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.abiquo.api.exceptions.APIException;
import com.abiquo.api.exceptions.BadRequestException;
import com.abiquo.api.exceptions.ConflictException;
import com.abiquo.api.exceptions.ForbiddenException;
import com.abiquo.api.exceptions.InternalServerErrorException;
import com.abiquo.api.exceptions.NotFoundException;
import com.abiquo.api.exceptions.ServiceUnavailableException;
import com.abiquo.api.exceptions.UnsupportedMediaException;
import com.abiquo.model.transport.error.CommonError;
import com.abiquo.model.transport.error.ErrorDto;
import com.abiquo.model.transport.error.ErrorsDto;

@Provider
public class APIExceptionMapper implements ExceptionMapper<APIException>
{

    private static final Logger logger = LoggerFactory.getLogger(APIExceptionMapper.class);

    @Override
    public Response toResponse(final APIException exception)
    {
        ErrorsDto errors = new ErrorsDto();
        APIException ext = exception;
        for (CommonError error : ext.getErrors())
        {
            errors.add(createError(error));
        }

        ResponseBuilder builder = new ResponseBuilderImpl();
        builder.entity(errors);
        builder.status(defineStatus(exception, errors));
        return builder.build();
    }

    private ErrorDto createError(final CommonError error)
    {
        ErrorDto errorDto = new ErrorDto();
        errorDto.setCode(error.getCode());
        errorDto.setMessage(error.getMessage());
        return errorDto;
    }

    private Status defineStatus(final APIException exception, final ErrorsDto dto)

    {
        if (exception instanceof ForbiddenException)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("API Response " + Status.FORBIDDEN.name() + "\n" + dto.toString(),
                    exception);
            }
            return Status.FORBIDDEN;
        }
        if (exception instanceof BadRequestException)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("API Response " + Status.BAD_REQUEST.name() + "\n" + dto.toString(),
                    exception);
            }
            return Status.BAD_REQUEST;
        }
        if (exception instanceof ConflictException)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("API Response " + Status.CONFLICT.name() + "\n" + dto.toString(),
                    exception);
            }
            return Status.CONFLICT;
        }
        if (exception instanceof NotFoundException)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("API Response " + Status.NOT_FOUND.name() + "\n" + dto.toString(),
                    exception);
            }
            return Status.NOT_FOUND;
        }
        if (exception instanceof ServiceUnavailableException)
        {
            logger.error(
                "Unexpected exception that throws a 503 error code in API:\n" + dto.toString(),
                exception);
            return Status.SERVICE_UNAVAILABLE;
        }
        if (exception instanceof InternalServerErrorException)
        {
            logger.error(
                "Unexpected exception that throws a 500 error code in API:\n" + dto.toString(),
                exception);
            return Status.INTERNAL_SERVER_ERROR;
        }
        if (exception instanceof UnsupportedMediaException)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace(
                    "API Response " + Status.UNSUPPORTED_MEDIA_TYPE.name() + "\n" + dto.toString(),
                    exception);
            }
            return Status.UNSUPPORTED_MEDIA_TYPE;
        }
        else
        {
            logger.error("Unknown exception thrown.", exception);
            return Status.INTERNAL_SERVER_ERROR;
        }

    }
}
