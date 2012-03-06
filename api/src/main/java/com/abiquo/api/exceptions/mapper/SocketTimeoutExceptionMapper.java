package com.abiquo.api.exceptions.mapper;

import java.net.SocketTimeoutException;

import javax.ws.rs.core.MediaType;
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

    private static final Logger LOGGER =
        LoggerFactory.getLogger(SocketTimeoutExceptionMapper.class);

    @Override
    public Response toResponse(final SocketTimeoutException exception)
    {
        // TODO Auto-generated method stub
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
        builder.type(MediaType.APPLICATION_XML);
        LOGGER.debug("SocketTimeoutException: " + errorDto.toString());
        LOGGER.info("Connection with API closed, caused by SocketTimeout");

        return builder.build();
    }
}
