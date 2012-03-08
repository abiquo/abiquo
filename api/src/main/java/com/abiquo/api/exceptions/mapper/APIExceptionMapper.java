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

import java.util.HashMap;
import java.util.Map;

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
import com.abiquo.api.exceptions.PreconditionFailedException;
import com.abiquo.api.exceptions.ServiceUnavailableException;
import com.abiquo.api.exceptions.UnsupportedMediaException;
import com.abiquo.model.transport.error.CommonError;
import com.abiquo.model.transport.error.ErrorDto;
import com.abiquo.model.transport.error.ErrorsDto;

@Provider
public class APIExceptionMapper implements ExceptionMapper<APIException>
{
    private static final Logger LOGGER = LoggerFactory.getLogger(APIExceptionMapper.class);

    private static final Map<Class< ? extends APIException>, Status> statusMappings;

    static
    {
        statusMappings = new HashMap<Class< ? extends APIException>, Status>();
        statusMappings.put(ForbiddenException.class, Status.FORBIDDEN);
        statusMappings.put(BadRequestException.class, Status.BAD_REQUEST);
        statusMappings.put(ConflictException.class, Status.CONFLICT);
        statusMappings.put(NotFoundException.class, Status.NOT_FOUND);
        statusMappings.put(ServiceUnavailableException.class, Status.SERVICE_UNAVAILABLE);
        statusMappings.put(InternalServerErrorException.class, Status.INTERNAL_SERVER_ERROR);
        statusMappings.put(UnsupportedMediaException.class, Status.UNSUPPORTED_MEDIA_TYPE);
        statusMappings.put(PreconditionFailedException.class, Status.PRECONDITION_FAILED);
    }

    @Override
    public Response toResponse(final APIException exception)
    {
        ErrorsDto errors = new ErrorsDto();

        for (CommonError error : exception.getErrors())
        {
            ErrorDto errorDto = new ErrorDto();
            errorDto.setCode(error.getCode());
            errorDto.setMessage(error.getMessage());

            errors.add(errorDto);
        }

        ResponseBuilder builder = new ResponseBuilderImpl();
        builder.entity(errors);
        builder.status(defineStatus(exception, errors));
        builder.type(ErrorsDto.MEDIA_TYPE);
        return builder.build();
    }

    private Status defineStatus(final APIException exception, final ErrorsDto dto)
    {
        Status status = statusMappings.get(exception.getClass());
        if (status == null)
        {
            status = Status.INTERNAL_SERVER_ERROR;
        }

        if (LOGGER.isTraceEnabled())
        {
            LOGGER.trace("API Response " + status.name() + "\n" + dto.toString() + "\n", exception);
        }

        return status;
    }
}
