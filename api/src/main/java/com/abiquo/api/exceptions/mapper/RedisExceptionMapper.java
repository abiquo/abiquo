package com.abiquo.api.exceptions.mapper;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.apache.wink.common.internal.ResponseImpl.ResponseBuilderImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.exceptions.JedisException;

import com.abiquo.api.exceptions.APIError;
import com.abiquo.model.transport.error.ErrorDto;
import com.abiquo.model.transport.error.ErrorsDto;

@Provider
public class RedisExceptionMapper implements ExceptionMapper<JedisException>
{
    private static final Logger LOGGER = LoggerFactory.getLogger(RedisExceptionMapper.class);

    @Override
    public Response toResponse(final JedisException exception)
    {
        return buildErrorResponse(Status.INTERNAL_SERVER_ERROR, APIError.INTERNAL_SERVER_ERROR,
            exception);
    }

    private Response buildErrorResponse(final Status status, final APIError error,
        final JedisException exception)
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

        LOGGER.error("Unexpected Jedis exception", exception);

        return builder.build();
    }
}
