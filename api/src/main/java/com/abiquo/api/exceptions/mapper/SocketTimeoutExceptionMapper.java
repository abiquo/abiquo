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

import java.net.SocketTimeoutException;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.apache.wink.common.internal.ResponseImpl.ResponseBuilderImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.abiquo.api.exceptions.APIError;
import com.abiquo.model.transport.error.ErrorDto;
import com.abiquo.model.transport.error.ErrorsDto;

@Provider
public class SocketTimeoutExceptionMapper implements ExceptionMapper<SocketTimeoutException>
{

    private static final Logger LOGGER = LoggerFactory
        .getLogger(SocketTimeoutExceptionMapper.class);

    @Override
    public Response toResponse(final SocketTimeoutException exception)
    {
        return buildErrorResponse(Status.INTERNAL_SERVER_ERROR, APIError.INTERNAL_SERVER_ERROR);
    }

    private Response buildErrorResponse(final Status status, final APIError error)
    {
        ErrorsDto errorsDto = new ErrorsDto();
        ErrorDto errorDto = new ErrorDto();
        ResponseBuilder builder = new ResponseBuilderImpl();

        errorDto.setCode(error.getCode());
        errorDto.setMessage(error.getMessage());
        errorsDto.getCollection().add(errorDto);

        builder.status(status);
        builder.entity(errorsDto);
        builder.type(ErrorsDto.MEDIA_TYPE);
        LOGGER.debug("SocketTimeoutException: " + errorDto.toString());
        LOGGER.info("Connection with API closed, caused by SocketTimeout");

        return builder.build();
    }
}
