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
package com.abiquo.nodecollector.provider;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.apache.wink.common.internal.ResponseImpl.ResponseBuilderImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.abiquo.model.transport.error.ErrorDto;
import com.abiquo.nodecollector.exception.BadRequestException;
import com.abiquo.nodecollector.exception.CannotExecuteException;
import com.abiquo.nodecollector.exception.CollectorException;
import com.abiquo.nodecollector.exception.ConnectionException;
import com.abiquo.nodecollector.exception.LoginException;
import com.abiquo.nodecollector.exception.NodecollectorException;
import com.abiquo.nodecollector.exception.ServiceUnavailableException;
import com.abiquo.nodecollector.exception.UnprovisionedException;

/**
 * Build the exceptions putting the values into the {@link ErrorResponseDto} object.
 * 
 * @author jdevesa@abiquo.com
 */
@Provider
public class NodeCollectorExceptionMapper<T extends Exception> implements ExceptionMapper<T>
{

    /** The logger. */
    private static final Logger LOGGER = LoggerFactory
        .getLogger(NodeCollectorExceptionMapper.class);

    @Override
    public Response toResponse(final T exception)
    {
        if (exception instanceof WebApplicationException)
        {
            // It comes from the {@link InputParamConstraintHandler}. Return as it comes.
            WebApplicationException wepExcp = (WebApplicationException) exception;
            return wepExcp.getResponse();
        }
        else if (exception instanceof NodecollectorException)
        {
            NodecollectorException ext = (NodecollectorException) exception;

            // Set the errordto.
            ErrorDto errorDto = new ErrorDto();
            errorDto.setCode(ext.getCommonError().getCode());
            errorDto.setMessage(ext.getCommonError().getMessage());

            ResponseBuilder builder = new ResponseBuilderImpl();
            builder.entity(errorDto);
            builder.status(defineStatus(ext));
            return builder.build();
        }
        else
        {
            // Unhandled exception.
            ErrorDto error = new ErrorDto();
            error.setCode(NodecollectorException.NODECOLLECTOR_ERROR);
            error.setMessage("Unexpected internal server error");

            LOGGER.error("Unexpected internal server error:", exception);

            return new ResponseBuilderImpl().type(MediaType.APPLICATION_XML_TYPE).entity(error)
                .status(Status.INTERNAL_SERVER_ERROR.getStatusCode()).build();

        }
    }

    private Status defineStatus(final NodecollectorException exception)
    {
        if (exception instanceof LoginException)
        {
            return Status.UNAUTHORIZED;
        }
        if (exception instanceof BadRequestException)
        {
            return Status.BAD_REQUEST;
        }
        else if (exception instanceof ServiceUnavailableException)
        {
            return Status.SERVICE_UNAVAILABLE;
        }
        if (exception instanceof ConnectionException)
        {
            return Status.PRECONDITION_FAILED;
        }
        if (exception instanceof UnprovisionedException)
        {
            return Status.NOT_FOUND;
        }
        if (exception instanceof CollectorException)
        {
            return Status.INTERNAL_SERVER_ERROR;
        }
        if (exception instanceof CannotExecuteException)
        {
            return Status.CONFLICT;
        }
        else
        {
            return Status.INTERNAL_SERVER_ERROR;
        }

    }

}
